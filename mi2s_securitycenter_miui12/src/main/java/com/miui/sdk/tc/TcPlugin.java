package com.miui.sdk.tc;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class TcPlugin {
    public static native LinkedHashMap<String, String> getBrandsMap(String str, boolean z);

    public static native LinkedHashMap<String, String> getCarriesMap(boolean z);

    public static native TreeMap<Integer, String> getCitiesMap(int i);

    public static native ArrayList<TcDirection> getInstructions(int i);

    public static native int getProvinceCodeByCityCode(int i);

    public static native TreeMap<Integer, String> getProvincesMap(boolean z);

    public static native int getResult(String str, String str2, HashMap<String, String> hashMap, int i);

    public static native int getResultByTcType(String str, String str2, HashMap<String, String> hashMap, int i, int i2);

    public static native int init(Context context, String str, String str2);

    public static native int setImsi(String str, int i);

    public static native int update(String str, String str2, String str3, int i);

    public static native int updateByTcType(String str, String str2, String str3, int i, int i2);
}
