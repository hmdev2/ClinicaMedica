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

public class ReceitaDAO extends BaseDAO{
	public void save(Integer idConsulta, Timestamp dataHora, String instrucoes) {
		String sql = "INSERT INTO receita (id_consulta, datahora_emissao, instrucoes) VALUES "
				+ "(?, ?, ?);";
		
		try (Connection con = DatabaseConnection.connect(); PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, idConsulta);
			ps.setTimestamp(2, dataHora);
			ps.setString(3, instrucoes);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Map<String, Object>> ReceitaByMedico(Integer idConsulta){
		String sql = "SELECT p.nome || ' ' || p.sobrenome AS paciente, "
				+ "m.nome || ' ' || m.sobrenome AS medico, "
				+ "r.instrucoes, "
				+ "ir.nome AS medicamento, ir.principio_ativo, ir.dosagem, ir.frequencia, ir.duracao_dias, "
				+ "c.datahora_registro AS datahora_consulta "
				+ "FROM receita r "
				+ "JOIN consulta c ON c.id = r.id_consulta "
				+ "JOIN agendamento a ON a.id = c.id_agendamento "
				+ "JOIN paciente p ON p.id = a.id_paciente "
				+ "JOIN medico m ON m.id = a.id_paciente "
				+ "JOIN item_receita ir ON ir.id_receita = r.id "
				+ "WHERE r.id_consulta = ? ;";
		
		List<Map<String, Object>> receita = new ArrayList<Map<String, Object>>();
		try (Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				receita.add(lineForMap(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return receita;
	}
}
