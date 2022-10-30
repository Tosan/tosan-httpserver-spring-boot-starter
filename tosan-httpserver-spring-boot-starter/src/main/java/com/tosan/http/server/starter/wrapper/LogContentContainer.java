package com.tosan.http.server.starter.wrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AmirHossein ZamanZade
 * @since 10/30/2022
 */
public class LogContentContainer {
    private HttpTitleType title;
    private String url;
    private String status;
    private Map<String, Object> headers;
    private String body;
    private boolean isFormBody;
    private boolean hasError;
    private Map<String, Object> errorParam;


    public HttpTitleType getTitle() {
        return title;
    }

    public void setTitle(HttpTitleType title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getHeaders() {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isFormBody() {
        return isFormBody;
    }

    public void setFormBody(boolean formBody) {
        isFormBody = formBody;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public Map<String, Object> getErrorParam() {
        if (errorParam == null) {
            errorParam = new LinkedHashMap<>();
        }
        return errorParam;
    }
}
