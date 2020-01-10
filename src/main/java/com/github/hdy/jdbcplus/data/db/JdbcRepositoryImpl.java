package com.github.hdy.jdbcplus.data.db;

import com.github.hdy.jdbcplus.data.annotation.*;
import com.github.hdy.jdbcplus.log.SqlLogs;
import com.github.hdy.jdbcplus.log.SqlStatementType;
import com.github.hdy.jdbcplus.log.Sqls;
import com.github.hdy.jdbcplus.result.Page;
import com.github.hdy.jdbcplus.util.Pager;
import com.github.hdy.jdbcplus.util.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JdbcTemplate 简单封装
 *
 * @author 贺鹏
 */
@Service
@Slf4j
public class JdbcRepositoryImpl<T, ID> implements JdbcRepository<T, ID> {

    @Value("${jdbc.log:true}")
    private Boolean jdbcLog;

    @Autowired
    @Qualifier("JdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 开始事务
     */
    public void beginTransaction() {
        log.info("----------------------  手动开启事务操作  ----------------------");
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
        log.info("----------------------  Error:100  操作失败,进行回滚操作  ----------------------");
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
            if (Strings.isNull(table_name)) {
                table_name = Strings.camelToUnderline(clazz.getSimpleName());
            }
        } else {
            table_name = Strings.camelToUnderline(clazz.getSimpleName());
        }
        if (Strings.isNull(table_name))
            log.error(" table annotation should be have [ name ] param : {} ", " Failed to get table name ");
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
        if (list == null || list.size() == 0)
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
        if (list == null || list.size() == 0)
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
        if (list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    public T getBySql(String sql, Class<T> tClass, Object... params) {
        List<T> list = findBySql(sql, tClass, params);
        if (list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    public T getBySql(String sql, Map<String, ?> params, Class<T> tClass) {
        List<T> list = findBySql(sql, params, tClass);
        if (list == null || list.size() == 0)
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
        if (jdbcLog)
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
        if (jdbcLog)
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
        if (jdbcLog)
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
        if (jdbcLog)
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
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.SELECT, result, start);
        return result;
    }

    public Map<String, Object> queryForMap(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    public Map<String, Object> queryForMap(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Map<String, Object> result = namedParameterJdbcTemplate.queryForMap(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    /**
     * 查询单个字段结果
     *
     * @param sql       带查询字段的sql,字段可取别名，fieldName与别名一致
     * @param fieldName 字段名
     * @return
     */
    public String getSingleValueBySqlAndFieldName(String sql, String fieldName) {
        Object value = queryForMap(sql).get(fieldName);
        return Strings.isNull(value) ? null : value.toString();
    }

    /**
     * 根据Sql查询集合
     *
     * @param sql
     */
    public List<Map<String, Object>> queryForList(String sql) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.SELECT, result, start);
        return result;
    }

    public List<Map<String, Object>> queryForList(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.SELECT, result, start, params);
        return result;
    }

    /**
     * 分页查询
     *
     * @param sql
     * @param pageNumber 页码
     * @param pageSize   页数量
     * @return
     */
    public Page<T> page(String sql, Integer pageNumber, Integer pageSize, Class<T> tClass) {
        Integer count = count(sql);
        if (count == 0)
            return new Page<T>(pageNumber, pageSize, 0, null);
        Pager pager = new Pager(pageNumber, pageSize);
        sql += " limit " + pager.getPageNumber() + "," + pager.getPageSize();
        List<T> list = findBySql(sql, tClass);
        return new Page<T>(pageNumber, pageSize, count, list);
    }

    public Page<T> page(String sql, Integer pageNumber, Integer pageSize, Class<T> tClass, Object... params) {
        Integer count = count(sql, params);
        if (count == 0)
            return new Page<T>(pageNumber, pageSize, 0, null);
        Pager pager = new Pager(pageNumber, pageSize);
        sql += " limit " + pager.getPageNumber() + "," + pager.getPageSize();
        List<T> list = findBySql(sql, tClass, params);
        return new Page<T>(pageNumber, pageSize, count, list);
    }

    public Page<T> page(String sql, Integer pageNumber, Integer pageSize, Map<String, ?> params, Class<T> tClass) {
        Integer count = count(sql, params);
        if (count == 0)
            return new Page<T>(pageNumber, pageSize, 0, null);
        Pager pager = new Pager(pageNumber, pageSize);
        sql += " limit " + pager.getPageNumber() + "," + pager.getPageSize();
        List<T> list = findBySql(sql, params, tClass);
        return new Page<T>(pageNumber, pageSize, count, list);
    }

    /**
     * 根据实体分页查询
     */
    public Page<T> page(Integer pageNumber, Integer pageSize, Class<T> tClass) {
        String sql = "select * from " + getTable(tClass);
        return page(sql, pageNumber, pageSize, tClass);
    }

    public Page<T> page(T entity) {
        StringBuffer sql = new StringBuffer("select * from " + getTable(entity.getClass()) + " where 1 = 1");
        CustomField[] customFields = getFieldNames(entity, (Class<T>) entity.getClass());
        for (CustomField customField : customFields) {
            if (!customField.isTransient()) {
                Object value = customField.getValue();
                if (!Strings.isNull(value)) {
                    if (!customField.isLike() && !customField.isLikeLeft() && !customField.isLikeRight()) {
                        if (Strings.isNumeric(value.toString())) {
                            sql.append(" and " + customField.getName() + " = " + value);
                        } else {
                            sql.append(" and " + customField.getName() + " = '" + value + "'");
                        }
                    } else {
                        if (customField.isLike()) {
                            sql.append(" and " + customField.getName() + " like '%" + value + "%'");
                        }
                        if (customField.isLikeLeft()) {
                            sql.append(" and " + customField.getName() + " like '%" + value + "'");
                        }
                        if (customField.isLikeRight()) {
                            sql.append(" and " + customField.getName() + " like '" + value + "%'");
                        }
                    }
                }
            }
        }
        BaseEntity baseEntity = new BaseEntity();
        BeanUtils.copyProperties(entity, baseEntity);
        if (!Strings.isNull(baseEntity.getOrderByField())) {
            sql.append(" order by " + baseEntity.getOrderByField());
            if (!Strings.isNull(baseEntity.getOrderByValue())) {
                sql.append(" " + baseEntity.getOrderByValue());
            }
        }
        return page(sql.toString(), baseEntity.getPageNumber(), baseEntity.getPageSize(), (Class<T>) entity.getClass());
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
        if (jdbcLog)
            SqlLogs.log(Sqls.buildCountSql(sql), SqlStatementType.SELECT, result, start);
        return result;
    }

    public Integer count(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Integer result = jdbcTemplate.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
        if (jdbcLog)
            SqlLogs.log(Sqls.buildCountSql(sql), SqlStatementType.SELECT, result, start, params);
        return result;
    }

    public Integer count(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        Integer result = namedParameterJdbcTemplate.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
        if (jdbcLog)
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
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.INSERT, result, start);
        return result;
    }

    public int insert(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.INSERT, result, start, params);
        return result;
    }

    public int insert(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        if (jdbcLog)
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
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.UPDATE, result, start);
        return result;
    }

