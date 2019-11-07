package com.atstudio.volatileweatherbot.repository.columns;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class EntityColumnsUtils {

    public static String joinColumnNames(String delimiter, EntityColumns... args) {
        return Stream.of(args)
                .map(EntityColumns::getColName)
                .collect(joining(delimiter));
    }

}
