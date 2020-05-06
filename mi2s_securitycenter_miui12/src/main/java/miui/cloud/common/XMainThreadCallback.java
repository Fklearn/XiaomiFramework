package miui.cloud.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.reflect.Method;

public class XMainThreadCallback<T> extends XDirectCallback<T> {
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            CallRequest callRequest = (CallRequest) message.obj;
            XMainThreadCallback.this.handleCallbackInMainThread(callRequest.method, callRequest.args);
        }
    };

    private static class CallRequest {
        public Object[] args;
        public Method method;

        public CallRequest(Method method2, Object[] objArr) {
            this.method = method2;
            this.args = objArr;
        }
    }

    public XMainThreadCallback(Class<T> cls, T t) {
        super(cls, t);
    }

    /* access modifiers changed from: protected */
    public Object handleCallback(Method method, Object[] objArr) {
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.obj = new CallRequest(method, objArr);
        obtainMessage.sendToTarget();
        return null;
    }

    /* access modifiers changed from: protected */
    public void handleCallbackInMainThread(Method method, Object[] objArr) {
        try {
            super.handleCallback(method, objArr);
        } catch (Throwable unused) {
            XLogger.loge("Exception in callback, but unable to propagate to the original thread. ");
        }
    }
}
