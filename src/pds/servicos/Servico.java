package pds.servicos;

import java.time.Duration;
import java.util.Objects;

import pds.dominio.RegraPlano;
import pds.dominio.TipoServico;
import pds.equipamentos.Equipamento;

public abstract class Servico {
    private final TipoServico tipo;

    protected Servico(TipoServico tipo) {
        this.tipo = Objects.requireNonNull(tipo);
    }

    public TipoServico getTipo() {
        return tipo;
    }

    public String getNome() {
        return tipo.getNome();
    }

    public abstract Duration getDuracao(RegraPlano regra, Equipamento equipamento);
}
