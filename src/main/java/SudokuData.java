import java.util.ArrayList;
import java.util.List;

public class SudokuData {
    private final int[][] field = new int[HEIGHT][WIDTH];
    private static final int HEIGHT = 9;
    private static final int WIDTH = 9;
    private static final int BLOCK = 3;
    private boolean sat;
    private List<ScopeConstraint> scopeConstraints = new ArrayList<>();
    private List<LessConstraint> lessConstraints = new ArrayList<>();

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

    public void addScopeConstraint(ScopeConstraint constraint) {
        scopeConstraints.add(constraint);
    }

    public void addLessConstraint(LessConstraint constraint) {
        lessConstraints.add(constraint);
    }

    public ScopeConstraint getConstraint(int i, int j) {
        for (ScopeConstraint sc : scopeConstraints) {
            if (sc.containsCell(i, j)) {
                return sc;
            }
        }
        return null;
    }

    public List<ScopeConstraint> getScopeConstraints() {
        return scopeConstraints;
    }

    public List<LessConstraint> getLessConstraints() {
        return lessConstraints;
    }
}
