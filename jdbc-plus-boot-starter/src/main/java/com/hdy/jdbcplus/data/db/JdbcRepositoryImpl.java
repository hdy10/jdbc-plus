package com.hdy.jdbcplus.data.db;

import com.hdy.jdbcplus.data.annotation.Entity;
import com.hdy.jdbcplus.data.annotation.Fields;
import com.hdy.jdbcplus.data.annotation.Id;
import com.hdy.jdbcplus.log.SqlLogs;
import com.hdy.jdbcplus.log.SqlStatementType;
import com.hdy.jdbcplus.log.Sqls;
import com.hdy.jdbcplus.result.PageResults;
import com.hdy.jdbcplus.result.PageUtil;
import com.hdy.jdbcplus.util.TypeConvert;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * JdbcTemplate 简单封装
 *
 * @author 贺鹏
 */
@Service
public class JdbcRepositoryImpl<T, ID> implements JdbcRepository<T, ID> {
    private final Logger logger = LogManager.getLogger(JdbcRepositoryImpl.class.getName());

    @Autowired
    @Qualifier("JdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 开始事务
     */
    public void beginTransaction() {
        logger.info("----------------------  手动开启事务操作  ----------------------");
        try {
            jdbcTemplate.getDataSource().getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 回滚
     */
    public void rollBack() {
        logger.info("----------------------  Error:100  操作失败,进行回滚操作  ----------------------");
        Connection conn = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != conn)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取实体Class
     *
     * @param clazz
     */
    private String getTable(Class<?> clazz) {
        String table_name = null;
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getAnnotation(Entity.class);
            table_name = entity.name();
            if (TypeConvert.isNull(table_name)) {
                logger.error(" miss annotation name : {} ", " There are comments, but no notes are found, the default table name ");
                table_name = clazz.getName();
            }
        } else {
            logger.error(" miss table annotation : {} ", " Could not find the corresponding comment, use the table name by default table name ");
            table_name = clazz.getName();
        }
        if (TypeConvert.isNull(table_name))
            logger.error(" table annotation should be have [ name ] param : {} ", " Failed to get table name ");
        return table_name;
    }

    /**
     * 根据ID查实体
     *
     * @param id
     */
    public T get(ID id, Class<T> tClass) {
        String sql = "select * from " + getTable(tClass) + " where id = ? limit 1";
        List<T> list = findBySql(sql, tClass, id);
        if (list == null && list.size() == 0)
            return null;
        return list.get(0);
    }

    /***
     * 根据某字段查询
     *
     * @param field 字段名
     * @param value 字段值
     */
    public T getByField(String field, Object value, Class<T> tClass) {
        String sql = "select * from " + getTable(tClass) + " where " + field + " = ? limit 1";
        List<T> list = findBySql(sql, tClass, value);
        if (list == null && list.size() == 0)
            return null;
        return list.get(0);
    }

    /**
     * 根据Sql查询实体
     *
     * @param sql
     */
    public T getBySql(String sql, Class<T> tClass) {
        List<T> list = findBySql(sql, tClass);
        if (list == null && list.size() == 0)
            return null;
        return list.get(0);
    }

    public T getBySql(String sql, Class<T> tClass, Object... params) {
        List<T> list = findBySql(sql, tClass, params);
        if (list == null && list.size() == 0)
            return null;
        return list.get(0);
    }

    public T getBySql(String sql, Map<String, ?> params, Class<T> tClass) {
        List<T> list = findBySql(sql, params, tClass);
        if (list == null && list.size() == 0)
            return null;
        return list.get(0);
    }


    /**
     * 查询所有实体
     */

    public List<T> findAll(Class<T> tClass) {
        long start = SqlLogs.getCurrentTimeMillis();
        String sql = "select * from " + getTable(tClass);
        List<T> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(tClass));
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start);
        return result;
    }


    /**
     * 根据Sql查询实体集合
     *
     * @param sql
     */

