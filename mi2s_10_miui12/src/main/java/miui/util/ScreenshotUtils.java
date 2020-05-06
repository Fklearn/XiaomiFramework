package miui.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MiuiSettings.Global;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import miui.graphics.BitmapFactory;
import miui.os.Build;
import miui.system.R.color;

public class ScreenshotUtils
{
  private static final File ACTIVITY_SCREENSHOT_FOLDER = new File("/data/system/app_screenshot");
  private static final String ACTIVITY_SCREENSHOT_FOLDER_PATH = "/data/system/app_screenshot";
  public static final float BLUR_SCALE_RATIO = 0.33333334F;
  public static final int DEFAULT_SCREENSHOT_COLOR = -1426063361;
  public static final int DEFAULT_SCREEN_BLUR_RADIUS;
  public static final float REAL_BLUR_BLACK;
  public static final int REAL_BLUR_MINIFY;
  public static final int REAL_BLUR_RADIUS;
  private static final String TAG = "ScreenshotUtils";
  private static SoftReference<Bitmap> sCacheBitmap;
  private static SoftReference<Bitmap> sCacheBitmapWithNavigationBarHide;
  private static SoftReference<Bitmap> sCacheBitmapWithNavigationBarShow;
  private static Display sDisplay;
  private static Handler sHandler;
  private static HandlerThread sHandlerThread;
  private static KeyguardManager sKeyguardManager;
  private static Paint sPaint;
  private static int sScreenHeight;
  private static int sScreenWidth;
  private static Point sSizeBuf = new Point();
  
  static
  {
    DEFAULT_SCREEN_BLUR_RADIUS = Resources.getSystem().getDimensionPixelSize(285606127);
    REAL_BLUR_BLACK = SystemProperties.getInt("persist.sys.real_blur_black", 0) / 100.0F;
    REAL_BLUR_MINIFY = SystemProperties.getInt("persist.sys.real_blur_minify", 4);
    REAL_BLUR_RADIUS = SystemProperties.getInt("persist.sys.real_blur_radius", 8);
  }
  
  public static void captureActivityScreenshot(Context paramContext, String paramString)
  {
    captureActivityScreenshot(paramContext, paramString, Boolean.valueOf(true));
  }
  
  public static void captureActivityScreenshot(Context paramContext, String paramString, Boolean paramBoolean)
  {
    initializeIfNeed(paramContext);
    if (sKeyguardManager.isKeyguardLocked()) {
      return;
    }
    final boolean bool1 = disallowTaskManagerScreenshotMode(paramContext);
    final boolean bool2 = true;
    if ((bool1) || (!SystemProperties.getBoolean("persist.sys.screenshot_mode", false)))
    {
      if (getActivityScreenshotFile(paramString, true).exists()) {
        return;
      }
      if (getActivityScreenshotFile(paramString, false).exists()) {
        return;
      }
    }
    final int i = sDisplay.getRotation();
    bool1 = bool2;
    if (i != 0) {
      if (i == 2) {
        bool1 = bool2;
      } else {
        bool1 = false;
      }
    }
    if (sHandler == null) {
      try
      {
        if (sHandler == null)
        {
          paramContext = new android/os/HandlerThread;
          paramContext.<init>("ScreenshotUtils");
          sHandlerThread = paramContext;
          sHandlerThread.start();
          paramContext = new android/os/Handler;
          paramContext.<init>(sHandlerThread.getLooper());
          sHandler = paramContext;
        }
      }
      finally {}
    }
    bool2 = false;
    try
    {
      boolean bool3 = CompatibilityHelper.hasNavigationBar(sDisplay.getDisplayId());
      bool2 = bool3;
    }
    catch (RemoteException paramContext)
    {
      paramContext.printStackTrace();
    }
    if (paramBoolean.booleanValue()) {
      sHandler.post(new Runnable()
      {
        public void run()
        {
          ScreenshotUtils.screenShotAndSave(ScreenshotUtils.this, i, bool1, bool2);
        }
      });
    } else {
      screenShotAndSave(paramString, i, bool1, bool2);
    }
  }
  
  public static boolean disallowTaskManagerScreenshotMode(Context paramContext)
  {
    if (!MiuiFeatureUtils.isLiteMode()) {
      return false;
    }
    return FeatureParser.getBoolean("enable_miui_lite", false);
  }
  
  public static File getActivityScreenshotFile(String paramString, boolean paramBoolean)
  {
    File localFile = ACTIVITY_SCREENSHOT_FOLDER;
    String str = paramString.replace('/', '-');
    if (paramBoolean) {
      paramString = "p";
    } else {
      paramString = "l";
    }
    return new File(localFile, String.format("%s--%s", new Object[] { str, paramString }));
  }
  
  public static void getActivityScreenshotSize(Context paramContext, Point paramPoint)
  {
    initializeIfNeed(paramContext);
    sDisplay.getRealSize(paramPoint);
    float f = Resources.getSystem().getFloat(285605918);
    paramPoint.x = ((int)(paramPoint.x * f + 0.5F));
    paramPoint.y = ((int)(paramPoint.y * f + 0.5F));
  }
  
  public static Bitmap getBlurBackground(Context paramContext, Bitmap paramBitmap)
  {
    paramContext = getScreenshot(paramContext, 0.33333334F, 0, 0, false);
    paramBitmap = getBlurBackground(paramContext, paramBitmap);
    paramContext.recycle();
    return paramBitmap;
  }
  
  public static Bitmap getBlurBackground(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    Bitmap localBitmap = paramBitmap2;
    if (paramBitmap1 != null) {
      localBitmap = BitmapFactory.fastBlur(paramBitmap1, paramBitmap2, Resources.getSystem().getDimensionPixelSize(285606127));
    }
    if (localBitmap != null) {
      new Canvas(localBitmap).drawColor(Resources.getSystem().getColor(R.color.blur_background_mask));
    }
    return localBitmap;
  }
  
  private static SoftReference<Bitmap> getCacheBitmap(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return sCacheBitmap;
    }
    Object localObject = new Point();
    sDisplay.getSize((Point)localObject);
    int i = sDisplay.getRotation();
    if ((i != 0) && (i != 2)) {
      i = 0;
    } else {
      i = 1;
    }
    if (i != 0) {
      i = ((Point)localObject).y;
    } else {
      i = ((Point)localObject).x;
    }
    if (i == sScreenHeight) {
      localObject = sCacheBitmapWithNavigationBarHide;
    } else {
      localObject = sCacheBitmapWithNavigationBarShow;
    }
    return (SoftReference<Bitmap>)localObject;
  }
  
  public static Bitmap getScreenshot(Context paramContext)
  {
    return getScreenshot(paramContext, 1.0F, 0, 0, true);
  }
  
  public static Bitmap getScreenshot(Context paramContext, float paramFloat, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    initializeIfNeed(paramContext);
    int i = (int)(sScreenWidth * paramFloat + 0.5F);
    int j = (int)(sScreenHeight * paramFloat + 0.5F);
    Bitmap localBitmap1;
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      localBitmap1 = CompatibilityHelper.screenshot(i, j);
    } else {
      localBitmap1 = CompatibilityHelper.screenshot(i, j, paramInt1, paramInt2);
    }
    Bitmap localBitmap2 = localBitmap1;
    if (localBitmap1 != null)
    {
      int k = sDisplay.getRotation();
      if ((k != 0) && (k != 2)) {
        paramInt2 = 0;
      } else {
        paramInt2 = 1;
      }
      if (paramBoolean)
      {
        sDisplay.getRealSize(sSizeBuf);
      }
      else
      {
        m = 0;
        int n = 0;
        try
        {
          boolean bool = CompatibilityHelper.hasNavigationBar(sDisplay.getDisplayId());
          paramBoolean = bool;
          if (bool)
          {
            paramBoolean = bool;
            if (MiuiSettings.Global.getBoolean(paramContext.getContentResolver(), "force_fsg_nav_bar")) {
              paramBoolean = false;
            }
          }
          paramInt1 = n;
          if (paramBoolean)
          {
            int i1 = paramContext.getResources().getIdentifier("navigation_bar_size", "dimen", "android");
            paramInt1 = n;
            if (i1 > 0) {
              paramInt1 = paramContext.getResources().getDimensionPixelSize(i1);
            }
          }
        }
        catch (RemoteException paramContext)
        {
          paramInt1 = m;
        }
        sDisplay.getRealSize(sSizeBuf);
        if (Build.IS_TABLET)
        {
          paramContext = sSizeBuf;
          paramContext.y -= paramInt1;
        }
        else if (paramInt2 != 0)
        {
          paramContext = sSizeBuf;
          paramContext.y -= paramInt1;
        }
        else
        {
          paramContext = sSizeBuf;
          paramContext.x -= paramInt1;
        }
      }
      paramInt1 = (int)(sSizeBuf.x * paramFloat + 0.5F);
      int m = (int)(sSizeBuf.y * paramFloat + 0.5F);
      if ((i == paramInt1) && (j == m))
      {
        localBitmap2 = localBitmap1;
        if (k == 0) {}
      }
      else
      {
        paramContext = new Matrix();
        if (k != 0)
        {
          paramContext.postTranslate(-i / 2.0F, -j / 2.0F);
          paramContext.postRotate(360 - k * 90);
          if (paramInt2 != 0) {
            paramFloat = i;
          } else {
            paramFloat = j;
          }
          float f = paramFloat / 2.0F;
          if (paramInt2 != 0) {
            paramFloat = j;
          } else {
            paramFloat = i;
          }
          paramContext.postTranslate(f, paramFloat / 2.0F);
        }
        localBitmap2 = Bitmap.createBitmap(paramInt1, m, Bitmap.Config.ARGB_8888);
        new Canvas(localBitmap2).drawBitmap(localBitmap1, paramContext, new Paint());
        localBitmap1.recycle();
      }
    }
    return localBitmap2;
  }
  
  private static void initializeIfNeed(Context paramContext)
  {
    if ((!ACTIVITY_SCREENSHOT_FOLDER.exists()) && (ACTIVITY_SCREENSHOT_FOLDER.mkdir())) {
      FileUtils.setPermissions(ACTIVITY_SCREENSHOT_FOLDER.getAbsolutePath(), 509, -1, -1);
    }
    if (sDisplay == null) {
      sDisplay = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    }
    if (sKeyguardManager == null) {
      sKeyguardManager = (KeyguardManager)paramContext.getSystemService("keyguard");
    }
    paramContext = sDisplay;
    if (paramContext != null)
    {
      int i = paramContext.getRotation();
      if ((i != 0) && (i != 2)) {
        i = 0;
      } else {
        i = 1;
      }
      CustomizeUtil.getRealSize(sDisplay, sSizeBuf);
      paramContext = sSizeBuf;
      int j;
      if (i != 0) {
        j = paramContext.x;
      } else {
        j = paramContext.y;
      }
      sScreenWidth = j;
      paramContext = sSizeBuf;
      if (i != 0) {
        i = paramContext.y;
      } else {
        i = paramContext.x;
      }
      sScreenHeight = i;
    }
  }
  
  private static void screenShotAndSave(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      Bitmap localBitmap = CompatibilityHelper.screenshot(sScreenWidth, sScreenHeight, 0, 30000);
      Object localObject1 = new android/graphics/Point;
      ((Point)localObject1).<init>();
      sDisplay.getSize((Point)localObject1);
      int i;
      if (paramBoolean1) {
        i = ((Point)localObject1).x;
      } else {
        i = ((Point)localObject1).y;
      }
      int j;
      if (paramBoolean1) {
        j = ((Point)localObject1).y;
      } else {
        j = ((Point)localObject1).x;
      }
      float f = Resources.getSystem().getFloat(285605918);
      if (paramBoolean2)
      {
        if ((paramInt != 3) && (paramInt != 2))
        {
          localObject1 = new Rect(0, 0, i, j);
        }
        else
        {
          localObject1 = new android/graphics/Rect;
          ((Rect)localObject1).<init>(0, sScreenHeight - j, i, sScreenHeight);
        }
      }
      else {
        localObject1 = new Rect(0, 0, sScreenWidth, sScreenHeight);
      }
      Object localObject2;
      if (getCacheBitmap(paramBoolean2) == null) {
        localObject2 = null;
      } else {
        localObject2 = (Bitmap)getCacheBitmap(paramBoolean2).get();
      }
      Object localObject3 = localObject2;
      if (localObject2 == null)
      {
        int k;
        if (paramBoolean2) {
          k = i;
        } else {
          k = sScreenWidth;
        }
        int m = (int)(k * f + 0.5F);
        if (paramBoolean2) {
          k = j;
        } else {
          k = sScreenHeight;
        }
        localObject3 = Bitmap.createBitmap(m, (int)(k * f + 0.5F), Bitmap.Config.ARGB_8888);
        localObject2 = new java/lang/ref/SoftReference;
        ((SoftReference)localObject2).<init>(localObject3);
        setCacheBitmap((SoftReference)localObject2, paramBoolean2);
      }
      if (sPaint == null)
      {
        localObject2 = new android/graphics/Paint;
        ((Paint)localObject2).<init>(3);
        sPaint = (Paint)localObject2;
      }
      Canvas localCanvas = new android/graphics/Canvas;
      localCanvas.<init>((Bitmap)localObject3);
      if ((paramInt != 1) && (paramInt != 2)) {
        break label352;
      }
      localCanvas.rotate(180.0F, ((Bitmap)localObject3).getWidth() / 2.0F, ((Bitmap)localObject3).getHeight() / 2.0F);
      label352:
      localCanvas.scale(f, f);
      if (localBitmap != null)
      {
        localObject2 = new android/graphics/Rect;
        if (paramBoolean2) {
          paramInt = i;
        } else {
          paramInt = sScreenWidth;
        }
        if (!paramBoolean2) {
          j = sScreenHeight;
        }
        ((Rect)localObject2).<init>(0, 0, paramInt, j);
        localCanvas.drawBitmap(localBitmap, (Rect)localObject1, (Rect)localObject2, sPaint);
        localBitmap.recycle();
      }
      else
      {
        localCanvas.drawColor(-1426063361, PorterDuff.Mode.SRC);
      }
      paramBoolean2 = false;
      localObject1 = new java/io/FileOutputStream;
      ((FileOutputStream)localObject1).<init>(getActivityScreenshotFile(paramString, paramBoolean1));
      ((Bitmap)localObject3).compress(Bitmap.CompressFormat.JPEG, 90, (OutputStream)localObject1);
      ((FileOutputStream)localObject1).close();
      if (!paramBoolean1) {
        paramBoolean2 = true;
      }
      localObject1 = getActivityScreenshotFile(paramString, paramBoolean2);
      if (((File)localObject1).exists()) {
        if (((File)localObject1).delete())
        {
          paramString = new java/lang/StringBuilder;
          paramString.<init>();
          paramString.append(((File)localObject1).getAbsolutePath());
          paramString.append("delete failed");
          Log.d("ScreenshotUtils", paramString.toString());
        }
        else {}
      }
    }
    catch (Exception paramString)
    {
      Log.d("ScreenshotUtils", "screenShotAndSave", paramString);
    }
  }
  
  private static void setCacheBitmap(SoftReference<Bitmap> paramSoftReference, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      sCacheBitmap = paramSoftReference;
      return;
    }
    Point localPoint = new Point();
    sDisplay.getSize(localPoint);
    int i = sDisplay.getRotation();
    if ((i != 0) && (i != 2)) {
      i = 0;
    } else {
      i = 1;
    }
    if (i != 0) {
      i = localPoint.y;
    } else {
      i = localPoint.x;
    }
    if (i == sScreenHeight) {
      sCacheBitmapWithNavigationBarHide = paramSoftReference;
    } else {
      sCacheBitmapWithNavigationBarShow = paramSoftReference;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ScreenshotUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */