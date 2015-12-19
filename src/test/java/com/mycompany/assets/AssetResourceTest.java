package com.mycompany.assets;

import com.mycompany.assets.store.MemoryAssetStore;
import com.mycompany.assets.store.MemoryStoreConfiguration;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;

import static org.junit.Assert.assertEquals;

/**
 * Created by amdonov on 12/18/15.
 */
public class AssetResourceTest {
    // Use MemoryStore directly to have access to clear()
    private static final MemoryAssetStore mStore = new MemoryAssetStore(new MemoryStoreConfiguration());

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().
            addResource(new AssetResource(mStore)).build();

    @After
    public void emptyStore() {
        // Empty the store between tests.
        mStore.clear();
    }

    @Test
    public void testValidCreate() {
        final Asset asset = new Asset("test", "label");
        assertEquals(201, resources.client().target("/asset").request()
                .post(Entity.json(asset)).getStatus());
    }

    @Test
    public void testConflictingCreate() {
        final Asset asset = new Asset("test", "label");
        resources.client().target("/asset").request()
                .post(Entity.json(asset)).getStatus();
        // Second post should produce a conflict
        assertEquals(409, resources.client().target("/asset").request()
                .post(Entity.json(asset)).getStatus());
    }

    @Test
    public void testValidGet() {
        final Asset asset = new Asset("test", "label");
        resources.client().target("/asset").request()
                .post(Entity.json(asset)).getStatus();
        final Asset result =  resources.client().target("/asset").queryParam("uri", "test").request().get(Asset.class);
        assertEquals(asset,result);
    }

    @Test
    public void testInvalidGet() {
        // skip required uri parameter
        assertEquals(400, resources.client().target("/asset").request().get().getStatus());
    }

    @Test
    public void testMissingGet() {
        assertEquals(404, resources.client().target("/asset").queryParam("uri", "test").request().get().getStatus());
    }

    @Test
    public void testValidDelete() {
        mStore.addAsset(new Asset("uri", "name"));
        assertEquals(204, resources.client().target("/asset").queryParam("uri", "uri").request().delete().getStatus());
    }

    @Test
    public void testInvalidDelete() {
        // Leave out the uri parameter
        assertEquals(400, resources.client().target("/asset").request().delete().getStatus());
    }

    @Test
    public void testMissingDelete() {
        // Try to delete a record from an empty store
        assertEquals(404, resources.client().target("/asset").queryParam("uri", "gone").request().delete().getStatus());
    }
}
