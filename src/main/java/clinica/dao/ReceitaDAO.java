package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.ItemReceitaDTO;
import clinica.dto.ReceitaDTO;

public class ReceitaDAO extends BaseDAO {

    public List<ReceitaDTO> findAll() throws SQLException {
        String sql = "SELECT id_consulta FROM receita ORDER BY datahora_emissao DESC";
        List<ReceitaDTO> receitas = new ArrayList<>();

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                receitas.add(findByConsulta(rs.getLong("id_consulta")));
            }
        }

        return receitas;
    }

    public ReceitaDTO findById(Long id) throws SQLException {
        String sql = "SELECT id_consulta FROM receita WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return findByConsulta(rs.getLong("id_consulta"));
            }
        }

        return null;
    }

    public ReceitaDTO findByConsulta(Long idConsulta) throws SQLException {
        String sql = "SELECT r.id, r.id_consulta, r.datahora_emissao, r.instrucoes, "
                   + "ir.id AS item_id, ir.nome, ir.principio_ativo, ir.dosagem, ir.frequencia, ir.duracao_dias "
                   + "FROM receita r "
                   + "LEFT JOIN item_receita ir ON ir.id_receita = r.id "
                   + "WHERE r.id_consulta = ? "
                   + "ORDER BY ir.id";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, idConsulta);
            try (ResultSet rs = ps.executeQuery()) {
                ReceitaDTO receita = null;

                while (rs.next()) {
                    if (receita == null) {
                        receita = new ReceitaDTO();
                        receita.setId(rs.getLong("id"));
                        receita.setIdConsulta(rs.getLong("id_consulta"));
                        receita.setDataHoraEmissao(rs.getTimestamp("datahora_emissao").toString());
                        receita.setInstrucoes(rs.getString("instrucoes"));
                        receita.setItens(new ArrayList<>());
                    }

                    long itemId = rs.getLong("item_id");
                    if (!rs.wasNull()) {
                        ItemReceitaDTO item = new ItemReceitaDTO();
                        item.setId(itemId);
                        item.setIdReceita(receita.getId());
                        item.setNome(rs.getString("nome"));
                        item.setPrincipioAtivo(rs.getString("principio_ativo"));
                        item.setDosagem(rs.getString("dosagem"));
                        item.setFrequencia(rs.getString("frequencia"));
                        item.setDuracaoDias(rs.getInt("duracao_dias"));
                        receita.getItens().add(item);
                    }
                }

                return receita;
            }
        }
    }

    public void insert(ReceitaDTO dto) throws SQLException {
        String sqlReceita = "INSERT INTO receita (id_consulta, datahora_emissao, instrucoes) VALUES (?, ?, ?)";
        String sqlItem    = "INSERT INTO item_receita "
                          + "(id_receita, nome, principio_ativo, dosagem, frequencia, duracao_dias) "
                          + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.connect();

        try {
            con.setAutoCommit(false);

            Long receitaId;

            try (PreparedStatement ps = con.prepareStatement(sqlReceita, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, dto.getIdConsulta());
                ps.setTimestamp(2, Timestamp.valueOf(dto.getDataHoraEmissao()));
                ps.setString(3, dto.getInstrucoes());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        receitaId = rs.getLong(1);
                    } else {
                        throw new SQLException("Erro ao obter id da receita");
                    }
                }
            }

            if (dto.getItens() != null && !dto.getItens().isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(sqlItem)) {
                    for (ItemReceitaDTO item : dto.getItens()) {
                        ps.setLong(1, receitaId);
                        ps.setString(2, item.getNome());
                        ps.setString(3, item.getPrincipioAtivo());
                        ps.setString(4, item.getDosagem());
                        ps.setString(5, item.getFrequencia());
                        ps.setInt(6, item.getDuracaoDias());
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

    public void update(Long id, ReceitaDTO dto) throws SQLException {
        String sqlReceita = "UPDATE receita SET id_consulta = ?, datahora_emissao = ?, instrucoes = ? WHERE id = ?";
        String sqlDeleteItens = "DELETE FROM item_receita WHERE id_receita = ?";
        String sqlItem = "INSERT INTO item_receita "
                       + "(id_receita, nome, principio_ativo, dosagem, frequencia, duracao_dias) "
                       + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = DatabaseConnection.connect();

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlReceita)) {
                ps.setLong(1, dto.getIdConsulta());
                ps.setTimestamp(2, Timestamp.valueOf(dto.getDataHoraEmissao()));
                ps.setString(3, dto.getInstrucoes());
                ps.setLong(4, id);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalArgumentException("Receita não encontrada: id " + id);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlDeleteItens)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            if (dto.getItens() != null && !dto.getItens().isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(sqlItem)) {
                    for (ItemReceitaDTO item : dto.getItens()) {
                        ps.setLong(1, id);
                        ps.setString(2, item.getNome());
                        ps.setString(3, item.getPrincipioAtivo());
                        ps.setString(4, item.getDosagem());
                        ps.setString(5, item.getFrequencia());
                        ps.setInt(6, item.getDuracaoDias());
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
        String sqlItens = "DELETE FROM item_receita WHERE id_receita = ?";
        String sqlReceita = "DELETE FROM receita WHERE id = ?";

        Connection con = DatabaseConnection.connect();

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlItens)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlReceita)) {
                ps.setLong(1, id);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalArgumentException("Receita não encontrada: id " + id);
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
