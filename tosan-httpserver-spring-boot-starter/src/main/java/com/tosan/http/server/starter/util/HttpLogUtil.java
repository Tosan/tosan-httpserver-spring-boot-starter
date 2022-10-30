package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.wrapper.CustomHttpServletRequestWrapper;
import com.tosan.http.server.starter.wrapper.HttpTitleType;
import com.tosan.http.server.starter.wrapper.LogContentContainer;
import com.tosan.tools.mask.starter.dto.JsonReplaceResultDto;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author M.khoshnevisan
 * @since 4/19/2021
 */
public class HttpLogUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpLogUtil.class);

    private final JsonReplaceHelperDecider replaceHelperDecider;
    private final LogContentProvider logContentProvider;

    public HttpLogUtil(JsonReplaceHelperDecider replaceHelperDecider, LogContentProvider logContentProvider) {
        this.replaceHelperDecider = replaceHelperDecider;
        this.logContentProvider = logContentProvider;
    }

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("text/plain"),
            MediaType.valueOf("text/xml")
//            MediaType.APPLICATION_XML,
//            MediaType.valueOf("application/*+xml"),
//            MediaType.MULTIPART_FORM_DATA
    );

    public void logRequest(CustomHttpServletRequestWrapper request) {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.REQUEST);
        logRequestHeader(request, container);
        logRequestBody(request, container);
        LOGGER.debug(logContentProvider.generateLogContent(container));
    }

    public void logResponse(ContentCachingResponseWrapper response) {
        LogContentContainer container = new LogContentContainer();
        container.setTitle(HttpTitleType.RESPONSE);
        logResponseHeaders(response, container);
        logResponseBody(response, container);
        LOGGER.debug(logContentProvider.generateLogContent(container));
    }

    private void logRequestHeader(CustomHttpServletRequestWrapper request, LogContentContainer container) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            container.setUrl(String.format("%s %s", request.getMethod(), request.getRequestURI()));
        } else {
            String maskedQueryString = maskQueryString(queryString);
            container.setUrl(String.format("%s %s?%s", request.getMethod(), request.getRequestURI(), maskedQueryString));
        }
        final Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            Collections.list(headerNames).forEach(headerName ->
                    Collections.list(request.getHeaders(headerName))
                            .forEach(headerValue -> addHeaders(headerName, headerValue, container)));
        }
    }

    private String maskQueryString(String queryString) {
        if (StringUtils.isEmpty(queryString)) {
            return queryString;
        }
        StringBuilder result = new StringBuilder();
        String[] queryParams = queryString.split("&");
        for (String queryParam : queryParams) {
            String[] fieldValueSplit = queryParam.split("=");
            if (fieldValueSplit.length == 2) {
                String maskedValue = replaceHelperDecider.replace(fieldValueSplit[0], fieldValueSplit[1]);
                result.append(fieldValueSplit[0]).append("=").append(maskedValue);
            } else {
                result.append(queryParam);
            }
            result.append("&");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private void logResponseHeaders(ContentCachingResponseWrapper response, LogContentContainer container) {
        int status = response.getStatus();
        container.setStatus(String.format("%s %s", status, HttpStatus.valueOf(status).getReasonPhrase()));
        Collection<String> headerNames = response.getHeaderNames();
        if (headerNames != null) {
            headerNames.forEach(headerName ->
                    response.getHeaders(headerName)
                            .forEach(headerValue -> addHeaders(headerName, headerValue, container)));
        }
    }

    private void addHeaders(String headerName, String headerValue, LogContentContainer container) {
        if (headerValue != null && headerValue.length() > 0) {
            JsonReplaceResultDto jsonReplaceResultDto = replaceHelperDecider.checkJsonAndReplace(headerValue);
            if (jsonReplaceResultDto.isJson()) {
                headerValue = jsonReplaceResultDto.getReplacedJson();
            } else if (isUrl(headerValue)) {
                String[] urlQueryParamSplit = headerValue.split("\\?");
                if (urlQueryParamSplit.length == 2) {
                    String maskedQueryParams = maskQueryString(urlQueryParamSplit[1]);
                    headerValue = urlQueryParamSplit[0] + "?" + maskedQueryParams;
                }
            } else {
                headerValue = replaceHelperDecider.replace(headerName, headerValue);
            }
        }
        container.getHeaders().put(headerName, headerValue);
    }

    private boolean isUrl(String headerValue) {
        try {
            new URL(headerValue);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private void logRequestBody(CustomHttpServletRequestWrapper request, LogContentContainer container) {
        try {
            logRequestContent(request, container);
        } catch (IOException e) {
            container.setHasError(true);
            container.getErrorParam().put("error in request body reading", e.getMessage());
        }
    }

    private void logResponseBody(ContentCachingResponseWrapper response, LogContentContainer container) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getContentType(), container);
        }
    }

    private void logContent(byte[] content, String contentType, LogContentContainer container) {
        if (StringUtils.isEmpty(contentType)) {
            return;
        }
        StringBuilder msg = new StringBuilder();
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        String mediaMainType = mediaType.getType() + "/" + mediaType.getSubtype();
        if (visible) {
            String contentString = new String(content, StandardCharsets.UTF_8);
            if (mediaType.equals(MediaType.APPLICATION_JSON) || mediaMainType.equals("application/json")) {
                contentString = replaceHelperDecider.replace(contentString);
            }
            Stream.of(contentString.split("\r\n|\r|\n")).forEach(msg::append);
            container.setBody(msg.toString());
        } else {
            container.setHasError(true);
            container.getErrorParam().put("content bytes", content.length);
        }
    }

    private void logRequestContent(CustomHttpServletRequestWrapper request, LogContentContainer container) throws IOException {
        String contentType = request.getContentType();
        if (StringUtils.isEmpty(contentType)) {
            return;
        }
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        String mediaMainType = mediaType.getType() + "/" + mediaType.getSubtype();
        if (visible) {
            if (mediaType.equals(MediaType.APPLICATION_JSON) || mediaMainType.equals("application/json")) {
                extractBody(request, container, true);
            } else if (mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED) || mediaMainType.equals("application/x-www-form-urlencoded")) {
                container.setFormBody(true);
                extractFormBody(request, container);
            } else {
                extractBody(request, container, false);
            }
        } else {
            container.setHasError(true);
            container.getErrorParam().put("unsupported media type", mediaType);
        }
    }

    private void extractFormBody(CustomHttpServletRequestWrapper request, LogContentContainer container) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder msg = new StringBuilder();
        if (parameterMap != null) {
            msg.append("\n");
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                msg.append(entry.getKey()).append(" : ");
                if (entry.getValue() != null && entry.getValue().length > 0) {
                    for (String value : entry.getValue()) {
                        String replacedValue = replaceHelperDecider.replace(entry.getKey(), value);
                        msg.append(replacedValue).append(",");
                    }
                    msg.deleteCharAt(msg.length() - 1);
                }
                msg.append("\n");
            }
        }
        container.setBody(msg.toString());
    }

    private void extractBody(CustomHttpServletRequestWrapper request, LogContentContainer container, boolean maskContent) throws IOException {
        byte[] content = request.getInputStream().getInputByteArray();
        if (content.length > 0) {
            StringBuilder msg = new StringBuilder();
            String contentString = new String(content, StandardCharsets.UTF_8);
            if (maskContent) {
                contentString = replaceHelperDecider.replace(contentString);
            }
            Stream.of(contentString.split("\r\n|\r|\n")).forEach(msg::append);
            container.setBody(msg.toString());
        }
    }
}