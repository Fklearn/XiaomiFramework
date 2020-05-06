package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAdapter
  implements ListAdapter, SpinnerAdapter
{
  private CharSequence[] mAutofillOptions;
  @UnsupportedAppUsage
  private final DataSetObservable mDataSetObservable = new DataSetObservable();
  
  public boolean areAllItemsEnabled()
  {
    return true;
  }
  
  public CharSequence[] getAutofillOptions()
  {
    return this.mAutofillOptions;
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    return getView(paramInt, paramView, paramViewGroup);
  }
  
  public int getItemViewType(int paramInt)
  {
    return 0;
  }
  
  public int getViewTypeCount()
  {
    return 1;
  }
  
  public boolean hasStableIds()
  {
    return false;
  }
  
  public boolean isEmpty()
  {
    boolean bool;
    if (getCount() == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isEnabled(int paramInt)
  {
    return true;
  }
  
  public void notifyDataSetChanged()
  {
    this.mDataSetObservable.notifyChanged();
  }
  
  public void notifyDataSetInvalidated()
  {
    this.mDataSetObservable.notifyInvalidated();
  }
  
  public void registerDataSetObserver(DataSetObserver paramDataSetObserver)
  {
    this.mDataSetObservable.registerObserver(paramDataSetObserver);
  }
  
  public void setAutofillOptions(CharSequence... paramVarArgs)
  {
    this.mAutofillOptions = paramVarArgs;
  }
  
  public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
  {
    this.mDataSetObservable.unregisterObserver(paramDataSetObserver);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/BaseAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */