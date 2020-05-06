package com.miui.powercenter.autotask;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.cloud.CloudPushConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class AutoTask implements Parcelable {
    public static final String AUTHORITY = "com.miui.powercenter.autotask";
    public static final Uri CONTENT_URI = Uri.parse("content://com.miui.powercenter.autotask/autotasks");
    public static final Parcelable.Creator<AutoTask> CREATOR = new C0474c();
    public static final String DATABASE_NAME = "power_auto_task.db";
    public static int GPS_OFF = 0;
    public static final int GPS_ON = 3;
    public static final int KEEP_STATE = 2;
    public static final String[] QUERY_COLUMNS = {"_id", "enabled", CloudPushConstants.XML_NAME, "condition", "operation", "repeat_type", "task_started", "restore_operation", "restore_level"};
    public static final int RESTORE_WHEN_CHARGED = 1;
    public static final int SWITCH_OFF = 0;
    public static final int SWITCH_ON = 1;
    public static final String TABLE_NAME = "autotasks";
    private static final String TAG = "autotask";
    private JSONObject mConditions;
    private boolean mEnabled;
    private long mId;
    private String mName;
    private JSONObject mOperations;
    private int mRepeatType;
    private int mRestoreLevel;
    private JSONObject mRestoreOperations;
    private boolean mStarted;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public int f6673a;

        /* renamed from: b  reason: collision with root package name */
        public int f6674b;
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public int f6675a;

        /* renamed from: b  reason: collision with root package name */
        public int f6676b;

        public b(int i) {
            a(i);
        }

        public b(int i, int i2) {
            this.f6675a = i;
            this.f6676b = i2;
        }

        public int a() {
            return (this.f6675a << 16) + this.f6676b;
        }

        public void a(int i) {
            this.f6675a = i >> 16;
            this.f6676b = i & 65535;
        }
    }

    static {
        int i = 2;
        GPS_OFF = 2;
        if (Build.VERSION.SDK_INT > 28) {
            i = 0;
        }
        GPS_OFF = i;
    }

    public AutoTask() {
        this.mId = -1;
        this.mEnabled = false;
        this.mRepeatType = 127;
        this.mStarted = false;
        this.mRestoreLevel = 0;
        init(-1, false, "", "{}", "{}", 127, false, "{}", 1);
    }

    public AutoTask(Cursor cursor) {
        this.mId = -1;
        this.mEnabled = false;
        this.mRepeatType = 127;
        this.mStarted = false;
        this.mRestoreLevel = 0;
        long j = cursor.getLong(cursor.getColumnIndex("_id"));
        boolean z = cursor.getInt(cursor.getColumnIndex("enabled")) != 0;
        String string = cursor.getString(cursor.getColumnIndex(CloudPushConstants.XML_NAME));
        String string2 = cursor.getString(cursor.getColumnIndex("condition"));
        String string3 = cursor.getString(cursor.getColumnIndex("operation"));
        int i = cursor.getInt(cursor.getColumnIndex("repeat_type"));
        boolean z2 = cursor.getInt(cursor.getColumnIndex("task_started")) != 0;
        String string4 = cursor.getString(cursor.getColumnIndex("restore_operation"));
        init(j, z, string == null ? "" : string, TextUtils.isEmpty(string2) ? "{}" : string2, TextUtils.isEmpty(string3) ? "{}" : string3, i, z2, TextUtils.isEmpty(string4) ? "{}" : string4, cursor.getInt(cursor.getColumnIndex("restore_level")));
    }

    protected AutoTask(Parcel parcel) {
        this.mId = -1;
        boolean z = false;
        this.mEnabled = false;
        this.mRepeatType = 127;
        this.mStarted = false;
        this.mRestoreLevel = 0;
        this.mId = parcel.readLong();
        this.mEnabled = parcel.readByte() != 0;
        this.mName = parcel.readString();
        try {
            this.mConditions = new JSONObject(parcel.readString());
            this.mOperations = new JSONObject(parcel.readString());
            this.mRepeatType = parcel.readInt();
            this.mStarted = parcel.readByte() != 0 ? true : z;
            this.mRestoreOperations = new JSONObject(parcel.readString());
            this.mRestoreLevel = parcel.readInt();
        } catch (JSONException e) {
            Log.e(TAG, "Parcel", e);
        }
    }

    public AutoTask(AutoTask autoTask) {
        this.mId = -1;
        this.mEnabled = false;
        this.mRepeatType = 127;
        this.mStarted = false;
        this.mRestoreLevel = 0;
        init(autoTask.getId(), autoTask.getEnabled(), autoTask.getName(), autoTask.getConditionString(), autoTask.getOperationString(), autoTask.getRepeatType(), autoTask.getStarted(), autoTask.getRestoreOperationString(), autoTask.getRestoreLevel());
    }

    public AutoTask(JSONObject jSONObject) {
        this.mId = -1;
        boolean z = false;
        this.mEnabled = false;
        this.mRepeatType = 127;
        this.mStarted = false;
        this.mRestoreLevel = 0;
        boolean optBoolean = jSONObject.optBoolean("enabled");
        String optString = jSONObject.optString(CloudPushConstants.XML_NAME);
        String optString2 = jSONObject.optString("condition");
        String optString3 = jSONObject.optString("operation");
        int optInt = jSONObject.optInt("repeat_type");
        boolean z2 = jSONObject.optInt("task_started") != 0 ? true : z;
        String optString4 = jSONObject.optString("restore_operation");
        init(-1, optBoolean, optString == null ? "" : optString, TextUtils.isEmpty(optString2) ? "{}" : optString2, TextUtils.isEmpty(optString3) ? "{}" : optString3, optInt, z2, TextUtils.isEmpty(optString4) ? "{}" : optString4, jSONObject.optInt("restore_level"));
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x001a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean compareJsonObject(org.json.JSONObject r9, org.json.JSONObject r10) {
        /*
            r8 = this;
            r0 = 0
            if (r9 == 0) goto L_0x0061
            if (r10 != 0) goto L_0x0006
            goto L_0x0061
        L_0x0006:
            java.util.Iterator r1 = r9.keys()
            java.util.Iterator r2 = r10.keys()
        L_0x000e:
            boolean r3 = r1.hasNext()
            if (r3 == 0) goto L_0x0052
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0052
            java.lang.Object r3 = r1.next()
            java.lang.String r3 = (java.lang.String) r3
            boolean r4 = r10.has(r3)
            if (r4 != 0) goto L_0x0027
            return r0
        L_0x0027:
            java.lang.Object r4 = r2.next()
            java.lang.String r4 = (java.lang.String) r4
            boolean r4 = r9.has(r4)
            if (r4 != 0) goto L_0x0034
            return r0
        L_0x0034:
            r4 = 0
            java.lang.Object r5 = r9.get(r3)     // Catch:{ JSONException -> 0x0040 }
            java.lang.Object r4 = r10.get(r3)     // Catch:{ JSONException -> 0x003e }
            goto L_0x0049
        L_0x003e:
            r3 = move-exception
            goto L_0x0042
        L_0x0040:
            r3 = move-exception
            r5 = r4
        L_0x0042:
            java.lang.String r6 = "autotask"
            java.lang.String r7 = "compareJsonObject"
            android.util.Log.e(r6, r7, r3)
        L_0x0049:
            if (r5 == 0) goto L_0x0051
            boolean r3 = r5.equals(r4)
            if (r3 != 0) goto L_0x000e
        L_0x0051:
            return r0
        L_0x0052:
            boolean r9 = r1.hasNext()
            if (r9 != 0) goto L_0x0061
            boolean r9 = r2.hasNext()
            if (r9 == 0) goto L_0x005f
            goto L_0x0061
        L_0x005f:
            r9 = 1
            return r9
        L_0x0061:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.autotask.AutoTask.compareJsonObject(org.json.JSONObject, org.json.JSONObject):boolean");
    }

    public static a getHourMinute(int i) {
        a aVar = new a();
        aVar.f6673a = i / 60;
        aVar.f6674b = i % 60;
        return aVar;
    }

    public static List<AutoTask> getInitAutoTaskList() {
        ArrayList arrayList = new ArrayList();
        AutoTask autoTask = new AutoTask();
        autoTask.setCondition("hour_minute_duration", Integer.valueOf(new b(0, 420).a()));
        autoTask.setOperation("airplane_mode", 1);
        arrayList.add(autoTask);
        AutoTask autoTask2 = new AutoTask();
        autoTask2.setCondition("battery_level_down", 20);
        autoTask2.setOperation("mute", 1);
        autoTask2.setOperation("brightness", 0);
        autoTask2.setOperation("gps", Integer.valueOf(GPS_OFF));
        autoTask2.setOperation("synchronization", 0);
        autoTask2.setOperation("bluetooth", 0);
        arrayList.add(autoTask2);
        return arrayList;
    }

    private List<String> getKeys(JSONObject jSONObject) {
        Iterator<String> keys = jSONObject.keys();
        ArrayList arrayList = new ArrayList();
        while (keys.hasNext()) {
            arrayList.add(keys.next());
        }
        return arrayList;
    }

    private void init(long j, boolean z, String str, String str2, String str3, int i, boolean z2, String str4, int i2) {
        try {
            this.mConditions = new JSONObject(str2);
            this.mOperations = new JSONObject(str3);
            this.mId = j;
            this.mEnabled = z;
            this.mName = str;
            this.mRepeatType = i;
            this.mStarted = z2;
            this.mRestoreOperations = new JSONObject(str4);
            this.mRestoreLevel = i2;
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
    }

    public boolean conditionsEquals(AutoTask autoTask) {
        return compareJsonObject(this.mConditions, autoTask.mConditions);
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return (obj instanceof AutoTask) && this.mId == ((AutoTask) obj).mId;
    }

    public <T> T getCondition(String str) {
        try {
            return this.mConditions.get(str);
        } catch (ClassCastException | JSONException unused) {
            return null;
        }
    }

    public List<String> getConditionNames() {
        return getKeys(this.mConditions);
    }

    public String getConditionString() {
        return this.mConditions.toString();
    }

    public boolean getEnabled() {
        return this.mEnabled;
    }

    public long getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public <T> T getOperation(String str) {
        try {
            return this.mOperations.get(str);
        } catch (ClassCastException | JSONException unused) {
            return null;
        }
    }

    public List<String> getOperationNames() {
        return getKeys(this.mOperations);
    }

    public String getOperationString() {
        return this.mOperations.toString();
    }

    public int getRepeatType() {
        return this.mRepeatType;
    }

    public int getRestoreLevel() {
        return this.mRestoreLevel;
    }

    public <T> T getRestoreOperation(String str) {
        try {
            return this.mRestoreOperations.get(str);
        } catch (ClassCastException | JSONException unused) {
            return null;
        }
    }

    public List<String> getRestoreOperationNames() {
        return getKeys(this.mRestoreOperations);
    }

    public String getRestoreOperationString() {
        return this.mRestoreOperations.toString();
    }

    public boolean getStarted() {
        return this.mStarted;
    }

    public boolean hasCondition(String str) {
        return this.mConditions.has(str);
    }

    public boolean hasOperation(String str) {
        return this.mOperations.has(str);
    }

    public boolean isConditionEmpty() {
        return getConditionNames().isEmpty();
    }

    public boolean isOperationEmpty() {
        return getOperationNames().isEmpty();
    }

    public boolean isPeriodTask() {
        return hasCondition("hour_minute_duration") || (hasCondition("battery_level_down") && getRestoreLevel() != 0);
    }

    public boolean operationsEquals(AutoTask autoTask) {
        return compareJsonObject(this.mOperations, autoTask.mOperations);
    }

    public void removeAllConditions() {
        for (String removeCondition : getConditionNames()) {
            removeCondition(removeCondition);
        }
    }

    public void removeAllOperations() {
        for (String removeOperation : getOperationNames()) {
            removeOperation(removeOperation);
        }
    }

    public void removeAllRestoreOperation() {
        for (String remove : getRestoreOperationNames()) {
            this.mRestoreOperations.remove(remove);
        }
    }

    public void removeCondition(String str) {
        this.mConditions.remove(str);
    }

    public void removeOperation(String str) {
        this.mOperations.remove(str);
    }

    public void setCondition(String str, Object obj) {
        try {
            this.mConditions.putOpt(str, obj);
        } catch (JSONException e) {
            Log.e(TAG, "setCondition", e);
        }
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setOperation(String str, Object obj) {
        try {
            this.mOperations.putOpt(str, obj);
        } catch (JSONException e) {
            Log.e(TAG, "setOperation", e);
        }
    }

    public void setRepeatType(int i) {
        this.mRepeatType = i;
    }

    public void setRestoreLevel(int i) {
        this.mRestoreLevel = i;
    }

    public void setRestoreOperation(String str, Object obj) {
        try {
            this.mRestoreOperations.putOpt(str, obj);
        } catch (JSONException e) {
            Log.e(TAG, "setRestoreOperation", e);
        }
    }

    public void setStarted(boolean z) {
        this.mStarted = z;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("_id", getId());
            jSONObject.put("enabled", getEnabled());
            jSONObject.put(CloudPushConstants.XML_NAME, getName());
            jSONObject.put("condition", getConditionString());
            jSONObject.put("operation", getOperationString());
            jSONObject.put("repeat_type", getRepeatType());
            jSONObject.put("task_started", getStarted());
            jSONObject.put("restore_operation", getRestoreOperationString());
            jSONObject.put("restore_level", getRestoreLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mId);
        parcel.writeByte(this.mEnabled ? (byte) 1 : 0);
        parcel.writeString(this.mName);
        parcel.writeString(this.mConditions.toString());
        parcel.writeString(this.mOperations.toString());
        parcel.writeInt(this.mRepeatType);
        parcel.writeByte(this.mStarted ? (byte) 1 : 0);
        parcel.writeString(this.mRestoreOperations.toString());
        parcel.writeInt(this.mRestoreLevel);
    }
}
