import java.util.List;

public class ScopeConstraint {
    private int weight;
    private List<ConstraintCell> cells;
    private static int id = 1;

    public ScopeConstraint(int weight, List<ConstraintCell> cells) {
        this.weight = weight;
        this.cells = cells;
        id += cells.size();
    }

    public static int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public List<ConstraintCell> getCells() {
        return cells;
    }

    public boolean containsCell(int i, int j) {
        for (ConstraintCell c : cells) {
            if (c.getX() == i && c.getY() == j) {
                return true;
            }
        }
        return false;
    }
}
