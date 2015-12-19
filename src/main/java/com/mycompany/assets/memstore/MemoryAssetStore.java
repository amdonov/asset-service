package com.mycompany.assets.memstore;

import com.codahale.metrics.health.HealthCheck.Result;
import com.mycompany.assets.Asset;
import com.mycompany.assets.AssetStore;
import com.mycompany.assets.AssetStoreException;
import com.mycompany.assets.Note;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by amdonov on 12/16/15.
 */
public class MemoryAssetStore implements AssetStore {

    private final Map<String, Asset> assets = new ConcurrentHashMap<>();

    @Override
    public Asset getAsset(String uri) {
        return assets.get(uri);
    }

    @Override
    public void addAsset(Asset asset) throws AssetStoreException {
        final Asset existingAsset = assets.putIfAbsent(asset.getUri(), asset);
        if (existingAsset != null) {
            throw new AssetStoreException("Asset with that URI alredy exists", Response.Status.CONFLICT);
        }
    }

    @Override
    public void addNote(Note note) throws AssetStoreException {
        final Asset asset = assets.get(note.getUri());
        if (null == asset) {
            throw new AssetStoreException("Asset not found.", Response.Status.NOT_FOUND);
        }
        synchronized (asset) {
            asset.setModtime(new Date());
            asset.getNotes().add(note.getNote());
        }
    }

    @Override
    public void deleteAsset(String uri) throws AssetStoreException {
        final Asset asset = assets.remove(uri);
        if (null == asset) {
            throw new AssetStoreException("Asset not found.", Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Result checkHealth() throws Exception {
        if (assets.size() > 1000) {
            return Result.unhealthy("memory store has a high number of assets");
        }
        return Result.healthy();
    }

    // facilitates unit testing
    public void clear() {
        assets.clear();
    }
}
