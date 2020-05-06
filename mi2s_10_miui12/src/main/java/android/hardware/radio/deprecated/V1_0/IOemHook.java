package android.hardware.radio.deprecated.V1_0;

import android.internal.hidl.base.V1_0.DebugInfo;
import android.internal.hidl.base.V1_0.IBase;
import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwBlob;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwBinder.DeathRecipient;
import android.os.IHwInterface;
import android.os.NativeHandle;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public abstract interface IOemHook
  extends IBase
{
  public static final String kInterfaceName = "android.hardware.radio.deprecated@1.0::IOemHook";
  
  public static IOemHook asInterface(IHwBinder paramIHwBinder)
  {
    if (paramIHwBinder == null) {
      return null;
    }
    Object localObject = paramIHwBinder.queryLocalInterface("android.hardware.radio.deprecated@1.0::IOemHook");
    if ((localObject != null) && ((localObject instanceof IOemHook))) {
      return (IOemHook)localObject;
    }
    localObject = new Proxy(paramIHwBinder);
    try
    {
      paramIHwBinder = ((IOemHook)localObject).interfaceChain().iterator();
      while (paramIHwBinder.hasNext())
      {
        boolean bool = ((String)paramIHwBinder.next()).equals("android.hardware.radio.deprecated@1.0::IOemHook");
        if (bool) {
          return (IOemHook)localObject;
        }
      }
    }
    catch (RemoteException paramIHwBinder) {}
    return null;
  }
  
  public static IOemHook castFrom(IHwInterface paramIHwInterface)
  {
    if (paramIHwInterface == null) {
      paramIHwInterface = null;
    } else {
      paramIHwInterface = asInterface(paramIHwInterface.asBinder());
    }
    return paramIHwInterface;
  }
  
  public static IOemHook getService()
    throws RemoteException
  {
    return getService("default");
  }
  
  public static IOemHook getService(String paramString)
    throws RemoteException
  {
    return asInterface(HwBinder.getService("android.hardware.radio.deprecated@1.0::IOemHook", paramString));
  }
  
  public static IOemHook getService(String paramString, boolean paramBoolean)
    throws RemoteException
  {
    return asInterface(HwBinder.getService("android.hardware.radio.deprecated@1.0::IOemHook", paramString, paramBoolean));
  }
  
  public static IOemHook getService(boolean paramBoolean)
    throws RemoteException
  {
    return getService("default", paramBoolean);
  }
  
  public abstract IHwBinder asBinder();
  
  public abstract void debug(NativeHandle paramNativeHandle, ArrayList<String> paramArrayList)
    throws RemoteException;
  
  public abstract DebugInfo getDebugInfo()
    throws RemoteException;
  
  public abstract ArrayList<byte[]> getHashChain()
    throws RemoteException;
  
  public abstract ArrayList<String> interfaceChain()
    throws RemoteException;
  
  public abstract String interfaceDescriptor()
    throws RemoteException;
  
  public abstract boolean linkToDeath(IHwBinder.DeathRecipient paramDeathRecipient, long paramLong)
    throws RemoteException;
  
  public abstract void notifySyspropsChanged()
    throws RemoteException;
  
  public abstract void ping()
    throws RemoteException;
  
  public abstract void sendRequestRaw(int paramInt, ArrayList<Byte> paramArrayList)
    throws RemoteException;
  
  public abstract void sendRequestStrings(int paramInt, ArrayList<String> paramArrayList)
    throws RemoteException;
  
  public abstract void setHALInstrumentation()
    throws RemoteException;
  
  public abstract void setResponseFunctions(IOemHookResponse paramIOemHookResponse, IOemHookIndication paramIOemHookIndication)
    throws RemoteException;
  
  public abstract boolean unlinkToDeath(IHwBinder.DeathRecipient paramDeathRecipient)
    throws RemoteException;
  
  public static final class Proxy
    implements IOemHook
  {
    private IHwBinder mRemote;
    
    public Proxy(IHwBinder paramIHwBinder)
    {
      this.mRemote = ((IHwBinder)Objects.requireNonNull(paramIHwBinder));
    }
    
    public IHwBinder asBinder()
    {
      return this.mRemote;
    }
    
    public void debug(NativeHandle paramNativeHandle, ArrayList<String> paramArrayList)
      throws RemoteException
    {
      HwParcel localHwParcel = new HwParcel();
      localHwParcel.writeInterfaceToken("android.hidl.base@1.0::IBase");
      localHwParcel.writeNativeHandle(paramNativeHandle);
      localHwParcel.writeStringVector(paramArrayList);
      paramNativeHandle = new HwParcel();
      try
      {
        this.mRemote.transact(256131655, localHwParcel, paramNativeHandle, 0);
        paramNativeHandle.verifySuccess();
        localHwParcel.releaseTemporaryStorage();
        return;
      }
      finally
      {
        paramNativeHandle.release();
      }
    }
    
    public final boolean equals(Object paramObject)
    {
      return HidlSupport.interfacesEqual(this, paramObject);
    }
    
    public DebugInfo getDebugInfo()
      throws RemoteException
    {
      Object localObject1 = new HwParcel();
      ((HwParcel)localObject1).writeInterfaceToken("android.hidl.base@1.0::IBase");
      HwParcel localHwParcel = new HwParcel();
      try
      {
        this.mRemote.transact(257049926, (HwParcel)localObject1, localHwParcel, 0);
        localHwParcel.verifySuccess();
        ((HwParcel)localObject1).releaseTemporaryStorage();
        localObject1 = new android/internal/hidl/base/V1_0/DebugInfo;
        ((DebugInfo)localObject1).<init>();
        ((DebugInfo)localObject1).readFromParcel(localHwParcel);
        return (DebugInfo)localObject1;
      }
      finally
      {
        localHwParcel.release();
      }
    }
    
    public ArrayList<byte[]> getHashChain()
      throws RemoteException
    {
      Object localObject1 = new HwParcel();
      ((HwParcel)localObject1).writeInterfaceToken("android.hidl.base@1.0::IBase");
      HwParcel localHwParcel = new HwParcel();
      try
      {
        this.mRemote.transact(256398152, (HwParcel)localObject1, localHwParcel, 0);
        localHwParcel.verifySuccess();
        ((HwParcel)localObject1).releaseTemporaryStorage();
        localObject1 = new java/util/ArrayList;
        ((ArrayList)localObject1).<init>();
        Object localObject3 = localHwParcel.readBuffer(16L);
        int i = ((HwBlob)localObject3).getInt32(8L);
        HwBlob localHwBlob = localHwParcel.readEmbeddedBuffer(i * 32, ((HwBlob)localObject3).handle(), 0L, true);
        ((ArrayList)localObject1).clear();
        for (int j = 0; j < i; j++)
        {
          localObject3 = new byte[32];
          localHwBlob.copyToInt8Array(j * 32, (byte[])localObject3, 32);
          ((ArrayList)localObject1).add(localObject3);
        }
        return (ArrayList<byte[]>)localObject1;
      }
      finally
      {
        localHwParcel.release();
      }
    }
    
    public final int hashCode()
    {
      return asBinder().hashCode();
    }
    
    public ArrayList<String> interfaceChain()
      throws RemoteException
    {
      Object localObject1 = new HwParcel();
      ((HwParcel)localObject1).writeInterfaceToken("android.hidl.base@1.0::IBase");
      HwParcel localHwParcel = new HwParcel();
      try
      {
        this.mRemote.transact(256067662, (HwParcel)localObject1, localHwParcel, 0);
        localHwParcel.verifySuccess();
        ((HwParcel)localObject1).releaseTemporaryStorage();
        localObject1 = localHwParcel.readStringVector();
        return (ArrayList<String>)localObject1;
      }
      finally
      {
        localHwParcel.release();
      }
    }
    
    public String interfaceDescriptor()
      throws RemoteException
    {
      Object localObject1 = new HwParcel();
      ((HwParcel)localObject1).writeInterfaceToken("android.hidl.base@1.0::IBase");
      HwParcel localHwParcel = new HwParcel();
      try
      {
        this.mRemote.transact(256136003, (HwParcel)localObject1, localHwParcel, 0);
        localHwParcel.verifySuccess();
        ((HwParcel)localObject1).releaseTemporaryStorage();
        localObject1 = localHwParcel.readString();
        return (String)localObject1;
      }
      finally
      {
        localHwParcel.release();
      }
    }
    
    public boolean linkToDeath(IHwBinder.DeathRecipient paramDeathRecipient, long paramLong)
      throws RemoteException
    {
      return this.mRemote.linkToDeath(paramDeathRecipient, paramLong);
    }
    
    public void notifySyspropsChanged()
      throws RemoteException
    {
      HwParcel localHwParcel1 = new HwParcel();
      localHwParcel1.writeInterfaceToken("android.hidl.base@1.0::IBase");
      HwParcel localHwParcel2 = new HwParcel();
      try
      {
        this.mRemote.transact(257120595, localHwParcel1, localHwParcel2, 1);
        localHwParcel1.releaseTemporaryStorage();
        return;
      }
      finally
      {
        localHwParcel2.release();
      }
    }
    
    public void ping()
      throws RemoteException
    {
      HwParcel localHwParcel1 = new HwParcel();
      localHwParcel1.writeInterfaceToken("android.hidl.base@1.0::IBase");
      HwParcel localHwParcel2 = new HwParcel();
      try
      {
        this.mRemote.transact(256921159, localHwParcel1, localHwParcel2, 0);
        localHwParcel2.verifySuccess();
        localHwParcel1.releaseTemporaryStorage();
        return;
      }
      finally
      {
        localHwParcel2.release();
      }
    }
    
    public void sendRequestRaw(int paramInt, ArrayList<Byte> paramArrayList)
      throws RemoteException
    {
      HwParcel localHwParcel = new HwParcel();
      localHwParcel.writeInterfaceToken("android.hardware.radio.deprecated@1.0::IOemHook");
      localHwParcel.writeInt32(paramInt);
      localHwParcel.writeInt8Vector(paramArrayList);
      paramArrayList = new HwParcel();
      try
      {
        this.mRemote.transact(2, localHwParcel, paramArrayList, 1);
        localHwParcel.releaseTemporaryStorage();
        return;
      }
      finally
      {
        paramArrayList.release();
      }
    }
    
    public void sendRequestStrings(int paramInt, ArrayList<String> paramArrayList)
      throws RemoteException
    {
      HwParcel localHwParcel = new HwParcel();
      localHwParcel.writeInterfaceToken("android.hardware.radio.deprecated@1.0::IOemHook");
      localHwParcel.writeInt32(paramInt);
      localHwParcel.writeStringVector(paramArrayList);
      paramArrayList = new HwParcel();
      try
      {
        this.mRemote.transact(3, localHwParcel, paramArrayList, 1);
        localHwParcel.releaseTemporaryStorage();
        return;
      }
      finally
      {
        paramArrayList.release();
      }
    }
    
    public void setHALInstrumentation()
      throws RemoteException
    {
      HwParcel localHwParcel1 = new HwParcel();
      localHwParcel1.writeInterfaceToken("android.hidl.base@1.0::IBase");
      HwParcel localHwParcel2 = new HwParcel();
      try
      {
        this.mRemote.transact(256462420, localHwParcel1, localHwParcel2, 1);
        localHwParcel1.releaseTemporaryStorage();
        return;
      }
      finally
      {
        localHwParcel2.release();
      }
    }
    
    public void setResponseFunctions(IOemHookResponse paramIOemHookResponse, IOemHookIndication paramIOemHookIndication)
      throws RemoteException
    {
      HwParcel localHwParcel = new HwParcel();
      localHwParcel.writeInterfaceToken("android.hardware.radio.deprecated@1.0::IOemHook");
      Object localObject = null;
      if (paramIOemHookResponse == null) {
        paramIOemHookResponse = null;
      } else {
        paramIOemHookResponse = paramIOemHookResponse.asBinder();
      }
      localHwParcel.writeStrongBinder(paramIOemHookResponse);
      if (paramIOemHookIndication == null) {
        paramIOemHookResponse = (IOemHookResponse)localObject;
      } else {
        paramIOemHookResponse = paramIOemHookIndication.asBinder();
      }
      localHwParcel.writeStrongBinder(paramIOemHookResponse);
      paramIOemHookResponse = new HwParcel();
      try
      {
        this.mRemote.transact(1, localHwParcel, paramIOemHookResponse, 0);
        paramIOemHookResponse.verifySuccess();
        localHwParcel.releaseTemporaryStorage();
        return;
      }
      finally
      {
        paramIOemHookResponse.release();
      }
    }
    
    public String toString()
    {
      try
      {
        Object localObject = new java/lang/StringBuilder;
        ((StringBuilder)localObject).<init>();
        ((StringBuilder)localObject).append(interfaceDescriptor());
        ((StringBuilder)localObject).append("@Proxy");
        localObject = ((StringBuilder)localObject).toString();
        return (String)localObject;
      }
      catch (RemoteException localRemoteException) {}
      return "[class or subclass of android.hardware.radio.deprecated@1.0::IOemHook]@Proxy";
    }
    
    public boolean unlinkToDeath(IHwBinder.DeathRecipient paramDeathRecipient)
      throws RemoteException
    {
      return this.mRemote.unlinkToDeath(paramDeathRecipient);
    }
  }
  
  public static abstract class Stub
    extends HwBinder
    implements IOemHook
  {
    public IHwBinder asBinder()
    {
      return this;
    }
    
    public void debug(NativeHandle paramNativeHandle, ArrayList<String> paramArrayList) {}
    
    public final DebugInfo getDebugInfo()
    {
      DebugInfo localDebugInfo = new DebugInfo();
      localDebugInfo.pid = HidlSupport.getPidIfSharable();
      localDebugInfo.ptr = 0L;
      localDebugInfo.arch = 0;
      return localDebugInfo;
    }
    
    public final ArrayList<byte[]> getHashChain()
    {
      return new ArrayList(Arrays.asList(new byte[][] { { 0, -9, 0, -123, -42, -6, -31, -44, -126, -5, 112, 10, 63, -44, 46, -44, 117, 56, 76, -107, -75, 28, -110, 105, -71, -82, 80, 55, -73, 74, -44, -35 }, { -20, 127, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76 } }));
    }
    
    public final ArrayList<String> interfaceChain()
    {
      return new ArrayList(Arrays.asList(new String[] { "android.hardware.radio.deprecated@1.0::IOemHook", "android.hidl.base@1.0::IBase" }));
    }
    
    public final String interfaceDescriptor()
    {
      return "android.hardware.radio.deprecated@1.0::IOemHook";
    }
    
    public final boolean linkToDeath(IHwBinder.DeathRecipient paramDeathRecipient, long paramLong)
    {
      return true;
    }
    
    public final void notifySyspropsChanged() {}
    
    public void onTransact(int paramInt1, HwParcel paramHwParcel1, HwParcel paramHwParcel2, int paramInt2)
      throws RemoteException
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      int i2 = 1;
      int i3 = 1;
      int i4 = 1;
      int i5 = 1;
      int i6 = 1;
      int i7 = 1;
      int i8 = 1;
      if (paramInt1 != 1)
      {
        if (paramInt1 != 2)
        {
          if (paramInt1 != 3)
          {
            switch (paramInt1)
            {
            default: 
              break;
            case 257250372: 
              paramInt1 = i1;
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = 1;
              }
              if (paramInt1 == 0) {
                break;
              }
              paramHwParcel2.writeStatus(Integer.MIN_VALUE);
              paramHwParcel2.send();
              break;
            case 257120595: 
              paramInt1 = i;
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = 1;
              }
              if (paramInt1 != 1)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                notifySyspropsChanged();
              }
              break;
            case 257049926: 
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = i8;
              } else {
                paramInt1 = 0;
              }
              if (paramInt1 != 0)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                paramHwParcel1 = getDebugInfo();
                paramHwParcel2.writeStatus(0);
                paramHwParcel1.writeToParcel(paramHwParcel2);
                paramHwParcel2.send();
              }
              break;
            case 256921159: 
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = i2;
              } else {
                paramInt1 = 0;
              }
              if (paramInt1 != 0)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                ping();
                paramHwParcel2.writeStatus(0);
                paramHwParcel2.send();
              }
              break;
            case 256660548: 
              paramInt1 = j;
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = 1;
              }
              if (paramInt1 == 0) {
                break;
              }
              paramHwParcel2.writeStatus(Integer.MIN_VALUE);
              paramHwParcel2.send();
              break;
            case 256462420: 
              paramInt1 = k;
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = 1;
              }
              if (paramInt1 != 1)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                setHALInstrumentation();
              }
              break;
            case 256398152: 
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = i3;
              } else {
                paramInt1 = 0;
              }
              if (paramInt1 != 0)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                ArrayList localArrayList = getHashChain();
                paramHwParcel2.writeStatus(0);
                paramHwParcel1 = new HwBlob(16);
                paramInt2 = localArrayList.size();
                paramHwParcel1.putInt32(8L, paramInt2);
                paramHwParcel1.putBool(12L, false);
                HwBlob localHwBlob = new HwBlob(paramInt2 * 32);
                paramInt1 = 0;
                while (paramInt1 < paramInt2)
                {
                  long l = paramInt1 * 32;
                  byte[] arrayOfByte = (byte[])localArrayList.get(paramInt1);
                  if ((arrayOfByte != null) && (arrayOfByte.length == 32))
                  {
                    localHwBlob.putInt8Array(l, arrayOfByte);
                    paramInt1++;
                  }
                  else
                  {
                    throw new IllegalArgumentException("Array element is not of the expected length");
                  }
                }
                paramHwParcel1.putBlob(0L, localHwBlob);
                paramHwParcel2.writeBuffer(paramHwParcel1);
                paramHwParcel2.send();
              }
              break;
            case 256136003: 
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = i4;
              } else {
                paramInt1 = 0;
              }
              if (paramInt1 != 0)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                paramHwParcel1 = interfaceDescriptor();
                paramHwParcel2.writeStatus(0);
                paramHwParcel2.writeString(paramHwParcel1);
                paramHwParcel2.send();
              }
              break;
            case 256131655: 
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = i5;
              } else {
                paramInt1 = 0;
              }
              if (paramInt1 != 0)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                debug(paramHwParcel1.readNativeHandle(), paramHwParcel1.readStringVector());
                paramHwParcel2.writeStatus(0);
                paramHwParcel2.send();
              }
              break;
            case 256067662: 
              if ((paramInt2 & 0x1) != 0) {
                paramInt1 = i6;
              } else {
                paramInt1 = 0;
              }
              if (paramInt1 != 0)
              {
                paramHwParcel2.writeStatus(Integer.MIN_VALUE);
                paramHwParcel2.send();
              }
              else
              {
                paramHwParcel1.enforceInterface("android.hidl.base@1.0::IBase");
                paramHwParcel1 = interfaceChain();
                paramHwParcel2.writeStatus(0);
                paramHwParcel2.writeStringVector(paramHwParcel1);
                paramHwParcel2.send();
              }
              break;
            }
          }
          else
          {
            paramInt1 = m;
            if ((paramInt2 & 0x1) != 0) {
              paramInt1 = 1;
            }
            if (paramInt1 != 1)
            {
              paramHwParcel2.writeStatus(Integer.MIN_VALUE);
              paramHwParcel2.send();
            }
            else
            {
              paramHwParcel1.enforceInterface("android.hardware.radio.deprecated@1.0::IOemHook");
              sendRequestStrings(paramHwParcel1.readInt32(), paramHwParcel1.readStringVector());
            }
          }
        }
        else
        {
          paramInt1 = n;
          if ((paramInt2 & 0x1) != 0) {
            paramInt1 = 1;
          }
          if (paramInt1 != 1)
          {
            paramHwParcel2.writeStatus(Integer.MIN_VALUE);
            paramHwParcel2.send();
          }
          else
          {
            paramHwParcel1.enforceInterface("android.hardware.radio.deprecated@1.0::IOemHook");
            sendRequestRaw(paramHwParcel1.readInt32(), paramHwParcel1.readInt8Vector());
          }
        }
      }
      else
      {
        if ((paramInt2 & 0x1) != 0) {
          paramInt1 = i7;
        } else {
          paramInt1 = 0;
        }
        if (paramInt1 != 0)
        {
          paramHwParcel2.writeStatus(Integer.MIN_VALUE);
          paramHwParcel2.send();
        }
        else
        {
          paramHwParcel1.enforceInterface("android.hardware.radio.deprecated@1.0::IOemHook");
          setResponseFunctions(IOemHookResponse.asInterface(paramHwParcel1.readStrongBinder()), IOemHookIndication.asInterface(paramHwParcel1.readStrongBinder()));
          paramHwParcel2.writeStatus(0);
          paramHwParcel2.send();
        }
      }
    }
    
    public final void ping() {}
    
    public IHwInterface queryLocalInterface(String paramString)
    {
      if ("android.hardware.radio.deprecated@1.0::IOemHook".equals(paramString)) {
        return this;
      }
      return null;
    }
    
    public void registerAsService(String paramString)
      throws RemoteException
    {
      registerService(paramString);
    }
    
    public final void setHALInstrumentation() {}
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(interfaceDescriptor());
      localStringBuilder.append("@Stub");
      return localStringBuilder.toString();
    }
    
    public final boolean unlinkToDeath(IHwBinder.DeathRecipient paramDeathRecipient)
    {
      return true;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/hardware/radio/deprecated/V1_0/IOemHook.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */