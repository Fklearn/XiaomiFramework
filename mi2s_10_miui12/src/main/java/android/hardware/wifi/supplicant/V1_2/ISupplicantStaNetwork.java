package android.hardware.wifi.supplicant.V1_2;

import android.hardware.wifi.supplicant.V1_0.ISupplicantNetwork;
import android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork;
import android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetworkCallback;
import android.hardware.wifi.supplicant.V1_0.SupplicantStatus;
import android.hidl.base.V1_0.DebugInfo;
import android.hidl.base.V1_0.IBase;
import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwBlob;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwInterface;
import android.os.NativeHandle;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import miui.bluetooth.ble.MiServiceData;

public interface ISupplicantStaNetwork extends android.hardware.wifi.supplicant.V1_1.ISupplicantStaNetwork {
    public static final String kInterfaceName = "android.hardware.wifi.supplicant@1.2::ISupplicantStaNetwork";

    @FunctionalInterface
    public interface getGroupCipher_1_2Callback {
        void onValues(SupplicantStatus supplicantStatus, int i);
    }

    @FunctionalInterface
    public interface getGroupMgmtCipherCallback {
        void onValues(SupplicantStatus supplicantStatus, int i);
    }

    @FunctionalInterface
    public interface getKeyMgmt_1_2Callback {
        void onValues(SupplicantStatus supplicantStatus, int i);
    }

    @FunctionalInterface
    public interface getPairwiseCipher_1_2Callback {
        void onValues(SupplicantStatus supplicantStatus, int i);
    }

    @FunctionalInterface
    public interface getSaePasswordCallback {
        void onValues(SupplicantStatus supplicantStatus, String str);
    }

    @FunctionalInterface
    public interface getSaePasswordIdCallback {
        void onValues(SupplicantStatus supplicantStatus, String str);
    }

    IHwBinder asBinder();

    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    SupplicantStatus enableSuiteBEapOpenSslCiphers() throws RemoteException;

    SupplicantStatus enableTlsSuiteBEapPhase1Param(boolean z) throws RemoteException;

    DebugInfo getDebugInfo() throws RemoteException;

    void getGroupCipher_1_2(getGroupCipher_1_2Callback getgroupcipher_1_2callback) throws RemoteException;

    void getGroupMgmtCipher(getGroupMgmtCipherCallback getgroupmgmtciphercallback) throws RemoteException;

    ArrayList<byte[]> getHashChain() throws RemoteException;

    void getKeyMgmt_1_2(getKeyMgmt_1_2Callback getkeymgmt_1_2callback) throws RemoteException;

    void getPairwiseCipher_1_2(getPairwiseCipher_1_2Callback getpairwisecipher_1_2callback) throws RemoteException;

    void getSaePassword(getSaePasswordCallback getsaepasswordcallback) throws RemoteException;

    void getSaePasswordId(getSaePasswordIdCallback getsaepasswordidcallback) throws RemoteException;

    ArrayList<String> interfaceChain() throws RemoteException;

    String interfaceDescriptor() throws RemoteException;

    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    void notifySyspropsChanged() throws RemoteException;

    void ping() throws RemoteException;

    SupplicantStatus setGroupCipher_1_2(int i) throws RemoteException;

    SupplicantStatus setGroupMgmtCipher(int i) throws RemoteException;

    void setHALInstrumentation() throws RemoteException;

    SupplicantStatus setKeyMgmt_1_2(int i) throws RemoteException;

    SupplicantStatus setPairwiseCipher_1_2(int i) throws RemoteException;

    SupplicantStatus setSaePassword(String str) throws RemoteException;

    SupplicantStatus setSaePasswordId(String str) throws RemoteException;

    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static ISupplicantStaNetwork asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof ISupplicantStaNetwork)) {
            return (ISupplicantStaNetwork) iface;
        }
        ISupplicantStaNetwork proxy = new Proxy(binder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals(kInterfaceName)) {
                    return proxy;
                }
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    static ISupplicantStaNetwork castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static ISupplicantStaNetwork getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static ISupplicantStaNetwork getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    static ISupplicantStaNetwork getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    static ISupplicantStaNetwork getService() throws RemoteException {
        return getService("default");
    }

    public static final class KeyMgmtMask {
        public static final int DPP = 8388608;
        public static final int FT_EAP = 32;
        public static final int FT_PSK = 64;
        public static final int IEEE8021X = 8;
        public static final int NONE = 4;
        public static final int OSEN = 32768;
        public static final int OWE = 4194304;
        public static final int SAE = 1024;
        public static final int SUITE_B_192 = 131072;
        public static final int WPA_EAP = 1;
        public static final int WPA_EAP_SHA256 = 128;
        public static final int WPA_PSK = 2;
        public static final int WPA_PSK_SHA256 = 256;

        public static final String toString(int o) {
            if (o == 1) {
                return "WPA_EAP";
            }
            if (o == 2) {
                return "WPA_PSK";
            }
            if (o == 4) {
                return "NONE";
            }
            if (o == 8) {
                return "IEEE8021X";
            }
            if (o == 32) {
                return "FT_EAP";
            }
            if (o == 64) {
                return "FT_PSK";
            }
            if (o == 32768) {
                return "OSEN";
            }
            if (o == 128) {
                return "WPA_EAP_SHA256";
            }
            if (o == 256) {
                return "WPA_PSK_SHA256";
            }
            if (o == 1024) {
                return "SAE";
            }
            if (o == 131072) {
                return "SUITE_B_192";
            }
            if (o == 4194304) {
                return "OWE";
            }
            if (o == 8388608) {
                return "DPP";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            if ((o & 1) == 1) {
                list.add("WPA_EAP");
                flipped = 0 | 1;
            }
            if ((o & 2) == 2) {
                list.add("WPA_PSK");
                flipped |= 2;
            }
            if ((o & 4) == 4) {
                list.add("NONE");
                flipped |= 4;
            }
            if ((o & 8) == 8) {
                list.add("IEEE8021X");
                flipped |= 8;
            }
            if ((o & 32) == 32) {
                list.add("FT_EAP");
                flipped |= 32;
            }
            if ((o & 64) == 64) {
                list.add("FT_PSK");
                flipped |= 64;
            }
            if ((o & 32768) == 32768) {
                list.add("OSEN");
                flipped |= 32768;
            }
            if ((o & 128) == 128) {
                list.add("WPA_EAP_SHA256");
                flipped |= 128;
            }
            if ((o & 256) == 256) {
                list.add("WPA_PSK_SHA256");
                flipped |= 256;
            }
            if ((o & 1024) == 1024) {
                list.add("SAE");
                flipped |= 1024;
            }
            if ((o & 131072) == 131072) {
                list.add("SUITE_B_192");
                flipped |= 131072;
            }
            if ((o & 4194304) == 4194304) {
                list.add("OWE");
                flipped |= 4194304;
            }
            if ((o & 8388608) == 8388608) {
                list.add("DPP");
                flipped |= 8388608;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    public static final class PairwiseCipherMask {
        public static final int CCMP = 16;
        public static final int GCMP_256 = 256;
        public static final int NONE = 1;
        public static final int TKIP = 8;

        public static final String toString(int o) {
            if (o == 1) {
                return "NONE";
            }
            if (o == 8) {
                return "TKIP";
            }
            if (o == 16) {
                return "CCMP";
            }
            if (o == 256) {
                return "GCMP_256";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            if ((o & 1) == 1) {
                list.add("NONE");
                flipped = 0 | 1;
            }
            if ((o & 8) == 8) {
                list.add("TKIP");
                flipped |= 8;
            }
            if ((o & 16) == 16) {
                list.add("CCMP");
                flipped |= 16;
            }
            if ((o & 256) == 256) {
                list.add("GCMP_256");
                flipped |= 256;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    public static final class GroupCipherMask {
        public static final int CCMP = 16;
        public static final int GCMP_256 = 256;
        public static final int GTK_NOT_USED = 16384;
        public static final int TKIP = 8;
        public static final int WEP104 = 4;
        public static final int WEP40 = 2;

        public static final String toString(int o) {
            if (o == 2) {
                return "WEP40";
            }
            if (o == 4) {
                return "WEP104";
            }
            if (o == 8) {
                return "TKIP";
            }
            if (o == 16) {
                return "CCMP";
            }
            if (o == 16384) {
                return "GTK_NOT_USED";
            }
            if (o == 256) {
                return "GCMP_256";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            if ((o & 2) == 2) {
                list.add("WEP40");
                flipped = 0 | 2;
            }
            if ((o & 4) == 4) {
                list.add("WEP104");
                flipped |= 4;
            }
            if ((o & 8) == 8) {
                list.add("TKIP");
                flipped |= 8;
            }
            if ((o & 16) == 16) {
                list.add("CCMP");
                flipped |= 16;
            }
            if ((o & 16384) == 16384) {
                list.add("GTK_NOT_USED");
                flipped |= 16384;
            }
            if ((o & 256) == 256) {
                list.add("GCMP_256");
                flipped |= 256;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    public static final class GroupMgmtCipherMask {
        public static final int BIP_CMAC_256 = 8192;
        public static final int BIP_GMAC_128 = 2048;
        public static final int BIP_GMAC_256 = 4096;

        public static final String toString(int o) {
            if (o == 2048) {
                return "BIP_GMAC_128";
            }
            if (o == 4096) {
                return "BIP_GMAC_256";
            }
            if (o == 8192) {
                return "BIP_CMAC_256";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            if ((o & 2048) == 2048) {
                list.add("BIP_GMAC_128");
                flipped = 0 | 2048;
            }
            if ((o & 4096) == 4096) {
                list.add("BIP_GMAC_256");
                flipped |= 4096;
            }
            if ((o & 8192) == 8192) {
                list.add("BIP_CMAC_256");
                flipped |= 8192;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    public static final class Proxy implements ISupplicantStaNetwork {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.wifi.supplicant@1.2::ISupplicantStaNetwork]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        public void getId(ISupplicantNetwork.getIdCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getInterfaceName(ISupplicantNetwork.getInterfaceNameCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getType(ISupplicantNetwork.getTypeCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus registerCallback(ISupplicantStaNetworkCallback callback) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeStrongBinder(callback == null ? null : callback.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setSsid(ArrayList<Byte> ssid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt8Vector(ssid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setBssid(byte[] bssid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwBlob _hidl_blob = new HwBlob(6);
            byte[] _hidl_array_item_0 = bssid;
            if (_hidl_array_item_0 == null || _hidl_array_item_0.length != 6) {
                throw new IllegalArgumentException("Array element is not of the expected length");
            }
            _hidl_blob.putInt8Array(0, _hidl_array_item_0);
            _hidl_request.writeBuffer(_hidl_blob);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setScanSsid(boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setKeyMgmt(int keyMgmtMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(keyMgmtMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setProto(int protoMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(protoMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setAuthAlg(int authAlgMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(authAlgMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setGroupCipher(int groupCipherMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(groupCipherMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setPairwiseCipher(int pairwiseCipherMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(pairwiseCipherMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setPskPassphrase(String psk) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(psk);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setPsk(byte[] psk) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwBlob _hidl_blob = new HwBlob(32);
            byte[] _hidl_array_item_0 = psk;
            if (_hidl_array_item_0 == null || _hidl_array_item_0.length != 32) {
                throw new IllegalArgumentException("Array element is not of the expected length");
            }
            _hidl_blob.putInt8Array(0, _hidl_array_item_0);
            _hidl_request.writeBuffer(_hidl_blob);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setWepKey(int keyIdx, ArrayList<Byte> wepKey) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(keyIdx);
            _hidl_request.writeInt8Vector(wepKey);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setWepTxKeyIdx(int keyIdx) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(keyIdx);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setRequirePmf(boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapMethod(int method) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(method);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(18, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapPhase2Method(int method) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(method);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(19, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapIdentity(ArrayList<Byte> identity) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt8Vector(identity);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(20, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapAnonymousIdentity(ArrayList<Byte> identity) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt8Vector(identity);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(21, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapPassword(ArrayList<Byte> password) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt8Vector(password);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(22, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapCACert(String path) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(path);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(23, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapCAPath(String path) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(path);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(24, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapClientCert(String path) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(path);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(25, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapPrivateKeyId(String id) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(id);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(26, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapSubjectMatch(String match) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(match);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(27, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapAltSubjectMatch(String match) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(match);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(28, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapEngine(boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(29, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapEngineID(String id) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(id);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(30, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapDomainSuffixMatch(String match) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(match);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(31, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setProactiveKeyCaching(boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(32, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setIdStr(String idStr) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(idStr);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(33, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setUpdateIdentifier(int id) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(id);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(34, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public void getSsid(ISupplicantStaNetwork.getSsidCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(35, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt8Vector());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getBssid(ISupplicantStaNetwork.getBssidCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(36, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                byte[] _hidl_out_bssid = new byte[6];
                _hidl_reply.readBuffer(6).copyToInt8Array(0, _hidl_out_bssid, 6);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_bssid);
            } finally {
                _hidl_reply.release();
            }
        }

        public void getScanSsid(ISupplicantStaNetwork.getScanSsidCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(37, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readBool());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getKeyMgmt(ISupplicantStaNetwork.getKeyMgmtCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(38, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getProto(ISupplicantStaNetwork.getProtoCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(39, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getAuthAlg(ISupplicantStaNetwork.getAuthAlgCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(40, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getGroupCipher(ISupplicantStaNetwork.getGroupCipherCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(41, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getPairwiseCipher(ISupplicantStaNetwork.getPairwiseCipherCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(42, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getPskPassphrase(ISupplicantStaNetwork.getPskPassphraseCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(43, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getPsk(ISupplicantStaNetwork.getPskCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(44, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                byte[] _hidl_out_psk = new byte[32];
                _hidl_reply.readBuffer(32).copyToInt8Array(0, _hidl_out_psk, 32);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_psk);
            } finally {
                _hidl_reply.release();
            }
        }

        public void getWepKey(int keyIdx, ISupplicantStaNetwork.getWepKeyCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(keyIdx);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(45, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt8Vector());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getWepTxKeyIdx(ISupplicantStaNetwork.getWepTxKeyIdxCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(46, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getRequirePmf(ISupplicantStaNetwork.getRequirePmfCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(47, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readBool());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapMethod(ISupplicantStaNetwork.getEapMethodCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(48, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapPhase2Method(ISupplicantStaNetwork.getEapPhase2MethodCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(49, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapIdentity(ISupplicantStaNetwork.getEapIdentityCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(50, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt8Vector());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapAnonymousIdentity(ISupplicantStaNetwork.getEapAnonymousIdentityCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(51, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt8Vector());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapPassword(ISupplicantStaNetwork.getEapPasswordCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(52, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt8Vector());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapCACert(ISupplicantStaNetwork.getEapCACertCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(53, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapCAPath(ISupplicantStaNetwork.getEapCAPathCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(54, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapClientCert(ISupplicantStaNetwork.getEapClientCertCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(55, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapPrivateKeyId(ISupplicantStaNetwork.getEapPrivateKeyIdCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(56, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapSubjectMatch(ISupplicantStaNetwork.getEapSubjectMatchCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(57, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapAltSubjectMatch(ISupplicantStaNetwork.getEapAltSubjectMatchCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(58, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapEngine(ISupplicantStaNetwork.getEapEngineCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(59, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readBool());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapEngineID(ISupplicantStaNetwork.getEapEngineIDCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(60, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getEapDomainSuffixMatch(ISupplicantStaNetwork.getEapDomainSuffixMatchCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(61, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getIdStr(ISupplicantStaNetwork.getIdStrCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(62, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getWpsNfcConfigurationToken(ISupplicantStaNetwork.getWpsNfcConfigurationTokenCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(63, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt8Vector());
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus enable(boolean noConnect) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeBool(noConnect);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(64, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus disable() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(65, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus select() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(66, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus sendNetworkEapSimGsmAuthResponse(ArrayList<ISupplicantStaNetwork.NetworkResponseEapSimGsmAuthParams> params) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            ISupplicantStaNetwork.NetworkResponseEapSimGsmAuthParams.writeVectorToParcel(_hidl_request, params);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(67, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus sendNetworkEapSimGsmAuthFailure() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(68, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus sendNetworkEapSimUmtsAuthResponse(ISupplicantStaNetwork.NetworkResponseEapSimUmtsAuthParams params) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            params.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(69, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus sendNetworkEapSimUmtsAutsResponse(byte[] auts) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwBlob _hidl_blob = new HwBlob(14);
            byte[] _hidl_array_item_0 = auts;
            if (_hidl_array_item_0 == null || _hidl_array_item_0.length != 14) {
                throw new IllegalArgumentException("Array element is not of the expected length");
            }
            _hidl_blob.putInt8Array(0, _hidl_array_item_0);
            _hidl_request.writeBuffer(_hidl_blob);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(70, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus sendNetworkEapSimUmtsAuthFailure() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(71, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus sendNetworkEapIdentityResponse(ArrayList<Byte> identity) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt8Vector(identity);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(72, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setEapEncryptedImsiIdentity(ArrayList<Byte> identity) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_1.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt8Vector(identity);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(73, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus sendNetworkEapIdentityResponse_1_1(ArrayList<Byte> identity, ArrayList<Byte> encryptedIdentity) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.wifi.supplicant.V1_1.ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt8Vector(identity);
            _hidl_request.writeInt8Vector(encryptedIdentity);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(74, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setKeyMgmt_1_2(int keyMgmtMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(keyMgmtMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(75, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public void getKeyMgmt_1_2(getKeyMgmt_1_2Callback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(76, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setPairwiseCipher_1_2(int pairwiseCipherMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(pairwiseCipherMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(77, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public void getPairwiseCipher_1_2(getPairwiseCipher_1_2Callback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(78, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setGroupCipher_1_2(int groupCipherMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(groupCipherMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(79, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public void getGroupCipher_1_2(getGroupCipher_1_2Callback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(80, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setGroupMgmtCipher(int groupMgmtCipherMask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeInt32(groupMgmtCipherMask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(81, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public void getGroupMgmtCipher(getGroupMgmtCipherCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(82, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readInt32());
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus enableTlsSuiteBEapPhase1Param(boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(83, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus enableSuiteBEapOpenSslCiphers() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(84, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public void getSaePassword(getSaePasswordCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(85, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public void getSaePasswordId(getSaePasswordIdCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(86, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_reply.readString());
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setSaePassword(String saePassword) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(saePassword);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(87, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public SupplicantStatus setSaePasswordId(String saePasswordId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(ISupplicantStaNetwork.kInterfaceName);
            _hidl_request.writeString(saePasswordId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(88, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                SupplicantStatus _hidl_out_status = new SupplicantStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256067662, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                return _hidl_reply.readStringVector();
            } finally {
                _hidl_reply.release();
            }
        }

        public void debug(NativeHandle fd, ArrayList<String> options) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            _hidl_request.writeNativeHandle(fd);
            _hidl_request.writeStringVector(options);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256131655, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        public String interfaceDescriptor() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256136003, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                return _hidl_reply.readString();
            } finally {
                _hidl_reply.release();
            }
        }

        public ArrayList<byte[]> getHashChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256398152, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<byte[]> _hidl_out_hashchain = new ArrayList<>();
                HwBlob _hidl_blob = _hidl_reply.readBuffer(16);
                int _hidl_vec_size = _hidl_blob.getInt32(8);
                HwBlob childBlob = _hidl_reply.readEmbeddedBuffer((long) (_hidl_vec_size * 32), _hidl_blob.handle(), 0, true);
                _hidl_out_hashchain.clear();
                for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                    byte[] _hidl_vec_element = new byte[32];
                    childBlob.copyToInt8Array((long) (_hidl_index_0 * 32), _hidl_vec_element, 32);
                    _hidl_out_hashchain.add(_hidl_vec_element);
                }
                return _hidl_out_hashchain;
            } finally {
                _hidl_reply.release();
            }
        }

        public void setHALInstrumentation() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256462420, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        public void ping() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256921159, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        public DebugInfo getDebugInfo() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257049926, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                DebugInfo _hidl_out_info = new DebugInfo();
                _hidl_out_info.readFromParcel(_hidl_reply);
                return _hidl_out_info;
            } finally {
                _hidl_reply.release();
            }
        }

        public void notifySyspropsChanged() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257120595, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    public static abstract class Stub extends HwBinder implements ISupplicantStaNetwork {
        public IHwBinder asBinder() {
            return this;
        }

        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(new String[]{ISupplicantStaNetwork.kInterfaceName, android.hardware.wifi.supplicant.V1_1.ISupplicantStaNetwork.kInterfaceName, android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.kInterfaceName, ISupplicantNetwork.kInterfaceName, IBase.kInterfaceName}));
        }

        public void debug(NativeHandle fd, ArrayList<String> arrayList) {
        }

        public final String interfaceDescriptor() {
            return ISupplicantStaNetwork.kInterfaceName;
        }

        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[][]{new byte[]{-17, -69, 6, 28, -106, -97, -87, 85, 61, 36, 61, -90, -18, 35, -72, 63, -27, -44, -86, 102, 58, 123, -120, -106, -83, -59, 46, 43, 1, 91, -62, -13}, new byte[]{16, -1, 47, -82, 81, 99, 70, -72, 97, 33, 54, -116, -27, 121, 13, 90, -52, -33, -53, 115, -104, 50, 70, -72, 19, -13, -44, -120, -74, 109, -76, 90}, new byte[]{-79, 46, -16, -67, -40, -92, -46, 71, -88, -90, -23, 96, -78, 39, -19, 50, 56, 63, 43, 2, 65, -11, 93, 103, -4, -22, 110, -1, 106, 103, 55, -6}, new byte[]{-51, -96, 16, 8, -64, 105, 34, -6, 55, -63, 33, 62, -101, -72, 49, -95, 9, -77, 23, 69, 50, Byte.MIN_VALUE, 86, 22, -5, 113, 97, -19, -60, 3, -122, 111}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, MiServiceData.CAPABILITY_IO, -54, 76}}));
        }

        public final void setHALInstrumentation() {
        }

        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        public final void ping() {
        }

        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0;
            info.arch = 0;
            return info;
        }

        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        public IHwInterface queryLocalInterface(String descriptor) {
            if (ISupplicantStaNetwork.kInterfaceName.equals(descriptor)) {
                return this;
            }
            return null;
        }

        public void registerAsService(String serviceName) throws RemoteException {
            registerService(serviceName);
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        /* JADX WARNING: type inference failed for: r0v8 */
        /* JADX WARNING: type inference failed for: r0v10 */
        /* JADX WARNING: type inference failed for: r0v12 */
        /* JADX WARNING: type inference failed for: r0v14 */
        /* JADX WARNING: type inference failed for: r0v16 */
        /* JADX WARNING: type inference failed for: r0v18 */
        /* JADX WARNING: type inference failed for: r0v20 */
        /* JADX WARNING: type inference failed for: r0v22 */
        /* JADX WARNING: type inference failed for: r0v24 */
        /* JADX WARNING: type inference failed for: r0v26 */
        /* JADX WARNING: type inference failed for: r0v28 */
        /* JADX WARNING: type inference failed for: r0v30 */
        /* JADX WARNING: type inference failed for: r0v32 */
        /* JADX WARNING: type inference failed for: r0v34 */
        /* JADX WARNING: type inference failed for: r0v36 */
        /* JADX WARNING: type inference failed for: r0v38 */
        /* JADX WARNING: type inference failed for: r0v40 */
        /* JADX WARNING: type inference failed for: r0v42 */
        /* JADX WARNING: type inference failed for: r0v44 */
        /* JADX WARNING: type inference failed for: r0v46 */
        /* JADX WARNING: type inference failed for: r0v48 */
        /* JADX WARNING: type inference failed for: r0v50 */
        /* JADX WARNING: type inference failed for: r0v52 */
        /* JADX WARNING: type inference failed for: r0v54 */
        /* JADX WARNING: type inference failed for: r0v56 */
        /* JADX WARNING: type inference failed for: r0v58 */
        /* JADX WARNING: type inference failed for: r0v60 */
        /* JADX WARNING: type inference failed for: r0v62 */
        /* JADX WARNING: type inference failed for: r0v64 */
        /* JADX WARNING: type inference failed for: r0v66 */
        /* JADX WARNING: type inference failed for: r0v68 */
        /* JADX WARNING: type inference failed for: r0v128 */
        /* JADX WARNING: type inference failed for: r0v130 */
        /* JADX WARNING: type inference failed for: r0v132 */
        /* JADX WARNING: type inference failed for: r0v134 */
        /* JADX WARNING: type inference failed for: r0v136 */
        /* JADX WARNING: type inference failed for: r0v138 */
        /* JADX WARNING: type inference failed for: r0v140 */
        /* JADX WARNING: type inference failed for: r0v142 */
        /* JADX WARNING: type inference failed for: r0v144 */
        /* JADX WARNING: type inference failed for: r1v77 */
        /* JADX WARNING: type inference failed for: r1v79 */
        /* JADX WARNING: type inference failed for: r0v148 */
        /* JADX WARNING: type inference failed for: r0v152 */
        /* JADX WARNING: type inference failed for: r0v156 */
        /* JADX WARNING: type inference failed for: r0v160 */
        /* JADX WARNING: type inference failed for: r0v164 */
        /* JADX WARNING: type inference failed for: r0v166 */
        /* JADX WARNING: type inference failed for: r0v172 */
        /* JADX WARNING: type inference failed for: r0v174 */
        /* JADX WARNING: type inference failed for: r1v95 */
        /* JADX WARNING: type inference failed for: r1v97 */
        /* JADX WARNING: type inference failed for: r1v99 */
        /* JADX WARNING: type inference failed for: r1v101 */
        /* JADX WARNING: type inference failed for: r1v105 */
        /* JADX WARNING: type inference failed for: r1v107 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onTransact(int r12, android.os.HwParcel r13, final android.os.HwParcel r14, int r15) throws android.os.RemoteException {
            /*
                r11 = this;
                java.lang.String r0 = "android.hardware.wifi.supplicant@1.1::ISupplicantStaNetwork"
                java.lang.String r1 = "android.hardware.wifi.supplicant@1.0::ISupplicantNetwork"
                r2 = 32
                java.lang.String r3 = "android.hardware.wifi.supplicant@1.2::ISupplicantStaNetwork"
                java.lang.String r4 = "android.hardware.wifi.supplicant@1.0::ISupplicantStaNetwork"
                r5 = -2147483648(0xffffffff80000000, float:-0.0)
                r6 = 0
                r7 = 1
                switch(r12) {
                    case 1: goto L_0x0d66;
                    case 2: goto L_0x0d4b;
                    case 3: goto L_0x0d30;
                    case 4: goto L_0x0d06;
                    case 5: goto L_0x0cdf;
                    case 6: goto L_0x0cad;
                    case 7: goto L_0x0c86;
                    case 8: goto L_0x0c5f;
                    case 9: goto L_0x0c38;
                    case 10: goto L_0x0c11;
                    case 11: goto L_0x0bea;
                    case 12: goto L_0x0bc3;
                    case 13: goto L_0x0b9c;
                    case 14: goto L_0x0b6b;
                    case 15: goto L_0x0b40;
                    case 16: goto L_0x0b19;
                    case 17: goto L_0x0af2;
                    case 18: goto L_0x0acb;
                    case 19: goto L_0x0aa4;
                    case 20: goto L_0x0a7d;
                    case 21: goto L_0x0a56;
                    case 22: goto L_0x0a2f;
                    case 23: goto L_0x0a08;
                    case 24: goto L_0x09e1;
                    case 25: goto L_0x09ba;
                    case 26: goto L_0x0993;
                    case 27: goto L_0x096c;
                    case 28: goto L_0x0945;
                    case 29: goto L_0x091e;
                    case 30: goto L_0x08f7;
                    case 31: goto L_0x08d0;
                    case 32: goto L_0x08a9;
                    case 33: goto L_0x0882;
                    case 34: goto L_0x085b;
                    case 35: goto L_0x083e;
                    case 36: goto L_0x0821;
                    case 37: goto L_0x0804;
                    case 38: goto L_0x07e7;
                    case 39: goto L_0x07ca;
                    case 40: goto L_0x07ad;
                    case 41: goto L_0x0790;
                    case 42: goto L_0x0773;
                    case 43: goto L_0x0756;
                    case 44: goto L_0x0739;
                    case 45: goto L_0x0718;
                    case 46: goto L_0x06fb;
                    case 47: goto L_0x06de;
                    case 48: goto L_0x06c1;
                    case 49: goto L_0x06a4;
                    case 50: goto L_0x0687;
                    case 51: goto L_0x066a;
                    case 52: goto L_0x064d;
                    case 53: goto L_0x0630;
                    case 54: goto L_0x0613;
                    case 55: goto L_0x05f6;
                    case 56: goto L_0x05d9;
                    case 57: goto L_0x05bc;
                    case 58: goto L_0x059f;
                    case 59: goto L_0x0582;
                    case 60: goto L_0x0565;
                    case 61: goto L_0x0548;
                    case 62: goto L_0x052b;
                    case 63: goto L_0x050e;
                    case 64: goto L_0x04e7;
                    case 65: goto L_0x04c4;
                    case 66: goto L_0x04a1;
                    case 67: goto L_0x047a;
                    case 68: goto L_0x0457;
                    case 69: goto L_0x042c;
                    case 70: goto L_0x03f9;
                    case 71: goto L_0x03d6;
                    case 72: goto L_0x03af;
                    case 73: goto L_0x0388;
                    case 74: goto L_0x035d;
                    case 75: goto L_0x0336;
                    case 76: goto L_0x0319;
                    case 77: goto L_0x02f2;
                    case 78: goto L_0x02d5;
                    case 79: goto L_0x02ae;
                    case 80: goto L_0x0291;
                    case 81: goto L_0x026a;
                    case 82: goto L_0x024d;
                    case 83: goto L_0x0226;
                    case 84: goto L_0x0203;
                    case 85: goto L_0x01e6;
                    case 86: goto L_0x01c9;
                    case 87: goto L_0x01a2;
                    case 88: goto L_0x017b;
                    default: goto L_0x0011;
                }
            L_0x0011:
                java.lang.String r0 = "android.hidl.base@1.0::IBase"
                switch(r12) {
                    case 256067662: goto L_0x0158;
                    case 256131655: goto L_0x0131;
                    case 256136003: goto L_0x010e;
                    case 256398152: goto L_0x00aa;
                    case 256462420: goto L_0x0092;
                    case 256660548: goto L_0x0082;
                    case 256921159: goto L_0x0063;
                    case 257049926: goto L_0x0040;
                    case 257120595: goto L_0x0028;
                    case 257250372: goto L_0x0018;
                    default: goto L_0x0016;
                }
            L_0x0016:
                goto L_0x0d81
            L_0x0018:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x001d
                r6 = r7
            L_0x001d:
                r0 = r6
                if (r0 == 0) goto L_0x0d81
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0028:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x002d
                r6 = r7
            L_0x002d:
                r1 = r6
                if (r1 == r7) goto L_0x0038
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0038:
                r13.enforceInterface(r0)
                r11.notifySyspropsChanged()
                goto L_0x0d81
            L_0x0040:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x0045
                goto L_0x0046
            L_0x0045:
                r7 = r6
            L_0x0046:
                r1 = r7
                if (r1 == 0) goto L_0x0051
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0051:
                r13.enforceInterface(r0)
                android.hidl.base.V1_0.DebugInfo r0 = r11.getDebugInfo()
                r14.writeStatus(r6)
                r0.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0063:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x0068
                goto L_0x0069
            L_0x0068:
                r7 = r6
            L_0x0069:
                r1 = r7
                if (r1 == 0) goto L_0x0074
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0074:
                r13.enforceInterface(r0)
                r11.ping()
                r14.writeStatus(r6)
                r14.send()
                goto L_0x0d81
            L_0x0082:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0087
                r6 = r7
            L_0x0087:
                r0 = r6
                if (r0 == 0) goto L_0x0d81
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0092:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x0097
                r6 = r7
            L_0x0097:
                r1 = r6
                if (r1 == r7) goto L_0x00a2
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x00a2:
                r13.enforceInterface(r0)
                r11.setHALInstrumentation()
                goto L_0x0d81
            L_0x00aa:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x00af
                goto L_0x00b0
            L_0x00af:
                r7 = r6
            L_0x00b0:
                r1 = r7
                if (r1 == 0) goto L_0x00bb
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x00bb:
                r13.enforceInterface(r0)
                java.util.ArrayList r0 = r11.getHashChain()
                r14.writeStatus(r6)
                android.os.HwBlob r3 = new android.os.HwBlob
                r4 = 16
                r3.<init>(r4)
                int r4 = r0.size()
                r7 = 8
                r3.putInt32(r7, r4)
                r7 = 12
                r3.putBool(r7, r6)
                android.os.HwBlob r5 = new android.os.HwBlob
                int r6 = r4 * 32
                r5.<init>(r6)
                r6 = 0
            L_0x00e2:
                if (r6 >= r4) goto L_0x0101
                int r7 = r6 * 32
                long r7 = (long) r7
                java.lang.Object r9 = r0.get(r6)
                byte[] r9 = (byte[]) r9
                if (r9 == 0) goto L_0x00f9
                int r10 = r9.length
                if (r10 != r2) goto L_0x00f9
                r5.putInt8Array(r7, r9)
                int r6 = r6 + 1
                goto L_0x00e2
            L_0x00f9:
                java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
                java.lang.String r10 = "Array element is not of the expected length"
                r2.<init>(r10)
                throw r2
            L_0x0101:
                r6 = 0
                r3.putBlob(r6, r5)
                r14.writeBuffer(r3)
                r14.send()
                goto L_0x0d81
            L_0x010e:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x0113
                goto L_0x0114
            L_0x0113:
                r7 = r6
            L_0x0114:
                r1 = r7
                if (r1 == 0) goto L_0x011f
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x011f:
                r13.enforceInterface(r0)
                java.lang.String r0 = r11.interfaceDescriptor()
                r14.writeStatus(r6)
                r14.writeString(r0)
                r14.send()
                goto L_0x0d81
            L_0x0131:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x0136
                goto L_0x0137
            L_0x0136:
                r7 = r6
            L_0x0137:
                r1 = r7
                if (r1 == 0) goto L_0x0142
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0142:
                r13.enforceInterface(r0)
                android.os.NativeHandle r0 = r13.readNativeHandle()
                java.util.ArrayList r2 = r13.readStringVector()
                r11.debug(r0, r2)
                r14.writeStatus(r6)
                r14.send()
                goto L_0x0d81
            L_0x0158:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x015d
                goto L_0x015e
            L_0x015d:
                r7 = r6
            L_0x015e:
                r1 = r7
                if (r1 == 0) goto L_0x0169
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0169:
                r13.enforceInterface(r0)
                java.util.ArrayList r0 = r11.interfaceChain()
                r14.writeStatus(r6)
                r14.writeStringVector(r0)
                r14.send()
                goto L_0x0d81
            L_0x017b:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0180
                goto L_0x0181
            L_0x0180:
                r7 = r6
            L_0x0181:
                r0 = r7
                if (r0 == 0) goto L_0x018c
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x018c:
                r13.enforceInterface(r3)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setSaePasswordId(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x01a2:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x01a7
                goto L_0x01a8
            L_0x01a7:
                r7 = r6
            L_0x01a8:
                r0 = r7
                if (r0 == 0) goto L_0x01b3
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x01b3:
                r13.enforceInterface(r3)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setSaePassword(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x01c9:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x01ce
                r6 = r7
            L_0x01ce:
                r0 = r6
                if (r0 == 0) goto L_0x01d9
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x01d9:
                r13.enforceInterface(r3)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$38 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$38
                r1.<init>(r14)
                r11.getSaePasswordId(r1)
                goto L_0x0d81
            L_0x01e6:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x01eb
                r6 = r7
            L_0x01eb:
                r0 = r6
                if (r0 == 0) goto L_0x01f6
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x01f6:
                r13.enforceInterface(r3)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$37 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$37
                r1.<init>(r14)
                r11.getSaePassword(r1)
                goto L_0x0d81
            L_0x0203:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0208
                goto L_0x0209
            L_0x0208:
                r7 = r6
            L_0x0209:
                r0 = r7
                if (r0 == 0) goto L_0x0214
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0214:
                r13.enforceInterface(r3)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r1 = r11.enableSuiteBEapOpenSslCiphers()
                r14.writeStatus(r6)
                r1.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0226:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x022b
                goto L_0x022c
            L_0x022b:
                r7 = r6
            L_0x022c:
                r0 = r7
                if (r0 == 0) goto L_0x0237
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0237:
                r13.enforceInterface(r3)
                boolean r1 = r13.readBool()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.enableTlsSuiteBEapPhase1Param(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x024d:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0252
                r6 = r7
            L_0x0252:
                r0 = r6
                if (r0 == 0) goto L_0x025d
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x025d:
                r13.enforceInterface(r3)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$36 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$36
                r1.<init>(r14)
                r11.getGroupMgmtCipher(r1)
                goto L_0x0d81
            L_0x026a:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x026f
                goto L_0x0270
            L_0x026f:
                r7 = r6
            L_0x0270:
                r0 = r7
                if (r0 == 0) goto L_0x027b
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x027b:
                r13.enforceInterface(r3)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setGroupMgmtCipher(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0291:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0296
                r6 = r7
            L_0x0296:
                r0 = r6
                if (r0 == 0) goto L_0x02a1
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x02a1:
                r13.enforceInterface(r3)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$35 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$35
                r1.<init>(r14)
                r11.getGroupCipher_1_2(r1)
                goto L_0x0d81
            L_0x02ae:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x02b3
                goto L_0x02b4
            L_0x02b3:
                r7 = r6
            L_0x02b4:
                r0 = r7
                if (r0 == 0) goto L_0x02bf
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x02bf:
                r13.enforceInterface(r3)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setGroupCipher_1_2(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x02d5:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x02da
                r6 = r7
            L_0x02da:
                r0 = r6
                if (r0 == 0) goto L_0x02e5
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x02e5:
                r13.enforceInterface(r3)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$34 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$34
                r1.<init>(r14)
                r11.getPairwiseCipher_1_2(r1)
                goto L_0x0d81
            L_0x02f2:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x02f7
                goto L_0x02f8
            L_0x02f7:
                r7 = r6
            L_0x02f8:
                r0 = r7
                if (r0 == 0) goto L_0x0303
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0303:
                r13.enforceInterface(r3)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setPairwiseCipher_1_2(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0319:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x031e
                r6 = r7
            L_0x031e:
                r0 = r6
                if (r0 == 0) goto L_0x0329
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0329:
                r13.enforceInterface(r3)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$33 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$33
                r1.<init>(r14)
                r11.getKeyMgmt_1_2(r1)
                goto L_0x0d81
            L_0x0336:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x033b
                goto L_0x033c
            L_0x033b:
                r7 = r6
            L_0x033c:
                r0 = r7
                if (r0 == 0) goto L_0x0347
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0347:
                r13.enforceInterface(r3)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setKeyMgmt_1_2(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x035d:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x0362
                goto L_0x0363
            L_0x0362:
                r7 = r6
            L_0x0363:
                r1 = r7
                if (r1 == 0) goto L_0x036e
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x036e:
                r13.enforceInterface(r0)
                java.util.ArrayList r0 = r13.readInt8Vector()
                java.util.ArrayList r2 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r3 = r11.sendNetworkEapIdentityResponse_1_1(r0, r2)
                r14.writeStatus(r6)
                r3.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0388:
                r1 = r15 & 1
                if (r1 == 0) goto L_0x038d
                goto L_0x038e
            L_0x038d:
                r7 = r6
            L_0x038e:
                r1 = r7
                if (r1 == 0) goto L_0x0399
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0399:
                r13.enforceInterface(r0)
                java.util.ArrayList r0 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapEncryptedImsiIdentity(r0)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x03af:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x03b4
                goto L_0x03b5
            L_0x03b4:
                r7 = r6
            L_0x03b5:
                r0 = r7
                if (r0 == 0) goto L_0x03c0
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x03c0:
                r13.enforceInterface(r4)
                java.util.ArrayList r1 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.sendNetworkEapIdentityResponse(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x03d6:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x03db
                goto L_0x03dc
            L_0x03db:
                r7 = r6
            L_0x03dc:
                r0 = r7
                if (r0 == 0) goto L_0x03e7
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x03e7:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r1 = r11.sendNetworkEapSimUmtsAuthFailure()
                r14.writeStatus(r6)
                r1.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x03f9:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x03fe
                goto L_0x03ff
            L_0x03fe:
                r7 = r6
            L_0x03ff:
                r0 = r7
                if (r0 == 0) goto L_0x040a
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x040a:
                r13.enforceInterface(r4)
                r1 = 14
                byte[] r2 = new byte[r1]
                r3 = 14
                android.os.HwBlob r3 = r13.readBuffer(r3)
                r4 = 0
                r3.copyToInt8Array(r4, r2, r1)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r1 = r11.sendNetworkEapSimUmtsAutsResponse(r2)
                r14.writeStatus(r6)
                r1.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x042c:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0431
                goto L_0x0432
            L_0x0431:
                r7 = r6
            L_0x0432:
                r0 = r7
                if (r0 == 0) goto L_0x043d
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x043d:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork$NetworkResponseEapSimUmtsAuthParams r1 = new android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork$NetworkResponseEapSimUmtsAuthParams
                r1.<init>()
                r1.readFromParcel(r13)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.sendNetworkEapSimUmtsAuthResponse(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0457:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x045c
                goto L_0x045d
            L_0x045c:
                r7 = r6
            L_0x045d:
                r0 = r7
                if (r0 == 0) goto L_0x0468
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0468:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r1 = r11.sendNetworkEapSimGsmAuthFailure()
                r14.writeStatus(r6)
                r1.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x047a:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x047f
                goto L_0x0480
            L_0x047f:
                r7 = r6
            L_0x0480:
                r0 = r7
                if (r0 == 0) goto L_0x048b
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x048b:
                r13.enforceInterface(r4)
                java.util.ArrayList r1 = android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetwork.NetworkResponseEapSimGsmAuthParams.readVectorFromParcel(r13)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.sendNetworkEapSimGsmAuthResponse(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x04a1:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x04a6
                goto L_0x04a7
            L_0x04a6:
                r7 = r6
            L_0x04a7:
                r0 = r7
                if (r0 == 0) goto L_0x04b2
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x04b2:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r1 = r11.select()
                r14.writeStatus(r6)
                r1.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x04c4:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x04c9
                goto L_0x04ca
            L_0x04c9:
                r7 = r6
            L_0x04ca:
                r0 = r7
                if (r0 == 0) goto L_0x04d5
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x04d5:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r1 = r11.disable()
                r14.writeStatus(r6)
                r1.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x04e7:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x04ec
                goto L_0x04ed
            L_0x04ec:
                r7 = r6
            L_0x04ed:
                r0 = r7
                if (r0 == 0) goto L_0x04f8
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x04f8:
                r13.enforceInterface(r4)
                boolean r1 = r13.readBool()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.enable(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x050e:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0513
                r6 = r7
            L_0x0513:
                r0 = r6
                if (r0 == 0) goto L_0x051e
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x051e:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$32 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$32
                r1.<init>(r14)
                r11.getWpsNfcConfigurationToken(r1)
                goto L_0x0d81
            L_0x052b:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0530
                r6 = r7
            L_0x0530:
                r0 = r6
                if (r0 == 0) goto L_0x053b
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x053b:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$31 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$31
                r1.<init>(r14)
                r11.getIdStr(r1)
                goto L_0x0d81
            L_0x0548:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x054d
                r6 = r7
            L_0x054d:
                r0 = r6
                if (r0 == 0) goto L_0x0558
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0558:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$30 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$30
                r1.<init>(r14)
                r11.getEapDomainSuffixMatch(r1)
                goto L_0x0d81
            L_0x0565:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x056a
                r6 = r7
            L_0x056a:
                r0 = r6
                if (r0 == 0) goto L_0x0575
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0575:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$29 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$29
                r1.<init>(r14)
                r11.getEapEngineID(r1)
                goto L_0x0d81
            L_0x0582:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0587
                r6 = r7
            L_0x0587:
                r0 = r6
                if (r0 == 0) goto L_0x0592
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0592:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$28 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$28
                r1.<init>(r14)
                r11.getEapEngine(r1)
                goto L_0x0d81
            L_0x059f:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x05a4
                r6 = r7
            L_0x05a4:
                r0 = r6
                if (r0 == 0) goto L_0x05af
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x05af:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$27 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$27
                r1.<init>(r14)
                r11.getEapAltSubjectMatch(r1)
                goto L_0x0d81
            L_0x05bc:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x05c1
                r6 = r7
            L_0x05c1:
                r0 = r6
                if (r0 == 0) goto L_0x05cc
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x05cc:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$26 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$26
                r1.<init>(r14)
                r11.getEapSubjectMatch(r1)
                goto L_0x0d81
            L_0x05d9:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x05de
                r6 = r7
            L_0x05de:
                r0 = r6
                if (r0 == 0) goto L_0x05e9
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x05e9:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$25 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$25
                r1.<init>(r14)
                r11.getEapPrivateKeyId(r1)
                goto L_0x0d81
            L_0x05f6:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x05fb
                r6 = r7
            L_0x05fb:
                r0 = r6
                if (r0 == 0) goto L_0x0606
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0606:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$24 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$24
                r1.<init>(r14)
                r11.getEapClientCert(r1)
                goto L_0x0d81
            L_0x0613:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0618
                r6 = r7
            L_0x0618:
                r0 = r6
                if (r0 == 0) goto L_0x0623
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0623:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$23 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$23
                r1.<init>(r14)
                r11.getEapCAPath(r1)
                goto L_0x0d81
            L_0x0630:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0635
                r6 = r7
            L_0x0635:
                r0 = r6
                if (r0 == 0) goto L_0x0640
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0640:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$22 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$22
                r1.<init>(r14)
                r11.getEapCACert(r1)
                goto L_0x0d81
            L_0x064d:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0652
                r6 = r7
            L_0x0652:
                r0 = r6
                if (r0 == 0) goto L_0x065d
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x065d:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$21 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$21
                r1.<init>(r14)
                r11.getEapPassword(r1)
                goto L_0x0d81
            L_0x066a:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x066f
                r6 = r7
            L_0x066f:
                r0 = r6
                if (r0 == 0) goto L_0x067a
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x067a:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$20 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$20
                r1.<init>(r14)
                r11.getEapAnonymousIdentity(r1)
                goto L_0x0d81
            L_0x0687:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x068c
                r6 = r7
            L_0x068c:
                r0 = r6
                if (r0 == 0) goto L_0x0697
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0697:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$19 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$19
                r1.<init>(r14)
                r11.getEapIdentity(r1)
                goto L_0x0d81
            L_0x06a4:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x06a9
                r6 = r7
            L_0x06a9:
                r0 = r6
                if (r0 == 0) goto L_0x06b4
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x06b4:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$18 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$18
                r1.<init>(r14)
                r11.getEapPhase2Method(r1)
                goto L_0x0d81
            L_0x06c1:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x06c6
                r6 = r7
            L_0x06c6:
                r0 = r6
                if (r0 == 0) goto L_0x06d1
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x06d1:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$17 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$17
                r1.<init>(r14)
                r11.getEapMethod(r1)
                goto L_0x0d81
            L_0x06de:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x06e3
                r6 = r7
            L_0x06e3:
                r0 = r6
                if (r0 == 0) goto L_0x06ee
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x06ee:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$16 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$16
                r1.<init>(r14)
                r11.getRequirePmf(r1)
                goto L_0x0d81
            L_0x06fb:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0700
                r6 = r7
            L_0x0700:
                r0 = r6
                if (r0 == 0) goto L_0x070b
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x070b:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$15 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$15
                r1.<init>(r14)
                r11.getWepTxKeyIdx(r1)
                goto L_0x0d81
            L_0x0718:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x071d
                r6 = r7
            L_0x071d:
                r0 = r6
                if (r0 == 0) goto L_0x0728
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0728:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$14 r2 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$14
                r2.<init>(r14)
                r11.getWepKey(r1, r2)
                goto L_0x0d81
            L_0x0739:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x073e
                r6 = r7
            L_0x073e:
                r0 = r6
                if (r0 == 0) goto L_0x0749
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0749:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$13 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$13
                r1.<init>(r14)
                r11.getPsk(r1)
                goto L_0x0d81
            L_0x0756:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x075b
                r6 = r7
            L_0x075b:
                r0 = r6
                if (r0 == 0) goto L_0x0766
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0766:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$12 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$12
                r1.<init>(r14)
                r11.getPskPassphrase(r1)
                goto L_0x0d81
            L_0x0773:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0778
                r6 = r7
            L_0x0778:
                r0 = r6
                if (r0 == 0) goto L_0x0783
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0783:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$11 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$11
                r1.<init>(r14)
                r11.getPairwiseCipher(r1)
                goto L_0x0d81
            L_0x0790:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0795
                r6 = r7
            L_0x0795:
                r0 = r6
                if (r0 == 0) goto L_0x07a0
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x07a0:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$10 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$10
                r1.<init>(r14)
                r11.getGroupCipher(r1)
                goto L_0x0d81
            L_0x07ad:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x07b2
                r6 = r7
            L_0x07b2:
                r0 = r6
                if (r0 == 0) goto L_0x07bd
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x07bd:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$9 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$9
                r1.<init>(r14)
                r11.getAuthAlg(r1)
                goto L_0x0d81
            L_0x07ca:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x07cf
                r6 = r7
            L_0x07cf:
                r0 = r6
                if (r0 == 0) goto L_0x07da
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x07da:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$8 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$8
                r1.<init>(r14)
                r11.getProto(r1)
                goto L_0x0d81
            L_0x07e7:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x07ec
                r6 = r7
            L_0x07ec:
                r0 = r6
                if (r0 == 0) goto L_0x07f7
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x07f7:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$7 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$7
                r1.<init>(r14)
                r11.getKeyMgmt(r1)
                goto L_0x0d81
            L_0x0804:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0809
                r6 = r7
            L_0x0809:
                r0 = r6
                if (r0 == 0) goto L_0x0814
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0814:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$6 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$6
                r1.<init>(r14)
                r11.getScanSsid(r1)
                goto L_0x0d81
            L_0x0821:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0826
                r6 = r7
            L_0x0826:
                r0 = r6
                if (r0 == 0) goto L_0x0831
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0831:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$5 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$5
                r1.<init>(r14)
                r11.getBssid(r1)
                goto L_0x0d81
            L_0x083e:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0843
                r6 = r7
            L_0x0843:
                r0 = r6
                if (r0 == 0) goto L_0x084e
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x084e:
                r13.enforceInterface(r4)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$4 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$4
                r1.<init>(r14)
                r11.getSsid(r1)
                goto L_0x0d81
            L_0x085b:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0860
                goto L_0x0861
            L_0x0860:
                r7 = r6
            L_0x0861:
                r0 = r7
                if (r0 == 0) goto L_0x086c
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x086c:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setUpdateIdentifier(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0882:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0887
                goto L_0x0888
            L_0x0887:
                r7 = r6
            L_0x0888:
                r0 = r7
                if (r0 == 0) goto L_0x0893
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0893:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setIdStr(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x08a9:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x08ae
                goto L_0x08af
            L_0x08ae:
                r7 = r6
            L_0x08af:
                r0 = r7
                if (r0 == 0) goto L_0x08ba
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x08ba:
                r13.enforceInterface(r4)
                boolean r1 = r13.readBool()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setProactiveKeyCaching(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x08d0:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x08d5
                goto L_0x08d6
            L_0x08d5:
                r7 = r6
            L_0x08d6:
                r0 = r7
                if (r0 == 0) goto L_0x08e1
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x08e1:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapDomainSuffixMatch(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x08f7:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x08fc
                goto L_0x08fd
            L_0x08fc:
                r7 = r6
            L_0x08fd:
                r0 = r7
                if (r0 == 0) goto L_0x0908
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0908:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapEngineID(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x091e:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0923
                goto L_0x0924
            L_0x0923:
                r7 = r6
            L_0x0924:
                r0 = r7
                if (r0 == 0) goto L_0x092f
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x092f:
                r13.enforceInterface(r4)
                boolean r1 = r13.readBool()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapEngine(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0945:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x094a
                goto L_0x094b
            L_0x094a:
                r7 = r6
            L_0x094b:
                r0 = r7
                if (r0 == 0) goto L_0x0956
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0956:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapAltSubjectMatch(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x096c:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0971
                goto L_0x0972
            L_0x0971:
                r7 = r6
            L_0x0972:
                r0 = r7
                if (r0 == 0) goto L_0x097d
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x097d:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapSubjectMatch(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0993:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0998
                goto L_0x0999
            L_0x0998:
                r7 = r6
            L_0x0999:
                r0 = r7
                if (r0 == 0) goto L_0x09a4
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x09a4:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapPrivateKeyId(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x09ba:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x09bf
                goto L_0x09c0
            L_0x09bf:
                r7 = r6
            L_0x09c0:
                r0 = r7
                if (r0 == 0) goto L_0x09cb
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x09cb:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapClientCert(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x09e1:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x09e6
                goto L_0x09e7
            L_0x09e6:
                r7 = r6
            L_0x09e7:
                r0 = r7
                if (r0 == 0) goto L_0x09f2
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x09f2:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapCAPath(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0a08:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0a0d
                goto L_0x0a0e
            L_0x0a0d:
                r7 = r6
            L_0x0a0e:
                r0 = r7
                if (r0 == 0) goto L_0x0a19
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0a19:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapCACert(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0a2f:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0a34
                goto L_0x0a35
            L_0x0a34:
                r7 = r6
            L_0x0a35:
                r0 = r7
                if (r0 == 0) goto L_0x0a40
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0a40:
                r13.enforceInterface(r4)
                java.util.ArrayList r1 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapPassword(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0a56:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0a5b
                goto L_0x0a5c
            L_0x0a5b:
                r7 = r6
            L_0x0a5c:
                r0 = r7
                if (r0 == 0) goto L_0x0a67
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0a67:
                r13.enforceInterface(r4)
                java.util.ArrayList r1 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapAnonymousIdentity(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0a7d:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0a82
                goto L_0x0a83
            L_0x0a82:
                r7 = r6
            L_0x0a83:
                r0 = r7
                if (r0 == 0) goto L_0x0a8e
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0a8e:
                r13.enforceInterface(r4)
                java.util.ArrayList r1 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapIdentity(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0aa4:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0aa9
                goto L_0x0aaa
            L_0x0aa9:
                r7 = r6
            L_0x0aaa:
                r0 = r7
                if (r0 == 0) goto L_0x0ab5
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0ab5:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapPhase2Method(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0acb:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0ad0
                goto L_0x0ad1
            L_0x0ad0:
                r7 = r6
            L_0x0ad1:
                r0 = r7
                if (r0 == 0) goto L_0x0adc
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0adc:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setEapMethod(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0af2:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0af7
                goto L_0x0af8
            L_0x0af7:
                r7 = r6
            L_0x0af8:
                r0 = r7
                if (r0 == 0) goto L_0x0b03
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0b03:
                r13.enforceInterface(r4)
                boolean r1 = r13.readBool()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setRequirePmf(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0b19:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0b1e
                goto L_0x0b1f
            L_0x0b1e:
                r7 = r6
            L_0x0b1f:
                r0 = r7
                if (r0 == 0) goto L_0x0b2a
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0b2a:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setWepTxKeyIdx(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0b40:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0b45
                goto L_0x0b46
            L_0x0b45:
                r7 = r6
            L_0x0b46:
                r0 = r7
                if (r0 == 0) goto L_0x0b51
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0b51:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                java.util.ArrayList r2 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r3 = r11.setWepKey(r1, r2)
                r14.writeStatus(r6)
                r3.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0b6b:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0b70
                goto L_0x0b71
            L_0x0b70:
                r7 = r6
            L_0x0b71:
                r0 = r7
                if (r0 == 0) goto L_0x0b7c
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0b7c:
                r13.enforceInterface(r4)
                byte[] r1 = new byte[r2]
                r3 = 32
                android.os.HwBlob r3 = r13.readBuffer(r3)
                r4 = 0
                r3.copyToInt8Array(r4, r1, r2)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setPsk(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0b9c:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0ba1
                goto L_0x0ba2
            L_0x0ba1:
                r7 = r6
            L_0x0ba2:
                r0 = r7
                if (r0 == 0) goto L_0x0bad
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0bad:
                r13.enforceInterface(r4)
                java.lang.String r1 = r13.readString()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setPskPassphrase(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0bc3:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0bc8
                goto L_0x0bc9
            L_0x0bc8:
                r7 = r6
            L_0x0bc9:
                r0 = r7
                if (r0 == 0) goto L_0x0bd4
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0bd4:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setPairwiseCipher(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0bea:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0bef
                goto L_0x0bf0
            L_0x0bef:
                r7 = r6
            L_0x0bf0:
                r0 = r7
                if (r0 == 0) goto L_0x0bfb
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0bfb:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setGroupCipher(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0c11:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0c16
                goto L_0x0c17
            L_0x0c16:
                r7 = r6
            L_0x0c17:
                r0 = r7
                if (r0 == 0) goto L_0x0c22
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0c22:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setAuthAlg(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0c38:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0c3d
                goto L_0x0c3e
            L_0x0c3d:
                r7 = r6
            L_0x0c3e:
                r0 = r7
                if (r0 == 0) goto L_0x0c49
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0c49:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setProto(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0c5f:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0c64
                goto L_0x0c65
            L_0x0c64:
                r7 = r6
            L_0x0c65:
                r0 = r7
                if (r0 == 0) goto L_0x0c70
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0c70:
                r13.enforceInterface(r4)
                int r1 = r13.readInt32()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setKeyMgmt(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0c86:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0c8b
                goto L_0x0c8c
            L_0x0c8b:
                r7 = r6
            L_0x0c8c:
                r0 = r7
                if (r0 == 0) goto L_0x0c97
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0c97:
                r13.enforceInterface(r4)
                boolean r1 = r13.readBool()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setScanSsid(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0cad:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0cb2
                goto L_0x0cb3
            L_0x0cb2:
                r7 = r6
            L_0x0cb3:
                r0 = r7
                if (r0 == 0) goto L_0x0cbe
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0cbe:
                r13.enforceInterface(r4)
                r1 = 6
                byte[] r2 = new byte[r1]
                r3 = 6
                android.os.HwBlob r3 = r13.readBuffer(r3)
                r4 = 0
                r3.copyToInt8Array(r4, r2, r1)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r1 = r11.setBssid(r2)
                r14.writeStatus(r6)
                r1.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0cdf:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0ce4
                goto L_0x0ce5
            L_0x0ce4:
                r7 = r6
            L_0x0ce5:
                r0 = r7
                if (r0 == 0) goto L_0x0cf0
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0cf0:
                r13.enforceInterface(r4)
                java.util.ArrayList r1 = r13.readInt8Vector()
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.setSsid(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0d06:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0d0b
                goto L_0x0d0c
            L_0x0d0b:
                r7 = r6
            L_0x0d0c:
                r0 = r7
                if (r0 == 0) goto L_0x0d17
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0d17:
                r13.enforceInterface(r4)
                android.os.IHwBinder r1 = r13.readStrongBinder()
                android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetworkCallback r1 = android.hardware.wifi.supplicant.V1_0.ISupplicantStaNetworkCallback.asInterface(r1)
                android.hardware.wifi.supplicant.V1_0.SupplicantStatus r2 = r11.registerCallback(r1)
                r14.writeStatus(r6)
                r2.writeToParcel(r14)
                r14.send()
                goto L_0x0d81
            L_0x0d30:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0d35
                r6 = r7
            L_0x0d35:
                r0 = r6
                if (r0 == 0) goto L_0x0d3f
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0d3f:
                r13.enforceInterface(r1)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$3 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$3
                r1.<init>(r14)
                r11.getType(r1)
                goto L_0x0d81
            L_0x0d4b:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0d50
                r6 = r7
            L_0x0d50:
                r0 = r6
                if (r0 == 0) goto L_0x0d5a
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0d5a:
                r13.enforceInterface(r1)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$2 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$2
                r1.<init>(r14)
                r11.getInterfaceName(r1)
                goto L_0x0d81
            L_0x0d66:
                r0 = r15 & 1
                if (r0 == 0) goto L_0x0d6b
                r6 = r7
            L_0x0d6b:
                r0 = r6
                if (r0 == 0) goto L_0x0d75
                r14.writeStatus(r5)
                r14.send()
                goto L_0x0d81
            L_0x0d75:
                r13.enforceInterface(r1)
                android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$1 r1 = new android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork$Stub$1
                r1.<init>(r14)
                r11.getId(r1)
            L_0x0d81:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.wifi.supplicant.V1_2.ISupplicantStaNetwork.Stub.onTransact(int, android.os.HwParcel, android.os.HwParcel, int):void");
        }
    }
}
