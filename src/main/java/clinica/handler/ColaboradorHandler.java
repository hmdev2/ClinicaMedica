package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.ColaboradorDAO;
import clinica.dto.ColaboradorDTO;

public class ColaboradorHandler extends BaseHandler {

    private final ColaboradorDAO dao = new ColaboradorDAO();
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

                ColaboradorDTO colaborador = dao.findById(id);
                if (colaborador == null) {
                    sendError(exchange, 404, "Colaborador nao encontrado");
                } else {
                    sendJson(exchange, 200, colaborador);
                }
                return;

            case "POST":
                ColaboradorDTO novo = readBody(exchange, ColaboradorDTO.class);
                dao.insert(novo);
                sendJson(exchange, 201, Map.of("mensagem", "Colaborador cadastrado com sucesso"));
                return;

            case "PUT":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do colaborador na URL: /api/colaboradores/{id}");
                    return;
                }

                ColaboradorDTO atualizado = readBody(exchange, ColaboradorDTO.class);
                dao.update(id, atualizado);
                sendJson(exchange, 200, Map.of("mensagem", "Colaborador atualizado com sucesso"));
                return;

            case "DELETE":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do colaborador na URL: /api/colaboradores/{id}");
                    return;
                }

                dao.delete(id);
                sendJson(exchange, 200, Map.of("mensagem", "Colaborador removido com sucesso"));
                return;

            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private <T> T readBody(HttpExchange exchange, Class<T> type) throws Exception {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, type);
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
