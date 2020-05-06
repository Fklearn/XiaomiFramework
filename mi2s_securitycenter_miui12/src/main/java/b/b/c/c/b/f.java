package b.b.c.c.b;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.View;
import android.widget.ListView;
import b.b.c.c.a.b;
import miui.R;
import miui.util.AttributeResolver;
import miuix.preference.s;

public abstract class f extends s {
    protected Activity mActivity;
    protected Context mAppContext;
    private MessageQueue mMsgQueue = Looper.myQueue();
    protected Handler mUIHandler = new Handler();

    /* access modifiers changed from: protected */
    public void finish() {
        getActivity().finish();
    }

    /* access modifiers changed from: protected */
    public CharSequence getTitle() {
        Activity activity = this.mActivity;
        return activity != null ? activity.getActionBar().getTitle() : "";
    }

    /* access modifiers changed from: protected */
    public abstract int getXmlPreference();

    /* access modifiers changed from: protected */
    public abstract void initPreferenceView();

    /* access modifiers changed from: protected */
    public boolean isAttatched() {
        return getActivity() != null;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayOptions(16, 16);
        onCustomizeActionBar(actionBar);
        initPreferenceView();
        int onSetTitle = onSetTitle();
        if (onSetTitle != -1) {
            Activity activity = this.mActivity;
            activity.setTitle(activity.getString(onSetTitle));
            actionBar.setTitle(onSetTitle);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAppContext = activity.getApplicationContext();
        this.mActivity = activity;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(getXmlPreference());
    }

    public void onCreatePreferences(Bundle bundle, String str) {
    }

    /* access modifiers changed from: protected */
    public abstract int onCustomizeActionBar(ActionBar actionBar);

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return -1;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ListView listView = (ListView) view.findViewById(16908298);
        if (listView != null) {
            listView.setClipToPadding(false);
            listView.setPadding(0, 0, 0, (int) AttributeResolver.resolveDimension(view.getContext(), R.attr.preferenceScreenPaddingBottom));
        }
    }

    /* access modifiers changed from: protected */
    public void postOnUiThread(b bVar) {
        if (isAttatched()) {
            this.mUIHandler.post(bVar);
        }
    }

    /* access modifiers changed from: protected */
    public void setTitle(int i) {
        Activity activity = this.mActivity;
        if (activity != null) {
            activity.setTitle(i);
            this.mActivity.getActionBar().setTitle(i);
        }
    }

    /* access modifiers changed from: protected */
    public void setTitle(String str) {
        Activity activity = this.mActivity;
        if (activity != null) {
            activity.setTitle(str);
            this.mActivity.getActionBar().setTitle(str);
        }
    }
}
