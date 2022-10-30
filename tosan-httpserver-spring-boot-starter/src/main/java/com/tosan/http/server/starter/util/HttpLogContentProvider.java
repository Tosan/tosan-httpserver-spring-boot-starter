package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.wrapper.LogContentContainer;

import java.util.Map;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 */
public class HttpLogContentProvider extends LogContentProvider {

    @Override
    protected String generateRequestLogContent(LogContentContainer container) {
        StringBuilder requestContent = new StringBuilder();
        requestContent.append("\n").append("-- Http Request --").append("\n");
        requestContent.append(container.getUrl()).append("\n");
        addHeader(container, requestContent);
        if (container.hasError()) {
            addError(container, requestContent);
        } else {
            if (container.isFormBody()) {
                requestContent.append("form parameters: ");
            }
            requestContent.append(container.getBody());
        }
        return requestContent.toString();
    }

    @Override
    protected String generateResponseLogContent(LogContentContainer container) {
        StringBuilder responseContent = new StringBuilder();
        responseContent.append("\n").append("-- Http Response --").append("\n");
        responseContent.append(container.getStatus()).append("\n");
        addHeader(container, responseContent);
        if (container.hasError()) {
            addError(container, responseContent);
        } else {
            responseContent.append(container.getBody());
        }
        return responseContent.toString();
    }

    private void addHeader(LogContentContainer container, StringBuilder content) {
        if (container.getHeaders() != null) {
            for (Map.Entry<String, Object> header : container.getHeaders().entrySet()) {
                content.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
            }
        }
    }

    private void addError(LogContentContainer container, StringBuilder content) {
        if (container.getErrorParam() != null) {
            for (Map.Entry<String, Object> header : container.getErrorParam().entrySet()) {
                content.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
            }
        }
    }
}
