import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SudokuData {
    private int[][] field = new int[HEIGHT][WIDTH];
    private static final int HEIGHT = 9;
    private static final int WIDTH = 9;

    public SudokuData() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                field[i][j] = 0;
            }
        }
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getWidth() {
        return WIDTH;
    }

    public void setValue(int x, int y, int value) {
        field[x][y] = value;
    }

    public int getValue(int x, int y) {
        return field[x][y];
    }
}