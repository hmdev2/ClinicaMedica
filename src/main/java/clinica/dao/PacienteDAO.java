package clinica.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clinica.connection.DatabaseConnection;
import clinica.dto.EnderecoDTO;

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
	
	public EnderecoDTO findEnderecoByPacienteId(int pacienteId) throws SQLException {
		String sql = "SELECT e.logradouro "
				+ "|| COALESCE(', ' || e.numero, '') "
				+ "|| COALESCE(', ' || e.complemento, '') || ' ' "
				+ "|| e.bairro || ' ' "
				+ "|| e.cep || ' ' "
				+ "|| e.cidade || '-' "
				+ "|| e.estado AS endereco "
				+ "FROM endereco e "
				+ "JOIN paciente p ON p.id = e.id_paciente "
				+ "WHERE p.id = ?";
		try (
				Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
			) {
			
			ps.setInt(1, pacienteId);
			
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return new EnderecoDTO(rs.getString("endereco"));
				}
			}
		}
		
		throw new SQLException("Endereço não encontrado para paciente id=" + pacienteId);
	}
	
	public void save(String nome, String sobrenome, Date nascimento, String sexo, String email, String cpf) throws SQLException {
		String sql = "INSERT INTO paciente (nome, sobrenome, nascimento, sexo, email, cpf) VALUES (?, ?, ?, ?, ?, ?)";
		try (
				Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
			) {
			
			ps.setString(1, nome);
            ps.setString(2, sobrenome);
            ps.setDate(3, nascimento);
            ps.setString(4, sexo);
            ps.setString(5, email);
            ps.setString(6, cpf);
            ps.executeUpdate();
		}
	}
	
	
	
}
