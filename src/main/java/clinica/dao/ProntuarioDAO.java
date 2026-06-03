package clinica.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.RegistroProntuarioDTO;

public class ProntuarioDAO extends BaseDAO {

    public List<RegistroProntuarioDTO> findByPaciente(Long idPaciente) throws SQLException {
        String sql = "SELECT rp.id, rp.id_prontuario, rp.id_consulta, rp.diagnostico, rp.tratamento, "
                   + "rp.datahora_registro, "
                   + "p.nome || ' ' || p.sobrenome AS paciente, p.cpf AS cpf_paciente, "
                   + "m.nome || ' ' || m.sobrenome AS medico, m.cpf AS cpf_medico, "
                   + "m.especialidade AS especialidade_medico, "
                   + "c.sintomas, c.anamnese, a.data_hora AS datahora_consulta "
                   + "FROM prontuario pr "
                   + "JOIN paciente p ON p.id = pr.id_paciente "
                   + "JOIN registro_prontuario rp ON rp.id_prontuario = pr.id "
                   + "JOIN consulta c ON c.id = rp.id_consulta "
                   + "JOIN agendamento a ON a.id = c.id_agendamento "
                   + "JOIN medico m ON m.id = a.id_medico "
                   + "WHERE pr.id_paciente = ? "
                   + "ORDER BY a.data_hora DESC";

        List<RegistroProntuarioDTO> registros = new ArrayList<>();

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRegistro(rs));
                }
            }
        }

        return registros;
    }

    public RegistroProntuarioDTO findRegistroByConsulta(Long idConsulta) throws SQLException {
        String sql = "SELECT rp.id, rp.id_prontuario, rp.id_consulta, rp.diagnostico, rp.tratamento, "
                   + "rp.datahora_registro, "
                   + "p.nome || ' ' || p.sobrenome AS paciente, p.cpf AS cpf_paciente, "
                   + "m.nome || ' ' || m.sobrenome AS medico, m.cpf AS cpf_medico, "
                   + "m.especialidade AS especialidade_medico, "
                   + "c.sintomas, c.anamnese, a.data_hora AS datahora_consulta "
                   + "FROM registro_prontuario rp "
                   + "JOIN prontuario pr ON pr.id = rp.id_prontuario "
                   + "JOIN paciente p ON p.id = pr.id_paciente "
                   + "JOIN consulta c ON c.id = rp.id_consulta "
                   + "JOIN agendamento a ON a.id = c.id_agendamento "
                   + "JOIN medico m ON m.id = a.id_medico "
                   + "WHERE rp.id_consulta = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, idConsulta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRegistro(rs);
            }
        }

        return null;
    }

    public void inserirRegistro(RegistroProntuarioDTO dto) throws SQLException {
        String sqlInsert = "INSERT INTO registro_prontuario "
                         + "(id_prontuario, id_consulta, diagnostico, tratamento, datahora_registro) "
                         + "VALUES (?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.connect();

        try {
            con.setAutoCommit(false);

            Long idProntuario = dto.getIdProntuario();
            if (idProntuario == null) {
                Long idPaciente = buscarPacienteDaConsulta(con, dto.getIdConsulta());
                idProntuario = buscarOuCriarProntuario(con, idPaciente);
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setLong(1, idProntuario);
                ps.setLong(2, dto.getIdConsulta());
                ps.setString(3, dto.getDiagnostico());
                ps.setString(4, dto.getTratamento());
                ps.setTimestamp(5, Timestamp.valueOf(dto.getDataHoraRegistro()));
                ps.executeUpdate();
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

    public void updateRegistro(Long id, RegistroProntuarioDTO dto) throws SQLException {
        String sql = "UPDATE registro_prontuario "
                   + "SET diagnostico = ?, tratamento = ?, datahora_registro = ? "
                   + "WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, dto.getDiagnostico());
            ps.setString(2, dto.getTratamento());
            ps.setTimestamp(3, Timestamp.valueOf(dto.getDataHoraRegistro()));
            ps.setLong(4, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Registro de prontuario nao encontrado: id " + id);
            }
        }
    }

    public void deleteRegistro(Long id) throws SQLException {
        String sql = "DELETE FROM registro_prontuario WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Registro de prontuario nao encontrado: id " + id);
            }
        }
    }

    private Long buscarPacienteDaConsulta(Connection con, Long idConsulta) throws SQLException {
        String sql = "SELECT a.id_paciente FROM consulta c "
                   + "JOIN agendamento a ON a.id = c.id_agendamento "
                   + "WHERE c.id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idConsulta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id_paciente");
            }
        }

        throw new IllegalArgumentException("Consulta nao encontrada: id " + idConsulta);
    }

    private Long buscarOuCriarProntuario(Connection con, Long idPaciente) throws SQLException {
        String sqlBusca = "SELECT id FROM prontuario WHERE id_paciente = ?";
        String sqlInsert = "INSERT INTO prontuario (id_paciente, data_abertura) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sqlBusca)) {
            ps.setLong(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }

        try (PreparedStatement ps = con.prepareStatement(sqlInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idPaciente);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        throw new SQLException("Erro ao obter id do prontuario");
    }

    private RegistroProntuarioDTO mapRegistro(ResultSet rs) throws SQLException {
        RegistroProntuarioDTO dto = new RegistroProntuarioDTO();
        dto.setId(rs.getLong("id"));
        dto.setIdProntuario(rs.getLong("id_prontuario"));
        dto.setIdConsulta(rs.getLong("id_consulta"));
        dto.setDiagnostico(rs.getString("diagnostico"));
        dto.setTratamento(rs.getString("tratamento"));
        dto.setDataHoraRegistro(rs.getTimestamp("datahora_registro").toString());
        dto.setPaciente(rs.getString("paciente"));
        dto.setCpfPaciente(rs.getString("cpf_paciente"));
        dto.setMedico(rs.getString("medico"));
        dto.setCpfMedico(rs.getString("cpf_medico"));
        dto.setEspecialidadeMedico(rs.getString("especialidade_medico"));
        dto.setSintomas(rs.getString("sintomas"));
        dto.setAnamnese(rs.getString("anamnese"));
        dto.setDataHoraConsulta(rs.getTimestamp("datahora_consulta").toString());
        return dto;
    }
}
