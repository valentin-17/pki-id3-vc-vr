package de.uni_trier.wi2.pki.io;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Is used to read in CSV files.
 */
public class CSVReader {

    /**
     * Read a CSV file and return a list of string arrays.
     *
     * @param relativePath the path where the CSV file is located (has to be relative path!)
     * @param delimiter    the delimiter symbol which is used in the CSV
     * @param ignoreHeader A boolean that indicates whether to ignore the header line or not, i.e., whether to include the first line into the list or not
     * @return A list that contains string arrays. Each string array stands for one parsed line of the CSV file
     * @throws IOException if something goes wrong. Exception should be handled at the calling function.
     */

    // TODO: seperate na filter from reader
    public static List<String[]> readCsvToArray(String relativePath, String delimiter, boolean ignoreHeader) throws IOException {
        File inputFile = new File(relativePath);
        String line = null;
        List<String[]> parsedLines = new ArrayList<>() {};

        // read the file line by line and split each line at the character denoted by delimiter
        try (BufferedReader data = new BufferedReader(new FileReader(inputFile))) {

            // omit the first line if ignoreHeader is true
            if (ignoreHeader) {data.readLine();}

            while((line=data.readLine())!=null) {
                // skip the line if it contains NA values
                if (containsNaValue(line.split(delimiter))) {
                    continue;
                }
                parsedLines.add(line.split(delimiter));
            }
        } catch (FileNotFoundException fne) {
            fne.getStackTrace();
        }

        return parsedLines;
    }

    /**
     * Read the header of a CSV file and return it as a string array.
     *
     * @param relativePath the path where the CSV file is located (has to be relative path!)
     * @param delimiter    the delimiter symbol which is used in the CSV
     * @return A string array that contains the header of the CSV file
     * @throws IOException if something goes wrong. Exception should be handled at the calling function.
     */
    public static String[] readCsvHeader(String relativePath, String delimiter) throws IOException {
        File inputFile = new File(relativePath);
        String line = null;
        String[] header = null;

        // read first header line by line and split each line at the character denoted by delimiter
        try (BufferedReader data = new BufferedReader(new FileReader(inputFile))) {
            line = data.readLine();
            header = line.split(delimiter);
        } catch (FileNotFoundException fne) {
            fne.getStackTrace();
        }
        return header;
    }

    /**
     * Check if a line contains NA values.
     *
     * @param line the line to check
     * @return true if the line contains NA values, false otherwise
     */
    private static boolean containsNaValue(String[] line) {

        String[] naValues = {"na", "n/a", "nan", "null", "nil", "none", "n.a.", "n.a", "n_a", ""};

        return Arrays.stream(line)
                .anyMatch(s -> (Arrays.asList(naValues).contains(s.toLowerCase())));
    }
}
