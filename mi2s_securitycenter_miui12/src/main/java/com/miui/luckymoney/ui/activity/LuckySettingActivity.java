package com.miui.luckymoney.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import b.b.c.j.B;
import b.b.c.j.i;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.CommonPerConstants;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.upgrade.LuckyMoneyHelper;
import com.miui.luckymoney.utils.PackageUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.luckymoney.webapi.UploadConfigAsyncTask;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import miui.app.Activity;
import miui.widget.SlidingButton;

public class LuckySettingActivity extends BaseMiuiActivity {
    private static final int MSG_UPDATE_CONFIG = 1;
    static final int SHOW_GUIDE_REQUEST = 0;
    static final int SHOW_GUIDE_RESULT_OPEN = 204;
    private final String TAG = LuckySettingActivity.class.getName();
    private View layoutAlarm;
    private View layoutFastOpen;
    private View layoutHasLuckyMoney;
    private View layoutNoLuckyMoney;
    /* access modifiers changed from: private */
    public Context mAppContext;
    private TextView mBackTextView;
    private TextView mBannerSummaryTextView;
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    private BroadcastReceiver mConfigChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String stringExtra;
            if (Constants.ACTION_CONFIG_CHANGED_BROADCAST.equals(intent.getAction()) && (stringExtra = intent.getStringExtra(Constants.KEY_CONFIG_CHANGED_FLAG)) != null) {
                Message obtainMessage = LuckySettingActivity.this.mMainHandler.obtainMessage(1);
                obtainMessage.obj = stringExtra;
                LuckySettingActivity.this.mMainHandler.sendMessage(obtainMessage);
            }
        }
    };
    private TextView mFastOpenTextView;
    private TextView mFunctionNoWorkView;
    private View mLayoutToolbar;
    private TextView mLuckyAlarmTextView;
    private TextView mLuckyMaxSourceTextView;
    private View mLuckyMoneyWarningInfoView;
    private TextView mLuckyWarningCountTextView;
    /* access modifiers changed from: private */
    public Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            if (message.what != 1) {
                super.handleMessage(message);
                return;
            }
            String str = (String) message.obj;
            if (Constants.TYPE_FAST_OPEN.equals(str)) {
                LuckySettingActivity.this.txvFastOpenStatus.setText(LuckySettingActivity.this.mCommonConfig.isFastOpenEnable() ? "已开启" : "已关闭");
            } else if (Constants.TYPE_LUCKY_OPEN.equals(str)) {
                LuckySettingActivity.this.mXiaomiLuckyMoneySliding.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                LuckySettingActivity.this.mXiaomiLuckyMoneySliding.setChecked(CommonConfig.getInstance(LuckySettingActivity.this.getApplicationContext()).getXiaomiLuckyMoneyEnable());
                LuckySettingActivity luckySettingActivity = LuckySettingActivity.this;
                luckySettingActivity.updateXiaomiLuckyMoney(CommonConfig.getInstance(luckySettingActivity.getApplicationContext()).getXiaomiLuckyMoneyEnable());
                LuckySettingActivity.this.mXiaomiLuckyMoneySliding.setOnCheckedChangeListener(LuckySettingActivity.this.mXiaomiLuckyMoneyChangedListener);
            }
        }
    };
    private View mMasterSwitchView;
    private View.OnClickListener mMoreSettingClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (LuckySettingActivity.this.mCommonConfig.getXiaomiLuckyMoneyEnable()) {
                Activity activity = LuckySettingActivity.this;
                activity.startActivity(new Intent(activity, SecondarySettingActivity.class));
            }
        }
    };
    private TextView mMoreSettingTextView;
    private View mMoreSettingView;
    private View mNoLuckyMoneyView;
    private Button mShareButton;
    /* access modifiers changed from: private */
    public CompoundButton.OnCheckedChangeListener mXiaomiLuckyMoneyChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (!z) {
                LuckySettingActivity.this.showCloseDialog();
            } else {
                LuckySettingActivity.this.updateXiaomiLuckyMoney(true);
            }
        }
    };
    /* access modifiers changed from: private */
    public SlidingButton mXiaomiLuckyMoneySliding;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Activity activity;
            Intent intent;
            switch (view.getId()) {
                case R.id.btn_back /*2131296541*/:
                    LuckySettingActivity.this.finish();
                    return;
                case R.id.btn_share /*2131296555*/:
                    Intent intent2 = new Intent("miui.luckymoney.action.ACCESS_LM_SHARE");
                    String str = null;
                    String luckyMaxSource = CommonPerConstants.DEFAULT.LUCKY_MAX_SOURCE_DEFAULT.equals(LuckySettingActivity.this.mCommonConfig.getLuckyMaxSource()) ? null : LuckySettingActivity.this.mCommonConfig.getLuckyMaxSource();
                    if (!CommonPerConstants.DEFAULT.LUCKY_MAX_SOURCE_DEFAULT.equals(LuckySettingActivity.this.mCommonConfig.getPersonalLuckyMaxSource())) {
                        str = LuckySettingActivity.this.mCommonConfig.getPersonalLuckyMaxSource();
                    }
                    intent2.putExtra("maxGroup", luckyMaxSource);
                    intent2.putExtra("maxPersonal", str);
                    intent2.putExtra("receivedValue", LuckySettingActivity.this.mCommonConfig.getReceiveTotalLuckyMoney());
                    intent2.putExtra("warningTotal", LuckySettingActivity.this.mCommonConfig.getWarningLuckyMoneyCount());
                    LuckySettingActivity.this.startActivity(intent2);
                    MiStatUtil.recordShare(true);
                    return;
                case R.id.layout_alarm /*2131297166*/:
                    if (LuckySettingActivity.this.mCommonConfig.getXiaomiLuckyMoneyEnable()) {
                        activity = LuckySettingActivity.this;
                        intent = new Intent(activity, LuckyAlarmActivity.class);
                        break;
                    } else {
                        return;
                    }
                case R.id.layout_fast_open /*2131297181*/:
                    if (LuckySettingActivity.this.mCommonConfig.getXiaomiLuckyMoneyEnable()) {
                        activity = LuckySettingActivity.this;
                        intent = new Intent(activity, FastOpenListActivity.class);
                        break;
                    } else {
                        return;
                    }
                case R.id.layout_master_switch /*2131297189*/:
                    LuckySettingActivity.this.mXiaomiLuckyMoneySliding.setChecked(true ^ LuckySettingActivity.this.mXiaomiLuckyMoneySliding.isChecked());
                    return;
                case R.id.tv_function_no_work /*2131297908*/:
                    PackageUtil.startUriWithBrowser(LuckySettingActivity.this.mAppContext, "http://api.miui.security.xiaomi.com/netassist/floworderunity/cdn/luckymoneyhelp");
                    MiStatUtil.recordFuncNoWork();
                    return;
                default:
                    return;
            }
            activity.startActivity(intent);
        }
    };
    private TextView txvAlarmStatus;
    /* access modifiers changed from: private */
    public TextView txvFastOpenStatus;
    private TextView txvNumberOfLuckyMoney;
    private TextView txvWarningSummary;

    private void initBannerSummaryView() {
        long warningLuckyMoneyCount = this.mCommonConfig.getWarningLuckyMoneyCount();
        if (warningLuckyMoneyCount > 0) {
            this.mNoLuckyMoneyView.setVisibility(8);
            this.mBannerSummaryTextView.setVisibility(8);
            this.mLuckyMoneyWarningInfoView.setVisibility(0);
            this.layoutHasLuckyMoney.setVisibility(0);
            this.layoutNoLuckyMoney.setVisibility(8);
            this.txvNumberOfLuckyMoney.setText(warningLuckyMoneyCount + "");
            this.mLuckyWarningCountTextView.setText(getResources().getString(R.string.lucky_str_yuan, new Object[]{Float.valueOf(((float) this.mCommonConfig.getMMMoney()) / 100.0f)}));
            this.mLuckyMaxSourceTextView.setText(getResources().getString(R.string.lucky_str_yuan, new Object[]{Float.valueOf(((float) this.mCommonConfig.getQQMoney()) / 100.0f)}));
            return;
        }
        this.mNoLuckyMoneyView.setVisibility(0);
        this.mBannerSummaryTextView.setVisibility(0);
        this.mLuckyMoneyWarningInfoView.setVisibility(8);
        this.layoutHasLuckyMoney.setVisibility(8);
        this.layoutNoLuckyMoney.setVisibility(0);
        this.mShareButton.setVisibility(8);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckySettingActivity, miui.app.Activity] */
    private void initView() {
        this.mLayoutToolbar = findViewById(R.id.layout_lucky_title);
        if (i.e()) {
            ScreenUtil.setNotchToolbarMarginTop(this, this.mLayoutToolbar);
        } else {
            ScreenUtil.setStatusbarMarginTop(this, this.mLayoutToolbar);
        }
        this.txvWarningSummary = (TextView) findViewById(R.id.warning_summary);
        this.txvWarningSummary.setText(Html.fromHtml(getString(R.string.best_warning_dialog_message)));
        this.mNoLuckyMoneyView = findViewById(R.id.no_luckymoney_view);
        this.mXiaomiLuckyMoneySliding = findViewById(R.id.sliding_button_all_control);
        this.mXiaomiLuckyMoneySliding.setChecked(this.mCommonConfig.getXiaomiLuckyMoneyEnable());
        this.mMoreSettingView = findViewById(R.id.layout_more_setting);
        this.layoutAlarm = findViewById(R.id.layout_alarm);
        this.layoutFastOpen = findViewById(R.id.layout_fast_open);
        this.mMasterSwitchView = findViewById(R.id.layout_master_switch);
        this.mLuckyAlarmTextView = (TextView) findViewById(R.id.open_alarm);
        this.mFastOpenTextView = (TextView) findViewById(R.id.txv_fast_open);
        this.mMoreSettingTextView = (TextView) findViewById(R.id.open_more_setting);
        this.mBannerSummaryTextView = (TextView) findViewById(R.id.tv_banner_summary_desc);
        this.mLuckyMoneyWarningInfoView = findViewById(R.id.layout_lucky_money_warning_info);
        this.mLuckyWarningCountTextView = (TextView) findViewById(R.id.lucky_warning_count);
        this.mLuckyMaxSourceTextView = (TextView) findViewById(R.id.lucky_max_chat_source);
        this.txvNumberOfLuckyMoney = (TextView) findViewById(R.id.txvNumberOfLuckyMoney);
        this.txvAlarmStatus = (TextView) findViewById(R.id.txv_alarm_status);
        this.txvFastOpenStatus = (TextView) findViewById(R.id.txv_fast_open_status);
        this.layoutHasLuckyMoney = findViewById(R.id.layoutHasLuckyMoney);
        this.layoutNoLuckyMoney = findViewById(R.id.layoutNoLuckyMoney);
        this.mFunctionNoWorkView = (TextView) findViewById(R.id.tv_function_no_work);
        this.mShareButton = (Button) findViewById(R.id.btn_share);
        this.mBackTextView = (TextView) findViewById(R.id.btn_back);
        this.mFunctionNoWorkView.setText(Html.fromHtml(this.mAppContext.getString(R.string.warn_function_no_work)));
        String str = "已开启";
        this.txvAlarmStatus.setText(this.mCommonConfig.getLuckyAlarmEnable() ? str : "已关闭");
        TextView textView = this.txvFastOpenStatus;
        if (!this.mCommonConfig.isFastOpenEnable()) {
            str = "已关闭";
        }
        textView.setText(str);
        int i = 0;
        boolean z = PackageUtil.isInstalledPackage(this.mAppContext, com.miui.earthquakewarning.Constants.SECURITY_ADD_PACKAGE) && PackageUtil.getAppVersionCode(this.mAppContext.getPackageManager(), com.miui.earthquakewarning.Constants.SECURITY_ADD_PACKAGE) > 61208;
        Button button = this.mShareButton;
        if (!z) {
            i = 8;
        }
        button.setVisibility(i);
        updateXiaomiLuckyMoney(this.mCommonConfig.getXiaomiLuckyMoneyEnable());
        initBannerSummaryView();
        this.mXiaomiLuckyMoneySliding.setOnCheckedChangeListener(this.mXiaomiLuckyMoneyChangedListener);
        this.mMoreSettingView.setOnClickListener(this.mMoreSettingClickListener);
        this.layoutAlarm.setOnClickListener(this.onClickListener);
        this.layoutFastOpen.setOnClickListener(this.onClickListener);
        this.mMasterSwitchView.setOnClickListener(this.onClickListener);
        this.mShareButton.setOnClickListener(this.onClickListener);
        this.mBackTextView.setOnClickListener(this.onClickListener);
        this.mFunctionNoWorkView.setOnClickListener(this.onClickListener);
    }

    private void registerConfigChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        registerReceiver(this.mConfigChangedReceiver, intentFilter);
    }

    private void sendConfigChangedBroadcast(String str) {
        Intent intent = new Intent(Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        intent.putExtra(Constants.KEY_CONFIG_CHANGED_FLAG, str);
        sendBroadcastAsUser(intent, B.b());
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckySettingActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void showCloseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.close_lucky_money_dialog_title);
        builder.setMessage(R.string.close_lucky_money_dialog_summary);
        builder.setNegativeButton(R.string.hongbao_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                LuckySettingActivity.this.mXiaomiLuckyMoneySliding.setChecked(true);
            }
        });
        builder.setPositiveButton(R.string.close_lucky_money_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                LuckySettingActivity.this.updateXiaomiLuckyMoney(false);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                LuckySettingActivity.this.mXiaomiLuckyMoneySliding.setChecked(true);
            }
        });
        if (!isFinishing()) {
            builder.create().show();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckySettingActivity, miui.app.Activity] */
    private void showGuide() {
        if (!this.mCommonConfig.isFirstStartUp() || this.mCommonConfig.getXiaomiLuckyMoneyEnable()) {
            showTipsDialog();
        } else {
            startActivityForResult(new Intent(this, GuideActivity.class), 0);
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckySettingActivity] */
    private void showTipsDialog() {
        if (this.mCommonConfig.isShouldUserTips()) {
            this.mCommonConfig.setShouldUserTips(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.best_warning_dialog_title);
            builder.setMessage(Html.fromHtml(this.mAppContext.getString(R.string.best_warning_dialog_message1)));
            builder.setCancelable(false);
            builder.setPositiveButton(this.mAppContext.getString(R.string.best_warning_dialog_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.create().show();
        }
    }

    private void unregisterConfigChangedReceiver() {
        unregisterReceiver(this.mConfigChangedReceiver);
    }

    /* access modifiers changed from: private */
    public void updateXiaomiLuckyMoney(boolean z) {
        this.mCommonConfig.setXiaomiLuckyMoneyEnable(z);
        if (z) {
            this.mLuckyAlarmTextView.setTextColor(this.mAppContext.getResources().getColor(R.color.lucky_settings_item_title_color));
            this.mFastOpenTextView.setTextColor(this.mAppContext.getResources().getColor(R.color.lucky_settings_item_title_color));
            this.mMoreSettingTextView.setTextColor(this.mAppContext.getResources().getColor(R.color.lucky_settings_item_title_color));
            LuckyMoneyHelper.startLuckyMoneyService(this.mAppContext);
        } else {
            this.mLuckyAlarmTextView.setTextColor(this.mAppContext.getResources().getColor(R.color.lucky_settings_item_summary_color));
            this.mFastOpenTextView.setTextColor(this.mAppContext.getResources().getColor(R.color.lucky_settings_item_summary_color));
            this.mMoreSettingTextView.setTextColor(this.mAppContext.getResources().getColor(R.color.lucky_settings_item_summary_color));
            LuckyMoneyHelper.stopLuckyMoneyService(this.mAppContext);
        }
        sendConfigChangedBroadcast(Constants.TYPE_SHOW_FLOAT_WINDOW_BUTTON);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 0) {
            return;
        }
        if (i2 == SHOW_GUIDE_RESULT_OPEN) {
            this.mCommonConfig.setFirstStartUp(false);
            this.mCommonConfig.setXiaomiLuckyMoneyEnable(true);
            this.mCommonConfig.setDesktopFloatWindowEnable(true);
            this.mXiaomiLuckyMoneySliding.setChecked(true);
            updateXiaomiLuckyMoney(true);
            showTipsDialog();
            return;
        }
        this.mCommonConfig.setFirstStartUp(true);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        LuckySettingActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_lucky_setting);
        k.a((Activity) this);
        this.mAppContext = getApplicationContext();
        this.mCommonConfig = CommonConfig.getInstance(this.mAppContext);
        showGuide();
        initView();
        registerConfigChangedReceiver();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        LuckySettingActivity.super.onDestroy();
        unregisterConfigChangedReceiver();
        this.mMainHandler.removeMessages(1);
        if (this.mCommonConfig.isConfigChanged()) {
            Log.i(this.TAG, "upload settings");
            new UploadConfigAsyncTask().execute(new Boolean[]{Boolean.valueOf(this.mCommonConfig.getXiaomiLuckyMoneyEnable()), Boolean.valueOf(this.mCommonConfig.getLuckyAlarmEnable())});
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        String str = "已开启";
        this.txvAlarmStatus.setText(this.mCommonConfig.getLuckyAlarmEnable() ? str : "已关闭");
        TextView textView = this.txvFastOpenStatus;
        if (!this.mCommonConfig.isFastOpenEnable()) {
            str = "已关闭";
        }
        textView.setText(str);
    }
}
