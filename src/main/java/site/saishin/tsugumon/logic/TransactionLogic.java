package site.saishin.tsugumon.logic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.entity.Answer;
import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.Entry;
import site.saishin.tsugumon.entity.Language;
import site.saishin.tsugumon.entity.User;
import site.saishin.tsugumon.model.EnqueteModel;
import site.saishin.tsugumon.model.UserModel;

public final class TransactionLogic {
	private static final Logger logger = LoggerFactory.getLogger(TransactionLogic.class);
	@PersistenceContext
	EntityManager em;
	ObjectMapper mapper = new ObjectMapper();
	TsugumonLogic logic;

	public Optional<Response> createUser(String addr) {
		em.getTransaction().begin();
		if(!logic.getUserByIpAddress(addr).isPresent()) {
			User user = new User(addr);
			em.persist(user);
			em.getTransaction().commit();
			return Optional.of(Response.status(Status.CREATED).entity(new UserModel(user)).build());
		}
		em.getTransaction().rollback();
		return Optional.of(TsugumonConstants.CONFLICT_RESPONSE);
	}
	public Optional<Response> createEnquete(String addr, ByteBuffer buffer) {
		return ifUser(addr, (user, tx) -> {
			logger.debug(user.ipAddress + "; own:" + user.id);
			try {
				EnqueteModel enqueteModel = mapper.readValue(Charset.forName("UTF-8").decode(buffer).toString(), EnqueteModel.class);
				logger.debug("" + enqueteModel.getEntries());
				// e1e2は必須
				if (enqueteModel == null
						|| enqueteModel.getLanguage() == null
						|| enqueteModel.getLanguage().getId() == null
						|| StringUtils.isEmpty(enqueteModel.getDescription())
						|| enqueteModel.getEntries() == null
						|| enqueteModel.getEntries().size() < 2
						|| StringUtils.isEmpty(enqueteModel.getEntry(0).getContent())
						|| StringUtils.isEmpty(enqueteModel.getEntry(1).getContent())) {
					logger.debug("不正な値");
					tx.rollback();
					return Optional.of(TsugumonConstants.FORBIDDEN_RESPONSE);
				} else {
					//正常な値
					Enquete enquete = new Enquete();
					enquete.user = user;
					enquete.description = enqueteModel.getDescription();
					enquete.language = em.find(Language.class, enqueteModel.getLanguage().getId());
					enquete.entries = new ArrayList<>();
					enqueteModel.getEntries().forEach(em -> {
						Entry entry = new Entry();
						entry.content = em.getContent();
						entry.enquete = enquete;					
						entry.number = em.getNumber();
						enquete.entries.add(entry);
					});
					em.persist(enquete);
					tx.commit();
					//
					return Optional.empty();
				}
			} catch (IOException e) {
				logger.debug(e.getLocalizedMessage());
				tx.rollback();
				return Optional.of(TsugumonConstants.FORBIDDEN_RESPONSE);
			}
		});
		
	}

