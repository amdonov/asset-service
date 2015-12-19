package com.mycompany.assets.store;

import com.codahale.metrics.health.HealthCheck.Result;
import com.mycompany.assets.*;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by amdonov on 12/16/15.
 */
public class MemoryAssetStore implements AssetStore {

    private final Map<String, Asset> assets = new ConcurrentHashMap<>();
    final int RESULTS_PER_PAGE;
    final int WARNING_ASSET_COUNT;

    public MemoryAssetStore(MemoryStoreConfiguration config) {
        RESULTS_PER_PAGE = config.getResultsPerPage();
        WARNING_ASSET_COUNT = config.getWarningAssetCount();
    }

    @Override
    public Asset getAsset(String uri) {
        final Asset asset = assets.get(uri);
        if (null == asset) {
            return null;
        }
        // Don't return the asset directly. It contains notes that could throw a concurrent modification exception
        // if another thread is reading while a request comes in to add a note.
        final List<String> notes = new ArrayList<>();
        synchronized (asset) {
            notes.addAll(asset.getNotes());
        }
        return new Asset(asset.getUri(),asset.getName(),asset.getModtime(),notes);
    }

    @Override
    public void addAsset(Asset asset) {
        final Asset existingAsset = assets.putIfAbsent(asset.getUri(), asset);
        if (existingAsset != null) {
            throw new AssetStoreException("Asset with that URI alredy exists", Response.Status.CONFLICT);
        }
    }

    @Override
    public void addNote(Note note) {
        final Asset asset = assets.get(note.getUri());
        if (null == asset) {
            throw new AssetStoreException("Asset not found.", Response.Status.NOT_FOUND);
        }
        // Although the asset map is thread-safe the note collection isn't
        // Make sure we don't create problems for other threads
        synchronized (asset) {
            asset.setModtime(new Date());
            asset.getNotes().add(note.getNote());
        }
    }

    @Override
    public SearchResult search(String page) {
        final SearchResult result = new SearchResult();
        // Create summary collection from the assets
        // avoids marshalling the notes.
        final List<AssetSummary> summaries = assets.values().stream()
                .map((a) -> new AssetSummary(a.getUri(), a.getName()))
                .collect(Collectors.toList());
        int pageNum;
        if (page==null) {
            pageNum = 1;
        } else {
            try {
                pageNum = Integer.parseInt(page);
            } catch (NumberFormatException ex) {
                throw new AssetStoreException("page is not an integer", Response.Status.BAD_REQUEST);
            }
            if (pageNum<1) {
                throw new AssetStoreException("page is not an positive number", Response.Status.BAD_REQUEST);
            }
        }
        int fromIndex = (pageNum-1)*RESULTS_PER_PAGE;
        // High endpoint is exclusive don't need to subtract 1
        int toIndex = fromIndex + RESULTS_PER_PAGE;
        int size = summaries.size();
        if (fromIndex>size) {
            throw new AssetStoreException("page exceeds the number of available assets", Response.Status.BAD_REQUEST);
        }
        if (toIndex>size) {
            toIndex = size;
        } else {
            // there are more results
            result.setNextPage(Integer.toString(pageNum+1));
        }
        result.setAssets(summaries.subList(fromIndex,toIndex));
        return result;
    }

    @Override
    public void deleteAsset(String uri) {
        final Asset asset = assets.remove(uri);
        if (null == asset) {
            throw new AssetStoreException("Asset not found.", Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Result checkHealth() throws Exception {
        if (assets.size() > WARNING_ASSET_COUNT) {
            return Result.unhealthy("memory store has a high number of assets");
        }
        return Result.healthy();
    }

    // facilitates unit testing
    public void clear() {
        assets.clear();
    }
}
