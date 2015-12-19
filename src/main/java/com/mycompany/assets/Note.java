package com.mycompany.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by amdonov on 12/18/15.
 */
public class Note {
    private String uri;
    private String note;

    /**
     * Constructor for Jackson to use rather than reflection. Causes validation of fields.
     */
    @JsonCreator
    public Note(@JsonProperty("uri") String uri, @JsonProperty("note") String note) {
        // Confirm that note is provided
        if (note==null || note.isEmpty()) {
            throw new IllegalArgumentException("note is required");
        }
        // Confirm that uri is provided and is valid
        if (uri==null || uri.isEmpty()) {
            throw new IllegalArgumentException("uri is required");
        }
        URI.create(uri);
        this.uri = uri;
        this.note = note;
    }

    @JsonPropertyDescription("asset identifier")
    @JsonProperty(required = true)
    public String getUri() {
        return uri;
    }

    @JsonPropertyDescription("note contents")
    @JsonProperty(required = true)
    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note1 = (Note) o;

        if (!uri.equals(note1.uri)) return false;
        return note.equals(note1.note);
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + note.hashCode();
        return result;
    }
}
