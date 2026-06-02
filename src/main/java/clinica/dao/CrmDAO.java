package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import clinica.connection.DatabaseConnection;

public class CrmDAO {
	public void save(Integer idMedico, Integer numero, String uf, Integer rqe) {
		String sql = "INSERT INTO crm (id_medico, numero, uf, rqe) VALUES" + "(?, ?, ?, ?)";

		try (Connection con = DatabaseConnection.connect(); PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, idMedico);
			ps.setInt(2, numero);
			ps.setString(3, uf);
			ps.setInt(4, rqe);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
