package clinica.dto;

public class RegistroProntuarioDTO {

    private Long id;
    private Long idProntuario;
    private Long idConsulta;

    private String diagnostico;
    private String tratamento;
    private String dataHoraRegistro;

    private String paciente;
    private String cpfPaciente;
    private String medico;
    private String cpfMedico;
    private String especialidadeMedico;
    private String sintomas;
    private String anamnese;
    private String dataHoraConsulta;

    public RegistroProntuarioDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdProntuario() { return idProntuario; }
    public void setIdProntuario(Long idProntuario) { this.idProntuario = idProntuario; }

    public Long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Long idConsulta) { this.idConsulta = idConsulta; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamento() { return tratamento; }
    public void setTratamento(String tratamento) { this.tratamento = tratamento; }

    public String getDataHoraRegistro() { return dataHoraRegistro; }
    public void setDataHoraRegistro(String dataHoraRegistro) { this.dataHoraRegistro = dataHoraRegistro; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }

    public String getCpfPaciente() { return cpfPaciente; }
    public void setCpfPaciente(String cpfPaciente) { this.cpfPaciente = cpfPaciente; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public String getCpfMedico() { return cpfMedico; }
    public void setCpfMedico(String cpfMedico) { this.cpfMedico = cpfMedico; }

    public String getEspecialidadeMedico() { return especialidadeMedico; }
    public void setEspecialidadeMedico(String especialidadeMedico) { this.especialidadeMedico = especialidadeMedico; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getAnamnese() { return anamnese; }
    public void setAnamnese(String anamnese) { this.anamnese = anamnese; }

    public String getDataHoraConsulta() { return dataHoraConsulta; }
    public void setDataHoraConsulta(String dataHoraConsulta) { this.dataHoraConsulta = dataHoraConsulta; }
}