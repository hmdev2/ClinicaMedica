package clinica.dto;

public class EnderecoDTO {
    private String endereco;
    
    public EnderecoDTO() {
    }

    public EnderecoDTO(String endereco) {
        this.endereco = endereco;
    }

    public String getEndereco() {
        return endereco;
    }
    
    @Override
    public String toString() {
        return endereco;
    }
}
