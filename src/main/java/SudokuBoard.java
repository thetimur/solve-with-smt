import ap.terfor.TermOrder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SudokuBoard {
    private final JPanel board;
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
                int colorNum = constraint.getWeight() * ScopeConstraint.getId() * 500;
                Color color = Color.getHSBColor(colorNum % (255*255*255), colorNum % (255*255), colorNum % 255);
                setBackground(color);
                assignment.setBackground(color);
            } else {
                setBackground(Color.cyan);
            }
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
                fields[i][j].getField().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
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