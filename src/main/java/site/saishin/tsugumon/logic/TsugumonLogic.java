package site.saishin.tsugumon.logic;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.entity.Answer;
import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.Entry;
import site.saishin.tsugumon.entity.User;

public class TsugumonLogic {

	private static final Logger logger = LoggerFactory.getLogger(TsugumonLogic.class);
	@Inject
	@PersistenceContext
	EntityManager em;

	@Inject
	private MemcachedClient mclient;
	private boolean alive;
	ObjectMapper mapper = new ObjectMapper();

	public TsugumonLogic() {
		logger.info("inited");
	}

	@PostConstruct
	public void pc() {
		alive = mclient.getConnection().isAlive();
	}

	public List<Enquete> search(final String keyword, final int page) {
		TypedQuery<Enquete> q = em.createNamedQuery(Enquete.SEARCH, Enquete.class);
		q.setMaxResults(TsugumonConstants.MAX_PAGE_SIZE);
		q.setFirstResult(page);
		return q.getResultList();
	}

	public int countByEnqueteAndEntry(Long id, int index) {
		Integer count = (Integer) mclient.get("count/" + id + "/" + index);
		if (count == null) {
			TypedQuery<Answer> q = em.createNamedQuery(Answer.COUNT_ALL, Answer.class);
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

	public Optional<User> getUserByIpAddress(String addr) {
		User user = null;
		if(alive) {
			user = (User) mclient.get("useraddr/" + addr);
		}
		
		if (user == null) {
			TypedQuery<User> q = em.createNamedQuery(User.BY_ADDR, User.class);
			q.setParameter(1, addr);
			user = q.getSingleResult();
			if (user != null) {
				if(alive) {
					mclient.set("useraddr/", TsugumonConstants.SHORT_EXPIRATION, user);
				}
				return Optional.of(user);
			}
		} else {
			return Optional.of(user);
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

	public Optional<Enquete> getEnqueteByUser(User user) {
		Enquete enquete = (Enquete) mclient.get("enqueteOf/" + user.id);
		if (enquete == null) {
			TypedQuery<Enquete> q = em.createNamedQuery(Enquete.BY_USER, Enquete.class);
			q.setParameter(1, user.id);
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

	public List<Entry> getEntries(Enquete enquete) {
		@SuppressWarnings("unchecked")
		List<Entry> entries = (List<Entry>) mclient.get("entries/" + enquete.id);
		if (entries == null) {
			TypedQuery<Entry> q = em.createNamedQuery("", Entry.class);
			q.setParameter(1, enquete.id);
			entries = q.getResultList();
			mclient.set("entries/" + enquete.id, TsugumonConstants.MIDDLE_EXPIRATION, entries);
		}
		return entries;
	}

	public List<Answer> getAnswers(User user) {
		List<Answer> answers = (List<Answer>) mclient.get("answers/" + user.id);
		if (answers == null) {
			TypedQuery<Answer> q = em.createNamedQuery(Answer.BY_USER, Answer.class);
			q.setParameter(1, user.id);
			answers = q.getResultList();
			mclient.set("answers/" + user.id, TsugumonConstants.SHORT_EXPIRATION, answers);
		}
		return answers;
	}

	public int countAnswers(User user) {
		Query q = em.createNamedQuery(Answer.COUNT_BY_USER);
		q.setParameter("user", user);
		return ((Integer) q.getSingleResult()).intValue();
	}

	public Integer countAnswersByEntry(Long entry_id) {
		Query q = em.createNamedQuery(Answer.COUNT_BY_ENTRY);
		q.setParameter("entry", em.find(Entry.class, entry_id));
		return (Integer) q.getSingleResult();
	}
}
