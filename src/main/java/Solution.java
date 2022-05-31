import org.sosy_lab.common.configuration.InvalidConfigurationException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Solution {

   /* private class Table {
        private static final int HEIGHT = 9;
        private static final int WIDTH = 9;

        JTable table = new JTable(WIDTH, HEIGHT);

        Table(SudokuData sudoku) {
            for (int i = 0; i < sudoku.getHeight(); i++) {
                for (int j = 0; j < sudoku.getWidth(); j++) {
                    table.set
                }
            }
        }
    }
*/
    private void draw() {
        try {
            JFrame frame = new JFrame();

            Solver solver = new Solver();
            SudokuData sudoku = new SudokuData();
            sudoku.setValue(0, 0, 1);
            sudoku.setValue(0, 1, 1);
            solver.solveSudoku(sudoku);

            if (!sudoku.isSat()) {
                System.out.println("Unsat");
            }
            JPanel panel = new JPanel(new GridLayout(sudoku.getWidth(), sudoku.getHeight())) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    //g.drawImage(image, 0, 0, null);
                    //g.drawLine(0, 0, 100, 100);
                    //g.drawLine(0, 100, 100, 0);
                }
            };

            JTextField[][] fields = new JTextField[sudoku.getHeight()][sudoku.getWidth()];

            for (int i = 0; i < sudoku.getHeight(); i++) {
                for (int j = 0; j < sudoku.getWidth(); j++) {
                    fields[i][j] = new JTextField( Integer.toString(sudoku.getValue(i, j)), 2);
                    panel.add(fields[i][j]);
                }
            }

            frame.add(panel);

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("OK");
            frame.setSize(400, 400);

            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();

        /*Solver solver = new Solver();
        SudokuData sudoku = new SudokuData();

        try {
            SudokuData array = new SudokuData();

            solver.solveSudoku(array);

            array.setValue(0, 0, 1);
            array.setValue(0, 1, 1);
            for (int i = 0; i < array.getHeight(); i++) {
                for (int j = 0; j < array.getWidth(); j++) {
                    System.out.print(array.getValue(i, j));
                }
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
//        String filename = "C:\\Users\\MSI GL75\\IdeaProjects\\solve-with-smt\\resources\\OK.jpg";

        solution.draw();
    }
}
