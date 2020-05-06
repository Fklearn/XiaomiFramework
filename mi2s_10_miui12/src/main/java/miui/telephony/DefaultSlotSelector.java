package miui.telephony;

public abstract interface DefaultSlotSelector
{
  public abstract int getDefaultDataSlot(int[] paramArrayOfInt, int paramInt);
  
  public abstract void onSimRemoved(int paramInt, String[] paramArrayOfString);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/DefaultSlotSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */