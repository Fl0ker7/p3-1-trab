package pds.dominio;

import java.time.Duration;
import java.util.Objects;

public final class ResultadoAcesso {
    private final boolean autorizado;
    private final String mensagem;
    private final Duration duracao;

    private ResultadoAcesso(boolean autorizado, String mensagem, Duration duracao) {
        this.autorizado = autorizado;
        this.mensagem = Objects.requireNonNull(mensagem);
        this.duracao = duracao;
    }

    public static ResultadoAcesso autorizado(String mensagem, Duration duracao) {
        return new ResultadoAcesso(true, mensagem, Objects.requireNonNull(duracao));
    }

    public static ResultadoAcesso recusado(String mensagem) {
        return new ResultadoAcesso(false, mensagem, null);
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Duration getDuracao() {
        return duracao;
    }
}
