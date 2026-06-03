package clinica.dto;

import java.util.List;

public class ReceitaDTO {

    private Long id;
    private Long idConsulta;

    private String dataHoraEmissao;
    private String instrucoes;

    private List<ItemReceitaDTO> itens;

    public ReceitaDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Long idConsulta) { this.idConsulta = idConsulta; }

    public String getDataHoraEmissao() { return dataHoraEmissao; }
    public void setDataHoraEmissao(String dataHoraEmissao) { this.dataHoraEmissao = dataHoraEmissao; }

    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }

    public List<ItemReceitaDTO> getItens() { return itens; }
    public void setItens(List<ItemReceitaDTO> itens) { this.itens = itens; }
}