package miui.telephony;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;
import java.util.List;

public abstract interface IMiuiTelephony
  extends IInterface
{
  public abstract Bundle getCellLocationForSlot(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract String getDeviceId(String paramString)
    throws RemoteException;
  
  public abstract List<String> getDeviceIdList(String paramString)
    throws RemoteException;
  
  public abstract String getImei(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract List<String> getImeiList(String paramString)
    throws RemoteException;
  
  public abstract String getMeid(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract List<String> getMeidList(String paramString)
    throws RemoteException;
  
  public abstract String getSmallDeviceId(String paramString)
    throws RemoteException;
  
  public abstract String getSpn(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getSystemDefaultSlotId()
    throws RemoteException;
  
  public abstract boolean isFiveGCapable()
    throws RemoteException;
  
  public abstract boolean isGwsdSupport()
    throws RemoteException;
  
  public abstract boolean isIccCardActivate(int paramInt)
    throws RemoteException;
  
  public abstract boolean isImsRegistered(int paramInt)
    throws RemoteException;
  
  public abstract boolean isSameOperator(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract boolean isUserFiveGEnabled()
    throws RemoteException;
  
  public abstract boolean isVideoTelephonyAvailable(int paramInt)
    throws RemoteException;
  
  public abstract boolean isVolteEnabledByPlatform()
    throws RemoteException;
  
  public abstract boolean isVolteEnabledByPlatformForSlot(int paramInt)
    throws RemoteException;
  
  public abstract boolean isVolteEnabledByUser()
    throws RemoteException;
  
  public abstract boolean isVolteEnabledByUserForSlot(int paramInt)
    throws RemoteException;
  
  public abstract boolean isVtEnabledByPlatform()
    throws RemoteException;
  
  public abstract boolean isVtEnabledByPlatformForSlot(int paramInt)
    throws RemoteException;
  
  public abstract boolean isWifiCallingAvailable(int paramInt)
    throws RemoteException;
  
  public abstract String onOperatorNumericOrNameSet(int paramInt, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setCallForwardingOption(int paramInt1, int paramInt2, int paramInt3, String paramString, ResultReceiver paramResultReceiver)
    throws RemoteException;
  
  public abstract boolean setDefaultDataSlotId(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setDefaultVoiceSlotId(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setIccCardActivate(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUserFiveGEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public static class Default
    implements IMiuiTelephony
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public Bundle getCellLocationForSlot(int paramInt, String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public String getDeviceId(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public List<String> getDeviceIdList(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public String getImei(int paramInt, String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public List<String> getImeiList(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public String getMeid(int paramInt, String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public List<String> getMeidList(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public String getSmallDeviceId(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public String getSpn(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
      throws RemoteException
    {
      return null;
    }
    
    public int getSystemDefaultSlotId()
      throws RemoteException
    {
      return 0;
    }
    
    public boolean isFiveGCapable()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isGwsdSupport()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isIccCardActivate(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isImsRegistered(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isSameOperator(String paramString1, String paramString2)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isUserFiveGEnabled()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isVideoTelephonyAvailable(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isVolteEnabledByPlatform()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isVolteEnabledByPlatformForSlot(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isVolteEnabledByUser()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isVolteEnabledByUserForSlot(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isVtEnabledByPlatform()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isVtEnabledByPlatformForSlot(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isWifiCallingAvailable(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public String onOperatorNumericOrNameSet(int paramInt, String paramString1, String paramString2)
      throws RemoteException
    {
      return null;
    }
    
    public void setCallForwardingOption(int paramInt1, int paramInt2, int paramInt3, String paramString, ResultReceiver paramResultReceiver)
      throws RemoteException
    {}
    
    public boolean setDefaultDataSlotId(int paramInt, String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public void setDefaultVoiceSlotId(int paramInt, String paramString)
      throws RemoteException
    {}
    
    public void setIccCardActivate(int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setUserFiveGEnabled(boolean paramBoolean)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IMiuiTelephony
  {
    private static final String DESCRIPTOR = "miui.telephony.IMiuiTelephony";
    static final int TRANSACTION_getCellLocationForSlot = 26;
    static final int TRANSACTION_getDeviceId = 19;
    static final int TRANSACTION_getDeviceIdList = 16;
    static final int TRANSACTION_getImei = 20;
    static final int TRANSACTION_getImeiList = 17;
    static final int TRANSACTION_getMeid = 21;
    static final int TRANSACTION_getMeidList = 18;
    static final int TRANSACTION_getSmallDeviceId = 22;
    static final int TRANSACTION_getSpn = 24;
    static final int TRANSACTION_getSystemDefaultSlotId = 12;
    static final int TRANSACTION_isFiveGCapable = 30;
    static final int TRANSACTION_isGwsdSupport = 13;
    static final int TRANSACTION_isIccCardActivate = 14;
    static final int TRANSACTION_isImsRegistered = 3;
    static final int TRANSACTION_isSameOperator = 23;
    static final int TRANSACTION_isUserFiveGEnabled = 29;
    static final int TRANSACTION_isVideoTelephonyAvailable = 4;
    static final int TRANSACTION_isVolteEnabledByPlatform = 10;
    static final int TRANSACTION_isVolteEnabledByPlatformForSlot = 11;
    static final int TRANSACTION_isVolteEnabledByUser = 6;
    static final int TRANSACTION_isVolteEnabledByUserForSlot = 7;
    static final int TRANSACTION_isVtEnabledByPlatform = 8;
    static final int TRANSACTION_isVtEnabledByPlatformForSlot = 9;
    static final int TRANSACTION_isWifiCallingAvailable = 5;
    static final int TRANSACTION_onOperatorNumericOrNameSet = 25;
    static final int TRANSACTION_setCallForwardingOption = 27;
    static final int TRANSACTION_setDefaultDataSlotId = 2;
    static final int TRANSACTION_setDefaultVoiceSlotId = 1;
    static final int TRANSACTION_setIccCardActivate = 15;
    static final int TRANSACTION_setUserFiveGEnabled = 28;
    
    public Stub()
    {
      attachInterface(this, "miui.telephony.IMiuiTelephony");
    }
    
    public static IMiuiTelephony asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.telephony.IMiuiTelephony");
      if ((localIInterface != null) && ((localIInterface instanceof IMiuiTelephony))) {
        return (IMiuiTelephony)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IMiuiTelephony getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 30: 
        return "isFiveGCapable";
      case 29: 
        return "isUserFiveGEnabled";
      case 28: 
        return "setUserFiveGEnabled";
      case 27: 
        return "setCallForwardingOption";
      case 26: 
        return "getCellLocationForSlot";
      case 25: 
        return "onOperatorNumericOrNameSet";
      case 24: 
        return "getSpn";
      case 23: 
        return "isSameOperator";
      case 22: 
        return "getSmallDeviceId";
      case 21: 
        return "getMeid";
      case 20: 
        return "getImei";
      case 19: 
        return "getDeviceId";
      case 18: 
        return "getMeidList";
      case 17: 
        return "getImeiList";
      case 16: 
        return "getDeviceIdList";
      case 15: 
        return "setIccCardActivate";
      case 14: 
        return "isIccCardActivate";
      case 13: 
        return "isGwsdSupport";
      case 12: 
        return "getSystemDefaultSlotId";
      case 11: 
        return "isVolteEnabledByPlatformForSlot";
      case 10: 
        return "isVolteEnabledByPlatform";
      case 9: 
        return "isVtEnabledByPlatformForSlot";
      case 8: 
        return "isVtEnabledByPlatform";
      case 7: 
        return "isVolteEnabledByUserForSlot";
      case 6: 
        return "isVolteEnabledByUser";
      case 5: 
        return "isWifiCallingAvailable";
      case 4: 
        return "isVideoTelephonyAvailable";
      case 3: 
        return "isImsRegistered";
      case 2: 
        return "setDefaultDataSlotId";
      }
      return "setDefaultVoiceSlotId";
    }
    
    public static boolean setDefaultImpl(IMiuiTelephony paramIMiuiTelephony)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIMiuiTelephony != null))
      {
        Proxy.sDefaultImpl = paramIMiuiTelephony;
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
        boolean bool2 = false;
        boolean bool3 = false;
        String str1;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 30: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isFiveGCapable();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 29: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isUserFiveGEnabled();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 28: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          if (paramParcel1.readInt() != 0) {
            bool3 = true;
          }
          setUserFiveGEnabled(bool3);
          paramParcel2.writeNoException();
          return true;
        case 27: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = paramParcel1.readInt();
          paramInt2 = paramParcel1.readInt();
          int i = paramParcel1.readInt();
          str1 = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          setCallForwardingOption(paramInt1, paramInt2, i, str1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 26: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getCellLocationForSlot(paramParcel1.readInt(), paramParcel1.readString());
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
        case 25: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = onOperatorNumericOrNameSet(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 24: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          str1 = paramParcel1.readString();
          paramInt1 = paramParcel1.readInt();
          String str2 = paramParcel1.readString();
          bool3 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool3 = true;
          }
          paramParcel1 = getSpn(str1, paramInt1, str2, bool3);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 23: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isSameOperator(paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 22: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getSmallDeviceId(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 21: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getMeid(paramParcel1.readInt(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 20: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getImei(paramParcel1.readInt(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 19: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getDeviceId(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 18: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getMeidList(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        case 17: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getImeiList(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        case 16: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramParcel1 = getDeviceIdList(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        case 15: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = paramParcel1.readInt();
          bool3 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool3 = true;
          }
          setIccCardActivate(paramInt1, bool3);
          paramParcel2.writeNoException();
          return true;
        case 14: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isIccCardActivate(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 13: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isGwsdSupport();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 12: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = getSystemDefaultSlotId();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 11: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isVolteEnabledByPlatformForSlot(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 10: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isVolteEnabledByPlatform();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 9: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isVtEnabledByPlatformForSlot(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 8: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isVtEnabledByPlatform();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 7: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isVolteEnabledByUserForSlot(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 6: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isVolteEnabledByUser();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isWifiCallingAvailable(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 4: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isVideoTelephonyAvailable(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = isImsRegistered(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 2: 
          paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
          paramInt1 = setDefaultDataSlotId(paramParcel1.readInt(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
        paramParcel1.enforceInterface("miui.telephony.IMiuiTelephony");
        setDefaultVoiceSlotId(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel2.writeString("miui.telephony.IMiuiTelephony");
      return true;
    }
    
    private static class Proxy
      implements IMiuiTelephony
    {
      public static IMiuiTelephony sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public Bundle getCellLocationForSlot(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(26, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getCellLocationForSlot(paramInt, paramString);
            return paramString;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramString = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString = null;
          }
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getDeviceId(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(19, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getDeviceId(paramString);
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
      
      public List<String> getDeviceIdList(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(16, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getDeviceIdList(paramString);
            return paramString;
          }
          localParcel2.readException();
          paramString = localParcel2.createStringArrayList();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getImei(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(20, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getImei(paramInt, paramString);
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
      
      public List<String> getImeiList(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(17, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getImeiList(paramString);
            return paramString;
          }
          localParcel2.readException();
          paramString = localParcel2.createStringArrayList();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "miui.telephony.IMiuiTelephony";
      }
      
      public String getMeid(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(21, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getMeid(paramInt, paramString);
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
      
      public List<String> getMeidList(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(18, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getMeidList(paramString);
            return paramString;
          }
          localParcel2.readException();
          paramString = localParcel2.createStringArrayList();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getSmallDeviceId(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(22, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString = IMiuiTelephony.Stub.getDefaultImpl().getSmallDeviceId(paramString);
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
      
      public String getSpn(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(24, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString1 = IMiuiTelephony.Stub.getDefaultImpl().getSpn(paramString1, paramInt, paramString2, paramBoolean);
            return paramString1;
          }
          localParcel2.readException();
          paramString1 = localParcel2.readString();
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getSystemDefaultSlotId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          if ((!this.mRemote.transact(12, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            i = IMiuiTelephony.Stub.getDefaultImpl().getSystemDefaultSlotId();
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
      
      public boolean isFiveGCapable()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(30, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isFiveGCapable();
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
      
      public boolean isGwsdSupport()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(13, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isGwsdSupport();
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
      
      public boolean isIccCardActivate(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(14, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isIccCardActivate(paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public boolean isImsRegistered(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(3, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isImsRegistered(paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public boolean isSameOperator(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(23, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isSameOperator(paramString1, paramString2);
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
      
      public boolean isUserFiveGEnabled()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(29, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isUserFiveGEnabled();
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
      
      public boolean isVideoTelephonyAvailable(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(4, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isVideoTelephonyAvailable(paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public boolean isVolteEnabledByPlatform()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(10, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isVolteEnabledByPlatform();
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
      
      public boolean isVolteEnabledByPlatformForSlot(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(11, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isVolteEnabledByPlatformForSlot(paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public boolean isVolteEnabledByUser()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(6, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isVolteEnabledByUser();
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
      
      public boolean isVolteEnabledByUserForSlot(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(7, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isVolteEnabledByUserForSlot(paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public boolean isVtEnabledByPlatform()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(8, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isVtEnabledByPlatform();
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
      
      public boolean isVtEnabledByPlatformForSlot(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(9, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isVtEnabledByPlatformForSlot(paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public boolean isWifiCallingAvailable(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(5, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().isWifiCallingAvailable(paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public String onOperatorNumericOrNameSet(int paramInt, String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          if ((!this.mRemote.transact(25, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            paramString1 = IMiuiTelephony.Stub.getDefaultImpl().onOperatorNumericOrNameSet(paramInt, paramString1, paramString2);
            return paramString1;
          }
          localParcel2.readException();
          paramString1 = localParcel2.readString();
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setCallForwardingOption(int paramInt1, int paramInt2, int paramInt3, String paramString, ResultReceiver paramResultReceiver)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeString(paramString);
          if (paramResultReceiver != null)
          {
            localParcel1.writeInt(1);
            paramResultReceiver.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(27, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            IMiuiTelephony.Stub.getDefaultImpl().setCallForwardingOption(paramInt1, paramInt2, paramInt3, paramString, paramResultReceiver);
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
      
      public boolean setDefaultDataSlotId(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(2, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            bool = IMiuiTelephony.Stub.getDefaultImpl().setDefaultDataSlotId(paramInt, paramString);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
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
      
      public void setDefaultVoiceSlotId(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            IMiuiTelephony.Stub.getDefaultImpl().setDefaultVoiceSlotId(paramInt, paramString);
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
      
      public void setIccCardActivate(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(15, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            IMiuiTelephony.Stub.getDefaultImpl().setIccCardActivate(paramInt, paramBoolean);
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
      
      public void setUserFiveGEnabled(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.telephony.IMiuiTelephony");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(28, localParcel1, localParcel2, 0)) && (IMiuiTelephony.Stub.getDefaultImpl() != null))
          {
            IMiuiTelephony.Stub.getDefaultImpl().setUserFiveGEnabled(paramBoolean);
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
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/IMiuiTelephony.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */