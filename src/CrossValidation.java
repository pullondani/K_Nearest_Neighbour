import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CrossValidation {
    public CrossValidation(PrintWriter writer, File file1, File file2, int kNearest, int crossValidation) throws IOException {
        List<String[]> allInstances = new ArrayList<>();
        allInstances.addAll(KNearestNeighbour.readFile(file1));
        allInstances.addAll(KNearestNeighbour.readFile(file2));

        int validationSize = allInstances.size() / crossValidation; // Decided to just leave out the instances if they don't fit

        int correct = 0;
        double perTotal = 0;
        for (int validationCount = 0; validationCount < crossValidation; validationCount++) {
            List<String[]> training = new ArrayList<>(validationSize * (crossValidation - 1));
            List<String[]> test = new ArrayList<>(validationSize);

            for (int i = 0; i < crossValidation; i++) {
                List<String[]> dataSet = allInstances.subList(i * validationSize, i * validationSize + validationSize);

                if (validationCount == i)
                    test.addAll(dataSet);
                else
                    training.addAll(dataSet);
            }

            KNearestNeighbour p1 = new KNearestNeighbour(training, test, kNearest);
            correct += p1.correct;
            perTotal += (double) p1.correct / (double) test.size();
            writer.println("\n*************************\n");
            writer.println("SUBTOTAL: \n" + p1.correct + "/" + test.size());
            writer.println((double) p1.correct / (double) test.size() * 100 + "%");
        }

        double lastPer = Math.round((perTotal / (double) crossValidation) * 10000) / 100.0;

        writer.println("\n*************************\n");
        writer.println("CROSS VALIDATION K VALUE = " + crossValidation);
        writer.println("CROSS VALIDATION FINAL TOTAL: \n" + correct + "/" + validationSize * crossValidation);
        writer.print("AVERAGE PERCENTAGE: (2dp)");
        writer.println( lastPer + "%");
        writer.println("\n*************************\n");

        writer.flush();
        writer.close();
    }

}
