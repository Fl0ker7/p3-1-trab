package pds.equipamentos;

import pds.dominio.TipoServico;
import pds.servicos.Servico;

public final class Profissional extends Equipamento {
    public Profissional(int id, String nome, Servico servico) {
        super(id, nome, servico, null, true);
        if (servico.getTipo() != TipoServico.PERSONAL_TRAINER
                && servico.getTipo() != TipoServico.NUTRICAO) {
            throw new IllegalArgumentException("O serviço do profissional tem de ser treino ou nutrição.");
        }
    }
}
