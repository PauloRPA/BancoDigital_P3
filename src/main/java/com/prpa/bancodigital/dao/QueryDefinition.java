package com.prpa.bancodigital.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
public class QueryDefinition {

    private String name;
    private Map<String, String> queryMap;

    public QueryDefinition() {
        queryMap = new HashMap<>();
    }

    public void setQueries(List<Map<String, Object>> queries) {
        queries.forEach(map ->
                map.forEach((key, value) -> queryMap.put(key, value.toString())));
    }

}
