package miui.cloud.net;

import java.io.OutputStream;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

public class XSendDataAutoAdaptProcessor extends XAutoAdaptProcessor implements XHttpClient.ISendDataProcessor {
    private Object mBufferedData = null;
    private XHttpClient.ISendDataProcessor mBufferedProcessor = null;
    private String mEncode = null;

    public XSendDataAutoAdaptProcessor(String str) {
        this.mEncode = str;
    }

    private void bufferData(Object obj) {
        if (this.mBufferedData != obj) {
            this.mBufferedData = obj;
            for (XAutoAdaptProcessor.DataProcessorCreator instanceIfAbleToProcessOutData : XAutoAdaptProcessor.AVALIABLE_PROCESSOR_CREATORS) {
                XHttpClient.ISendDataProcessor instanceIfAbleToProcessOutData2 = instanceIfAbleToProcessOutData.getInstanceIfAbleToProcessOutData(obj, this.mEncode);
                if (instanceIfAbleToProcessOutData2 != null) {
                    this.mBufferedProcessor = instanceIfAbleToProcessOutData2;
                    return;
                }
            }
            this.mBufferedProcessor = new XPlainTextProcessor(this.mEncode);
        }
    }

    public String getOutDataContentType(Object obj) {
        bufferData(obj);
        return this.mBufferedProcessor.getOutDataContentType(this.mBufferedData);
    }

    public int getOutDataLength(Object obj) {
        bufferData(obj);
        return this.mBufferedProcessor.getOutDataLength(this.mBufferedData);
    }

    public void processOutData(Object obj, OutputStream outputStream) {
        bufferData(obj);
        this.mBufferedProcessor.processOutData(this.mBufferedData, outputStream);
    }
}
