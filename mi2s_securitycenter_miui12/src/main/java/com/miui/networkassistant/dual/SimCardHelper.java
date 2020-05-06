package com.miui.networkassistant.dual;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.o.g.c;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.DualSimInfoManager;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import java.util.List;
import java.util.Map;

public class SimCardHelper {
    private static final String TAG = "DualSimCardHelper";
    private static SimCardHelper sInstance;
    protected Context mContext;
    protected String mImsi1;
    private String mImsi2;
    protected boolean mIsSim1Inserted;
    private boolean mIsSim2Inserted;
    private DualSimInfoManager.ISimInfoChangeListener mSimInfoChangeListener;

    public static class SingleSimCardHelper extends SimCardHelper {
        public SingleSimCardHelper(Context context) {
            super(context);
        }

        public boolean isSimCardReady(int i) {
            c.a a2 = c.a.a("miui.telephony.TelephonyManager");
            a2.b("getDefault", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("getSimState", (Class<?>[]) null, new Object[0]);
            return 5 == a2.c();
        }

        public boolean updateSimState() {
            String subscriberId = TelephonyUtil.getSubscriberId(this.mContext);
            if (TextUtils.isEmpty(subscriberId)) {
                this.mIsSim1Inserted = false;
                subscriberId = "default";
            } else {
                this.mIsSim1Inserted = true;
            }
            if (!TextUtils.equals(subscriberId, this.mImsi1)) {
                this.mImsi1 = subscriberId;
            }
            SimUserInfo.getInstance(this.mContext, this.mImsi1, 0).setSimInserted(this.mIsSim1Inserted);
            return !isImsiMissed();
        }
    }

    private SimCardHelper(Context context) {
        this.mImsi1 = "default";
        this.mImsi2 = "default";
        this.mSimInfoChangeListener = new DualSimInfoManager.ISimInfoChangeListener() {
            public void onSubscriptionsChanged() {
                SimCardHelper.this.updateSimState();
            }
        };
        this.mContext = context.getApplicationContext();
        updateSimState();
    }

    public static void asyncInit(final Context context) {
        a.a(new Runnable() {
            public void run() {
                SimCardHelper.getInstance(context).initForUIProcess();
            }
        });
    }

    public static synchronized SimCardHelper getInstance(Context context) {
        SimCardHelper simCardHelper;
        synchronized (SimCardHelper.class) {
            if (sInstance == null) {
                sInstance = DeviceUtil.IS_DUAL_CARD ? new SimCardHelper(context) : new SingleSimCardHelper(context);
                sInstance.initForUIProcess();
            }
            simCardHelper = sInstance;
        }
        return simCardHelper;
    }

    public static void init(Context context) {
        getInstance(context);
    }

    /* access modifiers changed from: private */
    public void initForUIProcess() {
        DualSimInfoManager.registerChangeListener(this.mContext, this.mSimInfoChangeListener);
    }

    private void makeSimUserInfo(String str, Map<String, String> map) {
        int parseInt = Integer.parseInt(map.get(Sim.SLOT_NUM));
        SimUserInfo instance = SimUserInfo.getInstance(this.mContext, str, parseInt);
        instance.setSimId(Long.parseLong(map.get(Sim.SIM_ID)));
        String str2 = map.get(Sim.SIM_NAME);
        if (TelephonyUtil.isVirtualSim(this.mContext, parseInt)) {
            str2 = TelephonyUtil.getVirtualSimCarrierName(this.mContext);
        }
        instance.setSimName(str2);
        instance.setIccid(map.get(Sim.ICCID));
        instance.setSimInserted(true);
        Log.i(TAG, "make siminfo: " + instance.toString());
    }

    public int getCurrentMobileSlotNum() {
        return TelephonyUtil.getCurrentMobileSlotNum();
    }

    public String getSim1Imsi() {
        return this.mImsi1;
    }

    public String getSim2Imsi() {
        return this.mImsi2;
    }

    public String getSimImsi(int i) {
        if (i == 0) {
            return this.mImsi1;
        }
        if (i == 1) {
            return this.mImsi2;
        }
        return null;
    }

    public int getSlotNumByImsi(String str) {
        if (TextUtils.equals(this.mImsi1, str)) {
            return 0;
        }
        return TextUtils.equals(this.mImsi2, str) ? 1 : -1;
    }

    public boolean isDualSimInserted() {
        return this.mIsSim1Inserted && this.mIsSim2Inserted;
    }

    public boolean isDualSimInsertedOne() {
        return (this.mIsSim1Inserted && !this.mIsSim2Inserted) || (!this.mIsSim1Inserted && this.mIsSim2Inserted);
    }

    /* access modifiers changed from: protected */
    public boolean isImsiMissed() {
        return (this.mIsSim1Inserted && this.mImsi1 == null) || (this.mIsSim2Inserted && this.mImsi2 == null);
    }

    public boolean isSim1Inserted() {
        return this.mIsSim1Inserted;
    }

    public boolean isSim2Inserted() {
        return this.mIsSim2Inserted;
    }

    public boolean isSimCardReady(int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSimStateForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.c() == 5;
    }

    public boolean isSimInserted() {
        return this.mIsSim1Inserted || this.mIsSim2Inserted;
    }

    public boolean updateSimState() {
        List<Map<String, String>> list;
        String str;
        Map map;
        int i;
        try {
            list = DualSimInfoManager.getSimInfoList(this.mContext);
        } catch (Exception e) {
            Log.i(TAG, "get sim info exception!", e);
            list = null;
        }
        if (list == null || list.isEmpty()) {
            this.mIsSim1Inserted = false;
            this.mIsSim2Inserted = false;
            this.mImsi1 = "default";
            this.mImsi2 = "default";
            return true;
        }
        if (list.size() == 1) {
            Log.i(TAG, "one sim card inserted");
            map = list.get(0);
            i = Integer.parseInt((String) map.get(Sim.SLOT_NUM));
            if (i == 0) {
                this.mIsSim1Inserted = true;
                this.mIsSim2Inserted = false;
                this.mImsi1 = TelephonyUtil.getSubscriberId(this.mContext, i);
                this.mImsi2 = "default";
                str = this.mImsi1;
                makeSimUserInfo(str, map);
                return !isImsiMissed();
            }
            if (i == 1) {
                this.mIsSim1Inserted = false;
                this.mIsSim2Inserted = true;
                this.mImsi1 = "default";
            }
            return !isImsiMissed();
        } else if (list.size() == 2) {
            Log.i(TAG, "two sim cards inserted");
            this.mIsSim1Inserted = true;
            Map map2 = list.get(0);
            this.mImsi1 = TelephonyUtil.getSubscriberId(this.mContext, Integer.parseInt((String) map2.get(Sim.SLOT_NUM)));
            makeSimUserInfo(this.mImsi1, map2);
            this.mIsSim2Inserted = true;
            map = list.get(1);
            i = Integer.parseInt((String) map.get(Sim.SLOT_NUM));
        } else {
            Log.i(TAG, "no sim card inserted");
            return !isImsiMissed();
        }
        this.mImsi2 = TelephonyUtil.getSubscriberId(this.mContext, i);
        str = this.mImsi2;
        makeSimUserInfo(str, map);
        return !isImsiMissed();
    }
}
