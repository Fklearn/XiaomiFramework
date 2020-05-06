package miui.telephony;

public class IccUtils
{
  public static String parseIccIdToString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramInt2 * 2);
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
    {
      int j = paramArrayOfByte[i] & 0xF;
      if ((j >= 0) && (j <= 9)) {
        localStringBuilder.append((char)(j + 48));
      } else {
        localStringBuilder.append((char)(j + 65 - 10));
      }
      j = paramArrayOfByte[i] >> 4 & 0xF;
      if ((j >= 0) && (j <= 9)) {
        localStringBuilder.append((char)(j + 48));
      } else {
        localStringBuilder.append((char)(j + 65 - 10));
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/IccUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */