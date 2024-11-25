package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
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

import static de.uni_trier.wi2.pki.util.Helpers.convertToObjectList;

public class Main {

    /* global constants */
    public static String[] HEADER = null;
    static final EqualFrequencyDiscretization EQUAL_FREQUENCY = new EqualFrequencyDiscretization();
    static final EqualWidthDiscretization EQUAL_WIDTH = new EqualWidthDiscretization();
    static final KMeansDiscretizer K_MEANS = new KMeansDiscretizer();
    static final CSVSettings csvSettings = new CSVSettings();
    static final ID3Settings id3Settings = new ID3Settings();

    public static void main(String[] args) {

        /* io for settings */
        settingsIO();

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

        performDiscretization(data, BINS, attrIsContinuous, K_MEANS);
        System.out.println("-----------------------------------------------------------------------------------------");

        List<Object[]> dataSmall = data.subList(0, 50);

        DecisionTree bestModel = CrossValidator.performCrossValidation(dataSmall, LABEL_ATTR_INDEX, ID3Utils::createTree, NUM_FOLDS);

        try {
            XMLWriter.writeXML("target/classes/best_model.xml", bestModel);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Prompt for settings values and set settings.
     */
    private static void settingsIO() {
        System.out.println("Enter the path of the CSV file:");
        String fileName = System.console().readLine();
        csvSettings.setFileName(fileName);

        System.out.println("Enter the delimiter of the CSV file:");
        String delim = System.console().readLine();
        csvSettings.setDelimiter(delim);

        System.out.println("Enter the index of the label attribute:");
        int labelAttrIndex = Integer.parseInt(System.console().readLine());
        csvSettings.setLabelAttributeIndex(labelAttrIndex);

        System.out.println("Enter the number of bins for discretization:");
        int bins = Integer.parseInt(System.console().readLine());
        id3Settings.setBins(bins);

        System.out.println("Enter the number of folds for cross-validation:");
        int numFolds = Integer.parseInt(System.console().readLine());
        id3Settings.setNumFolds(numFolds);

        System.out.println("Enter the value of epsilon for KMeans-discretization:");
        double epsilon = Double.parseDouble(System.console().readLine());
        id3Settings.setEpsilon(epsilon);

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
}
