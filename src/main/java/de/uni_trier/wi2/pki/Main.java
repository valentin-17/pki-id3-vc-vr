package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
import de.uni_trier.wi2.pki.postprocess.ReducedErrorPruner;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;
import de.uni_trier.wi2.pki.settings.CSVSettings;
import de.uni_trier.wi2.pki.settings.ID3Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;

import static de.uni_trier.wi2.pki.util.Helpers.convertToObjectList;
import static de.uni_trier.wi2.pki.util.Helpers.printData;

public class Main {

    /* global constants */
    public static String[] HEADER = null;
    public static final EqualFrequencyDiscretization EQUAL_FREQUENCY = new EqualFrequencyDiscretization();
    public static final EqualWidthDiscretization EQUAL_WIDTH = new EqualWidthDiscretization();
    public static final KMeansDiscretizer K_MEANS = new KMeansDiscretizer();
    public static final CSVSettings csvSettings = new CSVSettings();
    public static final ID3Settings id3Settings = new ID3Settings();
    public static final ReducedErrorPruner pruner = new ReducedErrorPruner();

    public static void main(String[] args) {

        /* io for settings */
        settingsIO();
        System.out.println("-----------------------------------------------------------------------------------------");

        /* initialize constants */
        final String FILE_NAME = csvSettings.getFileName();
        final int LABEL_ATTR_INDEX = csvSettings.getLabelAttributeIndex();
        final String delim = csvSettings.getDelimiter();
        final int BINS = id3Settings.getBins();
        final int NUM_FOLDS = id3Settings.getNumFolds();

        /* Parse the CSV file */
        List<String[]> parsedLines = null;
        try {
            parsedLines = CSVReader.readCsvToArray("target/classes/" + FILE_NAME, delim, true);
            HEADER = CSVReader.readCsvHeader("target/classes/" + FILE_NAME, delim);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        /* validate parsed data */
        if (parsedLines == null) {
            throw new RuntimeException("No data parsed");
        }

        System.out.println("Data parsed successfully.");
        System.out.println("-----------------------------------------------------------------------------------------");

        // define data types of the dataset
        ArrayList<Boolean> attrIsContinuous = new ArrayList<>();
        attrIsContinuous.add(0, true);
        attrIsContinuous.add(1, false);
        attrIsContinuous.add(2, false);
        attrIsContinuous.add(3, true);
        attrIsContinuous.add(4, false);
        attrIsContinuous.add(5, true);
        attrIsContinuous.add(6, false);
        attrIsContinuous.add(7, false);
        attrIsContinuous.add(8, false);
        attrIsContinuous.add(9, true);
        attrIsContinuous.add(10, false);

        // Train model, evaluate model, write XML, ...
        List<Object[]> data = convertToObjectList(parsedLines);

        System.out.println("Discretizing Data...");

        performDiscretization(data, BINS, attrIsContinuous, K_MEANS);

        System.out.println("-----------------------------------------------------------------------------------------");

        List<Object[]> dataSmall = data.subList(0, 100);
        List<Object[]>[] splittedData = splitData(dataSmall, id3Settings.getPruneSize());

        List<Object[]> pruningData = splittedData[0];
        List<Object[]> trainingData = splittedData[1];

        System.out.println("Cross-Validation before Pruning:");

        DecisionTree bestModel = CrossValidator.performCrossValidation(trainingData, LABEL_ATTR_INDEX, ID3Utils::createTree, NUM_FOLDS);

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Writing best model to XML...");

        try {
            XMLWriter.writeXML("target/classes/best_model_without_pruning.xml", bestModel);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        System.out.println("Successfully saved best model to XML-File [best_model_before_pruning.xml] (without Pruning).");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Initialized Pruning...");

        BiFunction<List<Object[]>, Integer, DecisionTree> function = (trainingSet, labelAttribute) -> {
            DecisionTree tree = ID3Utils.createTree(trainingSet, labelAttribute);

            try {
                XMLWriter.writeXML("target/classes/best_model_before_pruning.xml", tree);
                pruner.prune(tree, pruningData, labelAttribute);
                XMLWriter.writeXML("target/classes/best_model_after_pruning.xml", tree);
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }

            return tree;
        };

        System.out.println("Cross-Validation after Pruning:");
        DecisionTree bestPrunedModel = CrossValidator.performCrossValidation(trainingData, LABEL_ATTR_INDEX, function, NUM_FOLDS);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    /**
     * Prompt for settings values and set settings.
     */
    private static void settingsIO() {
        Scanner sc = new Scanner(System.in);

        id3Settings.printDefaultSettings();
        System.out.println();
        csvSettings.printDefaultSettings();
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Do you want to use default settings? (y/n)");
        String useDefault = sc.nextLine();
        if (useDefault.equals("y")) {
            csvSettings.resetToDefault();
            id3Settings.resetToDefault();
            System.out.println("Default settings loaded.");
            return;
        }

        System.out.println("Enter the path of the CSV file:");
        String fileName = sc.nextLine();
        csvSettings.setFileName(fileName);

        System.out.println("Enter the delimiter of the CSV file:");
        String delim = sc.nextLine();
        csvSettings.setDelimiter(delim);

        System.out.println("Enter the index of the label attribute:");
        int labelAttrIndex = Integer.parseInt(sc.nextLine());
        csvSettings.setLabelAttributeIndex(labelAttrIndex);

        System.out.println("Enter the number of bins for discretization:");
        int bins = Integer.parseInt(sc.nextLine());
        id3Settings.setBins(bins);

        System.out.println("Enter the number of folds for cross-validation:");
        int numFolds = Integer.parseInt(sc.nextLine());
        id3Settings.setNumFolds(numFolds);

        System.out.println("Enter the discretization method (1: EqualFrequency, 2: EqualWidth, 3: KMeans):");
        int method = Integer.parseInt(sc.nextLine());
        switch (method) {
            case 1:
                id3Settings.setDiscretizingMethod(EQUAL_FREQUENCY);
                break;
            case 2:
                id3Settings.setDiscretizingMethod(EQUAL_WIDTH);
                break;
            case 3:
                id3Settings.setDiscretizingMethod(K_MEANS);
                System.out.println("Enter the value of epsilon for KMeans-discretization:");
                double epsilon = Double.parseDouble(sc.nextLine());
                id3Settings.setEpsilon(epsilon);
                break;
            default:
                System.out.println("Invalid method. Using KMeans as default.");
                id3Settings.setDiscretizingMethod(K_MEANS);
        }

        System.out.println("Enter the percentage of data to use for pruning:");
        double pruneSize = Double.parseDouble(sc.nextLine());
        id3Settings.setPruneSize(pruneSize);

        System.out.println("Settings saved.");
    }

    /**
     * Perform discretization on the given data.
     *
     * @param data             The data to discretize.
     * @param bins             The number of bins to use.
     * @param attrIsContinuous The indices of the attributes to discretize.
     * @param method           The discretization method to use.
     */
    private static void performDiscretization(List<Object[]> data, int bins, ArrayList<Boolean> attrIsContinuous, BinningDiscretizer method) {

        /* loop through every attribute and discretize if attrIsContinuous == true */
        for (int i = 0; i < attrIsContinuous.size(); i++) {
            if (attrIsContinuous.get(i)) {
                method.discretize(bins, data, i);
            }
        }
    }

    /**
     * Splits the data into training and pruning sets without overlap.
     *
     * @param data      The data to split.
     * @param pruneSize The percentage of the data to use for pruning.
     * @return An array containing the training data and pruning data.
     */
    private static List<Object[]>[] splitData(List<Object[]> data, double pruneSize) {
        if (pruneSize < 0 || pruneSize > 1) {
            throw new IllegalArgumentException("Prune percentage must be between 0 and 1.");
        }

        ArrayList<Object[]> tmp = data.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        List<Object[]> pruningData = new ArrayList<>();
        int dataSize = tmp.size();

        for (int i = 0; i < dataSize; i++) {
            int randomIndex = (int) (Math.random() * dataSize);
            pruningData.add(tmp.get(randomIndex));
            tmp.remove(randomIndex);

            if (pruningData.size() >= dataSize * pruneSize) {
                break;
            }

            dataSize--;
        }

        return new List[]{pruningData, tmp};
    }
}
