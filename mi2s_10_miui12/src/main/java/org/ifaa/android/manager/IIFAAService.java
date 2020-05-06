package org.ifaa.android.manager;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IIFAAService
  extends IInterface
{
  public abstract int faceAuthenticate_v2(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int faceCancel_v2(String paramString)
    throws RemoteException;
  
  public abstract int faceEnroll(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int faceGetCellinfo()
    throws RemoteException;
  
  public abstract byte[] faceInvokeCommand(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract int faceUpgrade(int paramInt1, String paramString, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    throws RemoteException;
  
  public abstract int[] getIDList(int paramInt)
    throws RemoteException;
  
  public abstract byte[] processCmd_v2(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public static class Default
    implements IIFAAService
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public int faceAuthenticate_v2(String paramString, int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public int faceCancel_v2(String paramString)
      throws RemoteException
    {
      return 0;
    }
    
    public int faceEnroll(String paramString, int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public int faceGetCellinfo()
      throws RemoteException
    {
      return 0;
    }
    
    public byte[] faceInvokeCommand(byte[] paramArrayOfByte)
      throws RemoteException
    {
      return null;
    }
    
    public int faceUpgrade(int paramInt1, String paramString, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
      throws RemoteException
    {
      return 0;
    }
    
    public int[] getIDList(int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public byte[] processCmd_v2(byte[] paramArrayOfByte)
      throws RemoteException
    {
      return null;
    }
  }
  
  public static abstract class Stub
    extends Binder
    implements IIFAAService
  {
    private static final String DESCRIPTOR = "org.ifaa.android.manager.IIFAAService";
    static final int TRANSACTION_faceAuthenticate_v2 = 5;
    static final int TRANSACTION_faceCancel_v2 = 6;
    static final int TRANSACTION_faceEnroll = 3;
    static final int TRANSACTION_faceGetCellinfo = 8;
    static final int TRANSACTION_faceInvokeCommand = 7;
    static final int TRANSACTION_faceUpgrade = 4;
    static final int TRANSACTION_getIDList = 2;
    static final int TRANSACTION_processCmd_v2 = 1;
    
    public Stub()
    {
      attachInterface(this, "org.ifaa.android.manager.IIFAAService");
    }
    
    public static IIFAAService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("org.ifaa.android.manager.IIFAAService");
      if ((localIInterface != null) && ((localIInterface instanceof IIFAAService))) {
        return (IIFAAService)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IIFAAService getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 8: 
        return "faceGetCellinfo";
      case 7: 
        return "faceInvokeCommand";
      case 6: 
        return "faceCancel_v2";
      case 5: 
        return "faceAuthenticate_v2";
      case 4: 
        return "faceUpgrade";
      case 3: 
        return "faceEnroll";
      case 2: 
        return "getIDList";
      }
      return "processCmd_v2";
    }
    
    public static boolean setDefaultImpl(IIFAAService paramIIFAAService)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIIFAAService != null))
      {
        Proxy.sDefaultImpl = paramIIFAAService;
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
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 8: 
          paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
          paramInt1 = faceGetCellinfo();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 7: 
          paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
          paramParcel1 = faceInvokeCommand(paramParcel1.createByteArray());
          paramParcel2.writeNoException();
          paramParcel2.writeByteArray(paramParcel1);
          return true;
        case 6: 
          paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
          paramInt1 = faceCancel_v2(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
          paramInt1 = faceAuthenticate_v2(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 4: 
          paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
          paramInt1 = faceUpgrade(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
          paramInt1 = faceEnroll(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 2: 
          paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
          paramParcel1 = getIDList(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeIntArray(paramParcel1);
          return true;
        }
        paramParcel1.enforceInterface("org.ifaa.android.manager.IIFAAService");
        paramParcel1 = processCmd_v2(paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      }
      paramParcel2.writeString("org.ifaa.android.manager.IIFAAService");
      return true;
    }
    
    private static class Proxy
      implements IIFAAService
    {
      public static IIFAAService sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public int faceAuthenticate_v2(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(5, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            paramInt = IIFAAService.Stub.getDefaultImpl().faceAuthenticate_v2(paramString, paramInt);
            return paramInt;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int faceCancel_v2(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(6, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            i = IIFAAService.Stub.getDefaultImpl().faceCancel_v2(paramString);
            return i;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int faceEnroll(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            paramInt = IIFAAService.Stub.getDefaultImpl().faceEnroll(paramString, paramInt);
            return paramInt;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int faceGetCellinfo()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          if ((!this.mRemote.transact(8, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            i = IIFAAService.Stub.getDefaultImpl().faceGetCellinfo();
            return i;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public byte[] faceInvokeCommand(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          localParcel1.writeByteArray(paramArrayOfByte);
          if ((!this.mRemote.transact(7, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            paramArrayOfByte = IIFAAService.Stub.getDefaultImpl().faceInvokeCommand(paramArrayOfByte);
            return paramArrayOfByte;
          }
          localParcel2.readException();
          paramArrayOfByte = localParcel2.createByteArray();
          return paramArrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int faceUpgrade(int paramInt1, String paramString, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt3);
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            paramInt1 = IIFAAService.Stub.getDefaultImpl().faceUpgrade(paramInt1, paramString, paramInt2, paramArrayOfByte, paramInt3);
            return paramInt1;
          }
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getIDList(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            arrayOfInt = IIFAAService.Stub.getDefaultImpl().getIDList(paramInt);
            return arrayOfInt;
          }
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "org.ifaa.android.manager.IIFAAService";
      }
      
      public byte[] processCmd_v2(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("org.ifaa.android.manager.IIFAAService");
          localParcel1.writeByteArray(paramArrayOfByte);
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IIFAAService.Stub.getDefaultImpl() != null))
          {
            paramArrayOfByte = IIFAAService.Stub.getDefaultImpl().processCmd_v2(paramArrayOfByte);
            return paramArrayOfByte;
          }
          localParcel2.readException();
          paramArrayOfByte = localParcel2.createByteArray();
          return paramArrayOfByte;
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/IIFAAService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */