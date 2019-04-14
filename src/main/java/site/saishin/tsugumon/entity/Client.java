package site.saishin.tsugumon.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="Clients")
@NamedQueries({
	@NamedQuery(name = Client.BY_ADDR, query ="Select c from Client c where c.emailAddr = ?1")
})
public class Client {
	public static final String BY_ADDR = "Client.byAddr";
	@Id
	public Long id;
	public String emailAddr;
}
