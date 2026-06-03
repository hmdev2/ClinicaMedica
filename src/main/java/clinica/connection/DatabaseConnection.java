package clinica.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	private static final String HOST = Config.get("db.host");
    private static final String PORTA = Config.get("db.port");
    private static final String BANCO = Config.get("db.name");
    private static final String USUARIO = Config.get("db.user");
    private static final String SENHA = Config.get("db.pass");
    
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
