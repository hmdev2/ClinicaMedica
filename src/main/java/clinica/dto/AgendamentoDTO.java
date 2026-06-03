package clinica.dto;

public class AgendamentoDTO {

    private Long id;
    private Long idPaciente;
    private Long idMedico;
    private Long idColaborador;
    private String paciente;
    private String medico;
    private String colaborador;
    private String dataHora;
    private String status;

    public AgendamentoDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public Long getIdMedico() { return idMedico; }
    public void setIdMedico(Long idMedico) { this.idMedico = idMedico; }

    public Long getIdColaborador() { return idColaborador; }
    public void setIdColaborador(Long idColaborador) { this.idColaborador = idColaborador; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public String getColaborador() { return colaborador; }
    public void setColaborador(String colaborador) { this.colaborador = colaborador; }

    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}