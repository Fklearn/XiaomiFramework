package miui.telephony;

public abstract class SubscriptionInfo {
    public abstract CharSequence getDisplayName();

    public abstract String getDisplayNumber();

    public abstract String getIccId();

    public abstract int getPhoneId();

    public abstract int getSlotId();

    public abstract int getSubscriptionId();

    public abstract boolean isActivated();

    public int getMcc() {
        return 0;
    }

    public int getMnc() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{id=");
        sb.append(getSubscriptionId());
        sb.append(", iccId=");
        sb.append(PhoneDebug.VDBG ? getIccId() : TelephonyUtils.pii(getIccId()));
        sb.append(", slotId=");
        sb.append(getSlotId());
        sb.append(", displayName=");
        sb.append(getDisplayName());
        sb.append(", displayNumber=");
        sb.append(TelephonyUtils.pii(getDisplayNumber()));
        sb.append(", isActivated=");
        sb.append(isActivated());
        sb.append('}');
        return sb.toString();
    }
}
