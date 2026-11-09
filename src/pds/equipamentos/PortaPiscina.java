package pds.equipamentos;

import java.time.Duration;

import pds.servicos.Servico;

public final class PortaPiscina extends Equipamento {
    public PortaPiscina(int id, String nome, Servico servico, Duration duracao, boolean requerEntrada) {
        super(id, nome, servico, duracao, requerEntrada);
    }

    @Override
    public Duration getDuracaoOcupacao(Duration duracaoServico) {
        return getDuracao();
    }
}
