package android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public abstract interface Adapter
{
  public static final int IGNORE_ITEM_VIEW_TYPE = -1;
  public static final int NO_SELECTION = Integer.MIN_VALUE;
  
  public CharSequence[] getAutofillOptions()
  {
    return null;
  }
  
  public abstract int getCount();
  
  public abstract Object getItem(int paramInt);
  
  public abstract long getItemId(int paramInt);
  
  public abstract int getItemViewType(int paramInt);
  
  public abstract View getView(int paramInt, View paramView, ViewGroup paramViewGroup);
  
  public abstract int getViewTypeCount();
  
  public abstract boolean hasStableIds();
  
  public abstract boolean isEmpty();
  
  public abstract void registerDataSetObserver(DataSetObserver paramDataSetObserver);
  
  public abstract void unregisterDataSetObserver(DataSetObserver paramDataSetObserver);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Adapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */