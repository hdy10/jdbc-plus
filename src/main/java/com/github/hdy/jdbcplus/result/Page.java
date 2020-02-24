package com.github.hdy.jdbcplus.result;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Created by hdy on 2019/6/25.
 */
@Data
public class Page<T> {
    /**
     * 当前页
     */
    private Integer pageNumber = 1;

    /**
     * 每页显示条数，默认 10
     */
    private Integer pageSize = 10;

    /**
     * 总数
     */
    private Integer total = 0;

    /**
     * 查询数据列表
     */
    private List<T> rows = Collections.emptyList();

    public Page(Integer pageNumber, Integer pageSize, Integer total, List<T> rows) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.total = total;
        this.rows = rows;
    }
}
