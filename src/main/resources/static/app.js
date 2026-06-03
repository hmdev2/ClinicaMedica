const output = document.querySelector("#output");
const crudOutput = document.querySelector("#crudOutput");
const payload = document.querySelector("#payload");
const resource = document.querySelector("#resource");
const recordId = document.querySelector("#recordId");
const mes = document.querySelector("#mes");
const ano = document.querySelector("#ano");
const pacienteProntuario = document.querySelector("#pacienteProntuario");

const samples = {
  pacientes: {
    nome: "Lucas",
    sobrenome: "Moura",
    nascimento: "1990-04-15",
    sexo: "Masculino",
    email: "lucas.moura@email.com",
    cpf: "12345678901",
    endereco: {
      logradouro: "Rua Central",
      numero: "100",
      complemento: "Sala 2",
      bairro: "Centro",
      cidade: "Salvador",
      estado: "BA",
      cep: "40000000"
    },
    telefones: [
      { ddi: "55", ddd: "71", prefixo: "99999", sufixo: "0000" }
    ]
  },
  medicos: {
    nome: "Carla",
    sobrenome: "Nunes",
    especialidade: "Dermatologia",
    cpf: "12345678902",
    crms: [{ numero: "445566", uf: "BA", rqe: 123 }]
  },
  colaboradores: {
    nome: "Marina",
    sobrenome: "Alves",
    cpf: "12345678903"
  },
  agendamentos: {
    idPaciente: 1,
    idMedico: 1,
    idColaborador: 1,
    dataHora: "2026-06-10 09:00:00",
    status: "Agendado"
  },
  consultas: {
    idAgendamento: 1,
    sintomas: "Dor de garganta",
    anamnese: "Paciente relata sintomas ha dois dias.",
    dataHoraRegistro: "2026-06-10 09:30:00"
  },
  exames: {
    idConsulta: 1,
    tipoExame: "Hemograma completo",
    resultado: null,
    dataSolicitacao: "2026-06-10",
    dataResultado: null
  },
  receitas: {
    idConsulta: 1,
    dataHoraEmissao: "2026-06-10 09:40:00",
    instrucoes: "Tomar conforme orientacao medica.",
    itens: [
      {
        nome: "Dipirona 500mg",
        principioAtivo: "Dipirona sodica",
        dosagem: "500mg",
        frequencia: "A cada 6 horas se dor",
        duracaoDias: 3
      }
    ]
  }
};

const views = {
  agendamentos: () => "/api/agendamentos",
  pacientes: () => "/api/pacientes",
  colaboradores: () => "/api/colaboradores",
  medicos: () => "/api/medicos",
  prontuario: () => `/api/prontuarios/paciente/${pacienteProntuario.value || 1}`,
  consultas: () => "/api/consultas",
  receitas: () => "/api/receitas",
  exames: () => "/api/exames",
  consultasMes: () => `/api/consultas?mes=${mes.value || 1}&ano=${ano.value || 2026}`
};

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options
  });
  const text = await response.text();
  let data = text;
  try {
    data = text ? JSON.parse(text) : null;
  } catch (_) {
  }

  if (!response.ok) {
    throw new Error(JSON.stringify(data, null, 2));
  }

  return data;
}

function print(target, value) {
  target.textContent = typeof value === "string" ? value : JSON.stringify(value, null, 2);
}

async function loadView(name) {
  print(output, "Carregando...");
  try {
    print(output, await request(views[name]()));
  } catch (error) {
    print(output, error.message);
  }
}

async function runCrud(method) {
  const id = recordId.value.trim();
  const url = `/api/${resource.value}${id ? `/${id}` : ""}`;
  const options = { method };

  if (method === "POST" || method === "PUT") {
    options.body = payload.value;
  }

  print(crudOutput, "Processando...");
  try {
    print(crudOutput, await request(url, options));
  } catch (error) {
    print(crudOutput, error.message);
  }
}

document.querySelectorAll("[data-view]").forEach((button) => {
  button.addEventListener("click", () => loadView(button.dataset.view));
});

document.querySelectorAll("[data-action]").forEach((button) => {
  button.addEventListener("click", () => runCrud(button.dataset.action));
});

document.querySelector("#refreshAll").addEventListener("click", () => loadView("agendamentos"));

resource.addEventListener("change", () => {
  payload.value = JSON.stringify(samples[resource.value], null, 2);
});

payload.value = JSON.stringify(samples[resource.value], null, 2);
loadView("agendamentos");
