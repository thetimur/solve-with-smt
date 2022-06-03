import scala.Array;

import java.util.*;

public class ScopeConstraint {
    private int weight;
    private List<ConstraintCell> cells;
    private int id;
    private static int counter = 1;

    public ScopeConstraint(int weight, List<ConstraintCell> cells) throws Exception {
        this.weight = weight;
        this.cells = cells;
        if (!checkConnectivity()) {
            throw new Exception("Cells are not connected");
        }
        id = counter++;
    }

    private boolean checkConnectivity() {
        Deque<ConstraintCell> q = new ArrayDeque<>();
        Boolean[] vis = new Boolean[81];
        Arrays.fill(vis, Boolean.FALSE);
        q.add(cells.get(0));
        int visited = 0;

        while (!q.isEmpty()) {
            ConstraintCell v = q.pollFirst();
            vis[v.getX() * 9 + v.getY()] = true;

            final int[] XX = {0, 0, 1, -1};
            final int[] YY = {1, -1, 0, 0};
            for (int dim = 0; dim < 4; dim++) {
                ConstraintCell to = new ConstraintCell(v.getX() + XX[dim], v.getY() + YY[dim]);
                if (valid(to) && !vis[to.getX() * 9 + to.getY()]) {
                    q.add(to);
                }
            }
        }

        return Arrays.stream(vis).filter(it -> it).count() == cells.size();
    }

    private boolean valid(ConstraintCell c) {
        return c.getX() >= 0 && c.getY() >= 0 && c.getX() < 9 && c.getY() < 9 && containsCell(c.getX(), c.getY());
    }

    public int getId() {
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
