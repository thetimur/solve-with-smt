import javax.swing.*;
import java.awt.*;

class App {
    SudokuBoard board;

    public void launch() {
        try {
            JFrame frame = new JFrame();

            SudokuData sudoku = new SudokuData();
            sudoku.setValue(0, 0, 1);
            //sudoku.setValue(1, 0, 1);

            board = new SudokuBoard(sudoku);

            frame.add(board.getBoard());

            MenuPanel menu = new MenuPanel(sudoku, board);
            //menu.setOut(result);

            frame.add(menu, BorderLayout.AFTER_LINE_ENDS);

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("SMT SUDOKU SOLVER");
            frame.setSize(400, 400);

            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
