package pds.equipamentos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import pds.servicos.Servico;

public abstract class Equipamento {
    private final int id;
    private final String nome;
    private final Servico servico;
    private final Duration duracao;
    private final boolean requerEntrada;
    private LocalDateTime ocupadoAte;

    protected Equipamento(int id, String nome, Servico servico, Duration duracao, boolean requerEntrada) {
        if (id <= 0) {
            throw new IllegalArgumentException("O identificador do equipamento tem de ser positivo.");
        }
        this.id = id;
        this.nome = Objects.requireNonNull(nome).trim();
        if (this.nome.isEmpty()) {
            throw new IllegalArgumentException("O nome do equipamento não pode estar vazio.");
        }
        this.servico = Objects.requireNonNull(servico);
        this.duracao = duracao;
        if (duracao != null && (duracao.isZero() || duracao.isNegative())) {
            throw new IllegalArgumentException("A duração do equipamento tem de ser positiva.");
        }
        this.requerEntrada = requerEntrada;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Servico getServico() {
        return servico;
    }

    public Duration getDuracao() {
        if (duracao == null) {
            throw new IllegalStateException("Este equipamento não define uma duração fixa.");
        }
        return duracao;
    }

    public Duration getDuracaoOcupacao(Duration duracaoServico) {
        return Objects.requireNonNull(duracaoServico);
    }

    public boolean isEntradaRequerida() {
        return requerEntrada;
    }

    public synchronized boolean isDisponivel(LocalDateTime agora) {
        return ocupadoAte == null || !agora.isBefore(ocupadoAte);
    }

    public synchronized boolean isOcupado(LocalDateTime agora) {
        return !isDisponivel(agora);
    }

    public synchronized LocalDateTime iniciarUso(LocalDateTime agora, Duration duracaoUso) {
        if (!isDisponivel(agora)) {
            throw new IllegalStateException("O equipamento está ocupado.");
        }
        ocupadoAte = agora.plus(Objects.requireNonNull(duracaoUso));
        return ocupadoAte;
    }
}
