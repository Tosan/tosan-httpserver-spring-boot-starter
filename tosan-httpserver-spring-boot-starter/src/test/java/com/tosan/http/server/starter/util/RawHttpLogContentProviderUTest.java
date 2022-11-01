package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.wrapper.HttpTitleType;
import com.tosan.http.server.starter.wrapper.LogContentContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author AmirHossein ZamanZade
 * @since 10/31/2022
 */
public class RawHttpLogContentProviderUTest {

    private final RawHttpLogContentProvider logContentProvider = new RawHttpLogContentProvider();

    @Test
    public void testGenerateLogContent_withRequestTitleAndUrl_returnCorrectTitleAndUrl() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setUrl("Post /test");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("-- Http Request --", contents[1]);
        assertEquals("Post /test", contents[2]);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndHeader_returnCorrectHeader() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.getHeaders().put("test", "value");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("test: value", contents[3]);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndBody_returnCorrectBody() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setBody("raw value");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("raw value", contents[3]);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndFormParameterAndNoBody_returnCorrectBody() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setFormBody(true);
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("form parameters: ", contents[3]);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndFormParameterAndBody_returnCorrectBody() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setFormBody(true);
        container.setBody("key1:value1");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("form parameters: key1:value1", contents[3]);
    }

    @Test
    public void testGenerateLogContent_withRequestTitleAndHasErrorInBodyRendering_returnCorrectBody() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        container.setHasErrorInBodyRendering(true);
        container.getErrorParam().put("error", "test");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("error: test", contents[3]);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndStatus_returnCorrectTitleAndStatus() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.setStatus("200 Ok");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("-- Http Response --", contents[1]);
        assertEquals("200 Ok", contents[2]);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndHeader_returnCorrectHeader() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.getHeaders().put("test", "value");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("test: value", contents[3]);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndHasErrorInBodyRendering_returnCorrectBody() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.setHasErrorInBodyRendering(true);
        container.getErrorParam().put("error", "test");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("error: test", contents[3]);
    }

    @Test
    public void testGenerateLogContent_withResponseTitleAndBody_returnCorrectBody() {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        container.setBody("raw value");
        String content = logContentProvider.generateLogContent(container);
        String[] contents = content.split("\n");
        assertEquals("raw value", contents[3]);
    }
}
