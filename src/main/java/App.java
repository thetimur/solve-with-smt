import javax.swing.*;
import java.awt.*;

class App {
    SudokuBoard board;

    public void launch() {
        try {
            JFrame frame = new JFrame();

            ImageIcon img = new ImageIcon("resources\\Arima.png");
            frame.setIconImage(img.getImage());

            SudokuData sudoku = new SudokuData();
            sudoku.setValue(0, 0, 1);
            //sudoku.setValue(1, 0, 1);

            board = new SudokuBoard(sudoku);

            frame.add(board.getBoard());

            MenuPanel menu = new MenuPanel(sudoku, board);
            menu.makeSolveButton(frame);
            menu.makeLoadButton(frame);
            menu.makeResetButton(frame);
            menu.makeSaveButton();
            menu.makeUndoButton(frame);
            menu.makeAddConstraintButton(frame);

            frame.add(menu, BorderLayout.AFTER_LINE_ENDS);

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("SMT SUDOKU SOLVER");
            frame.setSize(600, 600);

            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
