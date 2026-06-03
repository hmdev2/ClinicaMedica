package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.ReceitaDAO;
import clinica.dto.ReceitaDTO;

public class ReceitaHandler extends BaseHandler {

    private final ReceitaDAO dao = new ReceitaDAO();
    private final Gson gson = new Gson();

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase();
        Long id = extractId(exchange.getRequestURI().getPath());

        switch (method) {
            case "GET":
                if (id == null) {
                    sendList(exchange, dao.findAll());
                    return;
                }

                ReceitaDTO receita = dao.findById(id);
                if (receita == null) {
                    sendError(exchange, 404, "Receita não encontrada");
                } else {
                    sendJson(exchange, 200, receita);
                }
                return;

            case "POST":
                ReceitaDTO nova = readBody(exchange);
                dao.insert(nova);
                sendJson(exchange, 201, Map.of("mensagem", "Receita emitida com sucesso"));
                return;

            case "PUT":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id da receita na URL: /api/receitas/{id}");
                    return;
                }

                ReceitaDTO atualizada = readBody(exchange);
                dao.update(id, atualizada);
                sendJson(exchange, 200, Map.of("mensagem", "Receita atualizada com sucesso"));
                return;

            case "DELETE":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id da receita na URL: /api/receitas/{id}");
                    return;
                }

                dao.delete(id);
                sendJson(exchange, 200, Map.of("mensagem", "Receita removida com sucesso"));
                return;

            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private ReceitaDTO readBody(HttpExchange exchange) throws Exception {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, ReceitaDTO.class);
    }

    private Long extractId(String path) {
        String[] parts = path.split("/");
        String last = parts[parts.length - 1];
        try {
            return Long.parseLong(last);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
