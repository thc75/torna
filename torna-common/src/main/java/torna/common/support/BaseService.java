package torna.common.support;

import torna.common.util.CopyUtil;
import com.gitee.fastmybatis.core.mapper.CrudMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * service基类
 * @author tanghc
 */
public abstract class BaseService<E, Mapper extends CrudMapper<E, Long>> {
    
    @Autowired
    private Mapper mapper;

    public Mapper getMapper() {
        return mapper;
    }

    public PageEasyui<E>  page(Query query) {
        return MapperUtil.queryForEasyuiDatagrid(mapper, query);
    }

    public <T> PageEasyui<T>  page(Query query, Class<T> clazz) {
        return MapperUtil.queryForEasyuiDatagrid(mapper, query, clazz);
    }

    public List<E> listAll() {
        return listAll(new Query());
    }

    public List<E> listAll(Query query) {
        return mapper.list(query.setQueryAll(true));
    }

    public <R> List<R> listAll(Supplier<R> supplier) {
        List<E> list = mapper.list(new Query().setQueryAll(true));
        return CopyUtil.copyList(list, supplier);
    }

    public List<E> list(Query query) {
        return mapper.list(query);
    }

    public List<E> list(String column, Object value) {
        return mapper.listByColumn(column, value);
    }

    public E get(Query query) {
        return mapper.getByQuery(query);
    }

    public E get(String column, Object value) {
        return mapper.getByColumn(column, value);
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    public E getById(Long id) {
        return mapper.getById(id);
    }

    /**
     * 新增，插入所有字段
     *
     * @param entity 新增的记录
     * @return 返回影响行数
     */
    public int save(E entity) {
        return mapper.save(entity);
    }

    /**
     * 新增，忽略null字段
     *
     * @param entity 新增的记录
     * @return 返回影响行数
     */
    public int saveIgnoreNull(E entity) {
        return mapper.saveIgnoreNull(entity);
    }

    /**
     * 修改，修改所有字段
     *
     * @param entity 修改的记录
     * @return 返回影响行数
     */
    public int update(E entity) {
        return mapper.update(entity);
    }

    /**
     * 修改，忽略null字段
     *
     * @param entity 修改的记录
     * @return 返回影响行数
     */
    public int updateIgnoreNull(E entity) {
        return mapper.updateIgnoreNull(entity);
    }

    /**
     * 删除记录
     *
     * @param entity 待删除的记录
     * @return 返回影响行数
     */
    public int delete(E entity) {
        return mapper.delete(entity);
    }
}
