import org.sosy_lab.common.configuration.InvalidConfigurationException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Solution {
    SudokuBord bord;

    public static class MenuPanel extends JPanel {

        private final JTextField out = new JTextField("Here will appear your results");;

        public MenuPanel(SudokuData sudoku) {
            setBorder(new EmptyBorder(4, 4, 4, 4));
            setLayout(new GridBagLayout());
            setBackground(Color.cyan);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JButton solveButton = new JButton("Solve");

            solveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Отладочный код!
                    System.out.println(sudoku.getValue(0, 0));
                    // Отладочный код!

                    Solver solver = new Solver();

                    try {
                        solver.solveSudoku(sudoku);
                        if (!sudoku.isSat()) {
                            out.setText("Unsat!");
                        } else {
                            out.setText("Sat!");
                        }
                    } catch (Exception exception) {
                        out.setText("Error while solving!");
                        exception.printStackTrace();
                    }
                }
            });

            add(solveButton, gbc);
            gbc.gridy++;
            add(new JButton("Load from file"), gbc);
            gbc.gridy++;
            add(new JButton("Reset"), gbc);
            gbc.gridy++;
            add(new JButton("Save assignment"), gbc);
            add(out);
        }

        public void setOut(String text) {
            this.out.setText(text);
        }
    }

    public static class SudokuBord {
        private final JPanel bord;

        SudokuBord(SudokuData sudoku) {
            bord = new JPanel(new GridLayout(sudoku.getWidth(), sudoku.getHeight())) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    //g.drawImage(image, 0, 0, null);
                    g.drawLine(0, 0, 100, 100);
                    g.drawLine(0, 100, 100, 0);
                }
            };

            bord.setBackground(Color.cyan);

            JTextField[][] fields = new JTextField[sudoku.getHeight()][sudoku.getWidth()];

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
                    bord.add(fields[i][j]);
                }
            }
        }

        public JPanel getBord() {
            return bord;
        }
    }

    private void draw() {
        try {
            JFrame frame = new JFrame();

            Solver solver = new Solver();
            SudokuData sudoku = new SudokuData();
            sudoku.setValue(0, 0, 1);
            sudoku.setValue(0, 1, 1);
            solver.solveSudoku(sudoku);

            String result = "Sat!";

            if (!sudoku.isSat()) {
                System.out.println("Unsat");
                result = "Unsat!";
            }

            bord = new SudokuBord(sudoku);

            frame.add(bord.getBord());

            MenuPanel menu = new MenuPanel(sudoku);
            menu.setOut(result);

            frame.add(menu, BorderLayout.AFTER_LINE_ENDS);

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("SMT SUDOKU SOLVER");
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