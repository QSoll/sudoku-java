package br.com.dio;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GeradorSolucaoSudoku {
    private static final int TAMANHO = 9;
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {
        FileWriter writer = new FileWriter("tabuleiros.txt");

        gerarNivel(writer, "FACIL", 35);
        gerarNivel(writer, "MEDIO", 30);
        gerarNivel(writer, "DIFICIL", 24);

        writer.close();
        System.out.println("Tabuleiros gerados com sucesso!");
    }

    private static void gerarNivel(FileWriter writer, String nivel, int fixos) throws IOException {
        for (int i = 0; i < 20; i++) {
            int[][] solucao = gerarTabuleiroResolvido();
            boolean[][] fixosMap = gerarFixosAleatorios(fixos);
            salvarTabuleiro(writer, nivel, solucao, fixosMap);
        }
    }

    private static int[][] gerarTabuleiroResolvido() {
        int[][] tabuleiro = new int[TAMANHO][TAMANHO];
        preencher(tabuleiro, 0, 0);
        return tabuleiro;
    }

    private static boolean preencher(int[][] tabuleiro, int linha, int coluna) {
        if (linha == TAMANHO)
            return true;
        if (coluna == TAMANHO)
            return preencher(tabuleiro, linha + 1, 0);

        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= TAMANHO; i++)
            numeros.add(i);
        Collections.shuffle(numeros);

        for (int num : numeros) {
            if (podeColocar(tabuleiro, linha, coluna, num)) {
                tabuleiro[linha][coluna] = num;
                if (preencher(tabuleiro, linha, coluna + 1))
                    return true;
                tabuleiro[linha][coluna] = 0;
            }
        }
        return false;
    }

    private static boolean podeColocar(int[][] tabuleiro, int linha, int coluna, int num) {
        for (int i = 0; i < TAMANHO; i++) {
            if (tabuleiro[linha][i] == num || tabuleiro[i][coluna] == num)
                return false;
        }

        int blocoLinha = (linha / 3) * 3;
        int blocoColuna = (coluna / 3) * 3;
        for (int i = blocoLinha; i < blocoLinha + 3; i++) {
            for (int j = blocoColuna; j < blocoColuna + 3; j++) {
                if (tabuleiro[i][j] == num)
                    return false;
            }
        }

        return true;
    }

    private static boolean[][] gerarFixosAleatorios(int quantidade) {
        boolean[][] fixos = new boolean[TAMANHO][TAMANHO];
        int count = 0;

        while (count < quantidade) {
            int linha = random.nextInt(TAMANHO);
            int coluna = random.nextInt(TAMANHO);
            if (!fixos[linha][coluna]) {
                fixos[linha][coluna] = true;
                count++;
            }
        }

        return fixos;
    }

    private static void salvarTabuleiro(FileWriter writer, String nivel, int[][] tabuleiro, boolean[][] fixos)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(nivel).append(": ");

        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (fixos[i][j]) {
                    sb.append(i).append(",").append(j).append(";")
                            .append(tabuleiro[i][j]).append(",true ");

                }
            }
        }

        writer.write(sb.toString().trim() + "\n");
    }
}
