package android.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ResourceCursorTreeAdapter
  extends CursorTreeAdapter
{
  private int mChildLayout;
  private int mCollapsedGroupLayout;
  private int mExpandedGroupLayout;
  private LayoutInflater mInflater;
  private int mLastChildLayout;
  
  public ResourceCursorTreeAdapter(Context paramContext, Cursor paramCursor, int paramInt1, int paramInt2)
  {
    this(paramContext, paramCursor, paramInt1, paramInt1, paramInt2, paramInt2);
  }
  
  public ResourceCursorTreeAdapter(Context paramContext, Cursor paramCursor, int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramContext, paramCursor, paramInt1, paramInt2, paramInt3, paramInt3);
  }
  
  public ResourceCursorTreeAdapter(Context paramContext, Cursor paramCursor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramCursor, paramContext);
    this.mCollapsedGroupLayout = paramInt1;
    this.mExpandedGroupLayout = paramInt2;
    this.mChildLayout = paramInt3;
    this.mLastChildLayout = paramInt4;
    this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
  }
  
  public View newChildView(Context paramContext, Cursor paramCursor, boolean paramBoolean, ViewGroup paramViewGroup)
  {
    paramContext = this.mInflater;
    int i;
    if (paramBoolean) {
      i = this.mLastChildLayout;
    } else {
      i = this.mChildLayout;
    }
    return paramContext.inflate(i, paramViewGroup, false);
  }
  
  public View newGroupView(Context paramContext, Cursor paramCursor, boolean paramBoolean, ViewGroup paramViewGroup)
  {
    paramContext = this.mInflater;
    int i;
    if (paramBoolean) {
      i = this.mExpandedGroupLayout;
    } else {
      i = this.mCollapsedGroupLayout;
    }
    return paramContext.inflate(i, paramViewGroup, false);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ResourceCursorTreeAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */