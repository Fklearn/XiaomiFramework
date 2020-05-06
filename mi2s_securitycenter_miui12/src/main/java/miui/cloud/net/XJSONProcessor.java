package miui.cloud.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class XJSONProcessor implements XHttpClient.ISendDataProcessor, XHttpClient.IReceiveDataProcessor {
    public static final XAutoAdaptProcessor.DataProcessorCreator CREATOR = new XAutoAdaptProcessor.DataProcessorCreator() {
        public XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2) {
            if (str.equals(XJSONProcessor.MIME_TYPE)) {
                return new XJSONProcessor(str2);
            }
            return null;
        }

        public XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str) {
            if ((obj instanceof JSONObject) || (obj instanceof JSONArray)) {
                return new XJSONProcessor(str);
            }
            return null;
        }
    };
    /* access modifiers changed from: private */
    public static String MIME_TYPE = "application/json";
    private Object mBufferedData;
    private String mBufferedDataString;
    private XPlainTextProcessor mPlainTextProcessor;

    public XJSONProcessor(String str) {
        this.mPlainTextProcessor = new XPlainTextProcessor(str);
    }

    private void bufferData(Object obj) {
        if (this.mBufferedData != obj) {
            this.mBufferedData = obj;
            this.mBufferedDataString = encode(obj);
        }
    }

    private Object decode(String str) {
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            throw new XHttpClient.DataConversionException("Bad JSON: " + str, e);
        }
    }

    private String encode(Object obj) {
        return obj.toString();
    }

    public String getOutDataContentType(Object obj) {
        return MIME_TYPE;
    }

    public int getOutDataLength(Object obj) {
        bufferData(obj);
        return this.mPlainTextProcessor.getOutDataLength(this.mBufferedDataString);
    }

    public Object processInData(Map<String, List<String>> map, InputStream inputStream) {
        return decode((String) this.mPlainTextProcessor.processInData(map, inputStream));
    }

    public void processOutData(Object obj, OutputStream outputStream) {
        bufferData(obj);
        this.mPlainTextProcessor.processOutData(this.mBufferedDataString, outputStream);
    }
}
