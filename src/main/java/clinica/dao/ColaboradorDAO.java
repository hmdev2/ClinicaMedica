package clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.ColaboradorDTO;

public class ColaboradorDAO extends BaseDAO {

    public List<ColaboradorDTO> findAll() throws SQLException {
        String sql = "SELECT id, nome, sobrenome, cpf FROM colaborador ORDER BY nome, sobrenome";
        List<ColaboradorDTO> colaboradores = new ArrayList<>();

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                colaboradores.add(mapRow(rs));
            }
        }

        return colaboradores;
    }

    public ColaboradorDTO findById(Long id) throws SQLException {
        String sql = "SELECT id, nome, sobrenome, cpf FROM colaborador WHERE id = ?";

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

    public void insert(ColaboradorDTO dto) throws SQLException {
        String sql = "INSERT INTO colaborador (nome, sobrenome, cpf) VALUES (?, ?, ?)";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, dto.getNome());
            ps.setString(2, dto.getSobrenome());
            ps.setString(3, dto.getCpf());
            ps.executeUpdate();
        }
    }

    public void update(Long id, ColaboradorDTO dto) throws SQLException {
        String sql = "UPDATE colaborador SET nome = ?, sobrenome = ?, cpf = ? WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, dto.getNome());
            ps.setString(2, dto.getSobrenome());
            ps.setString(3, dto.getCpf());
            ps.setLong(4, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Colaborador nao encontrado: id " + id);
            }
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM colaborador WHERE id = ?";

        try (
            Connection con = DatabaseConnection.connect();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Colaborador nao encontrado: id " + id);
            }
        }
    }

    private ColaboradorDTO mapRow(ResultSet rs) throws SQLException {
        ColaboradorDTO dto = new ColaboradorDTO();
        dto.setId(rs.getLong("id"));
        dto.setNome(rs.getString("nome"));
        dto.setSobrenome(rs.getString("sobrenome"));
        dto.setCpf(rs.getString("cpf"));
        return dto;
    }
}
