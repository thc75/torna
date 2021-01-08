package torna.dao.mapper;

import com.gitee.fastmybatis.core.mapper.CrudMapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import torna.dao.entity.DocSnapshot;

import java.util.List;

/**
 * @author tanghc
 */
public interface DocSnapshotMapper extends CrudMapper<DocSnapshot, Long> {

    @Select("SELECT id, modifier_name modifierName, modifier_time modifierTime " +
            "FROM doc_snapshot WHERE doc_id=#{docId} ORDER BY id DESC LIMIT 100")
    @ResultType(DocSnapshot.class)
    List<DocSnapshot> listDocSnapshotBaseInfo(long docId);
	
}