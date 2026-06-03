const state = {
  pacientes: [],
  medicos: [],
  colaboradores: [],
  agendamentos: [],
  consultas: [],
  exames: [],
  receitas: [],
  prontuario: []
};

const lists = {
  pacientes: document.querySelector("#listaPacientes"),
  medicos: document.querySelector("#listaMedicos"),
  colaboradores: document.querySelector("#listaColaboradores"),
  agendamentos: document.querySelector("#listaAgendamentos"),
  consultas: document.querySelector("#listaConsultas"),
  exames: document.querySelector("#listaExames"),
  receitas: document.querySelector("#listaReceitas")
};

const toast = document.querySelector("#toast");

document.querySelectorAll(".tabs button").forEach((button) => {
  button.addEventListener("click", () => showTab(button.dataset.tab));
});

document.querySelectorAll("[data-load]").forEach((button) => {
  button.addEventListener("click", () => loadResource(button.dataset.load));
});

document.querySelectorAll("form[data-form]").forEach((form) => {
  form.addEventListener("submit", handleForm);
});

document.querySelectorAll("[data-fill-current-month]").forEach((button) => {
  button.addEventListener("click", fillCurrentMonth);
});

document.querySelector("#refreshDashboard").addEventListener("click", loadDashboard);

function showTab(id) {
  document.querySelectorAll(".tabs button").forEach((button) => {
    button.classList.toggle("active", button.dataset.tab === id);
  });
  document.querySelectorAll(".panel").forEach((panel) => {
    panel.classList.toggle("active", panel.id === id);
  });
}

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: { "Content-Type": "application/json" },
    ...options
  });

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message = data && (data.erro || data.error) ? data.erro || data.error : text;
    throw new Error(message || "Erro ao comunicar com o servidor");
  }

  return data;
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function asDateTime(value) {
  return value ? value.replace("T", " ") + ":00" : null;
}

function numberOrNull(value) {
  return value === "" || value == null ? null : Number(value);
}

function clean(object) {
  return Object.fromEntries(
    Object.entries(object).filter(([, value]) => value !== "" && value !== null && value !== undefined)
  );
}

async function handleForm(event) {
  event.preventDefault();

  const form = event.currentTarget;
  const action = event.submitter?.dataset.action || "post";

  try {
    switch (form.dataset.form) {
      case "paciente":
        await submitCrud("pacientes", action, buildPaciente(formData(form)));
        break;
      case "medico":
        await submitCrud("medicos", action, buildMedico(formData(form)));
        break;
      case "colaborador":
        await submitCrud("colaboradores", action, buildColaborador(formData(form)));
        break;
      case "agendamento":
        await api("/api/agendamentos", {
          method: "POST",
          body: JSON.stringify(buildAgendamento(formData(form)))
        });
        notify("Agendamento criado com sucesso");
        form.reset();
        await loadResource("agendamentos");
        await loadDashboard();
        break;
      case "agendamentoStatus":
        await submitAgendamentoStatus(formData(form));
        form.reset();
        await loadResource("agendamentos");
        await loadDashboard();
        break;
      case "consulta":
        await submitCrud("consultas", action, buildConsulta(formData(form)));
        break;
      case "prontuario":
        await submitProntuario(formData(form));
        form.reset();
        break;
      case "exame":
        await submitExame(form, action);
        break;
      case "receita":
        await submitCrud("receitas", action, buildReceita(formData(form)));
        break;
      case "relatorioMes":
        await loadConsultasMes(formData(form));
        break;
      case "historicoPaciente":
        await loadHistorico(formData(form));
        break;
      case "receitaConsulta":
        await loadReceitaConsulta(formData(form));
        break;
      case "itemReceita":
        await submitItemReceita(event.submitter?.dataset.action, formData(form));
        form.reset();
        await loadResource("receitas");
        break;
      default:
        throw new Error("Formulário não reconhecido");
    }
  } catch (error) {
    notify(error.message, true);
  }
}

