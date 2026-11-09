package pds.dominio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class RegistoUtilizacao {
    private final TipoServico tipoServico;
    private final int equipamentoId;
    private final String nomeEquipamento;
    private final LocalDateTime dataHora;
    private final Integer nivelAula;
    private Duration duracao;

    public RegistoUtilizacao(TipoServico tipoServico, int equipamentoId, String nomeEquipamento,
            LocalDateTime dataHora, Duration duracao, Integer nivelAula) {
        this.tipoServico = Objects.requireNonNull(tipoServico);
        this.equipamentoId = equipamentoId;
        this.nomeEquipamento = Objects.requireNonNull(nomeEquipamento);
        this.dataHora = Objects.requireNonNull(dataHora);
        this.duracao = Objects.requireNonNull(duracao);
        this.nivelAula = nivelAula;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public int getEquipamentoId() {
        return equipamentoId;
    }

    public String getNomeEquipamento() {
        return nomeEquipamento;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public Integer getNivelAula() {
        return nivelAula;
    }

    public void acrescentarDuracao(Duration duracao) {
        this.duracao = this.duracao.plus(Objects.requireNonNull(duracao));
    }
}
