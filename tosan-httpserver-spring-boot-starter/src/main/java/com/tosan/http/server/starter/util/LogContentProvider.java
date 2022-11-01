package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.wrapper.LogContentContainer;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 * <p>
 * This class is for generating http log with different format
 */
public abstract class LogContentProvider {
    public String generateLogContent(LogContentContainer container) {
        if (container != null && container.getTitle() != null) {
            switch (container.getTitle()) {
                case REQUEST:
                    return generateRequestLogContent(container);
                case RESPONSE:
                    return generateResponseLogContent(container);
            }
        }
        return null;
    }

    protected abstract String generateRequestLogContent(LogContentContainer container);

    protected abstract String generateResponseLogContent(LogContentContainer container);
}
