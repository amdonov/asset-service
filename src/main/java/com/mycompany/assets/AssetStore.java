package com.mycompany.assets;

import com.codahale.metrics.health.HealthCheck.Result;

import java.util.List;

/**
 * Created by amdonov on 12/16/15.
 */
public interface AssetStore {
    Asset getAsset(String uri);

    void addAsset(Asset asset);

    void addNote(Note note);

    List<AssetSummary> search();

    void deleteAsset(String uri);

    Result checkHealth() throws Exception;
}
