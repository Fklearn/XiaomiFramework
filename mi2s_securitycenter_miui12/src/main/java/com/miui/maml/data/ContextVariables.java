package com.miui.maml.data;

import android.graphics.Bitmap;
import java.util.HashMap;

public class ContextVariables {
    private HashMap<String, Object> mMap = new HashMap<>();

    public void clear() {
        this.mMap.clear();
    }

    public Bitmap getBmp(String str) {
        Object obj = this.mMap.get(str);
        if (obj != null && (obj instanceof Bitmap)) {
            return (Bitmap) obj;
        }
        return null;
    }

    public Double getDouble(String str) {
        Object obj = this.mMap.get(str);
        if (obj != null && (obj instanceof Double)) {
            return (Double) obj;
        }
        return null;
    }

    public Integer getInt(String str) {
        Object obj = this.mMap.get(str);
        if (obj != null && (obj instanceof Integer)) {
            return (Integer) obj;
        }
        return null;
    }

    public Long getLong(String str) {
        Object obj = this.mMap.get(str);
        if (obj != null && (obj instanceof Long)) {
            return (Long) obj;
        }
        return null;
    }

    public String getString(String str) {
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        return !(obj instanceof String) ? String.valueOf(obj) : (String) obj;
    }

    public Object getVar(String str) {
        return this.mMap.get(str);
    }

    public void setVar(String str, Object obj) {
        this.mMap.put(str, obj);
    }
}
