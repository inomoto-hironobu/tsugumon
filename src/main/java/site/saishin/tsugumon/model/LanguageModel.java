package site.saishin.tsugumon.model;

import javax.xml.bind.annotation.XmlRootElement;

import site.saishin.tsugumon.entity.Language;

@XmlRootElement
public class LanguageModel {

	private Integer id;
	private String name;
	public LanguageModel(Language language) {
		this.id = language.id;
		this.name = language.name;
	}
	public Integer getId() {
		return id;
	}
	public String getName() {
		return name;
	}
}
