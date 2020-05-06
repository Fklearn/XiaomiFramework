package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.c.a.b;
import b.b.c.c.b.g;
import com.miui.networkassistant.service.TcSmsReportService;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.ui.base.TrafficRelatedFragment;
import com.miui.networkassistant.ui.dialog.MessageDialog;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.ui.dialog.TextInputDialog;
import com.miui.networkassistant.ui.view.ToolbarItemView;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.securitycenter.R;
import java.util.Map;

public class TcSmsReportFragment extends TrafficRelatedFragment {
    private static final int ACTION_SMS_DIRECTION = 2;
    private static final int ACTION_SMS_NUM = 1;
    private static final int ACTION_SMS_RECEIVE_NUM = 3;
    public static final String EXTRA_VIEW_FROM = "view_from";
    private static final int TITLE_FILED = 2131758264;
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            TextInputDialog textInputDialog;
            String str;
            int i = 1;
            switch (view.getId()) {
                case 16908313:
                    new MessageDialog(TcSmsReportFragment.this.mActivity).buildShowDialog(TcSmsReportFragment.this.getString(R.string.tc_sms_report_tips_dialog_title), TcSmsReportFragment.this.getString(R.string.tc_sms_report_tips_dialog_message));
                    return;
                case R.id.button_get_sms /*2131296566*/:
                    if (TcSmsReportFragment.this.isTcServiceConnected()) {
                        TcSmsReportFragment.this.mAppContext.startService(new Intent(TcSmsReportFragment.this.mAppContext, TcSmsReportService.class));
                        if (TextUtils.isEmpty(TcSmsReportFragment.this.mSmsNumberToolbar.getDesc()) || TextUtils.isEmpty(TcSmsReportFragment.this.mSmsDirectionToolbar.getDesc()) || TextUtils.isEmpty(TcSmsReportFragment.this.mSmsReceiverNumberToolbar.getDesc())) {
                            AnalyticsHelper.trackEmptySmsReport();
                            Toast.makeText(TcSmsReportFragment.this.mAppContext, R.string.traffic_correction_failed, 1).show();
                        } else {
                            Toast.makeText(TcSmsReportFragment.this.mAppContext, TcSmsReportFragment.this.getString(R.string.tc_sms_report_toast_text), 1).show();
                            TcSmsReportFragment.this.mTcBinder.startMonitorSms(TcSmsReportFragment.this.mSmsNumberToolbar.getDesc(), TcSmsReportFragment.this.mSmsDirectionToolbar.getDesc(), TcSmsReportFragment.this.mSmsReceiverNumberToolbar.getDesc(), TcSmsReportFragment.this.mSlotNum, TcSmsReportFragment.this.mUploadType);
                        }
                        TcSmsReportFragment.this.checkAndApplyStatus();
                        return;
                    }
                    return;
                case R.id.button_get_sms_again /*2131296567*/:
                    TcSmsReportFragment.this.mTipsResultTextView.setText(TcSmsReportFragment.this.getString(R.string.tc_sms_report_get_default));
                    if (TcSmsReportFragment.this.isTcServiceConnected()) {
                        TcSmsReportFragment.this.mTcBinder.reset();
                    }
                    TcSmsReportFragment.this.initData();
                    return;
                case R.id.button_report_sms /*2131296571*/:
                    TcSmsReportFragment.this.tcSmsReportDeclare();
                    return;
                case R.id.layout_sms_direction /*2131297202*/:
                    TcSmsReportFragment.this.mInputDialog.setNumberText(false);
                    textInputDialog = TcSmsReportFragment.this.mInputDialog;
                    str = TcSmsReportFragment.this.getString(R.string.tc_sms_report_direction);
                    i = 2;
                    break;
                case R.id.layout_sms_number /*2131297203*/:
                    TcSmsReportFragment.this.mInputDialog.setNumberText(true);
                    textInputDialog = TcSmsReportFragment.this.mInputDialog;
                    str = TcSmsReportFragment.this.getString(R.string.tc_sms_report_send_num);
                    break;
                case R.id.layout_sms_receive_num /*2131297204*/:
                    TcSmsReportFragment.this.mInputDialog.setNumberText(true);
                    textInputDialog = TcSmsReportFragment.this.mInputDialog;
                    str = TcSmsReportFragment.this.getString(R.string.tc_sms_report_receive_num);
                    i = 3;
                    break;
                case R.id.layout_upload_type /*2131297208*/:
                    TcSmsReportFragment.this.mSortChoiceDialog.buildDialog(TcSmsReportFragment.this.mAppContext.getString(R.string.tc_sms_report_type), TcSmsReportFragment.this.mSmsReportString, TcSmsReportFragment.this.mSmsReportSelected, 0);
                    return;
                default:
                    return;
            }
            textInputDialog.buildInputDialog(str, "", i);
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TcSmsReportService.TcSmsReportServiceBinder unused = TcSmsReportFragment.this.mTcBinder = (TcSmsReportService.TcSmsReportServiceBinder) iBinder;
            TcSmsReportFragment.this.mTcBinder.registerSmsReportListener(TcSmsReportFragment.this.mSmsReportListener);
            TcSmsReportFragment.this.initData();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            TcSmsReportService.TcSmsReportServiceBinder unused = TcSmsReportFragment.this.mTcBinder = null;
        }
    };
    /* access modifiers changed from: private */
    public LinearLayout mGetAgainAndReportLayout;
    private Button mGetSmsAgainButton;
    /* access modifiers changed from: private */
    public Button mGetSmsButton;
    /* access modifiers changed from: private */
    public TextInputDialog mInputDialog;
    private Button mReportButton;
    /* access modifiers changed from: private */
    public TextView mReturnSmsTextView;
    /* access modifiers changed from: private */
    public ToolbarItemView mSmsDirectionToolbar;
    /* access modifiers changed from: private */
    public ToolbarItemView mSmsNumberToolbar;
    /* access modifiers changed from: private */
    public ToolbarItemView mSmsReceiverNumberToolbar;
    /* access modifiers changed from: private */
    public TcSmsReportService.SmsReportListener mSmsReportListener = new TcSmsReportService.SmsReportListener() {
        public void onSmsReceived() {
            TcSmsReportFragment.this.checkAndApplyStatus();
            Toast.makeText(TcSmsReportFragment.this.mAppContext, R.string.tc_sms_report_get_success, 1).show();
        }

        public void onSmsSentFailure() {
            TcSmsReportFragment.this.checkAndApplyStatus();
            TcSmsReportFragment tcSmsReportFragment = TcSmsReportFragment.this;
            tcSmsReportFragment.postOnUiThread(new b(tcSmsReportFragment) {
                public void runOnUiThread() {
                    new MessageDialog(TcSmsReportFragment.this.mActivity).buildShowDialog(getString(R.string.tc_sms_report_notify_get_failure_title), getString(R.string.tc_sms_report_notify_get_failure_body));
                }
            });
        }

        public void onTimeOut() {
            TcSmsReportFragment.this.checkAndApplyStatus();
            TcSmsReportFragment tcSmsReportFragment = TcSmsReportFragment.this;
            tcSmsReportFragment.postOnUiThread(new b(tcSmsReportFragment) {
                public void runOnUiThread() {
                    new MessageDialog(TcSmsReportFragment.this.mActivity).buildShowDialog(getString(R.string.tc_sms_report_notify_get_timeout_title), getString(R.string.tc_sms_report_notify_get_timeout_body));
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public int mSmsReportSelected = 0;
    /* access modifiers changed from: private */
    public String[] mSmsReportString;
    /* access modifiers changed from: private */
    public ToolbarItemView mSmsReportTypeToolbar;
    /* access modifiers changed from: private */
    public SingleChoiceItemsDialog mSortChoiceDialog;
    private SingleChoiceItemsDialog.SingleChoiceItemsDialogListener mSortedChoiceDialogListener = new SingleChoiceItemsDialog.SingleChoiceItemsDialogListener() {
        public void onSelectItemUpdate(int i, int i2) {
            int unused = TcSmsReportFragment.this.mSmsReportSelected = i;
            TcSmsReportFragment tcSmsReportFragment = TcSmsReportFragment.this;
            int unused2 = tcSmsReportFragment.mUploadType = tcSmsReportFragment.parseReportType(i);
            TcSmsReportFragment.this.mGetSmsButton.setText(TcSmsReportFragment.this.getSmsButtonText());
            TcSmsReportFragment.this.mSmsReportTypeToolbar.setDesc(TcSmsReportFragment.this.mSmsReportString[TcSmsReportFragment.this.mSmsReportSelected]);
            TcSmsReportFragment.this.getPreDirectionAndNumber();
        }
    };
    /* access modifiers changed from: private */
    public TcSmsReportService.TcSmsReportServiceBinder mTcBinder;
    private TextInputDialog.TextInputDialogListener mTextInputDialogListener = new TextInputDialog.TextInputDialogListener() {
        public void onTextSetted(String str, int i) {
            ToolbarItemView toolbarItemView;
            if (i == 1) {
                toolbarItemView = TcSmsReportFragment.this.mSmsNumberToolbar;
            } else if (i != 2) {
                if (i == 3) {
                    toolbarItemView = TcSmsReportFragment.this.mSmsReceiverNumberToolbar;
                }
                TcSmsReportFragment.this.mGetSmsButton.setEnabled(TcSmsReportFragment.this.isSmsAndDirectionOk());
            } else {
                toolbarItemView = TcSmsReportFragment.this.mSmsDirectionToolbar;
            }
            toolbarItemView.setDesc(str);
            TcSmsReportFragment.this.mGetSmsButton.setEnabled(TcSmsReportFragment.this.isSmsAndDirectionOk());
        }
    };
    /* access modifiers changed from: private */
    public TextView mTipsResultTextView;
    /* access modifiers changed from: private */
    public int mUploadType = 1;
    private String mViewFrom;

    /* renamed from: com.miui.networkassistant.ui.fragment.TcSmsReportFragment$9  reason: invalid class name */
    static /* synthetic */ class AnonymousClass9 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus = new int[TcSmsReportService.SmsReportStatus.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|12) */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.networkassistant.service.TcSmsReportService$SmsReportStatus[] r0 = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus = r0
                int[] r0 = $SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.networkassistant.service.TcSmsReportService$SmsReportStatus r1 = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.Init     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.networkassistant.service.TcSmsReportService$SmsReportStatus r1 = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.Receiving     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.networkassistant.service.TcSmsReportService$SmsReportStatus r1 = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.Received     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.networkassistant.service.TcSmsReportService$SmsReportStatus r1 = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.SmsSendFailure     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.networkassistant.service.TcSmsReportService$SmsReportStatus r1 = com.miui.networkassistant.service.TcSmsReportService.SmsReportStatus.Timeout     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.TcSmsReportFragment.AnonymousClass9.<clinit>():void");
        }
    }

    private void bindTcSmsReportService() {
        Context context = this.mAppContext;
        context.bindService(new Intent(context, TcSmsReportService.class), this.mConnection, 1);
    }

    /* access modifiers changed from: private */
    public void checkAndApplyStatus() {
        if (this.mServiceConnected && isTcServiceConnected()) {
            postOnUiThread(new b(this) {
                public void runOnUiThread() {
                    int i;
                    TextView textView;
                    TcSmsReportFragment.this.mGetSmsButton.setEnabled(TcSmsReportFragment.this.isSmsAndDirectionOk());
                    TcSmsReportService.SmsReportStatus status = TcSmsReportFragment.this.mTcBinder.getStatus();
                    int i2 = AnonymousClass9.$SwitchMap$com$miui$networkassistant$service$TcSmsReportService$SmsReportStatus[status.ordinal()];
                    if (i2 != 1) {
                        if (i2 == 2) {
                            TcSmsReportFragment.this.mSmsNumberToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsNum());
                            TcSmsReportFragment.this.mSmsDirectionToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsDirection());
                            TcSmsReportFragment.this.mSmsReceiverNumberToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsReceiveNum());
                            TcSmsReportFragment.this.mGetSmsButton.setEnabled(false);
                            TcSmsReportFragment.this.setToolbarItemEnable(false);
                            TcSmsReportFragment.this.mGetSmsButton.setText(getString(R.string.tc_sms_report_geting_sms));
                            textView = TcSmsReportFragment.this.mTipsResultTextView;
                            i = R.string.tc_sms_report_get_default;
                        } else if (i2 == 3) {
                            TcSmsReportFragment.this.mSmsNumberToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsNum());
                            TcSmsReportFragment.this.mSmsDirectionToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsDirection());
                            TcSmsReportFragment.this.mSmsReceiverNumberToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsReceiveNum());
                            TcSmsReportFragment.this.mReturnSmsTextView.setText(TcSmsReportFragment.this.mTcBinder.getSmsReturned());
                            TcSmsReportFragment.this.mGetSmsButton.setVisibility(8);
                            TcSmsReportFragment.this.mGetAgainAndReportLayout.setVisibility(0);
                            TcSmsReportFragment.this.setToolbarItemEnable(false);
                            TcSmsReportFragment.this.mTipsResultTextView.setVisibility(0);
                            textView = TcSmsReportFragment.this.mTipsResultTextView;
                            i = R.string.tc_sms_report_get_sms_successs;
                        } else if (i2 == 4 || i2 == 5) {
                            TcSmsReportFragment.this.mSmsNumberToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsNum());
                            TcSmsReportFragment.this.mSmsDirectionToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsDirection());
                            TcSmsReportFragment.this.mSmsReceiverNumberToolbar.setDesc(TcSmsReportFragment.this.mTcBinder.getSmsReceiveNum());
                            TcSmsReportFragment.this.mReturnSmsTextView.setText(TcSmsReportFragment.this.mTcBinder.getSmsReturned());
                            TcSmsReportFragment.this.mGetSmsButton.setText(R.string.tc_sms_report_get_sms2);
                            TcSmsReportFragment.this.mGetSmsButton.setVisibility(0);
                            TcSmsReportFragment.this.mGetAgainAndReportLayout.setVisibility(8);
                            TcSmsReportFragment.this.setToolbarItemEnable(true);
                            TcSmsReportFragment.this.mTipsResultTextView.setVisibility(0);
                            TcSmsReportFragment.this.mTipsResultTextView.setText(getString(status == TcSmsReportService.SmsReportStatus.SmsSendFailure ? R.string.tc_sms_report_get_sms_failure : R.string.tc_sms_report_get_sms_timeout));
                            TcSmsReportFragment.this.mAppContext.stopService(new Intent(TcSmsReportFragment.this.mAppContext, TcSmsReportService.class));
                            return;
                        } else {
                            return;
                        }
                        textView.setText(getString(i));
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void getPreDirectionAndNumber() {
        Map map;
        String str;
        if (this.mServiceConnected) {
            String str2 = null;
            try {
                map = this.mTrafficCornBinders[this.mSlotNum].getInstructions(this.mUploadType);
            } catch (RemoteException e) {
                e.printStackTrace();
                map = null;
            }
            if (map != null && map.size() > 0) {
                Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();
                String str3 = (String) entry.getKey();
                int indexOf = str3.indexOf("#");
                if (indexOf > 0) {
                    str3 = str3.substring(0, indexOf);
                }
                String[] split = ((String) entry.getValue()).split("#");
                if (this.mUploadType == 1) {
                    str2 = this.mSimUserInfos[this.mSlotNum].getCustomizedSmsNum();
                    str = this.mSimUserInfos[this.mSlotNum].getCustomizedSmsContent();
                } else {
                    str = null;
                }
                if (TextUtils.isEmpty(str2)) {
                    str2 = str3;
                }
                if (TextUtils.isEmpty(str)) {
                    str = split[0];
                }
                this.mSmsNumberToolbar.setDesc(str2);
                this.mSmsDirectionToolbar.setDesc(str);
                this.mSmsReceiverNumberToolbar.setDesc(split[1]);
            }
        }
    }

    /* access modifiers changed from: private */
    public int getSmsButtonText() {
        int i = this.mUploadType;
        return i != 2 ? i != 4 ? R.string.tc_sms_report_get_sms : R.string.tc_sms_report_get_sms_calltime : R.string.tc_sms_report_get_sms_bill;
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mServiceConnected && isTcServiceConnected()) {
            if (this.mSlotNum != this.mTcBinder.getCurrentSlotNum()) {
                this.mTcBinder.reset();
            }
            this.mUploadType = this.mActivity.getIntent().getIntExtra(ITrafficCorrection.KEY_CORRECTION_TYPE, 1);
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(ITrafficCorrection.KEY_CORRECTION_TYPE)) {
                this.mUploadType = arguments.getInt(ITrafficCorrection.KEY_CORRECTION_TYPE, 1);
            }
            if (this.mTcBinder.getReportSmsType() != -1) {
                this.mUploadType = this.mTcBinder.getReportSmsType();
            }
            this.mSmsReportSelected = parseReportSelected(this.mUploadType);
            this.mSmsReportTypeToolbar.setDesc(this.mSmsReportString[this.mSmsReportSelected]);
            setToolbarItemEnable(true);
            this.mGetSmsButton.setText(getSmsButtonText());
            this.mGetSmsButton.setVisibility(0);
            this.mGetAgainAndReportLayout.setVisibility(8);
            this.mGetSmsButton.setEnabled(false);
            this.mReturnSmsTextView.setText(Html.fromHtml(getString(R.string.tc_sms_report_receive_content_null)));
            getPreDirectionAndNumber();
            checkAndApplyStatus();
        }
    }

    private void initToolbarListItem() {
        this.mSmsReportTypeToolbar = (ToolbarItemView) findViewById(R.id.layout_upload_type);
        this.mSmsDirectionToolbar = (ToolbarItemView) findViewById(R.id.layout_sms_direction);
        this.mSmsNumberToolbar = (ToolbarItemView) findViewById(R.id.layout_sms_number);
        this.mSmsReceiverNumberToolbar = (ToolbarItemView) findViewById(R.id.layout_sms_receive_num);
        this.mSmsReportTypeToolbar.setName((int) R.string.tc_sms_report_type);
        this.mSmsDirectionToolbar.setName((int) R.string.tc_sms_report_direction);
        this.mSmsNumberToolbar.setName((int) R.string.tc_sms_report_send_num);
        this.mSmsReceiverNumberToolbar.setName((int) R.string.tc_sms_report_receive_num);
        this.mSmsReportTypeToolbar.setOnClickListener(this.mClickListener);
        this.mSmsDirectionToolbar.setOnClickListener(this.mClickListener);
        this.mSmsNumberToolbar.setOnClickListener(this.mClickListener);
        this.mSmsReceiverNumberToolbar.setOnClickListener(this.mClickListener);
    }

    /* access modifiers changed from: private */
    public boolean isSmsAndDirectionOk() {
        return (this.mSmsNumberToolbar.getDesc() == null || this.mSmsDirectionToolbar.getDesc() == null || this.mSmsReceiverNumberToolbar.getDesc() == null) ? false : true;
    }

    /* access modifiers changed from: private */
    public boolean isTcServiceConnected() {
        return this.mTcBinder != null;
    }

    private int parseReportSelected(int i) {
        if (i != 2) {
            return i != 4 ? 0 : 2;
        }
        return 1;
    }

    /* access modifiers changed from: private */
    public int parseReportType(int i) {
        if (i != 1) {
            return i != 2 ? 1 : 4;
        }
        return 2;
    }

    /* access modifiers changed from: private */
    public void reportSms() {
        if (isTcServiceConnected()) {
            this.mTcBinder.report(this.mUploadType);
            finish();
        }
    }

    /* access modifiers changed from: private */
    public void setToolbarItemEnable(boolean z) {
        this.mSmsReportTypeToolbar.setItemEnabled(z);
        this.mSmsNumberToolbar.setItemEnabled(z);
        this.mSmsDirectionToolbar.setItemEnabled(z);
        this.mSmsReceiverNumberToolbar.setItemEnabled(z);
    }

    /* access modifiers changed from: private */
    public void tcSmsReportDeclare() {
        new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
            public void onOptionUpdated(boolean z) {
                if (z) {
                    TcSmsReportFragment.this.reportSms();
                    AnalyticsHelper.trackSmsReport();
                    AnalyticsHelper.trackTcSmsReport(TcSmsReportFragment.this.mUploadType);
                }
            }
        }).buildShowDialog(this.mAppContext.getResources().getString(R.string.privacy_declare_dialog_title), this.mAppContext.getResources().getString(R.string.privacy_prompt_tc_sms_report_message), (String) null, this.mAppContext.getResources().getString(R.string.privacy_prompt_upload));
    }

    private void unBindTcSmsReportService() {
        this.mAppContext.unbindService(this.mConnection);
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initToolbarListItem();
        this.mGetSmsButton = (Button) findViewById(R.id.button_get_sms);
        this.mGetAgainAndReportLayout = (LinearLayout) findViewById(R.id.layout_buttons_again_and_report);
        this.mGetSmsAgainButton = (Button) findViewById(R.id.button_get_sms_again);
        this.mReportButton = (Button) findViewById(R.id.button_report_sms);
        this.mReturnSmsTextView = (TextView) findViewById(R.id.textview_sms_receive_content);
        this.mTipsResultTextView = (TextView) findViewById(R.id.tips_textview);
        this.mGetSmsButton.setOnClickListener(this.mClickListener);
        this.mGetSmsAgainButton.setOnClickListener(this.mClickListener);
        this.mReportButton.setOnClickListener(this.mClickListener);
        this.mSmsReportString = getResources().getStringArray(R.array.sms_report_type);
        this.mSortChoiceDialog = new SingleChoiceItemsDialog(this.mActivity, this.mSortedChoiceDialogListener);
        this.mInputDialog = new TextInputDialog(this.mActivity, this.mTextInputDialogListener);
        Bundle bundleExtra = this.mActivity.getIntent().getBundleExtra(g.FRAGMENT_ARGS);
        if (bundleExtra != null) {
            this.mViewFrom = bundleExtra.getString("view_from");
        }
        if (TextUtils.isEmpty(this.mViewFrom)) {
            this.mViewFrom = "other";
        }
        AnalyticsHelper.trackTcSmsShow(this.mViewFrom);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bindTcSmsReportService();
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.fragment_tc_sms_report;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(16, 16);
        Button button = new Button(this.mActivity);
        button.setContentDescription(this.mAppContext.getString(R.string.tips_dialog_title));
        button.setId(16908313);
        button.setBackgroundResource(miui.R.drawable.icon_info_light);
        button.setOnClickListener(this.mClickListener);
        if (!(actionBar instanceof miui.app.ActionBar)) {
            return 0;
        }
        ((miui.app.ActionBar) actionBar).setEndView(button);
        return 0;
    }

    public void onDetach() {
        super.onDetach();
        TcSmsReportService.TcSmsReportServiceBinder tcSmsReportServiceBinder = this.mTcBinder;
        if (tcSmsReportServiceBinder != null) {
            tcSmsReportServiceBinder.unRegisterSmsReportListener(this.mSmsReportListener);
        }
        unBindTcSmsReportService();
    }

    public void onPause() {
        super.onPause();
        TcSmsReportService.TcSmsReportServiceBinder tcSmsReportServiceBinder = this.mTcBinder;
        if (tcSmsReportServiceBinder != null) {
            tcSmsReportServiceBinder.unRegisterSmsReportListener(this.mSmsReportListener);
        }
    }

    public void onResume() {
        super.onResume();
        TcSmsReportService.TcSmsReportServiceBinder tcSmsReportServiceBinder = this.mTcBinder;
        if (tcSmsReportServiceBinder != null) {
            tcSmsReportServiceBinder.registerSmsReportListener(this.mSmsReportListener);
        }
        NotificationUtil.cancelTcSmsReceivedNotify(this.mAppContext);
        NotificationUtil.cancelTcSmsTimeOutOrFailureNotify(this.mAppContext);
        NotificationUtil.cancelDataUsageCorrectionTimeOutOrFailureNotify(this.mAppContext);
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.tc_sms_report_title;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        postOnUiThread(new b(this) {
            public void runOnUiThread() {
                TcSmsReportFragment.this.initData();
            }
        });
    }
}
