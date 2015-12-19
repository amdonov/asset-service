package com.mycompany.assets.cqlstore;

import com.codahale.metrics.health.HealthCheck;
import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.PagingStateException;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.mycompany.assets.*;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by amdonov on 12/19/15.
 */
public class CassandraAssetStore implements AssetStore {

    private final Cluster cluster;
    private final Session session;
    private final PreparedStatement createAsset;
    private final PreparedStatement createAssetWithNotes;
    private final PreparedStatement deleteAsset;
    private final PreparedStatement getAsset;
    private final PreparedStatement addNote;
    final int RESULTS_PER_PAGE = 100;

    public CassandraAssetStore() {
        // Write and read from a majority of local data center hosts
        cluster = Cluster.builder().addContactPoint("127.0.0.1").
                withLoadBalancingPolicy(new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().
                        withUsedHostsPerRemoteDc(2).withLocalDc("datacenter1").build())).
                withQueryOptions(new QueryOptions().
                        setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM)).build();
        session = cluster.connect();
        String query = "CREATE KEYSPACE IF NOT EXISTS Assets WITH REPLICATION "
                + "= {'class':'NetworkTopologyStrategy', 'datacenter1':2};";
        session.execute(query);
        //using the KeySpace
        session.execute("USE Assets");
        query = "CREATE TABLE IF NOT EXISTS assets (  uri text PRIMARY KEY, name text,  modtime timestamp,  notes list<text> );";
        session.execute(query);
        // Don't let adding a note create an asset
        addNote = session.prepare("UPDATE assets SET modtime = ?, notes = notes +  ?  where uri = ? IF EXISTS");
        getAsset = session.prepare("select uri, name, modtime, notes from assets where uri=?");
        deleteAsset = session.prepare("delete from assets where uri=? if exists");
        createAsset = session.prepare(
                "INSERT INTO assets (uri,name,modtime)" +
                        "VALUES (?, ?, ?) " +
                        "IF NOT EXISTS");
        createAssetWithNotes = session.prepare(
                "INSERT INTO assets (uri,name,modtime,notes)" +
                        "VALUES (?, ?, ?, ?) " +
                        "IF NOT EXISTS");
    }

    @Override
    public Asset getAsset(String uri) {
        ResultSet rs = session.execute(getAsset.bind(uri));
        Row row = rs.one();
        if (null == row) {
            return null;
        }
        return new Asset(row.getString("uri"), row.getString("name"), row.getDate("modtime"), row.getList("notes", String.class));
    }

    @Override
    public void addAsset(Asset asset) {
        Statement stmt;
        // Per Cassandra best practices, avoid inserting empty notes column
        if (asset.getNotes().isEmpty()) {
            stmt = createAsset.bind(
                    asset.getUri(),
                    asset.getName(),
                    asset.getModtime());
        } else {
            stmt = createAssetWithNotes.bind(
                    asset.getUri(),
                    asset.getName(),
                    asset.getModtime(),
                    asset.getNotes());
        }
        ResultSet rs = session.execute(stmt);
        if (!rs.wasApplied()) {
            throw new AssetStoreException("Asset with that URI alredy exists", Response.Status.CONFLICT);
        }
    }

    @Override
    public void addNote(Note note) {
        Statement stmt = addNote.bind(new Date(), Arrays.asList(note.getNote()), note.getUri());
        ResultSet rs = session.execute(stmt);
        if (!rs.wasApplied()) {
            throw new AssetStoreException("Asset not found.", Response.Status.NOT_FOUND);
        }
    }

    @Override
    public SearchResult search(String page) {
        try {
            final SearchResult result = new SearchResult();
            final List<AssetSummary> assets = new ArrayList<>();
            final Statement stmt = new SimpleStatement("select uri, name from assets");
            stmt.setFetchSize(RESULTS_PER_PAGE);
            if (page != null) {
                result.setPreviousPage(page);
                stmt.setPagingState(
                        PagingState.fromString(page));
            }

            ResultSet rs = session.execute(stmt);
            PagingState nextPage = rs.getExecutionInfo().getPagingState();

            // Note that we don't rely on RESULTS_PER_PAGE, since Cassandra might
            // have not respected it, or we might be at the end of the result set
            int remaining = rs.getAvailableWithoutFetching();
            for (Row row : rs) {
                assets.add(new AssetSummary(row.getString("uri"), row.getString("name")));
                if (--remaining == 0) {
                    break;
                }
            }
            result.setAssets(assets);

            // This will be null if there are no more pages
            if (nextPage != null) {
                result.setNextPage(nextPage.toString());
            }
            return result;
        } catch (PagingStateException ex) {
            throw new AssetStoreException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @Override
    public void deleteAsset(String uri) {
        ResultSet rs = session.execute(deleteAsset.bind(uri));
        if (!rs.wasApplied()) {
            throw new AssetStoreException("Asset not found.", Response.Status.NOT_FOUND);
        }
    }

    @Override
    public HealthCheck.Result checkHealth() throws Exception {
        // TODO make this more meaningful and flexible
        if (session.getState().getConnectedHosts().size() < 3) {
            return HealthCheck.Result.unhealthy("One or more nodes are down.");
        }
        return HealthCheck.Result.healthy();
    }
}
