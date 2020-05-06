package miui.telephony.dtmf;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;

public class DTMFAudioInputStream
  extends InputStream
{
  private static final String LOG_TAG = "DTMFAudioInputStream";
  private byte[] mByteBuff;
  private int mReadedCnt;
  
  public DTMFAudioInputStream(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
    {
      Log.i("DTMFAudioInputStream", "parameter error");
      return;
    }
    this.mByteBuff = new byte[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      this.mByteBuff[i] = ((byte)paramArrayOfByte[i]);
    }
    this.mReadedCnt = 0;
  }
  
  public int read()
    throws IOException
  {
    int i = this.mReadedCnt;
    byte[] arrayOfByte = this.mByteBuff;
    if (i >= arrayOfByte.length) {
      return -1;
    }
    this.mReadedCnt = (i + 1);
    return arrayOfByte[i];
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = this.mByteBuff;
    if ((arrayOfByte != null) && (paramInt > 0))
    {
      int i = paramInt;
      int j = arrayOfByte.length;
      int k = this.mReadedCnt;
      if (j - k < paramInt) {
        i = arrayOfByte.length - k;
      }
      for (k = 0; k < i; k++) {
        paramArrayOfByte[k] = ((byte)(byte)read());
      }
      return paramInt;
    }
    Log.i("DTMFAudioInputStream", "paramenter error:fail to get subdatalist");
    return -1;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/dtmf/DTMFAudioInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */