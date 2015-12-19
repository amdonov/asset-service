package com.mycompany.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by amdonov on 12/16/15.
 */
public class Asset {

    private final String uri;
    private final String name;
    private Date modtime;
    private final List<String> notes;

    /**
     * Constructor for Jackson to use rather than reflection. Causes validation of fields.
     */
    @JsonCreator
    public Asset(@JsonProperty("uri") String uri, @JsonProperty("name") String name) {
        this(uri,name,null,new ArrayList<>());
    }

    public Asset(String uri, String name, Date modtime, List<String> notes) {
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
        this.modtime = modtime;
        this.notes = notes;
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

    public void setModtime(Date modtime) {
        this.modtime = modtime;
    }

    @JsonIgnore
    public Date getModtime() {
        return modtime;
    }

    @JsonPropertyDescription("notes")
    public List<String> getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Asset asset = (Asset) o;

        if (!uri.equals(asset.uri)) return false;
        if (!name.equals(asset.name)) return false;
        return notes.equals(asset.notes);

    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + notes.hashCode();
        return result;
    }
}
