package site.saishin.tsugumon.model;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import site.saishin.tsugumon.entity.Answer;


@XmlRootElement
public class AnswerModel {
	private EntryModel entry;
	private Timestamp created;
	public AnswerModel(Answer answer) {
		this.entry = new EntryModel(answer.entry);
		this.created = answer.created;
	}
	public EntryModel getEntry() {
		return entry;
	}
	public void setEntry(EntryModel entry) {
		this.entry = entry;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	
}
