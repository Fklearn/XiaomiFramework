package com.miui.networkassistant.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.FirewallRuleSet;
import java.util.List;
import java.util.Map;

public interface IFirewallBinder extends IInterface {

    public static abstract class Stub extends Binder implements IFirewallBinder {
        private static final String DESCRIPTOR = "com.miui.networkassistant.service.IFirewallBinder";
        static final int TRANSACTION_getMobileRestrictPackages = 12;
        static final int TRANSACTION_getMobileRule = 10;
        static final int TRANSACTION_getRoamingAppCountByRule = 19;
        static final int TRANSACTION_getRoamingRule = 16;
        static final int TRANSACTION_getRoamingWhiteListEnable = 18;
        static final int TRANSACTION_getRule = 2;
        static final int TRANSACTION_getTempMobileRule = 14;
        static final int TRANSACTION_getTempWifiRule = 8;
        static final int TRANSACTION_getWifiRestrictPackages = 6;
        static final int TRANSACTION_getWifiRule = 4;
        static final int TRANSACTION_isStarted = 1;
        static final int TRANSACTION_setMobileRule = 9;
        static final int TRANSACTION_setMobileRuleForPackages = 11;
        static final int TRANSACTION_setRoamingRule = 15;
        static final int TRANSACTION_setRoamingWhiteListEnable = 17;
        static final int TRANSACTION_setTempMobileRule = 13;
        static final int TRANSACTION_setTempWifiRule = 7;
        static final int TRANSACTION_setWifiRule = 3;
        static final int TRANSACTION_setWifiRuleForPackages = 5;

        private static class Proxy implements IFirewallBinder {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public List<String> getMobileRestrictPackages(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public FirewallRule getMobileRule(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? FirewallRule.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getRoamingAppCountByRule(FirewallRule firewallRule) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (firewallRule != null) {
                        obtain.writeInt(1);
                        firewallRule.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public FirewallRule getRoamingRule(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? FirewallRule.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRoamingWhiteListEnable() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public FirewallRuleSet getRule(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? FirewallRuleSet.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public FirewallRule getTempMobileRule(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? FirewallRule.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public FirewallRule getTempWifiRule(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? FirewallRule.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<String> getWifiRestrictPackages() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public FirewallRule getWifiRule(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? FirewallRule.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isStarted() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean setMobileRule(String str, FirewallRule firewallRule, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = true;
                    if (firewallRule != null) {
                        obtain.writeInt(1);
                        firewallRule.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setMobileRuleForPackages(Map map, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeMap(map);
                    obtain.writeInt(i);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setRoamingRule(String str, FirewallRule firewallRule) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (firewallRule != null) {
                        obtain.writeInt(1);
                        firewallRule.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setRoamingWhiteListEnable(boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean setTempMobileRule(String str, FirewallRule firewallRule, String str2, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = true;
                    if (firewallRule != null) {
                        obtain.writeInt(1);
                        firewallRule.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean setTempWifiRule(String str, FirewallRule firewallRule, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = true;
                    if (firewallRule != null) {
                        obtain.writeInt(1);
                        firewallRule.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str2);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean setWifiRule(String str, FirewallRule firewallRule) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = true;
                    if (firewallRule != null) {
                        obtain.writeInt(1);
                        firewallRule.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setWifiRuleForPackages(Map map) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeMap(map);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFirewallBinder asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IFirewallBinder)) ? new Proxy(iBinder) : (IFirewallBinder) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                FirewallRule firewallRule = null;
                boolean z = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean isStarted = isStarted();
                        parcel2.writeNoException();
                        if (isStarted) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        FirewallRuleSet rule = getRule(parcel.readString());
                        parcel2.writeNoException();
                        if (rule != null) {
                            parcel2.writeInt(1);
                            rule.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        String readString = parcel.readString();
                        if (parcel.readInt() != 0) {
                            firewallRule = FirewallRule.CREATOR.createFromParcel(parcel);
                        }
                        boolean wifiRule = setWifiRule(readString, firewallRule);
                        parcel2.writeNoException();
                        if (wifiRule) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        FirewallRule wifiRule2 = getWifiRule(parcel.readString());
                        parcel2.writeNoException();
                        if (wifiRule2 != null) {
                            parcel2.writeInt(1);
                            wifiRule2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        setWifiRuleForPackages(parcel.readHashMap(getClass().getClassLoader()));
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        List<String> wifiRestrictPackages = getWifiRestrictPackages();
                        parcel2.writeNoException();
                        parcel2.writeStringList(wifiRestrictPackages);
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        String readString2 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            firewallRule = FirewallRule.CREATOR.createFromParcel(parcel);
                        }
                        boolean tempWifiRule = setTempWifiRule(readString2, firewallRule, parcel.readString());
                        parcel2.writeNoException();
                        if (tempWifiRule) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        FirewallRule tempWifiRule2 = getTempWifiRule(parcel.readString());
                        parcel2.writeNoException();
                        if (tempWifiRule2 != null) {
                            parcel2.writeInt(1);
                            tempWifiRule2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        String readString3 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            firewallRule = FirewallRule.CREATOR.createFromParcel(parcel);
                        }
                        boolean mobileRule = setMobileRule(readString3, firewallRule, parcel.readInt());
                        parcel2.writeNoException();
                        if (mobileRule) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        FirewallRule mobileRule2 = getMobileRule(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        if (mobileRule2 != null) {
                            parcel2.writeInt(1);
                            mobileRule2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 11:
                        parcel.enforceInterface(DESCRIPTOR);
                        setMobileRuleForPackages(parcel.readHashMap(getClass().getClassLoader()), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 12:
                        parcel.enforceInterface(DESCRIPTOR);
                        List<String> mobileRestrictPackages = getMobileRestrictPackages(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeStringList(mobileRestrictPackages);
                        return true;
                    case 13:
                        parcel.enforceInterface(DESCRIPTOR);
                        String readString4 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            firewallRule = FirewallRule.CREATOR.createFromParcel(parcel);
                        }
                        boolean tempMobileRule = setTempMobileRule(readString4, firewallRule, parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        if (tempMobileRule) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 14:
                        parcel.enforceInterface(DESCRIPTOR);
                        FirewallRule tempMobileRule2 = getTempMobileRule(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        if (tempMobileRule2 != null) {
                            parcel2.writeInt(1);
                            tempMobileRule2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 15:
                        parcel.enforceInterface(DESCRIPTOR);
                        String readString5 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            firewallRule = FirewallRule.CREATOR.createFromParcel(parcel);
                        }
                        setRoamingRule(readString5, firewallRule);
                        parcel2.writeNoException();
                        return true;
                    case 16:
                        parcel.enforceInterface(DESCRIPTOR);
                        FirewallRule roamingRule = getRoamingRule(parcel.readString());
                        parcel2.writeNoException();
                        if (roamingRule != null) {
                            parcel2.writeInt(1);
                            roamingRule.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 17:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        setRoamingWhiteListEnable(z);
                        parcel2.writeNoException();
                        return true;
                    case 18:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean roamingWhiteListEnable = getRoamingWhiteListEnable();
                        parcel2.writeNoException();
                        if (roamingWhiteListEnable) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 19:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            firewallRule = FirewallRule.CREATOR.createFromParcel(parcel);
                        }
                        int roamingAppCountByRule = getRoamingAppCountByRule(firewallRule);
                        parcel2.writeNoException();
                        parcel2.writeInt(roamingAppCountByRule);
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    List<String> getMobileRestrictPackages(int i);

    FirewallRule getMobileRule(String str, int i);

    int getRoamingAppCountByRule(FirewallRule firewallRule);

    FirewallRule getRoamingRule(String str);

    boolean getRoamingWhiteListEnable();

    FirewallRuleSet getRule(String str);

    FirewallRule getTempMobileRule(String str, int i);

    FirewallRule getTempWifiRule(String str);

    List<String> getWifiRestrictPackages();

    FirewallRule getWifiRule(String str);

    boolean isStarted();

    boolean setMobileRule(String str, FirewallRule firewallRule, int i);

    void setMobileRuleForPackages(Map map, int i);

    void setRoamingRule(String str, FirewallRule firewallRule);

    void setRoamingWhiteListEnable(boolean z);

    boolean setTempMobileRule(String str, FirewallRule firewallRule, String str2, int i);

    boolean setTempWifiRule(String str, FirewallRule firewallRule, String str2);

    boolean setWifiRule(String str, FirewallRule firewallRule);

    void setWifiRuleForPackages(Map map);
}
