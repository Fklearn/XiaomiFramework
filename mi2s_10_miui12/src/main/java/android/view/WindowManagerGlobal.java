package android.view;

import android.animation.ValueAnimator;
import android.annotation.UnsupportedAppUsage;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import java.util.ArrayList;

public final class WindowManagerGlobal
{
  public static final int ADD_APP_EXITING = -4;
  public static final int ADD_BAD_APP_TOKEN = -1;
  public static final int ADD_BAD_SUBWINDOW_TOKEN = -2;
  public static final int ADD_DUPLICATE_ADD = -5;
  public static final int ADD_FLAG_ALWAYS_CONSUME_SYSTEM_BARS = 4;
  public static final int ADD_FLAG_APP_VISIBLE = 2;
  public static final int ADD_FLAG_IN_TOUCH_MODE = 1;
  public static final int ADD_INVALID_DISPLAY = -9;
  public static final int ADD_INVALID_TYPE = -10;
  public static final int ADD_MULTIPLE_SINGLETON = -7;
  public static final int ADD_NOT_APP_TOKEN = -3;
  public static final int ADD_OKAY = 0;
  public static final int ADD_PERMISSION_DENIED = -8;
  public static final int ADD_STARTING_NOT_NEEDED = -6;
  public static final int RELAYOUT_DEFER_SURFACE_DESTROY = 2;
  public static final int RELAYOUT_INSETS_PENDING = 1;
  public static final int RELAYOUT_RES_CONSUME_ALWAYS_SYSTEM_BARS = 64;
  public static final int RELAYOUT_RES_DRAG_RESIZING_DOCKED = 8;
  public static final int RELAYOUT_RES_DRAG_RESIZING_FREEFORM = 16;
  public static final int RELAYOUT_RES_FIRST_TIME = 2;
  public static final int RELAYOUT_RES_IN_TOUCH_MODE = 1;
  public static final int RELAYOUT_RES_SURFACE_CHANGED = 4;
  public static final int RELAYOUT_RES_SURFACE_RESIZED = 32;
  private static final String TAG = "WindowManager";
  @UnsupportedAppUsage
  private static WindowManagerGlobal sDefaultWindowManager;
  @UnsupportedAppUsage
  private static IWindowManager sWindowManagerService;
  @UnsupportedAppUsage
  private static IWindowSession sWindowSession;
  private final ArraySet<View> mDyingViews = new ArraySet();
  @UnsupportedAppUsage
  private final Object mLock = new Object();
  @UnsupportedAppUsage
  private final ArrayList<WindowManager.LayoutParams> mParams = new ArrayList();
  @UnsupportedAppUsage
  private final ArrayList<ViewRootImpl> mRoots = new ArrayList();
  private Runnable mSystemPropertyUpdater;
  @UnsupportedAppUsage
  private final ArrayList<View> mViews = new ArrayList();
  
  private void doTrimForeground()
  {
    int i = 0;
    synchronized (this.mLock)
    {
      for (int j = this.mRoots.size() - 1; j >= 0; j--)
      {
        ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mRoots.get(j);
        if ((localViewRootImpl.mView != null) && (localViewRootImpl.getHostVisibility() == 0) && (localViewRootImpl.mAttachInfo.mThreadedRenderer != null)) {
          i = 1;
        } else {
          localViewRootImpl.destroyHardwareResources();
        }
      }
      if (i == 0) {
        ThreadedRenderer.trimMemory(80);
      }
      return;
    }
  }
  
  private int findViewLocked(View paramView, boolean paramBoolean)
  {
    int i = this.mViews.indexOf(paramView);
    if ((paramBoolean) && (i < 0))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("View=");
      localStringBuilder.append(paramView);
      localStringBuilder.append(" not attached to window manager");
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    return i;
  }
  
  @UnsupportedAppUsage
  public static WindowManagerGlobal getInstance()
  {
    try
    {
      if (sDefaultWindowManager == null)
      {
        localWindowManagerGlobal = new android/view/WindowManagerGlobal;
        localWindowManagerGlobal.<init>();
        sDefaultWindowManager = localWindowManagerGlobal;
      }
      WindowManagerGlobal localWindowManagerGlobal = sDefaultWindowManager;
      return localWindowManagerGlobal;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public static IWindowManager getWindowManagerService()
  {
    try
    {
      if (sWindowManagerService == null)
      {
        sWindowManagerService = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try
        {
          if (sWindowManagerService != null) {
            ValueAnimator.setDurationScale(sWindowManagerService.getCurrentAnimatorScale());
          }
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
      }
      IWindowManager localIWindowManager = sWindowManagerService;
      return localIWindowManager;
    }
    finally {}
  }
  
  private static String getWindowName(ViewRootImpl paramViewRootImpl)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramViewRootImpl.mWindowAttributes.getTitle());
    localStringBuilder.append("/");
    localStringBuilder.append(paramViewRootImpl.getClass().getName());
    localStringBuilder.append('@');
    localStringBuilder.append(Integer.toHexString(paramViewRootImpl.hashCode()));
    return localStringBuilder.toString();
  }
  
  @UnsupportedAppUsage
  public static IWindowSession getWindowSession()
  {
    try
    {
      Object localObject1 = sWindowSession;
      if (localObject1 == null) {
        try
        {
          InputMethodManager.ensureDefaultInstanceForDefaultDisplayIfNecessary();
          IWindowManager localIWindowManager = getWindowManagerService();
          localObject1 = new android/view/WindowManagerGlobal$1;
          ((1)localObject1).<init>();
          sWindowSession = localIWindowManager.openSession((IWindowSessionCallback)localObject1);
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
      }
      IWindowSession localIWindowSession = sWindowSession;
      return localIWindowSession;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public static void initialize()
  {
    getWindowManagerService();
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  public static IWindowSession peekWindowSession()
  {
    try
    {
      IWindowSession localIWindowSession = sWindowSession;
      return localIWindowSession;
    }
    finally {}
  }
  
  private void removeViewLocked(int paramInt, boolean paramBoolean)
  {
    ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mRoots.get(paramInt);
    View localView = localViewRootImpl.getView();
    if (localView != null)
    {
      InputMethodManager localInputMethodManager = (InputMethodManager)localView.getContext().getSystemService(InputMethodManager.class);
      if (localInputMethodManager != null) {
        localInputMethodManager.windowDismissed(((View)this.mViews.get(paramInt)).getWindowToken());
      }
    }
    paramBoolean = localViewRootImpl.die(paramBoolean);
    if (localView != null)
    {
      localView.assignParent(null);
      if (paramBoolean) {
        this.mDyingViews.add(localView);
      }
    }
  }
  
  public static boolean shouldDestroyEglContext(int paramInt)
  {
    if (paramInt >= 80) {
      return true;
    }
    return (paramInt >= 60) && (!ActivityManager.isHighEndGfx());
  }
  
  public static void trimForeground()
  {
    if ((ThreadedRenderer.sTrimForeground) && (ThreadedRenderer.isAvailable())) {
      getInstance().doTrimForeground();
    }
  }
  
  private boolean updateBlurCropOnly(View paramView, WindowManager.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams.flags & 0x4) != 0)
    {
      ViewRootImpl localViewRootImpl = paramView.getViewRootImpl();
      if ((localViewRootImpl != null) && (localViewRootImpl.mWindowAttributes != null))
      {
        paramView = new WindowManager.LayoutParams();
        paramView.copyFrom(localViewRootImpl.mWindowAttributes);
        int i = paramView.copyFrom(paramLayoutParams);
        int j = 0x20000000 & i;
        if (j != 0) {
          localViewRootImpl.updateBlurCrop(paramLayoutParams);
        }
        if ((j != 0) && ((0xDFFFFFFF & i) == 0)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams, Display paramDisplay, Window paramWindow)
  {
    if (paramView != null)
    {
      if (paramDisplay != null)
      {
        if ((paramLayoutParams instanceof WindowManager.LayoutParams))
        {
          WindowManager.LayoutParams localLayoutParams = (WindowManager.LayoutParams)paramLayoutParams;
          if (paramWindow != null)
          {
            paramWindow.adjustLayoutParamsForSubWindow(localLayoutParams);
          }
          else
          {
            paramLayoutParams = paramView.getContext();
            if ((paramLayoutParams != null) && ((paramLayoutParams.getApplicationInfo().flags & 0x20000000) != 0)) {
              localLayoutParams.flags |= 0x1000000;
            }
          }
          Object localObject1 = null;
          paramLayoutParams = null;
          synchronized (this.mLock)
          {
            if (this.mSystemPropertyUpdater == null)
            {
              paramWindow = new android/view/WindowManagerGlobal$2;
              paramWindow.<init>(this);
              this.mSystemPropertyUpdater = paramWindow;
              SystemProperties.addChangeCallback(this.mSystemPropertyUpdater);
            }
            int i = findViewLocked(paramView, false);
            if (i >= 0) {
              if (this.mDyingViews.contains(paramView))
              {
                ((ViewRootImpl)this.mRoots.get(i)).doDie();
              }
              else
              {
                paramLayoutParams = new java/lang/IllegalStateException;
                paramDisplay = new java/lang/StringBuilder;
                paramDisplay.<init>();
                paramDisplay.append("View ");
                paramDisplay.append(paramView);
                paramDisplay.append(" has already been added to the window manager.");
                paramLayoutParams.<init>(paramDisplay.toString());
                throw paramLayoutParams;
              }
            }
            paramWindow = (Window)localObject1;
            if (localLayoutParams.type >= 1000)
            {
              paramWindow = (Window)localObject1;
              if (localLayoutParams.type <= 1999)
              {
                int j = this.mViews.size();
                for (i = 0;; i++)
                {
                  paramWindow = paramLayoutParams;
                  if (i >= j) {
                    break;
                  }
                  if (((ViewRootImpl)this.mRoots.get(i)).mWindow.asBinder() == localLayoutParams.token) {
                    paramLayoutParams = (View)this.mViews.get(i);
                  }
                }
              }
            }
            paramLayoutParams = new android/view/ViewRootImpl;
            paramLayoutParams.<init>(paramView.getContext(), paramDisplay);
            paramView.setLayoutParams(localLayoutParams);
            this.mViews.add(paramView);
            this.mRoots.add(paramLayoutParams);
            this.mParams.add(localLayoutParams);
            try
            {
              paramLayoutParams.setView(paramView, localLayoutParams, paramWindow);
              return;
            }
            catch (RuntimeException paramLayoutParams)
            {
              i = findViewLocked(paramView, false);
              if (i >= 0)
              {
                Log.e("WindowManager", "BadTokenException or InvalidDisplayException, clean up.");
                removeViewLocked(i, true);
              }
              throw paramLayoutParams;
            }
          }
        }
        throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
      }
      throw new IllegalArgumentException("display must not be null");
    }
    throw new IllegalArgumentException("view must not be null");
  }
  
  public void changeCanvasOpacity(IBinder paramIBinder, boolean paramBoolean)
  {
    if (paramIBinder == null) {
      return;
    }
    synchronized (this.mLock)
    {
      for (int i = this.mParams.size() - 1; i >= 0; i--) {
        if (((WindowManager.LayoutParams)this.mParams.get(i)).token == paramIBinder)
        {
          ((ViewRootImpl)this.mRoots.get(i)).changeCanvasOpacity(paramBoolean);
          return;
        }
      }
      return;
    }
  }
  
  public void closeAll(IBinder paramIBinder, String paramString1, String paramString2)
  {
    closeAllExceptView(paramIBinder, null, paramString1, paramString2);
  }
  
  public void closeAllExceptView(IBinder paramIBinder, View paramView, String paramString1, String paramString2)
  {
    synchronized (this.mLock)
    {
      int i = this.mViews.size();
      for (int j = 0; j < i; j++) {
        if (((paramView == null) || (this.mViews.get(j) != paramView)) && ((paramIBinder == null) || (((WindowManager.LayoutParams)this.mParams.get(j)).token == paramIBinder)))
        {
          ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mRoots.get(j);
          if (paramString1 != null)
          {
            WindowLeaked localWindowLeaked = new android/view/WindowLeaked;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append(paramString2);
            localStringBuilder.append(" ");
            localStringBuilder.append(paramString1);
            localStringBuilder.append(" has leaked window ");
            localStringBuilder.append(localViewRootImpl.getView());
            localStringBuilder.append(" that was originally added here");
            localWindowLeaked.<init>(localStringBuilder.toString());
            localWindowLeaked.setStackTrace(localViewRootImpl.getLocation().getStackTrace());
            Log.e("WindowManager", "", localWindowLeaked);
          }
          removeViewLocked(j, false);
        }
      }
      return;
    }
  }
  
  void doRemoveView(ViewRootImpl paramViewRootImpl)
  {
    synchronized (this.mLock)
    {
      int i = this.mRoots.indexOf(paramViewRootImpl);
      if (i >= 0)
      {
        this.mRoots.remove(i);
        this.mParams.remove(i);
        paramViewRootImpl = (View)this.mViews.remove(i);
        this.mDyingViews.remove(paramViewRootImpl);
      }
      if ((ThreadedRenderer.sTrimForeground) && (ThreadedRenderer.isAvailable())) {
        doTrimForeground();
      }
      return;
    }
  }
  
  /* Error */
  public void dumpGfxInfo(java.io.FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: new 484	com/android/internal/util/FastPrintWriter
    //   3: dup
    //   4: new 486	java/io/FileOutputStream
    //   7: dup
    //   8: aload_1
    //   9: invokespecial 489	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   12: invokespecial 492	com/android/internal/util/FastPrintWriter:<init>	(Ljava/io/OutputStream;)V
    //   15: astore_3
    //   16: aload_0
    //   17: getfield 81	android/view/WindowManagerGlobal:mLock	Ljava/lang/Object;
    //   20: astore 4
    //   22: aload 4
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 86	android/view/WindowManagerGlobal:mViews	Ljava/util/ArrayList;
    //   29: invokevirtual 105	java/util/ArrayList:size	()I
    //   32: istore 5
    //   34: aload_3
    //   35: ldc_w 494
    //   38: invokevirtual 499	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   41: iconst_0
    //   42: istore 6
    //   44: iload 6
    //   46: iload 5
    //   48: if_icmpge +83 -> 131
    //   51: aload_0
    //   52: getfield 88	android/view/WindowManagerGlobal:mRoots	Ljava/util/ArrayList;
    //   55: iload 6
    //   57: invokevirtual 109	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   60: checkcast 111	android/view/ViewRootImpl
    //   63: astore 7
    //   65: aload_3
    //   66: ldc_w 501
    //   69: iconst_2
    //   70: anewarray 4	java/lang/Object
    //   73: dup
    //   74: iconst_0
    //   75: aload 7
    //   77: invokestatic 503	android/view/WindowManagerGlobal:getWindowName	(Landroid/view/ViewRootImpl;)Ljava/lang/String;
    //   80: aastore
    //   81: dup
    //   82: iconst_1
    //   83: aload 7
    //   85: invokevirtual 118	android/view/ViewRootImpl:getHostVisibility	()I
    //   88: invokestatic 507	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   91: aastore
    //   92: invokevirtual 511	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   95: pop
    //   96: aload 7
    //   98: invokevirtual 278	android/view/ViewRootImpl:getView	()Landroid/view/View;
    //   101: getfield 512	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   104: getfield 128	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   107: astore 7
    //   109: aload 7
    //   111: ifnull +14 -> 125
    //   114: aload 7
    //   116: aload_3
    //   117: aload_1
    //   118: aload_2
    //   119: invokevirtual 515	android/view/ThreadedRenderer:dumpGfxInfo	(Ljava/io/PrintWriter;Ljava/io/FileDescriptor;[Ljava/lang/String;)V
    //   122: goto +3 -> 125
    //   125: iinc 6 1
    //   128: goto -84 -> 44
    //   131: aload_3
    //   132: ldc_w 517
    //   135: invokevirtual 499	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   138: iconst_0
    //   139: istore 8
    //   141: iconst_0
    //   142: istore 9
    //   144: iconst_2
    //   145: newarray <illegal type>
    //   147: astore_1
    //   148: iconst_0
    //   149: istore 6
    //   151: iload 6
    //   153: iload 5
    //   155: if_icmpge +97 -> 252
    //   158: aload_0
    //   159: getfield 88	android/view/WindowManagerGlobal:mRoots	Ljava/util/ArrayList;
    //   162: iload 6
    //   164: invokevirtual 109	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   167: checkcast 111	android/view/ViewRootImpl
    //   170: astore_2
    //   171: aload_2
    //   172: aload_1
    //   173: invokevirtual 520	android/view/ViewRootImpl:dumpGfxInfo	([I)V
    //   176: aload_3
    //   177: ldc_w 522
    //   180: iconst_3
    //   181: anewarray 4	java/lang/Object
    //   184: dup
    //   185: iconst_0
    //   186: aload_2
    //   187: invokestatic 503	android/view/WindowManagerGlobal:getWindowName	(Landroid/view/ViewRootImpl;)Ljava/lang/String;
    //   190: aastore
    //   191: dup
    //   192: iconst_1
    //   193: aload_1
    //   194: iconst_0
    //   195: iaload
    //   196: invokestatic 507	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   199: aastore
    //   200: dup
    //   201: iconst_2
    //   202: aload_1
    //   203: iconst_1
    //   204: iaload
    //   205: i2f
    //   206: ldc_w 523
    //   209: fdiv
    //   210: invokestatic 528	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   213: aastore
    //   214: invokevirtual 511	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   217: pop
    //   218: aload_3
    //   219: ldc_w 530
    //   222: iconst_0
    //   223: anewarray 4	java/lang/Object
    //   226: invokevirtual 511	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   229: pop
    //   230: iload 8
    //   232: aload_1
    //   233: iconst_0
    //   234: iaload
    //   235: iadd
    //   236: istore 8
    //   238: iload 9
    //   240: aload_1
    //   241: iconst_1
    //   242: iaload
    //   243: iadd
    //   244: istore 9
    //   246: iinc 6 1
    //   249: goto -98 -> 151
    //   252: aload_3
    //   253: ldc_w 532
    //   256: iconst_1
    //   257: anewarray 4	java/lang/Object
    //   260: dup
    //   261: iconst_0
    //   262: iload 5
    //   264: invokestatic 507	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   267: aastore
    //   268: invokevirtual 511	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   271: pop
    //   272: aload_3
    //   273: ldc_w 534
    //   276: iconst_1
    //   277: anewarray 4	java/lang/Object
    //   280: dup
    //   281: iconst_0
    //   282: iload 8
    //   284: invokestatic 507	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   287: aastore
    //   288: invokevirtual 511	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   291: pop
    //   292: aload_3
    //   293: ldc_w 536
    //   296: iconst_1
    //   297: anewarray 4	java/lang/Object
    //   300: dup
    //   301: iconst_0
    //   302: iload 9
    //   304: i2f
    //   305: ldc_w 523
    //   308: fdiv
    //   309: invokestatic 528	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   312: aastore
    //   313: invokevirtual 511	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   316: pop
    //   317: aload 4
    //   319: monitorexit
    //   320: aload_3
    //   321: invokevirtual 539	java/io/PrintWriter:flush	()V
    //   324: return
    //   325: astore_1
    //   326: aload 4
    //   328: monitorexit
    //   329: aload_1
    //   330: athrow
    //   331: astore_1
    //   332: goto +8 -> 340
    //   335: astore_1
    //   336: goto -10 -> 326
    //   339: astore_1
    //   340: aload_3
    //   341: invokevirtual 539	java/io/PrintWriter:flush	()V
    //   344: aload_1
    //   345: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	346	0	this	WindowManagerGlobal
    //   0	346	1	paramFileDescriptor	java.io.FileDescriptor
    //   0	346	2	paramArrayOfString	String[]
    //   15	326	3	localFastPrintWriter	com.android.internal.util.FastPrintWriter
    //   32	231	5	i	int
    //   42	205	6	j	int
    //   63	52	7	localObject2	Object
    //   139	144	8	k	int
    //   142	161	9	m	int
    // Exception table:
    //   from	to	target	type
    //   25	41	325	finally
    //   51	96	325	finally
    //   96	109	325	finally
    //   329	331	331	finally
    //   114	122	335	finally
    //   131	138	335	finally
    //   144	148	335	finally
    //   158	230	335	finally
    //   252	317	335	finally
    //   317	320	335	finally
    //   326	329	335	finally
    //   16	25	339	finally
  }
  
  @UnsupportedAppUsage
  public View getRootView(String paramString)
  {
    synchronized (this.mLock)
    {
      for (int i = this.mRoots.size() - 1; i >= 0; i--)
      {
        ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mRoots.get(i);
        if (paramString.equals(getWindowName(localViewRootImpl)))
        {
          paramString = localViewRootImpl.getView();
          return paramString;
        }
      }
      return null;
    }
  }
  
  @UnsupportedAppUsage
  public ArrayList<ViewRootImpl> getRootViews(IBinder paramIBinder)
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this.mLock)
    {
      int i = this.mRoots.size();
      for (int j = 0; j < i; j++)
      {
        WindowManager.LayoutParams localLayoutParams1 = (WindowManager.LayoutParams)this.mParams.get(j);
        if (localLayoutParams1.token != null) {
          if (localLayoutParams1.token != paramIBinder)
          {
            int k = 0;
            int m = k;
            if (localLayoutParams1.type >= 1000)
            {
              m = k;
              if (localLayoutParams1.type <= 1999) {
                for (int n = 0;; n++)
                {
                  m = k;
                  if (n >= i) {
                    break;
                  }
                  View localView = (View)this.mViews.get(n);
                  WindowManager.LayoutParams localLayoutParams2 = (WindowManager.LayoutParams)this.mParams.get(n);
                  if ((localLayoutParams1.token == localView.getWindowToken()) && (localLayoutParams2.token == paramIBinder))
                  {
                    m = 1;
                    break;
                  }
                }
              }
            }
            if (m == 0) {}
          }
          else
          {
            localArrayList.add((ViewRootImpl)this.mRoots.get(j));
          }
        }
      }
      return localArrayList;
    }
  }
  
  @UnsupportedAppUsage
  public String[] getViewRootNames()
  {
    synchronized (this.mLock)
    {
      int i = this.mRoots.size();
      String[] arrayOfString = new String[i];
      for (int j = 0; j < i; j++) {
        arrayOfString[j] = getWindowName((ViewRootImpl)this.mRoots.get(j));
      }
      return arrayOfString;
    }
  }
  
  public View getWindowView(IBinder paramIBinder)
  {
    synchronized (this.mLock)
    {
      int i = this.mViews.size();
      for (int j = 0; j < i; j++)
      {
        View localView = (View)this.mViews.get(j);
        if (localView.getWindowToken() == paramIBinder) {
          return localView;
        }
      }
      return null;
    }
  }
  
  public ArrayList<View> getWindowViews()
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = new java/util/ArrayList;
      localArrayList.<init>(this.mViews);
      return localArrayList;
    }
  }
  
  @UnsupportedAppUsage
  public void removeView(View paramView, boolean paramBoolean)
  {
    if (paramView != null) {
      synchronized (this.mLock)
      {
        int i = findViewLocked(paramView, true);
        View localView = ((ViewRootImpl)this.mRoots.get(i)).getView();
        removeViewLocked(i, paramBoolean);
        if (localView == paramView) {
          return;
        }
        IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localStringBuilder.append("Calling with view ");
        localStringBuilder.append(paramView);
        localStringBuilder.append(" but the ViewAncestor is attached to ");
        localStringBuilder.append(localView);
        localIllegalStateException.<init>(localStringBuilder.toString());
        throw localIllegalStateException;
      }
    }
    throw new IllegalArgumentException("view must not be null");
  }
  
  public void reportNewConfiguration(Configuration paramConfiguration)
  {
    synchronized (this.mLock)
    {
      int i = this.mViews.size();
      Configuration localConfiguration = new android/content/res/Configuration;
      localConfiguration.<init>(paramConfiguration);
      for (int j = 0; j < i; j++) {
        ((ViewRootImpl)this.mRoots.get(j)).requestUpdateConfiguration(localConfiguration);
      }
      return;
    }
  }
  
  public void setStoppedState(IBinder paramIBinder, boolean paramBoolean)
  {
    Object localObject1 = null;
    synchronized (this.mLock)
    {
      int i = this.mViews.size() - 1;
      while (i >= 0)
      {
        Object localObject3;
        if (paramIBinder != null)
        {
          localObject3 = localObject1;
          if (((WindowManager.LayoutParams)this.mParams.get(i)).token != paramIBinder) {}
        }
        else
        {
          ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mRoots.get(i);
          if (localViewRootImpl.mThread == Thread.currentThread())
          {
            localViewRootImpl.setWindowStopped(paramBoolean);
          }
          else
          {
            localObject3 = localObject1;
            if (localObject1 == null)
            {
              localObject3 = new java/util/ArrayList;
              ((ArrayList)localObject3).<init>();
            }
            ((ArrayList)localObject3).add(localViewRootImpl);
            localObject1 = localObject3;
          }
          setStoppedState(localViewRootImpl.mAttachInfo.mWindowToken, paramBoolean);
          localObject3 = localObject1;
        }
        i--;
        localObject1 = localObject3;
      }
      if (localObject1 != null) {
        for (i = ((ArrayList)localObject1).size() - 1; i >= 0; i--)
        {
          paramIBinder = (ViewRootImpl)((ArrayList)localObject1).get(i);
          paramIBinder.mHandler.runWithScissors(new _..Lambda.WindowManagerGlobal.2bR3FsEm4EdRwuXfttH0wA2xOW4(paramIBinder, paramBoolean), 0L);
        }
      }
      return;
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  public void trimMemory(int paramInt)
  {
    if (ThreadedRenderer.isAvailable())
    {
      int i = paramInt;
      if (shouldDestroyEglContext(paramInt)) {
        synchronized (this.mLock)
        {
          for (paramInt = this.mRoots.size() - 1; paramInt >= 0; paramInt--) {
            ((ViewRootImpl)this.mRoots.get(paramInt)).destroyHardwareResources();
          }
          i = 80;
        }
      }
      ThreadedRenderer.trimMemory(i);
      if (ThreadedRenderer.sTrimForeground) {
        doTrimForeground();
      }
    }
  }
  
  public void updateViewLayout(View paramView, ViewGroup.LayoutParams arg2)
  {
    if (paramView != null)
    {
      if ((??? instanceof WindowManager.LayoutParams))
      {
        WindowManager.LayoutParams localLayoutParams = (WindowManager.LayoutParams)???;
        paramView.setLayoutParams(localLayoutParams);
        synchronized (this.mLock)
        {
          int i = findViewLocked(paramView, true);
          paramView = (ViewRootImpl)this.mRoots.get(i);
          this.mParams.remove(i);
          this.mParams.add(i, localLayoutParams);
          paramView.setLayoutParams(localLayoutParams, false);
          return;
        }
      }
      throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
    }
    throw new IllegalArgumentException("view must not be null");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowManagerGlobal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */