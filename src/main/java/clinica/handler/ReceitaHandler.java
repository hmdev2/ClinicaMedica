package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.ReceitaDAO;
import clinica.dto.ItemReceitaDTO;
import clinica.dto.ReceitaDTO;

public class ReceitaHandler extends BaseHandler {

    private final ReceitaDAO dao = new ReceitaDAO();
    private final Gson gson = new Gson();

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();

        String[] parts = path.replaceFirst("^/", "").split("/");

        if (method.equals("POST") && parts.length == 4 && parts[3].equals("itens")) {
            Long receitaId = parseLong(parts[2]);
            if (receitaId == null) { sendError(exchange, 400, "ID inválido"); return; }
            ItemReceitaDTO item = gson.fromJson(
                new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                ItemReceitaDTO.class
            );
            dao.insertItem(receitaId, item);
            sendJson(exchange, 201, Map.of("mensagem", "Medicamento adicionado à receita"));
            return;
        }

        if (method.equals("DELETE") && parts.length == 4 && parts[2].equals("itens")) {
            Long itemId = parseLong(parts[3]);
            if (itemId == null) { sendError(exchange, 400, "ID inválido"); return; }
            dao.deleteItem(itemId);
            sendJson(exchange, 200, Map.of("mensagem", "Medicamento removido da receita"));
            return;
        }

        Long id = parts.length >= 3 ? parseLong(parts[2]) : null;

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

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}