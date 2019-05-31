package com.hdy.jdbcplus.result;

import com.hdy.jdbcplus.util.TypeConvert;

public class PageUtil {
    private int pageNumber;
    private int pageSize;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public PageUtil(Integer pageNumber, Integer pageSize) {
        int pageN = TypeConvert.isNull(pageNumber) ? 1 : pageNumber;
        int pageS = TypeConvert.isNull(pageSize) ? 10 : pageSize;
        this.pageNumber = (pageN - 1) * pageS;
        this.pageSize = pageS;
    }
}
