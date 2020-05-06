package miui.cloud.net;

import miui.cloud.net.XHttpClient;

public abstract class XAutoAdaptProcessor {
    protected static final DataProcessorCreator[] AVALIABLE_PROCESSOR_CREATORS = {XByteArrayProcessor.CREATOR, XPlainTextProcessor.CREATOR, XUrlencodedProcessor.CREATOR, XJSONProcessor.CREATOR};

    interface DataProcessorCreator {
        XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2);

        XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str);
    }
}
