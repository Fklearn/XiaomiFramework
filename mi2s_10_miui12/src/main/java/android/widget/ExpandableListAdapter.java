package android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public abstract interface ExpandableListAdapter
{
  public abstract boolean areAllItemsEnabled();
  
  public abstract Object getChild(int paramInt1, int paramInt2);
  
  public abstract long getChildId(int paramInt1, int paramInt2);
  
  public abstract View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup);
  
  public abstract int getChildrenCount(int paramInt);
  
  public abstract long getCombinedChildId(long paramLong1, long paramLong2);
  
  public abstract long getCombinedGroupId(long paramLong);
  
  public abstract Object getGroup(int paramInt);
  
  public abstract int getGroupCount();
  
  public abstract long getGroupId(int paramInt);
  
  public abstract View getGroupView(int paramInt, boolean paramBoolean, View paramView, ViewGroup paramViewGroup);
  
  public abstract boolean hasStableIds();
  
  public abstract boolean isChildSelectable(int paramInt1, int paramInt2);
  
  public abstract boolean isEmpty();
  
  public abstract void onGroupCollapsed(int paramInt);
  
  public abstract void onGroupExpanded(int paramInt);
  
  public abstract void registerDataSetObserver(DataSetObserver paramDataSetObserver);
  
  public abstract void unregisterDataSetObserver(DataSetObserver paramDataSetObserver);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ExpandableListAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */