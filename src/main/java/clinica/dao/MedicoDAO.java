package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.CrmDTO;
import clinica.dto.MedicoDTO;

public class MedicoDAO extends BaseDAO {
	
	public void insert(MedicoDTO dto) throws SQLException {
        String sqlMedico = "INSERT INTO medico (nome, sobrenome, especialidade, cpf) VALUES (?, ?, ?, ?)";
        String sqlCrm = "INSERT INTO crm (id_medico, numero, uf, rqe) VALUES (?, ?, ?, ?)";
 
        Connection con = DatabaseConnection.connect();
 
        try {
            con.setAutoCommit(false);
 
            Long medicoId;
 
            try (PreparedStatement ps = con.prepareStatement(sqlMedico, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, dto.getNome());
                ps.setString(2, dto.getSobrenome());
                ps.setString(3, dto.getEspecialidade());
                ps.setString(4, dto.getCpf());
                ps.executeUpdate();
 
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        medicoId = rs.getLong(1);
                    } else {
                        throw new SQLException("Erro ao obter id do médico");
                    }
                }
            }
 
            if (dto.getCrms() != null && !dto.getCrms().isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(sqlCrm)) {
                    for (CrmDTO crm : dto.getCrms()) {
                        ps.setLong(1, medicoId);
                        ps.setString(2, crm.getNumero());
                        ps.setString(3, crm.getUf());
                        if (crm.getRqe() != null) {
                            ps.setInt(4, crm.getRqe());
                        } else {
                            ps.setNull(4, java.sql.Types.INTEGER);
                        }
                        ps.addBatch();
                    }
                    ps.executeBatch();
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

	public List<MedicoDTO> findAll() throws SQLException {
        String sql = "SELECT m.id, m.nome, m.sobrenome, m.especialidade, m.cpf, "
                   + "c.numero AS crm_numero, c.uf AS crm_uf, c.rqe AS crm_rqe "
                   + "FROM medico m "
                   + "LEFT JOIN crm c ON c.id_medico = m.id "
                   + "ORDER BY m.nome, m.sobrenome, c.uf";
 
        List<MedicoDTO> lista = new ArrayList<>();
 
        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            MedicoDTO atual = null;
 
            while (rs.next()) {
                long id = rs.getLong("id");
 
                if (atual == null || atual.getId() != id) {
                    atual = new MedicoDTO();
                    atual.setId(id);
                    atual.setNome(rs.getString("nome"));
                    atual.setSobrenome(rs.getString("sobrenome"));
                    atual.setEspecialidade(rs.getString("especialidade"));
                    atual.setCpf(rs.getString("cpf"));
                    atual.setCrms(new ArrayList<>());
                    lista.add(atual);
                }
 
                String crmNumero = rs.getString("crm_numero");
                if (crmNumero != null) {
                    CrmDTO crm = new CrmDTO();
                    crm.setNumero(crmNumero);
                    crm.setUf(rs.getString("crm_uf"));
                    int rqe = rs.getInt("crm_rqe");
                    crm.setRqe(rs.wasNull() ? null : rqe);
                    atual.getCrms().add(crm);
                }
            }
        }
 
        return lista;
    }
	
	public MedicoDTO findById(Long id) throws SQLException {
        String sql = "SELECT m.id, m.nome, m.sobrenome, m.especialidade, m.cpf, "
                   + "c.numero AS crm_numero, c.uf AS crm_uf, c.rqe AS crm_rqe "
                   + "FROM medico m "
                   + "LEFT JOIN crm c ON c.id_medico = m.id "
                   + "WHERE m.id = ? ";
 
        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
 
            try (ResultSet rs = ps.executeQuery()) {
                MedicoDTO medico = null;
 
                while (rs.next()) {
                    if (medico == null) {
                        medico = new MedicoDTO();
                        medico.setId(rs.getLong("id"));
                        medico.setNome(rs.getString("nome"));
                        medico.setSobrenome(rs.getString("sobrenome"));
                        medico.setEspecialidade(rs.getString("especialidade"));
                        medico.setCpf(rs.getString("cpf"));
                        medico.setCrms(new ArrayList<>());
                    }
 
                    String crmNumero = rs.getString("crm_numero");
                    if (crmNumero != null) {
                        CrmDTO crm = new CrmDTO();
                        crm.setNumero(crmNumero);
                        crm.setUf(rs.getString("crm_uf"));
                        int rqe = rs.getInt("crm_rqe");
                        crm.setRqe(rs.wasNull() ? null : rqe);
                        medico.getCrms().add(crm);
                    }
                }
 
                return medico;
            }
        }
    }
	
	public void update(Long id, MedicoDTO dto) throws SQLException {
        String sqlMedico = "UPDATE medico SET nome = ?, sobrenome = ?, especialidade = ?, cpf = ? WHERE id = ?";
        String sqlDeleteCrm = "DELETE FROM crm WHERE id_medico = ?";
        String sqlInsertCrm = "INSERT INTO crm (id_medico, numero, uf, rqe) VALUES (?, ?, ?, ?)";
 
        Connection con = DatabaseConnection.connect();

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlMedico)) {
                ps.setString(1, dto.getNome());
                ps.setString(2, dto.getSobrenome());
                ps.setString(3, dto.getEspecialidade());
                ps.setString(4, dto.getCpf());
                ps.setLong(5, id);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalArgumentException("Médico não encontrado: id " + id);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlDeleteCrm)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            if (dto.getCrms() != null && !dto.getCrms().isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(sqlInsertCrm)) {
                    for (CrmDTO crm : dto.getCrms()) {
                        ps.setLong(1, id);
                        ps.setString(2, crm.getNumero());
                        ps.setString(3, crm.getUf());
                        if (crm.getRqe() != null) {
                            ps.setInt(4, crm.getRqe());
                        } else {
                            ps.setNull(4, java.sql.Types.INTEGER);
                        }
                        ps.addBatch();
                    }
                    ps.executeBatch();
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

    public void delete(Long id) throws SQLException {

        String sqlFindConsultas      = "SELECT c.id FROM consulta c JOIN agendamento a ON c.id_agendamento = a.id WHERE a.id_medico = ?";

        String sqlItemReceita        = "DELETE FROM item_receita WHERE id_receita IN (SELECT id FROM receita WHERE id_consulta = ?)";
        String sqlReceita            = "DELETE FROM receita WHERE id_consulta = ?";
        String sqlExame              = "DELETE FROM exame WHERE id_consulta = ?";
        String sqlRegistroProntuario = "DELETE FROM registro_prontuario WHERE id_consulta = ?";

        String sqlConsulta           = "DELETE FROM consulta WHERE id_agendamento IN (SELECT id FROM agendamento WHERE id_medico = ?)";
        String sqlAgendamento        = "DELETE FROM agendamento WHERE id_medico = ?";
        String sqlCrm                = "DELETE FROM crm WHERE id_medico = ?";
        String sqlMedico             = "DELETE FROM medico WHERE id = ?";

        Connection con = DatabaseConnection.connect();

        try {
            con.setAutoCommit(false);

            List<Long> consultaIds = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sqlFindConsultas)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        consultaIds.add(rs.getLong("id"));
                    }
                }
            }

            for (Long consultaId : consultaIds) {
                try (PreparedStatement ps = con.prepareStatement(sqlItemReceita)) {
                    ps.setLong(1, consultaId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlReceita)) {
                    ps.setLong(1, consultaId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlExame)) {
                    ps.setLong(1, consultaId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlRegistroProntuario)) {
                    ps.setLong(1, consultaId);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlConsulta)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlAgendamento)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlCrm)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlMedico)) {
                ps.setLong(1, id);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalArgumentException("Médico não encontrado: id " + id);
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
}
