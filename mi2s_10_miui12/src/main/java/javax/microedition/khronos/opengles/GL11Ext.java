package javax.microedition.khronos.opengles;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public abstract interface GL11Ext
  extends GL
{
  public static final int GL_MATRIX_INDEX_ARRAY_BUFFER_BINDING_OES = 35742;
  public static final int GL_MATRIX_INDEX_ARRAY_OES = 34884;
  public static final int GL_MATRIX_INDEX_ARRAY_POINTER_OES = 34889;
  public static final int GL_MATRIX_INDEX_ARRAY_SIZE_OES = 34886;
  public static final int GL_MATRIX_INDEX_ARRAY_STRIDE_OES = 34888;
  public static final int GL_MATRIX_INDEX_ARRAY_TYPE_OES = 34887;
  public static final int GL_MATRIX_PALETTE_OES = 34880;
  public static final int GL_MAX_PALETTE_MATRICES_OES = 34882;
  public static final int GL_MAX_VERTEX_UNITS_OES = 34468;
  public static final int GL_TEXTURE_CROP_RECT_OES = 35741;
  public static final int GL_WEIGHT_ARRAY_BUFFER_BINDING_OES = 34974;
  public static final int GL_WEIGHT_ARRAY_OES = 34477;
  public static final int GL_WEIGHT_ARRAY_POINTER_OES = 34476;
  public static final int GL_WEIGHT_ARRAY_SIZE_OES = 34475;
  public static final int GL_WEIGHT_ARRAY_STRIDE_OES = 34474;
  public static final int GL_WEIGHT_ARRAY_TYPE_OES = 34473;
  
  public abstract void glCurrentPaletteMatrixOES(int paramInt);
  
  public abstract void glDrawTexfOES(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5);
  
  public abstract void glDrawTexfvOES(FloatBuffer paramFloatBuffer);
  
  public abstract void glDrawTexfvOES(float[] paramArrayOfFloat, int paramInt);
  
  public abstract void glDrawTexiOES(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public abstract void glDrawTexivOES(IntBuffer paramIntBuffer);
  
  public abstract void glDrawTexivOES(int[] paramArrayOfInt, int paramInt);
  
  public abstract void glDrawTexsOES(short paramShort1, short paramShort2, short paramShort3, short paramShort4, short paramShort5);
  
  public abstract void glDrawTexsvOES(ShortBuffer paramShortBuffer);
  
  public abstract void glDrawTexsvOES(short[] paramArrayOfShort, int paramInt);
  
  public abstract void glDrawTexxOES(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public abstract void glDrawTexxvOES(IntBuffer paramIntBuffer);
  
  public abstract void glDrawTexxvOES(int[] paramArrayOfInt, int paramInt);
  
  public abstract void glEnable(int paramInt);
  
  public abstract void glEnableClientState(int paramInt);
  
  public abstract void glLoadPaletteFromModelViewMatrixOES();
  
  public abstract void glMatrixIndexPointerOES(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void glMatrixIndexPointerOES(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer);
  
  public abstract void glTexParameterfv(int paramInt1, int paramInt2, float[] paramArrayOfFloat, int paramInt3);
  
  public abstract void glWeightPointerOES(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void glWeightPointerOES(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/javax/microedition/khronos/opengles/GL11Ext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */