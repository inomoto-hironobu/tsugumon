package site.saishin.tsugumon.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="Entries", uniqueConstraints = @UniqueConstraint(columnNames = {"enquete_id", "number"}))
public class Entry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	public Long id;
	public Long enquete_id;
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