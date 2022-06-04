import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.*;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * This program parses user-given Sudoku and solves it with an SMT solver.
 *
 * <p>This program is just an example and clearly SMT is not the best solution for solving Sudoku.
 * There might be other algorithms out there that are better suited for solving Sudoku.
 *
 * <p>The more numbers are available in a Sudoku, the easier it can be solved. A completely empty
 * Sudoku will cause the longest runtime in the solver, because it will guess a lot of values.
 *
 * <p>The Sudoku is read from StdIn and should be formatted as the following example:
 *
 * <pre>
 * 2..9.6..1
 * ..6.4...9
 * ...52.4..
 * 3.2..7.5.
 * ...2..1..
 * .9.3..7..
 * .87.5.31.
 * 6.3.1.8..
 * 4....9...
 * </pre>
 *
 * <p>The solution will then be printed on StdOut, just like the following solution:
 *
 * <pre>
 * 248976531
 * 536148279
 * 179523468
 * 312487956
 * 764295183
 * 895361742
 * 987652314
 * 623714895
 * 451839627
 * </pre>\
 */
public class Sudoku {

    public static final int SIZE = 9;
    private static final int BLOCKSIZE = 3;
    private static final Integer[][] UNSOLVABLE_SUDOKU = null;

    public static void solve(SudokuData sudoku)
            throws InvalidConfigurationException, SolverException, InterruptedException {
        Configuration config = Configuration.defaultConfiguration();
        LogManager logger = BasicLogManager.create(config);
        ShutdownNotifier notifier = ShutdownNotifier.createDummy();

        // for (Solvers solver : Solvers.values()) {
        {
            Solvers solver = Solvers.SMTINTERPOL;
            try (SolverContext context =
                         SolverContextFactory.createSolverContext(config, logger, notifier, solver)) {

                SudokuSolver<?> sudokuSolver = new IntegerBasedSudokuSolver(context);
                Integer[][] grid = new Integer[SIZE][SIZE];
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        grid[i][j] = sudoku.getValue(i, j);
                    }
                }
                Integer[][] solution = sudokuSolver.solve(grid, sudoku.getScopeConstraints(), sudoku.getLessConstraints());

                if (solution == UNSOLVABLE_SUDOKU) {
                    sudoku.setSat(false);
                } else {
                    sudoku.setSat(true);
                    for (int i = 0; i < SIZE; i++) {
                        for (int j = 0; j < SIZE; j++) {
                            sudoku.setValue(i, j, solution[i][j]);
                        }
                    }
                }
            } catch (InvalidConfigurationException | UnsatisfiedLinkError e) {

                // on some machines we support only some solvers,
                // thus we can ignore these errors.
                logger.logUserException(Level.INFO, e, "Solver " + solver + " is not available.");

            } catch (UnsupportedOperationException e) {
                logger.logUserException(Level.INFO, e, e.getMessage());
            }
        }
    }

    public Sudoku() {}

    public abstract static class SudokuSolver<S> {

        private final SolverContext context;
        final BooleanFormulaManager bmgr;
        final IntegerFormulaManager imgr;

        private SudokuSolver(SolverContext pContext) {
            context = pContext;
            bmgr = context.getFormulaManager().getBooleanFormulaManager();
            if (context.getSolverName() != Solvers.BOOLECTOR) {
                imgr = context.getFormulaManager().getIntegerFormulaManager();
            } else {
                imgr = null;
            }
        }

        abstract S getSymbols();

        abstract List<BooleanFormula> getRules(S symbols, List<ScopeConstraint> scopeConstraints, List<LessConstraint> lessConstraints);

        abstract List<BooleanFormula> getAssignments(S symbols, Integer[][] grid);

        abstract Integer getValue(S symbols, Model model, int row, int col);

        /**
         * Solves a sudoku using the given grid values and returns a possible solution. Return <code>
         * Null
         * </code> if Sudoku cannot be solved.
         */
        @Nullable
        public Integer[][] solve(Integer[][] grid, List<ScopeConstraint> scopeConstraints, List<LessConstraint> lessConstraints) throws InterruptedException, SolverException {
            S symbols = getSymbols();
            List<BooleanFormula> rules = getRules(symbols, scopeConstraints, lessConstraints);
            List<BooleanFormula> assignments = getAssignments(symbols, grid);

            // solve Sudoku
            try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
                prover.push(bmgr.and(rules));
                prover.push(bmgr.and(assignments));

                boolean isUnsolvable = prover.isUnsat(); // the hard part
                if (isUnsolvable) {
                    return UNSOLVABLE_SUDOKU;
                }

                // get model and convert it
                Integer[][] solution = new Integer[SIZE][SIZE];
                try (Model model = prover.getModel()) {
                    for (int row = 0; row < SIZE; row++) {
                        for (int col = 0; col < SIZE; col++) {
                            solution[row][col] = getValue(symbols, model, row, col);
                        }
                    }
                }
                return solution;
            }
        }
    }

    public static class IntegerBasedSudokuSolver extends SudokuSolver<IntegerFormula[][]> {

        public IntegerBasedSudokuSolver(SolverContext context) {
            super(context);
        }

        /** prepare symbols: one symbol for each of the 9x9 cells. */
        @Override
        IntegerFormula[][] getSymbols() {
            final IntegerFormula[][] symbols = new IntegerFormula[SIZE][SIZE];
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    symbols[row][col] = imgr.makeVariable("x_" + row + "_" + col);
                }
            }
            return symbols;
        }

        /**
         * build the default Sudoku constraints:
         * <li>each symbol has a value from 1 to 9.
         * <li>each column, each row, and each 3x3 block contains 9 distinct integer values.
         */
        @Override
        List<BooleanFormula> getRules(IntegerFormula[][] symbols, List<ScopeConstraint> scopeConstraints, List<LessConstraint> lessConstraints) {
            final List<BooleanFormula> rules = new ArrayList<>();

            // each symbol has a value from 1 to 9
            IntegerFormula one = imgr.makeNumber(1);
            IntegerFormula nine = imgr.makeNumber(9);
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    for (int i = 0; i < SIZE; i++) {
                        rules.add(imgr.lessOrEquals(one, symbols[row][col]));
                        rules.add(imgr.lessOrEquals(symbols[row][col], nine));
                    }
                }
            }

            // row constraints: distinct numbers in all rows
            for (int row = 0; row < SIZE; row++) {
                List<IntegerFormula> lst = new ArrayList<>(Arrays.asList(symbols[row]).subList(0, SIZE));
                rules.add(imgr.distinct(lst));
            }

            // column constraints: distinct numbers in all columns
            for (int col = 0; col < SIZE; col++) {
                List<IntegerFormula> lst = new ArrayList<>();
                for (int row = 0; row < SIZE; row++) {
                    lst.add(symbols[row][col]);
                }
                rules.add(imgr.distinct(lst));
            }

            // block constraints: distinct numbers in all 3x3 blocks
            for (int rowB = 0; rowB < SIZE; rowB += BLOCKSIZE) {
                for (int colB = 0; colB < SIZE; colB += BLOCKSIZE) {
                    List<IntegerFormula> lst = new ArrayList<>();
                    for (int row = rowB; row < rowB + BLOCKSIZE; row++) {
                        lst.addAll(Arrays.asList(symbols[row]).subList(colB, colB + BLOCKSIZE));
                    }
                    rules.add(imgr.distinct(lst));
                }
            }

            // Less constraints
            for (LessConstraint lc : lessConstraints) {
                rules.add(imgr.lessThan(symbols[lc.getLeft().getX()][lc.getLeft().getY()], symbols[lc.getRight().getX()][lc.getRight().getY()]));
            }

            // Scope constraints
            for (ScopeConstraint sc : scopeConstraints) {
                List<IntegerFormula> cl = new ArrayList<>();
                for (ConstraintCell c : sc.getCells()) {
                    cl.add(symbols[c.getX()][c.getY()]);
                }
                rules.add(imgr.equal(imgr.sum(cl), imgr.makeNumber(sc.getWeight())));
            }

            return rules;
        }

        /** convert the user-given values into constraints for the solver. */
        @Override
        List<BooleanFormula> getAssignments(IntegerFormula[][] symbols, Integer[][] grid) {
            final List<BooleanFormula> assignments = new ArrayList<>();
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    Integer value = grid[row][col];
                    if (value > 0) {
                        assignments.add(imgr.equal(symbols[row][col], imgr.makeNumber(value)));
                    }
                }
            }
            return assignments;
        }

        @Override
        Integer getValue(IntegerFormula[][] symbols, Model model, int row, int col) {
            return Objects.requireNonNull(model.evaluate(symbols[row][col])).intValue();
        }
    }
}