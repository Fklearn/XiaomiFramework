package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.error.UpnpError;
import miui.upnp.typedef.exception.UpnpException;

public class AllowedValueRange implements Parcelable {
    public static final Parcelable.Creator<AllowedValueRange> CREATOR = new Parcelable.Creator<AllowedValueRange>() {
        public AllowedValueRange createFromParcel(Parcel in) {
            return new AllowedValueRange(in);
        }

        public AllowedValueRange[] newArray(int size) {
            return new AllowedValueRange[size];
        }
    };
    private static final String TAG = AllowedValueRange.class.getSimpleName();
    private DataType dataType;
    private Object maxValue;
    private Object minValue;

    public static AllowedValueRange create(DataType type, Object min, Object max) throws UpnpException {
        if (!type.getJavaDataType().isInstance(min)) {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT, "min dataType invalid");
        } else if (!type.getJavaDataType().isInstance(max)) {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT, "max dataType invalid");
        } else if (type.validate(min, max)) {
            AllowedValueRange thiz = new AllowedValueRange();
            thiz.dataType = type;
            thiz.minValue = min;
            thiz.maxValue = max;
            return thiz;
        } else {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT, "min >= max");
        }
    }

    public Object getMinValue() {
        return this.minValue;
    }

    public Object getMaxValue() {
        return this.maxValue;
    }

    public boolean isValid(Object value) {
        return this.dataType.validate(this.minValue, value, this.maxValue);
    }

    private AllowedValueRange() {
    }

    public AllowedValueRange(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.dataType = DataType.valueOf(in.readString());
        this.minValue = this.dataType.toObjectValue(in.readString());
        this.maxValue = this.dataType.toObjectValue(in.readString());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.dataType.toString());
        out.writeString(this.dataType.toStringValue(this.minValue));
        out.writeString(this.dataType.toStringValue(this.maxValue));
    }
}
