package b.b.c.c.b;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import b.b.c.c.a.b;

public abstract class d extends Fragment {
    private static final int TITLE_FILED = -1;
    public static final String TITLE_FILED_NAME = "TITLE_FILED";
    protected Activity mActivity;
    protected Context mAppContext;
    private MessageQueue mMsgQueue = Looper.myQueue();
    private Handler mUIHandler = new Handler();
    protected View mView;

    /* access modifiers changed from: protected */
    public void applyTitle() {
        Activity activity;
        int onSetTitle = onSetTitle();
        if (onSetTitle != -1 && (activity = this.mActivity) != null && activity.getActionBar() != null) {
            Activity activity2 = this.mActivity;
            activity2.setTitle(activity2.getString(onSetTitle));
            this.mActivity.getActionBar().setTitle(onSetTitle);
        }
    }

    /* access modifiers changed from: protected */
    public View findViewById(int i) {
        return this.mView.findViewById(i);
    }

    /* access modifiers changed from: protected */
    public void finish() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r0.getActionBar();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.CharSequence getTitle() {
        /*
            r1 = this;
            android.app.Activity r0 = r1.mActivity
            if (r0 == 0) goto L_0x000f
            android.app.ActionBar r0 = r0.getActionBar()
            if (r0 == 0) goto L_0x000f
            java.lang.CharSequence r0 = r0.getTitle()
            return r0
        L_0x000f:
            java.lang.String r0 = ""
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.c.b.d.getTitle():java.lang.CharSequence");
    }

    /* access modifiers changed from: protected */
    public abstract void initView();

    /* access modifiers changed from: protected */
    public boolean isAttatched() {
        return getActivity() != null;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        applyTitle();
        onCustomizeActionBar(getActivity().getActionBar());
        initView();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAppContext = activity.getApplicationContext();
        this.mActivity = activity;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mView = layoutInflater.inflate(onCreateViewLayout(), viewGroup, false);
        onCreateView2(layoutInflater, viewGroup, bundle);
        return this.mView;
    }

    /* access modifiers changed from: protected */
    public void onCreateView2(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
    }

    /* access modifiers changed from: protected */
    public abstract int onCreateViewLayout();

    /* access modifiers changed from: protected */
    public abstract int onCustomizeActionBar(ActionBar actionBar);

    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    public boolean onOptionsItemSelectedByActivity(MenuItem menuItem) {
        return false;
    }

    public boolean onPrepareOptionsMenuByActivity(Menu menu) {
        return false;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return -1;
    }

    /* access modifiers changed from: protected */
    public void postOnIdleUiThread(MessageQueue.IdleHandler idleHandler) {
        if (isAttatched()) {
            this.mMsgQueue.addIdleHandler(idleHandler);
        }
    }

    /* access modifiers changed from: protected */
    public void postOnUiDelayed(b bVar, long j) {
        if (isAttatched()) {
            this.mUIHandler.postDelayed(bVar, j);
        }
    }

    /* access modifiers changed from: protected */
    public void postOnUiThread(b bVar) {
        if (isAttatched()) {
            this.mUIHandler.post(bVar);
        }
    }

    /* access modifiers changed from: protected */
    public void setTitle(String str) {
        Activity activity = this.mActivity;
        if (activity != null) {
            activity.setTitle(str);
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(str);
            }
        }
    }
}
