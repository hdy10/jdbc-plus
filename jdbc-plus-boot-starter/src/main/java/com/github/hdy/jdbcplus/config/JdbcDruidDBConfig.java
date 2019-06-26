package com.github.hdy.jdbcplus.config;

/**
 * JDBC连接池配置
 *
 * @author 贺大爷
 * @date 2018/2/6
 */

import com.alibaba.druid.pool.DruidDataSource;
import com.github.hdy.common.util.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 连接池配置
 */
@Configuration
public class JdbcDruidDBConfig {
    private static final Logger logger = LogManager.getLogger(JdbcDruidDBConfig.class.getName());
    @Value("${spring.datasource.driverClassName:null}")
    private String driverClassName;

    @Value("${spring.datasource.initialSize:1}")
    private int initialSize;

    @Value("${spring.datasource.minIdle:1}")
    private int minIdle;

    @Value("${spring.datasource.maxActive:1}")
    private int maxActive;

    @Value("${spring.datasource.maxWait:1}")
    private int maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis:1}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis:1}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery:null}")
    private String validationQuery;

    @Value("${spring.datasource.testWhileIdle:true}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.testOnBorrow:true}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn:true}")
    private boolean testOnReturn;

    @Value("${spring.datasource.poolPreparedStatements:true}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize:1}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.filters:null}")
    private String filters;

    @Value("{spring.datasource.connectionProperties:null}")
    private String connectionProperties;

    @Bean     //声明其为Bean实例
    @Qualifier("dataSource")
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    public DataSource dataSource(@Value("${spring.datasource.url:null}") String url,
                                 @Value("${spring.datasource.username:null}") String username,
                                 @Value("${spring.datasource.password:}") String password) {
        DruidDataSource datasource = new DruidDataSource();
        if (!Strings.isNull(url, username)) {
            logger.info("\u001b[35m\ndataSource druid 配置注入：\nurl:{}\nusername:{}\npassword:{}\u001b[0m", url, username, password);
        }
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
            logger.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(connectionProperties);
        return datasource;
    }

    @Bean(name = "JdbcTemplate")
    public JdbcTemplate JdbcTemplate1(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
