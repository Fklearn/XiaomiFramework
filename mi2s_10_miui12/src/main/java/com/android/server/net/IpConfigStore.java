package com.android.server.net;

import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.net.DelayedDiskWrite;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Iterator;

public class IpConfigStore {
    private static final boolean DBG = false;
    protected static final String DNS_KEY = "dns";
    protected static final String EOS = "eos";
    protected static final String EXCLUSION_LIST_KEY = "exclusionList";
    protected static final String GATEWAY_KEY = "gateway";
    protected static final String ID_KEY = "id";
    protected static final int IPCONFIG_FILE_VERSION = 3;
    protected static final String IP_ASSIGNMENT_KEY = "ipAssignment";
    protected static final String LINK_ADDRESS_KEY = "linkAddress";
    protected static final String PROXY_HOST_KEY = "proxyHost";
    protected static final String PROXY_PAC_FILE = "proxyPac";
    protected static final String PROXY_PORT_KEY = "proxyPort";
    protected static final String PROXY_SETTINGS_KEY = "proxySettings";
    private static final String TAG = "IpConfigStore";
    protected final DelayedDiskWrite mWriter;

    public IpConfigStore(DelayedDiskWrite writer) {
        this.mWriter = writer;
    }

    public IpConfigStore() {
        this(new DelayedDiskWrite());
    }

    private static boolean writeConfig(DataOutputStream out, String configKey, IpConfiguration config) throws IOException {
        return writeConfig(out, configKey, config, 3);
    }

    @VisibleForTesting
    public static boolean writeConfig(DataOutputStream out, String configKey, IpConfiguration config, int version) throws IOException {
        boolean written = false;
        try {
            int i = AnonymousClass1.$SwitchMap$android$net$IpConfiguration$IpAssignment[config.ipAssignment.ordinal()];
            if (i == 1) {
                out.writeUTF(IP_ASSIGNMENT_KEY);
                out.writeUTF(config.ipAssignment.toString());
                StaticIpConfiguration staticIpConfiguration = config.staticIpConfiguration;
                if (staticIpConfiguration != null) {
                    if (staticIpConfiguration.ipAddress != null) {
                        LinkAddress ipAddress = staticIpConfiguration.ipAddress;
                        out.writeUTF(LINK_ADDRESS_KEY);
                        out.writeUTF(ipAddress.getAddress().getHostAddress());
                        out.writeInt(ipAddress.getPrefixLength());
                    }
                    if (staticIpConfiguration.gateway != null) {
                        out.writeUTF(GATEWAY_KEY);
                        out.writeInt(0);
                        out.writeInt(1);
                        out.writeUTF(staticIpConfiguration.gateway.getHostAddress());
                    }
                    Iterator it = staticIpConfiguration.dnsServers.iterator();
                    while (it.hasNext()) {
                        out.writeUTF(DNS_KEY);
                        out.writeUTF(((InetAddress) it.next()).getHostAddress());
                    }
                }
                written = true;
            } else if (i == 2) {
                out.writeUTF(IP_ASSIGNMENT_KEY);
                out.writeUTF(config.ipAssignment.toString());
                written = true;
            } else if (i != 3) {
                loge("Ignore invalid ip assignment while writing");
            }
            int i2 = AnonymousClass1.$SwitchMap$android$net$IpConfiguration$ProxySettings[config.proxySettings.ordinal()];
            if (i2 == 1) {
                ProxyInfo proxyPacProperties = config.httpProxy;
                String exclusionList = proxyPacProperties.getExclusionListAsString();
                out.writeUTF(PROXY_SETTINGS_KEY);
                out.writeUTF(config.proxySettings.toString());
                out.writeUTF(PROXY_HOST_KEY);
                out.writeUTF(proxyPacProperties.getHost());
                out.writeUTF(PROXY_PORT_KEY);
                out.writeInt(proxyPacProperties.getPort());
                if (exclusionList != null) {
                    out.writeUTF(EXCLUSION_LIST_KEY);
                    out.writeUTF(exclusionList);
                }
                written = true;
            } else if (i2 == 2) {
                ProxyInfo proxyPacProperties2 = config.httpProxy;
                out.writeUTF(PROXY_SETTINGS_KEY);
                out.writeUTF(config.proxySettings.toString());
                out.writeUTF(PROXY_PAC_FILE);
                out.writeUTF(proxyPacProperties2.getPacFileUrl().toString());
                written = true;
            } else if (i2 == 3) {
                out.writeUTF(PROXY_SETTINGS_KEY);
                out.writeUTF(config.proxySettings.toString());
                written = true;
            } else if (i2 != 4) {
                loge("Ignore invalid proxy settings while writing");
            }
            if (written) {
                out.writeUTF(ID_KEY);
                if (version < 3) {
                    out.writeInt(Integer.valueOf(configKey).intValue());
                } else {
                    out.writeUTF(configKey);
                }
            }
        } catch (NullPointerException e) {
            loge("Failure in writing " + config + e);
        }
        out.writeUTF(EOS);
        return written;
    }

