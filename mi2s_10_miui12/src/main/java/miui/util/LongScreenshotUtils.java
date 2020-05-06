package miui.util;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.MiuiSettings.Global;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import miui.os.Build;

public class LongScreenshotUtils
{
  public static final String ACTION_LONG_SCREENSHOT = "com.miui.util.LongScreenshotUtils.LongScreenshot";
  static final int DELAY_FOR_BROADCAST_CALLBACK = 200;
  static final int DELAY_FOR_FINISH = 500;
  public static final String EXTRA_BOTTOM_LOC = "BottomLoc";
  public static final String EXTRA_IS_END = "IsEnd";
  public static final String EXTRA_IS_LONG_SCREENSHOT = "IsLongScreenshot";
  public static final String EXTRA_IS_SCREENSHOT = "IsScreenshot";
  public static final String EXTRA_TOP_LOC = "TopLoc";
  public static final String EXTRA_VIEW_BOTTOM = "ViewBottom";
  public static final String EXTRA_VIEW_TOP = "ViewTop";
  public static final int LONG_SCREENSHOT_CMD_DETECT = 1;
  public static final int LONG_SCREENSHOT_CMD_FINISH = 4;
  public static final int LONG_SCREENSHOT_CMD_START = 2;
  public static final int LONG_SCREENSHOT_CMD_TAKED = 3;
  static final int MAX_HEIGHT_FOR_SCREEN_COUNT = 8;
  private static final String TAG = "LongScreenshotUtils";
  
  public static class ContentPort
  {
    private H mHandler = new H();
    private boolean mIsFakeTouchForScroll;
    private boolean mIsFirstMove;
    private int mLastCaptureBottom;
    private View mMainScrollView;
    private int mMainScrollViewTop;
    private int mNavBarHeight;
    private boolean mNeedUseMultiTouch;
    private int mPrevScrolledY;
    private View mPrevScrolledYChildView;
    private Rect mScreenRect = new Rect();
    private int[] mTempLoc = new int[2];
    private MotionEvent.PointerCoords[] mTmpPointerCoords = new MotionEvent.PointerCoords[2];
    private MotionEvent.PointerProperties[] mTmpPointerProperties = new MotionEvent.PointerProperties[2];
    private int mTotalScrollDistance;
    private int mTouchY;
    private int mVerticalEdge;
    private boolean mVerticalScrollBarEnabled;
    
    public ContentPort()
    {
      for (int i = 0; i < 2; i++)
      {
        this.mTmpPointerProperties[i] = new MotionEvent.PointerProperties();
        this.mTmpPointerProperties[i].id = i;
        this.mTmpPointerCoords[i] = new MotionEvent.PointerCoords();
        MotionEvent.PointerCoords[] arrayOfPointerCoords = this.mTmpPointerCoords;
        arrayOfPointerCoords[i].pressure = 1.0F;
        arrayOfPointerCoords[i].size = 1.0F;
      }
    }
    
    private void broadcastCallback()
    {
      boolean bool;
      if ((this.mMainScrollView.canScrollVertically(1)) && (this.mTotalScrollDistance < this.mScreenRect.height() * 8)) {
        bool = false;
      } else {
        bool = true;
      }
      int i;
      if (this.mPrevScrolledYChildView != null)
      {
        this.mMainScrollView.getLocationOnScreen(this.mTempLoc);
        i = this.mPrevScrolledY - this.mPrevScrolledYChildView.getTop() - this.mTempLoc[1];
      }
      else
      {
        this.mMainScrollView.getLocationOnScreen(this.mTempLoc);
        i = this.mMainScrollView.getScrollY() - this.mTempLoc[1] - this.mPrevScrolledY;
      }
      this.mTotalScrollDistance += i;
      calculateScrollViewTop();
      int j = this.mMainScrollViewTop + getScrollViewVisibleHeight();
      int k = this.mLastCaptureBottom;
      if (k == 0)
      {
        j -= this.mVerticalEdge;
        k = j - i;
        this.mLastCaptureBottom = j;
        i = j;
        j = k;
      }
      else
      {
        k -= i;
        int m = j - this.mVerticalEdge;
        i = m;
        if (m <= k) {
          i = j;
        }
        this.mLastCaptureBottom = i;
        j = k;
      }
      Intent localIntent = new Intent("com.miui.util.LongScreenshotUtils.LongScreenshot");
      localIntent.putExtra("IsEnd", bool);
      localIntent.putExtra("TopLoc", j);
      localIntent.putExtra("BottomLoc", i);
      localIntent.putExtra("ViewTop", this.mMainScrollViewTop);
      localIntent.putExtra("ViewBottom", this.mMainScrollViewTop + getScrollViewVisibleHeight());
      this.mMainScrollView.getContext().sendBroadcast(localIntent);
      if (bool) {
        this.mHandler.sendEmptyMessageDelayed(4, 500L);
      }
    }
    
    private void calculateScrollViewTop()
    {
      this.mMainScrollView.getLocationOnScreen(this.mTempLoc);
      this.mMainScrollViewTop = this.mTempLoc[1];
      this.mMainScrollViewTop = findVisibleTop(this.mMainScrollView, this.mMainScrollViewTop);
    }
    
    private boolean checkIsMainScrollView(View paramView)
    {
      try
      {
        if (!isKnownScrollableView(paramView))
        {
          boolean bool = paramView.canScrollVertically(1);
          if (!bool) {
            return false;
          }
        }
        int i = this.mScreenRect.width() / 3;
        int j = this.mScreenRect.height() / 2;
        if ((paramView.getWidth() >= i) && (paramView.getHeight() >= j))
        {
          paramView.getLocationOnScreen(this.mTempLoc);
          Rect localRect = new Rect(this.mScreenRect);
          int[] arrayOfInt = this.mTempLoc;
          if ((localRect.intersect(arrayOfInt[0], arrayOfInt[1], arrayOfInt[0] + paramView.getWidth(), this.mTempLoc[1] + paramView.getHeight())) && (localRect.width() >= i) && (localRect.height() >= j))
          {
            this.mMainScrollViewTop = this.mTempLoc[1];
            this.mMainScrollViewTop = findVisibleTop(paramView, this.mMainScrollViewTop);
            return true;
          }
          return false;
        }
        return false;
      }
      catch (Exception paramView)
      {
        Log.w("LongScreenshotUtils", "", paramView);
      }
      return false;
    }
    
    private boolean checkIsMayHasBg()
    {
      String str1 = this.mMainScrollView.getContext().getPackageName();
      String str2 = this.mMainScrollView.getClass().getName();
      boolean bool;
      if ((("com.miui.notes".equalsIgnoreCase(str1)) && (str2.equals("com.miui.notes.editor.RichEditView$RichEditScrollView"))) || (("com.tencent.mobileqq".equalsIgnoreCase(str1)) && (str2.equals("com.tencent.mobileqq.bubble.ChatXListView"))) || (("com.tencent.mm".equalsIgnoreCase(str1)) && ((this.mMainScrollView instanceof ListView)))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private boolean checkNeedFakeTouchForScroll()
    {
      View localView = this.mMainScrollView;
      if ((!(localView instanceof ScrollView)) && (!isRecyclerView(localView.getClass())) && (!isNestedScrollView(this.mMainScrollView.getClass()))) {
        return (!(this.mMainScrollView instanceof AbsoluteLayout)) || ((Build.VERSION.SDK_INT > 19) && (!"com.ucmobile".equalsIgnoreCase(this.mMainScrollView.getContext().getPackageName())) && (!"com.eg.android.AlipayGphone".equalsIgnoreCase(this.mMainScrollView.getContext().getPackageName())));
      }
      return false;
    }
    
    private void dispatchFakeTouchEvent(int paramInt)
    {
      this.mTmpPointerProperties[0].id = 0;
      this.mTmpPointerCoords[0].x = (this.mMainScrollView.getWidth() / 2);
      this.mTmpPointerCoords[0].y = this.mTouchY;
      MotionEvent localMotionEvent = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), paramInt, 1, this.mTmpPointerProperties, this.mTmpPointerCoords, 0, 0, 1.0F, 1.0F, 0, 0, 0, 0);
      this.mMainScrollView.dispatchTouchEvent(localMotionEvent);
      localMotionEvent.recycle();
    }
    
    private void dispatchMoveAndReset(int paramInt)
    {
      if (this.mIsFirstMove)
      {
        this.mTouchY -= paramInt;
        dispatchFakeTouchEvent(2);
        this.mIsFirstMove = false;
      }
      else
      {
        dispatchFakeTouchEvent(1);
        this.mTouchY = (getScrollViewVisibleHeight() - this.mVerticalEdge);
        dispatchFakeTouchEvent(0);
        this.mTouchY -= paramInt;
        dispatchFakeTouchEvent(2);
      }
    }
    
    private View findMainScrollView()
    {
      Object localObject1 = getTopActivity();
      if (localObject1 != null)
      {
        boolean bool = MiuiSettings.Global.getBoolean(((Activity)localObject1).getContentResolver(), "force_fsg_nav_bar");
        Object localObject2 = (WindowManager)((Activity)localObject1).getSystemService("window");
        Object localObject3 = new DisplayMetrics();
        localObject2 = ((WindowManager)localObject2).getDefaultDisplay();
        ((Display)localObject2).getRealMetrics((DisplayMetrics)localObject3);
        int i = 0;
        if (bool)
        {
          this.mScreenRect.set(0, 0, ((DisplayMetrics)localObject3).widthPixels, ((DisplayMetrics)localObject3).heightPixels);
        }
        else
        {
          this.mScreenRect.set(0, 0, ((DisplayMetrics)localObject3).widthPixels, ((DisplayMetrics)localObject3).heightPixels);
          int j = ((Display)localObject2).getRotation();
          if ((j == 0) || (j == 2)) {
            i = 1;
          }
          if (Build.IS_TABLET)
          {
            localObject3 = this.mScreenRect;
            ((Rect)localObject3).bottom -= this.mNavBarHeight;
          }
          else if (i != 0)
          {
            localObject3 = this.mScreenRect;
            ((Rect)localObject3).bottom -= this.mNavBarHeight;
          }
          else
          {
            localObject3 = this.mScreenRect;
            ((Rect)localObject3).right -= this.mNavBarHeight;
          }
        }
        localObject1 = findScrollView(((Activity)localObject1).getWindow().getDecorView());
        if (localObject1 == null) {
          return null;
        }
        if (!((View)localObject1).canScrollVertically(1)) {
          return null;
        }
        return (View)localObject1;
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Get top activity in ");
      ((StringBuilder)localObject1).append(Process.myPid());
      ((StringBuilder)localObject1).append(" failed.");
      Log.w("LongScreenshotUtils", ((StringBuilder)localObject1).toString());
      return null;
    }
    
    private View findScrollView(View paramView)
    {
      if ((paramView != null) && (paramView.getVisibility() == 0))
      {
        if ((paramView instanceof ViewGroup))
        {
          ViewGroup localViewGroup = (ViewGroup)paramView;
          for (int i = localViewGroup.getChildCount() - 1; i >= 0; i--)
          {
            View localView = findScrollView(localViewGroup.getChildAt(i));
            if (localView != null) {
              return localView;
            }
          }
        }
        if (checkIsMainScrollView(paramView)) {
          return paramView;
        }
        return null;
      }
      return null;
    }
    
    private static int findVisibleTop(View paramView, int paramInt)
    {
      int j;
      for (int i = 0;; i = j)
      {
        j = i + paramView.getTop();
        if (!(paramView.getParent() instanceof View)) {
          break;
        }
        paramView = (View)paramView.getParent();
      }
      i = paramInt;
      if (j < 0) {
        i = paramInt - j;
      }
      return Math.max(i, 0);
    }
    
    private void finish()
    {
      if (this.mMainScrollView == null) {
        return;
      }
      this.mHandler.removeMessages(2);
      this.mMainScrollView.setVerticalScrollBarEnabled(this.mVerticalScrollBarEnabled);
      if (this.mIsFakeTouchForScroll) {
        dispatchFakeTouchEvent(1);
      }
      this.mMainScrollView = null;
      this.mPrevScrolledYChildView = null;
      this.mLastCaptureBottom = 0;
    }
    
    private int getExpectScrollDistance()
    {
      return (getScrollViewVisibleHeight() - this.mVerticalEdge * 2) / 2;
    }
    
    private int getScrollViewVisibleHeight()
    {
      int i = this.mMainScrollView.getHeight();
      if (this.mMainScrollViewTop + i <= this.mScreenRect.height()) {
        return i;
      }
      return this.mScreenRect.height() - this.mMainScrollViewTop;
    }
    
    private static Activity getTopActivity()
    {
      Object localObject1 = ActivityThread.currentActivityThread();
      try
      {
        Object localObject2 = ActivityThread.class.getDeclaredField("mActivities");
        ((Field)localObject2).setAccessible(true);
        localObject2 = (ArrayMap)((Field)localObject2).get(localObject1);
        for (int i = 0; i < ((ArrayMap)localObject2).size(); i++)
        {
          localObject1 = ((ArrayMap)localObject2).valueAt(i);
          Field localField = localObject1.getClass().getDeclaredField("activity");
          localField.setAccessible(true);
          localObject1 = (Activity)localField.get(localObject1);
          boolean bool = ((Activity)localObject1).isResumed();
          if (bool) {
            return (Activity)localObject1;
          }
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      return null;
    }
    
    private boolean isKnownScrollableView(View paramView)
    {
      return ((paramView instanceof AbsListView)) || ((paramView instanceof ListView)) || ((paramView instanceof ScrollView)) || (isRecyclerView(paramView.getClass())) || (isNestedScrollView(paramView.getClass()));
    }
    
    private boolean isNestedScrollView(Class paramClass)
    {
      if ("android.support.v4.widget.NestedScrollView".equals(paramClass.getName())) {
        return true;
      }
      if (paramClass.equals(Object.class)) {
        return false;
      }
      return isNestedScrollView(paramClass.getSuperclass());
    }
    
    private boolean isRecyclerView(Class paramClass)
    {
      if ("android.support.v7.widget.RecyclerView".equals(paramClass.getName())) {
        return true;
      }
      if (paramClass.equals(Object.class)) {
        return false;
      }
      return isRecyclerView(paramClass.getSuperclass());
    }
    
    private boolean isTencentApp()
    {
      return this.mMainScrollView.getContext().getPackageName().startsWith("com.tencent.");
    }
    
    private void scrollView()
    {
      if (this.mMainScrollView.canScrollVertically(1)) {
        scrollY(this.mMainScrollView, getExpectScrollDistance());
      }
    }
    
    private void scrollY(View paramView, int paramInt)
    {
      if (((paramView instanceof ViewGroup)) && (!(paramView instanceof ScrollView)) && (!isNestedScrollView(paramView.getClass())) && (!(paramView instanceof WebView)) && (!(paramView instanceof AbsoluteLayout)) && (((ViewGroup)paramView).getChildCount() > 0))
      {
        paramView.getLocationOnScreen(this.mTempLoc);
        ViewGroup localViewGroup = (ViewGroup)paramView;
        this.mPrevScrolledYChildView = localViewGroup.getChildAt(localViewGroup.getChildCount() - 1);
        this.mPrevScrolledY = (this.mPrevScrolledYChildView.getTop() + this.mTempLoc[1]);
      }
      else
      {
        this.mPrevScrolledYChildView = null;
        paramView.getLocationOnScreen(this.mTempLoc);
        this.mPrevScrolledY = (paramView.getScrollY() - this.mTempLoc[1]);
      }
      if (this.mIsFakeTouchForScroll)
      {
        if (this.mNeedUseMultiTouch)
        {
          dispatchMoveAndReset(paramInt);
        }
        else
        {
          this.mTouchY -= paramInt;
          dispatchFakeTouchEvent(2);
        }
      }
      else if ((paramView instanceof AbsListView)) {
        ((AbsListView)paramView).scrollListBy(paramInt);
      } else {
        paramView.scrollBy(0, paramInt);
      }
    }
    
    private void start()
    {
      this.mTotalScrollDistance = 0;
      this.mVerticalEdge = (getScrollViewVisibleHeight() / 5);
      this.mVerticalScrollBarEnabled = this.mMainScrollView.isVerticalScrollBarEnabled();
      this.mIsFakeTouchForScroll = checkNeedFakeTouchForScroll();
      if (this.mIsFakeTouchForScroll)
      {
        boolean bool;
        if ((!(this.mMainScrollView instanceof AbsListView)) && (isTencentApp())) {
          bool = true;
        } else {
          bool = false;
        }
        this.mNeedUseMultiTouch = bool;
        this.mTouchY = (getScrollViewVisibleHeight() - this.mVerticalEdge);
        dispatchFakeTouchEvent(0);
        this.mIsFirstMove = true;
      }
      this.mMainScrollView.setVerticalScrollBarEnabled(false);
      this.mHandler.sendEmptyMessage(2);
    }
    
    public boolean longScreenshot(int paramInt1, int paramInt2)
    {
      boolean bool = true;
      if ((paramInt1 != 1) && (paramInt1 != 2))
      {
        if (paramInt1 != 3)
        {
          if (paramInt1 == 4) {
            this.mHandler.sendEmptyMessage(4);
          }
        }
        else {
          this.mHandler.sendEmptyMessage(2);
        }
      }
      else
      {
        this.mNavBarHeight = paramInt2;
        Object localObject = null;
        try
        {
          View localView = findMainScrollView();
          localObject = localView;
        }
        catch (Exception localException)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("findMainScrollView exception : ");
          localStringBuilder.append(localException);
          Log.e("LongScreenshotUtils", localStringBuilder.toString());
        }
        if (localObject == null) {
          return false;
        }
        if (paramInt1 == 1)
        {
          localObject = getTopActivity();
          if ((localObject == null) || (!((Activity)localObject).hasWindowFocus())) {
            bool = false;
          }
          return bool;
        }
        this.mMainScrollView = ((View)localObject);
        this.mHandler.sendEmptyMessage(1);
      }
      return true;
    }
    
    public class H
      extends Handler
    {
      public static final int MSG_BROADCAST_CALLBACK = 3;
      public static final int MSG_FINISH = 4;
      public static final int MSG_SCROLL = 2;
      public static final int MSG_START = 1;
      
      public H()
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        int i = paramMessage.what;
        if (i != 1)
        {
          if (i != 2)
          {
            if (i != 3)
            {
              if (i == 4) {
                LongScreenshotUtils.ContentPort.this.finish();
              }
            }
            else if (LongScreenshotUtils.ContentPort.this.mMainScrollView != null) {
              LongScreenshotUtils.ContentPort.this.broadcastCallback();
            }
          }
          else if (LongScreenshotUtils.ContentPort.this.mMainScrollView != null)
          {
            LongScreenshotUtils.ContentPort.this.scrollView();
            sendEmptyMessageDelayed(3, 200L);
          }
        }
        else {
          LongScreenshotUtils.ContentPort.this.start();
        }
      }
    }
  }
  
  public static class LongBitmapDrawable
    extends Drawable
  {
    static final int MAX_PART_HEIGHT = 1024;
    private Bitmap[] mBitmaps = new Bitmap[0];
    private Paint mPaint = new Paint(3);
    
    public LongBitmapDrawable(Bitmap paramBitmap)
    {
      if (paramBitmap == null) {
        return;
      }
      ArrayList localArrayList = new ArrayList();
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      Paint localPaint = new Paint(4);
      while (j > 0)
      {
        int k = paramBitmap.getHeight();
        int m = Math.min(j, 1024);
        Bitmap localBitmap = Bitmap.createBitmap(i, m, paramBitmap.getConfig());
        new Canvas(localBitmap).drawBitmap(paramBitmap, 0.0F, -(k - j), localPaint);
        localArrayList.add(localBitmap);
        j -= m;
      }
      this.mBitmaps = ((Bitmap[])localArrayList.toArray(new Bitmap[localArrayList.size()]));
    }
    
    public LongBitmapDrawable(String paramString)
    {
      ArrayList localArrayList = new ArrayList();
      try
      {
        paramString = BitmapRegionDecoder.newInstance(paramString, false);
        Rect localRect = new Rect(0, 0, paramString.getWidth(), Math.min(paramString.getHeight(), 1024));
        int i = paramString.getHeight();
        while (i > 0)
        {
          localArrayList.add(paramString.decodeRegion(localRect, null));
          localRect.offset(0, localRect.height());
          i -= localRect.height();
          if (i < 0) {
            localRect.set(localRect.left, localRect.top, localRect.right, localRect.bottom + i);
          }
        }
        paramString.recycle();
        this.mBitmaps = ((Bitmap[])localArrayList.toArray(new Bitmap[localArrayList.size()]));
        return;
      }
      catch (IOException paramString)
      {
        paramString.printStackTrace();
      }
    }
    
    public LongBitmapDrawable(Bitmap[] paramArrayOfBitmap)
    {
      this.mBitmaps = paramArrayOfBitmap;
    }
    
    public void draw(Canvas paramCanvas)
    {
      paramCanvas.save();
      Object localObject = getBounds();
      if (localObject != null) {
        paramCanvas.translate(((Rect)localObject).left, ((Rect)localObject).top);
      }
      for (int i = 0;; i++)
      {
        localObject = this.mBitmaps;
        if (i >= localObject.length) {
          break;
        }
        localObject = localObject[i];
        if (!paramCanvas.quickReject(0.0F, 0.0F, ((Bitmap)localObject).getWidth(), ((Bitmap)localObject).getHeight(), Canvas.EdgeType.BW)) {
          paramCanvas.drawBitmap((Bitmap)localObject, 0.0F, 0.0F, this.mPaint);
        }
        paramCanvas.translate(0.0F, ((Bitmap)localObject).getHeight());
      }
      paramCanvas.restore();
    }
    
    public Bitmap[] getBitmaps()
    {
      return this.mBitmaps;
    }
    
    public int getIntrinsicHeight()
    {
      Bitmap[] arrayOfBitmap = this.mBitmaps;
      if ((arrayOfBitmap != null) && (arrayOfBitmap.length != 0))
      {
        int i = 0;
        for (int j = 0;; j++)
        {
          arrayOfBitmap = this.mBitmaps;
          if (j >= arrayOfBitmap.length) {
            break;
          }
          i += arrayOfBitmap[j].getHeight();
        }
        return i;
      }
      return 0;
    }
    
    public int getIntrinsicWidth()
    {
      Bitmap[] arrayOfBitmap = this.mBitmaps;
      if ((arrayOfBitmap != null) && (arrayOfBitmap.length != 0)) {
        return arrayOfBitmap[0].getWidth();
      }
      return 0;
    }
    
    public int getOpacity()
    {
      return 0;
    }
    
    public void setAlpha(int paramInt)
    {
      this.mPaint.setAlpha(paramInt);
    }
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/LongScreenshotUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */