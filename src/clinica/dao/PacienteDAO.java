package clinica.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clinica.connection.DatabaseConnection;

public class PacienteDAO extends BaseDAO {
	
	public List<Map<String, Object>> findAll() throws SQLException {
		String sql = "SELECT id, nome, sobrenome, nascimento, sexo, email, cpf FROM paciente ORDER BY nome, sobrenome";
		List<Map<String, Object>> pacientes = new ArrayList<Map<String, Object>>();
		try (
				Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
			) {
			while (rs.next()) {
				pacientes.add(lineForMap(rs));
			}
		}
		
		return pacientes;
	}
	
}
