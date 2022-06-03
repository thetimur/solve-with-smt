public class LessConstraint {
    ConstraintCell left, right;

    public LessConstraint(ConstraintCell left, ConstraintCell right) {
        this.left = left;
        this.right = right;
    }

    public ConstraintCell getLeft() {
        return left;
    }

    public ConstraintCell getRight() {
        return right;
    }

    public boolean containsCell(int i, int j) {
        return (left.getX() == i && left.getY() == j) || (right.getX() == i && right.getY() == j);
    }
}
