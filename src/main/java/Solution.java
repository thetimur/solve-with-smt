import org.sosy_lab.common.configuration.InvalidConfigurationException;

public class Solution {
    public static void main(String[] args) {
        Solver solver = new Solver();
        try {
            SudokuData array = new SudokuData();

            solver.solveSudoku(array);

            array.setValue(0, 0, 1);
            array.setValue(0, 1, 1);
            for (int i = 0; i < array.getHeight(); i++) {
                for (int j = 0; j < array.getWidth(); j++) {
                    System.out.print(array.getValue(i, j));
                }
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
