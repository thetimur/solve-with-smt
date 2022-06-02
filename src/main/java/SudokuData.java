import de.uni_freiburg.informatik.ultimate.smtinterpol.theory.epr.util.Pair;

import java.util.ArrayList;
import java.util.List;

class Weight {
    public int weight;
    public int groupNum;
    public boolean isWeighted = false;
}

public class SudokuData {
    private final int[][] field = new int[HEIGHT][WIDTH];
    private static final int HEIGHT = 9;
    private static final int WIDTH = 9;
    private static final int BLOCK = 3;
    private boolean sat;
    Weight[][] weights = new Weight[HEIGHT][WIDTH];

    public SudokuData() {

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                field[i][j] = 0;
                weights[i][j] = new Weight();
            }
        }
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getWidth() {
        return WIDTH;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean value) {
        sat = value;
    }

    public void setValue(int x, int y, int value) {
        field[x][y] = value;
    }

    public int getValue(int x, int y) {
        return field[x][y];
    }

    public int getBlock() {
        return BLOCK;
    }

    public Weight getWeight(int x, int y) {
        return weights[x][y];
    }

    public void setWeight(int x, int y, Weight in_weight) {
        weights[x][y] = in_weight;
    }

}
