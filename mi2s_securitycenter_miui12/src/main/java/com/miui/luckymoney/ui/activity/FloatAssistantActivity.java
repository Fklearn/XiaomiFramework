package com.miui.luckymoney.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.utils.FloatWindowHelper;
import com.miui.luckymoney.utils.PackageUtil;
import com.miui.luckymoney.webapi.FloatResourceHelper;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;

public class FloatAssistantActivity extends BaseActivity {
    private static final int MSG_CHANGE_BUSINESS_PIC = 1;
    private DefaultConfig alipayConfig = new DefaultConfig();
    private ImageView imgGiveMoney;
    private View layoutGiveMoney;
    private RelativeLayout mActivityContainer;
    /* access modifiers changed from: private */
    public Context mAppContext;
    private int mChangeIndex = 0;
    /* access modifiers changed from: private */
    public Config mConfig = new Config();
    private RelativeLayout mDialogContainer;
    private TextView mGiveLuckyMoneyView;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                FloatAssistantActivity.this.postBgChangedDelay();
            }
        }
    };
    private ImageView mImgAlipay;
    private ImageView mImgWechat;
    private TextView mLuckyNewsTextView;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            String str;
            switch (view.getId()) {
                case R.id.btn_setting /*2131296554*/:
                    Intent intent = new Intent(FloatAssistantActivity.this.mAppContext, LuckySettingActivity.class);
                    intent.addFlags(268435456);
                    g.b((Context) FloatAssistantActivity.this, intent, B.b());
                    str = "settings";
                    break;
                case R.id.dialog_container /*2131296709*/:
                    return;
                case R.id.float_activity_container /*2131296823*/:
                    MiStatUtil.recordFloatWindowFuncClick(MiStatUtil.CLOSE);
                    break;
                case R.id.imgAlipay /*2131297023*/:
                case R.id.tv_shoop_again /*2131297924*/:
                    FloatAssistantActivity.this.startShoopAgainActivity();
                    MiStatUtil.recordFloatWindowFuncClick("alipay_shoop");
                    FloatAssistantActivity.this.finishActivityDelayed();
                    return;
                case R.id.imgWechat /*2131297029*/:
                case R.id.tv_shake_again /*2131297923*/:
                    FloatAssistantActivity.this.startCmdLuckyActivity();
                    str = "wechat_shake";
                    break;
                case R.id.layout_give_money /*2131297184*/:
                    if ("intent".equals(FloatAssistantActivity.this.mConfig.type)) {
                        FloatAssistantActivity floatAssistantActivity = FloatAssistantActivity.this;
                        floatAssistantActivity.startActivitybyComponent(floatAssistantActivity.mConfig.componentNames, FloatAssistantActivity.this.mConfig.errorText);
                    }
                    if (MijiaAlertModel.KEY_URL.equals(FloatAssistantActivity.this.mConfig.type)) {
                        Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse(FloatAssistantActivity.this.mConfig.url));
                        if (FloatAssistantActivity.this.mConfig.packageName != null) {
                            intent2.setPackage(FloatAssistantActivity.this.mConfig.packageName);
                        }
                        intent2.addFlags(268435456);
                        g.b((Context) FloatAssistantActivity.this, intent2, B.b());
                    }
                    MiStatUtil.recordFloatWindowHot(FloatAssistantActivity.this.mConfig.startTime + "", true);
                    break;
                case R.id.tv_news_lucky /*2131297917*/:
                    Intent intent3 = new Intent(FloatAssistantActivity.this.mAppContext, LuckyAlarmActivity.class);
                    intent3.addFlags(268435456);
                    g.b((Context) FloatAssistantActivity.this, intent3, B.b());
                    str = "alarm";
                    break;
                case R.id.tv_random_expression /*2131297918*/:
                    Intent intent4 = new Intent(FloatAssistantActivity.this.mAppContext, FastOpenListActivity.class);
                    intent4.addFlags(268435456);
                    g.b((Context) FloatAssistantActivity.this, intent4, B.b());
                    str = "fast";
                    break;
                default:
                    MiStatUtil.recordFloatWindowFuncClick(MiStatUtil.CLOSE);
                    return;
            }
            MiStatUtil.recordFloatWindowFuncClick(str);
            FloatAssistantActivity.this.finishActivity();
        }
    };
    private TextView mRandomExpressionTextView;
    private Button mSettingButton;
    private TextView mShakeAgainTextView;
    private TextView mShoopAgainTextView;
    private DefaultConfig wechatConfig = new DefaultConfig();

    public static class Config {
        public ArrayList<ComponentName> componentNames = new ArrayList<>();
        public long endTime;
        public String errorText;
        public long flashEndTime;
        public long flashStartTime;
        public Bitmap icon1;
        public Bitmap icon2;
        public boolean isFlashing;
        public boolean isFour = true;
        public String packageName;
        public long startTime;
        public String text;
        public String type;
        public String url;
    }

    public static class DefaultConfig {
        public ArrayList<ComponentName> componentNames = new ArrayList<>();
        public Bitmap icon;
        public String text;
    }

    /* access modifiers changed from: private */
    public void finishActivity() {
        finish();
    }

    /* access modifiers changed from: private */
    public void finishActivityDelayed() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                FloatAssistantActivity.this.finishActivity();
            }
        }, 500);
    }

    private void initView() {
        this.mLuckyNewsTextView = (TextView) findViewById(R.id.tv_news_lucky);
        this.mRandomExpressionTextView = (TextView) findViewById(R.id.tv_random_expression);
        this.mShoopAgainTextView = (TextView) findViewById(R.id.tv_shoop_again);
        this.mShakeAgainTextView = (TextView) findViewById(R.id.tv_shake_again);
        this.mActivityContainer = (RelativeLayout) findViewById(R.id.float_activity_container);
        this.mDialogContainer = (RelativeLayout) findViewById(R.id.dialog_container);
        this.mSettingButton = (Button) findViewById(R.id.btn_setting);
        this.mGiveLuckyMoneyView = (TextView) findViewById(R.id.tv_give_lucky_money);
        this.imgGiveMoney = (ImageView) findViewById(R.id.img_give_lucky_money);
        this.layoutGiveMoney = findViewById(R.id.layout_give_money);
        this.mImgWechat = (ImageView) findViewById(R.id.imgWechat);
        this.mImgAlipay = (ImageView) findViewById(R.id.imgAlipay);
        String str = this.wechatConfig.text;
        if (str != null) {
            this.mShakeAgainTextView.setText(str);
        }
        String str2 = this.alipayConfig.text;
        if (str2 != null) {
            this.mShoopAgainTextView.setText(str2);
        }
        Bitmap bitmap = this.wechatConfig.icon;
        if (bitmap != null) {
            this.mImgWechat.setImageBitmap(bitmap);
        }
        Bitmap bitmap2 = this.alipayConfig.icon;
        if (bitmap2 != null) {
            this.mImgAlipay.setImageBitmap(bitmap2);
        }
        this.mLuckyNewsTextView.setOnClickListener(this.mOnClickListener);
        this.mRandomExpressionTextView.setOnClickListener(this.mOnClickListener);
        this.mShoopAgainTextView.setOnClickListener(this.mOnClickListener);
        this.mShakeAgainTextView.setOnClickListener(this.mOnClickListener);
        this.mActivityContainer.setOnClickListener(this.mOnClickListener);
        this.mDialogContainer.setOnClickListener(this.mOnClickListener);
        this.mSettingButton.setOnClickListener(this.mOnClickListener);
        this.mImgWechat.setOnClickListener(this.mOnClickListener);
        this.mImgAlipay.setOnClickListener(this.mOnClickListener);
        if (!this.mConfig.isFour) {
            this.layoutGiveMoney.setOnClickListener(this.mOnClickListener);
            this.mGiveLuckyMoneyView.setText(this.mConfig.text);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), this.mConfig.icon1);
            BitmapDrawable bitmapDrawable2 = new BitmapDrawable(getResources(), this.mConfig.icon2);
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{16842919}, bitmapDrawable2);
            stateListDrawable.addState(new int[0], bitmapDrawable);
            this.imgGiveMoney.setImageDrawable(stateListDrawable);
            MiStatUtil.recordFloatWindowHot(this.mConfig.startTime + "", false);
        }
    }

    /* access modifiers changed from: private */
    public void postBgChangedDelay() {
        long currentTimeMillis = System.currentTimeMillis();
        Config config = this.mConfig;
        if (!config.isFour && config.isFlashing && currentTimeMillis >= config.flashStartTime && currentTimeMillis <= config.flashEndTime) {
            if (this.mHandler.hasMessages(1)) {
                this.mHandler.removeMessages(1);
            }
            this.mChangeIndex++;
            this.mChangeIndex = this.mChangeIndex % 2 == 0 ? 0 : 1;
            this.imgGiveMoney.setImageDrawable(this.mChangeIndex % 2 == 0 ? new BitmapDrawable(getResources(), this.mConfig.icon1) : new BitmapDrawable(getResources(), this.mConfig.icon2));
            this.mHandler.sendEmptyMessageDelayed(1, 800);
        }
    }

    private void startStickerActivity() {
        Intent stickerIntent = PackageUtil.getStickerIntent((String) null, true);
        if (PackageUtil.isIntentExist(this.mAppContext, stickerIntent, "")) {
            g.b((Context) this, stickerIntent, B.b());
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        getWindow().addFlags(67108864);
        super.onCreate(bundle);
        FloatResourceHelper.initConfig(this, this.mConfig, this.wechatConfig, this.alipayConfig);
        setContentView(this.mConfig.isFour ? R.layout.activity_float_assistant : R.layout.activity_float_assistant_5);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        this.mAppContext = getApplicationContext();
        initView();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        FloatWindowHelper.showFloatWindow(this.mAppContext);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        FloatWindowHelper.removeFloatWindow(this.mAppContext);
        postBgChangedDelay();
    }

    public void startActivitybyComponent(List<ComponentName> list, String str) {
        for (ComponentName next : list) {
            if (next != null && PackageUtil.isInstalledPackage(getApplicationContext(), next.getPackageName())) {
                Intent intent = new Intent();
                intent.setComponent(next);
                intent.addCategory(Constants.System.CATEGORY_DEFALUT);
                intent.setFlags(268435456);
                if (PackageUtil.isIntentExist(this.mAppContext, intent, next.getPackageName())) {
                    g.b((Context) this, intent, B.b());
                    return;
                }
            }
        }
        Toast.makeText(this.mAppContext, str, 1).show();
    }

    public void startCmdLuckyActivity() {
        startActivitybyComponent(this.wechatConfig.componentNames, this.mAppContext.getString(R.string.weChat_not_found));
    }

    public void startShoopAgainActivity() {
        startActivitybyComponent(this.alipayConfig.componentNames, this.mAppContext.getString(R.string.zhifubao_not_found));
    }
}
