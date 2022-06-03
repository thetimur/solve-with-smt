import scala.Array;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IllegalFormatException;
import java.util.List;

public class ScopeConstraint {
    private int weight;
    private List<ConstraintCell> cells;
    private static int id = 1;

    public ScopeConstraint(int weight, List<ConstraintCell> cells) throws Exception {
        this.weight = weight;
        this.cells = cells;
        if (!checkConnectivity()) {
            throw new Exception("Cells are not connected");
        }
        id += cells.size();
    }

    private boolean checkConnectivity() {
        Deque<ConstraintCell> q = new ArrayDeque<>();

        q.add(cells.get(0));
        int visited = 0;

        while (!q.isEmpty()) {
            visited++;
            ConstraintCell v = q.pollFirst();
            final int XX[] = {0, 0, 1, -1};
            final int YY[] = {1, -1, 0, 0};
            for (int dim = 0; dim < 4; dim++) {
                ConstraintCell to = new ConstraintCell(v.getX() + XX[dim], v.getY() + YY[dim]);
                if (containsCell(to.getX(), to.getY())) {
                    q.add(to);
                }
            }
        }

        return visited == cells.size();
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
