package android.widget;

import android.database.Cursor;

public abstract interface FilterQueryProvider
{
  public abstract Cursor runQuery(CharSequence paramCharSequence);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/FilterQueryProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */