package miui.slide;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.View;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AppSlideConfig
  implements Parcelable
{
  public static final Parcelable.Creator<AppSlideConfig> CREATOR = new Parcelable.Creator()
  {
    public AppSlideConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AppSlideConfig(paramAnonymousParcel, null);
    }
    
    public AppSlideConfig[] newArray(int paramAnonymousInt)
    {
      return new AppSlideConfig[paramAnonymousInt];
    }
  };
  public static final String TAG = "AppSlideConfig";
  public String mPackageName;
  public View mRecentClickView;
  private final List<SlideConfig> mSlideConfigArray = new ArrayList();
  
  public AppSlideConfig() {}
  
  private AppSlideConfig(Parcel paramParcel)
  {
    this.mPackageName = paramParcel.readString();
    paramParcel.readTypedList(this.mSlideConfigArray, SlideConfig.CREATOR);
  }
  
  public AppSlideConfig(String paramString)
  {
    this.mPackageName = paramString;
  }
  
  public void addSlideConfig(SlideConfig paramSlideConfig)
  {
    this.mSlideConfigArray.add(paramSlideConfig);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean matchStartingActivity(String paramString)
  {
    Iterator localIterator = this.mSlideConfigArray.iterator();
    while (localIterator.hasNext()) {
      if (((SlideConfig)localIterator.next()).mStartingActivity.equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public void matchVersionSlideConfig(int paramInt)
  {
    Object localObject1 = new ArrayList();
    Object localObject2 = new ArrayList();
    Object localObject3 = this.mSlideConfigArray.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (SlideConfig)((Iterator)localObject3).next();
      if (((SlideConfig)localObject4).mKeyCode == 700) {
        ((List)localObject1).add(localObject4);
      } else if (((SlideConfig)localObject4).mKeyCode == 701) {
        ((List)localObject2).add(localObject4);
      }
    }
    localObject3 = new ArrayList();
    int i = 0;
    localObject1 = ((List)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject4 = (SlideConfig)((Iterator)localObject1).next();
      if (paramInt >= ((SlideConfig)localObject4).mVersionCode) {
        if (i < ((SlideConfig)localObject4).mVersionCode)
        {
          ((List)localObject3).clear();
          ((List)localObject3).add(localObject4);
          i = ((SlideConfig)localObject4).mVersionCode;
        }
        else if (i == ((SlideConfig)localObject4).mVersionCode)
        {
          ((List)localObject3).add(localObject4);
        }
      }
    }
    localObject1 = new ArrayList();
    i = 0;
    Object localObject4 = ((List)localObject2).iterator();
    while (((Iterator)localObject4).hasNext())
    {
      localObject2 = (SlideConfig)((Iterator)localObject4).next();
      if (paramInt >= ((SlideConfig)localObject2).mVersionCode) {
        if (i < ((SlideConfig)localObject2).mVersionCode)
        {
          ((List)localObject1).clear();
          ((List)localObject1).add(localObject2);
          i = ((SlideConfig)localObject2).mVersionCode;
        }
        else if (i == ((SlideConfig)localObject2).mVersionCode)
        {
          ((List)localObject1).add(localObject2);
        }
      }
    }
    this.mSlideConfigArray.clear();
    this.mSlideConfigArray.addAll((Collection)localObject3);
    this.mSlideConfigArray.addAll((Collection)localObject1);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AppSlideConfig{mPackageName='");
    localStringBuilder.append(this.mPackageName);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public int tryGotoTarget(int paramInt, Activity paramActivity, View paramView)
  {
    int i = 0;
    Iterator localIterator = this.mSlideConfigArray.iterator();
    int j;
    for (;;)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
      SlideConfig localSlideConfig = (SlideConfig)localIterator.next();
      j = i;
      if (localSlideConfig.mKeyCode == paramInt)
      {
        j = i;
        if (localSlideConfig.mStartingActivity.equals(paramActivity.getComponentName().getClassName()))
        {
          i = localSlideConfig.tryGotoTarget(paramActivity, paramView);
          j = i;
          if (i > 0)
          {
            j = i;
            break;
          }
        }
      }
      i = j;
    }
    return j;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeTypedList(this.mSlideConfigArray);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/AppSlideConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */