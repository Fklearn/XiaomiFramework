package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.field.FieldList;

public class Icon implements Parcelable {
    public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() {
        public Icon createFromParcel(Parcel source) {
            return new Icon(source);
        }

        public Icon[] newArray(int size) {
            return new Icon[size];
        }
    };
    private FieldList fields = new FieldList();

    public boolean setMimeType(String mimeType) {
        return this.fields.setValue(IconDefinition.MimeType, (Object) mimeType);
    }

    public String getMimeType() {
        return (String) this.fields.getValue(IconDefinition.MimeType);
    }

    public boolean setWidth(int width) {
        return this.fields.setValue(IconDefinition.Width, (Object) Integer.valueOf(width));
    }

    public int getWidth() {
        return ((Integer) this.fields.getValue(IconDefinition.Width)).intValue();
    }

    public boolean setHeight(int height) {
        return this.fields.setValue(IconDefinition.Height, (Object) Integer.valueOf(height));
    }

    public int getHeight() {
        return ((Integer) this.fields.getValue(IconDefinition.Height)).intValue();
    }

    public boolean setDepth(int depth) {
        return this.fields.setValue(IconDefinition.Depth, (Object) Integer.valueOf(depth));
    }

    public int getDepth() {
        return ((Integer) this.fields.getValue(IconDefinition.Depth)).intValue();
    }

    public boolean setUrl(String url) {
        return this.fields.setValue(IconDefinition.Url, (Object) url);
    }

    public String getUrl() {
        return (String) this.fields.getValue(IconDefinition.Url);
    }

    public Icon() {
        initialize();
    }

    private void initialize() {
        this.fields.initField(IconDefinition.MimeType, (Object) null);
        this.fields.initField(IconDefinition.Width, (Object) null);
        this.fields.initField(IconDefinition.Height, (Object) null);
        this.fields.initField(IconDefinition.Depth, (Object) null);
        this.fields.initField(IconDefinition.Url, (Object) null);
    }

    public Icon(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.fields = (FieldList) in.readParcelable(FieldList.class.getClassLoader());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.fields, flags);
    }

    public int describeContents() {
        return 0;
    }
}
