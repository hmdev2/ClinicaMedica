package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.AgendamentoDTO;

public class AgendamentoDAO extends BaseDAO {

    public List<AgendamentoDTO> findAll() throws SQLException {
        String sql = "SELECT a.id, "
                   + "a.id_paciente, a.id_medico, a.id_colaborador, "
                   + "p.nome || ' ' || p.sobrenome AS paciente, "
                   + "m.nome || ' ' || m.sobrenome AS medico, "
                   + "c.nome || ' ' || c.sobrenome AS colaborador, "
                   + "a.data_hora, a.status "
                   + "FROM agendamento a "
                   + "JOIN paciente    p ON p.id = a.id_paciente "
                   + "JOIN medico      m ON m.id = a.id_medico "
                   + "JOIN colaborador c ON c.id = a.id_colaborador "
                   + "ORDER BY a.data_hora DESC";

        List<AgendamentoDTO> lista = new ArrayList<>();

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }

        return lista;
    }

    public AgendamentoDTO findById(Long id) throws SQLException {
        String sql = "SELECT a.id, "
                   + "a.id_paciente, a.id_medico, a.id_colaborador, "
                   + "p.nome || ' ' || p.sobrenome AS paciente, "
                   + "m.nome || ' ' || m.sobrenome AS medico, "
                   + "c.nome || ' ' || c.sobrenome AS colaborador, "
                   + "a.data_hora, a.status "
                   + "FROM agendamento a "
                   + "JOIN paciente    p ON p.id = a.id_paciente "
                   + "JOIN medico      m ON m.id = a.id_medico "
                   + "JOIN colaborador c ON c.id = a.id_colaborador "
                   + "WHERE a.id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    public void insert(AgendamentoDTO dto) throws SQLException {
        String sqlInsert = "INSERT INTO agendamento "
                         + "(id_paciente, id_medico, id_colaborador, data_hora, status) "
                         + "VALUES (?, ?, ?, ?, 'Agendado')";

        try (Connection con = DatabaseConnection.connect()) {
            validarConflito(con, dto.getIdMedico(), dto.getDataHora(), null);

            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setLong(1, dto.getIdPaciente());
                ps.setLong(2, dto.getIdMedico());
                ps.setLong(3, dto.getIdColaborador());
                ps.setTimestamp(4, Timestamp.valueOf(dto.getDataHora()));
                ps.executeUpdate();
            }
        }
    }

    public void update(Long id, AgendamentoDTO dto) throws SQLException {
        String sql = "UPDATE agendamento "
                   + "SET id_paciente = ?, id_medico = ?, id_colaborador = ?, data_hora = ?, status = ? "
                   + "WHERE id = ?";

        try (Connection con = DatabaseConnection.connect()) {
            validarConflito(con, dto.getIdMedico(), dto.getDataHora(), id);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, dto.getIdPaciente());
                ps.setLong(2, dto.getIdMedico());
                ps.setLong(3, dto.getIdColaborador());
                ps.setTimestamp(4, Timestamp.valueOf(dto.getDataHora()));
                ps.setString(5, dto.getStatus() != null ? dto.getStatus() : "Agendado");
                ps.setLong(6, id);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalArgumentException("Agendamento não encontrado: id " + id);
                }
            }
        }
    }

    public void cancelar(Long id) throws SQLException {
        String sql = "UPDATE agendamento SET status = 'Cancelado' WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Agendamento não encontrado: id " + id);
            }
        }
    }

    public void marcarRealizado(Long id) throws SQLException {
        String sql = "UPDATE agendamento SET status = 'Realizado' WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Agendamento não encontrado: id " + id);
            }
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM agendamento WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Agendamento não encontrado: id " + id);
            }
        }
    }

    private void validarConflito(Connection con, Long idMedico, String dataHora, Long ignorarId) throws SQLException {
        String sql = "SELECT 1 FROM agendamento "
                   + "WHERE id_medico = ? AND data_hora = ? AND status != 'Cancelado' "
                   + (ignorarId != null ? "AND id <> ?" : "");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idMedico);
            ps.setTimestamp(2, Timestamp.valueOf(dataHora));
            if (ignorarId != null) {
                ps.setLong(3, ignorarId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalArgumentException(
                        "Conflito de horário: médico já possui consulta agendada neste horário");
                }
            }
        }
    }

    private AgendamentoDTO mapRow(ResultSet rs) throws SQLException {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(rs.getLong("id"));
        dto.setIdPaciente(rs.getLong("id_paciente"));
        dto.setIdMedico(rs.getLong("id_medico"));
        dto.setIdColaborador(rs.getLong("id_colaborador"));
        dto.setPaciente(rs.getString("paciente"));
        dto.setMedico(rs.getString("medico"));
        dto.setColaborador(rs.getString("colaborador"));
        dto.setDataHora(rs.getTimestamp("data_hora").toString());
        dto.setStatus(rs.getString("status"));
        return dto;
    }
}
