import org.sosy_lab.common.configuration.InvalidConfigurationException;

public class Solution {
    public static void main(String[] args) {
        Solver solver = new Solver();
        try {
            System.out.println(solver.solve());
        } catch (InvalidConfigurationException e) {
            System.out.println("No configuration provided");
        } catch (InterruptedException e) {
            System.out.println("Process interrupted");
        }
    }
}
