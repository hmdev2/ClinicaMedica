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

public class Consulta extends BaseDAO {

	public void save(Integer idAgendamento, String sintomas, String anamnese, Timestamp dataHora) throws SQLException{
		String sql = "INSERT INTO consulta (id_agendamento, sintomas, anamnese, datahora_registro) VALUES (?, ?, ?, ?);";
		try (Connection con = DatabaseConnection.connect(); PreparedStatement ps = con.prepareStatement(sql);) {

			ps.setInt(1, idAgendamento);
			ps.setString(2, sintomas);
			ps.setString(3, anamnese);
			ps.setTimestamp(4, dataHora);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Map<String, Object>> listByMonth(int mes, int ano) throws SQLException{
		String sql = "SELECT p.nome || ' ' || p.sobrenome AS paciente, "
				+ "m.nome || ' ' || m.sobrenome AS medico, "
				+ "c.sintomas, "
				+ "c.anamnese, "
				+ "c.datahora_registro AS data_hora "
				+ "FROM consulta c "
				+ "INNER JOIN agendamento a ON c.id_agendamento = a.id "
				+ "INNER JOIN paciente p    ON a.id_paciente = p.id "
				+ "INNER JOIN medico m      ON a.id_medico   = m.id "
				+ "WHERE EXTRACT(MONTH FROM c.datahora_registro) = ? "
				+ "AND EXTRACT(YEAR  FROM c.datahora_registro) = ?";

		List<Map<String, Object>> consultaMonth = new ArrayList<>();

		try (Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, mes);
			ps.setInt(2, ano);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					consultaMonth.add(lineForMap(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return consultaMonth;
	}
	
	public List<Map<String, Object>> ListWithPrescription() throws SQLException{
		String sql = "SELECT p.nome || ' ' || p.sobrenome AS paciente, p.cpf AS cpf_paciente,\r\n"
				+ "m.nome || ' ' || m.sobrenome AS medico, m.cpf AS cpf_medico,\r\n"
				+ "rp.diagnostico, rp.tratamento,\r\n"
				+ "r.instrucoes AS instrucoes_receita,\r\n"
				+ "ir.nome AS medicamento, ir.principio_ativo, ir.dosagem, ir.frequencia, ir.duracao_dias,\r\n"
				+ "c.datahora_registro AS datahora_consulta\r\n"
				+ "FROM consulta c\r\n"
				+ "JOIN agendamento a ON a.id = c.id_agendamento\r\n"
				+ "JOIN paciente p ON p.id = a.id_paciente\r\n"
				+ "JOIN medico m ON m.id = a.id_medico\r\n"
				+ "JOIN registro_prontuario rp ON rp.id_consulta = c.id\r\n"
				+ "LEFT JOIN receita r ON r.id_consulta = c.id\r\n"
				+ "LEFT JOIN item_receita ir ON ir.id_receita = r.id\r\n"
				+ "ORDER BY c.datahora_registro DESC, p.cpf, ir.id";
		List<Map<String, Object>> consultaPrescription = new ArrayList<>();

	    try (
	        Connection con = DatabaseConnection.connect();
	        PreparedStatement ps = con.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	    ) {
	        while (rs.next()) {
	        	consultaPrescription.add(lineForMap(rs));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return consultaPrescription;
	}
	
	public List<Map<String, Object>> findAll() throws SQLException {
		String sql = "SELECT * FROM consulta;";
		List<Map<String, Object>> consultas = new ArrayList<Map<String, Object>>();
		try (
				Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
			) {
			while (rs.next()) {
				consultas.add(lineForMap(rs));
			}
		}
		
		return consultas;
	}
}