	/**
	 * @param addr
	 * @return ステータス アンケートが存在しなければ404 うまくいけば空
	 */
	public Optional<Response> deleteEnquete(String addr) {
		return ifUser(addr, (user, tx) -> {
			Optional<Enquete> enqopt = logic.getEnqueteByUser(user);
			if(enqopt.isPresent()) {
				Query q = em.createNamedQuery(Enquete.DEL_BY_USER);
				q.setParameter("user", user);
				q.executeUpdate();
				tx.commit();
				return Optional.empty();
			} else {
				tx.rollback();
				return TsugumonConstants.NOT_FOUND_RESPONSE_OPTION;
			}
		});
	}
	public Optional<Response> createAnswer(String addr, Long enqueteId, Integer entryNum) {
		return ifUser(addr, (user, tx) -> {
			if(user.answers.size() > TsugumonConstants.MAX_SELECT_ANSWER_SIZE) {
				return Optional.of(TsugumonConstants.FORBIDDEN_RESPONSE);
			}
			// アンケートが存在するか確認する
			Optional<Enquete> optEnq = logic.getEnqueteById(enqueteId);
			if(optEnq.isPresent()) {
				//すでにそのアンケートに答えているか調べる
				Optional<Answer> optAns = user
						.answers
						.stream()
						.filter(a -> {
							return a.entry.enquete.id.equals(enqueteId);
						})
						.findAny();
				if(optAns.isPresent()) {
					return Optional.of(TsugumonConstants.CONFLICT_RESPONSE);
				} else {
					//指定されたEntryがあるか調べる
					Optional<Entry> optEnt = optEnq
							.get()
							.entries
							.stream()
							.filter(e -> e.number.equals(entryNum))
							.findAny();
					if(optEnt.isPresent()) {
						Answer answer = new Answer();
						answer.user = user;
						answer.entry = optEnt.get();
						em.persist(answer);
						tx.commit();
						return Optional.empty();
					}
				}
			}
			return TsugumonConstants.NOT_FOUND_RESPONSE_OPTION;
		});
	}
	public Optional<Response> changeAnswer(String addr, long enqueteId, int entryNum) {
		logger.debug("user ipaddr:{}; id:{} {}", addr, enqueteId, entryNum);
		return ifUser(addr, (user, tx) -> {
			// アンケートが存在するか確認する
			Optional<Enquete> optEnq = logic.getEnqueteById(enqueteId);
			if (optEnq.isPresent()) {
				Optional<Entry> optEnt = logic
						.getEntries(optEnq.get())
						.stream()
						.filter(e -> e.number == entryNum)
						.findAny();
				//エントリーが存在するか確認する
				if(optEnt.isPresent()) {
					Entry e = optEnt.get();
					Optional<Answer> optAns = logic
					.getAnswers(user)
					.stream()
					.filter(a -> {
						return a.entry.equals(optEnt.get());
					})
					.findAny();
					if(optAns.isPresent()) {
						TypedQuery<Answer> q = em.createNamedQuery(Answer.CHANGE, Answer.class);
						q.setParameter(1, e.id);
						q.setParameter(2, optAns.get().id);
						q.executeUpdate();
						tx.commit();
						return Optional.empty();
					}
				}
			}
			return TsugumonConstants.NOT_FOUND_RESPONSE_OPTION;
		}); 
	}

	public Optional<Response> deleteAnswer(final String addr, final Long enqueteId) {
		return ifUser(addr, (user, tx) -> {
			Optional<Enquete> enqopt = logic.getEnqueteById(enqueteId);
			if (!enqopt.isPresent()) {
				return Optional.of(TsugumonConstants.NOT_FOUND_RESPONSE);
			}
			Optional<Answer> optans = user
					.answers
					.stream()
					.filter(a -> {
						return a.entry.enquete.id.equals(enqueteId);
					})
					.findAny();
			if(optans.isPresent()) {
				Query q = em.createNamedQuery(Answer.DEL_BY_ENQUETE);
				q.setParameter("enquete", enqopt.get());
				q.executeUpdate();
				tx.commit();
				return Optional.empty();
			} else {
				return TsugumonConstants.NOT_FOUND_RESPONSE_OPTION;
			}
		});
		
	}

	/**
	 * {@link EntityTransaction}を開始する
	 * @param addr
	 * @param func 正常なら空のOptionalをそうでなければ別のResponseを返すことを期待する
	 * @return addrの{@link User}が存在するならfuncの返すOptionalを返す
	 */
	public Optional<Response> ifUser(String addr, BiFunction<User, EntityTransaction, Optional<Response>> func) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		Optional<User> opt = logic.getUserByIpAddress(addr);
		if(!opt.isPresent()) {
			return func.apply(opt.get(), transaction);
		}
		return Optional.of(TsugumonConstants.FORBIDDEN_RESPONSE);
	}
}
