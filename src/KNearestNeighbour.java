
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.util.*;

public class KNearestNeighbour {
    public List<String[]> training;
    public static String[] headers;
    public double[] ranges;
    public final int K;
    public int correct;
    public PrintWriter writer;

    public KNearestNeighbour(PrintWriter writer, File trainingFile, File testFile, int k) throws IOException {
        this.K = k;
        training = readFile(trainingFile);
        ranges = new double[headers.length];
        calcRanges();
        List<String[]> test = readFile(testFile);

        determineClass(test);

        this.writer = writer;

        writer.println("\n*************************\n");
        writer.println("kNearest = " + k);
        writer.println("FINAL TOTAL: \n" + correct + "/" + test.size());
        writer.println((double) correct / (double) test.size() * 100 + "%");
        writer.println("\n*************************\n");

        writer.flush();
        writer.close();
    }

    public KNearestNeighbour(List<String[]> training, List<String[]> test, int k) {
        this.K = k;
        this.training = training;
        ranges = new double[headers.length];
        calcRanges();
        determineClass(test);
    }

    public void determineClass(List<String[]> test) {
        correct = 0;
        for (String[] line : test) {
            int exp = Integer.parseInt(line[line.length - 1]);
            int res = findClass(line);
            if (exp == res) {
                correct++;
            }
        }
    }

    public void calcRanges() {
        double[] minRanges = new double[training.size()];
        double[] maxRanges = new double[training.size()];

        for (String[] instance : training) {
            for (int i = 0; i < instance.length; i++) {
                double feature = Double.parseDouble(instance[i]);
                if (feature < minRanges[i]) minRanges[i] = feature;
                if (feature > maxRanges[i]) maxRanges[i] = feature;
            }
        }

        for (int i = 0; i < ranges.length; i++) {
            ranges[i] = maxRanges[i] - minRanges[i];
        }
    }

    public int findClass(String[] testInstance) {
        ArrayList<Map.Entry<Integer, Double>> kNearest = new ArrayList<>(this.K);
        for (String[] trainingInstance : training) {
            double diff = 0;
            for (int i = 0; i < testInstance.length - 1; i++) {
                double trainingFeat = Double.parseDouble(trainingInstance[i]);
                double testFeat = Double.parseDouble(testInstance[i]);
                diff += Math.pow(trainingFeat - testFeat, 2) / Math.pow(ranges[i], 2);
            }
            diff = Math.sqrt(diff);
            int wc = Integer.parseInt(trainingInstance[trainingInstance.length - 1]);

            checkKNearest(kNearest, wc, diff);
        }

        return calcWineClass(kNearest);
    }

    /**
     * It should keep track of the k smallest current values
     * OR we just keep track of everything and sort at the end, more expensive but simpler...
     */
    public void checkKNearest(ArrayList<Map.Entry<Integer, Double>> kNearest, int wineClass, double dist) {
        for (int i = 0; i < this.K; i++) {
            if (kNearest.size() < this.K || dist < kNearest.get(i).getValue()) { // Add values until we hit K then we can start only adding the smallest
                if (kNearest.size() == this.K) {
                    kNearest.remove(kNearest.size() - 1);
                }
                kNearest.add(i, new AbstractMap.SimpleImmutableEntry<>(wineClass, dist));
                break;
            }
        }

//        for (Map.Entry<Integer, Double> p : kNearest) {
//            System.out.print(p.getKey() + ", ");
//        }
//        System.out.println();
    }

    public int calcWineClass(ArrayList<Map.Entry<Integer, Double>> kNearest) {
        HashMap<Integer, Integer> wineClassVotes = new HashMap<>();
        int highestVote = -1;

        // Tally votes
        for (Map.Entry<Integer, Double> pair : kNearest) {
            wineClassVotes.put(pair.getKey(), wineClassVotes.get(pair.getKey()) == null ? 1 : wineClassVotes.get(pair.getKey()) + 1); // Update vote tally, if it doesn't exist add it in
//            System.out.println("Key " + pair.getKey() + " Value " + wineClassVotes.get(pair.getKey()));
            if (wineClassVotes.get(pair.getKey()) > highestVote)
                highestVote = wineClassVotes.get(pair.getKey()); // Update what the highest vote count is
        }

        HashMap<Integer, Integer> highestClassVotes = new HashMap<>();
        for (Map.Entry<Integer, Integer> voteEntry : wineClassVotes.entrySet()) {
            if (voteEntry.getValue() == highestVote)
                highestClassVotes.put(voteEntry.getKey(), voteEntry.getValue()); // If the entry has the highest votes add it to a new set.
        }

        int randInd = (int) (Math.random() * highestClassVotes.keySet().size());
        for (int i = 0; i < highestClassVotes.keySet().size(); i++) {
            if (i == randInd) {
//                System.out.println("Random index: " + randInd);
//                System.out.println(highestClassVotes.keySet().toArray()[i]);
                return (int) highestClassVotes.keySet().toArray()[i];
            }
        }

        throw new RuntimeException("uhh we didn't find nufin' boss");
    }

    public static List<String[]> readFile(File file) {
        CSVReader csvReader;
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(' ').build();
            csvReader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();

            List<String[]> data = csvReader.readAll();
            headers = data.get(0);
            data.remove(0); // Strip headers
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }


        throw new RuntimeException();
    }
}
