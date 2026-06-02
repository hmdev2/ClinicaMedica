package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clinica.connection.DatabaseConnection;

public class AgendamentoDAO extends BaseDAO{
	public void save(Integer idPaciente, Integer idMedico, Integer idColaborador, Timestamp dataHora, String status) {
		String sql = "INSERT INTO agendamento (id_paciente, id_medico, id_colaborador, data_hora, status) VALUES "
				+ "(?, ?, ?, ?, ?);";

		try (Connection con = DatabaseConnection.connect(); PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, idPaciente);
			ps.setInt(2, idMedico);
			ps.setInt(3, idColaborador);
			ps.setTimestamp(4, dataHora);
			ps.setString(5, status);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public List<Map<String, Object>> findAll(){
		String sql = "SELECT a.id AS id_agendamento, "
				+ "p.nome || ' ' || p.sobrenome AS paciente, "
				+ "m.nome || ' ' || m.sobrenome AS medico, "
				+ "c.nome || ' ' || c.sobrenome AS colaborador, "
				+ "a.data_hora, "
				+ "a.status FROM agendamento a "
				+ "JOIN paciente p ON p.id = id_paciente "
				+ "JOIN medico m ON m.id = id_medico "
				+ "JOIN colaborador c ON c.id = id_colaborador "
				+ "ORDER BY data_hora DESC;";
		
		List<Map<String, Object>> agendamentos = new ArrayList<Map<String, Object>>();
		try (Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				agendamentos.add(lineForMap(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return agendamentos;
	}
}