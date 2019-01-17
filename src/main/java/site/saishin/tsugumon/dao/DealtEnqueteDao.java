package site.saishin.tsugumon.dao;

import org.seasar.doma.AnnotateWith;
import org.seasar.doma.Annotation;
import org.seasar.doma.AnnotationTarget;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import site.saishin.tsugumon.entity.DealtEnquete;

@Dao
@AnnotateWith(annotations = {
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR, type = javax.inject.Inject.class),
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR_PARAMETER, type = javax.inject.Named.class, elements = "\"config\"")
	    }
)
public interface DealtEnqueteDao {
	@Select
	DealtEnquete select();
	@Update
	int update(DealtEnquete dealtEnquete);
}
