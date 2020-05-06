package android.view.autofill;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.service.autofill.UserData;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.IResultReceiver.Stub;
import java.util.List;

public abstract interface IAutoFillManager
  extends IInterface
{
  public abstract void addClient(IAutoFillManagerClient paramIAutoFillManagerClient, ComponentName paramComponentName, int paramInt, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void cancelSession(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void disableOwnedAutofillServices(int paramInt)
    throws RemoteException;
  
  public abstract void finishSession(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void getAutofillServiceComponentName(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void getAvailableFieldClassificationAlgorithms(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void getDefaultFieldClassificationAlgorithm(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void getFillEventHistory(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void getUserData(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void getUserDataId(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void isFieldClassificationEnabled(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void isServiceEnabled(int paramInt, String paramString, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void isServiceSupported(int paramInt, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void onPendingSaveUi(int paramInt, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void removeClient(IAutoFillManagerClient paramIAutoFillManagerClient, int paramInt)
    throws RemoteException;
  
  public abstract void restoreSession(int paramInt, IBinder paramIBinder1, IBinder paramIBinder2, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void setAugmentedAutofillWhitelist(List<String> paramList, List<ComponentName> paramList1, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void setAuthenticationResult(Bundle paramBundle, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void setAutofillFailure(int paramInt1, List<AutofillId> paramList, int paramInt2)
    throws RemoteException;
  
  public abstract void setHasCallback(int paramInt1, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUserData(UserData paramUserData)
    throws RemoteException;
  
  public abstract void startSession(IBinder paramIBinder1, IBinder paramIBinder2, AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt1, boolean paramBoolean1, int paramInt2, ComponentName paramComponentName, boolean paramBoolean2, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void updateSession(int paramInt1, AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public static class Default
    implements IAutoFillManager
  {
    public void addClient(IAutoFillManagerClient paramIAutoFillManagerClient, ComponentName paramComponentName, int paramInt, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public void cancelSession(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void disableOwnedAutofillServices(int paramInt)
      throws RemoteException
    {}
    
    public void finishSession(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void getAutofillServiceComponentName(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void getAvailableFieldClassificationAlgorithms(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void getDefaultFieldClassificationAlgorithm(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void getFillEventHistory(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void getUserData(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void getUserDataId(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void isFieldClassificationEnabled(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void isServiceEnabled(int paramInt, String paramString, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void isServiceSupported(int paramInt, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void onPendingSaveUi(int paramInt, IBinder paramIBinder)
      throws RemoteException
    {}
    
    public void removeClient(IAutoFillManagerClient paramIAutoFillManagerClient, int paramInt)
      throws RemoteException
    {}
    
    public void restoreSession(int paramInt, IBinder paramIBinder1, IBinder paramIBinder2, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void setAugmentedAutofillWhitelist(List<String> paramList, List<ComponentName> paramList1, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void setAuthenticationResult(Bundle paramBundle, int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {}
    
    public void setAutofillFailure(int paramInt1, List<AutofillId> paramList, int paramInt2)
      throws RemoteException
    {}
    
    public void setHasCallback(int paramInt1, int paramInt2, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setUserData(UserData paramUserData)
      throws RemoteException
    {}
    
    public void startSession(IBinder paramIBinder1, IBinder paramIBinder2, AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt1, boolean paramBoolean1, int paramInt2, ComponentName paramComponentName, boolean paramBoolean2, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void updateSession(int paramInt1, AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt2, int paramInt3, int paramInt4)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAutoFillManager
  {
    private static final String DESCRIPTOR = "android.view.autofill.IAutoFillManager";
    static final int TRANSACTION_addClient = 1;
    static final int TRANSACTION_cancelSession = 9;
    static final int TRANSACTION_disableOwnedAutofillServices = 12;
    static final int TRANSACTION_finishSession = 8;
    static final int TRANSACTION_getAutofillServiceComponentName = 20;
    static final int TRANSACTION_getAvailableFieldClassificationAlgorithms = 21;
    static final int TRANSACTION_getDefaultFieldClassificationAlgorithm = 22;
    static final int TRANSACTION_getFillEventHistory = 4;
    static final int TRANSACTION_getUserData = 16;
    static final int TRANSACTION_getUserDataId = 17;
    static final int TRANSACTION_isFieldClassificationEnabled = 19;
    static final int TRANSACTION_isServiceEnabled = 14;
    static final int TRANSACTION_isServiceSupported = 13;
    static final int TRANSACTION_onPendingSaveUi = 15;
    static final int TRANSACTION_removeClient = 2;
    static final int TRANSACTION_restoreSession = 5;
    static final int TRANSACTION_setAugmentedAutofillWhitelist = 23;
    static final int TRANSACTION_setAuthenticationResult = 10;
    static final int TRANSACTION_setAutofillFailure = 7;
    static final int TRANSACTION_setHasCallback = 11;
    static final int TRANSACTION_setUserData = 18;
    static final int TRANSACTION_startSession = 3;
    static final int TRANSACTION_updateSession = 6;
    
    public Stub()
    {
      attachInterface(this, "android.view.autofill.IAutoFillManager");
    }
    
    public static IAutoFillManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.autofill.IAutoFillManager");
      if ((localIInterface != null) && ((localIInterface instanceof IAutoFillManager))) {
        return (IAutoFillManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAutoFillManager getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 23: 
        return "setAugmentedAutofillWhitelist";
      case 22: 
        return "getDefaultFieldClassificationAlgorithm";
      case 21: 
        return "getAvailableFieldClassificationAlgorithms";
      case 20: 
        return "getAutofillServiceComponentName";
      case 19: 
        return "isFieldClassificationEnabled";
      case 18: 
        return "setUserData";
      case 17: 
        return "getUserDataId";
      case 16: 
        return "getUserData";
      case 15: 
        return "onPendingSaveUi";
      case 14: 
        return "isServiceEnabled";
      case 13: 
        return "isServiceSupported";
      case 12: 
        return "disableOwnedAutofillServices";
      case 11: 
        return "setHasCallback";
      case 10: 
        return "setAuthenticationResult";
      case 9: 
        return "cancelSession";
      case 8: 
        return "finishSession";
      case 7: 
        return "setAutofillFailure";
      case 6: 
        return "updateSession";
      case 5: 
        return "restoreSession";
      case 4: 
        return "getFillEventHistory";
      case 3: 
        return "startSession";
      case 2: 
        return "removeClient";
      }
      return "addClient";
    }
    
    public static boolean setDefaultImpl(IAutoFillManager paramIAutoFillManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAutoFillManager != null))
      {
        Proxy.sDefaultImpl = paramIAutoFillManager;
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
        boolean bool1 = false;
        AutofillValue localAutofillValue;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 23: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          setAugmentedAutofillWhitelist(paramParcel1.createStringArrayList(), paramParcel1.createTypedArrayList(ComponentName.CREATOR), IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 22: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          getDefaultFieldClassificationAlgorithm(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 21: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          getAvailableFieldClassificationAlgorithms(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 20: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          getAutofillServiceComponentName(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 19: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          isFieldClassificationEnabled(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 18: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (UserData)UserData.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          setUserData(paramParcel1);
          return true;
        case 17: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          getUserDataId(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 16: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          getUserData(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 15: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          onPendingSaveUi(paramParcel1.readInt(), paramParcel1.readStrongBinder());
          return true;
        case 14: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          isServiceEnabled(paramParcel1.readInt(), paramParcel1.readString(), IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 13: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          isServiceSupported(paramParcel1.readInt(), IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 12: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          disableOwnedAutofillServices(paramParcel1.readInt());
          return true;
        case 11: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          paramInt2 = paramParcel1.readInt();
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          }
          setHasCallback(paramInt2, paramInt1, bool1);
          return true;
        case 10: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          setAuthenticationResult(paramParcel2, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        case 9: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          cancelSession(paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        case 8: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          finishSession(paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          setAutofillFailure(paramParcel1.readInt(), paramParcel1.createTypedArrayList(AutofillId.CREATOR), paramParcel1.readInt());
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            localObject = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          if (paramParcel1.readInt() != 0) {
            localAutofillValue = (AutofillValue)AutofillValue.CREATOR.createFromParcel(paramParcel1);
          } else {
            localAutofillValue = null;
          }
          updateSession(paramInt1, paramParcel2, (Rect)localObject, localAutofillValue, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          restoreSession(paramParcel1.readInt(), paramParcel1.readStrongBinder(), paramParcel1.readStrongBinder(), IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          getFillEventHistory(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          IBinder localIBinder1 = paramParcel1.readStrongBinder();
          IBinder localIBinder2 = paramParcel1.readStrongBinder();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            localObject = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          if (paramParcel1.readInt() != 0) {
            localAutofillValue = (AutofillValue)AutofillValue.CREATOR.createFromParcel(paramParcel1);
          } else {
            localAutofillValue = null;
          }
          paramInt2 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          } else {
            bool1 = false;
          }
          paramInt1 = paramParcel1.readInt();
          ComponentName localComponentName;
          if (paramParcel1.readInt() != 0) {
            localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          } else {
            localComponentName = null;
          }
          boolean bool2;
          if (paramParcel1.readInt() != 0) {
            bool2 = true;
          } else {
            bool2 = false;
          }
          startSession(localIBinder1, localIBinder2, paramParcel2, (Rect)localObject, localAutofillValue, paramInt2, bool1, paramInt1, localComponentName, bool2, IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
          removeClient(IAutoFillManagerClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("android.view.autofill.IAutoFillManager");
        Object localObject = IAutoFillManagerClient.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {
          paramParcel2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel2 = null;
        }
        addClient((IAutoFillManagerClient)localObject, paramParcel2, paramParcel1.readInt(), IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      }
      paramParcel2.writeString("android.view.autofill.IAutoFillManager");
      return true;
    }
    
    private static class Proxy
      implements IAutoFillManager
    {
      public static IAutoFillManager sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addClient(IAutoFillManagerClient paramIAutoFillManagerClient, ComponentName paramComponentName, int paramInt, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIAutoFillManagerClient != null) {
            localIBinder = paramIAutoFillManagerClient.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if (paramComponentName != null)
          {
            localParcel.writeInt(1);
            paramComponentName.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt);
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().addClient(paramIAutoFillManagerClient, paramComponentName, paramInt, paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancelSession(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          if ((!this.mRemote.transact(9, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().cancelSession(paramInt1, paramInt2);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void disableOwnedAutofillServices(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(12, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().disableOwnedAutofillServices(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void finishSession(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          if ((!this.mRemote.transact(8, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().finishSession(paramInt1, paramInt2);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getAutofillServiceComponentName(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(20, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().getAutofillServiceComponentName(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getAvailableFieldClassificationAlgorithms(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(21, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().getAvailableFieldClassificationAlgorithms(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getDefaultFieldClassificationAlgorithm(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(22, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().getDefaultFieldClassificationAlgorithm(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getFillEventHistory(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(4, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().getFillEventHistory(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.autofill.IAutoFillManager";
      }
      
      public void getUserData(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(16, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().getUserData(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getUserDataId(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(17, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().getUserDataId(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void isFieldClassificationEnabled(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(19, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().isFieldClassificationEnabled(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void isServiceEnabled(int paramInt, String paramString, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt);
          localParcel.writeString(paramString);
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(14, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().isServiceEnabled(paramInt, paramString, paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void isServiceSupported(int paramInt, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt);
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(13, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().isServiceSupported(paramInt, paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPendingSaveUi(int paramInt, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt);
          localParcel.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(15, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().onPendingSaveUi(paramInt, paramIBinder);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeClient(IAutoFillManagerClient paramIAutoFillManagerClient, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          IBinder localIBinder;
          if (paramIAutoFillManagerClient != null) {
            localIBinder = paramIAutoFillManagerClient.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().removeClient(paramIAutoFillManagerClient, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void restoreSession(int paramInt, IBinder paramIBinder1, IBinder paramIBinder2, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt);
          localParcel.writeStrongBinder(paramIBinder1);
          localParcel.writeStrongBinder(paramIBinder2);
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(5, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().restoreSession(paramInt, paramIBinder1, paramIBinder2, paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setAugmentedAutofillWhitelist(List<String> paramList, List<ComponentName> paramList1, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeStringList(paramList);
          localParcel.writeTypedList(paramList1);
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(23, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().setAugmentedAutofillWhitelist(paramList, paramList1, paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setAuthenticationResult(Bundle paramBundle, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          if (paramBundle != null)
          {
            localParcel.writeInt(1);
            paramBundle.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          if ((!this.mRemote.transact(10, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().setAuthenticationResult(paramBundle, paramInt1, paramInt2, paramInt3);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setAutofillFailure(int paramInt1, List<AutofillId> paramList, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt1);
          localParcel.writeTypedList(paramList);
          localParcel.writeInt(paramInt2);
          if ((!this.mRemote.transact(7, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().setAutofillFailure(paramInt1, paramList, paramInt2);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setHasCallback(int paramInt1, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(11, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().setHasCallback(paramInt1, paramInt2, paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setUserData(UserData paramUserData)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          if (paramUserData != null)
          {
            localParcel.writeInt(1);
            paramUserData.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(18, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            IAutoFillManager.Stub.getDefaultImpl().setUserData(paramUserData);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void startSession(IBinder paramIBinder1, IBinder paramIBinder2, AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt1, boolean paramBoolean1, int paramInt2, ComponentName paramComponentName, boolean paramBoolean2, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManager");
          localParcel.writeStrongBinder(paramIBinder1);
          localParcel.writeStrongBinder(paramIBinder2);
          int i = 0;
          if (paramAutofillId != null) {
            try
            {
              localParcel.writeInt(1);
              paramAutofillId.writeToParcel(localParcel, 0);
            }
            finally
            {
              break label293;
            }
          } else {
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
          if (paramAutofillValue != null)
          {
            localParcel.writeInt(1);
            paramAutofillValue.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt1);
          if (paramBoolean1) {
            j = 1;
          } else {
            j = 0;
          }
          localParcel.writeInt(j);
          localParcel.writeInt(paramInt2);
          if (paramComponentName != null)
          {
            localParcel.writeInt(1);
            paramComponentName.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          int j = i;
          if (paramBoolean2) {
            j = 1;
          }
          localParcel.writeInt(j);
          Object localObject;
          if (paramIResultReceiver != null) {
            localObject = paramIResultReceiver.asBinder();
          } else {
            localObject = null;
          }
          localParcel.writeStrongBinder((IBinder)localObject);
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IAutoFillManager.Stub.getDefaultImpl() != null))
          {
            localObject = IAutoFillManager.Stub.getDefaultImpl();
            try
            {
              ((IAutoFillManager)localObject).startSession(paramIBinder1, paramIBinder2, paramAutofillId, paramRect, paramAutofillValue, paramInt1, paramBoolean1, paramInt2, paramComponentName, paramBoolean2, paramIResultReceiver);
              return;
            }
            finally
            {
              break label293;
            }
          }
          return;
        }
        finally
        {
          label293:
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void updateSession(int paramInt1, AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt2, int paramInt3, int paramInt4)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 8
        //   5: aload 8
        //   7: ldc 34
        //   9: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 8
        //   14: iload_1
        //   15: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   18: aload_2
        //   19: ifnull +19 -> 38
        //   22: aload 8
        //   24: iconst_1
        //   25: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   28: aload_2
        //   29: aload 8
        //   31: iconst_0
        //   32: invokevirtual 174	android/view/autofill/AutofillId:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: goto +9 -> 44
        //   38: aload 8
        //   40: iconst_0
        //   41: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   44: aload_3
        //   45: ifnull +19 -> 64
        //   48: aload 8
        //   50: iconst_1
        //   51: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   54: aload_3
        //   55: aload 8
        //   57: iconst_0
        //   58: invokevirtual 177	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   61: goto +9 -> 70
        //   64: aload 8
        //   66: iconst_0
        //   67: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   70: aload 4
        //   72: ifnull +20 -> 92
        //   75: aload 8
        //   77: iconst_1
        //   78: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   81: aload 4
        //   83: aload 8
        //   85: iconst_0
        //   86: invokevirtual 180	android/view/autofill/AutofillValue:writeToParcel	(Landroid/os/Parcel;I)V
        //   89: goto +9 -> 98
        //   92: aload 8
        //   94: iconst_0
        //   95: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   98: aload 8
        //   100: iload 5
        //   102: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   105: aload 8
        //   107: iload 6
        //   109: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   112: aload 8
        //   114: iload 7
        //   116: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   119: aload_0
        //   120: getfield 21	android/view/autofill/IAutoFillManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   123: bipush 6
        //   125: aload 8
        //   127: aconst_null
        //   128: iconst_1
        //   129: invokeinterface 66 5 0
        //   134: ifne +34 -> 168
        //   137: invokestatic 70	android/view/autofill/IAutoFillManager$Stub:getDefaultImpl	()Landroid/view/autofill/IAutoFillManager;
        //   140: ifnull +28 -> 168
        //   143: invokestatic 70	android/view/autofill/IAutoFillManager$Stub:getDefaultImpl	()Landroid/view/autofill/IAutoFillManager;
        //   146: iload_1
        //   147: aload_2
        //   148: aload_3
        //   149: aload 4
        //   151: iload 5
        //   153: iload 6
        //   155: iload 7
        //   157: invokeinterface 186 8 0
        //   162: aload 8
        //   164: invokevirtual 75	android/os/Parcel:recycle	()V
        //   167: return
        //   168: aload 8
        //   170: invokevirtual 75	android/os/Parcel:recycle	()V
        //   173: return
        //   174: astore_2
        //   175: goto +12 -> 187
        //   178: astore_2
        //   179: goto +8 -> 187
        //   182: astore_2
        //   183: goto +4 -> 187
        //   186: astore_2
        //   187: aload 8
        //   189: invokevirtual 75	android/os/Parcel:recycle	()V
        //   192: aload_2
        //   193: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	194	0	this	Proxy
        //   0	194	1	paramInt1	int
        //   0	194	2	paramAutofillId	AutofillId
        //   0	194	3	paramRect	Rect
        //   0	194	4	paramAutofillValue	AutofillValue
        //   0	194	5	paramInt2	int
        //   0	194	6	paramInt3	int
        //   0	194	7	paramInt4	int
        //   3	185	8	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   105	162	174	finally
        //   98	105	178	finally
        //   12	18	182	finally
        //   22	35	182	finally
        //   38	44	182	finally
        //   48	61	182	finally
        //   64	70	182	finally
        //   75	89	182	finally
        //   92	98	182	finally
        //   5	12	186	finally
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/IAutoFillManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */