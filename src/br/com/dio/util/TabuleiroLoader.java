package br.com.dio.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TabuleiroLoader {

    public static Map<String, String> carregarTabuleiro(String nivel) {
        try {
            List<String> linhas = Files.readAllLines(Paths.get("src/br/com/dio/tabuleiros.txt"));
            List<String> filtrados = linhas.stream()
                    .filter(l -> l.startsWith(nivel + ":"))
                    .toList();

            String linha = filtrados.get(new Random().nextInt(filtrados.size()));
            String[] argumentos = linha.replace(nivel + ": ", "").split(" ");

            return Arrays.stream(argumentos)
                    .collect(Collectors.toMap(
                            k -> {
                                String[] parts = k.split(";");
                                int row = Integer.parseInt(parts[0].split(",")[0]);
                                int col = Integer.parseInt(parts[0].split(",")[1]);
                                char rowChar = (char) ('A' + row);
                                return "" + rowChar + (col + 1); // Ex: "A1"
                            },
                            v -> v.split(";")[1] // Ex: "5,true"
                    ));
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of();
        }
    }
}
