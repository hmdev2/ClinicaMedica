package clinica.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	private static final String HOST = "localhost";
    private static final String PORTA = "5432";
    private static final String BANCO = "clinica";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "postgres";
    
    private DatabaseConnection() {
    	
    }
    
    public static Connection connect() throws SQLException {
    	String url = "jdbc:postgresql://" + HOST + ":" + PORTA + "/" + BANCO;
    	try {
    		Class.forName("org.postgresql.Driver");
    		return DriverManager.getConnection(url, USUARIO, SENHA);
    	} catch(ClassNotFoundException e) {
    		throw new SQLException("Driver JDBC do PostgreSQL nao encontrado", e);
    	}
    }
}
