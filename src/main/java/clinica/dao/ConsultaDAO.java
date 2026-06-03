package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.ConsultaDTO;

public class ConsultaDAO extends BaseDAO {

    public List<ConsultaDTO> listByMonth(int mes, int ano) throws SQLException {
        String sql = "SELECT c.id, c.id_agendamento, c.sintomas, c.anamnese, c.datahora_registro, "
                   + "p.nome || ' ' || p.sobrenome AS paciente, "
                   + "m.nome || ' ' || m.sobrenome AS medico "
                   + "FROM consulta c "
                   + "JOIN agendamento a ON a.id = c.id_agendamento "
                   + "JOIN paciente    p ON p.id = a.id_paciente "
                   + "JOIN medico      m ON m.id = a.id_medico "
                   + "WHERE EXTRACT(MONTH FROM c.datahora_registro) = ? "
                   + "AND   EXTRACT(YEAR  FROM c.datahora_registro) = ? "
                   + "ORDER BY c.datahora_registro DESC";

        List<ConsultaDTO> lista = new ArrayList<>();

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }

        return lista;
    }

    public List<ConsultaDTO> findAll() throws SQLException {
        String sql = "SELECT c.id, c.id_agendamento, c.sintomas, c.anamnese, c.datahora_registro, "
                   + "p.nome || ' ' || p.sobrenome AS paciente, "
                   + "m.nome || ' ' || m.sobrenome AS medico "
                   + "FROM consulta c "
                   + "JOIN agendamento a ON a.id = c.id_agendamento "
                   + "JOIN paciente    p ON p.id = a.id_paciente "
                   + "JOIN medico      m ON m.id = a.id_medico "
                   + "ORDER BY c.datahora_registro DESC";

        List<ConsultaDTO> lista = new ArrayList<>();

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

    public ConsultaDTO findById(Long id) throws SQLException {
        String sql = "SELECT c.id, c.id_agendamento, c.sintomas, c.anamnese, c.datahora_registro, "
                   + "p.nome || ' ' || p.sobrenome AS paciente, "
                   + "m.nome || ' ' || m.sobrenome AS medico "
                   + "FROM consulta c "
                   + "JOIN agendamento a ON a.id = c.id_agendamento "
                   + "JOIN paciente    p ON p.id = a.id_paciente "
                   + "JOIN medico      m ON m.id = a.id_medico "
                   + "WHERE c.id = ?";

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

    public void insert(ConsultaDTO dto) throws SQLException {
        String sql = "INSERT INTO consulta (id_agendamento, sintomas, anamnese, datahora_registro) "
                   + "VALUES (?, ?, ?, ?)";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, dto.getIdAgendamento());
            ps.setString(2, dto.getSintomas());
            ps.setString(3, dto.getAnamnese());
            ps.setTimestamp(4, Timestamp.valueOf(dto.getDataHoraRegistro()));
            ps.executeUpdate();
        }
    }

    public void update(Long id, ConsultaDTO dto) throws SQLException {
        String sql = "UPDATE consulta SET id_agendamento = ?, sintomas = ?, anamnese = ?, datahora_registro = ? "
                   + "WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, dto.getIdAgendamento());
            ps.setString(2, dto.getSintomas());
            ps.setString(3, dto.getAnamnese());
            ps.setTimestamp(4, Timestamp.valueOf(dto.getDataHoraRegistro()));
            ps.setLong(5, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Consulta não encontrada: id " + id);
            }
        }
    }

    public void delete(Long id) throws SQLException {
        String deleteItens = "DELETE FROM item_receita WHERE id_receita IN (SELECT id FROM receita WHERE id_consulta = ?)";
        String deleteReceita = "DELETE FROM receita WHERE id_consulta = ?";
        String deleteExames = "DELETE FROM exame WHERE id_consulta = ?";
        String deleteRegistro = "DELETE FROM registro_prontuario WHERE id_consulta = ?";
        String deleteConsulta = "DELETE FROM consulta WHERE id = ?";

        Connection con = DatabaseConnection.connect();

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(deleteItens)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteReceita)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteExames)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteRegistro)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteConsulta)) {
                ps.setLong(1, id);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalArgumentException("Consulta não encontrada: id " + id);
                }
            }

            con.commit();
        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private ConsultaDTO mapRow(ResultSet rs) throws SQLException {
        ConsultaDTO dto = new ConsultaDTO();
        dto.setId(rs.getLong("id"));
        dto.setIdAgendamento(rs.getLong("id_agendamento"));
        dto.setSintomas(rs.getString("sintomas"));
        dto.setAnamnese(rs.getString("anamnese"));
        dto.setDataHoraRegistro(rs.getTimestamp("datahora_registro").toString());
        dto.setPaciente(rs.getString("paciente"));
        dto.setMedico(rs.getString("medico"));
        return dto;
    }
}
