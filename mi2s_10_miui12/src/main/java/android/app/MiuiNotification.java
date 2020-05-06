package android.app;

import android.os.Parcel;
import android.text.TextUtils;

public class MiuiNotification {
    public static final String EXTRA_ENABLE_FLOAT = "miui.enableFloat";
    public static final String EXTRA_ENABLE_KEYGUARD = "miui.enableKeyguard";
    public static final String EXTRA_FLOAT_TIME = "miui.floatTime";
    public static final String EXTRA_MESSAGE_CLASSNAME = "miui.messageClassName";
    public static final String EXTRA_MESSAGE_COUNT = "miui.messageCount";
    public static final String EXTRA_SHOW_ACTION = "miui.showAction";
    public static final String EXTRA_TARGET_PKG = "miui.targetPkg";
    public static final int TYPE_ADVERTISEMENT = 1;
    public boolean customizedIcon;
    private boolean enableFloat = true;
    private boolean enableKeyguard = true;
    private PendingIntent exitFloatingIntent;
    private int floatTime = 5000;
    private CharSequence messageClassName;
    private int messageCount = 1;
    private CharSequence targetPkg;
    public CharSequence traceContent;
    public int traceType;

    @Deprecated
    public CharSequence getMessageClassName() {
        return this.messageClassName;
    }

    @Deprecated
    public void setMessageClassName(CharSequence messageClassName2) {
        this.messageClassName = messageClassName2;
    }

    @Deprecated
    public int getMessageCount() {
        return this.messageCount;
    }

    @Deprecated
    public void setMessageCount(int messageCount2) {
        this.messageCount = messageCount2;
    }

    @Deprecated
    public PendingIntent getExitFloatingIntent() {
        return this.exitFloatingIntent;
    }

    @Deprecated
    public MiuiNotification setExitFloatingIntent(PendingIntent intent) {
        this.exitFloatingIntent = intent;
        return this;
    }

    @Deprecated
    public MiuiNotification setEnableFloat(boolean enable) {
        this.enableFloat = enable;
        return this;
    }

    @Deprecated
    public MiuiNotification setEnableKeyguard(boolean enable) {
        this.enableKeyguard = enable;
        return this;
    }

    @Deprecated
    public void setTargetPkg(CharSequence pkg) {
        this.targetPkg = pkg;
    }

    @Deprecated
    public CharSequence getTargetPkg() {
        return this.targetPkg;
    }

    @Deprecated
    public boolean isEnableFloat() {
        return this.enableFloat;
    }

    @Deprecated
    public boolean isEnableKeyguard() {
        return this.enableKeyguard;
    }

    @Deprecated
    public MiuiNotification setFloatTime(int floatTime2) {
        if (floatTime2 > 0) {
            this.floatTime = floatTime2;
        }
        return this;
    }

    @Deprecated
    public int getFloatTime() {
        return this.floatTime;
    }

    @Deprecated
    public MiuiNotification setCustomizedIcon(boolean customizedIcon2) {
        this.customizedIcon = customizedIcon2;
        return this;
    }

    @Deprecated
    public MiuiNotification setTrace(int type, CharSequence content) {
        this.traceType = type;
        this.traceContent = content;
        return this;
    }

    public void readFromParcel(Parcel parcel) {
        boolean z = false;
        this.customizedIcon = parcel.readInt() == 1;
        this.traceType = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.traceContent = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
        this.enableFloat = parcel.readInt() == 1;
        this.floatTime = parcel.readInt();
        if (parcel.readInt() == 1) {
            z = true;
        }
        this.enableKeyguard = z;
        if (parcel.readInt() != 0) {
            this.targetPkg = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.exitFloatingIntent = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
        }
        this.messageCount = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.messageClassName = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.customizedIcon ? 1 : 0);
        parcel.writeInt(this.traceType);
        if (this.traceContent != null) {
            parcel.writeInt(1);
            TextUtils.writeToParcel(this.traceContent, parcel, flags);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.enableFloat ? 1 : 0);
        parcel.writeInt(this.floatTime);
        parcel.writeInt(this.enableKeyguard ? 1 : 0);
        if (this.targetPkg != null) {
            parcel.writeInt(1);
            TextUtils.writeToParcel(this.targetPkg, parcel, flags);
        } else {
            parcel.writeInt(0);
        }
        if (this.exitFloatingIntent != null) {
            parcel.writeInt(1);
            this.exitFloatingIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.messageCount);
        if (this.messageClassName != null) {
            parcel.writeInt(1);
            TextUtils.writeToParcel(this.messageClassName, parcel, flags);
            return;
        }
        parcel.writeInt(0);
    }

    public static int[] getLedPwmOffOn(int totalLength) {
        int[] values = new int[2];
        values[0] = (totalLength / 4) * 3;
        values[1] = totalLength - values[0];
        return values;
    }

    public void setTo(MiuiNotification extraNotification) {
        this.traceType = extraNotification.traceType;
        this.traceContent = extraNotification.traceContent;
        this.customizedIcon = extraNotification.customizedIcon;
        this.enableFloat = extraNotification.enableFloat;
        this.floatTime = extraNotification.floatTime;
        this.enableKeyguard = extraNotification.enableKeyguard;
        this.targetPkg = extraNotification.targetPkg;
        this.exitFloatingIntent = extraNotification.exitFloatingIntent;
        this.messageCount = extraNotification.messageCount;
        this.messageClassName = extraNotification.messageClassName;
    }
}
