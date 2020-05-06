package com.miui.maml.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.networkassistant.config.Constants;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {
    private static ArrayList<String> INTENT_BLACK_LIST = new ArrayList<>();
    private static final int INVALID = -2;
    private static int sAcrossUsersFullPermission = -2;
    private static int sAcrossUsersPermission = -2;

    public static class GetChildWrapper {
        private Element mEle;

        public GetChildWrapper(Element element) {
            this.mEle = element;
        }

        public GetChildWrapper getChild(String str) {
            return new GetChildWrapper(Utils.getChild(this.mEle, str));
        }

        public Element getElement() {
            return this.mEle;
        }
    }

    public static class Point {
        public double x;
        public double y;

        public Point(double d2, double d3) {
            this.x = d2;
            this.y = d3;
        }

        public void Offset(Point point) {
            this.x += point.x;
            this.y += point.y;
        }

        /* access modifiers changed from: package-private */
        public Point minus(Point point) {
            return new Point(this.x - point.x, this.y - point.y);
        }
    }

    public interface XmlTraverseListener {
        void onChild(Element element);
    }

    static {
        INTENT_BLACK_LIST.add("android.intent.action.AIRPLANE_MODE");
        INTENT_BLACK_LIST.add("android.intent.action.BATTERY_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.BATTERY_LOW");
        INTENT_BLACK_LIST.add("android.intent.action.BATTERY_OKAY");
        INTENT_BLACK_LIST.add(Constants.System.ACTION_BOOT_COMPLETED);
        INTENT_BLACK_LIST.add("android.intent.action.CONFIGURATION_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.DEVICE_STORAGE_LOW");
        INTENT_BLACK_LIST.add("android.intent.action.DEVICE_STORAGE_OK");
        INTENT_BLACK_LIST.add("android.intent.action.DREAMING_STARTED");
        INTENT_BLACK_LIST.add("android.intent.action.DREAMING_STOPPED");
        INTENT_BLACK_LIST.add("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        INTENT_BLACK_LIST.add("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        INTENT_BLACK_LIST.add(Constants.System.ACTION_LOCALE_CHANGED);
        INTENT_BLACK_LIST.add("android.intent.action.MY_PACKAGE_REPLACED");
        INTENT_BLACK_LIST.add("android.intent.action.NEW_OUTGOING_CALL");
        INTENT_BLACK_LIST.add(Constants.System.ACTION_PACKAGE_ADDED);
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_DATA_CLEARED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_FIRST_LAUNCH");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_FULLY_REMOVED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_INSTALL");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
        INTENT_BLACK_LIST.add(Constants.System.ACTION_PACKAGE_REMOVED);
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_REPLACED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_RESTARTED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_VERIFIED");
        INTENT_BLACK_LIST.add("android.intent.action.ACTION_POWER_CONNECTED");
        INTENT_BLACK_LIST.add("android.intent.action.ACTION_POWER_DISCONNECTED");
        INTENT_BLACK_LIST.add("android.intent.action.REBOOT");
        INTENT_BLACK_LIST.add(Constants.System.ACTION_SCREEN_OFF);
        INTENT_BLACK_LIST.add(Constants.System.ACTION_SCREEN_ON);
        INTENT_BLACK_LIST.add("android.intent.action.ACTION_SHUTDOWN");
        INTENT_BLACK_LIST.add("android.intent.action.TIMEZONE_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.TIME_TICK");
        INTENT_BLACK_LIST.add("android.intent.action.UID_REMOVED");
        INTENT_BLACK_LIST.add(Constants.System.ACTION_USER_PRESENT);
    }

    public static double Dist(Point point, Point point2, boolean z) {
        double d2 = point.x - point2.x;
        double d3 = point.y - point2.y;
        return z ? Math.sqrt((d2 * d2) + (d3 * d3)) : (d2 * d2) + (d3 * d3);
    }

    public static String addFileNameSuffix(String str, String str2) {
        return addFileNameSuffix(str, "_", str2);
    }

    public static String addFileNameSuffix(String str, String str2, String str3) {
        int indexOf = str.indexOf(46);
        if (indexOf == -1) {
            return str;
        }
        return str.substring(0, indexOf) + str2 + str3 + str.substring(indexOf);
    }

    private static boolean arrContains(String[] strArr, String str) {
        for (String equals : strArr) {
            if (TextUtils.equals(equals, str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(String[] strArr, String str) {
        for (String equals : strArr) {
            if (equals(equals, str)) {
                return true;
            }
        }
        return false;
    }

    public static void asserts(boolean z) {
        asserts(z, "assert error");
    }

    public static void asserts(boolean z, String str) {
        if (!z) {
            throw new Exception(str);
        }
    }

    public static String doubleToString(double d2) {
        String valueOf = String.valueOf(d2);
        return valueOf.endsWith(".0") ? valueOf.substring(0, valueOf.length() - 2) : valueOf;
    }

    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static float getAttrAsFloat(Element element, String str, float f) {
        try {
            return Float.parseFloat(element.getAttribute(str));
        } catch (NumberFormatException unused) {
            return f;
        }
    }

    public static float getAttrAsFloatThrows(Element element, String str) {
        return Float.parseFloat(element.getAttribute(str));
    }

    public static int getAttrAsInt(Element element, String str, int i) {
        try {
            return Integer.parseInt(element.getAttribute(str));
        } catch (NumberFormatException unused) {
            return i;
        }
    }

    public static int getAttrAsIntThrows(Element element, String str) {
        return Integer.parseInt(element.getAttribute(str));
    }

    public static long getAttrAsLong(Element element, String str, long j) {
        try {
            return Long.parseLong(element.getAttribute(str));
        } catch (NumberFormatException unused) {
            return j;
        }
    }

    public static long getAttrAsLongThrows(Element element, String str) {
        return Long.parseLong(element.getAttribute(str));
    }

    public static Element getChild(Element element, String str) {
        if (element == null) {
            return null;
        }
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNodeName().equalsIgnoreCase(str)) {
                return (Element) item;
            }
        }
        return null;
    }

    public static PorterDuff.Mode getPorterDuffMode(int i) {
        return getPorterDuffMode(i, PorterDuff.Mode.SRC_OVER);
    }

    public static PorterDuff.Mode getPorterDuffMode(int i, PorterDuff.Mode mode) {
        for (PorterDuff.Mode mode2 : PorterDuff.Mode.values()) {
            if (mode2.ordinal() == i) {
                return mode2;
            }
        }
        return mode;
    }

    public static PorterDuff.Mode getPorterDuffMode(String str) {
        if (TextUtils.isEmpty(str)) {
            return PorterDuff.Mode.SRC_OVER;
        }
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
        for (PorterDuff.Mode mode2 : PorterDuff.Mode.values()) {
            if (str.equalsIgnoreCase(mode2.name())) {
                return mode2;
            }
        }
        return mode;
    }

    public static double getVariableNumber(String str, Variables variables) {
        return new IndexedVariable(str, variables, true).getDouble();
    }

    public static String getVariableString(String str, Variables variables) {
        return new IndexedVariable(str, variables, false).getString();
    }

    public static boolean isProtectedIntent(String str) {
        if (str == null) {
            return false;
        }
        return INTENT_BLACK_LIST.contains(str.trim());
    }

    public static int mixAlpha(int i, int i2) {
        if (i >= 255) {
            i = i2;
        } else if (i2 < 255) {
            i = Math.round(((float) (i * i2)) / 255.0f);
        }
        return Math.min(255, Math.max(0, i));
    }

    public static String numberToString(Number number) {
        String valueOf = String.valueOf(number);
        return valueOf.endsWith(".0") ? valueOf.substring(0, valueOf.length() - 2) : valueOf;
    }

    public static double parseDouble(String str) {
        if (str.startsWith("+") && str.length() > 1) {
            str = str.substring(1);
        }
        return Double.parseDouble(str);
    }

    public static Point pointProjectionOnSegment(Point point, Point point2, Point point3, boolean z) {
        Point minus = point2.minus(point);
        Point minus2 = point3.minus(point);
        double Dist = ((minus.x * minus2.x) + (minus.y * minus2.y)) / Dist(point, point2, false);
        int i = (Dist > 0.0d ? 1 : (Dist == 0.0d ? 0 : -1));
        if (i >= 0 && Dist <= 1.0d) {
            minus.x *= Dist;
            minus.y *= Dist;
            minus.Offset(point);
            return minus;
        } else if (!z) {
            return null;
        } else {
            return i < 0 ? point : point2;
        }
    }

    public static void putVariableNumber(String str, Variables variables, double d2) {
        variables.put(str, d2);
    }

    public static void putVariableNumber(String str, Variables variables, Double d2) {
        variables.put(str, d2.doubleValue());
    }

    public static void putVariableString(String str, Variables variables, String str2) {
        variables.put(str, (Object) str2);
    }

    public static void sendBroadcast(Context context, Intent intent) {
        if (sAcrossUsersPermission == -2) {
            sAcrossUsersPermission = context.checkSelfPermission("android.permission.INTERACT_ACROSS_USERS");
        }
        if (sAcrossUsersPermission == 0) {
            context.sendBroadcastAsUser(intent, HideSdkDependencyUtils.UserHandle_CURRENT());
        } else {
            context.sendBroadcast(intent);
        }
    }

    public static void startActivity(Context context, Intent intent, Bundle bundle) {
        if (sAcrossUsersFullPermission == -2) {
            sAcrossUsersFullPermission = context.checkSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL");
        }
        if (sAcrossUsersFullPermission == 0) {
            HideSdkDependencyUtils.Context_startActivityAsUser(context, intent, bundle, HideSdkDependencyUtils.UserHandle_CURRENT());
        } else {
            context.startActivity(intent, bundle);
        }
    }

    public static void startService(Context context, Intent intent) {
        if (sAcrossUsersPermission == -2) {
            sAcrossUsersPermission = context.checkSelfPermission("android.permission.INTERACT_ACROSS_USERS");
        }
        if (sAcrossUsersPermission == 0) {
            HideSdkDependencyUtils.Context_startServiceAsUser(context, intent, HideSdkDependencyUtils.UserHandle_CURRENT());
        } else {
            context.startService(intent);
        }
    }

    public static double stringToDouble(String str, double d2) {
        if (str == null) {
            return d2;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException unused) {
            return d2;
        }
    }

    public static void traverseXmlElementChildren(Element element, String str, XmlTraverseListener xmlTraverseListener) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && (str == null || TextUtils.equals(item.getNodeName(), str))) {
                xmlTraverseListener.onChild((Element) item);
            }
        }
    }

    public static void traverseXmlElementChildrenTags(Element element, String[] strArr, XmlTraverseListener xmlTraverseListener) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            String nodeName = item.getNodeName();
            if (item.getNodeType() == 1 && (strArr == null || arrContains(strArr, nodeName))) {
                xmlTraverseListener.onChild((Element) item);
            }
        }
    }

    public byte[] splitByteArray(String str) {
        return splitByteArray(str, 10);
    }

    public byte[] splitByteArray(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] split = str.split(",");
        int length = split.length;
        byte[] bArr = new byte[length];
        for (int i2 = 0; i2 < length; i2++) {
            try {
                bArr[i2] = Byte.parseByte(split[i2], i);
            } catch (NumberFormatException unused) {
            }
        }
        return bArr;
    }

    public double[] splitDoubleArray(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] split = str.split(",");
        int length = split.length;
        double[] dArr = new double[length];
        for (int i = 0; i < length; i++) {
            try {
                dArr[i] = Double.parseDouble(split[i]);
            } catch (NumberFormatException unused) {
            }
        }
        return dArr;
    }

    public int[] splitIntArray(String str) {
        return splitIntArray(str, 10);
    }

    public int[] splitIntArray(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] split = str.split(",");
        int length = split.length;
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            try {
                iArr[i2] = Integer.parseInt(split[i2], i);
            } catch (NumberFormatException unused) {
            }
        }
        return iArr;
    }

    public String[] splitStringArray(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return str.split(str2);
    }
}
