package site.saishin.tsugumon.logic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.TsugumonException;
import site.saishin.tsugumon.entity.Answer;
import site.saishin.tsugumon.entity.DealtEnquete;
import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.Entry;
import site.saishin.tsugumon.entity.User;
import site.saishin.tsugumon.model.AnswerModel;
import site.saishin.tsugumon.model.EnqueteModel;
import site.saishin.tsugumon.model.EntryModel;
import site.saishin.tsugumon.model.HomeModel;
import site.saishin.tsugumon.model.UserModel;

public class TsugumonLogic {

	private static final Logger logger = LoggerFactory.getLogger(TsugumonLogic.class);
	@Inject
	@PersistenceContext
	EntityManager em;
	@Inject
	private Set<String> availableUsers;
	@Inject
	private MemcachedClient mclient;
	ObjectMapper mapper = new ObjectMapper();

	@Inject
	public TsugumonLogic() {
		logger.info("inited");
	}

	public Optional<UserModel> getUserModel(String addr) {
		UserModel userModel = new UserModel();
		userModel.setIpAddress(addr);
		Optional<User> user = getUserByIpAddress(addr);
		if (user.isPresent()) {
			userModel.setAvailable(checkAvailable(user.get(), getDealtEnquete().enquete_id));
		} else {
			userModel.setAvailable(false);
		}
		return Optional.empty();
	}

	public Optional<EnqueteModel> getDealtEnqueteModel() {
		Long dealtEnqueteId = getDealtEnquete().enquete_id;
		Optional<Enquete> ret = getEnqueteById(dealtEnqueteId);
		logger.debug("dealtenquete is " + ret.isPresent());
		if (ret.isPresent()) {
			return Optional.of(new EnqueteModel(ret.get()));
		} else {
			return Optional.empty();
		}
	}

	public Optional<HomeModel> getHome(String addr) {
		Optional<User> userOpt = getUserByIpAddress(addr);
		HomeModel home = new HomeModel();

		if (!userOpt.isPresent()) {
			// データベースに登録されていない場合
			logger.debug("user is null");
			return Optional.empty();
		} else {
			User user = userOpt.get();
			logger.debug(user.ipAddress + "; " + user.id);
			getEnqueteByUser(user).ifPresent((e) -> {
				home.setOwnEnquete(new EnqueteModel(e));
			});

			List<Answer> answers = getAnswers(user);

			List<AnswerModel> answerModels = answers.stream().map((answer) -> {
				AnswerModel am = new AnswerModel();
				Entry entry = em.find(Entry.class, answer.entry_id);
				am.setEntry(entry);
				am.setCreated(answer.created);
				getEnqueteById(entry.enquete_id).ifPresent((e) -> {
					am.setEnquete(new EnqueteModel(e));
				});
				return am;
			}).collect(Collectors.toList());
			logger.debug("" + answerModels.size());
			home.setAnswers(answerModels);
			return Optional.of(home);
		}
	}

	public Optional<EnqueteModel> getEnqueteModel(Long id, String addr) {
		Optional<Enquete> optEnquete = getEnqueteById(id);
		if (!optEnquete.isPresent()) {
			return Optional.empty();
		} else {
			Enquete enquete = optEnquete.get();
			EnqueteModel enqueteModel = new EnqueteModel(enquete);
			List<Entry> entries = getEntries(optEnquete.get());
			List<EntryModel> entryModels = new ArrayList<>(entries.size());
			Optional<User> useropt = getUserByIpAddress(addr);
			boolean available = false;
			if (useropt.isPresent()) {
				available = checkAvailable(useropt.get(), getDealtEnquete().enquete_id);
			}
			for (int i = 0; i < entries.size(); i++) {
				EntryModel entryModel = new EntryModel();
				entryModel.setNumber(entries.get(i).number);
				entryModel.setContent(entries.get(i).content);
				if (available) {
					// availableなら
					entryModel.setQuantity(countByEnqueteAndEntry(enquete.id, i + 1));
				}
				entryModels.add(entryModel);
			}
			enqueteModel.setEntries(entryModels);
			return Optional.of(enqueteModel);
		}
	}

	public List<EnqueteModel> search(final String keyword, final int page) {
		TypedQuery<Enquete> q = em.createNamedQuery("", Enquete.class);

		List<EnqueteModel> enqueteModels = q.getResultList().stream().map((e) -> {
			EnqueteModel em = new EnqueteModel(e);
			return em;
		}).collect(Collectors.toList());
		return enqueteModels;
	}

	public List<EnqueteModel> getRank(final int page) {
		List<EnqueteModel> cacheenquetes = (List<EnqueteModel>) mclient.get("ranking/" + page);
		if (cacheenquetes == null) {
			List<Enquete> enquetes2 = em.createQuery("", Enquete.class).getResultList();
			if (!enquetes2.isEmpty()) {
				List<EnqueteModel> enqmodels = new ArrayList<>();
				for (Enquete e : enquetes2) {
					EnqueteModel em = new EnqueteModel(e);
					enqmodels.add(em);
				}
				mclient.set("ranking/" + page, TsugumonConstants.LONG_EXPIRATION, enqmodels);
				return enqmodels;
			}
		} else {
			return cacheenquetes;
		}
		return Collections.emptyList();
	}

