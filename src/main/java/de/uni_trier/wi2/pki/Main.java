package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
import de.uni_trier.wi2.pki.postprocess.ReducedErrorPruner;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.ID3Utils;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static de.uni_trier.wi2.pki.util.Helpers.convertToObjectList;
import static de.uni_trier.wi2.pki.util.Helpers.printData;

public class Main {

    public static String[] HEADER = null;

    public static void main(String[] args) {

        final String FILE_NAME = "churn_data.csv";
        final int LABEL_ATTR_INDEX = 10;
        final String delim = ";";
        final int NUMBER_OF_FOLDS = 5;

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
        Collection<Object[]> test_data = new ArrayList<>();
        for (int i = 0; i < test_data_size; i++) {
            int randomIndex = (int) (Math.random() * size_of_data);
            test_data.add(parsedLines.get(randomIndex));
            parsedLines.remove(randomIndex);
            size_of_data--;
        }

        List<String[]>valli_test = new ArrayList<>();
        valli_test.add(new String[]{"klein" , "blond", "blau", "+"});
        valli_test.add(new String[]{"gross", "rot", "blau", "+"});
        valli_test.add(new String[]{"gross" , "blond", "blau", "+"});
        valli_test.add(new String[]{"gross" , "blond", "braun", "-"});
        valli_test.add(new String[]{"klein" , "dunkel", "blau", "-"});
        valli_test.add(new String[]{"gross" , "dunkel", "blau", "-"});
        valli_test.add(new String[]{"gross" , "dunkel", "braun", "-"});
        valli_test.add(new String[]{"klein" , "blond", "braun", "-"});

        Collection<Object[]>test_data1 = new ArrayList<>();
        test_data1.add(new String[]{"klein" , "blond", "blau", "+"});
        test_data1.add(new String[]{"gross", "rot", "blau", "+"});
        test_data1.add(new String[]{"gross" , "blond", "blau", "+"});
        test_data1.add(new String[]{"gross" , "blond", "braun", "-"});
        test_data1.add(new String[]{"klein" , "dunkel", "blau", "-"});
        test_data1.add(new String[]{"gross" , "dunkel", "blau", "-"});
        test_data1.add(new String[]{"gross" , "dunkel", "braun", "-"});
        test_data1.add(new String[]{"klein" , "blond", "braun", "-"});



        List<String[]> parsedLinesSmall = parsedLines.subList(0, 50);
        Collection<Object[]> test_da = new ArrayList<>();
        test_da.add(parsedLines.get(100));
        test_da.add(parsedLines.get(101));
        test_da.add(parsedLines.get(102));
        test_da.add(parsedLines.get(103));
        test_da.add(parsedLines.get(104));
        test_da.add(parsedLines.get(105));





        System.out.println("parsedLinesSmall: ");
        System.out.println();
        for (String[] line : parsedLinesSmall) {
            System.out.println(Arrays.toString(line));
        }
        System.out.println("-------------------------------------------------");


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


        /// EWD Discretization
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 0);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 3);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 5);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 9);


        /*
        /// EFD Discretization
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 0);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 3);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 5);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 9);
         */


        /// KMEans Print
        System.out.println("EWDDiscretizedData: ");
        System.out.println();

        for (Object[] line : EWDDiscretizedData) {
            System.out.println(Arrays.toString(line));
        }
        System.out.println("-------------------------------------------------");



        /// Baum machen mit KMeansDiscretizedData
        //DecisionTree dt = ID3Utils.createTree(KMeansDiscretizedData, LABEL_ATTR_INDEX);

        /// Baum machen mit EWDDiscretizedData
        DecisionTree dt = ID3Utils.createTree(EWDDiscretizedData, LABEL_ATTR_INDEX, 20);

        /// Baum machen mit EFDDiscretizedData
        //DecisionTree dt = ID3Utils.createTree(EFDDiscretizedData, LABEL_ATTR_INDEX);



        System.out.println();
        System.out.println("-------------------- Baum testen (vor Pruning) --------------------");
        System.out.println();

        try {
            XMLWriter.writeXML("target/classes/decision_tree_BP.xml", dt);
            // XMLWriter.writeXML("target/classes/best_model.xml", bestModel);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        DecisionTree bestModel_BP = CrossValidator.performCrossValidation(KMeansDiscretizedData, LABEL_ATTR_INDEX, ID3Utils::createTree, NUMBER_OF_FOLDS);
        System.out.println(" nach CrossValidation: 1");


        /// Baum prunen
        ReducedErrorPruner pruner = new ReducedErrorPruner();
        pruner.prune(dt, test_data, LABEL_ATTR_INDEX);

        try {
            XMLWriter.writeXML("target/classes/decision_tree_AP.xml", dt);
            // XMLWriter.writeXML("target/classes/best_model.xml", bestModel);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        DecisionTree bestModel_AP = CrossValidator.performCrossValidation(KMeansDiscretizedData, LABEL_ATTR_INDEX, ID3Utils::createTree, NUMBER_OF_FOLDS);
        System.out.println("nach CrossValidation: 2");


        /*
        /// Baum testen (nach Pruning)
        double prunedAccuracy = CrossValidator.evaluateModel(dt, new ArrayList<>(test_data), LABEL_ATTR_INDEX);
        System.out.println("Genauigkeit nach Pruning: " + prunedAccuracy);
         */

    }

}
