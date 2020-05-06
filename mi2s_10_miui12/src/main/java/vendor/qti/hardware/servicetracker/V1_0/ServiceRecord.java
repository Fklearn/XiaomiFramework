package vendor.qti.hardware.servicetracker.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class ServiceRecord {
    public ArrayList<ServiceConnection> conn = new ArrayList<>();
    public double lastActivity;
    public String packageName = new String();
    public int pid;
    public String processName = new String();
    public boolean serviceB;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ServiceRecord.class) {
            return false;
        }
        ServiceRecord other = (ServiceRecord) otherObject;
        if (HidlSupport.deepEquals(this.packageName, other.packageName) && HidlSupport.deepEquals(this.processName, other.processName) && this.pid == other.pid && this.serviceB == other.serviceB && this.lastActivity == other.lastActivity && HidlSupport.deepEquals(this.conn, other.conn)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(this.packageName)), Integer.valueOf(HidlSupport.deepHashCode(this.processName)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.pid))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.serviceB))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.lastActivity))), Integer.valueOf(HidlSupport.deepHashCode(this.conn))});
    }

    public final String toString() {
        return "{" + ".packageName = " + this.packageName + ", .processName = " + this.processName + ", .pid = " + this.pid + ", .serviceB = " + this.serviceB + ", .lastActivity = " + this.lastActivity + ", .conn = " + this.conn + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(64), 0);
    }

    public static final ArrayList<ServiceRecord> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ServiceRecord> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 64), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ServiceRecord _hidl_vec_element = new ServiceRecord();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 64));
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
        this.serviceB = hwBlob.getBool(_hidl_offset + 36);
        this.lastActivity = hwBlob.getDouble(_hidl_offset + 40);
        int _hidl_vec_size = hwBlob.getInt32(_hidl_offset + 48 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 24), _hidl_blob.handle(), _hidl_offset + 48 + 0, true);
        this.conn.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ServiceConnection _hidl_vec_element = new ServiceConnection();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 24));
            this.conn.add(_hidl_vec_element);
        }
        HwParcel hwParcel = parcel;
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(64);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ServiceRecord> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8, _hidl_vec_size);
        _hidl_blob.putBool(12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 64);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 64));
        }
        _hidl_blob.putBlob(0, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(_hidl_offset + 0, this.packageName);
        _hidl_blob.putString(16 + _hidl_offset, this.processName);
        _hidl_blob.putInt32(32 + _hidl_offset, this.pid);
        _hidl_blob.putBool(36 + _hidl_offset, this.serviceB);
        _hidl_blob.putDouble(40 + _hidl_offset, this.lastActivity);
        int _hidl_vec_size = this.conn.size();
        _hidl_blob.putInt32(_hidl_offset + 48 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 48 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 24);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.conn.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 24));
        }
        _hidl_blob.putBlob(48 + _hidl_offset + 0, childBlob);
    }
}
