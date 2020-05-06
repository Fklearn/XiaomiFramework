package org.egret.plugin.mi.java.egretruntimelauncher;

import android.util.Log;
import java.io.File;
import org.egret.plugin.mi.android.util.launcher.FileUtil;
import org.egret.plugin.mi.android.util.launcher.Md5Util;
import org.egret.plugin.mi.android.util.launcher.NetClass;
import org.egret.plugin.mi.android.util.launcher.NetClass.OnNetListener;
import org.egret.plugin.mi.android.util.launcher.ZipClass;
import org.egret.plugin.mi.android.util.launcher.ZipClass.OnZipListener;

public class EgretRuntimeLibrary
  implements Runnable
{
  protected static final String TAG = "EgretRuntimeLibrary";
  private File cacheRoot;
  private OnDownloadListener downloadListener;
  private volatile boolean isCancelling;
  private Library library;
  private File root;
  private File sdRoot;
  
  public EgretRuntimeLibrary(Library paramLibrary, File paramFile1, File paramFile2, File paramFile3)
  {
    this.library = paramLibrary;
    this.root = paramFile1;
    this.cacheRoot = paramFile2;
    this.sdRoot = paramFile3;
  }
  
  private void doDownload()
  {
    File localFile = this.sdRoot;
    if (localFile == null) {
      localFile = this.cacheRoot;
    }
    localFile = new File(localFile, this.library.getZipName());
    new NetClass().writeResponseToFile(this.library.getUrl(), localFile, new NetClass.OnNetListener()
    {
      public void onError(String paramAnonymousString)
      {
        EgretRuntimeLibrary.this.downloadListener.onError(paramAnonymousString);
      }
      
      public void onProgress(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        EgretRuntimeLibrary.this.downloadListener.onProgress(paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public void onSuccess(String paramAnonymousString)
      {
        if (EgretRuntimeLibrary.this.isCancelling) {
          return;
        }
        if (EgretRuntimeLibrary.this.doMove()) {
          EgretRuntimeLibrary.this.doUnzip();
        }
      }
    });
  }
  
  private boolean doMove()
  {
    if (this.isCancelling)
    {
      this.downloadListener.onError("thread is cancelling");
      return false;
    }
    File localFile = this.sdRoot;
    if ((localFile != null) && (!FileUtil.Copy(new File(localFile, this.library.getZipName()), new File(this.cacheRoot, this.library.getZipName()))))
    {
      this.downloadListener.onError("copy file error");
      return false;
    }
    return true;
  }
  
  private void doUnzip()
  {
    if (this.isCancelling) {
      this.downloadListener.onError("thread is cancelling");
    }
    final File localFile1 = new File(this.cacheRoot, this.library.getZipName());
    final File localFile2 = new File(this.root, this.library.getLibraryName());
    if (!Md5Util.checkMd5(localFile1, this.library.getZipCheckSum())) {
      this.downloadListener.onError("cache file md5 error");
    }
    new ZipClass().unzip(localFile1, this.root, new ZipClass.OnZipListener()
    {
      public void onError(String paramAnonymousString)
      {
        paramAnonymousString = EgretRuntimeLibrary.this.downloadListener;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("fail to unzip file: ");
        localStringBuilder.append(localFile1.getAbsolutePath());
        paramAnonymousString.onError(localStringBuilder.toString());
      }
      
      public void onFileProgress(int paramAnonymousInt1, int paramAnonymousInt2) {}
      
      public void onProgress(int paramAnonymousInt1, int paramAnonymousInt2) {}
      
      public void onSuccess()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Success to unzip file: ");
        localStringBuilder.append(localFile1.getAbsolutePath());
        Log.i("EgretRuntimeLibrary", localStringBuilder.toString());
        if (!localFile1.delete())
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Fail to delete file: ");
          localStringBuilder.append(localFile1.getAbsolutePath());
          Log.e("EgretRuntimeLibrary", localStringBuilder.toString());
        }
        if (!Md5Util.checkMd5(localFile2, EgretRuntimeLibrary.this.library.getLibraryCheckSum()))
        {
          EgretRuntimeLibrary.this.downloadListener.onError("target file md5 error");
          return;
        }
        EgretRuntimeLibrary.this.downloadListener.onSuccess();
      }
    });
  }
  
  public void download(OnDownloadListener paramOnDownloadListener)
  {
    if ((this.library != null) && (this.root != null) && (this.cacheRoot != null) && (paramOnDownloadListener != null))
    {
      this.downloadListener = paramOnDownloadListener;
      return;
    }
    paramOnDownloadListener.onError("libray, root, cacheRoot, listener may be null");
  }
  
  public void run()
  {
    this.isCancelling = false;
    doDownload();
  }
  
  public void stop()
  {
    this.isCancelling = true;
  }
  
  public static abstract interface OnDownloadListener
  {
    public abstract void onError(String paramString);
    
    public abstract void onProgress(int paramInt1, int paramInt2);
    
    public abstract void onSuccess();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/java/egretruntimelauncher/EgretRuntimeLibrary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */