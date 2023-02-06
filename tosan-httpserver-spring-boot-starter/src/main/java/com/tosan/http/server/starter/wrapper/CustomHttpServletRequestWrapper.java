package com.tosan.http.server.starter.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;

/**
 * @author M.khoshnevisan
 * @since 4/21/2021
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private ByteArrayOutputStream cachedBytes;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public CachedServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null) {
            cacheInputStream();
        }
        return new CachedServletInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private void cacheInputStream() throws IOException {
        cachedBytes = new ByteArrayOutputStream();
        copy(super.getInputStream(), cachedBytes);
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) != -1) {
            target.write(buf, 0, length);
        }
    }

    public class CachedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream input;
        byte[] byteArray = cachedBytes.toByteArray();

        public CachedServletInputStream() {
            input = new ByteArrayInputStream(byteArray);
        }

        public byte[] getInputByteArray() {
            return byteArray;
        }

        @Override
        public int read() {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }
}