package android.view.autofill;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.WindowManager.LayoutParams;

public abstract interface IAutofillWindowPresenter
  extends IInterface
{
  public abstract void hide(Rect paramRect)
    throws RemoteException;
  
  public abstract void show(WindowManager.LayoutParams paramLayoutParams, Rect paramRect, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public static class Default
    implements IAutofillWindowPresenter
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void hide(Rect paramRect)
      throws RemoteException
    {}
    
    public void show(WindowManager.LayoutParams paramLayoutParams, Rect paramRect, boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAutofillWindowPresenter
  {
    private static final String DESCRIPTOR = "android.view.autofill.IAutofillWindowPresenter";
    static final int TRANSACTION_hide = 2;
    static final int TRANSACTION_show = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.autofill.IAutofillWindowPresenter");
    }
    
    public static IAutofillWindowPresenter asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.autofill.IAutofillWindowPresenter");
      if ((localIInterface != null) && ((localIInterface instanceof IAutofillWindowPresenter))) {
        return (IAutofillWindowPresenter)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAutofillWindowPresenter getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2) {
          return null;
        }
        return "hide";
      }
      return "show";
    }
    
    public static boolean setDefaultImpl(IAutofillWindowPresenter paramIAutofillWindowPresenter)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAutofillWindowPresenter != null))
      {
        Proxy.sDefaultImpl = paramIAutofillWindowPresenter;
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
      if (paramInt1 != 1)
      {
        if (paramInt1 != 2)
        {
          if (paramInt1 != 1598968902) {
            return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
          }
          paramParcel2.writeString("android.view.autofill.IAutofillWindowPresenter");
          return true;
        }
        paramParcel1.enforceInterface("android.view.autofill.IAutofillWindowPresenter");
        if (paramParcel1.readInt() != 0) {
          paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel1 = null;
        }
        hide(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.view.autofill.IAutofillWindowPresenter");
      if (paramParcel1.readInt() != 0) {
        paramParcel2 = (WindowManager.LayoutParams)WindowManager.LayoutParams.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel2 = null;
      }
      Rect localRect;
      if (paramParcel1.readInt() != 0) {
        localRect = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
      } else {
        localRect = null;
      }
      boolean bool;
      if (paramParcel1.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      show(paramParcel2, localRect, bool, paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IAutofillWindowPresenter
    {
      public static IAutofillWindowPresenter sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.autofill.IAutofillWindowPresenter";
      }
      
      public void hide(Rect paramRect)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutofillWindowPresenter");
          if (paramRect != null)
          {
            localParcel.writeInt(1);
            paramRect.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IAutofillWindowPresenter.Stub.getDefaultImpl() != null))
          {
            IAutofillWindowPresenter.Stub.getDefaultImpl().hide(paramRect);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void show(WindowManager.LayoutParams paramLayoutParams, Rect paramRect, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutofillWindowPresenter");
          int i = 0;
          if (paramLayoutParams != null)
          {
            localParcel.writeInt(1);
            paramLayoutParams.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramRect != null)
          {
            localParcel.writeInt(1);
            paramRect.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramBoolean) {
            i = 1;
          }
          localParcel.writeInt(i);
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IAutofillWindowPresenter.Stub.getDefaultImpl() != null))
          {
            IAutofillWindowPresenter.Stub.getDefaultImpl().show(paramLayoutParams, paramRect, paramBoolean, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/IAutofillWindowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */