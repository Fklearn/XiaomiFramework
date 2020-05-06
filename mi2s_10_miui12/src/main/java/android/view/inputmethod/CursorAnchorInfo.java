package android.view.inputmethod;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannedString;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.Objects;

public final class CursorAnchorInfo
  implements Parcelable
{
  public static final Parcelable.Creator<CursorAnchorInfo> CREATOR = new Parcelable.Creator()
  {
    public CursorAnchorInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CursorAnchorInfo(paramAnonymousParcel);
    }
    
    public CursorAnchorInfo[] newArray(int paramAnonymousInt)
    {
      return new CursorAnchorInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_HAS_INVISIBLE_REGION = 2;
  public static final int FLAG_HAS_VISIBLE_REGION = 1;
  public static final int FLAG_IS_RTL = 4;
  private final SparseRectFArray mCharacterBoundsArray;
  private final CharSequence mComposingText;
  private final int mComposingTextStart;
  private final int mHashCode;
  private final float mInsertionMarkerBaseline;
  private final float mInsertionMarkerBottom;
  private final int mInsertionMarkerFlags;
  private final float mInsertionMarkerHorizontal;
  private final float mInsertionMarkerTop;
  private final float[] mMatrixValues;
  private final int mSelectionEnd;
  private final int mSelectionStart;
  
  private CursorAnchorInfo(int paramInt1, int paramInt2, int paramInt3, CharSequence paramCharSequence, int paramInt4, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, SparseRectFArray paramSparseRectFArray, float[] paramArrayOfFloat)
  {
    this.mSelectionStart = paramInt1;
    this.mSelectionEnd = paramInt2;
    this.mComposingTextStart = paramInt3;
    this.mComposingText = paramCharSequence;
    this.mInsertionMarkerFlags = paramInt4;
    this.mInsertionMarkerHorizontal = paramFloat1;
    this.mInsertionMarkerTop = paramFloat2;
    this.mInsertionMarkerBaseline = paramFloat3;
    this.mInsertionMarkerBottom = paramFloat4;
    this.mCharacterBoundsArray = paramSparseRectFArray;
    this.mMatrixValues = paramArrayOfFloat;
    this.mHashCode = (Objects.hashCode(this.mComposingText) * 31 + Arrays.hashCode(paramArrayOfFloat));
  }
  
  public CursorAnchorInfo(Parcel paramParcel)
  {
    this.mHashCode = paramParcel.readInt();
    this.mSelectionStart = paramParcel.readInt();
    this.mSelectionEnd = paramParcel.readInt();
    this.mComposingTextStart = paramParcel.readInt();
    this.mComposingText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mInsertionMarkerFlags = paramParcel.readInt();
    this.mInsertionMarkerHorizontal = paramParcel.readFloat();
    this.mInsertionMarkerTop = paramParcel.readFloat();
    this.mInsertionMarkerBaseline = paramParcel.readFloat();
    this.mInsertionMarkerBottom = paramParcel.readFloat();
    this.mCharacterBoundsArray = ((SparseRectFArray)paramParcel.readParcelable(SparseRectFArray.class.getClassLoader()));
    this.mMatrixValues = paramParcel.createFloatArray();
  }
  
  private static boolean areSameFloatImpl(float paramFloat1, float paramFloat2)
  {
    boolean bool1 = Float.isNaN(paramFloat1);
    boolean bool2 = true;
    if ((bool1) && (Float.isNaN(paramFloat2))) {
      return true;
    }
    if (paramFloat1 != paramFloat2) {
      bool2 = false;
    }
    return bool2;
  }
  
  private static float[] computeMatrixValues(Matrix paramMatrix, CursorAnchorInfo paramCursorAnchorInfo)
  {
    if (paramMatrix.isIdentity()) {
      return paramCursorAnchorInfo.mMatrixValues;
    }
    Matrix localMatrix = new Matrix();
    localMatrix.setValues(paramCursorAnchorInfo.mMatrixValues);
    localMatrix.postConcat(paramMatrix);
    paramMatrix = new float[9];
    localMatrix.getValues(paramMatrix);
    return paramMatrix;
  }
  
  private static CursorAnchorInfo create(Builder paramBuilder)
  {
    SparseRectFArray localSparseRectFArray;
    if (paramBuilder.mCharacterBoundsArrayBuilder != null) {
      localSparseRectFArray = paramBuilder.mCharacterBoundsArrayBuilder.build();
    } else {
      localSparseRectFArray = null;
    }
    float[] arrayOfFloat = new float[9];
    if (paramBuilder.mMatrixInitialized) {
      System.arraycopy(paramBuilder.mMatrixValues, 0, arrayOfFloat, 0, 9);
    } else {
      Matrix.IDENTITY_MATRIX.getValues(arrayOfFloat);
    }
    return new CursorAnchorInfo(paramBuilder.mSelectionStart, paramBuilder.mSelectionEnd, paramBuilder.mComposingTextStart, paramBuilder.mComposingText, paramBuilder.mInsertionMarkerFlags, paramBuilder.mInsertionMarkerHorizontal, paramBuilder.mInsertionMarkerTop, paramBuilder.mInsertionMarkerBaseline, paramBuilder.mInsertionMarkerBottom, localSparseRectFArray, arrayOfFloat);
  }
  
  public static CursorAnchorInfo createForAdditionalParentMatrix(CursorAnchorInfo paramCursorAnchorInfo, Matrix paramMatrix)
  {
    return new CursorAnchorInfo(paramCursorAnchorInfo.mSelectionStart, paramCursorAnchorInfo.mSelectionEnd, paramCursorAnchorInfo.mComposingTextStart, paramCursorAnchorInfo.mComposingText, paramCursorAnchorInfo.mInsertionMarkerFlags, paramCursorAnchorInfo.mInsertionMarkerHorizontal, paramCursorAnchorInfo.mInsertionMarkerTop, paramCursorAnchorInfo.mInsertionMarkerBaseline, paramCursorAnchorInfo.mInsertionMarkerBottom, paramCursorAnchorInfo.mCharacterBoundsArray, computeMatrixValues(paramMatrix, paramCursorAnchorInfo));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CursorAnchorInfo)) {
      return false;
    }
    CursorAnchorInfo localCursorAnchorInfo = (CursorAnchorInfo)paramObject;
    if (hashCode() != localCursorAnchorInfo.hashCode()) {
      return false;
    }
    if ((this.mSelectionStart == localCursorAnchorInfo.mSelectionStart) && (this.mSelectionEnd == localCursorAnchorInfo.mSelectionEnd))
    {
      if ((this.mInsertionMarkerFlags == localCursorAnchorInfo.mInsertionMarkerFlags) && (areSameFloatImpl(this.mInsertionMarkerHorizontal, localCursorAnchorInfo.mInsertionMarkerHorizontal)) && (areSameFloatImpl(this.mInsertionMarkerTop, localCursorAnchorInfo.mInsertionMarkerTop)) && (areSameFloatImpl(this.mInsertionMarkerBaseline, localCursorAnchorInfo.mInsertionMarkerBaseline)) && (areSameFloatImpl(this.mInsertionMarkerBottom, localCursorAnchorInfo.mInsertionMarkerBottom)))
      {
        if (!Objects.equals(this.mCharacterBoundsArray, localCursorAnchorInfo.mCharacterBoundsArray)) {
          return false;
        }
        if ((this.mComposingTextStart == localCursorAnchorInfo.mComposingTextStart) && (Objects.equals(this.mComposingText, localCursorAnchorInfo.mComposingText)))
        {
          if (this.mMatrixValues.length != localCursorAnchorInfo.mMatrixValues.length) {
            return false;
          }
          for (int i = 0;; i++)
          {
            paramObject = this.mMatrixValues;
            if (i >= paramObject.length) {
              break;
            }
            if (paramObject[i] != localCursorAnchorInfo.mMatrixValues[i]) {
              return false;
            }
          }
          return true;
        }
        return false;
      }
      return false;
    }
    return false;
  }
  
  public RectF getCharacterBounds(int paramInt)
  {
    SparseRectFArray localSparseRectFArray = this.mCharacterBoundsArray;
    if (localSparseRectFArray == null) {
      return null;
    }
    return localSparseRectFArray.get(paramInt);
  }
  
  public int getCharacterBoundsFlags(int paramInt)
  {
    SparseRectFArray localSparseRectFArray = this.mCharacterBoundsArray;
    if (localSparseRectFArray == null) {
      return 0;
    }
    return localSparseRectFArray.getFlags(paramInt, 0);
  }
  
  public CharSequence getComposingText()
  {
    return this.mComposingText;
  }
  
  public int getComposingTextStart()
  {
    return this.mComposingTextStart;
  }
  
  public float getInsertionMarkerBaseline()
  {
    return this.mInsertionMarkerBaseline;
  }
  
  public float getInsertionMarkerBottom()
  {
    return this.mInsertionMarkerBottom;
  }
  
  public int getInsertionMarkerFlags()
  {
    return this.mInsertionMarkerFlags;
  }
  
  public float getInsertionMarkerHorizontal()
  {
    return this.mInsertionMarkerHorizontal;
  }
  
  public float getInsertionMarkerTop()
  {
    return this.mInsertionMarkerTop;
  }
  
  public Matrix getMatrix()
  {
    Matrix localMatrix = new Matrix();
    localMatrix.setValues(this.mMatrixValues);
    return localMatrix;
  }
  
  public int getSelectionEnd()
  {
    return this.mSelectionEnd;
  }
  
  public int getSelectionStart()
  {
    return this.mSelectionStart;
  }
  
  public int hashCode()
  {
    return this.mHashCode;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CursorAnchorInfo{mHashCode=");
    localStringBuilder.append(this.mHashCode);
    localStringBuilder.append(" mSelection=");
    localStringBuilder.append(this.mSelectionStart);
    localStringBuilder.append(",");
    localStringBuilder.append(this.mSelectionEnd);
    localStringBuilder.append(" mComposingTextStart=");
    localStringBuilder.append(this.mComposingTextStart);
    localStringBuilder.append(" mComposingText=");
    localStringBuilder.append(Objects.toString(this.mComposingText));
    localStringBuilder.append(" mInsertionMarkerFlags=");
    localStringBuilder.append(this.mInsertionMarkerFlags);
    localStringBuilder.append(" mInsertionMarkerHorizontal=");
    localStringBuilder.append(this.mInsertionMarkerHorizontal);
    localStringBuilder.append(" mInsertionMarkerTop=");
    localStringBuilder.append(this.mInsertionMarkerTop);
    localStringBuilder.append(" mInsertionMarkerBaseline=");
    localStringBuilder.append(this.mInsertionMarkerBaseline);
    localStringBuilder.append(" mInsertionMarkerBottom=");
    localStringBuilder.append(this.mInsertionMarkerBottom);
    localStringBuilder.append(" mCharacterBoundsArray=");
    localStringBuilder.append(Objects.toString(this.mCharacterBoundsArray));
    localStringBuilder.append(" mMatrix=");
    localStringBuilder.append(Arrays.toString(this.mMatrixValues));
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mHashCode);
    paramParcel.writeInt(this.mSelectionStart);
    paramParcel.writeInt(this.mSelectionEnd);
    paramParcel.writeInt(this.mComposingTextStart);
    TextUtils.writeToParcel(this.mComposingText, paramParcel, paramInt);
    paramParcel.writeInt(this.mInsertionMarkerFlags);
    paramParcel.writeFloat(this.mInsertionMarkerHorizontal);
    paramParcel.writeFloat(this.mInsertionMarkerTop);
    paramParcel.writeFloat(this.mInsertionMarkerBaseline);
    paramParcel.writeFloat(this.mInsertionMarkerBottom);
    paramParcel.writeParcelable(this.mCharacterBoundsArray, paramInt);
    paramParcel.writeFloatArray(this.mMatrixValues);
  }
  
  public static final class Builder
  {
    private SparseRectFArray.SparseRectFArrayBuilder mCharacterBoundsArrayBuilder = null;
    private CharSequence mComposingText = null;
    private int mComposingTextStart = -1;
    private float mInsertionMarkerBaseline = NaN.0F;
    private float mInsertionMarkerBottom = NaN.0F;
    private int mInsertionMarkerFlags = 0;
    private float mInsertionMarkerHorizontal = NaN.0F;
    private float mInsertionMarkerTop = NaN.0F;
    private boolean mMatrixInitialized = false;
    private float[] mMatrixValues = null;
    private int mSelectionEnd = -1;
    private int mSelectionStart = -1;
    
    public Builder addCharacterBounds(int paramInt1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2)
    {
      if (paramInt1 >= 0)
      {
        if (this.mCharacterBoundsArrayBuilder == null) {
          this.mCharacterBoundsArrayBuilder = new SparseRectFArray.SparseRectFArrayBuilder();
        }
        this.mCharacterBoundsArrayBuilder.append(paramInt1, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt2);
        return this;
      }
      throw new IllegalArgumentException("index must not be a negative integer.");
    }
    
    public CursorAnchorInfo build()
    {
      if (!this.mMatrixInitialized)
      {
        SparseRectFArray.SparseRectFArrayBuilder localSparseRectFArrayBuilder = this.mCharacterBoundsArrayBuilder;
        int i;
        if ((localSparseRectFArrayBuilder != null) && (!localSparseRectFArrayBuilder.isEmpty())) {
          i = 1;
        } else {
          i = 0;
        }
        if ((i != 0) || (!Float.isNaN(this.mInsertionMarkerHorizontal)) || (!Float.isNaN(this.mInsertionMarkerTop)) || (!Float.isNaN(this.mInsertionMarkerBaseline)) || (!Float.isNaN(this.mInsertionMarkerBottom))) {
          throw new IllegalArgumentException("Coordinate transformation matrix is required when positional parameters are specified.");
        }
      }
      return CursorAnchorInfo.create(this);
    }
    
    public void reset()
    {
      this.mSelectionStart = -1;
      this.mSelectionEnd = -1;
      this.mComposingTextStart = -1;
      this.mComposingText = null;
      this.mInsertionMarkerFlags = 0;
      this.mInsertionMarkerHorizontal = NaN.0F;
      this.mInsertionMarkerTop = NaN.0F;
      this.mInsertionMarkerBaseline = NaN.0F;
      this.mInsertionMarkerBottom = NaN.0F;
      this.mMatrixInitialized = false;
      SparseRectFArray.SparseRectFArrayBuilder localSparseRectFArrayBuilder = this.mCharacterBoundsArrayBuilder;
      if (localSparseRectFArrayBuilder != null) {
        localSparseRectFArrayBuilder.reset();
      }
    }
    
    public Builder setComposingText(int paramInt, CharSequence paramCharSequence)
    {
      this.mComposingTextStart = paramInt;
      if (paramCharSequence == null) {
        this.mComposingText = null;
      } else {
        this.mComposingText = new SpannedString(paramCharSequence);
      }
      return this;
    }
    
    public Builder setInsertionMarkerLocation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt)
    {
      this.mInsertionMarkerHorizontal = paramFloat1;
      this.mInsertionMarkerTop = paramFloat2;
      this.mInsertionMarkerBaseline = paramFloat3;
      this.mInsertionMarkerBottom = paramFloat4;
      this.mInsertionMarkerFlags = paramInt;
      return this;
    }
    
    public Builder setMatrix(Matrix paramMatrix)
    {
      if (this.mMatrixValues == null) {
        this.mMatrixValues = new float[9];
      }
      if (paramMatrix == null) {
        paramMatrix = Matrix.IDENTITY_MATRIX;
      }
      paramMatrix.getValues(this.mMatrixValues);
      this.mMatrixInitialized = true;
      return this;
    }
    
    public Builder setSelectionRange(int paramInt1, int paramInt2)
    {
      this.mSelectionStart = paramInt1;
      this.mSelectionEnd = paramInt2;
      return this;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/CursorAnchorInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */