package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

public abstract class CursorAdapter
  extends BaseAdapter
  implements Filterable, CursorFilter.CursorFilterClient, ThemedSpinnerAdapter
{
  @Deprecated
  public static final int FLAG_AUTO_REQUERY = 1;
  public static final int FLAG_REGISTER_CONTENT_OBSERVER = 2;
  protected boolean mAutoRequery;
  @UnsupportedAppUsage
  protected ChangeObserver mChangeObserver;
  @UnsupportedAppUsage
  protected Context mContext;
  @UnsupportedAppUsage
  protected Cursor mCursor;
  protected CursorFilter mCursorFilter;
  @UnsupportedAppUsage
  protected DataSetObserver mDataSetObserver;
  @UnsupportedAppUsage
  protected boolean mDataValid;
  protected Context mDropDownContext;
  protected FilterQueryProvider mFilterQueryProvider;
  @UnsupportedAppUsage
  protected int mRowIDColumn;
  
  @Deprecated
  public CursorAdapter(Context paramContext, Cursor paramCursor)
  {
    init(paramContext, paramCursor, 1);
  }
  
  public CursorAdapter(Context paramContext, Cursor paramCursor, int paramInt)
  {
    init(paramContext, paramCursor, paramInt);
  }
  
  public CursorAdapter(Context paramContext, Cursor paramCursor, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 1;
    } else {
      i = 2;
    }
    init(paramContext, paramCursor, i);
  }
  
  public abstract void bindView(View paramView, Context paramContext, Cursor paramCursor);
  
  public void changeCursor(Cursor paramCursor)
  {
    paramCursor = swapCursor(paramCursor);
    if (paramCursor != null) {
      paramCursor.close();
    }
  }
  
  public CharSequence convertToString(Cursor paramCursor)
  {
    if (paramCursor == null) {
      paramCursor = "";
    } else {
      paramCursor = paramCursor.toString();
    }
    return paramCursor;
  }
  
  public int getCount()
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
  
  public Cursor getCursor()
  {
    return this.mCursor;
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (this.mDataValid)
    {
      Context localContext1 = this.mDropDownContext;
      Context localContext2 = localContext1;
      if (localContext1 == null) {
        localContext2 = this.mContext;
      }
      this.mCursor.moveToPosition(paramInt);
      if (paramView == null) {
        paramView = newDropDownView(localContext2, this.mCursor, paramViewGroup);
      }
      bindView(paramView, localContext2, this.mCursor);
      return paramView;
    }
    return null;
  }
  
  public Resources.Theme getDropDownViewTheme()
  {
    Object localObject = this.mDropDownContext;
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = ((Context)localObject).getTheme();
    }
    return (Resources.Theme)localObject;
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
  
  public Object getItem(int paramInt)
  {
    if (this.mDataValid)
    {
      Cursor localCursor = this.mCursor;
      if (localCursor != null)
      {
        localCursor.moveToPosition(paramInt);
        return this.mCursor;
      }
    }
    return null;
  }
  
  public long getItemId(int paramInt)
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
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (this.mDataValid)
    {
      if (this.mCursor.moveToPosition(paramInt))
      {
        if (paramView == null) {
          paramView = newView(this.mContext, this.mCursor, paramViewGroup);
        }
        bindView(paramView, this.mContext, this.mCursor);
        return paramView;
      }
      paramView = new StringBuilder();
      paramView.append("couldn't move cursor to position ");
      paramView.append(paramInt);
      throw new IllegalStateException(paramView.toString());
    }
    throw new IllegalStateException("this should only be called when the cursor is valid");
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  void init(Context paramContext, Cursor paramCursor, int paramInt)
  {
    boolean bool = false;
    if ((paramInt & 0x1) == 1)
    {
      paramInt |= 0x2;
      this.mAutoRequery = true;
    }
    else
    {
      this.mAutoRequery = false;
    }
    if (paramCursor != null) {
      bool = true;
    }
    this.mCursor = paramCursor;
    this.mDataValid = bool;
    this.mContext = paramContext;
    int i;
    if (bool) {
      i = paramCursor.getColumnIndexOrThrow("_id");
    } else {
      i = -1;
    }
    this.mRowIDColumn = i;
    if ((paramInt & 0x2) == 2)
    {
      this.mChangeObserver = new ChangeObserver();
      this.mDataSetObserver = new MyDataSetObserver(null);
    }
    else
    {
      this.mChangeObserver = null;
      this.mDataSetObserver = null;
    }
    if (bool)
    {
      paramContext = this.mChangeObserver;
      if (paramContext != null) {
        paramCursor.registerContentObserver(paramContext);
      }
      paramContext = this.mDataSetObserver;
      if (paramContext != null) {
        paramCursor.registerDataSetObserver(paramContext);
      }
    }
  }
  
  @Deprecated
  protected void init(Context paramContext, Cursor paramCursor, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 1;
    } else {
      i = 2;
    }
    init(paramContext, paramCursor, i);
  }
  
  public View newDropDownView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    return newView(paramContext, paramCursor, paramViewGroup);
  }
  
  public abstract View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup);
  
  protected void onContentChanged()
  {
    if (this.mAutoRequery)
    {
      Cursor localCursor = this.mCursor;
      if ((localCursor != null) && (!localCursor.isClosed())) {
        this.mDataValid = this.mCursor.requery();
      }
    }
  }
  
  public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence)
  {
    FilterQueryProvider localFilterQueryProvider = this.mFilterQueryProvider;
    if (localFilterQueryProvider != null) {
      return localFilterQueryProvider.runQuery(paramCharSequence);
    }
    return this.mCursor;
  }
  
  public void setDropDownViewTheme(Resources.Theme paramTheme)
  {
    if (paramTheme == null) {
      this.mDropDownContext = null;
    } else if (paramTheme == this.mContext.getTheme()) {
      this.mDropDownContext = this.mContext;
    } else {
      this.mDropDownContext = new ContextThemeWrapper(this.mContext, paramTheme);
    }
  }
  
  public void setFilterQueryProvider(FilterQueryProvider paramFilterQueryProvider)
  {
    this.mFilterQueryProvider = paramFilterQueryProvider;
  }
  
  public Cursor swapCursor(Cursor paramCursor)
  {
    if (paramCursor == this.mCursor) {
      return null;
    }
    Cursor localCursor = this.mCursor;
    Object localObject;
    if (localCursor != null)
    {
      localObject = this.mChangeObserver;
      if (localObject != null) {
        localCursor.unregisterContentObserver((ContentObserver)localObject);
      }
      localObject = this.mDataSetObserver;
      if (localObject != null) {
        localCursor.unregisterDataSetObserver((DataSetObserver)localObject);
      }
    }
    this.mCursor = paramCursor;
    if (paramCursor != null)
    {
      localObject = this.mChangeObserver;
      if (localObject != null) {
        paramCursor.registerContentObserver((ContentObserver)localObject);
      }
      localObject = this.mDataSetObserver;
      if (localObject != null) {
        paramCursor.registerDataSetObserver((DataSetObserver)localObject);
      }
      this.mRowIDColumn = paramCursor.getColumnIndexOrThrow("_id");
      this.mDataValid = true;
      notifyDataSetChanged();
    }
    else
    {
      this.mRowIDColumn = -1;
      this.mDataValid = false;
      notifyDataSetInvalidated();
    }
    return localCursor;
  }
  
  private class ChangeObserver
    extends ContentObserver
  {
    public ChangeObserver()
    {
      super();
    }
    
    public boolean deliverSelfNotifications()
    {
      return true;
    }
    
    public void onChange(boolean paramBoolean)
    {
      CursorAdapter.this.onContentChanged();
    }
  }
  
  private class MyDataSetObserver
    extends DataSetObserver
  {
    private MyDataSetObserver() {}
    
    public void onChanged()
    {
      CursorAdapter localCursorAdapter = CursorAdapter.this;
      localCursorAdapter.mDataValid = true;
      localCursorAdapter.notifyDataSetChanged();
    }
    
    public void onInvalidated()
    {
      CursorAdapter localCursorAdapter = CursorAdapter.this;
      localCursorAdapter.mDataValid = false;
      localCursorAdapter.notifyDataSetInvalidated();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CursorAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */