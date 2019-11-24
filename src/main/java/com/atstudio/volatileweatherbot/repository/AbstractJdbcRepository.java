package com.atstudio.volatileweatherbot.repository;

import com.atstudio.volatileweatherbot.repository.columns.EntityColumns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;
import java.util.stream.Stream;

import static com.atstudio.volatileweatherbot.repository.columns.EntityColumnsUtils.joinColumnNames;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class AbstractJdbcRepository<T> {

    protected NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected void createIfNotExist(T entity, String tableName, EntityColumns<T>[] fields) {

        String paramsList = Stream.of(fields).map(fld -> ":" + fld.getColName()).collect(joining(","));

        Map<String, Object> paramValues = Stream.of(fields).collect(
                toMap(EntityColumns::getColName, fld -> fld.getProp(entity))
        );

        jdbcTemplate.update(
                " INSERT INTO " + tableName +
                        "(" + joinColumnNames(",", fields) + ")" +
                        " VALUES (" + paramsList + ") ON CONFLICT DO NOTHING",
                paramValues
        );
    }

}
