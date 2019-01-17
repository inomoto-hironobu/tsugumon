package site.saishin.tsugumon.dao;

import java.util.List;

import org.seasar.doma.AnnotateWith;
import org.seasar.doma.Annotation;
import org.seasar.doma.AnnotationTarget;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;

import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.Entry;

@Dao
@AnnotateWith(annotations = {
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR, type = javax.inject.Inject.class),
	    @Annotation(target = AnnotationTarget.CONSTRUCTOR_PARAMETER, type = javax.inject.Named.class, elements = "\"config\"") })
public interface EntryDao {

	@Select
	List<Entry> selectByEnquetId(Long enquete_id);
	
	@Insert
	int insert(Entry entry);
	
    @Delete(sqlFile=true)
    int deleteByEnquete(Enquete enquete);

}
