package android.hardware.radio.config.V1_0;

import java.util.ArrayList;

public final class SlotState
{
  public static final int ACTIVE = 1;
  public static final int INACTIVE = 0;
  
  public static final String dumpBitfield(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    localArrayList.add("INACTIVE");
    if ((paramInt & 0x1) == 1)
    {
      localArrayList.add("ACTIVE");
      i = 0x0 | 0x1;
    }
    if (paramInt != i)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("0x");
      localStringBuilder.append(Integer.toHexString(i & paramInt));
      localArrayList.add(localStringBuilder.toString());
    }
    return String.join(" | ", localArrayList);
  }
  
  public static final String toString(int paramInt)
  {
    if (paramInt == 0) {
      return "INACTIVE";
    }
    if (paramInt == 1) {
      return "ACTIVE";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("0x");
    localStringBuilder.append(Integer.toHexString(paramInt));
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/hardware/radio/config/V1_0/SlotState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */