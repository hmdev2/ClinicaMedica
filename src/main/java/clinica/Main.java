package clinica;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import clinica.handler.PacienteHandler;

public class Main {

	public static void main(String[] args) throws Exception{
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		
		server.createContext("/", exchange -> {
		    String resposta = "tudo certo";

		    exchange.sendResponseHeaders(200, resposta.length());
		    exchange.getResponseBody().write(resposta.getBytes());
		    exchange.close();
		});
		server.createContext("/api/pacientes", new PacienteHandler());
		
		server.setExecutor(Executors.newFixedThreadPool(10));
		server.start();
		
		System.out.println("Servidor iniciado em http://localhost:8080");
	}
	
}


