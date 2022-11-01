package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.wrapper.HttpTitleType;
import com.tosan.http.server.starter.wrapper.LogContentContainer;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

/**
 * @author AmirHossein ZamanZade
 * @since 10/31/2022
 */
public class JsonHttpLogContentProviderUTest {

    private final JsonHttpLogContentProvider logContentProvider = new JsonHttpLogContentProvider();

    @Test
    public void testGenerateLogContent_withRequestTitleAndUrl_returnCorrectTitleAndUrl() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"Post /test\"\n" +
                "  }\n" +
                "}\n";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setUrl("Post /test");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndHeaders_returnCorrectHeaders() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"headers\" : {\n" +
                "      \"key1\" : \"value1\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.getHeaders().put("key1", "value1");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndHasErrorInBodyRendering_returnCorrectBody() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"error\" : \"value\"\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setHasErrorInBodyRendering(true);
        container.getErrorParam().put("error", "value");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndFormParameters_returnCorrectBody() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"form parameters\" : \"key:value\"\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setFormBody(true);
        container.setBody("key:value");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndBody_returnCorrectBody() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"body\" : \"raw text\"\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setBody("raw text");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndStatus_returnCorrectTitleAndStatus() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : \"200 Ok\"\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.setStatus("200 Ok");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndHeader_returnCorrectHeader() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"headers\" : {\n" +
                "      \"test\" : \"value\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.getHeaders().put("test", "value");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndHasErrorInBodyRendering_returnCorrectBody() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"error\" : \"test\"\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.setHasErrorInBodyRendering(true);
        container.getErrorParam().put("error", "test");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndBody_returnCorrectBody() throws JSONException {
        final String expectedContent = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"body\" : \"raw value\"\n" +
                "  }\n" +
                "}";
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.setBody("raw value");
        String content = logContentProvider.generateLogContent(container);
        assertEquals(expectedContent, content, true);
    }
}
