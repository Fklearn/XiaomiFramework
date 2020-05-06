package android.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class CursorTreeAdapter
  extends BaseExpandableListAdapter
  implements Filterable, CursorFilter.CursorFilterClient
{
  private boolean mAutoRequery;
  SparseArray<MyCursorHelper> mChildrenCursorHelpers;
  private Context mContext;
  CursorFilter mCursorFilter;
  FilterQueryProvider mFilterQueryProvider;
  MyCursorHelper mGroupCursorHelper;
  private Handler mHandler;
  
  public CursorTreeAdapter(Cursor paramCursor, Context paramContext)
  {
    init(paramCursor, paramContext, true);
  }
  
  public CursorTreeAdapter(Cursor paramCursor, Context paramContext, boolean paramBoolean)
  {
    init(paramCursor, paramContext, paramBoolean);
  }
  
  private void init(Cursor paramCursor, Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
    this.mAutoRequery = paramBoolean;
    this.mGroupCursorHelper = new MyCursorHelper(paramCursor);
    this.mChildrenCursorHelpers = new SparseArray();
  }
  
  private void releaseCursorHelpers()
  {
    try
    {
      for (int i = this.mChildrenCursorHelpers.size() - 1; i >= 0; i--) {
        ((MyCursorHelper)this.mChildrenCursorHelpers.valueAt(i)).deactivate();
      }
      this.mChildrenCursorHelpers.clear();
      return;
    }
    finally {}
  }
  
  protected abstract void bindChildView(View paramView, Context paramContext, Cursor paramCursor, boolean paramBoolean);
  
  protected abstract void bindGroupView(View paramView, Context paramContext, Cursor paramCursor, boolean paramBoolean);
  
  public void changeCursor(Cursor paramCursor)
  {
    this.mGroupCursorHelper.changeCursor(paramCursor, true);
  }
  
  public String convertToString(Cursor paramCursor)
  {
    if (paramCursor == null) {
      paramCursor = "";
    } else {
      paramCursor = paramCursor.toString();
    }
    return paramCursor;
  }
  
  void deactivateChildrenCursorHelper(int paramInt)
  {
    try
    {
      MyCursorHelper localMyCursorHelper = getChildrenCursorHelper(paramInt, true);
      this.mChildrenCursorHelpers.remove(paramInt);
      localMyCursorHelper.deactivate();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Cursor getChild(int paramInt1, int paramInt2)
  {
    return getChildrenCursorHelper(paramInt1, true).moveTo(paramInt2);
  }
  
  public long getChildId(int paramInt1, int paramInt2)
  {
    return getChildrenCursorHelper(paramInt1, true).getId(paramInt2);
  }
  
  public View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup)
  {
    Cursor localCursor = getChildrenCursorHelper(paramInt1, true).moveTo(paramInt2);
    if (localCursor != null)
    {
      if (paramView == null) {
        paramView = newChildView(this.mContext, localCursor, paramBoolean, paramViewGroup);
      }
      bindChildView(paramView, this.mContext, localCursor, paramBoolean);
      return paramView;
    }
    throw new IllegalStateException("this should only be called when the cursor is valid");
  }
  
  public int getChildrenCount(int paramInt)
  {
    MyCursorHelper localMyCursorHelper = getChildrenCursorHelper(paramInt, true);
    if ((this.mGroupCursorHelper.isValid()) && (localMyCursorHelper != null)) {
      paramInt = localMyCursorHelper.getCount();
    } else {
      paramInt = 0;
    }
    return paramInt;
  }
  
  protected abstract Cursor getChildrenCursor(Cursor paramCursor);
  
  MyCursorHelper getChildrenCursorHelper(int paramInt, boolean paramBoolean)
  {
    try
    {
      Object localObject1 = (MyCursorHelper)this.mChildrenCursorHelpers.get(paramInt);
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = this.mGroupCursorHelper.moveTo(paramInt);
        if (localObject2 == null) {
          return null;
        }
        localObject1 = getChildrenCursor(this.mGroupCursorHelper.getCursor());
        localObject2 = new android/widget/CursorTreeAdapter$MyCursorHelper;
        ((MyCursorHelper)localObject2).<init>(this, (Cursor)localObject1);
        this.mChildrenCursorHelpers.put(paramInt, localObject2);
      }
      return (MyCursorHelper)localObject2;
    }
    finally {}
  }
  
  public Cursor getCursor()
  {
    return this.mGroupCursorHelper.getCursor();
  }
  
  public Filter getFilter()
  {
    if (this.mCursorFilter == null) {
      this.mCursorFilter = new CursorFilter(this);
    }
    return this.mCursorFilter;
  }
  
  public FilterQueryProvider getFilterQueryProvider()
  {
    return this.mFilterQueryProvider;
  }
  
  public Cursor getGroup(int paramInt)
  {
    return this.mGroupCursorHelper.moveTo(paramInt);
  }
  
  public int getGroupCount()
  {
    return this.mGroupCursorHelper.getCount();
  }
  
  public long getGroupId(int paramInt)
  {
    return this.mGroupCursorHelper.getId(paramInt);
  }
  
  public View getGroupView(int paramInt, boolean paramBoolean, View paramView, ViewGroup paramViewGroup)
  {
    Cursor localCursor = this.mGroupCursorHelper.moveTo(paramInt);
    if (localCursor != null)
    {
      if (paramView == null) {
        paramView = newGroupView(this.mContext, localCursor, paramBoolean, paramViewGroup);
      }
      bindGroupView(paramView, this.mContext, localCursor, paramBoolean);
      return paramView;
    }
    throw new IllegalStateException("this should only be called when the cursor is valid");
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public boolean isChildSelectable(int paramInt1, int paramInt2)
  {
    return true;
  }
  
  protected abstract View newChildView(Context paramContext, Cursor paramCursor, boolean paramBoolean, ViewGroup paramViewGroup);
  
  protected abstract View newGroupView(Context paramContext, Cursor paramCursor, boolean paramBoolean, ViewGroup paramViewGroup);
  
  public void notifyDataSetChanged()
  {
    notifyDataSetChanged(true);
  }
  
  public void notifyDataSetChanged(boolean paramBoolean)
  {
    if (paramBoolean) {
      releaseCursorHelpers();
    }
    super.notifyDataSetChanged();
  }
  
  public void notifyDataSetInvalidated()
  {
    releaseCursorHelpers();
    super.notifyDataSetInvalidated();
  }
  
  public void onGroupCollapsed(int paramInt)
  {
    deactivateChildrenCursorHelper(paramInt);
  }
  
  public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence)
  {
    FilterQueryProvider localFilterQueryProvider = this.mFilterQueryProvider;
    if (localFilterQueryProvider != null) {
      return localFilterQueryProvider.runQuery(paramCharSequence);
    }
    return this.mGroupCursorHelper.getCursor();
  }
  
  public void setChildrenCursor(int paramInt, Cursor paramCursor)
  {
    getChildrenCursorHelper(paramInt, false).changeCursor(paramCursor, false);
  }
  
  public void setFilterQueryProvider(FilterQueryProvider paramFilterQueryProvider)
  {
    this.mFilterQueryProvider = paramFilterQueryProvider;
  }
  
  public void setGroupCursor(Cursor paramCursor)
  {
    this.mGroupCursorHelper.changeCursor(paramCursor, false);
  }
  
  class MyCursorHelper
  {
    private MyContentObserver mContentObserver;
    private Cursor mCursor;
    private MyDataSetObserver mDataSetObserver;
    private boolean mDataValid;
    private int mRowIDColumn;
    
    MyCursorHelper(Cursor paramCursor)
    {
      boolean bool;
      if (paramCursor != null) {
        bool = true;
      } else {
        bool = false;
      }
      this.mCursor = paramCursor;
      this.mDataValid = bool;
      int i;
      if (bool) {
        i = paramCursor.getColumnIndex("_id");
      } else {
        i = -1;
      }
      this.mRowIDColumn = i;
      this.mContentObserver = new MyContentObserver();
      this.mDataSetObserver = new MyDataSetObserver(null);
      if (bool)
      {
        paramCursor.registerContentObserver(this.mContentObserver);
        paramCursor.registerDataSetObserver(this.mDataSetObserver);
      }
    }
    
    void changeCursor(Cursor paramCursor, boolean paramBoolean)
    {
      if (paramCursor == this.mCursor) {
        return;
      }
      deactivate();
      this.mCursor = paramCursor;
      if (paramCursor != null)
      {
        paramCursor.registerContentObserver(this.mContentObserver);
        paramCursor.registerDataSetObserver(this.mDataSetObserver);
        this.mRowIDColumn = paramCursor.getColumnIndex("_id");
        this.mDataValid = true;
        CursorTreeAdapter.this.notifyDataSetChanged(paramBoolean);
      }
      else
      {
        this.mRowIDColumn = -1;
        this.mDataValid = false;
        CursorTreeAdapter.this.notifyDataSetInvalidated();
      }
    }
    
    void deactivate()
    {
      Cursor localCursor = this.mCursor;
      if (localCursor == null) {
        return;
      }
      localCursor.unregisterContentObserver(this.mContentObserver);
      this.mCursor.unregisterDataSetObserver(this.mDataSetObserver);
      this.mCursor.close();
      this.mCursor = null;
    }
    
    int getCount()
    {
      if (this.mDataValid)
      {
        Cursor localCursor = this.mCursor;
        if (localCursor != null) {
          return localCursor.getCount();
        }
      }
      return 0;
    }
    
    Cursor getCursor()
    {
      return this.mCursor;
    }
    
    long getId(int paramInt)
    {
      if (this.mDataValid)
      {
        Cursor localCursor = this.mCursor;
        if (localCursor != null)
        {
          if (localCursor.moveToPosition(paramInt)) {
            return this.mCursor.getLong(this.mRowIDColumn);
          }
          return 0L;
        }
      }
      return 0L;
    }
    
    boolean isValid()
    {
      boolean bool;
      if ((this.mDataValid) && (this.mCursor != null)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    Cursor moveTo(int paramInt)
    {
      if (this.mDataValid)
      {
        Cursor localCursor = this.mCursor;
        if ((localCursor != null) && (localCursor.moveToPosition(paramInt))) {
          return this.mCursor;
        }
      }
      return null;
    }
    
    private class MyContentObserver
      extends ContentObserver
    {
      public MyContentObserver()
      {
        super();
      }
      
      public boolean deliverSelfNotifications()
      {
        return true;
      }
      
      public void onChange(boolean paramBoolean)
      {
        if ((CursorTreeAdapter.this.mAutoRequery) && (CursorTreeAdapter.MyCursorHelper.this.mCursor != null) && (!CursorTreeAdapter.MyCursorHelper.this.mCursor.isClosed()))
        {
          CursorTreeAdapter.MyCursorHelper localMyCursorHelper = CursorTreeAdapter.MyCursorHelper.this;
          CursorTreeAdapter.MyCursorHelper.access$402(localMyCursorHelper, localMyCursorHelper.mCursor.requery());
        }
      }
    }
    
    private class MyDataSetObserver
      extends DataSetObserver
    {
      private MyDataSetObserver() {}
      
      public void onChanged()
      {
        CursorTreeAdapter.MyCursorHelper.access$402(CursorTreeAdapter.MyCursorHelper.this, true);
        CursorTreeAdapter.this.notifyDataSetChanged();
      }
      
      public void onInvalidated()
      {
        CursorTreeAdapter.MyCursorHelper.access$402(CursorTreeAdapter.MyCursorHelper.this, false);
        CursorTreeAdapter.this.notifyDataSetInvalidated();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CursorTreeAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */