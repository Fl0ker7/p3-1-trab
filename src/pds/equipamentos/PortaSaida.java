package pds.equipamentos;

import java.time.Duration;

import pds.servicos.Servico;

public final class PortaSaida extends Equipamento {
    public PortaSaida(int id, String nome, Servico servico, Duration duracao) {
        super(id, nome, servico, duracao, true);
    }
}
