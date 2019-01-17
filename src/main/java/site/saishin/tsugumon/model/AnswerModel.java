package site.saishin.tsugumon.model;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class AnswerModel {
	private EnqueteModel enquete;
	private Integer entry;
	private Timestamp created;
	public EnqueteModel getEnquete() {
		return enquete;
	}
	public void setEnquete(EnqueteModel enquete) {
		this.enquete = enquete;
	}
	public Integer getEntry() {
		return entry;
	}
	public void setEntry(Integer entry) {
		this.entry = entry;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	
}
