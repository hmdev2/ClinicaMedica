package clinica.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.ExameDTO;

public class ExameDAO extends BaseDAO {

    public List<ExameDTO> findAll() throws SQLException {
        String sql = "SELECT id, id_consulta, tipo_exame, resultado, data_solicitacao, data_resultado "
                   + "FROM exame ORDER BY data_solicitacao DESC, id DESC";

        List<ExameDTO> lista = new ArrayList<>();

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

    public ExameDTO findById(Long id) throws SQLException {
        String sql = "SELECT id, id_consulta, tipo_exame, resultado, data_solicitacao, data_resultado "
                   + "FROM exame WHERE id = ?";

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

    public List<ExameDTO> findByConsulta(Long idConsulta) throws SQLException {
        String sql = "SELECT id, id_consulta, tipo_exame, resultado, data_solicitacao, data_resultado "
                   + "FROM exame WHERE id_consulta = ? ORDER BY data_solicitacao";

        List<ExameDTO> lista = new ArrayList<>();

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, idConsulta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }

        return lista;
    }

    public void insert(ExameDTO dto) throws SQLException {
        String sql = "INSERT INTO exame (id_consulta, tipo_exame, resultado, data_solicitacao, data_resultado) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, dto.getIdConsulta());
            ps.setString(2, dto.getTipoExame());
            ps.setString(3, dto.getResultado());
            ps.setDate(4, Date.valueOf(dto.getDataSolicitacao()));
            ps.setDate(5, dto.getDataResultado() != null ? Date.valueOf(dto.getDataResultado()) : null);
            ps.executeUpdate();
        }
    }

    public void registrarResultado(Long id, String resultado, String dataResultado) throws SQLException {
        String sql = "UPDATE exame SET resultado = ?, data_resultado = ? WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, resultado);
            ps.setDate(2, Date.valueOf(dataResultado));
            ps.setLong(3, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Exame não encontrado: id " + id);
            }
        }
    }

    public void update(Long id, ExameDTO dto) throws SQLException {
        String sql = "UPDATE exame "
                   + "SET id_consulta = ?, tipo_exame = ?, resultado = ?, data_solicitacao = ?, data_resultado = ? "
                   + "WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, dto.getIdConsulta());
            ps.setString(2, dto.getTipoExame());
            ps.setString(3, dto.getResultado());
            ps.setDate(4, Date.valueOf(dto.getDataSolicitacao()));
            ps.setDate(5, dto.getDataResultado() != null ? Date.valueOf(dto.getDataResultado()) : null);
            ps.setLong(6, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Exame não encontrado: id " + id);
            }
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM exame WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Exame não encontrado: id " + id);
            }
        }
    }

    private ExameDTO mapRow(ResultSet rs) throws SQLException {
        ExameDTO dto = new ExameDTO();
        dto.setId(rs.getLong("id"));
        dto.setIdConsulta(rs.getLong("id_consulta"));
        dto.setTipoExame(rs.getString("tipo_exame"));
        dto.setResultado(rs.getString("resultado"));
        dto.setDataSolicitacao(rs.getDate("data_solicitacao").toString());
        Date dr = rs.getDate("data_resultado");
        dto.setDataResultado(dr != null ? dr.toString() : null);
        return dto;
    }
}
