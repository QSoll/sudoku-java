package br.com.dio.service;

import br.com.dio.model.Board;
import br.com.dio.model.GameStatusEnum;
import br.com.dio.model.Space;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class BoardService {
    private List<List<Space>> spaces;

    private final static int BOARD_LIMIT = 9;

    private final Board board;
    private final Deque<Move> moveHistory = new ArrayDeque<>();

    private static class Move {
        final int col;
        final int row;
        final Integer previousValue;

        Move(int col, int row, Integer previousValue) {
            this.col = col;
            this.row = row;
            this.previousValue = previousValue;
        }
    }

    public BoardService(final Map<String, String> gameConfig) {
        this.spaces = initBoard(gameConfig);
        this.board = new Board(spaces);
    }

    public List<List<Space>> getSpaces() {
        return board.getSpaces();
    }

    public void reset() {
        board.reset();
        moveHistory.clear();
    }

    public boolean hasErrors() {
        return board.hasErrors();
    }

    public GameStatusEnum getStatus() {
        return board.getStatus();
    }

    public boolean gameIsFinished() {
        return board.gameIsFinished();
    }

    public boolean changeValue(int col, int row, int value) {
        Space space = board.getSpaces().get(col).get(row);
        if (space.isFixed())
            return false;

        moveHistory.push(new Move(col, row, space.getActual()));
        space.setActual(value);
        return true;
    }

    public boolean clearValue(int col, int row) {
        Space space = board.getSpaces().get(col).get(row);
        if (space.isFixed())
            return false;

        moveHistory.push(new Move(col, row, space.getActual()));
        space.clearSpace();
        return true;
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty())
            return false;

        Move last = moveHistory.pop();
        Space space = board.getSpaces().get(last.col).get(last.row);
        space.setActual(last.previousValue);
        return true;
    }

    public Space getHint() {
        for (List<Space> column : this.spaces) {
            for (Space space : column) {
                if (space.getActual() == null && space.getExpected() != 0) {
                    return space;
                }
            }
        }
        return null; // Nenhuma dica disponível
    }

    private List<List<Space>> initBoard(final Map<String, String> gameConfig) {
    List<List<Space>> spaces = new ArrayList<>();

    // Inicializa tabuleiro vazio
    for (int col = 0; col < BOARD_LIMIT; col++) {
        List<Space> column = new ArrayList<>();
        for (int row = 0; row < BOARD_LIMIT; row++) {
            column.add(new Space(0, false)); // valor 0, não fixo
        }
        spaces.add(column);
    }

    // Preenche posições fixas se houver configuração
    String positionConfig = gameConfig.get("positionConfig");
    if (positionConfig != null && !positionConfig.isBlank()) {
        for (String pos : positionConfig.split(";")) {
            if (pos.length() < 2) continue;

            char rowChar = Character.toUpperCase(pos.charAt(0));
            int col = Character.getNumericValue(pos.charAt(1)) - 1;
            int row = rowChar - 'A';

            if (row >= 0 && row < BOARD_LIMIT && col >= 0 && col < BOARD_LIMIT) {
                Space space = new Space(5, true); // valor fixo de exemplo
                spaces.get(col).set(row, space);
            }
        }
    }

    return spaces;
}


}
