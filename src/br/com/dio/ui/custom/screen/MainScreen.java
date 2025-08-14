package br.com.dio.ui.custom.screen;

import br.com.dio.model.Space;
import br.com.dio.service.BoardService;
import br.com.dio.service.EventEnum;
import br.com.dio.service.NotifierService;
import br.com.dio.ui.custom.button.CheckGameStatusButton;
import br.com.dio.ui.custom.button.FinishGameButton;
import br.com.dio.ui.custom.button.ResetButton;
import br.com.dio.ui.custom.frame.MainFrame;
import br.com.dio.ui.custom.input.NumberText;
import br.com.dio.ui.custom.panel.SudokuSector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.swing.JOptionPane.*;

public class MainScreen {

  

    private final int timeLimitSeconds = -1; // ilimitado
    private final static Dimension dimension = new Dimension(600, 600);

    private final BoardService boardService;
    private final NotifierService notifierService;

    private JButton checkGameStatusButton;
    private JButton finishGameButton;
    private JButton resetButton;
    //private JButton hintButton;
    private JButton pauseButton;
    private JButton undoButton;

    private JLabel timerLabel;
    private Timer gameTimer;
    private int elapsedSeconds = 0;
    private boolean isPaused = false;
    

    private long startTimeMillis;

    public MainScreen(final Map<String, String> gameConfig) {

        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen() {

        

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        /*
         * // ⏱️ Cronômetro regressivo no topo
         * timerLabel = new JLabel("Tempo restante: 05:00");
         * timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
         * timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
         * mainPanel.add(timerLabel, BorderLayout.NORTH);
         * 
         * 
         * elapsedSeconds = timeLimitSeconds;
         * 
         * gameTimer = new Timer(1000, __ -> {
         * elapsedSeconds--;
         * timerLabel.setText("Tempo restante: " + formatTime(elapsedSeconds));
         * 
         * if (elapsedSeconds <= 0) {
         * gameTimer.stop();
         * disableGame();
         * JOptionPane.showMessageDialog(null,
         * "⏳ Tempo esgotado! Você perdeu o desafio.");
         * }
         * });
         * gameTimer.start();
         */

        // 🧩 Setores do Sudoku no centro
        JPanel sectorsPanel = new JPanel(new GridLayout(3, 3));
        for (int r = 0; r < 9; r += 3) {
            var endRow = r + 2;
            for (int c = 0; c < 9; c += 3) {
                var endCol = c + 2;
                var spaces = getSpacesFromSector(boardService.getSpaces(), c, endCol, r, endRow);
                JPanel sector = generateSection(spaces);
                sectorsPanel.add(sector);
            }
        }
        mainPanel.add(sectorsPanel, BorderLayout.CENTER);

        // 🎮 Botões na parte inferior
        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);
        
        addPauseButton(mainPanel);
        addUndoButton(mainPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // ✅ centralizado
        buttonPanel.add(resetButton);
        buttonPanel.add(checkGameStatusButton);
        buttonPanel.add(finishGameButton);
        
        buttonPanel.add(pauseButton);
        buttonPanel.add(undoButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.revalidate();
        mainFrame.repaint();
        startTimeMillis = System.currentTimeMillis();

        mainFrame.setVisible(true); // ✅ Correção adicionada
    }

    private List<Space> getSpacesFromSector(final List<List<Space>> spaces,
            final int initCol, final int endCol,
            final int initRow, final int endRow) {
        List<Space> spaceSector = new ArrayList<>();
        for (int r = initRow; r <= endRow; r++) {
            for (int c = initCol; c <= endCol; c++) {
                spaceSector.add(spaces.get(r).get(c)); // ✅ linha → coluna
            }
        }
        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces) {
        List<NumberText> fields = new ArrayList<>();
        spaces.forEach(space -> fields.add(new NumberText(space)));
        fields.forEach(t -> notifierService.subscribe(EventEnum.CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private void addFinishGameButton(final JPanel mainPanel) {
        finishGameButton = new FinishGameButton(__ -> {
            if (boardService.gameIsFinished()) {
                showMessageDialog(null, "🎉 Parabéns! Você concluiu o jogo!");
                disableGame();
            } else {
                showMessageDialog(null, "⚠️ Seu jogo tem alguma inconsistência. Ajuste e tente novamente.");
            }

        });
    }

    private void addCheckGameStatusButton(final JPanel mainPanel) {
        checkGameStatusButton = new CheckGameStatusButton(__ -> {
            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus) {
                case NON_STARTED -> "O jogo não foi iniciado";
                case INCOMPLETE -> "O jogo está incompleto";
                case COMPLETE -> "O jogo está completo";
            };
            message += hasErrors ? " e contém erros" : " e não contém erros";
            showMessageDialog(null, message);
        });
    }

    private void addResetButton(final JPanel mainPanel) {
        resetButton = new ResetButton(__ -> {
            var dialogResult = showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE);
            if (dialogResult == YES_OPTION) {
                boardService.reset();
                notifierService.notify(EventEnum.CLEAR_SPACE);
                // elapsedSeconds = timeLimitSeconds;
                // timerLabel.setText("Tempo restante: 05:00");
                // gameTimer.restart();
                //hintsRemaining = 3;
                //hintButton.setText("Dica (3)");
            }
        });
    }

    

    private void addPauseButton(JPanel mainPanel) {
        pauseButton = new JButton("⏸️ Pausar");
        pauseButton.addActionListener(__ -> {
            if (isPaused) {
                gameTimer.start();
                pauseButton.setText("⏸️ Pausar");
                enableGame();
               
            } else {
                // gameTimer.stop();
                pauseButton.setText("▶️ Retomar");
                disableGame();
            }
            isPaused = !isPaused;
        });
    }

    private void addUndoButton(JPanel mainPanel) {
        undoButton = new JButton("↩️ Desfazer");
        undoButton.addActionListener(__ -> {
            boolean undone = boardService.undoLastMove();
            if (undone) {
                notifierService.notify(EventEnum.CLEAR_SPACE);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhuma jogada para desfazer.");
            }
        });
    }

    private void disableGame() {
        resetButton.setEnabled(false);
        checkGameStatusButton.setEnabled(false);
        finishGameButton.setEnabled(false);
        //hintButton.setEnabled(false);
        undoButton.setEnabled(false);
    }

    private void enableGame() {
        resetButton.setEnabled(true);
        checkGameStatusButton.setEnabled(true);
        finishGameButton.setEnabled(true);
        //hintButton.setEnabled(true);
        undoButton.setEnabled(true);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}