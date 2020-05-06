package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.i;
import com.miui.maml.component.MamlView;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.TextPrepareUtil;
import com.miui.networkassistant.utils.TypefaceHelper;
import com.miui.permission.PermissionContract;
import com.miui.securitycenter.R;
import java.util.Locale;

public class MainTrafficUsedView extends RelativeLayout implements View.OnClickListener {
    public static final int STATUS_AD = 3;
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_NO_SET = 4;
    public static final int STATUS_OVERFLOW = 2;
    public static final int STATUS_WARNING = 1;
    private LinearLayout mAdSimView;
    private BackgroundView mBackgroundAnimView;
    private View mBillLayout;
    private TextView mBillRemainedTextView;
    private TextView mBillRemainedUnitTextView;
    /* access modifiers changed from: private */
    public LoadingButton mButtonAdjustUsage;
    private RelativeLayout mCardBackgroundView;
    private TextView mCardTitle;
    private ImageView mCardTitleImage;
    private Context mContext;
    private TextView mErrorTextView;
    private boolean mHasLeisure;
    private TextView mMainBillRemainTextView;
    private TextView mMainMonthPackageTextView;
    private TextView mMainTodayUsedTextView;
    private TextView mMonthPackageTextView;
    private TextView mMonthRemainedView;
    private TextView mMonthUsedUnit;
    private MamlView mNoSimIconView;
    private boolean mNoSimIconViewIsActive;
    private LinearLayout mNoSimView;
    private View mPackageUsedView;
    private TextView mPreAdjustTimeTextView;
    private View mPrimaryTextLayout;
    private TextView mPrimaryTextView;
    private View mSplitView;
    private TextView mTodayUsedTextUnit;
    private TextView mTodayUsedTextView;
    private TextView mUnitTextView;

