package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class PropertyList implements Parcelable {
    public static final Parcelable.Creator<PropertyList> CREATOR = new Parcelable.Creator<PropertyList>() {
        public PropertyList createFromParcel(Parcel in) {
            return new PropertyList(in);
        }

        public PropertyList[] newArray(int size) {
            return new PropertyList[size];
        }
    };
    private static final String TAG = PropertyList.class.getSimpleName();
    private List<Property> propertyList = new ArrayList();

    public boolean isChanged() {
        for (Property p : this.propertyList) {
            if (p.getPropertyValue().isChanged()) {
                return true;
            }
        }
        return false;
    }

    public void cleanState() {
        for (Property p : this.propertyList) {
            p.getPropertyValue().cleanState();
        }
    }

    public List<Property> getList() {
        return this.propertyList;
    }

    public List<Property> getChangedPropertyList() {
        List<Property> list = null;
        for (Property p : this.propertyList) {
            if (p.getPropertyValue().isChanged()) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(p);
            }
        }
        return list;
    }

    public Property getProperty(String name) {
        Property property = null;
        for (Property p : this.propertyList) {
            if (p.getDefinition().getName().equals(name)) {
                property = p;
            }
        }
        return property;
    }

    public Property getProperty(PropertyDefinition definition) {
        for (Property p : this.propertyList) {
            if (p.getDefinition().equals(definition)) {
                return p;
            }
        }
        return null;
    }

    public PropertyDefinition getPropertyDefinition(String name) {
        for (Property p : this.propertyList) {
            if (p.getDefinition().getName().equals(name)) {
                return p.getDefinition();
            }
        }
        return null;
    }

    public PropertyValue getPropertyValue(PropertyDefinition definition) {
        for (Property p : this.propertyList) {
            if (p.getDefinition().equals(definition)) {
                return p.getPropertyValue();
            }
        }
        return null;
    }

    public Object getPropertyDataValue(PropertyDefinition definition) {
        for (Property p : this.propertyList) {
            if (p.getDefinition().equals(definition)) {
                return p.getCurrentValue();
            }
        }
        return null;
    }

    public Object getPropertyDataValue(String propertyName) {
        Property property = getProperty(propertyName);
        if (property != null) {
            return property.getCurrentValue();
        }
        return null;
    }

    public void initProperty(PropertyDefinition definition, Object value) {
        this.propertyList.add(new Property(definition, value));
    }

    public void initProperty(Property property) {
        this.propertyList.add(property);
    }

    public boolean setPropertyDataValue(PropertyDefinition definition, Object value) {
        if (value == null) {
            return false;
        }
        if (!definition.validate(value)) {
            Log.d(TAG, String.format("invalid value: %s", new Object[]{value}));
            return false;
        }
        PropertyValue v = getPropertyValue(definition);
        if (v == null) {
            return false;
        }
        v.update(value);
        return true;
    }

    public boolean setPropertyDataValue(String propertyName, Object value) {
        PropertyDefinition definition;
        if (value == null || (definition = getPropertyDefinition(propertyName)) == null) {
            return false;
        }
        return setPropertyDataValue(definition, value);
    }

    public PropertyList() {
    }

    public PropertyList(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            this.propertyList.add((Property) in.readParcelable(Property.class.getClassLoader()));
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.propertyList.size());
        for (Property p : this.propertyList) {
            out.writeParcelable(p, flags);
        }
    }
}
