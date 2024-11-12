package de.uni_trier.wi2.pki.preprocess;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

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
        String header = (String) examples.get(0)[attributeId];
        String[] binNames;
        ArrayList<Object[]> examplesWithoutHeader = new ArrayList<>(examples);
        double quality_new = Double.MAX_VALUE;
        double quality_old = 0;
        double epsilon = 0.01;
        int iterCounter = 0;
        double[] values = null;
        double[] centroids = null;

        /* remove header */
        examplesWithoutHeader.remove(0);

        int[] clusters = new int[examplesWithoutHeader.size()];

        /* check if attribute to discretize is numeric */
        try {
            values = examplesWithoutHeader.stream().mapToDouble(e -> Double.parseDouble((String) e[attributeId])).toArray();
            centroids = initializeCentroids(values, numberOfBins);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not initialize Centroids. Attribute must be numeric! " + nfe.getMessage());
        }

        /* check if values and centroids are initialized */
        assert values != null;
        assert centroids != null;

        /* clustering loop */
        while (Math.abs(quality_old - quality_new) >= epsilon) {
            iterCounter++;

            /* Assign each example to the nearest centroid */
            for (int i = 0; i < examplesWithoutHeader.size(); i++) {
                Object[] example = examplesWithoutHeader.get(i);
                int nearestCentroid = findNearestCentroid(Double.parseDouble((String) example[attributeId]), centroids);

                clusters[i] = nearestCentroid;
            }

            /* Recalculate the centroids based on the current cluster assignments */
            centroids = calculateNewCentroids(values, clusters, numberOfBins);

            /* Update the quality measure */
            quality_old = quality_new;
            quality_new = calculateQuality(values, centroids, clusters);
        }

        binNames = createBinNames(numberOfBins, values, clusters, header);

        /* Print the final bins */
        System.out.println("Final bins with their bounds:");
        for (String binName : binNames) {
            System.out.println(binName);
        }

        System.out.println("KMeans clustering completed after " + iterCounter + " iterations.");

        /* Assign bin names to examples */
        return examplesWithoutHeader.stream().peek(e -> {
            if (examples.indexOf(e) > 0) {
                e[attributeId] = binNames[clusters[examples.indexOf(e) - 1]];
            }
        }).toList();
    }

    /**
     * Creates bin names based on the cluster assignments.
     *
     * @param numberOfBins The number of bins.
     * @param values       The original values.
     * @param clusters     The current cluster assignments.
     * @param header       The header of the attribute to discretize.
     * @return An array of bin names.
     */
    private String[] createBinNames(int numberOfBins, double[] values, int[] clusters, String header) {
        String[] binNames = new String[numberOfBins];
        double[] minValues = new double[numberOfBins];
        double[] maxValues = new double[numberOfBins];
        Arrays.fill(minValues, Double.POSITIVE_INFINITY);
        Arrays.fill(maxValues, Double.NEGATIVE_INFINITY);

        /* Calculate the min and max values for each bin */
        for (int i = 0; i < clusters.length; i++) {
            int cluster = clusters[i];
            double value = values[i];
            if (value < minValues[cluster]) {
                minValues[cluster] = value;
            }
            if (value > maxValues[cluster]) {
                maxValues[cluster] = value;
            }
        }

        /* Create bin names */
        for (int i = 0; i < numberOfBins; i++) {
            double lowerBound = minValues[i];
            double upperBound = maxValues[i];
            binNames[i] = String.format("%s: [%s; %s]", header, lowerBound, upperBound);
        }

        return binNames;
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

        Collections.shuffle(tmpValues);                                                                                 /* shuffling the values ensures random selection of centroids */

        for (int i = 0; i < numberOfBins; i++) {                                                                        /* pick the first n (numberOfBins) values as initial centroids */
            centroids[i] = tmpValues.get(i);                                                                            /* because of random shuffling we can simply use the first n values */
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

        /* Loop through all centroids and find the one with the smallest absolute distance to the value */
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
        double[] centroids = new double[numberOfBins];

        /* Loop through all clusters and calculate the new centroid as the mean of all values in the cluster */
        for (int i = 0; i < numberOfBins; i++) {
            int clusterIndex = i;
            int clusterSize = (int) Arrays.stream(clusters).filter(c -> c == clusterIndex).count();                     /* count the examples assigned to clusterIndex */
            double clusterSum = calculateClusterSum(values, clusters, i);                                               /* calculate the sum of all values assigned to clusterIndex */

            centroids[i] = (double) 1 / clusterSize * clusterSum;                                                       /* apply the formula for new centroids */
        }

        return centroids;
    }

    /**
     * Calculates the quality of the current clustering.
     *
     * @param values             The values to cluster.
     * @param centroids          The current centroids.
     * @param clusters           The current cluster assignments.
     * @return The quality of the clustering.
     */
    private double calculateQuality(double[] values, double[] centroids, int[] clusters) {
        double quality = 0;

        for (int i = 0; i < centroids.length; i++) {
            quality += calculateDistanceSumToCentroid(values, centroids, clusters, i);
        }

        return quality;
    }

    /**
     * Calculates the sum of all values assigned to a cluster.
     *
     * @param values       The original values.
     * @param clusters     The current cluster assignments.
     * @param clusterIndex The index of the cluster to calculate.
     * @return The sum of all values assigned to the given cluster.
     */
    private double calculateClusterSum(double[] values, int[] clusters, int clusterIndex) {
        return IntStream.range(0, values.length)                                                                        /* create a int range for all values */
                .filter(c -> clusters[c] == clusterIndex)                                                               /* filter for examples assigned to clusterIndex */
                .mapToDouble(v -> values[v])                                                                            /* extract and sum the values from value array */
                .sum();
    }

    /**
     * Calculates the sum of all squared distances of values assigned to a cluster to the centroid of that cluster.
     *
     * @param values       The original values.
     * @param centroids    The current centroids.
     * @param clusters     The current cluster assignments.
     * @param clusterIndex The index of the cluster to calculate.
     * @return The sum of all squared distances of values assigned to the given cluster to the centroid of that cluster.
     */
    private double calculateDistanceSumToCentroid (double[] values, double[] centroids, int[] clusters, int clusterIndex) {
        return IntStream.range(0, values.length)                                                                        /* create a int range for all values */
                .filter(c -> clusters[c] == clusterIndex)                                                               /* filter for examples assigned to clusterIndex */
                .mapToDouble(v -> Math.abs(values[v] - centroids[clusterIndex]))                                        /* calculate the distance of each value to the centroid */
                .map(d -> Math.pow(d, 2))                                                                               /* square the distance */
                .sum();                                                                                                 /* sum up all distances */
    }
}
