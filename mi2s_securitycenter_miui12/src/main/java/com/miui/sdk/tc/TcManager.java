package com.miui.sdk.tc;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.networkassistant.model.DataUsageConstants;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.TelephonyUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TcManager {
    private static final String TAG = "com.miui.sdk.tc.TcManager";
    public static final int TC_TYPE_ALL = 7;
    public static final int TC_TYPE_BILL = 2;
    public static final int TC_TYPE_CALLTIME = 4;
    public static final int TC_TYPE_TRAFFIC = 1;
    private static TcManager sInstance = null;
    private static boolean sLibLoad = false;
    private static HashMap<String, String> sOperatorMap = new HashMap<>();
    private final ArrayList<String>[] mBlockNumberList = new ArrayList[2];
    private Context mContext;
    private boolean mIsCH = true;
    private boolean mIsInited = false;

    public enum ReturnCode {
        OK(0),
        ErrorInvalidParams(-1),
        ErrorInvalidPackageName(-2),
        ErrorInvalidSlotNum(-3),
        ErrorCreateFileFailed(-4),
        ErrorNotInited(-5),
        ErrorUpdating(-6),
        ErrorUpdateFailed(-7),
        ErrorJavaException(-8),
        ErrorParseError(-9),
        Error;
        
        private final int mValue;

        private ReturnCode(int i) {
            this.mValue = i;
        }

        public static ReturnCode parse(int i) {
            switch (i) {
                case -9:
                    return ErrorParseError;
                case -8:
                    return ErrorJavaException;
                case -7:
                    return ErrorUpdateFailed;
                case DataUsageConstants.UID_OTHERS:
                    return ErrorUpdating;
                case -5:
                    return ErrorNotInited;
                case -4:
                    return ErrorCreateFileFailed;
                case -3:
                    return ErrorInvalidSlotNum;
                case -2:
                    return ErrorInvalidPackageName;
                case -1:
                    return ErrorInvalidParams;
                case 0:
                    return OK;
                default:
                    return Error;
            }
        }

        public int value() {
            return this.mValue;
        }
    }

    static {
        sOperatorMap.put(TelephonyUtil.CMCC, "100");
        sOperatorMap.put(TelephonyUtil.UNICOM, "200");
        sOperatorMap.put(TelephonyUtil.TELECOM, "300");
    }

    private TcManager() {
        this.mBlockNumberList[0] = new ArrayList<>();
        this.mBlockNumberList[1] = new ArrayList<>();
    }

    private synchronized void clearBlockNumberList(int i) {
        this.mBlockNumberList[i].clear();
    }

    public static synchronized TcManager getInstance() {
        TcManager tcManager;
        synchronized (TcManager.class) {
            if (sInstance == null) {
                sInstance = new TcManager();
            }
            tcManager = sInstance;
        }
        return tcManager;
    }

    private synchronized boolean isInBlockNumberList(String str, int i) {
        return this.mBlockNumberList[i].contains(str);
    }

    private void loadLib() {
        try {
            System.loadLibrary("tcp");
            sLibLoad = true;
        } catch (Throwable th) {
            sLibLoad = false;
            AnalyticsHelper.recordThrowable(th);
            Log.e(TAG, "not find libtcp.so");
        }
    }

    public synchronized void addBlockNumber(String str, int i) {
        if (!this.mBlockNumberList[i].contains(str)) {
            this.mBlockNumberList[i].add(str);
        }
    }

    public List<TcDirection> getAllInstructions(int i) {
        try {
            ArrayList<TcDirection> instructions = TcPlugin.getInstructions(i);
            if (instructions == null || instructions.isEmpty()) {
                return new ArrayList();
            }
            Iterator<TcDirection> it = instructions.iterator();
            while (it.hasNext()) {
                addBlockNumber(it.next().getReceiveNumber(), i);
            }
            return instructions;
        } catch (Throwable unused) {
            return new ArrayList();
        }
    }

    public Map<String, String> getBrands(String str) {
        if (sLibLoad) {
            return TcPlugin.getBrandsMap(str, this.mIsCH);
        }
        return null;
    }

    public Map<Integer, String> getCities(int i) {
        if (sLibLoad) {
            return TcPlugin.getCitiesMap(i);
        }
        return null;
    }

    public Map<String, String> getInstructions() {
        if (sLibLoad) {
            return getInstructionsMapByType(0, 1);
        }
        return null;
    }

    public List<TcDirection> getInstructionsByTcType(int i, int i2) {
        try {
            ArrayList<TcDirection> instructions = TcPlugin.getInstructions(i);
            ArrayList arrayList = new ArrayList();
            if (instructions == null || instructions.isEmpty()) {
                return new ArrayList();
            }
            Iterator<TcDirection> it = instructions.iterator();
            while (it.hasNext()) {
                TcDirection next = it.next();
                if ((next.getCmdType() & i2) != 0) {
                    addBlockNumber(next.getReceiveNumber(), i);
                    arrayList.add(next);
                }
            }
            return arrayList;
        } catch (Throwable unused) {
            return new ArrayList();
        }
    }

    public Map<String, String> getInstructionsMapByType(int i, int i2) {
        TreeMap treeMap = null;
        try {
            ArrayList<TcDirection> instructions = TcPlugin.getInstructions(i);
            if (instructions != null && !instructions.isEmpty()) {
                treeMap = new TreeMap();
                Iterator<TcDirection> it = instructions.iterator();
                int i3 = 0;
                while (it.hasNext()) {
                    TcDirection next = it.next();
                    if ((next.getCmdType() & i2) != 0) {
                        treeMap.put(String.format("%s#%d", new Object[]{next.getSendNumber(), Integer.valueOf(i3)}), String.format("%s#%s", new Object[]{next.getDirection(), next.getReceiveNumber()}));
                        addBlockNumber(next.getReceiveNumber(), i);
                        i3++;
                    }
                }
            }
        } catch (Throwable unused) {
        }
        return treeMap;
    }

    public Map<String, String> getOperators() {
        if (sLibLoad) {
            return TcPlugin.getCarriesMap(this.mIsCH);
        }
        return null;
    }

    public int getProvinceCodeByCityCode(int i) {
        if (sLibLoad) {
            return TcPlugin.getProvinceCodeByCityCode(i);
        }
        return -1;
    }

    public Map<Integer, String> getProvinces() {
        if (sLibLoad) {
            return TcPlugin.getProvincesMap(this.mIsCH);
        }
        return null;
    }

    public DataUsage getResult(String str, String str2) {
        return getResult(str, str2, 0);
    }

    public DataUsage getResult(String str, String str2, int i) {
        return getResult(str, str2, i, 1);
    }

    public DataUsage getResult(String str, String str2, int i, int i2) {
        DataUsage dataUsage = new DataUsage(1);
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            HashMap hashMap = new HashMap(4);
            int i3 = -1;
            try {
                i3 = TcPlugin.getResultByTcType(str2, str, hashMap, i, i2);
            } catch (Throwable unused) {
            }
            if (i3 == 0) {
                dataUsage.parse((String) hashMap.get("Result"));
            }
            Log.i(TAG, dataUsage.toString());
        }
        return dataUsage;
    }

    public synchronized ReturnCode init(Context context, String str, String str2) {
        int i;
        i = -1;
        this.mContext = context;
        if (!this.mIsInited) {
            loadLib();
            if (sLibLoad) {
                i = TcPlugin.init(this.mContext, str, str2);
                this.mIsInited = true;
            }
        }
        return ReturnCode.parse(i);
    }

    public boolean isSmsNeedBlock(String str) {
        return isSmsNeedBlock(str, 0);
    }

    public boolean isSmsNeedBlock(String str, int i) {
        return str != null && isInBlockNumberList(str, i);
    }

    public ReturnCode setConfig(UserConfig userConfig) {
        return setConfig(userConfig, 0);
    }

    public ReturnCode setConfig(UserConfig userConfig, int i) {
        return setConfig(userConfig, i, 1);
    }

    public ReturnCode setConfig(UserConfig userConfig, int i, int i2) {
        String str = sOperatorMap.get(userConfig.getOperator());
        if (TextUtils.isEmpty(str)) {
            str = userConfig.getOperator();
        }
        int i3 = -1;
        try {
            i3 = TcPlugin.updateByTcType(userConfig.getProvince(), userConfig.getCity(), str, i, i2);
        } catch (Throwable unused) {
        }
        ReturnCode parse = ReturnCode.parse(i3);
        if (parse == ReturnCode.OK) {
            clearBlockNumberList(i);
        }
        return parse;
    }

    public ReturnCode setImsi(String str) {
        return ReturnCode.parse(sLibLoad ? TcPlugin.setImsi(str, 0) : -1);
    }

    public ReturnCode setImsi(String str, int i) {
        return ReturnCode.parse(sLibLoad ? TcPlugin.setImsi(str, i) : -1);
    }

    public ReturnCode startCorrection() {
        return startCorrection(0);
    }

    public ReturnCode startCorrection(int i) {
        int indexOf;
        Map<String, String> instructionsMapByType = getInstructionsMapByType(i, 1);
        boolean z = false;
        if (instructionsMapByType != null && instructionsMapByType.size() > 0) {
            boolean z2 = false;
            for (Map.Entry next : instructionsMapByType.entrySet()) {
                String str = (String) next.getKey();
                if (!TextUtils.isEmpty(str) && (indexOf = str.indexOf("#")) > 0) {
                    str = str.substring(0, indexOf);
                }
                String str2 = (String) next.getValue();
                if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                    SmsSender.sendTextMessage(str, str2, i);
                    z2 = true;
                }
            }
            z = z2;
        }
        return z ? ReturnCode.OK : ReturnCode.Error;
    }
}
