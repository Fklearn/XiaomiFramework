package miui.upnp.typedef.device.urn;

import android.os.Parcel;
import android.os.Parcelable;

public class Urn implements Parcelable {
    public static final Parcelable.Creator<Urn> CREATOR = new Parcelable.Creator<Urn>() {
        public Urn createFromParcel(Parcel in) {
            return new Urn(in);
        }

        public Urn[] newArray(int size) {
            return new Urn[size];
        }
    };
    private static final String URN = "urn";
    private String domain;
    private String subType;
    private Type type = Type.UNDEFINED;
    private String version;

    public enum Type {
        UNDEFINED,
        DEVICE,
        SERVICE;
        
        private static final String STR_DEVICE = "device";
        private static final String STR_SERVICE = "service";
        private static final String STR_UNDEFINED = "undefined";

        public static Type retrieveType(String value) {
            if (value.equals(STR_UNDEFINED)) {
                return UNDEFINED;
            }
            if (value.equals("device")) {
                return DEVICE;
            }
            if (value.equals(STR_SERVICE)) {
                return SERVICE;
            }
            return UNDEFINED;
        }

        public String toString() {
            int i = AnonymousClass2.$SwitchMap$miui$upnp$typedef$device$urn$Urn$Type[ordinal()];
            if (i == 1) {
                return "device";
            }
            if (i != 2) {
                return STR_UNDEFINED;
            }
            return STR_SERVICE;
        }
    }

    /* renamed from: miui.upnp.typedef.device.urn.Urn$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miui$upnp$typedef$device$urn$Urn$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$miui$upnp$typedef$device$urn$Urn$Type[Type.DEVICE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$device$urn$Urn$Type[Type.SERVICE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static Urn create(String domain2, Type type2, String subType2, String version2) {
        Urn thiz = new Urn();
        thiz.domain = domain2;
        thiz.type = type2;
        thiz.subType = subType2;
        thiz.version = version2;
        return thiz;
    }

    public static Urn create(String domain2, Type type2, String subType2, float version2) {
        return create(domain2, type2, subType2, String.valueOf(version2));
    }

    public static Urn create(String domain2, Type type2, String subType2, int version2) {
        return create(domain2, type2, subType2, String.valueOf(version2));
    }

    public boolean parse(String string) {
        String[] a = string.split(":");
        if (a.length != 5 || !a[0].equals(URN)) {
            return false;
        }
        this.domain = a[1];
        this.type = Type.retrieveType(a[2]);
        this.subType = a[3];
        try {
            this.version = a[4];
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain2) {
        this.domain = domain2;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type2) {
        this.type = type2;
    }

    public String getSubType() {
        return this.subType;
    }

    public void setSubType(String subType2) {
        this.subType = subType2;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version2) {
        this.version = version2;
    }

    public String toString() {
        return String.format("%s:%s:%s:%s:%s", new Object[]{URN, this.domain, this.type.toString(), this.subType, this.version});
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.domain;
        int i2 = 0;
        int result = (i + (str == null ? 0 : str.hashCode())) * 31;
        Type type2 = this.type;
        int result2 = (result + (type2 == null ? 0 : type2.hashCode())) * 31;
        String str2 = this.subType;
        int result3 = (result2 + (str2 == null ? 0 : str2.hashCode())) * 31;
        String str3 = this.version;
        if (str3 != null) {
            i2 = str3.hashCode();
        }
        return result3 + i2;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Urn)) {
            return false;
        }
        Urn other = (Urn) obj;
        String str = this.domain;
        if (str == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!str.equals(other.domain)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        String str2 = this.subType;
        if (str2 == null) {
            if (other.subType != null) {
                return false;
            }
        } else if (!str2.equals(other.subType)) {
            return false;
        }
        if (!this.version.equals(other.version)) {
            return false;
        }
        return true;
    }

    public Urn() {
    }

    public Urn(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        parse(in.readString());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(toString());
    }
}
