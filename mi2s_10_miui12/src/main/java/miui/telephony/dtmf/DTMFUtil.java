package miui.telephony.dtmf;

import android.util.Log;
import java.io.IOException;

public class DTMFUtil
{
  private static final int[] DECIBEL_THRESHOLD_LIST = { 23, 32, 31, 34, 37 };
  private static final int DEFAULT_SAMPLE_RATE = 8000;
  private static final int FREQUENCE_INDEX_GAP = 5;
  private static final int HEALTHY = 3;
  private static final String LOG_TAG = "DTMFUtil";
  private static final int[] SAMPLE_RATE_LIST;
  private static final int STEP = 256;
  private static final float[] TARGET_FREQUENCIES = { 600.0F, 697.0F, 770.0F, 852.0F, 941.0F, 1075.0F, 1209.0F, 1336.0F, 1477.0F, 1633.0F };
  private static final float TARGET_HIGH_FREQUENCY = 1075.0F;
  private static final float TARGET_LOW_FREQUENCY = 600.0F;
  private static final int VERSION = 2;
  private int mHealthy;
  private float[] mPreCalculatedCosines;
  private int mSampleRate;
  private float[] mWnkList;
  
  static
  {
    SAMPLE_RATE_LIST = new int[] { 8000, 11025, 22050, 32000, 44100 };
  }
  
  public DTMFUtil()
  {
    this(8000, 3);
  }
  
  public DTMFUtil(int paramInt1, int paramInt2)
  {
    float[] arrayOfFloat = TARGET_FREQUENCIES;
    this.mPreCalculatedCosines = new float[arrayOfFloat.length];
    this.mWnkList = new float[arrayOfFloat.length];
    this.mSampleRate = paramInt1;
    initPrecalculatedCosines();
    if (paramInt2 > 0) {
      paramInt1 = paramInt2;
    } else {
      paramInt1 = 3;
    }
    this.mHealthy = paramInt1;
  }
  
  private float[] bufferFilter(float[] paramArrayOfFloat, int paramInt)
  {
    if (paramArrayOfFloat.length <= 2) {
      return paramArrayOfFloat;
    }
    float f1 = paramArrayOfFloat[0];
    float f2 = paramArrayOfFloat[1];
    paramInt = 0;
    float f3;
    while (paramInt < paramArrayOfFloat.length / 2)
    {
      f3 = f1;
      if (f1 != paramArrayOfFloat[paramInt]) {
        f3 = paramArrayOfFloat[paramInt];
      }
      paramInt += 2;
      f1 = f3;
    }
    paramInt = 1;
    while (paramInt < paramArrayOfFloat.length / 2)
    {
      f3 = f2;
      if (f2 != paramArrayOfFloat[paramInt]) {
        f3 = paramArrayOfFloat[paramInt];
      }
      paramInt += 2;
      f2 = f3;
    }
    if ((f1 == paramArrayOfFloat[0]) && (f2 == paramArrayOfFloat[1])) {
      return new float[] { paramArrayOfFloat[0], paramArrayOfFloat[1] };
    }
    return null;
  }
  
  private float[] dtmfDetectAndValidate(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws IOException
  {
    int i = 0;
    int j = 0;
    byte[] arrayOfByte = new byte['Ā'];
    float[] arrayOfFloat1 = new float[this.mHealthy * 2];
    Object localObject = new float[2];
    localObject = new DTMFAudioInputStream(paramArrayOfByte);
    DTMFDataConveter localDTMFDataConveter = new DTMFDataConveter(paramInt, paramBoolean);
    paramInt = i;
    for (;;)
    {
      i = paramInt;
      if ((j >= this.mHealthy) || (i >= paramArrayOfByte.length)) {
        break;
      }
      if (i + 256 > paramArrayOfByte.length) {
        paramInt = paramArrayOfByte.length;
      } else {
        paramInt = i + 256;
      }
      ((DTMFAudioInputStream)localObject).read(arrayOfByte, paramInt - i);
      float[] arrayOfFloat2 = dtmfFrequenciesDetecter(localDTMFDataConveter.byteToFloat(arrayOfByte));
      i = j;
      if (arrayOfFloat2 != null)
      {
        arrayOfFloat1[(j * 2)] = arrayOfFloat2[0];
        arrayOfFloat1[(j * 2 + 1)] = arrayOfFloat2[1];
        i = j + 1;
      }
      j = i;
    }
    ((DTMFAudioInputStream)localObject).close();
    if (j == this.mHealthy) {
      return bufferFilter(arrayOfFloat1, j * 2);
    }
    return null;
  }
  
  private float[] dtmfFrequenciesDetecter(float[] paramArrayOfFloat)
  {
    boolean[] arrayOfBoolean = new boolean[TARGET_FREQUENCIES.length];
    float[] arrayOfFloat1 = new float[2];
    arrayOfFloat1[1] = -1.0F;
    arrayOfFloat1[0] = -1.0F;
    int i = getDecibelThreshold(this.mSampleRate);
    if (i == -1)
    {
      Log.i("DTMFUtil", "can not get threshold");
      return arrayOfFloat1;
    }
    float[] arrayOfFloat2 = new float[TARGET_FREQUENCIES.length];
    int k;
    for (int j = 0; j < TARGET_FREQUENCIES.length; j++)
    {
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3;
      for (k = 0;; k++)
      {
        f3 = f1;
        if (k >= paramArrayOfFloat.length) {
          break;
        }
        f1 = f2;
        f2 = this.mPreCalculatedCosines[j] * f1 - f3 + paramArrayOfFloat[k];
      }
      arrayOfFloat2[j] = ((float)(Math.log10(Math.abs(f2 - this.mWnkList[j] * f3)) * 20.0D));
      if (arrayOfFloat2[j] > i) {
        arrayOfBoolean[j] = true;
      }
    }
    for (j = 0; j < arrayOfBoolean.length / 2; j++)
    {
      paramArrayOfFloat = TARGET_FREQUENCIES;
      k = (j + 5) % paramArrayOfFloat.length;
      if ((arrayOfBoolean[j] != 0) && (arrayOfBoolean[k] != 0))
      {
        arrayOfFloat1[0] = paramArrayOfFloat[j];
        arrayOfFloat1[1] = paramArrayOfFloat[k];
        if (isValidate(arrayOfFloat1)) {
          return arrayOfFloat1;
        }
      }
    }
    return null;
  }
  
  private static float[] getAudioFloatBuffer(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    int i = paramInt1 * paramInt2 / 1000;
    if (paramArrayOfFloat.length != 2)
    {
      Log.i("DTMFUtil", "parameter buffer is null");
      return null;
    }
    long l = 4602678819172646912L;
    double d1 = paramArrayOfFloat[0];
    double d2 = paramArrayOfFloat[1];
    paramArrayOfFloat = new float[i];
    for (paramInt2 = 0; paramInt2 < paramArrayOfFloat.length; paramInt2++)
    {
      double d3 = paramInt2 / paramInt1;
      paramArrayOfFloat[paramInt2] = ((float)(Math.sin(d1 * 6.283185307179586D * d3) * 0.5D + Math.sin(d2 * 6.283185307179586D * d3) * 0.5D));
    }
    return paramArrayOfFloat;
  }
  
  private int getDecibelThreshold(int paramInt)
  {
    if (SAMPLE_RATE_LIST.length != DECIBEL_THRESHOLD_LIST.length)
    {
      Log.i("DTMFUtil", "the number of SAMPLE_RATE_LIST and DECIBEL_THRESHOLD_LIST can not match");
      return -1;
    }
    for (int i = 0;; i++)
    {
      int[] arrayOfInt = SAMPLE_RATE_LIST;
      if ((i >= arrayOfInt.length) || (paramInt == arrayOfInt[i])) {
        break;
      }
    }
    if (i >= SAMPLE_RATE_LIST.length)
    {
      Log.i("DTMFUtil", "can not find db threshold");
      return -1;
    }
    return DECIBEL_THRESHOLD_LIST[i];
  }
  
  public static float[] getDualFrequence(char paramChar)
  {
    float[] arrayOfFloat = new float[2];
    if (paramChar != '#')
    {
      if (paramChar != '*')
      {
        switch (paramChar)
        {
        default: 
          arrayOfFloat[1] = -1.0F;
          arrayOfFloat[0] = -1.0F;
          break;
        case '9': 
          arrayOfFloat[0] = 852.0F;
          arrayOfFloat[1] = 1477.0F;
          break;
        case '8': 
          arrayOfFloat[0] = 852.0F;
          arrayOfFloat[1] = 1336.0F;
          break;
        case '7': 
          arrayOfFloat[0] = 852.0F;
          arrayOfFloat[1] = 1209.0F;
          break;
        case '6': 
          arrayOfFloat[0] = 770.0F;
          arrayOfFloat[1] = 1477.0F;
          break;
        case '5': 
          arrayOfFloat[0] = 770.0F;
          arrayOfFloat[1] = 1336.0F;
          break;
        case '4': 
          arrayOfFloat[0] = 770.0F;
          arrayOfFloat[1] = 1209.0F;
          break;
        case '3': 
          arrayOfFloat[0] = 697.0F;
          arrayOfFloat[1] = 1477.0F;
          break;
        case '2': 
          arrayOfFloat[0] = 697.0F;
          arrayOfFloat[1] = 1336.0F;
          break;
        case '1': 
          arrayOfFloat[0] = 697.0F;
          arrayOfFloat[1] = 1209.0F;
          break;
        case '0': 
          arrayOfFloat[0] = 941.0F;
          arrayOfFloat[1] = 1336.0F;
          break;
        }
      }
      else
      {
        arrayOfFloat[0] = 941.0F;
        arrayOfFloat[1] = 1209.0F;
      }
    }
    else
    {
      arrayOfFloat[0] = 941.0F;
      arrayOfFloat[1] = 1477.0F;
    }
    return arrayOfFloat;
  }
  
  public static float getTargetHighFrequency()
  {
    return 1075.0F;
  }
  
  public static float getTargetLowFrequency()
  {
    return 600.0F;
  }
  
  public static int getVersion()
  {
    return 2;
  }
  
  private void initPrecalculatedCosines()
  {
    if (this.mSampleRate == 0)
    {
      Log.i("DTMFUtil", "fail to dispatching funtion initPrecalculatedCosines: you need to set mSampleRate");
      return;
    }
    for (int i = 0;; i++)
    {
      float[] arrayOfFloat = TARGET_FREQUENCIES;
      if (i >= arrayOfFloat.length) {
        break;
      }
      double d = Math.cos(arrayOfFloat[i] * 6.283185307179586D / this.mSampleRate);
      this.mPreCalculatedCosines[i] = ((float)(d * 2.0D));
      d = Math.exp(TARGET_FREQUENCIES[i] * -6.283185307179586D / this.mSampleRate);
      this.mWnkList[i] = ((float)d);
    }
  }
  
  private boolean isValidate(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null)
    {
      Log.i("DTMFUtil", "null parameter");
      return false;
    }
    if (paramArrayOfFloat[1] - paramArrayOfFloat[0] < 268.0F) {
      return false;
    }
    int i = 0;
    int j = 0;
    for (int k = 0;; k++)
    {
      float[] arrayOfFloat = TARGET_FREQUENCIES;
      if (k >= arrayOfFloat.length) {
        break;
      }
      if (paramArrayOfFloat[0] == arrayOfFloat[k]) {
        i = k;
      }
      if (paramArrayOfFloat[1] == TARGET_FREQUENCIES[k]) {
        j = k;
      }
    }
    return j - i == 5;
  }
  
  public int getHealthy()
  {
    return this.mHealthy;
  }
  
  public int getSampleRate()
  {
    return this.mSampleRate;
  }
  
  public float[] parseFrequency(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (paramArrayOfByte == null)
    {
      Log.i("DTMFUtil", "parameter error: null");
      return null;
    }
    return dtmfDetectAndValidate(paramArrayOfByte, paramInt, paramBoolean);
  }
  
  public void setHealthy(int paramInt)
  {
    this.mHealthy = paramInt;
  }
  
  public void setSampleRate(int paramInt)
  {
    this.mSampleRate = paramInt;
    initPrecalculatedCosines();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/dtmf/DTMFUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */