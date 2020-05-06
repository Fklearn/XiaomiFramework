package b.b.c.c.b;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.MenuItem;
import b.b.c.c.a.b;
import miui.app.ActionBar;
import miui.app.Activity;

public abstract class a extends Activity {
    protected android.app.Activity mActivity;
    protected Context mAppContext;
    private MessageQueue mMsgQueue = Looper.myQueue();
    private Handler mUIHandler = new Handler();

    private void customizeActionBar() {
        onCustomizeActionBar(getActionBar());
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [miui.app.Activity, android.app.Activity, b.b.c.c.b.a] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        a.super.onCreate(bundle);
        this.mActivity = this;
        this.mAppContext = getApplicationContext();
        int onCreateContentView = onCreateContentView();
        if (onCreateContentView > 0) {
            setContentView(onCreateContentView);
        }
        customizeActionBar();
    }

    /* access modifiers changed from: protected */
    public abstract int onCreateContentView();

    /* access modifiers changed from: protected */
    public abstract void onCustomizeActionBar(ActionBar actionBar);

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return a.super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    public void postOnIdleUiThread(MessageQueue.IdleHandler idleHandler) {
        this.mMsgQueue.addIdleHandler(idleHandler);
    }

    /* access modifiers changed from: protected */
    public void postOnUiDelayed(Runnable runnable, long j) {
        this.mUIHandler.postDelayed(runnable, j);
    }

    /* access modifiers changed from: protected */
    public void postOnUiThread(b bVar) {
        this.mUIHandler.post(bVar);
    }
}
