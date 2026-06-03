package clinica.handler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import clinica.dao.ConsultaDAO;
import clinica.dao.ExameDAO;
import clinica.dao.ReceitaDAO;
import clinica.dao.ProntuarioDAO;
import clinica.dto.ConsultaDTO;
import clinica.dto.ExameDTO;
import clinica.dto.ReceitaDTO;
import clinica.dto.RegistroProntuarioDTO;

public class ConsultaHandler extends BaseHandler {

    private final ConsultaDAO consultaDAO = new ConsultaDAO();
    private final ExameDAO exameDAO = new ExameDAO();
    private final ReceitaDAO receitaDAO = new ReceitaDAO();
    private final ProntuarioDAO prontuarioDAO = new ProntuarioDAO();
    private final Gson gson = new Gson();

    @Override
    protected void process(HttpExchange exchange) throws Exception {

        String method = exchange.getRequestMethod().toUpperCase();
        String path   = exchange.getRequestURI().getPath();
        String query  = exchange.getRequestURI().getQuery();

        Long id   = extractId(path);
        String sub = extrairSub(path);
        switch (method) {

            case "GET":
                if (id != null && "exames".equals(sub)) {
                    sendList(exchange, exameDAO.findByConsulta(id));

                } else if (id != null && "receita".equals(sub)) {
                    ReceitaDTO receita = receitaDAO.findByConsulta(id);
                    if (receita == null) {
                        sendError(exchange, 404, "Receita não encontrada para esta consulta");
                    } else {
                        sendJson(exchange, 200, receita);
                    }

                } else if (id != null && "prontuario".equals(sub)) {
                    RegistroProntuarioDTO registro = prontuarioDAO.findRegistroByConsulta(id);
                    if (registro == null) {
                        sendError(exchange, 404, "Registro de prontuário não encontrado para esta consulta");
                    } else {
                        sendJson(exchange, 200, registro);
                    }

                } else if (id != null) {
                    ConsultaDTO consulta = consultaDAO.findById(id);
                    if (consulta == null) {
                        sendError(exchange, 404, "Consulta não encontrada");
                    } else {
                        sendJson(exchange, 200, consulta);
                    }

                } else if (query != null && query.startsWith("mes=")) {
                    Map<String, String> params = parseQuery(query);
                    int mes = Integer.parseInt(params.get("mes"));
                    int ano = Integer.parseInt(params.getOrDefault("ano",
                            String.valueOf(java.time.Year.now().getValue())));
                    sendList(exchange, consultaDAO.listByMonth(mes, ano));

                } else {
                    sendList(exchange, consultaDAO.findAll());
                }
                break;

            case "POST":
                String bodyPost = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                if (id != null && "exames".equals(sub)) {
                    ExameDTO exame = gson.fromJson(bodyPost, ExameDTO.class);
                    exame.setIdConsulta(id);
                    exameDAO.insert(exame);
                    Map<String, String> res = new HashMap<>();
                    res.put("mensagem", "Exame solicitado com sucesso");
                    sendJson(exchange, 201, res);

                } else if (id != null && "receita".equals(sub)) {
                    ReceitaDTO receita = gson.fromJson(bodyPost, ReceitaDTO.class);
                    receita.setIdConsulta(id);
                    receitaDAO.insert(receita);
                    Map<String, String> res = new HashMap<>();
                    res.put("mensagem", "Receita emitida com sucesso");
                    sendJson(exchange, 201, res);

                } else if (id != null && "prontuario".equals(sub)) {
                    RegistroProntuarioDTO reg = gson.fromJson(bodyPost, RegistroProntuarioDTO.class);
                    reg.setIdConsulta(id);
                    prontuarioDAO.inserirRegistro(reg);
                    Map<String, String> res = new HashMap<>();
                    res.put("mensagem", "Registro de prontuário salvo com sucesso");
                    sendJson(exchange, 201, res);

                } else {
                    ConsultaDTO dto = gson.fromJson(bodyPost, ConsultaDTO.class);
                    consultaDAO.insert(dto);
                    Map<String, String> res = new HashMap<>();
                    res.put("mensagem", "Consulta registrada com sucesso");
                    sendJson(exchange, 201, res);
                }
                break;

            case "PUT":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id da consulta na URL: /api/consultas/{id}");
                    return;
                }

                String bodyPut = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                ConsultaDTO atualizada = gson.fromJson(bodyPut, ConsultaDTO.class);
                consultaDAO.update(id, atualizada);
                sendJson(exchange, 200, Map.of("mensagem", "Consulta atualizada com sucesso"));
                break;

            case "PATCH":
                if ("resultado".equals(sub)) {
                    String bodyPatch = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                    );
                    @SuppressWarnings("unchecked")
                    Map<String, String> body = gson.fromJson(bodyPatch, Map.class);
                    exameDAO.registrarResultado(id, body.get("resultado"), body.get("dataResultado"));
                    Map<String, String> res = new HashMap<>();
                    res.put("mensagem", "Resultado do exame registrado com sucesso");
                    sendJson(exchange, 200, res);
                } else {
                    sendError(exchange, 400, "Ação inválida");
                }
                break;

            case "DELETE":
                if (id == null) {
                    sendError(exchange, 400, "Informe o id da consulta na URL: /api/consultas/{id}");
                    return;
                }

                consultaDAO.delete(id);
                sendJson(exchange, 200, Map.of("mensagem", "Consulta removida com sucesso"));
                break;

            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private Long extractId(String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            try {
                return Long.parseLong(part);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String extrairSub(String path) {
        String[] parts = path.split("/");
        String last = parts[parts.length - 1];
        try {
            Long.parseLong(last);
            return null;
        } catch (NumberFormatException e) {
            return last;
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }
}
