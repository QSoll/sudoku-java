package br.com.dio;

import br.com.dio.ui.custom.screen.MainScreen;
import br.com.dio.ui.custom.screen.WelcomeScreen;
import br.com.dio.util.TabuleiroLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class UIMain {

    public static void main(String[] args) {

            System.out.println("Gerador iniciado...");

        Map<String, String> gameConfig;

        if (args.length > 0) {
            // 🧪 MODO DESENVOLVEDOR (via terminal)
            // Permite passar argumentos como: A1;5,true B2;3,true C3;7,true
            // Útil para testes automatizados ou execução rápida sem interface
            gameConfig = Stream.of(args)
                    .filter(arg -> arg.contains(";"))
                    .collect(toMap(
                            k -> k.split(";")[0].trim(),
                            v -> v.split(";")[1].trim()));
        } else {
            // 🎮 MODO JOGADOR (via interface gráfica)
            // Exibe tela de boas-vindas para escolher o nível
            // Carrega tabuleiro aleatório com base na escolha
            String nivel = WelcomeScreen.showWelcomeAndGetLevel();
            Map<String, String> positions = TabuleiroLoader.carregarTabuleiro(nivel);

            gameConfig = new HashMap<>();
            gameConfig.put("modo", nivel.toLowerCase());
            gameConfig.put("tema", "claro");
            gameConfig.put("positionConfig", String.join(";", positions.keySet()));
            gameConfig.putAll(positions); // inclui os valores reais
        }

        // 🖨️ Exibe a configuração carregada no terminal
        System.out.println("Configuração do jogo:");
        gameConfig.forEach((k, v) -> System.out.println(k + " → " + v));

        // Inicializa a tela principal do jogo com a configuração escolhida
        MainScreen mainScreen = new MainScreen(gameConfig);
        mainScreen.buildMainScreen();
    }
}
