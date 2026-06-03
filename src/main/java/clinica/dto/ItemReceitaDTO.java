package clinica.dto;

public class ItemReceitaDTO {

    private Long id;
    private Long idReceita;

    private String nome;
    private String principioAtivo;
    private String dosagem;
    private String frequencia;
    private Integer duracaoDias;

    public ItemReceitaDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdReceita() { return idReceita; }
    public void setIdReceita(Long idReceita) { this.idReceita = idReceita; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getPrincipioAtivo() { return principioAtivo; }
    public void setPrincipioAtivo(String principioAtivo) { this.principioAtivo = principioAtivo; }

    public String getDosagem() { return dosagem; }
    public void setDosagem(String dosagem) { this.dosagem = dosagem; }

    public String getFrequencia() { return frequencia; }
    public void setFrequencia(String frequencia) { this.frequencia = frequencia; }

    public Integer getDuracaoDias() { return duracaoDias; }
    public void setDuracaoDias(Integer duracaoDias) { this.duracaoDias = duracaoDias; }
}