package com.mycompany.assets;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

/**
 * Created by amdonov on 12/18/15.
 */
public class NotesTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void deserializeValid() throws IOException {
        assertEquals(new Note("fineuri", "note1"),
                MAPPER.readValue(fixture("fixtures/notes/valid.json"), Note.class));
    }

    private void testValidation(String testfile) throws IOException {
        thrown.expect(JsonMappingException.class);
        final Note note = MAPPER.readValue(fixture(testfile), Note.class);
    }

    @Test
    public void deserializeNoNote() throws IOException {
        testValidation("fixtures/notes/no-note.json");
    }

    @Test
    public void deserializeNoURI() throws IOException {
        testValidation("fixtures/notes/no-uri.json");
    }

    @Test
    public void deserializeInvalidURI()throws IOException  {
        testValidation("fixtures/notes/invalid-uri.json");
    }
}
