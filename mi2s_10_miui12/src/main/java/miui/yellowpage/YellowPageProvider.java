package miui.yellowpage;

import android.graphics.Bitmap;
import miui.yellowpage.YellowPageContract;

public class YellowPageProvider {
    public static final YellowPageProvider DEFAULT_PROVIDER = new YellowPageProvider(0, YellowPageContract.Provider.PNAME_DEFAULT, (Bitmap) null, (Bitmap) null);
    private Bitmap mIcon;
    private Bitmap mIconBig;
    private int mId;
    private String mName;

    public YellowPageProvider(int id, String name, Bitmap icon, Bitmap iconBig) {
        this.mId = id;
        this.mName = name;
        this.mIcon = icon;
        this.mIconBig = iconBig;
    }

    public int getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public Bitmap getIcon() {
        return this.mIcon;
    }

    public Bitmap getBigIcon() {
        return this.mIconBig;
    }

    public boolean isMiui() {
        return this.mId == 0;
    }
}
