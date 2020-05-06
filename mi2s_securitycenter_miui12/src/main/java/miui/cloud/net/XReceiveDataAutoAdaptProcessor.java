package miui.cloud.net;

import android.text.TextUtils;
import android.util.Pair;
import com.miui.maml.util.net.SimpleRequest;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

public class XReceiveDataAutoAdaptProcessor extends XAutoAdaptProcessor implements XHttpClient.IReceiveDataProcessor {
    private Pair<String, String> getContentTypeAndEncodeFromHeader(Map<String, List<String>> map) {
        String str;
        List list = map.get("Content-Type");
        String str2 = SimpleRequest.UTF8;
        if (list == null || list.isEmpty()) {
            str = "text/plain";
        } else {
            String[] split = TextUtils.split((String) list.get(0), ";");
            str = split[0].trim();
            if (split.length > 1) {
                String[] split2 = TextUtils.split(split[1], "=");
                if (split2.length > 1) {
                    str2 = split2[1].trim();
                }
            }
        }
        return new Pair<>(str, str2);
    }

    public Object processInData(Map<String, List<String>> map, InputStream inputStream) {
        Pair<String, String> contentTypeAndEncodeFromHeader = getContentTypeAndEncodeFromHeader(map);
        for (XAutoAdaptProcessor.DataProcessorCreator instanceIfAbleToProcessInData : XAutoAdaptProcessor.AVALIABLE_PROCESSOR_CREATORS) {
            XHttpClient.IReceiveDataProcessor instanceIfAbleToProcessInData2 = instanceIfAbleToProcessInData.getInstanceIfAbleToProcessInData((String) contentTypeAndEncodeFromHeader.first, (String) contentTypeAndEncodeFromHeader.second);
            if (instanceIfAbleToProcessInData2 != null) {
                return instanceIfAbleToProcessInData2.processInData(map, inputStream);
            }
        }
        return new XPlainTextProcessor((String) contentTypeAndEncodeFromHeader.second).processInData(map, inputStream);
    }
}
