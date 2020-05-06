package miui.upnp.typedef.datatype;

import android.util.Log;
import miui.cloud.backup.data.KeyStringSettingItem;
import miui.telephony.phonenumber.Prefix;
import miui.upnp.typedef.exception.InvalidDataTypeException;

public enum DataType {
    UNKNOWN,
    BIN_BASE64,
    BIN_HEX,
    BOOLEAN,
    CHAR,
    DATE,
    DATETIME,
    DATETIME_TZ,
    FIXED_14_4,
    FLOAT,
    I1,
    I2,
    I4,
    INT,
    NUMBER,
    R4,
    R8,
    STRING,
    TIME,
    TIME_TZ,
    UI1,
    UI2,
    UI4,
    URI,
    UUID;
    
    private static final String TAG = null;

    static {
        TAG = DataType.class.getSimpleName();
    }

    public boolean validate(Object min, Object max) {
        Class<?> clazz = getJavaDataType();
        if (clazz == String.class || clazz == Boolean.class || min.getClass() != max.getClass() || min.getClass() != clazz) {
            return false;
        }
        if (clazz == Integer.class) {
            if (((Integer) min).intValue() <= ((Integer) max).intValue()) {
                return true;
            }
            return false;
        } else if (clazz == Long.class) {
            if (((Long) min).longValue() <= ((Long) max).longValue()) {
                return true;
            }
            return false;
        } else if (clazz == Float.class) {
            if (((Float) min).floatValue() <= ((Float) max).floatValue()) {
                return true;
            }
            return false;
        } else if (clazz != Double.class || ((Double) min).doubleValue() > ((Double) max).doubleValue()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validate(Object min, Object value, Object max) {
        Class<?> clazz = getJavaDataType();
        if (clazz == String.class || clazz == Boolean.class) {
            return false;
        }
        if (clazz != value.getClass()) {
            Log.d(TAG, "dataType invalid");
            return false;
        } else if (clazz == Integer.class) {
            if (((Integer) min).intValue() > ((Integer) value).intValue() || ((Integer) value).intValue() > ((Integer) max).intValue()) {
                return false;
            }
            return true;
        } else if (clazz == Long.class) {
            if (((Long) min).longValue() > ((Long) value).longValue() || ((Long) value).longValue() > ((Long) max).longValue()) {
                return false;
            }
            return true;
        } else if (clazz == Float.class) {
            if (Float.compare(((Float) value).floatValue(), ((Float) min).floatValue()) < 0 || Float.compare(((Float) max).floatValue(), ((Float) value).floatValue()) < 0) {
                return false;
            }
            return true;
        } else if (clazz != Double.class || Double.compare(((Double) value).doubleValue(), ((Double) min).doubleValue()) < 0 || Double.compare(((Double) max).doubleValue(), ((Double) value).doubleValue()) < 0) {
            return false;
        } else {
            return true;
        }
    }

    public Class<?> getJavaDataType() {
        switch (this) {
            case BIN_BASE64:
            case BIN_HEX:
            case STRING:
            case TIME:
            case TIME_TZ:
            case DATE:
            case DATETIME:
            case DATETIME_TZ:
            case FIXED_14_4:
            case URI:
            case UUID:
            case CHAR:
                return String.class;
            case I1:
            case I2:
            case INT:
            case NUMBER:
            case UI1:
            case UI2:
            case I4:
                return Integer.class;
            case UI4:
                return Long.class;
            case FLOAT:
            case R4:
                return Float.class;
            case R8:
                return Double.class;
            case BOOLEAN:
                return Boolean.class;
            default:
                return null;
        }
    }

    public static DataType create(String type) throws InvalidDataTypeException {
        if (type.equals("bin.base64")) {
            return BIN_BASE64;
        }
        if (type.equals("bin.hex")) {
            return BIN_HEX;
        }
        if (type.equals(KeyStringSettingItem.TYPE)) {
            return STRING;
        }
        if (type.equals("time")) {
            return TIME;
        }
        if (type.equals("time.tz")) {
            return TIME_TZ;
        }
        if (type.equals("date")) {
            return DATE;
        }
        if (type.equals("dateTime")) {
            return DATETIME;
        }
        if (type.equals("dateTime.tz")) {
            return DATETIME_TZ;
        }
        if (type.equals("fixed.14.4")) {
            return FIXED_14_4;
        }
        if (type.equals("uri")) {
            return URI;
        }
        if (type.equals("uuid")) {
            return UUID;
        }
        if (type.equals("i1")) {
            return I1;
        }
        if (type.equals("i2")) {
            return I2;
        }
        if (type.equals("int")) {
            return INT;
        }
        if (type.equals("number")) {
            return NUMBER;
        }
        if (type.equals("ui1")) {
            return UI1;
        }
        if (type.equals("ui2")) {
            return UI2;
        }
        if (type.equals("i4")) {
            return I4;
        }
        if (type.equals("ui4")) {
            return UI4;
        }
        if (type.equals("float")) {
            return FLOAT;
        }
        if (type.equals("r4")) {
            return R4;
        }
        if (type.equals("r8")) {
            return R8;
        }
        if (type.equals("char")) {
            return CHAR;
        }
        if (type.equals("boolean")) {
            return BOOLEAN;
        }
        throw new InvalidDataTypeException(type);
    }

    public String getStringType() {
        switch (this) {
            case BIN_BASE64:
                return "bin.base64";
            case BIN_HEX:
                return "bin.hex";
            case STRING:
                return KeyStringSettingItem.TYPE;
            case TIME:
                return "time";
            case TIME_TZ:
                return "time.tz";
            case DATE:
                return "date";
            case DATETIME:
                return "dateTime";
            case DATETIME_TZ:
                return "dateTime.tz";
            case FIXED_14_4:
                return "fixed.14.4";
            case URI:
                return "uri";
            case UUID:
                return "uuid";
            case CHAR:
                return "char";
            case I1:
                return "i1";
            case I2:
                return "i2";
            case INT:
                return "int";
            case NUMBER:
                return "number";
            case UI1:
                return "ui1";
            case UI2:
                return "ui2";
            case I4:
                return "i4";
            case UI4:
                return "ui4";
            case FLOAT:
                return "float";
            case R4:
                return "r4";
            case R8:
                return "r8";
            case BOOLEAN:
                return "boolean";
            default:
                return null;
        }
    }

    public String toStringValue(Object value) {
        Class<?> clazz = getJavaDataType();
        if (clazz == String.class) {
            return value.toString();
        }
        if (clazz == Integer.class) {
            return value.toString();
        }
        if (clazz == Long.class) {
            return value.toString();
        }
        if (clazz == Float.class) {
            return value.toString();
        }
        if (clazz == Double.class) {
            return value.toString();
        }
        if (clazz == Boolean.class) {
            return BooleanValueToString(((Boolean) value).booleanValue());
        }
        return null;
    }

    public Object toObjectValue(String string) {
        Class<?> clazz = getJavaDataType();
        if (clazz == String.class) {
            return string;
        }
        if (clazz == Integer.class) {
            try {
                return Integer.valueOf(string);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        } else if (clazz == Long.class) {
            return Long.valueOf(string);
        } else {
            if (clazz == Float.class) {
                return Float.valueOf(string);
            }
            if (clazz == Double.class) {
                return Double.valueOf(string);
            }
            if (clazz == Boolean.class) {
                return BooleanValueOf(string);
            }
            return null;
        }
    }

    public Object createObjectValue() {
        Class<?> clazz = getJavaDataType();
        if (clazz == String.class) {
            return Prefix.EMPTY;
        }
        if (clazz == Integer.class) {
            try {
                return 0;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        } else if (clazz == Long.class) {
            return 0L;
        } else {
            if (clazz == Float.class) {
                return Float.valueOf(0.0f);
            }
            if (clazz == Double.class) {
                return Double.valueOf(0.0d);
            }
            if (clazz == Boolean.class) {
                return Boolean.FALSE;
            }
            return null;
        }
    }

    public static Boolean BooleanValueOf(String string) {
        if (string == null) {
            return false;
        }
        String v = string.toUpperCase();
        if (v.equals("1") || v.equals("YES") || v.equals("TRUE")) {
            return true;
        }
        if (v.equals("0") || v.equals("NO") || v.equals("FALSE")) {
            return false;
        }
        String str = TAG;
        Log.e(str, "invalid value: " + string);
        return false;
    }

    public static String BooleanValueToString(boolean b) {
        return b ? "1" : "0";
    }
}
