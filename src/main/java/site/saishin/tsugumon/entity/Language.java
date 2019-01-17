package site.saishin.tsugumon.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

@Entity
@Table(name="Languages")
public class Language {
	@Id
	public int id;
	public String name;
}
