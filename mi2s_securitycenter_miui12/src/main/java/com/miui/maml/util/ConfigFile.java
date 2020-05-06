package com.miui.maml.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.miui.activityutil.o;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.util.Utils;
import com.xiaomi.stat.MiStat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import miui.cloud.CloudPushConstants;
import miui.content.res.ThemeNativeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigFile {
    private static final String LOG_TAG = "ConfigFile";
    public static final String TAG_APP_PICKER = "AppPicker";
    public static final String TAG_CHECK_BOX = "CheckBox";
    private static final String TAG_GADGET = "Gadget";
    private static final String TAG_GADGETS = "Gadgets";
    public static final String TAG_GROUP = "Group";
    public static final String TAG_IMAGE_PICKER = "ImagePicker";
    public static final String TAG_NUMBER_CHOICE = "NumberChoice";
    public static final String TAG_NUMBER_INPUT = "NumberInput";
    private static final String TAG_ROOT = "Config";
    public static final String TAG_STRING_CHOICE = "StringChoice";
    public static final String TAG_STRING_INPUT = "StringInput";
    private static final String TAG_TASK = "Intent";
    private static final String TAG_TASKS = "Tasks";
    private static final String TAG_VARIABLE = "Variable";
    private static final String TAG_VARIABLES = "Variables";
    private boolean mDirty;
    private String mFilePath;
    private ArrayList<Gadget> mGadgets = new ArrayList<>();
    private HashMap<String, Task> mTasks = new HashMap<>();
    private HashMap<String, Variable> mVariables = new HashMap<>();

    public static class Gadget {
        public String path;
        public int x;
        public int y;

        public Gadget(String str, int i, int i2) {
            this.path = str;
            this.x = i;
            this.y = i2;
        }
    }

    private interface OnLoadElementListener {
        void OnLoadElement(Element element);
    }

    public static class Variable {
        public String name;
        public String type;
        public String value;
    }

    private void createNewFile(String str) {
        if (!TextUtils.isEmpty(str)) {
            File file = new File(str);
            file.getParentFile().mkdirs();
            file.delete();
            file.createNewFile();
        }
    }

    private void loadGadgets(Element element) {
        loadList(element, TAG_GADGETS, TAG_GADGET, new OnLoadElementListener() {
            public void OnLoadElement(Element element) {
                if (element != null) {
                    ConfigFile.this.putGadget(new Gadget(element.getAttribute("path"), Utils.getAttrAsInt(element, AnimatedProperty.PROPERTY_NAME_X, 0), Utils.getAttrAsInt(element, AnimatedProperty.PROPERTY_NAME_Y, 0)));
                }
            }
        });
    }

    private void loadList(Element element, String str, String str2, OnLoadElementListener onLoadElementListener) {
        Element child = Utils.getChild(element, str);
        if (child != null) {
            NodeList childNodes = child.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item.getNodeType() == 1 && item.getNodeName().equals(str2)) {
                    onLoadElementListener.OnLoadElement((Element) item);
                }
            }
        }
    }

    private void loadTasks(Element element) {
        loadList(element, TAG_TASKS, TAG_TASK, new OnLoadElementListener() {
            public void OnLoadElement(Element element) {
                ConfigFile.this.putTask(Task.load(element));
            }
        });
    }

    private void loadVariables(Element element) {
        loadList(element, TAG_VARIABLES, "Variable", new OnLoadElementListener() {
            public void OnLoadElement(Element element) {
                ConfigFile.this.put(element.getAttribute(CloudPushConstants.XML_NAME), element.getAttribute(MiStat.Param.VALUE), element.getAttribute("type"));
            }
        });
    }

    /* access modifiers changed from: private */
    public void put(String str, String str2, String str3) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str3)) {
            if ("string".equals(str3) || "number".equals(str3)) {
                Variable variable = this.mVariables.get(str);
                if (variable == null) {
                    variable = new Variable();
                    variable.name = str;
                    this.mVariables.put(str, variable);
                }
                variable.type = str3;
                variable.value = str2;
            }
        }
    }

    private void writeGadgets(StringBuilder sb) {
        if (this.mGadgets.size() != 0) {
            writeTag(sb, TAG_GADGETS, false);
            String[] strArr = {"path", AnimatedProperty.PROPERTY_NAME_X, AnimatedProperty.PROPERTY_NAME_Y};
            Iterator<Gadget> it = this.mGadgets.iterator();
            while (it.hasNext()) {
                Gadget next = it.next();
                writeTag(sb, TAG_GADGET, strArr, new String[]{next.path, String.valueOf(next.x), String.valueOf(next.y)}, true);
            }
            writeTag(sb, TAG_GADGETS, true);
        }
    }

    private static void writeTag(StringBuilder sb, String str, boolean z) {
        sb.append("<");
        if (z) {
            sb.append("/");
        }
        sb.append(str);
        sb.append(">\n");
    }

    private static void writeTag(StringBuilder sb, String str, String[] strArr, String[] strArr2) {
        writeTag(sb, str, strArr, strArr2, true);
    }

    private static void writeTag(StringBuilder sb, String str, String[] strArr, String[] strArr2, boolean z) {
        sb.append("<");
        sb.append(str);
        for (int i = 0; i < strArr.length; i++) {
            if (!z || !TextUtils.isEmpty(strArr2[i])) {
                sb.append(" ");
                sb.append(strArr[i]);
                sb.append("=\"");
                sb.append(strArr2[i]);
                sb.append("\"");
            }
        }
        sb.append("/>\n");
    }

    private void writeTasks(StringBuilder sb) {
        if (this.mTasks.size() != 0) {
            writeTag(sb, TAG_TASKS, false);
            String[] strArr = {Task.TAG_ID, Task.TAG_ACTION, Task.TAG_TYPE, Task.TAG_CATEGORY, Task.TAG_PACKAGE, Task.TAG_CLASS, Task.TAG_NAME};
            for (Task next : this.mTasks.values()) {
                writeTag(sb, TAG_TASK, strArr, new String[]{next.id, next.action, next.type, next.category, next.packageName, next.className, next.name}, true);
            }
            writeTag(sb, TAG_TASKS, true);
        }
    }

    private void writeVariables(StringBuilder sb) {
        if (this.mVariables.size() != 0) {
            writeTag(sb, TAG_VARIABLES, false);
            String[] strArr = {CloudPushConstants.XML_NAME, "type", MiStat.Param.VALUE};
            for (Variable next : this.mVariables.values()) {
                writeTag(sb, "Variable", strArr, new String[]{next.name, next.type, next.value});
            }
            writeTag(sb, TAG_VARIABLES, true);
        }
    }

    public Collection<Gadget> getGadgets() {
        return this.mGadgets;
    }

    public Task getTask(String str) {
        return this.mTasks.get(str);
    }

    public Collection<Task> getTasks() {
        return this.mTasks.values();
    }

    public String getVariable(String str) {
        Variable variable = this.mVariables.get(str);
        if (variable == null) {
            return null;
        }
        return variable.value;
    }

    public Collection<Variable> getVariables() {
        return this.mVariables.values();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x005c, code lost:
        if (r2 != null) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0074, code lost:
        if (r2 != null) goto L_0x005e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0068 A[Catch:{ FileNotFoundException -> 0x007d, ParserConfigurationException -> 0x0070, SAXException -> 0x0069, IOException -> 0x0062, Exception -> 0x0058, all -> 0x0055 }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x006f A[Catch:{ FileNotFoundException -> 0x007d, ParserConfigurationException -> 0x0070, SAXException -> 0x0069, IOException -> 0x0062, Exception -> 0x0058, all -> 0x0055 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0079 A[SYNTHETIC, Splitter:B:54:0x0079] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0080 A[SYNTHETIC, Splitter:B:62:0x0080] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:42:0x0063=Splitter:B:42:0x0063, B:36:0x0059=Splitter:B:36:0x0059} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean load(java.lang.String r5) {
        /*
            r4 = this;
            r4.mFilePath = r5
            java.util.HashMap<java.lang.String, com.miui.maml.util.ConfigFile$Variable> r0 = r4.mVariables
            r0.clear()
            java.util.HashMap<java.lang.String, com.miui.maml.util.Task> r0 = r4.mTasks
            r0.clear()
            javax.xml.parsers.DocumentBuilderFactory r0 = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            r1 = 0
            r2 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x007d, ParserConfigurationException -> 0x0070, SAXException -> 0x0069, IOException -> 0x0062, Exception -> 0x0058 }
            r3.<init>(r5)     // Catch:{ FileNotFoundException -> 0x007d, ParserConfigurationException -> 0x0070, SAXException -> 0x0069, IOException -> 0x0062, Exception -> 0x0058 }
            javax.xml.parsers.DocumentBuilder r5 = r0.newDocumentBuilder()     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            org.w3c.dom.Document r5 = r5.parse(r3)     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            org.w3c.dom.Element r5 = r5.getDocumentElement()     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            if (r5 != 0) goto L_0x0029
            r3.close()     // Catch:{ IOException -> 0x0028 }
        L_0x0028:
            return r1
        L_0x0029:
            java.lang.String r0 = r5.getNodeName()     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            java.lang.String r2 = "Config"
            boolean r0 = r0.equals(r2)     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            if (r0 != 0) goto L_0x0039
            r3.close()     // Catch:{ IOException -> 0x0038 }
        L_0x0038:
            return r1
        L_0x0039:
            r4.loadVariables(r5)     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            r4.loadTasks(r5)     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            r4.loadGadgets(r5)     // Catch:{ FileNotFoundException -> 0x007e, ParserConfigurationException -> 0x0052, SAXException -> 0x004f, IOException -> 0x004c, Exception -> 0x0049, all -> 0x0047 }
            r5 = 1
            r3.close()     // Catch:{ IOException -> 0x0046 }
        L_0x0046:
            return r5
        L_0x0047:
            r5 = move-exception
            goto L_0x0077
        L_0x0049:
            r5 = move-exception
            r2 = r3
            goto L_0x0059
        L_0x004c:
            r5 = move-exception
            r2 = r3
            goto L_0x0063
        L_0x004f:
            r5 = move-exception
            r2 = r3
            goto L_0x006a
        L_0x0052:
            r5 = move-exception
            r2 = r3
            goto L_0x0071
        L_0x0055:
            r5 = move-exception
            r3 = r2
            goto L_0x0077
        L_0x0058:
            r5 = move-exception
        L_0x0059:
            r5.printStackTrace()     // Catch:{ all -> 0x0055 }
            if (r2 == 0) goto L_0x0083
        L_0x005e:
            r2.close()     // Catch:{ IOException -> 0x0083 }
            goto L_0x0083
        L_0x0062:
            r5 = move-exception
        L_0x0063:
            r5.printStackTrace()     // Catch:{ all -> 0x0055 }
            if (r2 == 0) goto L_0x0083
            goto L_0x005e
        L_0x0069:
            r5 = move-exception
        L_0x006a:
            r5.printStackTrace()     // Catch:{ all -> 0x0055 }
            if (r2 == 0) goto L_0x0083
            goto L_0x005e
        L_0x0070:
            r5 = move-exception
        L_0x0071:
            r5.printStackTrace()     // Catch:{ all -> 0x0055 }
            if (r2 == 0) goto L_0x0083
            goto L_0x005e
        L_0x0077:
            if (r3 == 0) goto L_0x007c
            r3.close()     // Catch:{ IOException -> 0x007c }
        L_0x007c:
            throw r5
        L_0x007d:
            r3 = r2
        L_0x007e:
            if (r3 == 0) goto L_0x0083
            r3.close()     // Catch:{ IOException -> 0x0083 }
        L_0x0083:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.ConfigFile.load(java.lang.String):boolean");
    }

    public void loadDefaultSettings(Element element) {
        if (element != null && element.getNodeName().equals(TAG_ROOT)) {
            Utils.traverseXmlElementChildren(element, "Group", new Utils.XmlTraverseListener() {
                public void onChild(Element element) {
                    Utils.traverseXmlElementChildren(element, (String) null, new Utils.XmlTraverseListener() {
                        public void onChild(Element element) {
                            String nodeName = element.getNodeName();
                            String attribute = element.getAttribute("id");
                            if (!ConfigFile.TAG_STRING_INPUT.equals(nodeName)) {
                                if (ConfigFile.TAG_CHECK_BOX.equals(nodeName)) {
                                    ConfigFile configFile = ConfigFile.this;
                                    String attribute2 = element.getAttribute("default");
                                    String str = o.f2310b;
                                    if (!attribute2.equals(str)) {
                                        str = o.f2309a;
                                    }
                                    configFile.putNumber(attribute, str);
                                    return;
                                }
                                if (!ConfigFile.TAG_NUMBER_INPUT.equals(nodeName)) {
                                    if (!ConfigFile.TAG_STRING_CHOICE.equals(nodeName)) {
                                        if (!ConfigFile.TAG_NUMBER_CHOICE.equals(nodeName)) {
                                            if (ConfigFile.TAG_APP_PICKER.equals(nodeName)) {
                                                ConfigFile.this.putTask(Task.load(element));
                                                return;
                                            }
                                            return;
                                        }
                                    }
                                }
                                ConfigFile.this.putNumber(attribute, Utils.doubleToString((double) Utils.getAttrAsFloat(element, "default", 0.0f)));
                                return;
                            }
                            ConfigFile.this.putString(attribute, element.getAttribute("default"));
                        }
                    });
                }
            });
        }
    }

    public void moveGadget(Gadget gadget, int i) {
        if (this.mGadgets.remove(gadget)) {
            this.mGadgets.add(i, gadget);
        }
    }

    public void putGadget(Gadget gadget) {
        if (gadget != null) {
            this.mGadgets.add(gadget);
            this.mDirty = true;
        }
    }

    public void putNumber(String str, double d2) {
        putNumber(str, Utils.doubleToString(d2));
    }

    public void putNumber(String str, String str2) {
        put(str, str2, "number");
        this.mDirty = true;
    }

    public void putString(String str, String str2) {
        put(str, str2, "string");
        this.mDirty = true;
    }

    public void putTask(Task task) {
        if (task != null && !TextUtils.isEmpty(task.id)) {
            this.mTasks.put(task.id, task);
            this.mDirty = true;
        }
    }

    public void removeGadget(Gadget gadget) {
        this.mGadgets.remove(gadget);
        this.mDirty = true;
    }

    public boolean save(Context context) {
        boolean z = this.mDirty;
        this.mDirty = false;
        return !z || save(this.mFilePath, context);
    }

    public boolean save(String str, Context context) {
        StringBuilder sb;
        String str2;
        String str3;
        String str4;
        ThemeNativeUtils.remove(str);
        String str5 = null;
        try {
            createNewFile(str);
        } catch (IOException e) {
            try {
                File externalFilesDir = context.getExternalFilesDir((String) null);
                if (Build.VERSION.SDK_INT >= 23) {
                    if (externalFilesDir != null) {
                        str4 = externalFilesDir.getPath() + File.separator + "temp";
                    }
                    createNewFile(str5);
                } else {
                    str4 = context.getDir("temp", 0).getPath() + File.separator + "temp";
                }
                str5 = str4;
                createNewFile(str5);
            } catch (Exception unused) {
                str3 = "create target file failed" + e;
                Log.e(LOG_TAG, str3);
                return false;
            }
        } catch (Exception e2) {
            e = e2;
            sb = new StringBuilder();
            str2 = "create target file or temp file failed";
            sb.append(str2);
            sb.append(e);
            str3 = sb.toString();
            Log.e(LOG_TAG, str3);
            return false;
        }
        StringBuilder sb2 = new StringBuilder();
        writeTag(sb2, TAG_ROOT, false);
        writeVariables(sb2);
        writeTasks(sb2);
        writeGadgets(sb2);
        writeTag(sb2, TAG_ROOT, true);
        try {
            if (new File(str).exists()) {
                ThemeNativeUtils.write(str, sb2.toString());
            } else if (TextUtils.isEmpty(str5) || !new File(str5).exists()) {
                Log.w(LOG_TAG, "target file and temp file are not exists");
                return false;
            } else {
                ThemeNativeUtils.write(str5, sb2.toString());
                ThemeNativeUtils.copy(str5, str);
                ThemeNativeUtils.remove(str5);
            }
            ThemeNativeUtils.updateFilePermissionWithThemeContext("/data/system/theme/config.config");
            return true;
        } catch (Exception e3) {
            e = e3;
            sb = new StringBuilder();
            str2 = "write file error";
            sb.append(str2);
            sb.append(e);
            str3 = sb.toString();
            Log.e(LOG_TAG, str3);
            return false;
        }
    }
}
