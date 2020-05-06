package android.hardware.radio.config.V1_1;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class PhoneCapability
{
  public boolean isInternetLingeringSupported;
  public ArrayList<ModemInfo> logicalModemList = new ArrayList();
  public byte maxActiveData;
  public byte maxActiveInternetData;
  
  public static final ArrayList<PhoneCapability> readVectorFromParcel(HwParcel paramHwParcel)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = paramHwParcel.readBuffer(16L);
    int i = ((HwBlob)localObject).getInt32(8L);
    HwBlob localHwBlob = paramHwParcel.readEmbeddedBuffer(i * 24, ((HwBlob)localObject).handle(), 0L, true);
    localArrayList.clear();
    for (int j = 0; j < i; j++)
    {
      localObject = new PhoneCapability();
      ((PhoneCapability)localObject).readEmbeddedFromParcel(paramHwParcel, localHwBlob, j * 24);
      localArrayList.add(localObject);
    }
    return localArrayList;
  }
  
  public static final void writeVectorToParcel(HwParcel paramHwParcel, ArrayList<PhoneCapability> paramArrayList)
  {
    HwBlob localHwBlob1 = new HwBlob(16);
    int i = paramArrayList.size();
    localHwBlob1.putInt32(8L, i);
    localHwBlob1.putBool(12L, false);
    HwBlob localHwBlob2 = new HwBlob(i * 24);
    for (int j = 0; j < i; j++) {
      ((PhoneCapability)paramArrayList.get(j)).writeEmbeddedToBlob(localHwBlob2, j * 24);
    }
    localHwBlob1.putBlob(0L, localHwBlob2);
    paramHwParcel.writeBuffer(localHwBlob1);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (paramObject.getClass() != PhoneCapability.class) {
      return false;
    }
    paramObject = (PhoneCapability)paramObject;
    if (this.maxActiveData != ((PhoneCapability)paramObject).maxActiveData) {
      return false;
    }
    if (this.maxActiveInternetData != ((PhoneCapability)paramObject).maxActiveInternetData) {
      return false;
    }
    if (this.isInternetLingeringSupported != ((PhoneCapability)paramObject).isInternetLingeringSupported) {
      return false;
    }
    return HidlSupport.deepEquals(this.logicalModemList, ((PhoneCapability)paramObject).logicalModemList);
  }
  
  public final int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.maxActiveData))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.maxActiveInternetData))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isInternetLingeringSupported))), Integer.valueOf(HidlSupport.deepHashCode(this.logicalModemList)) });
  }
  
  public final void readEmbeddedFromParcel(HwParcel paramHwParcel, HwBlob paramHwBlob, long paramLong)
  {
    this.maxActiveData = paramHwBlob.getInt8(paramLong + 0L);
    this.maxActiveInternetData = paramHwBlob.getInt8(paramLong + 1L);
    this.isInternetLingeringSupported = paramHwBlob.getBool(paramLong + 2L);
    int i = paramHwBlob.getInt32(paramLong + 8L + 8L);
    paramHwBlob = paramHwParcel.readEmbeddedBuffer(i * 1, paramHwBlob.handle(), paramLong + 8L + 0L, true);
    this.logicalModemList.clear();
    for (int j = 0; j < i; j++)
    {
      ModemInfo localModemInfo = new ModemInfo();
      localModemInfo.readEmbeddedFromParcel(paramHwParcel, paramHwBlob, j * 1);
      this.logicalModemList.add(localModemInfo);
    }
  }
  
  public final void readFromParcel(HwParcel paramHwParcel)
  {
    readEmbeddedFromParcel(paramHwParcel, paramHwParcel.readBuffer(24L), 0L);
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{");
    localStringBuilder.append(".maxActiveData = ");
    localStringBuilder.append(this.maxActiveData);
    localStringBuilder.append(", .maxActiveInternetData = ");
    localStringBuilder.append(this.maxActiveInternetData);
    localStringBuilder.append(", .isInternetLingeringSupported = ");
    localStringBuilder.append(this.isInternetLingeringSupported);
    localStringBuilder.append(", .logicalModemList = ");
    localStringBuilder.append(this.logicalModemList);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public final void writeEmbeddedToBlob(HwBlob paramHwBlob, long paramLong)
  {
    paramHwBlob.putInt8(paramLong + 0L, this.maxActiveData);
    paramHwBlob.putInt8(1L + paramLong, this.maxActiveInternetData);
    paramHwBlob.putBool(2L + paramLong, this.isInternetLingeringSupported);
    int i = this.logicalModemList.size();
    paramHwBlob.putInt32(paramLong + 8L + 8L, i);
    paramHwBlob.putBool(paramLong + 8L + 12L, false);
    HwBlob localHwBlob = new HwBlob(i * 1);
    for (int j = 0; j < i; j++) {
      ((ModemInfo)this.logicalModemList.get(j)).writeEmbeddedToBlob(localHwBlob, j * 1);
    }
    paramHwBlob.putBlob(8L + paramLong + 0L, localHwBlob);
  }
  
  public final void writeToParcel(HwParcel paramHwParcel)
  {
    HwBlob localHwBlob = new HwBlob(24);
    writeEmbeddedToBlob(localHwBlob, 0L);
    paramHwParcel.writeBuffer(localHwBlob);
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/hardware/radio/config/V1_1/PhoneCapability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */