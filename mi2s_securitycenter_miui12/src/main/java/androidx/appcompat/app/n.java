package androidx.appcompat.app;

import android.content.res.Resources;
import java.lang.Thread;

class n implements Thread.UncaughtExceptionHandler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Thread.UncaughtExceptionHandler f317a;

    n(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.f317a = uncaughtExceptionHandler;
    }

    private boolean a(Throwable th) {
        String message;
        if (!(th instanceof Resources.NotFoundException) || (message = th.getMessage()) == null) {
            return false;
        }
        return message.contains("drawable") || message.contains("Drawable");
    }

    public void uncaughtException(Thread thread, Throwable th) {
        if (a(th)) {
            Resources.NotFoundException notFoundException = new Resources.NotFoundException(th.getMessage() + ". If the resource you are trying to use is a vector resource, you may be referencing it in an unsupported way. See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info.");
            notFoundException.initCause(th.getCause());
            notFoundException.setStackTrace(th.getStackTrace());
            this.f317a.uncaughtException(thread, notFoundException);
            return;
        }
        this.f317a.uncaughtException(thread, th);
    }
}
