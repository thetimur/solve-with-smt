import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

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
        local_info.setEnabled(false);
        local_info.setDisabledTextColor(Color.BLACK);

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
                        List<LessConstraint> lessConstraintLists = sudoku.getLessConstraintsByCell(finalI, finalJ);
                        List<ScopeConstraint> scopeConstraintList = sudoku.getScopeConstraintsByCell(finalI, finalJ);

                        StringBuilder info = new StringBuilder();

                        if (!lessConstraintLists.isEmpty()) {
                            info.append("Less constraints:");
                        }

                        for (LessConstraint s: lessConstraintLists) {
                            String line = "\n("  + s.left.getX() + ", " +
                                    s.left.getY() + ") < (" +
                                    s.right.getX() + ", " +
                                    s.right.getY() + ")";

                            if (!info.toString().contains(line)) {
                                info.append(line);
                            }
                        }

                        if (!scopeConstraintList.isEmpty()) {
                            if (!lessConstraintLists.isEmpty()) {
                                info.append("\n");
                            }
                            info.append("Scope constraints:");
                        }

                        for (ScopeConstraint s: scopeConstraintList) {
                            StringBuilder line = new StringBuilder("\n" + s.getWeight() + " ");

                            for (ConstraintCell cell: s.getCells()) {
                                line.append("(").append(cell.getX()).append(", ").append(cell.getY()).append(") ");
                            }

                            if (!info.toString().contains(line.toString())) {
                                info.append(line);
                            }
                        }

                        local_info.setText(info.toString());
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if (fields[finalI][finalJ].getField().getText().equals("") ||
                                fields[finalI][finalJ].getField().getText() == null) {
                            sudoku.setValue(finalI, finalJ, 0);
                        } else {
                            sudoku.setValue(finalI, finalJ, Integer.parseInt(fields[finalI][finalJ].getField().getText()));
                        }

                        local_info.setText("");
                    }
                });

                board.add(fields[i][j]);
            }
        }
    }

    public JTextArea getLocal_info() {
        return local_info;
    }
}