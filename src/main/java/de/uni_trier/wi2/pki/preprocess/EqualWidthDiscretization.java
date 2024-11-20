package de.uni_trier.wi2.pki.preprocess;

import javax.lang.model.element.Element;
import java.io.Console;
import java.util.*;
import de.uni_trier.wi2.pki.Main;


/**
 * Class that holds logic for discretizing values.
 */
public class EqualWidthDiscretization extends BinningDiscretizer {

    /**
     * Discretizes a collection of examples according to the number of bins and the respective attribute ID.
     *
     * @param numberOfBins Specifies the number of numeric ranges that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */

    public List<Object[]> discretize(int numberOfBins, List<Object[]> examples, int attributeId) {
        List<Object[]> outputList = new ArrayList<>();
        List<Object[]> sortedExamples = sortExamplesViaAttribute(examples, attributeId);

        int binWidth = getBinWidth(sortedExamples, numberOfBins, attributeId);
        double min = getMin(sortedExamples, attributeId);

        // Create bin names
        String[] binNames = createBinNames(min, binWidth, numberOfBins, attributeId);

        //print sortedExamples
        for (Object[] example : sortedExamples) {
            System.out.println(Arrays.toString(example));
        }

        for (int i = 1; i <= numberOfBins; i++) {
            double lowerBound = min + (i - 1) * binWidth;
            double upperBound = min + (binWidth * i);

            for (Object[] example : sortedExamples) {
                double tempNumber = Double.parseDouble(example[attributeId].toString());
                if (tempNumber == min) {
                    example[attributeId] = 1;
                    outputList.add(example);
                } else if (tempNumber >= lowerBound && tempNumber < upperBound) {
                    example[attributeId] = i;
                    outputList.add(example);
                } else if (tempNumber >= lowerBound && tempNumber >= upperBound && i == numberOfBins) {
                    example[attributeId] = numberOfBins;
                    outputList.add(example);
                }
            }
        }

        return outputList;
    }

    // Minimum
    private double getMin(List<Object[]> sortedExamples, int attributeId) {
        return Double.parseDouble((String) sortedExamples.get(0)[attributeId]);
    }

    // Maximum
    private double getMax(List<Object[]> examples, int attributeId) {
        return Double.parseDouble((String) examples.get(examples.size() - 1)[attributeId]);
    }

    // Calculate the bin width
    private int getBinWidth(List<Object[]> sortedExamples, int numberOfBins, int attributeId) {
        double min = getMin(sortedExamples, attributeId);
        double max = getMax(sortedExamples, attributeId);
        return (int) ((max - min) / numberOfBins);
    }

    // Sort list via attribute
    private List<Object[]> sortExamplesViaAttribute(List<Object[]> examples, int attributeId) {
        return examples.stream()
                .sorted(Comparator.comparing(o -> Double.parseDouble(o[attributeId].toString())))
                .toList();
    }

    /**
     * Creates the names for the bins using lower and upper bounds.
     *
     * @param min         The minimum value of the attribute.
     * @param binWidth    The width of each bin.
     * @param numberOfBins The number of bins.
     * @param attributeId The ID of the attribute to discretize.
     * @return An array of bin names.
     */
    private String[] createBinNames(double min, int binWidth, int numberOfBins, int attributeId) {
        String[] binNames = new String[numberOfBins];

        String attributeName = Main.HEADER[attributeId];

        for (int i = 0; i < numberOfBins; i++) {
            double lowerBound = min + i * binWidth;
            double upperBound = min + (i + 1) * binWidth;

            // Include the attribute name in the bin name
            binNames[i] = String.format("%s: [%f, %f)", attributeName, lowerBound, upperBound);
        }

        return binNames;
    }
}
