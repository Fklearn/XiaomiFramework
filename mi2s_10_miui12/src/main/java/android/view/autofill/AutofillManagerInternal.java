package android.view.autofill;

import android.content.AutofillOptions;

public abstract class AutofillManagerInternal
{
  public abstract AutofillOptions getAutofillOptions(String paramString, long paramLong, int paramInt);
  
  public abstract boolean isAugmentedAutofillServiceForUser(int paramInt1, int paramInt2);
  
  public abstract void onBackKeyPressed();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/AutofillManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */