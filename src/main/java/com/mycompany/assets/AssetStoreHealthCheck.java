package com.mycompany.assets;

import com.codahale.metrics.health.HealthCheck;

/**
 * Created by amdonov on 12/18/15.
 */
public class AssetStoreHealthCheck extends HealthCheck {
    private final AssetStore mStore;

    public AssetStoreHealthCheck(AssetStore store) {
     mStore = store;
    }

    @Override
    protected Result check() throws Exception {
        return mStore.checkHealth();
    }
}
