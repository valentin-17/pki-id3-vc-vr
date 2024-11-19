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
        // some constants
        final String FILE_NAME = "churn_data.csv";
        final int LABEL_ATTR_INDEX = 0;
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
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 4);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 5);
        KMeansDiscretizedData = kmd.discretize(BINS, KMeansDiscretizedData, 9);

        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 0);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 3);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 5);
        EFDDiscretizedData = efd.discretize(BINS, EFDDiscretizedData, 9);

        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 0);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 3);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 4);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 5);
        EWDDiscretizedData = ewd.discretize(BINS, EWDDiscretizedData, 9);

        List<Object[]> KMeansDiscretizedDataSmall = KMeansDiscretizedData.subList(0, 100);
        List<Object[]> EFDDiscretizedDataSmall = EFDDiscretizedData.subList(0, 50);

        DecisionTree dt = ID3Utils.createTree(EFDDiscretizedDataSmall, LABEL_ATTR_INDEX);
        //DecisionTree bestModel = CrossValidator.performCrossValidation(EFDDiscretizedDataSmall, LABEL_ATTR_INDEX, ID3Utils::createTree, 5);

        try {
            XMLWriter.writeXML("target/classes/decision_tree.xml", dt);
            // XMLWriter.writeXML("target/classes/best_model.xml", bestModel);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
