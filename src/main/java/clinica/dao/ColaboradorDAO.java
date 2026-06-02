package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clinica.connection.DatabaseConnection;

public class ColaboradorDAO extends BaseDAO {
	
	public void save(String nome, String sobrenome, String cpf) {
		String sql = "INSERT INTO colaborador (nome, sobrenome, cpf) VALUES (?, ?, ?);";
		try (
				Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
			) {
			
			ps.setString(1, nome);
            ps.setString(2, sobrenome);
            ps.setString(3, cpf);
            ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public List<Map<String, Object>> findAll() throws SQLException{
		String sql = "SELECT id, nome || ' ' || sobrenome AS nome_completo, cpf FROM colaborador;";
		List<Map<String, Object>> colaboradores = new ArrayList<Map<String, Object>>();
		try (
				Connection con = DatabaseConnection.connect();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
			) {
			while (rs.next()) {
				colaboradores.add(lineForMap(rs));
			}
		}
		
		return colaboradores;
	}
}
