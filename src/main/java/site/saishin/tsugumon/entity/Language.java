package site.saishin.tsugumon.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Languages")
public class Language implements Serializable {
	
	private static final long serialVersionUID = -2978740482605134208L;
	@Id
	@GeneratedValue
	public Integer id;
	public String name;
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Language)) return false;
		Language o = (Language) obj;
		if(id != null && o.id == id) return true;
		return super.equals(obj);
	}
}