    public List<T> findBySql(String sql, Class<T> tClass) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<T> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(tClass));
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start);
        return result;
    }

    /**
     * 根据Sql查询实体集合
     *
     * @param sql    (sql中的"?"数量必须和参数数量、顺序一致)
     * @param params 参数
     */

    public List<T> findBySql(String sql, Class<T> tClass, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<T> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(tClass), params);
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    /**
     * 根据Sql查询实体集合
     *
     * @param sql    (sql中的"?"数量必须和参数数量、顺序一致)
     * @param params 参数
     */

    public List<T> findBySql(String sql, Map<String, ?> params, Class<T> tClass) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<T> result = namedParameterJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(tClass));
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    /**
     * 根据Sql查询Map
     *
     * @param sql (sql中的"?"数量必须和参数数量、顺序一致)
     */

    public Map<String, Object> queryForMap(String sql) {
        long start = SqlLogs.getCurrentTimeMillis();
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start);
        return result;
    }

    public Map<String, Object> queryForMap(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, params);
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    public Map<String, Object> queryForMap(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Map<String, Object> result = namedParameterJdbcTemplate.queryForMap(sql, params);
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    /**
     * 根据Sql查询集合
     *
     * @param sql
     */
    public List<Map<String, Object>> queryForList(String sql) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start);
        return result;
    }

    public List<Map<String, Object>> queryForList(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, params);
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList(sql, params);
        SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
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
    public PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Class<T> tClass) {
        Integer count = count(sql);
        if (count == 0) {
            return PageResults.noData();
        }
        PageUtil pager = new PageUtil(pageNumber, pageSize);
        sql += " limit " + pager.getPageNumber() + "," + pager.getPageSize();
        if (toEntity) {
            List<T> list = findBySql(sql, tClass);
            return PageResults.success(count, list);
        } else {
            List<Map<String, Object>> list = queryForList(sql);
            return PageResults.success(count, list);
        }
    }

    public PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Class<T> tClass, Object... params) {
        Integer count = count(sql, params);
        if (count == 0) {
            return PageResults.noData();
        }
        PageUtil pager = new PageUtil(pageNumber, pageSize);
        sql += " limit " + pager.getPageNumber() + "," + pager.getPageSize();
        if (toEntity) {
            List<T> list = findBySql(sql, tClass, params);
            return PageResults.success(count, list);
        } else {
            List<Map<String, Object>> list = queryForList(sql, params);
            return PageResults.success(count, list);
        }
    }

    public PageResults page(String sql, boolean toEntity, Integer pageNumber, Integer pageSize, Map<String, ?> params, Class<T> tClass) {
        Integer count = count(sql, params);
        if (count == 0) {
            return PageResults.noData();
        }
        PageUtil pager = new PageUtil(pageNumber, pageSize);
        sql += " limit " + pager.getPageNumber() + "," + pager.getPageSize();
        if (toEntity) {
            List<T> list = findBySql(sql, params, tClass);
            return PageResults.success(count, list);
        } else {
            List<Map<String, Object>> list = queryForList(sql, params);
            return PageResults.success(count, list);
        }
    }

    /**
     * 查询总量
     *
     * @param sql
     * @return
     */
    public Integer count(String sql) {
        long start = SqlLogs.getCurrentTimeMillis();
        Integer result = jdbcTemplate.queryForObject(Sqls.buildCountSql(sql), Integer.class);
        SqlLogs.log(Sqls.buildCountSql(sql), SqlStatementType.SELECT, result, start);
        return result;
    }

    public Integer count(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Integer result = jdbcTemplate.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
        SqlLogs.log(Sqls.buildCountSql(sql), SqlStatementType.SELECT, result, start, params);
        return result;
    }

    public Integer count(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Integer result = namedParameterJdbcTemplate.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
        SqlLogs.log(Sqls.buildCountSql(sql), SqlStatementType.SELECT, result, start, params);
        return result;
    }

    /**
     * 执行SQL
     *
     * @param sql
     * @return
     */
    public int execute(String sql) {
        return jdbcTemplate.update(sql);
    }

    public int execute(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    public int execute(String sql, Map<String, ?> params) {
        return namedParameterJdbcTemplate.update(sql, params);
    }

    /**
     * 执行插入SQL
     *
     * @param sql
     * @return
     */
    public int insert(String sql) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql);
        SqlLogs.log(sql, SqlStatementType.INSERT, result, start);
        return result;
    }

    public int insert(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        SqlLogs.log(sql, SqlStatementType.INSERT, result, start, params);
        return result;
    }

    public int insert(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        SqlLogs.log(sql, SqlStatementType.INSERT, result, start, params);
        return result;
    }

    /**
     * 执行修改SQL
     *
     * @param sql
     * @return
     */
    public int update(String sql) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql);
        SqlLogs.log(sql, SqlStatementType.UPDATE, result, start);
        return result;
    }

    public int update(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        SqlLogs.log(sql, SqlStatementType.UPDATE, result, start, params);
        return result;
    }

    public int update(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        SqlLogs.log(sql, SqlStatementType.UPDATE, result, start, params);
        return result;
    }

    /**
     * 执行删除SQL
     *
     * @param sql
     * @return
     */
    public int delete(String sql) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql);
        SqlLogs.log(sql, SqlStatementType.DELETE, result, start);
        return result;
    }

    public int delete(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        SqlLogs.log(sql, SqlStatementType.DELETE, result, start, params);
        return result;
    }

    public int delete(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        SqlLogs.log(sql, SqlStatementType.DELETE, result, start, params);
        return result;
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    public int delete(ID id, Class<T> tClass) {
        long start = System.currentTimeMillis();
        String sql = "DELETE FROM " + getTable(tClass) + " WHERE id = ?";
        int k = execute(sql, id);
        SqlLogs.log(sql, SqlStatementType.UPDATE, k, start, id);
        return k;
    }

    /**
     * 新增实体
     *
     * @param entity
     * @return
     */
    public T insert(T entity, Class<T> tClass) {
        long start = System.currentTimeMillis();
        CustomField[] customFields = getFieldNames(entity, tClass);
        boolean isAutomatic = false;
        Method setMethod = null;
        String type = null;
        StringBuffer fields = new StringBuffer(" (");
        StringBuffer values = new StringBuffer("(");
        for (CustomField customField : customFields) {
            Object value = customField.getValue();
            // 自增
            if (customField.isAutomatic()) {
                isAutomatic = true;
                setMethod = customField.getSetMethod();
                type = customField.getType();
                if (!TypeConvert.isNull(value)) {
                    logger.error("自增主键不能设置ID！");
                    return null;
                }
            } else {
                if (TypeConvert.isNull(value)) {
                    logger.error("非自增主键未设置ID！");
                    return null;
                }
                fields.append(customField.getName() + ",");
                if (StringUtils.isNumeric(value.toString())) {
                    values.append(customField.getValue() + ",");
                } else {
                    values.append("'" + customField.getValue() + "',");
                }
            }
        }
        if (!fields.toString().endsWith(",") || !values.toString().endsWith(",")) {
            logger.error("没有找到需要新增的字段或值！");
            return null;
        }
        String fieldStr = fields.toString().substring(0, fields.toString().length() - 1) + ")";
        String valueStr = values.toString().substring(0, values.toString().length() - 1) + ")";
        String sql = "INSERT INTO " + getTable(tClass) + fieldStr + " VALUES " + valueStr;
        int k = 0;
        if (isAutomatic) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            PreparedStatementCreator preparedStatementCreator = con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                return ps;
            };

            k = jdbcTemplate.update(preparedStatementCreator, keyHolder);
            try {
                Number number = keyHolder.getKey();
                switch (type.toLowerCase()) {
                    case "string":
                        setMethod.invoke(entity, number.toString());
                        break;
                    case "long":
                        setMethod.invoke(entity, number.longValue());
                        break;
                    case "int":
                        setMethod.invoke(entity, number.intValue());
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            k = execute(sql);
        }
        SqlLogs.log(sql, SqlStatementType.INSERT, k, start);
        if (k > 0) {
            return entity;
        }
        return null;
    }

    /**
     * 修改实体
     *
     * @param entity
     * @return
     */
    public T update(T entity, Class<T> tClass) {
        long start = System.currentTimeMillis();
        CustomField[] customFields = getFieldNames(entity, tClass);
        ID id = null;
        StringBuffer set = new StringBuffer(" set ");
        for (CustomField customField : customFields) {
            Object value = customField.getValue();
            if (customField.isPrimaryKey()) {
                if (TypeConvert.isNull(value)) {
                    logger.error("实体类ID为空！");
                    return null;
                }
                id = (ID) value;
            } else {
                if (value != null) {
                    if (StringUtils.isNumeric(value.toString())) {
                        set.append(customField.getName() + "=" + value + ",");
                    } else {
                        set.append(customField.getName() + "='" + value + "',");
                    }
                }
            }
        }
        if (!set.toString().endsWith(",")) {
            logger.error("没有找到要修改的字段！");
            return null;
        }
        String sql = "UPDATE " + getTable(tClass) + set.toString().substring(0, set.toString().length() - 1) + " WHERE id = ?";
        int k = execute(sql, id);
        SqlLogs.log(sql, SqlStatementType.UPDATE, k, start, id);
        if (k > 0) {
            return entity;
        }
        return null;
    }

    /**
     * 获取类的所有属性名及方法,有注解返回注解值
     */
    private CustomField[] getFieldNames(T entity, Class<T> tClass) {
        Field[] fields = tClass.getDeclaredFields();
        CustomField[] customFields = new CustomField[fields.length];
        int k = 0;
        for (Field field : fields) {
            CustomField customField = new CustomField();
            customField.setFieldName(field.getName());
            String type = field.getType().toString();
            if (type.contains(".")) {
                customField.setType(type.substring(type.lastIndexOf(".") + 1));
            } else {
                customField.setType(type);
            }
            try {
                customField.setGetMethod(tClass.getMethod("get" + TypeConvert.getMethodName(customField.getFieldName())));
                customField.setSetMethod(tClass.getMethod("set" + TypeConvert.getMethodName(customField.getFieldName()), new Class[]{field.getType()}));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (!TypeConvert.isNull(entity)) {
                try {
                    customField.setValue(customField.getGetMethod().invoke(entity));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            if (field.isAnnotationPresent(Id.class)) {
                Id id = field.getAnnotation(Id.class);
                customField.setName(id.name());
                customField.setPrimaryKey(true);
                customField.setAutomatic(id.automatic());
            } else {
                customField.setName(field.getName());
            }
            if (field.isAnnotationPresent(Fields.class)) {
                Fields fields1 = field.getAnnotation(Fields.class);
                customField.setName(TypeConvert.isNull(fields1.name()) ? field.getName() : fields1.name());
            } else {
                if (!field.isAnnotationPresent(Id.class)) {
                    customField.setName(field.getName());
                }
            }
            customFields[k++] = customField;
        }
        return customFields;
    }

}

