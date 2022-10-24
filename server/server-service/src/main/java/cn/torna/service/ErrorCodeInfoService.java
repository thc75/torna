package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ErrorCodeInfo;
import cn.torna.dao.mapper.ErrorCodeInfoMapper;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author tanghc
 */
@Service
public class ErrorCodeInfoService extends BaseService<ErrorCodeInfo, ErrorCodeInfoMapper> {

    public String getDocErrorCode(long docId) {
        Query query = new Query().eq("doc_id", docId);
        return this.getMapper().getBySpecifiedColumns(Collections.singletonList("content"), query, String.class);
    }

}