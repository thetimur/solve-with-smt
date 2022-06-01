import java.util.*;

import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.*;


public class Solver {

    private static String getNameByIndex(int i, int j) {
        return String.format("cell_%d_%d", i, j);
    }

    public static void solveSudoku(SudokuData current) throws Exception {
        Sudoku.solve(current);
    }
}
