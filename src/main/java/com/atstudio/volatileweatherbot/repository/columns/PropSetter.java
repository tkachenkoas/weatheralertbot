package com.atstudio.volatileweatherbot.repository.columns;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface PropSetter<T> {

    void setProp(T object, ResultSet rs, String column) throws SQLException;

}
