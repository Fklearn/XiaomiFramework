package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

@Deprecated
public class AppSecurityPermissions
{
  public static View getPermissionItemView(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean)
  {
    LayoutInflater localLayoutInflater = (LayoutInflater)paramContext.getSystemService("layout_inflater");
    int i;
    if (paramBoolean) {
      i = 17302329;
    } else {
      i = 17302844;
    }
    return getPermissionItemViewOld(paramContext, localLayoutInflater, paramCharSequence1, paramCharSequence2, paramBoolean, paramContext.getDrawable(i));
  }
  
  private static View getPermissionItemViewOld(Context paramContext, LayoutInflater paramLayoutInflater, CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean, Drawable paramDrawable)
  {
    paramContext = paramLayoutInflater.inflate(17367097, null);
    paramLayoutInflater = (TextView)paramContext.findViewById(16909254);
    TextView localTextView = (TextView)paramContext.findViewById(16909256);
    ((ImageView)paramContext.findViewById(16909250)).setImageDrawable(paramDrawable);
    if (paramCharSequence1 != null)
    {
      paramLayoutInflater.setText(paramCharSequence1);
      localTextView.setText(paramCharSequence2);
    }
    else
    {
      paramLayoutInflater.setText(paramCharSequence2);
      localTextView.setVisibility(8);
    }
    return paramContext;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AppSecurityPermissions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */