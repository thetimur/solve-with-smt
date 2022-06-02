import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

                            List<String> lines = reader.lines().collect(Collectors.toList());

                            for (int i = 0; i < sudoku.getHeight(); i++) {
                                String line = lines.get(i);

                                for (int j = 0; j < sudoku.getWidth(); j++) {
                                    if (line.charAt(j) == '.') {
                                        sudoku.setValue(i, j, 0);
                                    } else {
                                        sudoku.setValue(i, j, line.charAt(j) - 48);
                                    }
                                }
                            }

                            if (lines.size() != sudoku.getHeight()) {
                                for (int i = sudoku.getHeight(); i < lines.size(); i++) {
                                    List<String> subLimes = Arrays.asList(lines.get(i).split(" "));

                                    if (subLimes.get(0).equals("+")) {
                                        int weight = Integer.parseInt(subLimes.get(1));
                                        List<ConstraintCell> cells = new ArrayList<>();

                                        for (int j = 2; j < subLimes.size(); j+=2) {
                                            cells.add(new ConstraintCell(
                                                    Integer.parseInt(subLimes.get(j)),
                                                    Integer.parseInt(subLimes.get(j + 1))
                                            ));
                                        }

                                        sudoku.addConstraint(new ScopeConstraint(weight, cells));
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

                        List<ScopeConstraint> constraints = sudoku.getScopeConstraints();

                        for (ScopeConstraint constraint : constraints) {
                            StringBuilder line = new StringBuilder();

                            line.append("+ ");
                            line.append(constraint.getWeight());

                            for (ConstraintCell s : constraint.getCells()) {
                                line.append(" ");
                                line.append(s.getX());
                                line.append(" ");
                                line.append(s.getY());
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
        removeFrame(frame);

        board.fillBoard(sudoku);

        updateFrame(frame);
    }

    public void setOut(String text) {
        this.out.setText(text);
    }
}