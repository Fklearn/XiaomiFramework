package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.util.HexDump;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.pm.CloudControlPreinstallService;
import com.android.server.usb.descriptors.UsbDescriptor;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

final class HdmiUtils {
    private static final int[] ADDRESS_TO_TYPE = {0, 1, 1, 3, 4, 5, 3, 3, 4, 1, 3, 4, 2, 2, 0};
    private static final String[] DEFAULT_NAMES = {"TV", "Recorder_1", "Recorder_2", "Tuner_1", "Playback_1", "AudioSystem", "Tuner_2", "Tuner_3", "Playback_2", "Recorder_3", "Tuner_4", "Playback_3", "Reserved_1", "Reserved_2", "Secondary_TV"};
    private static final String TAG = "HdmiUtils";
    static final int TARGET_NOT_UNDER_LOCAL_DEVICE = -1;
    static final int TARGET_SAME_PHYSICAL_ADDRESS = 0;

    private HdmiUtils() {
    }

    static boolean isValidAddress(int address) {
        return address >= 0 && address <= 14;
    }

    static int getTypeFromAddress(int address) {
        if (isValidAddress(address)) {
            return ADDRESS_TO_TYPE[address];
        }
        return -1;
    }

    static String getDefaultDeviceName(int address) {
        if (isValidAddress(address)) {
            return DEFAULT_NAMES[address];
        }
        return "";
    }

    static void verifyAddressType(int logicalAddress, int deviceType) {
        int actualDeviceType = getTypeFromAddress(logicalAddress);
        if (actualDeviceType != deviceType) {
            throw new IllegalArgumentException("Device type missmatch:[Expected:" + deviceType + ", Actual:" + actualDeviceType);
        }
    }

    static boolean checkCommandSource(HdmiCecMessage cmd, int expectedAddress, String tag) {
        int src = cmd.getSource();
        if (src == expectedAddress) {
            return true;
        }
        Slog.w(tag, "Invalid source [Expected:" + expectedAddress + ", Actual:" + src + "]");
        return false;
    }

    static boolean parseCommandParamSystemAudioStatus(HdmiCecMessage cmd) {
        return cmd.getParams()[0] == 1;
    }

    static boolean isAudioStatusMute(HdmiCecMessage cmd) {
        return (cmd.getParams()[0] & 128) == 128;
    }

    static int getAudioStatusVolume(HdmiCecMessage cmd) {
        int volume = cmd.getParams()[0] & 127;
        if (volume < 0 || 100 < volume) {
            return -1;
        }
        return volume;
    }

    static List<Integer> asImmutableList(int[] is) {
        ArrayList<Integer> list = new ArrayList<>(is.length);
        for (int type : is) {
            list.add(Integer.valueOf(type));
        }
        return Collections.unmodifiableList(list);
    }

    static int twoBytesToInt(byte[] data) {
        return ((data[0] & 255) << 8) | (data[1] & 255);
    }

    static int twoBytesToInt(byte[] data, int offset) {
        return ((data[offset] & 255) << 8) | (data[offset + 1] & 255);
    }

    static int threeBytesToInt(byte[] data) {
        return ((data[0] & 255) << UsbDescriptor.DESCRIPTORTYPE_CAPABILITY) | ((data[1] & 255) << 8) | (data[2] & 255);
    }

