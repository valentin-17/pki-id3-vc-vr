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
        double [] binCenters = getbinCenters(sortedExamples, numberOfBins, attributeId, binWidth);


        // Zuweisung jedes Wertes zum nächstgelegenen Bin-Center
        for (Object[] example : sortedExamples) {
            double temp_number = ((Number) example[attributeId]).doubleValue();
            double closestCenter = findClosestBinCenter(temp_number, binCenters);
            Object[] modifyedExample = example.clone();
            modifyedExample[attributeId] = closestCenter;
            outputList.add(modifyedExample);
        }
      
        return outputList;
    }

    //Minimum
    private static double getMin(List<Object[]> sortedExamples, int attributeId) {
        return ((Number) sortedExamples.get(0)[attributeId]).doubleValue();
    }

    //Maximum
    private static double getMax(List<Object[]> examples, int attributeId) {
        return ((Number) examples.get(examples.size() - 1)[attributeId]).doubleValue();
    }

    //calculate the bin width
    private static int getbinWidth(List<Object[]> sortedExamples, int numberOfBins, int attributeId) {

        double min = getMin(sortedExamples, attributeId);
        double max = getMax(sortedExamples, attributeId);
        return (int) ((max - min) / numberOfBins);
    }

    //binCenter berechnen
    private static double[] getbinCenters(List<Object[]> examples, int numberOfBins, int attributeId, int binWidth) {

        double min = getMin(examples, attributeId);
        double[] binCenters = new double[numberOfBins];
        for (int i = 0; i < numberOfBins; i++) {
            binCenters[i] = min + binWidth * (i + 0.5);
        }

        return binCenters;
    }

    //am nächten liegende binCenter finden
    private static double findClosestBinCenter(double temp_number, double[] binCenters) {
        double closestCenter = binCenters[0];
        double minDistance = Math.abs(temp_number - binCenters[0]);

        for (double center : binCenters) {
            double distance = Math.abs(temp_number - center);
            if (distance < minDistance) {
                minDistance = distance;
                closestCenter = center;
            }
        }

        return closestCenter;
    }

    //aort list via attribute
    private static List<Object[]> sortExamplesViaAtribute(List<Object[]> examples, int attributeId) {
        List<Object[]> sortedExamples = examples.stream()
                .sorted(Comparator.comparing(o -> Double.parseDouble(o[attributeId].toString())))
                .toList();
        return sortedExamples;
    }
}
