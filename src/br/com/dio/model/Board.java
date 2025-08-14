package br.com.dio.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.dio.model.GameStatusEnum.COMPLETE;
import static br.com.dio.model.GameStatusEnum.INCOMPLETE;
import static br.com.dio.model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus() {
        if (spaces.stream().flatMap(Collection::stream)
                .noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))) {
            return NON_STARTED;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    // Verifica se há erros no tabuleiro (duplicações)
    public boolean hasErrors() {
        return hasRowErrors() || hasColumnErrors() || hasBlockErrors();
    }

    private boolean hasRowErrors() {
        for (int row = 0; row < 9; row++) {
            Set<Integer> seen = new HashSet<>();
            for (int col = 0; col < 9; col++) {
                Integer value = spaces.get(row).get(col).getActual();
                if (value != null) {
                    if (seen.contains(value))
                        return true;
                    seen.add(value);
                }
            }
        }
        return false;
    }

    private boolean hasColumnErrors() {
        for (int col = 0; col < 9; col++) {
            Set<Integer> seen = new HashSet<>();
            for (int row = 0; row < 9; row++) {
                Integer value = spaces.get(row).get(col).getActual();
                if (value != null) {
                    if (seen.contains(value))
                        return true;
                    seen.add(value);
                }
            }
        }
        return false;
    }

    private boolean hasBlockErrors() {
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                Set<Integer> seen = new HashSet<>();
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        int x = blockCol * 3 + col;
                        int y = blockRow * 3 + row;
                        Integer value = spaces.get(y).get(x).getActual();
                        if (value != null) {
                            if (seen.contains(value))
                                return true;
                            seen.add(value);
                        }
                    }
                }
            }
        }
        return false;
    }

    // Retorna os valores possíveis para uma célula
    public Set<Integer> getHints(final int col, final int row) {
        var space = spaces.get(row).get(col);
        if (space.isFixed()) {
            return Set.of(); // Sem dicas para células fixas
        }

        Integer originalValue = space.getActual();
        space.clearSpace(); // Remove temporariamente o valor para evitar conflito

        Set<Integer> used = new HashSet<>();

        // Verifica linha
        for (int c = 0; c < 9; c++) {
            Integer value = spaces.get(row).get(c).getActual();
            if (value != null)
                used.add(value);
        }

        // Verifica coluna
        for (int r = 0; r < 9; r++) {
            Integer value = spaces.get(r).get(col).getActual();
            if (value != null)
                used.add(value);
        }

        // Verifica bloco 3x3
        int blockCol = (col / 3) * 3;
        int blockRow = (row / 3) * 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Integer value = spaces.get(blockRow + r).get(blockCol + c).getActual();
                if (value != null)
                    used.add(value);
            }
        }

        space.setActual(originalValue); // Restaura valor original

        Set<Integer> hints = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            if (!used.contains(i))
                hints.add(i);
        }

        return hints;
    }

    // Altera valor de uma célula, se permitido
    public boolean changeValue(final int col, final int row, final int value) {
        var space = spaces.get(row).get(col);

        if (space.isFixed()) {
            return false;
        }

        if (space.getActual() != null) {
            return false;
        }

        Set<Integer> hints = getHints(col, row);
        if (!hints.contains(value)) {
            return false;
        }

        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row) {
        var space = spaces.get(row).get(col);
        if (space.isFixed()) {
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset() {
        spaces.forEach(row -> row.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished() {
        return !hasErrors() && getStatus().equals(COMPLETE);
    }
}
