package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static de.uni_trier.wi2.pki.util.Helpers.printData;

/**
 * Class that holds logic for discretizing values.
 */
public class EqualFrequencyDiscretization extends BinningDiscretizer {

    /**
     * Discretizes a collection of examples according to the number of bins and the respective attribute ID.
     *
     * @param numberOfBins Specifies the number of numeric ranges that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */
    public List<Object[]> discretize(int numberOfBins, List<Object[]> examples, int attributeId) {
        int numberOfExamples = examples.size();
        int numberOfExamplesPerBin = numberOfExamples / numberOfBins;
        int overflow = numberOfExamples % numberOfBins;
        ArrayList<Object[]> examplesList = new ArrayList<>(examples);

        /* sort the given examples by the given attribute */
        List<Object[]> sortedExamples = examplesList.stream()
                .sorted(Comparator.comparing(o -> Double.parseDouble(o[attributeId].toString())))
                .toList();

        /* create bin names */
        String[] binNames = createBinNames(sortedExamples, attributeId, numberOfExamplesPerBin, numberOfBins, overflow);

        /* loop through the sorted examples and assign them to bins */
        int currentIndex = 0;
        for (int i = 0; i < numberOfBins; i++) {

            int endIndex = calculateEndIndex(examples, numberOfExamplesPerBin, overflow, currentIndex, attributeId);
            overflow--;

            for (int j = currentIndex; j < endIndex; j++) {
                sortedExamples.get(j)[attributeId] = binNames[i];
            }

            currentIndex = endIndex + 1;
        }

            System.out.println("Successfully discretized data into " + numberOfBins + " bins: " + Arrays.toString(binNames));
        return sortedExamples;
    }

    /**
     * Creates the names for the bins.
     *
     * @param examples               The list of examples to discretize.
     * @param attributeId            The ID of the attribute to discretize.
     * @param numberOfExamplesPerBin The number of examples per bin.
     * @param numberOfBins           The number of bins.
     * @return the array of bin names.
     */
    private String[] createBinNames(List<Object[]> examples, int attributeId, int numberOfExamplesPerBin, int numberOfBins, int overflow) {
        String[] binNames = new String[numberOfBins];
        int currentIndex = 0;

        /* loop through the examples and create bin names */
        for (int i = 0; i < numberOfBins; i++) {

            int endIndex = calculateEndIndex(examples, numberOfExamplesPerBin, overflow, currentIndex, attributeId);

            String startVal = examples.get(currentIndex)[attributeId].toString();
            String endVal = examples.get(endIndex)[attributeId].toString();
            binNames[i] = String.format("%s: [%s; %s]", Main.HEADER[attributeId], startVal, endVal);

            if (endIndex == examples.size() - 1) {
                currentIndex = endIndex;
            } else {
                currentIndex = endIndex + 1;
            }
        }

        return binNames;
    }

    /**
     * Calculates the end index of the bin.
     * If the current bin is the last bin, the end index is the last example in the list.
     *
     * @param examples               The list of examples to discretize.
     * @param numberOfExamplesPerBin The number of examples per bin.
     * @param overflow               The number of examples that could not be evenly distributed.
     * @param currentIndex           The current index in the list of examples.
     * @param attributeId            The ID of the attribute to discretize.
     * @return the end index of the bin.
     */
    private int calculateEndIndex (List<Object[]> examples, int numberOfExamplesPerBin, int overflow, int currentIndex, int attributeId) {
        /* calculate the size of the current bin by adding the number of examples per bin and the overflow */
        int binSize = numberOfExamplesPerBin + (overflow > 0 ? 1 : 0);
        int endIndex = currentIndex + binSize - 1;

        /* if end index exceeds the examples list then the end index is automatically the last index of examples because the rest of examples goes in the last bin */
        if (endIndex >= examples.size()) {
            return examples.size() - 1;
        }

        /* if the current bin is not the last bin and the next example has the same value as the current example, extend the bin */
        while (endIndex < examples.size() - 1 && examples.get(endIndex)[attributeId].equals(examples.get(endIndex + 1)[attributeId])) {
            endIndex++;
        }

        return endIndex;
    }
}
