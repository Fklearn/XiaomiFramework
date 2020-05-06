package android.os;

public final class UserHandle implements Parcelable {
    public static final UserHandle ALL = null;
    public static final UserHandle CURRENT = null;
    public static final UserHandle CURRENT_OR_SELF = null;
    public static final UserHandle OWNER = null;
    public static final int USER_ALL = -1;
    public static final int USER_CURRENT = -2;
    public static final int USER_NULL = -10000;
    public static final int USER_OWNER = 0;

    public UserHandle(int i) {
    }

    public static int getAppId(int i) {
        return 0;
    }

    public static int getCallingUserId() {
        return 0;
    }

    public static UserHandle getUserHandleForUid(int i) {
        return null;
    }

    public static int getUserId(int i) {
        return 0;
    }

    public static int myUserId() {
        return 0;
    }

    public int describeContents() {
        return 0;
    }

    public int getIdentifier() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
    }
}
