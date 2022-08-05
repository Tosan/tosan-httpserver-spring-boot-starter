package com.tosan.http.server.starter.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tosan.http.server.starter.wrapper.CustomHttpServletRequestWrapper;
import com.tosan.tools.mask.starter.dto.JsonReplaceResultDto;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.tosan.http.server.starter.TestLogUtil.getAppenderList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

/**
 * @author M.khoshnevisan
 * @since 6/23/2021
 */
public class HttpLogUtilUTest {

    private HttpLogUtil httpLogUtil;
    private JsonReplaceHelperDecider replaceHelperDecider;
    private CustomHttpServletRequestWrapper request;
    private ContentCachingResponseWrapper response;
    private JsonReplaceResultDto jsonReplaceResultDto;
    private String testMethod = "POST";
    private String testUri = "/test";
    private CustomHttpServletRequestWrapper.CachedServletInputStream inputStream;
    private Integer responseStatus = 200;

    @BeforeEach
    public void setup() throws IOException {
        replaceHelperDecider = mock(JsonReplaceHelperDecider.class);
        httpLogUtil = new HttpLogUtil(replaceHelperDecider);
        request = mock(CustomHttpServletRequestWrapper.class);
        when(request.getMethod()).thenReturn(testMethod);
        when(request.getRequestURI()).thenReturn(testUri);
        inputStream = mock(CustomHttpServletRequestWrapper.CachedServletInputStream.class);
        when(request.getInputStream()).thenReturn(inputStream);
        response = mock(ContentCachingResponseWrapper.class);
        when(response.getStatus()).thenReturn(responseStatus);
        jsonReplaceResultDto = mock(JsonReplaceResultDto.class);
        when(replaceHelperDecider.checkJsonAndReplace(any())).thenReturn(jsonReplaceResultDto);
    }

