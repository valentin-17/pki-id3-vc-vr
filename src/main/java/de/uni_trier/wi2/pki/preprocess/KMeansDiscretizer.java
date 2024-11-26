package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.Main;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static de.uni_trier.wi2.pki.Main.id3Settings;

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
        ArrayList<Object[]> examplesList = new ArrayList<>(examples);
        double[] values = parseNumericValues(examplesList, attributeId);
        double[] centroids = initializeCentroids(values, numberOfBins);

        int[] clusters = runKMeansClustering(values, centroids, numberOfBins);

        String [] binNames = createBinNames(numberOfBins, values, clusters, attributeId);

        printBinNames(binNames);

        return assignBinNamesToExamples(examplesList, clusters, binNames, attributeId);
    }

    /**
     * Assigns the bin names to the examples.
     *
     * @param examples The examples to assign the bin names to.
     * @param clusters The cluster assignments.
     * @param binNames The bin names.
     * @param attributeId The ID of the attribute to assign the bin names to.
     * @return The examples with the bin names assigned.
     */
    private List<Object[]> assignBinNamesToExamples(List<Object[]> examples, int[] clusters, String[] binNames, int attributeId) {
        return examples.stream().peek(e -> {
            int clusterIndex = clusters[examples.indexOf(e)];
            e[attributeId] = binNames[clusterIndex];
        }).toList();
    }


    /**
     * Parses the numeric values from the examples.
     *
     * @param examples    The examples to parse.
     * @param attributeId The ID of the attribute to parse.
     * @return An array of parsed numeric values.
     */
    private double[] parseNumericValues(List<Object[]> examples, int attributeId) {
        try {
            return examples.stream()
                    .mapToDouble(e -> Double.parseDouble((String) e[attributeId]))
                    .toArray();
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Attribute must be numeric! " + nfe.getMessage());
        }
    }

    /**
     * Assigns each value to the nearest centroid.
     *
     * @param values    The values to cluster.
     * @param centroids The current centroids.
     * @return An array of cluster assignments.
     */
    private int[] runKMeansClustering(double[] values, double[] centroids, int numberOfBins) {
        int[] clusters = new int[values.length];
        double qualityOld = 0;
        double qualityNew = Double.MAX_VALUE;
        final double epsilon = id3Settings.getEpsilon();

        while (Math.abs(qualityOld - qualityNew) >= epsilon) {
            clusters = assignClusters(values, centroids);
            centroids = recalculateCentroids(values, clusters, numberOfBins);

            qualityOld = qualityNew;
            qualityNew = calculateQuality(values, centroids, clusters);
        }

        return clusters;
    }

    /**
     * Calculates the mean of all values assigned to a cluster.
     *
     * @param values The original values.
     * @param clusters The current cluster assignments.
     * @param clusters The index of the cluster to calculate.
     * @return The mean of all values assigned to the given cluster.
     */
    private double[] recalculateCentroids(double[] values, int[] clusters, int numberOfBins) {
        return IntStream.range(0, numberOfBins)
                .mapToDouble(cluster -> calculateClusterMean(values, clusters, cluster))
                .toArray();
    }

    /**
     * Calculates the mean of all values assigned to a cluster.
     *
     * @param values The original values.
     * @param clusters The current cluster assignments.
     * @param clusterIndex The index of the cluster to calculate.
     * @return The mean of all values assigned to the given cluster.
     */
    private double calculateClusterMean(double[] values, int[] clusters, int clusterIndex) {
        double clusterSum = calculateClusterSum(values, clusters, clusterIndex);
        long clusterSize = Arrays.stream(clusters).filter(c -> c == clusterIndex).count();
        return clusterSize == 0 ? 0 : clusterSum / clusterSize;
    }

    /**
     * Assigns each value to the nearest centroid.
     *
     * @param values The values to cluster.
     * @param centroids The current centroids.
     * @return An array of cluster assignments.
     */
    private int[] assignClusters(double[] values, double[] centroids) {
        return Arrays.stream(values).mapToInt(value -> findNearestCentroid(value, centroids))
                .toArray();
    }

    /**
     * Creates bin names based on the cluster assignments.
     *
     * @param numberOfBins The number of bins.
     * @param values       The original values.
     * @param clusters     The current cluster assignments.
     * @param attributeId  The ID of the name giving attribute.
     * @return An array of bin names.
     */
    private String[] createBinNames(int numberOfBins, double[] values, int[] clusters, int attributeId) {
        double[] minValues = new double[numberOfBins];
        double[] maxValues = new double[numberOfBins];
        Arrays.fill(minValues, Double.POSITIVE_INFINITY);
        Arrays.fill(maxValues, Double.NEGATIVE_INFINITY);

        updateMinMaxValues(values, clusters, minValues, maxValues);

        return IntStream.range(0, numberOfBins)
                .mapToObj(i -> formatBinName(Main.HEADER[attributeId], minValues[i], maxValues[i]))
                .toArray(String[]::new);
    }

    /**
     * Initializes the centroids for the K-means algorithm.
     *
     * @param values The values to be clustered.
     * @param numberOfBins The number of clusters (centroids).
     * @return An array of initial centroids.
     */
    private double[] initializeCentroids(double[] values, int numberOfBins) {
        List<Double> shuffledValues = DoubleStream.of(values).boxed().collect(Collectors.toList());
        Collections.shuffle(shuffledValues);
        return IntStream.range(0, numberOfBins)
                .mapToDouble(shuffledValues::get)
                .toArray();
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
        /* create a int range for all values */
        return IntStream.range(0, values.length)
                /* filter for examples assigned to clusterIndex */
                .filter(c -> clusters[c] == clusterIndex)
                /* extract and sum the values from value array */
                .mapToDouble(v -> values[v])
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

        /* create a int range for all values */
        return IntStream.range(0, values.length)
                /* filter for examples assigned to clusterIndex */
                .filter(c -> clusters[c] == clusterIndex)
                /* calculate the distance of each value to the centroid */
                .mapToDouble(v -> Math.abs(values[v] - centroids[clusterIndex]))
                .map(d -> Math.pow(d, 2))
                /* sum up all distances */
                .sum();
    }

    /**
     * Updates the min and max values for each cluster
     *
     * @param values The values to update the min and max values for
     * @param clusters The current cluster assignments
     * @param minValues The current minimum values for each cluster
     * @param maxValues The current maximum values for each cluster
     */
    private void updateMinMaxValues(double[] values, int[] clusters, double[] minValues, double[] maxValues) {
        IntStream.range(0, values.length).forEach(i -> {
            int cluster = clusters[i];
            double value = values[i];
            minValues[cluster] = Math.min(minValues[cluster], value);
            maxValues[cluster] = Math.max(maxValues[cluster], value);
        });
    }

    /**
     * Format the Bin names
     *
     * @param attributeName The name of the attribute
     * @param minValue The minimum value of the bin
     * @param maxValue The maximum value of the bin
     * @return The formatted bin name
     */
    private String formatBinName(String attributeName, double minValue, double maxValue) {
        if (minValue == Double.POSITIVE_INFINITY && maxValue == Double.NEGATIVE_INFINITY) {
            return String.format("%s: [empty]", attributeName);
        }
        return String.format("%s: [%s; %s]", attributeName, minValue, maxValue);
    }

    /**
     * Pints the bin names
     *
     * @param binNames The bin names to print
     */
    private void printBinNames(String[] binNames) {
        System.out.println("Final bins with their bounds:");
        for (String binName : binNames) {
            System.out.println(binName);
        }
        System.out.println();
    }
}
