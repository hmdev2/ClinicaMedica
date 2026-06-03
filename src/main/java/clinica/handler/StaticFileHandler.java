package clinica.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StaticFileHandler implements HttpHandler {

    private static final Path STATIC_DIR = Path.of("src", "main", "resources", "static").toAbsolutePath().normalize();
    private static final Map<String, String> CONTENT_TYPES = Map.of(
        ".html", "text/html; charset=utf-8",
        ".css", "text/css; charset=utf-8",
        ".js", "application/javascript; charset=utf-8",
        ".json", "application/json; charset=utf-8"
    );

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        Path file = STATIC_DIR.resolve(requestPath.substring(1)).normalize();
        if (!file.startsWith(STATIC_DIR) || !Files.exists(file) || Files.isDirectory(file)) {
            send(exchange, 404, "Pagina nao encontrada".getBytes());
            return;
        }

        exchange.getResponseHeaders().set("Content-Type", contentType(file));
        send(exchange, 200, Files.readAllBytes(file));
    }

    private String contentType(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            return CONTENT_TYPES.getOrDefault(name.substring(dot), "application/octet-stream");
        }
        return "application/octet-stream";
    }

    private void send(HttpExchange exchange, int status, byte[] response) throws IOException {
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
