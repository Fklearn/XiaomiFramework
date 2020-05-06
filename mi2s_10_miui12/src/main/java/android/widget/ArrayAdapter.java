package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArrayAdapter<T>
  extends BaseAdapter
  implements Filterable, ThemedSpinnerAdapter
{
  private final Context mContext;
  private LayoutInflater mDropDownInflater;
  private int mDropDownResource;
  private int mFieldId = 0;
  private ArrayAdapter<T>.ArrayFilter mFilter;
  private final LayoutInflater mInflater;
  @UnsupportedAppUsage
  private final Object mLock = new Object();
  private boolean mNotifyOnChange = true;
  @UnsupportedAppUsage
  private List<T> mObjects;
  private boolean mObjectsFromResources;
  @UnsupportedAppUsage
  private ArrayList<T> mOriginalValues;
  private final int mResource;
  
  public ArrayAdapter(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, 0, new ArrayList());
  }
  
  public ArrayAdapter(Context paramContext, int paramInt1, int paramInt2)
  {
    this(paramContext, paramInt1, paramInt2, new ArrayList());
  }
  
  public ArrayAdapter(Context paramContext, int paramInt1, int paramInt2, List<T> paramList)
  {
    this(paramContext, paramInt1, paramInt2, paramList, false);
  }
  
  private ArrayAdapter(Context paramContext, int paramInt1, int paramInt2, List<T> paramList, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(paramContext);
    this.mDropDownResource = paramInt1;
    this.mResource = paramInt1;
    this.mObjects = paramList;
    this.mObjectsFromResources = paramBoolean;
    this.mFieldId = paramInt2;
  }
  
  public ArrayAdapter(Context paramContext, int paramInt1, int paramInt2, T[] paramArrayOfT)
  {
    this(paramContext, paramInt1, paramInt2, Arrays.asList(paramArrayOfT));
  }
  
  public ArrayAdapter(Context paramContext, int paramInt, List<T> paramList)
  {
    this(paramContext, paramInt, 0, paramList);
  }
  
  public ArrayAdapter(Context paramContext, int paramInt, T[] paramArrayOfT)
  {
    this(paramContext, paramInt, 0, Arrays.asList(paramArrayOfT));
  }
  
  public static ArrayAdapter<CharSequence> createFromResource(Context paramContext, int paramInt1, int paramInt2)
  {
    return new ArrayAdapter(paramContext, paramInt2, 0, Arrays.asList(paramContext.getResources().getTextArray(paramInt1)), true);
  }
  
  private View createViewFromResource(LayoutInflater paramLayoutInflater, int paramInt1, View paramView, ViewGroup paramViewGroup, int paramInt2)
  {
    if (paramView == null) {
      paramLayoutInflater = paramLayoutInflater.inflate(paramInt2, paramViewGroup, false);
    } else {
      paramLayoutInflater = paramView;
    }
    try
    {
      if (this.mFieldId == 0)
      {
        paramView = (TextView)paramLayoutInflater;
      }
      else
      {
        paramView = (TextView)paramLayoutInflater.findViewById(this.mFieldId);
        if (paramView == null) {
          break label88;
        }
      }
      paramViewGroup = getItem(paramInt1);
      if ((paramViewGroup instanceof CharSequence)) {
        paramView.setText((CharSequence)paramViewGroup);
      } else {
        paramView.setText(paramViewGroup.toString());
      }
      return paramLayoutInflater;
      label88:
      paramLayoutInflater = new java/lang/RuntimeException;
      paramView = new java/lang/StringBuilder;
      paramView.<init>();
      paramView.append("Failed to find view with ID ");
      paramView.append(this.mContext.getResources().getResourceName(this.mFieldId));
      paramView.append(" in item layout");
      paramLayoutInflater.<init>(paramView.toString());
      throw paramLayoutInflater;
    }
    catch (ClassCastException paramLayoutInflater)
    {
      Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
      throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", paramLayoutInflater);
    }
  }
  
  public void add(T paramT)
  {
    synchronized (this.mLock)
    {
      if (this.mOriginalValues != null) {
        this.mOriginalValues.add(paramT);
      } else {
        this.mObjects.add(paramT);
      }
      this.mObjectsFromResources = false;
      if (this.mNotifyOnChange) {
        notifyDataSetChanged();
      }
      return;
    }
  }
  
  public void addAll(Collection<? extends T> paramCollection)
  {
    synchronized (this.mLock)
    {
      if (this.mOriginalValues != null) {
        this.mOriginalValues.addAll(paramCollection);
      } else {
        this.mObjects.addAll(paramCollection);
      }
      this.mObjectsFromResources = false;
      if (this.mNotifyOnChange) {
        notifyDataSetChanged();
      }
      return;
    }
  }
  
  public void addAll(T... paramVarArgs)
  {
    synchronized (this.mLock)
    {
      if (this.mOriginalValues != null) {
        Collections.addAll(this.mOriginalValues, paramVarArgs);
      } else {
        Collections.addAll(this.mObjects, paramVarArgs);
      }
      this.mObjectsFromResources = false;
      if (this.mNotifyOnChange) {
        notifyDataSetChanged();
      }
      return;
    }
  }
  
  public void clear()
  {
    synchronized (this.mLock)
    {
      if (this.mOriginalValues != null) {
        this.mOriginalValues.clear();
      } else {
        this.mObjects.clear();
      }
      this.mObjectsFromResources = false;
      if (this.mNotifyOnChange) {
        notifyDataSetChanged();
      }
      return;
    }
  }
  
  public CharSequence[] getAutofillOptions()
  {
    Object localObject = super.getAutofillOptions();
    if (localObject != null) {
      return (CharSequence[])localObject;
    }
    if (this.mObjectsFromResources)
    {
      localObject = this.mObjects;
      if ((localObject != null) && (!((List)localObject).isEmpty()))
      {
        localObject = new CharSequence[this.mObjects.size()];
        this.mObjects.toArray((Object[])localObject);
        return (CharSequence[])localObject;
      }
    }
    return null;
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public int getCount()
  {
    return this.mObjects.size();
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    LayoutInflater localLayoutInflater1 = this.mDropDownInflater;
    LayoutInflater localLayoutInflater2 = localLayoutInflater1;
    if (localLayoutInflater1 == null) {
      localLayoutInflater2 = this.mInflater;
    }
    return createViewFromResource(localLayoutInflater2, paramInt, paramView, paramViewGroup, this.mDropDownResource);
  }
  
  public Resources.Theme getDropDownViewTheme()
  {
    Object localObject = this.mDropDownInflater;
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = ((LayoutInflater)localObject).getContext().getTheme();
    }
    return (Resources.Theme)localObject;
  }
  
  public Filter getFilter()
  {
    if (this.mFilter == null) {
      this.mFilter = new ArrayFilter(null);
    }
    return this.mFilter;
  }
  
  public T getItem(int paramInt)
  {
    return (T)this.mObjects.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getPosition(T paramT)
  {
    return this.mObjects.indexOf(paramT);
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    return createViewFromResource(this.mInflater, paramInt, paramView, paramViewGroup, this.mResource);
  }
  
  public void insert(T paramT, int paramInt)
  {
    synchronized (this.mLock)
    {
      if (this.mOriginalValues != null) {
        this.mOriginalValues.add(paramInt, paramT);
      } else {
        this.mObjects.add(paramInt, paramT);
      }
      this.mObjectsFromResources = false;
      if (this.mNotifyOnChange) {
        notifyDataSetChanged();
      }
      return;
    }
  }
  
  public void notifyDataSetChanged()
  {
    super.notifyDataSetChanged();
    this.mNotifyOnChange = true;
  }
  
  public void remove(T paramT)
  {
    synchronized (this.mLock)
    {
      if (this.mOriginalValues != null) {
        this.mOriginalValues.remove(paramT);
      } else {
        this.mObjects.remove(paramT);
      }
      this.mObjectsFromResources = false;
      if (this.mNotifyOnChange) {
        notifyDataSetChanged();
      }
      return;
    }
  }
  
  public void setDropDownViewResource(int paramInt)
  {
    this.mDropDownResource = paramInt;
  }
  
  public void setDropDownViewTheme(Resources.Theme paramTheme)
  {
    if (paramTheme == null) {
      this.mDropDownInflater = null;
    } else if (paramTheme == this.mInflater.getContext().getTheme()) {
      this.mDropDownInflater = this.mInflater;
    } else {
      this.mDropDownInflater = LayoutInflater.from(new ContextThemeWrapper(this.mContext, paramTheme));
    }
  }
  
  public void setNotifyOnChange(boolean paramBoolean)
  {
    this.mNotifyOnChange = paramBoolean;
  }
  
  public void sort(Comparator<? super T> paramComparator)
  {
    synchronized (this.mLock)
    {
      if (this.mOriginalValues != null) {
        Collections.sort(this.mOriginalValues, paramComparator);
      } else {
        Collections.sort(this.mObjects, paramComparator);
      }
      if (this.mNotifyOnChange) {
        notifyDataSetChanged();
      }
      return;
    }
  }
  
  private class ArrayFilter
    extends Filter
  {
    private ArrayFilter() {}
    
    protected Filter.FilterResults performFiltering(CharSequence arg1)
    {
      Filter.FilterResults localFilterResults = new Filter.FilterResults();
      Object localObject3;
      if (ArrayAdapter.this.mOriginalValues == null) {
        synchronized (ArrayAdapter.this.mLock)
        {
          localObject3 = ArrayAdapter.this;
          ??? = new java/util/ArrayList;
          ((ArrayList)???).<init>(ArrayAdapter.this.mObjects);
          ArrayAdapter.access$102((ArrayAdapter)localObject3, (ArrayList)???);
        }
      }
      if ((??? != null) && (???.length() != 0))
      {
        ??? = ???.toString().toLowerCase();
        synchronized (ArrayAdapter.this.mLock)
        {
          ??? = new java/util/ArrayList;
          ((ArrayList)???).<init>(ArrayAdapter.this.mOriginalValues);
          int i = ((ArrayList)???).size();
          ??? = new ArrayList();
          for (int j = 0; j < i; j++)
          {
            localObject3 = ((ArrayList)???).get(j);
            Object localObject5 = localObject3.toString().toLowerCase();
            if (((String)localObject5).startsWith(???))
            {
              ((ArrayList)???).add(localObject3);
            }
            else
            {
              localObject5 = ((String)localObject5).split(" ");
              int k = localObject5.length;
              for (int m = 0; m < k; m++) {
                if (localObject5[m].startsWith(???))
                {
                  ((ArrayList)???).add(localObject3);
                  break;
                }
              }
            }
          }
          localFilterResults.values = ???;
          localFilterResults.count = ((ArrayList)???).size();
        }
      }
      synchronized (ArrayAdapter.this.mLock)
      {
        ??? = new java/util/ArrayList;
        ((ArrayList)???).<init>(ArrayAdapter.this.mOriginalValues);
        localFilterResults.values = ???;
        localFilterResults.count = ((ArrayList)???).size();
        return localFilterResults;
      }
    }
    
    protected void publishResults(CharSequence paramCharSequence, Filter.FilterResults paramFilterResults)
    {
      ArrayAdapter.access$302(ArrayAdapter.this, (List)paramFilterResults.values);
      if (paramFilterResults.count > 0) {
        ArrayAdapter.this.notifyDataSetChanged();
      } else {
        ArrayAdapter.this.notifyDataSetInvalidated();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ArrayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */