package site.saishin.tsugumon.entity;

import java.io.Serializable;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;

@Entity
public class DealtEnquete implements Serializable {
	private static final long serialVersionUID = -3538202383386690487L;
	@Id
    public Long enquete_id;
}