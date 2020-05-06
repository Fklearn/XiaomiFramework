package b.b.c.c.b;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import miui.app.Activity;

public abstract class e extends Activity {
    private MessageQueue mMsgQueue = Looper.myQueue();
    private Handler mUIHandler = new Handler();

    /* access modifiers changed from: protected */
    public d getCurrentBaseFragment() {
        Fragment findFragmentById = getFragmentManager().findFragmentById(16908290);
        if (findFragmentById == null || !(findFragmentById instanceof d)) {
            return null;
        }
        return (d) findFragmentById;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        e.super.onCreate(bundle);
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, onCreateFragment()).commit();
        }
    }

    public abstract Fragment onCreateFragment();

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        d currentBaseFragment = getCurrentBaseFragment();
        if (!(currentBaseFragment != null ? currentBaseFragment.onKeyDown(i, keyEvent) : false)) {
            return e.super.onKeyDown(i, keyEvent);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        d currentBaseFragment = getCurrentBaseFragment();
        if (!(currentBaseFragment != null ? currentBaseFragment.onOptionsItemSelectedByActivity(menuItem) : false)) {
            return e.super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        d currentBaseFragment = getCurrentBaseFragment();
        if (currentBaseFragment != null ? currentBaseFragment.onPrepareOptionsMenuByActivity(menu) : false) {
            return e.super.onPrepareOptionsMenu(menu);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void postOnIdleUiThread(MessageQueue.IdleHandler idleHandler) {
        this.mMsgQueue.addIdleHandler(idleHandler);
    }
}
