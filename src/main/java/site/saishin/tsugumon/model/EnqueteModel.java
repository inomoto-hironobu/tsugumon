package site.saishin.tsugumon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.text.StringEscapeUtils;

import site.saishin.tsugumon.entity.Enquete;

@XmlRootElement
public class EnqueteModel implements Serializable {
	
	private static final long serialVersionUID = 8906836091614451462L;
    private Long id;
	private LanguageModel language;
	private String description;
    private List<EntryModel> entries = new ArrayList<>();
    private Integer total;
	private Timestamp created;

	public EnqueteModel(Enquete enquete) {
		this.id = enquete.id;
		this.description = enquete.description;
		this.entries = enquete.entries
				.stream()
				.map(e -> {
					return new EntryModel(e);
				})
				.collect(Collectors.toList());
		this.created = enquete.created;
		this.language = new LanguageModel(enquete.language);
	}
	public EnqueteModel() {}
	
	public Long getId() {
		return id;
	}
	public LanguageModel getLanguage() {
		return language;
	}
	public String getDescription() {
		return description;
	}
	public List<EntryModel> getEntries() {
		return entries;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
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
    public String getContentEscaped(int num) {
    	return StringEscapeUtils.escapeXml10(getEntry(num).getContent());
    }
    public EntryModel getEntry(int index) {
    	if(index < 0 && index >= entries.size()) return null;
    	return entries.get(index);
    }
    @XmlAttribute
    public String getTitle() {
    	return StringEscapeUtils.escapeXml10(description.substring(0, description.length() < 20 ? description.length() : 20));
    }
}
