package android.webkit;

import android.annotation.UnsupportedAppUsage;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IWebViewUpdateService
  extends IInterface
{
  public abstract String changeProviderAndSetting(String paramString)
    throws RemoteException;
  
  public abstract void enableMultiProcess(boolean paramBoolean)
    throws RemoteException;
  
  public abstract WebViewProviderInfo[] getAllWebViewPackages()
    throws RemoteException;
  
  public abstract PackageInfo getCurrentWebViewPackage()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract String getCurrentWebViewPackageName()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract WebViewProviderInfo[] getValidWebViewPackages()
    throws RemoteException;
  
  public abstract boolean isMultiProcessEnabled()
    throws RemoteException;
  
  public abstract void notifyRelroCreationCompleted()
    throws RemoteException;
  
  public abstract WebViewProviderResponse waitForAndGetProvider()
    throws RemoteException;
  
  public static class Default
    implements IWebViewUpdateService
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public String changeProviderAndSetting(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public void enableMultiProcess(boolean paramBoolean)
      throws RemoteException
    {}
    
    public WebViewProviderInfo[] getAllWebViewPackages()
      throws RemoteException
    {
      return null;
    }
    
    public PackageInfo getCurrentWebViewPackage()
      throws RemoteException
    {
      return null;
    }
    
    public String getCurrentWebViewPackageName()
      throws RemoteException
    {
      return null;
    }
    
    public WebViewProviderInfo[] getValidWebViewPackages()
      throws RemoteException
    {
      return null;
    }
    
    public boolean isMultiProcessEnabled()
      throws RemoteException
    {
      return false;
    }
    
    public void notifyRelroCreationCompleted()
      throws RemoteException
    {}
    
    public WebViewProviderResponse waitForAndGetProvider()
      throws RemoteException
    {
      return null;
    }
  }
  
  public static abstract class Stub
    extends Binder
    implements IWebViewUpdateService
  {
    private static final String DESCRIPTOR = "android.webkit.IWebViewUpdateService";
    static final int TRANSACTION_changeProviderAndSetting = 3;
    static final int TRANSACTION_enableMultiProcess = 9;
    static final int TRANSACTION_getAllWebViewPackages = 5;
    static final int TRANSACTION_getCurrentWebViewPackage = 7;
    static final int TRANSACTION_getCurrentWebViewPackageName = 6;
    static final int TRANSACTION_getValidWebViewPackages = 4;
    static final int TRANSACTION_isMultiProcessEnabled = 8;
    static final int TRANSACTION_notifyRelroCreationCompleted = 1;
    static final int TRANSACTION_waitForAndGetProvider = 2;
    
    public Stub()
    {
      attachInterface(this, "android.webkit.IWebViewUpdateService");
    }
    
    public static IWebViewUpdateService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.webkit.IWebViewUpdateService");
      if ((localIInterface != null) && ((localIInterface instanceof IWebViewUpdateService))) {
        return (IWebViewUpdateService)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWebViewUpdateService getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 9: 
        return "enableMultiProcess";
      case 8: 
        return "isMultiProcessEnabled";
      case 7: 
        return "getCurrentWebViewPackage";
      case 6: 
        return "getCurrentWebViewPackageName";
      case 5: 
        return "getAllWebViewPackages";
      case 4: 
        return "getValidWebViewPackages";
      case 3: 
        return "changeProviderAndSetting";
      case 2: 
        return "waitForAndGetProvider";
      }
      return "notifyRelroCreationCompleted";
    }
    
    public static boolean setDefaultImpl(IWebViewUpdateService paramIWebViewUpdateService)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWebViewUpdateService != null))
      {
        Proxy.sDefaultImpl = paramIWebViewUpdateService;
        return true;
      }
      return false;
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public String getTransactionName(int paramInt)
    {
      return getDefaultTransactionName(paramInt);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      if (paramInt1 != 1598968902)
      {
        boolean bool = false;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 9: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          if (paramParcel1.readInt() != 0) {
            bool = true;
          }
          enableMultiProcess(bool);
          paramParcel2.writeNoException();
          return true;
        case 8: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          paramInt1 = isMultiProcessEnabled();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          paramParcel1 = getCurrentWebViewPackage();
          paramParcel2.writeNoException();
          if (paramParcel1 != null)
          {
            paramParcel2.writeInt(1);
            paramParcel1.writeToParcel(paramParcel2, 1);
          }
          else
          {
            paramParcel2.writeInt(0);
          }
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          paramParcel1 = getCurrentWebViewPackageName();
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          paramParcel1 = getAllWebViewPackages();
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          paramParcel1 = getValidWebViewPackages();
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          paramParcel1 = changeProviderAndSetting(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
          paramParcel1 = waitForAndGetProvider();
          paramParcel2.writeNoException();
          if (paramParcel1 != null)
          {
            paramParcel2.writeInt(1);
            paramParcel1.writeToParcel(paramParcel2, 1);
          }
          else
          {
            paramParcel2.writeInt(0);
          }
          return true;
        }
        paramParcel1.enforceInterface("android.webkit.IWebViewUpdateService");
        notifyRelroCreationCompleted();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel2.writeString("android.webkit.IWebViewUpdateService");
      return true;
    }
    
    private static class Proxy
      implements IWebViewUpdateService
    {
      public static IWebViewUpdateService sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String changeProviderAndSetting(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            paramString = IWebViewUpdateService.Stub.getDefaultImpl().changeProviderAndSetting(paramString);
            return paramString;
          }
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void enableMultiProcess(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(9, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            IWebViewUpdateService.Stub.getDefaultImpl().enableMultiProcess(paramBoolean);
            return;
          }
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public WebViewProviderInfo[] getAllWebViewPackages()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          if ((!this.mRemote.transact(5, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            arrayOfWebViewProviderInfo = IWebViewUpdateService.Stub.getDefaultImpl().getAllWebViewPackages();
            return arrayOfWebViewProviderInfo;
          }
          localParcel2.readException();
          WebViewProviderInfo[] arrayOfWebViewProviderInfo = (WebViewProviderInfo[])localParcel2.createTypedArray(WebViewProviderInfo.CREATOR);
          return arrayOfWebViewProviderInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public PackageInfo getCurrentWebViewPackage()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          PackageInfo localPackageInfo;
          if ((!this.mRemote.transact(7, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            localPackageInfo = IWebViewUpdateService.Stub.getDefaultImpl().getCurrentWebViewPackage();
            return localPackageInfo;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            localPackageInfo = (PackageInfo)PackageInfo.CREATOR.createFromParcel(localParcel2);
          } else {
            localPackageInfo = null;
          }
          return localPackageInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getCurrentWebViewPackageName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          if ((!this.mRemote.transact(6, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            str = IWebViewUpdateService.Stub.getDefaultImpl().getCurrentWebViewPackageName();
            return str;
          }
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.webkit.IWebViewUpdateService";
      }
      
      public WebViewProviderInfo[] getValidWebViewPackages()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            arrayOfWebViewProviderInfo = IWebViewUpdateService.Stub.getDefaultImpl().getValidWebViewPackages();
            return arrayOfWebViewProviderInfo;
          }
          localParcel2.readException();
          WebViewProviderInfo[] arrayOfWebViewProviderInfo = (WebViewProviderInfo[])localParcel2.createTypedArray(WebViewProviderInfo.CREATOR);
          return arrayOfWebViewProviderInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean isMultiProcessEnabled()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(8, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            bool = IWebViewUpdateService.Stub.getDefaultImpl().isMultiProcessEnabled();
            return bool;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
            bool = true;
          }
          return bool;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void notifyRelroCreationCompleted()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            IWebViewUpdateService.Stub.getDefaultImpl().notifyRelroCreationCompleted();
            return;
          }
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public WebViewProviderResponse waitForAndGetProvider()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.webkit.IWebViewUpdateService");
          WebViewProviderResponse localWebViewProviderResponse;
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IWebViewUpdateService.Stub.getDefaultImpl() != null))
          {
            localWebViewProviderResponse = IWebViewUpdateService.Stub.getDefaultImpl().waitForAndGetProvider();
            return localWebViewProviderResponse;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            localWebViewProviderResponse = (WebViewProviderResponse)WebViewProviderResponse.CREATOR.createFromParcel(localParcel2);
          } else {
            localWebViewProviderResponse = null;
          }
          return localWebViewProviderResponse;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/IWebViewUpdateService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */