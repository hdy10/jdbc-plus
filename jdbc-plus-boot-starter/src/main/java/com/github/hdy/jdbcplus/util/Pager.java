package com.github.hdy.jdbcplus.util;

import com.github.hdy.jdbcplus.data.annotation.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 分页转换limit
 */
@Getter
@Setter
public class Pager {
    @Transient
    private Integer pageNumber = 1;
    @Transient
    private Integer pageSize = 10;

    public Pager(Integer pageNumber, Integer pageSize) {
        Integer current = Strings.isNull(pageNumber) ? 1 : pageNumber;
        Integer size = Strings.isNull(pageSize) ? 10 : pageSize;
        this.pageNumber = (current - 1) * size;
        this.pageSize = size;
    }

}
