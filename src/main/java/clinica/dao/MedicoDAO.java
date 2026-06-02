package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clinica.connection.DatabaseConnection;

public class MedicoDAO extends BaseDAO {
	public void save(String nome, String sobrenome, String especialidade, String cpf) {
		String sql = "INSERT INTO medico (nome, sobrenome, especialidade, cpf) VALUES " + "(?, ?, ?, ?);";

		try (Connection con = DatabaseConnection.connect(); PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, nome);
			ps.setString(2, sobrenome);
			ps.setString(3, especialidade);
			ps.setString(4, cpf);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}

	public List<Map<String, Object>> findAll() {
		String sql = "SELECT m.id, m.nome || ' ' || m.sobrenome AS nome_completo, m.cpf, m.especialidade, "
				+ "STRING_AGG('CRM-' || c.uf || ' ' || c.numero, ', ') AS crms " + "FROM medico "
				+ "LEFT JOIN crm c ON c.id_medico = m.id "
				+ "GROUP BY m.id, m.nome, m.sobrenome, m.cpf, m.especialidade;";

		List<Map<String, Object>> medicos = new ArrayList<Map<String, Object>>();
		try (Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				medicos.add(lineForMap(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return medicos;
	}
}
