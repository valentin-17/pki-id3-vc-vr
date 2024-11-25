package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.Main;

import java.util.*;
import java.util.stream.Collectors;
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
        ArrayList<Object[]> examplesList = new ArrayList<>(examples);
        double[] values = parseNumericValues(examplesList, attributeId);
        double[] centroids = initializeCentroids(values, numberOfBins);

        int[] clusters = runKMeansClustering(values, centroids, numberOfBins);

        String[] binNames = createBinNames(numberOfBins, values, clusters, attributeId);

        printBinNames(binNames);

        return assignBinNamesToExamples(examplesList, clusters, binNames, attributeId);
    }

    /**
     * Assigns bin names to the examples based on the clustering results.
     */
    private List<Object[]> assignBinNamesToExamples(List<Object[]> examples, int[] clusters, String[] binNames, int attributeId) {
        return examples.stream().peek(e -> {
            int clusterIndex = clusters[examples.indexOf(e)];
            e[attributeId] = binNames[clusterIndex];
        }).toList();
    }

    /**
     * Parses the numeric values from the examples.
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
     * Prints the bin names.
     */
    private void printBinNames(String[] binNames) {
        System.out.println("Final bins with their bounds:");
        for (String binName : binNames) {
            System.out.println(binName);
        }
    }

    /**
     * Runs the K-means clustering algorithm.
     */
    private int[] runKMeansClustering(double[] values, double[] centroids, int numberOfBins) {
        int[] clusters = new int[values.length];
        double qualityOld = 0, qualityNew = Double.MAX_VALUE, epsilon = 0.05;

        while (Math.abs(qualityOld - qualityNew) >= epsilon) {
            clusters = assignClusters(values, centroids);
            centroids = recalculateCentroids(values, clusters, numberOfBins);

            qualityOld = qualityNew;
            qualityNew = calculateQuality(values, centroids, clusters);
        }

        return clusters;
    }

    /**
     * Assigns the values to the nearest centroids.
     */
    private int[] assignClusters(double[] values, double[] centroids) {
        return IntStream.range(0, values.length)
                .map(i -> findNearestCentroid(values[i], centroids))
                .toArray();
    }

    /**
     * Recalculates the centroids based on the current cluster assignments.
     */
    private double[] recalculateCentroids(double[] values, int[] clusters, int numberOfBins) {
        return IntStream.range(0, numberOfBins)
                .mapToDouble(cluster -> calculateClusterMean(values, clusters, cluster))
                .toArray();
    }

    /**
     * Calculates the mean of the values in the cluster.
     */
    private double calculateClusterMean(double[] values, int[] clusters, int clusterIndex) {
        double clusterSum = calculateClusterSum(values, clusters, clusterIndex);
        long clusterSize = Arrays.stream(clusters).filter(c -> c == clusterIndex).count();
        return clusterSize == 0 ? 0 : clusterSum / clusterSize;
    }

    /**
     * Initializes the centroids by shuffling the values and taking the first n values as centroids.
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
     */
    private int findNearestCentroid(double value, double[] centroids) {
        return IntStream.range(0, centroids.length)
                .boxed()
                .min(Comparator.comparingDouble(i -> Math.abs(value - centroids[i])))
                .orElseThrow();
    }

    /**
     * Calculates the quality of the clustering.
     */
    private double calculateQuality(double[] values, double[] centroids, int[] clusters) {
        return IntStream.range(0, centroids.length)
                .mapToDouble(cluster -> calculateClusterDistanceSum(values, centroids[cluster], clusters, cluster))
                .sum();
    }

    /**
     * Calculates the sum of the values in the cluster.
     */
    private double calculateClusterSum(double[] values, int[] clusters, int clusterIndex) {
        return IntStream.range(0, values.length)
                .filter(i -> clusters[i] == clusterIndex)
                .mapToDouble(i -> values[i])
                .sum();
    }

    /**
     * Calculates the sum of the squared distances of the values to the centroid.
     */
    private double calculateClusterDistanceSum(double[] values, double centroid, int[] clusters, int clusterIndex) {
        return IntStream.range(0, values.length)
                .filter(i -> clusters[i] == clusterIndex)
                .mapToDouble(i -> Math.pow(values[i] - centroid, 2))
                .sum();
    }

    /**
     * Creates the bin names based on the clustering results.
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
     * Updates the min and max values for each cluster.
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
     * Formats the bin name based on the min and max values.
     */
    private String formatBinName(String attributeName, double minValue, double maxValue) {
        if (minValue == Double.POSITIVE_INFINITY && maxValue == Double.NEGATIVE_INFINITY) {
            return String.format("%s: [empty]", attributeName);
        }
        return String.format("%s: [%s; %s]", attributeName, minValue, maxValue);
    }
}
