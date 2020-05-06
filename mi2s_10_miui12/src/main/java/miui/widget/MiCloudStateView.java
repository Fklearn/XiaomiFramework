package miui.widget;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import java.lang.ref.WeakReference;
import miui.accounts.ExtraAccountManager;
import miui.animation.Folme;
import miui.animation.ITouchStyle;
import miui.animation.base.AnimConfig;

public class MiCloudStateView extends LinearLayout {
    private static final int SYNC_OBSERVER_MASK = 13;
    private static final int SYNC_OBSERVER_TYPE_STATUS = 8;
    private Drawable mArrowRight;
    private int mCloudCountNormalTextAppearance;
    private int mCloudStatusDisabledTextAppearance;
    private int mCloudStatusHighlightTextAppearance;
    private int mCloudStatusNormalTextAppearance;
    private Context mContext;
    /* access modifiers changed from: private */
    public UpdateStateTask mCurrentUpdateTask;
    private FrameLayout mCustomView;
    private String mDisabledStatusText;
    private Handler mHandler;
    private int mLastVisible;
    private ILayoutUpdateListener mLayoutUpdateListener;
    private TextView mMiCloudCountText;
    private TextView mMiCloudStatusText;
    /* access modifiers changed from: private */
    public boolean mPendingUpdate;
    private Object mSyncChangeHandle;
    /* access modifiers changed from: private */
    public ISyncInfoProvider mSyncInfoProvider;
    /* access modifiers changed from: private */
    public boolean mSyncing;

    public interface ILayoutUpdateListener {
        void onLayoutUpdate(boolean z, boolean z2, int[] iArr);
    }

    public interface ISyncInfoProvider {
        String getAuthority();

        int[] getUnsyncedCount(Context context);

        String getUnsyncedCountText(Context context, int[] iArr);
    }

