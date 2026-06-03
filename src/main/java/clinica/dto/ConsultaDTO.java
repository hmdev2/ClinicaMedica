package clinica.dto;

import java.util.List;

public class ConsultaDTO {

    private Long id;
    private Long idAgendamento;

    private String sintomas;
    private String anamnese;
    private String dataHoraRegistro;
    private String paciente;
    private String medico;
    private RegistroProntuarioDTO registroProntuario;
    private ReceitaDTO receita;
    private List<ExameDTO> exames;

    public ConsultaDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(Long idAgendamento) { this.idAgendamento = idAgendamento; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getAnamnese() { return anamnese; }
    public void setAnamnese(String anamnese) { this.anamnese = anamnese; }

    public String getDataHoraRegistro() { return dataHoraRegistro; }
    public void setDataHoraRegistro(String dataHoraRegistro) { this.dataHoraRegistro = dataHoraRegistro; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public RegistroProntuarioDTO getRegistroProntuario() { return registroProntuario; }
    public void setRegistroProntuario(RegistroProntuarioDTO registroProntuario) { this.registroProntuario = registroProntuario; }

    public ReceitaDTO getReceita() { return receita; }
    public void setReceita(ReceitaDTO receita) { this.receita = receita; }

    public List<ExameDTO> getExames() { return exames; }
    public void setExames(List<ExameDTO> exames) { this.exames = exames; }
}