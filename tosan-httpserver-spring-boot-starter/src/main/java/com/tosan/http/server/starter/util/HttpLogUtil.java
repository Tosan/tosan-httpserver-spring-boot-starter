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

    public HttpLogUtil(JsonReplaceHelperDecider replaceHelperDecider) {
        this.replaceHelperDecider = replaceHelperDecider;
    }

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("text/*")
//            MediaType.APPLICATION_XML,
//            MediaType.valueOf("application/*+xml"),
//            MediaType.MULTIPART_FORM_DATA
    );

    public void logRequest(CustomHttpServletRequestWrapper request) {
        StringBuilder requestLog = new StringBuilder();
        requestLog.append("\n-- Http Request --\n");
        logRequestHeader(request, requestLog);
        logRequestBody(request, requestLog);
        LOGGER.debug(requestLog.toString());
    }

    public void logResponse(ContentCachingResponseWrapper response) {
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("\n-- Http Response --\n");
        logResponseHeaders(response, responseLog);
        logResponseBody(response, responseLog);
        LOGGER.debug(responseLog.toString());
    }

    private void logRequestHeader(CustomHttpServletRequestWrapper request, StringBuilder requestLog) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            requestLog.append(String.format("%s %s", request.getMethod(), request.getRequestURI())).append("\n");
        } else {
            String maskedQueryString = maskQueryString(queryString);
            requestLog.append(String.format("%s %s?%s", request.getMethod(), request.getRequestURI(), maskedQueryString)).append("\n");
        }
        final Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            Collections.list(headerNames).forEach(headerName ->
                    Collections.list(request.getHeaders(headerName))
                            .forEach(headerValue -> addHeaders(requestLog, headerName, headerValue)));
        }
        requestLog.append("\n");
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
                result.append(fieldValueSplit[0] + "=" + maskedValue);
            } else {
                result.append(queryParam);
            }
            result.append("&");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private void logResponseHeaders(ContentCachingResponseWrapper response, StringBuilder responseLog) {
        int status = response.getStatus();
        responseLog.append(String.format("%s %s", status, HttpStatus.valueOf(status).getReasonPhrase())).append("\n");
        Collection<String> headerNames = response.getHeaderNames();
        if (headerNames != null) {
            headerNames.forEach(headerName ->
                    response.getHeaders(headerName)
                            .forEach(headerValue -> addHeaders(responseLog, headerName, headerValue)));
        }
    }

    private void addHeaders(StringBuilder logMessage, String headerName, String headerValue) {
        JsonReplaceResultDto jsonReplaceResultDto = replaceHelperDecider.checkJsonAndReplace(headerValue);
        if (!jsonReplaceResultDto.isJson()) {
            headerValue = replaceHelperDecider.replace(headerName, headerValue);
        } else {
            headerValue = jsonReplaceResultDto.getReplacedJson();
        }
        logMessage.append(String.format("%s: %s", headerName, headerValue)).append("\n");
    }

    private void logRequestBody(CustomHttpServletRequestWrapper request, StringBuilder requestLog) {
        try {
            logRequestContent(request, requestLog);
        } catch (IOException e) {
            requestLog.append(String.format("[error in request body reading : %s]", e.getMessage())).append("\n");
        }
    }

    private void logResponseBody(ContentCachingResponseWrapper response, StringBuilder responseLog) {
        responseLog.append("\n");
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getContentType(), responseLog);
        }
    }

    private void logContent(byte[] content, String contentType, StringBuilder msg) {
        if (StringUtils.isEmpty(contentType)) {
            return;
        }
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        String mediaMainType = mediaType.getType() + "/" + mediaType.getSubtype();
        if (visible) {
            String contentString = new String(content, StandardCharsets.UTF_8);
            if (mediaType.equals(MediaType.APPLICATION_JSON) || mediaMainType.equals("application/json")) {
                contentString = replaceHelperDecider.replace(contentString);
            }
            Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> msg.append(line).append("\n"));
        } else {
            msg.append(String.format("[%d bytes content]", content.length)).append("\n");
        }
    }

    private void logRequestContent(CustomHttpServletRequestWrapper request, StringBuilder msg) throws IOException {
        String contentType = request.getContentType();
        if (StringUtils.isEmpty(contentType)) {
            return;
        }
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        String mediaMainType = mediaType.getType() + "/" + mediaType.getSubtype();
        if (visible) {
            if (mediaType.equals(MediaType.APPLICATION_JSON) || mediaMainType.equals("application/json")) {
                extractBody(request, msg, true);
            } else if (mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED) || mediaMainType.equals("application/x-www-form-urlencoded")) {
                extractFormBody(request, msg);
            } else {
                extractBody(request, msg, false);
            }
        } else {
            msg.append(String.format("unsupported media type")).append("\n");
        }
    }

    private void extractFormBody(CustomHttpServletRequestWrapper request, StringBuilder msg) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        msg.append("form parameters:\n");
        if (parameterMap != null) {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                msg.append(entry.getKey() + " : ");
                if (entry.getValue() != null && entry.getValue().length > 0) {
                    for (String value : entry.getValue()) {
                        String replacedValue = replaceHelperDecider.replace(entry.getKey(), value);
                        msg.append(replacedValue + ",");
                    }
                    msg.deleteCharAt(msg.length() - 1);
                }
                msg.append("\n");
            }
        }
    }

    private void extractBody(CustomHttpServletRequestWrapper request, StringBuilder msg, boolean maskContent) throws IOException {
        byte[] content = request.getInputStream().getInputByteArray();
        if (content.length > 0) {
            String contentString = new String(content, StandardCharsets.UTF_8);
            if (maskContent) {
                contentString = replaceHelperDecider.replace(contentString);
            }
            Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> msg.append(line).append("\n"));
        }
    }
}