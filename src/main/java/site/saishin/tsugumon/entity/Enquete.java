package site.saishin.tsugumon.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * 
 */
@Entity
@Table(name="Enquetes")
@NamedQueries({
	@NamedQuery(name = Enquete.COUNT_ALL, query = "select count(eq) from Enquete eq"),
	@NamedQuery(name = Enquete.COUNT_BY_LANG, query = "select count(eq) from Enquete eq where eq.language = :lang"),
	@NamedQuery(name = Enquete.BY_USER, query = "select eq from Enquete eq where eq.user = :user"),
})
public class Enquete implements Serializable {
	private static final long serialVersionUID = -8775731913448253476L;
	public static final String BY_USER = "Enquete.byUser";
	public static final String COUNT_ALL = "Enquete.count";
	public static final String COUNT_BY_LANG = "Enquete.countByLang";
	public static final String SEARCH = "Enquete.search";
	public static final String DEL_BY_USER = "Enquete.del";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @OneToOne
    public User user;
    @OneToMany(mappedBy="enquete")
    public List<Entry> entries;
    public String description;
    @ManyToOne
    @JoinColumn(name="language_id")
    public Language language;
    public Timestamp created;
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Enquete)) return false;
        Enquete o = (Enquete) obj;
        if (id != null && o.id == id) return true;
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "[ id=" + id + " ]";
    }
    
}
