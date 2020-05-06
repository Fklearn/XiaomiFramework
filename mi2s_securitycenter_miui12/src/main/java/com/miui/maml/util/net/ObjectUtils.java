package com.miui.maml.util.net;

import android.content.ContentValues;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ObjectUtils {
    private static Object convertObj(Object obj) {
        if (obj instanceof JSONObject) {
            return jsonToMap((JSONObject) obj);
        }
        if (obj instanceof JSONArray) {
            JSONArray jSONArray = (JSONArray) obj;
            int length = jSONArray.length();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < length; i++) {
                arrayList.add(convertObj(jSONArray.opt(i)));
            }
            return arrayList;
        } else if (obj == JSONObject.NULL) {
            return null;
        } else {
            return obj;
        }
    }

    public static Object convertObjectToJson(Object obj) {
        if (obj instanceof List) {
            JSONArray jSONArray = new JSONArray();
            for (Object convertObjectToJson : (List) obj) {
                jSONArray.put(convertObjectToJson(convertObjectToJson));
            }
            return jSONArray;
        } else if (!(obj instanceof Map)) {
            return obj;
        } else {
            JSONObject jSONObject = new JSONObject();
            Map map = (Map) obj;
            for (Object next : map.keySet()) {
                try {
                    jSONObject.put((String) next, convertObjectToJson(map.get(next)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jSONObject;
        }
    }

    public static String flattenMap(Map<?, ?> map) {
        if (map == null) {
            return "null";
        }
        Set<Map.Entry<?, ?>> entrySet = map.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry next : entrySet) {
            Object key = next.getKey();
            Object value = next.getValue();
            sb.append("(");
            sb.append(key);
            sb.append(",");
            sb.append(value);
            sb.append("),");
        }
        sb.append("}");
        return sb.toString();
    }

    public static Map<String, Object> jsonToMap(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            hashMap.put(next, convertObj(jSONObject.opt(next)));
        }
        return hashMap;
    }

    public static Map<String, String> listToMap(Map<String, List<String>> map) {
        HashMap hashMap = new HashMap();
        if (map != null) {
            for (Map.Entry next : map.entrySet()) {
                String str = (String) next.getKey();
                List list = (List) next.getValue();
                if (!(str == null || list == null || list.size() <= 0)) {
                    hashMap.put(str, list.get(0));
                }
            }
        }
        return hashMap;
    }

    public static ContentValues mapToPairs(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        for (Map.Entry next : map.entrySet()) {
            String str = (String) next.getKey();
            String str2 = (String) next.getValue();
            if (str2 == null) {
                str2 = "";
            }
            contentValues.put(str, str2);
        }
        return contentValues;
    }
}
