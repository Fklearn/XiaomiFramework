package miui.cloud.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

public class XByteArrayProcessor implements XHttpClient.ISendDataProcessor, XHttpClient.IReceiveDataProcessor {
    public static final XAutoAdaptProcessor.DataProcessorCreator CREATOR = new XAutoAdaptProcessor.DataProcessorCreator() {
        public XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2) {
            if (str.equals(XByteArrayProcessor.MIME_TYPE)) {
                return new XByteArrayProcessor();
            }
            return null;
        }

        public XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str) {
            if (obj instanceof byte[]) {
                return new XByteArrayProcessor();
            }
            return null;
        }
    };
    private static int IN_DATA_PROCESSING_BUFFER_LEN = 256;
    /* access modifiers changed from: private */
    public static String MIME_TYPE = "application/octet-stream";

    protected static int getContentLengthFromHeader(Map<String, List<String>> map) {
        String str;
        List<String> list = map.get("Content-Encoding");
        if (list != null) {
            for (String equals : list) {
                if (!equals.equals("identity")) {
                    return -1;
                }
            }
        }
        List list2 = map.get("Content-Length");
        if (list2 == null || list2.isEmpty() || (str = (String) list2.get(0)) == null) {
            return -1;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    public String getOutDataContentType(Object obj) {
        return MIME_TYPE;
    }

    public int getOutDataLength(Object obj) {
        return ((byte[]) obj).length;
    }

    public Object processInData(Map<String, List<String>> map, InputStream inputStream) {
        int read;
        int contentLengthFromHeader = getContentLengthFromHeader(map);
        int i = 0;
        if (contentLengthFromHeader < 0) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[IN_DATA_PROCESSING_BUFFER_LEN];
            while (true) {
                int read2 = inputStream.read(bArr);
                if (read2 > 0) {
                    byteArrayOutputStream.write(bArr, 0, read2);
                } else {
                    byteArrayOutputStream.close();
                    return byteArrayOutputStream.toByteArray();
                }
            }
        } else {
            byte[] bArr2 = new byte[contentLengthFromHeader];
            do {
                read = inputStream.read(bArr2, i, contentLengthFromHeader - i);
                if (read <= 0 || (i = i + read) == contentLengthFromHeader) {
                }
                read = inputStream.read(bArr2, i, contentLengthFromHeader - i);
                break;
            } while ((i = i + read) == contentLengthFromHeader);
            return bArr2;
        }
    }

    public void processOutData(Object obj, OutputStream outputStream) {
        outputStream.write((byte[]) obj);
    }
}
