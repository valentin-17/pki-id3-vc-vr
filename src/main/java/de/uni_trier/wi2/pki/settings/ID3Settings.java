package de.uni_trier.wi2.pki.settings;

import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;

import static de.uni_trier.wi2.pki.Main.K_MEANS;

/**
 * Settings for the ID3 algorithm.
 */
public class ID3Settings extends Settings {
    private int bins;
    private int numFolds;
    private double epsilon;
    private BinningDiscretizer discretizingMethod;

    /* Default settings */
    public ID3Settings() {
        resetToDefault();
    }

    /* Getters and Setters */
    public int getBins() {
        return bins;
    }

    public void setBins(int bins) {
        this.bins = bins;
    }

    public int getNumFolds() {
        return numFolds;
    }

    public void setNumFolds(int numFolds) {
        this.numFolds = numFolds;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public BinningDiscretizer getDiscretizingMethod() {
        return discretizingMethod;
    }

    public void setDiscretizingMethod(BinningDiscretizer discretizingMethod) {
        this.discretizingMethod = discretizingMethod;
    }

    @Override
    public void resetToDefault() {
        this.bins = 5;
        this.numFolds = 5;
        this.epsilon = 0.1;
        this.discretizingMethod = K_MEANS;
    }
}