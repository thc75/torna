package cn.torna.dao.mapper;

import cn.torna.dao.entity.DocParam;
import com.gitee.fastmybatis.core.mapper.BaseMapper;

/**
 * @author tanghc
 */
public interface DocParamMapper extends BaseMapper<DocParam> {

    int saveParam(DocParam docParam);

}
