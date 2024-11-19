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
        ArrayList<Object[]> examplesList = new ArrayList<>(examples);

        /* sort the given examples by the given attribute */
        List<Object[]> sortedExamples = examplesList.stream()
                .sorted(Comparator.comparing(o -> Double.parseDouble(o[attributeId].toString())))
                .toList();

        /* create bin names */
        String[] binNames = createBinNames(sortedExamples, attributeId, numberOfExamplesPerBin, numberOfBins);

        /* loop through the sorted examples and assign them to bins */
        for (int i = 0; i < numberOfBins; i++) {
            int finalI = i;

            /* assign numberOfExamplesPerBin examples to the current bin */
            sortedExamples.stream()
                          .skip((long) i * numberOfExamplesPerBin)                                                   /* skip the first n examples from the previous bin */
                          .limit(calculateEndIndex(i, finalI, numberOfBins, numberOfExamplesPerBin, sortedExamples))    /* limit the stream to n examples */
                          .forEach(example -> example[attributeId] = binNames[finalI]);
        }

        System.out.println("Successfully discretized data into " + numberOfBins + " bins: " + Arrays.toString(binNames));
        return sortedExamples;
    }

    /**
     * Calculates the end index of the bin.
     * If the current bin is the last bin, the end index is the last example in the list.
     *
     * @param i                      The current bin index.
     * @param finalI                 The final bin index.
     * @param numberOfBins           The number of bins.
     * @param numberOfExamplesPerBin The number of examples per bin.
     * @param sortedExamples         The list of examples to discretize.
     * @return the end index of the bin.
     */
    private long calculateEndIndex(int i, int finalI, int numberOfBins, int numberOfExamplesPerBin, List<Object[]> sortedExamples) {
        return (i == numberOfBins - 1) ? sortedExamples.size() - 1 : (long) (finalI + 1) * numberOfExamplesPerBin - 1;
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
    private String[] createBinNames(List<Object[]> examples, int attributeId, int numberOfExamplesPerBin, int numberOfBins) {
        String[] binNames = new String[numberOfBins];

        for (int i = 0; i < numberOfBins; i++) {
            int startIdx = i * numberOfExamplesPerBin;                                                                  /* determine the index of the starting example */
            int endIdx = (i == numberOfBins - 1) ? examples.size() - 1                                     /* determine the index of the last example, */
                          : (i + 1) * numberOfExamplesPerBin - 1;                                                       /* if this is the last bin extend the range to include all examples remaining*/
            String startVal = examples.get(startIdx)[attributeId].toString();
            String endVal = examples.get(endIdx)[attributeId].toString();

            binNames[i] = String.format("%s: [%s; %s]", Main.HEADER[attributeId], startVal, endVal);
        }

        return binNames;
    }
}
