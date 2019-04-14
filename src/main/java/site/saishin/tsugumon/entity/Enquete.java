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
    public String description;
    public Integer language_id;
    public Integer total = 0;
    public Timestamp created;
    public Long user_id;
    public Long client_id;
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Enquete)) {
            return false;
        }
        Enquete other = (Enquete) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[ id=" + id + " ]";
    }
    
}
