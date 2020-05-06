package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class HeaderViewListAdapter
  implements WrapperListAdapter, Filterable
{
  static final ArrayList<ListView.FixedViewInfo> EMPTY_INFO_LIST = new ArrayList();
  @UnsupportedAppUsage
  private final ListAdapter mAdapter;
  boolean mAreAllFixedViewsSelectable;
  @UnsupportedAppUsage
  ArrayList<ListView.FixedViewInfo> mFooterViewInfos;
  @UnsupportedAppUsage
  ArrayList<ListView.FixedViewInfo> mHeaderViewInfos;
  private final boolean mIsFilterable;
  
  public HeaderViewListAdapter(ArrayList<ListView.FixedViewInfo> paramArrayList1, ArrayList<ListView.FixedViewInfo> paramArrayList2, ListAdapter paramListAdapter)
  {
    this.mAdapter = paramListAdapter;
    this.mIsFilterable = (paramListAdapter instanceof Filterable);
    if (paramArrayList1 == null) {
      this.mHeaderViewInfos = EMPTY_INFO_LIST;
    } else {
      this.mHeaderViewInfos = paramArrayList1;
    }
    if (paramArrayList2 == null) {
      this.mFooterViewInfos = EMPTY_INFO_LIST;
    } else {
      this.mFooterViewInfos = paramArrayList2;
    }
    boolean bool;
    if ((areAllListInfosSelectable(this.mHeaderViewInfos)) && (areAllListInfosSelectable(this.mFooterViewInfos))) {
      bool = true;
    } else {
      bool = false;
    }
    this.mAreAllFixedViewsSelectable = bool;
  }
  
  private boolean areAllListInfosSelectable(ArrayList<ListView.FixedViewInfo> paramArrayList)
  {
    if (paramArrayList != null)
    {
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext()) {
        if (!((ListView.FixedViewInfo)paramArrayList.next()).isSelectable) {
          return false;
        }
      }
    }
    return true;
  }
  
  public boolean areAllItemsEnabled()
  {
    ListAdapter localListAdapter = this.mAdapter;
    boolean bool = true;
    if (localListAdapter != null)
    {
      if ((!this.mAreAllFixedViewsSelectable) || (!localListAdapter.areAllItemsEnabled())) {
        bool = false;
      }
      return bool;
    }
    return true;
  }
  
  public int getCount()
  {
    if (this.mAdapter != null) {
      return getFootersCount() + getHeadersCount() + this.mAdapter.getCount();
    }
    return getFootersCount() + getHeadersCount();
  }
  
  public Filter getFilter()
  {
    if (this.mIsFilterable) {
      return ((Filterable)this.mAdapter).getFilter();
    }
    return null;
  }
  
  public int getFootersCount()
  {
    return this.mFooterViewInfos.size();
  }
  
  public int getHeadersCount()
  {
    return this.mHeaderViewInfos.size();
  }
  
  public Object getItem(int paramInt)
  {
    int i = getHeadersCount();
    if (paramInt < i) {
      return ((ListView.FixedViewInfo)this.mHeaderViewInfos.get(paramInt)).data;
    }
    int j = paramInt - i;
    paramInt = 0;
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null)
    {
      i = localListAdapter.getCount();
      paramInt = i;
      if (j < i) {
        return this.mAdapter.getItem(j);
      }
    }
    return ((ListView.FixedViewInfo)this.mFooterViewInfos.get(j - paramInt)).data;
  }
  
  public long getItemId(int paramInt)
  {
    int i = getHeadersCount();
    ListAdapter localListAdapter = this.mAdapter;
    if ((localListAdapter != null) && (paramInt >= i))
    {
      paramInt -= i;
      if (paramInt < localListAdapter.getCount()) {
        return this.mAdapter.getItemId(paramInt);
      }
    }
    return -1L;
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = getHeadersCount();
    ListAdapter localListAdapter = this.mAdapter;
    if ((localListAdapter != null) && (paramInt >= i))
    {
      paramInt -= i;
      if (paramInt < localListAdapter.getCount()) {
        return this.mAdapter.getItemViewType(paramInt);
      }
    }
    return -2;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    int i = getHeadersCount();
    if (paramInt < i) {
      return ((ListView.FixedViewInfo)this.mHeaderViewInfos.get(paramInt)).view;
    }
    int j = paramInt - i;
    paramInt = 0;
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null)
    {
      i = localListAdapter.getCount();
      paramInt = i;
      if (j < i) {
        return this.mAdapter.getView(j, paramView, paramViewGroup);
      }
    }
    return ((ListView.FixedViewInfo)this.mFooterViewInfos.get(j - paramInt)).view;
  }
  
  public int getViewTypeCount()
  {
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null) {
      return localListAdapter.getViewTypeCount();
    }
    return 1;
  }
  
  public ListAdapter getWrappedAdapter()
  {
    return this.mAdapter;
  }
  
  public boolean hasStableIds()
  {
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null) {
      return localListAdapter.hasStableIds();
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    ListAdapter localListAdapter = this.mAdapter;
    boolean bool;
    if ((localListAdapter != null) && (!localListAdapter.isEmpty())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isEnabled(int paramInt)
  {
    int i = getHeadersCount();
    if (paramInt < i) {
      return ((ListView.FixedViewInfo)this.mHeaderViewInfos.get(paramInt)).isSelectable;
    }
    int j = paramInt - i;
    paramInt = 0;
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null)
    {
      i = localListAdapter.getCount();
      paramInt = i;
      if (j < i) {
        return this.mAdapter.isEnabled(j);
      }
    }
    return ((ListView.FixedViewInfo)this.mFooterViewInfos.get(j - paramInt)).isSelectable;
  }
  
  public void registerDataSetObserver(DataSetObserver paramDataSetObserver)
  {
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null) {
      localListAdapter.registerDataSetObserver(paramDataSetObserver);
    }
  }
  
  public boolean removeFooter(View paramView)
  {
    for (int i = 0;; i++)
    {
      int j = this.mFooterViewInfos.size();
      boolean bool = false;
      if (i >= j) {
        break;
      }
      if (((ListView.FixedViewInfo)this.mFooterViewInfos.get(i)).view == paramView)
      {
        this.mFooterViewInfos.remove(i);
        if ((areAllListInfosSelectable(this.mHeaderViewInfos)) && (areAllListInfosSelectable(this.mFooterViewInfos))) {
          bool = true;
        }
        this.mAreAllFixedViewsSelectable = bool;
        return true;
      }
    }
    return false;
  }
  
  public boolean removeHeader(View paramView)
  {
    for (int i = 0;; i++)
    {
      int j = this.mHeaderViewInfos.size();
      boolean bool = false;
      if (i >= j) {
        break;
      }
      if (((ListView.FixedViewInfo)this.mHeaderViewInfos.get(i)).view == paramView)
      {
        this.mHeaderViewInfos.remove(i);
        if ((areAllListInfosSelectable(this.mHeaderViewInfos)) && (areAllListInfosSelectable(this.mFooterViewInfos))) {
          bool = true;
        }
        this.mAreAllFixedViewsSelectable = bool;
        return true;
      }
    }
    return false;
  }
  
  public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
  {
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null) {
      localListAdapter.unregisterDataSetObserver(paramDataSetObserver);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/HeaderViewListAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */