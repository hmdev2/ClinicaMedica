package clinica.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class BaseHandler implements HttpHandler {
	
	private static final Gson gson = new Gson();
	
	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		
		addCors(exchange);
		
		if("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
			send(exchange, 204, "");
			return;
		}
		
		try {
			process(exchange);
		} catch(SQLException e) {
			sendError(exchange, 500, "Erro ao acessar o banco: " + e.getMessage());
		} catch(IllegalArgumentException e) {
			sendError(exchange, 400, e.getMessage());
		} catch(Exception e) {
			sendError(exchange, 500, "Erro inesperado: " + e.getMessage());
		}
	}
	
	private void addCors(HttpExchange exchange) {
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
	}
	
	private void send(HttpExchange exchange, int status, String response) throws IOException {
		byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
		
		exchange.sendResponseHeaders(status, bytes.length);
		
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(bytes);
		}
	}
	
	protected abstract void process(HttpExchange exchange) throws Exception;
	
	protected void sendJson(HttpExchange exchange, int status, Object object) throws IOException {
		String json = gson.toJson(object);
		
		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
		
		send(exchange, status, json);
	}
	
	protected void sendError(HttpExchange exchange, int status, String mensage) throws IOException {
		Map<String, String> error = new HashMap<>();
		error.put("erro", mensage);
		
		sendJson(exchange, status, error);
	}
	
	protected void requireMethod(HttpExchange exchange, String method) {
		if(!method.equalsIgnoreCase(exchange.getRequestMethod())) {
			throw new IllegalArgumentException("Metodo HTTP nao permitido. Use " + method);
		}
	}
	
	protected void sendList(HttpExchange exchange, List<?> list) throws IOException {
		sendJson(exchange, 200, list);
	}
}