    public MiCloudStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiCloudStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSyncing = false;
        initialize(context, attrs, defStyle);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MiCloudStateView, R.attr.cloudStateViewStyle, miui.system.R.style.Widget_MiCloudStateView_Light);
        this.mCloudCountNormalTextAppearance = a.getResourceId(R.styleable.MiCloudStateView_cloudCountNormalTextAppearance, 0);
        this.mCloudStatusNormalTextAppearance = a.getResourceId(R.styleable.MiCloudStateView_cloudStatusNormalTextAppearance, 0);
        this.mCloudStatusHighlightTextAppearance = a.getResourceId(R.styleable.MiCloudStateView_cloudStatusHighlightTextAppearance, 0);
        this.mCloudStatusDisabledTextAppearance = a.getResourceId(R.styleable.MiCloudStateView_cloudStatusDisabledTextAppearance, 0);
        Drawable bg = a.getDrawable(R.styleable.MiCloudStateView_cloudStatusBackground);
        this.mArrowRight = a.getDrawable(R.styleable.MiCloudStateView_cloudArrowRight);
        a.recycle();
        this.mDisabledStatusText = getResources().getString(R.string.cloud_state_disabled);
        this.mContext = context;
        this.mHandler = new Handler();
        setBackground(bg);
        Folme.useAt(new View[]{this}).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(this, new AnimConfig[0]);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mMiCloudCountText = (TextView) findViewById(R.id.cloud_count);
        this.mMiCloudStatusText = (TextView) findViewById(R.id.cloud_status);
        this.mCustomView = (FrameLayout) findViewById(R.id.custom_view);
        Context context = getContext();
        this.mMiCloudCountText.setTextAppearance(context, this.mCloudCountNormalTextAppearance);
        this.mMiCloudStatusText.setTextAppearance(context, this.mCloudStatusNormalTextAppearance);
    }

    public void setCustomView(View customView) {
        if (customView != null) {
            this.mCustomView.setVisibility(0);
            this.mCustomView.removeAllViews();
            this.mCustomView.addView(customView);
            this.mLastVisible = this.mMiCloudStatusText.getVisibility();
            this.mMiCloudStatusText.setVisibility(8);
            return;
        }
        this.mCustomView.setVisibility(8);
        this.mCustomView.removeAllViews();
        this.mMiCloudStatusText.setVisibility(this.mLastVisible);
    }

    private boolean hasCustomView() {
        return this.mCustomView.getVisibility() == 0;
    }

    public void setTotalCountText(String countText) {
        this.mMiCloudCountText.setText(countText);
    }

    public void setDisabledStatusText(String disabledStatusText) {
        if (!TextUtils.isEmpty(disabledStatusText)) {
            this.mDisabledStatusText = disabledStatusText;
        }
    }

    public void setSyncInfoProvider(ISyncInfoProvider provider) {
        this.mSyncInfoProvider = provider;
    }

    public void setLayoutUpdateListener(ILayoutUpdateListener listener) {
        this.mLayoutUpdateListener = listener;
    }

    private static class SyncObserver implements SyncStatusObserver {
        WeakReference<MiCloudStateView> mView;

        SyncObserver(MiCloudStateView view) {
            this.mView = new WeakReference<>(view);
        }

        public void onStatusChanged(int which) {
            MiCloudStateView view = (MiCloudStateView) this.mView.get();
            if (view != null) {
                view.updateState(true);
            }
        }
    }

    public void registerObserver() {
        if (this.mSyncChangeHandle == null) {
            this.mSyncChangeHandle = ContentResolver.addStatusChangeListener(13, new SyncObserver(this));
        }
    }

    public void unregisterObserver() {
        Object obj = this.mSyncChangeHandle;
        if (obj != null) {
            ContentResolver.removeStatusChangeListener(obj);
            this.mSyncChangeHandle = null;
        }
    }

    public void updateState() {
        updateState(false);
    }

    public void updateState(final boolean force) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    MiCloudStateView.this.updateState(force);
                }
            });
        } else if (!isAttachedToWindow()) {
            if (force) {
                this.mPendingUpdate = true;
            }
        } else if (!force && this.mSyncing) {
        } else {
            if (this.mSyncInfoProvider == null) {
                throw new IllegalStateException("mSyncInfoProvider can't be null");
            } else if (this.mCurrentUpdateTask == null) {
                this.mCurrentUpdateTask = new UpdateStateTask();
                this.mCurrentUpdateTask.execute(new Void[0]);
            } else {
                this.mPendingUpdate = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mPendingUpdate) {
            this.mPendingUpdate = false;
            updateState(true);
        }
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean enabled, boolean syncing, int[] unsyncedCounts) {
        int totalUnsyncedCount = getTotalCount(unsyncedCounts);
        if (!enabled) {
            this.mMiCloudStatusText.setVisibility(8);
            this.mMiCloudCountText.setText(this.mDisabledStatusText);
            this.mMiCloudCountText.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.cloud_btn_padding));
            this.mMiCloudCountText.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, this.mArrowRight, (Drawable) null);
        } else if (!syncing) {
            this.mMiCloudCountText.setCompoundDrawablePadding(0);
            this.mMiCloudCountText.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            if (!hasCustomView()) {
                this.mMiCloudStatusText.setVisibility(0);
                if (totalUnsyncedCount > 0) {
                    this.mMiCloudStatusText.setText(this.mSyncInfoProvider.getUnsyncedCountText(this.mContext, unsyncedCounts));
                } else {
                    this.mMiCloudStatusText.setText(R.string.cloud_state_finished);
                }
            }
        } else {
            this.mMiCloudCountText.setCompoundDrawablePadding(0);
            this.mMiCloudCountText.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            if (!hasCustomView()) {
                this.mMiCloudStatusText.setVisibility(0);
                this.mMiCloudStatusText.setText(R.string.cloud_state_syncing);
            }
        }
        Context context = getContext();
        if (syncing || totalUnsyncedCount <= 0) {
            this.mMiCloudStatusText.setTextAppearance(context, this.mCloudStatusNormalTextAppearance);
        } else {
            this.mMiCloudStatusText.setTextAppearance(context, this.mCloudStatusHighlightTextAppearance);
        }
        ILayoutUpdateListener iLayoutUpdateListener = this.mLayoutUpdateListener;
        if (iLayoutUpdateListener != null) {
            iLayoutUpdateListener.onLayoutUpdate(enabled, syncing, unsyncedCounts);
        }
        requestLayout();
    }

    private int getTotalCount(int[] counts) {
        int totalCount = 0;
        if (counts != null && counts.length > 0) {
            for (int cnt : counts) {
                totalCount += cnt;
            }
        }
        return totalCount;
    }

    private class UpdateStateTask extends AsyncTask<Void, Void, Void> {
        boolean enabled;
        boolean syncing;
        int[] unsyncedCounts;

        private UpdateStateTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voids) {
            Account account = ExtraAccountManager.getXiaomiAccount(MiCloudStateView.this.getContext());
            if (account == null) {
                this.enabled = false;
                this.syncing = false;
            } else {
                String authority = MiCloudStateView.this.mSyncInfoProvider.getAuthority();
                if (TextUtils.isEmpty(authority)) {
                    this.enabled = false;
                    this.syncing = false;
                } else {
                    this.enabled = ContentResolver.getSyncAutomatically(account, authority);
                    this.syncing = ContentResolver.isSyncActive(account, authority);
                }
            }
            boolean unused = MiCloudStateView.this.mSyncing = this.syncing;
            if (!this.enabled || this.syncing) {
                return null;
            }
            this.unsyncedCounts = MiCloudStateView.this.mSyncInfoProvider.getUnsyncedCount(MiCloudStateView.this.getContext());
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            UpdateStateTask unused = MiCloudStateView.this.mCurrentUpdateTask = null;
            if (MiCloudStateView.this.isAttachedToWindow()) {
                MiCloudStateView.this.updateLayout(this.enabled, this.syncing, this.unsyncedCounts);
                if (MiCloudStateView.this.mPendingUpdate) {
                    boolean unused2 = MiCloudStateView.this.mPendingUpdate = false;
                    MiCloudStateView.this.updateState(true);
                }
            }
        }
    }
}
