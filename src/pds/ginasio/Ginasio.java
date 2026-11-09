package pds.ginasio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pds.dominio.Membro;
import pds.dominio.Plano;
import pds.dominio.RegistoUtilizacao;
import pds.dominio.RegraPlano;
import pds.dominio.ResultadoAcesso;
import pds.dominio.TipoServico;
import pds.equipamentos.Duche;
import pds.equipamentos.Equipamento;
import pds.equipamentos.SalaAula;
import pds.servicos.CatalogoServicos;
import pds.servicos.Servico;
import pds.util.RelogioSimulado;

public final class Ginasio {
    private final String nome;
    private final CatalogoServicos catalogoServicos = new CatalogoServicos();
    private final Map<Integer, Membro> membros = new LinkedHashMap<>();
    private final Map<Integer, Equipamento> equipamentos = new LinkedHashMap<>();

    public Ginasio(String nome) {
        this.nome = Objects.requireNonNull(nome).trim();
        if (this.nome.isEmpty()) {
            throw new IllegalArgumentException("O nome do ginásio não pode estar vazio.");
        }
    }

    public String getNome() {
        return nome;
    }

    public CatalogoServicos getCatalogoServicos() {
        return catalogoServicos;
    }

    public synchronized void adicionarMembro(Membro membro) {
        Objects.requireNonNull(membro);
        if (membros.putIfAbsent(membro.getId(), membro) != null) {
            throw new IllegalArgumentException("Já existe um membro com o identificador " + membro.getId() + ".");
        }
    }

    public synchronized void adicionarEquipamento(Equipamento equipamento) {
        Objects.requireNonNull(equipamento);
        if (equipamentos.containsKey(equipamento.getId())) {
            throw new IllegalArgumentException("Já existe um equipamento com o identificador "
                    + equipamento.getId() + ".");
        }
        Servico servicoCatalogado = catalogoServicos.get(equipamento.getServico().getTipo());
        if (servicoCatalogado != equipamento.getServico()) {
            throw new IllegalArgumentException("O equipamento tem de usar o serviço deste ginásio.");
        }
        equipamentos.put(equipamento.getId(), equipamento);
    }

    public synchronized Membro getMembro(int id) {
        return membros.get(id);
    }

    public synchronized Equipamento getEquipamento(int id) {
        return equipamentos.get(id);
    }

    public synchronized List<Membro> getMembros() {
        return Collections.unmodifiableList(new ArrayList<>(membros.values()));
    }

    public synchronized List<Equipamento> getEquipamentos() {
        return Collections.unmodifiableList(new ArrayList<>(equipamentos.values()));
    }

    public synchronized ResultadoAcesso usarEquipamento(int equipamentoId, int membroId) {
        Equipamento equipamento = equipamentos.get(equipamentoId);
        if (equipamento == null) {
            return ResultadoAcesso.recusado("Equipamento desconhecido.");
        }
        Membro membro = membros.get(membroId);
        if (membro == null) {
            return ResultadoAcesso.recusado("Membro desconhecido.");
        }

        LocalDateTime agora = RelogioSimulado.getTempoAtual();
        TipoServico tipo = equipamento.getServico().getTipo();
        Integer nivelAula = equipamento instanceof SalaAula ? ((SalaAula) equipamento).getNivel() : null;
        RegraPlano regra = membro.getPlano().getRegra(tipo);

        if (!equipamento.isDisponivel(agora)) {
            return ResultadoAcesso.recusado("O equipamento está ocupado.");
        }
        if (membro.isOcupado(agora)) {
            return ResultadoAcesso.recusado("O membro já está a usufruir de um serviço.");
        }
        if (tipo == TipoServico.ENTRADA && membro.isDentroDoGinasio()) {
            return ResultadoAcesso.recusado("O membro já se encontra no ginásio.");
        }
        if (tipo == TipoServico.SAIDA && !membro.isDentroDoGinasio()) {
            return ResultadoAcesso.recusado("O membro ainda não entrou no ginásio.");
        }
        if (equipamento.isEntradaRequerida() && !membro.isDentroDoGinasio()) {
            return ResultadoAcesso.recusado("O membro tem de entrar primeiro no ginásio.");
        }
        if (regra == null) {
            return ResultadoAcesso.recusado("O plano do membro não inclui este serviço.");
        }

        if (equipamento instanceof Duche && regra.getDuracaoExtra() != null
                && ((Duche) equipamento).podeAtivarExtra(membro, agora)) {
            Duration duracaoExtra = regra.getDuracaoExtra();
            try {
                ((Duche) equipamento).ativarExtra(membro, agora, duracaoExtra);
            } catch (IllegalStateException e) {
                return ResultadoAcesso.recusado(e.getMessage());
            }
            membro.acrescentarOcupacao(agora.plus(duracaoExtra));
            return ResultadoAcesso.autorizado("Tempo extra do duche ativado.", duracaoExtra);
        }

        if (!membro.podeUsar(tipo, agora, nivelAula)) {
            return ResultadoAcesso.recusado("O limite de utilização do plano foi atingido.");
        }

        Duration duracao = equipamento.getServico().getDuracao(regra, equipamento);
        if (duracao == null || duracao.isZero() || duracao.isNegative()) {
            return ResultadoAcesso.recusado("Não está definida uma duração válida para este serviço.");
        }

        Duration duracaoOcupacao = equipamento.getDuracaoOcupacao(duracao);
        LocalDateTime fimEquipamento;
        try {
            fimEquipamento = equipamento.iniciarUso(agora, duracaoOcupacao);
        } catch (IllegalStateException e) {
            return ResultadoAcesso.recusado(e.getMessage());
        }
        LocalDateTime fimUtilizacao = agora.plus(duracao);
        if (equipamento instanceof SalaAula) {
            ((SalaAula) equipamento).adicionarParticipante(membro.getId(), agora);
            fimUtilizacao = fimEquipamento;
            duracao = Duration.between(agora, fimUtilizacao);
        }

        RegistoUtilizacao registo = new RegistoUtilizacao(tipo, equipamentoId, equipamento.getNome(),
                agora, duracao, nivelAula);
        membro.registar(registo, fimUtilizacao);
        if (tipo == TipoServico.ENTRADA) {
            membro.entrarNoGinasio();
        } else if (tipo == TipoServico.SAIDA) {
            membro.sairDoGinasio();
        }
        if (equipamento instanceof Duche && regra.getDuracaoExtra() != null
                && regra.getJanelaAtivacaoExtra() != null) {
            ((Duche) equipamento).registarTempoExtra(membro, registo, fimUtilizacao,
                    regra.getJanelaAtivacaoExtra());
        }

        return ResultadoAcesso.autorizado("Acesso autorizado: " + equipamento.getServico().getNome() + ".",
                duracao);
    }

    public synchronized Map<TipoServico, String> getDescricaoServicosContratados(int membroId) {
        Membro membro = membros.get(membroId);
        if (membro == null) {
            return Collections.emptyMap();
        }
        Map<TipoServico, String> descricoes = new LinkedHashMap<>();
        for (Map.Entry<TipoServico, RegraPlano> entrada : membro.getPlano().getServicosContratados().entrySet()) {
            descricoes.put(entrada.getKey(), entrada.getValue().getDescricao());
        }
        return Collections.unmodifiableMap(descricoes);
    }
}
