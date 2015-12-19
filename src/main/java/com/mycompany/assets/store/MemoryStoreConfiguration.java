package com.mycompany.assets.store;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

/**
 * Created by amdonov on 12/19/15.
 */
public class MemoryStoreConfiguration {
    @Min(1)
    @JsonProperty
    private int resultsPerPage = 100;

    @JsonProperty
    private int warningAssetCount = 1000;

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public int getWarningAssetCount() {
        return warningAssetCount;
    }

    public void setWarningAssetCount(int warningAssetCount) {
        this.warningAssetCount = warningAssetCount;
    }
}
