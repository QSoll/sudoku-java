package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.stream.Collectors;

import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import java.util.stream.Stream;

import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private static Board board;
    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) throws IOException {
        // Escolha de dificuldade
        System.out.println("Escolha o nível de dificuldade:");
        System.out.println("1 - Fácil");
        System.out.println("2 - Médio");
        System.out.println("3 - Difícil");
        int escolha = scanner.nextInt();

        String nivel = switch (escolha) {
            case 1 -> "FACIL";
            case 2 -> "MEDIO";
            case 3 -> "DIFICIL";
            default -> "MEDIO";
        };

        // Leitura do tabuleiro aleatório
        Path caminho = Paths.get("src/br/com/dio/tabuleiros.txt");

        List<String> linhas = Files.readAllLines(caminho);
        List<String> filtrados = linhas.stream()
                .filter(l -> l.startsWith(nivel + ":"))
                .toList();

        String linha = filtrados.get(new Random().nextInt(filtrados.size()));
        String[] argumentos = linha.replace(nivel + ": ", "").split(" ");

        // Transformar argumentos em mapa de posições
        final var positions = Stream.of(argumentos)
                .collect(Collectors.toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]));

        // Menu interativo
        var option = -1;
        while (true) {
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O jogo já foi iniciado");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var key = "%s,%s".formatted(i, j);
                var positionConfig = positions.get(key);

                if (positionConfig == null) {
                    positionConfig = "0,false";
                }

                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo está pronto para começar");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado iniciado");
            return;
        }

        System.out.println("Informe a coluna que em que o número será inserido");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha que em que o número será inserido");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%s,%s]\n", col, row);
        var value = runUntilGetValidNumber(1, 9);
        if (!board.changeValue(col, row, value)) {
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a coluna da posição que deseja apagar");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha da posição que deseja apagar");
        var row = runUntilGetValidNumber(0, 8);

        var atual = board.getSpaces().get(col).get(row);

        if (atual.isFixed()) {
            System.out.printf("A posição [%s,%s] tem um valor fixo e não pode ser apagada.\n", col, row);
            return;
        }

        if (atual.getActual() == null) {
            System.out.printf("A posição [%s,%s] já está vazia.\n", col, row);
            return;
        }

        board.clearValue(col, row);
        System.out.printf("Número removido da posição [%s,%s].\n", col, row);
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado iniciado");
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col : board.getSpaces()) {
                args[argPos++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado iniciado");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status %s\n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("O jogo contém erros");
        } else {
            System.out.println("O jogo não contém erros");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado iniciado");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado iniciado");
            return;
        }

        if (board.gameIsFinished()) {
            System.out.println("Parabéns você concluiu o jogo");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo conté, erros, verifique seu board e ajuste-o");
        } else {
            System.out.println("Você ainda precisa preenhcer algum espaço");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        var current = scanner.nextInt();
        while (current < min || current > max) {
            System.out.printf("Informe um número entre %s e %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }

}
