package teste.wuzuy;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommandListener extends ListenerAdapter {
    private final Map<Member, BatePonto> pontos = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        Properties prop = new Properties();
        String canalId;
        try {
            FileInputStream input = new FileInputStream("application.properties");
            prop.load(input);
            canalId = prop.getProperty("canal.bateponto.id");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TextChannel canalEspecifico = event.getGuild().getTextChannelById(canalId);

        if (canalEspecifico == null) {
            event.getChannel().sendMessage("Canal de bate-ponto não encontrado!").queue();
            return;
        }

        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase("br!iniciar")) {
            String call = args.length > 1 ? args[1] : "Não informado.";
            Member usuario = event.getMember();

            if (args.length < 2) {
                assert usuario != null;
                event.getChannel().sendMessage(usuario.getAsMention() +
                        " Você não iniciou o bate-ponto corretamente!" +
                        "\nInicie usando o comando: `br!iniciar [NOME DA CALL]`").queue();

            } else if (usuario != null && !pontos.containsKey(usuario)) {
                event.getChannel().sendMessage("Iniciando bate-ponto para " + usuario.getAsMention() + " na call: " + call)
                        .queue();

                canalEspecifico.sendMessage("Nome: " + usuario.getAsMention() + "\nCall: " + call +
                                "\nEntrada: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) +
                                "\nSaída: --" +
                                "\nTempo: --")
                        .queue(mensagem -> {
                            BatePonto batePonto = new BatePonto(usuario, call, mensagem);
                            pontos.put(usuario, batePonto);
                        });
            } else {
                event.getChannel().sendMessage("Você já iniciou um bate-ponto!").queue();
            }
        }

        if (args[0].equalsIgnoreCase("br!parar")) {
            Member usuario = event.getMember();

            if (usuario != null && pontos.containsKey(usuario)) {

                event.getChannel().sendMessage(usuario.getAsMention() + " seu bate ponto foi finalizado!").queue();

                BatePonto batePonto = pontos.remove(usuario);
                batePonto.finalizarPonto();
                // Edita a mensagem inicial com os dados finais (inclusão do tempo de saída)
                batePonto.getMensagem().editMessage(batePonto.toString()).queue();
            } else {
                event.getChannel().sendMessage("Você ainda não iniciou um bate-ponto!").queue();
            }
        }
    }
}
