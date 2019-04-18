package site.saishin.tsugumon.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.text.StringEscapeUtils;

import site.saishin.tsugumon.entity.Entry;


@XmlRootElement
public class EntryModel implements Serializable {
	private static final long serialVersionUID = 324557935070714424L;
	private Long id;
	private EnqueteModel enquete;
	private int number;
	private String content;
	private Integer quantity;
	public EntryModel(Entry entry) {
		this.id = entry.id;
		this.number = entry.number;
		this.content = entry.content;
		this.enquete = new EnqueteModel(entry.enquete);
	}
	public EnqueteModel getEnquete() {
		return enquete;
	}
	public void setEnquete(EnqueteModel enquete) {
		this.enquete = enquete;
	}
	public String getStringEscaped() {
		return StringEscapeUtils.escapeXml10(content);
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Long getId() {
		return id;
	}
	
}