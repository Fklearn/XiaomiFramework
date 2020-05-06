package com.miui.luckymoney.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.B;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.config.DoNotDisturbConstants;
import com.miui.luckymoney.utils.DateUtil;
import com.miui.securitycenter.R;
import com.miui.warningcenter.WarningCenterAlertAdapter;
import java.lang.ref.WeakReference;
import miui.app.TimePickerDialog;
import miui.widget.SlidingButton;
import miui.widget.TimePicker;

public class SecondarySettingActivity extends BaseMiuiActivity {
    private Context mAppContext;
    private CompoundButton.OnCheckedChangeListener mBusinessLuckyWarningChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            SecondarySettingActivity.this.mCommonConfig.setBusinessLuckyWarningEnable(z);
        }
    };
    /* access modifiers changed from: private */
    public SlidingButton mBusniessLuckyWarningSliding;
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    private BroadcastReceiver mConfigChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_CONFIG_CHANGED_BROADCAST.equals(intent.getAction()) && intent.getStringExtra(Constants.KEY_CONFIG_CHANGED_FLAG) != null) {
                SecondarySettingActivity.this.mDesktopFloatWindowSliding.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                SecondarySettingActivity.this.mDesktopFloatWindowSliding.setChecked(SecondarySettingActivity.this.mCommonConfig.getDesktopFloatWindowEnable());
                SecondarySettingActivity.this.mDesktopFloatWindowSliding.setOnCheckedChangeListener(SecondarySettingActivity.this.mDesktopFloatWindowChangedListener);
            }
        }
    };
    /* access modifiers changed from: private */
    public TextView mDNDEndTime;
    private ImageView mDNDEndTimeIco;
    private TextView mDNDEndTimeTile;
    private CompoundButton.OnCheckedChangeListener mDNDModeChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            SecondarySettingActivity.this.mCommonConfig.setDNDModeEnable(z);
            SecondarySettingActivity.this.setDNDViewEnable(z);
        }
    };
    /* access modifiers changed from: private */
    public TextView mDNDStartTime;
    private ImageView mDNDStartTimeIco;
    private TextView mDNDStartTimeTile;
    /* access modifiers changed from: private */
    public TextView mDNDType;
    private ImageView mDNDTypeIco;
    private TextView mDNDTypeTile;
    /* access modifiers changed from: private */
    public CompoundButton.OnCheckedChangeListener mDesktopFloatWindowChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (z != SecondarySettingActivity.this.mCommonConfig.getDesktopFloatWindowEnable()) {
                SecondarySettingActivity.this.mCommonConfig.setDesktopFloatWindowEnable(z);
                SecondarySettingActivity.this.sendConfigChangedBroadcast(Constants.TYPE_SHOW_FLOAT_WINDOW_BUTTON);
            }
        }
    };
    /* access modifiers changed from: private */
    public SlidingButton mDesktopFloatWindowSliding;
    /* access modifiers changed from: private */
    public SlidingButton mDoNotDisturbModeSliding;
    private View mLayoutDND;
    private View mLayoutDNDEndTime;
    private View mLayoutDNDStartTime;
    private View mLayoutDNDType;
    private View mLayoutFloatTips;
    private View mLayoutLuckySoundMode;
    private View mLayoutRemindBussness;
    private View mLayoutRemindGroups;
    private View mLayoutRemindMM;
    private View mLayoutRemindMiLiao;
    private View mLayoutRemindQQ;
    /* access modifiers changed from: private */
    public TextView mLuckySoundMode;
    private CompoundButton.OnCheckedChangeListener mMiliaoLuckyWarningChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            SecondarySettingActivity.this.mCommonConfig.setMiliaoLuckyWarningEnable(z);
        }
    };
    /* access modifiers changed from: private */
    public SlidingButton mMiliaoLuckyWarningSliding;
    private View.OnClickListener mOnTextViewClickListener = new View.OnClickListener() {
        /* JADX WARNING: type inference failed for: r3v5, types: [com.miui.luckymoney.ui.activity.SecondarySettingActivity, android.content.Context] */
        /* JADX WARNING: type inference failed for: r2v12, types: [com.miui.luckymoney.ui.activity.SecondarySettingActivity, android.content.Context] */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x00a0, code lost:
            r6.setSingleChoiceItems(r2, r3, r4).setNegativeButton(com.miui.securitycenter.R.string.hongbao_cancel, (android.content.DialogInterface.OnClickListener) null).show();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x00dd, code lost:
            com.miui.luckymoney.ui.activity.SecondarySettingActivity.access$500(r6, r0, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x00e7, code lost:
            r6.toggle();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onClick(android.view.View r6) {
            /*
                r5 = this;
                int r6 = r6.getId()
                r0 = 0
                r1 = 2131756593(0x7f100631, float:1.9144098E38)
                switch(r6) {
                    case 2131296560: goto L_0x00e1;
                    case 2131297176: goto L_0x00c5;
                    case 2131297177: goto L_0x00ac;
                    case 2131297178: goto L_0x0066;
                    case 2131297179: goto L_0x005e;
                    case 2131297185: goto L_0x0056;
                    case 2131297187: goto L_0x002d;
                    case 2131297206: goto L_0x0025;
                    case 2131297326: goto L_0x001d;
                    case 2131297497: goto L_0x0015;
                    case 2131298048: goto L_0x000d;
                    default: goto L_0x000b;
                }
            L_0x000b:
                goto L_0x00ea
            L_0x000d:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                miui.widget.SlidingButton r6 = r6.mWechatLuckyWarningSliding
                goto L_0x00e7
            L_0x0015:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                miui.widget.SlidingButton r6 = r6.mQQLuckyWarningSliding
                goto L_0x00e7
            L_0x001d:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                miui.widget.SlidingButton r6 = r6.mMiliaoLuckyWarningSliding
                goto L_0x00e7
            L_0x0025:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                miui.widget.SlidingButton r6 = r6.mDesktopFloatWindowSliding
                goto L_0x00e7
            L_0x002d:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                r6.getResources()
                miui.app.AlertDialog$Builder r6 = new miui.app.AlertDialog$Builder
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r2 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                r6.<init>(r2)
                r2 = 2131756733(0x7f1006bd, float:1.9144382E38)
                miui.app.AlertDialog$Builder r6 = r6.setTitle(r2)
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r2 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                java.lang.String[] r2 = r2.soundModeArr
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r3 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                com.miui.luckymoney.config.CommonConfig r3 = r3.mCommonConfig
                int r3 = r3.getLuckySoundWarningLevel()
                com.miui.luckymoney.ui.activity.SecondarySettingActivity$8$2 r4 = new com.miui.luckymoney.ui.activity.SecondarySettingActivity$8$2
                r4.<init>()
                goto L_0x00a0
            L_0x0056:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                miui.widget.SlidingButton r6 = r6.mOnlyNotiGroupLuckyMoneySliding
                goto L_0x00e7
            L_0x005e:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                miui.widget.SlidingButton r6 = r6.mDoNotDisturbModeSliding
                goto L_0x00e7
            L_0x0066:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                android.content.res.Resources r6 = r6.getResources()
                r2 = 2
                java.lang.String[] r2 = new java.lang.String[r2]
                r3 = 0
                r4 = 2131755991(0x7f1003d7, float:1.9142877E38)
                java.lang.String r4 = r6.getString(r4)
                r2[r3] = r4
                r3 = 1
                r4 = 2131755990(0x7f1003d6, float:1.9142875E38)
                java.lang.String r6 = r6.getString(r4)
                r2[r3] = r6
                miui.app.AlertDialog$Builder r6 = new miui.app.AlertDialog$Builder
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r3 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                r6.<init>(r3)
                r3 = 2131755989(0x7f1003d5, float:1.9142873E38)
                miui.app.AlertDialog$Builder r6 = r6.setTitle(r3)
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r3 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                com.miui.luckymoney.config.CommonConfig r3 = r3.mCommonConfig
                int r3 = r3.getDNDModeLevel()
                com.miui.luckymoney.ui.activity.SecondarySettingActivity$8$1 r4 = new com.miui.luckymoney.ui.activity.SecondarySettingActivity$8$1
                r4.<init>()
            L_0x00a0:
                miui.app.AlertDialog$Builder r6 = r6.setSingleChoiceItems(r2, r3, r4)
                miui.app.AlertDialog$Builder r6 = r6.setNegativeButton(r1, r0)
                r6.show()
                goto L_0x00ea
            L_0x00ac:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                long r0 = com.miui.luckymoney.utils.DateUtil.getTodayTimeMillis()
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r2 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                com.miui.luckymoney.config.CommonConfig r2 = r2.mCommonConfig
                long r2 = r2.getDNDStartTime()
                long r0 = r0 + r2
                com.miui.luckymoney.ui.activity.SecondarySettingActivity$StartTimeListener r2 = new com.miui.luckymoney.ui.activity.SecondarySettingActivity$StartTimeListener
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r3 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                r2.<init>(r3)
                goto L_0x00dd
            L_0x00c5:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                long r0 = com.miui.luckymoney.utils.DateUtil.getTodayTimeMillis()
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r2 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                com.miui.luckymoney.config.CommonConfig r2 = r2.mCommonConfig
                long r2 = r2.getDNDStopTime()
                long r0 = r0 + r2
                com.miui.luckymoney.ui.activity.SecondarySettingActivity$EndTimeListener r2 = new com.miui.luckymoney.ui.activity.SecondarySettingActivity$EndTimeListener
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r3 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                r2.<init>(r3)
            L_0x00dd:
                r6.createTimePicker(r0, r2)
                goto L_0x00ea
            L_0x00e1:
                com.miui.luckymoney.ui.activity.SecondarySettingActivity r6 = com.miui.luckymoney.ui.activity.SecondarySettingActivity.this
                miui.widget.SlidingButton r6 = r6.mBusniessLuckyWarningSliding
            L_0x00e7:
                r6.toggle()
            L_0x00ea:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.luckymoney.ui.activity.SecondarySettingActivity.AnonymousClass8.onClick(android.view.View):void");
        }
    };
    private CompoundButton.OnCheckedChangeListener mOnlyNotiGroupLuckyMoneyChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            SecondarySettingActivity.this.mCommonConfig.setOnlyNotiGroupLuckuMoneyConfig(z);
        }
    };
    /* access modifiers changed from: private */
    public SlidingButton mOnlyNotiGroupLuckyMoneySliding;
    private CompoundButton.OnCheckedChangeListener mQQLuckyWarningChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            SecondarySettingActivity.this.mCommonConfig.setQQLuckyWarningEnable(z);
        }
    };
    /* access modifiers changed from: private */
    public SlidingButton mQQLuckyWarningSliding;
    private CompoundButton.OnCheckedChangeListener mWechatLuckyWarningChangedListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            SecondarySettingActivity.this.mCommonConfig.setWeChatLuckyWarningEnable(z);
        }
    };
    /* access modifiers changed from: private */
    public SlidingButton mWechatLuckyWarningSliding;
    /* access modifiers changed from: private */
    public String[] soundModeArr;

    private static class EndTimeListener implements TimePickerDialog.OnTimeSetListener {
        private WeakReference<SecondarySettingActivity> mActivityRef;

        public EndTimeListener(SecondarySettingActivity secondarySettingActivity) {
            this.mActivityRef = new WeakReference<>(secondarySettingActivity);
        }

        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            SecondarySettingActivity secondarySettingActivity = (SecondarySettingActivity) this.mActivityRef.get();
            if (secondarySettingActivity != null) {
                long millisUsingHM = DateUtil.getMillisUsingHM(i, i2);
                secondarySettingActivity.mCommonConfig.setDNDStopTime(millisUsingHM);
                secondarySettingActivity.mDNDEndTime.setText(DateFormat.format(WarningCenterAlertAdapter.FORMAT_TIME, millisUsingHM + DateUtil.getTodayTimeMillis()));
            }
        }
    }

    private static class StartTimeListener implements TimePickerDialog.OnTimeSetListener {
        private WeakReference<SecondarySettingActivity> mActivityRef;

        public StartTimeListener(SecondarySettingActivity secondarySettingActivity) {
            this.mActivityRef = new WeakReference<>(secondarySettingActivity);
        }

        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            SecondarySettingActivity secondarySettingActivity = (SecondarySettingActivity) this.mActivityRef.get();
            if (secondarySettingActivity != null) {
                long millisUsingHM = DateUtil.getMillisUsingHM(i, i2);
                secondarySettingActivity.mCommonConfig.setDNDStartTime(millisUsingHM);
                secondarySettingActivity.mDNDStartTime.setText(DateFormat.format(WarningCenterAlertAdapter.FORMAT_TIME, millisUsingHM + DateUtil.getTodayTimeMillis()));
            }
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createTimePicker(long r8, miui.app.TimePickerDialog.OnTimeSetListener r10) {
        /*
            r7 = this;
            java.util.Calendar r0 = java.util.Calendar.getInstance()
            r0.setTimeInMillis(r8)
            r8 = 11
            int r4 = r0.get(r8)
            r8 = 12
            int r5 = r0.get(r8)
            miui.app.TimePickerDialog r8 = new miui.app.TimePickerDialog
            r6 = 1
            r1 = r8
            r2 = r7
            r3 = r10
            r1.<init>(r2, r3, r4, r5, r6)
            r8.show()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.luckymoney.ui.activity.SecondarySettingActivity.createTimePicker(long, miui.app.TimePickerDialog$OnTimeSetListener):void");
    }

    private void initView() {
        this.mWechatLuckyWarningSliding = findViewById(R.id.sliding_button_lucky_warning);
        this.mQQLuckyWarningSliding = findViewById(R.id.sliding_button_qq_lucky_warning);
        this.mMiliaoLuckyWarningSliding = findViewById(R.id.sliding_button_miliao_lucky_warning);
        this.mBusniessLuckyWarningSliding = findViewById(R.id.sliding_button_business_lucky_warning);
        this.mOnlyNotiGroupLuckyMoneySliding = findViewById(R.id.sliding_lucky_money_group);
        this.mDoNotDisturbModeSliding = findViewById(R.id.sliding_button_open_dnd_mode);
        this.mDesktopFloatWindowSliding = findViewById(R.id.sliding_button_desktop_float_window);
        this.mLayoutDNDStartTime = findViewById(R.id.layout_dnd_start_time);
        this.mLayoutDNDEndTime = findViewById(R.id.layout_dnd_end_time);
        this.mLayoutDNDType = findViewById(R.id.layout_dnd_type);
        this.mLayoutLuckySoundMode = findViewById(R.id.layout_lucky_sound);
        this.mLayoutFloatTips = findViewById(R.id.layout_tips);
        this.mLayoutRemindMM = findViewById(R.id.wechat_lucky_warning);
        this.mLayoutRemindQQ = findViewById(R.id.qq_lucky_warning);
        this.mLayoutRemindMiLiao = findViewById(R.id.miliao_lucky_warning);
        this.mLayoutRemindBussness = findViewById(R.id.business_lucky_warning);
        this.mLayoutRemindGroups = findViewById(R.id.layout_lucky_money_group);
        this.mLayoutDND = findViewById(R.id.layout_donotdistrub_mode);
        this.mDNDStartTime = (TextView) findViewById(R.id.tv_dnd_start_time);
        this.mDNDStartTimeTile = (TextView) findViewById(R.id.tv_dnd_start_time_title);
        this.mDNDStartTimeIco = (ImageView) findViewById(R.id.iv_dnd_start_time_ico);
        this.mDNDEndTime = (TextView) findViewById(R.id.tv_dnd_end_time);
        this.mDNDEndTimeTile = (TextView) findViewById(R.id.tv_dnd_end_time_title);
        this.mDNDEndTimeIco = (ImageView) findViewById(R.id.iv_dnd_end_time_ico);
        this.mDNDType = (TextView) findViewById(R.id.tv_dnd_type);
        this.mDNDTypeTile = (TextView) findViewById(R.id.tv_dnd_type_title);
        this.mDNDTypeIco = (ImageView) findViewById(R.id.iv_dnd_type_ico);
        this.mLuckySoundMode = (TextView) findViewById(R.id.txv_lucky_sound);
        this.mWechatLuckyWarningSliding.setOnCheckedChangeListener(this.mWechatLuckyWarningChangedListener);
        this.mQQLuckyWarningSliding.setOnCheckedChangeListener(this.mQQLuckyWarningChangedListener);
        this.mMiliaoLuckyWarningSliding.setOnCheckedChangeListener(this.mMiliaoLuckyWarningChangedListener);
        this.mBusniessLuckyWarningSliding.setOnCheckedChangeListener(this.mBusinessLuckyWarningChangedListener);
        this.mOnlyNotiGroupLuckyMoneySliding.setOnCheckedChangeListener(this.mOnlyNotiGroupLuckyMoneyChangedListener);
        this.mDoNotDisturbModeSliding.setOnCheckedChangeListener(this.mDNDModeChangedListener);
        this.mDesktopFloatWindowSliding.setOnCheckedChangeListener(this.mDesktopFloatWindowChangedListener);
        this.mLayoutDNDStartTime.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutDNDEndTime.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutDNDType.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutLuckySoundMode.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutFloatTips.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutRemindMM.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutRemindQQ.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutRemindMiLiao.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutRemindBussness.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutRemindGroups.setOnClickListener(this.mOnTextViewClickListener);
        this.mLayoutDND.setOnClickListener(this.mOnTextViewClickListener);
        this.mWechatLuckyWarningSliding.setChecked(this.mCommonConfig.getWeChatLuckyWarningEnable());
        this.mQQLuckyWarningSliding.setChecked(this.mCommonConfig.getQQLuckyWarningEnable());
        this.mMiliaoLuckyWarningSliding.setChecked(this.mCommonConfig.getMiliaoLuckyWarningEnable());
        this.mBusniessLuckyWarningSliding.setChecked(this.mCommonConfig.getBusinessLuckyWarningEnable());
        this.mOnlyNotiGroupLuckyMoneySliding.setChecked(this.mCommonConfig.getOnlyNotiGroupLuckuMoneyConfig());
        this.mDoNotDisturbModeSliding.setChecked(this.mCommonConfig.isDNDModeOpen());
        this.mDesktopFloatWindowSliding.setChecked(this.mCommonConfig.getDesktopFloatWindowEnable());
        this.mDNDStartTime.setText(DateFormat.format(WarningCenterAlertAdapter.FORMAT_TIME, this.mCommonConfig.getDNDStartTime() + DateUtil.getTodayTimeMillis()));
        this.mDNDEndTime.setText(DateFormat.format(WarningCenterAlertAdapter.FORMAT_TIME, this.mCommonConfig.getDNDStopTime() + DateUtil.getTodayTimeMillis()));
        this.mDNDType.setText(DoNotDisturbConstants.DND_TEXT_ID[this.mCommonConfig.getDNDModeLevel()]);
        this.soundModeArr = getResources().getStringArray(R.array.luckymoney_sound);
        this.mLuckySoundMode.setText(this.soundModeArr[this.mCommonConfig.getLuckySoundWarningLevel()]);
        setDNDViewEnable(this.mCommonConfig.isDNDModeOpen());
    }

    private void registerConfigChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        registerReceiver(this.mConfigChangedReceiver, intentFilter);
    }

    /* access modifiers changed from: private */
    public void sendConfigChangedBroadcast(String str) {
        Intent intent = new Intent(Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        intent.putExtra(Constants.KEY_CONFIG_CHANGED_FLAG, str);
        sendBroadcastAsUser(intent, B.b());
    }

    /* access modifiers changed from: private */
    public void setDNDViewEnable(boolean z) {
        float f = 1.0f;
        this.mDNDStartTime.setAlpha(z ? 1.0f : 0.4f);
        this.mDNDStartTimeTile.setAlpha(z ? 1.0f : 0.4f);
        this.mDNDStartTimeIco.setAlpha(z ? 1.0f : 0.4f);
        this.mDNDEndTime.setAlpha(z ? 1.0f : 0.4f);
        this.mDNDEndTimeTile.setAlpha(z ? 1.0f : 0.4f);
        this.mDNDEndTimeIco.setAlpha(z ? 1.0f : 0.4f);
        this.mDNDType.setAlpha(z ? 1.0f : 0.4f);
        this.mDNDTypeTile.setAlpha(z ? 1.0f : 0.4f);
        ImageView imageView = this.mDNDTypeIco;
        if (!z) {
            f = 0.4f;
        }
        imageView.setAlpha(f);
        this.mLayoutDNDStartTime.setClickable(z);
        this.mLayoutDNDEndTime.setClickable(z);
        this.mLayoutDNDType.setClickable(z);
    }

    private void unregisterConfigChangedReceiver() {
        unregisterReceiver(this.mConfigChangedReceiver);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        SecondarySettingActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_secondary_setting);
        this.mAppContext = getApplicationContext();
        this.mCommonConfig = CommonConfig.getInstance(this.mAppContext);
        initView();
        registerConfigChangedReceiver();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        SecondarySettingActivity.super.onDestroy();
        unregisterConfigChangedReceiver();
    }
}
