package android.hardware.radio.config.V1_2;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class SimSlotStatus
{
  public android.hardware.radio.config.V1_0.SimSlotStatus base = new android.hardware.radio.config.V1_0.SimSlotStatus();
  public String eid = new String();
  
  public static final ArrayList<SimSlotStatus> readVectorFromParcel(HwParcel paramHwParcel)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = paramHwParcel.readBuffer(16L);
    int i = ((HwBlob)localObject).getInt32(8L);
    HwBlob localHwBlob = paramHwParcel.readEmbeddedBuffer(i * 64, ((HwBlob)localObject).handle(), 0L, true);
    localArrayList.clear();
    for (int j = 0; j < i; j++)
    {
      localObject = new SimSlotStatus();
      ((SimSlotStatus)localObject).readEmbeddedFromParcel(paramHwParcel, localHwBlob, j * 64);
      localArrayList.add(localObject);
    }
    return localArrayList;
  }
  
  public static final void writeVectorToParcel(HwParcel paramHwParcel, ArrayList<SimSlotStatus> paramArrayList)
  {
    HwBlob localHwBlob1 = new HwBlob(16);
    int i = paramArrayList.size();
    localHwBlob1.putInt32(8L, i);
    localHwBlob1.putBool(12L, false);
    HwBlob localHwBlob2 = new HwBlob(i * 64);
    for (int j = 0; j < i; j++) {
      ((SimSlotStatus)paramArrayList.get(j)).writeEmbeddedToBlob(localHwBlob2, j * 64);
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
    if (paramObject.getClass() != SimSlotStatus.class) {
      return false;
    }
    paramObject = (SimSlotStatus)paramObject;
    if (!HidlSupport.deepEquals(this.base, ((SimSlotStatus)paramObject).base)) {
      return false;
    }
    return HidlSupport.deepEquals(this.eid, ((SimSlotStatus)paramObject).eid);
  }
  
  public final int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(HidlSupport.deepHashCode(this.base)), Integer.valueOf(HidlSupport.deepHashCode(this.eid)) });
  }
  
  public final void readEmbeddedFromParcel(HwParcel paramHwParcel, HwBlob paramHwBlob, long paramLong)
  {
    this.base.readEmbeddedFromParcel(paramHwParcel, paramHwBlob, paramLong + 0L);
    this.eid = paramHwBlob.getString(paramLong + 48L);
    paramHwParcel.readEmbeddedBuffer(this.eid.getBytes().length + 1, paramHwBlob.handle(), paramLong + 48L + 0L, false);
  }
  
  public final void readFromParcel(HwParcel paramHwParcel)
  {
    readEmbeddedFromParcel(paramHwParcel, paramHwParcel.readBuffer(64L), 0L);
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{");
    localStringBuilder.append(".base = ");
    localStringBuilder.append(this.base);
    localStringBuilder.append(", .eid = ");
    localStringBuilder.append(this.eid);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public final void writeEmbeddedToBlob(HwBlob paramHwBlob, long paramLong)
  {
    this.base.writeEmbeddedToBlob(paramHwBlob, 0L + paramLong);
    paramHwBlob.putString(48L + paramLong, this.eid);
  }
  
  public final void writeToParcel(HwParcel paramHwParcel)
  {
    HwBlob localHwBlob = new HwBlob(64);
    writeEmbeddedToBlob(localHwBlob, 0L);
    paramHwParcel.writeBuffer(localHwBlob);
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/hardware/radio/config/V1_2/SimSlotStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */