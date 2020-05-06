package android.view.accessibility;

import android.annotation.UnsupportedAppUsage;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class CaptioningManager
{
  private static final int DEFAULT_ENABLED = 0;
  private static final float DEFAULT_FONT_SCALE = 1.0F;
  private static final int DEFAULT_PRESET = 0;
  private final ContentObserver mContentObserver;
  private final ContentResolver mContentResolver;
  private final ArrayList<CaptioningChangeListener> mListeners = new ArrayList();
  private final Runnable mStyleChangedRunnable = new Runnable()
  {
    public void run()
    {
      CaptioningManager.this.notifyUserStyleChanged();
    }
  };
  
  public CaptioningManager(Context paramContext)
  {
    this.mContentResolver = paramContext.getContentResolver();
    this.mContentObserver = new MyContentObserver(new Handler(paramContext.getMainLooper()));
  }
  
  private void notifyEnabledChanged()
  {
    boolean bool = isEnabled();
    synchronized (this.mListeners)
    {
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((CaptioningChangeListener)localIterator.next()).onEnabledChanged(bool);
      }
      return;
    }
  }
  
  private void notifyFontScaleChanged()
  {
    float f = getFontScale();
    synchronized (this.mListeners)
    {
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((CaptioningChangeListener)localIterator.next()).onFontScaleChanged(f);
      }
      return;
    }
  }
  
  private void notifyLocaleChanged()
  {
    Locale localLocale = getLocale();
    synchronized (this.mListeners)
    {
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((CaptioningChangeListener)localIterator.next()).onLocaleChanged(localLocale);
      }
      return;
    }
  }
  
  private void notifyUserStyleChanged()
  {
    CaptionStyle localCaptionStyle = getUserStyle();
    synchronized (this.mListeners)
    {
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((CaptioningChangeListener)localIterator.next()).onUserStyleChanged(localCaptionStyle);
      }
      return;
    }
  }
  
  private void registerObserver(String paramString)
  {
    this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(paramString), false, this.mContentObserver);
  }
  
  public void addCaptioningChangeListener(CaptioningChangeListener paramCaptioningChangeListener)
  {
    synchronized (this.mListeners)
    {
      if (this.mListeners.isEmpty())
      {
        registerObserver("accessibility_captioning_enabled");
        registerObserver("accessibility_captioning_foreground_color");
        registerObserver("accessibility_captioning_background_color");
        registerObserver("accessibility_captioning_window_color");
        registerObserver("accessibility_captioning_edge_type");
        registerObserver("accessibility_captioning_edge_color");
        registerObserver("accessibility_captioning_typeface");
        registerObserver("accessibility_captioning_font_scale");
        registerObserver("accessibility_captioning_locale");
        registerObserver("accessibility_captioning_preset");
      }
      this.mListeners.add(paramCaptioningChangeListener);
      return;
    }
  }
  
  public final float getFontScale()
  {
    return Settings.Secure.getFloat(this.mContentResolver, "accessibility_captioning_font_scale", 1.0F);
  }
  
  public final Locale getLocale()
  {
    Object localObject = getRawLocale();
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      localObject = ((String)localObject).split("_");
      int i = localObject.length;
      if (i != 1)
      {
        if (i != 2)
        {
          if (i == 3) {
            return new Locale(localObject[0], localObject[1], localObject[2]);
          }
        }
        else {
          return new Locale(localObject[0], localObject[1]);
        }
      }
      else {
        return new Locale(localObject[0]);
      }
    }
    return null;
  }
  
  public final String getRawLocale()
  {
    return Settings.Secure.getString(this.mContentResolver, "accessibility_captioning_locale");
  }
  
  public int getRawUserStyle()
  {
    return Settings.Secure.getInt(this.mContentResolver, "accessibility_captioning_preset", 0);
  }
  
  public CaptionStyle getUserStyle()
  {
    int i = getRawUserStyle();
    if (i == -1) {
      return CaptionStyle.getCustomStyle(this.mContentResolver);
    }
    return CaptionStyle.PRESETS[i];
  }
  
  public final boolean isEnabled()
  {
    ContentResolver localContentResolver = this.mContentResolver;
    boolean bool = false;
    if (Settings.Secure.getInt(localContentResolver, "accessibility_captioning_enabled", 0) == 1) {
      bool = true;
    }
    return bool;
  }
  
  public void removeCaptioningChangeListener(CaptioningChangeListener paramCaptioningChangeListener)
  {
    synchronized (this.mListeners)
    {
      this.mListeners.remove(paramCaptioningChangeListener);
      if (this.mListeners.isEmpty()) {
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
      }
      return;
    }
  }
  
  public static final class CaptionStyle
  {
    private static final CaptionStyle BLACK_ON_WHITE;
    private static final int COLOR_NONE_OPAQUE = 255;
    public static final int COLOR_UNSPECIFIED = 16777215;
    public static final CaptionStyle DEFAULT;
    private static final CaptionStyle DEFAULT_CUSTOM;
    public static final int EDGE_TYPE_DEPRESSED = 4;
    public static final int EDGE_TYPE_DROP_SHADOW = 2;
    public static final int EDGE_TYPE_NONE = 0;
    public static final int EDGE_TYPE_OUTLINE = 1;
    public static final int EDGE_TYPE_RAISED = 3;
    public static final int EDGE_TYPE_UNSPECIFIED = -1;
    @UnsupportedAppUsage
    public static final CaptionStyle[] PRESETS;
    public static final int PRESET_CUSTOM = -1;
    private static final CaptionStyle UNSPECIFIED;
    private static final CaptionStyle WHITE_ON_BLACK = new CaptionStyle(-1, -16777216, 0, -16777216, 255, null);
    private static final CaptionStyle YELLOW_ON_BLACK;
    private static final CaptionStyle YELLOW_ON_BLUE;
    public final int backgroundColor;
    public final int edgeColor;
    public final int edgeType;
    public final int foregroundColor;
    private final boolean mHasBackgroundColor;
    private final boolean mHasEdgeColor;
    private final boolean mHasEdgeType;
    private final boolean mHasForegroundColor;
    private final boolean mHasWindowColor;
    private Typeface mParsedTypeface;
    public final String mRawTypeface;
    public final int windowColor;
    
    static
    {
      BLACK_ON_WHITE = new CaptionStyle(-16777216, -1, 0, -16777216, 255, null);
      YELLOW_ON_BLACK = new CaptionStyle(65280, -16777216, 0, -16777216, 255, null);
      YELLOW_ON_BLUE = new CaptionStyle(65280, -16776961, 0, -16777216, 255, null);
      UNSPECIFIED = new CaptionStyle(16777215, 16777215, -1, 16777215, 16777215, null);
      CaptionStyle localCaptionStyle = WHITE_ON_BLACK;
      PRESETS = new CaptionStyle[] { localCaptionStyle, BLACK_ON_WHITE, YELLOW_ON_BLACK, YELLOW_ON_BLUE, UNSPECIFIED };
      DEFAULT_CUSTOM = localCaptionStyle;
      DEFAULT = localCaptionStyle;
    }
    
    private CaptionStyle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString)
    {
      this.mHasForegroundColor = hasColor(paramInt1);
      this.mHasBackgroundColor = hasColor(paramInt2);
      int i = 0;
      int j = -1;
      if (paramInt3 != -1) {
        bool = true;
      } else {
        bool = false;
      }
      this.mHasEdgeType = bool;
      this.mHasEdgeColor = hasColor(paramInt4);
      this.mHasWindowColor = hasColor(paramInt5);
      if (this.mHasForegroundColor) {
        j = paramInt1;
      }
      this.foregroundColor = j;
      boolean bool = this.mHasBackgroundColor;
      j = -16777216;
      if (!bool) {
        paramInt2 = -16777216;
      }
      this.backgroundColor = paramInt2;
      paramInt1 = i;
      if (this.mHasEdgeType) {
        paramInt1 = paramInt3;
      }
      this.edgeType = paramInt1;
      paramInt1 = j;
      if (this.mHasEdgeColor) {
        paramInt1 = paramInt4;
      }
      this.edgeColor = paramInt1;
      if (this.mHasWindowColor) {
        paramInt1 = paramInt5;
      } else {
        paramInt1 = 255;
      }
      this.windowColor = paramInt1;
      this.mRawTypeface = paramString;
    }
    
    public static CaptionStyle getCustomStyle(ContentResolver paramContentResolver)
    {
      CaptionStyle localCaptionStyle = DEFAULT_CUSTOM;
      int i = Settings.Secure.getInt(paramContentResolver, "accessibility_captioning_foreground_color", localCaptionStyle.foregroundColor);
      int j = Settings.Secure.getInt(paramContentResolver, "accessibility_captioning_background_color", localCaptionStyle.backgroundColor);
      int k = Settings.Secure.getInt(paramContentResolver, "accessibility_captioning_edge_type", localCaptionStyle.edgeType);
      int m = Settings.Secure.getInt(paramContentResolver, "accessibility_captioning_edge_color", localCaptionStyle.edgeColor);
      int n = Settings.Secure.getInt(paramContentResolver, "accessibility_captioning_window_color", localCaptionStyle.windowColor);
      paramContentResolver = Settings.Secure.getString(paramContentResolver, "accessibility_captioning_typeface");
      if (paramContentResolver == null) {
        paramContentResolver = localCaptionStyle.mRawTypeface;
      }
      return new CaptionStyle(i, j, k, m, n, paramContentResolver);
    }
    
    public static boolean hasColor(int paramInt)
    {
      boolean bool;
      if ((paramInt >>> 24 == 0) && ((0xFFFF00 & paramInt) != 0)) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public CaptionStyle applyStyle(CaptionStyle paramCaptionStyle)
    {
      int i;
      if (paramCaptionStyle.hasForegroundColor()) {
        i = paramCaptionStyle.foregroundColor;
      } else {
        i = this.foregroundColor;
      }
      int j;
      if (paramCaptionStyle.hasBackgroundColor()) {
        j = paramCaptionStyle.backgroundColor;
      } else {
        j = this.backgroundColor;
      }
      int k;
      if (paramCaptionStyle.hasEdgeType()) {
        k = paramCaptionStyle.edgeType;
      } else {
        k = this.edgeType;
      }
      int m;
      if (paramCaptionStyle.hasEdgeColor()) {
        m = paramCaptionStyle.edgeColor;
      } else {
        m = this.edgeColor;
      }
      int n;
      if (paramCaptionStyle.hasWindowColor()) {
        n = paramCaptionStyle.windowColor;
      } else {
        n = this.windowColor;
      }
      paramCaptionStyle = paramCaptionStyle.mRawTypeface;
      if (paramCaptionStyle == null) {
        paramCaptionStyle = this.mRawTypeface;
      }
      return new CaptionStyle(i, j, k, m, n, paramCaptionStyle);
    }
    
    public Typeface getTypeface()
    {
      if ((this.mParsedTypeface == null) && (!TextUtils.isEmpty(this.mRawTypeface))) {
        this.mParsedTypeface = Typeface.create(this.mRawTypeface, 0);
      }
      return this.mParsedTypeface;
    }
    
    public boolean hasBackgroundColor()
    {
      return this.mHasBackgroundColor;
    }
    
    public boolean hasEdgeColor()
    {
      return this.mHasEdgeColor;
    }
    
    public boolean hasEdgeType()
    {
      return this.mHasEdgeType;
    }
    
    public boolean hasForegroundColor()
    {
      return this.mHasForegroundColor;
    }
    
    public boolean hasWindowColor()
    {
      return this.mHasWindowColor;
    }
  }
  
  public static abstract class CaptioningChangeListener
  {
    public void onEnabledChanged(boolean paramBoolean) {}
    
    public void onFontScaleChanged(float paramFloat) {}
    
    public void onLocaleChanged(Locale paramLocale) {}
    
    public void onUserStyleChanged(CaptioningManager.CaptionStyle paramCaptionStyle) {}
  }
  
  private class MyContentObserver
    extends ContentObserver
  {
    private final Handler mHandler;
    
    public MyContentObserver(Handler paramHandler)
    {
      super();
      this.mHandler = paramHandler;
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      paramUri = paramUri.getPath();
      paramUri = paramUri.substring(paramUri.lastIndexOf('/') + 1);
      if ("accessibility_captioning_enabled".equals(paramUri))
      {
        CaptioningManager.this.notifyEnabledChanged();
      }
      else if ("accessibility_captioning_locale".equals(paramUri))
      {
        CaptioningManager.this.notifyLocaleChanged();
      }
      else if ("accessibility_captioning_font_scale".equals(paramUri))
      {
        CaptioningManager.this.notifyFontScaleChanged();
      }
      else
      {
        this.mHandler.removeCallbacks(CaptioningManager.this.mStyleChangedRunnable);
        this.mHandler.post(CaptioningManager.this.mStyleChangedRunnable);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/CaptioningManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */