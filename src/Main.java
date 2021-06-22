import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static final String TRAINING_PATH = "./data/wine-training";
    public static final String TEST_PATH = "./data/wine-test";

    /**
     * @param args 0: training, 1: test 2: kNearest
     */
    public static void main(String[] args) {
        try {
            switch (args.length) {
                case 1:
                    new KNearestNeighbour(new PrintWriter(new FileWriter("sampledoutput.txt")), new File(TRAINING_PATH), new File(TEST_PATH), Integer.parseInt(args[0]));
                    break;
                case 2:
                    new CrossValidation(new PrintWriter(new FileWriter("sampledoutput.txt")), new File(TRAINING_PATH), new File(TEST_PATH), Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                    break;
                default:
                    new CrossValidation(new PrintWriter(new FileWriter("sampledoutput.txt")), new File(TRAINING_PATH), new File(TEST_PATH), 3, 7);
                    break;
            }
        } catch (IOException e) {
            System.out.println("File writer failed");
        }
    }
}
