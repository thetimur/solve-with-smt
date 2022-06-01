import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

class MenuPanel extends JPanel {

    private final JTextField out = new JTextField("Here will appear your results");;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final SudokuData sudoku;
    private final SudokuBoard board;

    public MenuPanel(SudokuData in_sudoku, SudokuBoard in_board) {
        sudoku = in_sudoku;
        board = in_board;

        setBorder(new EmptyBorder(4, 4, 4, 4));
        setLayout(new GridBagLayout());
        setBackground(Color.cyan);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        makeSolveButton();
        makeLoadButton();

        add(new JButton("Reset"), gbc);
        gbc.gridy++;
        makeSaveButton();

        add(out);
    }

    private void makeSolveButton() {
        JButton solveButton = new JButton("Solve");

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Solver.solveSudoku(sudoku);
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
    }

    private void makeLoadButton() {
        JButton loadButton = new JButton("Load from file");

        loadButton.addActionListener(new ActionListener() {

            private Component frame;

            @Override
            public void actionPerformed(ActionEvent enterPress) {
                JFileChooser chooser = new JFileChooser();
                File F;
                chooser.setCurrentDirectory(new java.io.File("sudoku"));
                chooser.setSelectedFile(new File(""));
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (chooser.showOpenDialog(frame) == JFileChooser.OPEN_DIALOG) {
                    F = chooser.getSelectedFile();
                    if (F.isFile() && getFileExtension(F).equals("sudoku")) {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(F));

                            JTextField[][] fields = new JTextField[sudoku.getHeight()][sudoku.getWidth()];
                            board.getBoard().removeAll();

                            for (int i = 0; i < sudoku.getHeight(); i++) {
                                String line = reader.readLine();

                                for (int j = 0; j < sudoku.getWidth(); j++) {
                                    if (line.charAt(j) == '.') {
                                        sudoku.setValue(i, j, 0);
                                        fields[i][j] = new JTextField( Integer.toString(0), 2);
                                    } else {
                                        sudoku.setValue(i, j, line.charAt(j));
                                        fields[i][j] = new JTextField( Integer.toString(line.charAt(j)), 2);
                                    }
                                    board.getBoard().add(fields[i][j]);
                                }
                            }

                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            out.setText("Unpredicted file error!");
                        }
                    } else {
                        out.setText("Incorrect file format!");
                    }
                } else {
                    out.setText("Unpredicted file error!");
                }
            }
        });

        add(loadButton, gbc);
        gbc.gridy++;
    }

    private void makeSaveButton() {
        JButton saveButton = new JButton("Save assignment");

        saveButton.addActionListener(enterPress -> {
                    File filePath = new File("sudoku");
                    try {
                        FileWriter writer = new FileWriter(filePath + "\\tmp.sudoku", false);

                        for (int i = 0; i < sudoku.getHeight(); i++) {
                            StringBuilder line = new StringBuilder();

                            for (int j = 0; j < sudoku.getWidth(); j++) {
                                int value = sudoku.getValue(i, j);

                                if (value == 0) {
                                    line.append(".");
                                } else {
                                    line.append(value);
                                }
                            }

                            line.append("\n");
                            writer.append(line.toString());
                        }

                        setOut("Save success!");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        out.setText("Unpredicted file error!");
                    }
                });


        add(saveButton, gbc);
        gbc.gridy++;
    }

    private String getFileExtension(File F) {
        String filename = F.getName();

        if(filename.lastIndexOf(".") > 0) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
    }

    public void setOut(String text) {
        this.out.setText(text);
    }
}