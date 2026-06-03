package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.ProntuarioDAO;
import clinica.dto.RegistroProntuarioDTO;

public class ProntuarioHandler extends BaseHandler {

    private final ProntuarioDAO dao = new ProntuarioDAO();
    private final Gson gson = new Gson();

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();
        Long id = extractId(path);
        String sub = extractSub(path);

        switch (method) {
            case "GET":
                if (id != null && "paciente".equals(sub)) {
                    sendList(exchange, dao.findByPaciente(id));
                    return;
                }

                sendError(exchange, 400, "Use /api/prontuarios/paciente/{idPaciente}");
                return;

            case "PUT":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do registro na URL: /api/prontuarios/{id}");
                    return;
                }

                RegistroProntuarioDTO dto = readBody(exchange);
                dao.updateRegistro(id, dto);
                sendJson(exchange, 200, Map.of("mensagem", "Registro de prontuario atualizado com sucesso"));
                return;

            case "DELETE":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do registro na URL: /api/prontuarios/{id}");
                    return;
                }

                dao.deleteRegistro(id);
                sendJson(exchange, 200, Map.of("mensagem", "Registro de prontuario removido com sucesso"));
                return;

            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private RegistroProntuarioDTO readBody(HttpExchange exchange) throws Exception {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, RegistroProntuarioDTO.class);
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
        if (parts.length >= 4) {
            return parts[3];
        }
        return null;
    }
}
