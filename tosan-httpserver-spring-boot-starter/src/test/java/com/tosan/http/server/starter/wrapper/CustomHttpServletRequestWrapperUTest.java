package com.tosan.http.server.starter.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author M.khoshnevisan
 * @since 7/3/2021
 */
public class CustomHttpServletRequestWrapperUTest {

    private CustomHttpServletRequestWrapper customHttpServletRequestWrapper;
    private MockHttpServletRequest mockHttpServletRequest;

    @BeforeEach
    public void setup() {
        mockHttpServletRequest = new MockHttpServletRequest();
        customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper(mockHttpServletRequest);
    }

    @Test
    public void testCustomHttpServletRequestWrapper_getInputStreamMultipleTimes_correctInputStream() throws IOException {
        ServletInputStream inputStream = customHttpServletRequestWrapper.getInputStream();
        assertNotNull(inputStream);
        ServletInputStream inputStream1 = customHttpServletRequestWrapper.getInputStream();
        assertNotNull(inputStream1);
    }

    @Test
    public void testCustomHttpServletRequestWrapper_getReader_getReaderSuccessfully() throws IOException {
        BufferedReader reader = customHttpServletRequestWrapper.getReader();
        assertNotNull(reader);
    }

    @Test
    public void testGetInputByteArray_normalCall_returnCorrectByteArray() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletInputStream inputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return -1;
            }
        };
        when(request.getInputStream()).thenReturn(inputStream);
        CustomHttpServletRequestWrapper wrapper = new CustomHttpServletRequestWrapper(request);
        CustomHttpServletRequestWrapper.CachedServletInputStream wrapperInputStream = wrapper.getInputStream();
        byte[] inputByteArray = wrapperInputStream.getInputByteArray();
        assertNotNull(inputByteArray);
    }
}