package site.saishin.tsugumon.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * 
 */
@Entity
@Table(name="Enquetes")
@NamedQueries({
	@NamedQuery(name = Enquete.BY_USER, query = "select e from Enquete e where e.user_id = ?1")
})
public class Enquete implements Serializable {
	private static final long serialVersionUID = -8775731913448253476L;
	public static final String BY_USER = "Enquete.byUser";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long user_id;
    public String description;
    public Integer language_id;
    public Integer total = 0;
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
