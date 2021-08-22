package com.lolup.lolup_project.config.mybatis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MybatisTypeHandler implements TypeHandler<Map<String, Integer>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setParameter(PreparedStatement ps, int i, Map<String, Integer> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, this.toJson(parameter));
    }

    @Override
    public Map<String, Integer> getResult(ResultSet rs, String columnName) throws SQLException {
        return this.toMap(rs.getString(columnName));

    }

    @Override
    public Map<String, Integer> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toMap(rs.getString(columnIndex));
    }

    @Override
    public Map<String, Integer> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toMap(cs.getString(columnIndex));
    }

    private String toJson(Map<String, Integer> map) {
        try {
            log.info("TypeConverter convert map to json, result:{}",mapper.writeValueAsString(map));
            return mapper.writeValueAsString(map);
        } catch (IOException e) {
            log.error("TypeConverter failed to convert map to json, map:{}", map.toString());
            throw new RuntimeException(e);
        }
    }

    private Map<String, Integer> toMap(String json) {
        try {
            log.info("TypeConverter convert json to map, result:{}", mapper.readValue(json, HashMap.class));
            return mapper.readValue(json, HashMap.class);
        } catch (IOException e) {
            log.error("TypeConverter failed to convert json to map, json:{}", json);
            throw new RuntimeException();
        }
    }

}
