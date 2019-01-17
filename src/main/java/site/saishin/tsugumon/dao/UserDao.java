package site.saishin.tsugumon.dao;

import org.seasar.doma.AnnotateWith;
import org.seasar.doma.Annotation;
import org.seasar.doma.AnnotationTarget;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;

import site.saishin.tsugumon.entity.User;

@Dao
@AnnotateWith(annotations = {
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR, type = javax.inject.Inject.class),
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR_PARAMETER, type = javax.inject.Named.class, elements = "\"config\"") })
public interface UserDao {

	@Select
	User selectById(Long id);
	
    @Select
    User selectByIpAddress(String ipAddress);

    @Select
    long selectLast();
    
    @Select
    long count();
    
    @Insert
    int insert(User user);
}
