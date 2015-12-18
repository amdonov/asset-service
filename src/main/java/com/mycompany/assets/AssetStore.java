package com.mycompany.assets;

/**
 * Created by amdonov on 12/16/15.
 */
public interface AssetStore {
    Asset getAsset(String uri);

    void addAsset(Asset asset) throws AssetStoreException;

    void deleteAsset(String uri) throws AssetStoreException;
}
