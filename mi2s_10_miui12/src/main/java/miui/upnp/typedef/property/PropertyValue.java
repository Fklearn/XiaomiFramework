package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class PropertyValue implements Parcelable {
    public static final Parcelable.Creator<PropertyValue> CREATOR = new Parcelable.Creator<PropertyValue>() {
        public PropertyValue createFromParcel(Parcel in) {
            return new PropertyValue(in);
        }

        public PropertyValue[] newArray(int size) {
            return new PropertyValue[size];
        }
    };
    private static final String TAG = "PropertyValue";
    private volatile Object currentValue = null;
    private boolean isChanged = false;
    private boolean isOldValueAvailable = false;
    private volatile Object oldValue = null;
    private boolean single = true;
    private volatile List<Object> valueList = new ArrayList();

    public static PropertyValue create(Object value) {
        PropertyValue thiz = new PropertyValue();
        thiz.single = true;
        thiz.oldValue = value;
        thiz.isOldValueAvailable = true;
        return thiz;
    }

    public boolean isSingle() {
        return this.single;
    }

    public void setSingle(boolean single2) {
        this.single = single2;
    }

    public boolean isMultiple() {
        return !this.single;
    }

    public void setMultiple(boolean multiple) {
        this.single = !multiple;
    }

    public void update(Object value) {
        if (value == null) {
            Log.e(TAG, "value is null");
            return;
        }
        this.single = true;
        if (this.oldValue != null && !this.oldValue.getClass().equals(value.getClass())) {
            Log.e(TAG, String.format("invalid: oldValue is %s, new value is %s (%s)", new Object[]{this.oldValue.getClass().getSimpleName(), value.getClass().getSimpleName(), value.toString()}));
        } else if (this.currentValue == null) {
            this.currentValue = value;
            this.isChanged = true;
        } else if (!this.currentValue.equals(value)) {
            this.oldValue = this.currentValue;
            this.isOldValueAvailable = true;
            this.currentValue = value;
            this.isChanged = true;
        }
    }

    public Object getValue() {
        if (this.currentValue != null) {
            return this.currentValue;
        }
        return this.oldValue;
    }

    public Object getCurrentValue() {
        return this.currentValue;
    }

    public boolean isChanged() {
        return this.isChanged;
    }

    public boolean isOldValueAvailable() {
        return this.isOldValueAvailable;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public void cleanState() {
        this.isChanged = false;
    }

    public List<Object> getValueList() {
        return this.valueList;
    }

    public void addMultipleValue(Object newValue) {
        this.single = false;
        this.valueList.add(newValue);
    }

    public PropertyValue() {
    }

    public PropertyValue(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        boolean z = false;
        this.single = in.readInt() == 1;
        if (this.single) {
            this.isChanged = in.readInt() == 1;
            if (in.readInt() == 1) {
                z = true;
            }
            this.isOldValueAvailable = z;
            if (in.readInt() == 1) {
                this.oldValue = in.readValue(Object.class.getClassLoader());
            }
            if (in.readInt() == 1) {
                this.currentValue = in.readValue(Object.class.getClassLoader());
                return;
            }
            return;
        }
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            this.valueList.add(in.readValue(Object.class.getClassLoader()));
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.single ? 1 : 0);
        if (this.single) {
            out.writeInt(this.isChanged ? 1 : 0);
            out.writeInt(this.isOldValueAvailable ? 1 : 0);
            if (this.oldValue == null) {
                out.writeInt(0);
            } else {
                out.writeInt(1);
                out.writeValue(this.oldValue);
            }
            if (this.currentValue == null) {
                out.writeInt(0);
                return;
            }
            out.writeInt(1);
            out.writeValue(this.currentValue);
            return;
        }
        int n = this.valueList.size();
        out.writeInt(n);
        for (int i = 0; i < n; i++) {
            out.writeValue(this.valueList.get(i));
        }
    }
}
