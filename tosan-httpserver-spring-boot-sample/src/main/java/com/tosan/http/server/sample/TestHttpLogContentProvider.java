package com.tosan.http.server.sample;

import com.tosan.http.server.starter.util.LogContentProvider;
import com.tosan.http.server.starter.wrapper.LogContentContainer;

/**
 * @author AmirHossein ZamanZade
 * @since 11/1/2022
 */
public class TestHttpLogContentProvider extends LogContentProvider {
    @Override
    protected String generateRequestLogContent(LogContentContainer container) {
        return "-- Http Request --";
    }

    @Override
    protected String generateResponseLogContent(LogContentContainer container) {
        return "-- Http Response --";
    }
}
