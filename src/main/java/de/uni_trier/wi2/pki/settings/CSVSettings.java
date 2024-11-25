package de.uni_trier.wi2.pki.settings;

/**
 * Settings for the CSV reader.
 */
public class CSVSettings extends Settings {
    private String fileName;
    private int labelAttributeIndex;
    private String delimiter;

    /* Default settings */
    public CSVSettings() {
        resetToDefault();
    }

    /* Getters and Setters */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLabelAttributeIndex() {
        return labelAttributeIndex;
    }

    public void setLabelAttributeIndex(int labelAttributeIndex) {
        this.labelAttributeIndex = labelAttributeIndex;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public void resetToDefault() {
        this.fileName = "churn_data.csv";
        this.labelAttributeIndex = 0;
        this.delimiter = ";";
    }
}
