package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleAdapter
  extends BaseAdapter
  implements Filterable, ThemedSpinnerAdapter
{
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private List<? extends Map<String, ?>> mData;
  private LayoutInflater mDropDownInflater;
  private int mDropDownResource;
  private SimpleFilter mFilter;
  private String[] mFrom;
  private final LayoutInflater mInflater;
  private int mResource;
  private int[] mTo;
  private ArrayList<Map<String, ?>> mUnfilteredData;
  private ViewBinder mViewBinder;
  
  public SimpleAdapter(Context paramContext, List<? extends Map<String, ?>> paramList, int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    this.mData = paramList;
    this.mDropDownResource = paramInt;
    this.mResource = paramInt;
    this.mFrom = paramArrayOfString;
    this.mTo = paramArrayOfInt;
    this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
  }
  
  private void bindView(int paramInt, View paramView)
  {
    Map localMap = (Map)this.mData.get(paramInt);
    if (localMap == null) {
      return;
    }
    ViewBinder localViewBinder = this.mViewBinder;
    String[] arrayOfString = this.mFrom;
    int[] arrayOfInt = this.mTo;
    int i = arrayOfInt.length;
    for (paramInt = 0; paramInt < i; paramInt++)
    {
      View localView = paramView.findViewById(arrayOfInt[paramInt]);
      if (localView != null)
      {
        Object localObject1 = localMap.get(arrayOfString[paramInt]);
        Object localObject2;
        if (localObject1 == null) {
          localObject2 = "";
        } else {
          localObject2 = localObject1.toString();
        }
        Object localObject3 = localObject2;
        if (localObject2 == null) {
          localObject3 = "";
        }
        boolean bool = false;
        if (localViewBinder != null) {
          bool = localViewBinder.setViewValue(localView, localObject1, (String)localObject3);
        }
        if (!bool) {
          if ((localView instanceof Checkable))
          {
            if ((localObject1 instanceof Boolean))
            {
              ((Checkable)localView).setChecked(((Boolean)localObject1).booleanValue());
            }
            else if ((localView instanceof TextView))
            {
              setViewText((TextView)localView, (String)localObject3);
            }
            else
            {
              localObject2 = new StringBuilder();
              ((StringBuilder)localObject2).append(localView.getClass().getName());
              ((StringBuilder)localObject2).append(" should be bound to a Boolean, not a ");
              if (localObject1 == null) {
                paramView = "<unknown type>";
              } else {
                paramView = localObject1.getClass();
              }
              ((StringBuilder)localObject2).append(paramView);
              throw new IllegalStateException(((StringBuilder)localObject2).toString());
            }
          }
          else if ((localView instanceof TextView))
          {
            setViewText((TextView)localView, (String)localObject3);
          }
          else if ((localView instanceof ImageView))
          {
            if ((localObject1 instanceof Integer)) {
              setViewImage((ImageView)localView, ((Integer)localObject1).intValue());
            } else {
              setViewImage((ImageView)localView, (String)localObject3);
            }
          }
          else
          {
            paramView = new StringBuilder();
            paramView.append(localView.getClass().getName());
            paramView.append(" is not a  view that can be bounds by this SimpleAdapter");
            throw new IllegalStateException(paramView.toString());
          }
        }
      }
    }
  }
  
  private View createViewFromResource(LayoutInflater paramLayoutInflater, int paramInt1, View paramView, ViewGroup paramViewGroup, int paramInt2)
  {
    if (paramView == null) {
      paramLayoutInflater = paramLayoutInflater.inflate(paramInt2, paramViewGroup, false);
    } else {
      paramLayoutInflater = paramView;
    }
    bindView(paramInt1, paramLayoutInflater);
    return paramLayoutInflater;
  }
  
  public int getCount()
  {
    return this.mData.size();
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
      this.mFilter = new SimpleFilter(null);
    }
    return this.mFilter;
  }
  
  public Object getItem(int paramInt)
  {
    return this.mData.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    return createViewFromResource(this.mInflater, paramInt, paramView, paramViewGroup, this.mResource);
  }
  
  public ViewBinder getViewBinder()
  {
    return this.mViewBinder;
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
      this.mDropDownInflater = LayoutInflater.from(new ContextThemeWrapper(this.mInflater.getContext(), paramTheme));
    }
  }
  
  public void setViewBinder(ViewBinder paramViewBinder)
  {
    this.mViewBinder = paramViewBinder;
  }
  
  public void setViewImage(ImageView paramImageView, int paramInt)
  {
    paramImageView.setImageResource(paramInt);
  }
  
  public void setViewImage(ImageView paramImageView, String paramString)
  {
    try
    {
      paramImageView.setImageResource(Integer.parseInt(paramString));
    }
    catch (NumberFormatException localNumberFormatException)
    {
      paramImageView.setImageURI(Uri.parse(paramString));
    }
  }
  
  public void setViewText(TextView paramTextView, String paramString)
  {
    paramTextView.setText(paramString);
  }
  
  private class SimpleFilter
    extends Filter
  {
    private SimpleFilter() {}
    
    protected Filter.FilterResults performFiltering(CharSequence paramCharSequence)
    {
      Filter.FilterResults localFilterResults = new Filter.FilterResults();
      Object localObject;
      if (SimpleAdapter.this.mUnfilteredData == null)
      {
        localObject = SimpleAdapter.this;
        SimpleAdapter.access$102((SimpleAdapter)localObject, new ArrayList(((SimpleAdapter)localObject).mData));
      }
      if ((paramCharSequence != null) && (paramCharSequence.length() != 0))
      {
        String str = paramCharSequence.toString().toLowerCase();
        paramCharSequence = SimpleAdapter.this.mUnfilteredData;
        int i = paramCharSequence.size();
        ArrayList localArrayList = new ArrayList(i);
        for (int j = 0; j < i; j++)
        {
          Map localMap = (Map)paramCharSequence.get(j);
          if (localMap != null)
          {
            int k = SimpleAdapter.this.mTo.length;
            for (int m = 0; m < k; m++)
            {
              localObject = ((String)localMap.get(SimpleAdapter.this.mFrom[m])).split(" ");
              int n = localObject.length;
              for (int i1 = 0; i1 < n; i1++) {
                if (localObject[i1].toLowerCase().startsWith(str))
                {
                  localArrayList.add(localMap);
                  break;
                }
              }
            }
          }
        }
        localFilterResults.values = localArrayList;
        localFilterResults.count = localArrayList.size();
      }
      else
      {
        paramCharSequence = SimpleAdapter.this.mUnfilteredData;
        localFilterResults.values = paramCharSequence;
        localFilterResults.count = paramCharSequence.size();
      }
      return localFilterResults;
    }
    
    protected void publishResults(CharSequence paramCharSequence, Filter.FilterResults paramFilterResults)
    {
      SimpleAdapter.access$202(SimpleAdapter.this, (List)paramFilterResults.values);
      if (paramFilterResults.count > 0) {
        SimpleAdapter.this.notifyDataSetChanged();
      } else {
        SimpleAdapter.this.notifyDataSetInvalidated();
      }
    }
  }
  
  public static abstract interface ViewBinder
  {
    public abstract boolean setViewValue(View paramView, Object paramObject, String paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SimpleAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */