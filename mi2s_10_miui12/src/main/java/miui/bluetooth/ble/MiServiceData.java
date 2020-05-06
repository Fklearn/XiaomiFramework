package miui.bluetooth.ble;

import android.os.ParcelUuid;

public class MiServiceData {
    public static final byte CAPABILITY_CENTRAL = 2;
    public static final byte CAPABILITY_CONNECTABLE = 1;
    public static final byte CAPABILITY_ENCRYPT = 4;
    public static final byte CAPABILITY_IO = 24;
    private static final int DATA_MIN_LENGTH = 5;
    private static final int FLAG_BINDING = 512;
    private static final int FLAG_CAPABILITY = 32;
    private static final int FLAG_CENTRAL = 4;
    private static final int FLAG_CONNECTED = 2;
    private static final int FLAG_CUSTOM_DATA = 128;
    private static final int FLAG_ENCRYPTED = 8;
    private static final int FLAG_EVENT = 64;
    private static final int FLAG_MAC_ADDRESS = 16;
    private static final int FLAG_NEW_FACTORY = 1;
    private static final int FLAG_SUBTITLE = 256;
    public static final ParcelUuid MI_SERVICE_UUID = ParcelUuid.fromString(String.format(UUID_BASE, new Object[]{"fe95"}));
    private static final String UUID_BASE = "0000%4s-0000-1000-8000-00805f9b34fb";
    private byte[] mData;
    private int mFrameControl;
    private int mVersion;

    public static MiServiceData fromScanRecord(ScanRecord record) {
        byte[] data;
        if (record == null || record.getServiceData() == null || (data = record.getServiceData(MI_SERVICE_UUID)) == null || data.length < 5) {
            return null;
        }
        return new MiServiceData(data);
    }

    public MiServiceData(byte[] data) {
        if (data == null || data.length < 5) {
            throw new IllegalArgumentException("Mi Service data length must >= 5");
        }
        this.mData = data;
        byte[] bArr = this.mData;
        this.mFrameControl = (bArr[0] & 255) | ((bArr[1] & 255) << 8);
        this.mVersion = (bArr[1] & 240) >> 4;
    }

    public byte[] getData() {
        return this.mData;
    }

    public boolean isNewFactory() {
        return (this.mFrameControl & 1) != 0;
    }

    public boolean isConnected() {
        return (this.mFrameControl & 2) != 0;
    }

    public boolean isCentral() {
        return (this.mFrameControl & 4) != 0;
    }

    public boolean isEncrypted() {
        return (this.mFrameControl & 8) != 0;
    }

    public boolean hasMacAddress() {
        return (this.mFrameControl & 16) != 0;
    }

    public byte[] getMacAddress() {
        if (!hasMacAddress()) {
            return null;
        }
        byte[] bArr = this.mData;
        if (bArr.length < 11) {
            return null;
        }
        byte[] mac = new byte[6];
        System.arraycopy(bArr, 5, mac, 0, 6);
        return mac;
    }

    public boolean hasCapability() {
        return (this.mFrameControl & 32) != 0;
    }

    public byte getCapability() {
        if (!hasCapability()) {
            return 0;
        }
        int offset = 5;
        if (hasMacAddress()) {
            offset = 5 + 6;
        }
        byte[] bArr = this.mData;
        if (bArr.length >= offset + 6) {
            return bArr[offset];
        }
        return 0;
    }

    public boolean hasEvent() {
        return (this.mFrameControl & 64) != 0;
    }

    private int getEventDataIndex() {
        int offset = 5;
        if (hasMacAddress()) {
            offset = 5 + 6;
        }
        if (hasCapability()) {
            return offset + 1;
        }
        return offset;
    }

    public int getEventID() {
        if (!hasEvent()) {
            return 0;
        }
        int offset = getEventDataIndex();
        byte[] bArr = this.mData;
        return ((bArr[offset + 1] & 255) << 8) | (bArr[offset] & 255);
    }

    public byte getEvent() {
        if (!hasEvent()) {
            return 0;
        }
        int offset = getEventDataIndex();
        byte[] bArr = this.mData;
        if (bArr.length >= offset + 3) {
            return bArr[offset + 2];
        }
        return 0;
    }

    public boolean hasCustomData() {
        return (this.mFrameControl & 128) != 0;
    }

    public byte[] getCustomData() {
        if (!hasCustomData()) {
            return null;
        }
        int offset = 5;
        if (hasMacAddress()) {
            offset = 5 + 6;
        }
        if (hasCapability()) {
            offset++;
        }
        if (hasEvent()) {
            offset += 3;
        }
        byte[] bArr = this.mData;
        if (bArr.length <= offset) {
            return null;
        }
        byte len = bArr[offset];
        byte[] data = new byte[len];
        System.arraycopy(bArr, offset + 1, data, 0, len);
        return data;
    }

    public boolean hasSubTitle() {
        return (this.mFrameControl & 256) != 0;
    }

    public boolean isBindingFrame() {
        return (this.mFrameControl & 512) != 0;
    }

    public int getProductID() {
        byte[] bArr = this.mData;
        return ((bArr[3] & 255) << 8) | (bArr[2] & 255);
    }

    public int getFrameCounter() {
        return this.mData[4] & 255;
    }
}
