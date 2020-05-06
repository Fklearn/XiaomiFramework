package com.android.server;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.server.am.SplitScreenReporter;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.FastImmutableArraySet;
import android.util.LogPrinter;
import android.util.MutableInt;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.FastPrintWriter;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class IntentResolver<F extends IntentFilter, R> {
    private static final boolean DEBUG = false;
    private static final String TAG = "IntentResolver";
    private static final boolean localLOGV = false;
    private static final boolean localVerificationLOGV = false;
    private static final Comparator mResolvePrioritySorter = new Comparator() {
        public int compare(Object o1, Object o2) {
            int q1 = ((IntentFilter) o1).getPriority();
            int q2 = ((IntentFilter) o2).getPriority();
            if (q1 > q2) {
                return -1;
            }
            return q1 < q2 ? 1 : 0;
        }
    };
    private final ArrayMap<String, F[]> mActionToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mBaseTypeToFilter = new ArrayMap<>();
    private final ArraySet<F> mFilters = new ArraySet<>();
    private final ArrayMap<String, F[]> mSchemeToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mTypeToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mTypedActionToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mWildTypeToFilter = new ArrayMap<>();

    /* access modifiers changed from: protected */
    public abstract boolean isPackageForFilter(String str, F f);

    /* access modifiers changed from: protected */
    public abstract F[] newArray(int i);

    public void addFilter(F f) {
        this.mFilters.add(f);
        int numS = register_intent_filter(f, f.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
        int numT = register_mime_types(f, "      Type: ");
        if (numS == 0 && numT == 0) {
            register_intent_filter(f, f.actionsIterator(), this.mActionToFilter, "      Action: ");
        }
        if (numT != 0) {
            register_intent_filter(f, f.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
        }
    }

    public static boolean filterEquals(IntentFilter f1, IntentFilter f2) {
        int s1 = f1.countActions();
        if (s1 != f2.countActions()) {
            return false;
        }
        for (int i = 0; i < s1; i++) {
            if (!f2.hasAction(f1.getAction(i))) {
                return false;
            }
        }
        int s12 = f1.countCategories();
        if (s12 != f2.countCategories()) {
            return false;
        }
        for (int i2 = 0; i2 < s12; i2++) {
            if (!f2.hasCategory(f1.getCategory(i2))) {
                return false;
            }
        }
        int s13 = f1.countDataTypes();
        if (s13 != f2.countDataTypes()) {
            return false;
        }
        for (int i3 = 0; i3 < s13; i3++) {
            if (!f2.hasExactDataType(f1.getDataType(i3))) {
                return false;
            }
        }
        int s14 = f1.countDataSchemes();
        if (s14 != f2.countDataSchemes()) {
            return false;
        }
        for (int i4 = 0; i4 < s14; i4++) {
            if (!f2.hasDataScheme(f1.getDataScheme(i4))) {
                return false;
            }
        }
        int s15 = f1.countDataAuthorities();
        if (s15 != f2.countDataAuthorities()) {
            return false;
        }
        for (int i5 = 0; i5 < s15; i5++) {
            if (!f2.hasDataAuthority(f1.getDataAuthority(i5))) {
                return false;
            }
        }
        int s16 = f1.countDataPaths();
        if (s16 != f2.countDataPaths()) {
            return false;
        }
        for (int i6 = 0; i6 < s16; i6++) {
            if (!f2.hasDataPath(f1.getDataPath(i6))) {
                return false;
            }
        }
        int s17 = f1.countDataSchemeSpecificParts();
        if (s17 != f2.countDataSchemeSpecificParts()) {
            return false;
        }
        for (int i7 = 0; i7 < s17; i7++) {
            if (!f2.hasDataSchemeSpecificPart(f1.getDataSchemeSpecificPart(i7))) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<F> collectFilters(F[] array, IntentFilter matching) {
        F cur;
        ArrayList<F> res = null;
        if (array != null) {
            int i = 0;
            while (i < array.length && (cur = array[i]) != null) {
                if (filterEquals(cur, matching)) {
                    if (res == null) {
                        res = new ArrayList<>();
                    }
                    res.add(cur);
                }
                i++;
            }
        }
        return res;
    }

    public ArrayList<F> findFilters(IntentFilter matching) {
        if (matching.countDataSchemes() == 1) {
            return collectFilters((IntentFilter[]) this.mSchemeToFilter.get(matching.getDataScheme(0)), matching);
        }
        if (matching.countDataTypes() != 0 && matching.countActions() == 1) {
            return collectFilters((IntentFilter[]) this.mTypedActionToFilter.get(matching.getAction(0)), matching);
        }
        if (matching.countDataTypes() == 0 && matching.countDataSchemes() == 0 && matching.countActions() == 1) {
            return collectFilters((IntentFilter[]) this.mActionToFilter.get(matching.getAction(0)), matching);
        }
        ArrayList<F> res = null;
        Iterator<F> it = this.mFilters.iterator();
        while (it.hasNext()) {
            F cur = (IntentFilter) it.next();
            if (filterEquals(cur, matching)) {
                if (res == null) {
                    res = new ArrayList<>();
                }
                res.add(cur);
            }
        }
        return res;
    }

    public void removeFilter(F f) {
        removeFilterInternal(f);
        this.mFilters.remove(f);
    }

    /* access modifiers changed from: package-private */
    public void removeFilterInternal(F f) {
        int numS = unregister_intent_filter(f, f.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
        int numT = unregister_mime_types(f, "      Type: ");
        if (numS == 0 && numT == 0) {
            unregister_intent_filter(f, f.actionsIterator(), this.mActionToFilter, "      Action: ");
        }
        if (numT != 0) {
            unregister_intent_filter(f, f.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean dumpMap(PrintWriter out, String titlePrefix, String title, String prefix, ArrayMap<String, F[]> map, String packageName, boolean printFilter, boolean collapseDuplicates) {
        String str;
        String str2;
        Printer printer;
        String str3;
        boolean printedSomething;
        Printer printer2;
        boolean filter;
        String str4;
        boolean printedSomething2;
        boolean printedHeader;
        Printer printer3;
        F filter2;
        IntentResolver intentResolver = this;
        PrintWriter printWriter = out;
        String str5 = prefix;
        ArrayMap<String, F[]> arrayMap = map;
        String str6 = packageName;
        StringBuilder sb = new StringBuilder();
        sb.append(str5);
        String str7 = "  ";
        sb.append(str7);
        String eprefix = sb.toString();
        String fprefix = str5 + "    ";
        ArrayMap<Object, MutableInt> found = new ArrayMap<>();
        boolean printedSomething3 = false;
        int mapi = 0;
        Printer printer4 = null;
        String title2 = title;
        while (mapi < map.size()) {
            F[] a = (IntentFilter[]) arrayMap.valueAt(mapi);
            int N = a.length;
            boolean printedHeader2 = false;
            if (!collapseDuplicates || printFilter) {
                String str8 = str7;
                boolean printedSomething4 = printedSomething3;
                Printer printer5 = printer4;
                boolean printedHeader3 = false;
                int i = 0;
                String title3 = title2;
                while (true) {
                    if (i >= N) {
                        str = str8;
                        break;
                    }
                    F f = a[i];
                    F filter3 = f;
                    if (f == null) {
                        str = str8;
                        break;
                    }
                    if (str6 == null || intentResolver.isPackageForFilter(str6, filter3)) {
                        if (title3 != null) {
                            out.print(titlePrefix);
                            printWriter.println(title3);
                            title3 = null;
                        }
                        if (!printedHeader3) {
                            printWriter.print(eprefix);
                            printWriter.print(arrayMap.keyAt(mapi));
                            printWriter.println(":");
                            printedHeader3 = true;
                        }
                        intentResolver.dumpFilter(printWriter, fprefix, filter3);
                        if (printFilter) {
                            if (printer5 == null) {
                                printer = new PrintWriterPrinter(printWriter);
                            } else {
                                printer = printer5;
                            }
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(fprefix);
                            str2 = str8;
                            sb2.append(str2);
                            filter3.dump(printer, sb2.toString());
                            printedSomething4 = true;
                            printer5 = printer;
                        } else {
                            str2 = str8;
                            printedSomething4 = true;
                        }
                    } else {
                        str2 = str8;
                    }
                    i++;
                    intentResolver = this;
                    str8 = str2;
                    printWriter = out;
                }
                title2 = title3;
                printer4 = printer5;
                printedSomething3 = printedSomething4;
            } else {
                found.clear();
                String title4 = title2;
                int i2 = 0;
                while (true) {
                    if (i2 >= N) {
                        str3 = str7;
                        printedSomething = printedSomething3;
                        printer2 = printer4;
                        filter = printedHeader2;
                        break;
                    }
                    F f2 = a[i2];
                    F filter4 = f2;
                    if (f2 == null) {
                        str3 = str7;
                        printedSomething = printedSomething3;
                        printer2 = printer4;
                        F f3 = filter4;
                        filter = printedHeader2;
                        break;
                    }
                    if (str6 != null) {
                        printer3 = printer4;
                        filter2 = filter4;
                        if (!intentResolver.isPackageForFilter(str6, filter2)) {
                            str4 = str7;
                            printedSomething2 = printedSomething3;
                            F f4 = filter2;
                            printedHeader = printedHeader2;
                            i2++;
                            printer4 = printer3;
                            printedHeader2 = printedHeader;
                            printedSomething3 = printedSomething2;
                            str7 = str4;
                        }
                    } else {
                        printer3 = printer4;
                        filter2 = filter4;
                    }
                    printedHeader = printedHeader2;
                    Object label = intentResolver.filterToLabel(filter2);
                    F f5 = filter2;
                    int index = found.indexOfKey(label);
                    printedSomething2 = printedSomething3;
                    if (index < 0) {
                        str4 = str7;
                        found.put(label, new MutableInt(1));
                    } else {
                        str4 = str7;
                        int i3 = index;
                        found.valueAt(index).value++;
                    }
                    i2++;
                    printer4 = printer3;
                    printedHeader2 = printedHeader;
                    printedSomething3 = printedSomething2;
                    str7 = str4;
                }
                String title5 = title4;
                for (int i4 = 0; i4 < found.size(); i4++) {
                    if (title5 != null) {
                        out.print(titlePrefix);
                        printWriter.println(title5);
                        title5 = null;
                    }
                    if (!filter) {
                        printWriter.print(eprefix);
                        printWriter.print(arrayMap.keyAt(mapi));
                        printWriter.println(":");
                        filter = true;
                    }
                    printedSomething = true;
                    intentResolver.dumpFilterLabel(printWriter, fprefix, found.keyAt(i4), found.valueAt(i4).value);
                }
                title2 = title5;
                printer4 = printer2;
                printedSomething3 = printedSomething;
                str = str3;
            }
            mapi++;
            intentResolver = this;
            String str9 = prefix;
            str7 = str;
            printWriter = out;
        }
        return printedSomething3;
    }

    /* access modifiers changed from: package-private */
    public void writeProtoMap(ProtoOutputStream proto, long fieldId, ArrayMap<String, F[]> map) {
        int N = map.size();
        for (int mapi = 0; mapi < N; mapi++) {
            long token = proto.start(fieldId);
            proto.write(1138166333441L, map.keyAt(mapi));
            for (F f : (IntentFilter[]) map.valueAt(mapi)) {
                if (f != null) {
                    proto.write(2237677961218L, f.toString());
                }
            }
            proto.end(token);
        }
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        writeProtoMap(proto, 2246267895809L, this.mTypeToFilter);
        writeProtoMap(proto, 2246267895810L, this.mBaseTypeToFilter);
        writeProtoMap(proto, 2246267895811L, this.mWildTypeToFilter);
        writeProtoMap(proto, 2246267895812L, this.mSchemeToFilter);
        writeProtoMap(proto, 2246267895813L, this.mActionToFilter);
        writeProtoMap(proto, 2246267895814L, this.mTypedActionToFilter);
        proto.end(token);
    }

    public boolean dump(PrintWriter out, String title, String prefix, String packageName, boolean printFilter, boolean collapseDuplicates) {
        String str = prefix;
        String innerPrefix = str + "  ";
        String sepPrefix = "\n" + str;
        String curPrefix = title + "\n" + str;
        if (dumpMap(out, curPrefix, "Full MIME Types:", innerPrefix, this.mTypeToFilter, packageName, printFilter, collapseDuplicates)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Base MIME Types:", innerPrefix, this.mBaseTypeToFilter, packageName, printFilter, collapseDuplicates)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Wild MIME Types:", innerPrefix, this.mWildTypeToFilter, packageName, printFilter, collapseDuplicates)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Schemes:", innerPrefix, this.mSchemeToFilter, packageName, printFilter, collapseDuplicates)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Non-Data Actions:", innerPrefix, this.mActionToFilter, packageName, printFilter, collapseDuplicates)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "MIME Typed Actions:", innerPrefix, this.mTypedActionToFilter, packageName, printFilter, collapseDuplicates)) {
            curPrefix = sepPrefix;
        }
        return curPrefix == sepPrefix;
    }

    private class IteratorWrapper implements Iterator<F> {
        private F mCur;
        private final Iterator<F> mI;

        IteratorWrapper(Iterator<F> it) {
            this.mI = it;
        }

        public boolean hasNext() {
            return this.mI.hasNext();
        }

        public F next() {
            F f = (IntentFilter) this.mI.next();
            this.mCur = f;
            return f;
        }

        public void remove() {
            F f = this.mCur;
            if (f != null) {
                IntentResolver.this.removeFilterInternal(f);
            }
            this.mI.remove();
        }
    }

    public Iterator<F> filterIterator() {
        return new IteratorWrapper(this.mFilters.iterator());
    }

    public Set<F> filterSet() {
        return Collections.unmodifiableSet(this.mFilters);
    }

    public List<R> queryIntentFromList(Intent intent, String resolvedType, boolean defaultOnly, ArrayList<F[]> listCut, int userId) {
        ArrayList<R> resultList = new ArrayList<>();
        boolean debug = (intent.getFlags() & 8) != 0;
        FastImmutableArraySet<String> categories = getFastIntentCategories(intent);
        String scheme = intent.getScheme();
        int N = listCut.size();
        for (int i = 0; i < N; i++) {
            buildResolveList(intent, categories, debug, defaultOnly, resolvedType, scheme, (IntentFilter[]) listCut.get(i), resultList, userId);
        }
        filterResults(resultList);
        sortResults(resultList);
        return resultList;
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x01c9  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x022b  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0247  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<R> queryIntent(android.content.Intent r22, java.lang.String r23, boolean r24, int r25) {
        /*
            r21 = this;
            r10 = r21
            r11 = r23
            java.lang.String r12 = r22.getScheme()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r13 = r0
            int r0 = r22.getFlags()
            r0 = r0 & 8
            r1 = 0
            if (r0 == 0) goto L_0x001a
            r0 = 1
            goto L_0x001b
        L_0x001a:
            r0 = r1
        L_0x001b:
            r14 = r0
            java.lang.String r15 = "IntentResolver"
            if (r14 == 0) goto L_0x005b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Resolving type="
            r0.append(r2)
            r0.append(r11)
            java.lang.String r2 = " scheme="
            r0.append(r2)
            r0.append(r12)
            java.lang.String r2 = " defaultOnly="
            r0.append(r2)
            r9 = r24
            r0.append(r9)
            java.lang.String r2 = " userId="
            r0.append(r2)
            r8 = r25
            r0.append(r8)
            java.lang.String r2 = " of "
            r0.append(r2)
            r7 = r22
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Slog.v(r15, r0)
            goto L_0x0061
        L_0x005b:
            r7 = r22
            r9 = r24
            r8 = r25
        L_0x0061:
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            if (r11 == 0) goto L_0x0180
            r5 = 47
            int r5 = r11.indexOf(r5)
            if (r5 <= 0) goto L_0x0177
            java.lang.String r1 = r11.substring(r1, r5)
            java.lang.String r6 = "*"
            boolean r16 = r1.equals(r6)
            if (r16 != 0) goto L_0x0142
            r16 = r0
            int r0 = r23.length()
            r17 = r2
            int r2 = r5 + 2
            r18 = r3
            java.lang.String r3 = "Second type cut: "
            r19 = r4
            java.lang.String r4 = "First type cut: "
            if (r0 != r2) goto L_0x00db
            int r0 = r5 + 1
            char r0 = r11.charAt(r0)
            r2 = 42
            if (r0 == r2) goto L_0x009a
            goto L_0x00db
        L_0x009a:
            android.util.ArrayMap<java.lang.String, F[]> r0 = r10.mBaseTypeToFilter
            java.lang.Object r0 = r0.get(r1)
            android.content.IntentFilter[] r0 = (android.content.IntentFilter[]) r0
            if (r14 == 0) goto L_0x00ba
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            java.lang.String r4 = java.util.Arrays.toString(r0)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            android.util.Slog.v(r15, r2)
        L_0x00ba:
            android.util.ArrayMap<java.lang.String, F[]> r2 = r10.mWildTypeToFilter
            java.lang.Object r2 = r2.get(r1)
            android.content.IntentFilter[] r2 = (android.content.IntentFilter[]) r2
            if (r14 == 0) goto L_0x011b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = java.util.Arrays.toString(r2)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            android.util.Slog.v(r15, r3)
            goto L_0x011b
        L_0x00db:
            android.util.ArrayMap<java.lang.String, F[]> r0 = r10.mTypeToFilter
            java.lang.Object r0 = r0.get(r11)
            android.content.IntentFilter[] r0 = (android.content.IntentFilter[]) r0
            if (r14 == 0) goto L_0x00fb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            java.lang.String r4 = java.util.Arrays.toString(r0)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            android.util.Slog.v(r15, r2)
        L_0x00fb:
            android.util.ArrayMap<java.lang.String, F[]> r2 = r10.mWildTypeToFilter
            java.lang.Object r2 = r2.get(r1)
            android.content.IntentFilter[] r2 = (android.content.IntentFilter[]) r2
            if (r14 == 0) goto L_0x011b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = java.util.Arrays.toString(r2)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            android.util.Slog.v(r15, r3)
        L_0x011b:
            android.util.ArrayMap<java.lang.String, F[]> r3 = r10.mWildTypeToFilter
            java.lang.Object r3 = r3.get(r6)
            android.content.IntentFilter[] r3 = (android.content.IntentFilter[]) r3
            if (r14 == 0) goto L_0x013d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "Third type cut: "
            r4.append(r6)
            java.lang.String r6 = java.util.Arrays.toString(r3)
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            android.util.Slog.v(r15, r4)
        L_0x013d:
            r17 = r2
            r18 = r3
            goto L_0x018a
        L_0x0142:
            r16 = r0
            r17 = r2
            r18 = r3
            r19 = r4
            java.lang.String r0 = r22.getAction()
            if (r0 == 0) goto L_0x0188
            android.util.ArrayMap<java.lang.String, F[]> r0 = r10.mTypedActionToFilter
            java.lang.String r2 = r22.getAction()
            java.lang.Object r0 = r0.get(r2)
            android.content.IntentFilter[] r0 = (android.content.IntentFilter[]) r0
            if (r14 == 0) goto L_0x018a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Typed Action list: "
            r2.append(r3)
            java.lang.String r3 = java.util.Arrays.toString(r0)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.v(r15, r2)
            goto L_0x018a
        L_0x0177:
            r16 = r0
            r17 = r2
            r18 = r3
            r19 = r4
            goto L_0x0188
        L_0x0180:
            r16 = r0
            r17 = r2
            r18 = r3
            r19 = r4
        L_0x0188:
            r0 = r16
        L_0x018a:
            if (r12 == 0) goto L_0x01b0
            android.util.ArrayMap<java.lang.String, F[]> r1 = r10.mSchemeToFilter
            java.lang.Object r1 = r1.get(r12)
            android.content.IntentFilter[] r1 = (android.content.IntentFilter[]) r1
            if (r14 == 0) goto L_0x01ae
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Scheme list: "
            r2.append(r3)
            java.lang.String r3 = java.util.Arrays.toString(r1)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.v(r15, r2)
        L_0x01ae:
            r19 = r1
        L_0x01b0:
            if (r11 != 0) goto L_0x01e1
            if (r12 != 0) goto L_0x01e1
            java.lang.String r1 = r22.getAction()
            if (r1 == 0) goto L_0x01e1
            android.util.ArrayMap<java.lang.String, F[]> r1 = r10.mActionToFilter
            java.lang.String r2 = r22.getAction()
            java.lang.Object r1 = r1.get(r2)
            r0 = r1
            android.content.IntentFilter[] r0 = (android.content.IntentFilter[]) r0
            if (r14 == 0) goto L_0x01e1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Action list: "
            r1.append(r2)
            java.lang.String r2 = java.util.Arrays.toString(r0)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Slog.v(r15, r1)
        L_0x01e1:
            r16 = r0
            android.util.FastImmutableArraySet r20 = getFastIntentCategories(r22)
            if (r16 == 0) goto L_0x01fd
            r0 = r21
            r1 = r22
            r2 = r20
            r3 = r14
            r4 = r24
            r5 = r23
            r6 = r12
            r7 = r16
            r8 = r13
            r9 = r25
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x01fd:
            if (r17 == 0) goto L_0x0213
            r0 = r21
            r1 = r22
            r2 = r20
            r3 = r14
            r4 = r24
            r5 = r23
            r6 = r12
            r7 = r17
            r8 = r13
            r9 = r25
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x0213:
            if (r18 == 0) goto L_0x0229
            r0 = r21
            r1 = r22
            r2 = r20
            r3 = r14
            r4 = r24
            r5 = r23
            r6 = r12
            r7 = r18
            r8 = r13
            r9 = r25
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x0229:
            if (r19 == 0) goto L_0x023f
            r0 = r21
            r1 = r22
            r2 = r20
            r3 = r14
            r4 = r24
            r5 = r23
            r6 = r12
            r7 = r19
            r8 = r13
            r9 = r25
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x023f:
            r10.filterResults(r13)
            r10.sortResults(r13)
            if (r14 == 0) goto L_0x026e
            java.lang.String r0 = "Final result list:"
            android.util.Slog.v(r15, r0)
            r0 = 0
        L_0x024d:
            int r1 = r13.size()
            if (r0 >= r1) goto L_0x026e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "  "
            r1.append(r2)
            java.lang.Object r2 = r13.get(r0)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Slog.v(r15, r1)
            int r0 = r0 + 1
            goto L_0x024d
        L_0x026e:
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.IntentResolver.queryIntent(android.content.Intent, java.lang.String, boolean, int):java.util.List");
    }

    /* access modifiers changed from: protected */
    public boolean allowFilterResult(F f, List<R> list) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isFilterStopped(F f, int userId) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isFilterVerified(F filter) {
        return filter.isVerified();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [R, F] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public R newResult(F r1, int r2, int r3) {
        /*
            r0 = this;
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.IntentResolver.newResult(android.content.IntentFilter, int, int):java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public void sortResults(List<R> results) {
        Collections.sort(results, mResolvePrioritySorter);
    }

    /* access modifiers changed from: protected */
    public void filterResults(List<R> list) {
    }

    /* access modifiers changed from: protected */
    public void dumpFilter(PrintWriter out, String prefix, F filter) {
        out.print(prefix);
        out.println(filter);
    }

    /* access modifiers changed from: protected */
    public Object filterToLabel(F f) {
        return "IntentFilter";
    }

    /* access modifiers changed from: protected */
    public void dumpFilterLabel(PrintWriter out, String prefix, Object label, int count) {
        out.print(prefix);
        out.print(label);
        out.print(": ");
        out.println(count);
    }

    private final void addFilter(ArrayMap<String, F[]> map, String name, F filter) {
        F[] array = (IntentFilter[]) map.get(name);
        if (array == null) {
            F[] array2 = newArray(2);
            map.put(name, array2);
            array2[0] = filter;
            return;
        }
        int N = array.length;
        int i = N;
        while (i > 0 && array[i - 1] == null) {
            i--;
        }
        if (i < N) {
            array[i] = filter;
            return;
        }
        F[] newa = newArray((N * 3) / 2);
        System.arraycopy(array, 0, newa, 0, N);
        newa[N] = filter;
        map.put(name, newa);
    }

    private final int register_mime_types(F filter, String prefix) {
        Iterator<String> i = filter.typesIterator();
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            String name = i.next();
            num++;
            String baseName = name;
            int slashpos = name.indexOf(47);
            if (slashpos > 0) {
                baseName = name.substring(0, slashpos).intern();
            } else {
                name = name + "/*";
            }
            addFilter(this.mTypeToFilter, name, filter);
            if (slashpos > 0) {
                addFilter(this.mBaseTypeToFilter, baseName, filter);
            } else {
                addFilter(this.mWildTypeToFilter, baseName, filter);
            }
        }
        return num;
    }

    private final int unregister_mime_types(F filter, String prefix) {
        Iterator<String> i = filter.typesIterator();
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            String name = i.next();
            num++;
            String baseName = name;
            int slashpos = name.indexOf(47);
            if (slashpos > 0) {
                baseName = name.substring(0, slashpos).intern();
            } else {
                name = name + "/*";
            }
            remove_all_objects(this.mTypeToFilter, name, filter);
            if (slashpos > 0) {
                remove_all_objects(this.mBaseTypeToFilter, baseName, filter);
            } else {
                remove_all_objects(this.mWildTypeToFilter, baseName, filter);
            }
        }
        return num;
    }

    private final int register_intent_filter(F filter, Iterator<String> i, ArrayMap<String, F[]> dest, String prefix) {
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            num++;
            addFilter(dest, i.next(), filter);
        }
        return num;
    }

    private final int unregister_intent_filter(F filter, Iterator<String> i, ArrayMap<String, F[]> dest, String prefix) {
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            num++;
            remove_all_objects(dest, i.next(), filter);
        }
        return num;
    }

    private final void remove_all_objects(ArrayMap<String, F[]> map, String name, Object object) {
        F[] array = (IntentFilter[]) map.get(name);
        if (array != null) {
            int LAST = array.length - 1;
            while (LAST >= 0 && array[LAST] == null) {
                LAST--;
            }
            for (int idx = LAST; idx >= 0; idx--) {
                if (array[idx] == object) {
                    int remain = LAST - idx;
                    if (remain > 0) {
                        System.arraycopy(array, idx + 1, array, idx, remain);
                    }
                    array[LAST] = null;
                    LAST--;
                }
            }
            if (LAST < 0) {
                map.remove(name);
            } else if (LAST < array.length / 2) {
                F[] newa = newArray(LAST + 2);
                System.arraycopy(array, 0, newa, 0, LAST + 1);
                map.put(name, newa);
            }
        }
    }

    private static FastImmutableArraySet<String> getFastIntentCategories(Intent intent) {
        Set<String> categories = intent.getCategories();
        if (categories == null) {
            return null;
        }
        return new FastImmutableArraySet<>((String[]) categories.toArray(new String[categories.size()]));
    }

    private void buildResolveList(Intent intent, FastImmutableArraySet<String> categories, boolean debug, boolean defaultOnly, String resolvedType, String scheme, F[] src, List<R> dest, int userId) {
        Printer logPrinter;
        PrintWriter logPrintWriter;
        String packageName;
        Uri data;
        int N;
        int i;
        String action;
        Printer logPrinter2;
        PrintWriter logPrintWriter2;
        String reason;
        F[] fArr = src;
        List<R> list = dest;
        int i2 = userId;
        String filter = intent.getAction();
        Uri data2 = intent.getData();
        String packageName2 = intent.getPackage();
        boolean excludingStopped = intent.isExcludingStopped();
        if (debug) {
            Printer logPrinter3 = new LogPrinter(2, TAG, 3);
            logPrinter = logPrinter3;
            logPrintWriter = new FastPrintWriter(logPrinter3);
        } else {
            logPrinter = null;
            logPrintWriter = null;
        }
        int N2 = fArr != null ? fArr.length : 0;
        boolean hasNonDefaults = false;
        int i3 = 0;
        while (true) {
            if (i3 >= N2) {
                int i4 = N2;
                String str = filter;
                Uri uri = data2;
                String str2 = packageName2;
                PrintWriter printWriter = logPrintWriter;
                Printer printer = logPrinter;
                break;
            }
            F f = fArr[i3];
            F filter2 = f;
            if (f == null) {
                int i5 = i3;
                int i6 = N2;
                String str3 = filter;
                Uri uri2 = data2;
                String str4 = packageName2;
                String action2 = filter2;
                PrintWriter printWriter2 = logPrintWriter;
                Printer printer2 = logPrinter;
                break;
            }
            if (debug) {
                Slog.v(TAG, "Matching against filter " + filter2);
            }
            if (!excludingStopped || !isFilterStopped(filter2, i2)) {
                if (packageName2 == null || isPackageForFilter(packageName2, filter2)) {
                    if (filter2.getAutoVerify() && debug) {
                        Slog.v(TAG, "  Filter verified: " + isFilterVerified(filter2));
                        int authorities = filter2.countDataAuthorities();
                        int z = 0;
                        while (z < authorities) {
                            Slog.v(TAG, "   " + filter2.getDataAuthority(z).getHost());
                            z++;
                            F[] fArr2 = src;
                            authorities = authorities;
                        }
                    }
                    if (allowFilterResult(filter2, list)) {
                        String str5 = filter;
                        action = filter;
                        F filter3 = filter2;
                        i = i3;
                        N = N2;
                        Uri uri3 = data2;
                        data = data2;
                        logPrintWriter2 = logPrintWriter;
                        packageName = packageName2;
                        logPrinter2 = logPrinter;
                        int match = filter2.match(str5, resolvedType, scheme, uri3, categories, TAG);
                        if (match >= 0) {
                            if (debug) {
                                Slog.v(TAG, "  Filter matched!  match=0x" + Integer.toHexString(match) + " hasDefault=" + filter3.hasCategory("android.intent.category.DEFAULT"));
                            }
                            if (!defaultOnly || filter3.hasCategory("android.intent.category.DEFAULT")) {
                                R oneResult = newResult(filter3, match, i2);
                                if (debug) {
                                    Slog.v(TAG, "    Created result: " + oneResult);
                                }
                                if (oneResult != null) {
                                    list.add(oneResult);
                                    if (debug) {
                                        dumpFilter(logPrintWriter2, "    ", filter3);
                                        logPrintWriter2.flush();
                                        filter3.dump(logPrinter2, "    ");
                                    }
                                }
                            } else {
                                hasNonDefaults = true;
                            }
                        } else if (debug) {
                            if (match == -4) {
                                reason = "category";
                            } else if (match == -3) {
                                reason = SplitScreenReporter.STR_ACTION;
                            } else if (match == -2) {
                                reason = "data";
                            } else if (match != -1) {
                                reason = "unknown reason";
                            } else {
                                reason = DatabaseHelper.SoundModelContract.KEY_TYPE;
                            }
                            Slog.v(TAG, "  Filter did not match: " + reason);
                        }
                    } else if (debug) {
                        Slog.v(TAG, "  Filter's target already added");
                        i = i3;
                        N = N2;
                        action = filter;
                        data = data2;
                        packageName = packageName2;
                        String action3 = filter2;
                        logPrintWriter2 = logPrintWriter;
                        logPrinter2 = logPrinter;
                    } else {
                        i = i3;
                        N = N2;
                        action = filter;
                        data = data2;
                        packageName = packageName2;
                        String action4 = filter2;
                        logPrintWriter2 = logPrintWriter;
                        logPrinter2 = logPrinter;
                    }
                } else if (debug) {
                    Slog.v(TAG, "  Filter is not from package " + packageName2 + "; skipping");
                    i = i3;
                    N = N2;
                    action = filter;
                    data = data2;
                    packageName = packageName2;
                    String action5 = filter2;
                    logPrintWriter2 = logPrintWriter;
                    logPrinter2 = logPrinter;
                } else {
                    i = i3;
                    N = N2;
                    action = filter;
                    data = data2;
                    packageName = packageName2;
                    String action6 = filter2;
                    logPrintWriter2 = logPrintWriter;
                    logPrinter2 = logPrinter;
                }
            } else if (debug) {
                Slog.v(TAG, "  Filter's target is stopped; skipping");
                i = i3;
                N = N2;
                action = filter;
                data = data2;
                packageName = packageName2;
                String action7 = filter2;
                logPrintWriter2 = logPrintWriter;
                logPrinter2 = logPrinter;
            } else {
                i = i3;
                N = N2;
                action = filter;
                data = data2;
                packageName = packageName2;
                String action8 = filter2;
                logPrintWriter2 = logPrintWriter;
                logPrinter2 = logPrinter;
            }
            i3 = i + 1;
            fArr = src;
            logPrintWriter = logPrintWriter2;
            logPrinter = logPrinter2;
            filter = action;
            N2 = N;
            data2 = data;
            packageName2 = packageName;
        }
        if (debug && hasNonDefaults) {
            if (dest.size() == 0) {
                Slog.v(TAG, "resolveIntent failed: found match, but none with CATEGORY_DEFAULT");
            } else if (dest.size() > 1) {
                Slog.v(TAG, "resolveIntent: multiple matches, only some with CATEGORY_DEFAULT");
            }
        }
    }
}
