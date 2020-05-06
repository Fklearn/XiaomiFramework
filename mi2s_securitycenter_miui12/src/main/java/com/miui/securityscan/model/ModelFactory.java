package com.miui.securityscan.model;

import android.content.Context;
import android.util.Log;
import b.b.c.j.C0196c;
import b.b.c.j.i;
import com.miui.securityscan.M;
import com.miui.securityscan.i.m;
import com.xiaomi.stat.MiStat;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.CloudPushConstants;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelFactory {
    private static final String TAG = "ModelFactory";

    private static Constructor<?> createConstructor(String str, Class<?>... clsArr) {
        try {
            return Class.forName(str).getConstructor(clsArr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private static List<AbsModel> createFirstAidKitGroupModelListFromAsset(Context context, String str) {
        String firstAidKitScanItemJSONStr = getFirstAidKitScanItemJSONStr(context);
        if (firstAidKitScanItemJSONStr == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        JSONArray jSONArray = new JSONObject(firstAidKitScanItemJSONStr).getJSONArray(str);
        String b2 = i.b();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            String optString = jSONObject.optString("key");
            String optString2 = jSONObject.optString(CloudPushConstants.XML_NAME);
            int optInt = jSONObject.optInt(MiStat.Param.SCORE);
            if (jsonArrContains(jSONObject.getJSONArray("version"), b2) && !m.c(optString)) {
                AbsModel absModel = (AbsModel) createNewInstance(createConstructor(optString2, String.class, Integer.class), optString, Integer.valueOf(optInt));
                if (absModel != null) {
                    arrayList.add(absModel);
                }
            }
        }
        return arrayList;
    }

    private static List<GroupModel> createGroupModelListFromAsset(Context context, String str) {
        String scanItemJSONStr = getScanItemJSONStr(context);
        if (scanItemJSONStr == null) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(scanItemJSONStr);
        JSONArray jSONArray = jSONObject.getJSONArray(str);
        int i = jSONObject.getInt("version");
        Log.i(TAG, String.format("ScanItem typeName : %s, version : %d", new Object[]{str, Integer.valueOf(i)}));
        ArrayList arrayList = new ArrayList();
        String b2 = i.b();
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            GroupModel jsonArrToModelList = jsonArrToModelList(jSONArray.getJSONArray(i2), b2);
            if (jsonArrToModelList != null && !jsonArrToModelList.isGroupEmpty()) {
                arrayList.add(jsonArrToModelList);
            }
        }
        return arrayList;
    }

    private static Object createNewInstance(Constructor<?> constructor, Object... objArr) {
        if (constructor == null || !isValidTypeMappingValue(constructor.getParameterTypes(), objArr)) {
            return null;
        }
        try {
            return constructor.newInstance(objArr);
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return null;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return null;
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
            return null;
        }
    }

    protected static String getFirstAidKitScanItemJSONStr(Context context) {
        return C0196c.a(context, "firstaid_scanitem.json");
    }

    protected static String getScanItemJSONStr(Context context) {
        boolean h = M.h();
        String strFromDataDir = getStrFromDataDir(context, "scanitem.json_v" + (h ? 1 : 0));
        if (strFromDataDir != null) {
            return strFromDataDir;
        }
        Log.i(TAG, "read scan item from assets");
        return C0196c.a(context, "scanitem.json");
    }

    private static String getStrFromDataDir(Context context, String str) {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        try {
            if (new File(context.getFilesDir(), str).exists()) {
                fileInputStream = context.openFileInput(str);
                try {
                    String iOUtils = IOUtils.toString(fileInputStream);
                    IOUtils.closeQuietly(fileInputStream);
                    return iOUtils;
                } catch (Exception e) {
                    e = e;
                    try {
                        e.printStackTrace();
                        IOUtils.closeQuietly(fileInputStream);
                        return null;
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream2 = fileInputStream;
                        IOUtils.closeQuietly(fileInputStream2);
                        throw th;
                    }
                }
            } else {
                IOUtils.closeQuietly((InputStream) null);
                return null;
            }
        } catch (Exception e2) {
            e = e2;
            fileInputStream = null;
            e.printStackTrace();
            IOUtils.closeQuietly(fileInputStream);
            return null;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(fileInputStream2);
            throw th;
        }
    }

    private static boolean isValidTypeMappingValue(Class<?>[] clsArr, Object[] objArr) {
        if (!((clsArr == null && objArr == null) || clsArr == null || objArr == null)) {
            if (clsArr.length != objArr.length) {
                return false;
            }
            for (int i = 0; i < clsArr.length; i++) {
                if (!clsArr[i].equals(objArr[i].getClass())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean jsonArrContains(JSONArray jSONArray, Object obj) {
        for (int i = 0; i < jSONArray.length(); i++) {
            if (jSONArray.get(i).equals(obj)) {
                return true;
            }
        }
        return false;
    }

    private static GroupModel jsonArrToModelList(JSONArray jSONArray, String str) {
        GroupModel groupModel = new GroupModel();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            String optString = jSONObject.optString("key");
            String optString2 = jSONObject.optString(CloudPushConstants.XML_NAME);
            int optInt = jSONObject.optInt(MiStat.Param.SCORE);
            if (jsonArrContains(jSONObject.getJSONArray("version"), str)) {
                AbsModel absModel = (AbsModel) createNewInstance(createConstructor(optString2, String.class, Integer.class), optString, Integer.valueOf(optInt));
                if (absModel != null) {
                    groupModel.addModel(absModel);
                }
            }
        }
        return groupModel;
    }

    public static List<AbsModel> produceFirstAidKitGroupModel(Context context) {
        try {
            String[] strArr = {"Performance", "Internet", "Operation", "ConsumePower", "Other"};
            String firstAidKitScanItemJSONStr = getFirstAidKitScanItemJSONStr(context);
            if (firstAidKitScanItemJSONStr == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            String b2 = i.b();
            JSONObject jSONObject = new JSONObject(firstAidKitScanItemJSONStr);
            for (String jSONArray : strArr) {
                JSONArray jSONArray2 = jSONObject.getJSONArray(jSONArray);
                for (int i = 0; i < jSONArray2.length(); i++) {
                    JSONObject jSONObject2 = jSONArray2.getJSONObject(i);
                    String optString = jSONObject2.optString("key");
                    String optString2 = jSONObject2.optString(CloudPushConstants.XML_NAME);
                    int optInt = jSONObject2.optInt(MiStat.Param.SCORE);
                    if (jsonArrContains(jSONObject2.getJSONArray("version"), b2)) {
                        AbsModel absModel = (AbsModel) createNewInstance(createConstructor(optString2, String.class, Integer.class), optString, Integer.valueOf(optInt));
                        if (absModel != null) {
                            arrayList.add(absModel);
                        }
                    }
                }
            }
            return arrayList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<AbsModel> produceFirstAidKitGroupModel(Context context, String str) {
        try {
            return createFirstAidKitGroupModelListFromAsset(context, str);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<GroupModel> produceManualGroupModel(Context context) {
        try {
            return createGroupModelListFromAsset(context, "manual");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<GroupModel> produceSystemGroupModel(Context context) {
        try {
            return createGroupModelListFromAsset(context, "system");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
