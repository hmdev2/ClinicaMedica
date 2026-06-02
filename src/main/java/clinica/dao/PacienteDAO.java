package clinica.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clinica.connection.DatabaseConnection;
import clinica.dto.EnderecoDTO;
import clinica.dto.PacienteDTO;

public class PacienteDAO extends BaseDAO {
	
	public List<PacienteDTO> findAll() throws SQLException {
	    String sql = "SELECT id, nome, sobrenome, nascimento, sexo, email, cpf FROM paciente ORDER BY nome, sobrenome";

	    List<PacienteDTO> lista = new ArrayList<>();

	    try (
	        Connection con = DatabaseConnection.connect();
	        PreparedStatement ps = con.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	    ) {
	        while (rs.next()) {
	            PacienteDTO dto = new PacienteDTO();

	            dto.setId(rs.getLong("id"));
	            dto.setNome(rs.getString("nome"));
	            dto.setSobrenome(rs.getString("sobrenome"));
	            dto.setNascimento(rs.getDate("nascimento").toString());
	            dto.setSexo(rs.getString("sexo"));
	            dto.setEmail(rs.getString("email"));
	            dto.setCpf(rs.getString("cpf"));

	            lista.add(dto);
	        }
	    }

	    return lista;
	}
	
	public EnderecoDTO findEnderecoByPacienteId(Long pacienteId) throws SQLException {
	    String sql = "SELECT logradouro, numero, complemento, bairro, cidade, estado, cep " +
	                 "FROM endereco WHERE id_paciente = ?";

	    try (
	        Connection con = DatabaseConnection.connect();
	        PreparedStatement ps = con.prepareStatement(sql);
	    ) {
	        ps.setLong(1, pacienteId);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                EnderecoDTO e = new EnderecoDTO();

	                e.setLogradouro(rs.getString("logradouro"));
	                e.setNumero(rs.getString("numero"));
	                e.setComplemento(rs.getString("complemento"));
	                e.setBairro(rs.getString("bairro"));
	                e.setCidade(rs.getString("cidade"));
	                e.setEstado(rs.getString("estado"));
	                e.setCep(rs.getString("cep"));

	                return e;
	            }
	        }
	    }

	    return null;
	}
	
	public void insert(PacienteDTO dto) throws SQLException {

		String sqlPaciente = "INSERT INTO paciente (nome, sobrenome, nascimento, sexo, email, cpf) VALUES (?, ?, ?, ?, ?, ?)";
	    String sqlEndereco = "INSERT INTO endereco (id_paciente, logradouro, numero, complemento, bairro, cidade, estado, cep) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	    String sqlTelefone = "INSERT INTO telefone (id_paciente, ddi, ddd, prefixo, sufixo) VALUES (?, ?, ?, ?, ?)";
	    
	    Connection con = DatabaseConnection.connect();
	    
	    try {
	    	con.setAutoCommit(false);
	    	
	    	Long pacienteId;
	    	
	    	try(PreparedStatement ps = con.prepareStatement(sqlPaciente, PreparedStatement.RETURN_GENERATED_KEYS);) {
	    		
	    		ps.setString(1, dto.getNome());
	    		ps.setString(2, dto.getSobrenome());
	    		ps.setDate(3, Date.valueOf(dto.getNascimento()));
	    		ps.setString(4, dto.getSexo());
	    		ps.setString(5, dto.getEmail());
	    		ps.setString(6, dto.getCpf());
	    		
	    		ps.executeUpdate();
	    		
	    		try(ResultSet rs = ps.getGeneratedKeys()) {
	    			if(rs.next()) {
	    				pacienteId = rs.getLong(1);
	    			} else {
	    				throw new SQLException("Erro ao obter Id do paciente");
	    			}
	    		}
	    	}
	    	
	    	if(dto.getEndereco() != null) {
	    		try(PreparedStatement ps = con.prepareStatement(sqlEndereco)) {
	    			var e = dto.getEndereco();
	    			
	    			ps.setLong(1, pacienteId);
	    			ps.setString(2, e.getLogradouro());
	    			ps.setString(3, e.getNumero());
	    			ps.setString(4, e.getComplemento());
	    			ps.setString(5, e.getBairro());
	    			ps.setString(6, e.getCidade());
	    			ps.setString(7, e.getEstado());
	    			ps.setString(8, e.getCep());
	    			
	    			ps.executeUpdate();
	    		}
	    	}
	    	
	    	if(dto.getTelefones() != null && !dto.getTelefones().isEmpty()) {
	    		try (PreparedStatement ps = con.prepareStatement(sqlTelefone)) {
	    			
	    			for(var t : dto.getTelefones()) {
	    				ps.setLong(1, pacienteId);
	    				ps.setString(2, t.getDdi());
	    				ps.setString(3, t.getDdd());
	    				ps.setString(4, t.getPrefixo());
	    				ps.setString(5, t.getSufixo());
	    				
	    				ps.addBatch();
	    			}
	    			
	    			ps.executeBatch();
	    		}
	    	}
	    	
	    	con.commit();
	    	
	    } catch(Exception e) {
	    	con.rollback();
	    	throw e;
	    } finally {
	    	con.setAutoCommit(true);
	    	con.close();
	    }

	}
	
}
