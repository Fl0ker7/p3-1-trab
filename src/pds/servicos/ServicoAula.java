package pds.servicos;

import java.time.Duration;

import pds.dominio.RegraPlano;
import pds.dominio.TipoServico;
import pds.equipamentos.Equipamento;
import pds.equipamentos.SalaAula;

public final class ServicoAula extends Servico {
    public ServicoAula() {
        super(TipoServico.AULA);
    }

    @Override
    public Duration getDuracao(RegraPlano regra, Equipamento equipamento) {
        if (!(equipamento instanceof SalaAula)) {
            throw new IllegalArgumentException("O serviço de aula requer uma sala de aula.");
        }
        return equipamento.getDuracao();
    }
}
