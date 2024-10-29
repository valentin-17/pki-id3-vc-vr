package de.uni_trier.wi2.pki.io;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
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
    public static List<String[]> readCsvToArray(String relativePath, String delimiter, boolean ignoreHeader) throws IOException {

        File inputFile = new File(relativePath);
        String line = null;
        List<String[]> parsedLines = new ArrayList<>() {};

        //debug
        System.out.println(relativePath);

        // checks if the file that is being used exists
        validateFile(inputFile);

        // read the file line by line and split each line at the character denoted by delimiter
        try (BufferedReader data = new BufferedReader(new FileReader(inputFile))) {

            // omit the first line if ignoreHeader is true
            if (ignoreHeader) {data.readLine();}

            while((line=data.readLine())!=null) {
                parsedLines.add(line.split(delimiter));
            }
        } catch (FileNotFoundException fne) {
            fne.getStackTrace();
        }

        return parsedLines;
    }

    private static void validateFile(File toTest) throws FileNotFoundException {
        if (!toTest.exists()) throw new FileNotFoundException("This File does not Exist");
    }

}
