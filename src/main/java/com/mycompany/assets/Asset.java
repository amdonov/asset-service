package com.mycompany.assets;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

/**
 * Created by amdonov on 12/16/15.
 */
public class Asset {

    private String uri;
    private String name;
    private Date modtime;
    private List<String> notes;

    public Asset() {

    }

    public Asset(String uri, String name, Date modtime, List<String> notes) {
        this.uri = uri;
        this.name = name;
        this.modtime = modtime;
        this.notes = notes;
    }

    public String getUri() {
        return uri;
    }

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

    public List<String> getNotes() {
        return notes;
    }
}
