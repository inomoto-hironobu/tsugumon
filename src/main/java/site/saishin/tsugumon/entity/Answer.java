package site.saishin.tsugumon.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="Answers")
@NamedQueries({
	@NamedQuery(name = Answer.COUNT_ALL, query = "select count(a) from Answer a"),
	@NamedQuery(name = Answer.COUNT_BY_ENTRY, query = "select count(a) from Answer a where a.entry = :entry"),
	@NamedQuery(name = Answer.COUNT_BY_USER, query = "select count(a) from Answer a where a.user = :user"),
	@NamedQuery(name = Answer.BY_USER, query = "select a from Answer a where a.user = :user"),
	@NamedQuery(name = Answer.BY_ENTRY, query = "select a from Answer a where a.entry = :entry"),
	@NamedQuery(name = Answer.CHANGE, query = "update Answer a set a.entry = :entry where a.id = :id")
})
public final class Answer implements Serializable {
	private static final long serialVersionUID = -2705922864124905350L;
	public static final String COUNT_ALL = "Answer.countAll";
	public static final String COUNT_BY_ENTRY = "Answer.countByEntry";
	public static final String COUNT_BY_USER = "Answer.countByUser";
	public static final String BY_USER = "Answer.byUser";
	public static final String BY_ENTRY = "Answer.byEntry";
	public static final String CHANGE = "Answer.change";
	public static final String DEL_BY_ENQUETE  = "Answer.delByEnq";
	@Id
	@GeneratedValue
	public Long id;
	@ManyToOne
	public User user;
	@ManyToOne
	@JoinColumn(name="entry_id")
	public Entry entry;
	public Timestamp created;
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Answer)) return false;
		Answer o = (Answer) obj;
		if(o.id == id) return true;
		return super.equals(obj);
	}
}