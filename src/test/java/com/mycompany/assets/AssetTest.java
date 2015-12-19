package com.mycompany.assets;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;


/**
 * Created by amdonov on 12/18/15.
 */
public class AssetTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void deserializeValid() throws IOException {
        assertEquals(new Asset("fineuri", "asset1"),
                MAPPER.readValue(fixture("fixtures/valid.json"), Asset.class));
    }

    private void testValidation(String testfile) throws IOException {
        thrown.expect(JsonMappingException.class);
        final Asset asset = MAPPER.readValue(fixture(testfile), Asset.class);
    }

    @Test
    public void deserializeNoName() throws IOException {
        testValidation("fixtures/no-name.json");
    }

    @Test
    public void deserializeNoURI() throws IOException {
        testValidation("fixtures/no-uri.json");
    }

    @Test
    public void deserializeInvalidURI()throws IOException  {
        testValidation("fixtures/invalid-uri.json");
    }

}
