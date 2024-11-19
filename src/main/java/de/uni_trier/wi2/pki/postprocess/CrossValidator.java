package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Contains util methods for performing a cross-validation.
 */
public class CrossValidator {

    /**
     * Performs a cross-validation with the specified dataset and the function to train the model.
     *
     * @param dataset        the complete dataset to use.
     * @param labelAttribute the label attribute.
     * @param trainFunction  the function to train the model with.
     * @param numFolds       the number of data folds.
     */
    public static DecisionTree performCrossValidation(List<Object[]> dataset, int labelAttribute, BiFunction<List<Object[]>, Integer, DecisionTree> trainFunction,
                                                      int numFolds) {
        ArrayList<Object[]> data = new ArrayList<>(dataset);
        int foldSize = dataset.size() / numFolds;
        DecisionTree bestModel = null;
        double bestAccuracy = 0.0;

        /* Shuffle the dataset */
        Collections.shuffle(data);

        /* Split the dataset into training and validation sets */
        for (int i = 0; i < numFolds; i++) {
            List<Object[]> trainingSet = new ArrayList<>();
            List<Object[]> validationSet = new ArrayList<>();

            for (int j = 0; j < dataset.size(); j++) {
                if (j >= i * foldSize && j < (i + 1) * foldSize) {
                    validationSet.add(dataset.get(j));
                } else {
                    trainingSet.add(dataset.get(j));
                }
            }

            /* Train the model and evaluate it */
            DecisionTree model = trainFunction.apply(trainingSet, labelAttribute);
            double accuracy = evaluateModel(model, validationSet, labelAttribute);

            if (accuracy > bestAccuracy) {
                bestAccuracy = accuracy;
                bestModel = model;
            }
        }

        System.out.printf("Classification accuracy of best model: %.2f%%", bestAccuracy * 100);
        return bestModel;
    }

    /**
     * Evaluates the given model with the specified validation set.
     *
     * @param model          the model to evaluate.
     * @param validationSet  the validation set to use.
     * @param labelAttribute the label attribute.
     * @return the classification accuracy of the model.
     */
    private static double evaluateModel(DecisionTree model, List<Object[]> validationSet, int labelAttribute) {
        int correct = 0;

        /* Predict the class of each example and compare it to the actual class */
        for (Object[] instance : validationSet) {
            Object predicted = model.predict(instance);
            Object actual = instance[labelAttribute];

            if (predicted.equals(actual)) {
                correct++;
            }
        }
        return (double) correct / validationSet.size();
    }
}