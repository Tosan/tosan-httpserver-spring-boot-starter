package com.tosan.http.server.starter.util;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 */
public class HttpLogContentProvider extends LogContentProvider<StringBuilder> {

    @Override
    public StringBuilder getContentContainer() {
        return new StringBuilder();
    }

    @Override
    public void addToContent(String key, Object value, StringBuilder container) {
        container.append(key).append(": ").append(value).append("\n");
    }

    @Override
    public String generateLogContent(String key, StringBuilder container) {
        StringBuilder finalContent = new StringBuilder();
        finalContent.append("\n").append("-- ").append(key).append(" --").append("\n");
        finalContent.append(container);
        return finalContent.toString();
    }
}
