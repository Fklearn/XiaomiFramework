package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Horizontal implements Parcelable {
    public static final Parcelable.Creator<Horizontal> CREATOR = new k();
    private int positionIndex;
    private int templateType;
    private List<VerticalInRow> verticalInRow = new ArrayList();

    public Horizontal() {
    }

    protected Horizontal(Parcel parcel) {
        this.positionIndex = parcel.readInt();
        parcel.readTypedList(this.verticalInRow, VerticalInRow.CREATOR);
        this.templateType = parcel.readInt();
    }

    public static Horizontal parseFromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        try {
            Horizontal horizontal = new Horizontal();
            if (jSONObject.has("positionIndex")) {
                horizontal.positionIndex = jSONObject.optInt("positionIndex");
            }
            if (jSONObject.has("templateType")) {
                horizontal.templateType = jSONObject.optInt("templateType");
            }
            if (jSONObject.has("verticalInRow")) {
                JSONArray optJSONArray = jSONObject.optJSONArray("verticalInRow");
                for (int i = 0; i < optJSONArray.length(); i++) {
                    VerticalInRow parseFromJson = VerticalInRow.parseFromJson(optJSONArray.getJSONObject(i));
                    if (parseFromJson != null) {
                        horizontal.verticalInRow.add(parseFromJson);
                    }
                }
            }
            if (horizontal.verticalInRow.isEmpty()) {
                return null;
            }
            return horizontal;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public int getPositionIndex() {
        return this.positionIndex;
    }

    public int getTemplateType() {
        return this.templateType;
    }

    public List<VerticalInRow> getVerticalInRows() {
        return this.verticalInRow;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.positionIndex);
        parcel.writeTypedList(this.verticalInRow);
        parcel.writeInt(this.templateType);
    }
}
