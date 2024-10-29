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

        if (examples == null || examples.isEmpty()) {
            throw new IllegalArgumentException("The list of examples is empty.");
        }

        List<Object[]> outputList = new ArrayList<>();


        int binIndex = (int) binWidth(minValue(examples, attributeId), maxValue(examples, attributeId), numberOfBins);

        //noch nicht fertig da fehlt noch woher weis du wann binIndex erreicht ist und du musst das ganz Object Array mit in die neue liste packen
        for (Object[] example : examples) {
            example[attributeId] = binIndex;
            outputList.add(example);
        }

        return outputList;
    }

    private double minValue(List<Object[]> examples, int attributeId) {
        double min = Double.MAX_VALUE;
        for (Object[] example : examples) {
            double value = (double) example[attributeId];
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private double maxValue(List<Object[]> examples, int attributeId) {
        double max = Double.MIN_VALUE;
        for (Object[] example : examples) {
            double value = (double) example[attributeId];
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private double binWidth(double min, double max, int numberOfBins) {
        return (max - min) / numberOfBins;
    }



}
