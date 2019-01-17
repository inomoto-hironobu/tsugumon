package site.saishin.tsugumon.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

@Entity
@Table(name="Answers")
public class Answer implements Serializable {
	private static final long serialVersionUID = -2705922864124905350L;
	@Id
	public Long user_id;
	@Id
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