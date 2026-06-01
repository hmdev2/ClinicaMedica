package clinica;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import clinica.connection.DatabaseConnection;
import clinica.dao.PacienteDAO;
import clinica.dto.EnderecoDTO;

public class Main {

	public static void main(String[] args) {
		testePacienteDAOFindEnderecoByPacienteId();
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
	
	public static void testePacienteDAOSave() {
		PacienteDAO dao = new PacienteDAO();

        try {
            dao.save(
                "João",
                "Silva",
                java.sql.Date.valueOf("2000-01-01"),
                "Masculino",
                "joao@email.com",
                "12345678900"
            );

            System.out.println("Paciente salvo");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void testePacienteDAOFindEnderecoByPacienteId() {
		PacienteDAO dao = new PacienteDAO();

        try {
            int pacienteId = 1;

            EnderecoDTO endereco = dao.findEnderecoByPacienteId(pacienteId);

            System.out.println("Endereço do paciente:");
            System.out.println(endereco);

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}


