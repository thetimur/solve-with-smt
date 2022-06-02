import ap.terfor.TermOrder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SudokuBoard {
    private final JPanel board;
    Cell[][] fields;

    static class Cell extends JPanel {
        private final JTextField assigment;

        Cell(int value, ConstraintCell cell, ScopeConstraint constraint) {
            setLayout(new GridLayout(1, 1, 2, 2));
            assigment = new JTextField(Integer.toString(value));
            add(assigment);
            if (constraint != null && constraint.containsCell(cell.getX(), cell.getY())) {
                int colorNum = constraint.getWeight() * ScopeConstraint.getId() * 500;
                Color color = Color.getHSBColor(colorNum % (255*255*255), colorNum % (255*255), colorNum % 255);
                setBackground(color);
                assigment.setBackground(color);
            } else {
                setBackground(Color.cyan);
            }
        }

        public JTextField getField() {
            return assigment;
        }
    }

    SudokuBoard(SudokuData sudoku) {
        board = new JPanel(new GridLayout(sudoku.getWidth(), sudoku.getHeight())) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                //g.drawImage(image, 0, 0, null);
                g.drawLine(0, 0, 100, 100);
                g.drawLine(0, 100, 100, 0);
            }
        };

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
                        sudoku.setValue(finalI, finalJ, Integer.parseInt(fields[finalI][finalJ].getField().getText()));
                    }
                });
                board.add(fields[i][j]);
            }
        }
    }

    public Cell[][] getBoardInfo() { return fields; }
}