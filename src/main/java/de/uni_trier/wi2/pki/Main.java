package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.uni_trier.wi2.pki.util.Helpers.convertToObjectList;
import static de.uni_trier.wi2.pki.util.Helpers.printData;

public class Main {

    public static void main(String[] args) {
        // some constants
        final String FILE_NAME = "housing_data.csv";
        final int LABEL_ATTR_INDEX = 13;
        final String delim = ",";

        //discretization params
        EqualFrequencyDiscretization efd = new EqualFrequencyDiscretization();
        EqualWidthDiscretization ewd = new EqualWidthDiscretization();
        KMeansDiscretizer kmd = new KMeansDiscretizer();
        int BINS = 4;
        int ATTRIBUTE_ID = 13;

        // parse CSV data
        List<String[]> parsedLines = null;
        try {
            parsedLines = CSVReader.readCsvToArray("target/classes/" + FILE_NAME, delim, true);
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
        attrIsContinuous.add(1, true);
        attrIsContinuous.add(2, true);
        attrIsContinuous.add(3, true);
        attrIsContinuous.add(4, true);
        attrIsContinuous.add(5, true);
        attrIsContinuous.add(6, true);
        attrIsContinuous.add(7, true);
        attrIsContinuous.add(8, true);
        attrIsContinuous.add(9, true);
        attrIsContinuous.add(10, true);
        attrIsContinuous.add(11, true);
        attrIsContinuous.add(12, true);
        attrIsContinuous.add(13, true);
        // Train model, evaluate model, write XML, ...

        List<Object[]> discretizedData = convertToObjectList(parsedLines);

        for (int i = 0; i < attrIsContinuous.size(); i++) {
            if (attrIsContinuous.get(i)) {
                ATTRIBUTE_ID = i;
                discretizedData = kmd.discretize(BINS, discretizedData, ATTRIBUTE_ID);
            }
        }

        //System.out.println("Discretized data: ");
        //printData(discretizedData);

        DecisionTree dt = ID3Utils.createTree(discretizedData, LABEL_ATTR_INDEX);

        System.out.println(dt.getSplits().toString());

        //System.out.printf("Decision Tree:" + dt);
    }

}
