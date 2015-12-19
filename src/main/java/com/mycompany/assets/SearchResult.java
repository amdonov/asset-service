package com.mycompany.assets;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * Created by amdonov on 12/18/15.
 */
public class SearchResult {
    private List<AssetSummary> assets;
    private String nextPage;

    public List<AssetSummary> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetSummary> assets) {
        this.assets = assets;
    }

    @JsonPropertyDescription("use this value as page argument to retrieve the next page of results")
    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }
}
