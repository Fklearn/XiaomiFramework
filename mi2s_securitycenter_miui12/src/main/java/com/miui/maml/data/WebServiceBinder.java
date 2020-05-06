package com.miui.maml.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.elements.ListScreenElement;
import com.miui.maml.util.JSONPath;
import com.miui.maml.util.TextFormatter;
import com.miui.maml.util.Utils;
import com.miui.maml.util.net.IOUtils;
import com.miui.maml.util.net.SimpleRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import miui.cloud.CloudPushConstants;
import miui.net.ConnectivityHelper;
import miui.os.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WebServiceBinder extends VariableBinder {
    private static final String LOG_TAG = "WebServiceBinder";
    public static final String TAG_NAME = "WebServiceBinder";
    /* access modifiers changed from: private */
    public boolean mAuthentication;
    IndexedVariable mContentStringVar;
    public String mEncryptedUser;
    IndexedVariable mErrorCodeVar;
    IndexedVariable mErrorStringVar;
    private long mLastQueryTime;
    private List mList;
    /* access modifiers changed from: private */
    public TextFormatter mParamsFormatter;
    /* access modifiers changed from: private */
    public ResponseProtocol mProtocol = ResponseProtocol.XML;
    /* access modifiers changed from: private */
    public boolean mQueryInProgress;
    private Thread mQueryThread;
    RequestMethod mRequestMethod = RequestMethod.POST;
    /* access modifiers changed from: private */
    public boolean mSecure;
    public String mSecurity;
    /* access modifiers changed from: private */
    public String mServiceId;
    public String mServiceToken;
    IndexedVariable mStatusCodeVar;
    private int mUpdateInterval = -1;
    protected TextFormatter mUriFormatter;
    private int mUseNetwork = 2;
    private Expression mUseNetworkExp;

    /* renamed from: com.miui.maml.data.WebServiceBinder$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$WebServiceBinder$RequestMethod = new int[RequestMethod.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$WebServiceBinder$ResponseProtocol = new int[ResponseProtocol.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = new int[ListScreenElement.ColumnInfo.Type.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(25:0|(2:1|2)|3|(2:5|6)|7|9|10|11|12|13|(2:15|16)|17|(2:19|20)|21|23|24|25|26|27|28|29|30|31|32|34) */
        /* JADX WARNING: Can't wrap try/catch for region: R(27:0|1|2|3|(2:5|6)|7|9|10|11|12|13|15|16|17|(2:19|20)|21|23|24|25|26|27|28|29|30|31|32|34) */
        /* JADX WARNING: Can't wrap try/catch for region: R(28:0|1|2|3|(2:5|6)|7|9|10|11|12|13|15|16|17|19|20|21|23|24|25|26|27|28|29|30|31|32|34) */
        /* JADX WARNING: Can't wrap try/catch for region: R(29:0|1|2|3|5|6|7|9|10|11|12|13|15|16|17|19|20|21|23|24|25|26|27|28|29|30|31|32|34) */
        /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0032 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0065 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x006f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x0079 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x0083 */
        static {
            /*
                com.miui.maml.data.WebServiceBinder$RequestMethod[] r0 = com.miui.maml.data.WebServiceBinder.RequestMethod.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$WebServiceBinder$RequestMethod = r0
                r0 = 1
                int[] r1 = $SwitchMap$com$miui$maml$data$WebServiceBinder$RequestMethod     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.WebServiceBinder$RequestMethod r2 = com.miui.maml.data.WebServiceBinder.RequestMethod.GET     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                r1 = 2
                int[] r2 = $SwitchMap$com$miui$maml$data$WebServiceBinder$RequestMethod     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.WebServiceBinder$RequestMethod r3 = com.miui.maml.data.WebServiceBinder.RequestMethod.POST     // Catch:{ NoSuchFieldError -> 0x001f }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                com.miui.maml.data.WebServiceBinder$ResponseProtocol[] r2 = com.miui.maml.data.WebServiceBinder.ResponseProtocol.values()
                int r2 = r2.length
                int[] r2 = new int[r2]
                $SwitchMap$com$miui$maml$data$WebServiceBinder$ResponseProtocol = r2
                int[] r2 = $SwitchMap$com$miui$maml$data$WebServiceBinder$ResponseProtocol     // Catch:{ NoSuchFieldError -> 0x0032 }
                com.miui.maml.data.WebServiceBinder$ResponseProtocol r3 = com.miui.maml.data.WebServiceBinder.ResponseProtocol.XML     // Catch:{ NoSuchFieldError -> 0x0032 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0032 }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x0032 }
            L_0x0032:
                int[] r2 = $SwitchMap$com$miui$maml$data$WebServiceBinder$ResponseProtocol     // Catch:{ NoSuchFieldError -> 0x003c }
                com.miui.maml.data.WebServiceBinder$ResponseProtocol r3 = com.miui.maml.data.WebServiceBinder.ResponseProtocol.JSONobj     // Catch:{ NoSuchFieldError -> 0x003c }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x003c }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x003c }
            L_0x003c:
                r2 = 3
                int[] r3 = $SwitchMap$com$miui$maml$data$WebServiceBinder$ResponseProtocol     // Catch:{ NoSuchFieldError -> 0x0047 }
                com.miui.maml.data.WebServiceBinder$ResponseProtocol r4 = com.miui.maml.data.WebServiceBinder.ResponseProtocol.JSONarray     // Catch:{ NoSuchFieldError -> 0x0047 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0047 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0047 }
            L_0x0047:
                r3 = 4
                int[] r4 = $SwitchMap$com$miui$maml$data$WebServiceBinder$ResponseProtocol     // Catch:{ NoSuchFieldError -> 0x0052 }
                com.miui.maml.data.WebServiceBinder$ResponseProtocol r5 = com.miui.maml.data.WebServiceBinder.ResponseProtocol.BITMAP     // Catch:{ NoSuchFieldError -> 0x0052 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0052 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0052 }
            L_0x0052:
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type[] r4 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = r4
                int[] r4 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0065 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r5 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.STRING     // Catch:{ NoSuchFieldError -> 0x0065 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0065 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0065 }
            L_0x0065:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x006f }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r4 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.DOUBLE     // Catch:{ NoSuchFieldError -> 0x006f }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x006f }
                r0[r4] = r1     // Catch:{ NoSuchFieldError -> 0x006f }
            L_0x006f:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0079 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.FLOAT     // Catch:{ NoSuchFieldError -> 0x0079 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0079 }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0079 }
            L_0x0079:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x0083 }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.INTEGER     // Catch:{ NoSuchFieldError -> 0x0083 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0083 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x0083 }
            L_0x0083:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type     // Catch:{ NoSuchFieldError -> 0x008e }
                com.miui.maml.elements.ListScreenElement$ColumnInfo$Type r1 = com.miui.maml.elements.ListScreenElement.ColumnInfo.Type.LONG     // Catch:{ NoSuchFieldError -> 0x008e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x008e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x008e }
            L_0x008e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.WebServiceBinder.AnonymousClass1.<clinit>():void");
        }
    }

    public static final class AuthToken {
        public final String authToken;
        public final String security;

        private AuthToken(String str, String str2) {
            this.authToken = str;
            this.security = str2;
        }

        public static AuthToken parse(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String[] split = str.split(",");
            if (split.length != 2) {
                return null;
            }
            return new AuthToken(split[0], split[1]);
        }
    }

    private static class List {
        public String mDataPath;
        private ListScreenElement mList;
        /* access modifiers changed from: private */
        public String mName;
        private ScreenElementRoot mRoot;

        public List(Element element, ScreenElementRoot screenElementRoot) {
            this.mDataPath = element.getAttribute("path");
            if (TextUtils.isEmpty(this.mDataPath)) {
                this.mDataPath = element.getAttribute("xpath");
            }
            this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
            this.mRoot = screenElementRoot;
        }

        public void fill(JSONArray jSONArray) {
            if (this.mList == null) {
                this.mList = (ListScreenElement) this.mRoot.findElement(this.mName);
                if (this.mList == null) {
                    Log.e("WebServiceBinder", "fail to find list: " + this.mName);
                    return;
                }
            }
            this.mList.removeAllItems();
            if (jSONArray.length() != 0) {
                ArrayList<ListScreenElement.ColumnInfo> columnsInfo = this.mList.getColumnsInfo();
                int size = columnsInfo.size();
                Object[] objArr = new Object[size];
                int i = 0;
                while (i < jSONArray.length()) {
                    try {
                        Object obj = jSONArray.get(i);
                        if (!(obj instanceof JSONObject)) {
                            i++;
                        } else {
                            JSONObject jSONObject = (JSONObject) obj;
                            for (int i2 = 0; i2 < size; i2++) {
                                objArr[i2] = null;
                                ListScreenElement.ColumnInfo columnInfo = columnsInfo.get(i2);
                                int i3 = AnonymousClass1.$SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[columnInfo.mType.ordinal()];
                                if (i3 == 1) {
                                    objArr[i2] = jSONObject.optString(columnInfo.mVarName);
                                } else if (i3 == 2 || i3 == 3) {
                                    objArr[i2] = Double.valueOf(jSONObject.optDouble(columnInfo.mVarName));
                                } else if (i3 == 4) {
                                    objArr[i2] = Integer.valueOf(jSONObject.optInt(columnInfo.mVarName));
                                } else if (i3 == 5) {
                                    objArr[i2] = Long.valueOf(jSONObject.optLong(columnInfo.mVarName));
                                }
                            }
                            this.mList.addItem(objArr);
                            i++;
                        }
                    } catch (JSONException e) {
                        Log.e("WebServiceBinder", "JSON error: " + e.toString());
                    }
                }
            }
        }

        public void fill(NodeList nodeList) {
            String textContent;
            if (nodeList != null) {
                if (this.mList == null) {
                    this.mList = (ListScreenElement) this.mRoot.findElement(this.mName);
                    if (this.mList == null) {
                        Log.e("WebServiceBinder", "fail to find list: " + this.mName);
                        return;
                    }
                }
                this.mList.removeAllItems();
                ArrayList<ListScreenElement.ColumnInfo> columnsInfo = this.mList.getColumnsInfo();
                int size = columnsInfo.size();
                Object[] objArr = new Object[size];
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    for (int i2 = 0; i2 < size; i2++) {
                        objArr[i2] = null;
                        ListScreenElement.ColumnInfo columnInfo = columnsInfo.get(i2);
                        Element child = Utils.getChild(element, columnInfo.mVarName);
                        if (!(child == null || (textContent = child.getTextContent()) == null)) {
                            try {
                                int i3 = AnonymousClass1.$SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[columnInfo.mType.ordinal()];
                                if (i3 == 1) {
                                    objArr[i2] = textContent;
                                } else if (i3 == 2) {
                                    objArr[i2] = Double.valueOf(textContent);
                                } else if (i3 == 3) {
                                    objArr[i2] = Float.valueOf(textContent);
                                } else if (i3 == 4) {
                                    objArr[i2] = Integer.valueOf(textContent);
                                } else if (i3 == 5) {
                                    objArr[i2] = Long.valueOf(textContent);
                                }
                            } catch (NumberFormatException unused) {
                            }
                        }
                    }
                    this.mList.addItem(objArr);
                }
            }
        }
    }

    private class QueryThread extends Thread {
        private static final int ERROR_IO_EXCEPTION = 8;
        private static final int ERROR_OK = 0;
        private static final int ERROR_SECURE_ACCOUNT_AUTHTOKEN_FAIL = 5;
        private static final int ERROR_SECURE_ACCOUNT_NOT_LOGIN = 4;
        private static final int ERROR_SECURE_CIPHER_EXCEPTION = 6;
        private static final int ERROR_SECURE_INVALID_RESPONSE = 7;
        public static final int ERROR_USE_NETWORK_FORBIDDEN = 3;
        private static final String KEY_ENCRYPTED_USER_ID = "encrypted_user_id";

        public QueryThread() {
            super("WebServiceBinder QueryThread");
        }

        private Object doRequest(String str, RequestMethod requestMethod, boolean z, boolean z2) {
            return doRequest(str, requestMethod, z, z2, true);
        }

        /* JADX WARNING: Removed duplicated region for block: B:33:0x0087  */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x008e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.lang.Object doRequest(java.lang.String r16, com.miui.maml.data.WebServiceBinder.RequestMethod r17, boolean r18, boolean r19, boolean r20) {
            /*
                r15 = this;
                r7 = r15
                r2 = r16
                java.lang.String r0 = "WebServiceBinder"
                java.lang.String r1 = "doRequest"
                android.util.Log.i(r0, r1)
                com.miui.maml.data.WebServiceBinder r1 = com.miui.maml.data.WebServiceBinder.this
                com.miui.maml.ScreenContext r1 = r1.getContext()
                android.content.Context r1 = r1.mContext
                android.accounts.AccountManager r1 = android.accounts.AccountManager.get(r1)
                java.lang.String r3 = "com.xiaomi"
                r4 = 0
                r5 = 0
                if (r18 == 0) goto L_0x00b0
                com.miui.maml.data.WebServiceBinder r6 = com.miui.maml.data.WebServiceBinder.this
                java.lang.String r8 = r6.mEncryptedUser
                if (r8 == 0) goto L_0x0026
                java.lang.String r6 = r6.mServiceToken
                if (r6 != 0) goto L_0x0098
            L_0x0026:
                android.accounts.Account[] r6 = r1.getAccountsByType(r3)
                int r8 = r6.length
                if (r8 <= 0) goto L_0x0031
                r6 = r6[r4]
                r9 = r6
                goto L_0x0032
            L_0x0031:
                r9 = r5
            L_0x0032:
                if (r9 != 0) goto L_0x0040
                com.miui.maml.data.WebServiceBinder r1 = com.miui.maml.data.WebServiceBinder.this
                r2 = 4
                r1.setErrorCode(r2)
                java.lang.String r1 = "xiaomi account not login"
                android.util.Log.e(r0, r1)
                return r5
            L_0x0040:
                com.miui.maml.data.WebServiceBinder r6 = com.miui.maml.data.WebServiceBinder.this
                java.lang.String r8 = "encrypted_user_id"
                java.lang.String r8 = r1.getUserData(r9, r8)
                r6.mEncryptedUser = r8
                com.miui.maml.data.WebServiceBinder r6 = com.miui.maml.data.WebServiceBinder.this
                java.lang.String r10 = r6.mServiceId
                r11 = 0
                r12 = 1
                r13 = 0
                r14 = 0
                r8 = r1
                android.accounts.AccountManagerFuture r6 = r8.getAuthToken(r9, r10, r11, r12, r13, r14)
                if (r6 == 0) goto L_0x007c
                java.lang.Object r6 = r6.getResult()     // Catch:{ OperationCanceledException -> 0x007a, AuthenticatorException -> 0x0078, IOException -> 0x0076, Exception -> 0x0074 }
                android.os.Bundle r6 = (android.os.Bundle) r6     // Catch:{ OperationCanceledException -> 0x007a, AuthenticatorException -> 0x0078, IOException -> 0x0076, Exception -> 0x0074 }
                if (r6 == 0) goto L_0x006e
                java.lang.String r0 = "authtoken"
                java.lang.String r0 = r6.getString(r0)     // Catch:{ OperationCanceledException -> 0x007a, AuthenticatorException -> 0x0078, IOException -> 0x0076, Exception -> 0x0074 }
                com.miui.maml.data.WebServiceBinder$AuthToken r0 = com.miui.maml.data.WebServiceBinder.AuthToken.parse(r0)     // Catch:{ OperationCanceledException -> 0x007a, AuthenticatorException -> 0x0078, IOException -> 0x0076, Exception -> 0x0074 }
                goto L_0x0085
            L_0x006e:
                java.lang.String r6 = "getAuthToken: future getResult is null"
            L_0x0070:
                android.util.Log.d(r0, r6)     // Catch:{ OperationCanceledException -> 0x007a, AuthenticatorException -> 0x0078, IOException -> 0x0076, Exception -> 0x0074 }
                goto L_0x0084
            L_0x0074:
                r0 = move-exception
                goto L_0x007f
            L_0x0076:
                r0 = move-exception
                goto L_0x007f
            L_0x0078:
                r0 = move-exception
                goto L_0x007f
            L_0x007a:
                r0 = move-exception
                goto L_0x007f
            L_0x007c:
                java.lang.String r6 = "getAuthToken: future is null"
                goto L_0x0070
            L_0x007f:
                com.miui.maml.data.WebServiceBinder r6 = com.miui.maml.data.WebServiceBinder.this
                r6.handleException(r0)
            L_0x0084:
                r0 = r5
            L_0x0085:
                if (r0 != 0) goto L_0x008e
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                r1 = 5
                r0.setErrorCode(r1)
                return r5
            L_0x008e:
                com.miui.maml.data.WebServiceBinder r6 = com.miui.maml.data.WebServiceBinder.this
                java.lang.String r8 = r0.authToken
                r6.mServiceToken = r8
                java.lang.String r0 = r0.security
                r6.mSecurity = r0
            L_0x0098:
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                com.miui.maml.data.WebServiceBinder r6 = com.miui.maml.data.WebServiceBinder.this
                java.lang.String r6 = r6.mEncryptedUser
                java.lang.String r8 = "cUserId"
                r0.put(r8, r6)
                com.miui.maml.data.WebServiceBinder r6 = com.miui.maml.data.WebServiceBinder.this
                java.lang.String r6 = r6.mServiceToken
                java.lang.String r8 = "serviceToken"
                r0.put(r8, r6)
                goto L_0x00b1
            L_0x00b0:
                r0 = r5
            L_0x00b1:
                java.util.HashMap r6 = new java.util.HashMap
                r6.<init>()
                com.miui.maml.data.WebServiceBinder r8 = com.miui.maml.data.WebServiceBinder.this
                com.miui.maml.util.TextFormatter r8 = r8.mParamsFormatter
                java.lang.String r8 = r8.getText()
                boolean r9 = android.text.TextUtils.isEmpty(r8)
                r10 = 2
                r11 = 1
                if (r9 != 0) goto L_0x00e8
                java.lang.String r9 = ","
                java.lang.String[] r8 = r8.split(r9)
                int r9 = r8.length
                r12 = r4
            L_0x00d0:
                if (r12 >= r9) goto L_0x00e8
                r13 = r8[r12]
                java.lang.String r14 = ":"
                java.lang.String[] r13 = r13.split(r14)
                int r14 = r13.length
                if (r14 == r10) goto L_0x00de
                goto L_0x00e5
            L_0x00de:
                r14 = r13[r4]
                r13 = r13[r11]
                r6.put(r14, r13)
            L_0x00e5:
                int r12 = r12 + 1
                goto L_0x00d0
            L_0x00e8:
                com.miui.maml.data.WebServiceBinder r4 = com.miui.maml.data.WebServiceBinder.this     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                com.miui.maml.data.WebServiceBinder$ResponseProtocol r4 = r4.mProtocol     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                com.miui.maml.data.WebServiceBinder$ResponseProtocol r8 = com.miui.maml.data.WebServiceBinder.ResponseProtocol.BITMAP     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                r9 = 200(0xc8, float:2.8E-43)
                if (r4 != r8) goto L_0x0100
                com.miui.maml.util.net.SimpleRequest$StreamContent r0 = com.miui.maml.util.net.SimpleRequest.getAsStream(r2, r6, r5)     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                if (r0 == 0) goto L_0x0198
                com.miui.maml.data.WebServiceBinder r4 = com.miui.maml.data.WebServiceBinder.this     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                r4.setStatusCode(r9)     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                return r0
            L_0x0100:
                int[] r4 = com.miui.maml.data.WebServiceBinder.AnonymousClass1.$SwitchMap$com$miui$maml$data$WebServiceBinder$RequestMethod     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                int r8 = r17.ordinal()     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                r4 = r4[r8]     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                if (r4 == r11) goto L_0x011e
                if (r4 == r10) goto L_0x010e
                r0 = r5
                goto L_0x012d
            L_0x010e:
                if (r19 == 0) goto L_0x0119
                com.miui.maml.data.WebServiceBinder r4 = com.miui.maml.data.WebServiceBinder.this     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                java.lang.String r4 = r4.mSecurity     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                com.miui.maml.util.net.SimpleRequest$StringContent r0 = com.miui.maml.util.net.SecureRequest.postAsString(r2, r6, r0, r11, r4)     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                goto L_0x012d
            L_0x0119:
                com.miui.maml.util.net.SimpleRequest$StringContent r0 = com.miui.maml.util.net.SimpleRequest.postAsString(r2, r6, r0, r11)     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                goto L_0x012d
            L_0x011e:
                if (r19 == 0) goto L_0x0129
                com.miui.maml.data.WebServiceBinder r4 = com.miui.maml.data.WebServiceBinder.this     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                java.lang.String r4 = r4.mSecurity     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                com.miui.maml.util.net.SimpleRequest$StringContent r0 = com.miui.maml.util.net.SecureRequest.getAsString(r2, r6, r0, r11, r4)     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                goto L_0x012d
            L_0x0129:
                com.miui.maml.util.net.SimpleRequest$StringContent r0 = com.miui.maml.util.net.SimpleRequest.getAsString(r2, r6, r0, r11)     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
            L_0x012d:
                if (r0 == 0) goto L_0x0198
                com.miui.maml.data.WebServiceBinder r4 = com.miui.maml.data.WebServiceBinder.this     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                r4.setStatusCode(r9)     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                java.lang.String r0 = r0.getBody()     // Catch:{ IOException -> 0x018b, CipherException -> 0x0181, AccessDeniedException -> 0x0173, InvalidResponseException -> 0x0169, AuthenticationFailureException -> 0x0140, Exception -> 0x0139 }
                return r0
            L_0x0139:
                r0 = move-exception
                com.miui.maml.data.WebServiceBinder r1 = com.miui.maml.data.WebServiceBinder.this
                r1.handleException(r0)
                goto L_0x0198
            L_0x0140:
                r0 = move-exception
                com.miui.maml.data.WebServiceBinder r4 = com.miui.maml.data.WebServiceBinder.this
                r4.handleException(r0)
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                r4 = 400(0x190, float:5.6E-43)
                r0.setStatusCode(r4)
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                java.lang.String r0 = r0.mServiceToken
                r1.invalidateAuthToken(r3, r0)
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                r0.mServiceToken = r5
                if (r20 == 0) goto L_0x0198
                r6 = 0
                r1 = r15
                r2 = r16
                r3 = r17
                r4 = r18
                r5 = r19
                java.lang.Object r0 = r1.doRequest(r2, r3, r4, r5, r6)
                return r0
            L_0x0169:
                r0 = move-exception
                com.miui.maml.data.WebServiceBinder r1 = com.miui.maml.data.WebServiceBinder.this
                r1.handleException(r0)
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                r1 = 7
                goto L_0x0195
            L_0x0173:
                r0 = move-exception
                com.miui.maml.data.WebServiceBinder r1 = com.miui.maml.data.WebServiceBinder.this
                r1.handleException(r0)
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                r1 = 403(0x193, float:5.65E-43)
                r0.setStatusCode(r1)
                goto L_0x0198
            L_0x0181:
                r0 = move-exception
                com.miui.maml.data.WebServiceBinder r1 = com.miui.maml.data.WebServiceBinder.this
                r1.handleException(r0)
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                r1 = 6
                goto L_0x0195
            L_0x018b:
                r0 = move-exception
                com.miui.maml.data.WebServiceBinder r1 = com.miui.maml.data.WebServiceBinder.this
                r1.handleException(r0)
                com.miui.maml.data.WebServiceBinder r0 = com.miui.maml.data.WebServiceBinder.this
                r1 = 8
            L_0x0195:
                r0.setErrorCode(r1)
            L_0x0198:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.WebServiceBinder.QueryThread.doRequest(java.lang.String, com.miui.maml.data.WebServiceBinder$RequestMethod, boolean, boolean, boolean):java.lang.Object");
        }

        public void run() {
            Log.i("WebServiceBinder", "QueryThread start");
            WebServiceBinder.this.setErrorCode(0);
            WebServiceBinder.this.setStatusCode(0);
            String text = WebServiceBinder.this.mUriFormatter.getText();
            String str = null;
            String replaceAll = text != null ? text.replaceAll(" ", "") : null;
            if (!TextUtils.isEmpty(replaceAll)) {
                WebServiceBinder webServiceBinder = WebServiceBinder.this;
                Object doRequest = doRequest(replaceAll, webServiceBinder.mRequestMethod, webServiceBinder.mAuthentication, WebServiceBinder.this.mSecure);
                IndexedVariable indexedVariable = WebServiceBinder.this.mContentStringVar;
                if (indexedVariable != null) {
                    if (doRequest == null || !(doRequest instanceof String)) {
                        indexedVariable = WebServiceBinder.this.mContentStringVar;
                    } else {
                        str = (String) doRequest;
                    }
                    indexedVariable.set((Object) str);
                }
                if (doRequest != null) {
                    int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$WebServiceBinder$ResponseProtocol[WebServiceBinder.this.mProtocol.ordinal()];
                    if (i == 1) {
                        WebServiceBinder.this.processResponseXml((String) doRequest);
                    } else if (i == 2 || i == 3) {
                        WebServiceBinder.this.processResponseJson((String) doRequest);
                    } else if (i == 4) {
                        WebServiceBinder.this.processResponseBitmap((SimpleRequest.StreamContent) doRequest);
                    }
                }
            } else {
                Log.w("WebServiceBinder", "url is null");
            }
            WebServiceBinder.this.onUpdateComplete();
            boolean unused = WebServiceBinder.this.mQueryInProgress = false;
            Log.i("WebServiceBinder", "QueryThread end");
        }
    }

    enum RequestMethod {
        INVALID,
        POST,
        GET
    }

    enum ResponseProtocol {
        XML,
        JSONobj,
        JSONarray,
        BITMAP
    }

    public static class Variable extends VariableBinder.Variable {
        private boolean mCache;
        private String mInnerPath;
        public String mPath;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mPath = element.getAttribute("xpath");
            if (TextUtils.isEmpty(this.mPath)) {
                this.mPath = element.getAttribute("path");
            }
            this.mInnerPath = element.getAttribute("innerPath");
            this.mCache = Boolean.parseBoolean(element.getAttribute("cache"));
        }

        private final String getCacheName() {
            return this.mName + ".cache";
        }

        /* access modifiers changed from: private */
        public boolean hasCache(String str) {
            return this.mCache && !TextUtils.isEmpty(str);
        }

        /* JADX WARNING: type inference failed for: r2v0 */
        /* JADX WARNING: type inference failed for: r2v1 */
        /* JADX WARNING: type inference failed for: r1v6, types: [android.graphics.Bitmap] */
        /* JADX WARNING: type inference failed for: r2v11 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void loadCache(java.lang.String r6) {
            /*
                r5 = this;
                java.lang.String r0 = "WebServiceBinder"
                int r1 = r5.mType
                r2 = 7
                if (r1 != r2) goto L_0x0046
                boolean r1 = r5.hasCache(r6)
                if (r1 != 0) goto L_0x000e
                goto L_0x0046
            L_0x000e:
                r1 = 0
                java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0035, OutOfMemoryError -> 0x002b, all -> 0x0029 }
                java.io.File r3 = new java.io.File     // Catch:{ FileNotFoundException -> 0x0035, OutOfMemoryError -> 0x002b, all -> 0x0029 }
                java.lang.String r4 = r5.getCacheName()     // Catch:{ FileNotFoundException -> 0x0035, OutOfMemoryError -> 0x002b, all -> 0x0029 }
                r3.<init>(r6, r4)     // Catch:{ FileNotFoundException -> 0x0035, OutOfMemoryError -> 0x002b, all -> 0x0029 }
                r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x0035, OutOfMemoryError -> 0x002b, all -> 0x0029 }
                android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeStream(r2)     // Catch:{ FileNotFoundException -> 0x0027, OutOfMemoryError -> 0x0025 }
            L_0x0021:
                com.miui.maml.util.net.IOUtils.closeQuietly((java.io.InputStream) r2)
                goto L_0x003c
            L_0x0025:
                r6 = move-exception
                goto L_0x002d
            L_0x0027:
                r6 = move-exception
                goto L_0x0037
            L_0x0029:
                r6 = move-exception
                goto L_0x0042
            L_0x002b:
                r6 = move-exception
                r2 = r1
            L_0x002d:
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0040 }
            L_0x0031:
                android.util.Log.e(r0, r6)     // Catch:{ all -> 0x0040 }
                goto L_0x0021
            L_0x0035:
                r6 = move-exception
                r2 = r1
            L_0x0037:
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0040 }
                goto L_0x0031
            L_0x003c:
                r5.set(r1)
                return
            L_0x0040:
                r6 = move-exception
                r1 = r2
            L_0x0042:
                com.miui.maml.util.net.IOUtils.closeQuietly((java.io.InputStream) r1)
                throw r6
            L_0x0046:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.WebServiceBinder.Variable.loadCache(java.lang.String):void");
        }

        public void saveCache(InputStream inputStream, String str) {
            FileOutputStream fileOutputStream;
            if (hasCache(str)) {
                File file = new File(str);
                if (!file.exists()) {
                    FileUtils.mkdirs(file, 493, -1, -1);
                }
                try {
                    File file2 = new File(str, getCacheName());
                    file2.delete();
                    if (inputStream != null) {
                        fileOutputStream = new FileOutputStream(file2, false);
                        byte[] bArr = new byte[65536];
                        while (true) {
                            int read = inputStream.read(bArr, 0, 65536);
                            if (read > 0) {
                                fileOutputStream.write(bArr, 0, read);
                            } else {
                                IOUtils.closeQuietly((OutputStream) fileOutputStream);
                                return;
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    Log.e("WebServiceBinder", e.toString());
                    e.printStackTrace();
                } catch (IOException e2) {
                    Log.e("WebServiceBinder", e2.toString());
                    e2.printStackTrace();
                } catch (OutOfMemoryError e3) {
                    Log.e("WebServiceBinder", e3.toString());
                    e3.printStackTrace();
                } catch (Throwable th) {
                    IOUtils.closeQuietly((OutputStream) fileOutputStream);
                    throw th;
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:21:0x0044  */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x0076  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x007c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void set(java.lang.Object r9) {
            /*
                r8 = this;
                boolean r0 = r8.isArray()
                if (r0 == 0) goto L_0x0084
                boolean r0 = r9 instanceof org.json.JSONArray
                if (r0 == 0) goto L_0x0084
                org.json.JSONArray r9 = (org.json.JSONArray) r9
                com.miui.maml.data.IndexedVariable r0 = r8.mVar
                com.miui.maml.data.Variables r0 = r0.getVariables()
                com.miui.maml.data.IndexedVariable r1 = r8.mVar
                int r1 = r1.getIndex()
                java.lang.Object r0 = r0.get((int) r1)
                boolean r1 = r0 instanceof double[]
                r2 = 0
                if (r1 == 0) goto L_0x0026
                double[] r0 = (double[]) r0
                int r0 = r0.length
                r1 = 1
                goto L_0x0031
            L_0x0026:
                boolean r1 = r0 instanceof java.lang.String[]
                if (r1 == 0) goto L_0x002f
                java.lang.String[] r0 = (java.lang.String[]) r0
                int r0 = r0.length
                r1 = r2
                goto L_0x0031
            L_0x002f:
                r0 = r2
                r1 = r0
            L_0x0031:
                if (r2 >= r0) goto L_0x0087
                r3 = 0
                int r4 = r9.length()     // Catch:{ JSONException -> 0x003f }
                if (r2 >= r4) goto L_0x003f
                java.lang.Object r4 = r9.get(r2)     // Catch:{ JSONException -> 0x003f }
                goto L_0x0040
            L_0x003f:
                r4 = r3
            L_0x0040:
                r5 = 0
                if (r4 == 0) goto L_0x0074
                java.lang.Object r7 = org.json.JSONObject.NULL
                if (r4 == r7) goto L_0x0067
                boolean r7 = r4 instanceof org.json.JSONObject
                if (r7 == 0) goto L_0x0067
                com.miui.maml.util.JSONPath r7 = new com.miui.maml.util.JSONPath
                org.json.JSONObject r4 = (org.json.JSONObject) r4
                r7.<init>((org.json.JSONObject) r4)
                java.lang.String r4 = r8.mInnerPath
                java.lang.Object r4 = r7.get(r4)
                boolean r7 = r4 instanceof java.lang.String
                if (r7 == 0) goto L_0x0074
                java.lang.String r4 = (java.lang.String) r4
                if (r1 == 0) goto L_0x0065
                double r5 = com.miui.maml.util.Utils.parseDouble(r4)     // Catch:{ NumberFormatException -> 0x0065 }
            L_0x0065:
                r3 = r4
                goto L_0x0074
            L_0x0067:
                boolean r7 = r4 instanceof java.lang.String
                if (r7 == 0) goto L_0x0074
                r3 = r4
                java.lang.String r3 = (java.lang.String) r3
                if (r1 == 0) goto L_0x0074
                double r5 = com.miui.maml.util.Utils.parseDouble(r3)     // Catch:{ NumberFormatException -> 0x0074 }
            L_0x0074:
                if (r1 == 0) goto L_0x007c
                com.miui.maml.data.IndexedVariable r3 = r8.mVar
                r3.setArr((int) r2, (double) r5)
                goto L_0x0081
            L_0x007c:
                com.miui.maml.data.IndexedVariable r4 = r8.mVar
                r4.setArr((int) r2, (java.lang.Object) r3)
            L_0x0081:
                int r2 = r2 + 1
                goto L_0x0031
            L_0x0084:
                super.set((java.lang.Object) r9)
            L_0x0087:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.WebServiceBinder.Variable.set(java.lang.Object):void");
        }
    }

    public WebServiceBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private boolean canUseNetwork() {
        int i = this.mUseNetwork;
        if (i == 2) {
            return true;
        }
        return i != 0 && i == 1 && ConnectivityHelper.getInstance().isWifiConnected();
    }

    /* access modifiers changed from: private */
    public void handleException(Exception exc) {
        Log.e("WebServiceBinder", exc.toString());
        exc.printStackTrace();
        setErrorString(exc.toString());
    }

    private void load(Element element) {
        if (element != null) {
            if ("get".equalsIgnoreCase(element.getAttribute("requestMethod"))) {
                this.mRequestMethod = RequestMethod.GET;
            }
            this.mQueryAtStart = Boolean.parseBoolean(element.getAttribute("queryAtStart"));
            Variables variables = getVariables();
            Variables variables2 = variables;
            this.mUriFormatter = new TextFormatter(variables2, element.getAttribute("uri"), element.getAttribute("uriFormat"), element.getAttribute("uriParas"), Expression.build(variables, element.getAttribute("uriExp")), Expression.build(variables, element.getAttribute("uriFormatExp")));
            this.mParamsFormatter = new TextFormatter(variables, element.getAttribute("params"), element.getAttribute("paramsFormat"), element.getAttribute("paramsParas"));
            this.mUpdateInterval = Utils.getAttrAsInt(element, "updateInterval", -1);
            parseProtocol(element.getAttribute("protocol"));
            this.mAuthentication = Boolean.parseBoolean(element.getAttribute("authentication"));
            this.mSecure = Boolean.parseBoolean(element.getAttribute("secure"));
            this.mServiceId = element.getAttribute("serviceID");
            String attribute = element.getAttribute("useNetwork");
            if (TextUtils.isEmpty(attribute) || "all".equalsIgnoreCase(attribute)) {
                this.mUseNetwork = 2;
            } else if ("wifi".equalsIgnoreCase(attribute)) {
                this.mUseNetwork = 1;
            } else if ("none".equalsIgnoreCase(attribute)) {
                this.mUseNetwork = 0;
            } else {
                this.mUseNetworkExp = Expression.build(variables, attribute);
            }
            loadVariables(element);
            if (!TextUtils.isEmpty(this.mName)) {
                this.mStatusCodeVar = new IndexedVariable(this.mName + ".statusCode", variables, true);
                this.mErrorCodeVar = new IndexedVariable(this.mName + ".errorCode", variables, true);
                this.mErrorStringVar = new IndexedVariable(this.mName + ".errorString", variables, false);
                if (Boolean.parseBoolean(element.getAttribute("dbgContentString"))) {
                    this.mContentStringVar = new IndexedVariable(this.mName + ".contentString", variables, false);
                }
            }
            Element child = Utils.getChild(element, ListScreenElement.TAG_NAME);
            if (child != null) {
                try {
                    this.mList = new List(child, this.mRoot);
                } catch (IllegalArgumentException unused) {
                    Log.e("WebServiceBinder", "invalid List");
                }
            }
        } else {
            Log.e("WebServiceBinder", "WebServiceBinder node is null");
            throw new NullPointerException("node is null");
        }
    }

    private void parseProtocol(String str) {
        ResponseProtocol responseProtocol;
        if ("xml".equalsIgnoreCase(str)) {
            responseProtocol = ResponseProtocol.XML;
        } else if ("json/obj".equalsIgnoreCase(str)) {
            responseProtocol = ResponseProtocol.JSONobj;
        } else if ("json/array".equalsIgnoreCase(str)) {
            responseProtocol = ResponseProtocol.JSONarray;
        } else if ("bitmap".equalsIgnoreCase(str)) {
            responseProtocol = ResponseProtocol.BITMAP;
        } else {
            return;
        }
        this.mProtocol = responseProtocol;
    }

    /* access modifiers changed from: private */
    public void processResponseJson(String str) {
        Object obj;
        try {
            JSONPath jSONPath = this.mProtocol == ResponseProtocol.JSONobj ? new JSONPath(new JSONObject(str)) : new JSONPath(new JSONArray(str));
            Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
            while (it.hasNext()) {
                Variable variable = (Variable) it.next();
                variable.set(jSONPath.get(variable.mPath));
            }
            if (this.mList != null && (obj = jSONPath.get(this.mList.mDataPath)) != null && (obj instanceof JSONArray)) {
                this.mList.fill((JSONArray) obj);
            }
        } catch (JSONException e) {
            handleException(e);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00cc A[Catch:{ ParserConfigurationException -> 0x00db, SAXException -> 0x00d4, UnsupportedEncodingException -> 0x00cd, IOException -> 0x00c6, Exception -> 0x00bc, all -> 0x00b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00d3 A[Catch:{ ParserConfigurationException -> 0x00db, SAXException -> 0x00d4, UnsupportedEncodingException -> 0x00cd, IOException -> 0x00c6, Exception -> 0x00bc, all -> 0x00b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00da A[Catch:{ ParserConfigurationException -> 0x00db, SAXException -> 0x00d4, UnsupportedEncodingException -> 0x00cd, IOException -> 0x00c6, Exception -> 0x00bc, all -> 0x00b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00e5 A[SYNTHETIC, Splitter:B:61:0x00e5] */
    /* JADX WARNING: Removed duplicated region for block: B:70:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:71:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:72:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:73:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:41:0x00c2=Splitter:B:41:0x00c2, B:22:0x00a4=Splitter:B:22:0x00a4} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:44:0x00c7=Splitter:B:44:0x00c7, B:38:0x00bd=Splitter:B:38:0x00bd} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processResponseXml(java.lang.String r11) {
        /*
            r10 = this;
            javax.xml.xpath.XPathFactory r0 = javax.xml.xpath.XPathFactory.newInstance()
            javax.xml.xpath.XPath r0 = r0.newXPath()
            javax.xml.parsers.DocumentBuilderFactory r1 = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            r2 = 0
            javax.xml.parsers.DocumentBuilder r1 = r1.newDocumentBuilder()     // Catch:{ ParserConfigurationException -> 0x00db, SAXException -> 0x00d4, UnsupportedEncodingException -> 0x00cd, IOException -> 0x00c6, Exception -> 0x00bc }
            java.io.ByteArrayInputStream r3 = new java.io.ByteArrayInputStream     // Catch:{ ParserConfigurationException -> 0x00db, SAXException -> 0x00d4, UnsupportedEncodingException -> 0x00cd, IOException -> 0x00c6, Exception -> 0x00bc }
            java.lang.String r4 = "utf-8"
            byte[] r11 = r11.getBytes(r4)     // Catch:{ ParserConfigurationException -> 0x00db, SAXException -> 0x00d4, UnsupportedEncodingException -> 0x00cd, IOException -> 0x00c6, Exception -> 0x00bc }
            r3.<init>(r11)     // Catch:{ ParserConfigurationException -> 0x00db, SAXException -> 0x00d4, UnsupportedEncodingException -> 0x00cd, IOException -> 0x00c6, Exception -> 0x00bc }
            org.w3c.dom.Document r11 = r1.parse(r3)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.util.ArrayList<com.miui.maml.data.VariableBinder$Variable> r1 = r10.mVariables     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
        L_0x0026:
            boolean r4 = r1.hasNext()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r5 = " :"
            java.lang.String r6 = "WebServiceBinder"
            if (r4 == 0) goto L_0x0069
            java.lang.Object r4 = r1.next()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            com.miui.maml.data.VariableBinder$Variable r4 = (com.miui.maml.data.VariableBinder.Variable) r4     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            com.miui.maml.data.WebServiceBinder$Variable r4 = (com.miui.maml.data.WebServiceBinder.Variable) r4     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r7 = r4.mPath     // Catch:{ XPathExpressionException -> 0x0044 }
            javax.xml.namespace.QName r8 = javax.xml.xpath.XPathConstants.STRING     // Catch:{ XPathExpressionException -> 0x0044 }
            java.lang.Object r7 = r0.evaluate(r7, r11, r8)     // Catch:{ XPathExpressionException -> 0x0044 }
            r4.set(r7)     // Catch:{ XPathExpressionException -> 0x0044 }
            goto L_0x0026
        L_0x0044:
            r7 = move-exception
            r4.set(r2)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r8.<init>()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r9 = "fail to get variable: "
            r8.append(r9)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r4 = r4.mName     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r8.append(r4)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r8.append(r5)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r4 = r7.toString()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r8.append(r4)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r4 = r8.toString()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            android.util.Log.e(r6, r4)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            goto L_0x0026
        L_0x0069:
            com.miui.maml.data.WebServiceBinder$List r1 = r10.mList     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            if (r1 == 0) goto L_0x00a4
            com.miui.maml.data.WebServiceBinder$List r1 = r10.mList     // Catch:{ XPathExpressionException -> 0x007f }
            java.lang.String r1 = r1.mDataPath     // Catch:{ XPathExpressionException -> 0x007f }
            javax.xml.namespace.QName r2 = javax.xml.xpath.XPathConstants.NODESET     // Catch:{ XPathExpressionException -> 0x007f }
            java.lang.Object r11 = r0.evaluate(r1, r11, r2)     // Catch:{ XPathExpressionException -> 0x007f }
            org.w3c.dom.NodeList r11 = (org.w3c.dom.NodeList) r11     // Catch:{ XPathExpressionException -> 0x007f }
            com.miui.maml.data.WebServiceBinder$List r0 = r10.mList     // Catch:{ XPathExpressionException -> 0x007f }
            r0.fill((org.w3c.dom.NodeList) r11)     // Catch:{ XPathExpressionException -> 0x007f }
            goto L_0x00a4
        L_0x007f:
            r11 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r0.<init>()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r1 = "fail to get list: "
            r0.append(r1)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            com.miui.maml.data.WebServiceBinder$List r1 = r10.mList     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r1 = r1.mName     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r0.append(r1)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r0.append(r5)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r11 = r11.toString()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            r0.append(r11)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            java.lang.String r11 = r0.toString()     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
            android.util.Log.e(r6, r11)     // Catch:{ ParserConfigurationException -> 0x00b6, SAXException -> 0x00b3, UnsupportedEncodingException -> 0x00b0, IOException -> 0x00ad, Exception -> 0x00aa, all -> 0x00a8 }
        L_0x00a4:
            r3.close()     // Catch:{ IOException -> 0x00e2 }
            goto L_0x00e2
        L_0x00a8:
            r11 = move-exception
            goto L_0x00e3
        L_0x00aa:
            r11 = move-exception
            r2 = r3
            goto L_0x00bd
        L_0x00ad:
            r11 = move-exception
            r2 = r3
            goto L_0x00c7
        L_0x00b0:
            r11 = move-exception
            r2 = r3
            goto L_0x00ce
        L_0x00b3:
            r11 = move-exception
            r2 = r3
            goto L_0x00d5
        L_0x00b6:
            r11 = move-exception
            r2 = r3
            goto L_0x00dc
        L_0x00b9:
            r11 = move-exception
            r3 = r2
            goto L_0x00e3
        L_0x00bc:
            r11 = move-exception
        L_0x00bd:
            r10.handleException(r11)     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00e2
        L_0x00c2:
            r2.close()     // Catch:{ IOException -> 0x00e2 }
            goto L_0x00e2
        L_0x00c6:
            r11 = move-exception
        L_0x00c7:
            r10.handleException(r11)     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00e2
            goto L_0x00c2
        L_0x00cd:
            r11 = move-exception
        L_0x00ce:
            r10.handleException(r11)     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00e2
            goto L_0x00c2
        L_0x00d4:
            r11 = move-exception
        L_0x00d5:
            r10.handleException(r11)     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00e2
            goto L_0x00c2
        L_0x00db:
            r11 = move-exception
        L_0x00dc:
            r10.handleException(r11)     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00e2
            goto L_0x00c2
        L_0x00e2:
            return
        L_0x00e3:
            if (r3 == 0) goto L_0x00e8
            r3.close()     // Catch:{ IOException -> 0x00e8 }
        L_0x00e8:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.WebServiceBinder.processResponseXml(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public void setErrorCode(int i) {
        IndexedVariable indexedVariable = this.mErrorCodeVar;
        if (indexedVariable != null) {
            indexedVariable.set((double) i);
        }
        Log.e("WebServiceBinder", "QueryThread error: #" + i);
    }

    private void setErrorString(String str) {
        IndexedVariable indexedVariable = this.mErrorStringVar;
        if (indexedVariable != null) {
            indexedVariable.set((Object) str);
        }
    }

    /* access modifiers changed from: private */
    public void setStatusCode(int i) {
        IndexedVariable indexedVariable = this.mStatusCodeVar;
        if (indexedVariable != null) {
            indexedVariable.set((double) i);
        }
        Log.d("WebServiceBinder", "QueryThread status code: #" + i);
    }

    private void tryStartQuery() {
        int i;
        long currentTimeMillis = System.currentTimeMillis() - this.mLastQueryTime;
        if (currentTimeMillis < 0) {
            this.mLastQueryTime = 0;
        }
        if (this.mLastQueryTime == 0 || ((i = this.mUpdateInterval) > 0 && currentTimeMillis > ((long) (i * 1000)))) {
            startQuery();
        }
    }

    public void finish() {
        super.finish();
    }

    public void init() {
        super.init();
        this.mEncryptedUser = null;
        this.mServiceToken = null;
        this.mSecurity = null;
        Expression expression = this.mUseNetworkExp;
        if (expression != null) {
            this.mUseNetwork = (int) expression.evaluate();
        }
        if (this.mVariables.size() > 0) {
            ((Variable) this.mVariables.get(0)).loadCache(this.mRoot.getCacheDir());
        }
        if (this.mQueryAtStart) {
            this.mLastQueryTime = 0;
            tryStartQuery();
        }
    }

    /* access modifiers changed from: protected */
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    public void pause() {
        super.pause();
    }

    public void processResponseBitmap(SimpleRequest.StreamContent streamContent) {
        String str;
        if (this.mVariables.size() < 1) {
            Log.w("WebServiceBinder", "no image element var");
            return;
        }
        InputStream stream = streamContent.getStream();
        Variable variable = (Variable) this.mVariables.get(0);
        String cacheDir = this.mRoot.getCacheDir();
        if (variable.hasCache(cacheDir)) {
            variable.saveCache(stream, cacheDir);
            variable.loadCache(cacheDir);
        } else {
            Bitmap bitmap = null;
            if (stream != null) {
                bitmap = BitmapFactory.decodeStream(stream);
                if (bitmap == null) {
                    str = "decoded bitmap is null";
                }
                variable.set(bitmap);
            } else {
                str = "response stream is null";
            }
            Log.w("WebServiceBinder", str);
            variable.set(bitmap);
        }
        IOUtils.closeQuietly(stream);
    }

    public void refresh() {
        super.refresh();
        startQuery();
    }

    public void resume() {
        super.resume();
        if (this.mQueryAtStart) {
            tryStartQuery();
        }
    }

    public void startQuery() {
        if (!this.mRoot.getCapability(1)) {
            Log.w("WebServiceBinder", "capability disabled: webservice");
        } else if (!this.mQueryInProgress) {
            this.mLastQueryTime = System.currentTimeMillis();
            if (!canUseNetwork()) {
                IndexedVariable indexedVariable = this.mErrorCodeVar;
                if (indexedVariable != null) {
                    indexedVariable.set(3.0d);
                }
                IndexedVariable indexedVariable2 = this.mErrorStringVar;
                if (indexedVariable2 != null) {
                    indexedVariable2.set((Object) "cancel query because current network is forbidden by useNetwork config: " + this.mUseNetwork);
                }
                Log.w("WebServiceBinder", "cancel query because current network is forbidden by useNetwork config: " + this.mUseNetwork);
                return;
            }
            this.mQueryInProgress = true;
            this.mQueryThread = new QueryThread();
            this.mQueryThread.start();
        }
    }
}
