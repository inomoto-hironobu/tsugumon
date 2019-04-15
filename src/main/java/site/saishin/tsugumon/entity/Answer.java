package site.saishin.tsugumon.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="Answers")
@NamedQueries({
	@NamedQuery(name=Answer.COUNT, query="select count(a) from Answer a"),
	@NamedQuery(name=Answer.BY_USER, query="select a from Answer a where a.user_id = ?1"),
	@NamedQuery(name=Answer.BY_ENTRY, query="select a from Answer a where a.entry_id = ?1")
})
public final class Answer implements Serializable {
	private static final long serialVersionUID = -2705922864124905350L;
	public static final String COUNT = "Answer.count";
	public static final String BY_USER = "Answer.byUser";
	public static final String BY_ENTRY = "Answer.byEntry";
	public static final String DEL_BY_ENQUETE  = "Answer.delByEnq";
	@Id
	@GeneratedValue
	public Long id;
	public Long user_id;
	public Long entry_id;
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