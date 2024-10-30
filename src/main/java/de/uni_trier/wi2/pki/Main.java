package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static de.uni_trier.wi2.pki.util.Helpers.convertToObjectList;
import static de.uni_trier.wi2.pki.util.Helpers.printData;

public class Main {

    public static void main(String[] args) {
        // some constants
        final String FILE_NAME = "housing_data.csv";
        final int LABEL_ATTR_INDEX = 10;
        final String delim = ",";

        //discretization params
        EqualFrequencyDiscretization efd = new EqualFrequencyDiscretization();
        EqualWidthDiscretization ewd = new EqualWidthDiscretization();
        KMeansDiscretizer kmd = new KMeansDiscretizer();
        int BINS = 6;
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

        printData(convertToObjectList(parsedLines));
        System.out.println(parsedLines.size());

        List<Object[]> convertedInput = convertToObjectList(parsedLines);
        printData(efd.discretize(BINS, convertedInput, ATTRIBUTE_ID));

        /*
        // define data types of the dataset
        ArrayList<Boolean> attrIsContinuous = new ArrayList<>();
        attrIsContinuous.add(0, true);
        attrIsContinuous.add(1, false);
        attrIsContinuous.add(2, false);
        attrIsContinuous.add(3, true);
        attrIsContinuous.add(4, true);
        attrIsContinuous.add(5, true);
        attrIsContinuous.add(6, true);
        attrIsContinuous.add(7, false);
        attrIsContinuous.add(8, false);
        attrIsContinuous.add(9, true);
        attrIsContinuous.add(10, false);
        */
        // Train model, evaluate model, write XML, ...
    }

}