    public int update(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.UPDATE, result, start, params);
        return result;
    }

    public int update(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.UPDATE, result, start, params);
        return result;
    }

    public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) {
        return jdbcTemplate.batchUpdate(sql, pss);
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
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.DELETE, result, start);
        return result;
    }

    public int delete(String sql, Object... params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.DELETE, result, start, params);
        return result;
    }

    public int delete(String sql, Map<String, ?> params) {
        long start = SqlLogs.getCurrentTimeMillis();
        int result = execute(sql, params);
        if (jdbcLog)
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
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.DELETE, k, start, id);
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
        String idType = null;
        StringBuffer fields = new StringBuffer(" (");
        StringBuffer values = new StringBuffer("(");
        for (CustomField customField : customFields) {
            if (!customField.isTransient()) {
                Object value = customField.getValue();
                type = customField.getType();
                // 自增
                if (customField.isAutomatic()) {
                    idType = type;
                    isAutomatic = true;
                    setMethod = customField.getSetMethod();
                    if (!Strings.isNull(value)) {
                        log.error("自增主键不能设置ID！");
                        return null;
                    }
                } else {
                    if (customField.isPrimaryKey()) {
                        if (Strings.isNull(value)) {
                            log.error("非自增主键未设置ID！");
                            return null;
                        }
                    }
                    if (!Strings.isNull(value)) {
                        fields.append(customField.getName() + ",");
                        switch (type.toLowerCase()) {
                            case "long":
                            case "int":
                            case "double":
                            case "float":
                            case "bigdecimal":
                            case "integer":
                                values.append(value + ",");
                                break;
                            case "boolean":
                                boolean b = Boolean.parseBoolean(value.toString());
                                values.append((b ? 1 : 2) + ",");
                                break;
                            case "date":
                                Date d = (Date) value;
                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                                values.append("'" + date + "',");
                                break;
                            default:
                                values.append("'" + value + "',");
                                break;
                        }
                    }
                }
            }
        }
        if (!fields.toString().endsWith(",") || !values.toString().endsWith(",")) {
            log.error("没有找到需要新增的字段或值！");
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
                switch (idType.toLowerCase()) {
                    case "string":
                        setMethod.invoke(entity, number.toString());
                        break;
                    case "long":
                        setMethod.invoke(entity, number.longValue());
                        break;
                    case "int":
                    case "integer":
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
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.INSERT, k, start);
        if (k > 0) {
            return entity;
        }
        return null;
    }

    /**
     * 新增List<T>实体
     *
     * @param entitys
     * @param tClass
     * @return
     */
    public List<T> insert(List<T> entitys, Class<T> tClass) {
        List<T> result = new ArrayList<>();
        if (entitys.size() == 0) {
            return result;
        }
        for (T entity : entitys) {
            result.add(insert(entity, tClass));
        }
        return result;
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
            if (!customField.isTransient()) {
                Object value = customField.getValue();
                if (customField.isPrimaryKey()) {
                    if (Strings.isNull(value)) {
                        log.error("实体类ID为空！");
                        return null;
                    }
                    id = (ID) value;
                } else {
                    if (value != null) {
                        if (Strings.isNumeric(value.toString())) {
                            set.append(customField.getName() + "=" + value + ",");
                        } else {
                            set.append(customField.getName() + "='" + value + "',");
                        }
                    }
                }
            }
        }
        if (!set.toString().endsWith(",")) {
            log.error("没有找到要修改的字段！");
            return null;
        }
        String sql = "UPDATE " + getTable(tClass) + set.toString().substring(0, set.toString().length() - 1) + " WHERE id = ?";
        int k = execute(sql, id);
        if (jdbcLog)
            SqlLogs.log(sql, SqlStatementType.UPDATE, k, start, id);
        if (k > 0) {
            return entity;
        }
        return null;
    }

    /**
     * 修改List<T>实体
     *
     * @param entitys
     * @param tClass
     * @return
     */
    public List<T> update(List<T> entitys, Class<T> tClass) {
        List<T> result = new ArrayList<>();
        if (entitys.size() == 0) {
            return result;
        }
        for (T entity : entitys) {
            result.add(update(entity, tClass));
        }
        return result;
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
                customField.setGetMethod(tClass.getMethod("get" + Strings.capitalize(customField.getFieldName())));
                customField.setSetMethod(tClass.getMethod("set" + Strings.capitalize(customField.getFieldName()), new Class[]{field.getType()}));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (!Strings.isNull(entity)) {
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
                customField.setName(Strings.isNull(fields1.name()) ? Strings.camelToUnderline(field.getName()) : fields1.name());
            } else {
                if (!field.isAnnotationPresent(Id.class)) {
                    customField.setName(Strings.camelToUnderline(field.getName()));
                }
            }
            // 是否是扩展字段
            if (field.isAnnotationPresent(Transient.class)) {
                customField.setTransient(true);
            } else {
                customField.setTransient(false);
            }
            // 查询条件(like)
            if (field.isAnnotationPresent(Like.class)) {
                customField.setLike(true);
            } else {
                customField.setLike(false);
            }
            // 查询条件(like) 左%
            if (field.isAnnotationPresent(LikeLeft.class)) {
                customField.setLikeLeft(true);
            } else {
                customField.setLikeLeft(false);
            }
            // 查询条件(like) 右%
            if (field.isAnnotationPresent(LikeRight.class)) {
                customField.setLikeRight(true);
            } else {
                customField.setLikeRight(false);
            }
            customFields[k++] = customField;
        }
        return customFields;
    }
}

