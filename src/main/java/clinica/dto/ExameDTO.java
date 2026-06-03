package clinica.dto;

public class ExameDTO {

    private Long id;
    private Long idConsulta;

    private String tipoExame;
    private String resultado;
    private String dataSolicitacao;
    private String dataResultado;

    public ExameDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Long idConsulta) { this.idConsulta = idConsulta; }

    public String getTipoExame() { return tipoExame; }
    public void setTipoExame(String tipoExame) { this.tipoExame = tipoExame; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(String dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getDataResultado() { return dataResultado; }
    public void setDataResultado(String dataResultado) { this.dataResultado = dataResultado; }
}