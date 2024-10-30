package de.uni_trier.wi2.pki.util;

import javax.print.attribute.standard.MediaSize;
import java.util.Arrays;
import java.util.List;

/**
 * Collection of various helpers and debugging Methods.
 */
public class Helpers {

    /**
     * Prints the data parsed by the CSV reader to the console.
     *
     * @param parsedLines the parsed lines to print
     */
    public static void printData(List<Object[]> parsedLines) {
        for (Object[] line : parsedLines) {
            System.out.println(Arrays.toString(line));
        }
    }

    /**
     * Converts a list of string arrays to a list of object arrays.
     *
     * @param toConvert the list of string arrays to convert
     * @return the converted list of object arrays
     */
    public static List<Object[]> convertToObjectList(List<String[]> toConvert) {
        return toConvert.stream()
                .map(line -> Arrays.stream(line)
                        .map(s -> (Object) s)
                        .toArray())
                .toList();
    }
}
