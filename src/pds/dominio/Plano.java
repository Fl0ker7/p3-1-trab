package pds.dominio;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Plano {
    private final String nome;
    private final Map<TipoServico, RegraPlano> regras = new EnumMap<>(TipoServico.class);

    public Plano(String nome) {
        this.nome = Objects.requireNonNull(nome).trim();
        if (this.nome.isEmpty()) {
            throw new IllegalArgumentException("O nome do plano não pode estar vazio.");
        }
    }

    public Plano contratar(TipoServico servico, RegraPlano regra) {
        regras.put(Objects.requireNonNull(servico), Objects.requireNonNull(regra));
        return this;
    }

    public String getNome() {
        return nome;
    }

    public RegraPlano getRegra(TipoServico servico) {
        return regras.get(servico);
    }

    public Map<TipoServico, RegraPlano> getServicosContratados() {
        return Collections.unmodifiableMap(regras);
    }
}
