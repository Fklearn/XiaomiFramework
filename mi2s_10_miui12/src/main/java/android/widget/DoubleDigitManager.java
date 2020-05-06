package android.widget;

import android.os.Handler;

class DoubleDigitManager
{
  private Integer intermediateDigit;
  private final CallBack mCallBack;
  private final long timeoutInMillis;
  
  public DoubleDigitManager(long paramLong, CallBack paramCallBack)
  {
    this.timeoutInMillis = paramLong;
    this.mCallBack = paramCallBack;
  }
  
  public void reportDigit(int paramInt)
  {
    Integer localInteger = this.intermediateDigit;
    if (localInteger == null)
    {
      this.intermediateDigit = Integer.valueOf(paramInt);
      new Handler().postDelayed(new Runnable()
      {
        public void run()
        {
          if (DoubleDigitManager.this.intermediateDigit != null)
          {
            DoubleDigitManager.this.mCallBack.singleDigitFinal(DoubleDigitManager.this.intermediateDigit.intValue());
            DoubleDigitManager.access$002(DoubleDigitManager.this, null);
          }
        }
      }, this.timeoutInMillis);
      if (!this.mCallBack.singleDigitIntermediate(paramInt))
      {
        this.intermediateDigit = null;
        this.mCallBack.singleDigitFinal(paramInt);
      }
    }
    else if (this.mCallBack.twoDigitsFinal(localInteger.intValue(), paramInt))
    {
      this.intermediateDigit = null;
    }
  }
  
  static abstract interface CallBack
  {
    public abstract void singleDigitFinal(int paramInt);
    
    public abstract boolean singleDigitIntermediate(int paramInt);
    
    public abstract boolean twoDigitsFinal(int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DoubleDigitManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */