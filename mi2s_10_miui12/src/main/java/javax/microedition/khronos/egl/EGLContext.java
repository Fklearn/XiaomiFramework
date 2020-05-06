package javax.microedition.khronos.egl;

import com.google.android.gles_jni.EGLImpl;
import javax.microedition.khronos.opengles.GL;

public abstract class EGLContext
{
  private static final EGL EGL_INSTANCE = new EGLImpl();
  
  public static EGL getEGL()
  {
    return EGL_INSTANCE;
  }
  
  public abstract GL getGL();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/javax/microedition/khronos/egl/EGLContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */