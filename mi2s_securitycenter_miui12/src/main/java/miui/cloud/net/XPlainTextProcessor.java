package miui.cloud.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

public class XPlainTextProcessor implements XHttpClient.ISendDataProcessor, XHttpClient.IReceiveDataProcessor {
    public static final XAutoAdaptProcessor.DataProcessorCreator CREATOR = new XAutoAdaptProcessor.DataProcessorCreator() {
        public XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2) {
            if (str.equals(XPlainTextProcessor.MIME_TYPE)) {
                return new XPlainTextProcessor(str2);
            }
            return null;
        }

        public XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str) {
            if (obj instanceof String) {
                return new XPlainTextProcessor(str);
            }
            return null;
        }
    };
    /* access modifiers changed from: private */
    public static String MIME_TYPE = "text/plain";
    private String mBufferedString;
    private byte[] mBufferedStringByte;
    private XByteArrayProcessor mByteArrayProcessor = new XByteArrayProcessor();
    private String mEncode;

    public XPlainTextProcessor(String str) {
        this.mEncode = str;
    }

    private void bufferString(String str) {
        if (this.mBufferedString != str) {
            this.mBufferedString = str;
            this.mBufferedStringByte = new byte[0];
            try {
                this.mBufferedStringByte = str.getBytes(this.mEncode);
            } catch (UnsupportedEncodingException e) {
                throw new XHttpClient.DataConversionException((Throwable) e);
            }
        }
    }

    public String getOutDataContentType(Object obj) {
        return MIME_TYPE;
    }

    public int getOutDataLength(Object obj) {
        bufferString((String) obj);
        return this.mBufferedStringByte.length;
    }

    public Object processInData(Map<String, List<String>> map, InputStream inputStream) {
        try {
            return new String((byte[]) this.mByteArrayProcessor.processInData(map, inputStream), this.mEncode);
        } catch (UnsupportedEncodingException e) {
            throw new XHttpClient.DataConversionException((Throwable) e);
        }
    }

    public void processOutData(Object obj, OutputStream outputStream) {
        bufferString(obj.toString());
        this.mByteArrayProcessor.processOutData(this.mBufferedStringByte, outputStream);
    }
}
