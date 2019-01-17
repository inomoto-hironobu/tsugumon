package site.saishin.tsugumon.dao;

import java.util.List;

import org.seasar.doma.AnnotateWith;
import org.seasar.doma.Annotation;
import org.seasar.doma.AnnotationTarget;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import site.saishin.tsugumon.entity.Enquete;

/**
 * updateｈ
 */
@Dao
@AnnotateWith(annotations = {
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR, type = javax.inject.Inject.class),
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR_PARAMETER, type = javax.inject.Named.class, elements = "\"config\"") })
public interface EnqueteDao {

    @Select
    Enquete selectById(Long id);
    
    @Select
    Enquete selectByUserId(Long user_id);

    @Select
    long selectLast();
    
    @Select
    List<Enquete> search(String keyword, int limit, int offset);
    
    @Select
    List<Enquete> ranking(int limit, int offset);
    
    @Select
    long count();
    
    @Update
    int updateTotal(Enquete enquete);
    
    @Insert(sqlFile=true)
    int insert(Enquete enquete);
    
    @Delete
    int delete(Enquete enquete);
}
