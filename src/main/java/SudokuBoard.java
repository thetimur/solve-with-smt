import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class SudokuBoard {
    private final JPanel board;
    private final JTextArea local_info = new JTextArea();
    Cell[][] fields;

    static class Cell extends JPanel {
        private final JTextField assignment;

        Cell(int value, ConstraintCell cell, ScopeConstraint constraint) {
            setLayout(new GridLayout(1, 1, 2, 2));
            String text = (value == 0) ? "" : Integer.toString(value);
            assignment = new JTextField(text);
            assignment.setHorizontalAlignment(SwingConstants.CENTER);
            add(assignment);
            if (constraint != null && constraint.containsCell(cell.getX(), cell.getY())) {
                Color currentColor = Colors.getColor(constraint.getId());
                setBackground(Color.cyan);
                assignment.setBackground(currentColor);
            } else {
                setBackground(Color.cyan);
            }

            int[] flags = new int[2];

            if (cell.getY() % 3 == 0) {
                flags[1] = 3;
            }
            if (cell.getX() % 3 == 0) {
                flags[0] = 3;
            }
            setBorder(BorderFactory.createEmptyBorder(flags[0], flags[1], 0, 0));
        }

        public JTextField getField() {
            return assignment;
        }
    }

    SudokuBoard(SudokuData sudoku) {
        board = new JPanel(new GridLayout(sudoku.getWidth(), sudoku.getHeight()));

        board.setBackground(Color.cyan);
        fillBoard(sudoku);
    }

    public JPanel getBoard() {
        return board;
    }

    public void fillBoard(SudokuData sudoku) {
        fields = new Cell[sudoku.getHeight()][sudoku.getWidth()];

        for (int i = 0; i < sudoku.getHeight(); i++) {
            for (int j = 0; j < sudoku.getWidth(); j++) {
                fields[i][j] = new Cell(sudoku.getValue(i, j), new ConstraintCell(i, j), sudoku.getConstraint(i, j));

                int finalJ = j;
                int finalI = i;

                fields[i][j].getField().addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        sudoku.getConstraint(finalI, finalJ);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if (fields[finalI][finalJ].getField().getText().equals("") ||
                                fields[finalI][finalJ].getField().getText() == null) {
                            sudoku.setValue(finalI, finalJ, 0);
                        } else {
                            sudoku.setValue(finalI, finalJ, Integer.parseInt(fields[finalI][finalJ].getField().getText()));
                        }
                    }
                });

                board.add(fields[i][j]);
            }
        }
    }

    public Cell[][] getBoardInfo() { return fields; }
}