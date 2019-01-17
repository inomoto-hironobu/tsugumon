package site.saishin.tsugumon.entity;

import java.io.Serializable;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

@Entity
@Table(name="Entries")
public class Entry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	public Long enquete_id;
	@Id
	public Integer number;
	public String string;

	@Override
	public int hashCode() {
		return enquete_id.hashCode() + number.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Entry) {
			Entry entry = (Entry) obj;
			if(entry.number == this.number && entry.enquete_id == this.enquete_id) {
				return true;
			}
		}
		return super.equals(obj);
	}
}