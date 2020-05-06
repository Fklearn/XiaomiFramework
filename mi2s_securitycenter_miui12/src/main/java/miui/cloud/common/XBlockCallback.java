package miui.cloud.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class XBlockCallback<T> extends XCallback<T> {
    private CallbackRequest mCallbackRequest;
    private ReentrantLock mCallbackRequestLock = new ReentrantLock();
    private Condition mCallbackRequestReady = this.mCallbackRequestLock.newCondition();
    private Condition mCallbackRequestWait = this.mCallbackRequestLock.newCondition();

    private static class CallbackRequest {
        public Object[] args;
        public Method method;

        public CallbackRequest(Method method2, Object[] objArr) {
            this.method = method2;
            this.args = objArr;
        }
    }

    public XBlockCallback(Class<T> cls) {
        super(cls);
    }

    /* access modifiers changed from: protected */
    public Object handleCallback(Method method, Object[] objArr) {
        try {
            this.mCallbackRequestLock.lock();
            while (this.mCallbackRequest != null) {
                this.mCallbackRequestReady.signalAll();
                this.mCallbackRequestWait.await();
            }
            this.mCallbackRequest = new CallbackRequest(method, objArr);
            this.mCallbackRequestReady.signalAll();
        } catch (InterruptedException unused) {
            XLogger.loge("Interrupted while waiting for callback handlers. ");
        } catch (Throwable th) {
            this.mCallbackRequestLock.unlock();
            throw th;
        }
        this.mCallbackRequestLock.unlock();
        return null;
    }

    public void waitForCallBack(T t) {
        Object[] objArr;
        try {
            this.mCallbackRequestLock.lock();
            while (this.mCallbackRequest == null) {
                this.mCallbackRequestWait.signalAll();
                this.mCallbackRequestReady.await();
            }
            this.mCallbackRequest.method.invoke(t, this.mCallbackRequest.args);
        } catch (IllegalAccessException unused) {
            objArr = new Object[]{"Bad callback. "};
        } catch (IllegalArgumentException unused2) {
            objArr = new Object[]{"Bad callback. "};
        } catch (InvocationTargetException e) {
            XLogger.loge("Exception in callback, but unable to propagate to the original thread. ", e);
        } catch (Throwable th) {
            this.mCallbackRequestLock.unlock();
            throw th;
        }
        this.mCallbackRequest = null;
        this.mCallbackRequestWait.signalAll();
        this.mCallbackRequestLock.unlock();
        XLogger.loge(objArr);
        this.mCallbackRequest = null;
        this.mCallbackRequestWait.signalAll();
        this.mCallbackRequestLock.unlock();
    }
}
