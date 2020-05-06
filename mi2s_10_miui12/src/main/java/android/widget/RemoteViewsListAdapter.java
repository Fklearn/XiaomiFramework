package android.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class RemoteViewsListAdapter
  extends BaseAdapter
{
  private Context mContext;
  private ArrayList<RemoteViews> mRemoteViewsList;
  private int mViewTypeCount;
  private ArrayList<Integer> mViewTypes = new ArrayList();
  
  public RemoteViewsListAdapter(Context paramContext, ArrayList<RemoteViews> paramArrayList, int paramInt)
  {
    this.mContext = paramContext;
    this.mRemoteViewsList = paramArrayList;
    this.mViewTypeCount = paramInt;
    init();
  }
  
  private void init()
  {
    if (this.mRemoteViewsList == null) {
      return;
    }
    this.mViewTypes.clear();
    Iterator localIterator = this.mRemoteViewsList.iterator();
    while (localIterator.hasNext())
    {
      RemoteViews localRemoteViews = (RemoteViews)localIterator.next();
      if (!this.mViewTypes.contains(Integer.valueOf(localRemoteViews.getLayoutId()))) {
        this.mViewTypes.add(Integer.valueOf(localRemoteViews.getLayoutId()));
      }
    }
    int i = this.mViewTypes.size();
    int j = this.mViewTypeCount;
    if ((i <= j) && (j >= 1)) {
      return;
    }
    throw new RuntimeException("Invalid view type count -- view type count must be >= 1and must be as large as the total number of distinct view types");
  }
  
  public int getCount()
  {
    ArrayList localArrayList = this.mRemoteViewsList;
    if (localArrayList != null) {
      return localArrayList.size();
    }
    return 0;
  }
  
  public Object getItem(int paramInt)
  {
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    if (paramInt < getCount())
    {
      paramInt = ((RemoteViews)this.mRemoteViewsList.get(paramInt)).getLayoutId();
      return this.mViewTypes.indexOf(Integer.valueOf(paramInt));
    }
    return 0;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (paramInt < getCount())
    {
      RemoteViews localRemoteViews = (RemoteViews)this.mRemoteViewsList.get(paramInt);
      localRemoteViews.addFlags(2);
      if ((paramView != null) && (paramView.getId() == localRemoteViews.getLayoutId())) {
        localRemoteViews.reapply(this.mContext, paramView);
      } else {
        paramView = localRemoteViews.apply(this.mContext, paramViewGroup);
      }
      return paramView;
    }
    return null;
  }
  
  public int getViewTypeCount()
  {
    return this.mViewTypeCount;
  }
  
  public boolean hasStableIds()
  {
    return false;
  }
  
  public void setViewsList(ArrayList<RemoteViews> paramArrayList)
  {
    this.mRemoteViewsList = paramArrayList;
    init();
    notifyDataSetChanged();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RemoteViewsListAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */