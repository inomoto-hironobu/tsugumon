package site.saishin.tsugumon.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Languages")
public class Language {
	@Id
	public int id;
	public String name;
}
