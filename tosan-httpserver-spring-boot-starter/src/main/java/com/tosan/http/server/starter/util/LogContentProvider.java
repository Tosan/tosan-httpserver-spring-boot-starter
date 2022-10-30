package com.tosan.http.server.starter.util;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 */
public abstract class LogContentProvider<T> {

    public abstract T getContentContainer();

    public abstract void addToContent(String key, Object value, T container);

    public abstract String generateLogContent(String key, T container);
}
