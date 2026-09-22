package com.aistudy.tutor.shared.common;

/**
 * 分页查询参数：pageNum 从 1 开始，pageSize 默认 10。
 */
public record PageQuery(int pageNum, int pageSize) {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    public PageQuery {
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
    }

    /** 供 Repository 计算 offset */
    public int offset() {
        return (pageNum - 1) * pageSize;
    }

    /** 供 Repository 计算总页数 */
    public static int totalPages(long total, int pageSize) {
        return (int) Math.ceil(total * 1.0 / pageSize);
    }
}
