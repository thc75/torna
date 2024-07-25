package cn.torna.common.support;

import com.gitee.fastmybatis.core.ext.MapperRunner;
import com.gitee.fastmybatis.core.mapper.BaseMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.LambdaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * service基类
 *
 * @author tanghc
 */
public abstract class BaseService<E, Mapper extends BaseMapper<E>> implements LambdaService<E, Mapper> {

    private static final Map<Object, MapperRunner<?>> cache = new ConcurrentHashMap<>(64);

    @Autowired
    private Mapper mapper;

    public Mapper getMapper() {
        return mapper;
    }

    @Override
    public MapperRunner<Mapper> getMapperRunner() {
        return (MapperRunner<Mapper>) cache.computeIfAbsent(getMapper(), mapper -> new MapperRunner<>(mapper, null));
    }

    /**
     * 查询全部
     *
     * @return 返回全部数据，不包括已删除的，没有返回空集合
     */
    public List<E> listAll() {
        return list(new Query());
    }

}
