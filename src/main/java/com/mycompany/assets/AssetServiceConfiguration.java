package com.mycompany.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.assets.store.CassandraStoreConfiguration;
import com.mycompany.assets.store.MemoryStoreConfiguration;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;

/**
 * Created by amdonov on 12/16/15.
 */
public class AssetServiceConfiguration extends Configuration {
    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;


    @JsonProperty("cassandra")
    @Valid
    private CassandraStoreConfiguration cassandraStoreConfiguration;


    @JsonProperty("memory")
    @Valid
    private MemoryStoreConfiguration memoryStoreConfiguration;

    @JsonCreator
    public AssetServiceConfiguration(@JsonProperty("cassandra") CassandraStoreConfiguration cassandra, @JsonProperty("memory") MemoryStoreConfiguration memory, @JsonProperty("swagger") SwaggerBundleConfiguration swagger) {
        if (memory == null && cassandra == null) {
            throw new IllegalArgumentException("Either Cassandra or Memory Store Configuration must be provided.");
        }
        cassandraStoreConfiguration = cassandra;
        memoryStoreConfiguration = memory;
        swaggerBundleConfiguration = swagger;
    }

    public MemoryStoreConfiguration getMemoryStoreConfiguration() {
        return memoryStoreConfiguration;
    }

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    public CassandraStoreConfiguration getCassandraStoreConfiguration() {
        return cassandraStoreConfiguration;
    }
}
