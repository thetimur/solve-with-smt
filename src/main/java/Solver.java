import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Solver {

    private String getNameByIndex(int i, int j) {
        return String.format("cell_%d_%d", i, j);
    }

    public SudokuData solveSudoku(SudokuData current) throws Exception {

        try {
            Configuration config = Configuration.defaultConfiguration();
            LogManager logger = BasicLogManager.create(config);
            ShutdownNotifier notifier = ShutdownNotifier.createDummy();

            SolverContext context = SolverContextFactory.createSolverContext(
                    config, logger, notifier, SolverContextFactory.Solvers.PRINCESS);

            FormulaManager fmgr = context.getFormulaManager();

            BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();
            IntegerFormulaManager imgr = fmgr.getIntegerFormulaManager();
            List<BooleanFormula> rules = new ArrayList<>();
            List<BooleanFormula> assignments = new ArrayList<>();

            NumeralFormula.IntegerFormula[][] vars = new NumeralFormula.IntegerFormula[current.getHeight()][current.getWidth()];
            for (int i = 0; i < current.getHeight(); i++) {
                for (int j = 0; j < current.getWidth(); j++) {
                    vars[i][j] = imgr.makeVariable(getNameByIndex(i, j));
                }
            }

            // Add constraint that all values in [1, 9]
            for (int i = 0; i < current.getHeight(); i++) {
                for (int j = 0; j < current.getWidth(); j++) {
                    rules.add(imgr.lessOrEquals(vars[i][j], imgr.makeNumber(9)));
                    rules.add(imgr.greaterOrEquals(vars[i][j], imgr.makeNumber(1)));
                }
            }

            // Add row constraints
            for (int i = 0; i < current.getHeight(); i++) {
                List<NumeralFormula.IntegerFormula> row = new ArrayList<>();
                for (int j = 0; j < current.getWidth(); j++) {
                    row.add(vars[i][j]);
                }
                rules.add(imgr.distinct(row));
            }

            // Add column constraints
            for (int j = 0; j < current.getWidth(); j++) {
                List<NumeralFormula.IntegerFormula> col = new ArrayList<>();
                for (int i = 0; i < current.getHeight(); i++) {
                    col.add(vars[i][j]);
                }
                rules.add(imgr.distinct(col));
            }

            // Add user-given constraints
            for (int i = 0; i < current.getHeight(); i++) {
                for (int j = 0; j < current.getWidth(); j++) {
                    int val = current.getValue(i, j);
                    if (val > 0) {
                        assignments.add(imgr.equal(imgr.makeNumber(val), vars[i][j]));
                    }
                }
            }

            // Add 3x3 block constaints
            final int block = current.getBlock();
            for (int i = 0; i < current.getHeight(); i += block) {
                for (int j = 0; j < current.getWidth(); j += block) {
                    rules.add(getBlockConstraints(i, j, block, vars, imgr));
                }
            }

            try (ProverEnvironment prover = context.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS)) {
                prover.push(bmgr.and(rules));
                prover.push(bmgr.and(assignments));

                if (prover.isUnsat()) {
                    current.setSat(false);
                    return current;
                }
                Model model = prover.getModel();
                current.setSat(true);
                for (int i = 0; i < current.getHeight(); i++) {
                    for (int j = 0; j < current.getWidth(); j++) {
                        current.setValue(i, j, Objects.requireNonNull(model.evaluate(vars[i][j])).intValue());
                    }
                }
            } catch (SolverException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new Exception("Invalid solver context: " + e.getMessage());
        }

        return current;
    }

    private BooleanFormula getBlockConstraints(int i, int j, int block, NumeralFormula.IntegerFormula[][] vars, IntegerFormulaManager imgr) {
        List<NumeralFormula.IntegerFormula> ret = new ArrayList<>();

        for (int ci = i; ci < i + block; ci++) {
            ret.addAll(Arrays.asList(vars[ci]).subList(j, j + block));
        }
        return imgr.distinct(ret);
    }

}
