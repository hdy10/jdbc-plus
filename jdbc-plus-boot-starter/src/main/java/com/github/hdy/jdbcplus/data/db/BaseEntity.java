package com.github.hdy.jdbcplus.data.db;

import com.github.hdy.jdbcplus.data.annotation.Transient;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 实体类公用属性
 *
 * @author hdy
 * @date 2019/9/5
 */
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 页码
     */
    @Transient
    @Getter
    @Setter
    private Integer pageNumber;
    /**
     * 每页数量
     */
    @Transient
    @Getter
    @Setter
    private Integer pageSize;
    /**
     * 排序字段
     */
    @Transient
    @Getter
    @Setter
    private String orderByField;
    /**
     * 排序方式
     */
    @Transient
    @Getter
    @Setter
    private String orderByValue;
}
