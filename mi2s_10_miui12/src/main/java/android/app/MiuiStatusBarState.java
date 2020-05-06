package android.app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import com.miui.system.internal.R;
import java.lang.reflect.Method;

public class MiuiStatusBarState {
    public static final String ACTION_CLEAR_STATUS_BAR_STATE = "action_clear_status_bar_state";
    public static final String ACTION_SET_STATUS_BAR_STATE = "action_set_status_bar_state";
    public static final String KEY_STATUS_BAR_MINI_STATE = "key_status_bar_mini_state";
    public static final String KEY_STATUS_BAR_PACKAGE_NAME = "key_status_bar_package_name";
    public static final String KEY_STATUS_BAR_PRIORITY = "key_status_bar_priority";
    public static final String KEY_STATUS_BAR_STANDARD_STATE = "key_status_bar_standard_state";
    public static final String KEY_STATUS_BAR_TAG = "key_status_bar_tag";
    public static final int PRIORITY_DEFAULT = 1;
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MAX = 3;
    private static final String SET_BACKGROUND_COLOR = "setBackgroundColor";
    private static final String SET_GRAVITY = "setGravity";
    private static final String SET_TEXT_COLOR = "setTextColor";
    private static final String TAG = "MiuiStatusBarState";
    /* access modifiers changed from: private */
    public static final Method sSetDrawableTint;
    private Bundle mBundle;
    private RemoteViews mMiniStateViews;
    private int mPriority;
    private RemoteViews mStandardStateViews;
    private String mTag;

    static {
        Method method = null;
        try {
            method = RemoteViews.class.getMethod("setDrawableTint", new Class[]{Integer.TYPE, Boolean.TYPE, Integer.TYPE, PorterDuff.Mode.class});
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        sSetDrawableTint = method;
    }

    public MiuiStatusBarState(String tag, RemoteViews standardStateViews, RemoteViews miniStateViews, int priority) {
        this.mTag = tag;
        if (priority > 3 || priority < 0) {
            this.mPriority = 1;
        } else {
            this.mPriority = priority;
        }
        this.mMiniStateViews = miniStateViews;
        this.mStandardStateViews = standardStateViews;
    }

    public MiuiStatusBarState(String tag, RemoteViews standardStateViews, RemoteViews miniStateViews, int priority, Bundle bundle) {
        this.mTag = tag;
        if (priority > 3 || priority < 0) {
            this.mPriority = 1;
        } else {
            this.mPriority = priority;
        }
        this.mBundle = bundle;
        this.mMiniStateViews = miniStateViews;
        this.mStandardStateViews = standardStateViews;
    }

    public String getTag(Context context) {
        return context.getPackageName() + "." + this.mTag;
    }

    /* access modifiers changed from: package-private */
    public Bundle toBundle(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_STATUS_BAR_TAG, this.mTag);
        bundle.putInt(KEY_STATUS_BAR_PRIORITY, this.mPriority);
        bundle.putString(KEY_STATUS_BAR_PACKAGE_NAME, context.getPackageName());
        bundle.putParcelable(KEY_STATUS_BAR_MINI_STATE, this.mMiniStateViews);
        bundle.putParcelable(KEY_STATUS_BAR_STANDARD_STATE, this.mStandardStateViews);
        bundle.putBundle("extra", this.mBundle);
        return bundle;
    }

    public static class StandardViewBuilder {
        private RemoteViews mViews;

        public StandardViewBuilder(Context context) {
            this.mViews = new RemoteViews(context.getPackageName(), R.layout.miui_status_bar_standard_state_layout);
        }

        public StandardViewBuilder setPendingIntent(PendingIntent pendingIntent) {
            this.mViews.setOnClickPendingIntent(R.id.app_info, pendingIntent);
            return this;
        }

        public StandardViewBuilder setAppIcon(int appIcon) {
            this.mViews.setImageViewResource(16908294, appIcon);
            this.mViews.setViewVisibility(16908294, 0);
            return this;
        }

        public StandardViewBuilder setTitle(String title) {
            this.mViews.setTextViewText(16908310, title);
            this.mViews.setViewVisibility(16908310, 0);
            return this;
        }

        public StandardViewBuilder setShowStatusInfo(boolean show) {
            int i = 8;
            int i2 = 0;
            this.mViews.setViewVisibility(16908292, show ? 0 : 8);
            RemoteViews remoteViews = this.mViews;
            if (show) {
                i = 0;
            }
            remoteViews.setViewVisibility(16908290, i);
            RemoteViews remoteViews2 = this.mViews;
            int i3 = R.id.app_info;
            if (!show) {
                i2 = 17;
            }
            remoteViews2.setInt(i3, MiuiStatusBarState.SET_GRAVITY, i2);
            return this;
        }

