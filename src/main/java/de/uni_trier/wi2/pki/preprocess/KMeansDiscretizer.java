package de.uni_trier.wi2.pki.preprocess;

import org.apache.commons.math3.ml.clustering.CentroidCluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

/**
 * Class that holds logic for discretizing values using K-means clustering.
 */
public class KMeansDiscretizer extends BinningDiscretizer {

    /**
     * Discretizes a collection of examples according to the number of bins (clusters) and the respective attribute ID.
     *
     * @param numberOfBins Specifies the number of numeric clusters that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */
    public List<Object[]> discretize(int numberOfBins, List<Object[]> examples, int attributeId) {

        int quality_new = Integer.MAX_VALUE;
        int quality_old = 0;
        double epsilon = 0.001;


        while (quality_old - quality_new < epsilon) {

            // initialize centroids
            double[] values = examples.stream().mapToDouble(e -> (double) e[attributeId]).toArray();
            double[] centroids = initializeCentroids(values, numberOfBins);

            for (Object[] example : examples) {
                int nearestCentroid = findNearestCentroid((double) example[attributeId], centroids);
                centroids[nearestCentroid] = 0.0;            }
        }

        //tmp
        List<Object[]> result = null;
        return result;
    }


    /**
     * Initializes the centroids for the K-means algorithm.
     *
     * @param values The values to be clustered.
     * @param numberOfBins The number of clusters (centroids).
     * @return An array of initial centroids.
     */
    private double[] initializeCentroids(double[] values, int numberOfBins) {

        //tmp
        double[] centroids = null;
        return centroids;
    }

    /**
     * Finds the nearest centroid for a given value.
     *
     * @param value The value to cluster.
     * @param centroids The array of centroid values.
     * @return The index of the nearest centroid.
     */
    private int findNearestCentroid(double value, double[] centroids) {

        //tmp
        int nearestIndex = 0;
        return nearestIndex;
    }

    /**
     * Calculates new centroids based on current cluster assignments.
     *
     * @param values The original values.
     * @param clusters The current cluster assignments.
     * @param numberOfBins The number of clusters.
     * @return The recalculated centroids.
     */
    private double[] calculateNewCentroids(double[] values, int[] clusters, int numberOfBins) {

        //tmp
        double[] centroids = null;
        return centroids;
    }
}
