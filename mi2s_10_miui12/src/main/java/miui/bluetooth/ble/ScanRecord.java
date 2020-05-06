package miui.bluetooth.ble;

import android.os.ParcelUuid;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.miui.internal.vip.utils.JsonParser;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ScanRecord {
    private static final ParcelUuid BASE_UUID = ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");
    private static final int DATA_TYPE_FLAGS = 1;
    private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 9;
    private static final int DATA_TYPE_LOCAL_NAME_SHORT = 8;
    private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 255;
    private static final int DATA_TYPE_SERVICE_DATA = 22;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 7;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 6;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 3;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 2;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 5;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 4;
    private static final int DATA_TYPE_TX_POWER_LEVEL = 10;
    private static final String TAG = "ScanRecord";
    public static final int UUID_BYTES_128_BIT = 16;
    public static final int UUID_BYTES_16_BIT = 2;
    public static final int UUID_BYTES_32_BIT = 4;
    private final int mAdvertiseFlags;
    private final byte[] mBytes;
    private final String mDeviceName;
    private final SparseArray<byte[]> mManufacturerSpecificData;
    private final Map<ParcelUuid, byte[]> mServiceData;
    private final List<ParcelUuid> mServiceUuids;
    private final int mTxPowerLevel;

    public int getAdvertiseFlags() {
        return this.mAdvertiseFlags;
    }

    public List<ParcelUuid> getServiceUuids() {
        return this.mServiceUuids;
    }

    public SparseArray<byte[]> getManufacturerSpecificData() {
        return this.mManufacturerSpecificData;
    }

    public byte[] getManufacturerSpecificData(int manufacturerId) {
        return this.mManufacturerSpecificData.get(manufacturerId);
    }

    public Map<ParcelUuid, byte[]> getServiceData() {
        return this.mServiceData;
    }

    public byte[] getServiceData(ParcelUuid serviceDataUuid) {
        Map<ParcelUuid, byte[]> map;
        if (serviceDataUuid == null || (map = this.mServiceData) == null) {
            return null;
        }
        return map.get(serviceDataUuid);
    }

    public int getTxPowerLevel() {
        return this.mTxPowerLevel;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public byte[] getBytes() {
        return this.mBytes;
    }

    private ScanRecord(List<ParcelUuid> serviceUuids, SparseArray<byte[]> manufacturerData, Map<ParcelUuid, byte[]> serviceData, int advertiseFlags, int txPowerLevel, String localName, byte[] bytes) {
        this.mServiceUuids = serviceUuids;
        this.mManufacturerSpecificData = manufacturerData;
        this.mServiceData = serviceData;
        this.mDeviceName = localName;
        this.mAdvertiseFlags = advertiseFlags;
        this.mTxPowerLevel = txPowerLevel;
        this.mBytes = bytes;
    }

    public static ScanRecord parseFromBytes(byte[] scanRecord) {
        List<ParcelUuid> serviceUuids;
        byte[] bArr = scanRecord;
        if (bArr == null) {
            return null;
        }
        List<ParcelUuid> arrayList = new ArrayList<>();
        SparseArray<byte[]> manufacturerData = new SparseArray<>();
        Map<ParcelUuid, byte[]> serviceData = new ArrayMap<>();
        int advertiseFlag = -1;
        String localName = null;
        int txPowerLevel = Integer.MIN_VALUE;
        int serviceUuidLength = 0;
        while (true) {
            try {
                if (serviceUuidLength < bArr.length) {
                    int currentPos = serviceUuidLength + 1;
                    try {
                        int length = bArr[serviceUuidLength] & 255;
                        if (length == 0) {
                            int i = currentPos;
                        } else {
                            int dataLength = length - 1;
                            int currentPos2 = currentPos + 1;
                            try {
                                int fieldType = bArr[currentPos] & 255;
                                if (fieldType == 22) {
                                    serviceData.put(parseUuidFrom(extractBytes(bArr, currentPos2, 2)), extractBytes(bArr, currentPos2 + 2, dataLength - 2));
                                } else if (fieldType != 255) {
                                    switch (fieldType) {
                                        case 1:
                                            advertiseFlag = bArr[currentPos2] & 255;
                                            break;
                                        case 2:
                                        case 3:
                                            parseServiceUuid(bArr, currentPos2, dataLength, 2, arrayList);
                                            break;
                                        case 4:
                                        case 5:
                                            parseServiceUuid(bArr, currentPos2, dataLength, 4, arrayList);
                                            break;
                                        case 6:
                                        case 7:
                                            parseServiceUuid(bArr, currentPos2, dataLength, 16, arrayList);
                                            break;
                                        case 8:
                                        case 9:
                                            localName = new String(extractBytes(bArr, currentPos2, dataLength));
                                            break;
                                        case 10:
                                            txPowerLevel = bArr[currentPos2];
                                            break;
                                    }
                                } else {
                                    manufacturerData.put(((bArr[currentPos2 + 1] & 255) << 8) + (255 & bArr[currentPos2]), extractBytes(bArr, currentPos2 + 2, dataLength - 2));
                                }
                                serviceUuidLength = currentPos2 + dataLength;
                            } catch (Exception e) {
                                List<ParcelUuid> list = arrayList;
                                int i2 = currentPos2;
                                Log.e(TAG, "unable to parse scan record: " + Arrays.toString(scanRecord));
                                return new ScanRecord((List<ParcelUuid>) null, (SparseArray<byte[]>) null, (Map<ParcelUuid, byte[]>) null, -1, Integer.MIN_VALUE, (String) null, scanRecord);
                            }
                        }
                    } catch (Exception e2) {
                        List<ParcelUuid> list2 = arrayList;
                        int i3 = currentPos;
                        Log.e(TAG, "unable to parse scan record: " + Arrays.toString(scanRecord));
                        return new ScanRecord((List<ParcelUuid>) null, (SparseArray<byte[]>) null, (Map<ParcelUuid, byte[]>) null, -1, Integer.MIN_VALUE, (String) null, scanRecord);
                    }
                } else {
                    int i4 = serviceUuidLength;
                }
            } catch (Exception e3) {
                int i5 = serviceUuidLength;
                List<ParcelUuid> list3 = arrayList;
                Log.e(TAG, "unable to parse scan record: " + Arrays.toString(scanRecord));
                return new ScanRecord((List<ParcelUuid>) null, (SparseArray<byte[]>) null, (Map<ParcelUuid, byte[]>) null, -1, Integer.MIN_VALUE, (String) null, scanRecord);
            }
        }
        try {
            if (arrayList.isEmpty()) {
                serviceUuids = null;
            } else {
                serviceUuids = arrayList;
            }
        } catch (Exception e4) {
            List<ParcelUuid> list4 = arrayList;
            Log.e(TAG, "unable to parse scan record: " + Arrays.toString(scanRecord));
            return new ScanRecord((List<ParcelUuid>) null, (SparseArray<byte[]>) null, (Map<ParcelUuid, byte[]>) null, -1, Integer.MIN_VALUE, (String) null, scanRecord);
        }
        try {
            return new ScanRecord(serviceUuids, manufacturerData, serviceData, advertiseFlag, txPowerLevel, localName, scanRecord);
        } catch (Exception e5) {
            Log.e(TAG, "unable to parse scan record: " + Arrays.toString(scanRecord));
            return new ScanRecord((List<ParcelUuid>) null, (SparseArray<byte[]>) null, (Map<ParcelUuid, byte[]>) null, -1, Integer.MIN_VALUE, (String) null, scanRecord);
        }
    }

    public String toString() {
        return "ScanRecord [mAdvertiseFlags=" + this.mAdvertiseFlags + ", mServiceUuids=" + this.mServiceUuids + ", mManufacturerSpecificData=" + toString(this.mManufacturerSpecificData) + ", mServiceData=" + toString(this.mServiceData) + ", mTxPowerLevel=" + this.mTxPowerLevel + ", mDeviceName=" + this.mDeviceName + "]";
    }

    private static ParcelUuid parseUuidFrom(byte[] uuidBytes) {
        long shortUuid;
        if (uuidBytes != null) {
            int length = uuidBytes.length;
            if (length != 2 && length != 4 && length != 16) {
                throw new IllegalArgumentException("uuidBytes length invalid - " + length);
            } else if (length == 16) {
                ByteBuffer buf = ByteBuffer.wrap(uuidBytes).order(ByteOrder.LITTLE_ENDIAN);
                return new ParcelUuid(new UUID(buf.getLong(8), buf.getLong(0)));
            } else {
                if (length == 2) {
                    shortUuid = ((long) (uuidBytes[0] & 255)) + ((long) ((uuidBytes[1] & 255) << 8));
                } else {
                    shortUuid = ((long) ((uuidBytes[3] & 255) << MiServiceData.CAPABILITY_IO)) + ((long) (uuidBytes[0] & 255)) + ((long) ((uuidBytes[1] & 255) << 8)) + ((long) ((uuidBytes[2] & 255) << 16));
                }
                return new ParcelUuid(new UUID(BASE_UUID.getUuid().getMostSignificantBits() + (shortUuid << 32), BASE_UUID.getUuid().getLeastSignificantBits()));
            }
        } else {
            throw new IllegalArgumentException("uuidBytes cannot be null");
        }
    }

    private static int parseServiceUuid(byte[] scanRecord, int currentPos, int dataLength, int uuidLength, List<ParcelUuid> serviceUuids) {
        while (dataLength > 0) {
            serviceUuids.add(parseUuidFrom(extractBytes(scanRecord, currentPos, uuidLength)));
            dataLength -= uuidLength;
            currentPos += uuidLength;
        }
        return currentPos;
    }

    private static byte[] extractBytes(byte[] scanRecord, int start, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(scanRecord, start, bytes, 0, length);
        return bytes;
    }

    static String toString(SparseArray<byte[]> array) {
        if (array == null) {
            return "null";
        }
        if (array.size() == 0) {
            return JsonParser.EMPTY_OBJECT;
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        for (int i = 0; i < array.size(); i++) {
            buffer.append(array.keyAt(i));
            buffer.append("=");
            buffer.append(Arrays.toString(array.valueAt(i)));
        }
        buffer.append('}');
        return buffer.toString();
    }

    static <T> String toString(Map<T, byte[]> map) {
        if (map == null) {
            return "null";
        }
        if (map.isEmpty()) {
            return JsonParser.EMPTY_OBJECT;
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        Iterator<Map.Entry<T, byte[]>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Object key = it.next().getKey();
            buffer.append(key);
            buffer.append("=");
            buffer.append(Arrays.toString(map.get(key)));
            if (it.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }
}
