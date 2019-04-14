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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.seasar.doma.jdbc.tx.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.TsugumonException;
import site.saishin.tsugumon.dao.AnswerDao;
import site.saishin.tsugumon.dao.AnswerDaoImpl;
import site.saishin.tsugumon.dao.DealtEnqueteDao;
import site.saishin.tsugumon.dao.DealtEnqueteDaoImpl;
import site.saishin.tsugumon.dao.EnqueteDao;
import site.saishin.tsugumon.dao.EnqueteDaoImpl;
import site.saishin.tsugumon.dao.EntryDao;
import site.saishin.tsugumon.dao.EntryDaoImpl;
import site.saishin.tsugumon.dao.UserDao;
import site.saishin.tsugumon.dao.UserDaoImpl;
import site.saishin.tsugumon.dao.setting.AppConfig;
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
	@PersistenceContext
	EntityManager em;
	// 許可ユーザ
	private Set<String> availableUsers;
	private MemcachedClient mclient;;
	ObjectMapper mapper = new ObjectMapper();

	public TsugumonLogic(Set<String> availableUsers, MemcachedClient mclient) {
		this.availableUsers = availableUsers;

		this.mclient = mclient;
		logger.info("inited");
	}

	public Optional<UserModel> getUserAtTransaction(String addr) {
		UserModel userModel = new UserModel();
		userModel.setIpAddress(addr);
		return withTransaction(() -> {
			Optional<User> user = getUserByIpAddress(addr);
			if (user.isPresent()) {
				userModel.setAvailable(checkAvailable(user.get(), getDealtEnquete().enquete_id));
			} else {
				userModel.setAvailable(false);
			}
			return Optional.of(userModel);
		});
	}

	public Optional<EnqueteModel> getDealtEnqueteModelAtTransaction() {
		return withTransaction(()->{
			Long dealtEnqueteId = getDealtEnquete().enquete_id;
			Optional<Enquete> ret = getEnqueteById(dealtEnqueteId);
			logger.debug("dealtenquete is " + ret.isPresent());
			if(ret.isPresent()) {
				return Optional.of(new EnqueteModel(ret.get()));
			} else {
				return Optional.empty();
			}
		});
		
	}
	public Optional<HomeModel> getHomeAtTransaction(String addr) {
		return withTransaction(()->{
			Optional<User> userOpt = getUserByIpAddress(addr);
			HomeModel home = new HomeModel();

			if (!userOpt.isPresent()) {
				// データベースに登録されていない場合
				logger.debug("user is null");
				return Optional.empty();
			} else {
				User user = userOpt.get();
				logger.debug(user.ipAddress + "; " + user.id);
				getEnqueteByUser(user).ifPresent((e)->{
					home.setOwnEnquete(new EnqueteModel(e));
				});
				
				List<Answer> answers = getAnswers(user);
				answers.removeIf(answer -> !getEnqueteById(answer.enquete_id).isPresent());
				List<AnswerModel> answerModels = answers.stream().map((answer) -> {
					AnswerModel am = new AnswerModel();
					am.setEntry(answer.entry);
					am.setCreated(answer.created);
					getEnqueteById(answer.enquete_id).ifPresent((e) -> {
						am.setEnquete(new EnqueteModel(e));
					});
					return am;
				}).collect(Collectors.toList());
				logger.debug("" + answerModels.size());
				home.setAnswers(answerModels);
				return Optional.of(home);
			}
		});
	}

	public Optional<EnqueteModel> getEnqueteWithResultAtTransaction(Long id, String addr) {
		return withTransaction(()->{
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
				if(useropt.isPresent()) {
					available = checkAvailable(useropt.get(), getDealtEnquete().enquete_id);
				}
				for (int i = 0; i < entries.size(); i++) {
					EntryModel entryModel = new EntryModel();
					entryModel.setNumber(entries.get(i).number);
					entryModel.setString(entries.get(i).string);
					if (available) {
						// availableなら
						entryModel.setQuantity(countByEnqueteAndEntry(enquete.id, i + 1));
					}
					entryModels.add(entryModel);
				}
				enqueteModel.setEntries(entryModels);
				return Optional.of(enqueteModel);
			}
		});
	}

	public List<EnqueteModel> searchWithTransaction(final String keyword, final int page) {
		return withTransaction(() -> {
			TypedQuery<Enquete> q = em.createNamedQuery("", Enquete.class);
			
			List<EnqueteModel> enqueteModels = q.getResultList().stream().map((e) -> {
				EnqueteModel em = new EnqueteModel(e);
				return em;
			}).collect(Collectors.toList());
			return enqueteModels;
		});
	}

	public List<EnqueteModel> rankWithTransaction(final int page) {
		return withTransaction(() -> {
			List<EnqueteModel> cacheenquetes = (List<EnqueteModel>) mclient.get("ranking/" + page);
			if (cacheenquetes == null) {
				List<Enquete> enquetes2 = em.createQuery("", Enquete.class).getResultList();
				if (!enquetes2.isEmpty()) {
					List<EnqueteModel> enqmodels = new ArrayList<>();
					for(Enquete e :enquetes2) {
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
		});
	}

	public Optional<Response> createEnquete(String addr, ByteBuffer buffer) {
		return asValidUser(addr, (user) -> {
			if(em.find(Enquete.class, user.id) == null) {
				EnqueteModel enqueteModel;
				try {
					enqueteModel = mapper.readValue(Charset.forName("UTF-8").decode(buffer).toString(), EnqueteModel.class);
					logger.debug("" + enqueteModel.getEntries());
					// e1e2は必須
					if (enqueteModel == null || StringUtils.isEmpty(enqueteModel.getDescription())
							|| enqueteModel.getEntries().size() < 2
							|| StringUtils.isEmpty(enqueteModel.getEntry(0).getString())
							|| StringUtils.isEmpty(enqueteModel.getEntry(1).getString())) {
						logger.debug("不正な値");
						return Optional.of(TsugumonConstants.FORBIDDEN_RESPONSE);
					} else {
						logger.debug(user.ipAddress + "; own:" + user.id);
						//
						Enquete enquete = new Enquete();
						enquete.description = enqueteModel.getDescription();
						enquete.user_id = user.id;
						enquete.language_id = 1;
						em.persist(enquete);
						Enquete newEnquete = em.find(Enquete.class, enquete.user_id);
						List<Entry> entries = enqueteModel.getEntries().stream().map((e) -> {
							Entry entry = new Entry();
							entry.number = e.getNumber();
							entry.string = e.getString();
							entry.enquete_id = newEnquete.id;
							return entry;
						}).collect(Collectors.toList());
						//

						for (Entry entry : entries) {
							em.persist(entry);
						}
						//
						mclient.set("enquete" + enquete.id, TsugumonConstants.MIDDLE_EXPIRATION, newEnquete);
						mclient.set("total/" + enquete.id, TsugumonConstants.MIDDLE_EXPIRATION, 0);
						return Optional.empty();
					}
				} catch (IOException e) {
					logger.debug(e.getLocalizedMessage());
					return Optional.of(TsugumonConstants.FORBIDDEN_RESPONSE);
				}
			} else {
				return Optional.of(TsugumonConstants.CONFLICT_RESPONSE);
			}
		});
	}

	public Optional<Response> putAnswer(String addr, long enqueteId, int entry) {
		return asValidUser(addr, (user) -> {
			logger.debug("user ipaddr:" + user.ipAddress + "; id:" + user.id);
			// アンケートが存在するか確認する
			Optional<Enquete> enqueteOptional = getEnqueteById(enqueteId);
			if (enqueteOptional.isPresent()) {
				List<Answer> answers = getAnswers(user);
				int size = answers.size();
				Optional<Answer> o = answers.stream().filter((answer) -> answer.enquete_id.equals(enqueteId)).findAny();
				if (size <= TsugumonConstants.MAX_SELECT_ANSWER_SIZE + 1 && o.isPresent()) {
					// 既存のアンケートの選択を変える場合
					Answer answer = o.get();
					int oldEntry = answer.entry;
					answer.entry = entry;
					em.persist(answer);
					return Optional.empty();
				} else if (size < TsugumonConstants.MAX_SELECT_ANSWER_SIZE + 1) {
					//
					Answer answer = new Answer();
					answer.entry = entry;
					answer.user_id = user.id;
					answer.enquete_id = enqueteId;
					answer.created = new Timestamp(System.currentTimeMillis());
					em.persist(answer);
					Enquete enquete = em.find(Enquete.class, answer.enquete_id);
					System.out.println(enquete + " " + answer.enquete_id);
					if (enquete.total == null) {
						enquete.total = 0;
					} else {
						enquete.total += 1;
					}
					em.persist(enquete);
					answers.add(answer);
					return Optional.empty();

				} else if (size >= TsugumonConstants.MAX_SELECT_ANSWER_SIZE + 1) {
					//
					return Optional.of(TsugumonConstants.CONFLICT_RESPONSE);
				} else {
					logger.error("条件に漏れがある");
					return Optional.of(TsugumonConstants.SERVER_ERROR_RESPONSE);
				}
			} else {
				return Optional.of(TsugumonConstants.NOT_FOUND_RESPONSE);
			}
		});
	}

	/**
	 * @param addr
	 * @return ステータス アンケートが存在しなければ404 うまくいけば空
	 */
	public Optional<Response> deleteEnqueteAtTransaction(String addr) {
		return asValidUser(addr, (user) -> {
			Optional<Enquete> ret = getEnqueteByUser(user);
			if (ret.isPresent()) {
				Enquete enquete = ret.get();
				TypedQuery<Answer> q = em.createNamedQuery("", Answer.class);
				q.setParameter("", enquete.id);
				q.executeUpdate();
				mclient.delete("enquete/" + enquete.id);
				TypedQuery<Enquete> q2 = em.createNamedQuery("", Enquete.class);
				q2.setParameter("id", enquete.id);
				return Optional.empty();
			} else {
				return Optional.of(TsugumonConstants.NOT_FOUND_RESPONSE);
			}
		});
	}

	public Optional<Response> deleteAnswerAtTransaction(final String addr, final Long enqueteId) {
		return asValidUser(addr, (user) -> {
			Optional<Enquete> optionalEnqute = getEnqueteById(enqueteId);
			if (!optionalEnqute.isPresent()) {
				return Optional.of(TsugumonConstants.NOT_FOUND_RESPONSE);
			}
			List<Answer> answers = getAnswers(user);
			boolean removed = false;
			// enqueteIdを持たないAnswerを取り出す。取り出す過程で持っているものはデータアクセスし、持っていなければ何もせず収集する
			for (int i = 0; i < answers.size(); i++) {
				if (answers.get(i).enquete_id.equals(enqueteId)) {
					Answer answer = answers.get(i);
					if(answerDao.delete(answer) != 1) {
						System.out.println("error");
					}
					Enquete enquete = em.find(Enquete.class, answer.enquete_id);
					if (enquete.total == null) {
						enquete.total = 0;
					} else {
						enquete.total -= 1;
					}
					answers.remove(answer);
					mclient.set("answers/"+enqueteId, TsugumonConstants.MIDDLE_EXPIRATION, answers);
					removed = true;
					break;
				}
			}
			logger.debug("resource answers size:" + answers.size());
			// サイズに変更があったかどうかで対応を変える
			if (removed) {
				return Optional.empty();
			} else {
				logger.debug(answers.toString());
				return Optional.of(TsugumonConstants.SERVER_ERROR_RESPONSE);
			}
		});
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

	int countByEnqueteAndEntry(Long id, int index) {
		Integer count = (Integer) mclient.get("count/" + id + "/" + index);
		if (count == null) {
			em.createNamedQuery(Answer.COUNT, Answer.class);
			count = answerDao.countByEnqueteAndEntry(id, index);
			if (count != null) {
				mclient.set("count/" + id + "/" + index, TsugumonConstants.SHORT_EXPIRATION, count);
				return count;
			}
		}
		return count;
	}

	List<Answer> getAnswers(User user) {
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
		answers = Collections.emptyList();
		return answers;
	}

	int getCountOfAnswers(User user) {
		return 0;
	}

	/**
	 * カウントを込でアンケートを返す TransactionManagerを呼び出される前提のメソッド
	 * 
	 * @throws TsugumonException
	 */
	Optional<EnqueteModel> getEnqueteWithResult(Long id, boolean available) {
		Optional<Enquete> optEnquete = getEnqueteById(id);
		if (!optEnquete.isPresent()) {
			return Optional.empty();
		} else {
			Enquete enquete = optEnquete.get();
			EnqueteModel enqueteModel = new EnqueteModel(enquete);
			List<Entry> entries = getEntries(optEnquete.get());
			List<EntryModel> entryModels = new ArrayList<>(entries.size());
			for (int i = 0; i < entries.size(); i++) {
				EntryModel entryModel = new EntryModel();
				entryModel.setNumber(entries.get(i).number);
				entryModel.setString(entries.get(i).string);
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

	List<Entry> getEntries(Enquete enquete) {
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

	Optional<Enquete> getEnqueteByUser(User user) {
		Enquete enquete = (Enquete) mclient.get("enqueteOf/" + user.id);
		if (enquete == null) {
			TypedQuery<Enquete> q = em.createNamedQuery(Enquete.BY_USER, Enquete.class);
			q.setParameter("",user.id);
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

	Optional<Enquete> getEnqueteById(Long id) {
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

	DealtEnquete getDealtEnquete() {
		DealtEnquete dealtEnquete = (DealtEnquete) mclient.get("dealtEnquete");
		if (dealtEnquete == null) {
			dealtEnquete = dealtEnqueteDao.select();
			if(dealtEnquete != null) {
				logger.debug("dealtEnquete != null");
				mclient.set("dealtEnquete", TsugumonConstants.LONG_EXPIRATION, dealtEnquete);
				return dealtEnquete;
			}
		} else {
			return dealtEnquete;
		}
		return null;
	}

	boolean checkAvailable(User user, Long dealtEnqueteId) {
		if (availableUsers.contains(user.ipAddress)) {
			return true;
		} else {
			boolean available;
			if (availableUsers.contains(user.ipAddress)) {
				available = true;
			} else {
				logger.debug(user.ipAddress + " id:" + user.id);
				List<Answer> answers = getAnswers(user);
				if (answers == null) {
					logger.debug("null");
				} else {
					logger.debug(answers.toString());
				}
				Optional<Answer> answerAny = answers.stream().filter((answer) -> {
					return answer.enquete_id.equals(dealtEnqueteId);
				}).findAny();
				logger.debug("answers.size():" + answers.size() + "; answersAny:" + answerAny.isPresent());
				available = answers.size() == TsugumonConstants.MAX_SELECT_ANSWER_SIZE + 1 && answerAny.isPresent();
				if (available) {
					availableUsers.add(user.ipAddress);
				}
			}
			return available;
		}
	}

	<RESULT> RESULT withTransaction(Supplier<RESULT> s) {
		return s.get();
	}

	private long insertUser(User user) {
		em.persist(user);
		return em.find(User.class, user).id;
	}

	/**
	 * TransactionManagerを使っている
	 */
	Optional<Response> asValidUser(String addr, Function<User, Optional<Response>> func) {
		logger.debug("asValidUser;" + addr);
		em.getTransaction().begin();
		User user = getUserByIpAddress(addr).orElseGet(() -> {
			final User usertmp2 = new User(addr);
			logger.info("user created. ipaddr:" + usertmp2.ipAddress);
			return null;
		});

		User usertmp1;
		logger.debug("user info :" + usertmp1.ipAddress);
		try {
			Optional<Response> ret = func.apply(usertmp1);
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Optional.of(TsugumonConstants.BAD_REQUEST_RESPONSE);
		}
	}

}
