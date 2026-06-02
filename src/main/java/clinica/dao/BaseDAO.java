package clinica.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseDAO {
	
	protected Map<String, Object> lineForMap(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		Map<String, Object> line = new LinkedHashMap<String, Object>();
		
		for (int i = 1; i <= meta.getColumnCount(); i++) {
            line.put(meta.getColumnLabel(i), rs.getObject(i));
        }
		
		return line; 
	}
}
