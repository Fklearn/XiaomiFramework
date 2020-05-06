package com.miui.systemAdSolution.common;

import android.text.TextUtils;
import android.util.Log;
import com.miui.systemAdSolution.common.Material;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdInfo {
    private static final String KEY_DIGEST = "digest";
    private static final String KEY_END_TIME_IN_MAILLS = "endTimeInMillis";
    private static final String KEY_EXTRA = "extra";
    private static final String KEY_ID = "id";
    private static final String KEY_MATERIALS = "materials";
    private static final String KEY_START_TIME_IN_MAILLS = "startTimeInMillis";
    private static final String KEY_TAG_ID = "tagId";
    private static final String TAG = "AdInfo";
    private String mDigest;
    private long mEndTimeInMillis;
    private String mExtra;
    private long mId;
    private List<Material> mMaterials;
    private long mStartTimeInMillis;
    private String mTagId;

    private AdInfo(JSONObject jSONObject) {
        this.mId = jSONObject.optLong("id");
        this.mTagId = jSONObject.optString(KEY_TAG_ID);
        this.mStartTimeInMillis = jSONObject.optLong(KEY_START_TIME_IN_MAILLS);
        this.mEndTimeInMillis = jSONObject.optLong(KEY_END_TIME_IN_MAILLS);
        this.mMaterials = parseMaterials(jSONObject.optJSONArray(KEY_MATERIALS));
        this.mDigest = jSONObject.optString(KEY_DIGEST);
        this.mExtra = jSONObject.optString(KEY_EXTRA);
    }

    public static AdInfo deserialize(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return valueOf(new JSONObject(str));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Material> parseMaterials(JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        if (jSONArray != null) {
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                Material parse = Material.parse(jSONArray.optJSONObject(i), true);
                if (parse != null) {
                    arrayList.add(parse);
                }
            }
        }
        return arrayList;
    }

    public static AdInfo valueOf(JSONObject jSONObject) {
        return new AdInfo(jSONObject);
    }

    public AdInfo clone() {
        return (AdInfo) super.clone();
    }

    public String getDigest() {
        return this.mDigest;
    }

    public long getEndTimeInMillis() {
        return this.mEndTimeInMillis;
    }

    public String getExtra() {
        return this.mExtra;
    }

    public long getId() {
        return this.mId;
    }

    public Material getMaterialById(long j) {
        List<Material> list = this.mMaterials;
        if (list != null && !list.isEmpty()) {
            for (Material next : this.mMaterials) {
                if (next != null && next.getId() == j) {
                    return next;
                }
            }
        }
        return null;
    }

    public List<Material> getMaterials() {
        return this.mMaterials;
    }

    public Material.Resource getResourceById(long j, long j2) {
        if (getMaterialById(j) == null) {
            return null;
        }
        return getResourceById(j, j2);
    }

    public Material.Resource getResourceById(Material material, long j) {
        List<Material.Resource> resources;
        if (!(material == null || (resources = material.getResources()) == null || resources.isEmpty())) {
            for (Material.Resource next : resources) {
                if (next.getId() == j) {
                    return next;
                }
            }
        }
        return null;
    }

    public long getStartTimeInMillis() {
        return this.mStartTimeInMillis;
    }

    public String getTagId() {
        return this.mTagId;
    }

    public boolean isInvalid() {
        long startTimeInMillis = getStartTimeInMillis();
        long endTimeInMillis = getEndTimeInMillis();
        long currentTimeMillis = System.currentTimeMillis();
        return startTimeInMillis > currentTimeMillis || currentTimeMillis > endTimeInMillis;
    }

    public String serialize() {
        JSONArray jSONArray = new JSONArray();
        List<Material> list = this.mMaterials;
        if (list != null) {
            for (Material serialize : list) {
                jSONArray.put(serialize.serialize());
            }
        }
        return serialize(jSONArray);
    }

    public String serialize(JSONArray jSONArray) {
        if (jSONArray == null) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("id", this.mId);
            jSONObject.put(KEY_TAG_ID, this.mTagId);
            jSONObject.put(KEY_START_TIME_IN_MAILLS, this.mStartTimeInMillis);
            jSONObject.put(KEY_END_TIME_IN_MAILLS, this.mEndTimeInMillis);
            jSONObject.put(KEY_MATERIALS, jSONArray);
            jSONObject.put(KEY_DIGEST, this.mDigest);
            jSONObject.put(KEY_EXTRA, this.mExtra);
            return jSONObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "parse failed!", e);
            return null;
        }
    }
}
