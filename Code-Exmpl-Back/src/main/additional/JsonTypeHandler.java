package main.additional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Log
@MappedJdbcTypes(includeNullJdbcType = true, value = JdbcType.VARCHAR)
public class JsonTypeHandler extends BaseTypeHandler<HashMap<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, HashMap<String, Object> parameter, JdbcType jdbcType)
            throws SQLException {

    }

    @Override
    public HashMap<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        HashMap<String, Object> value = null;

        String json_string = rs.getString(columnName);
        if (json_string != null) {
            try {
                value = objectMapper.readValue(json_string, HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    @Override
    public HashMap<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public HashMap<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
