package site.saishin.tsugumon.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.text.StringEscapeUtils;


@XmlRootElement
public class EntryModel implements Serializable {
	private static final long serialVersionUID = 324557935070714424L;
	private int number;
	private String string;
	private Integer quantity;
	public String getStringEscaped() {
		return StringEscapeUtils.escapeXml10(string);
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}