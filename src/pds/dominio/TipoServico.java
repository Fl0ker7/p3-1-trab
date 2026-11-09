package pds.dominio;

public enum TipoServico {
    ENTRADA("Entrada"),
    SAIDA("Saída"),
    AULA("Aula"),
    PISCINA("Piscina"),
    DUCHE("Duche"),
    BEBIDA("Bebida vitaminada"),
    PERSONAL_TRAINER("Personal Trainer"),
    NUTRICAO("Consulta nutrição"),
    AVALIACAO_FISICA("Avaliação física"),
    MASSAGEM("Cadeira de Massagem");

    private final String nome;

    TipoServico(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
