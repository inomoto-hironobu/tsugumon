package site.saishin.tsugumon.logic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.derby.iapi.store.raw.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.entity.Answer;
import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.Entry;
import site.saishin.tsugumon.entity.User;
import site.saishin.tsugumon.model.EnqueteModel;

public class TransactionLogic {
	private static final Logger logger = LoggerFactory.getLogger(TransactionLogic.class);
	@PersistenceContext
	EntityManager em;
	// 許可ユーザ
	private Set<String> availableUsers;
	private MemcachedClient mclient;;
	ObjectMapper mapper = new ObjectMapper();
	TsugumonLogic logic;
	

	public Optional<Response> createEnquete(String addr, ByteBuffer buffer) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
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
			transaction.commit();
		} catch (IOException e) {
			logger.debug(e.getLocalizedMessage());
			transaction.rollback();
			return Optional.of(TsugumonConstants.FORBIDDEN_RESPONSE);
		}
	}
	
	public Optional<Response> putAnswer(String addr, long enqueteId, int entry) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		logger.debug("user ipaddr:" + user.ipAddress + "; id:" + user.id);
		// アンケートが存在するか確認する
		Optional<Enquete> enqueteOptional = logic.getEnqueteById(enqueteId);
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
				transaction.commit();
				return Optional.empty();

			} else if (size >= TsugumonConstants.MAX_SELECT_ANSWER_SIZE + 1) {
				//
				transaction.rollback();
				return Optional.of(TsugumonConstants.CONFLICT_RESPONSE);
			} else {
				transaction.rollback();
				logger.error("条件に漏れがある");
				return Optional.of(TsugumonConstants.SERVER_ERROR_RESPONSE);
			}
		} else {
			transaction.rollback();
			return Optional.of(TsugumonConstants.NOT_FOUND_RESPONSE);
		}
	}
	/**
	 * @param addr
	 * @return ステータス アンケートが存在しなければ404 うまくいけば空
	 */
	public Optional<Response> deleteEnqueteAtTransaction(String addr) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		Optional<Enquete> ret = getEnqueteByUser(user);
		if (ret.isPresent()) {
			Enquete enquete = ret.get();
			TypedQuery<Answer> q = em.createNamedQuery("", Answer.class);
			q.setParameter("", enquete.id);
			q.executeUpdate();
			mclient.delete("enquete/" + enquete.id);
			TypedQuery<Enquete> q2 = em.createNamedQuery("", Enquete.class);
			q2.setParameter("id", enquete.id);
			transaction.commit();
			return Optional.empty();
		} else {
			transaction.rollback();
			return Optional.of(TsugumonConstants.NOT_FOUND_RESPONSE);
		}
	}

	public Optional<Response> deleteAnswerAtTransaction(final String addr, final Long enqueteId) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		Optional<Enquete> optionalEnqute = logic.getEnqueteById(enqueteId);
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
			transaction.commit();
			return Optional.empty();
		} else {
			transaction.rollback();
			logger.debug(answers.toString());
			return Optional.of(TsugumonConstants.SERVER_ERROR_RESPONSE);
		}
	}

}
