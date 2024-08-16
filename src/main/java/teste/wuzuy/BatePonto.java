package teste.wuzuy;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class BatePonto {
    private final Member usuario;
    private final String call;
    private final LocalDateTime entrada;
    private LocalDateTime saida;
    private final Message mensagem;

    public BatePonto(Member usuario, String call, Message mensagem) {
        this.usuario = usuario;
        this.call = call;
        this.entrada = LocalDateTime.now();
        this.mensagem = mensagem;
    }

    public void finalizarPonto() {
        this.saida = LocalDateTime.now();
    }

    public Duration calcularTempoTotal() {
        if (saida != null) {
            return Duration.between(entrada, saida);
        }
        return Duration.ZERO;
    }

    @Override
    public String toString() {
        Duration tempoTotal = calcularTempoTotal();
        return "Call: " + call + "\n" +
                "Nome: " + usuario.getAsMention() + "\n" +
                "Entrada: " + entrada.format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                "Saída: " + (saida != null ? saida.format(DateTimeFormatter.ofPattern("HH:mm")) : "Em andamento") + "\n" +
                "Tempo: " + tempoTotal.toHoursPart() + "h " + tempoTotal.toMinutesPart() + "min";
    }
    public Message getMensagem() {
        return mensagem;
    }
}
