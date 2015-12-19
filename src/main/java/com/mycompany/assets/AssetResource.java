package com.mycompany.assets;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Date;

/**
 * Created by amdonov on 12/16/15.
 */
@Path("/asset")
@Api("/asset")
public class AssetResource {

    private final AssetStore mStore;

    AssetResource(AssetStore store) {
        mStore = store;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a new asset", consumes = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "asset created"),
            @ApiResponse(code = 409, message = "asset exists with that uri"),
            @ApiResponse(code = 400, message = "asset is invalid such as missing or invalid name or uri"),
            @ApiResponse(code = 500, message = "unexpected error")})
    public Response createAsset(@ApiParam(value = "asset to create", required = true) final Asset asset,
                                @Context final UriInfo uriInfo) {
        if (asset==null) {
            throw new WebApplicationException("asset is required", Response.Status.BAD_REQUEST);
        }
        asset.setModtime(new Date());
        mStore.addAsset(asset);
        final URI assetUri = uriInfo.getAbsolutePathBuilder().queryParam("uri", asset.getUri()).build();
        return Response.created(assetUri).build();
    }

    @DELETE
    @Timed
    @ApiOperation(value = "Delete an asset")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "asset deleted"),
            @ApiResponse(code = 400, message = "invalid request"),
            @ApiResponse(code = 404, message = "asset not found"),
            @ApiResponse(code = 500, message = "unexpected error")})
    public Response deleteAsset(@ApiParam(value = "uri of asset to delete", required = true)
                                @QueryParam("uri") final String uri) {
        validateUri(uri);
        mStore.deleteAsset(uri);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Retrieve an asset", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 304, message = "asset not modified"),
            @ApiResponse(code = 400, message = "invalid request"),
            @ApiResponse(code = 404, message = "asset not found"),
            @ApiResponse(code = 200, message = "success",response=Asset.class),
            @ApiResponse(code = 500, message = "unexpected error")})
    public Response getAsset(@ApiParam(value = "uri of asset to retrieve", required = true)
                             @QueryParam("uri") final String uri, @Context final Request request) {
        validateUri(uri);
        final Asset asset = mStore.getAsset(uri);
        if (null == asset) {
            throw new WebApplicationException("Asset not found.", Response.Status.NOT_FOUND);
        }
        // Let the client use its cache if asset hasn't changed
        Response.ResponseBuilder builder = request.evaluatePreconditions(asset.getModtime());
        if (builder != null) {
            return builder.build();
        }
        builder = Response.ok(asset);
        builder.lastModified(asset.getModtime());
        return builder.build();
    }

    private void validateUri(String uri) {
        if (null == uri || uri.isEmpty()) {
            throw new WebApplicationException("uri query param is required.",
                    Response.Status.BAD_REQUEST);
        }
        try {
            URI.create(uri);
        } catch (IllegalArgumentException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

}
