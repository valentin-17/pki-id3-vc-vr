package de.uni_trier.wi2.pki.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains methods that help with computing the entropy.
 */
public class EntropyUtils {




    /**
     * Calculates the information gain for all attributes
     *
     * @param matrix     Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                   football, than labelIndex is 2
     * @return the information gain for each attribute
     */
    //muss besser
    public static List<Double> calcInformationGain(Collection<Object[]> matrix, int labelIndex) {

        List<Double> informationGains = new ArrayList<>();

        // Number of attributes in each row
        int numAttributes = matrix.iterator().next().length;

        // Calculate information gain for each attribute, except the target label attribute
        for (int attributeIndex = 0; attributeIndex < numAttributes; attributeIndex++) {
            if (attributeIndex != labelIndex) {
                double gain = calcInformationGainForAttribute(attributeIndex, matrix, labelIndex);
                informationGains.add(gain);
            } //else {
            //    informationGains.add(0.0);  // or add a null, depending on your requirements
            //}
        }
        //informationGains.remove(labelIndex);

        return informationGains;
    }

    /**
     * Calculates the information gain for the given attributes.
     *
     * @param attributeIndex the index of the attribute for which the entropy is to be calculated.
     * @param matrix         Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex     the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                       football, than labelIndex is 2
     * @return the information gain for a single attribute
     */
    //muss besser
    public static double calcInformationGainForAttribute(int attributeIndex, Collection<Object[]> matrix, int labelIndex) {
        // Gain(A) = calculateEntropy - calculateRestEntropyForAttribute
        // Step 1: Calculate the overall entropy of the dataset for the target label
        Map<Object, Long> labelCounts = matrix.stream()
                .map(row -> row[labelIndex])
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long[] counts = labelCounts.values().stream().mapToLong(Long::longValue).toArray();
        double totalEntropy = calculateEntropy(counts);

        // Step 2: Calculate the rest entropy for the attribute
        double restEntropy = calculateRestEntropyForAttribute(attributeIndex, matrix, labelIndex);

        // Step 3: Return information gain as total entropy - rest entropy
        return totalEntropy - restEntropy;
    }

    /**
     * Calculates the entropy for the given parts of the classes. e.g. counts = [3,5] returns entropy 0.954434002924965.
     *
     * @param counts the list containing the sum for each label / value
     * @return the entropy for the given array
     */
    public static double calculateEntropy(long[] counts) {

        //tmp H(E) = - (p/p+n) * log2(p/p+n) - (n/p+n) * log2(n/p+n) für einen bereich mit mehr klassifizierungen

        double entropy = 0;
        int size = Math.toIntExact(Arrays.stream(counts).sum());
        for (long count : counts) {
            entropy = entropy - ((double) count / size) * (Math.log((double) count / size) / Math.log(2));
        }

        return entropy;
    }

    /**
     * Compute the rest entropy for a single attribute.
     *
     * @param attributeIndex the index of the attribute for which the entropy is to be calculated.
     * @param matrix         Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex     the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                       football, than labelIndex is 2
     * @return the rest entropy for a single attribute
     */
    public static double calculateRestEntropyForAttribute(int attributeIndex, Collection<Object[]> matrix, int labelIndex) {

        //Zählen wie oft das label vorkommt
        Map<Object, Long> labelCounts = matrix.stream()
                .map(row -> row[attributeIndex])
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        //berechnen der einzelnen entropien für die attribute
        double[] entropies = new double[labelCounts.size()];
        for (int i = 0; i < labelCounts.size(); i++) {
            Object value = labelCounts.keySet().toArray()[i];
            entropies[i] = calculateEntropyForAttributeValue(attributeIndex, matrix, value, labelIndex);
        }

        //trennen der labels von dem count aus der Map in ein array
        long[] temp = new long[labelCounts.size()];
        int count = 0;
        for (long value : labelCounts.values()){
            temp[count] = value;
            count++;
        }

        //berechnen der Restentropie
        double restEntropy = 0;
        for (int i = 0; i < entropies.length; i++) {
            restEntropy = restEntropy + (((double) temp[i] /matrix.size())* entropies[i]);
        }

        return restEntropy;
    }

    /**
     * Computes the entropy for an attribute value.
     *
     * @param attributeIndex the index of the attribute for which the entropy is to be calculated.
     * @param matrix         Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param value          attribute value (z.B. 'huge', 'small')
     * @param labelIndex     the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                       football, than labelIndex is 2
     * @return the entropy an attribute value
     */
    public static double calculateEntropyForAttributeValue(int attributeIndex, Collection<Object[]> matrix, Object value, int labelIndex) {

        //Filtern der Matrix nach dem Attribut - subset erstellen
        List<Object[]> filtertMatrix = new ArrayList<>();
        for (Object[] row : matrix){
            if(row[attributeIndex].equals(value)){
                filtertMatrix.add(row);
            }
        }

        //Zählen wie oft das label im subset vorkommt
        Map<Object, Long> labelCounts = new HashMap<>();
        for (Object[] row : filtertMatrix){
            labelCounts.put(row[labelIndex], labelCounts.getOrDefault(row[labelIndex], 0L) + 1);
        }

        //Zählen wie oft das label im subset vorkommt
        long counts[] = new long[labelCounts.size()];
        int i = 0;
        for (long count : labelCounts.values()){
            counts[i++] = count;
        }

        return calculateEntropy(counts);
    }


}