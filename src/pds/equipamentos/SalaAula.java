package pds.equipamentos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import pds.dominio.TipoServico;
import pds.servicos.Servico;

public final class SalaAula extends Equipamento {
    private final int nivel;
    private final int capacidade;
    private final Set<Integer> participantes = new HashSet<>();
    private LocalDateTime fimAula;

    public SalaAula(int id, String nome, Servico servico, int nivel, int capacidade, Duration duracao,
            boolean requerEntrada) {
        super(id, nome, servico, duracao, requerEntrada);
        if (servico.getTipo() != TipoServico.AULA || nivel < 1 || capacidade < 1) {
            throw new IllegalArgumentException("Nível, capacidade e serviço de aula têm de ser válidos.");
        }
        this.nivel = nivel;
        this.capacidade = capacidade;
    }

    public int getNivel() {
        return nivel;
    }

    public int getCapacidade() {
        return capacidade;
    }

    @Override
    public synchronized boolean isDisponivel(LocalDateTime agora) {
        atualizar(agora);
        return participantes.size() < capacidade;
    }

    @Override
    public synchronized boolean isOcupado(LocalDateTime agora) {
        return !isDisponivel(agora);
    }

    @Override
    public synchronized LocalDateTime iniciarUso(LocalDateTime agora, Duration duracaoUso) {
        atualizar(agora);
        if (participantes.size() >= capacidade) {
            throw new IllegalStateException("A aula está lotada.");
        }
        if (fimAula == null) {
            fimAula = agora.plus(getDuracao());
        }
        return fimAula;
    }

    public synchronized void adicionarParticipante(int membroId, LocalDateTime agora) {
        atualizar(agora);
        if (participantes.size() >= capacidade) {
            throw new IllegalStateException("A aula está lotada.");
        }
        participantes.add(membroId);
    }

    private void atualizar(LocalDateTime agora) {
        if (fimAula != null && !agora.isBefore(fimAula)) {
            participantes.clear();
            fimAula = null;
        }
    }
}
