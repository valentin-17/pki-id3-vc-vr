package de.uni_trier.wi2.pki.preprocess;

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
    // TODO: trim data to fit the number of bins
    public List<Object[]> discretize(int numberOfBins, List<Object[]> examples, int attributeId) {

        int numberOfExamples = examples.size();
        int numberOfExamplesPerBin = numberOfExamples / numberOfBins;
        // TODO: remove magic number
        int binId = 1;

        // loop through the sorted examples and assign them to bins
        for (int i = 0; i < numberOfBins; i++) {

            int finalBinId = binId;

            // assign numberOfExamplesPerBin examples to the current bin
            examples.stream()
                    .skip((long) i * numberOfExamplesPerBin)
                    .limit(numberOfExamplesPerBin)
                    .forEach(example -> example[attributeId] = finalBinId);
            binId++;
        }

        return examples;
    }
}
