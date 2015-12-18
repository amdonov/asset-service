package com.mycompany.assets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by amdonov on 12/16/15.
 */
public class AssetStoreException extends WebApplicationException {
    public AssetStoreException(String message, Response.Status status) {
        super(message, status);
    }
}
