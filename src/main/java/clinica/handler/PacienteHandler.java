package clinica.handler;


import com.sun.net.httpserver.HttpExchange;

import clinica.dao.PacienteDAO;

public class PacienteHandler extends BaseHandler {
	
	private final PacienteDAO dao = new PacienteDAO();
	
	@Override
	protected void process(HttpExchange exchange) throws Exception {
		if("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			sendList(exchange, dao.findAll());
			return;
		}
		
		
	}
}
