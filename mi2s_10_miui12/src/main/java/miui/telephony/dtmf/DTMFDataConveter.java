package miui.telephony.dtmf;

import android.util.Log;

public class DTMFDataConveter
{
  private static final String LOG_TAG = "DTMFDataConveter";
  private int mBitPerSample;
  private boolean mSign;
  
  public DTMFDataConveter()
  {
    this(8, true);
  }
  
  public DTMFDataConveter(int paramInt, boolean paramBoolean)
  {
    setBitPerSample(paramInt);
    setSign(paramBoolean);
  }
  
  public float[] byteToFloat(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
    {
      Log.i("DTMFDataConveter", "bit mode can not match");
      return null;
    }
    float[] arrayOfFloat = new float[paramArrayOfByte.length / (getBitPerSample() / 8)];
    for (int i = 0; i < arrayOfFloat.length; i++) {
      if (getBitPerSample() == 8)
      {
        if (this.mSign) {
          arrayOfFloat[i] = (paramArrayOfByte[i] / 127.0F);
        } else {
          arrayOfFloat[i] = (((paramArrayOfByte[i] & 0xFF) - Byte.MAX_VALUE) * 0.007874016F);
        }
      }
      else if (getBitPerSample() == 16) {
        if (this.mSign) {
          arrayOfFloat[i] = ((short)(paramArrayOfByte[(i * 2)] & 0xFF | paramArrayOfByte[(i * 2 + 1)] << 8) * 3.051851E-5F);
        } else {
          arrayOfFloat[i] = (((paramArrayOfByte[(i * 2)] & 0xFF | (paramArrayOfByte[(i * 2 + 1)] & 0xFF) << 8) - 32767) * 3.051851E-5F);
        }
      }
    }
    return arrayOfFloat;
  }
  
  public byte[] floatToByte(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null)
    {
      Log.i("DTMFDataConveter", "bit mode can not match");
      return null;
    }
    byte[] arrayOfByte = new byte[paramArrayOfFloat.length * (getBitPerSample() / 8)];
    for (int i = 0; i < paramArrayOfFloat.length; i++) {
      if (getBitPerSample() == 8)
      {
        if (this.mSign) {
          arrayOfByte[i] = ((byte)(byte)(int)(paramArrayOfFloat[i] * 127.0F));
        } else {
          arrayOfByte[i] = ((byte)(byte)(int)(paramArrayOfFloat[i] * 127.0F + 127.0F));
        }
      }
      else if (getBitPerSample() == 16)
      {
        int j;
        if (this.mSign)
        {
          j = (int)(paramArrayOfFloat[i] * 32767.0D);
          arrayOfByte[(i * 2)] = ((byte)(byte)j);
          arrayOfByte[(i * 2 + 1)] = ((byte)(byte)(j >>> 8));
        }
        else
        {
          j = (int)(paramArrayOfFloat[i] * 32767.0D) + 32767;
          arrayOfByte[(i * 2)] = ((byte)(byte)j);
          arrayOfByte[(i * 2 + 1)] = ((byte)(byte)(j >>> 8));
        }
      }
    }
    return arrayOfByte;
  }
  
  public int getBitPerSample()
  {
    return this.mBitPerSample;
  }
  
  public boolean getSign()
  {
    return this.mSign;
  }
  
  public void setBitPerSample(int paramInt)
  {
    this.mBitPerSample = paramInt;
  }
  
  public void setSign(boolean paramBoolean)
  {
    this.mSign = paramBoolean;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/dtmf/DTMFDataConveter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */