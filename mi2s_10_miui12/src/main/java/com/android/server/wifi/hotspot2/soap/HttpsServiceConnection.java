package com.android.server.wifi.hotspot2.soap;

import android.net.Network;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.ksoap2.HeaderProperty;
import org.ksoap2.transport.ServiceConnection;

public class HttpsServiceConnection implements ServiceConnection {
    public static final int DEFAULT_TIMEOUT_MS = 5000;
    private HttpsURLConnection mConnection;

    public HttpsServiceConnection(Network network, URL url) throws IOException {
        this.mConnection = (HttpsURLConnection) network.openConnection(url);
        this.mConnection.setConnectTimeout(5000);
        this.mConnection.setReadTimeout(5000);
    }

    public void connect() throws IOException {
        this.mConnection.connect();
    }

    public void disconnect() {
        this.mConnection.disconnect();
    }

    public List<HeaderProperty> getResponseProperties() {
        Map<String, List<String>> properties = this.mConnection.getHeaderFields();
        Set<String> keys = properties.keySet();
        List<HeaderProperty> retList = new ArrayList<>();
        keys.forEach(new Consumer(properties, retList) {
            private final /* synthetic */ Map f$0;
            private final /* synthetic */ List f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                ((List) this.f$0.get((String) obj)).forEach(new Consumer(this.f$1, (String) obj) {
                    private final /* synthetic */ List f$0;
                    private final /* synthetic */ String f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        this.f$0.add(new HeaderProperty(this.f$1, (String) obj));
                    }
                });
            }
        });
        return retList;
    }

    public int getResponseCode() throws IOException {
        return this.mConnection.getResponseCode();
    }

    public void setRequestProperty(String propertyName, String value) {
        if (!TextUtils.equals("Connection", propertyName) || !TextUtils.equals("close", value)) {
            this.mConnection.setRequestProperty(propertyName, value);
        }
    }

    public void setRequestMethod(String requestMethodType) throws IOException {
        this.mConnection.setRequestMethod(requestMethodType);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        this.mConnection.setFixedLengthStreamingMode(contentLength);
    }

    public void setChunkedStreamingMode() {
        this.mConnection.setChunkedStreamingMode(0);
    }

    public OutputStream openOutputStream() throws IOException {
        return this.mConnection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return this.mConnection.getInputStream();
    }

    public InputStream getErrorStream() {
        return this.mConnection.getErrorStream();
    }

    public String getHost() {
        return this.mConnection.getURL().getHost();
    }

    public int getPort() {
        return this.mConnection.getURL().getPort();
    }

    public String getPath() {
        return this.mConnection.getURL().getPath();
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.mConnection.setSSLSocketFactory(sslSocketFactory);
    }
}
