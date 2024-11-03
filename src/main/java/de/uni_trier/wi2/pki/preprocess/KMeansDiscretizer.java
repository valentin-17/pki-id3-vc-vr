package de.uni_trier.wi2.pki.preprocess;

import java.util.*;
import java.util.stream.DoubleStream;

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

        double quality_new = Double.MAX_VALUE;
        double quality_old = 0;
        double epsilon = 0.01;
        int[] clusterAssignments = new int[examples.size()];

        // initialize centroids
        double[] attributeToCluster = examples.stream().mapToDouble(e -> (double) e[attributeId]).toArray();
        double[] centroids = initializeCentroids(attributeToCluster, numberOfBins);

        // clustering loop
        while (quality_old - quality_new < epsilon) {

            for (int i = 0; i < examples.size(); i++) {
                Object[] example = examples.get(i);
                int nearestCentroid = findNearestCentroid((double) example[attributeId], centroids);
                clusterAssignments[i] = nearestCentroid;
            }

            // Recalculate the centroids based on the current cluster assignments
            centroids = calculateNewCentroids(attributeToCluster, clusterAssignments, numberOfBins);

            // Update the quality measure
            quality_old = quality_new;
            quality_new = calculateQuality(attributeToCluster, centroids, clusterAssignments);
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

        ArrayList<Double> tmpValues = new ArrayList<>(DoubleStream.of(values).boxed().toList());
        double[] centroids = new double[numberOfBins];

        // shuffle the values to ensure random selection of initial centroids
        Collections.shuffle(tmpValues);

        // pick the first n (numberOfBins) values as initial centroids
        // because of random shuffling we can simply use the first n values
        for (int i = 0; i < numberOfBins; i++) {
            centroids[i] = tmpValues.get(i);
        }
        
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

        int index = 0;
        // TODO: review if Double.MAX_VALUE is a good choice for the initial value
        double minDistance = Double.MAX_VALUE;

        // Loop through all centroids and find the one with the smallest absolute distance to the value
        for (int i = 0; i < centroids.length; i++) {
            double distance = Math.abs(value - centroids[i]);
            if (distance < minDistance) {
                minDistance = distance;
                index = i;
            }
        }

        return index;
    }

    /**
     * Calculates new centroids based on current cluster assignments.
     *
     * @param values       The original values.
     * @param clusters     The current cluster assignments.
     * @param numberOfBins The number of clusters.
     * @return The recalculated centroids.
     */
    private double[] calculateNewCentroids(double[] values, int[] clusters, int numberOfBins) {

        //tmp
        double[] centroids = null;
        return centroids;
    }

    /**
     * Calculates the quality of the current clustering.
     *
     * @param attributeToCluster The values to cluster.
     * @param centroids          The current centroids.
     * @param clusterAssignments The current cluster assignments.
     * @return The quality of the clustering.
     */
    private double calculateQuality(double[] attributeToCluster, double[] centroids, int[] clusterAssignments) {

    }
}
