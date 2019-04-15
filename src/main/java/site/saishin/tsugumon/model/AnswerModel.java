package site.saishin.tsugumon.model;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import site.saishin.tsugumon.entity.Entry;


@XmlRootElement
public class AnswerModel {
	private EnqueteModel enquete;
	private Entry entry;
	private Timestamp created;
	public EnqueteModel getEnquete() {
		return enquete;
	}
	public void setEnquete(EnqueteModel enquete) {
		this.enquete = enquete;
	}
	public Entry getEntry() {
		return entry;
	}
	public void setEntry(Entry entry2) {
		this.entry = entry2;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	
}
