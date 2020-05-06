package android.view.autofill;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.KeyEvent;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.IResultReceiver.Stub;
import java.util.List;

public abstract interface IAutoFillManagerClient
  extends IInterface
{
  public abstract void authenticate(int paramInt1, int paramInt2, IntentSender paramIntentSender, Intent paramIntent)
    throws RemoteException;
  
  public abstract void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
    throws RemoteException;
  
  public abstract void dispatchUnhandledKey(int paramInt, AutofillId paramAutofillId, KeyEvent paramKeyEvent)
    throws RemoteException;
  
  public abstract void getAugmentedAutofillClient(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void notifyNoFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2)
    throws RemoteException;
  
  public abstract void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
    throws RemoteException;
  
  public abstract void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
    throws RemoteException;
  
  public abstract void setSaveUiState(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSessionFinished(int paramInt, List<AutofillId> paramList)
    throws RemoteException;
  
  public abstract void setState(int paramInt)
    throws RemoteException;
  
  public abstract void setTrackedViews(int paramInt, AutofillId[] paramArrayOfAutofillId1, boolean paramBoolean1, boolean paramBoolean2, AutofillId[] paramArrayOfAutofillId2, AutofillId paramAutofillId)
    throws RemoteException;
  
  public abstract void startIntentSender(IntentSender paramIntentSender, Intent paramIntent)
    throws RemoteException;
  
  public static class Default
    implements IAutoFillManagerClient
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void authenticate(int paramInt1, int paramInt2, IntentSender paramIntentSender, Intent paramIntent)
      throws RemoteException
    {}
    
    public void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
      throws RemoteException
    {}
    
    public void dispatchUnhandledKey(int paramInt, AutofillId paramAutofillId, KeyEvent paramKeyEvent)
      throws RemoteException
    {}
    
    public void getAugmentedAutofillClient(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void notifyNoFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2)
      throws RemoteException
    {}
    
    public void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
      throws RemoteException
    {}
    
    public void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
      throws RemoteException
    {}
    
    public void setSaveUiState(int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setSessionFinished(int paramInt, List<AutofillId> paramList)
      throws RemoteException
    {}
    
    public void setState(int paramInt)
      throws RemoteException
    {}
    
    public void setTrackedViews(int paramInt, AutofillId[] paramArrayOfAutofillId1, boolean paramBoolean1, boolean paramBoolean2, AutofillId[] paramArrayOfAutofillId2, AutofillId paramAutofillId)
      throws RemoteException
    {}
    
    public void startIntentSender(IntentSender paramIntentSender, Intent paramIntent)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAutoFillManagerClient
  {
    private static final String DESCRIPTOR = "android.view.autofill.IAutoFillManagerClient";
    static final int TRANSACTION_authenticate = 3;
    static final int TRANSACTION_autofill = 2;
    static final int TRANSACTION_dispatchUnhandledKey = 8;
    static final int TRANSACTION_getAugmentedAutofillClient = 12;
    static final int TRANSACTION_notifyNoFillUi = 7;
    static final int TRANSACTION_requestHideFillUi = 6;
    static final int TRANSACTION_requestShowFillUi = 5;
    static final int TRANSACTION_setSaveUiState = 10;
    static final int TRANSACTION_setSessionFinished = 11;
    static final int TRANSACTION_setState = 1;
    static final int TRANSACTION_setTrackedViews = 4;
    static final int TRANSACTION_startIntentSender = 9;
    
    public Stub()
    {
      attachInterface(this, "android.view.autofill.IAutoFillManagerClient");
    }
    
    public static IAutoFillManagerClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.autofill.IAutoFillManagerClient");
      if ((localIInterface != null) && ((localIInterface instanceof IAutoFillManagerClient))) {
        return (IAutoFillManagerClient)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAutoFillManagerClient getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 12: 
        return "getAugmentedAutofillClient";
      case 11: 
        return "setSessionFinished";
      case 10: 
        return "setSaveUiState";
      case 9: 
        return "startIntentSender";
      case 8: 
        return "dispatchUnhandledKey";
      case 7: 
        return "notifyNoFillUi";
      case 6: 
        return "requestHideFillUi";
      case 5: 
        return "requestShowFillUi";
      case 4: 
        return "setTrackedViews";
      case 3: 
        return "authenticate";
      case 2: 
        return "autofill";
      }
      return "setState";
    }
    
    public static boolean setDefaultImpl(IAutoFillManagerClient paramIAutoFillManagerClient)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAutoFillManagerClient != null))
      {
        Proxy.sDefaultImpl = paramIAutoFillManagerClient;
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
        Object localObject;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 12: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          getAugmentedAutofillClient(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 11: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          setSessionFinished(paramParcel1.readInt(), paramParcel1.createTypedArrayList(AutofillId.CREATOR));
          return true;
        case 10: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          }
          setSaveUiState(paramInt1, bool1);
          return true;
        case 9: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (IntentSender)IntentSender.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          startIntentSender(paramParcel2, paramParcel1);
          return true;
        case 8: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (KeyEvent)KeyEvent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          dispatchUnhandledKey(paramInt1, paramParcel2, paramParcel1);
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          notifyNoFillUi(paramInt1, paramParcel2, paramParcel1.readInt());
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          requestHideFillUi(paramInt1, paramParcel1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          paramInt2 = paramParcel1.readInt();
          int i = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          requestShowFillUi(paramInt1, paramParcel2, paramInt2, i, (Rect)localObject, IAutofillWindowPresenter.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          paramInt1 = paramParcel1.readInt();
          paramParcel2 = (AutofillId[])paramParcel1.createTypedArray(AutofillId.CREATOR);
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          } else {
            bool1 = false;
          }
          boolean bool2;
          if (paramParcel1.readInt() != 0) {
            bool2 = true;
          } else {
            bool2 = false;
          }
          localObject = (AutofillId[])paramParcel1.createTypedArray(AutofillId.CREATOR);
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          setTrackedViews(paramInt1, paramParcel2, bool1, bool2, (AutofillId[])localObject, paramParcel1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          paramInt2 = paramParcel1.readInt();
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (IntentSender)IntentSender.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          authenticate(paramInt2, paramInt1, paramParcel2, paramParcel1);
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
          autofill(paramParcel1.readInt(), paramParcel1.createTypedArrayList(AutofillId.CREATOR), paramParcel1.createTypedArrayList(AutofillValue.CREATOR));
          return true;
        }
        paramParcel1.enforceInterface("android.view.autofill.IAutoFillManagerClient");
        setState(paramParcel1.readInt());
        return true;
      }
      paramParcel2.writeString("android.view.autofill.IAutoFillManagerClient");
      return true;
    }
    
    private static class Proxy
      implements IAutoFillManagerClient
    {
      public static IAutoFillManagerClient sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void authenticate(int paramInt1, int paramInt2, IntentSender paramIntentSender, Intent paramIntent)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          if (paramIntentSender != null)
          {
            localParcel.writeInt(1);
            paramIntentSender.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramIntent != null)
          {
            localParcel.writeInt(1);
            paramIntent.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().authenticate(paramInt1, paramInt2, paramIntentSender, paramIntent);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt);
          localParcel.writeTypedList(paramList);
          localParcel.writeTypedList(paramList1);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().autofill(paramInt, paramList, paramList1);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dispatchUnhandledKey(int paramInt, AutofillId paramAutofillId, KeyEvent paramKeyEvent)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt);
          if (paramAutofillId != null)
          {
            localParcel.writeInt(1);
            paramAutofillId.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramKeyEvent != null)
          {
            localParcel.writeInt(1);
            paramKeyEvent.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(8, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().dispatchUnhandledKey(paramInt, paramAutofillId, paramKeyEvent);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getAugmentedAutofillClient(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(12, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().getAugmentedAutofillClient(paramIResultReceiver);
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
        return "android.view.autofill.IAutoFillManagerClient";
      }
      
      public void notifyNoFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt1);
          if (paramAutofillId != null)
          {
            localParcel.writeInt(1);
            paramAutofillId.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt2);
          if ((!this.mRemote.transact(7, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().notifyNoFillUi(paramInt1, paramAutofillId, paramInt2);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt);
          if (paramAutofillId != null)
          {
            localParcel.writeInt(1);
            paramAutofillId.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(6, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().requestHideFillUi(paramInt, paramAutofillId);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 34	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: aload 7
        //   7: ldc 36
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 7
        //   14: iload_1
        //   15: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   18: aload_2
        //   19: ifnull +19 -> 38
        //   22: aload 7
        //   24: iconst_1
        //   25: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   28: aload_2
        //   29: aload 7
        //   31: iconst_0
        //   32: invokevirtual 84	android/view/autofill/AutofillId:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: goto +9 -> 44
        //   38: aload 7
        //   40: iconst_0
        //   41: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   44: aload 7
        //   46: iload_3
        //   47: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   50: aload 7
        //   52: iload 4
        //   54: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   57: aload 5
        //   59: ifnull +20 -> 79
        //   62: aload 7
        //   64: iconst_1
        //   65: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   68: aload 5
        //   70: aload 7
        //   72: iconst_0
        //   73: invokevirtual 115	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   76: goto +9 -> 85
        //   79: aload 7
        //   81: iconst_0
        //   82: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   85: aload 6
        //   87: ifnull +15 -> 102
        //   90: aload 6
        //   92: invokeinterface 118 1 0
        //   97: astore 8
        //   99: goto +6 -> 105
        //   102: aconst_null
        //   103: astore 8
        //   105: aload 7
        //   107: aload 8
        //   109: invokevirtual 98	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   112: aload_0
        //   113: getfield 21	android/view/autofill/IAutoFillManagerClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   116: iconst_5
        //   117: aload 7
        //   119: aconst_null
        //   120: iconst_1
        //   121: invokeinterface 59 5 0
        //   126: ifne +32 -> 158
        //   129: invokestatic 63	android/view/autofill/IAutoFillManagerClient$Stub:getDefaultImpl	()Landroid/view/autofill/IAutoFillManagerClient;
        //   132: ifnull +26 -> 158
        //   135: invokestatic 63	android/view/autofill/IAutoFillManagerClient$Stub:getDefaultImpl	()Landroid/view/autofill/IAutoFillManagerClient;
        //   138: iload_1
        //   139: aload_2
        //   140: iload_3
        //   141: iload 4
        //   143: aload 5
        //   145: aload 6
        //   147: invokeinterface 120 7 0
        //   152: aload 7
        //   154: invokevirtual 68	android/os/Parcel:recycle	()V
        //   157: return
        //   158: aload 7
        //   160: invokevirtual 68	android/os/Parcel:recycle	()V
        //   163: return
        //   164: astore_2
        //   165: goto +16 -> 181
        //   168: astore_2
        //   169: goto +12 -> 181
        //   172: astore_2
        //   173: goto +8 -> 181
        //   176: astore_2
        //   177: goto +4 -> 181
        //   180: astore_2
        //   181: aload 7
        //   183: invokevirtual 68	android/os/Parcel:recycle	()V
        //   186: aload_2
        //   187: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	188	0	this	Proxy
        //   0	188	1	paramInt1	int
        //   0	188	2	paramAutofillId	AutofillId
        //   0	188	3	paramInt2	int
        //   0	188	4	paramInt3	int
        //   0	188	5	paramRect	Rect
        //   0	188	6	paramIAutofillWindowPresenter	IAutofillWindowPresenter
        //   3	179	7	localParcel	Parcel
        //   97	11	8	localIBinder	IBinder
        // Exception table:
        //   from	to	target	type
        //   112	152	164	finally
        //   50	57	168	finally
        //   62	76	168	finally
        //   79	85	168	finally
        //   90	99	168	finally
        //   105	112	168	finally
        //   44	50	172	finally
        //   12	18	176	finally
        //   22	35	176	finally
        //   38	44	176	finally
        //   5	12	180	finally
      }
      
      public void setSaveUiState(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(10, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().setSaveUiState(paramInt, paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setSessionFinished(int paramInt, List<AutofillId> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt);
          localParcel.writeTypedList(paramList);
          if ((!this.mRemote.transact(11, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().setSessionFinished(paramInt, paramList);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setState(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().setState(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setTrackedViews(int paramInt, AutofillId[] paramArrayOfAutofillId1, boolean paramBoolean1, boolean paramBoolean2, AutofillId[] paramArrayOfAutofillId2, AutofillId paramAutofillId)
        throws RemoteException
      {
        localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          int i;
          throw paramArrayOfAutofillId1;
        }
        finally
        {
          try
          {
            localParcel.writeInt(paramInt);
            try
            {
              localParcel.writeTypedArray(paramArrayOfAutofillId1, 0);
              if (paramBoolean1) {
                i = 1;
              } else {
                i = 0;
              }
              localParcel.writeInt(i);
              if (paramBoolean2) {
                i = 1;
              } else {
                i = 0;
              }
              localParcel.writeInt(i);
              try
              {
                localParcel.writeTypedArray(paramArrayOfAutofillId2, 0);
                if (paramAutofillId != null)
                {
                  localParcel.writeInt(1);
                  paramAutofillId.writeToParcel(localParcel, 0);
                }
                else
                {
                  localParcel.writeInt(0);
                }
                try
                {
                  if ((!this.mRemote.transact(4, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
                  {
                    IAutoFillManagerClient.Stub.getDefaultImpl().setTrackedViews(paramInt, paramArrayOfAutofillId1, paramBoolean1, paramBoolean2, paramArrayOfAutofillId2, paramAutofillId);
                    localParcel.recycle();
                    return;
                  }
                  localParcel.recycle();
                  return;
                }
                finally {}
                paramArrayOfAutofillId1 = finally;
              }
              finally {}
              paramArrayOfAutofillId1 = finally;
            }
            finally {}
            localParcel.recycle();
          }
          finally
          {
            break label171;
            paramArrayOfAutofillId1 = finally;
          }
        }
      }
      
      public void startIntentSender(IntentSender paramIntentSender, Intent paramIntent)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.autofill.IAutoFillManagerClient");
          if (paramIntentSender != null)
          {
            localParcel.writeInt(1);
            paramIntentSender.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramIntent != null)
          {
            localParcel.writeInt(1);
            paramIntent.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(9, localParcel, null, 1)) && (IAutoFillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAutoFillManagerClient.Stub.getDefaultImpl().startIntentSender(paramIntentSender, paramIntent);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/IAutoFillManagerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */