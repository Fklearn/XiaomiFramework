package miui.drm;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import miui.content.res.ThemeResources;
import miui.telephony.TelephonyManagerUtil;
import miui.telephony.phonenumber.Prefix;
import miui.util.HashUtils;
import miui.util.RSAUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DrmManager {
    private static final String ASSET_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:asset/o-ex:context/o-dd:uid";
    private static final String DISPLAY_COUNT_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:display/o-ex:constraint/o-dd:count";
    private static final String IMEI_EVERYONE = "-1";
    private static final String IMEI_PREFIX = "d";
    private static final String INDIVIDUAL_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:execute/o-ex:constraint/oma-dd:individual/o-ex:context/o-dd:uid";
    private static final String ITEM_SEPARATOR = ",";
    private static final String O_EX_ID_CATEGORY = "o-ex:id";
    private static final String PAIR_SEPARATOR = ":";
    private static final String PUBLIC_KEY_E = "10001";
    private static final String PUBLIC_KEY_M = "a2ebd07cfae9a72345fc3c95d80cf5a21a55bf553fbab3025c82747ba4d53d1f9b02f46c20b5520585a910732698b165f0ecf7bd9ce5402e27c646cd0c5d34cff92b184d6a477e156a7d3503b756cc3e8531fb26c0da0ca051ab531c7f9f2a040a06642cadb698882c048630030b73edbbd62da73f7027065443c6e2558edfbd";
    private static final String SUPPORT_AD = "support_ad";
    public static final String TAG = "drm";
    private static final String TIME_END_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:execute/o-ex:constraint/o-dd:datetime/o-dd:end";
    private static final String TIME_START_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:execute/o-ex:constraint/o-dd:datetime/o-dd:start";
    private static final String USER_EVERYONE = "-1";
    private static final String USER_PREFIX = "m";
    private static Map<String, RightObjectCache> mRightsCache = new LinkedHashMap<String, RightObjectCache>(0, 0.75f, true) {
        private static final long serialVersionUID = 1;

        /* access modifiers changed from: protected */
        public boolean removeEldestEntry(Map.Entry<String, RightObjectCache> entry) {
            return size() > 50;
        }
    };

    public enum DrmResult {
        DRM_SUCCESS,
        DRM_ERROR_IMEI_NOT_MATCH,
        DRM_ERROR_ASSET_NOT_MATCH,
        DRM_ERROR_TIME_NOT_MATCH,
        DRM_ERROR_RIGHT_OBJECT_IS_NULL,
        DRM_ERROR_RIGHT_FILE_NOT_EXISTS,
        DRM_ERROR_UNKNOWN
    }

    private static class RightObjectCache {
        public long lastModified;
        public RightObject ro;

        private RightObjectCache() {
        }
    }

    private static class RightObject {
        public boolean adSupport;
        public List<String> assets;
        public long endTime;
        public List<String> imeis;
        public long startTime;
        public List<String> users;

        private RightObject() {
            this.assets = new ArrayList();
            this.imeis = new ArrayList();
            this.users = new ArrayList();
        }
    }

    public static class TrialLimits {
        public long endTime;
        public long startTime;

        TrialLimits(long startTime2, long endTime2) {
            this.startTime = startTime2;
            this.endTime = endTime2;
        }
    }

    private static class DrmNSContext implements NamespaceContext {
        private DrmNSContext() {
        }

        public String getNamespaceURI(String prefix) {
            if (prefix.equals("o-ex")) {
                return "http://odrl.net/1.1/ODRL-EX";
            }
            if (prefix.equals("o-dd")) {
                return "http://odrl.net/1.1/ODRL-DD";
            }
            if (prefix.equals("oma-dd")) {
                return "http://www.openmobilealliance.com/oma-dd";
            }
            return null;
        }

        public Iterator getPrefixes(String val) {
            return null;
        }

        public String getPrefix(String uri) {
            return null;
        }
    }

    public static DrmResult getMorePreciseDrmResult(DrmResult r1, DrmResult r2) {
        if ((r1 != DrmResult.DRM_ERROR_TIME_NOT_MATCH || r2 == DrmResult.DRM_SUCCESS) && (r1 == DrmResult.DRM_SUCCESS || r2 != DrmResult.DRM_ERROR_TIME_NOT_MATCH)) {
            return r1.compareTo(r2) < 0 ? r1 : r2;
        }
        return DrmResult.DRM_ERROR_TIME_NOT_MATCH;
    }

    public static boolean isSupportAd(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SUPPORT_AD, 0) > 0;
    }

    public static void setSupportAd(Context context, boolean support) {
        Settings.System.putInt(context.getContentResolver(), SUPPORT_AD, support);
    }

    public static boolean isSupportAd(File rightsFile) {
        if (!rightsFile.exists() || !rightsFile.isFile()) {
            return false;
        }
        return parseRightsFile(rightsFile).adSupport;
    }

    public static DrmResult isLegal(Context context, File contentFile, File rightsFile) {
        return isLegal(context, HashUtils.getSHA1(contentFile), rightsFile);
    }

    public static DrmResult isLegal(Context context, String hash, File rightsFile) {
        if (!rightsFile.exists() || (rightsFile.isDirectory() && rightsFile.listFiles() == null)) {
            return DrmResult.DRM_ERROR_RIGHT_FILE_NOT_EXISTS;
        }
        DrmResult ret = DrmResult.DRM_ERROR_UNKNOWN;
        if (rightsFile.isDirectory()) {
            for (File file : rightsFile.listFiles()) {
                Log.d(TAG, "checking asset " + hash + " with " + file.getAbsolutePath());
                DrmResult tempRet = isLegal(context, hash, parseRightsFile(file));
                if (tempRet == DrmResult.DRM_SUCCESS) {
                    return DrmResult.DRM_SUCCESS;
                }
                ret = getMorePreciseDrmResult(ret, tempRet);
            }
            return ret;
        }
        Log.d(TAG, "checking asset " + hash + " with " + rightsFile.getAbsolutePath());
        return isLegal(context, hash, parseRightsFile(rightsFile));
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00a3 A[EDGE_INSN: B:58:0x00a3->B:34:0x00a3 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static miui.drm.DrmManager.DrmResult isLegal(android.content.Context r9, java.lang.String r10, miui.drm.DrmManager.RightObject r11) {
        /*
            if (r11 != 0) goto L_0x0005
            miui.drm.DrmManager$DrmResult r0 = miui.drm.DrmManager.DrmResult.DRM_ERROR_RIGHT_OBJECT_IS_NULL
            return r0
        L_0x0005:
            r0 = 0
            java.util.List<java.lang.String> r1 = r11.assets
            java.util.Iterator r1 = r1.iterator()
        L_0x000c:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0021
            java.lang.Object r2 = r1.next()
            java.lang.String r2 = (java.lang.String) r2
            boolean r3 = r2.equals(r10)
            if (r3 == 0) goto L_0x0020
            r0 = 1
            goto L_0x0021
        L_0x0020:
            goto L_0x000c
        L_0x0021:
            java.lang.String r1 = "drm"
            if (r0 != 0) goto L_0x003c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "right object has no definition for asset "
            r2.append(r3)
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            exportFatalLog(r1, r2)
            miui.drm.DrmManager$DrmResult r1 = miui.drm.DrmManager.DrmResult.DRM_ERROR_ASSET_NOT_MATCH
            return r1
        L_0x003c:
            java.util.List<java.lang.String> r2 = r11.imeis
            int r2 = r2.size()
            if (r2 != 0) goto L_0x004a
            java.lang.String r2 = "right object does not have any imeis"
            android.util.Log.d(r1, r2)
            goto L_0x00ad
        L_0x004a:
            java.lang.String r2 = getOriginImei(r9)
            java.lang.String r3 = getEncodedImei(r9)
            java.lang.String r4 = getVAID(r9)
            boolean r5 = android.text.TextUtils.isEmpty(r2)
            if (r5 == 0) goto L_0x0062
            java.lang.String r5 = "the imei retrieved is empty"
            exportFatalLog(r1, r5)
            goto L_0x006d
        L_0x0062:
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x006d
            java.lang.String r5 = "the imei encoded is empty"
            exportFatalLog(r1, r5)
        L_0x006d:
            r5 = 0
            java.util.List<java.lang.String> r6 = r11.imeis
            java.util.Iterator r6 = r6.iterator()
        L_0x0074:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x00a3
            java.lang.Object r7 = r6.next()
            java.lang.String r7 = (java.lang.String) r7
            boolean r8 = r7.equals(r2)
            if (r8 != 0) goto L_0x009c
            boolean r8 = r7.equals(r3)
            if (r8 != 0) goto L_0x009c
            boolean r8 = r7.equals(r4)
            if (r8 != 0) goto L_0x009c
            java.lang.String r8 = "-1"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x009b
            goto L_0x009c
        L_0x009b:
            goto L_0x0074
        L_0x009c:
            java.lang.String r6 = "right object has matched imei"
            android.util.Log.d(r1, r6)
            r5 = 1
        L_0x00a3:
            if (r5 != 0) goto L_0x00ad
            java.lang.String r6 = "right object does not have matched imei"
            exportFatalLog(r1, r6)
            miui.drm.DrmManager$DrmResult r1 = miui.drm.DrmManager.DrmResult.DRM_ERROR_IMEI_NOT_MATCH
            return r1
        L_0x00ad:
            long r1 = r11.startTime
            r3 = 0
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 < 0) goto L_0x00d8
            long r1 = r11.endTime
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x00bc
            goto L_0x00d8
        L_0x00bc:
            long r1 = r11.endTime
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x00d5
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = r11.startTime
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x00d2
            long r3 = r11.endTime
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x00d5
        L_0x00d2:
            miui.drm.DrmManager$DrmResult r3 = miui.drm.DrmManager.DrmResult.DRM_ERROR_TIME_NOT_MATCH
            return r3
        L_0x00d5:
            miui.drm.DrmManager$DrmResult r1 = miui.drm.DrmManager.DrmResult.DRM_SUCCESS
            return r1
        L_0x00d8:
            miui.drm.DrmManager$DrmResult r1 = miui.drm.DrmManager.DrmResult.DRM_ERROR_TIME_NOT_MATCH
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.drm.DrmManager.isLegal(android.content.Context, java.lang.String, miui.drm.DrmManager$RightObject):miui.drm.DrmManager$DrmResult");
    }

    public static boolean isPermanentRights(File rightsFile) {
        return isPermanentRights(parseRightsFile(rightsFile));
    }

    private static boolean isPermanentRights(RightObject ro) {
        return ro != null && ro.startTime == 0 && ro.endTime == 0;
    }

    private static RightObject parseRightsFile(File file) {
        boolean z;
        NodeList assetList;
        boolean z2;
        String path = file.getAbsolutePath();
        RightObjectCache cache = mRightsCache.get(path);
        if (cache != null && file.lastModified() == cache.lastModified) {
            return cache.ro;
        }
        if (!isRightsFileLegal(file)) {
            return null;
        }
        RightObject ro = new RightObject();
        RightObjectCache cache2 = new RightObjectCache();
        cache2.ro = ro;
        cache2.lastModified = file.lastModified();
        mRightsCache.put(path, cache2);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            boolean z3 = true;
            factory.setNamespaceAware(true);
            try {
                Document document = factory.newDocumentBuilder().parse(file);
                XPath xPath = XPathFactory.newInstance().newXPath();
                xPath.setNamespaceContext(new DrmNSContext());
                NodeList assetList2 = (NodeList) xPath.evaluate(ASSET_XPATH, document, XPathConstants.NODESET);
                if (assetList2 == null || assetList2.getLength() <= 0) {
                    RightObjectCache rightObjectCache = cache2;
                    NodeList nodeList = assetList2;
                    z = true;
                } else {
                    int i = 0;
                    while (i < assetList2.getLength()) {
                        String[] pair = ((Element) assetList2.item(i)).getTextContent().split(PAIR_SEPARATOR);
                        String path2 = path;
                        RightObjectCache cache3 = cache2;
                        if (pair.length == 1) {
                            try {
                                assetList = assetList2;
                                ro.assets.addAll(Arrays.asList(pair[0].split(ITEM_SEPARATOR)));
                                z2 = true;
                            } catch (ParserConfigurationException e) {
                                e = e;
                                e.printStackTrace();
                                return ro;
                            } catch (SAXException e2) {
                                e = e2;
                                e.printStackTrace();
                                return ro;
                            } catch (IOException e3) {
                                e = e3;
                                e.printStackTrace();
                                return ro;
                            } catch (XPathExpressionException e4) {
                                e = e4;
                                e.printStackTrace();
                                return ro;
                            } catch (NumberFormatException e5) {
                                e = e5;
                                e.printStackTrace();
                                return ro;
                            }
                        } else {
                            assetList = assetList2;
                            if (pair.length == 2) {
                                ro.assets.add(pair[0]);
                                z2 = true;
                                ro.assets.addAll(Arrays.asList(pair[1].split(ITEM_SEPARATOR)));
                            } else {
                                z2 = true;
                            }
                        }
                        i++;
                        z3 = z2;
                        path = path2;
                        cache2 = cache3;
                        assetList2 = assetList;
                    }
                    RightObjectCache rightObjectCache2 = cache2;
                    NodeList nodeList2 = assetList2;
                    z = z3;
                }
                NodeList individualList = (NodeList) xPath.evaluate(INDIVIDUAL_XPATH, document, XPathConstants.NODESET);
                if (individualList != null && individualList.getLength() > 0) {
                    for (int i2 = 0; i2 < individualList.getLength(); i2++) {
                        String content = ((Element) individualList.item(i2)).getTextContent();
                        if (content.startsWith("d")) {
                            ro.imeis.add(content.substring("d".length()));
                        } else if (content.startsWith(USER_PREFIX)) {
                            ro.users.add(content.substring(USER_PREFIX.length()));
                        }
                    }
                }
                NodeList timeStartList = (NodeList) xPath.evaluate(TIME_START_XPATH, document, XPathConstants.NODESET);
                if (timeStartList != null && timeStartList.getLength() > 0) {
                    ro.startTime = getTime(((Element) timeStartList.item(0)).getTextContent());
                }
                NodeList timeEndList = (NodeList) xPath.evaluate(TIME_END_XPATH, document, XPathConstants.NODESET);
                if (timeEndList != null && timeEndList.getLength() > 0) {
                    ro.endTime = getTime(((Element) timeEndList.item(0)).getTextContent());
                }
                NodeList displayCountList = (NodeList) xPath.evaluate(DISPLAY_COUNT_XPATH, document, XPathConstants.NODESET);
                if (displayCountList != null && displayCountList.getLength() > 0) {
                    if (Integer.valueOf(((Element) displayCountList.item(0)).getTextContent()).intValue() <= 0) {
                        z = false;
                    }
                    ro.adSupport = z;
                }
            } catch (ParserConfigurationException e6) {
                e = e6;
                String str = path;
                RightObjectCache rightObjectCache3 = cache2;
                e.printStackTrace();
                return ro;
            } catch (SAXException e7) {
                e = e7;
                String str2 = path;
                RightObjectCache rightObjectCache4 = cache2;
                e.printStackTrace();
                return ro;
            } catch (IOException e8) {
                e = e8;
                String str3 = path;
                RightObjectCache rightObjectCache5 = cache2;
                e.printStackTrace();
                return ro;
            } catch (XPathExpressionException e9) {
                e = e9;
                String str4 = path;
                RightObjectCache rightObjectCache6 = cache2;
                e.printStackTrace();
                return ro;
            } catch (NumberFormatException e10) {
                e = e10;
                String str5 = path;
                RightObjectCache rightObjectCache7 = cache2;
                e.printStackTrace();
                return ro;
            }
        } catch (ParserConfigurationException e11) {
            e = e11;
            File file2 = file;
            String str6 = path;
            RightObjectCache rightObjectCache32 = cache2;
            e.printStackTrace();
            return ro;
        } catch (SAXException e12) {
            e = e12;
            File file3 = file;
            String str22 = path;
            RightObjectCache rightObjectCache42 = cache2;
            e.printStackTrace();
            return ro;
        } catch (IOException e13) {
            e = e13;
            File file4 = file;
            String str32 = path;
            RightObjectCache rightObjectCache52 = cache2;
            e.printStackTrace();
            return ro;
        } catch (XPathExpressionException e14) {
            e = e14;
            File file5 = file;
            String str42 = path;
            RightObjectCache rightObjectCache62 = cache2;
            e.printStackTrace();
            return ro;
        } catch (NumberFormatException e15) {
            e = e15;
            File file6 = file;
            String str52 = path;
            RightObjectCache rightObjectCache72 = cache2;
            e.printStackTrace();
            return ro;
        }
        return ro;
    }

    public static TrialLimits getTrialLimits(File rightsFile) {
        RightObject ro = parseRightsFile(rightsFile);
        if (ro != null) {
            return new TrialLimits(ro.startTime, ro.endTime);
        }
        return null;
    }

    private static long getTime(String rfc822) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(rfc822).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static boolean isRightsFileLegal(File file) {
        try {
            try {
                Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getDocumentElement();
                String signaturePart = root.getAttribute(O_EX_ID_CATEGORY);
                NodeList nodes = root.getChildNodes();
                String contentPart = Prefix.EMPTY;
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    contentPart = contentPart + nodeToString(node);
                }
                if (TextUtils.isEmpty(contentPart) != 0 || TextUtils.isEmpty(signaturePart)) {
                    return false;
                }
                byte[] contentBytes = contentPart.getBytes();
                byte[] signatureBytes = convertHexStringToBytes(signaturePart);
                if (RSAUtils.verify(contentBytes, RSAUtils.getPublicKey(PUBLIC_KEY_M, PUBLIC_KEY_E), signatureBytes)) {
                    Log.i(TAG, "standard format rights file verify is legal");
                    return true;
                }
                boolean isLegal = RSAUtils.verify(contentPart.replaceAll("/>", " />").getBytes(), RSAUtils.getPublicKey(PUBLIC_KEY_M, PUBLIC_KEY_E), signatureBytes);
                Log.i(TAG, "old format rights file verify result : " + isLegal);
                return isLegal;
            } catch (ParserConfigurationException e) {
                e = e;
                e.printStackTrace();
                return false;
            } catch (SAXException e2) {
                e = e2;
                e.printStackTrace();
                return false;
            } catch (IOException e3) {
                e = e3;
                e.printStackTrace();
                return false;
            } catch (XPathExpressionException e4) {
                e = e4;
                e.printStackTrace();
                return false;
            } catch (NumberFormatException e5) {
                e = e5;
                e.printStackTrace();
                return false;
            } catch (Exception e6) {
                e = e6;
                e.printStackTrace();
                return false;
            }
        } catch (ParserConfigurationException e7) {
            e = e7;
            File file2 = file;
            e.printStackTrace();
            return false;
        } catch (SAXException e8) {
            e = e8;
            File file3 = file;
            e.printStackTrace();
            return false;
        } catch (IOException e9) {
            e = e9;
            File file4 = file;
            e.printStackTrace();
            return false;
        } catch (XPathExpressionException e10) {
            e = e10;
            File file5 = file;
            e.printStackTrace();
            return false;
        } catch (NumberFormatException e11) {
            e = e11;
            File file6 = file;
            e.printStackTrace();
            return false;
        } catch (Exception e12) {
            e = e12;
            File file7 = file;
            e.printStackTrace();
            return false;
        }
    }

    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty("omit-xml-declaration", "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    private static byte[] convertHexStringToBytes(String hex) {
        byte[] data = new byte[(hex.length() / 2)];
        for (int i = 0; i < hex.length(); i += 2) {
            data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return data;
    }

    public static void exportFatalLog(String tag, String message) {
        Log.e(tag, message);
        BufferedWriter writer = null;
        try {
            File file = new File(ThemeResources.THEME_MAGIC_PATH + "drm.log");
            if (file.length() > 102400) {
                Log.i(tag, "recreate log file " + file.getAbsolutePath());
                file.delete();
            }
            if (!file.exists()) {
                Log.i(tag, "create log file " + file.getAbsolutePath());
                file.createNewFile();
            }
            Log.i(tag, "export error message into " + file.getAbsolutePath());
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(file, true));
            writer2.append(getContextInfo() + " " + System.currentTimeMillis() + " " + tag + " " + message);
            writer2.newLine();
            try {
                writer2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (writer != null) {
                writer.close();
            }
        } catch (Throwable th) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
    }

    private static String getContextInfo() {
        return String.format("%s %s_%s %s", new Object[]{Build.DEVICE, Build.VERSION.RELEASE, Build.VERSION.INCREMENTAL, DateFormat.getDateTimeInstance().format(new Date())});
    }

    public static String getEncodedImei(Context c) {
        String imei = getOriginImei(c);
        if (TextUtils.isEmpty(imei)) {
            return Prefix.EMPTY;
        }
        return HashUtils.getMD5(imei);
    }

    private static String getOriginImei(Context c) {
        String imei = TelephonyManagerUtil.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            return Prefix.EMPTY;
        }
        return imei;
    }

    public static String getVAID(Context c) {
        if (Build.VERSION.SDK_INT <= 28) {
            return null;
        }
        try {
            return HashUtils.getMD5((String) Class.forName("com.android.id.IdentifierManager").getMethod("getVAID", new Class[]{Context.class}).invoke((Object) null, new Object[]{c}));
        } catch (Exception e) {
            Log.e(TAG, "getVAID hanppens exception e = " + e);
            return null;
        }
    }
}
