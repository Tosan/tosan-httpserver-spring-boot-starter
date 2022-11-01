package com.tosan.http.server.starter.wrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AmirHossein ZamanZade
 * @since 10/30/2022
 * <p>
 * This class is a container for http log content
 */
public class LogContentContainer {
    private HttpTitleType title;
    private String url;
    private String status;
    private Map<String, Object> headers;
    private String body;
    private boolean isFormBody;
    private boolean hasErrorInBodyRendering;
    private Map<String, Object> errorParam;

    /**
     * @return title
     */
    public HttpTitleType getTitle() {
        return title;
    }

    public void setTitle(HttpTitleType title) {
        this.title = title;
    }

    /**
     * @return url (exp: POST /test)
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return headers
     */
    public Map<String, Object> getHeaders() {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        return headers;
    }

    /**
     * @return body
     */
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return is form parameter
     */
    public boolean isFormBody() {
        return isFormBody;
    }

    public void setFormBody(boolean formBody) {
        isFormBody = formBody;
    }

    /**
     * @return status of response
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return show that rendering body has error or not
     */
    public boolean hasErrorInBodyRendering() {
        return hasErrorInBodyRendering;
    }

    public void setHasErrorInBodyRendering(boolean hasErrorInBodyRendering) {
        this.hasErrorInBodyRendering = hasErrorInBodyRendering;
    }

    /**
     * @return if {@link #hasErrorInBodyRendering()} is true this method return error params otherwise return empty map
     */
    public Map<String, Object> getErrorParam() {
        if (errorParam == null) {
            errorParam = new LinkedHashMap<>();
        }
        return errorParam;
    }
}
