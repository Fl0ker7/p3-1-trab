package pds.dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Membro {
    private final int id;
    private final String nome;
    private final Plano plano;
    private final List<RegistoUtilizacao> registos = new ArrayList<>();
    private boolean dentroDoGinasio;
    private LocalDateTime ocupadoAte;

    public Membro(int id, String nome, Plano plano) {
        if (id <= 0) {
            throw new IllegalArgumentException("O identificador do membro tem de ser positivo.");
        }
        this.id = id;
        this.nome = Objects.requireNonNull(nome).trim();
        if (this.nome.isEmpty()) {
            throw new IllegalArgumentException("O nome do membro não pode estar vazio.");
        }
        this.plano = Objects.requireNonNull(plano);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Plano getPlano() {
        return plano;
    }

    public synchronized boolean isDentroDoGinasio() {
        return dentroDoGinasio;
    }

    public synchronized boolean isOcupado(LocalDateTime agora) {
        return ocupadoAte != null && agora.isBefore(ocupadoAte);
    }

    public synchronized List<RegistoUtilizacao> getRegistos() {
        return Collections.unmodifiableList(new ArrayList<>(registos));
    }

    public synchronized boolean podeUsar(TipoServico servico, LocalDateTime agora, Integer nivelAula) {
        RegraPlano regra = plano.getRegra(servico);
        return regra != null && regra.permite(registosDoServico(servico), agora, nivelAula);
    }

    public synchronized RegistoUtilizacao getUltimoRegisto(TipoServico servico) {
        for (int i = registos.size() - 1; i >= 0; i--) {
            if (registos.get(i).getTipoServico() == servico) {
                return registos.get(i);
            }
        }
        return null;
    }

    public synchronized void registar(RegistoUtilizacao registo, LocalDateTime ocupadoAte) {
        registos.add(Objects.requireNonNull(registo));
        this.ocupadoAte = Objects.requireNonNull(ocupadoAte);
    }

    public synchronized void acrescentarOcupacao(LocalDateTime ocupadoAte) {
        this.ocupadoAte = Objects.requireNonNull(ocupadoAte);
    }

    public synchronized void entrarNoGinasio() {
        dentroDoGinasio = true;
    }

    public synchronized void sairDoGinasio() {
        dentroDoGinasio = false;
    }

    private List<RegistoUtilizacao> registosDoServico(TipoServico servico) {
        List<RegistoUtilizacao> resultado = new ArrayList<>();
        for (RegistoUtilizacao registo : registos) {
            if (registo.getTipoServico() == servico) {
                resultado.add(registo);
            }
        }
        return resultado;
    }
}
