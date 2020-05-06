package android.view.accessibility;

import android.accessibilityservice.IAccessibilityServiceConnection;
import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public final class AccessibilityInteractionClient
  extends IAccessibilityInteractionConnectionCallback.Stub
{
  private static final boolean CHECK_INTEGRITY = true;
  private static final boolean DEBUG = false;
  private static final String LOG_TAG = "AccessibilityInteractionClient";
  public static final int NO_ID = -1;
  private static final long TIMEOUT_INTERACTION_MILLIS = 5000L;
  private static AccessibilityCache sAccessibilityCache = new AccessibilityCache(new AccessibilityCache.AccessibilityNodeRefresher());
  private static final LongSparseArray<AccessibilityInteractionClient> sClients;
  private static final SparseArray<IAccessibilityServiceConnection> sConnectionCache;
  private static final Object sStaticLock = new Object();
  private AccessibilityNodeInfo mFindAccessibilityNodeInfoResult;
  private List<AccessibilityNodeInfo> mFindAccessibilityNodeInfosResult;
  private final Object mInstanceLock = new Object();
  private volatile int mInteractionId = -1;
  private final AtomicInteger mInteractionIdCounter = new AtomicInteger();
  private boolean mPerformAccessibilityActionResult;
  private Message mSameThreadMessage;
  
  static
  {
    sClients = new LongSparseArray();
    sConnectionCache = new SparseArray();
  }
  
  public static void addConnection(int paramInt, IAccessibilityServiceConnection paramIAccessibilityServiceConnection)
  {
    synchronized (sConnectionCache)
    {
      sConnectionCache.put(paramInt, paramIAccessibilityServiceConnection);
      return;
    }
  }
  
  private void checkFindAccessibilityNodeInfoResultIntegrity(List<AccessibilityNodeInfo> paramList)
  {
    if (paramList.size() == 0) {
      return;
    }
    Object localObject1 = (AccessibilityNodeInfo)paramList.get(0);
    int i = paramList.size();
    int j = 1;
    int k;
    while (j < i)
    {
      for (k = j;; k++)
      {
        localObject2 = localObject1;
        if (k >= i) {
          break;
        }
        localObject2 = (AccessibilityNodeInfo)paramList.get(k);
        if (((AccessibilityNodeInfo)localObject1).getParentNodeId() == ((AccessibilityNodeInfo)localObject2).getSourceNodeId()) {
          break;
        }
      }
      j++;
      localObject1 = localObject2;
    }
    if (localObject1 == null) {
      Log.e("AccessibilityInteractionClient", "No root.");
    }
    HashSet localHashSet = new HashSet();
    Object localObject2 = new LinkedList();
    ((Queue)localObject2).add(localObject1);
    while (!((Queue)localObject2).isEmpty())
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo = (AccessibilityNodeInfo)((Queue)localObject2).poll();
      if (!localHashSet.add(localAccessibilityNodeInfo))
      {
        Log.e("AccessibilityInteractionClient", "Duplicate node.");
        return;
      }
      int m = localAccessibilityNodeInfo.getChildCount();
      for (j = 0; j < m; j++)
      {
        long l = localAccessibilityNodeInfo.getChildId(j);
        for (k = 0; k < i; k++)
        {
          localObject1 = (AccessibilityNodeInfo)paramList.get(k);
          if (((AccessibilityNodeInfo)localObject1).getSourceNodeId() == l) {
            ((Queue)localObject2).add(localObject1);
          }
        }
      }
    }
    j = paramList.size() - localHashSet.size();
    if (j > 0)
    {
      paramList = new StringBuilder();
      paramList.append(j);
      paramList.append(" Disconnected nodes.");
      Log.e("AccessibilityInteractionClient", paramList.toString());
    }
  }
  
  private void clearResultLocked()
  {
    this.mInteractionId = -1;
    this.mFindAccessibilityNodeInfoResult = null;
    this.mFindAccessibilityNodeInfosResult = null;
    this.mPerformAccessibilityActionResult = false;
  }
  
  private void finalizeAndCacheAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt, boolean paramBoolean, String[] paramArrayOfString)
  {
    if (paramAccessibilityNodeInfo != null)
    {
      paramAccessibilityNodeInfo.setConnectionId(paramInt);
      if (!ArrayUtils.isEmpty(paramArrayOfString))
      {
        CharSequence localCharSequence = paramAccessibilityNodeInfo.getPackageName();
        if ((localCharSequence == null) || (!ArrayUtils.contains(paramArrayOfString, localCharSequence.toString()))) {
          paramAccessibilityNodeInfo.setPackageName(paramArrayOfString[0]);
        }
      }
      paramAccessibilityNodeInfo.setSealed(true);
      if (!paramBoolean) {
        sAccessibilityCache.add(paramAccessibilityNodeInfo);
      }
    }
  }
  
  private void finalizeAndCacheAccessibilityNodeInfos(List<AccessibilityNodeInfo> paramList, int paramInt, boolean paramBoolean, String[] paramArrayOfString)
  {
    if (paramList != null)
    {
      int i = paramList.size();
      for (int j = 0; j < i; j++) {
        finalizeAndCacheAccessibilityNodeInfo((AccessibilityNodeInfo)paramList.get(j), paramInt, paramBoolean, paramArrayOfString);
      }
    }
  }
  
  public static IAccessibilityServiceConnection getConnection(int paramInt)
  {
    synchronized (sConnectionCache)
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = (IAccessibilityServiceConnection)sConnectionCache.get(paramInt);
      return localIAccessibilityServiceConnection;
    }
  }
  
  private AccessibilityNodeInfo getFindAccessibilityNodeInfoResultAndClear(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo;
      if (waitForResultTimedLocked(paramInt)) {
        localAccessibilityNodeInfo = this.mFindAccessibilityNodeInfoResult;
      } else {
        localAccessibilityNodeInfo = null;
      }
      clearResultLocked();
      return localAccessibilityNodeInfo;
    }
  }
  
  private List<AccessibilityNodeInfo> getFindAccessibilityNodeInfosResultAndClear(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      List localList;
      if (waitForResultTimedLocked(paramInt)) {
        localList = this.mFindAccessibilityNodeInfosResult;
      } else {
        localList = Collections.emptyList();
      }
      clearResultLocked();
      if (Build.IS_DEBUGGABLE) {
        checkFindAccessibilityNodeInfoResultIntegrity(localList);
      }
      return localList;
    }
  }
  
  @UnsupportedAppUsage
  public static AccessibilityInteractionClient getInstance()
  {
    return getInstanceForThread(Thread.currentThread().getId());
  }
  
  public static AccessibilityInteractionClient getInstanceForThread(long paramLong)
  {
    synchronized (sStaticLock)
    {
      AccessibilityInteractionClient localAccessibilityInteractionClient1 = (AccessibilityInteractionClient)sClients.get(paramLong);
      AccessibilityInteractionClient localAccessibilityInteractionClient2 = localAccessibilityInteractionClient1;
      if (localAccessibilityInteractionClient1 == null)
      {
        localAccessibilityInteractionClient2 = new android/view/accessibility/AccessibilityInteractionClient;
        localAccessibilityInteractionClient2.<init>();
        sClients.put(paramLong, localAccessibilityInteractionClient2);
      }
      return localAccessibilityInteractionClient2;
    }
  }
  
  private boolean getPerformAccessibilityActionResultAndClear(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      boolean bool;
      if (waitForResultTimedLocked(paramInt)) {
        bool = this.mPerformAccessibilityActionResult;
      } else {
        bool = false;
      }
      clearResultLocked();
      return bool;
    }
  }
  
  private Message getSameProcessMessageAndClear()
  {
    synchronized (this.mInstanceLock)
    {
      Message localMessage = this.mSameThreadMessage;
      this.mSameThreadMessage = null;
      return localMessage;
    }
  }
  
  private static String idToString(int paramInt, long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramInt);
    localStringBuilder.append("/");
    localStringBuilder.append(AccessibilityNodeInfo.idToString(paramLong));
    return localStringBuilder.toString();
  }
  
  public static void removeConnection(int paramInt)
  {
    synchronized (sConnectionCache)
    {
      sConnectionCache.remove(paramInt);
      return;
    }
  }
  
  @VisibleForTesting
  public static void setCache(AccessibilityCache paramAccessibilityCache)
  {
    sAccessibilityCache = paramAccessibilityCache;
  }
  
  private boolean waitForResultTimedLocked(int paramInt)
  {
    long l1 = SystemClock.uptimeMillis();
    for (;;)
    {
      try
      {
        Message localMessage = getSameProcessMessageAndClear();
        if (localMessage != null) {
          localMessage.getTarget().handleMessage(localMessage);
        }
        if (this.mInteractionId == paramInt) {
          return true;
        }
        if (this.mInteractionId > paramInt) {
          return false;
        }
        long l2 = 5000L - (SystemClock.uptimeMillis() - l1);
        if (l2 <= 0L) {
          return false;
        }
        this.mInstanceLock.wait(l2);
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  @UnsupportedAppUsage
  public void clearCache()
  {
    sAccessibilityCache.clear();
  }
  
  /* Error */
  public AccessibilityNodeInfo findAccessibilityNodeInfoByAccessibilityId(int paramInt1, int paramInt2, long paramLong, boolean paramBoolean, int paramInt3, Bundle paramBundle)
  {
    // Byte code:
    //   0: iload 6
    //   2: iconst_2
    //   3: iand
    //   4: ifeq +24 -> 28
    //   7: iload 6
    //   9: iconst_1
    //   10: iand
    //   11: ifeq +6 -> 17
    //   14: goto +14 -> 28
    //   17: new 318	java/lang/IllegalArgumentException
    //   20: dup
    //   21: ldc_w 320
    //   24: invokespecial 323	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   27: athrow
    //   28: iload_1
    //   29: invokestatic 325	android/view/accessibility/AccessibilityInteractionClient:getConnection	(I)Landroid/accessibilityservice/IAccessibilityServiceConnection;
    //   32: astore 8
    //   34: aload 8
    //   36: ifnull +185 -> 221
    //   39: iload 5
    //   41: ifne +30 -> 71
    //   44: getstatic 71	android/view/accessibility/AccessibilityInteractionClient:sAccessibilityCache	Landroid/view/accessibility/AccessibilityCache;
    //   47: astore 9
    //   49: aload 9
    //   51: iload_2
    //   52: lload_3
    //   53: invokevirtual 329	android/view/accessibility/AccessibilityCache:getNode	(IJ)Landroid/view/accessibility/AccessibilityNodeInfo;
    //   56: astore 9
    //   58: aload 9
    //   60: ifnull +11 -> 71
    //   63: aload 9
    //   65: areturn
    //   66: astore 7
    //   68: goto +150 -> 218
    //   71: aload_0
    //   72: getfield 78	android/view/accessibility/AccessibilityInteractionClient:mInteractionIdCounter	Ljava/util/concurrent/atomic/AtomicInteger;
    //   75: invokevirtual 332	java/util/concurrent/atomic/AtomicInteger:getAndIncrement	()I
    //   78: istore 10
    //   80: invokestatic 337	android/os/Binder:clearCallingIdentity	()J
    //   83: lstore 11
    //   85: aload 8
    //   87: iload_2
    //   88: lload_3
    //   89: iload 10
    //   91: aload_0
    //   92: iload 6
    //   94: invokestatic 249	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   97: invokevirtual 252	java/lang/Thread:getId	()J
    //   100: aload 7
    //   102: invokeinterface 340 10 0
    //   107: astore 8
    //   109: lload 11
    //   111: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   114: aload 8
    //   116: ifnull +82 -> 198
    //   119: aload_0
    //   120: iload 10
    //   122: invokespecial 345	android/view/accessibility/AccessibilityInteractionClient:getFindAccessibilityNodeInfosResultAndClear	(I)Ljava/util/List;
    //   125: astore 7
    //   127: aload_0
    //   128: aload 7
    //   130: iload_1
    //   131: iload 5
    //   133: aload 8
    //   135: invokespecial 347	android/view/accessibility/AccessibilityInteractionClient:finalizeAndCacheAccessibilityNodeInfos	(Ljava/util/List;IZ[Ljava/lang/String;)V
    //   138: aload 7
    //   140: ifnull +81 -> 221
    //   143: aload 7
    //   145: invokeinterface 348 1 0
    //   150: ifne +71 -> 221
    //   153: iconst_1
    //   154: istore_1
    //   155: iload_1
    //   156: aload 7
    //   158: invokeinterface 96 1 0
    //   163: if_icmpge +23 -> 186
    //   166: aload 7
    //   168: iload_1
    //   169: invokeinterface 100 2 0
    //   174: checkcast 102	android/view/accessibility/AccessibilityNodeInfo
    //   177: invokevirtual 351	android/view/accessibility/AccessibilityNodeInfo:recycle	()V
    //   180: iinc 1 1
    //   183: goto -28 -> 155
    //   186: aload 7
    //   188: iconst_0
    //   189: invokeinterface 100 2 0
    //   194: checkcast 102	android/view/accessibility/AccessibilityNodeInfo
    //   197: areturn
    //   198: goto +23 -> 221
    //   201: astore 7
    //   203: lload 11
    //   205: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   208: aload 7
    //   210: athrow
    //   211: astore 7
    //   213: goto +13 -> 226
    //   216: astore 7
    //   218: goto +8 -> 226
    //   221: goto +16 -> 237
    //   224: astore 7
    //   226: ldc 13
    //   228: ldc_w 353
    //   231: aload 7
    //   233: invokestatic 356	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   236: pop
    //   237: aconst_null
    //   238: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	239	0	this	AccessibilityInteractionClient
    //   0	239	1	paramInt1	int
    //   0	239	2	paramInt2	int
    //   0	239	3	paramLong	long
    //   0	239	5	paramBoolean	boolean
    //   0	239	6	paramInt3	int
    //   0	239	7	paramBundle	Bundle
    //   32	102	8	localObject1	Object
    //   47	17	9	localObject2	Object
    //   78	43	10	i	int
    //   83	121	11	l	long
    // Exception table:
    //   from	to	target	type
    //   44	49	66	android/os/RemoteException
    //   85	109	201	finally
    //   127	138	211	android/os/RemoteException
    //   143	153	211	android/os/RemoteException
    //   155	180	211	android/os/RemoteException
    //   186	198	211	android/os/RemoteException
    //   203	211	211	android/os/RemoteException
    //   49	58	216	android/os/RemoteException
    //   71	85	216	android/os/RemoteException
    //   109	114	216	android/os/RemoteException
    //   119	127	216	android/os/RemoteException
    //   28	34	224	android/os/RemoteException
  }
  
  /* Error */
  public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(int paramInt1, int paramInt2, long paramLong, String paramString)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 325	android/view/accessibility/AccessibilityInteractionClient:getConnection	(I)Landroid/accessibilityservice/IAccessibilityServiceConnection;
    //   4: astore 6
    //   6: aload 6
    //   8: ifnull +96 -> 104
    //   11: aload_0
    //   12: getfield 78	android/view/accessibility/AccessibilityInteractionClient:mInteractionIdCounter	Ljava/util/concurrent/atomic/AtomicInteger;
    //   15: invokevirtual 332	java/util/concurrent/atomic/AtomicInteger:getAndIncrement	()I
    //   18: istore 7
    //   20: invokestatic 337	android/os/Binder:clearCallingIdentity	()J
    //   23: lstore 8
    //   25: aload 6
    //   27: iload_2
    //   28: lload_3
    //   29: aload 5
    //   31: iload 7
    //   33: aload_0
    //   34: invokestatic 249	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   37: invokevirtual 252	java/lang/Thread:getId	()J
    //   40: invokeinterface 361 9 0
    //   45: astore 5
    //   47: lload 8
    //   49: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: aload 5
    //   54: ifnull +32 -> 86
    //   57: aload_0
    //   58: iload 7
    //   60: invokespecial 345	android/view/accessibility/AccessibilityInteractionClient:getFindAccessibilityNodeInfosResultAndClear	(I)Ljava/util/List;
    //   63: astore 6
    //   65: aload 6
    //   67: ifnull +16 -> 83
    //   70: aload_0
    //   71: aload 6
    //   73: iload_1
    //   74: iconst_0
    //   75: aload 5
    //   77: invokespecial 347	android/view/accessibility/AccessibilityInteractionClient:finalizeAndCacheAccessibilityNodeInfos	(Ljava/util/List;IZ[Ljava/lang/String;)V
    //   80: aload 6
    //   82: areturn
    //   83: goto +21 -> 104
    //   86: goto +18 -> 104
    //   89: astore 5
    //   91: lload 8
    //   93: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   96: aload 5
    //   98: athrow
    //   99: astore 5
    //   101: goto +8 -> 109
    //   104: goto +16 -> 120
    //   107: astore 5
    //   109: ldc 13
    //   111: ldc_w 363
    //   114: aload 5
    //   116: invokestatic 366	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   119: pop
    //   120: invokestatic 232	java/util/Collections:emptyList	()Ljava/util/List;
    //   123: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	this	AccessibilityInteractionClient
    //   0	124	1	paramInt1	int
    //   0	124	2	paramInt2	int
    //   0	124	3	paramLong	long
    //   0	124	5	paramString	String
    //   4	77	6	localObject	Object
    //   18	41	7	i	int
    //   23	69	8	l	long
    // Exception table:
    //   from	to	target	type
    //   25	47	89	finally
    //   70	80	99	android/os/RemoteException
    //   91	99	99	android/os/RemoteException
    //   0	6	107	android/os/RemoteException
    //   11	25	107	android/os/RemoteException
    //   47	52	107	android/os/RemoteException
    //   57	65	107	android/os/RemoteException
  }
  
  /* Error */
  public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(int paramInt1, int paramInt2, long paramLong, String paramString)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 325	android/view/accessibility/AccessibilityInteractionClient:getConnection	(I)Landroid/accessibilityservice/IAccessibilityServiceConnection;
    //   4: astore 6
    //   6: aload 6
    //   8: ifnull +96 -> 104
    //   11: aload_0
    //   12: getfield 78	android/view/accessibility/AccessibilityInteractionClient:mInteractionIdCounter	Ljava/util/concurrent/atomic/AtomicInteger;
    //   15: invokevirtual 332	java/util/concurrent/atomic/AtomicInteger:getAndIncrement	()I
    //   18: istore 7
    //   20: invokestatic 337	android/os/Binder:clearCallingIdentity	()J
    //   23: lstore 8
    //   25: aload 6
    //   27: iload_2
    //   28: lload_3
    //   29: aload 5
    //   31: iload 7
    //   33: aload_0
    //   34: invokestatic 249	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   37: invokevirtual 252	java/lang/Thread:getId	()J
    //   40: invokeinterface 370 9 0
    //   45: astore 6
    //   47: lload 8
    //   49: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: aload 6
    //   54: ifnull +32 -> 86
    //   57: aload_0
    //   58: iload 7
    //   60: invokespecial 345	android/view/accessibility/AccessibilityInteractionClient:getFindAccessibilityNodeInfosResultAndClear	(I)Ljava/util/List;
    //   63: astore 5
    //   65: aload 5
    //   67: ifnull +16 -> 83
    //   70: aload_0
    //   71: aload 5
    //   73: iload_1
    //   74: iconst_0
    //   75: aload 6
    //   77: invokespecial 347	android/view/accessibility/AccessibilityInteractionClient:finalizeAndCacheAccessibilityNodeInfos	(Ljava/util/List;IZ[Ljava/lang/String;)V
    //   80: aload 5
    //   82: areturn
    //   83: goto +21 -> 104
    //   86: goto +18 -> 104
    //   89: astore 5
    //   91: lload 8
    //   93: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   96: aload 5
    //   98: athrow
    //   99: astore 5
    //   101: goto +8 -> 109
    //   104: goto +16 -> 120
    //   107: astore 5
    //   109: ldc 13
    //   111: ldc_w 372
    //   114: aload 5
    //   116: invokestatic 366	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   119: pop
    //   120: invokestatic 232	java/util/Collections:emptyList	()Ljava/util/List;
    //   123: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	this	AccessibilityInteractionClient
    //   0	124	1	paramInt1	int
    //   0	124	2	paramInt2	int
    //   0	124	3	paramLong	long
    //   0	124	5	paramString	String
    //   4	72	6	localObject	Object
    //   18	41	7	i	int
    //   23	69	8	l	long
    // Exception table:
    //   from	to	target	type
    //   25	47	89	finally
    //   70	80	99	android/os/RemoteException
    //   91	99	99	android/os/RemoteException
    //   0	6	107	android/os/RemoteException
    //   11	25	107	android/os/RemoteException
    //   47	52	107	android/os/RemoteException
    //   57	65	107	android/os/RemoteException
  }
  
  /* Error */
  public AccessibilityNodeInfo findFocus(int paramInt1, int paramInt2, long paramLong, int paramInt3)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 325	android/view/accessibility/AccessibilityInteractionClient:getConnection	(I)Landroid/accessibilityservice/IAccessibilityServiceConnection;
    //   4: astore 6
    //   6: aload 6
    //   8: ifnull +88 -> 96
    //   11: aload_0
    //   12: getfield 78	android/view/accessibility/AccessibilityInteractionClient:mInteractionIdCounter	Ljava/util/concurrent/atomic/AtomicInteger;
    //   15: invokevirtual 332	java/util/concurrent/atomic/AtomicInteger:getAndIncrement	()I
    //   18: istore 7
    //   20: invokestatic 337	android/os/Binder:clearCallingIdentity	()J
    //   23: lstore 8
    //   25: aload 6
    //   27: iload_2
    //   28: lload_3
    //   29: iload 5
    //   31: iload 7
    //   33: aload_0
    //   34: invokestatic 249	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   37: invokevirtual 252	java/lang/Thread:getId	()J
    //   40: invokeinterface 377 9 0
    //   45: astore 6
    //   47: lload 8
    //   49: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: aload 6
    //   54: ifnull +24 -> 78
    //   57: aload_0
    //   58: iload 7
    //   60: invokespecial 379	android/view/accessibility/AccessibilityInteractionClient:getFindAccessibilityNodeInfoResultAndClear	(I)Landroid/view/accessibility/AccessibilityNodeInfo;
    //   63: astore 10
    //   65: aload_0
    //   66: aload 10
    //   68: iload_1
    //   69: iconst_0
    //   70: aload 6
    //   72: invokespecial 210	android/view/accessibility/AccessibilityInteractionClient:finalizeAndCacheAccessibilityNodeInfo	(Landroid/view/accessibility/AccessibilityNodeInfo;IZ[Ljava/lang/String;)V
    //   75: aload 10
    //   77: areturn
    //   78: goto +18 -> 96
    //   81: astore 6
    //   83: lload 8
    //   85: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   88: aload 6
    //   90: athrow
    //   91: astore 6
    //   93: goto +8 -> 101
    //   96: goto +16 -> 112
    //   99: astore 6
    //   101: ldc 13
    //   103: ldc_w 381
    //   106: aload 6
    //   108: invokestatic 366	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   111: pop
    //   112: aconst_null
    //   113: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	114	0	this	AccessibilityInteractionClient
    //   0	114	1	paramInt1	int
    //   0	114	2	paramInt2	int
    //   0	114	3	paramLong	long
    //   0	114	5	paramInt3	int
    //   4	67	6	localObject1	Object
    //   81	8	6	localObject2	Object
    //   91	1	6	localRemoteException1	RemoteException
    //   99	8	6	localRemoteException2	RemoteException
    //   18	41	7	i	int
    //   23	61	8	l	long
    //   63	13	10	localAccessibilityNodeInfo	AccessibilityNodeInfo
    // Exception table:
    //   from	to	target	type
    //   25	47	81	finally
    //   65	75	91	android/os/RemoteException
    //   83	91	91	android/os/RemoteException
    //   0	6	99	android/os/RemoteException
    //   11	25	99	android/os/RemoteException
    //   47	52	99	android/os/RemoteException
    //   57	65	99	android/os/RemoteException
  }
  
  /* Error */
  public AccessibilityNodeInfo focusSearch(int paramInt1, int paramInt2, long paramLong, int paramInt3)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 325	android/view/accessibility/AccessibilityInteractionClient:getConnection	(I)Landroid/accessibilityservice/IAccessibilityServiceConnection;
    //   4: astore 6
    //   6: aload 6
    //   8: ifnull +88 -> 96
    //   11: aload_0
    //   12: getfield 78	android/view/accessibility/AccessibilityInteractionClient:mInteractionIdCounter	Ljava/util/concurrent/atomic/AtomicInteger;
    //   15: invokevirtual 332	java/util/concurrent/atomic/AtomicInteger:getAndIncrement	()I
    //   18: istore 7
    //   20: invokestatic 337	android/os/Binder:clearCallingIdentity	()J
    //   23: lstore 8
    //   25: aload 6
    //   27: iload_2
    //   28: lload_3
    //   29: iload 5
    //   31: iload 7
    //   33: aload_0
    //   34: invokestatic 249	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   37: invokevirtual 252	java/lang/Thread:getId	()J
    //   40: invokeinterface 384 9 0
    //   45: astore 6
    //   47: lload 8
    //   49: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: aload 6
    //   54: ifnull +24 -> 78
    //   57: aload_0
    //   58: iload 7
    //   60: invokespecial 379	android/view/accessibility/AccessibilityInteractionClient:getFindAccessibilityNodeInfoResultAndClear	(I)Landroid/view/accessibility/AccessibilityNodeInfo;
    //   63: astore 10
    //   65: aload_0
    //   66: aload 10
    //   68: iload_1
    //   69: iconst_0
    //   70: aload 6
    //   72: invokespecial 210	android/view/accessibility/AccessibilityInteractionClient:finalizeAndCacheAccessibilityNodeInfo	(Landroid/view/accessibility/AccessibilityNodeInfo;IZ[Ljava/lang/String;)V
    //   75: aload 10
    //   77: areturn
    //   78: goto +18 -> 96
    //   81: astore 6
    //   83: lload 8
    //   85: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
    //   88: aload 6
    //   90: athrow
    //   91: astore 6
    //   93: goto +8 -> 101
    //   96: goto +16 -> 112
    //   99: astore 6
    //   101: ldc 13
    //   103: ldc_w 386
    //   106: aload 6
    //   108: invokestatic 366	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   111: pop
    //   112: aconst_null
    //   113: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	114	0	this	AccessibilityInteractionClient
    //   0	114	1	paramInt1	int
    //   0	114	2	paramInt2	int
    //   0	114	3	paramLong	long
    //   0	114	5	paramInt3	int
    //   4	67	6	localObject1	Object
    //   81	8	6	localObject2	Object
    //   91	1	6	localRemoteException1	RemoteException
    //   99	8	6	localRemoteException2	RemoteException
    //   18	41	7	i	int
    //   23	61	8	l	long
    //   63	13	10	localAccessibilityNodeInfo	AccessibilityNodeInfo
    // Exception table:
    //   from	to	target	type
    //   25	47	81	finally
    //   65	75	91	android/os/RemoteException
    //   83	91	91	android/os/RemoteException
    //   0	6	99	android/os/RemoteException
    //   11	25	99	android/os/RemoteException
    //   47	52	99	android/os/RemoteException
    //   57	65	99	android/os/RemoteException
  }
  
  public AccessibilityNodeInfo getRootInActiveWindow(int paramInt)
  {
    return findAccessibilityNodeInfoByAccessibilityId(paramInt, Integer.MAX_VALUE, AccessibilityNodeInfo.ROOT_NODE_ID, false, 4, null);
  }
  
  public AccessibilityWindowInfo getWindow(int paramInt1, int paramInt2)
  {
    try
    {
      Object localObject1 = getConnection(paramInt1);
      if (localObject1 != null)
      {
        AccessibilityWindowInfo localAccessibilityWindowInfo = sAccessibilityCache.getWindow(paramInt2);
        if (localAccessibilityWindowInfo != null) {
          return localAccessibilityWindowInfo;
        }
        long l = Binder.clearCallingIdentity();
        try
        {
          localObject1 = ((IAccessibilityServiceConnection)localObject1).getWindow(paramInt2);
          Binder.restoreCallingIdentity(l);
          if (localObject1 != null)
          {
            sAccessibilityCache.addWindow((AccessibilityWindowInfo)localObject1);
            return (AccessibilityWindowInfo)localObject1;
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("AccessibilityInteractionClient", "Error while calling remote getWindow", localRemoteException);
    }
    return null;
  }
  
  public List<AccessibilityWindowInfo> getWindows(int paramInt)
  {
    try
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = getConnection(paramInt);
      if (localIAccessibilityServiceConnection != null)
      {
        List localList = sAccessibilityCache.getWindows();
        if (localList != null) {
          return localList;
        }
        long l = Binder.clearCallingIdentity();
        try
        {
          localList = localIAccessibilityServiceConnection.getWindows();
          Binder.restoreCallingIdentity(l);
          if (localList != null)
          {
            sAccessibilityCache.setWindows(localList);
            return localList;
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("AccessibilityInteractionClient", "Error while calling remote getWindows", localRemoteException);
    }
    return Collections.emptyList();
  }
  
  public void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    sAccessibilityCache.onAccessibilityEvent(paramAccessibilityEvent);
  }
  
  public boolean performAccessibilityAction(int paramInt1, int paramInt2, long paramLong, int paramInt3, Bundle paramBundle)
  {
    try
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = getConnection(paramInt1);
      if (localIAccessibilityServiceConnection != null)
      {
        paramInt1 = this.mInteractionIdCounter.getAndIncrement();
        long l = Binder.clearCallingIdentity();
        try
        {
          boolean bool = localIAccessibilityServiceConnection.performAccessibilityAction(paramInt2, paramLong, paramInt3, paramBundle, paramInt1, this, Thread.currentThread().getId());
          Binder.restoreCallingIdentity(l);
          if (bool) {
            return getPerformAccessibilityActionResultAndClear(paramInt1);
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    catch (RemoteException paramBundle)
    {
      Log.w("AccessibilityInteractionClient", "Error while calling remote performAccessibilityAction", paramBundle);
    }
    return false;
  }
  
  public void setFindAccessibilityNodeInfoResult(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (paramInt > this.mInteractionId)
      {
        this.mFindAccessibilityNodeInfoResult = paramAccessibilityNodeInfo;
        this.mInteractionId = paramInt;
      }
      this.mInstanceLock.notifyAll();
      return;
    }
  }
  
  public void setFindAccessibilityNodeInfosResult(List<AccessibilityNodeInfo> paramList, int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (paramInt > this.mInteractionId)
      {
        if (paramList != null)
        {
          int i;
          if (Binder.getCallingPid() != Process.myPid()) {
            i = 1;
          } else {
            i = 0;
          }
          if (i == 0)
          {
            ArrayList localArrayList = new java/util/ArrayList;
            localArrayList.<init>(paramList);
            this.mFindAccessibilityNodeInfosResult = localArrayList;
          }
          else
          {
            this.mFindAccessibilityNodeInfosResult = paramList;
          }
        }
        else
        {
          this.mFindAccessibilityNodeInfosResult = Collections.emptyList();
        }
        this.mInteractionId = paramInt;
      }
      this.mInstanceLock.notifyAll();
      return;
    }
  }
  
  public void setPerformAccessibilityActionResult(boolean paramBoolean, int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (paramInt > this.mInteractionId)
      {
        this.mPerformAccessibilityActionResult = paramBoolean;
        this.mInteractionId = paramInt;
      }
      this.mInstanceLock.notifyAll();
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void setSameThreadMessage(Message paramMessage)
  {
    synchronized (this.mInstanceLock)
    {
      this.mSameThreadMessage = paramMessage;
      this.mInstanceLock.notifyAll();
      return;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityInteractionClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */