package pds.ginasio;

import java.time.Duration;
import java.time.Period;

import pds.dominio.Membro;
import pds.dominio.Plano;
import pds.dominio.RegraPlano;
import pds.dominio.TipoServico;
import pds.equipamentos.DispensadorBebida;
import pds.equipamentos.DispositivoServico;
import pds.equipamentos.Duche;
import pds.equipamentos.PortaEntrada;
import pds.equipamentos.PortaPiscina;
import pds.equipamentos.PortaSaida;
import pds.equipamentos.Profissional;
import pds.equipamentos.SalaAula;
import pds.servicos.CatalogoServicos;

public final class GinasioExemplo {
    private GinasioExemplo() {
    }

    public static Ginasio criar() {
        Ginasio ginasio = new Ginasio("ESTGym");
        CatalogoServicos servicos = ginasio.getCatalogoServicos();

        adicionarEquipamentos(ginasio, servicos);
        adicionarMembros(ginasio);
        return ginasio;
    }

    private static void adicionarEquipamentos(Ginasio ginasio, CatalogoServicos servicos) {
        ginasio.adicionarEquipamento(new PortaEntrada(10, "Porta de entrada",
                servicos.get(TipoServico.ENTRADA), Duration.ofSeconds(3)));
        ginasio.adicionarEquipamento(new PortaSaida(11, "Porta de saída",
                servicos.get(TipoServico.SAIDA), Duration.ofSeconds(3)));

        ginasio.adicionarEquipamento(new SalaAula(12, "Aula Step", servicos.get(TipoServico.AULA),
                1, 30, Duration.ofMinutes(30), true));
        ginasio.adicionarEquipamento(new SalaAula(13, "Aula Cycling", servicos.get(TipoServico.AULA),
                1, 25, Duration.ofMinutes(50), true));
        ginasio.adicionarEquipamento(new SalaAula(14, "Aula Body combat", servicos.get(TipoServico.AULA),
                2, 20, Duration.ofMinutes(30), true));
        ginasio.adicionarEquipamento(new SalaAula(15, "Aula Ioga", servicos.get(TipoServico.AULA),
                2, 15, Duration.ofMinutes(60), true));

        ginasio.adicionarEquipamento(new DispensadorBebida(16, "Dispensador bebida",
                servicos.get(TipoServico.BEBIDA), Duration.ofSeconds(20), true));
        for (int id = 17; id <= 19; id++) {
            ginasio.adicionarEquipamento(new Duche(id, "Duche " + (id - 16),
                    servicos.get(TipoServico.DUCHE), true));
        }

        ginasio.adicionarEquipamento(new Profissional(20, "Manel Mandão",
                servicos.get(TipoServico.PERSONAL_TRAINER)));
        ginasio.adicionarEquipamento(new Profissional(21, "Rita Rígida",
                servicos.get(TipoServico.PERSONAL_TRAINER)));
        ginasio.adicionarEquipamento(new Profissional(22, "Carlos Comilão",
                servicos.get(TipoServico.NUTRICAO)));
        ginasio.adicionarEquipamento(new Profissional(23, "Marta Magra",
                servicos.get(TipoServico.NUTRICAO)));

        ginasio.adicionarEquipamento(new DispositivoServico(24, "Sala Avaliação",
                servicos.get(TipoServico.AVALIACAO_FISICA), Duration.ofMinutes(20), true));
        ginasio.adicionarEquipamento(new DispositivoServico(25, "Cadeira de Massagem 1",
                servicos.get(TipoServico.MASSAGEM), Duration.ofMinutes(10), true));
        ginasio.adicionarEquipamento(new DispositivoServico(26, "Cadeira de Massagem 2",
                servicos.get(TipoServico.MASSAGEM), Duration.ofMinutes(10), true));
        ginasio.adicionarEquipamento(new PortaPiscina(27, "Entrada Piscina",
                servicos.get(TipoServico.PISCINA), Duration.ofSeconds(3), true));
    }

    private static void adicionarMembros(Ginasio ginasio) {
        ginasio.adicionarMembro(new Membro(1, "Vítor Virtual", planoVitor()));
        ginasio.adicionarMembro(new Membro(2, "Iris Irreal", planoIris()));
        ginasio.adicionarMembro(new Membro(3, "Inês Inexistente", planoInes()));
        ginasio.adicionarMembro(new Membro(4, "Filipe Fictício", planoFilipe()));
        ginasio.adicionarMembro(new Membro(5, "Diogo Digital", planoDiogo()));
        ginasio.adicionarMembro(new Membro(6, "Beatriz Bytes", planoBeatriz()));
    }

    private static Plano planoVitor() {
        return planoComSaida("Plano Vítor")
                .contratar(TipoServico.ENTRADA, regra(Duration.ofHours(1)).comMaxDiasPorSemana(3))
                .contratar(TipoServico.AULA, aulas(1, 1))
                .contratar(TipoServico.DUCHE, duche(3, 2, 1))
                .contratar(TipoServico.BEBIDA, regra(null).comMaxUsosPorDia(1))
                .contratar(TipoServico.NUTRICAO, consulta(6))
                .contratar(TipoServico.AVALIACAO_FISICA, consulta(3));
    }

    private static Plano planoIris() {
        return planoComSaida("Plano Iris")
                .contratar(TipoServico.ENTRADA, regra(null).comMaxUsosPorSemana(3))
                .contratar(TipoServico.PISCINA, regra(Duration.ofMinutes(45)).comMaxDiasPorSemana(2))
                .contratar(TipoServico.DUCHE, duche(5, 2, 1))
                .contratar(TipoServico.BEBIDA, regra(null).comMaxUsosPorDia(2)
                        .comIntervaloMinimo(Duration.ofMinutes(30)))
                .contratar(TipoServico.PERSONAL_TRAINER,
                        regra(Duration.ofMinutes(45)).comMaxDiasPorSemana(3))
                .contratar(TipoServico.NUTRICAO, consulta(6))
                .contratar(TipoServico.AVALIACAO_FISICA, consulta(3));
    }

    private static Plano planoInes() {
        return planoComSaida("Plano Inês")
                .contratar(TipoServico.ENTRADA, regra(null))
                .contratar(TipoServico.AULA, aulas(1, 1))
                .contratar(TipoServico.DUCHE, duche(5, 2, 1))
                .contratar(TipoServico.BEBIDA, regra(null).comMaxUsosPorDia(2)
                        .comIntervaloMinimo(Duration.ofMinutes(30)))
                .contratar(TipoServico.NUTRICAO, consulta(1))
                .contratar(TipoServico.AVALIACAO_FISICA, consulta(3));
    }

    private static Plano planoFilipe() {
        return planoComSaida("Plano Filipe")
                .contratar(TipoServico.ENTRADA, regra(null).comMaxUsosPorSemana(3))
                .contratar(TipoServico.AULA, aulas(2, 1))
                .contratar(TipoServico.PISCINA, regra(Duration.ofMinutes(45)).comMaxDiasPorSemana(2))
                .contratar(TipoServico.DUCHE, duche(5, 2, 1))
                .contratar(TipoServico.BEBIDA, regra(null).comMaxUsosPorDia(2)
                        .comIntervaloMinimo(Duration.ofMinutes(30)))
                .contratar(TipoServico.PERSONAL_TRAINER,
                        regra(Duration.ofMinutes(45)).comMaxDiasPorSemana(3))
                .contratar(TipoServico.NUTRICAO, consulta(6))
                .contratar(TipoServico.AVALIACAO_FISICA, consulta(3))
                .contratar(TipoServico.MASSAGEM, regra(null).comMaxUsosPorDia(1)
                        .comMaxDiasPorSemana(3));
    }

    private static Plano planoDiogo() {
        return planoComSaida("Plano Diogo")
                .contratar(TipoServico.ENTRADA, regra(null))
                .contratar(TipoServico.AULA, aulas(Integer.MAX_VALUE, 3))
                .contratar(TipoServico.PISCINA, regra(Duration.ofHours(1)).comMaxDiasPorSemana(5))
                .contratar(TipoServico.DUCHE, duche(6, 3, 2))
                .contratar(TipoServico.BEBIDA, regra(null).comMaxUsosPorDia(4)
                        .comIntervaloMinimo(Duration.ofMinutes(20)))
                .contratar(TipoServico.NUTRICAO, consulta(3))
                .contratar(TipoServico.AVALIACAO_FISICA, consulta(3))
                .contratar(TipoServico.MASSAGEM, regra(null).comMaxUsosPorDia(1)
                        .comMaxDiasPorSemana(7));
    }

    private static Plano planoBeatriz() {
        return planoComSaida("Plano Beatriz")
                .contratar(TipoServico.ENTRADA, regra(null))
                .contratar(TipoServico.AULA, aulas(Integer.MAX_VALUE, Integer.MAX_VALUE))
                .contratar(TipoServico.DUCHE, duche(6, 3, 2))
                .contratar(TipoServico.BEBIDA, regra(null).comMaxUsosPorDia(4)
                        .comIntervaloMinimo(Duration.ofMinutes(20)))
                .contratar(TipoServico.PERSONAL_TRAINER,
                        regra(Duration.ofHours(1)).comMaxDiasPorSemana(5))
                .contratar(TipoServico.NUTRICAO, consulta(1))
                .contratar(TipoServico.AVALIACAO_FISICA, consulta(1))
                .contratar(TipoServico.MASSAGEM, regra(null).comMaxUsosPorDia(1)
                        .comMaxDiasPorSemana(3));
    }

    private static Plano planoComSaida(String nome) {
        return new Plano(nome).contratar(TipoServico.SAIDA, regra(null));
    }

    private static RegraPlano regra(Duration duracao) {
        RegraPlano regra = new RegraPlano();
        return duracao == null ? regra : regra.comDuracao(duracao);
    }

    private static RegraPlano duche(int minutos, int extra, int usosDiarios) {
        return regra(Duration.ofMinutes(minutos))
                .comDuracaoExtra(Duration.ofMinutes(extra), Duration.ofMinutes(minutos))
                .comMaxUsosPorDia(usosDiarios);
    }

    private static RegraPlano consulta(int meses) {
        return regra(null).comIntervaloMinimo(Period.ofMonths(meses));
    }

    private static RegraPlano aulas(int nivelUm, int nivelDois) {
        return regra(null)
                .comMaxAulasPorNivelSemana(1, nivelUm)
                .comMaxAulasPorNivelSemana(2, nivelDois);
    }
}
