import java.util.ArrayList;
import java.util.List;

public class SudokuData {
    private int[][] field = new int[HEIGHT][WIDTH];
    private static final int HEIGHT = 9;
    private static final int WIDTH = 9;

    public void setValue(int x, int y, int value) {
        field[x][y] = value;
    }

    public int getValue(int x, int y) {
        return field[x][y];
    }
}
