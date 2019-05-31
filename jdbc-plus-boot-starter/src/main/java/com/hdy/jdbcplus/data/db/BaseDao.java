package com.hdy.jdbcplus.data.db;

import com.hdy.jdbcplus.result.PageResults;
import com.hdy.jdbcplus.util.TypeConvert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by hdy on 2019/5/30.
 */
public class BaseDao<T, ID> {
    @Autowired
    private JdbcRepository<T, ID> jdbcRepository;

    private Class<T> tClass;


    @PostConstruct
    public void init() {
        this.tClass = TypeConvert.getClassGenricType(getClass());
    }

    /**
     * 根据ID查实体
     *
     * @param id
     */
    public T get(ID id) {
        return jdbcRepository.get(id, tClass);
    }

    /***
     * 根据某字段查询
     *
     * @param field 字段名
     * @param value 字段值
     */
    public T getByField(String field, Object value) {
        return jdbcRepository.getByField(field, value, tClass);
    }

    /**
     * 根据Sql查询实体
     *
     * @param sql
     */
    public T getBySql(String sql) {
        return jdbcRepository.getBySql(sql, tClass);
    }

    public T getBySql(String sql, Object... params) {
        return jdbcRepository.getBySql(sql, tClass, params);
    }

    public T getBySql(String sql, Map<String, ?> params) {
        return jdbcRepository.getBySql(sql, params, tClass);
    }

    /**
     * 查询所有实体
     */

    public List<T> findAll() {
        return jdbcRepository.findAll(tClass);
    }


    /**
     * 根据Sql查询实体集合
     *
     * @param sql
     */

    public List<T> findBySql(String sql) {
        return jdbcRepository.findBySql(sql, tClass);
    }

    /**
     * 根据Sql查询实体集合
     *
     * @param sql    (sql中的"?"数量必须和参数数量、顺序一致)
     * @param params 参数
     */

    public List<T> findBySql(String sql, Object... params) {
        return jdbcRepository.findBySql(sql, tClass, params);
    }

    /**
     * 根据Sql查询实体集合
     *
     * @param sql    (sql中的"?"数量必须和参数数量、顺序一致)
     * @param params 参数
     */

    public List<T> findBySql(String sql, Map<String, ?> params) {
        return jdbcRepository.findBySql(sql, params, tClass);
    }

    /**
     * 根据Sql查询Map
     *
     * @param sql (sql中的"?"数量必须和参数数量、顺序一致)
     */

    public Map<String, Object> queryForMap(String sql) {
        return jdbcRepository.queryForMap(sql);
    }

    public Map<String, Object> queryForMap(String sql, Object... params) {
        return jdbcRepository.queryForMap(sql, params);
    }

    public Map<String, Object> queryForMap(String sql, Map<String, ?> params) {
        return jdbcRepository.queryForMap(sql, params);
    }

    /**
     * 根据Sql查询集合
     *
     * @param sql
     */
    public List<Map<String, Object>> queryForList(String sql) {
        return jdbcRepository.queryForList(sql);
    }

    public List<Map<String, Object>> queryForList(String sql, Object... params) {
        return jdbcRepository.queryForList(sql, params);
    }

    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> params) {
        return jdbcRepository.queryForList(sql, params);
    }

    /**
     * 分页查询
     *
     * @param sql
     * @param toEntity   结果是否是转实体
     * @param pageNumber 页码
     * @param pageSize   页数量
     * @return
     */
    public PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize) {
        return jdbcRepository.page(sql, toEntity, pageNumber, pageSize, tClass);
    }

    public PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Object... params) {
        return jdbcRepository.page(sql, toEntity, pageNumber, pageSize, tClass, params);
    }

    public PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Map<String, ?> params) {
        return jdbcRepository.page(sql, toEntity, pageNumber, pageSize, params, tClass);
    }

    /**
     * 查询总量
     *
     * @param sql
     * @return
     */
    public Integer count(String sql) {
        return jdbcRepository.count(sql);
    }

    public Integer count(String sql, Object... params) {
        return jdbcRepository.count(sql, params);
    }

    public Integer count(String sql, Map<String, ?> params) {
        return jdbcRepository.count(sql, params);
    }

    /**
     * 执行SQL
     *
     * @param sql
     * @return
     */
    public int execute(String sql) {
        return jdbcRepository.execute(sql);
    }

    public int execute(String sql, Object... params) {
        return jdbcRepository.execute(sql, params);
    }

    public int execute(String sql, Map<String, ?> params) {
        return jdbcRepository.execute(sql, params);
    }

    /**
     * 执行插入SQL
     *
     * @param sql
     * @return
     */
    public int insert(String sql) {
        return jdbcRepository.insert(sql);
    }

    public int insert(String sql, Object... params) {
        return jdbcRepository.insert(sql, params);
    }

    public int insert(String sql, Map<String, ?> params) {
        return jdbcRepository.insert(sql, params);
    }

    /**
     * 执行修改SQL
     *
     * @param sql
     * @return
     */
    public int update(String sql) {
        return jdbcRepository.update(sql);
    }

    public int update(String sql, Object... params) {
        return jdbcRepository.update(sql, params);
    }

    public int update(String sql, Map<String, ?> params) {
        return jdbcRepository.update(sql, params);
    }

    /**
     * 执行删除SQL
     *
     * @param sql
     * @return
     */
    public int delete(String sql) {
        return jdbcRepository.delete(sql);
    }

    public int delete(String sql, Object... params) {
        return jdbcRepository.delete(sql, params);
    }

    public int delete(String sql, Map<String, ?> params) {
        return jdbcRepository.delete(sql, params);
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    public int delete(ID id) {
        return jdbcRepository.delete(id, tClass);
    }

    /**
     * 新增实体
     *
     * @param entity
     * @return
     */
    public T insert(T entity) {
        return jdbcRepository.insert(entity, tClass);
    }

    /**
     * 修改实体
     *
     * @param entity
     * @return
     */
    public T update(T entity) {
        return jdbcRepository.update(entity, tClass);
    }
}
