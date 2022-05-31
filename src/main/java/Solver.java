import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.*;

public class Solver {
    public String solve() throws InvalidConfigurationException, InterruptedException {

        Configuration config = Configuration.defaultConfiguration();
        LogManager logger = BasicLogManager.create(config);
        ShutdownNotifier notifier = ShutdownNotifier.createDummy();


        try (SolverContext context = SolverContextFactory.createSolverContext(
                config, logger, notifier, SolverContextFactory.Solvers.SMTINTERPOL)) {
            IntegerFormulaManager imgr = context.getFormulaManager().getIntegerFormulaManager();

            // Create formula "a = b" with two integer variables
            NumeralFormula.IntegerFormula a = imgr.makeVariable("a");
            NumeralFormula.IntegerFormula b = imgr.makeVariable("b");
            BooleanFormula f = imgr.equal(a, b);

            // Solve formula, get model, and print variable assignment
            try (ProverEnvironment prover = context.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS)) {
                prover.addConstraint(f);
                if (prover.isUnsat()) {
                    return "Formula not satisfiable";
                }
                try (Model model = prover.getModel()) {
                    return String.format("SAT with a = %s, b = %s", model.evaluate(a), model.evaluate(b));
                }
            } catch (SolverException e) {
                return "Solver error: " + e.getMessage();
            }
        }
    }
}
