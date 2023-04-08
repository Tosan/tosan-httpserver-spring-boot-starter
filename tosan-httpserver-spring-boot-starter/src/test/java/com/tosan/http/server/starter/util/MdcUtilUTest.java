package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.config.HttpHeaderMdcParameter;
import com.tosan.http.server.starter.config.MdcFilterConfig;
import com.tosan.http.server.starter.config.RandomGenerationType;
import com.tosan.http.server.starter.config.RandomParameter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author mina khoshnevisan
 * @since 7/17/2022
 */
public class MdcUtilUTest {

    private MdcUtil mdcUtil;
    private MdcFilterConfig mdcFilterConfig;
    private static final MockedStatic<RequestContextHolder> requestContextHolderMockedStatic =
            mockStatic(RequestContextHolder.class);

    @BeforeEach
    public void setup() {
        mdcFilterConfig = mock(MdcFilterConfig.class);
        mdcUtil = new MdcUtil(mdcFilterConfig);
        mdcUtil.clear();
    }

    @Test
    public void testExtractHeaderMdcParameters_noFilterConfigParameters_noAction() {
        when(mdcFilterConfig.getParameters()).thenReturn(new ArrayList<>());
        HttpServletRequest request = mock(HttpServletRequest.class);
        mdcUtil.extractHeaderMdcParameters(request);
    }

    @Test
    public void testExtractHeaderMdcParameters_withFilterParameters_addParameterToMdc() {
        ArrayList<HttpHeaderMdcParameter> mdcParameters = new ArrayList<>();
        String headerName1 = "header1";
        String mdcName1 = "mdc1";
        mdcParameters.add(new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(headerName1, mdcName1).build());
        String headerName2 = "header2";
        String mdcName2 = "mdc2";
        mdcParameters.add(new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(headerName2, mdcName2).build());
        when(mdcFilterConfig.getParameters()).thenReturn(mdcParameters);
        HttpServletRequest request = mock(HttpServletRequest.class);
        String value1 = "value1";
        when(request.getHeader(headerName1)).thenReturn(value1);
        String value2 = "value2";
        when(request.getHeader(headerName2)).thenReturn(value2);
        mdcUtil.extractHeaderMdcParameters(request);
        assertEquals(value1, MDC.get(mdcName1));
        assertEquals(value2, MDC.get(mdcName2));
    }

    @Test
    public void testCheckAndApplyRandomParameter_NoRandomParameterSpecified_returnOriginalValueWithNoChange() {
        HttpHeaderMdcParameter headerMdcParameter = new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(
                "test", "test").build();
        String value = "testValue";
        String result = mdcUtil.checkAndApplyRandomParameter(headerMdcParameter, value);
        assertEquals(value, result);
    }

    @Test
    public void testCheckAndApplyRandomParameter_hasRandomParameterButValueSent_returnOriginalValue() {
        HttpHeaderMdcParameter headerMdcParameter = new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(
                "test", "test").randomParameter(new RandomParameter(
                RandomGenerationType.NUMERIC, 9)).build();
        String value = "testValue";
        String result = mdcUtil.checkAndApplyRandomParameter(headerMdcParameter, value);
        assertEquals(value, result);
    }

    @Test
    public void testCheckAndApplyRandomParameter_hasRandomParameterButNullGenerationType_returnOriginalValue() {
        HttpHeaderMdcParameter headerMdcParameter = new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(
                "test", "test").randomParameter(new RandomParameter(
                null, 9)).build();
        String result = mdcUtil.checkAndApplyRandomParameter(headerMdcParameter, null);
        assertNull(result);
    }

    @Test
    public void testCheckAndApplyRandomParameter_alphanumericRandom_createAlphaNumericValue() {
        HttpHeaderMdcParameter headerMdcParameter = new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(
                "test", "test").randomParameter(new RandomParameter(
                RandomGenerationType.ALPHANUMERIC, 5)).build();
        String result = mdcUtil.checkAndApplyRandomParameter(headerMdcParameter, null);
        assertEquals(5, result.length());
        assertFalse(result.matches("\\d+"));
    }

    @Test
    public void testCheckAndApplyRandomParameter_numericRandom_createNumericValue() {
        HttpHeaderMdcParameter headerMdcParameter = new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(
                "test", "test").randomParameter(new RandomParameter(
                RandomGenerationType.NUMERIC, 7)).build();
        String result = mdcUtil.checkAndApplyRandomParameter(headerMdcParameter, null);
        assertEquals(7, result.length());
        assertTrue(result.matches("\\d+"));
    }

    @Test
    public void testReplaceUnfreeChars_emptyValue_returnInput() {
        String input = "";
        String result = mdcUtil.replaceUnfreeChars(input);
        assertEquals(input, result);
    }

    @Test
    public void testReplaceUnfreeChars_StringWithUnfreeCharacters_replaceUnfreeCharacters() {
        char[] chars = {'*'};
        when(mdcFilterConfig.getUnFreeChars()).thenReturn(chars);
        char newChar = '-';
        when(mdcFilterConfig.getNewChar()).thenReturn(newChar);
        String input = "mina*te*st";
        String result = mdcUtil.replaceUnfreeChars(input);
        assertEquals("mina-te-st", result);
    }

    @Test
    public void testFillRemoteClientIp_nullRemoteAddressAndNullXForward_noRemoteAddressInMdc() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(eq(Constants.X_FORWARDED_FOR))).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(null);
        mdcUtil.fillRemoteClientIp(request);
        assertNull(MDC.get(Constants.MDC_CLIENT_IP));
    }

    @Test
    public void testFillRemoteClientIp_haveRemoteAddressButNoExForwarded_fillRemoteAddressInMdc() {
        String remoteAddress = "127.0.0.1";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(eq(Constants.X_FORWARDED_FOR))).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(remoteAddress);
        mdcUtil.fillRemoteClientIp(request);
        assertEquals(remoteAddress, MDC.get(Constants.MDC_CLIENT_IP));
    }

    @Test
    public void testFillRemoteClientIp_requestHaveXForwardedForHeader_fillRemoteAddressInMdc() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String xForwardHeader = "192.178.4.20";
        when(request.getHeader(eq(Constants.X_FORWARDED_FOR))).thenReturn(xForwardHeader);
        mdcUtil.fillRemoteClientIp(request);
        assertEquals(xForwardHeader, MDC.get(Constants.MDC_CLIENT_IP));
    }

    @Test
    public void testFillRemoteClientIp_requestHaveMultipleIpInXForwardedForHeader_fillRemoteAddressInMdc() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String xForwardHeader = "192.178.4.20 , 194.180.3.4";
        when(request.getHeader(eq(Constants.X_FORWARDED_FOR))).thenReturn(xForwardHeader);
        mdcUtil.fillRemoteClientIp(request);
        assertEquals("192.178.4.20", MDC.get(Constants.MDC_CLIENT_IP));
    }

    @Test
    public void testProcessMdcParameter_parameterIsReplaceUnfreeChar_checkRapalceUnfreeChars() {
        HttpHeaderMdcParameter headerMdcParam = new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(
                "test", "test").removeUnfreeCharacters(true).build();
        String value = "initialValue";
        mdcUtil.processMdcParameter(headerMdcParam, value);
        verify(mdcFilterConfig, times(1)).getUnFreeChars();
    }
}