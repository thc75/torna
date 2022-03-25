package cn.torna.common.support;

import cn.torna.common.util.CopyUtil;
import com.gitee.fastmybatis.core.mapper.CrudMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * service基类
 *
 * @author tanghc
 */
public abstract class BaseService<E, Mapper extends CrudMapper<E, Long>> {

    @Autowired
    private Mapper mapper;

    public Mapper getMapper() {
        return mapper;
    }

    /**
     * 分页查询
     * @param query 查询条件
     * @return 分页内容
     */
    public PageEasyui<E> page(Query query) {
        return MapperUtil.queryForEasyuiDatagrid(mapper, query);
    }

    /**
     * 分页查询，指定转换类
     * @param query 查询条件
     * @param clazz 转换类class，会将结果转换成对应类
     * @param <T> 对应类
     * @return 分页内容
     */
    public <T> PageEasyui<T> page(Query query, Class<T> clazz) {
        return MapperUtil.queryForEasyuiDatagrid(mapper, query, clazz);
    }

    /**
     * 查询全部
     * @return 返回全部数据，不包括已删除的，没有返回空集合
     */
    public List<E> listAll() {
        return listAll(new Query());
    }

    /**
     * 根据条件查询全部数据
     * @param query 查询条件
     * @return 返回对应条件下的全部数据，不包括已删除的，没有返回空集合
     */
    public List<E> listAll(Query query) {
        return mapper.list(query.setQueryAll(true));
    }

    /**
     * 查询全部，并对结果集进行转换
     * @param supplier 转换类
     * @param <R>
     * @return 返回转换后的结果，没有返回空集合
     */
    public <R> List<R> listAll(Supplier<R> supplier) {
        List<E> list = mapper.list(new Query().setQueryAll(true));
        return CopyUtil.copyList(list, supplier);
    }

    /**
     * 根据条件查询
     * @param query 查询条件
     * @return 返回结果集，没有返回空集合
     */
    public List<E> list(Query query) {
        return mapper.list(query);
    }

    /**
     * 根据某个字段查询结果
     * @param column 数据库字段名
     * @param value 查询值
     * @return 返回结果集，没有返回空集合
     */
    public List<E> list(String column, Object value) {
        return mapper.listByColumn(column, value);
    }

    /**
     * 根据某个字段集合查询结果，即 IN 查询
     * @param column 数据库字段名
     * @param values 查询值
     * @return 返回结果集，没有返回空集合
     */
    public List<E> listByCollection(String column, Collection<?> values) {
        return mapper.listByCollection(column, values);
    }

    /**
     * 只返回id列
     * @param query 查询条件
     * @return id列表
     */
    public List<Long> listId(Query query) {
        return mapper.listBySpecifiedColumns(Collections.singletonList("id"), query, Long.class);
    }

    /**
     * 根据条件查询单条记录
     * @param query 查询条件
     * @return 返回单条记录，没有返回null
     */
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
        if (id == null) {
            return null;
        }
        return mapper.getById(id);
    }

    /**
     * 新增，忽略null字段
     *
     * @param entity 新增的记录
     * @return 返回影响行数
     */
    public int save(E entity) {
        return mapper.saveIgnoreNull(entity);
    }

    /**
     * 批量添加
     *
     * @param entityList 添加对象
     * @return 返回影响行数
     */
    public int saveBatch(List<E> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return 0;
        }
        return mapper.saveBatch(entityList);
    }

    /**
     * 修改，忽略null字段
     *
     * @param entity 修改的记录
     * @return 返回影响行数
     */
    public int update(E entity) {
        return mapper.updateIgnoreNull(entity);
    }

    /**
     * 删除记录
     *
     * @param entity 待删除的记录
     * @return 返回影响行数
     */
    public int delete(E entity) {
        if (entity == null) {
            return 0;
        }
        return mapper.delete(entity);
    }

    /**
     * 删除记录
     *
     * @param id id
     * @return 返回影响行数
     */
    public int deleteById(Long id) {
        if (id == null) {
            return 0;
        }
        return mapper.deleteById(id);
    }


}