async function submitItemReceita(action, data) {
  if (action === "delete") {
    requireId(numberOrNull(data.idItem));
    await api(`/api/receitas/itens/${data.idItem}`, { method: "DELETE" });
    notify("Medicamento removido da receita");
    return;
  }

  requireId(numberOrNull(data.idReceita));
  await api(`/api/receitas/${data.idReceita}/itens`, {
    method: "POST",
    body: JSON.stringify({
      nome: data.nomeMedicamento,
      principioAtivo: data.principioAtivo,
      dosagem: data.dosagem,
      frequencia: data.frequencia,
      duracaoDias: Number(data.duracaoDias)
    })
  });
  notify("Medicamento adicionado à receita");
}

async function submitCrud(resource, action, payload) {
  const id = payload.id;
  delete payload.id;

  if (action === "delete") {
    requireId(id);
    await api(`/api/${resource}/${id}`, { method: "DELETE" });
    notify("Registro removido com sucesso");
  } else if (action === "put") {
    requireId(id);
    await api(`/api/${resource}/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload)
    });
    notify("Registro atualizado com sucesso");
  } else {
    await api(`/api/${resource}`, {
      method: "POST",
      body: JSON.stringify(payload)
    });
    notify("Registro salvo com sucesso");
  }

  await loadResource(resource);
  await loadDashboard();
}

function requireId(id) {
  if (!id) throw new Error("Informe o ID para atualizar ou remover");
}

function buildPaciente(data) {
  return {
    id: numberOrNull(data.id),
    nome: data.nome,
    sobrenome: data.sobrenome,
    nascimento: data.nascimento,
    sexo: data.sexo,
    email: data.email,
    cpf: data.cpf,
    endereco: clean({
      logradouro: data.logradouro,
      numero: data.numero,
      bairro: data.bairro,
      cidade: data.cidade,
      estado: data.estado,
      cep: data.cep
    }),
    telefones: data.ddd || data.prefixo || data.sufixo
      ? [clean({ ddi: "55", ddd: data.ddd, prefixo: data.prefixo, sufixo: data.sufixo })]
      : []
  };
}

function buildMedico(data) {
  const crm = clean({
    numero: data.crmNumero,
    uf: data.crmUf,
    rqe: numberOrNull(data.rqe)
  });

  return {
    id: numberOrNull(data.id),
    nome: data.nome,
    sobrenome: data.sobrenome,
    especialidade: data.especialidade,
    cpf: data.cpf,
    crms: crm.numero ? [crm] : []
  };
}

function buildColaborador(data) {
  return {
    id: numberOrNull(data.id),
    nome: data.nome,
    sobrenome: data.sobrenome,
    cpf: data.cpf
  };
}

function buildAgendamento(data) {
  return {
    idPaciente: Number(data.idPaciente),
    idMedico: Number(data.idMedico),
    idColaborador: Number(data.idColaborador),
    dataHora: asDateTime(data.dataHora),
    status: data.status || "Agendado"
  };
}

function buildConsulta(data) {
  return {
    id: numberOrNull(data.id),
    idAgendamento: Number(data.idAgendamento),
    sintomas: data.sintomas,
    anamnese: data.anamnese,
    dataHoraRegistro: asDateTime(data.dataHoraRegistro)
  };
}

function buildExame(data) {
  return {
    id: numberOrNull(data.id),
    idConsulta: Number(data.idConsulta),
    tipoExame: data.tipoExame,
    resultado: data.resultado || null,
    dataSolicitacao: data.dataSolicitacao,
    dataResultado: data.dataResultado || null
  };
}

function buildReceita(data) {
  return {
    id: numberOrNull(data.id),
    idConsulta: Number(data.idConsulta),
    dataHoraEmissao: asDateTime(data.dataHoraEmissao),
    instrucoes: data.instrucoes,
    itens: [
      {
        nome: data.nomeMedicamento,
        principioAtivo: data.principioAtivo,
        dosagem: data.dosagem,
        frequencia: data.frequencia,
        duracaoDias: Number(data.duracaoDias)
      }
    ]
  };
}

async function submitAgendamentoStatus(data) {
  const id = Number(data.id);
  requireId(id);

  if (data.acao === "delete") {
    await api(`/api/agendamentos/${id}`, { method: "DELETE" });
    notify("Agendamento removido");
    return;
  }

  await api(`/api/agendamentos/${id}/${data.acao}`, { method: "PATCH" });
  notify(data.acao === "cancelar" ? "Agendamento cancelado" : "Agendamento marcado como realizado");
}

async function submitProntuario(data) {
  await api(`/api/consultas/${data.idConsulta}/prontuario`, {
    method: "POST",
    body: JSON.stringify({
      diagnostico: data.diagnostico,
      tratamento: data.tratamento,
      dataHoraRegistro: asDateTime(data.dataHoraRegistro)
    })
  });
  notify("Registro salvo no prontuário");
}

async function submitExame(form, action) {
  const payload = buildExame(formData(form));
  const id = payload.id;

  if (action === "patch") {
    requireId(id);
    await api(`/api/exames/${id}/resultado`, {
      method: "PATCH",
      body: JSON.stringify({
        resultado: payload.resultado,
        dataResultado: payload.dataResultado
      })
    });
    notify("Resultado do exame registrado");
    await loadResource("exames");
    return;
  }

  await submitCrud("exames", action, payload);
}

async function loadDashboard() {
  await Promise.all([
    loadResource("pacientes"),
    loadResource("medicos"),
    loadResource("colaboradores"),
    loadResource("agendamentos"),
    loadResource("consultas"),
    loadResource("exames"),
    loadResource("receitas")
  ]);

  document.querySelector("#countPacientes").textContent = state.pacientes.length;
  document.querySelector("#countMedicos").textContent = state.medicos.length;
  document.querySelector("#countColaboradores").textContent = state.colaboradores.length;
  document.querySelector("#countAgendamentos").textContent = state.agendamentos.length;
  document.querySelector("#countConsultas").textContent = state.consultas.length;
  document.querySelector("#countReceitas").textContent = state.receitas.length;
}

async function loadResource(resource) {
  state[resource] = await api(`/api/${resource}`);
  renderResource(resource);
}

function renderResource(resource) {
  const data = state[resource] || [];
  const target = lists[resource];
  if (!target) return;

  const columns = {
    pacientes: ["id", "nome", "sobrenome", "cpf", "email", "sexo"],
    medicos: ["id", "nome", "sobrenome", "especialidade", "cpf", "crms"],
    colaboradores: ["id", "nome", "sobrenome", "cpf"],
    agendamentos: ["id", "paciente", "medico", "colaborador", "dataHora", "status"],
    consultas: ["id", "idAgendamento", "paciente", "medico", "sintomas", "anamnese", "dataHoraRegistro"],
    exames: ["id", "idConsulta", "tipoExame", "resultado", "dataSolicitacao", "dataResultado"],
    receitas: ["id", "idConsulta", "dataHoraEmissao", "instrucoes", "itens"]
  }[resource];

  target.innerHTML = table(data, columns);
}

async function loadConsultasMes(data) {
  const result = await api(`/api/consultas?mes=${data.mes}&ano=${data.ano}`);
  document.querySelector("#resultadoRelatorio").innerHTML = table(result, [
    "id",
    "idAgendamento",
    "paciente",
    "medico",
    "sintomas",
    "anamnese",
    "dataHoraRegistro"
  ]);
  notify("Relatório gerado");
}

async function loadHistorico(data) {
  const result = await api(`/api/prontuarios/paciente/${data.idPaciente}`);
  state.prontuario = result;

  const target = document.querySelector("#resultadoProntuario");
  if (target) target.innerHTML = prontuarioCards(result);

  notify("Histórico carregado");
}

async function loadReceitaConsulta(data) {
  const result = await api(`/api/consultas/${data.idConsulta}/receita`);
  document.querySelector("#resultadoReceitaConsulta").innerHTML = receitaCard(result);
  notify("Receita carregada");
}

function table(data, columns) {
  if (!data || data.length === 0) {
    return '<p class="empty">Nenhum registro encontrado.</p>';
  }

  const head = columns.map((column) => `<th>${label(column)}</th>`).join("");
  const rows = data.map((item) => {
    const cells = columns.map((column) => `<td>${formatValue(column, item[column])}</td>`).join("");
    return `<tr>${cells}</tr>`;
  }).join("");

  return `<table><thead><tr>${head}</tr></thead><tbody>${rows}</tbody></table>`;
}

function prontuarioCards(data) {
  if (!data || data.length === 0) {
    return '<p class="empty">Nenhum registro de prontuário encontrado.</p>';
  }

  return data.map((item) => `
    <article class="record-card">
      <div class="record-head">
        <div>
          <strong>${escapeHtml(item.paciente || "Paciente")}</strong>
          <span>${escapeHtml(item.cpfPaciente || "CPF não informado")}</span>
        </div>
        <span class="badge ok">${escapeHtml(item.dataHoraConsulta || item.dataHoraRegistro || "-")}</span>
      </div>
      <dl class="record-grid">
        <div><dt>Médico</dt><dd>${escapeHtml(item.medico || "-")}</dd></div>
        <div><dt>Especialidade</dt><dd>${escapeHtml(item.especialidadeMedico || "-")}</dd></div>
        <div><dt>Sintomas</dt><dd>${escapeHtml(item.sintomas || "-")}</dd></div>
        <div><dt>Anamnese</dt><dd>${escapeHtml(item.anamnese || "-")}</dd></div>
        <div><dt>Diagnóstico</dt><dd>${escapeHtml(item.diagnostico || "-")}</dd></div>
        <div><dt>Tratamento</dt><dd>${escapeHtml(item.tratamento || "-")}</dd></div>
      </dl>
    </article>
  `).join("");
}

function receitaCard(receita) {
  if (!receita) {
    return '<p class="empty">Receita não encontrada para esta consulta.</p>';
  }

  const itens = receita.itens && receita.itens.length
    ? receita.itens.map((item) => `
      <li>
        <strong>${escapeHtml(item.nome || "-")} <small style="font-weight:normal;color:#61727d">#${item.id}</small></strong>
        <span>${escapeHtml(item.principioAtivo || "-")} | ${escapeHtml(item.dosagem || "-")} | ${escapeHtml(item.frequencia || "-")} | ${escapeHtml(String(item.duracaoDias || "-"))} dias</span>
      </li>
    `).join("")
    : "<li><span>Nenhum medicamento informado.</span></li>";

  return `
    <article class="prescription">
      <div class="record-head">
        <div>
          <strong>Receita da consulta #${escapeHtml(String(receita.idConsulta || "-"))}</strong>
          <span>Emitida em ${escapeHtml(receita.dataHoraEmissao || "-")}</span>
        </div>
        <span class="badge ok">Prescrição médica</span>
      </div>
      <ul>${itens}</ul>
      <p>${escapeHtml(receita.instrucoes || "Sem instruções adicionais.")}</p>
    </article>
  `;
}

function label(value) {
  return value
    .replace(/([A-Z])/g, " $1")
    .replace(/^id$/, "ID")
    .replace(/^id /, "ID ")
    .replace("data Hora", "Data hora")
    .replace("cpf", "CPF")
    .replace("crms", "CRMs")
    .replace("itens", "Medicamentos");
}

function formatValue(column, value) {
  if (value == null || value === "") return "-";

  if (column === "status") {
    const kind = value === "Realizado" ? "ok" : value === "Cancelado" ? "off" : "warn";
    return `<span class="badge ${kind}">${escapeHtml(value)}</span>`;
  }

  if (Array.isArray(value)) {
    if (value.length === 0) return "-";
    return value.map((item) => {
      if (item.numero && item.uf) return `CRM-${escapeHtml(item.uf)} ${escapeHtml(item.numero)}`;
      if (item.nome) return `${escapeHtml(item.nome)} (${escapeHtml(item.dosagem || "")})`;
      return escapeHtml(JSON.stringify(item));
    }).join("<br>");
  }

  if (typeof value === "object") return escapeHtml(JSON.stringify(value));
  return escapeHtml(String(value));
}

function fillCurrentMonth(event) {
  const form = event.currentTarget.closest("form");
  const now = new Date();
  form.elements.mes.value = now.getMonth() + 1;
  form.elements.ano.value = now.getFullYear();
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function notify(message, isError = false) {
  toast.textContent = message;
  toast.hidden = false;
  toast.style.background = isError ? "#8f1b13" : "#152b35";
  clearTimeout(notify.timer);
  notify.timer = setTimeout(() => {
    toast.hidden = true;
  }, 4200);
}

fillCurrentMonth({ currentTarget: document.querySelector("[data-fill-current-month]") });
loadDashboard().catch((error) => notify(error.message, true));