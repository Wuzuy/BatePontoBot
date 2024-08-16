package teste.wuzuy;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

public class Main {

    private static final String token;

    static {
        Properties prop = new Properties();
        File file = new File("application.properties");

        // Verifica se o arquivo existe, caso contrário, cria um com valores padrão
        if (!file.exists()) {
            try (FileOutputStream output = new FileOutputStream(file)) {
                // Define valores padrão
                prop.setProperty("token", "default_token_here");
                prop.setProperty("canal.bateponto.id", "ID_channel_here");
                prop.store(output, "Default properties file");
                System.out.println("Arquivo de propriedades criado com valores padrão.");
            } catch (IOException e) {
                throw new RuntimeException("Failed to create properties file", e);
            }
        }

        try (FileInputStream input = new FileInputStream(file)) {
            prop.load(input);
            token = prop.getProperty("token");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault(token)
                .addEventListeners(new CommandListener())
                .enableIntents(EnumSet.allOf(GatewayIntent.class));
        builder.build();
    }
}
