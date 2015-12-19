package com.mycompany.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by amdonov on 12/16/15.
 */
public class Asset extends AssetSummary {

    private Date modtime;
    private final List<String> notes;

    /**
     * Constructor for Jackson to use rather than reflection. Causes validation of fields.
     */
    @JsonCreator
    public Asset(@JsonProperty("uri") String uri, @JsonProperty("name") String name) {
        this(uri,name,null,null);
    }

    public Asset(String uri, String name, Date modtime, List<String> notes) {
        super(uri,name);
        this.modtime = modtime;
         if (notes == null ) {
            this.notes = new ArrayList<>();
        } else {
             this.notes = notes;
         }
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
        if (!super.equals(o)) return false;

        Asset asset = (Asset) o;

        return notes.equals(asset.notes);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + notes.hashCode();
        return result;
    }
}
