package com.mycompany.assets;

import com.mycompany.assets.memstore.MemoryAssetStore;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

/**
 * Created by amdonov on 12/16/15.
 */
public class AssetServiceApplication extends Application<AssetServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new AssetServiceApplication().run(args);
    }

    @Override
    public void run(AssetServiceConfiguration configuration, Environment environment) throws Exception {
        final AssetResource resource = new AssetResource(new MemoryAssetStore());
        environment.jersey().register(resource);
    }

    @Override
    public String getName() {
        return "asset-service";
    }

    @Override
    public void initialize(Bootstrap<AssetServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<AssetServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AssetServiceConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });
    }
}
