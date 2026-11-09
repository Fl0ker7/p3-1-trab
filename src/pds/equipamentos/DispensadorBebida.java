package pds.equipamentos;

import java.time.Duration;

import pds.servicos.Servico;

public final class DispensadorBebida extends Equipamento {
    public DispensadorBebida(int id, String nome, Servico servico, Duration duracao, boolean requerEntrada) {
        super(id, nome, servico, duracao, requerEntrada);
    }
}
