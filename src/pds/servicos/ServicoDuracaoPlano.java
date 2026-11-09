package pds.servicos;

import java.time.Duration;

import pds.dominio.RegraPlano;
import pds.dominio.TipoServico;
import pds.equipamentos.Equipamento;

public final class ServicoDuracaoPlano extends Servico {
    public ServicoDuracaoPlano(TipoServico tipo) {
        super(tipo);
    }

    @Override
    public Duration getDuracao(RegraPlano regra, Equipamento equipamento) {
        return regra.getDuracao();
    }
}
