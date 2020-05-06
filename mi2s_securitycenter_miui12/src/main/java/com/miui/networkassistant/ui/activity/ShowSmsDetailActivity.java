package com.miui.networkassistant.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.c.a.b;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import com.miui.networkassistant.traffic.purchase.CooperationManager;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.fragment.PackageSettingFragment;
import com.miui.networkassistant.ui.fragment.TemplateSettingFragment;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.TextPrepareUtil;
import com.miui.securitycenter.R;
import java.util.Map;
import miui.app.ActionBar;
import org.json.JSONArray;

public class ShowSmsDetailActivity extends BaseStatsActivity implements View.OnClickListener {
    private static final String CHARGE_URL = "https://app.mipay.com?id=mipay.phoneRecharge&miref=42&slot_id=";
    public static final String EXTRA_VIEW_FROM = "view_from";
    private static final String TAG = "ShowSmsDetailActivity";
    public static final String TYPE = "type";
    public static final int TYPE_BILL = 1;
    public static final int TYPE_TRAFFIC = 0;
    private boolean isDataSaved;
    private TextView mAdjustCMDText;
    private TextView mBackTextView;
    private MenuItem mChargeMenuItem;
    private View mCorrectFailLayout;
    private View mCorrectSuccessLayout;
    private TextView mFailReason;
    private TextView mLeftText;
    private TextView mLeftUnit;
    private MenuItem mReportMenuItem;
    /* access modifiers changed from: private */
    public boolean mServiceConnected;
    private MenuItem mSettingMenuItem;
    private SimUserInfo mSimUserInfo;
    private String mSmsDetail;
    private TextView mSmsDetailView;
    private int mSmsResult;
    /* access modifiers changed from: private */
    public int mSmsShowType;
    private TextView mSubTitle;
    private TextView mSuccessTitle;
    /* access modifiers changed from: private */
    public ITrafficManageBinder mTrafficManageBinder;
    private ServiceConnection mTrafficManageConnection = new ServiceConnection() {
        /* JADX WARNING: type inference failed for: r2v3, types: [com.miui.networkassistant.ui.activity.ShowSmsDetailActivity, android.app.Activity] */
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            boolean unused = ShowSmsDetailActivity.this.mServiceConnected = true;
            ITrafficManageBinder unused2 = ShowSmsDetailActivity.this.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            ? r2 = ShowSmsDetailActivity.this;
            r2.postOnUiThread(new b(r2) {
                public void runOnUiThread() {
                    ShowSmsDetailActivity.this.setSubTitle();
                }
            });
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ITrafficManageBinder unused = ShowSmsDetailActivity.this.mTrafficManageBinder = null;
            boolean unused2 = ShowSmsDetailActivity.this.mServiceConnected = false;
        }
    };
    private TextView mUsageText;
    private TextView mUsageUnit;
    private String mViewFrom;

    private void bindTrafficManageService() {
        TmBinderCacher.getInstance().bindTmService(this.mTrafficManageConnection);
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, android.view.View$OnClickListener, com.miui.networkassistant.ui.activity.ShowSmsDetailActivity, miui.app.Activity, b.b.c.c.b.a] */
    private void initView() {
        int i;
        this.mViewFrom = getIntent().getStringExtra("view_from");
        if (TextUtils.isEmpty(this.mViewFrom)) {
            this.mViewFrom = "other";
        }
        AnalyticsHelper.trackTcSmsDetailShow(this.mViewFrom);
        this.mSimUserInfo = SimUserInfo.getInstance(this.mAppContext, Sim.getCurrentOptSlotNum());
        this.mBackTextView = (TextView) findViewById(R.id.back);
        this.mBackTextView.setOnClickListener(this);
        this.mSmsDetailView = (TextView) findViewById(R.id.sms_detail);
        this.mSmsShowType = getIntent().getIntExtra("type", 0);
        this.mCorrectSuccessLayout = findViewById(R.id.layout_correct_success);
        this.mCorrectFailLayout = findViewById(R.id.layout_correct_fail);
        this.mSuccessTitle = (TextView) findViewById(R.id.text_correct_success_title);
        this.mUsageText = (TextView) findViewById(R.id.text_usage);
        this.mUsageUnit = (TextView) findViewById(R.id.text_usage_unit);
        this.mLeftText = (TextView) findViewById(R.id.text_left);
        this.mLeftUnit = (TextView) findViewById(R.id.text_left_unit);
        this.mFailReason = (TextView) findViewById(R.id.text_correct_fail_subtitle);
        this.mAdjustCMDText = (TextView) findViewById(R.id.text_adjust_sms_cmd);
        this.mAdjustCMDText.setOnClickListener(this);
        resetTitle();
        this.mSmsDetail = this.mSmsShowType == 1 ? this.mSimUserInfo.getBillSmsDetail() : this.mSimUserInfo.getTrafficSmsDetail();
        this.mSmsResult = this.mSmsShowType == 1 ? this.mSimUserInfo.getBillTcResultCode() : this.mSimUserInfo.getTrafficTcResultCode();
        this.mSmsDetailView.setText(this.mSmsDetail);
        Linkify.addLinks(this.mSmsDetailView, 15);
        this.mSmsDetailView.setTextIsSelectable(true);
        if (this.mSmsShowType == 0 && this.mSmsResult == 0) {
            this.mCorrectSuccessLayout.setVisibility(0);
            this.mCorrectFailLayout.setVisibility(8);
            this.mAdjustCMDText.setVisibility(8);
            this.mSuccessTitle.setText(getString(R.string.sms_detail_correct_success_text, new Object[]{TextPrepareUtil.getPreAdjustTimeTips(this.mAppContext, this.mSimUserInfo.getDataUsageCorrectedTime(), System.currentTimeMillis())}));
            String[] formatBytesSplited = FormatBytesUtil.formatBytesSplited(this, this.mSimUserInfo.getLastTcUsed());
            this.mUsageText.setText(formatBytesSplited[0]);
            this.mUsageUnit.setText(formatBytesSplited[1]);
            String[] formatBytesSplited2 = FormatBytesUtil.formatBytesSplited(this, this.mSimUserInfo.getLastTcRemain());
            this.mLeftText.setText(formatBytesSplited2[0]);
            this.mLeftUnit.setText(formatBytesSplited2[1]);
        } else if (this.mSmsResult != 0) {
            this.mCorrectSuccessLayout.setVisibility(8);
            this.mCorrectFailLayout.setVisibility(0);
            if (this.mSmsShowType == 0) {
                this.mAdjustCMDText.setVisibility(0);
            } else {
                this.mAdjustCMDText.setVisibility(8);
            }
            switch (this.mSmsResult) {
                case 1:
                    i = R.string.tc_send_sms_error;
                    break;
                case 2:
                    i = R.string.tc_cmd_invalid;
                    break;
                case 3:
                    i = R.string.tc_receive_timeout;
                    break;
                case 4:
                    i = R.string.tc_parse_error;
                    break;
                case 5:
                    i = R.string.tc_get_cmd_error;
                    break;
                case 6:
                    i = R.string.tc_web_correct_error;
                    break;
                default:
                    i = -1;
                    break;
            }
            if (i > 0) {
                this.mFailReason.setText(i);
            }
        } else {
            this.mCorrectSuccessLayout.setVisibility(8);
            this.mCorrectFailLayout.setVisibility(8);
            this.mAdjustCMDText.setVisibility(8);
        }
    }

    private void resetTitle() {
        String string = getString(this.mSmsShowType == 1 ? R.string.sms_detail_fragment_title_bill : R.string.sms_detail_fragment_title_traffic);
        if (SimCardHelper.getInstance(this.mAppContext).isDualSimInserted()) {
            string = TextPrepareUtil.getDualCardTitle(this.mAppContext, string, Sim.getCurrentOptSlotNum());
        }
        this.mBackTextView.setText(string);
    }

    /* access modifiers changed from: private */
    public synchronized void saveUploadCorrectionResult() {
        Map map;
        String str;
        Object obj;
        String str2;
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(this.mSmsDetail);
        String billTcResult = this.mSmsShowType == 1 ? this.mSimUserInfo.getBillTcResult() : this.mSimUserInfo.getTrafficTcResult();
        int i = this.mSmsShowType == 1 ? 2 : 1;
        if (this.mServiceConnected) {
            String str3 = null;
            try {
                map = this.mTrafficManageBinder.getTrafficCornBinder(Sim.getCurrentOptSlotNum()).getInstructions(i);
            } catch (RemoteException e) {
                e.printStackTrace();
                map = null;
            }
            if (map == null || map.size() <= 0) {
                obj = null;
                str = null;
            } else {
                Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();
                String str4 = (String) entry.getKey();
                int indexOf = str4.indexOf("#");
                if (indexOf > 0) {
                    str4 = str4.substring(0, indexOf);
                }
                String[] split = ((String) entry.getValue()).split("#");
                if (i == 1) {
                    str3 = this.mSimUserInfo.getCustomizedSmsNum();
                    str2 = this.mSimUserInfo.getCustomizedSmsContent();
                } else {
                    str2 = null;
                }
                if (TextUtils.isEmpty(str3)) {
                    str3 = str4;
                }
                str = TextUtils.isEmpty(str2) ? split[0] : str2;
                obj = split[1];
            }
            JSONArray jSONArray2 = new JSONArray();
            jSONArray2.put(str3);
            jSONArray2.put(str);
            jSONArray2.put(obj);
            jSONArray2.put(jSONArray.toString());
            jSONArray2.put(this.mSimUserInfo.getProvince());
            jSONArray2.put(this.mSimUserInfo.getCity());
            jSONArray2.put(this.mSimUserInfo.getOperator());
            jSONArray2.put(this.mSimUserInfo.getBrand());
            jSONArray2.put(i);
            jSONArray2.put(billTcResult);
            this.mSimUserInfo.setTcSmsReportCache(jSONArray2.toString());
            this.isDataSaved = true;
        }
    }

    /* access modifiers changed from: private */
    public void setSubTitle() {
        this.mSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        long dataUsageCorrectedTime = this.mSimUserInfo.getDataUsageCorrectedTime();
        StringBuilder sb = new StringBuilder();
        if (!(this.mSmsShowType == 0 && this.mSmsResult == 0) && dataUsageCorrectedTime > 0) {
            sb.append(TextPrepareUtil.getPreAdjustTimeTips(this.mAppContext, dataUsageCorrectedTime, System.currentTimeMillis()));
        }
        this.mSubTitle.setText(sb.toString());
    }

    private void smsReportDeclare() {
        new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
            public void onOptionUpdated(boolean z) {
                if (z) {
                    Toast.makeText(ShowSmsDetailActivity.this.getApplicationContext(), R.string.tc_sms_report_upload_when_net, 1).show();
                    ShowSmsDetailActivity.this.saveUploadCorrectionResult();
                    AnalyticsHelper.trackTcSmsDetailReport(ShowSmsDetailActivity.this.mSmsShowType);
                    ShowSmsDetailActivity.this.finish();
                }
            }
        }).buildShowDialog(this.mAppContext.getResources().getString(R.string.menu_report), this.mAppContext.getResources().getString(R.string.privacy_dialog_message), (String) null, this.mAppContext.getResources().getString(R.string.privacy_prompt_upload));
    }

    private void startChargeActivity() {
        if (this.mSmsShowType == 1) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(CHARGE_URL + this.mSimUserInfo.getSlotNum()));
                startActivity(intent);
            } catch (Exception e) {
                Log.i(TAG, "go to phoneRecharge failed", e);
            }
        } else {
            CooperationManager.navigationToTrafficPurchasePage(this.mActivity, this.mSimUserInfo, "100001");
        }
    }

    public static void startSmsDetailActivity(Activity activity, int i, String str) {
        Intent intent = new Intent(activity, ShowSmsDetailActivity.class);
        intent.putExtra("type", i);
        intent.putExtra("view_from", str);
        activity.startActivity(intent);
    }

    private void unbindTrafficManageService() {
        TmBinderCacher.getInstance().unbindTmService(this.mTrafficManageConnection);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.networkassistant.ui.activity.ShowSmsDetailActivity, miui.app.Activity, android.app.Activity] */
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.text_adjust_sms_cmd) {
            Bundle bundle = new Bundle();
            bundle.putInt(Sim.SIM_SLOT_NUM_TAG, this.mSimUserInfo.getSlotNum());
            g.startWithFragment(this, TemplateSettingFragment.class, bundle);
        }
    }

    public void onCreate(Bundle bundle) {
        setTranslucentStatus(2);
        super.onCreate(bundle);
        initView();
        bindTrafficManageService();
    }

    public int onCreateContentView() {
        return R.layout.sms_detail_fragment;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sms_detail_menu, menu);
        this.mReportMenuItem = menu.findItem(R.id.report_menu);
        this.mChargeMenuItem = menu.findItem(R.id.charge_menu);
        this.mSettingMenuItem = menu.findItem(R.id.setting_menu);
        if (this.mSmsShowType == 1) {
            this.mSettingMenuItem.setVisible(false);
        }
        this.mChargeMenuItem.setTitle(this.mSmsShowType == 1 ? R.string.menu_charge_bill : R.string.menu_charge_traffic);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCustomizeActionBar(ActionBar actionBar) {
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        ShowSmsDetailActivity.super.onDestroy();
        unbindTrafficManageService();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem == this.mReportMenuItem) {
            smsReportDeclare();
            return true;
        } else if (menuItem == this.mChargeMenuItem) {
            startChargeActivity();
            return true;
        } else if (menuItem != this.mSettingMenuItem) {
            return true;
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(Sim.SIM_SLOT_NUM_TAG, this.mSimUserInfo.getSlotNum());
            g.startWithFragment(this.mActivity, PackageSettingFragment.class, bundle);
            return true;
        }
    }
}
