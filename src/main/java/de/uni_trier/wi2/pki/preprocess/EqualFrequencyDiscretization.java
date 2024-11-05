package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.util.Helpers;

import java.util.Comparator;
import java.util.List;

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
        String binName = "";

        /* sort the given examples by the given attribute */
        List<Object[]> sortedExamples = examples.stream()
                           .sorted(Comparator.comparing(o -> Double.parseDouble(o[attributeId].toString()))).toList();

        System.out.println("Sorted List:");
        Helpers.printData(sortedExamples);

        /* loop through the sorted examples and assign them to bins */
        for (int i = 0; i < numberOfBins; i++) {
            int finalI = i;
            String[] binNames = createBinNames(examples, finalI, attributeId, numberOfExamplesPerBin, numberOfBins);

            /* assign numberOfExamplesPerBin examples to the current bin */
            sortedExamples.stream()
                          .skip((long) i * numberOfExamplesPerBin)                                                   /* skip the first n examples from the previous bin */
                          .limit(i == (numberOfBins - 1) ? examples.size() - 1 : numberOfExamplesPerBin)                /* leave the right border open if the last bin is reached */
                          .forEach(example -> example[attributeId] = binNames[finalI]);
        }

        return sortedExamples;
    }

    private String[] createBinNames(List<Object[]> examples, int finalI, int attributeId,
                                    int numberOfExamplesPerBin, int numberOfBins) {
        String[] binNames = new String[numberOfBins];
        int startIdx = finalI * numberOfExamplesPerBin;                                                                 /* determine the value of the starting example */
        int endIdx = finalI == (numberOfBins - 1)                                                                       /* determine the value of the last example, */
                               ? examples.size() - 1                                                                    /* if this is the last bin extend the range */
                               : numberOfExamplesPerBin;                                                                /* to include all examples remaining */
        String startVal = examples.get(startIdx)[attributeId].toString();
        String endVal = examples.get(endIdx)[attributeId].toString();

        for (int i = 0; i < numberOfBins; i++) {
            binNames[i] = ("Bin " + i + ": ").concat("[")                                                                             /* build the bin name */
                                             .concat(String.valueOf(startVal))
                                             .concat(", ")
                                             .concat(String.valueOf(endVal))
                                             .concat("]");;
        }

        return binNames;
    }
}
