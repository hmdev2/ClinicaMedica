package clinica.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.PacienteDAO;
import clinica.dto.PacienteDTO;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PacienteHandler extends BaseHandler {

    private final PacienteDAO dao = new PacienteDAO();
    private final Gson gson = new Gson();

    @Override
    protected void process(HttpExchange exchange) throws Exception {

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        	
        	String path = exchange.getRequestURI().getPath();
        	String[] parts = path.split("/");
        	
        	if(parts.length == 3) {
        		
        		sendList(exchange, dao.findAll());
        		return;
        	}
        	
        	if(parts.length == 4) {
        		try {
        			Long id = Long.parseLong(parts[3]);
        			
        			PacienteDTO p = dao.findById(id);
        			
        			if(p == null) {
        				sendJson(exchange, 400, Map.of("error", "Paciente não encontrado"));
        				return;
        			}
        			
        			sendJson(exchange, 200, p);
        			return;
        			
        		} catch(NumberFormatException e) {
        			sendJson(exchange, 400, Map.of("error", "Id inválido"));
        			return;
        		}
        	}
        	
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            String json = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            PacienteDTO dto = gson.fromJson(json, PacienteDTO.class);

            dao.insert(dto);

            Map<String, String> resp = new HashMap<>();
            resp.put("mensagem", "Paciente cadastrado");

            sendJson(exchange, 201, resp);
            return;
        }
        
        if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
        	
        	String path = exchange.getRequestURI().getPath();
        	String[] parts = path.split("/");
        	
        	if(parts.length != 4) {
        		exchange.sendResponseHeaders(405, -1);
        		return;
        	}
        	
        	Long id;

    	    try {
    	        id = Long.parseLong(parts[3]);
    	    } catch (NumberFormatException e) {
    	        sendJson(exchange, 400, Map.of("error", "ID inválido"));
    	        return;
    	    }
        	
        	PacienteDTO p = dao.findById(id);
        	
        	if (p == null) {
                sendJson(exchange, 404, Map.of("error", "Paciente não encontrado"));
                return;
            }
        	
        	String json = new String(
    	        exchange.getRequestBody().readAllBytes(),
    	        StandardCharsets.UTF_8
    	    );
        	
        	PacienteDTO dto = gson.fromJson(json, PacienteDTO.class);
        	
        	dto.setId(id);
        	
        	dao.update(dto);
        	
        	Map<String, String> resp = new HashMap<>();
        	resp.put("messagem", "Paciente atualizado");
        	
        	sendJson(exchange, 200, resp);
        	return;
        }

        exchange.sendResponseHeaders(405, -1);
    }
}