package com.mycompany.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.net.URI;

/**
 * Created by amdonov on 12/16/15.
 */
public class AssetSummary {

    private final String uri;
    private final String name;

    /**
     * Constructor for Jackson to use rather than reflection. Causes validation of fields.
     */
    @JsonCreator
    public AssetSummary(@JsonProperty("uri") String uri, @JsonProperty("name") String name) {
        // Confirm that name is provided
        if (name==null || name.isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        // Confirm that uri is provided and is valid
        if (uri==null || uri.isEmpty()) {
            throw new IllegalArgumentException("uri is required");
        }
        URI.create(uri);
        this.uri = uri;
        this.name = name;
    }

    @JsonPropertyDescription("identifier")
    @JsonProperty(required = true)
    public String getUri() {
        return uri;
    }

    @JsonPropertyDescription("human readable label")
    @JsonProperty(required = true)
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetSummary that = (AssetSummary) o;

        if (!uri.equals(that.uri)) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
