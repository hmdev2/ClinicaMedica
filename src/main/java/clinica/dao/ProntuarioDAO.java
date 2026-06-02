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

public class ProntuarioDAO extends BaseDAO{
	public void save(Integer idConsulta, String diagnostico, String tratamento, Timestamp dataHora) {
		String sql = "INSERT INTO prontuario (id_consulta, diagnostico, tratamento, datahora_registro) VALUES "
				+ "(?, ?, ?, ?);";
		
		try (Connection con = DatabaseConnection.connect(); PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, idConsulta);
			ps.setString(2, diagnostico);
			ps.setString(3, tratamento);
			ps.setTimestamp(4, dataHora);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public List<Map<String, Object>> prontuarioByIdPaciente(Integer idPaciente){
		String sql = "SELECT pr.id AS id_prontuario, "
				+ "p.nome || ' ' || p.sobrenome AS paciente, p.cpf AS cpf_paciente, "
				+ "m.nome || ' ' || m.sobrenome AS medico, m.cpf AS cpf_medico, m.especialidade AS especialidade_medico, "
				+ "c.sintomas, c.anamnese, "
				+ "rp.diagnostico, rp.tratamento, "
				+ "a.data_hora As datahora_consulta, "
				+ "pr.data_abertura "
				+ "FROM prontuario pr "
				+ "JOIN paciente p ON p.id = pr.id_paciente "
				+ "JOIN registro_prontuario rp ON rp.id_prontuario = pr.id "
				+ "JOIN consulta c ON c.id = rp.id_consulta "
				+ "JOIN agendamento a ON a.id = c.id_agendamento "
				+ "JOIN medico m ON m.id = a.id_medico "
				+ "WHERE pr.id_paciente = ? "
				+ "ORDER BY a.data_hora DESC;";
		
		List<Map<String, Object>> prontuario = new ArrayList<Map<String, Object>>();
		try (Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {
			
			ps.setInt(1, idPaciente);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return prontuario;
	}
}