        public StandardViewBuilder setStatusIcon1(int statusIcon1) {
            this.mViews.setImageViewResource(16908295, statusIcon1);
            this.mViews.setViewVisibility(16908295, 0);
            return this;
        }

        public StandardViewBuilder setStatusIcon2(int statusIcon2) {
            this.mViews.setImageViewResource(16908296, statusIcon2);
            this.mViews.setViewVisibility(16908296, 0);
            return this;
        }

        public StandardViewBuilder setStatusDescripiton(String statusDescripiton) {
            this.mViews.setTextViewText(16908308, statusDescripiton);
            this.mViews.setViewVisibility(16908309, 8);
            this.mViews.setViewVisibility(16908308, 0);
            return this;
        }

        public StandardViewBuilder setStatusChronometer(long base, String format, boolean started) {
            this.mViews.setChronometer(R.id.chronometer, base, format, started);
            this.mViews.setViewVisibility(16908308, 8);
            this.mViews.setViewVisibility(R.id.chronometer, 0);
            return this;
        }

        public StandardViewBuilder setBackgroundColor(int bgColor) {
            this.mViews.setInt(R.id.app_info, MiuiStatusBarState.SET_BACKGROUND_COLOR, bgColor);
            return this;
        }

        public StandardViewBuilder setTextColor(int textColor) {
            this.mViews.setInt(R.id.title, MiuiStatusBarState.SET_TEXT_COLOR, textColor);
            this.mViews.setInt(16908308, MiuiStatusBarState.SET_TEXT_COLOR, textColor);
            this.mViews.setInt(R.id.chronometer, MiuiStatusBarState.SET_TEXT_COLOR, textColor);
            return this;
        }

        public RemoteViews build() {
            return this.mViews;
        }
    }

    public static class MiniStateViewBuilder {
        private boolean mChronometerShow = false;
        private boolean mIconShow = false;
        private boolean mTitleShow = false;
        private RemoteViews mViews;

        public MiniStateViewBuilder(Context context) {
            this.mViews = new RemoteViews(context.getPackageName(), R.layout.miui_status_bar_mini_state_layout);
        }

        public MiniStateViewBuilder setPendingIntent(PendingIntent pendingIntent) {
            this.mViews.setOnClickPendingIntent(R.id.app_info, pendingIntent);
            return this;
        }

        public MiniStateViewBuilder setAppIcon(int appIcon) {
            this.mViews.setImageViewResource(16908294, appIcon);
            this.mViews.setViewVisibility(16908294, 0);
            this.mIconShow = true;
            return this;
        }

        public MiniStateViewBuilder setTitle(String title) {
            this.mViews.setTextViewText(16908310, title);
            this.mViews.setViewVisibility(R.id.chronometer, 8);
            this.mViews.setViewVisibility(16908310, 0);
            this.mChronometerShow = false;
            this.mTitleShow = true;
            return this;
        }

        public MiniStateViewBuilder setChronometer(long base, String format, boolean started) {
            this.mViews.setChronometer(R.id.chronometer, base, format, started);
            this.mViews.setViewVisibility(16908310, 8);
            this.mViews.setViewVisibility(R.id.chronometer, 0);
            this.mTitleShow = false;
            this.mChronometerShow = true;
            return this;
        }

        public MiniStateViewBuilder setBackgroundColor(int bgColor) {
            try {
                MiuiStatusBarState.sSetDrawableTint.invoke(this.mViews, new Object[]{Integer.valueOf(R.id.app_info), true, Integer.valueOf(bgColor), PorterDuff.Mode.SRC_IN});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public MiniStateViewBuilder setTextColor(int textColor) {
            this.mViews.setInt(16908310, MiuiStatusBarState.SET_TEXT_COLOR, textColor);
            this.mViews.setInt(R.id.chronometer, MiuiStatusBarState.SET_TEXT_COLOR, textColor);
            return this;
        }

        public RemoteViews build() {
            if (!this.mIconShow || (!this.mChronometerShow && !this.mTitleShow)) {
                this.mViews.setViewVisibility(R.id.gap, 8);
            } else {
                this.mViews.setViewVisibility(R.id.gap, 0);
            }
            return this.mViews;
        }
    }
}
