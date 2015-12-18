package com.mycompany.assets;

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
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a new asset", consumes = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "asset created"),
            @ApiResponse(code = 409, message = "asset exists with that uri"),
            @ApiResponse(code = 400, message = "asset is invalid"),
            @ApiResponse(code = 500, message = "unexpected error")})
    public Response createAsset(@ApiParam(value = "asset to create", required = true) final Asset asset,
                                @Context final UriInfo uriInfo) {
        if (null == asset.getUri()) {
            throw new WebApplicationException("URI of asset is required.", Response.Status.BAD_REQUEST);
        }
        if (null == asset.getName()) {
            throw new WebApplicationException("Name of asset is required.", Response.Status.BAD_REQUEST);
        }
        asset.setModtime(new Date());
        mStore.addAsset(asset);
        final URI assetUri = uriInfo.getAbsolutePathBuilder().path(asset.getUri()).build();
        return Response.created(assetUri).build();
    }

    @DELETE
    @Path("/{uri}")
    @ApiOperation(value = "Delete an asset")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "asset deleted"),
            @ApiResponse(code = 404, message = "asset not found"),
            @ApiResponse(code = 500, message = "unexpected error")})
    public Response deleteAsset(@ApiParam(value = "uri of asset to delete", required = true)
                                @PathParam("uri") final String uri) {
        mStore.deleteAsset(uri);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uri}")
    @ApiOperation(value = "Retrieve an asset", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 304, message = "asset not modified"),
            @ApiResponse(code = 404, message = "asset not found"), @ApiResponse(code = 200, message = "success",response=Asset.class),
            @ApiResponse(code = 500, message = "unexpected error")})
    public Response getAsset(@ApiParam(value = "uri of asset to retrieve", required = true)
                             @PathParam("uri") final String uri, @Context final Request request) {
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
}
