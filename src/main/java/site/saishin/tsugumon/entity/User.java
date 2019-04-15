package site.saishin.tsugumon.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="Users")
@NamedQueries({
	@NamedQuery(name=User.BY_ADDR,query="select u from User u where u.ipAddress = ?1")
})
public class User implements Serializable {
	public static final String BY_ADDR = "User.byAddr";
	private static final long serialVersionUID = 4537522107715911711L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	public String ipAddress;
	public User(String addr) {
		this.ipAddress = addr;
	}
	public User() {}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof User)) return false;
		User o = (User) obj;
		if(id != null && o.id == id) return true;
		return super.equals(obj);
	}
}
