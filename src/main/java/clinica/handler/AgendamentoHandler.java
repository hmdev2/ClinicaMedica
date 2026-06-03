package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.AgendamentoDAO;
import clinica.dto.AgendamentoDTO;

public class AgendamentoHandler extends BaseHandler {

    private final AgendamentoDAO dao = new AgendamentoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void process(HttpExchange exchange) throws Exception {

        String method = exchange.getRequestMethod().toUpperCase();
        String path   = exchange.getRequestURI().getPath();

        Long id = extractId(path);

        switch (method) {

            case "GET":
                if (id != null) {
                    AgendamentoDTO agendamento = dao.findById(id);
                    if (agendamento == null) {
                        sendError(exchange, 404, "Agendamento não encontrado");
                    } else {
                        sendJson(exchange, 200, agendamento);
                    }
                } else {
                    sendList(exchange, dao.findAll());
                }
                break;

            case "POST":
                String bodyPost = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                AgendamentoDTO dto = gson.fromJson(bodyPost, AgendamentoDTO.class);
                dao.insert(dto);

                Map<String, String> resPost = new HashMap<>();
                resPost.put("mensagem", "Consulta agendada com sucesso");
                sendJson(exchange, 201, resPost);
                break;

            case "PUT":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do agendamento na URL: /api/agendamentos/{id}");
                    return;
                }

                String bodyPut = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                AgendamentoDTO atualizado = gson.fromJson(bodyPut, AgendamentoDTO.class);
                dao.update(id, atualizado);
                sendJson(exchange, 200, Map.of("mensagem", "Agendamento atualizado com sucesso"));
                break;

            case "PATCH":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do agendamento na URL: /api/agendamentos/{id}/cancelar");
                    return;
                }

                String acao = extrairAcao(path);

                if ("cancelar".equals(acao)) {
                    dao.cancelar(id);
                    Map<String, String> resCancelar = new HashMap<>();
                    resCancelar.put("mensagem", "Consulta cancelada com sucesso");
                    sendJson(exchange, 200, resCancelar);
                } else if ("realizar".equals(acao)) {
                    dao.marcarRealizado(id);
                    Map<String, String> resRealizar = new HashMap<>();
                    resRealizar.put("mensagem", "Consulta marcada como realizada");
                    sendJson(exchange, 200, resRealizar);
                } else {
                    sendError(exchange, 400, "Ação inválida. Use /cancelar ou /realizar");
                }
                break;

            case "DELETE":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do agendamento na URL: /api/agendamentos/{id}");
                    return;
                }

                dao.delete(id);
                sendJson(exchange, 200, Map.of("mensagem", "Agendamento removido com sucesso"));
                break;

            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private Long extractId(String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            try {
                return Long.parseLong(parts[i]);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String extrairAcao(String path) {
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
