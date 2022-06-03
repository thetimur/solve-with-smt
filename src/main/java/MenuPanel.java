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
    private final SudokuData savedSudoku;
    private final SudokuBoard board;

    public MenuPanel(SudokuData in_sudoku, SudokuBoard in_board) {
        sudoku = in_sudoku;
        savedSudoku = new SudokuData();
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
                copySudoku(savedSudoku, sudoku);

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
                copySudoku(savedSudoku, sudoku);

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

                            for (int i = sudoku.getHeight(); i < lines.size(); i++) {
                                List<String> subLimes = Arrays.asList(lines.get(i).split(" "));

                                if (subLimes.get(0).equals("+")) {
                                    mAddScopeConstraint(subLimes);
                                } else if (subLimes.get(0).equals("<")) {
                                    mAddLessConstraint(subLimes);
                                }
                            }

                            updateRelatedBoard(in_frame);
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            out.setText("Unpredicted file error!");
                        } catch (Exception e) {
                            e.printStackTrace();
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

    private void mAddLessConstraint(List<String> subLimes) {
        sudoku.addLessConstraint(new LessConstraint(
                new ConstraintCell(
                        Integer.parseInt(subLimes.get(1)),
                        Integer.parseInt(subLimes.get(2))),
                new ConstraintCell(
                        Integer.parseInt(subLimes.get(3)),
                        Integer.parseInt(subLimes.get(4))

        )));
    }

    private void mAddScopeConstraint(List<String> subLimes) {
        int weight = Integer.parseInt(subLimes.get(1));
        List<ConstraintCell> cells = new ArrayList<>();

        for (int j = 2; j < subLimes.size(); j += 2) {
            cells.add(new ConstraintCell(
                    Integer.parseInt(subLimes.get(j)),
                    Integer.parseInt(subLimes.get(j + 1))
            ));
        }

        sudoku.addScopeConstraint(new ScopeConstraint(weight, cells));
    }

    public void makeResetButton(JFrame in_frame) {
        JButton resetButton = new JButton("Reset");

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySudoku(savedSudoku, sudoku);

                sudoku.dropConstraints();
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

                        List<ScopeConstraint> scopeConstraints = sudoku.getScopeConstraints();

                        for (ScopeConstraint constraint : scopeConstraints) {
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

                        List<LessConstraint> lessConstraints = sudoku.getLessConstraints();

                        for (LessConstraint constraint : lessConstraints) {

                            String line = "< " +
                                    constraint.left.getX() + " " + constraint.left.getY() + " " +
                                    constraint.right.getX() + " " + constraint.right.getY() +
                                    "\n";
                            writer.append(line);
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

    public void makeUndoButton(JFrame in_frame) {
        JButton undoButton = new JButton("Undo");

        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySudoku(sudoku, savedSudoku);

                updateRelatedBoard(in_frame);
            }
        });

        add(undoButton, gbc);
        gbc.gridy++;
    }

    public void makeAddConstraintButton(JFrame in_frame) {
        JButton addConstraintButton = new JButton("Add constraint");

        JTextField console = new JTextField();

        addConstraintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySudoku(savedSudoku, sudoku);

                List<String> input = Arrays.asList(console.getText().split(" "));

                try {
                    if (input.get(0).equals("+")) {
                        mAddScopeConstraint(input);
                    } else if (input.get(0).equals("<")) {
                        mAddLessConstraint(input);
                    }
                } catch(Exception exp) {
                    out.setText("Incorrect format!");
                }

                updateRelatedBoard(in_frame);
            }
        });

        add(addConstraintButton, gbc);
        gbc.gridy++;
        add(console, gbc);
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

    private void copySudoku(SudokuData s1, SudokuData s2) {
        for (int i = 0; i < sudoku.getHeight(); i++) {
            for (int j = 0; j < sudoku.getWidth(); j++) {
                s1.setValue(i, j, s2.getValue(i, j));
                s1.dropConstraints();
                for (LessConstraint s: s2.getLessConstraints()) {
                    s1.addLessConstraint(s);
                }
                for (ScopeConstraint s: s2.getScopeConstraints()) {
                    s1.addScopeConstraint(s);
                }
            }
        }
    }
}