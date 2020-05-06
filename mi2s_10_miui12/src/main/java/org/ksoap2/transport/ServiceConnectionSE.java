package org.ksoap2.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.ksoap2.HeaderProperty;

public class ServiceConnectionSE implements ServiceConnection {
    private HttpURLConnection connection;

    public ServiceConnectionSE(String url) throws IOException {
        this((Proxy) null, url, 20000);
    }

    public ServiceConnectionSE(Proxy proxy, String url) throws IOException {
        this(proxy, url, 20000);
    }

    public ServiceConnectionSE(String url, int timeout) throws IOException {
        this((Proxy) null, url, timeout);
    }

    public ServiceConnectionSE(Proxy proxy, String url, int timeout) throws IOException {
        HttpURLConnection httpURLConnection;
        if (proxy == null) {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        } else {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection(proxy);
        }
        this.connection = httpURLConnection;
        this.connection.setUseCaches(false);
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
        this.connection.setConnectTimeout(timeout);
        this.connection.setReadTimeout(timeout);
    }

    public void connect() throws IOException {
        this.connection.connect();
    }

    public void disconnect() {
        this.connection.disconnect();
    }

    public List getResponseProperties() throws IOException {
        List retList = new LinkedList();
        Map properties = this.connection.getHeaderFields();
        if (properties != null) {
            for (String key : properties.keySet()) {
                List values = (List) properties.get(key);
                for (int j = 0; j < values.size(); j++) {
                    retList.add(new HeaderProperty(key, (String) values.get(j)));
                }
            }
        }
        return retList;
    }

    public int getResponseCode() throws IOException {
        return this.connection.getResponseCode();
    }

    public void setRequestProperty(String string, String soapAction) {
        this.connection.setRequestProperty(string, soapAction);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        this.connection.setRequestMethod(requestMethod);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        this.connection.setFixedLengthStreamingMode(contentLength);
    }

    public void setChunkedStreamingMode() {
        this.connection.setChunkedStreamingMode(0);
    }

    public OutputStream openOutputStream() throws IOException {
        return this.connection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return this.connection.getInputStream();
    }

    public InputStream getErrorStream() {
        return this.connection.getErrorStream();
    }

    public String getHost() {
        return this.connection.getURL().getHost();
    }

    public int getPort() {
        return this.connection.getURL().getPort();
    }

    public String getPath() {
        return this.connection.getURL().getPath();
    }
}
