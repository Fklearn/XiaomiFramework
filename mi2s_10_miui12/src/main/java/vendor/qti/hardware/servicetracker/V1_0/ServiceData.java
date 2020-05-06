package vendor.qti.hardware.servicetracker.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class ServiceData {
    public double lastActivity;
    public String packageName = new String();
    public int pid;
    public String processName = new String();
    public boolean serviceB;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ServiceData.class) {
            return false;
        }
        ServiceData other = (ServiceData) otherObject;
        if (HidlSupport.deepEquals(this.packageName, other.packageName) && HidlSupport.deepEquals(this.processName, other.processName) && this.pid == other.pid && this.lastActivity == other.lastActivity && this.serviceB == other.serviceB) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(this.packageName)), Integer.valueOf(HidlSupport.deepHashCode(this.processName)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.pid))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.lastActivity))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.serviceB)))});
    }

    public final String toString() {
        return "{" + ".packageName = " + this.packageName + ", .processName = " + this.processName + ", .pid = " + this.pid + ", .lastActivity = " + this.lastActivity + ", .serviceB = " + this.serviceB + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(56), 0);
    }

    public static final ArrayList<ServiceData> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ServiceData> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 56), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ServiceData _hidl_vec_element = new ServiceData();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 56));
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        HwBlob hwBlob = _hidl_blob;
        this.packageName = hwBlob.getString(_hidl_offset + 0);
        parcel.readEmbeddedBuffer((long) (this.packageName.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        this.processName = hwBlob.getString(_hidl_offset + 16);
        parcel.readEmbeddedBuffer((long) (this.processName.getBytes().length + 1), _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        this.pid = hwBlob.getInt32(_hidl_offset + 32);
        this.lastActivity = hwBlob.getDouble(_hidl_offset + 40);
        this.serviceB = hwBlob.getBool(_hidl_offset + 48);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(56);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ServiceData> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8, _hidl_vec_size);
        _hidl_blob.putBool(12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 56);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 56));
        }
        _hidl_blob.putBlob(0, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.packageName);
        _hidl_blob.putString(16 + _hidl_offset, this.processName);
        _hidl_blob.putInt32(32 + _hidl_offset, this.pid);
        _hidl_blob.putDouble(40 + _hidl_offset, this.lastActivity);
        _hidl_blob.putBool(48 + _hidl_offset, this.serviceB);
    }
}