    /* renamed from: com.android.server.net.IpConfigStore$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$net$IpConfiguration$IpAssignment = new int[IpConfiguration.IpAssignment.values().length];
        static final /* synthetic */ int[] $SwitchMap$android$net$IpConfiguration$ProxySettings = new int[IpConfiguration.ProxySettings.values().length];

        static {
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.STATIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.PAC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.UNASSIGNED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$IpAssignment[IpConfiguration.IpAssignment.STATIC.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$IpAssignment[IpConfiguration.IpAssignment.DHCP.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$IpAssignment[IpConfiguration.IpAssignment.UNASSIGNED.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    @Deprecated
    public void writeIpAndProxyConfigurationsToFile(String filePath, SparseArray<IpConfiguration> networks) {
        this.mWriter.write(filePath, new DelayedDiskWrite.Writer(networks) {
            private final /* synthetic */ SparseArray f$0;

            {
                this.f$0 = r1;
            }

            public final void onWriteCalled(DataOutputStream dataOutputStream) {
                IpConfigStore.lambda$writeIpAndProxyConfigurationsToFile$0(this.f$0, dataOutputStream);
            }
        });
    }

    static /* synthetic */ void lambda$writeIpAndProxyConfigurationsToFile$0(SparseArray networks, DataOutputStream out) throws IOException {
        out.writeInt(3);
        for (int i = 0; i < networks.size(); i++) {
            writeConfig(out, String.valueOf(networks.keyAt(i)), (IpConfiguration) networks.valueAt(i));
        }
    }

    public void writeIpConfigurations(String filePath, ArrayMap<String, IpConfiguration> networks) {
        this.mWriter.write(filePath, new DelayedDiskWrite.Writer(networks) {
            private final /* synthetic */ ArrayMap f$0;

            {
                this.f$0 = r1;
            }

            public final void onWriteCalled(DataOutputStream dataOutputStream) {
                IpConfigStore.lambda$writeIpConfigurations$1(this.f$0, dataOutputStream);
            }
        });
    }

    static /* synthetic */ void lambda$writeIpConfigurations$1(ArrayMap networks, DataOutputStream out) throws IOException {
        out.writeInt(3);
        for (int i = 0; i < networks.size(); i++) {
            writeConfig(out, (String) networks.keyAt(i), (IpConfiguration) networks.valueAt(i));
        }
    }

    public static ArrayMap<String, IpConfiguration> readIpConfigurations(String filePath) {
        try {
            return readIpConfigurations((InputStream) new BufferedInputStream(new FileInputStream(filePath)));
        } catch (FileNotFoundException e) {
            loge("Error opening configuration file: " + e);
            return new ArrayMap<>(0);
        }
    }

    @Deprecated
    public static SparseArray<IpConfiguration> readIpAndProxyConfigurations(String filePath) {
        try {
            return readIpAndProxyConfigurations((InputStream) new BufferedInputStream(new FileInputStream(filePath)));
        } catch (FileNotFoundException e) {
            loge("Error opening configuration file: " + e);
            return new SparseArray<>();
        }
    }

    @Deprecated
    public static SparseArray<IpConfiguration> readIpAndProxyConfigurations(InputStream inputStream) {
        ArrayMap<String, IpConfiguration> networks = readIpConfigurations(inputStream);
        if (networks == null) {
            return null;
        }
        SparseArray<IpConfiguration> networksById = new SparseArray<>();
        for (int i = 0; i < networks.size(); i++) {
            networksById.put(Integer.valueOf(networks.keyAt(i)).intValue(), networks.valueAt(i));
        }
        return networksById;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r18v8 */
    /* JADX WARNING: type inference failed for: r18v23 */
    /* JADX WARNING: type inference failed for: r18v25 */
    /* JADX WARNING: type inference failed for: r18v27 */
    /* JADX WARNING: type inference failed for: r18v29 */
    /* JADX WARNING: type inference failed for: r18v31 */
    /* JADX WARNING: type inference failed for: r18v33 */
    /* JADX WARNING: type inference failed for: r18v34 */
    /* JADX WARNING: type inference failed for: r18v41 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x02d5 A[SYNTHETIC, Splitter:B:166:0x02d5] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.util.ArrayMap<java.lang.String, android.net.IpConfiguration> readIpConfigurations(java.io.InputStream r20) {
        /*
            android.util.ArrayMap r1 = new android.util.ArrayMap
            r1.<init>()
            r2 = 0
            java.io.DataInputStream r3 = new java.io.DataInputStream     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4 = r20
            r3.<init>(r4)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r2 = r3
            int r3 = r2.readInt()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r5 = 0
            r6 = 2
            r7 = 3
            r8 = 1
            if (r3 == r7) goto L_0x002a
            if (r3 == r6) goto L_0x002a
            if (r3 == r8) goto L_0x002a
            java.lang.String r6 = "Bad version on IP configuration file, ignore read"
            loge(r6)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r2.close()     // Catch:{ Exception -> 0x0027 }
        L_0x0026:
            goto L_0x0029
        L_0x0027:
            r0 = move-exception
            goto L_0x0026
        L_0x0029:
            return r5
        L_0x002a:
            r9 = 0
            android.net.IpConfiguration$IpAssignment r10 = android.net.IpConfiguration.IpAssignment.DHCP     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            android.net.IpConfiguration$ProxySettings r11 = android.net.IpConfiguration.ProxySettings.NONE     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            android.net.StaticIpConfiguration r12 = new android.net.StaticIpConfiguration     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r12.<init>()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r13 = 0
            r14 = 0
            r15 = -1
            r16 = r5
        L_0x0039:
            java.lang.String r17 = r2.readUTF()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r18 = r17
            java.lang.String r6 = "id"
            r8 = r18
            boolean r6 = r8.equals(r6)     // Catch:{ IllegalArgumentException -> 0x0284 }
            if (r6 == 0) goto L_0x0066
            if (r3 >= r7) goto L_0x005c
            int r6 = r2.readInt()     // Catch:{ IllegalArgumentException -> 0x0284 }
            java.lang.String r18 = java.lang.String.valueOf(r6)     // Catch:{ IllegalArgumentException -> 0x0284 }
            r6 = r18
            r18 = r3
            r9 = r6
            r7 = 2
            goto L_0x0277
        L_0x005c:
            java.lang.String r6 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x0284 }
            r18 = r3
            r9 = r6
            r7 = 2
            goto L_0x0277
        L_0x0066:
            java.lang.String r6 = "ipAssignment"
            boolean r6 = r8.equals(r6)     // Catch:{ IllegalArgumentException -> 0x0284 }
            if (r6 == 0) goto L_0x007d
            java.lang.String r6 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x0284 }
            android.net.IpConfiguration$IpAssignment r6 = android.net.IpConfiguration.IpAssignment.valueOf(r6)     // Catch:{ IllegalArgumentException -> 0x0284 }
            r18 = r3
            r10 = r6
            r7 = 2
            goto L_0x0277
        L_0x007d:
            java.lang.String r6 = "linkAddress"
            boolean r6 = r8.equals(r6)     // Catch:{ IllegalArgumentException -> 0x0284 }
            if (r6 == 0) goto L_0x00c0
            android.net.LinkAddress r6 = new android.net.LinkAddress     // Catch:{ IllegalArgumentException -> 0x0284 }
            java.lang.String r18 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x0284 }
            java.net.InetAddress r7 = android.net.NetworkUtils.numericToInetAddress(r18)     // Catch:{ IllegalArgumentException -> 0x0284 }
            int r4 = r2.readInt()     // Catch:{ IllegalArgumentException -> 0x0284 }
            r6.<init>(r7, r4)     // Catch:{ IllegalArgumentException -> 0x0284 }
            r4 = r6
            java.net.InetAddress r6 = r4.getAddress()     // Catch:{ IllegalArgumentException -> 0x0284 }
            boolean r6 = r6 instanceof java.net.Inet4Address     // Catch:{ IllegalArgumentException -> 0x0284 }
            if (r6 == 0) goto L_0x00a7
            android.net.LinkAddress r6 = r12.ipAddress     // Catch:{ IllegalArgumentException -> 0x0284 }
            if (r6 != 0) goto L_0x00a7
            r12.ipAddress = r4     // Catch:{ IllegalArgumentException -> 0x0284 }
            goto L_0x00bb
        L_0x00a7:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0284 }
            r6.<init>()     // Catch:{ IllegalArgumentException -> 0x0284 }
            java.lang.String r7 = "Non-IPv4 or duplicate address: "
            r6.append(r7)     // Catch:{ IllegalArgumentException -> 0x0284 }
            r6.append(r4)     // Catch:{ IllegalArgumentException -> 0x0284 }
            java.lang.String r6 = r6.toString()     // Catch:{ IllegalArgumentException -> 0x0284 }
            loge(r6)     // Catch:{ IllegalArgumentException -> 0x0284 }
        L_0x00bb:
            r18 = r3
            r7 = 2
            goto L_0x0277
        L_0x00c0:
            java.lang.String r4 = "gateway"
            boolean r4 = r8.equals(r4)     // Catch:{ IllegalArgumentException -> 0x0284 }
            if (r4 == 0) goto L_0x015f
            r4 = 0
            r6 = 0
            r7 = 1
            if (r3 != r7) goto L_0x00ff
            java.lang.String r7 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x00fb }
            java.net.InetAddress r7 = android.net.NetworkUtils.numericToInetAddress(r7)     // Catch:{ IllegalArgumentException -> 0x00fb }
            r6 = r7
            java.net.InetAddress r7 = r12.gateway     // Catch:{ IllegalArgumentException -> 0x00fb }
            if (r7 != 0) goto L_0x00e0
            r12.gateway = r6     // Catch:{ IllegalArgumentException -> 0x0284 }
            r18 = r3
            goto L_0x0157
        L_0x00e0:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x00fb }
            r7.<init>()     // Catch:{ IllegalArgumentException -> 0x00fb }
            r18 = r3
            java.lang.String r3 = "Duplicate gateway: "
            r7.append(r3)     // Catch:{ IllegalArgumentException -> 0x015a }
            java.lang.String r3 = r6.getHostAddress()     // Catch:{ IllegalArgumentException -> 0x015a }
            r7.append(r3)     // Catch:{ IllegalArgumentException -> 0x015a }
            java.lang.String r3 = r7.toString()     // Catch:{ IllegalArgumentException -> 0x015a }
            loge(r3)     // Catch:{ IllegalArgumentException -> 0x015a }
            goto L_0x0157
        L_0x00fb:
            r0 = move-exception
            r18 = r3
            goto L_0x015b
        L_0x00ff:
            r18 = r3
            int r3 = r2.readInt()     // Catch:{ IllegalArgumentException -> 0x015a }
            r7 = 1
            if (r3 != r7) goto L_0x011d
            android.net.LinkAddress r3 = new android.net.LinkAddress     // Catch:{ IllegalArgumentException -> 0x015a }
            java.lang.String r7 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x015a }
            java.net.InetAddress r7 = android.net.NetworkUtils.numericToInetAddress(r7)     // Catch:{ IllegalArgumentException -> 0x015a }
            r19 = r4
            int r4 = r2.readInt()     // Catch:{ IllegalArgumentException -> 0x015a }
            r3.<init>(r7, r4)     // Catch:{ IllegalArgumentException -> 0x015a }
            r4 = r3
            goto L_0x011f
        L_0x011d:
            r19 = r4
        L_0x011f:
            int r3 = r2.readInt()     // Catch:{ IllegalArgumentException -> 0x015a }
            r7 = 1
            if (r3 != r7) goto L_0x012f
            java.lang.String r3 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x015a }
            java.net.InetAddress r3 = android.net.NetworkUtils.numericToInetAddress(r3)     // Catch:{ IllegalArgumentException -> 0x015a }
            r6 = r3
        L_0x012f:
            android.net.RouteInfo r3 = new android.net.RouteInfo     // Catch:{ IllegalArgumentException -> 0x015a }
            r3.<init>(r4, r6)     // Catch:{ IllegalArgumentException -> 0x015a }
            boolean r7 = r3.isIPv4Default()     // Catch:{ IllegalArgumentException -> 0x015a }
            if (r7 == 0) goto L_0x0141
            java.net.InetAddress r7 = r12.gateway     // Catch:{ IllegalArgumentException -> 0x015a }
            if (r7 != 0) goto L_0x0141
            r12.gateway = r6     // Catch:{ IllegalArgumentException -> 0x015a }
            goto L_0x0157
        L_0x0141:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x015a }
            r7.<init>()     // Catch:{ IllegalArgumentException -> 0x015a }
            r19 = r4
            java.lang.String r4 = "Non-IPv4 default or duplicate route: "
            r7.append(r4)     // Catch:{ IllegalArgumentException -> 0x015a }
            r7.append(r3)     // Catch:{ IllegalArgumentException -> 0x015a }
            java.lang.String r4 = r7.toString()     // Catch:{ IllegalArgumentException -> 0x015a }
            loge(r4)     // Catch:{ IllegalArgumentException -> 0x015a }
        L_0x0157:
            r7 = 2
            goto L_0x0277
        L_0x015a:
            r0 = move-exception
        L_0x015b:
            r3 = r0
            r7 = 2
            goto L_0x0291
        L_0x015f:
            r18 = r3
            java.lang.String r3 = "dns"
            boolean r3 = r8.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0282 }
            if (r3 == 0) goto L_0x0179
            java.util.ArrayList r3 = r12.dnsServers     // Catch:{ IllegalArgumentException -> 0x015a }
            java.lang.String r4 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x015a }
            java.net.InetAddress r4 = android.net.NetworkUtils.numericToInetAddress(r4)     // Catch:{ IllegalArgumentException -> 0x015a }
            r3.add(r4)     // Catch:{ IllegalArgumentException -> 0x015a }
            r7 = 2
            goto L_0x0277
        L_0x0179:
            java.lang.String r3 = "proxySettings"
            boolean r3 = r8.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0282 }
            if (r3 == 0) goto L_0x018e
            java.lang.String r3 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x015a }
            android.net.IpConfiguration$ProxySettings r3 = android.net.IpConfiguration.ProxySettings.valueOf(r3)     // Catch:{ IllegalArgumentException -> 0x015a }
            r11 = r3
            r7 = 2
            goto L_0x0277
        L_0x018e:
            java.lang.String r3 = "proxyHost"
            boolean r3 = r8.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0282 }
            if (r3 == 0) goto L_0x019f
            java.lang.String r3 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x015a }
            r13 = r3
            r7 = 2
            goto L_0x0277
        L_0x019f:
            java.lang.String r3 = "proxyPort"
            boolean r3 = r8.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0282 }
            if (r3 == 0) goto L_0x01b0
            int r3 = r2.readInt()     // Catch:{ IllegalArgumentException -> 0x015a }
            r15 = r3
            r7 = 2
            goto L_0x0277
        L_0x01b0:
            java.lang.String r3 = "proxyPac"
            boolean r3 = r8.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0282 }
            if (r3 == 0) goto L_0x01c1
            java.lang.String r3 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x015a }
            r14 = r3
            r7 = 2
            goto L_0x0277
        L_0x01c1:
            java.lang.String r3 = "exclusionList"
            boolean r3 = r8.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0282 }
            if (r3 == 0) goto L_0x01d1
            java.lang.String r3 = r2.readUTF()     // Catch:{ IllegalArgumentException -> 0x015a }
            r5 = r3
            r7 = 2
            goto L_0x0277
        L_0x01d1:
            java.lang.String r3 = "eos"
            boolean r3 = r8.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0282 }
            if (r3 == 0) goto L_0x025c
            if (r9 == 0) goto L_0x0251
            android.net.IpConfiguration r3 = new android.net.IpConfiguration     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.<init>()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r1.put(r9, r3)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            int[] r4 = com.android.server.net.IpConfigStore.AnonymousClass1.$SwitchMap$android$net$IpConfiguration$IpAssignment     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            int r6 = r10.ordinal()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4 = r4[r6]     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r6 = 1
            if (r4 == r6) goto L_0x020c
            r6 = 2
            if (r4 == r6) goto L_0x0209
            r6 = 3
            if (r4 == r6) goto L_0x01ff
            java.lang.String r4 = "Ignore invalid ip assignment while reading."
            loge(r4)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            android.net.IpConfiguration$IpAssignment r4 = android.net.IpConfiguration.IpAssignment.UNASSIGNED     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.ipAssignment = r4     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0211
        L_0x01ff:
            java.lang.String r4 = "BUG: Found UNASSIGNED IP on file, use DHCP"
            loge(r4)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            android.net.IpConfiguration$IpAssignment r4 = android.net.IpConfiguration.IpAssignment.DHCP     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.ipAssignment = r4     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0211
        L_0x0209:
            r3.ipAssignment = r10     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0211
        L_0x020c:
            r3.staticIpConfiguration = r12     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.ipAssignment = r10     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
        L_0x0211:
            int[] r4 = com.android.server.net.IpConfigStore.AnonymousClass1.$SwitchMap$android$net$IpConfiguration$ProxySettings     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            int r6 = r11.ordinal()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4 = r4[r6]     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r6 = 1
            if (r4 == r6) goto L_0x0246
            r7 = 2
            if (r4 == r7) goto L_0x023c
            r6 = 3
            if (r4 == r6) goto L_0x0239
            r6 = 4
            if (r4 == r6) goto L_0x022f
            java.lang.String r4 = "Ignore invalid proxy settings while reading"
            loge(r4)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            android.net.IpConfiguration$ProxySettings r4 = android.net.IpConfiguration.ProxySettings.UNASSIGNED     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.proxySettings = r4     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0252
        L_0x022f:
            java.lang.String r4 = "BUG: Found UNASSIGNED proxy on file, use NONE"
            loge(r4)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            android.net.IpConfiguration$ProxySettings r4 = android.net.IpConfiguration.ProxySettings.NONE     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.proxySettings = r4     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0252
        L_0x0239:
            r3.proxySettings = r11     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0252
        L_0x023c:
            android.net.ProxyInfo r4 = new android.net.ProxyInfo     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4.<init>(r14)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.proxySettings = r11     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.httpProxy = r4     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0252
        L_0x0246:
            r7 = 2
            android.net.ProxyInfo r4 = new android.net.ProxyInfo     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4.<init>(r13, r15, r5)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.proxySettings = r11     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r3.httpProxy = r4     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            goto L_0x0252
        L_0x0251:
            r7 = 2
        L_0x0252:
            r5 = 0
            r8 = 1
            r4 = r20
            r6 = r7
            r3 = r18
            r7 = 3
            goto L_0x002a
        L_0x025c:
            r7 = 2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0280 }
            r3.<init>()     // Catch:{ IllegalArgumentException -> 0x0280 }
            java.lang.String r4 = "Ignore unknown key "
            r3.append(r4)     // Catch:{ IllegalArgumentException -> 0x0280 }
            r3.append(r8)     // Catch:{ IllegalArgumentException -> 0x0280 }
            java.lang.String r4 = "while reading"
            r3.append(r4)     // Catch:{ IllegalArgumentException -> 0x0280 }
            java.lang.String r3 = r3.toString()     // Catch:{ IllegalArgumentException -> 0x0280 }
            loge(r3)     // Catch:{ IllegalArgumentException -> 0x0280 }
        L_0x0277:
            r4 = r20
            r6 = r7
            r3 = r18
            r7 = 3
            r8 = 1
            goto L_0x0039
        L_0x0280:
            r0 = move-exception
            goto L_0x0288
        L_0x0282:
            r0 = move-exception
            goto L_0x0287
        L_0x0284:
            r0 = move-exception
            r18 = r3
        L_0x0287:
            r7 = 2
        L_0x0288:
            r3 = r0
            goto L_0x0291
        L_0x028a:
            r0 = move-exception
            r8 = r18
            r7 = 2
            r18 = r3
            r3 = r0
        L_0x0291:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4.<init>()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            java.lang.String r6 = "Ignore invalid address while reading"
            r4.append(r6)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4.append(r3)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            java.lang.String r4 = r4.toString()     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            loge(r4)     // Catch:{ EOFException -> 0x02dc, IOException -> 0x02b2, all -> 0x02ae }
            r4 = r20
            r6 = r7
            r3 = r18
            r7 = 3
            r8 = 1
            goto L_0x0039
        L_0x02ae:
            r0 = move-exception
            r3 = r2
            r2 = r0
            goto L_0x02d3
        L_0x02b2:
            r0 = move-exception
            r3 = r2
            r2 = r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x02d1 }
            r4.<init>()     // Catch:{ all -> 0x02d1 }
            java.lang.String r5 = "Error parsing configuration: "
            r4.append(r5)     // Catch:{ all -> 0x02d1 }
            r4.append(r2)     // Catch:{ all -> 0x02d1 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x02d1 }
            loge(r4)     // Catch:{ all -> 0x02d1 }
            if (r3 == 0) goto L_0x02e4
            r3.close()     // Catch:{ Exception -> 0x02cf }
        L_0x02ce:
            goto L_0x02e4
        L_0x02cf:
            r0 = move-exception
            goto L_0x02ce
        L_0x02d1:
            r0 = move-exception
            r2 = r0
        L_0x02d3:
            if (r3 == 0) goto L_0x02db
            r3.close()     // Catch:{ Exception -> 0x02d9 }
        L_0x02d8:
            goto L_0x02db
        L_0x02d9:
            r0 = move-exception
            goto L_0x02d8
        L_0x02db:
            throw r2
        L_0x02dc:
            r0 = move-exception
            r3 = r2
            if (r3 == 0) goto L_0x02e4
            r3.close()     // Catch:{ Exception -> 0x02cf }
            goto L_0x02ce
        L_0x02e4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.IpConfigStore.readIpConfigurations(java.io.InputStream):android.util.ArrayMap");
    }

    protected static void loge(String s) {
        Log.e(TAG, s);
    }

    protected static void log(String s) {
        Log.d(TAG, s);
    }
}
