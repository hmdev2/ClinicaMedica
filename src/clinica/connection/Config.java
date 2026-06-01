package clinica.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static Properties props = new Properties();

    static {
        try {
            FileInputStream file = new FileInputStream("config.properties");
            props.load(file);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}