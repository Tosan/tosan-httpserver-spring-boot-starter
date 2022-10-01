package com.tosan.http.server.sample.dto;

import java.util.List;

/**
 * @author mina khoshnevisan
 * @since 9/20/2022
 */
public class ResultSetModel <T>{
    private List<T> dataSource;
    private int totalCount;

    public ResultSetModel(List<T> dataSource, int totalCount) {
        this.dataSource = dataSource;
        this.totalCount = totalCount;
    }

    public List<T> getDataSource() {
        return dataSource;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