    @Test
    public void testLogRequest_WithoutQueryString_correctWebMethodAndPathLogging() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[1], "-- Http Request --");
        assertEquals(messageSplit[2], testMethod + " " + testUri);
    }

    @Test
    public void testLogRequest_WithQueryString_correctQueryStringLogging() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=mina&description=test";
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("mina");
        when(replaceHelperDecider.replace(eq("description"), eq("test"))).thenReturn("test");
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[2], testMethod + " " + testUri + "?" + queryString);
    }

    @Test
    public void testLogRequest_WithSensitiveQueryString_correctQueryStringLogging() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=mina&description=test";
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("maskedValue");
        when(replaceHelperDecider.replace(eq("description"), eq("test"))).thenReturn("maskedVal2");
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[2], testMethod + " " + testUri + "?" + "name=maskedValue&description=maskedVal2");
    }

    @Test
    public void testLogRequest_emptyQueryString_correctQueryStringLogging() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "";
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[2], testMethod + " " + testUri + "?");
    }

    @Test
    public void testLogRequest_oneQueryString_correctQueryStringLogging() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=mina";
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("mina");
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[2], testMethod + " " + testUri + "?" + queryString);
    }

    @Test
    public void testLogRequest_wrongFormatOfQueryString_logQueryStringWithNoChange() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=m=ina";
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[2], testMethod + " " + testUri + "?" + queryString);
    }

    @Test
    public void testLogRequest_withSensitiveHeaderStringType_returnReplacerResult() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        Enumeration<String> headerNames = mock(Enumeration.class);
        when(headerNames.hasMoreElements()).thenReturn(true).thenReturn(false);
        String headerName = "authorization";
        when(headerNames.nextElement()).thenReturn(headerName);
        when(request.getHeaderNames()).thenReturn(headerNames);

        Enumeration<String> headerValues = mock(Enumeration.class);
        when(headerValues.hasMoreElements()).thenReturn(true).thenReturn(false);
        String sensitiveValue = "sensitiveValue";
        when(headerValues.nextElement()).thenReturn(sensitiveValue);
        when(request.getHeaders(anyString())).thenReturn(headerValues);

        when(replaceHelperDecider.replace(eq(sensitiveValue))).thenReturn(sensitiveValue);
        String maskedValue = "maskedVal**";
        when(replaceHelperDecider.replace(eq(headerName), eq(sensitiveValue))).thenReturn(maskedValue);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[3], headerName + ": " + maskedValue);
        assertFalse(message.contains(sensitiveValue));
    }

    @Test
    public void testLogRequest_withJsonHeader_maskHeader() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        Enumeration<String> headerNames = mock(Enumeration.class);
        when(headerNames.hasMoreElements()).thenReturn(true).thenReturn(false);
        String headerName = "context";
        when(headerNames.nextElement()).thenReturn(headerName);
        when(request.getHeaderNames()).thenReturn(headerNames);

        Enumeration<String> headerValues = mock(Enumeration.class);
        when(headerValues.hasMoreElements()).thenReturn(true).thenReturn(false);
        String sensitiveValue = "{\"password\":\"123456\"}";
        when(headerValues.nextElement()).thenReturn(sensitiveValue);
        when(request.getHeaders(anyString())).thenReturn(headerValues);

        final String maskedValue = "{\"password\":\"**ENCRYPTED\"}";
        when(jsonReplaceResultDto.isJson()).thenReturn(false);
        when(replaceHelperDecider.replace(anyString(), any())).thenReturn(maskedValue);

        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[3], headerName + ": " + maskedValue);
        assertFalse(message.contains(sensitiveValue));
        verify(replaceHelperDecider, times(1)).checkJsonAndReplace(eq(sensitiveValue));
        verify(replaceHelperDecider, times(1)).replace(anyString(), any());
    }

    @Test
    public void testLogRequest_withNormalHeader_addRawHeader() throws IOException {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        Enumeration<String> headerNames = mock(Enumeration.class);
        when(headerNames.hasMoreElements()).thenReturn(true).thenReturn(false);
        String sampleHeader = "sampleHeader";
        when(headerNames.nextElement()).thenReturn(sampleHeader);
        when(request.getHeaderNames()).thenReturn(headerNames);

        Enumeration<String> headerValues = mock(Enumeration.class);
        when(headerValues.hasMoreElements()).thenReturn(true).thenReturn(false);
        String value = "simpleValue";
        when(headerValues.nextElement()).thenReturn(value);
        when(request.getHeaders(anyString())).thenReturn(headerValues);

        when(replaceHelperDecider.replace(anyString())).thenReturn(value);
        when(replaceHelperDecider.replace(anyString(), any())).thenReturn(value);

        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[3], sampleHeader + ": " + value);
    }

    @Test
    public void testLogRequest_withInvisibleBodyType_showBodyLength() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[1]);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("multipart/mixed");

        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "unsupported media type");
    }

    @Test
    public void testLogRequest_withApplicationJsonType_showMaskedJson() throws IOException {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[1]);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/json");
        String maskedJson = "maskedJson";
        when(replaceHelperDecider.replace(anyString())).thenReturn(maskedJson);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], maskedJson);
        verify(replaceHelperDecider, times(1)).replace(anyString());
    }

    @Test
    public void testLogRequest_withApplicationTextType_showOriginalText() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("text/plain");
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "test");
        verify(replaceHelperDecider, times(0)).replace(anyString());
    }

    @Test
    public void testLogRequest_withFormUrlEncodedTypeWithNullParameterMap_emptyParameters() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(request.getParameterMap()).thenReturn(null);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "form parameters:");
        verify(replaceHelperDecider, times(0)).replace(anyString(), anyString());
    }

    @Test
    public void testLogRequest_withFormUrlEncodedTypeWithEmptyValues_logParameterKey() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        Map<String, String[]> parameterMap = new HashMap<>();
        String parameterKey = "testKey";
        parameterMap.put(parameterKey, null);
        when(request.getParameterMap()).thenReturn(parameterMap);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "form parameters:");
        assertEquals(messageSplit[5], parameterKey + " : ");
        verify(replaceHelperDecider, times(0)).replace(anyString(), anyString());
    }

    @Test
    public void testLogRequest_withFormUrlEncodedTypeWithFilledValues_logMaskedParameterValues() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        String parameterKey = "testKey";
        Map<String, String[]> parameterMap = new HashMap<>();
        String[] values = new String[2];
        String testValue1 = "testValue1";
        values[0] = testValue1;
        String testValue2 = "testValue2";
        values[1] = testValue2;
        parameterMap.put(parameterKey, values);
        when(request.getParameterMap()).thenReturn(parameterMap);
        String maskedValue1 = "maskedValue1";
        when(replaceHelperDecider.replace(eq(parameterKey), eq(testValue1))).thenReturn(maskedValue1);
        String maskedValue2 = "maskedValue2";
        when(replaceHelperDecider.replace(eq(parameterKey), eq(testValue2))).thenReturn(maskedValue2);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "form parameters:");
        assertEquals(messageSplit[5], parameterKey + " : " + maskedValue1 + "," + maskedValue2);
        verify(replaceHelperDecider, times(0)).replace(anyString());
    }


    /*@Test
    public void testLogRequest_withApplicationXmlType_showXmlWithoutCallingReplaceHelper() throws IOException {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        String bodyValue = "<test>";
        when(inputStream.getInputByteArray()).thenReturn(bodyValue.getBytes(StandardCharsets.UTF_8));
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/xml");
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], bodyValue);
        verify(replaceHelperDecider, times(0)).replace(anyString(), any());
    }*/

    @Test
    public void testLogRequest_withInvalidBody_logErrorInRequestBody() throws IOException {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getContentType()).thenReturn("application/json");
        when(request.getQueryString()).thenReturn(null);
        when(request.getInputStream()).thenThrow(IOException.class);
        when(request.getHeaderNames()).thenReturn(null);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "[error in request body reading : null]");
    }

    @Test
    public void testLogResponse_withResponseHeader_correctResponseHeaderLog() throws IOException {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[0]);
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[1], "-- Http Response --");
        assertEquals(messageSplit[2], responseStatus + " OK");
    }

    @Test
    public void testLogResponse_withSensitiveResponseHeader_maskSensitiveHeader() throws IOException {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[0]);
        Collection<String> headerNames = new ArrayList<>();
        String headerName = "authorization";
        headerNames.add(headerName);
        when(response.getHeaderNames()).thenReturn(headerNames);
        Collection<String> headerValues = new ArrayList<>();
        String sensitiveValue = "sensitiveValue";
        headerValues.add(sensitiveValue);
        when(response.getHeaders(anyString())).thenReturn(headerValues);

        when(replaceHelperDecider.replace(eq(sensitiveValue))).thenReturn(sensitiveValue);
        String maskedValue = "**ENCRYPTED";
        when(replaceHelperDecider.replace(eq(headerName), eq(sensitiveValue))).thenReturn(maskedValue);

        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(headerName + ": " + maskedValue, messageSplit[3]);
        assertFalse(message.contains(sensitiveValue));
    }

    @Test
    public void testLogResponse_withJsonHeader_maskHeader() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[0]);

        Collection<String> headerNames = new ArrayList<>();
        String headerName = "context";
        headerNames.add(headerName);
        when(response.getHeaderNames()).thenReturn(headerNames);

        Collection<String> headerValues = new ArrayList<>();
        String sensitiveValue = "toBeMasked";
        headerValues.add(sensitiveValue);
        when(response.getHeaders(anyString())).thenReturn(headerValues);

        String maskedValue = "**ENCRYPTED";
        when(jsonReplaceResultDto.getReplacedJson()).thenReturn(maskedValue);
        when(jsonReplaceResultDto.isJson()).thenReturn(true);

        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[3], "context: " + maskedValue);
        assertFalse(message.contains(sensitiveValue));
        verify(replaceHelperDecider, times(1)).checkJsonAndReplace(eq(sensitiveValue));
    }

    @Test
    public void testLogResponse_withNormalHeader_addRawHeader() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[0]);

        Collection<String> headerNames = new ArrayList<>();
        final String sampleHeaderName = "sampleHeader";
        headerNames.add(sampleHeaderName);
        when(response.getHeaderNames()).thenReturn(headerNames);

        Collection<String> headerValues = new ArrayList<>();
        String value = "simpleValue";
        headerValues.add(value);
        when(response.getHeaders(anyString())).thenReturn(headerValues);

        when(replaceHelperDecider.replace(eq(value))).thenReturn(value);
        when(replaceHelperDecider.replace(eq(sampleHeaderName), eq(value))).thenReturn(value);

        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[3], sampleHeaderName + ": " + value);
    }

    @Test
    public void testLogResponse_withInvisibleBodyType_showBodyLength() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[1]);

        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentType()).thenReturn("multipart/mixed");

        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "[1 bytes content]");
    }

    @Test
    public void testLogResponse_withApplicationJsonType_showMaskedJson() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[1]);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentType()).thenReturn("application/json");
        String maskedJson = "maskedJson";
        when(replaceHelperDecider.replace(anyString())).thenReturn(maskedJson);
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], maskedJson);
        verify(replaceHelperDecider, times(1)).replace(anyString());
    }

    @Test
    public void testLogResponse_withApplicationTextType_showOriginalText() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentType()).thenReturn("text/plain");
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], "test");
        verify(replaceHelperDecider, times(0)).replace(anyString());
    }

    /*@Test
    public void testLogResponse_withApplicationXmlType_showXmlWithoutCallingReplaceHelper() throws IOException {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        String bodyValue = "<test>";
        when(response.getContentAsByteArray()).thenReturn(bodyValue.getBytes(StandardCharsets.UTF_8));
        when(response.getContentType()).thenReturn("application/xml");
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        String[] messageSplit = message.split("\n");
        assertEquals(messageSplit[4], bodyValue);
        verify(replaceHelperDecider, times(0)).replace(anyString());
    }*/
}