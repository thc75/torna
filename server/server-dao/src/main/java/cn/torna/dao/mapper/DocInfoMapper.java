package cn.torna.dao.mapper;

import com.gitee.fastmybatis.core.mapper.BaseMapper;
import cn.torna.dao.entity.DocInfo;

/**
 * @author tanghc
 */
public interface DocInfoMapper extends BaseMapper<DocInfo> {

    int saveDocInfo(DocInfo docInfo);

}
