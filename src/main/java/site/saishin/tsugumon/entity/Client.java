package site.saishin.tsugumon.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Table;

@Entity
@Table(name="Clients")
public class Client {
	public Long id;
	public String email;
}
