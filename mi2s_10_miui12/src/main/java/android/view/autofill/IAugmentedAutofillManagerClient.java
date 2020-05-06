package android.view.autofill;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface IAugmentedAutofillManagerClient
  extends IInterface
{
  public abstract void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
    throws RemoteException;
  
  public abstract Rect getViewCoordinates(AutofillId paramAutofillId)
    throws RemoteException;
  
  public abstract void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
    throws RemoteException;
  
  public abstract void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
    throws RemoteException;
  
  public static class Default
    implements IAugmentedAutofillManagerClient
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
      throws RemoteException
    {}
    
    public Rect getViewCoordinates(AutofillId paramAutofillId)
      throws RemoteException
    {
      return null;
    }
    
    public void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
      throws RemoteException
    {}
    
    public void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAugmentedAutofillManagerClient
  {
    private static final String DESCRIPTOR = "android.view.autofill.IAugmentedAutofillManagerClient";
    static final int TRANSACTION_autofill = 2;
    static final int TRANSACTION_getViewCoordinates = 1;
    static final int TRANSACTION_requestHideFillUi = 4;
    static final int TRANSACTION_requestShowFillUi = 3;
    
    public Stub()
    {
      attachInterface(this, "android.view.autofill.IAugmentedAutofillManagerClient");
    }
    
    public static IAugmentedAutofillManagerClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.autofill.IAugmentedAutofillManagerClient");
      if ((localIInterface != null) && ((localIInterface instanceof IAugmentedAutofillManagerClient))) {
        return (IAugmentedAutofillManagerClient)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAugmentedAutofillManagerClient getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3)
          {
            if (paramInt != 4) {
              return null;
            }
            return "requestHideFillUi";
          }
          return "requestShowFillUi";
        }
        return "autofill";
      }
      return "getViewCoordinates";
    }
    
    public static boolean setDefaultImpl(IAugmentedAutofillManagerClient paramIAugmentedAutofillManagerClient)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAugmentedAutofillManagerClient != null))
      {
        Proxy.sDefaultImpl = paramIAugmentedAutofillManagerClient;
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
          if (paramInt1 != 3)
          {
            if (paramInt1 != 4)
            {
              if (paramInt1 != 1598968902) {
                return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
              }
              paramParcel2.writeString("android.view.autofill.IAugmentedAutofillManagerClient");
              return true;
            }
            paramParcel1.enforceInterface("android.view.autofill.IAugmentedAutofillManagerClient");
            paramInt1 = paramParcel1.readInt();
            if (paramParcel1.readInt() != 0) {
              paramParcel1 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
            } else {
              paramParcel1 = null;
            }
            requestHideFillUi(paramInt1, paramParcel1);
            paramParcel2.writeNoException();
            return true;
          }
          paramParcel1.enforceInterface("android.view.autofill.IAugmentedAutofillManagerClient");
          paramInt1 = paramParcel1.readInt();
          AutofillId localAutofillId;
          if (paramParcel1.readInt() != 0) {
            localAutofillId = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
          } else {
            localAutofillId = null;
          }
          paramInt2 = paramParcel1.readInt();
          int i = paramParcel1.readInt();
          Rect localRect;
          if (paramParcel1.readInt() != 0) {
            localRect = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localRect = null;
          }
          requestShowFillUi(paramInt1, localAutofillId, paramInt2, i, localRect, IAutofillWindowPresenter.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
        paramParcel1.enforceInterface("android.view.autofill.IAugmentedAutofillManagerClient");
        autofill(paramParcel1.readInt(), paramParcel1.createTypedArrayList(AutofillId.CREATOR), paramParcel1.createTypedArrayList(AutofillValue.CREATOR));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.view.autofill.IAugmentedAutofillManagerClient");
      if (paramParcel1.readInt() != 0) {
        paramParcel1 = (AutofillId)AutofillId.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel1 = null;
      }
      paramParcel1 = getViewCoordinates(paramParcel1);
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
    
    private static class Proxy
      implements IAugmentedAutofillManagerClient
    {
      public static IAugmentedAutofillManagerClient sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.autofill.IAugmentedAutofillManagerClient");
          localParcel1.writeInt(paramInt);
          localParcel1.writeTypedList(paramList);
          localParcel1.writeTypedList(paramList1);
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IAugmentedAutofillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAugmentedAutofillManagerClient.Stub.getDefaultImpl().autofill(paramInt, paramList, paramList1);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.view.autofill.IAugmentedAutofillManagerClient";
      }
      
      public Rect getViewCoordinates(AutofillId paramAutofillId)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.autofill.IAugmentedAutofillManagerClient");
          if (paramAutofillId != null)
          {
            localParcel1.writeInt(1);
            paramAutofillId.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IAugmentedAutofillManagerClient.Stub.getDefaultImpl() != null))
          {
            paramAutofillId = IAugmentedAutofillManagerClient.Stub.getDefaultImpl().getViewCoordinates(paramAutofillId);
            return paramAutofillId;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramAutofillId = (Rect)Rect.CREATOR.createFromParcel(localParcel2);
          } else {
            paramAutofillId = null;
          }
          return paramAutofillId;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.autofill.IAugmentedAutofillManagerClient");
          localParcel1.writeInt(paramInt);
          if (paramAutofillId != null)
          {
            localParcel1.writeInt(1);
            paramAutofillId.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IAugmentedAutofillManagerClient.Stub.getDefaultImpl() != null))
          {
            IAugmentedAutofillManagerClient.Stub.getDefaultImpl().requestHideFillUi(paramInt, paramAutofillId);
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
      
      /* Error */
      public void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 34	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 34	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 36
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: iload_1
        //   20: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   23: aload_2
        //   24: ifnull +19 -> 43
        //   27: aload 7
        //   29: iconst_1
        //   30: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 7
        //   36: iconst_0
        //   37: invokevirtual 79	android/view/autofill/AutofillId:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: goto +9 -> 49
        //   43: aload 7
        //   45: iconst_0
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: aload 7
        //   51: iload_3
        //   52: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   55: aload 7
        //   57: iload 4
        //   59: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   62: aload 5
        //   64: ifnull +20 -> 84
        //   67: aload 7
        //   69: iconst_1
        //   70: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   73: aload 5
        //   75: aload 7
        //   77: iconst_0
        //   78: invokevirtual 104	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   81: goto +9 -> 90
        //   84: aload 7
        //   86: iconst_0
        //   87: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   90: aload 6
        //   92: ifnull +15 -> 107
        //   95: aload 6
        //   97: invokeinterface 108 1 0
        //   102: astore 9
        //   104: goto +6 -> 110
        //   107: aconst_null
        //   108: astore 9
        //   110: aload 7
        //   112: aload 9
        //   114: invokevirtual 111	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   117: aload_0
        //   118: getfield 21	android/view/autofill/IAugmentedAutofillManagerClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   121: iconst_3
        //   122: aload 7
        //   124: aload 8
        //   126: iconst_0
        //   127: invokeinterface 54 5 0
        //   132: ifne +37 -> 169
        //   135: invokestatic 58	android/view/autofill/IAugmentedAutofillManagerClient$Stub:getDefaultImpl	()Landroid/view/autofill/IAugmentedAutofillManagerClient;
        //   138: ifnull +31 -> 169
        //   141: invokestatic 58	android/view/autofill/IAugmentedAutofillManagerClient$Stub:getDefaultImpl	()Landroid/view/autofill/IAugmentedAutofillManagerClient;
        //   144: iload_1
        //   145: aload_2
        //   146: iload_3
        //   147: iload 4
        //   149: aload 5
        //   151: aload 6
        //   153: invokeinterface 113 7 0
        //   158: aload 8
        //   160: invokevirtual 63	android/os/Parcel:recycle	()V
        //   163: aload 7
        //   165: invokevirtual 63	android/os/Parcel:recycle	()V
        //   168: return
        //   169: aload 8
        //   171: invokevirtual 66	android/os/Parcel:readException	()V
        //   174: aload 8
        //   176: invokevirtual 63	android/os/Parcel:recycle	()V
        //   179: aload 7
        //   181: invokevirtual 63	android/os/Parcel:recycle	()V
        //   184: return
        //   185: astore_2
        //   186: goto +16 -> 202
        //   189: astore_2
        //   190: goto +12 -> 202
        //   193: astore_2
        //   194: goto +8 -> 202
        //   197: astore_2
        //   198: goto +4 -> 202
        //   201: astore_2
        //   202: aload 8
        //   204: invokevirtual 63	android/os/Parcel:recycle	()V
        //   207: aload 7
        //   209: invokevirtual 63	android/os/Parcel:recycle	()V
        //   212: aload_2
        //   213: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	214	0	this	Proxy
        //   0	214	1	paramInt1	int
        //   0	214	2	paramAutofillId	AutofillId
        //   0	214	3	paramInt2	int
        //   0	214	4	paramInt3	int
        //   0	214	5	paramRect	Rect
        //   0	214	6	paramIAutofillWindowPresenter	IAutofillWindowPresenter
        //   3	205	7	localParcel1	Parcel
        //   8	195	8	localParcel2	Parcel
        //   102	11	9	localIBinder	IBinder
        // Exception table:
        //   from	to	target	type
        //   117	158	185	finally
        //   169	174	185	finally
        //   55	62	189	finally
        //   67	81	189	finally
        //   84	90	189	finally
        //   95	104	189	finally
        //   110	117	189	finally
        //   49	55	193	finally
        //   17	23	197	finally
        //   27	40	197	finally
        //   43	49	197	finally
        //   10	17	201	finally
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/IAugmentedAutofillManagerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */