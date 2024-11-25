package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.uni_trier.wi2.pki.util.Helpers.convertToObjectList;
import static de.uni_trier.wi2.pki.util.Helpers.printData;

public class Main {

    public static String[] HEADER = null;

    public static void main(String[] args) {

        /*
        // some constants
        final String FILE_NAME = "churn_data.csv";
        final int LABEL_ATTR_INDEX = 10;
        final String delim = ";";

        //discretization params
        EqualFrequencyDiscretization efd = new EqualFrequencyDiscretization();
        EqualWidthDiscretization ewd = new EqualWidthDiscretization();
        KMeansDiscretizer kmd = new KMeansDiscretizer();
        int BINS = 5;
        int ATTRIBUTE_ID;

        // parse CSV data
        List<String[]> parsedLines = null;
        try {
            parsedLines = CSVReader.readCsvToArray("target/classes/" + FILE_NAME, delim, true);
            HEADER = CSVReader.readCsvHeader("target/classes/" + FILE_NAME, delim);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // check if any data was parsed
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

        List<String[]> parsedLinesSmall = parsedLines.subList(0, 10);

        List<Object[]> KMeansDiscretizedData = convertToObjectList(parsedLines);
        List<Object[]> EFDDiscretizedData = convertToObjectList(parsedLines);
        List<Object[]> EWDDiscretizedData = convertToObjectList(parsedLinesSmall);

        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 0);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 3);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 5);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 9);

        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 0);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 3);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 5);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 9);

        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 0);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 3);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 5);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 9);

        List<Object[]> KMeansDiscretizedDataSmall = KMeansDiscretizedData.subList(0, 100);
        List<Object[]> EFDDiscretizedDataSmall = EFDDiscretizedData.subList(0, 50);
        List<Object[]> EWDDiscretizedDataSmall = EWDDiscretizedData.subList(0, 10);

        //DecisionTree dt = ID3Utils.createTree(EFDDiscretizedDataSmall, LABEL_ATTR_INDEX);
        DecisionTree dt = ID3Utils.createTree(EWDDiscretizedDataSmall, LABEL_ATTR_INDEX);
        //DecisionTree dt = ID3Utils.createTree(KMeansDiscretizedDataSmall, LABEL_ATTR_INDEX);
        //DecisionTree bestModel = CrossValidator.performCrossValidation(EFDDiscretizedDataSmall, LABEL_ATTR_INDEX, ID3Utils::createTree, 5);

        try {
            XMLWriter.writeXML("target/classes/decision_tree.xml", dt);
            // XMLWriter.writeXML("target/classes/best_model.xml", bestModel);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        */

        final String FILE_NAME = "churn_data.csv";
        final int LABEL_ATTR_INDEX = 10;
        final String delim = ";";

        EqualFrequencyDiscretization efd = new EqualFrequencyDiscretization();
        EqualWidthDiscretization ewd = new EqualWidthDiscretization();
        KMeansDiscretizer kmd = new KMeansDiscretizer();
        int BINS = 5;
        int ATTRIBUTE_ID;

        List<String[]> parsedLines = null;
        try {
            parsedLines = CSVReader.readCsvToArray("target/classes/" + FILE_NAME, delim, true);
            HEADER = CSVReader.readCsvHeader("target/classes/" + FILE_NAME, delim);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (parsedLines == null) {
            throw new RuntimeException("No data parsed");
        }

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

        int size_of_data = parsedLines.size();

        //20% der Daten für Testdaten
        int test_data_size = (int) (size_of_data * 0.2);

        //zufällige Auswahl von Testdaten
        List<String[]> test_data = new ArrayList<>();
        for (int i = 0; i < test_data_size; i++) {
            int randomIndex = (int) (Math.random() * size_of_data);
            test_data.add(parsedLines.get(randomIndex));
            parsedLines.remove(randomIndex);
            size_of_data--;
        }

        List<String[]> parsedLinesSmall = parsedLines.subList(0, 10);

        /*
        System.out.println("parsedLinesSmall: ");
        System.out.println();
        for (String[] line : parsedLinesSmall) {
            System.out.println(Arrays.toString(line));
        }
        System.out.println("-------------------------------------------------");
         */

        List<Object[]> test_valli = new ArrayList<>();
        test_valli.add(new Object[]{"klein", "blond", "blau", "+"});
        test_valli.add(new Object[]{"gross", "rot", "blau", "+"});
        test_valli.add(new Object[]{"gross", "blond", "blau", "+"});
        test_valli.add(new Object[]{"gross", "blond", "braun", "-"});
        test_valli.add(new Object[]{"klein", "dunkel", "blau", "-"});
        test_valli.add(new Object[]{"gross", "dunkel", "blau", "-"});
        test_valli.add(new Object[]{"gross", "dunkel", "braun", "-"});
        test_valli.add(new Object[]{"klein", "blond", "braun", "-"});

        test_valli.add(new Object[]{"klein", "blond", "blau", "+"});
        test_valli.add(new Object[]{"gross", "rot", "blau", "+"});
        test_valli.add(new Object[]{"gross", "blond", "blau", "+"});
        test_valli.add(new Object[]{"gross", "blond", "braun", "-"});
        test_valli.add(new Object[]{"klein", "dunkel", "blau", "-"});
        test_valli.add(new Object[]{"gross", "dunkel", "blau", "-"});
        test_valli.add(new Object[]{"gross", "dunkel", "braun", "-"});
        test_valli.add(new Object[]{"klein", "blond", "braun", "-"});
        test_valli.add(new Object[]{"klein", "blond", "blau", "+"});
        test_valli.add(new Object[]{"gross", "rot", "blau", "+"});
        test_valli.add(new Object[]{"gross", "blond", "blau", "+"});
        test_valli.add(new Object[]{"gross", "blond", "braun", "-"});
        test_valli.add(new Object[]{"klein", "dunkel", "blau", "-"});
        test_valli.add(new Object[]{"gross", "dunkel", "blau", "-"});
        test_valli.add(new Object[]{"gross", "dunkel", "braun", "-"});
        test_valli.add(new Object[]{"klein", "blond", "braun", "-"});


        /// KMeans
        List<Object[]> KMeansDiscretizedData = convertToObjectList(parsedLinesSmall);

        /// EWD
        List<Object[]> EWDDiscretizedData = convertToObjectList(parsedLinesSmall);

        /// EFD
        List<Object[]> EFDDiscretizedData = convertToObjectList(parsedLinesSmall);




        /*
        /// KMeans Discretization
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 0);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 3);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 5);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 9);
         */

        /*
        /// EWD Discretization
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 0);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 3);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 5);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 9);
         */

        /*
        /// EFD Discretization
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 0);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 3);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 5);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 9);
         */

        /*
        ///  KMeansDiscretizedData Print
        System.out.println("KMeansDiscretizedData");
        System.out.println();

        for (Object[] line : KMeansDiscretizedData) {
            System.out.println(Arrays.toString(line));
        }
        System.out.println("-------------------------------------------------");
        */

        /*
        /// EWDDiscretizedData Print
        System.out.println("EWDDiscretizedData");
        System.out.println();

        for (Object[] line : EWDDiscretizedData) {
            System.out.println(Arrays.toString(line));
        }
        System.out.println("-------------------------------------------------");
        */

        /*
        /// EFDDiscretizedData Print
        System.out.println("EFDDiscretizedData");
        System.out.println();

        for (Object[] line : EFDDiscretizedData) {
            System.out.println(Arrays.toString(line));
        }
        System.out.println("-------------------------------------------------");
        */


        /// Baum machen mit KMeansDiscretizedData
        //DecisionTree dt = ID3Utils.createTree(KMeansDiscretizedData, LABEL_ATTR_INDEX);


        /// Baum machen mit EWDDiscretizedData
        //DecisionTree dt = ID3Utils.createTree(EWDDiscretizedData, LABEL_ATTR_INDEX);

        /// Baum machen mit EFDDiscretizedData
        //DecisionTree dt = ID3Utils.createTree(EFDDiscretizedData, LABEL_ATTR_INDEX);

        /// Baum machen mit test_valli
        DecisionTree dt = ID3Utils.createTree(test_valli, 3);





        try {
            XMLWriter.writeXML("target/classes/decision_tree.xml", dt);
            // XMLWriter.writeXML("target/classes/best_model.xml", bestModel);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }






    }

}
