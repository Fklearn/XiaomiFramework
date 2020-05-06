package android.webkit;

import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.LoadedApk;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import dalvik.system.VMRuntime;
import java.util.Arrays;

@VisibleForTesting
public class WebViewLibraryLoader
{
  private static final String CHROMIUM_WEBVIEW_NATIVE_RELRO_32 = "/data/misc/shared_relro/libwebviewchromium32.relro";
  private static final String CHROMIUM_WEBVIEW_NATIVE_RELRO_64 = "/data/misc/shared_relro/libwebviewchromium64.relro";
  private static final boolean DEBUG = false;
  private static final String LOGTAG = WebViewLibraryLoader.class.getSimpleName();
  private static boolean sAddressSpaceReserved = false;
  
  static void createRelroFile(boolean paramBoolean, String paramString1, String paramString2)
  {
    String str1;
    if (paramBoolean) {
      str1 = Build.SUPPORTED_64_BIT_ABIS[0];
    } else {
      str1 = Build.SUPPORTED_32_BIT_ABIS[0];
    }
    Runnable local1 = new Runnable()
    {
      public void run()
      {
        try
        {
          str = WebViewLibraryLoader.LOGTAG;
          StringBuilder localStringBuilder1 = new java/lang/StringBuilder;
          localStringBuilder1.<init>();
          localStringBuilder1.append("relro file creator for ");
          localStringBuilder1.append(WebViewLibraryLoader.this);
          localStringBuilder1.append(" crashed. Proceeding without");
          Log.e(str, localStringBuilder1.toString());
          WebViewFactory.getUpdateService().notifyRelroCreationCompleted();
        }
        catch (RemoteException localRemoteException)
        {
          String str = WebViewLibraryLoader.LOGTAG;
          StringBuilder localStringBuilder2 = new StringBuilder();
          localStringBuilder2.append("Cannot reach WebViewUpdateService. ");
          localStringBuilder2.append(localRemoteException.getMessage());
          Log.e(str, localStringBuilder2.toString());
        }
      }
    };
    try
    {
      localObject1 = (ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class);
      String str2 = RelroFileCreator.class.getName();
      Object localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      ((StringBuilder)localObject2).append("WebViewLoader-");
      ((StringBuilder)localObject2).append(str1);
      localObject2 = ((StringBuilder)localObject2).toString();
      if (!((ActivityManagerInternal)localObject1).startIsolatedProcess(str2, new String[] { paramString1, paramString2 }, (String)localObject2, str1, 1037, local1))
      {
        paramString1 = new java/lang/Exception;
        paramString1.<init>("Failed to start the relro file creator process");
        throw paramString1;
      }
    }
    finally
    {
      paramString2 = LOGTAG;
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("error starting relro file creator for abi ");
      ((StringBuilder)localObject1).append(str1);
      Log.e(paramString2, ((StringBuilder)localObject1).toString(), paramString1);
      local1.run();
    }
  }
  
  private static int createRelros(String paramString1, String paramString2)
  {
    int i = 0;
    if (Build.SUPPORTED_32_BIT_ABIS.length > 0)
    {
      createRelroFile(false, paramString1, paramString2);
      i = 0 + 1;
    }
    int j = i;
    if (Build.SUPPORTED_64_BIT_ABIS.length > 0)
    {
      createRelroFile(true, paramString1, paramString2);
      j = i + 1;
    }
    return j;
  }
  
  public static int loadNativeLibrary(ClassLoader paramClassLoader, String paramString)
  {
    if (!sAddressSpaceReserved)
    {
      Log.e(LOGTAG, "can't load with relro file; address space not reserved");
      return 2;
    }
    String str;
    if (VMRuntime.getRuntime().is64Bit()) {
      str = "/data/misc/shared_relro/libwebviewchromium64.relro";
    } else {
      str = "/data/misc/shared_relro/libwebviewchromium32.relro";
    }
    int i = nativeLoadWithRelroFile(paramString, str, paramClassLoader);
    if (i != 0) {
      Log.w(LOGTAG, "failed to load with relro file, proceeding without");
    }
    return i;
  }
  
  static native boolean nativeCreateRelroFile(String paramString1, String paramString2, ClassLoader paramClassLoader);
  
  static native int nativeLoadWithRelroFile(String paramString1, String paramString2, ClassLoader paramClassLoader);
  
  static native boolean nativeReserveAddressSpace(long paramLong);
  
  static int prepareNativeLibraries(PackageInfo paramPackageInfo)
  {
    String str = WebViewFactory.getWebViewLibrary(paramPackageInfo.applicationInfo);
    if (str == null) {
      return 0;
    }
    return createRelros(paramPackageInfo.packageName, str);
  }
  
  static void reserveAddressSpaceInZygote()
  {
    System.loadLibrary("webviewchromium_loader");
    long l;
    if (VMRuntime.getRuntime().is64Bit()) {
      l = 1073741824L;
    } else {
      l = 136314880L;
    }
    sAddressSpaceReserved = nativeReserveAddressSpace(l);
    if (!sAddressSpaceReserved)
    {
      String str = LOGTAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("reserving ");
      localStringBuilder.append(l);
      localStringBuilder.append(" bytes of address space failed");
      Log.e(str, localStringBuilder.toString());
    }
  }
  
  private static class RelroFileCreator
  {
    public static void main(String[] paramArrayOfString)
    {
      boolean bool = VMRuntime.getRuntime().is64Bit();
      try
      {
        if ((paramArrayOfString.length == 2) && (paramArrayOfString[0] != null) && (paramArrayOfString[1] != null))
        {
          localObject1 = paramArrayOfString[0];
          localObject2 = paramArrayOfString[1];
          paramArrayOfString = WebViewLibraryLoader.LOGTAG;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append("RelroFileCreator (64bit = ");
          localStringBuilder.append(bool);
          localStringBuilder.append("), package: ");
          localStringBuilder.append((String)localObject1);
          localStringBuilder.append(" library: ");
          localStringBuilder.append((String)localObject2);
          Log.v(paramArrayOfString, localStringBuilder.toString());
          if (!WebViewLibraryLoader.sAddressSpaceReserved)
          {
            Log.e(WebViewLibraryLoader.LOGTAG, "can't create relro file; address space not reserved");
            return;
          }
          localObject1 = ActivityThread.currentActivityThread().getPackageInfo((String)localObject1, null, 3);
          if (bool) {
            paramArrayOfString = "/data/misc/shared_relro/libwebviewchromium64.relro";
          } else {
            paramArrayOfString = "/data/misc/shared_relro/libwebviewchromium32.relro";
          }
          bool = WebViewLibraryLoader.nativeCreateRelroFile((String)localObject2, paramArrayOfString, ((LoadedApk)localObject1).getClassLoader());
          try
          {
            WebViewFactory.getUpdateServiceUnchecked().notifyRelroCreationCompleted();
          }
          catch (RemoteException paramArrayOfString)
          {
            Log.e(WebViewLibraryLoader.LOGTAG, "error notifying update service", paramArrayOfString);
          }
          if (!bool) {
            Log.e(WebViewLibraryLoader.LOGTAG, "failed to create relro file");
          }
          System.exit(0);
          return;
        }
        Object localObject1 = WebViewLibraryLoader.LOGTAG;
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append("Invalid RelroFileCreator args: ");
        ((StringBuilder)localObject2).append(Arrays.toString(paramArrayOfString));
        Log.e((String)localObject1, ((StringBuilder)localObject2).toString());
        return;
      }
      finally
      {
        try
        {
          WebViewFactory.getUpdateServiceUnchecked().notifyRelroCreationCompleted();
        }
        catch (RemoteException paramArrayOfString)
        {
          Log.e(WebViewLibraryLoader.LOGTAG, "error notifying update service", paramArrayOfString);
        }
        if (0 == 0) {
          Log.e(WebViewLibraryLoader.LOGTAG, "failed to create relro file");
        }
        System.exit(0);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewLibraryLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */