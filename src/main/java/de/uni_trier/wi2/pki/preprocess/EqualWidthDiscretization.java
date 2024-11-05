package de.uni_trier.wi2.pki.preprocess;

import javax.lang.model.element.Element;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        int binWidth = getbinWidth(examples, numberOfBins, attributeId);
        double [] binCenters = getbinCenters(examples, numberOfBins, attributeId, binWidth);

        


        // Zuweisung jedes Wertes zum nächstgelegenen Bin-Center
        for (Object[] example : examples) {
            double temp_number = ((Number) example[attributeId]).doubleValue();
            double closestCenter = findClosestBinCenter(temp_number, binCenters);
            Object[] modifyedExample = example.clone();
            modifyedExample[attributeId] = closestCenter;
            outputList.add(modifyedExample);
        }
      
        return outputList;
    }

    //Minimum suchen
    public static double getMin(List<Object[]> examples, int attributeId) {
        double min = Double.MAX_VALUE;
        for (Object[] example : examples) {
            double value = ((Number) example[attributeId]).doubleValue();
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    //Maximum suchen
    public static double getMax(List<Object[]> examples, int attributeId) {
        double max = Double.MIN_VALUE;
        for (Object[] example : examples) {
            double value = ((Number) example[attributeId]).doubleValue();
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    //calculate the bin width
    public static int getbinWidth(List<Object[]> examples, int numberOfBins, int attributeId) {

        double min = getMin(examples, attributeId);
        double max = getMax(examples, attributeId);
        //System.out.println("in binWidth");
        return (int) ((max - min) / numberOfBins);
    }

    //binCenter berechnen
    public static double[] getbinCenters(List<Object[]> examples, int numberOfBins, int attributeId, int binWidth) {

        double min = getMin(examples, attributeId);
        double[] binCenters = new double[numberOfBins];
        for (int i = 0; i < numberOfBins; i++) {
            binCenters[i] = min + binWidth * (i + 0.5);
        }

        return binCenters;
    }

    //am nächten liegende binCenter finden
    public static double findClosestBinCenter(double temp_number, double[] binCenters) {
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
}
