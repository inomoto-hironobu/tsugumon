package site.saishin.tsugumon.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

/**
 *
 * 
 */
@Entity
@Table(name="Enquetes")
public class Enquete implements Serializable {
	private static final long serialVersionUID = -8775731913448253476L;
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
