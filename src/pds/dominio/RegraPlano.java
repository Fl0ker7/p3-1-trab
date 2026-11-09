package pds.dominio;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class RegraPlano {
    private Duration duracao;
    private Duration duracaoExtra;
    private Duration janelaAtivacaoExtra;
    private Duration intervaloMinimo;
    private Period intervaloMinimoCalendario;
    private Integer maxUsosPorDia;
    private Integer maxUsosPorSemana;
    private Integer maxDiasPorSemana;
    private final Map<Integer, Integer> maxAulasPorNivelSemana = new HashMap<>();

    public RegraPlano comDuracao(Duration duracao) {
        this.duracao = requirePositive(duracao, "duração");
        return this;
    }

    public RegraPlano comDuracaoExtra(Duration duracaoExtra, Duration janelaAtivacao) {
        this.duracaoExtra = requirePositive(duracaoExtra, "duração extra");
        this.janelaAtivacaoExtra = requirePositive(janelaAtivacao, "janela de ativação");
        return this;
    }

    public RegraPlano comIntervaloMinimo(Duration intervalo) {
        this.intervaloMinimo = requirePositive(intervalo, "intervalo mínimo");
        this.intervaloMinimoCalendario = null;
        return this;
    }

    public RegraPlano comIntervaloMinimo(Period intervalo) {
        Objects.requireNonNull(intervalo, "intervalo mínimo");
        if (intervalo.isZero() || intervalo.isNegative()) {
            throw new IllegalArgumentException("O intervalo mínimo tem de ser positivo.");
        }
        this.intervaloMinimoCalendario = intervalo;
        this.intervaloMinimo = null;
        return this;
    }

    public RegraPlano comMaxUsosPorDia(int maximo) {
        this.maxUsosPorDia = requirePositive(maximo, "máximo de usos por dia");
        return this;
    }

    public RegraPlano comMaxUsosPorSemana(int maximo) {
        this.maxUsosPorSemana = requirePositive(maximo, "máximo de usos por semana");
        return this;
    }

    public RegraPlano comMaxDiasPorSemana(int maximo) {
        this.maxDiasPorSemana = requirePositive(maximo, "máximo de dias por semana");
        return this;
    }

    public RegraPlano comMaxAulasPorNivelSemana(int nivel, int maximo) {
        if (nivel < 1 || maximo < 1) {
            throw new IllegalArgumentException("Nível e número de aulas têm de ser positivos.");
        }
        maxAulasPorNivelSemana.put(nivel, maximo);
        return this;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public Duration getDuracaoExtra() {
        return duracaoExtra;
    }

    public Duration getJanelaAtivacaoExtra() {
        return janelaAtivacaoExtra;
    }

    public Map<Integer, Integer> getMaxAulasPorNivelSemana() {
        return Collections.unmodifiableMap(maxAulasPorNivelSemana);
    }

    public boolean permite(List<RegistoUtilizacao> registos, LocalDateTime agora, Integer nivelAula) {
        Objects.requireNonNull(registos);
        Objects.requireNonNull(agora);

        int usosHoje = 0;
        int usosEstaSemana = 0;
        int usosNivelEstaSemana = 0;
        LocalDate primeiraDataSemana = agora.toLocalDate()
                .with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDate ultimaDataSemana = primeiraDataSemana.plusDays(6);
        java.util.Set<LocalDate> diasUsados = new java.util.HashSet<>();

        for (RegistoUtilizacao registo : registos) {
            LocalDate data = registo.getDataHora().toLocalDate();
            if (data.equals(agora.toLocalDate())) {
                usosHoje++;
            }
            if (!data.isBefore(primeiraDataSemana) && !data.isAfter(ultimaDataSemana)) {
                usosEstaSemana++;
                diasUsados.add(data);
                if (nivelAula != null && nivelAula.equals(registo.getNivelAula())) {
                    usosNivelEstaSemana++;
                }
            }
            if (intervaloMinimo != null && registo.getDataHora().plus(intervaloMinimo).isAfter(agora)) {
                return false;
            }
            if (intervaloMinimoCalendario != null
                    && registo.getDataHora().plus(intervaloMinimoCalendario).isAfter(agora)) {
                return false;
            }
        }

        if (maxUsosPorDia != null && usosHoje >= maxUsosPorDia) {
            return false;
        }
        if (maxUsosPorSemana != null && usosEstaSemana >= maxUsosPorSemana) {
            return false;
        }
        if (maxDiasPorSemana != null && !diasUsados.contains(agora.toLocalDate())
                && diasUsados.size() >= maxDiasPorSemana) {
            return false;
        }
        if (nivelAula != null && maxAulasPorNivelSemana.containsKey(nivelAula)
                && usosNivelEstaSemana >= maxAulasPorNivelSemana.get(nivelAula)) {
            return false;
        }
        return nivelAula == null || !maxAulasPorNivelSemana.isEmpty()
                && maxAulasPorNivelSemana.containsKey(nivelAula);
    }

    public String getDescricao() {
        StringBuilder descricao = new StringBuilder();
        if (duracao != null) {
            descricao.append(formatarDuracao(duracao));
        }
        if (duracaoExtra != null) {
            acrescentar(descricao, formatarDuracao(duracaoExtra) + " extra");
        }
        if (maxUsosPorDia != null) {
            acrescentar(descricao, maxUsosPorDia + (maxUsosPorDia == 1 ? " uso/dia" : " usos/dia"));
        }
        if (maxUsosPorSemana != null) {
            acrescentar(descricao, maxUsosPorSemana + " usos/semana");
        }
        if (maxDiasPorSemana != null) {
            acrescentar(descricao, maxDiasPorSemana + " dias/semana");
        }
        if (intervaloMinimo != null) {
            acrescentar(descricao, "intervalo mínimo " + formatarDuracao(intervaloMinimo));
        }
        if (intervaloMinimoCalendario != null) {
            acrescentar(descricao, "intervalo mínimo " + formatarPeriodo(intervaloMinimoCalendario));
        }
        for (Map.Entry<Integer, Integer> aula : maxAulasPorNivelSemana.entrySet()) {
            String maximo = aula.getValue() == Integer.MAX_VALUE ? "Ilimitado" : aula.getValue().toString();
            acrescentar(descricao, maximo + " aulas nível " + aula.getKey() + "/semana");
        }
        return descricao.length() == 0 ? "Ilimitado" : descricao.toString();
    }

    private static Duration requirePositive(Duration value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(field + " tem de ser positiva.");
        }
        return value;
    }

    private static int requirePositive(int value, String field) {
        if (value < 1) {
            throw new IllegalArgumentException(field + " tem de ser positivo.");
        }
        return value;
    }

    private static void acrescentar(StringBuilder descricao, String texto) {
        if (descricao.length() > 0) {
            descricao.append(", ");
        }
        descricao.append(texto);
    }

    private static String formatarDuracao(Duration duracao) {
        long segundos = duracao.getSeconds();
        if (segundos % 3600 == 0) {
            return (segundos / 3600) + " h";
        }
        if (segundos % 60 == 0) {
            return (segundos / 60) + " min";
        }
        return segundos + " s";
    }

    private static String formatarPeriodo(Period periodo) {
        if (periodo.getMonths() != 0 && periodo.getDays() == 0) {
            return periodo.getMonths() + (periodo.getMonths() == 1 ? " mês" : " meses");
        }
        if (periodo.getDays() != 0 && periodo.getMonths() == 0) {
            return periodo.getDays() + (periodo.getDays() == 1 ? " dia" : " dias");
        }
        return periodo.getYears() + " anos, " + periodo.getMonths() + " meses e " + periodo.getDays() + " dias";
    }
}
