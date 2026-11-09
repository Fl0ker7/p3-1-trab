package pds.equipamentos;

import java.time.Duration;

import pds.dominio.TipoServico;
import pds.servicos.Servico;

public final class DispositivoServico extends Equipamento {
    public DispositivoServico(int id, String nome, Servico servico, Duration duracao, boolean requerEntrada) {
        super(id, nome, servico, duracao, requerEntrada);
        TipoServico tipo = servico.getTipo();
        if (tipo != TipoServico.AVALIACAO_FISICA && tipo != TipoServico.MASSAGEM) {
            throw new IllegalArgumentException("Tipo de serviço não suportado pelo dispositivo.");
        }
    }
}
