package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MixedContent implements Parcelable {
    public static final Parcelable.Creator<MixedContent> CREATOR = new o();
    private List<Horizontal> horizontals;

    public MixedContent() {
        this.horizontals = new ArrayList();
    }

    protected MixedContent(Parcel parcel) {
        this.horizontals = new ArrayList();
        this.horizontals = new ArrayList();
        parcel.readList(this.horizontals, Horizontal.class.getClassLoader());
    }

    public static MixedContent parseFromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        try {
            MixedContent mixedContent = new MixedContent();
            if (jSONObject.has("horizontal")) {
                JSONArray optJSONArray = jSONObject.optJSONArray("horizontal");
                for (int i = 0; i < optJSONArray.length(); i++) {
                    Horizontal parseFromJson = Horizontal.parseFromJson(optJSONArray.getJSONObject(i));
                    if (parseFromJson != null) {
                        mixedContent.horizontals.add(parseFromJson);
                    }
                }
            }
            if (mixedContent.horizontals.isEmpty()) {
                return null;
            }
            return mixedContent;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public List<Horizontal> getHorizontals() {
        return this.horizontals;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this.horizontals);
    }
}
