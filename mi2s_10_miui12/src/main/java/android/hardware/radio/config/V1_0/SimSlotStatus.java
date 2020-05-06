package android.hardware.radio.config.V1_0;

import android.hardware.radio.V1_0.CardState;
import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class SimSlotStatus
{
  public String atr = new String();
  public int cardState;
  public String iccid = new String();
  public int logicalSlotId;
  public int slotState;
  
  public static final ArrayList<SimSlotStatus> readVectorFromParcel(HwParcel paramHwParcel)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = paramHwParcel.readBuffer(16L);
    int i = ((HwBlob)localObject).getInt32(8L);
    HwBlob localHwBlob = paramHwParcel.readEmbeddedBuffer(i * 48, ((HwBlob)localObject).handle(), 0L, true);
    localArrayList.clear();
    for (int j = 0; j < i; j++)
    {
      localObject = new SimSlotStatus();
      ((SimSlotStatus)localObject).readEmbeddedFromParcel(paramHwParcel, localHwBlob, j * 48);
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
    HwBlob localHwBlob2 = new HwBlob(i * 48);
    for (int j = 0; j < i; j++) {
      ((SimSlotStatus)paramArrayList.get(j)).writeEmbeddedToBlob(localHwBlob2, j * 48);
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
    if (this.cardState != ((SimSlotStatus)paramObject).cardState) {
      return false;
    }
    if (this.slotState != ((SimSlotStatus)paramObject).slotState) {
      return false;
    }
    if (!HidlSupport.deepEquals(this.atr, ((SimSlotStatus)paramObject).atr)) {
      return false;
    }
    if (this.logicalSlotId != ((SimSlotStatus)paramObject).logicalSlotId) {
      return false;
    }
    return HidlSupport.deepEquals(this.iccid, ((SimSlotStatus)paramObject).iccid);
  }
  
  public final int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cardState))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.slotState))), Integer.valueOf(HidlSupport.deepHashCode(this.atr)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.logicalSlotId))), Integer.valueOf(HidlSupport.deepHashCode(this.iccid)) });
  }
  
  public final void readEmbeddedFromParcel(HwParcel paramHwParcel, HwBlob paramHwBlob, long paramLong)
  {
    this.cardState = paramHwBlob.getInt32(paramLong + 0L);
    this.slotState = paramHwBlob.getInt32(paramLong + 4L);
    this.atr = paramHwBlob.getString(paramLong + 8L);
    paramHwParcel.readEmbeddedBuffer(this.atr.getBytes().length + 1, paramHwBlob.handle(), paramLong + 8L + 0L, false);
    this.logicalSlotId = paramHwBlob.getInt32(paramLong + 24L);
    this.iccid = paramHwBlob.getString(paramLong + 32L);
    paramHwParcel.readEmbeddedBuffer(this.iccid.getBytes().length + 1, paramHwBlob.handle(), paramLong + 32L + 0L, false);
  }
  
  public final void readFromParcel(HwParcel paramHwParcel)
  {
    readEmbeddedFromParcel(paramHwParcel, paramHwParcel.readBuffer(48L), 0L);
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{");
    localStringBuilder.append(".cardState = ");
    localStringBuilder.append(CardState.toString(this.cardState));
    localStringBuilder.append(", .slotState = ");
    localStringBuilder.append(SlotState.toString(this.slotState));
    localStringBuilder.append(", .atr = ");
    localStringBuilder.append(this.atr);
    localStringBuilder.append(", .logicalSlotId = ");
    localStringBuilder.append(this.logicalSlotId);
    localStringBuilder.append(", .iccid = ");
    localStringBuilder.append(this.iccid);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public final void writeEmbeddedToBlob(HwBlob paramHwBlob, long paramLong)
  {
    paramHwBlob.putInt32(0L + paramLong, this.cardState);
    paramHwBlob.putInt32(4L + paramLong, this.slotState);
    paramHwBlob.putString(8L + paramLong, this.atr);
    paramHwBlob.putInt32(24L + paramLong, this.logicalSlotId);
    paramHwBlob.putString(32L + paramLong, this.iccid);
  }
  
  public final void writeToParcel(HwParcel paramHwParcel)
  {
    HwBlob localHwBlob = new HwBlob(48);
    writeEmbeddedToBlob(localHwBlob, 0L);
    paramHwParcel.writeBuffer(localHwBlob);
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/hardware/radio/config/V1_0/SimSlotStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */