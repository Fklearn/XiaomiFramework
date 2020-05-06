package b.b.c.c.a;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContextWrapper;

public abstract class b extends ContextWrapper implements Runnable {
    private Activity mActivity;
    private Fragment mFragment;

    public b(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    public b(Fragment fragment) {
        super(fragment.getActivity());
        this.mFragment = fragment;
    }

    public void run() {
        Fragment fragment = this.mFragment;
        if (fragment != null) {
            if (fragment.getActivity() == null) {
                return;
            }
        } else if (this.mActivity == null) {
            return;
        }
        runOnUiThread();
    }

    /* access modifiers changed from: protected */
    public abstract void runOnUiThread();
}
