package android.view.inputmethod;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class SparseRectFArray
  implements Parcelable
{
  public static final Parcelable.Creator<SparseRectFArray> CREATOR = new Parcelable.Creator()
  {
    public SparseRectFArray createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SparseRectFArray(paramAnonymousParcel);
    }
    
    public SparseRectFArray[] newArray(int paramAnonymousInt)
    {
      return new SparseRectFArray[paramAnonymousInt];
    }
  };
  private final float[] mCoordinates;
  private final int[] mFlagsArray;
  private final int[] mKeys;
  
  public SparseRectFArray(Parcel paramParcel)
  {
    this.mKeys = paramParcel.createIntArray();
    this.mCoordinates = paramParcel.createFloatArray();
    this.mFlagsArray = paramParcel.createIntArray();
  }
  
  private SparseRectFArray(SparseRectFArrayBuilder paramSparseRectFArrayBuilder)
  {
    if (paramSparseRectFArrayBuilder.mCount == 0)
    {
      this.mKeys = null;
      this.mCoordinates = null;
      this.mFlagsArray = null;
    }
    else
    {
      this.mKeys = new int[paramSparseRectFArrayBuilder.mCount];
      this.mCoordinates = new float[paramSparseRectFArrayBuilder.mCount * 4];
      this.mFlagsArray = new int[paramSparseRectFArrayBuilder.mCount];
      System.arraycopy(paramSparseRectFArrayBuilder.mKeys, 0, this.mKeys, 0, paramSparseRectFArrayBuilder.mCount);
      System.arraycopy(paramSparseRectFArrayBuilder.mCoordinates, 0, this.mCoordinates, 0, paramSparseRectFArrayBuilder.mCount * 4);
      System.arraycopy(paramSparseRectFArrayBuilder.mFlagsArray, 0, this.mFlagsArray, 0, paramSparseRectFArrayBuilder.mCount);
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SparseRectFArray)) {
      return false;
    }
    paramObject = (SparseRectFArray)paramObject;
    if ((Arrays.equals(this.mKeys, ((SparseRectFArray)paramObject).mKeys)) && (Arrays.equals(this.mCoordinates, ((SparseRectFArray)paramObject).mCoordinates)) && (Arrays.equals(this.mFlagsArray, ((SparseRectFArray)paramObject).mFlagsArray))) {
      bool = true;
    }
    return bool;
  }
  
  public RectF get(int paramInt)
  {
    Object localObject = this.mKeys;
    if (localObject == null) {
      return null;
    }
    if (paramInt < 0) {
      return null;
    }
    paramInt = Arrays.binarySearch((int[])localObject, paramInt);
    if (paramInt < 0) {
      return null;
    }
    paramInt *= 4;
    localObject = this.mCoordinates;
    return new RectF(localObject[paramInt], localObject[(paramInt + 1)], localObject[(paramInt + 2)], localObject[(paramInt + 3)]);
  }
  
  public int getFlags(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = this.mKeys;
    if (arrayOfInt == null) {
      return paramInt2;
    }
    if (paramInt1 < 0) {
      return paramInt2;
    }
    paramInt1 = Arrays.binarySearch(arrayOfInt, paramInt1);
    if (paramInt1 < 0) {
      return paramInt2;
    }
    return this.mFlagsArray[paramInt1];
  }
  
  public int hashCode()
  {
    int[] arrayOfInt = this.mKeys;
    if ((arrayOfInt != null) && (arrayOfInt.length != 0))
    {
      int i = arrayOfInt.length;
      for (int j = 0; j < 4; j++) {
        i = (int)(i * 31 + this.mCoordinates[j]);
      }
      return i * 31 + this.mFlagsArray[0];
    }
    return 0;
  }
  
  public String toString()
  {
    if ((this.mKeys != null) && (this.mCoordinates != null) && (this.mFlagsArray != null))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SparseRectFArray{");
      for (int i = 0; i < this.mKeys.length; i++)
      {
        if (i != 0) {
          localStringBuilder.append(", ");
        }
        int j = i * 4;
        localStringBuilder.append(this.mKeys[i]);
        localStringBuilder.append(":[");
        localStringBuilder.append(this.mCoordinates[(j + 0)]);
        localStringBuilder.append(",");
        localStringBuilder.append(this.mCoordinates[(j + 1)]);
        localStringBuilder.append("],[");
        localStringBuilder.append(this.mCoordinates[(j + 2)]);
        localStringBuilder.append(",");
        localStringBuilder.append(this.mCoordinates[(j + 3)]);
        localStringBuilder.append("]:flagsArray=");
        localStringBuilder.append(this.mFlagsArray[i]);
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    return "SparseRectFArray{}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeIntArray(this.mKeys);
    paramParcel.writeFloatArray(this.mCoordinates);
    paramParcel.writeIntArray(this.mFlagsArray);
  }
  
  public static final class SparseRectFArrayBuilder
  {
    private static int INITIAL_SIZE = 16;
    private float[] mCoordinates = null;
    private int mCount = 0;
    private int[] mFlagsArray = null;
    private int[] mKeys = null;
    
    private void checkIndex(int paramInt)
    {
      int i = this.mCount;
      if (i == 0) {
        return;
      }
      if (this.mKeys[(i - 1)] < paramInt) {
        return;
      }
      throw new IllegalArgumentException("key must be greater than all existing keys.");
    }
    
    private void ensureBufferSize()
    {
      if (this.mKeys == null) {
        this.mKeys = new int[INITIAL_SIZE];
      }
      if (this.mCoordinates == null) {
        this.mCoordinates = new float[INITIAL_SIZE * 4];
      }
      if (this.mFlagsArray == null) {
        this.mFlagsArray = new int[INITIAL_SIZE];
      }
      int i = this.mCount;
      int j = i + 1;
      Object localObject1 = this.mKeys;
      if (localObject1.length <= j)
      {
        localObject2 = new int[j * 2];
        System.arraycopy(localObject1, 0, localObject2, 0, i);
        this.mKeys = ((int[])localObject2);
      }
      i = this.mCount;
      int k = (i + 1) * 4;
      Object localObject2 = this.mCoordinates;
      if (localObject2.length <= k)
      {
        localObject1 = new float[k * 2];
        System.arraycopy(localObject2, 0, localObject1, 0, i * 4);
        this.mCoordinates = ((float[])localObject1);
      }
      localObject2 = this.mFlagsArray;
      if (localObject2.length <= j)
      {
        localObject1 = new int[j * 2];
        System.arraycopy(localObject2, 0, localObject1, 0, this.mCount);
        this.mFlagsArray = ((int[])localObject1);
      }
    }
    
    public SparseRectFArrayBuilder append(int paramInt1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2)
    {
      checkIndex(paramInt1);
      ensureBufferSize();
      int i = this.mCount;
      int j = i * 4;
      float[] arrayOfFloat = this.mCoordinates;
      arrayOfFloat[(j + 0)] = paramFloat1;
      arrayOfFloat[(j + 1)] = paramFloat2;
      arrayOfFloat[(j + 2)] = paramFloat3;
      arrayOfFloat[(j + 3)] = paramFloat4;
      j = this.mCount;
      this.mFlagsArray[j] = paramInt2;
      this.mKeys[i] = paramInt1;
      this.mCount = (i + 1);
      return this;
    }
    
    public SparseRectFArray build()
    {
      return new SparseRectFArray(this, null);
    }
    
    public boolean isEmpty()
    {
      boolean bool;
      if (this.mCount <= 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void reset()
    {
      if (this.mCount == 0)
      {
        this.mKeys = null;
        this.mCoordinates = null;
        this.mFlagsArray = null;
      }
      this.mCount = 0;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/SparseRectFArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */