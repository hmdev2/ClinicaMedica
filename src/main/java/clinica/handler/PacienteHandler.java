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
            sendList(exchange, dao.findAll());
            return;
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

        exchange.sendResponseHeaders(405, -1);
    }
}