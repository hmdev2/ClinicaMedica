package clinica.dto;

public class CrmDTO {
	private String numero;
    private String uf;
    private Integer rqe;
 
    public CrmDTO() {
    }
 
    public String getNumero() {
        return numero;
    }
 
    public void setNumero(String numero) {
        this.numero = numero;
    }
 
    public String getUf() {
        return uf;
    }
 
    public void setUf(String uf) {
        this.uf = uf;
    }
 
    public Integer getRqe() {
        return rqe;
    }
 
    public void setRqe(Integer rqe) {
        this.rqe = rqe;
    }
}