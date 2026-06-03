package clinica;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import clinica.handler.AgendamentoHandler;
import clinica.handler.ColaboradorHandler;
import clinica.handler.ConsultaHandler;
import clinica.handler.ExameHandler;
import clinica.handler.MedicoHandler;
import clinica.handler.PacienteHandler;
import clinica.handler.ProntuarioHandler;
import clinica.handler.ReceitaHandler;
import clinica.handler.StaticFileHandler;

public class Main {

	public static void main(String[] args) throws Exception{
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		
		server.createContext("/api/pacientes", new PacienteHandler());
		server.createContext("/api/medicos", new MedicoHandler());
		server.createContext("/api/colaboradores", new ColaboradorHandler());
		server.createContext("/api/agendamentos", new AgendamentoHandler());
		server.createContext("/api/consultas",    new ConsultaHandler());
		server.createContext("/api/exames",       new ExameHandler());
		server.createContext("/api/receitas",     new ReceitaHandler());
		server.createContext("/api/prontuarios",  new ProntuarioHandler());
		server.createContext("/",                 new StaticFileHandler());
		
		server.setExecutor(Executors.newFixedThreadPool(10));
		server.start();
		
		System.out.println("Servidor iniciado em http://localhost:" + port); 
	}
	
}


