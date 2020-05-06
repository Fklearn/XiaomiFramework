package com.miui.luckymoney.controller;

import android.app.PendingIntent;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.d.a;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.FastOpenConfig;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.model.message.AppMessage;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.ui.view.PendingIntentRunnable;
import com.miui.luckymoney.ui.view.messageview.MessageView;
import com.miui.luckymoney.ui.view.messageview.MessageViewCreator;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.utils.PackageUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.luckymoney.utils.SettingsUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Pipeline {
    private static final int MAX_HEADS_UP_VIEW_COUNT = 20;
    private static final String TAG = "Pipeline";
    private static final ArrayList<Pipeline> allPipelines = new ArrayList<>();
    private static final ArrayList<String> headsUpMessageViewHistory = new ArrayList<>();
    /* access modifiers changed from: private */
    public static final ArrayList<Pipeline> keyguardPipelineHistory = new ArrayList<>();
    private static ScreenUtil.KeyguardUnlockedListener keyguardUnlockedListener;
    private final BaseConfiguration configuration;
    private final HashMap<String, WeakReference<MessageView>> headsUpMessageViews = new HashMap<>();
    /* access modifiers changed from: private */
    public AppMessage lastLockScreenMessage = null;
    private WeakReference<MessageView> lockScreenMessageView = null;
    private final MessageViewCreator viewCreator;

    private Pipeline(BaseConfiguration baseConfiguration, MessageViewCreator messageViewCreator) {
        this.configuration = baseConfiguration;
        this.viewCreator = messageViewCreator;
    }

    private boolean checkFastOpenMode() {
        if (ScreenUtil.isScreenLocked(this.configuration.context()) || !CommonConfig.getInstance(this.configuration.context()).isFastOpenEnable()) {
            return false;
        }
        String foregroundApp = PackageUtil.getForegroundApp(this.configuration.context());
        String str = TAG;
        Log.d(str, "currentPackage:" + foregroundApp);
        return !FastOpenConfig.getInstance(this.configuration.context()).isRestrict(foregroundApp);
    }

    private void closeAllHeadsUpMessageView() {
        for (String next : this.headsUpMessageViews.keySet()) {
            MessageView messageView = (MessageView) this.headsUpMessageViews.get(next).get();
            if (messageView != null && messageView.isAlive()) {
                messageView.hide();
            }
            headsUpMessageViewHistory.remove(next);
        }
        this.headsUpMessageViews.clear();
    }

    private static synchronized void closeAllHeadsUpMessageViewOfAllPipelines() {
        synchronized (Pipeline.class) {
            Iterator<Pipeline> it = allPipelines.iterator();
            while (it.hasNext()) {
                it.next().closeAllHeadsUpMessageView();
            }
        }
    }

    private void closeHeadsUpMessageView(String str) {
        WeakReference weakReference = this.headsUpMessageViews.get(str);
        if (weakReference != null) {
            MessageView messageView = (MessageView) weakReference.get();
            if (messageView != null && messageView.isAlive()) {
                messageView.hide();
            }
            this.headsUpMessageViews.remove(str);
            headsUpMessageViewHistory.remove(str);
        }
    }

    private static synchronized void closeHeadsUpMessageViewOfAllPipelines(String str) {
        synchronized (Pipeline.class) {
            Iterator<Pipeline> it = allPipelines.iterator();
            while (it.hasNext()) {
                it.next().closeHeadsUpMessageView(str);
            }
        }
    }

    private void closeLockScreenMessageView() {
        WeakReference<MessageView> weakReference = this.lockScreenMessageView;
        if (weakReference != null) {
            MessageView messageView = (MessageView) weakReference.get();
            if (messageView != null && messageView.isAlive()) {
                messageView.hide();
            }
            this.lockScreenMessageView = null;
        }
    }

    private static synchronized void closeLockScreenMessageViewExcept(Pipeline pipeline) {
        synchronized (Pipeline.class) {
            Iterator<Pipeline> it = allPipelines.iterator();
            while (it.hasNext()) {
                Pipeline next = it.next();
                if (next != pipeline) {
                    next.closeLockScreenMessageView();
                }
            }
        }
    }

    public static synchronized Pipeline create(BaseConfiguration baseConfiguration, MessageViewCreator messageViewCreator) {
        Pipeline pipeline;
        synchronized (Pipeline.class) {
            if (keyguardUnlockedListener == null) {
                keyguardUnlockedListener = new ScreenUtil.KeyguardUnlockedListener() {
                    public void onKeyguardUnlocked() {
                        if (Pipeline.keyguardPipelineHistory.size() > 0) {
                            for (int size = Pipeline.keyguardPipelineHistory.size() - 1; size > 0; size--) {
                                Pipeline pipeline = (Pipeline) Pipeline.keyguardPipelineHistory.get(size);
                                if (pipeline.lastLockScreenMessage != null) {
                                    AppMessage access$100 = pipeline.lastLockScreenMessage;
                                    AppMessage unused = pipeline.lastLockScreenMessage = null;
                                    boolean unused2 = pipeline.process(access$100, true);
                                }
                            }
                            AppMessage unused3 = ((Pipeline) Pipeline.keyguardPipelineHistory.get(0)).lastLockScreenMessage = null;
                        }
                        Pipeline.keyguardPipelineHistory.clear();
                    }
                };
                ScreenUtil.register(keyguardUnlockedListener);
            }
            pipeline = new Pipeline(baseConfiguration, messageViewCreator);
            allPipelines.add(pipeline);
        }
        return pipeline;
    }

    private MessageView obtainLockScreenMessageView(AppMessage appMessage) {
        WeakReference<MessageView> weakReference = this.lockScreenMessageView;
        MessageView messageView = null;
        if (weakReference != null) {
            MessageView messageView2 = (MessageView) weakReference.get();
            if (messageView2 == null || !messageView2.isAlive()) {
                this.lockScreenMessageView = null;
            } else {
                messageView = messageView2;
            }
        }
        if (messageView != null) {
            return messageView;
        }
        MessageView createLockScreenMessageView = this.viewCreator.createLockScreenMessageView();
        this.lockScreenMessageView = new WeakReference<>(createLockScreenMessageView);
        return createLockScreenMessageView;
    }

    private MessageView obtianHeadsUpMessageView(AppMessage appMessage) {
        String id = appMessage.getId();
        WeakReference weakReference = this.headsUpMessageViews.get(id);
        MessageView messageView = null;
        if (weakReference != null) {
            MessageView messageView2 = (MessageView) weakReference.get();
            if (messageView2 == null || !messageView2.isAlive()) {
                this.headsUpMessageViews.remove(id);
            } else {
                messageView = messageView2;
            }
        }
        if (messageView == null && (messageView = this.viewCreator.createHeadsUpMessageView()) != null) {
            this.headsUpMessageViews.put(id, new WeakReference(messageView));
        }
        headsUpMessageViewHistory.remove(id);
        headsUpMessageViewHistory.add(0, id);
        if (headsUpMessageViewHistory.size() > 20) {
            ArrayList<String> arrayList = headsUpMessageViewHistory;
            closeHeadsUpMessageViewOfAllPipelines(arrayList.get(arrayList.size() - 1));
        }
        return messageView;
    }

    private MessageView obtianMessageView(AppMessage appMessage) {
        if (!ScreenUtil.isScreenLocked(this.configuration.context())) {
            return obtianHeadsUpMessageView(appMessage);
        }
        closeAllHeadsUpMessageViewOfAllPipelines();
        closeLockScreenMessageViewExcept(this);
        return obtainLockScreenMessageView(appMessage);
    }

    /* access modifiers changed from: private */
    public boolean process(AppMessage appMessage, boolean z) {
        String str;
        String str2;
        boolean z2;
        if (!appMessage.isHongbao()) {
            return false;
        }
        Log.d(TAG, "Message is lucky money, continue");
        CommonConfig instance = CommonConfig.getInstance(this.configuration.context());
        BaseConfiguration.NotifyType notifyType = this.configuration.getNotifyType();
        if (notifyType == null || notifyType == BaseConfiguration.NotifyType.NONE) {
            str = TAG;
            str2 = "Remind is disabled, do not remind";
        } else if (!this.configuration.justForGroupMessage() || appMessage.isGroupMessage() || appMessage.isBusinessMessage()) {
            this.lastLockScreenMessage = null;
            if (!z && !TextUtils.isEmpty(appMessage.getName())) {
                if (!appMessage.isGroupMessage()) {
                    int personalLuckyCountFrom = instance.getPersonalLuckyCountFrom(appMessage.getName()) + 1;
                    instance.setPersonalLuckyCountFrom(appMessage.getName(), personalLuckyCountFrom);
                    if (personalLuckyCountFrom > instance.getPersonalLuckyCountFrom(instance.getPersonalLuckyMaxSource())) {
                        instance.setPersonalLuckyMaxSource(appMessage.getName());
                    }
                } else {
                    int luckyCountFrom = instance.getLuckyCountFrom(appMessage.getName()) + 1;
                    instance.setLuckyCountFrom(appMessage.getName(), luckyCountFrom);
                    if (luckyCountFrom > instance.getLuckyCountFrom(instance.getLuckyMaxSource())) {
                        instance.setLuckyMaxSource(appMessage.getName());
                    }
                }
            }
            if (!SettingsUtil.isQuietModeEnable(this.configuration.context())) {
                ScreenUtil.powerOnScreen(this.configuration.context());
            }
            if (z || !checkFastOpenMode()) {
                MessageView obtianMessageView = obtianMessageView(appMessage);
                if (obtianMessageView == null) {
                    str = TAG;
                    str2 = "Failed to obtain message view, do not remind";
                } else {
                    if (!z) {
                        instance.setWarningLuckyMoneyCount(instance.getWarningLuckyMoneyCount() + 1);
                    }
                    obtianMessageView.show(appMessage);
                    if (!z && ScreenUtil.isScreenLocked(this.configuration.context())) {
                        keyguardPipelineHistory.remove(this);
                        keyguardPipelineHistory.add(0, this);
                        this.lastLockScreenMessage = appMessage;
                    }
                    z2 = false;
                }
            } else {
                new PendingIntentRunnable(appMessage.getAction()).run();
                instance.setWarningLuckyMoneyCount(instance.getWarningLuckyMoneyCount() + 1);
                MiStatUtil.recordLuckyMoneyFastOpen(this.configuration.getLuckyMoneyEventKeyPostfix());
                z2 = true;
            }
            if (!SettingsUtil.isQuietModeEnable(this.configuration.context()) && this.configuration.needPlaySource()) {
                a.b(this.configuration.context());
            }
            if (z2 || (!z && this.configuration.needPlaySource())) {
                NotificationUtil.showFloatNotification(this.configuration.context(), this.configuration.getSoundResId().intValue(), this.configuration.context().getResources().getString(R.string.hongbao_name), String.format(this.configuration.context().getResources().getString(R.string.fast_open_noti_summary), new Object[]{appMessage.getName()}), (PendingIntent) null, this.configuration.getSoundResId().intValue(), this.configuration.needPlaySource(), true);
            }
            return true;
        } else {
            str = TAG;
            str2 = "Message is not for group, do not remind";
        }
        Log.d(str, str2);
        return false;
    }

    public static synchronized void recycle(Pipeline pipeline) {
        synchronized (Pipeline.class) {
            if (pipeline != null) {
                pipeline.closeAllHeadsUpMessageView();
                pipeline.closeLockScreenMessageView();
                allPipelines.remove(pipeline);
                keyguardPipelineHistory.remove(pipeline);
            }
        }
    }

    public void notifyPhoneArrived() {
        closeAllHeadsUpMessageView();
        closeLockScreenMessageView();
    }

    public boolean process(AppMessage appMessage) {
        return process(appMessage, false);
    }
}
