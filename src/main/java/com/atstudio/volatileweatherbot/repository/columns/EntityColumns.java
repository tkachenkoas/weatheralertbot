package com.atstudio.volatileweatherbot.repository.columns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public interface EntityColumns<T> {

    String getColName();
    Function<T, Object> getPropAccessor();
    PropSetter<T> getPropSetter();

    default void setProp(T object, ResultSet rs) throws SQLException {
        getPropSetter().setProp(object, rs, getColName());
    }

    default <R> R getProp(T object) {
        return (R) getPropAccessor().apply(object);
    }

}
