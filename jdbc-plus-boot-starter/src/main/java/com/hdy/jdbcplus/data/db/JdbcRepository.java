package com.hdy.jdbcplus.data.db;

import com.hdy.jdbcplus.result.PageResults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by hdy on 2019/5/30.
 */
@Component
interface JdbcRepository<T, ID> {


    /**
     * 根据ID查实体
     *
     * @param id
     */
    T get(ID id, Class<T> tClass);

    /***
     * 根据某字段查询
     *
     * @param field 字段名
     * @param value 字段值
     */
    T getByField(String field, Object value, Class<T> tClass);

    /**
     * 根据Sql查询实体
     *
     * @param sql
     */
    T getBySql(String sql, Class<T> tClass);

    T getBySql(String sql, Class<T> tClass, Object... params);

    T getBySql(String sql, Map<String, ?> params, Class<T> tClass);

    /**
     * 查询所有实体
     */

    List<T> findAll(Class<T> tClass);


    /**
     * 根据Sql查询实体集合
     *
     * @param sql
     */

    List<T> findBySql(String sql, Class<T> tClass);

    /**
     * 根据Sql查询实体集合
     *
     * @param sql    (sql中的"?"数量必须和参数数量、顺序一致)
     * @param params 参数
     */

    List<T> findBySql(String sql, Class<T> tClass, Object... params);

    /**
     * 根据Sql查询实体集合
     *
     * @param sql    (sql中的"?"数量必须和参数数量、顺序一致)
     * @param params 参数
     */

    List<T> findBySql(String sql, Map<String, ?> params, Class<T> tClass);

    /**
     * 根据Sql查询Map
     *
     * @param sql (sql中的"?"数量必须和参数数量、顺序一致)
     */

    Map<String, Object> queryForMap(String sql);

    Map<String, Object> queryForMap(String sql, Object... params);

    Map<String, Object> queryForMap(String sql, Map<String, ?> params);

    /**
     * 根据Sql查询集合
     *
     * @param sql
     */
    List<Map<String, Object>> queryForList(String sql);

    List<Map<String, Object>> queryForList(String sql, Object... params);

    List<Map<String, Object>> queryForList(String sql, Map<String, ?> params);

    /**
     * 分页查询
     *
     * @param sql
     * @param toEntity   结果是否是转实体
     * @param pageNumber 页码
     * @param pageSize   页数量
     * @return
     */
    PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Class<T> tClass);

    PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Class<T> tClass, Object... params);

    PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Map<String, ?> params, Class<T> tClass);

    /**
     * 查询总量
     *
     * @param sql
     * @return
     */
    Integer count(String sql);

    Integer count(String sql, Object... params);

    Integer count(String sql, Map<String, ?> params);

    /**
     * 执行SQL
     *
     * @param sql
     * @return
     */
    int execute(String sql);

    int execute(String sql, Object... params);

    int execute(String sql, Map<String, ?> params);

    /**
     * 执行插入SQL
     *
     * @param sql
     * @return
     */
    int insert(String sql);

    int insert(String sql, Object... params);

    int insert(String sql, Map<String, ?> params);

    /**
     * 执行修改SQL
     *
     * @param sql
     * @return
     */
    int update(String sql);

    int update(String sql, Object... params);

    int update(String sql, Map<String, ?> params);

    /**
     * 执行删除SQL
     *
     * @param sql
     * @return
     */
    int delete(String sql);

    int delete(String sql, Object... params);

    int delete(String sql, Map<String, ?> params);

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    int delete(ID id, Class<T> tClass);

    /**
     * 新增实体
     *
     * @param entity
     * @return
     */
    T insert(T entity, Class<T> tClass);

    /**
     * 修改实体
     *
     * @param entity
     * @return
     */
    T update(T entity, Class<T> tClass);
}
