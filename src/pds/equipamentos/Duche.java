package pds.equipamentos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import pds.dominio.Membro;
import pds.dominio.RegistoUtilizacao;
import pds.dominio.TipoServico;
import pds.servicos.Servico;

public final class Duche extends Equipamento {
    private final Map<Integer, TempoExtra> temposExtra = new HashMap<>();

    public Duche(int id, String nome, Servico servico, boolean requerEntrada) {
        super(id, nome, servico, null, requerEntrada);
        if (servico.getTipo() != TipoServico.DUCHE) {
            throw new IllegalArgumentException("O duche tem de fornecer o serviço Duche.");
        }
    }

    public synchronized boolean podeAtivarExtra(Membro membro, LocalDateTime agora) {
        TempoExtra tempo = temposExtra.get(membro.getId());
        if (tempo == null || tempo.ativado || agora.isAfter(tempo.limiteAtivacao)
                || agora.isBefore(tempo.fimInicial)) {
            return false;
        }
        return membro.getUltimoRegisto(TipoServico.DUCHE) == tempo.registo;
    }

    public synchronized Duration ativarExtra(Membro membro, LocalDateTime agora, Duration duracaoExtra) {
        if (!podeAtivarExtra(membro, agora) || !isDisponivel(agora)) {
            throw new IllegalStateException("O tempo extra do duche já não pode ser ativado.");
        }
        TempoExtra tempo = temposExtra.get(membro.getId());
        tempo.ativado = true;
        tempo.registo.acrescentarDuracao(duracaoExtra);
        iniciarUso(agora, duracaoExtra);
        return duracaoExtra;
    }

    public synchronized void registarTempoExtra(Membro membro, RegistoUtilizacao registo,
            LocalDateTime fimInicial, Duration janelaAtivacao) {
        temposExtra.put(membro.getId(),
                new TempoExtra(registo, fimInicial, fimInicial.plus(janelaAtivacao)));
    }

    private static final class TempoExtra {
        private final RegistoUtilizacao registo;
        private final LocalDateTime fimInicial;
        private final LocalDateTime limiteAtivacao;
        private boolean ativado;

        private TempoExtra(RegistoUtilizacao registo, LocalDateTime fimInicial, LocalDateTime limiteAtivacao) {
            this.registo = registo;
            this.fimInicial = fimInicial;
            this.limiteAtivacao = limiteAtivacao;
        }
    }
}
