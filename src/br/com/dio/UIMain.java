package br.com.dio;

import br.com.dio.ui.custom.screen.MainScreen;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class UIMain {

    public static void main(String[] args) {
        // Verifica se há argumentos e os converte em um mapa de configuração
        Map<String, String> gameConfig = (args.length > 0)
                ? Stream.of(args)
                        .filter(arg -> arg.contains(";"))
                        .collect(toMap(
                                k -> k.split(";")[0].trim(),
                                v -> v.split(";")[1].trim()
                        ))
                : getDefaultConfig(); // Usa configuração padrão se nenhum argumento for passado

        // Cria e exibe a tela principal
        MainScreen mainScreen = new MainScreen(gameConfig);
        mainScreen.buildMainScreen();
    }

    // Configuração padrão caso nenhum argumento seja fornecido
    private static Map<String, String> getDefaultConfig() {
        return Map.of(
                "modo", "normal",
                "tema", "claro",
                "positionConfig", "A1;B2;C3" // Exemplo de posições iniciais válidas
        );
    }
}
