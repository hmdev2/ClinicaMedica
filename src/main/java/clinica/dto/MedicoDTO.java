package clinica.dto;

import java.util.List;

public class MedicoDTO {
	 private Long id;
	 
	    private String nome;
	    private String sobrenome;
	    private String especialidade;
	    private String cpf;
	 
	    private List<CrmDTO> crms;
	 
	    public MedicoDTO() {
	    }
	 
	    public Long getId() {
	        return id;
	    }
	 
	    public void setId(Long id) {
	        this.id = id;
	    }
	 
	    public String getNome() {
	        return nome;
	    }
	 
	    public void setNome(String nome) {
	        this.nome = nome;
	    }
	 
	    public String getSobrenome() {
	        return sobrenome;
	    }
	 
	    public void setSobrenome(String sobrenome) {
	        this.sobrenome = sobrenome;
	    }
	 
	    public String getEspecialidade() {
	        return especialidade;
	    }
	 
	    public void setEspecialidade(String especialidade) {
	        this.especialidade = especialidade;
	    }
	 
	    public String getCpf() {
	        return cpf;
	    }
	 
	    public void setCpf(String cpf) {
	        this.cpf = cpf;
	    }
	 
	    public List<CrmDTO> getCrms() {
	        return crms;
	    }
	 
	    public void setCrms(List<CrmDTO> crms) {
	        this.crms = crms;
	    }
}