    static <T> List<T> sparseArrayToList(SparseArray<T> array) {
        ArrayList<T> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.valueAt(i));
        }
        return list;
    }

    static <T> List<T> mergeToUnmodifiableList(List<T> a, List<T> b) {
        if (a.isEmpty() && b.isEmpty()) {
            return Collections.emptyList();
        }
        if (a.isEmpty()) {
            return Collections.unmodifiableList(b);
        }
        if (b.isEmpty()) {
            return Collections.unmodifiableList(a);
        }
        List<T> newList = new ArrayList<>();
        newList.addAll(a);
        newList.addAll(b);
        return Collections.unmodifiableList(newList);
    }

    static boolean isAffectingActiveRoutingPath(int activePath, int newPath) {
        int i = 0;
        while (true) {
            if (i > 12) {
                break;
            } else if (((newPath >> i) & 15) != 0) {
                newPath &= 65520 << i;
                break;
            } else {
                i += 4;
            }
        }
        if (newPath == 0) {
            return true;
        }
        return isInActiveRoutingPath(activePath, newPath);
    }

    static boolean isInActiveRoutingPath(int activePath, int newPath) {
        int nibbleNew;
        for (int i = 12; i >= 0; i -= 4) {
            int nibbleActive = (activePath >> i) & 15;
            if (nibbleActive == 0 || (nibbleNew = (newPath >> i) & 15) == 0) {
                return true;
            }
            if (nibbleActive != nibbleNew) {
                return false;
            }
        }
        return true;
    }

    static HdmiDeviceInfo cloneHdmiDeviceInfo(HdmiDeviceInfo info, int newPowerStatus) {
        return new HdmiDeviceInfo(info.getLogicalAddress(), info.getPhysicalAddress(), info.getPortId(), info.getDeviceType(), info.getVendorId(), info.getDisplayName(), newPowerStatus);
    }

    static <T> void dumpSparseArray(IndentingPrintWriter pw, String name, SparseArray<T> sparseArray) {
        printWithTrailingColon(pw, name);
        pw.increaseIndent();
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            int key = sparseArray.keyAt(i);
            pw.printPair(Integer.toString(key), sparseArray.get(key));
            pw.println();
        }
        pw.decreaseIndent();
    }

    private static void printWithTrailingColon(IndentingPrintWriter pw, String name) {
        pw.println(name.endsWith(":") ? name : name.concat(":"));
    }

    static <K, V> void dumpMap(IndentingPrintWriter pw, String name, Map<K, V> map) {
        printWithTrailingColon(pw, name);
        pw.increaseIndent();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            pw.printPair(entry.getKey().toString(), entry.getValue());
            pw.println();
        }
        pw.decreaseIndent();
    }

    static <T> void dumpIterable(IndentingPrintWriter pw, String name, Iterable<T> values) {
        printWithTrailingColon(pw, name);
        pw.increaseIndent();
        for (T value : values) {
            pw.println(value);
        }
        pw.decreaseIndent();
    }

    public static int getLocalPortFromPhysicalAddress(int targetPhysicalAddress, int myPhysicalAddress) {
        if (myPhysicalAddress == targetPhysicalAddress) {
            return 0;
        }
        int mask = 61440;
        int finalMask = 61440;
        int maskedAddress = myPhysicalAddress;
        while (maskedAddress != 0) {
            maskedAddress = myPhysicalAddress & mask;
            finalMask |= mask;
            mask >>= 4;
        }
        int portAddress = targetPhysicalAddress & finalMask;
        if (((finalMask << 4) & portAddress) != myPhysicalAddress) {
            return -1;
        }
        int port = portAddress & (mask << 4);
        while ((port >> 4) != 0) {
            port >>= 4;
        }
        return port;
    }

    public static class ShortAudioDescriptorXmlParser {
        private static final String NS = null;

        public static List<DeviceConfig> parse(InputStream in) throws XmlPullParserException, IOException {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            parser.setInput(in, (String) null);
            parser.nextTag();
            return readDevices(parser);
        }

        private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() == 2) {
                int depth = 1;
                while (depth != 0) {
                    int next = parser.next();
                    if (next == 2) {
                        depth++;
                    } else if (next == 3) {
                        depth--;
                    }
                }
                return;
            }
            throw new IllegalStateException();
        }

        private static List<DeviceConfig> readDevices(XmlPullParser parser) throws XmlPullParserException, IOException {
            List<DeviceConfig> devices = new ArrayList<>();
            parser.require(2, NS, "config");
            while (parser.next() != 3) {
                if (parser.getEventType() == 2) {
                    if (parser.getName().equals(CloudControlPreinstallService.ConnectEntity.DEVICE)) {
                        String deviceType = parser.getAttributeValue((String) null, DatabaseHelper.SoundModelContract.KEY_TYPE);
                        DeviceConfig config = null;
                        if (deviceType != null) {
                            config = readDeviceConfig(parser, deviceType);
                        }
                        if (config != null) {
                            devices.add(config);
                        }
                    } else {
                        skip(parser);
                    }
                }
            }
            return devices;
        }

        private static DeviceConfig readDeviceConfig(XmlPullParser parser, String deviceType) throws XmlPullParserException, IOException {
            List<CodecSad> codecSads = new ArrayList<>();
            parser.require(2, NS, CloudControlPreinstallService.ConnectEntity.DEVICE);
            while (parser.next() != 3) {
                if (parser.getEventType() == 2) {
                    if (parser.getName().equals("supportedFormat")) {
                        String codecAttriValue = parser.getAttributeValue((String) null, "format");
                        String sadAttriValue = parser.getAttributeValue((String) null, "descriptor");
                        int format = codecAttriValue == null ? 0 : formatNameToNum(codecAttriValue);
                        byte[] descriptor = readSad(sadAttriValue);
                        if (!(format == 0 || descriptor == null)) {
                            codecSads.add(new CodecSad(format, descriptor));
                        }
                        parser.nextTag();
                        parser.require(3, NS, "supportedFormat");
                    } else {
                        skip(parser);
                    }
                }
            }
            if (codecSads.size() == 0) {
                return null;
            }
            return new DeviceConfig(deviceType, codecSads);
        }

        private static byte[] readSad(String sad) {
            if (sad == null || sad.length() == 0) {
                return null;
            }
            byte[] sadBytes = HexDump.hexStringToByteArray(sad);
            if (sadBytes.length == 3) {
                return sadBytes;
            }
            Slog.w(HdmiUtils.TAG, "SAD byte array length is not 3. Length = " + sadBytes.length);
            return null;
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int formatNameToNum(java.lang.String r17) {
            /*
                r0 = r17
                int r1 = r17.hashCode()
                r2 = 14
                r3 = 13
                r4 = 12
                r5 = 11
                r6 = 10
                r7 = 9
                r8 = 8
                r9 = 7
                r10 = 6
                r11 = 5
                r12 = 4
                r13 = 3
                r14 = 2
                r15 = 1
                r16 = 0
                switch(r1) {
                    case -2131742975: goto L_0x00c1;
                    case -1197237630: goto L_0x00b7;
                    case -1194465888: goto L_0x00ad;
                    case -1186286867: goto L_0x00a3;
                    case -1186286866: goto L_0x0099;
                    case -358943216: goto L_0x008f;
                    case -282810364: goto L_0x0085;
                    case -282807375: goto L_0x007b;
                    case -282806906: goto L_0x0071;
                    case -282806876: goto L_0x0066;
                    case -282798811: goto L_0x005a;
                    case -282798383: goto L_0x004f;
                    case -176844499: goto L_0x0044;
                    case -176785545: goto L_0x0038;
                    case 129424511: goto L_0x002d;
                    case 2082539401: goto L_0x0022;
                    default: goto L_0x0020;
                }
            L_0x0020:
                goto L_0x00cb
            L_0x0022:
                java.lang.String r1 = "AUDIO_FORMAT_TRUEHD"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r4
                goto L_0x00cc
            L_0x002d:
                java.lang.String r1 = "AUDIO_FORMAT_DD"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r14
                goto L_0x00cc
            L_0x0038:
                java.lang.String r1 = "AUDIO_FORMAT_NONE"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r16
                goto L_0x00cc
            L_0x0044:
                java.lang.String r1 = "AUDIO_FORMAT_LPCM"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r15
                goto L_0x00cc
            L_0x004f:
                java.lang.String r1 = "AUDIO_FORMAT_MP3"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r12
                goto L_0x00cc
            L_0x005a:
                java.lang.String r1 = "AUDIO_FORMAT_MAX"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = 15
                goto L_0x00cc
            L_0x0066:
                java.lang.String r1 = "AUDIO_FORMAT_DTS"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r9
                goto L_0x00cc
            L_0x0071:
                java.lang.String r1 = "AUDIO_FORMAT_DST"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r3
                goto L_0x00cc
            L_0x007b:
                java.lang.String r1 = "AUDIO_FORMAT_DDP"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r6
                goto L_0x00cc
            L_0x0085:
                java.lang.String r1 = "AUDIO_FORMAT_AAC"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r10
                goto L_0x00cc
            L_0x008f:
                java.lang.String r1 = "AUDIO_FORMAT_ONEBITAUDIO"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r7
                goto L_0x00cc
            L_0x0099:
                java.lang.String r1 = "AUDIO_FORMAT_MPEG2"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r11
                goto L_0x00cc
            L_0x00a3:
                java.lang.String r1 = "AUDIO_FORMAT_MPEG1"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r13
                goto L_0x00cc
            L_0x00ad:
                java.lang.String r1 = "AUDIO_FORMAT_DTSHD"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r5
                goto L_0x00cc
            L_0x00b7:
                java.lang.String r1 = "AUDIO_FORMAT_ATRAC"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r8
                goto L_0x00cc
            L_0x00c1:
                java.lang.String r1 = "AUDIO_FORMAT_WMAPRO"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0020
                r1 = r2
                goto L_0x00cc
            L_0x00cb:
                r1 = -1
            L_0x00cc:
                switch(r1) {
                    case 0: goto L_0x00e1;
                    case 1: goto L_0x00e0;
                    case 2: goto L_0x00df;
                    case 3: goto L_0x00de;
                    case 4: goto L_0x00dd;
                    case 5: goto L_0x00dc;
                    case 6: goto L_0x00db;
                    case 7: goto L_0x00da;
                    case 8: goto L_0x00d9;
                    case 9: goto L_0x00d8;
                    case 10: goto L_0x00d7;
                    case 11: goto L_0x00d6;
                    case 12: goto L_0x00d5;
                    case 13: goto L_0x00d4;
                    case 14: goto L_0x00d3;
                    case 15: goto L_0x00d0;
                    default: goto L_0x00cf;
                }
            L_0x00cf:
                return r16
            L_0x00d0:
                r1 = 15
                return r1
            L_0x00d3:
                return r2
            L_0x00d4:
                return r3
            L_0x00d5:
                return r4
            L_0x00d6:
                return r5
            L_0x00d7:
                return r6
            L_0x00d8:
                return r7
            L_0x00d9:
                return r8
            L_0x00da:
                return r9
            L_0x00db:
                return r10
            L_0x00dc:
                return r11
            L_0x00dd:
                return r12
            L_0x00de:
                return r13
            L_0x00df:
                return r14
            L_0x00e0:
                return r15
            L_0x00e1:
                return r16
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.hdmi.HdmiUtils.ShortAudioDescriptorXmlParser.formatNameToNum(java.lang.String):int");
        }
    }

    public static class DeviceConfig {
        public final String name;
        public final List<CodecSad> supportedCodecs;

        public DeviceConfig(String name2, List<CodecSad> supportedCodecs2) {
            this.name = name2;
            this.supportedCodecs = supportedCodecs2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof DeviceConfig)) {
                return false;
            }
            DeviceConfig that = (DeviceConfig) obj;
            if (!that.name.equals(this.name) || !that.supportedCodecs.equals(this.supportedCodecs)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.name, Integer.valueOf(this.supportedCodecs.hashCode())});
        }
    }

    public static class CodecSad {
        public final int audioCodec;
        public final byte[] sad;

        public CodecSad(int audioCodec2, byte[] sad2) {
            this.audioCodec = audioCodec2;
            this.sad = sad2;
        }

        public CodecSad(int audioCodec2, String sad2) {
            this.audioCodec = audioCodec2;
            this.sad = HexDump.hexStringToByteArray(sad2);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CodecSad)) {
                return false;
            }
            CodecSad that = (CodecSad) obj;
            if (that.audioCodec != this.audioCodec || !Arrays.equals(that.sad, this.sad)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(this.audioCodec), Integer.valueOf(Arrays.hashCode(this.sad))});
        }
    }
}