	public Optional<User> getUserByIpAddress(String addr) {
		User user = (User) mclient.get("useraddr/" + addr);
		if (user == null) {
			TypedQuery<User> q = em.createNamedQuery(User.BY_ADDR, User.class);
			q.setParameter(1, addr);
			user = q.getSingleResult();
			if (user != null) {
				mclient.set("useraddr/", TsugumonConstants.SHORT_EXPIRATION, user);
				return Optional.of(user);
			}
		} else {
			return Optional.of(user);
		}
		return Optional.empty();
	}

	public int countByEnqueteAndEntry(Long id, int index) {
		Integer count = (Integer) mclient.get("count/" + id + "/" + index);
		if (count == null) {
			TypedQuery<Answer> q = em.createNamedQuery(Answer.COUNT, Answer.class);
			q.setParameter(0, id);
			q.setParameter(1, index);
			count = q.executeUpdate();
			if (count != null) {
				mclient.set("count/" + id + "/" + index, TsugumonConstants.SHORT_EXPIRATION, count);
				return count;
			}
		}
		return count;
	}

	public int getCountOfAnswers(User user) {
		return 0;
	}

	public List<Answer> getAnswers(User user) {
		Object obj = mclient.get("answers/" + user.id);
		List<Answer> answers = null;
		if (obj == null) {
			TypedQuery<Answer> q = em.createNamedQuery("", Answer.class);
			q.setParameter("", user.id);
			answers = q.getResultList();
			if (answers != null) {
				mclient.set("answers/" + user.id, TsugumonConstants.SHORT_EXPIRATION, answers);
				return answers;
			}
		} else {
			return (List<Answer>) obj;
		}
		return Collections.emptyList();
	}

	public List<Entry> getEntries(Enquete enquete) {
		@SuppressWarnings("unchecked")
		List<Entry> entries = (List<Entry>) mclient.get("entries/" + enquete.id);
		if (entries == null) {
			TypedQuery<Entry> q = em.createNamedQuery("", Entry.class);
			q.setParameter("", enquete.id);
			entries = q.getResultList();
			if (!entries.isEmpty()) {
				mclient.set("entries/" + enquete.id, TsugumonConstants.MIDDLE_EXPIRATION, entries);
			}
		}
		return entries;
	}

	public Optional<Enquete> getEnqueteByUser(User user) {
		Enquete enquete = (Enquete) mclient.get("enqueteOf/" + user.id);
		if (enquete == null) {
			TypedQuery<Enquete> q = em.createNamedQuery(Enquete.BY_USER, Enquete.class);
			q.setParameter("", user.id);
			enquete = q.getSingleResult();
			if (enquete != null) {
				mclient.set("enqueteOf/", TsugumonConstants.SHORT_EXPIRATION, enquete);
				return Optional.of(enquete);
			}
		} else {
			return Optional.of(enquete);
		}
		return Optional.empty();
	}

	public Optional<Enquete> getEnqueteById(Long id) {
		Enquete enquete = (Enquete) mclient.get("enquete/" + id);
		if (enquete == null) {
			enquete = em.find(Enquete.class, id);
			if (enquete != null) {
				mclient.set("enquete/" + id, TsugumonConstants.SHORT_EXPIRATION, enquete);
				return Optional.of(enquete);
			}
		} else {
			return Optional.of(enquete);
		}
		return Optional.empty();
	}

	public DealtEnquete getDealtEnquete() {
		DealtEnquete dealtEnquete = (DealtEnquete) mclient.get("dealtEnquete");
		if (dealtEnquete == null) {
			dealtEnquete = dealtEnqueteDao.select();
			if (dealtEnquete != null) {
				logger.debug("dealtEnquete != null");
				mclient.set("dealtEnquete", TsugumonConstants.LONG_EXPIRATION, dealtEnquete);
				return dealtEnquete;
			}
		} else {
			return dealtEnquete;
		}
		return null;
	}

	public boolean checkAvailable(User user, Long dealtEnqueteId) {
		if (availableUsers.contains(user.ipAddress)) {
			return true;
		} else {
			boolean available;
			logger.debug(user.ipAddress + " id:" + user.id);
			List<Answer> answers = getAnswers(user);
			logger.debug(answers.toString());
			Optional<Answer> answerAny = answers
					.stream()
					.filter((answer) -> {
						return em.find(Entry.class, answer.entry_id).equals(dealtEnqueteId);
					}).findAny();
			logger.debug("answers.size():" + answers.size() + "; answersAny:" + answerAny.isPresent());
			//
			available = answers.size() == TsugumonConstants.MAX_SELECT_ANSWER_SIZE + 1 && answerAny.isPresent();
			if (available) {
				availableUsers.add(user.ipAddress);
			}
			return available;
		}
	}
}
