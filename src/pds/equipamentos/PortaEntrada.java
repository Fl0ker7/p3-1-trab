package pds.equipamentos;

import java.time.Duration;

import pds.servicos.Servico;

public final class PortaEntrada extends Equipamento {
    public PortaEntrada(int id, String nome, Servico servico, Duration duracao) {
        super(id, nome, servico, duracao, false);
    }
}
