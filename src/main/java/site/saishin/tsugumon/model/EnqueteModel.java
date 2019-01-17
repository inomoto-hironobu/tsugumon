package site.saishin.tsugumon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.text.StringEscapeUtils;

import site.saishin.tsugumon.entity.Enquete;

@XmlRootElement
public class EnqueteModel implements Serializable {
	
	private static final long serialVersionUID = 8906836091614451462L;

	public EnqueteModel(Enquete enquete) {
		this.id = enquete.id;
		this.description = enquete.description;
		this.total = enquete.total;
		this.created = enquete.created;
		this.language_id = enquete.language_id;
	}
	public EnqueteModel() {}
    private Long id;
	private String description;
    private List<EntryModel> entries = new ArrayList<>();
    private Integer total;
	private Timestamp created;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<EntryModel> getEntries() {
		return entries;
	}
	public void setEntries(List<EntryModel> entries) {
		this.entries = entries;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Integer getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(Integer language_id) {
		this.language_id = language_id;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	private Integer language_id;

	public Integer getTotal() {
		if(total == null) {
			return 0;
		}
		return total;
	}
	
	@XmlTransient
    public String getDescriptionEscaped() {
    	return StringEscapeUtils.escapeXml10(description);
    }
    public Integer getQuantity(int num) {
    	return getEntry(num).getQuantity();
    }
    public String getStringEscaped(int num) {
    	return StringEscapeUtils.escapeXml10(getEntry(num).getString());
    }
    public EntryModel getEntry(int index) {
    	if(index < 0 && index >= entries.size()) return null;
    	return entries.get(index);
    }
    @XmlAttribute
    public String getTitle() {
    	return StringEscapeUtils.escapeXml10(description.substring(0, description.length() < 20 ? description.length() : 20));
    }
    public EnqueteModel cloneNoCount() {
    	return null;
    }
}
