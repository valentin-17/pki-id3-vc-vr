package de.uni_trier.wi2.pki.preprocess;

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
        String[] binNames = createBinNames(min, binWidth, numberOfBins, attributeId);

        //Assign each example to the apprpriate bin
        for (Object[] example : sortedExamples) {
            double value = Double.parseDouble(example[attributeId].toString());
            String binName = getBinNameForValue(value, min, binWidth, binNames);
            example[attributeId] = binName;
            outputList.add(example);
        }
        System.out.println("Successfully discretized data into " + numberOfBins + " bins: " + Arrays.toString(binNames));
        return outputList;
    }

    /**
     * Creates and Format  the names for the bins.
     */
    private String[] createBinNames(double min, int binWidth, int numberOfBins, int attributeId) {
        String[] binNames = new String[numberOfBins];
        String attributeName = Main.HEADER[attributeId];

        for (int i = 0; i < numberOfBins; i++) {
            double lowerBound = min + i * binWidth;
            double upperBound = min + (i + 1) * binWidth;
            binNames[i] = String.format("%s: [%f, %f)", attributeName, lowerBound, upperBound);
        }
        return binNames;
    }

    /**
     * Get the bin name for a given value
     */
    private String getBinNameForValue(double value, double min, int binWidth, String[] binNames) {
        int binIndex = (int) Math.min((value - min) / binWidth, binNames.length - 1);
        return binNames[binIndex];
    }

    /**
     * Calculate the bin width
     */
    private int getBinWidth(List<Object[]> sortedExamples, int numberOfBins, int attributeId) {
        double min = getMin(sortedExamples, attributeId);
        double max = getMax(sortedExamples, attributeId);
        return (int) ((max - min) / numberOfBins);
    }

    /**
     * Minimum
     */
    private double getMin(List<Object[]> sortedExamples, int attributeId) {
        return Double.parseDouble((String) sortedExamples.get(0)[attributeId]);
    }

    /**
     * Maximum
     */
    private double getMax(List<Object[]> examples, int attributeId) {
        return Double.parseDouble((String) examples.get(examples.size() - 1)[attributeId]);
    }

    /**
     * Sort list via attribute
     */
    private List<Object[]> sortExamplesViaAttribute(List<Object[]> examples, int attributeId) {
        return examples.stream()
                .sorted(Comparator.comparing(o -> Double.parseDouble(o[attributeId].toString())))
                .toList();
    }
}


