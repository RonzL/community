package com.ronz.community.entity;

/**
 * @Description 封装页面的相关信息。
 * 比如当前页码、当前页面记录条数、总页码、总记录条数等。
 * @Author Ronz
 * @Date 2021/5/7 20:29
 * @Version 1.0
 */
public class Page {
    private int curPage = 1;    // 当前页码，默认页码从 1 开始
    private int limit = 10;  // 当前页面最大记录条数，默认为 10
    private int rows;   // 所有记录条数
    private String path; // 分页查询路径


    /**
     * 设置当前页码
     */
    public void setCurPage(int curPage) {
        this.curPage = curPage < 1 ? 1 : curPage;
    }
    public int getCurPage() {
        return curPage;
    }

    /**
     * 设置每页显示的记录条数
     */
    public void setLimit(int limit) {
        this.limit = limit < 1 ? 10 : limit;
    }
    public int getLimit() {
        return limit;
    }

    /**
     * 设置总记录条数
     */
    public void setRows(int rows) {
        this.rows = rows;
    }
    public int getRows() {
        return rows;
    }

    /**
     * 设置请求路径
     */
    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }

    /**
     * 获取总页数
     */
    public int getTotalPages(){
        if (rows % limit == 0){
             return rows/limit;
        }else{
             return rows/limit + 1;
        }
    }

    /**
     * 获取当页的起始记录行数，页码从 1 开始
     */
    public int getOffset(){
        return (curPage-1) * limit;
    }

    /**
     * 在一个页面中可以展示的页码是有限的，我们假设一页最多显示 5 个页码
     * 获取本页展示的开始页码
     */
    public int getStart(){
        return curPage-2 < 1 ? 1 : curPage-2;
    }

    /**
     * 获取本页展示的结束页码
     */
    public int getEnd(){
        return curPage+2 > getTotalPages() ? getTotalPages() : curPage+2;
    }
}
