package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.wrapper.CustomHttpServletRequestWrapper;
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
        Object logContainer = logContentProvider.getContentContainer();
        logRequestHeader(request, logContentProvider, logContainer);
        logRequestBody(request, logContentProvider, logContainer);
        LOGGER.debug(logContentProvider.generateLogContent("Http Request", logContainer));
    }

    public void logResponse(ContentCachingResponseWrapper response) {
        Object logContainer = logContentProvider.getContentContainer();
        logResponseHeaders(response, logContentProvider, logContainer);
        logResponseBody(response, logContentProvider, logContainer);
        LOGGER.debug(logContentProvider.generateLogContent("Http Response", logContainer));
    }

    private void logRequestHeader(CustomHttpServletRequestWrapper request, LogContentProvider logContentProvider, Object logContainer) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            logContentProvider.addToContent("service", String.format("%s %s", request.getMethod(), request.getRequestURI()), logContainer);
        } else {
            String maskedQueryString = maskQueryString(queryString);
            logContentProvider.addToContent("service", String.format("%s %s?%s", request.getMethod(), request.getRequestURI(), maskedQueryString), logContainer);
        }
        final Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            Collections.list(headerNames).forEach(headerName ->
                    Collections.list(request.getHeaders(headerName))
                            .forEach(headerValue -> addHeaders(logContentProvider, logContainer, headerName, headerValue)));
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

    private void logResponseHeaders(ContentCachingResponseWrapper response, LogContentProvider logContentProvider, Object logContainer) {
        int status = response.getStatus();
        logContentProvider.addToContent("status", String.format("%s %s", status, HttpStatus.valueOf(status).getReasonPhrase()), logContainer);
        Collection<String> headerNames = response.getHeaderNames();
        if (headerNames != null) {
            headerNames.forEach(headerName ->
                    response.getHeaders(headerName)
                            .forEach(headerValue -> addHeaders(logContentProvider, logContainer, headerName, headerValue)));
        }
    }

    private void addHeaders(LogContentProvider logContentProvider, Object logContainer, String headerName, String headerValue) {
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
        logContentProvider.addToContent(headerName, headerValue, logContainer);
    }

    private boolean isUrl(String headerValue) {
        try {
            new URL(headerValue);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private void logRequestBody(CustomHttpServletRequestWrapper request, LogContentProvider logContentProvider, Object logContainer) {
        try {
            logRequestContent(request, logContentProvider, logContainer);
        } catch (IOException e) {
            logContentProvider.addToContent("error in request body reading", e.getMessage(), logContainer);
        }
    }

    private void logResponseBody(ContentCachingResponseWrapper response, LogContentProvider logContentProvider, Object logContainer) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getContentType(), logContentProvider, logContainer);
        }
    }

    private void logContent(byte[] content, String contentType, LogContentProvider logContentProvider, Object logContainer) {
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
            logContentProvider.addToContent("body", msg, logContainer);
        } else {
            logContentProvider.addToContent("content bytes", content.length, logContainer);
        }
    }

    private void logRequestContent(CustomHttpServletRequestWrapper request, LogContentProvider logContentProvider, Object logContainer) throws IOException {
        String contentType = request.getContentType();
        if (StringUtils.isEmpty(contentType)) {
            return;
        }
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        String mediaMainType = mediaType.getType() + "/" + mediaType.getSubtype();
        if (visible) {
            if (mediaType.equals(MediaType.APPLICATION_JSON) || mediaMainType.equals("application/json")) {
                extractBody(request, logContentProvider, logContainer, true);
            } else if (mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED) || mediaMainType.equals("application/x-www-form-urlencoded")) {
                extractFormBody(request, logContentProvider, logContainer);
            } else {
                extractBody(request, logContentProvider, logContainer, false);
            }
        } else {
            logContentProvider.addToContent("unsupported media type", mediaType, logContainer);
        }
    }

    private void extractFormBody(CustomHttpServletRequestWrapper request, LogContentProvider logContentProvider, Object logContainer) {
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
        logContentProvider.addToContent("form parameters", msg.toString(), logContainer);
    }

    private void extractBody(CustomHttpServletRequestWrapper request, LogContentProvider logContentProvider, Object logContainer, boolean maskContent) throws IOException {
        byte[] content = request.getInputStream().getInputByteArray();
        if (content.length > 0) {
            StringBuilder msg = new StringBuilder();
            String contentString = new String(content, StandardCharsets.UTF_8);
            if (maskContent) {
                contentString = replaceHelperDecider.replace(contentString);
            }
            Stream.of(contentString.split("\r\n|\r|\n")).forEach(msg::append);
            logContentProvider.addToContent("body", msg.toString(), logContainer);
        }
    }
}