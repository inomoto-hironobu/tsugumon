package site.saishin.tsugumon.entity;

import java.io.Serializable;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

@Entity
@Table(name="Users")
public class User implements Serializable {
	public User(String addr) {
		this.ipAddress = addr;
	}
	public User() {}
	private static final long serialVersionUID = 4537522107715911711L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	public String ipAddress;
}
