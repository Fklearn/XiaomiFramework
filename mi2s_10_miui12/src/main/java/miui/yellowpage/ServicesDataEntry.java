package miui.yellowpage;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServicesDataEntry {
    private String mGroupName;
    private Type mItemType;
    private List<Service> mServices;

    public enum Type {
        NONE,
        TOP_SERVICE,
        SINGLE_LINE_BANNER,
        CONVENIENT_SERVICE,
        DOUBLE_BANNER,
        USEFUL_NUMBERS,
        BOTTOM_BANNER
    }

    public ServicesDataEntry(Type itemType) {
        this.mItemType = itemType;
    }

    public Type getItemType() {
        return this.mItemType;
    }

    public String getGroupName() {
        return this.mGroupName;
    }

    public void setItemType(Type type) {
        this.mItemType = type;
    }

    public void setName(String name) {
        this.mGroupName = name;
    }

    public void setServices(List<Service> services) {
        this.mServices = services;
    }

    public List<Service> getServices() {
        return this.mServices;
    }

    public static ServicesDataEntry fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return fromJson(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ServicesDataEntry fromJson(JSONObject servicesData) {
        Service service;
        if (servicesData == null) {
            return null;
        }
        try {
            String name = servicesData.optString("name");
            int style = servicesData.getInt(Tag.TagServicesData.GROUP_STYLE);
            JSONArray data = servicesData.getJSONArray("data");
            ServicesDataEntry servicesDataEntry = new ServicesDataEntry(Type.values()[style]);
            servicesDataEntry.setName(name);
            if (data != null && data.length() > 0) {
                List<Service> services = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject serviceJson = data.getJSONObject(i);
                    if (!(serviceJson == null || (service = Service.fromJson(serviceJson)) == null)) {
                        service.setRawData(serviceJson.toString());
                        services.add(service);
                    }
                }
                servicesDataEntry.setServices(services);
            }
            return servicesDataEntry;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
