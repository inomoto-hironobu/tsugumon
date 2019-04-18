package site.saishin.tsugumon.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="Entries", uniqueConstraints = @UniqueConstraint(columnNames = {"enquete_id", "number"}))
@NamedQueries({
	@NamedQuery(name=Entry.BY_ENQ, query="select e from Entry e where e.enquete.id = ?1"),
	@NamedQuery(name=Entry.DEL_BY_ENQ, query="delete from Entry e where e.enquete.id = ?1")
})
public final class Entry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String BY_ENQ = "Entry.byEnq";
	public static final String DEL_BY_ENQ = "Entry.delByEnq";
	@Id
	@GeneratedValue
	public Long id;
	@ManyToOne
	@JoinColumn(name="enquete_id")
	public Enquete enquete;
	public Integer number;
	public String content;

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Entry)) return false;
		Entry o = (Entry) obj;
		if(id != null && o.id == id) return true;
		return super.equals(obj);
	}
}