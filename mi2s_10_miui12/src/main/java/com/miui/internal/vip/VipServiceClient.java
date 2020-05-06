package com.miui.internal.vip;

import android.content.Context;
import android.text.TextUtils;
import com.miui.internal.vip.protocol.Convertor;
import com.miui.internal.vip.protocol.PortraitData;
import com.miui.internal.vip.utils.AuthHttpRequest;
import com.miui.internal.vip.utils.DeviceHelper;
import com.miui.internal.vip.utils.JsonParser;
import com.miui.internal.vip.utils.RunnableHelper;
import com.miui.internal.vip.utils.Utils;
import com.miui.internal.vip.utils.VipDataPref;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miui.os.Build;
import miui.vip.QueryCallback;
import miui.vip.VipAchievement;
import miui.vip.VipUserInfo;

public class VipServiceClient {
    private static final String CMD_PORTRAIT = "/user/portrait";
    private static final String CMD_TONGJI = "/tongji/page";
    private static final String KEY_DATA = "portrait_data";
    private static final String PARAM_NAME = "name";
    private static final String PREF_NAME = "xiaomi_vip_portrait_data";
    private final CopyOnWriteArrayList<WeakReference<QueryCallback>> mCallbacks = new CopyOnWriteArrayList<>();
    private final Context mCtx;
    private PortraitData mData;
    private final boolean mIsServiceAvailable;
    private VipDataPref mPref;

    static class ConnectData {
        public List<VipAchievement> achievements;
        public boolean isServiceAvailable;
        public VipUserInfo userInfo;

        ConnectData() {
        }
    }

    public VipServiceClient(Context ctx) {
        this.mCtx = ctx;
        this.mIsServiceAvailable = localCheck() && Utils.getPackageInfo(VipConstants.VIP_PACKAGE) != null;
        this.mPref = new VipDataPref(ctx, PREF_NAME);
        String strPortrait = this.mPref.getString(KEY_DATA);
        if (!TextUtils.isEmpty(strPortrait)) {
            this.mData = (PortraitData) JsonParser.parseJsonObject(strPortrait, PortraitData.class);
        }
    }

    public static boolean localCheck() {
        return !Build.IS_INTERNATIONAL_BUILD && !DeviceHelper.isPad();
    }

    public void connect(QueryCallback callback) {
        if (this.mIsServiceAvailable) {
            Utils.log("VipService, connect, callback = %s", callback);
            addConnectCallback(callback);
            queryPortraitInfo();
            notifyResult(callback, 8, new VipResponse(0, (Object) getConnectData()));
        }
    }

    private ConnectData getConnectData() {
        ConnectData connect = new ConnectData();
        connect.isServiceAvailable = this.mIsServiceAvailable && this.mData != null;
        PortraitData portraitData = this.mData;
        if (portraitData != null) {
            connect.userInfo = Convertor.toVipUserInfo(portraitData.level);
            connect.achievements = Convertor.toVipAchievement(this.mData.badgeList);
        }
        return connect;
    }

    public void disconnect(QueryCallback listener) {
        removeCallback(listener);
    }

    public void sendStatistic(String data) {
        String ret = new AuthHttpRequest(CMD_TONGJI, "name", data).queryAsString();
        if (!JsonParser.isEmptyJson(ret)) {
            Utils.logI("send statistic failed, ret = %s", ret);
        }
    }

    public boolean isConnected() {
        return true;
    }

    private void addConnectCallback(QueryCallback listener) {
        if (listener != null && searchCallback(new ListenerSelector(listener)).isEmpty()) {
            this.mCallbacks.add(new WeakReference(listener));
        }
    }

    static abstract class Selector {
        public boolean isRemoveFoundItem = false;

        public abstract boolean isFound(QueryCallback queryCallback);

        Selector() {
        }
    }

    private class ListenerSelector extends Selector {
        QueryCallback mListener;

        ListenerSelector(QueryCallback listener) {
            this.mListener = listener;
        }

        public boolean isFound(QueryCallback callback) {
            return callback.equals(this.mListener);
        }
    }

    private List<QueryCallback> searchCallback(Selector selector) {
        List<QueryCallback> matchList = new ArrayList<>();
        List<WeakReference<QueryCallback>> removeList = null;
        Iterator<WeakReference<QueryCallback>> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            WeakReference<QueryCallback> ref = it.next();
            QueryCallback callback = (QueryCallback) ref.get();
            boolean remove = false;
            if (callback == null) {
                remove = true;
            } else if (selector.isFound(callback)) {
                if (selector.isRemoveFoundItem) {
                    remove = true;
                } else {
                    matchList.add(callback);
                }
            }
            if (remove) {
                if (removeList == null) {
                    removeList = new ArrayList<>();
                }
                removeList.add(ref);
            }
        }
        if (Utils.hasData((List<?>) removeList)) {
            this.mCallbacks.removeAll(removeList);
        }
        return matchList;
    }

    private void removeCallback(QueryCallback listener) {
        if (listener != null) {
            ListenerSelector selector = new ListenerSelector(listener);
            selector.isRemoveFoundItem = true;
            searchCallback(selector);
        }
    }

    public void queryUserVipInfo() {
        int i = this.mData != null ? 0 : 1006;
        PortraitData portraitData = this.mData;
        notifyResult(1, new VipResponse(i, (Object) portraitData != null ? Convertor.toVipUserInfo(portraitData.level) : null));
    }

    public void queryAchievements() {
        int i = this.mData != null ? 0 : 1006;
        PortraitData portraitData = this.mData;
        notifyResult(16, new VipResponse(i, (Object) portraitData != null ? Convertor.toVipAchievement(portraitData.badgeList) : null));
    }

    public void queryBanners() {
        int i = this.mData != null ? 0 : 1006;
        PortraitData portraitData = this.mData;
        notifyResult(64, new VipResponse(i, (Object) portraitData != null ? Convertor.toVipBanner(portraitData.bannerList) : null));
    }

    private void queryPortraitInfo() {
        String data = new AuthHttpRequest(CMD_PORTRAIT, new String[0]).queryAsString();
        PortraitData portraitData = (PortraitData) JsonParser.parseJsonObject(data, PortraitData.class);
        if (portraitData != null) {
            this.mData = portraitData;
            this.mPref.setString(KEY_DATA, data);
        }
    }

    private void notifyResult(final int type, final VipResponse res) {
        searchCallback(new Selector() {
            public boolean isFound(QueryCallback callback) {
                if (!callback.isMonitorType(type)) {
                    return false;
                }
                VipServiceClient.this.notifyResult(callback, type, res);
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyResult(final QueryCallback callback, final int type, final VipResponse res) {
        if (callback != null) {
            RunnableHelper.runInUIThread(new Runnable() {
                public void run() {
                    VipServiceClient.this.invokeCallback(type, res, callback);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void invokeCallback(int type, VipResponse result, QueryCallback callback) {
        if (result != null && callback != null) {
            if (type == 1) {
                callback.onUserInfo(result.state, (VipUserInfo) result.value, result.errMsg);
            } else if (type == 8) {
                ConnectData data = (ConnectData) result.value;
                callback.onConnected(data.isServiceAvailable, data.userInfo, data.achievements);
            } else if (type == 16) {
                callback.onAchievements(result.state, (List) result.value, result.errMsg);
            } else if (type == 64) {
                callback.onBanners(result.state, (List) result.value, result.errMsg);
            }
        }
    }
}
