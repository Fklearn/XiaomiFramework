package miui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioSystem;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.miui.R.styleable;
import android.os.Build.VERSION;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import miui.util.FeatureParser;

public class SpectrumVisualizer
  extends ImageView
{
  private static final int CONSIDER_SAMPLE_LENGTH = 160;
  public static boolean IS_LPA_DECODE = SystemProperties.getBoolean("persist.sys.lpa.decode", false);
  private static final int RES_DEFAULT_SLIDING_DOT_BAR_ID = 285671816;
  private static final int RES_DEFAULT_SLIDING_PANEL_ID = 285671815;
  private static final int RES_DEFAULT_SLIDING_SHADOW_DOT_BAR_ID = 285671817;
  private static final String TAG = "SpectrumVisualizer";
  private static final int VISUALIZATION_SAMPLE_LENGTH = 256;
  private float INDEX_SCALE_FACTOR;
  private final int MAX_VALID_SAMPLE = 20;
  private float SAMPLE_SCALE_FACTOR;
  private float VISUALIZE_DESC_HEIGHT;
  int mAlphaWidthNum;
  private Bitmap mCachedBitmap;
  private Canvas mCachedCanvas;
  int mCellSize;
  int mDotbarHeight;
  private DotBarDrawer mDrawer;
  private boolean mEnableDrawing;
  private boolean mIsEnableUpdate;
  private boolean mIsNeedCareStreamActive;
  private Visualizer.OnDataCaptureListener mOnDataCaptureListener = new Visualizer.OnDataCaptureListener()
  {
    public void onFftDataCapture(Visualizer paramAnonymousVisualizer, byte[] paramAnonymousArrayOfByte, int paramAnonymousInt)
    {
      SpectrumVisualizer.this.update(paramAnonymousArrayOfByte);
    }
    
    public void onWaveFormDataCapture(Visualizer paramAnonymousVisualizer, byte[] paramAnonymousArrayOfByte, int paramAnonymousInt) {}
  };
  Paint mPaint = new Paint();
  int[] mPixels;
  float[] mPointData;
  private short[] mSampleBuf = new short[' '];
  int mShadowDotbarHeight;
  int[] mShadowPixels;
  private boolean mSoftDrawEnabled = true;
  private int mVisualizationHeight;
  int mVisualizationHeightNum;
  private int mVisualizationWidth;
  int mVisualizationWidthNum;
  private Visualizer mVisualizer;
  
  public SpectrumVisualizer(Context paramContext)
  {
    super(paramContext);
    init(paramContext, null);
  }
  
  public SpectrumVisualizer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet);
  }
  
  public SpectrumVisualizer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet);
  }
  
  private void drawInternal(Canvas paramCanvas)
  {
    this.mPaint.setAlpha(255);
    int i = this.mVisualizationWidthNum;
    int j = this.mAlphaWidthNum;
    for (int k = this.mAlphaWidthNum; k < i - j; k++) {
      this.mDrawer.drawDotBar(paramCanvas, k);
    }
    for (k = this.mAlphaWidthNum; k > 0; k--)
    {
      this.mPaint.setAlpha(k * 255 / this.mAlphaWidthNum);
      this.mDrawer.drawDotBar(paramCanvas, k - 1);
      this.mDrawer.drawDotBar(paramCanvas, this.mVisualizationWidthNum - k);
    }
  }
  
  private Bitmap drawToBitmap()
  {
    Object localObject1 = this.mCachedBitmap;
    Canvas localCanvas = this.mCachedCanvas;
    Object localObject2 = localObject1;
    if (localObject1 != null) {
      if (((Bitmap)localObject1).getWidth() == getWidth())
      {
        localObject2 = localObject1;
        if (((Bitmap)localObject1).getHeight() == getHeight()) {}
      }
      else
      {
        ((Bitmap)localObject1).recycle();
        localObject2 = null;
      }
    }
    localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
      this.mCachedBitmap = ((Bitmap)localObject1);
      localCanvas = new Canvas((Bitmap)localObject1);
      this.mCachedCanvas = localCanvas;
    }
    localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    drawInternal(localCanvas);
    return (Bitmap)localObject1;
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Drawable localDrawable = null;
    boolean bool = false;
    this.mEnableDrawing = true;
    this.mIsNeedCareStreamActive = true;
    this.mAlphaWidthNum = 0;
    if (paramAttributeSet != null)
    {
      paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SpectrumVisualizer);
      localObject1 = paramAttributeSet.getDrawable(3);
      localObject2 = paramAttributeSet.getDrawable(2);
      localDrawable = paramAttributeSet.getDrawable(4);
      bool = paramAttributeSet.getBoolean(5, false);
      this.mAlphaWidthNum = paramAttributeSet.getInt(0, this.mAlphaWidthNum);
      this.mIsEnableUpdate = paramAttributeSet.getBoolean(6, false);
      this.mIsNeedCareStreamActive = paramAttributeSet.getBoolean(1, false);
      paramAttributeSet.recycle();
    }
    paramAttributeSet = (AttributeSet)localObject1;
    if (localObject1 == null) {
      paramAttributeSet = paramContext.getResources().getDrawable(285671815);
    }
    localObject1 = ((BitmapDrawable)paramAttributeSet).getBitmap();
    paramAttributeSet = (AttributeSet)localObject2;
    if (localObject2 == null) {
      paramAttributeSet = paramContext.getResources().getDrawable(285671816);
    }
    localObject2 = ((BitmapDrawable)paramAttributeSet).getBitmap();
    paramAttributeSet = null;
    if (bool)
    {
      paramAttributeSet = localDrawable;
      if (localDrawable == null) {
        paramAttributeSet = paramContext.getResources().getDrawable(285671817);
      }
      paramAttributeSet = ((BitmapDrawable)paramAttributeSet).getBitmap();
    }
    setBitmaps((Bitmap)localObject1, (Bitmap)localObject2, paramAttributeSet);
  }
  
  public void enableDrawing(boolean paramBoolean)
  {
    this.mEnableDrawing = paramBoolean;
  }
  
  public void enableUpdate(boolean paramBoolean)
  {
    try
    {
      if (this.mIsEnableUpdate != paramBoolean)
      {
        if ((paramBoolean) && (this.mVisualizer == null))
        {
          if (IS_LPA_DECODE)
          {
            Log.v("SpectrumVisualizer", "lpa decode is on, can't enable");
          }
          else
          {
            Visualizer localVisualizer = new android/media/audiofx/Visualizer;
            localVisualizer.<init>(0);
            this.mVisualizer = localVisualizer;
            if (!this.mVisualizer.getEnabled())
            {
              this.mVisualizer.setCaptureSize(512);
              this.mVisualizer.setDataCaptureListener(this.mOnDataCaptureListener, Visualizer.getMaxCaptureRate(), false, true);
              this.mVisualizer.setEnabled(true);
            }
          }
        }
        else if ((!paramBoolean) && (this.mVisualizer != null))
        {
          this.mVisualizer.setEnabled(false);
          if ((Build.VERSION.SDK_INT < 22) && (!FeatureParser.getBoolean("is_xiaomi_device", false))) {
            Thread.sleep(50L);
          }
          this.mVisualizer.release();
          this.mVisualizer = null;
        }
        this.mIsEnableUpdate = paramBoolean;
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
    catch (RuntimeException localRuntimeException) {}catch (IllegalStateException localIllegalStateException) {}
  }
  
  public int getVisualHeight()
  {
    return this.mVisualizationHeight;
  }
  
  public int getVisualWidth()
  {
    return this.mVisualizationWidth;
  }
  
  public boolean isUpdateEnabled()
  {
    return this.mIsEnableUpdate;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (!this.mEnableDrawing) {
      return;
    }
    if (this.mSoftDrawEnabled) {
      paramCanvas.drawBitmap(drawToBitmap(), 0.0F, 0.0F, null);
    } else {
      drawInternal(paramCanvas);
    }
  }
  
  public void setAlphaNum(int paramInt)
  {
    if (paramInt <= 0)
    {
      this.mAlphaWidthNum = 0;
      return;
    }
    int i = this.mVisualizationWidthNum;
    if (paramInt > i / 2) {
      paramInt = i / 2;
    }
    this.mAlphaWidthNum = paramInt;
  }
  
  public void setBitmaps(int paramInt1, int paramInt2, Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    this.mVisualizationWidth = paramInt1;
    this.mVisualizationHeight = paramInt2;
    this.mCellSize = paramBitmap1.getWidth();
    this.mDotbarHeight = paramBitmap1.getHeight();
    paramInt2 = this.mDotbarHeight;
    paramInt1 = this.mVisualizationHeight;
    if (paramInt2 > paramInt1) {
      this.mDotbarHeight = paramInt1;
    }
    paramInt1 = this.mCellSize;
    paramInt2 = this.mDotbarHeight;
    this.mPixels = new int[paramInt1 * paramInt2];
    paramBitmap1.getPixels(this.mPixels, 0, paramInt1, 0, 0, paramInt1, paramInt2);
    paramInt2 = this.mVisualizationWidth;
    paramInt1 = this.mCellSize;
    this.mVisualizationWidthNum = (paramInt2 / paramInt1);
    this.mVisualizationHeightNum = (this.mDotbarHeight / paramInt1);
    this.SAMPLE_SCALE_FACTOR = (20.0F / this.mVisualizationHeightNum);
    this.INDEX_SCALE_FACTOR = ((float)Math.log(this.mVisualizationWidthNum / 3));
    this.VISUALIZE_DESC_HEIGHT = (1.0F / this.mVisualizationHeightNum);
    paramInt1 = this.mVisualizationWidthNum;
    this.mPointData = new float[paramInt1];
    if (this.mAlphaWidthNum == 0) {
      this.mAlphaWidthNum = (paramInt1 / 2);
    }
    this.mShadowPixels = null;
    if (paramBitmap2 != null)
    {
      this.mShadowDotbarHeight = paramBitmap2.getHeight();
      paramInt2 = this.mShadowDotbarHeight;
      paramInt1 = this.mDotbarHeight;
      int i = this.mVisualizationHeight;
      if (paramInt2 + paramInt1 > i) {
        this.mShadowDotbarHeight = (i - paramInt1);
      }
      paramInt2 = this.mShadowDotbarHeight;
      paramInt1 = this.mCellSize;
      if (paramInt2 < paramInt1)
      {
        this.mDrawer = new AsymmetryDotBar();
        return;
      }
      this.mShadowPixels = new int[paramInt1 * paramInt2];
      paramBitmap2.getPixels(this.mShadowPixels, 0, paramInt1, 0, 0, paramInt1, paramInt2);
      this.mDrawer = new SymmetryDotBar();
    }
    else
    {
      this.mDrawer = new AsymmetryDotBar();
    }
  }
  
  public void setBitmaps(Bitmap paramBitmap1, Bitmap paramBitmap2, Bitmap paramBitmap3)
  {
    setImageBitmap(paramBitmap1);
    setBitmaps(paramBitmap1.getWidth(), paramBitmap1.getHeight(), paramBitmap2, paramBitmap3);
  }
  
  public void setSoftDrawEnabled(boolean paramBoolean)
  {
    this.mSoftDrawEnabled = paramBoolean;
    if (!paramBoolean)
    {
      Bitmap localBitmap = this.mCachedBitmap;
      if (localBitmap != null)
      {
        localBitmap.recycle();
        this.mCachedBitmap = null;
        this.mCachedCanvas = null;
      }
    }
  }
  
  void update(byte[] paramArrayOfByte)
  {
    if ((this.mIsNeedCareStreamActive) && (!AudioSystem.isStreamActive(3, 0)))
    {
      enableDrawing(false);
      return;
    }
    enableDrawing(true);
    if (paramArrayOfByte == null) {
      return;
    }
    short[] arrayOfShort = this.mSampleBuf;
    int i = arrayOfShort.length;
    for (int j = 0; j < i; j++)
    {
      k = paramArrayOfByte[(j * 2)];
      m = paramArrayOfByte[(j * 2 + 1)];
      m = (int)Math.sqrt(k * k + m * m);
      k = 32767;
      if (m < 32767) {
        k = m;
      }
      arrayOfShort[j] = ((short)(short)k);
    }
    int m = 0;
    j = 0;
    for (int k = 0; k < this.mVisualizationWidthNum; k++)
    {
      int n = 0;
      while (j < i)
      {
        n = Math.max(n, arrayOfShort[m]);
        m++;
        j += this.mVisualizationWidthNum;
      }
      j -= i;
      float f;
      if (n > 1)
      {
        f = (float)(Math.log(k + 2) / this.INDEX_SCALE_FACTOR);
        f = (n - 1) * f * f;
      }
      else
      {
        f = 0.0F;
      }
      if (f > 20.0F) {
        f = this.mVisualizationHeightNum;
      } else {
        f /= this.SAMPLE_SCALE_FACTOR;
      }
      paramArrayOfByte = this.mPointData;
      paramArrayOfByte[k] = Math.max(f / this.mVisualizationHeightNum, paramArrayOfByte[k] - this.VISUALIZE_DESC_HEIGHT);
    }
    invalidate();
  }
  
  class AsymmetryDotBar
    implements SpectrumVisualizer.DotBarDrawer
  {
    AsymmetryDotBar() {}
    
    public void drawDotBar(Canvas paramCanvas, int paramInt)
    {
      int i = (int)(SpectrumVisualizer.this.mDotbarHeight * (1.0F - SpectrumVisualizer.this.mPointData[paramInt]) / SpectrumVisualizer.this.mCellSize + 0.5D) * SpectrumVisualizer.this.mCellSize;
      if (i < SpectrumVisualizer.this.mDotbarHeight) {
        paramCanvas.drawBitmap(SpectrumVisualizer.this.mPixels, SpectrumVisualizer.this.mCellSize * i, SpectrumVisualizer.this.mCellSize, SpectrumVisualizer.this.mCellSize * paramInt, i, SpectrumVisualizer.this.mCellSize, SpectrumVisualizer.this.mDotbarHeight - i, true, SpectrumVisualizer.this.mPaint);
      }
    }
  }
  
  private static abstract interface DotBarDrawer
  {
    public abstract void drawDotBar(Canvas paramCanvas, int paramInt);
  }
  
  class SymmetryDotBar
    implements SpectrumVisualizer.DotBarDrawer
  {
    SymmetryDotBar() {}
    
    public void drawDotBar(Canvas paramCanvas, int paramInt)
    {
      int i = (int)(SpectrumVisualizer.this.mDotbarHeight * (1.0F - SpectrumVisualizer.this.mPointData[paramInt]) / SpectrumVisualizer.this.mCellSize + 0.5D) * SpectrumVisualizer.this.mCellSize;
      if (i < SpectrumVisualizer.this.mDotbarHeight) {
        paramCanvas.drawBitmap(SpectrumVisualizer.this.mPixels, SpectrumVisualizer.this.mCellSize * i, SpectrumVisualizer.this.mCellSize, SpectrumVisualizer.this.mCellSize * paramInt, i, SpectrumVisualizer.this.mCellSize, SpectrumVisualizer.this.mDotbarHeight - i, true, SpectrumVisualizer.this.mPaint);
      }
      int j = (int)(SpectrumVisualizer.this.mShadowDotbarHeight * SpectrumVisualizer.this.mPointData[paramInt] / SpectrumVisualizer.this.mCellSize + 0.5D) * SpectrumVisualizer.this.mCellSize;
      i = j;
      if (j > SpectrumVisualizer.this.mShadowDotbarHeight) {
        i = SpectrumVisualizer.this.mShadowDotbarHeight;
      }
      if (i > 0) {
        paramCanvas.drawBitmap(SpectrumVisualizer.this.mShadowPixels, 0, SpectrumVisualizer.this.mCellSize, SpectrumVisualizer.this.mCellSize * paramInt, SpectrumVisualizer.this.mDotbarHeight, SpectrumVisualizer.this.mCellSize, i, true, SpectrumVisualizer.this.mPaint);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/widget/SpectrumVisualizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */