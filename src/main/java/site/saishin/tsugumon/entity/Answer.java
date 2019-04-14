package site.saishin.tsugumon.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="Answers")
@NamedQueries({
	@NamedQuery(name = Answer.COUNT,query="")
})
public class Answer implements Serializable {
	private static final long serialVersionUID = -2705922864124905350L;
	public static final String COUNT = "Answer.count";
	@Id
	public Long id;
	public Long user_id;
	public Long enquete_id;
	public Integer entry;
	public Timestamp created;
	@Override
	public int hashCode() {
		return user_id.hashCode() + enquete_id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Answer) {
			Answer answer = (Answer) obj;
			if(answer.user_id == this.user_id && answer.enquete_id == this.enquete_id) {
				return true;
			}
		}
		return super.equals(obj);
	}
}