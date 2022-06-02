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

        add(out);
    }

    public void makeSolveButton(JFrame frame) {
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

                    updateRelatedBoard(frame);
                } catch (Exception exception) {
                    out.setText("Error while solving!");
                    exception.printStackTrace();
                }
            }
        });

        add(solveButton, gbc);
        gbc.gridy++;
    }

    public void makeLoadButton(JFrame in_frame) {
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

                            for (int i = 0; i < sudoku.getHeight(); i++) {
                                String line = reader.readLine();

                                for (int j = 0; j < sudoku.getWidth(); j++) {
                                    if (line.charAt(j) == '.') {
                                        sudoku.setValue(i, j, 0);
                                    } else {
                                        sudoku.setValue(i, j, line.charAt(j) - 48);
                                    }
                                }
                            }

                            updateRelatedBoard(in_frame);
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

    public void makeResetButton(JFrame in_frame) {
        JButton resetButton = new JButton("Reset");

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < sudoku.getHeight(); i++) {
                    for (int j = 0; j < sudoku.getWidth(); j++) {
                        sudoku.setValue(i, j, 0);
                    }
                }
                updateRelatedBoard(in_frame);
            }
        });

        add(resetButton, gbc);
        gbc.gridy++;
    }

    public void makeSaveButton() {
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

    private void removeFrame(JFrame frame) {
        frame.getContentPane().remove(board.getBoard());
        board.getBoard().removeAll();
    }

    private void updateFrame(JFrame frame) {
        frame.getContentPane().add(board.getBoard());
        frame.validate();
    }

    private void updateRelatedBoard(JFrame frame) {
        JTextField[][] fields = new JTextField[sudoku.getHeight()][sudoku.getWidth()];
        removeFrame(frame);

        for (int i = 0; i < sudoku.getHeight(); i++) {

            for (int j = 0; j < sudoku.getWidth(); j++) {
                fields[i][j] = new JTextField( Integer.toString(sudoku.getValue(i, j)), 2);
                board.getBoard().add(fields[i][j]);
            }
        }

        updateFrame(frame);
    }

    public void setOut(String text) {
        this.out.setText(text);
    }
}