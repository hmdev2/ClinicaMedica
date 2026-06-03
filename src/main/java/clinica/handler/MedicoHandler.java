package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.MedicoDAO;
import clinica.dto.MedicoDTO;

public class MedicoHandler extends BaseHandler{

    private final MedicoDAO dao = new MedicoDAO();
    private final Gson gson = new Gson();
 
    @Override
    protected void process(HttpExchange exchange) throws Exception {
 
        String method = exchange.getRequestMethod().toUpperCase();
        String path   = exchange.getRequestURI().getPath();
 
        Long id = extractId(path);
 
        switch (method) {
 
            case "GET":
                if (id != null) {
                    MedicoDTO medico = dao.findById(id);
                    if (medico == null) {
                        sendError(exchange, 404, "Médico não encontrado");
                    } else {
                        sendJson(exchange, 200, medico);
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
 
                MedicoDTO novoMedico = gson.fromJson(bodyPost, MedicoDTO.class);
                dao.insert(novoMedico);
 
                Map<String, String> resPost = new HashMap<>();
                resPost.put("mensagem", "Médico cadastrado com sucesso");
                sendJson(exchange, 201, resPost);
                break;
 
            case "PUT":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do médico na URL: /api/medicos/{id}");
                    return;
                }
 
                String bodyPut = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );
 
                MedicoDTO dadosAtualizados = gson.fromJson(bodyPut, MedicoDTO.class);
                dao.update(id, dadosAtualizados);
 
                Map<String, String> resPut = new HashMap<>();
                resPut.put("mensagem", "Médico atualizado com sucesso");
                sendJson(exchange, 200, resPut);
                break;

            case "DELETE":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id do médico na URL: /api/medicos/{id}");
                    return;
                }

                dao.delete(id);
                sendJson(exchange, 200, Map.of("mensagem", "Médico removido com sucesso"));
                break;
 
            default:
                exchange.sendResponseHeaders(405, -1);
        }
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

