package clinica;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import clinica.connection.DatabaseConnection;
import clinica.dao.PacienteDAO;

public class Main {

	public static void main(String[] args) {
		testePacienteDAOFindAll();
	}
	
	public static void testeConexaoDB() {
		try {
            Connection conn = DatabaseConnection.connect();
            System.out.println("Conectado");
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro:");
            e.printStackTrace();
        }
	}
	
	public static void testePacienteDAOFindAll() {
		try {
			PacienteDAO dao = new PacienteDAO();
			List<Map<String, Object>> pacientes = dao.findAll();
			for (Map<String, Object> p : pacientes) {
				System.out.println(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


