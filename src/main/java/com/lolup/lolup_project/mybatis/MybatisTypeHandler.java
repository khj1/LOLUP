package com.lolup.lolup_project.mybatis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.api.riot_api.summoner.MostInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class MybatisTypeHandler implements TypeHandler<List<MostInfo>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setParameter(PreparedStatement ps, int i, List<MostInfo> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, this.toJson(parameter));
    }

    @Override
    public List<MostInfo> getResult(ResultSet rs, String columnName) throws SQLException {
        return this.toList(rs.getString(columnName));

    }

    @Override
    public List<MostInfo> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toList(rs.getString(columnIndex));
    }

    @Override
    public List<MostInfo> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toList(cs.getString(columnIndex));
    }

    private String toJson(List<MostInfo> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<MostInfo> toList(String json) {
        try {
            List<MostInfo> most3 = mapper.readValue(json, new TypeReference<List<MostInfo>>() {});
            return most3;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
