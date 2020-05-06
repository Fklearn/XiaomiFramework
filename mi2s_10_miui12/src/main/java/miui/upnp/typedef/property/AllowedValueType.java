package miui.upnp.typedef.property;

public enum AllowedValueType {
    ANY,
    LIST,
    RANGE;

    public static AllowedValueType retrieveType(int value) {
        if (value == 0) {
            return ANY;
        }
        if (value == 1) {
            return LIST;
        }
        if (value != 2) {
            return ANY;
        }
        return RANGE;
    }

    /* renamed from: miui.upnp.typedef.property.AllowedValueType$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$miui$upnp$typedef$property$AllowedValueType = null;

        static {
            $SwitchMap$miui$upnp$typedef$property$AllowedValueType = new int[AllowedValueType.values().length];
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.ANY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.LIST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.RANGE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public int toInt() {
        int i = AnonymousClass1.$SwitchMap$miui$upnp$typedef$property$AllowedValueType[ordinal()];
        if (i == 1) {
            return 0;
        }
        if (i != 2) {
            return i != 3 ? 0 : 2;
        }
        return 1;
    }
}
