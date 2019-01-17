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

import site.saishin.tsugumon.entity.Answer;
import site.saishin.tsugumon.entity.Enquete;

@Dao
@AnnotateWith(annotations = {
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR, type = javax.inject.Inject.class),
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR_PARAMETER, type = javax.inject.Named.class, elements = "\"config\"") })
public interface AnswerDao {

    @Select
    List<Answer> selectByEnqueteId(Long enquete_id);

    @Select
    List<Answer> selectByUser(Long user_id);

    @Select
    long count();
    
    @Select
    int countByEnqueteAndEntry(Long enquete_id, int entry);
    
    @Select
    int countByEnquete(Long enquete_id);
    
    @Select
    int countByUser(Long user_id);
    
    @Insert
    int insert(Answer answer);

    @Update
    int update(Answer answer);

    @Delete(sqlFile=true)
    int deleteByEnquete(Enquete enquete);
    
    @Delete
    int delete(Answer answer);
}
