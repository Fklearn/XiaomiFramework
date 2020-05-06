package com.android.server.firewall;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.server.EventLogTags;
import com.android.server.IntentResolver;
import com.android.server.power.IntentFirewallInjector;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class IntentFirewall {
    private static final int LOG_PACKAGES_MAX_LENGTH = 150;
    private static final int LOG_PACKAGES_SUFFICIENT_LENGTH = 125;
    private static final File RULES_DIR = new File(Environment.getDataSystemDirectory(), "ifw");
    static final String TAG = "IntentFirewall";
    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_BROADCAST = "broadcast";
    private static final String TAG_RULES = "rules";
    private static final String TAG_SERVICE = "service";
    private static final int TYPE_ACTIVITY = 0;
    private static final int TYPE_BROADCAST = 1;
    private static final int TYPE_SERVICE = 2;
    private static final HashMap<String, FilterFactory> factoryMap;
    private FirewallIntentResolver mActivityResolver = new FirewallIntentResolver();
    private final AMSInterface mAms;
    private FirewallIntentResolver mBroadcastResolver = new FirewallIntentResolver();
    final FirewallHandler mHandler;
    private final RuleObserver mObserver;
    private FirewallIntentResolver mServiceResolver = new FirewallIntentResolver();

    public interface AMSInterface {
        int checkComponentPermission(String str, int i, int i2, int i3, boolean z);

        Object getAMSLock();
    }

    static {
        FilterFactory[] factories = {AndFilter.FACTORY, OrFilter.FACTORY, NotFilter.FACTORY, StringFilter.ACTION, StringFilter.COMPONENT, StringFilter.COMPONENT_NAME, StringFilter.COMPONENT_PACKAGE, StringFilter.DATA, StringFilter.HOST, StringFilter.MIME_TYPE, StringFilter.SCHEME, StringFilter.PATH, StringFilter.SSP, CategoryFilter.FACTORY, SenderFilter.FACTORY, SenderPackageFilter.FACTORY, SenderPermissionFilter.FACTORY, PortFilter.FACTORY};
        factoryMap = new HashMap<>((factories.length * 4) / 3);
        for (FilterFactory factory : factories) {
            factoryMap.put(factory.getTagName(), factory);
        }
    }

    public IntentFirewall(AMSInterface ams, Handler handler) {
        this.mAms = ams;
        this.mHandler = new FirewallHandler(handler.getLooper());
        File rulesDir = getRulesDir();
        rulesDir.mkdirs();
        readRulesDir(rulesDir);
        this.mObserver = new RuleObserver(rulesDir);
        this.mObserver.startWatching();
    }

    public boolean checkStartActivity(Intent intent, int callerUid, int callerPid, String resolvedType, ApplicationInfo resolvedApp) {
        return checkIntent(this.mActivityResolver, intent.getComponent(), 0, intent, callerUid, callerPid, resolvedType, resolvedApp.uid);
    }

    public boolean checkService(ComponentName resolvedService, Intent intent, int callerUid, int callerPid, String resolvedType, ApplicationInfo resolvedApp) {
        return checkIntent(this.mServiceResolver, resolvedService, 2, intent, callerUid, callerPid, resolvedType, resolvedApp.uid);
    }

    public boolean checkBroadcast(Intent intent, int callerUid, int callerPid, String resolvedType, int receivingUid) {
        return checkIntent(this.mBroadcastResolver, intent.getComponent(), 1, intent, callerUid, callerPid, resolvedType, receivingUid);
    }

    public boolean checkIntent(FirewallIntentResolver resolver, ComponentName resolvedComponent, int intentType, Intent intent, int callerUid, int callerPid, String resolvedType, int receivingUid) {
        List<Rule> candidateRules;
        FirewallIntentResolver firewallIntentResolver = resolver;
        int i = intentType;
        Intent intent2 = intent;
        int i2 = callerUid;
        String str = resolvedType;
        List<Rule> candidateRules2 = firewallIntentResolver.queryIntent(intent2, str, false, 0);
        if (candidateRules2 == null) {
            candidateRules = new ArrayList<>();
        } else {
            candidateRules = candidateRules2;
        }
        firewallIntentResolver.queryByComponent(resolvedComponent, candidateRules);
        boolean log = false;
        boolean block = false;
        int i3 = 0;
        while (true) {
            if (i3 >= candidateRules.size()) {
                int i4 = i3;
                break;
            }
            Rule rule = candidateRules.get(i3);
            int i5 = i3;
            if (rule.matches(this, resolvedComponent, intent, callerUid, callerPid, resolvedType, receivingUid)) {
                block |= rule.getBlock();
                log |= rule.getLog();
                if (block && log) {
                    break;
                }
            }
            i3 = i5 + 1;
        }
        if (log) {
            logIntent(i, intent2, i2, str);
        }
        if (!block && !IntentFirewallInjector.checkIntentForFrozenUid(i, i2, callerPid, receivingUid)) {
            return true;
        }
        return false;
    }

    private static void logIntent(int intentType, Intent intent, int callerUid, String resolvedType) {
        String shortComponent;
        String callerPackages;
        int callerPackageCount;
        ComponentName cn = intent.getComponent();
        if (cn != null) {
            shortComponent = cn.flattenToShortString();
        } else {
            shortComponent = null;
        }
        String callerPackages2 = null;
        int callerPackageCount2 = 0;
        IPackageManager pm = AppGlobals.getPackageManager();
        if (pm != null) {
            try {
                String[] callerPackagesArray = pm.getPackagesForUid(callerUid);
                if (callerPackagesArray != null) {
                    callerPackageCount2 = callerPackagesArray.length;
                    callerPackages2 = joinPackages(callerPackagesArray);
                }
                callerPackages = callerPackages2;
                callerPackageCount = callerPackageCount2;
            } catch (RemoteException ex) {
                Slog.e(TAG, "Remote exception while retrieving packages", ex);
                callerPackages = null;
                callerPackageCount = 0;
            }
        } else {
            int i = callerUid;
            callerPackages = null;
            callerPackageCount = 0;
        }
        EventLogTags.writeIfwIntentMatched(intentType, shortComponent, callerUid, callerPackageCount, callerPackages, intent.getAction(), resolvedType, intent.getDataString(), intent.getFlags());
    }

    private static String joinPackages(String[] packages) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String pkg : packages) {
            if (sb.length() + pkg.length() + 1 < 150) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(pkg);
            } else if (sb.length() >= 125) {
                return sb.toString();
            }
        }
        if (sb.length() != 0 || packages.length <= 0) {
            return null;
        }
        String pkg2 = packages[0];
        return pkg2.substring((pkg2.length() - 150) + 1) + '-';
    }

    public static File getRulesDir() {
        return RULES_DIR;
    }

    /* access modifiers changed from: private */
    public void readRulesDir(File rulesDir) {
        FirewallIntentResolver[] resolvers = new FirewallIntentResolver[3];
        for (int i = 0; i < resolvers.length; i++) {
            resolvers[i] = new FirewallIntentResolver();
        }
        File[] files = rulesDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".xml")) {
                    readRules(file, resolvers);
                }
            }
        }
        Slog.i(TAG, "Read new rules (A:" + resolvers[0].filterSet().size() + " B:" + resolvers[1].filterSet().size() + " S:" + resolvers[2].filterSet().size() + ")");
        synchronized (this.mAms.getAMSLock()) {
            this.mActivityResolver = resolvers[0];
            this.mBroadcastResolver = resolvers[1];
            this.mServiceResolver = resolvers[2];
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00f9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        android.util.Slog.e(TAG, "Error reading intent firewall rules from " + r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0111, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0112, code lost:
        android.util.Slog.e(TAG, "Error while closing " + r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0127, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
        android.util.Slog.e(TAG, "Error reading intent firewall rules from " + r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x013f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0140, code lost:
        android.util.Slog.e(TAG, "Error while closing " + r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00f9 A[ExcHandler: IOException (r0v10 'e' java.io.IOException A[CUSTOM_DECLARE]), Splitter:B:6:0x0024] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readRules(java.io.File r17, com.android.server.firewall.IntentFirewall.FirewallIntentResolver[] r18) {
        /*
            r16 = this;
            r1 = r17
            java.lang.String r2 = "Error reading intent firewall rules from "
            java.lang.String r3 = "Error while closing "
            java.lang.String r4 = "IntentFirewall"
            java.util.ArrayList r0 = new java.util.ArrayList
            r5 = 3
            r0.<init>(r5)
            r6 = r0
            r0 = 0
        L_0x0010:
            if (r0 >= r5) goto L_0x001d
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r6.add(r7)
            int r0 = r0 + 1
            goto L_0x0010
        L_0x001d:
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x016f }
            r0.<init>(r1)     // Catch:{ FileNotFoundException -> 0x016f }
            r5 = r0
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            r7 = r0
            r8 = 0
            r7.setInput(r5, r8)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            java.lang.String r0 = "rules"
            com.android.internal.util.XmlUtils.beginDocument(r7, r0)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            int r0 = r7.getDepth()     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            r9 = r0
        L_0x0038:
            boolean r0 = com.android.internal.util.XmlUtils.nextElementWithin(r7, r9)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            if (r0 == 0) goto L_0x0097
            r0 = -1
            java.lang.String r10 = r7.getName()     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            java.lang.String r11 = "activity"
            boolean r11 = r10.equals(r11)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            if (r11 == 0) goto L_0x004e
            r0 = 0
            r11 = r0
            goto L_0x0066
        L_0x004e:
            java.lang.String r11 = "broadcast"
            boolean r11 = r10.equals(r11)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            if (r11 == 0) goto L_0x0059
            r0 = 1
            r11 = r0
            goto L_0x0066
        L_0x0059:
            java.lang.String r11 = "service"
            boolean r11 = r10.equals(r11)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            if (r11 == 0) goto L_0x0065
            r0 = 2
            r11 = r0
            goto L_0x0066
        L_0x0065:
            r11 = r0
        L_0x0066:
            r0 = -1
            if (r11 == r0) goto L_0x0096
            com.android.server.firewall.IntentFirewall$Rule r0 = new com.android.server.firewall.IntentFirewall$Rule     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            r0.<init>()     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            r12 = r0
            java.lang.Object r0 = r6.get(r11)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            r13 = r0
            r12.readFromXml((org.xmlpull.v1.XmlPullParser) r7)     // Catch:{ XmlPullParserException -> 0x007e, IOException -> 0x00f9 }
            r13.add(r12)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            goto L_0x0096
        L_0x007e:
            r0 = move-exception
            r14 = r0
            r0 = r14
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            r14.<init>()     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            java.lang.String r15 = "Error reading an intent firewall rule from "
            r14.append(r15)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            r14.append(r1)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            java.lang.String r14 = r14.toString()     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            android.util.Slog.e(r4, r14, r0)     // Catch:{ XmlPullParserException -> 0x0127, IOException -> 0x00f9 }
            goto L_0x0038
        L_0x0096:
            goto L_0x0038
        L_0x0097:
            r5.close()     // Catch:{ IOException -> 0x009b }
            goto L_0x00b1
        L_0x009b:
            r0 = move-exception
            r2 = r0
            r0 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            android.util.Slog.e(r4, r2, r0)
        L_0x00b1:
            r0 = 0
        L_0x00b2:
            int r2 = r6.size()
            if (r0 >= r2) goto L_0x00f5
            java.lang.Object r2 = r6.get(r0)
            java.util.List r2 = (java.util.List) r2
            r3 = r18[r0]
            r4 = 0
        L_0x00c1:
            int r7 = r2.size()
            if (r4 >= r7) goto L_0x00f2
            java.lang.Object r7 = r2.get(r4)
            com.android.server.firewall.IntentFirewall$Rule r7 = (com.android.server.firewall.IntentFirewall.Rule) r7
            r8 = 0
        L_0x00ce:
            int r9 = r7.getIntentFilterCount()
            if (r8 >= r9) goto L_0x00de
            com.android.server.firewall.IntentFirewall$FirewallIntentFilter r9 = r7.getIntentFilter(r8)
            r3.addFilter(r9)
            int r8 = r8 + 1
            goto L_0x00ce
        L_0x00de:
            r8 = 0
        L_0x00df:
            int r9 = r7.getComponentFilterCount()
            if (r8 >= r9) goto L_0x00ef
            android.content.ComponentName r9 = r7.getComponentFilter(r8)
            r3.addComponentFilter(r9, r7)
            int r8 = r8 + 1
            goto L_0x00df
        L_0x00ef:
            int r4 = r4 + 1
            goto L_0x00c1
        L_0x00f2:
            int r0 = r0 + 1
            goto L_0x00b2
        L_0x00f5:
            return
        L_0x00f6:
            r0 = move-exception
            r2 = r0
            goto L_0x0155
        L_0x00f9:
            r0 = move-exception
            r7 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f6 }
            r0.<init>()     // Catch:{ all -> 0x00f6 }
            r0.append(r2)     // Catch:{ all -> 0x00f6 }
            r0.append(r1)     // Catch:{ all -> 0x00f6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00f6 }
            android.util.Slog.e(r4, r0, r7)     // Catch:{ all -> 0x00f6 }
            r5.close()     // Catch:{ IOException -> 0x0111 }
            goto L_0x0126
        L_0x0111:
            r0 = move-exception
            r2 = r0
            r0 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            android.util.Slog.e(r4, r2, r0)
        L_0x0126:
            return
        L_0x0127:
            r0 = move-exception
            r7 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f6 }
            r0.<init>()     // Catch:{ all -> 0x00f6 }
            r0.append(r2)     // Catch:{ all -> 0x00f6 }
            r0.append(r1)     // Catch:{ all -> 0x00f6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00f6 }
            android.util.Slog.e(r4, r0, r7)     // Catch:{ all -> 0x00f6 }
            r5.close()     // Catch:{ IOException -> 0x013f }
            goto L_0x0154
        L_0x013f:
            r0 = move-exception
            r2 = r0
            r0 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            android.util.Slog.e(r4, r2, r0)
        L_0x0154:
            return
        L_0x0155:
            r5.close()     // Catch:{ IOException -> 0x0159 }
            goto L_0x016e
        L_0x0159:
            r0 = move-exception
            r7 = r0
            r0 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            r7.append(r1)
            java.lang.String r3 = r7.toString()
            android.util.Slog.e(r4, r3, r0)
        L_0x016e:
            throw r2
        L_0x016f:
            r0 = move-exception
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.firewall.IntentFirewall.readRules(java.io.File, com.android.server.firewall.IntentFirewall$FirewallIntentResolver[]):void");
    }

    static Filter parseFilter(XmlPullParser parser) throws IOException, XmlPullParserException {
        String elementName = parser.getName();
        FilterFactory factory = factoryMap.get(elementName);
        if (factory != null) {
            return factory.newFilter(parser);
        }
        throw new XmlPullParserException("Unknown element in filter list: " + elementName);
    }

    private static class Rule extends AndFilter {
        private static final String ATTR_BLOCK = "block";
        private static final String ATTR_LOG = "log";
        private static final String ATTR_NAME = "name";
        private static final String TAG_COMPONENT_FILTER = "component-filter";
        private static final String TAG_INTENT_FILTER = "intent-filter";
        private boolean block;
        private boolean log;
        private final ArrayList<ComponentName> mComponentFilters;
        private final ArrayList<FirewallIntentFilter> mIntentFilters;

        private Rule() {
            this.mIntentFilters = new ArrayList<>(1);
            this.mComponentFilters = new ArrayList<>(0);
        }

        public Rule readFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
            this.block = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_BLOCK));
            this.log = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_LOG));
            super.readFromXml(parser);
            return this;
        }

        /* access modifiers changed from: protected */
        public void readChild(XmlPullParser parser) throws IOException, XmlPullParserException {
            String currentTag = parser.getName();
            if (currentTag.equals(TAG_INTENT_FILTER)) {
                FirewallIntentFilter intentFilter = new FirewallIntentFilter(this);
                intentFilter.readFromXml(parser);
                this.mIntentFilters.add(intentFilter);
            } else if (currentTag.equals(TAG_COMPONENT_FILTER)) {
                String componentStr = parser.getAttributeValue((String) null, "name");
                if (componentStr != null) {
                    ComponentName componentName = ComponentName.unflattenFromString(componentStr);
                    if (componentName != null) {
                        this.mComponentFilters.add(componentName);
                        return;
                    }
                    throw new XmlPullParserException("Invalid component name: " + componentStr);
                }
                throw new XmlPullParserException("Component name must be specified.", parser, (Throwable) null);
            } else {
                super.readChild(parser);
            }
        }

        public int getIntentFilterCount() {
            return this.mIntentFilters.size();
        }

        public FirewallIntentFilter getIntentFilter(int index) {
            return this.mIntentFilters.get(index);
        }

        public int getComponentFilterCount() {
            return this.mComponentFilters.size();
        }

        public ComponentName getComponentFilter(int index) {
            return this.mComponentFilters.get(index);
        }

        public boolean getBlock() {
            return this.block;
        }

        public boolean getLog() {
            return this.log;
        }
    }

    private static class FirewallIntentFilter extends IntentFilter {
        /* access modifiers changed from: private */
        public final Rule rule;

        public FirewallIntentFilter(Rule rule2) {
            this.rule = rule2;
        }
    }

    private static class FirewallIntentResolver extends IntentResolver<FirewallIntentFilter, Rule> {
        private final ArrayMap<ComponentName, Rule[]> mRulesByComponent;

        private FirewallIntentResolver() {
            this.mRulesByComponent = new ArrayMap<>(0);
        }

        /* access modifiers changed from: protected */
        public boolean allowFilterResult(FirewallIntentFilter filter, List<Rule> dest) {
            return !dest.contains(filter.rule);
        }

        /* access modifiers changed from: protected */
        public boolean isPackageForFilter(String packageName, FirewallIntentFilter filter) {
            return true;
        }

        /* access modifiers changed from: protected */
        public FirewallIntentFilter[] newArray(int size) {
            return new FirewallIntentFilter[size];
        }

        /* access modifiers changed from: protected */
        public Rule newResult(FirewallIntentFilter filter, int match, int userId) {
            return filter.rule;
        }

        /* access modifiers changed from: protected */
        public void sortResults(List<Rule> list) {
        }

        public void queryByComponent(ComponentName componentName, List<Rule> candidateRules) {
            Rule[] rules = this.mRulesByComponent.get(componentName);
            if (rules != null) {
                candidateRules.addAll(Arrays.asList(rules));
            }
        }

        public void addComponentFilter(ComponentName componentName, Rule rule) {
            ArrayMap<ComponentName, Rule[]> arrayMap = this.mRulesByComponent;
            arrayMap.put(componentName, (Rule[]) ArrayUtils.appendElement(Rule.class, this.mRulesByComponent.get(componentName), rule));
        }
    }

    private final class FirewallHandler extends Handler {
        public FirewallHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            IntentFirewall.this.readRulesDir(IntentFirewall.getRulesDir());
        }
    }

    private class RuleObserver extends FileObserver {
        private static final int MONITORED_EVENTS = 968;

        public RuleObserver(File monitoredDir) {
            super(monitoredDir.getAbsolutePath(), MONITORED_EVENTS);
        }

        public void onEvent(int event, String path) {
            if (path.endsWith(".xml")) {
                IntentFirewall.this.mHandler.removeMessages(0);
                IntentFirewall.this.mHandler.sendEmptyMessageDelayed(0, 250);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean checkComponentPermission(String permission, int pid, int uid, int owningUid, boolean exported) {
        return this.mAms.checkComponentPermission(permission, pid, uid, owningUid, exported) == 0;
    }

    /* access modifiers changed from: package-private */
    public boolean signaturesMatch(int uid1, int uid2) {
        try {
            if (AppGlobals.getPackageManager().checkUidSignatures(uid1, uid2) == 0) {
                return true;
            }
            return false;
        } catch (RemoteException ex) {
            Slog.e(TAG, "Remote exception while checking signatures", ex);
            return false;
        }
    }
}
