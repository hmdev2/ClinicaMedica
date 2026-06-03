package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.ExameDAO;
import clinica.dto.ExameDTO;

public class ExameHandler extends BaseHandler {

    private final ExameDAO dao = new ExameDAO();
    private final Gson gson = new Gson();

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();
        Long id = extractId(path);
        String sub = extractSub(path);

        switch (method) {
            case "GET":
                if (id == null) {
                    sendList(exchange, dao.findAll());
                    return;
                }

                ExameDTO exame = dao.findById(id);
                if (exame == null) {
                    sendError(exchange, 404, "Exame não encontrado");
                } else {
                    sendJson(exchange, 200, exame);
                }
                return;

            case "POST":
                ExameDTO novo = readBody(exchange, ExameDTO.class);
                dao.insert(novo);
                sendJson(exchange, 201, Map.of("mensagem", "Exame solicitado com sucesso"));
                return;

            case "PUT":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do exame na URL: /api/exames/{id}");
                    return;
                }

                ExameDTO atualizado = readBody(exchange, ExameDTO.class);
                dao.update(id, atualizado);
                sendJson(exchange, 200, Map.of("mensagem", "Exame atualizado com sucesso"));
                return;

            case "PATCH":
                if (id == null || !"resultado".equals(sub)) {
                    sendError(exchange, 400, "Use /api/exames/{id}/resultado");
                    return;
                }

                @SuppressWarnings("unchecked")
                Map<String, String> body = readBody(exchange, Map.class);
                dao.registrarResultado(id, body.get("resultado"), body.get("dataResultado"));
                sendJson(exchange, 200, Map.of("mensagem", "Resultado do exame registrado com sucesso"));
                return;

            case "DELETE":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do exame na URL: /api/exames/{id}");
                    return;
                }

                dao.delete(id);
                sendJson(exchange, 200, Map.of("mensagem", "Exame removido com sucesso"));
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
        for (int i = parts.length - 1; i >= 0; i--) {
            try {
                return Long.parseLong(parts[i]);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String extractSub(String path) {
        String[] parts = path.split("/");
        String last = parts[parts.length - 1];
        try {
            Long.parseLong(last);
            return null;
        } catch (NumberFormatException e) {
            return last;
        }
    }
}
