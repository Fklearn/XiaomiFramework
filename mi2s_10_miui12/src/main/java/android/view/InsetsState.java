package android.view;

import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseIntArray;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

public class InsetsState
  implements Parcelable
{
  public static final Parcelable.Creator<InsetsState> CREATOR = new Parcelable.Creator()
  {
    public InsetsState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InsetsState(paramAnonymousParcel);
    }
    
    public InsetsState[] newArray(int paramAnonymousInt)
    {
      return new InsetsState[paramAnonymousInt];
    }
  };
  static final int FIRST_TYPE = 0;
  static final int INSET_SIDE_BOTTOM = 3;
  static final int INSET_SIDE_LEFT = 0;
  static final int INSET_SIDE_RIGHT = 2;
  static final int INSET_SIDE_TOP = 1;
  static final int INSET_SIDE_UNKNWON = 4;
  static final int LAST_TYPE = 10;
  public static final int TYPE_BOTTOM_GESTURES = 5;
  public static final int TYPE_BOTTOM_TAPPABLE_ELEMENT = 9;
  public static final int TYPE_IME = 10;
  public static final int TYPE_LEFT_GESTURES = 6;
  public static final int TYPE_NAVIGATION_BAR = 1;
  public static final int TYPE_RIGHT_GESTURES = 7;
  public static final int TYPE_SHELF = 1;
  public static final int TYPE_SIDE_BAR_1 = 1;
  public static final int TYPE_SIDE_BAR_2 = 2;
  public static final int TYPE_SIDE_BAR_3 = 3;
  public static final int TYPE_TOP_BAR = 0;
  public static final int TYPE_TOP_GESTURES = 4;
  public static final int TYPE_TOP_TAPPABLE_ELEMENT = 8;
  private final Rect mDisplayFrame = new Rect();
  private final ArrayMap<Integer, InsetsSource> mSources = new ArrayMap();
  
  public InsetsState() {}
  
  public InsetsState(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public InsetsState(InsetsState paramInsetsState)
  {
    set(paramInsetsState);
  }
  
  public InsetsState(InsetsState paramInsetsState, boolean paramBoolean)
  {
    set(paramInsetsState, paramBoolean);
  }
  
  public static boolean getDefaultVisibility(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2) && (paramInt != 3)) {
      return paramInt != 10;
    }
    return true;
  }
  
  private int getInsetSide(Insets paramInsets)
  {
    if (paramInsets.left != 0) {
      return 0;
    }
    if (paramInsets.top != 0) {
      return 1;
    }
    if (paramInsets.right != 0) {
      return 2;
    }
    if (paramInsets.bottom != 0) {
      return 3;
    }
    return 4;
  }
  
  private void processSource(InsetsSource paramInsetsSource, Rect paramRect, boolean paramBoolean, Insets[] paramArrayOfInsets, SparseIntArray paramSparseIntArray, boolean[] paramArrayOfBoolean)
  {
    paramRect = paramInsetsSource.calculateInsets(paramRect, paramBoolean);
    int i = toPublicType(paramInsetsSource.getType());
    processSourceAsPublicType(paramInsetsSource, paramArrayOfInsets, paramSparseIntArray, paramArrayOfBoolean, paramRect, i);
    if (i == 16) {
      processSourceAsPublicType(paramInsetsSource, paramArrayOfInsets, paramSparseIntArray, paramArrayOfBoolean, paramRect, 8);
    }
  }
  
  private void processSourceAsPublicType(InsetsSource paramInsetsSource, Insets[] paramArrayOfInsets, SparseIntArray paramSparseIntArray, boolean[] paramArrayOfBoolean, Insets paramInsets, int paramInt)
  {
    paramInt = WindowInsets.Type.indexOf(paramInt);
    Insets localInsets = paramArrayOfInsets[paramInt];
    if (localInsets == null) {
      paramArrayOfInsets[paramInt] = paramInsets;
    } else {
      paramArrayOfInsets[paramInt] = Insets.max(localInsets, paramInsets);
    }
    if (paramArrayOfBoolean != null) {
      paramArrayOfBoolean[paramInt] = paramInsetsSource.isVisible();
    }
    if ((paramSparseIntArray != null) && (!Insets.NONE.equals(paramInsets)) && (getInsetSide(paramInsets) != 4)) {
      paramSparseIntArray.put(paramInsetsSource.getType(), getInsetSide(paramInsets));
    }
  }
  
  public static ArraySet<Integer> toInternalType(int paramInt)
  {
    ArraySet localArraySet = new ArraySet();
    if ((paramInt & 0x1) != 0) {
      localArraySet.add(Integer.valueOf(0));
    }
    if ((paramInt & 0x4) != 0)
    {
      localArraySet.add(Integer.valueOf(1));
      localArraySet.add(Integer.valueOf(2));
      localArraySet.add(Integer.valueOf(3));
    }
    if ((paramInt & 0x2) != 0) {
      localArraySet.add(Integer.valueOf(10));
    }
    return localArraySet;
  }
  
  static int toPublicType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unknown type: ");
      localStringBuilder.append(paramInt);
      throw new IllegalArgumentException(localStringBuilder.toString());
    case 10: 
      return 2;
    case 8: 
    case 9: 
      return 32;
    case 6: 
    case 7: 
      return 8;
    case 4: 
    case 5: 
      return 16;
    case 1: 
    case 2: 
    case 3: 
      return 4;
    }
    return 1;
  }
  
  public static String typeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("TYPE_UNKNOWN_");
      localStringBuilder.append(paramInt);
      return localStringBuilder.toString();
    case 9: 
      return "TYPE_BOTTOM_TAPPABLE_ELEMENT";
    case 8: 
      return "TYPE_TOP_TAPPABLE_ELEMENT";
    case 7: 
      return "TYPE_RIGHT_GESTURES";
    case 6: 
      return "TYPE_LEFT_GESTURES";
    case 5: 
      return "TYPE_BOTTOM_GESTURES";
    case 4: 
      return "TYPE_TOP_GESTURES";
    case 3: 
      return "TYPE_SIDE_BAR_3";
    case 2: 
      return "TYPE_SIDE_BAR_2";
    case 1: 
      return "TYPE_SIDE_BAR_1";
    }
    return "TYPE_TOP_BAR";
  }
  
  public void addSource(InsetsSource paramInsetsSource)
  {
    this.mSources.put(Integer.valueOf(paramInsetsSource.getType()), paramInsetsSource);
  }
  
  public WindowInsets calculateInsets(Rect paramRect1, boolean paramBoolean1, boolean paramBoolean2, DisplayCutout paramDisplayCutout, Rect paramRect2, Rect paramRect3, int paramInt, SparseIntArray paramSparseIntArray)
  {
    Insets[] arrayOfInsets1 = new Insets[7];
    Insets[] arrayOfInsets2 = new Insets[7];
    boolean[] arrayOfBoolean = new boolean[7];
    Rect localRect = new Rect(paramRect1);
    paramRect1 = new Rect(paramRect1);
    if ((ViewRootImpl.sNewInsetsMode != 2) && (paramRect2 != null) && (paramRect3 != null))
    {
      WindowInsets.assignCompatInsets(arrayOfInsets1, paramRect2);
      WindowInsets.assignCompatInsets(arrayOfInsets2, paramRect3);
    }
    for (int i = 0; i <= 10; i++)
    {
      paramRect2 = (InsetsSource)this.mSources.get(Integer.valueOf(i));
      if (paramRect2 != null)
      {
        int j = ViewRootImpl.sNewInsetsMode;
        int k = 1;
        if ((j != 2) && ((i == 0) || (i == 1))) {
          j = 1;
        } else {
          j = 0;
        }
        int m;
        if ((paramRect2.getType() == 10) && ((paramInt & 0x10) == 0)) {
          m = 1;
        } else {
          m = 0;
        }
        if ((ViewRootImpl.sNewInsetsMode != 0) || ((toPublicType(i) & WindowInsets.Type.compatSystemInsets()) == 0)) {
          k = 0;
        }
        if ((j == 0) && (m == 0) && (k == 0))
        {
          processSource(paramRect2, localRect, false, arrayOfInsets1, paramSparseIntArray, arrayOfBoolean);
          if (paramRect2.getType() != 10) {
            processSource(paramRect2, paramRect1, true, arrayOfInsets2, null, null);
          }
        }
        else
        {
          arrayOfBoolean[WindowInsets.Type.indexOf(toPublicType(i))] = paramRect2.isVisible();
        }
      }
    }
    return new WindowInsets(arrayOfInsets1, arrayOfInsets2, arrayOfBoolean, paramBoolean1, paramBoolean2, paramDisplayCutout);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("InsetsState");
    paramPrintWriter.println(localStringBuilder.toString());
    for (int i = this.mSources.size() - 1; i >= 0; i--)
    {
      InsetsSource localInsetsSource = (InsetsSource)this.mSources.valueAt(i);
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("  ");
      localInsetsSource.dump(localStringBuilder.toString(), paramPrintWriter);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (InsetsState)paramObject;
      if (!this.mDisplayFrame.equals(((InsetsState)paramObject).mDisplayFrame)) {
        return false;
      }
      if (this.mSources.size() != ((InsetsState)paramObject).mSources.size()) {
        return false;
      }
      for (int i = this.mSources.size() - 1; i >= 0; i--)
      {
        InsetsSource localInsetsSource1 = (InsetsSource)this.mSources.valueAt(i);
        InsetsSource localInsetsSource2 = (InsetsSource)((InsetsState)paramObject).mSources.get(Integer.valueOf(localInsetsSource1.getType()));
        if (localInsetsSource2 == null) {
          return false;
        }
        if (!localInsetsSource2.equals(localInsetsSource1)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public Rect getDisplayFrame()
  {
    return this.mDisplayFrame;
  }
  
  public InsetsSource getSource(int paramInt)
  {
    return (InsetsSource)this.mSources.computeIfAbsent(Integer.valueOf(paramInt), _..Lambda.cZhmLzK8aetUdx4VlP9w5jR7En0.INSTANCE);
  }
  
  public int getSourcesCount()
  {
    return this.mSources.size();
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mDisplayFrame, this.mSources });
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.mSources.clear();
    this.mDisplayFrame.set((Rect)paramParcel.readParcelable(null));
    int i = paramParcel.readInt();
    for (int j = 0; j < i; j++)
    {
      InsetsSource localInsetsSource = (InsetsSource)paramParcel.readParcelable(null);
      this.mSources.put(Integer.valueOf(localInsetsSource.getType()), localInsetsSource);
    }
  }
  
  public void removeSource(int paramInt)
  {
    this.mSources.remove(Integer.valueOf(paramInt));
  }
  
  public void set(InsetsState paramInsetsState)
  {
    set(paramInsetsState, false);
  }
  
  public void set(InsetsState paramInsetsState, boolean paramBoolean)
  {
    this.mDisplayFrame.set(paramInsetsState.mDisplayFrame);
    this.mSources.clear();
    if (paramBoolean) {
      for (int i = 0; i < paramInsetsState.mSources.size(); i++)
      {
        InsetsSource localInsetsSource = (InsetsSource)paramInsetsState.mSources.valueAt(i);
        this.mSources.put(Integer.valueOf(localInsetsSource.getType()), new InsetsSource(localInsetsSource));
      }
    } else {
      this.mSources.putAll(paramInsetsState.mSources);
    }
  }
  
  public void setDisplayFrame(Rect paramRect)
  {
    this.mDisplayFrame.set(paramRect);
  }
  
  public InsetsSource sourceAt(int paramInt)
  {
    return (InsetsSource)this.mSources.valueAt(paramInt);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mDisplayFrame, paramInt);
    paramParcel.writeInt(this.mSources.size());
    for (int i = 0; i < this.mSources.size(); i++) {
      paramParcel.writeParcelable((Parcelable)this.mSources.valueAt(i), paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface InsetSide {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface InternalInsetType {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InsetsState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */