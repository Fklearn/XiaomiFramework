package miui.yellowpage;

import android.content.Intent;
import java.io.Serializable;
import java.util.ArrayList;

public class ModuleIntent implements Serializable {
    private static final long serialVersionUID = 757699801853589L;
    private int mHotId;
    private int mHotShowCount;
    private Intent mIntent;
    private int mModuleId;
    private boolean mSubItemsFlag;
    private ArrayList<ModuleIntent> mSubModuleIntent;
    private String mTitle;

    public int getHotShowCount() {
        return this.mHotShowCount;
    }

    public int getHotId() {
        return this.mHotId;
    }

    public ModuleIntent(String title, Intent intent, int moduleId) {
        this.mTitle = title;
        this.mIntent = intent;
        this.mModuleId = moduleId;
    }

    public ModuleIntent(String title, Intent intent, int moduleId, boolean subItemsFlag) {
        this.mTitle = title;
        this.mIntent = intent;
        this.mModuleId = moduleId;
        this.mSubItemsFlag = subItemsFlag;
    }

    public ModuleIntent(String title, Intent intent, int moduleId, boolean subItemsFlag, int hotId, int hotShowCount) {
        this(title, intent, moduleId, subItemsFlag);
        this.mHotId = hotId;
        this.mHotShowCount = hotShowCount;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public int getModuleId() {
        return this.mModuleId;
    }

    public boolean getSubItemsFlag() {
        return this.mSubItemsFlag;
    }

    public void setSubModuleIntent(ArrayList<ModuleIntent> subModuleIntent) {
        this.mSubModuleIntent = subModuleIntent;
    }

    public ArrayList<ModuleIntent> getSubModuleIntent() {
        return this.mSubModuleIntent;
    }
}
