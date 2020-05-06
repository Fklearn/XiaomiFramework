package miui.cloud.net;

import android.text.TextUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import miui.cloud.common.XBlockCallback;
import miui.cloud.common.XCallback;
import miui.cloud.common.XLogger;
import miui.cloud.common.XWrapper;

public final class XHttpClient {
    private static final boolean DEBUG = true;
    private static final String DEFAULT_OUT_ENCODING = "utf-8";
    private static final int DEFAULT_RUNNING_TASKS = 5;
    private static final int HTTP_STATUS_OK_CODE = 200;
    private static final int REQUEST_TIME_OUT = 30000;
    private static final int TEST_RESPONSE_DELAY = 200;
    private static final int TEST_RESPONSE_STATUS_CODE = 1024;
    private static final String TEST_RESPONSE_STATUS_MSG = "TEST OK";
    private static final String TEST_URL = "[TEST]";
    /* access modifiers changed from: private */
    public volatile DataProcessorFactor mDataProcessorFactor = new DataProcessorFactor();
    private int mMaxRuningTaskCount = 5;
    private LinkedList<HttpRequest> mPendingTasks = new LinkedList<>();
    private int mRunningTaskCount = 0;
    /* access modifiers changed from: private */
    public volatile IUserAgentNameProvider mUserAgentNameProvider = null;

    public static class DataConversionException extends Exception {
        DataConversionException() {
        }

        DataConversionException(String str) {
            super(str);
        }

        DataConversionException(String str, Throwable th) {
            super(str, th);
        }

        DataConversionException(Throwable th) {
            super(th);
        }
    }

    public static class DataProcessorFactor {
        public IReceiveDataProcessor getReceiveDataProcessor(Map<String, List<String>> map, InputStream inputStream) {
            return new XReceiveDataAutoAdaptProcessor();
        }

        public ISendDataProcessor getSendDataProcessor(String str, Object obj) {
            return new XSendDataAutoAdaptProcessor(str);
        }
    }

    private class HttpRequest implements Runnable {
        private XCallback<IResponseHandler> mCallback;
        private Object mCtx;
        private Object mData;
        private Map<String, List<String>> mHeader;
        private String mMethod;
        private IReceiveDataProcessor mReceiveDataProcessor;
        private HttpResponse mResponse = new HttpResponse();
        private ISendDataProcessor mSendDataProcessor;
        private String mUrl;

        public HttpRequest(String str, String str2, Map<String, List<String>> map, Object obj, ISendDataProcessor iSendDataProcessor, IReceiveDataProcessor iReceiveDataProcessor, XCallback<IResponseHandler> xCallback, Object obj2) {
            this.mMethod = str;
            this.mUrl = str2;
            this.mHeader = map;
            this.mData = obj;
            this.mSendDataProcessor = iSendDataProcessor;
            this.mReceiveDataProcessor = iReceiveDataProcessor;
            this.mCallback = xCallback;
            this.mCtx = obj2;
        }

        private void prepareConn(URLConnection uRLConnection) {
            uRLConnection.setConnectTimeout(XHttpClient.REQUEST_TIME_OUT);
            uRLConnection.setReadTimeout(XHttpClient.REQUEST_TIME_OUT);
            if (XHttpClient.this.mUserAgentNameProvider != null) {
                String userAgent = XHttpClient.this.mUserAgentNameProvider.getUserAgent();
                if (!TextUtils.isEmpty(userAgent)) {
                    uRLConnection.setRequestProperty("User-Agent", userAgent);
                }
            }
        }

        private void setRequestHeader(HttpURLConnection httpURLConnection, Map<String, List<String>> map) {
            if (map != null) {
                for (String next : map.keySet()) {
                    httpURLConnection.setRequestProperty(next, TextUtils.join(", ", map.get(next).toArray(new String[0])));
                }
            }
        }

        /* JADX WARNING: type inference failed for: r0v0 */
        /* JADX WARNING: type inference failed for: r0v27, types: [java.net.HttpURLConnection] */
        /* JADX WARNING: type inference failed for: r0v33, types: [java.util.Date] */
        /* JADX WARNING: type inference failed for: r0v36 */
        /* JADX WARNING: type inference failed for: r0v40 */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x0104, code lost:
            if (r0 != null) goto L_0x016e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x012e, code lost:
            if (r0 != null) goto L_0x016e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x014d, code lost:
            if (r0 != null) goto L_0x016e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:76:0x016c, code lost:
            if (r0 != null) goto L_0x016e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:77:0x016e, code lost:
            r0.asInterface().handleHttpResponse(r13.mResponse);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:78:0x0179, code lost:
            r4 = java.lang.System.currentTimeMillis() - r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:79:0x0183, code lost:
            if (r13.mResponse.error != null) goto L_0x019c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:80:0x0185, code lost:
            r10 = com.xiaomi.micloudsdk.stat.c.a();
            r1 = r13.mUrl;
            r0 = r13.mResponse;
            r10.a(new com.xiaomi.micloudsdk.stat.NetSuccessStatParam(r1, r2, r4, r0.contentLength, r0.stateCode, 0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x019c, code lost:
            com.xiaomi.micloudsdk.stat.c.a().a(new com.xiaomi.micloudsdk.stat.NetFailedStatParam(r13.mUrl, r2, r4, r13.mResponse.error, 0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:89:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:90:?, code lost:
            return;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0123  */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x0142  */
        /* JADX WARNING: Removed duplicated region for block: B:74:0x0161  */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x01b4  */
        /* JADX WARNING: Removed duplicated region for block: B:87:0x01c1  */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:71:0x0154=Splitter:B:71:0x0154, B:63:0x0135=Splitter:B:63:0x0135, B:55:0x0116=Splitter:B:55:0x0116} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void doHttpRequest() {
            /*
                r13 = this;
                long r2 = java.lang.System.currentTimeMillis()
                r0 = 0
                r1 = 0
                r4 = 1
                java.lang.String r5 = r13.mUrl     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                java.lang.String r6 = "[TEST]"
                boolean r5 = r5.equals(r6)     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                if (r5 == 0) goto L_0x0030
                miui.cloud.net.XHttpClient$HttpResponse r5 = r13.mResponse     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                r6 = 1024(0x400, float:1.435E-42)
                r5.stateCode = r6     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                miui.cloud.net.XHttpClient$HttpResponse r5 = r13.mResponse     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                java.lang.String r6 = "TEST OK"
                r5.stateMessage = r6     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                miui.cloud.net.XHttpClient$HttpResponse r5 = r13.mResponse     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> r6 = r13.mHeader     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                r5.headers = r6     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                miui.cloud.net.XHttpClient$HttpResponse r5 = r13.mResponse     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                java.lang.Object r6 = r13.mData     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                r5.content = r6     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                r5 = 200(0xc8, double:9.9E-322)
                java.lang.Thread.sleep(r5)     // Catch:{ InterruptedException -> 0x00f7 }
                goto L_0x00f7
            L_0x0030:
                java.net.URL r5 = new java.net.URL     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                java.lang.String r6 = r13.mUrl     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                r5.<init>(r6)     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                java.net.URLConnection r5 = r5.openConnection()     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                java.net.HttpURLConnection r5 = (java.net.HttpURLConnection) r5     // Catch:{ MalformedURLException -> 0x0150, IOException -> 0x0131, DataConversionException -> 0x0112, all -> 0x010e }
                r13.prepareConn(r5)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.lang.String r6 = r13.mMethod     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r5.setRequestMethod(r6)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> r6 = r13.mHeader     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r13.setRequestHeader(r5, r6)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.lang.String r6 = "Connection"
                java.lang.String r7 = "close"
                r5.addRequestProperty(r6, r7)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.lang.Object r6 = r13.mData     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                if (r6 == 0) goto L_0x008a
                r5.setDoOutput(r4)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$ISendDataProcessor r6 = r13.mSendDataProcessor     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.lang.Object r7 = r13.mData     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                int r6 = r6.getOutDataLength(r7)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                if (r6 >= 0) goto L_0x0066
                r5.setChunkedStreamingMode(r1)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                goto L_0x0069
            L_0x0066:
                r5.setFixedLengthStreamingMode(r6)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
            L_0x0069:
                java.lang.String r6 = "Content-Type"
                miui.cloud.net.XHttpClient$ISendDataProcessor r7 = r13.mSendDataProcessor     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.lang.Object r8 = r13.mData     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.lang.String r7 = r7.getOutDataContentType(r8)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r5.setRequestProperty(r6, r7)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.io.OutputStream r6 = r5.getOutputStream()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$ISendDataProcessor r7 = r13.mSendDataProcessor     // Catch:{ all -> 0x0085 }
                java.lang.Object r8 = r13.mData     // Catch:{ all -> 0x0085 }
                r7.processOutData(r8, r6)     // Catch:{ all -> 0x0085 }
                r6.close()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                goto L_0x008a
            L_0x0085:
                r0 = move-exception
                r6.close()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                throw r0     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
            L_0x008a:
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                int r7 = r5.getResponseCode()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r6.stateCode = r7     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.lang.String r7 = r5.getResponseMessage()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r6.stateMessage = r7     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                int r7 = r5.getContentLength()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                long r7 = (long) r7     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r6.contentLength = r7     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                java.util.Map r7 = r5.getHeaderFields()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r6.headers = r7     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                long r6 = r5.getDate()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$HttpResponse r8 = r13.mResponse     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r9 = 0
                int r9 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r9 != 0) goto L_0x00b8
                goto L_0x00bd
            L_0x00b8:
                java.util.Date r0 = new java.util.Date     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r0.<init>(r6)     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
            L_0x00bd:
                r8.date = r0     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                int r0 = r0.stateCode     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                r6 = 200(0xc8, float:2.8E-43)
                if (r0 != r6) goto L_0x00f6
                java.io.InputStream r0 = r5.getInputStream()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                miui.cloud.net.XHttpClient$IReceiveDataProcessor r6 = r13.mReceiveDataProcessor     // Catch:{ all -> 0x00f1 }
                if (r6 != 0) goto L_0x00df
                miui.cloud.net.XHttpClient r6 = miui.cloud.net.XHttpClient.this     // Catch:{ all -> 0x00f1 }
                miui.cloud.net.XHttpClient$DataProcessorFactor r6 = r6.mDataProcessorFactor     // Catch:{ all -> 0x00f1 }
                miui.cloud.net.XHttpClient$HttpResponse r7 = r13.mResponse     // Catch:{ all -> 0x00f1 }
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> r7 = r7.headers     // Catch:{ all -> 0x00f1 }
                miui.cloud.net.XHttpClient$IReceiveDataProcessor r6 = r6.getReceiveDataProcessor(r7, r0)     // Catch:{ all -> 0x00f1 }
                r13.mReceiveDataProcessor = r6     // Catch:{ all -> 0x00f1 }
            L_0x00df:
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ all -> 0x00f1 }
                miui.cloud.net.XHttpClient$IReceiveDataProcessor r7 = r13.mReceiveDataProcessor     // Catch:{ all -> 0x00f1 }
                miui.cloud.net.XHttpClient$HttpResponse r8 = r13.mResponse     // Catch:{ all -> 0x00f1 }
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> r8 = r8.headers     // Catch:{ all -> 0x00f1 }
                java.lang.Object r7 = r7.processInData(r8, r0)     // Catch:{ all -> 0x00f1 }
                r6.content = r7     // Catch:{ all -> 0x00f1 }
                r0.close()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                goto L_0x00f6
            L_0x00f1:
                r6 = move-exception
                r0.close()     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
                throw r6     // Catch:{ MalformedURLException -> 0x010c, IOException -> 0x010a, DataConversionException -> 0x0108 }
            L_0x00f6:
                r0 = r5
            L_0x00f7:
                if (r0 == 0) goto L_0x00fc
                r0.disconnect()
            L_0x00fc:
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                java.lang.Object r1 = r13.mCtx
                r0.ctx = r1
                miui.cloud.common.XCallback<miui.cloud.net.XHttpClient$IResponseHandler> r0 = r13.mCallback
                if (r0 == 0) goto L_0x0179
                goto L_0x016e
            L_0x0108:
                r0 = move-exception
                goto L_0x0116
            L_0x010a:
                r0 = move-exception
                goto L_0x0135
            L_0x010c:
                r0 = move-exception
                goto L_0x0154
            L_0x010e:
                r1 = move-exception
                r5 = r0
                goto L_0x01b2
            L_0x0112:
                r5 = move-exception
                r12 = r5
                r5 = r0
                r0 = r12
            L_0x0116:
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ all -> 0x01b1 }
                r6.error = r0     // Catch:{ all -> 0x01b1 }
                java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x01b1 }
                r4[r1] = r0     // Catch:{ all -> 0x01b1 }
                miui.cloud.common.XLogger.loge(r4)     // Catch:{ all -> 0x01b1 }
                if (r5 == 0) goto L_0x0126
                r5.disconnect()
            L_0x0126:
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                java.lang.Object r1 = r13.mCtx
                r0.ctx = r1
                miui.cloud.common.XCallback<miui.cloud.net.XHttpClient$IResponseHandler> r0 = r13.mCallback
                if (r0 == 0) goto L_0x0179
                goto L_0x016e
            L_0x0131:
                r5 = move-exception
                r12 = r5
                r5 = r0
                r0 = r12
            L_0x0135:
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ all -> 0x01b1 }
                r6.error = r0     // Catch:{ all -> 0x01b1 }
                java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x01b1 }
                r4[r1] = r0     // Catch:{ all -> 0x01b1 }
                miui.cloud.common.XLogger.loge(r4)     // Catch:{ all -> 0x01b1 }
                if (r5 == 0) goto L_0x0145
                r5.disconnect()
            L_0x0145:
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                java.lang.Object r1 = r13.mCtx
                r0.ctx = r1
                miui.cloud.common.XCallback<miui.cloud.net.XHttpClient$IResponseHandler> r0 = r13.mCallback
                if (r0 == 0) goto L_0x0179
                goto L_0x016e
            L_0x0150:
                r5 = move-exception
                r12 = r5
                r5 = r0
                r0 = r12
            L_0x0154:
                miui.cloud.net.XHttpClient$HttpResponse r6 = r13.mResponse     // Catch:{ all -> 0x01b1 }
                r6.error = r0     // Catch:{ all -> 0x01b1 }
                java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x01b1 }
                r4[r1] = r0     // Catch:{ all -> 0x01b1 }
                miui.cloud.common.XLogger.loge(r4)     // Catch:{ all -> 0x01b1 }
                if (r5 == 0) goto L_0x0164
                r5.disconnect()
            L_0x0164:
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                java.lang.Object r1 = r13.mCtx
                r0.ctx = r1
                miui.cloud.common.XCallback<miui.cloud.net.XHttpClient$IResponseHandler> r0 = r13.mCallback
                if (r0 == 0) goto L_0x0179
            L_0x016e:
                java.lang.Object r0 = r0.asInterface()
                miui.cloud.net.XHttpClient$IResponseHandler r0 = (miui.cloud.net.XHttpClient.IResponseHandler) r0
                miui.cloud.net.XHttpClient$HttpResponse r1 = r13.mResponse
                r0.handleHttpResponse(r1)
            L_0x0179:
                long r0 = java.lang.System.currentTimeMillis()
                long r4 = r0 - r2
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                java.lang.Exception r0 = r0.error
                if (r0 != 0) goto L_0x019c
                com.xiaomi.micloudsdk.stat.c r10 = com.xiaomi.micloudsdk.stat.c.a()
                com.xiaomi.micloudsdk.stat.NetSuccessStatParam r11 = new com.xiaomi.micloudsdk.stat.NetSuccessStatParam
                java.lang.String r1 = r13.mUrl
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                long r6 = r0.contentLength
                int r8 = r0.stateCode
                r9 = 0
                r0 = r11
                r0.<init>(r1, r2, r4, r6, r8, r9)
                r10.a((com.xiaomi.micloudsdk.stat.NetSuccessStatParam) r11)
                goto L_0x01b0
            L_0x019c:
                com.xiaomi.micloudsdk.stat.c r8 = com.xiaomi.micloudsdk.stat.c.a()
                com.xiaomi.micloudsdk.stat.NetFailedStatParam r9 = new com.xiaomi.micloudsdk.stat.NetFailedStatParam
                java.lang.String r1 = r13.mUrl
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                java.lang.Exception r6 = r0.error
                r7 = 0
                r0 = r9
                r0.<init>(r1, r2, r4, r6, r7)
                r8.a((com.xiaomi.micloudsdk.stat.NetFailedStatParam) r9)
            L_0x01b0:
                return
            L_0x01b1:
                r1 = move-exception
            L_0x01b2:
                if (r5 == 0) goto L_0x01b7
                r5.disconnect()
            L_0x01b7:
                miui.cloud.net.XHttpClient$HttpResponse r0 = r13.mResponse
                java.lang.Object r2 = r13.mCtx
                r0.ctx = r2
                miui.cloud.common.XCallback<miui.cloud.net.XHttpClient$IResponseHandler> r0 = r13.mCallback
                if (r0 == 0) goto L_0x01cc
                java.lang.Object r0 = r0.asInterface()
                miui.cloud.net.XHttpClient$IResponseHandler r0 = (miui.cloud.net.XHttpClient.IResponseHandler) r0
                miui.cloud.net.XHttpClient$HttpResponse r2 = r13.mResponse
                r0.handleHttpResponse(r2)
            L_0x01cc:
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.cloud.net.XHttpClient.HttpRequest.doHttpRequest():void");
        }

        public void run() {
            doHttpRequest();
            XHttpClient.this.finishTask(this);
        }
    }

    public static class HttpResponse {
        public Object content;
        public long contentLength;
        public Object ctx;
        public Date date;
        public Exception error;
        public Map<String, List<String>> headers;
        public int stateCode;
        public String stateMessage;

        public String toString() {
            if (this.error != null) {
                return "Error: \n" + this.error.toString() + "\n";
            }
            Object[] objArr = new Object[5];
            objArr[0] = Integer.valueOf(this.stateCode);
            objArr[1] = this.stateMessage;
            objArr[2] = this.headers;
            Object obj = this.content;
            objArr[3] = obj == null ? null : obj.getClass();
            objArr[4] = this.content;
            return String.format("%s %s \n%s \n%s:%s", objArr);
        }
    }

    public interface IReceiveDataProcessor {
        Object processInData(Map<String, List<String>> map, InputStream inputStream);
    }

    public interface IResponseHandler {
        void handleHttpResponse(HttpResponse httpResponse);
    }

    public interface ISendDataProcessor {
        String getOutDataContentType(Object obj);

        int getOutDataLength(Object obj);

        void processOutData(Object obj, OutputStream outputStream);
    }

    public interface IUserAgentNameProvider {
        String getUserAgent();
    }

    private synchronized void addTask(HttpRequest httpRequest) {
        this.mPendingTasks.add(httpRequest);
        scheduleTasksLocked();
    }

    /* access modifiers changed from: private */
    public synchronized void finishTask(HttpRequest httpRequest) {
        this.mRunningTaskCount--;
        XLogger.log("Task--", Integer.valueOf(this.mRunningTaskCount));
        scheduleTasksLocked();
    }

    private void scheduleTasksLocked() {
        if (this.mRunningTaskCount < this.mMaxRuningTaskCount && !this.mPendingTasks.isEmpty()) {
            while (this.mRunningTaskCount < this.mMaxRuningTaskCount && !this.mPendingTasks.isEmpty()) {
                new Thread(this.mPendingTasks.getFirst()).start();
                this.mPendingTasks.removeFirst();
                this.mRunningTaskCount++;
            }
            XLogger.log("task++", Integer.valueOf(this.mRunningTaskCount));
        }
    }

    public void asyncGet(String str, Map<String, List<String>> map, XCallback<IResponseHandler> xCallback, Object obj) {
        asyncSend("GET", str, map, (Object) null, (ISendDataProcessor) null, (IReceiveDataProcessor) null, xCallback, obj);
    }

    public void asyncGet(String str, XCallback<IResponseHandler> xCallback, Object obj) {
        asyncSend("GET", str, (Map<String, List<String>>) null, (Object) null, (ISendDataProcessor) null, (IReceiveDataProcessor) null, xCallback, obj);
    }

    public void asyncPost(String str, Object obj, String str2, XCallback<IResponseHandler> xCallback, Object obj2) {
        asyncSend("POST", str, (Map<String, List<String>>) null, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), (IReceiveDataProcessor) null, xCallback, obj2);
    }

    public void asyncPost(String str, Object obj, XCallback<IResponseHandler> xCallback, Object obj2) {
        asyncSend("POST", str, (Map<String, List<String>>) null, obj, this.mDataProcessorFactor.getSendDataProcessor("utf-8", obj), (IReceiveDataProcessor) null, xCallback, obj2);
    }

    public void asyncPost(String str, Map<String, List<String>> map, Object obj, String str2, XCallback<IResponseHandler> xCallback, Object obj2) {
        String str3 = str2;
        String str4 = str;
        Map<String, List<String>> map2 = map;
        asyncSend("POST", str4, map2, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), (IReceiveDataProcessor) null, xCallback, obj2);
    }

    public void asyncPost(String str, Map<String, List<String>> map, Object obj, XCallback<IResponseHandler> xCallback, Object obj2) {
        asyncSend("POST", str, map, obj, this.mDataProcessorFactor.getSendDataProcessor("utf-8", obj), (IReceiveDataProcessor) null, xCallback, obj2);
    }

    public void asyncSend(String str, String str2, Map<String, List<String>> map, Object obj, ISendDataProcessor iSendDataProcessor, IReceiveDataProcessor iReceiveDataProcessor, XCallback<IResponseHandler> xCallback, Object obj2) {
        addTask(new HttpRequest(str, str2, map, obj, iSendDataProcessor, iReceiveDataProcessor, xCallback, obj2));
    }

    public void setDataProcessorFactor(DataProcessorFactor dataProcessorFactor) {
        if (dataProcessorFactor != null) {
            this.mDataProcessorFactor = dataProcessorFactor;
            return;
        }
        throw new NullPointerException();
    }

    public synchronized void setMaxRunningTasks(int i) {
        this.mMaxRuningTaskCount = i;
        scheduleTasksLocked();
    }

    public void setUserAgentNameProvider(IUserAgentNameProvider iUserAgentNameProvider) {
        this.mUserAgentNameProvider = iUserAgentNameProvider;
    }

    public HttpResponse syncGet(String str) {
        return syncSend("GET", str, (Map<String, List<String>>) null, (Object) null, (ISendDataProcessor) null, (IReceiveDataProcessor) null);
    }

    public HttpResponse syncGet(String str, Map<String, List<String>> map) {
        return syncSend("GET", str, map, (Object) null, (ISendDataProcessor) null, (IReceiveDataProcessor) null);
    }

    public HttpResponse syncPost(String str, Object obj) {
        return syncSend("POST", str, (Map<String, List<String>>) null, obj, this.mDataProcessorFactor.getSendDataProcessor("utf-8", obj), (IReceiveDataProcessor) null);
    }

    public HttpResponse syncPost(String str, Object obj, String str2) {
        return syncSend("POST", str, (Map<String, List<String>>) null, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), (IReceiveDataProcessor) null);
    }

    public HttpResponse syncPost(String str, Map<String, List<String>> map, Object obj) {
        return syncSend("POST", str, map, obj, this.mDataProcessorFactor.getSendDataProcessor("utf-8", obj), (IReceiveDataProcessor) null);
    }

    public HttpResponse syncPost(String str, Map<String, List<String>> map, Object obj, String str2) {
        return syncSend("POST", str, map, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), (IReceiveDataProcessor) null);
    }

    public HttpResponse syncSend(String str, String str2, Map<String, List<String>> map, Object obj, ISendDataProcessor iSendDataProcessor, IReceiveDataProcessor iReceiveDataProcessor) {
        final XWrapper xWrapper = new XWrapper();
        XBlockCallback xBlockCallback = new XBlockCallback(IResponseHandler.class);
        asyncSend(str, str2, map, obj, iSendDataProcessor, iReceiveDataProcessor, xBlockCallback, (Object) null);
        xBlockCallback.waitForCallBack(new IResponseHandler() {
            public void handleHttpResponse(HttpResponse httpResponse) {
                xWrapper.set(httpResponse);
            }
        });
        return (HttpResponse) xWrapper.get();
    }
}
