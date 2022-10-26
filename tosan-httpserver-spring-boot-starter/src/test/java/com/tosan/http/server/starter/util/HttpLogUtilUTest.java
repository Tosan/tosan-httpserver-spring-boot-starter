package com.tosan.http.server.starter.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tosan.http.server.starter.wrapper.CustomHttpServletRequestWrapper;
import com.tosan.tools.mask.starter.dto.JsonReplaceResultDto;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import com.tosan.tools.mask.starter.replace.StaticJsonReplaceHelperDecider;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.tosan.http.server.starter.TestLogUtil.getAppenderList;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

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
        StaticJsonReplaceHelperDecider.init(replaceHelperDecider);
    }

    @Test
    public void testLogRequest_WithoutQueryString_correctWebMethodAndPathLogging() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"POST /test\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("service"), eq(testMethod + " " + testUri))).thenReturn(testMethod + " " + testUri);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_WithQueryString_correctQueryStringLogging() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"POST /test?name=mina&description=test\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=mina&description=test";
        String result = testMethod + " " + testUri + "?" + queryString;
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("mina");
        when(replaceHelperDecider.replace(eq("description"), eq("test"))).thenReturn("test");
        when(replaceHelperDecider.replace(eq("service"), eq(result))).thenReturn(result);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_WithSensitiveQueryString_correctQueryStringLogging() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"POST /test?name=maskedValue&description=maskedVal2\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=mina&description=test";
        String result = testMethod + " " + testUri + "?" + "name=maskedValue&description=maskedVal2";
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("maskedValue");
        when(replaceHelperDecider.replace(eq("description"), eq("test"))).thenReturn("maskedVal2");
        when(replaceHelperDecider.replace(eq("service"), eq(result))).thenReturn(result);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_emptyQueryString_correctQueryStringLogging() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"POST /test?\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "";
        String result = testMethod + " " + testUri + "?";
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("service"), eq(result))).thenReturn(result);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_oneQueryString_correctQueryStringLogging() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"POST /test?name=mina\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=mina";
        String result = testMethod + " " + testUri + "?" + queryString;
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("mina");
        when(replaceHelperDecider.replace(eq("service"), eq(result))).thenReturn(result);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_wrongFormatOfQueryString_logQueryStringWithNoChange() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"POST /test?name=m=ina\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        String queryString = "name=m=ina";
        String result = testMethod + " " + testUri + "?" + queryString;
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getHeaderNames()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("service"), eq(result))).thenReturn(result);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withSensitiveHeaderStringType_returnReplacerResult() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"authorization\" : \"authorization: maskedVal**\"\n" +
                "  }\n" +
                "}";
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
        String result = headerName + ": " + maskedValue;
        when(replaceHelperDecider.replace(eq(headerName), eq(sensitiveValue))).thenReturn(maskedValue);
        when(replaceHelperDecider.replace(eq(headerName), eq(maskedValue))).thenReturn(result);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withHeaderContainingUrlWithoutQueryParam_returnUnmaskedUrlAsHeader() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"referer\" : \"https://192.168.107.9:8090/api/payman/PaymanReturn\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        Enumeration<String> headerNames = mock(Enumeration.class);
        when(headerNames.hasMoreElements()).thenReturn(true).thenReturn(false);
        String headerName = "referer";
        when(headerNames.nextElement()).thenReturn(headerName);
        when(request.getHeaderNames()).thenReturn(headerNames);

        Enumeration<String> headerValues = mock(Enumeration.class);
        when(headerValues.hasMoreElements()).thenReturn(true).thenReturn(false);
        String urlValue = "https://192.168.107.9:8090/api/payman/PaymanReturn";
        when(headerValues.nextElement()).thenReturn(urlValue);
        when(request.getHeaders(anyString())).thenReturn(headerValues);
        when(replaceHelperDecider.replace(eq(headerName), eq(urlValue))).thenReturn(urlValue);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withHeaderContainingUrlWithUnsensitiveQueryParams_returnUnmaskedUrlAsHeader() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"referer\" : \"https://192.168.107.9:8090/api/payman/PaymanReturn?name=mina\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        Enumeration<String> headerNames = mock(Enumeration.class);
        when(headerNames.hasMoreElements()).thenReturn(true).thenReturn(false);
        String headerName = "referer";
        when(headerNames.nextElement()).thenReturn(headerName);
        when(request.getHeaderNames()).thenReturn(headerNames);

        Enumeration<String> headerValues = mock(Enumeration.class);
        when(headerValues.hasMoreElements()).thenReturn(true).thenReturn(false);
        String urlValue = "https://192.168.107.9:8090/api/payman/PaymanReturn?name=mina";
        when(headerValues.nextElement()).thenReturn(urlValue);
        when(request.getHeaders(anyString())).thenReturn(headerValues);

        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("mina");
        when(replaceHelperDecider.replace(eq(headerName), eq(urlValue))).thenReturn(urlValue);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withHeaderContainingUrlWithSensitiveQueryParams_returnMaskedUrlAsHeader() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"referer\" : \"https://customer.com/api/payman/PaymanReturn?name=mina&secret=1**&family=kh\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(inputStream.getInputByteArray()).thenReturn(new byte[0]);
        Enumeration<String> headerNames = mock(Enumeration.class);
        when(headerNames.hasMoreElements()).thenReturn(true).thenReturn(false);
        String headerName = "referer";
        when(headerNames.nextElement()).thenReturn(headerName);
        when(request.getHeaderNames()).thenReturn(headerNames);

        Enumeration<String> headerValues = mock(Enumeration.class);
        when(headerValues.hasMoreElements()).thenReturn(true).thenReturn(false);
        String urlValue = "https://customer.com/api/payman/PaymanReturn?name=mina&secret=1234&family=kh";
        when(headerValues.nextElement()).thenReturn(urlValue);
        when(request.getHeaders(anyString())).thenReturn(headerValues);
        String headerValue = "https://customer.com/api/payman/PaymanReturn?name=mina&secret=1**&family=kh";
        when(replaceHelperDecider.replace(eq("name"), eq("mina"))).thenReturn("mina");
        when(replaceHelperDecider.replace(eq("secret"), eq("1234"))).thenReturn("1**");
        when(replaceHelperDecider.replace(eq("family"), eq("kh"))).thenReturn("kh");
        when(replaceHelperDecider.replace(eq(headerName), eq(headerValue))).thenReturn(headerValue);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withJsonHeader_maskHeader() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "\"context\" : \"{\\\"password\\\":\\\"**ENCRYPTED\\\"}\"\n" +
                "  }\n" +
                "}";
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
        when(replaceHelperDecider.replace(eq("context"), any())).thenReturn(maskedValue);

        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withNormalHeader_addRawHeader() throws IOException, JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : \"simpleValue\",\n" +
                "    \"sampleHeader\" : \"simpleValue\"\n" +
                "  }\n" +
                "}";
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
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withInvisibleBodyType_showBodyLength() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"unsupported media type\" : {\n" +
                "      \"type\" : null,\n" +
                "      \"subtype\" : null,\n" +
                "      \"parameters\" : { },\n" +
                "      \"qualityValue\" : 1.0,\n" +
                "      \"wildcardType\" : false,\n" +
                "      \"concrete\" : true,\n" +
                "      \"wildcardSubtype\" : false\n" +
                "    }\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[1]);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("multipart/mixed");

        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withApplicationJsonType_showMaskedJson() throws IOException, JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"body\" : \"maskedJson\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn(new byte[1]);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/json");
        String maskedJson = "maskedJson";
        when(replaceHelperDecider.replace(anyString())).thenReturn(maskedJson);
        when(replaceHelperDecider.replace(eq("body"), anyString())).thenReturn(maskedJson);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withApplicationTextType_showOriginalText() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"body\" : \"test\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(inputStream.getInputByteArray()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("text/plain");
        when(replaceHelperDecider.replace(eq("body"), anyString())).thenReturn("test");
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
        verify(replaceHelperDecider, times(0)).replace(anyString());
    }

    @Test
    public void testLogRequest_withFormUrlEncodedTypeWithNullParameterMap_emptyParameters() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"form parameters\" : null\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(request.getParameterMap()).thenReturn(null);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withFormUrlEncodedTypeWithEmptyValues_logParameterKey() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"form parameters\" : \"{testKey=null}\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        Map<String, String[]> parameterMap = new HashMap<>();
        String parameterKey = "testKey";
        parameterMap.put(parameterKey, null);
        when(request.getParameterMap()).thenReturn(parameterMap);
        when(replaceHelperDecider.replace(eq("form parameters"), anyString())).thenReturn(parameterMap.toString());
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogRequest_withFormUrlEncodedTypeWithFilledValues_logMaskedParameterValues() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"form parameters\" : \"{testKey=[testValue1, testValue2]}\"\n" +
                "  }\n" +
                "}";
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
        Map<String, List<String>> resultMap = new HashMap<>();
        List<String> value = new ArrayList<>();
        value.add(testValue1);
        value.add(testValue2);
        resultMap.put(parameterKey, value);
        when(request.getParameterMap()).thenReturn(parameterMap);
        String maskedValue1 = "maskedValue1";
        when(replaceHelperDecider.replace(eq(parameterKey), eq(testValue1))).thenReturn(maskedValue1);
        String maskedValue2 = "maskedValue2";
        when(replaceHelperDecider.replace(eq(parameterKey), eq(testValue2))).thenReturn(maskedValue2);

        when(replaceHelperDecider.replace(eq("form parameters"), anyString())).thenReturn(resultMap.toString());
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
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
    public void testLogRequest_withInvalidBody_logErrorInRequestBody() throws IOException, JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Request\" : {\n" +
                "    \"service\" : null,\n" +
                "    \"error in request body reading\" : \"Exception occurred\"\n" +
                "  }\n" +
                "}";
        String exceptionMessage = "Exception occurred";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(request.getContentType()).thenReturn("application/json");
        when(request.getQueryString()).thenReturn(null);
        when(request.getInputStream()).thenThrow(new IOException(exceptionMessage));
        when(request.getHeaderNames()).thenReturn(null);
        when(replaceHelperDecider.replace(eq("error in request body reading"), eq(exceptionMessage))).thenReturn(exceptionMessage);
        httpLogUtil.logRequest(request);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogResponse_withResponseHeader_correctResponseHeaderLog() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : \"200 OK\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[0]);
        when(replaceHelperDecider.replace(eq("status"), eq(responseStatus + " OK"))).thenReturn(responseStatus + " OK");
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogResponse_withSensitiveResponseHeader_maskSensitiveHeader() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : \"200 OK\",\n" +
                "    \"authorization\" : \"**ENCRYPTED\"\n" +
                "  }\n" +
                "}";
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
        when(replaceHelperDecider.replace(eq("status"), eq("200 OK"))).thenReturn("200 OK");
        when(replaceHelperDecider.replace(eq(headerName), eq(maskedValue))).thenReturn(maskedValue);
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogResponse_withJsonHeader_maskHeader() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : \"200 OK\",\n" +
                "    \"context\" : \"**ENCRYPTED\"\n" +
                "  }\n" +
                "}";
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
        when(replaceHelperDecider.replace(eq("status"), eq("200 OK"))).thenReturn("200 OK");
        when(replaceHelperDecider.replace(eq(headerName), eq(maskedValue))).thenReturn(maskedValue);
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogResponse_withNormalHeader_addRawHeader() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : \"200 OK\",\n" +
                "    \"sampleHeader\" : \"simpleValue\"\n" +
                "  }\n" +
                "}";
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
        when(replaceHelperDecider.replace(eq("status"), eq("200 OK"))).thenReturn("200 OK");
        when(replaceHelperDecider.replace(eq(sampleHeaderName), eq(value))).thenReturn(value);
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogResponse_withInvisibleBodyType_showBodyLength() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : null,\n" +
                "    \"content bytes\" : 1\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[1]);

        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentType()).thenReturn("multipart/mixed");
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogResponse_withApplicationJsonType_showMaskedJson() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : null,\n" +
                "    \"body\" : \"maskedJson\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn(new byte[1]);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentType()).thenReturn("application/json");
        String maskedJson = "maskedJson";
        when(replaceHelperDecider.replace(anyString())).thenReturn(maskedJson);
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
    }

    @Test
    public void testLogResponse_withApplicationTextType_showOriginalText() throws JSONException {
        final String expectedJson = "{\n" +
                "  \"Http Response\" : {\n" +
                "    \"status\" : null,\n" +
                "    \"body\" : \"test\"\n" +
                "  }\n" +
                "}";
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpLogUtil.class);
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentAsByteArray()).thenReturn("test".getBytes(StandardCharsets.UTF_8));
        when(response.getHeaderNames()).thenReturn(null);
        when(response.getContentType()).thenReturn("text/plain");
        httpLogUtil.logResponse(response);
        String message = listAppender.list.get(0).getMessage();
        assertEquals(expectedJson, message, false);
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