    public MainTrafficUsedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainTrafficUsedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainTrafficUsedView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNoSimIconViewIsActive = false;
        View.inflate(context, R.layout.view_main_traffic_used, this);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.mNoSimView = (LinearLayout) findViewById(R.id.card_no_sim);
        this.mAdSimView = (LinearLayout) findViewById(R.id.card_ad_sim);
        this.mCardBackgroundView = (RelativeLayout) findViewById(R.id.na_main_background);
        this.mBackgroundAnimView = (BackgroundView) findViewById(R.id.main_bg_view);
        this.mMonthRemainedView = (TextView) findViewById(R.id.number);
        this.mMonthRemainedView.setTypeface(TypefaceHelper.getMiuiTypefaceForNA(this.mContext));
        this.mUnitTextView = (TextView) findViewById(R.id.unit);
        this.mPrimaryTextView = (TextView) findViewById(R.id.text_primary);
        this.mSplitView = findViewById(R.id.view_split);
        this.mPreAdjustTimeTextView = (TextView) findViewById(R.id.pre_adjust_time);
        this.mErrorTextView = (TextView) findViewById(R.id.text_error);
        this.mPackageUsedView = findViewById(R.id.view_package_used);
        this.mTodayUsedTextView = (TextView) findViewById(R.id.today_used);
        this.mTodayUsedTextUnit = (TextView) findViewById(R.id.today_used_unit);
        this.mMonthPackageTextView = (TextView) findViewById(R.id.month_package);
        this.mMonthUsedUnit = (TextView) findViewById(R.id.month_package_unit);
        this.mBillLayout = this.mPackageUsedView.findViewById(R.id.layout_bill_remained);
        this.mBillRemainedTextView = (TextView) findViewById(R.id.bill_remained);
        this.mBillRemainedUnitTextView = (TextView) findViewById(R.id.bill_remained_unit);
        this.mButtonAdjustUsage = (LoadingButton) findViewById(R.id.button_adjust_usage);
        this.mCardTitle = (TextView) findViewById(R.id.card_title);
        this.mCardTitleImage = (ImageView) findViewById(R.id.card_title_image);
        this.mPrimaryTextLayout = findViewById(R.id.primary_text);
        this.mMainTodayUsedTextView = (TextView) findViewById(R.id.main_today_used);
        this.mMainBillRemainTextView = (TextView) findViewById(R.id.main_bill_remained);
        this.mMainMonthPackageTextView = (TextView) findViewById(R.id.month_used_text);
    }

    /* access modifiers changed from: private */
    public void switchMamlView() {
        MamlView mamlView = this.mNoSimIconView;
        if (mamlView != null) {
            mamlView.onCommand(this.mNoSimIconViewIsActive ? "deactive" : PermissionContract.Active.TABLE_NAME);
            this.mNoSimIconViewIsActive = !this.mNoSimIconViewIsActive;
        }
    }

    private void updatePrimaryMessage(long j, long j2, float f, boolean z) {
        showPrimaryMessage(!z ? R.string.main_month_total_used : this.mHasLeisure ? ((float) j) < ((float) j2) * f ? R.string.main_primary_message_leisure_remain : j < j2 ? R.string.main_primary_message_leisure_alert : R.string.main_primary_message_leisure_overlimit : ((float) j) < ((float) j2) * f ? R.string.main_primary_message_traffic_remain : j < j2 ? R.string.main_primary_message_traffic_warning : R.string.main_primary_message_traffic_overlimit);
    }

    private void updateSplitViewVisible() {
        boolean z = true;
        int i = 0;
        boolean z2 = !TextUtils.isEmpty(this.mPrimaryTextView.getText().toString()) && this.mPrimaryTextView.getVisibility() == 0;
        if (this.mPrimaryTextView.getVisibility() != 0 && (this.mPreAdjustTimeTextView.getVisibility() != 0 || TextUtils.isEmpty(this.mPreAdjustTimeTextView.getText().toString()))) {
            z = false;
        }
        View view = this.mSplitView;
        if (!z2 || !z) {
            i = 8;
        }
        view.setVisibility(i);
    }

    public void onClick(View view) {
        switchMamlView();
    }

    public void onDestroy() {
        MamlView mamlView = this.mNoSimIconView;
        if (mamlView != null) {
            mamlView.onDestroy();
            this.mNoSimIconView = null;
        }
    }

    public void onPause() {
        MamlView mamlView = this.mNoSimIconView;
        if (mamlView != null) {
            mamlView.onPause();
        }
    }

    public void onResume() {
        MamlView mamlView = this.mNoSimIconView;
        if (mamlView != null) {
            mamlView.onResume();
        }
    }

    public void resetView() {
        this.mButtonAdjustUsage.setEnabled(true);
        this.mButtonAdjustUsage.setText(R.string.main_button_usage_adjust);
    }

    public void setBillLayoutClickListener(View.OnClickListener onClickListener) {
        this.mBillLayout.setOnClickListener(onClickListener);
    }

    public void setBillLayoutVisible(boolean z) {
        this.mBillLayout.setVisibility(z ? 0 : 8);
    }

    public void setBillRemainedTextView(long j) {
        if (j > Long.MIN_VALUE) {
            this.mBillRemainedTextView.setText((((double) j) / 100.0d) + "");
            this.mBillRemainedUnitTextView.setVisibility(0);
        }
    }

    public void setCardStyle(String str, int i, float f, int i2) {
        int i3 = 8;
        this.mAdSimView.setVisibility(8);
        int i4 = R.drawable.card_blue;
        int i5 = R.color.na_anim_null;
        if (i == 0) {
            i5 = R.color.na_anim_blue;
        } else if (i == 1) {
            i4 = R.drawable.card_warn;
            i5 = R.color.na_anim_warn;
        } else if (i == 2) {
            i4 = R.drawable.card_red;
        } else if (i == 3) {
            i4 = R.drawable.card_grey;
            str = String.format(Locale.getDefault(), "%s%d-%s", new Object[]{this.mContext.getString(R.string.na_main_card_title_sim), Integer.valueOf(i2 + 1), this.mContext.getString(R.string.main_indicator_title)});
            this.mAdSimView.setVisibility(0);
        } else if (i == 4) {
            this.mCardBackgroundView.setBackgroundResource(0);
            showNoSimView(true);
            return;
        }
        showNoSimView(false);
        this.mCardTitleImage.setImageResource(i2 == 0 ? R.drawable.icon_sim1 : R.drawable.icon_sim2);
        this.mCardTitle.setText(str);
        this.mCardBackgroundView.setBackgroundResource(i4);
        View view = this.mPackageUsedView;
        if (i != 3) {
            i3 = 0;
        }
        view.setVisibility(i3);
        this.mBackgroundAnimView.setParam(this.mContext.getResources().getColor(i5), f, false);
        invalidate();
    }

    public void setDataUsageButtonEnable(boolean z) {
        this.mButtonAdjustUsage.setEnabled(z);
    }

    public void setDataUsageButtonText(int i) {
        this.mButtonAdjustUsage.setText(i);
    }

    public void setDataUsageButtonText(String str) {
        this.mButtonAdjustUsage.setText(str);
    }

    public void setDataUsageButtonVisible(boolean z) {
        this.mButtonAdjustUsage.setVisibility(z ? 0 : 8);
    }

    public void setDataUsageClickListener(View.OnClickListener onClickListener) {
        this.mButtonAdjustUsage.setOnClickListener(onClickListener);
    }

    public void setDataUsageLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mButtonAdjustUsage.setLongClickable(true);
        this.mButtonAdjustUsage.setOnLongClickListener(onLongClickListener);
    }

    public void setErrorTextVisibility(int i) {
        TextView textView;
        this.mErrorTextView.setVisibility(i);
        int i2 = 8;
        if (i != 0 && !TextUtils.isEmpty(this.mPreAdjustTimeTextView.getText())) {
            textView = this.mPreAdjustTimeTextView;
            i2 = 0;
        } else {
            textView = this.mPreAdjustTimeTextView;
        }
        textView.setVisibility(i2);
        updateSplitViewVisible();
    }

    public void setFormattingTextView(TextView textView, TextView textView2, long j) {
        String[] formatBytesSplited = FormatBytesUtil.formatBytesSplited(this.mContext, j);
        textView.setText(formatBytesSplited[0]);
        textView2.setText(formatBytesSplited[1]);
    }

    public void setHasLeisure(boolean z) {
        this.mHasLeisure = z;
    }

    public void setLeisureTrafficRemained(boolean z, long j) {
    }

    public void setMainBillRemainTextView(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mMainBillRemainTextView.setText(str);
        }
    }

    public void setMainMonthPackageTextView(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mMainMonthPackageTextView.setText(str);
        }
    }

    public void setMainTodayUsedTextView(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mMainTodayUsedTextView.setText(str);
        }
    }

    public void setMonthPackage(long j) {
        if (j >= 0) {
            setFormattingTextView(this.mMonthPackageTextView, this.mMonthUsedUnit, j);
        }
    }

    public void setMonthPackageInfo(long j, long j2, float f, boolean z) {
        long j3 = j2 - j;
        if (j2 < 0 || j < 0) {
            String[] formatBytesSplited = FormatBytesUtil.formatBytesSplited(this.mContext, j);
            this.mMonthRemainedView.setText(formatBytesSplited[0]);
            this.mUnitTextView.setText(formatBytesSplited[1]);
            return;
        }
        if (z) {
            setFormattingTextView(this.mMonthPackageTextView, this.mMonthUsedUnit, j2);
        } else {
            this.mMonthPackageTextView.setText(R.string.main_no_info);
            this.mMonthUsedUnit.setText("");
        }
        String[] formatBytesSplited2 = FormatBytesUtil.formatBytesSplited(this.mContext, Math.abs(j3));
        this.mMonthRemainedView.setText(formatBytesSplited2[0]);
        this.mUnitTextView.setText(formatBytesSplited2[1]);
        updatePrimaryMessage(j, j2, f, z);
    }

    public void setMonthPackageViewVisible(boolean z) {
        int i = 0;
        this.mMonthPackageTextView.setVisibility(z ? 0 : 8);
        TextView textView = this.mMainMonthPackageTextView;
        if (!z) {
            i = 8;
        }
        textView.setVisibility(i);
    }

    public void setMonthRemain(long j) {
        if (j <= 0) {
            j = 0;
        }
        String[] formatBytesSplited = FormatBytesUtil.formatBytesSplited(this.mContext, Math.abs(j));
        this.mMonthRemainedView.setText(formatBytesSplited[0]);
        this.mUnitTextView.setText(formatBytesSplited[1]);
    }

    public void setMonthRemainedClickListener(View.OnClickListener onClickListener) {
        this.mMonthRemainedView.setOnClickListener(onClickListener);
        this.mErrorTextView.setOnClickListener(onClickListener);
    }

    public void setMonthRemainedViewVisible(boolean z) {
        this.mMonthRemainedView.setVisibility(z ? 0 : 8);
    }

    public void setMonthUsedText(int i) {
        this.mMainMonthPackageTextView.setText(i);
    }

    public void setPreAdjustTime(long j) {
        if (j > 0) {
            if (this.mErrorTextView.getVisibility() != 0) {
                this.mPreAdjustTimeTextView.setVisibility(0);
            }
            this.mPreAdjustTimeTextView.setText(TextPrepareUtil.getPreAdjustTimeTips(this.mContext, j, System.currentTimeMillis()));
        }
        updateSplitViewVisible();
    }

    public void setPrimaryTextLayoutVisible(boolean z) {
        this.mPrimaryTextLayout.setVisibility(z ? 0 : 8);
    }

    public void setTodayUsed(long j) {
        if (j > 0) {
            setFormattingTextView(this.mTodayUsedTextView, this.mTodayUsedTextUnit, j);
        }
    }

    public void setUnitTextViewVisible(boolean z) {
        this.mUnitTextView.setVisibility(z ? 0 : 8);
    }

    public void setUnlimitedMonthPackageInfo(long j, String str) {
        this.mMonthPackageTextView.setText(str);
        this.mMonthUsedUnit.setText("");
        String[] formatBytesSplited = FormatBytesUtil.formatBytesSplited(this.mContext, j);
        this.mMonthRemainedView.setText(formatBytesSplited[0]);
        this.mUnitTextView.setText(formatBytesSplited[1]);
        showPrimaryMessage((int) R.string.main_month_total_used);
    }

    public void showNoSimView(boolean z) {
        int i = 8;
        this.mCardTitleImage.setVisibility(z ? 8 : 0);
        this.mCardTitle.setVisibility(z ? 8 : 0);
        this.mPackageUsedView.setVisibility(z ? 8 : 0);
        this.mBackgroundAnimView.setVisibility(z ? 8 : 0);
        LinearLayout linearLayout = this.mNoSimView;
        if (z) {
            i = 0;
        }
        linearLayout.setVisibility(i);
        if (z && this.mNoSimIconView == null) {
            this.mNoSimIconView = new MamlView(getContext(), DeviceUtil.isDarkMode(getContext()) ? "maml/na_nosim_dark" : "maml/na_nosim", 2);
            int a2 = i.a(getContext(), 175.0f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(a2, a2);
            layoutParams.addRule(14, -1);
            layoutParams.setMargins(0, i.a(getContext(), 55.0f), 0, 0);
            this.mNoSimView.addView(this.mNoSimIconView, 0, layoutParams);
            this.mNoSimView.setOnClickListener(this);
            this.mNoSimIconView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 1) {
                        MainTrafficUsedView.this.switchMamlView();
                    }
                    return true;
                }
            });
        }
    }

    public void showPrimaryMessage(int i) {
        this.mPrimaryTextView.setText(i);
    }

    public void showPrimaryMessage(String str) {
        this.mPrimaryTextView.setText(str);
    }

    public void startAnim() {
        getHandler().post(new Runnable() {
            public void run() {
                MainTrafficUsedView.this.mButtonAdjustUsage.startAnim();
            }
        });
    }

    public void unRegisterBillLayoutClickListener() {
        setBillLayoutClickListener((View.OnClickListener) null);
    }

    public void unRegisterMonthRemainedClickListener() {
        setMonthRemainedClickListener((View.OnClickListener) null);
    }
}
