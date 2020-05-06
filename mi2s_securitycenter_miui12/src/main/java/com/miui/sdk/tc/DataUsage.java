package com.miui.sdk.tc;

import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataUsage {
    public static final int RETURN_CODE_ERROR = 1;
    public static final int RETURN_CODE_OK = 0;
    private static final String TAG = "DataUsage";
    private PackageDetail mBillPkg;
    private PackageDetail mCallTimePkg;
    private PackageDetail mDailyPkg;
    private PackageDetail mExtraPkg;
    private PackageDetail mLeisurePkg;
    private int mReturnCode;

    /* renamed from: com.miui.sdk.tc.DataUsage$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType = new int[PackageDetail.PkgType.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType[] r0 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType = r0
                int[] r0 = $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType r1 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.DailyPkg     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType r1 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.OldPkg     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType r1 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.AddPkg     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType r1 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.LeisurePkg     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType r1 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.BillPkg     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType r1 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.CallTimePkg     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                int[] r0 = $SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType     // Catch:{ NoSuchFieldError -> 0x0056 }
                com.miui.sdk.tc.DataUsage$PackageDetail$PkgType r1 = com.miui.sdk.tc.DataUsage.PackageDetail.PkgType.NoPkg     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.sdk.tc.DataUsage.AnonymousClass1.<clinit>():void");
        }
    }

    public static class PackageDetail {
        private static final String ADD_EXCEEDD = "ADD_EXCEEDD";
        private static final String ADD_REMAINED = "ADD_REMAINED";
        private static final String ADD_TOTAL = "ADD_TOTAL";
        private static final String ADD_USED = "ADD_USED";
        private static final String BILL_EXCEED = "BILL_EXCEED";
        private static final String BILL_REMAINED = "BILL_REMAINED";
        private static final String BILL_TOTAL = "BILL_TOTAL";
        private static final String BILL_USED = "BILL_USED";
        private static final String CALLTIME_EXCEED = "CALLTIME_EXCEED";
        private static final String CALLTIME_REMAINED = "CALLTIME_REMAINED";
        private static final String CALLTIME_TOTAL = "CALLTIME_TOTAL";
        private static final String CALLTIME_USED = "CALLTIME_USED";
        private static final String DAILY_EXCEED = "DAILY_EXCEED";
        private static final String DAILY_REMAINED = "DAILY_REMAINED";
        private static final String DAILY_TOTAL = "DAILY_TOTAL";
        private static final String DAILY_USED = "DAILY_USED";
        private static final String LEISURE_EXCEED = "LEISURE_EXCEED";
        private static final String LEISURE_REMAINED = "LEISURE_REMAINED";
        private static final String LEISURE_TOTAL = "LEISURE_TOTAL";
        private static final String LEISURE_USED = "LEISURE_USED";
        private static final String OLD_EXCEED = "GPRS_EXCEED";
        private static final String OLD_REMAINED = "TOTAL_GPRS_BALANCE";
        private static final String OLD_TOTAL = "TOTAL_GPRS";
        private static final String OLD_USED = "TOTAL_GPRS_USED";
        private static Map<String, PkgType> sPkgKeyTypeMap = new HashMap();
        private boolean mHasRemain = false;
        private boolean mHasTotal = false;
        private boolean mHasUsed = false;
        private boolean mIsJustOver = false;
        private boolean mIsStable = false;
        private PkgType mPkgType = PkgType.NoPkg;
        private long mRemainTrafficB = -1;
        private long mTotalTrafficB = -1;
        private long mUsedTrafficB = -1;

        private static class AddPkgKeys extends PkgKeys {
            public AddPkgKeys() {
                super((AnonymousClass1) null);
                this.mTotalKey = PackageDetail.ADD_TOTAL;
                this.mUsedKey = PackageDetail.ADD_USED;
                this.mRemainKey = PackageDetail.ADD_REMAINED;
                this.mExceedKey = PackageDetail.ADD_EXCEEDD;
            }
        }

        private static class BillKeys extends PkgKeys {
            public BillKeys() {
                super((AnonymousClass1) null);
                this.mTotalKey = PackageDetail.BILL_TOTAL;
                this.mUsedKey = PackageDetail.BILL_USED;
                this.mRemainKey = PackageDetail.BILL_REMAINED;
                this.mExceedKey = PackageDetail.BILL_EXCEED;
            }
        }

        private static class CallTimeKeys extends PkgKeys {
            public CallTimeKeys() {
                super((AnonymousClass1) null);
                this.mTotalKey = PackageDetail.CALLTIME_TOTAL;
                this.mUsedKey = PackageDetail.CALLTIME_USED;
                this.mRemainKey = PackageDetail.CALLTIME_REMAINED;
                this.mExceedKey = PackageDetail.CALLTIME_EXCEED;
            }
        }

        private static class DailyPkgKeys extends PkgKeys {
            public DailyPkgKeys() {
                super((AnonymousClass1) null);
                this.mTotalKey = PackageDetail.DAILY_TOTAL;
                this.mUsedKey = PackageDetail.DAILY_USED;
                this.mRemainKey = PackageDetail.DAILY_REMAINED;
                this.mExceedKey = PackageDetail.DAILY_EXCEED;
            }
        }

        private static class LeisureKeys extends PkgKeys {
            public LeisureKeys() {
                super((AnonymousClass1) null);
                this.mTotalKey = PackageDetail.LEISURE_TOTAL;
                this.mUsedKey = PackageDetail.LEISURE_USED;
                this.mRemainKey = PackageDetail.LEISURE_REMAINED;
                this.mExceedKey = PackageDetail.LEISURE_EXCEED;
            }
        }

        private static class OldKeys extends PkgKeys {
            public OldKeys() {
                super((AnonymousClass1) null);
                this.mTotalKey = PackageDetail.OLD_TOTAL;
                this.mUsedKey = PackageDetail.OLD_USED;
                this.mRemainKey = PackageDetail.OLD_REMAINED;
                this.mExceedKey = PackageDetail.OLD_EXCEED;
            }
        }

        private static class PkgKeys {
            protected String mExceedKey;
            protected String mRemainKey;
            protected String mTotalKey;
            protected String mUsedKey;

            private PkgKeys() {
            }

            /* synthetic */ PkgKeys(AnonymousClass1 r1) {
                this();
            }
        }

        private enum PkgType {
            NoPkg,
            DailyPkg,
            AddPkg,
            LeisurePkg,
            OldPkg,
            BillPkg,
            CallTimePkg
        }

        static {
            sPkgKeyTypeMap.put(DAILY_TOTAL, PkgType.DailyPkg);
            sPkgKeyTypeMap.put(DAILY_USED, PkgType.DailyPkg);
            sPkgKeyTypeMap.put(DAILY_REMAINED, PkgType.DailyPkg);
            sPkgKeyTypeMap.put(DAILY_EXCEED, PkgType.DailyPkg);
            sPkgKeyTypeMap.put(ADD_TOTAL, PkgType.AddPkg);
            sPkgKeyTypeMap.put(ADD_USED, PkgType.AddPkg);
            sPkgKeyTypeMap.put(ADD_REMAINED, PkgType.AddPkg);
            sPkgKeyTypeMap.put(ADD_EXCEEDD, PkgType.AddPkg);
            sPkgKeyTypeMap.put(LEISURE_TOTAL, PkgType.LeisurePkg);
            sPkgKeyTypeMap.put(LEISURE_USED, PkgType.LeisurePkg);
            sPkgKeyTypeMap.put(LEISURE_REMAINED, PkgType.LeisurePkg);
            sPkgKeyTypeMap.put(LEISURE_EXCEED, PkgType.LeisurePkg);
            sPkgKeyTypeMap.put(OLD_USED, PkgType.OldPkg);
            sPkgKeyTypeMap.put(OLD_REMAINED, PkgType.OldPkg);
            sPkgKeyTypeMap.put(BILL_TOTAL, PkgType.BillPkg);
            sPkgKeyTypeMap.put(BILL_USED, PkgType.BillPkg);
            sPkgKeyTypeMap.put(BILL_REMAINED, PkgType.BillPkg);
            sPkgKeyTypeMap.put(BILL_EXCEED, PkgType.BillPkg);
            sPkgKeyTypeMap.put(CALLTIME_TOTAL, PkgType.CallTimePkg);
            sPkgKeyTypeMap.put(CALLTIME_USED, PkgType.CallTimePkg);
            sPkgKeyTypeMap.put(CALLTIME_REMAINED, PkgType.CallTimePkg);
            sPkgKeyTypeMap.put(CALLTIME_EXCEED, PkgType.CallTimePkg);
        }

        public PackageDetail(JSONObject jSONObject) {
            parse(jSONObject);
        }

        private void checkPkgType(JSONObject jSONObject) {
            Iterator<String> keys = jSONObject.keys();
            if (keys != null && keys.hasNext()) {
                this.mPkgType = sPkgKeyTypeMap.get(keys.next());
            }
            if (this.mPkgType == null) {
                this.mPkgType = PkgType.NoPkg;
            }
        }

        private String getTrafficStr(long j) {
            return j >= 0 ? String.valueOf(j) : "NULL";
        }

        /* access modifiers changed from: private */
        public void merge(PackageDetail packageDetail) {
            boolean isStable = packageDetail.isStable();
            if (!this.mIsStable || !isStable) {
                boolean z = false;
                this.mIsStable = false;
                if (this.mHasUsed && packageDetail.mHasUsed) {
                    this.mUsedTrafficB += packageDetail.mUsedTrafficB;
                } else if (this.mHasUsed || packageDetail.mHasUsed) {
                    this.mUsedTrafficB = this.mHasUsed ? this.mUsedTrafficB : packageDetail.mUsedTrafficB;
                    this.mHasUsed = true;
                }
                if (this.mHasRemain && packageDetail.mHasRemain) {
                    this.mRemainTrafficB += packageDetail.mRemainTrafficB;
                } else if (this.mHasRemain || packageDetail.mHasRemain) {
                    this.mRemainTrafficB = this.mHasRemain ? this.mRemainTrafficB : packageDetail.mRemainTrafficB;
                    this.mHasRemain = true;
                }
                if (this.mHasUsed && this.mHasRemain) {
                    z = true;
                }
                this.mIsStable = z;
                if (this.mIsStable) {
                    this.mTotalTrafficB = this.mRemainTrafficB + this.mUsedTrafficB;
                    return;
                }
                return;
            }
            this.mTotalTrafficB += packageDetail.mTotalTrafficB;
            this.mUsedTrafficB += packageDetail.mUsedTrafficB;
            this.mRemainTrafficB += packageDetail.mRemainTrafficB;
        }

        private void parse(JSONObject jSONObject) {
            PkgKeys pkgKeys;
            if (jSONObject != null) {
                checkPkgType(jSONObject);
                switch (AnonymousClass1.$SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType[this.mPkgType.ordinal()]) {
                    case 1:
                        pkgKeys = new DailyPkgKeys();
                        break;
                    case 2:
                        pkgKeys = new OldKeys();
                        break;
                    case 3:
                        pkgKeys = new AddPkgKeys();
                        break;
                    case 4:
                        pkgKeys = new LeisureKeys();
                        break;
                    case 5:
                        pkgKeys = new BillKeys();
                        break;
                    case 6:
                        pkgKeys = new CallTimeKeys();
                        break;
                    default:
                        return;
                }
                String optString = jSONObject.optString(pkgKeys.mTotalKey);
                if (!TextUtils.isEmpty(optString)) {
                    this.mTotalTrafficB = Long.parseLong(optString);
                    jSONObject.remove(pkgKeys.mTotalKey);
                    this.mHasTotal = true;
                }
                String optString2 = jSONObject.optString(pkgKeys.mUsedKey);
                boolean z = false;
                if (!TextUtils.isEmpty(optString2)) {
                    this.mUsedTrafficB = Long.parseLong(optString2);
                    jSONObject.remove(pkgKeys.mUsedKey);
                    this.mHasUsed = true;
                    if (this.mUsedTrafficB == -2) {
                        this.mHasUsed = false;
                        if (this.mHasTotal) {
                            this.mUsedTrafficB = this.mTotalTrafficB;
                            this.mHasUsed = true;
                            this.mIsJustOver = true;
                        }
                    }
                }
                String optString3 = jSONObject.optString(pkgKeys.mRemainKey);
                if (!TextUtils.isEmpty(optString3)) {
                    this.mRemainTrafficB = Long.parseLong(optString3);
                    jSONObject.remove(pkgKeys.mRemainKey);
                    this.mHasRemain = true;
                    if (this.mHasTotal && !this.mHasUsed) {
                        this.mUsedTrafficB = this.mTotalTrafficB - this.mRemainTrafficB;
                        this.mHasUsed = true;
                    } else if (!this.mHasTotal && this.mHasUsed) {
                        this.mTotalTrafficB = this.mUsedTrafficB + this.mRemainTrafficB;
                        this.mHasTotal = true;
                    }
                }
                String optString4 = jSONObject.optString(pkgKeys.mExceedKey);
                if (!TextUtils.isEmpty(optString4)) {
                    long parseLong = Long.parseLong(optString4);
                    jSONObject.remove(pkgKeys.mExceedKey);
                    if (this.mRemainTrafficB <= 0 && parseLong > 0) {
                        this.mHasRemain = true;
                        this.mRemainTrafficB = -parseLong;
                        if (this.mHasTotal && !this.mHasUsed) {
                            this.mUsedTrafficB = this.mTotalTrafficB - this.mRemainTrafficB;
                            this.mHasUsed = true;
                        } else if (!this.mHasTotal && this.mHasUsed) {
                            this.mTotalTrafficB = this.mUsedTrafficB + this.mRemainTrafficB;
                            this.mHasTotal = true;
                        }
                    }
                }
                if (this.mHasTotal && this.mHasUsed) {
                    z = true;
                }
                this.mIsStable = z;
                if (this.mIsStable && !this.mHasRemain) {
                    this.mRemainTrafficB = this.mTotalTrafficB - this.mUsedTrafficB;
                    this.mHasRemain = true;
                }
            }
        }

        public String getDetailString() {
            StringBuilder sb = new StringBuilder();
            sb.append("是否完整:");
            sb.append(this.mIsStable ? "是" : "否");
            sb.append(";套餐总量:");
            sb.append(getTrafficStr(this.mTotalTrafficB));
            sb.append(";已使用:");
            sb.append(getTrafficStr(this.mUsedTrafficB));
            sb.append(";剩余:");
            sb.append(getTrafficStr(this.mRemainTrafficB));
            return sb.toString();
        }

        public long getRemainTrafficB() {
            return this.mRemainTrafficB;
        }

        public long getTotalTrafficB() {
            return this.mTotalTrafficB;
        }

        public PkgType getType() {
            return this.mPkgType;
        }

        public long getUsedTrafficB() {
            return this.mUsedTrafficB;
        }

        public boolean hasRemain() {
            return this.mHasRemain;
        }

        public boolean hasTotal() {
            return this.mHasTotal;
        }

        public boolean hasUsed() {
            return this.mHasUsed;
        }

        public boolean hasValue() {
            return this.mHasTotal || this.mHasUsed || this.mHasRemain;
        }

        public boolean isJustOver() {
            return this.mIsJustOver;
        }

        public boolean isStable() {
            return this.mIsStable;
        }

        public String toString() {
            return "mIsStable:" + this.mIsStable + ";mTotalTrafficB:" + this.mTotalTrafficB + ";mUsedTrafficB:" + this.mUsedTrafficB + ";mRemainTrafficB:" + this.mRemainTrafficB;
        }
    }

    public DataUsage(int i) {
        this.mReturnCode = i;
    }

    public DataUsage(String str) {
        parse(str);
    }

    private void addPackage(PackageDetail packageDetail) {
        PackageDetail packageDetail2;
        switch (AnonymousClass1.$SwitchMap$com$miui$sdk$tc$DataUsage$PackageDetail$PkgType[packageDetail.getType().ordinal()]) {
            case 1:
            case 2:
                packageDetail2 = this.mDailyPkg;
                if (packageDetail2 == null) {
                    this.mDailyPkg = packageDetail;
                    return;
                }
                break;
            case 3:
                packageDetail2 = this.mExtraPkg;
                if (packageDetail2 == null) {
                    this.mExtraPkg = packageDetail;
                    return;
                }
                break;
            case 4:
                packageDetail2 = this.mLeisurePkg;
                if (packageDetail2 == null) {
                    this.mLeisurePkg = packageDetail;
                    return;
                }
                break;
            case 5:
                packageDetail2 = this.mBillPkg;
                if (packageDetail2 == null) {
                    this.mBillPkg = packageDetail;
                    return;
                }
                break;
            case 6:
                packageDetail2 = this.mCallTimePkg;
                if (packageDetail2 == null) {
                    this.mCallTimePkg = packageDetail;
                    return;
                }
                break;
            default:
                return;
        }
        packageDetail2.merge(packageDetail);
    }

    private boolean isReturnError() {
        return this.mDailyPkg == null && this.mBillPkg == null && this.mCallTimePkg == null;
    }

    private JSONObject mergeSameKeys(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        JSONObject jSONObject2 = new JSONObject();
        Iterator<String> keys = jSONObject.keys();
        while (keys != null && keys.hasNext()) {
            String next = keys.next();
            String replaceFirst = next.replaceFirst("@.*", "");
            try {
                jSONObject2.put(replaceFirst, jSONObject2.optLong(replaceFirst) + jSONObject.optLong(next));
            } catch (JSONException e) {
                Log.i(TAG, "mergeSameKeys exception", e);
            }
        }
        return jSONObject2;
    }

    public PackageDetail getBillPkg() {
        return this.mBillPkg;
    }

    public PackageDetail getCallTimePkg() {
        return this.mCallTimePkg;
    }

    public PackageDetail getDailyPkgDetail() {
        return this.mDailyPkg;
    }

    public String getDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("解析状态:");
        sb.append(this.mReturnCode == 0 ? "成功" : "失败");
        if (this.mDailyPkg != null) {
            sb.append("\n日常套餐:\n");
            sb.append(this.mDailyPkg.getDetailString());
        }
        if (this.mExtraPkg != null) {
            sb.append("\n叠加包:\n");
            sb.append(this.mExtraPkg.getDetailString());
        }
        if (this.mLeisurePkg != null) {
            sb.append("\n闲时套餐:\n");
            sb.append(this.mLeisurePkg.getDetailString());
        }
        if (this.mBillPkg != null) {
            sb.append("\n话费账单:\n");
            sb.append(this.mBillPkg.getDetailString());
        }
        if (this.mCallTimePkg != null) {
            sb.append("\n通话套餐:\n");
            sb.append(this.mCallTimePkg.getDetailString());
        }
        return sb.toString();
    }

    public PackageDetail getExtraPkgDetail() {
        return this.mExtraPkg;
    }

    public PackageDetail getLeisurePkgDetail() {
        return this.mLeisurePkg;
    }

    public int getReturnCode() {
        return this.mReturnCode;
    }

    public void parse(String str) {
        try {
            JSONArray jSONArray = new JSONArray(str);
            if (jSONArray.length() > 0) {
                this.mReturnCode = 0;
                for (int i = 0; i < jSONArray.length(); i++) {
                    try {
                        JSONObject mergeSameKeys = mergeSameKeys(jSONArray.getJSONObject(i));
                        for (int i2 = 0; i2 < 5 && mergeSameKeys.length() > 0; i2++) {
                            PackageDetail packageDetail = new PackageDetail(mergeSameKeys);
                            if (packageDetail.hasValue()) {
                                addPackage(packageDetail);
                            }
                        }
                    } catch (JSONException e) {
                        Log.i(TAG, "merge package exception", e);
                    }
                }
            }
        } catch (JSONException e2) {
            Log.i(TAG, "parse result exception", e2);
        }
        if (isReturnError()) {
            this.mReturnCode = 1;
        } else {
            this.mReturnCode = 0;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mReturnCode:");
        sb.append(this.mReturnCode);
        if (this.mDailyPkg != null) {
            sb.append("\nmDailyPkg:\n");
            sb.append(this.mDailyPkg.toString());
        }
        if (this.mExtraPkg != null) {
            sb.append("\nmExtraPkg:\n");
            sb.append(this.mExtraPkg.toString());
        }
        if (this.mLeisurePkg != null) {
            sb.append("\nmLeisurePkg:\n");
            sb.append(this.mLeisurePkg.toString());
        }
        if (this.mBillPkg != null) {
            sb.append("\nmBillPkg:\n");
            sb.append(this.mBillPkg.toString());
        }
        if (this.mCallTimePkg != null) {
            sb.append("\nmCallTimePkg:\n");
            sb.append(this.mCallTimePkg.toString());
        }
        return sb.toString();
    }
}
