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
}
