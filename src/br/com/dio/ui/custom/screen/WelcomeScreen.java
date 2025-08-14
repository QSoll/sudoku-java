package br.com.dio.ui.custom.screen;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen {

    public static String showWelcomeAndGetLevel() {
        JFrame frame = new JFrame("Sudoku - Bem-vindo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Escolha o nível do jogo:");
        String[] levels = {"FACIL", "MEDIO", "DIFICIL"};
        JComboBox<String> levelBox = new JComboBox<>(levels);

        JButton startButton = new JButton("Iniciar Jogo");

        final String[] selectedLevel = {"FACIL"};

        startButton.addActionListener(e -> {
            selectedLevel[0] = (String) levelBox.getSelectedItem();
            frame.dispose();
        });

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(levelBox);
        panel.add(startButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        while (frame.isDisplayable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        return selectedLevel[0];
    }
}
