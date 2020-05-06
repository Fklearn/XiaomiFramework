package b.b.c.i;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class a<D> extends AsyncTaskLoader<D> {

    /* renamed from: a  reason: collision with root package name */
    private D f1740a;

    public a(Context context) {
        super(context);
    }

    public void deliverResult(D d2) {
        if (!isReset() && isStarted()) {
            super.deliverResult(d2);
        }
    }

    public D loadInBackground() {
        return null;
    }

    /* access modifiers changed from: protected */
    public D onLoadInBackground() {
        this.f1740a = super.onLoadInBackground();
        return this.f1740a;
    }

    /* access modifiers changed from: protected */
    public void onStartLoading() {
        D d2 = this.f1740a;
        if (d2 != null) {
            deliverResult(d2);
        }
        if (takeContentChanged() || this.f1740a == null) {
            forceLoad();
        }
    }

    /* access modifiers changed from: protected */
    public void onStopLoading() {
        cancelLoad();
    }
}
