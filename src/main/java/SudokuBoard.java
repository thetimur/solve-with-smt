import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SudokuBoard {
    private final JPanel board;
    JTextField[][] fields;

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

        fields = new JTextField[sudoku.getHeight()][sudoku.getWidth()];

        for (int i = 0; i < sudoku.getHeight(); i++) {
            for (int j = 0; j < sudoku.getWidth(); j++) {
                fields[i][j] = new JTextField( Integer.toString(sudoku.getValue(i, j)), 2);

                int finalJ = j;
                int finalI = i;
                fields[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sudoku.setValue(finalI, finalJ, Integer.parseInt(fields[finalI][finalJ].getText()));
                    }
                });
                board.add(fields[i][j]);
            }
        }
    }

    public JPanel getBoard() {
        return board;
    }

    public JTextField[][] getBoardInfo() { return fields; }
}