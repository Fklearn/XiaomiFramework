package miui.cloud.net;

import android.text.TextUtils;
import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

public class XUrlencodedProcessor implements XHttpClient.ISendDataProcessor, XHttpClient.IReceiveDataProcessor {
    public static final XAutoAdaptProcessor.DataProcessorCreator CREATOR = new XAutoAdaptProcessor.DataProcessorCreator() {
        public XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2) {
            if (str.equals(XUrlencodedProcessor.MIME_TYPE)) {
                return new XUrlencodedProcessor(str2);
            }
            return null;
        }

        public XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str) {
            if (obj instanceof Map) {
                return new XUrlencodedProcessor(str);
            }
            return null;
        }
    };
    /* access modifiers changed from: private */
    public static String MIME_TYPE = "application/x-www-form-urlencoded";
    private Map<String, String> mBufferedData;
    private String mBufferedDataString;
    private String mEncode;
    private XPlainTextProcessor mPlainTextProcessor;

    public XUrlencodedProcessor(String str) {
        this.mPlainTextProcessor = new XPlainTextProcessor(str);
        this.mEncode = str;
    }

    private void bufferData(Map<String, String> map) {
        if (this.mBufferedData != map) {
            this.mBufferedData = map;
            this.mBufferedDataString = encode(map);
        }
    }

    private Map<String, String> decode(String str) {
        HashMap hashMap = new HashMap();
        String[] split = TextUtils.split(str, "&");
        int i = 0;
        while (i < split.length) {
            String[] split2 = TextUtils.split(split[i], "=");
            if (split2.length >= 2) {
                try {
                    hashMap.put(URLDecoder.decode(split2[0], this.mEncode), URLDecoder.decode(split2[1], this.mEncode));
                } catch (UnsupportedEncodingException unused) {
                    Log.e("XUrlencodedProcessor", "Encode not supported: " + this.mEncode);
                }
                i++;
            } else {
                throw new XHttpClient.DataConversionException("Bad input data: " + str + ", wrong format near: " + split[i]);
            }
        }
        return hashMap;
    }

    private String encode(Map<String, String> map) {
        ArrayList arrayList = new ArrayList();
        for (String next : map.keySet()) {
            try {
                String str = map.get(next);
                if (str == null) {
                    str = "null";
                }
                arrayList.add(URLEncoder.encode(next, this.mEncode) + "=" + URLEncoder.encode(str, this.mEncode));
            } catch (UnsupportedEncodingException e) {
                throw new XHttpClient.DataConversionException((Throwable) e);
            }
        }
        return TextUtils.join("&", arrayList.toArray(new String[0]));
    }

    public String getOutDataContentType(Object obj) {
        return MIME_TYPE;
    }

    public int getOutDataLength(Object obj) {
        bufferData((Map) obj);
        return this.mPlainTextProcessor.getOutDataLength(this.mBufferedDataString);
    }

    public Object processInData(Map<String, List<String>> map, InputStream inputStream) {
        return decode((String) this.mPlainTextProcessor.processInData(map, inputStream));
    }

    public void processOutData(Object obj, OutputStream outputStream) {
        bufferData((Map) obj);
        this.mPlainTextProcessor.processOutData(this.mBufferedDataString, outputStream);
    }
}
