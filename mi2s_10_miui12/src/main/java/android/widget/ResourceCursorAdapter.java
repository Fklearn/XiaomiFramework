package android.widget;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ResourceCursorAdapter
  extends CursorAdapter
{
  private LayoutInflater mDropDownInflater;
  private int mDropDownLayout;
  private LayoutInflater mInflater;
  private int mLayout;
  
  @Deprecated
  public ResourceCursorAdapter(Context paramContext, int paramInt, Cursor paramCursor)
  {
    super(paramContext, paramCursor);
    this.mDropDownLayout = paramInt;
    this.mLayout = paramInt;
    this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    this.mDropDownInflater = this.mInflater;
  }
  
  public ResourceCursorAdapter(Context paramContext, int paramInt1, Cursor paramCursor, int paramInt2)
  {
    super(paramContext, paramCursor, paramInt2);
    this.mDropDownLayout = paramInt1;
    this.mLayout = paramInt1;
    this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    this.mDropDownInflater = this.mInflater;
  }
  
  public ResourceCursorAdapter(Context paramContext, int paramInt, Cursor paramCursor, boolean paramBoolean)
  {
    super(paramContext, paramCursor, paramBoolean);
    this.mDropDownLayout = paramInt;
    this.mLayout = paramInt;
    this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    this.mDropDownInflater = this.mInflater;
  }
  
  public View newDropDownView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    return this.mDropDownInflater.inflate(this.mDropDownLayout, paramViewGroup, false);
  }
  
  public View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    return this.mInflater.inflate(this.mLayout, paramViewGroup, false);
  }
  
  public void setDropDownViewResource(int paramInt)
  {
    this.mDropDownLayout = paramInt;
  }
  
  public void setDropDownViewTheme(Resources.Theme paramTheme)
  {
    super.setDropDownViewTheme(paramTheme);
    if (paramTheme == null) {
      this.mDropDownInflater = null;
    } else if (paramTheme == this.mInflater.getContext().getTheme()) {
      this.mDropDownInflater = this.mInflater;
    } else {
      this.mDropDownInflater = LayoutInflater.from(new ContextThemeWrapper(this.mContext, paramTheme));
    }
  }
  
  public void setViewResource(int paramInt)
  {
    this.mLayout = paramInt;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ResourceCursorAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */