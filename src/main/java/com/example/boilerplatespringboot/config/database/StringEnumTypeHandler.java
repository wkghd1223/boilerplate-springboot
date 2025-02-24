package com.example.boilerplatespringboot.config.database;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class StringEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
      throws SQLException {

    if (jdbcType == null) {
      ps.setString(i, get(parameter));
    } else {
      ps.setObject(i, get(parameter), jdbcType.TYPE_CODE); // see r3589
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    if (rs.getObject(columnName) == null) {
      return null;
    }

    return of(rs.getString(columnName));
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    if (rs.getObject(columnIndex) == null) {
      return null;
    }

    return of(rs.getString(columnIndex));
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    if (cs.getObject(columnIndex) == null) {
      return null;
    }

    return of(cs.getString(columnIndex));
  }

  public abstract String get(E myType);

  public abstract E of(String code);
}