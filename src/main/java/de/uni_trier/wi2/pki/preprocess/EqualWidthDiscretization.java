package de.uni_trier.wi2.pki.preprocess;

import javax.lang.model.element.Element;
import java.io.Console;
import java.util.*;

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
        List<Object[]> sortedExamples = sortExamplesViaAtribute(examples, attributeId);

        int binWidth = getbinWidth(sortedExamples, numberOfBins, attributeId);

        double min = getMin(sortedExamples, attributeId);

        for (int i = 1; i <= numberOfBins; i++) {

            double lowerBound = min + (i - 1) * binWidth;
            double upperBound = min + (binWidth * i);

            for (Object[] example : sortedExamples){
                double temp_number = Double.parseDouble(example[attributeId].toString());
                if (temp_number == min){
                    example[attributeId] = 1;
                    outputList.add(example);
                }
                else if (temp_number >= lowerBound && temp_number < upperBound){
                    example[attributeId] = i;
                    outputList.add(example);
                }
            }
        }

        return outputList;
    }

    //Minimum
    private static double getMin(List<Object[]> sortedExamples, int attributeId) {
        return Double.parseDouble((String) sortedExamples.get(0)[attributeId]);
    }

    //Maximum
    private static double getMax(List<Object[]> examples, int attributeId) {
        return Double.parseDouble((String) examples.get(examples.size() - 1)[attributeId]);
    }

    //calculate the bin width
    private static int getbinWidth(List<Object[]> sortedExamples, int numberOfBins, int attributeId) {

        double min = getMin(sortedExamples, attributeId);
        double max = getMax(sortedExamples, attributeId);
        return (int) ((max - min) / numberOfBins);
    }

    //sort list via attribute
    private static List<Object[]> sortExamplesViaAtribute(List<Object[]> examples, int attributeId) {
        return examples.stream()
                .sorted(Comparator.comparing(o -> Double.parseDouble(o[attributeId].toString())))
                .toList();
    }
}
