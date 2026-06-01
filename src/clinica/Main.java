package clinica;

import java.sql.Connection;

import clinica.connection.DatabaseConnection;

public class Main {

	public static void main(String[] args) {
		testeConexaoDB();
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

}


