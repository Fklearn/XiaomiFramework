package com.miui.luckymoney.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.utils.DateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LuckyMoneyAccessibilityService extends AccessibilityService {
    private static final int DELAY_TIME = 500;
    private static final int RETRY_TIMES = 3;
    /* access modifiers changed from: private */
    public static final String TAG = "LuckyMoneyAccessibilityService";
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public Handler mMainHandler = null;
    private BaseLuckyMoneyProcessor mmLuckyMoneyProcessor;
    private BaseLuckyMoneyProcessor qqLuckyMoneyProcessor;

    private abstract class BaseLuckyMoneyProcessor {
        int mCountMoney;
        int mFlagCount;
        int mSumMoney;

        private BaseLuckyMoneyProcessor() {
            this.mFlagCount = 0;
            this.mSumMoney = 0;
            this.mCountMoney = 0;
        }

        private boolean isNumeric(String str) {
            Pattern compile = Pattern.compile("[0-9]*");
            if (str.indexOf(".") > 0) {
                if (str.indexOf(".") != str.lastIndexOf(".") || str.split("\\.").length != 2) {
                    return false;
                }
                str = str.replace(".", "");
            }
            return compile.matcher(str).matches();
        }

        private void recursiveFindText(AccessibilityNodeInfo accessibilityNodeInfo, int i, ArrayList<String> arrayList, String str) {
            if (i > 0 && accessibilityNodeInfo != null) {
                for (int i2 = 0; i2 < i; i2++) {
                    AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i2);
                    if (child != null) {
                        CharSequence text = child.getText();
                        if (text != null) {
                            arrayList.add(text.toString());
                            this.mSumMoney++;
                            if (Boolean.valueOf(isNumeric(text.toString())).booleanValue()) {
                                this.mCountMoney++;
                                if (("已存入余额".equals(str) && this.mCountMoney == 1) || "元".equals(str)) {
                                    this.mFlagCount = this.mSumMoney;
                                }
                            }
                        }
                        recursiveFindText(child, child.getChildCount(), arrayList, str);
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public String findMoneyByNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo, String str) {
            int i;
            ArrayList arrayList = new ArrayList();
            recursiveFindText(accessibilityNodeInfo, accessibilityNodeInfo.getChildCount(), arrayList, str);
            if (arrayList.size() == 0 || (i = this.mFlagCount) <= 0 || i > arrayList.size()) {
                return null;
            }
            return (String) arrayList.get(this.mFlagCount - 1);
        }

        /* access modifiers changed from: protected */
        public void process(AccessibilityEvent accessibilityEvent) {
            this.mCountMoney = 0;
            this.mSumMoney = 0;
            this.mFlagCount = 0;
        }
    }

    private class MMLuckyMoneyProcessor extends BaseLuckyMoneyProcessor {
        /* access modifiers changed from: private */
        public boolean isMMMoneyFirstOpen;

        private MMLuckyMoneyProcessor() {
            super();
            this.isMMMoneyFirstOpen = false;
        }

        /* access modifiers changed from: protected */
        public void process(AccessibilityEvent accessibilityEvent) {
            super.process(accessibilityEvent);
            if (accessibilityEvent.getClassName() != null) {
                String charSequence = accessibilityEvent.getClassName().toString();
                if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(charSequence) && this.isMMMoneyFirstOpen) {
                    this.isMMMoneyFirstOpen = false;
                    LuckyMoneyAccessibilityService.this.mMainHandler.post(new Runnable() {
                        private int times = 0;

                        public void run() {
                            Log.d(LuckyMoneyAccessibilityService.TAG, "process mm lucky monkey " + this.times);
                            AccessibilityNodeInfo rootInActiveWindow = LuckyMoneyAccessibilityService.this.getRootInActiveWindow();
                            if (rootInActiveWindow != null) {
                                try {
                                    String findMoneyByNodeInfo = MMLuckyMoneyProcessor.this.findMoneyByNodeInfo(rootInActiveWindow, "元");
                                    if (findMoneyByNodeInfo != null) {
                                        Log.i(LuckyMoneyAccessibilityService.TAG, "found money " + findMoneyByNodeInfo);
                                        boolean unused = MMLuckyMoneyProcessor.this.isMMMoneyFirstOpen = false;
                                        long receiveTotalLuckyMoney = LuckyMoneyAccessibilityService.this.mCommonConfig.getReceiveTotalLuckyMoney();
                                        long round = (long) Math.round(Float.parseFloat(findMoneyByNodeInfo) * 100.0f);
                                        LuckyMoneyAccessibilityService.this.mCommonConfig.saveReceiveTotalLuckyMoney(receiveTotalLuckyMoney + round);
                                        LuckyMoneyAccessibilityService.this.mCommonConfig.setMMMoney(LuckyMoneyAccessibilityService.this.mCommonConfig.getMMMoney() + round);
                                        LuckyMoneyAccessibilityService.this.mCommonConfig.setTodayMMMoney(LuckyMoneyAccessibilityService.this.mCommonConfig.getTodayMMMoney() + round);
                                    } else if (this.times < 3) {
                                        this.times++;
                                        LuckyMoneyAccessibilityService.this.mMainHandler.postDelayed(this, 500);
                                    }
                                } catch (Exception e) {
                                    Log.i(LuckyMoneyAccessibilityService.TAG, "format text exception", e);
                                }
                            } else {
                                int i = this.times;
                                if (i < 3) {
                                    this.times = i + 1;
                                    LuckyMoneyAccessibilityService.this.mMainHandler.postDelayed(this, 500);
                                }
                            }
                        }
                    });
                } else if (charSequence.contains("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI")) {
                    Log.d(LuckyMoneyAccessibilityService.TAG, "staying at mm NotHook ui");
                    if (accessibilityEvent.getSource() != null) {
                        this.isMMMoneyFirstOpen = true;
                    }
                } else {
                    this.isMMMoneyFirstOpen = false;
                }
            }
        }
    }

    private class QQLuckyMoneyProcessor extends BaseLuckyMoneyProcessor {
        private QQLuckyMoneyProcessor() {
            super();
        }

        /* access modifiers changed from: protected */
        public void process(AccessibilityEvent accessibilityEvent) {
            super.process(accessibilityEvent);
            CharSequence className = accessibilityEvent.getClassName();
            final AccessibilityNodeInfo source = accessibilityEvent.getSource();
            if (className != null && "cooperation.qwallet.plugin.QWalletPluginProxyActivity".equals(className)) {
                LuckyMoneyAccessibilityService.this.mMainHandler.post(new Runnable() {
                    private int times = 0;

                    public void run() {
                        Log.d(LuckyMoneyAccessibilityService.TAG, "process qq lucky monkey " + this.times);
                        AccessibilityNodeInfo rootInActiveWindow = LuckyMoneyAccessibilityService.this.getRootInActiveWindow();
                        if (rootInActiveWindow == null) {
                            rootInActiveWindow = source;
                        }
                        if (rootInActiveWindow == null) {
                            Log.d(LuckyMoneyAccessibilityService.TAG, "nodeinfo is null");
                            int i = this.times;
                            if (i < 3) {
                                this.times = i + 1;
                                LuckyMoneyAccessibilityService.this.mMainHandler.postDelayed(this, 500);
                                return;
                            }
                            return;
                        }
                        List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText = rootInActiveWindow.findAccessibilityNodeInfosByText("元");
                        if (findAccessibilityNodeInfosByText.size() == 0) {
                            Log.d(LuckyMoneyAccessibilityService.TAG, "not found money");
                            int i2 = this.times;
                            if (i2 < 3) {
                                this.times = i2 + 1;
                                LuckyMoneyAccessibilityService.this.mMainHandler.postDelayed(this, 500);
                                return;
                            }
                            return;
                        }
                        try {
                            String findMoneyByNodeInfo = QQLuckyMoneyProcessor.this.findMoneyByNodeInfo(findAccessibilityNodeInfosByText.get(0), "已存入余额");
                            if (findMoneyByNodeInfo != null) {
                                Log.i(LuckyMoneyAccessibilityService.TAG, "found money " + findMoneyByNodeInfo);
                                long receiveTotalLuckyMoney = LuckyMoneyAccessibilityService.this.mCommonConfig.getReceiveTotalLuckyMoney();
                                long round = (long) Math.round(Float.parseFloat(findMoneyByNodeInfo) * 100.0f);
                                LuckyMoneyAccessibilityService.this.mCommonConfig.saveReceiveTotalLuckyMoney(receiveTotalLuckyMoney + round);
                                LuckyMoneyAccessibilityService.this.mCommonConfig.setQQMoney(LuckyMoneyAccessibilityService.this.mCommonConfig.getQQMoney() + round);
                                LuckyMoneyAccessibilityService.this.mCommonConfig.setTodayQQMoney(LuckyMoneyAccessibilityService.this.mCommonConfig.getTodayQQMoney() + round);
                            } else if (this.times < 3) {
                                this.times++;
                                LuckyMoneyAccessibilityService.this.mMainHandler.postDelayed(this, 500);
                            }
                        } catch (Exception e) {
                            Log.i(LuckyMoneyAccessibilityService.TAG, "QQ get money failed", e);
                        }
                    }
                });
            }
        }
    }

    private void recordMoney() {
        long todayQQMoney = this.mCommonConfig.getTodayQQMoney();
        long todayMMMoney = this.mCommonConfig.getTodayMMMoney();
        MiStatUtil.recordQQMoney(todayQQMoney);
        MiStatUtil.recordMMMoney(todayMMMoney);
        this.mCommonConfig.setLastRecordMoneyTime(System.currentTimeMillis());
        this.mCommonConfig.setTodayQQMoney(0);
        this.mCommonConfig.setTodayMMMoney(0);
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        BaseLuckyMoneyProcessor baseLuckyMoneyProcessor;
        if (DateUtil.getTodayTimeMillis() > this.mCommonConfig.getLastRecordMoneyTime()) {
            recordMoney();
        }
        CharSequence packageName = accessibilityEvent.getPackageName();
        if (!TextUtils.isEmpty(packageName)) {
            if (packageName.equals(AppConstants.Package.PACKAGE_NAME_QQ)) {
                if (accessibilityEvent.getEventType() == 32) {
                    baseLuckyMoneyProcessor = this.qqLuckyMoneyProcessor;
                } else {
                    return;
                }
            } else if (packageName.equals(AppConstants.Package.PACKAGE_NAME_MM) && accessibilityEvent.getEventType() == 32) {
                baseLuckyMoneyProcessor = this.mmLuckyMoneyProcessor;
            } else {
                return;
            }
            baseLuckyMoneyProcessor.process(accessibilityEvent);
        }
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mCommonConfig = CommonConfig.getInstance(this);
        this.qqLuckyMoneyProcessor = new QQLuckyMoneyProcessor();
        this.mmLuckyMoneyProcessor = new MMLuckyMoneyProcessor();
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    public void onInterrupt() {
    }
}
