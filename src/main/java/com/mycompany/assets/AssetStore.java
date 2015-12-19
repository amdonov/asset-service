package com.mycompany.assets;

import com.codahale.metrics.health.HealthCheck.Result;

/**
 * Created by amdonov on 12/16/15.
 */
public interface AssetStore {
    Asset getAsset(String uri);

    void addAsset(Asset asset) throws AssetStoreException;

    void addNote(Note note) throws AssetStoreException;

    void deleteAsset(String uri) throws AssetStoreException;

    Result checkHealth() throws Exception;
}
