package pds.servicos;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import pds.dominio.TipoServico;

public final class CatalogoServicos {
    private final Map<TipoServico, Servico> servicos = new EnumMap<>(TipoServico.class);

    public CatalogoServicos() {
        registar(new ServicoDuracaoEquipamento(TipoServico.ENTRADA));
        registar(new ServicoDuracaoEquipamento(TipoServico.SAIDA));
        registar(new ServicoAula());
        registar(new ServicoDuracaoPlano(TipoServico.PISCINA));
        registar(new ServicoDuracaoPlano(TipoServico.DUCHE));
        registar(new ServicoDuracaoEquipamento(TipoServico.BEBIDA));
        registar(new ServicoDuracaoPlano(TipoServico.PERSONAL_TRAINER));
        registar(new ServicoDuracaoEquipamento(TipoServico.NUTRICAO));
        registar(new ServicoDuracaoEquipamento(TipoServico.AVALIACAO_FISICA));
        registar(new ServicoDuracaoEquipamento(TipoServico.MASSAGEM));
    }

    public Servico get(TipoServico tipo) {
        Servico servico = servicos.get(tipo);
        if (servico == null) {
            throw new IllegalArgumentException("Serviço não registado: " + tipo);
        }
        return servico;
    }

    public Map<TipoServico, Servico> getServicos() {
        return Collections.unmodifiableMap(servicos);
    }

    private void registar(Servico servico) {
        servicos.put(servico.getTipo(), servico);
    }
}
