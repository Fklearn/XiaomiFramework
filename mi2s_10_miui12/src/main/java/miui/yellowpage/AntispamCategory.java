package miui.yellowpage;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Locale;

public class AntispamCategory {
    private String mCustomName;
    private String mIcon;
    private int mId;
    private HashMap<String, String> mNameMap;
    private String mNames;
    private int mOrder;
    private int mType;

    public AntispamCategory(int id, String names, int type, String icon, int order) {
        this.mId = id;
        this.mNames = names;
        this.mType = type;
        if (isUserCustom()) {
            this.mCustomName = this.mNames;
        } else {
            for (String nameEntry : this.mNames.split(";")) {
                String[] nameArray = nameEntry.split(":");
                String language = nameArray[0];
                String name = nameArray[1];
                if (this.mNameMap == null) {
                    this.mNameMap = new HashMap<>();
                }
                this.mNameMap.put(language, name);
            }
        }
        this.mIcon = icon;
        this.mOrder = order;
    }

    public AntispamCategory(int id, String names, String icon, int order) {
        this(id, names, 0, icon, order);
    }

    public String getCategoryName() {
        if (isUserCustom()) {
            return this.mCustomName;
        }
        String name = this.mNameMap.get(Locale.getDefault().toString());
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        return this.mNameMap.get(Locale.US.toString());
    }

    public String getCategoryAllNames() {
        return this.mNames;
    }

    public int getCategoryId() {
        return this.mId;
    }

    public int getCategoryType() {
        return this.mType;
    }

    public boolean isUserCustom() {
        return this.mId >= 10000;
    }

    public int getOrder() {
        return this.mOrder;
    }

    public String getIcon() {
        return this.mIcon;
    }
}
