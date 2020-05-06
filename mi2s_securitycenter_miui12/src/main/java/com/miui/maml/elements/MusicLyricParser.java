package com.miui.maml.elements;

import android.text.Html;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;

public class MusicLyricParser {
    public static final String CRLF = "\r\n";
    private static final int INTERVAL_OF_LAST = 8000;
    private static final int LINE_PARSE_IGNORE = 1;
    private static final int LINE_PARSE_REGULAR = 2;
    private static final int LINE_PARSE_STOP = 0;
    public static final int MAX_VALID_TIME = 18000000;
    private static final String TAG_ALBUM = "al";
    private static final String TAG_ARTIST = "ar";
    private static final String TAG_EDITOR = "by";
    private static final Pattern TAG_EXTRA_LRC = Pattern.compile("<[0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2}>");
    private static final String TAG_OFFSET = "offset";
    private static final String TAG_TITLE = "ti";
    private static final String TAG_VERSION = "ve";

    static class EntityCompator implements Comparator<LyricEntity> {
        EntityCompator() {
        }

        public int compare(LyricEntity lyricEntity, LyricEntity lyricEntity2) {
            return lyricEntity.time - lyricEntity2.time;
        }
    }

    public static class Lyric {
        private final LyricEntity EMPTY_AFTER;
        private final LyricEntity EMPTY_BEFORE;
        /* access modifiers changed from: private */
        public final ArrayList<LyricEntity> mEntityList;
        private final LyricHeader mHeader;
        private boolean mIsModified;
        private LyricLocator mLyricLocator = new LyricLocator();
        private final long mOpenTime = System.currentTimeMillis();
        private int mOriginHeaderOffset;

        class LyricLine {
            CharSequence lyric;
            int pos;

            LyricLine() {
            }
        }

        class LyricLocator {
            final int CRLF_LENGTH = 2;
            String mFullLyric;
            ArrayList<LyricLine> mLyricLines;
            int[] mTimeArr;

            LyricLocator() {
            }

            private int getLineNumber(long j) {
                int i = 0;
                while (true) {
                    int[] iArr = this.mTimeArr;
                    if (i >= iArr.length) {
                        return -1;
                    }
                    if (j >= ((long) iArr[i])) {
                        if (j < (i < iArr.length + -1 ? (long) iArr[i + 1] : Long.MAX_VALUE)) {
                            return i;
                        }
                    }
                    i++;
                }
            }

            private void inflateLyricLines(ArrayList<CharSequence> arrayList) {
                int[] iArr = this.mTimeArr;
                if (iArr == null || arrayList == null || iArr.length != arrayList.size()) {
                    this.mTimeArr = null;
                    this.mLyricLines = null;
                    return;
                }
                this.mLyricLines = new ArrayList<>();
                int i = 0;
                while (i < this.mTimeArr.length) {
                    LyricLine lyricLine = new LyricLine();
                    lyricLine.lyric = arrayList.get(i);
                    LyricLine lyricLine2 = i > 0 ? this.mLyricLines.get(i - 1) : null;
                    lyricLine.pos = lyricLine2 != null ? this.CRLF_LENGTH + lyricLine2.pos + lyricLine2.lyric.length() : 0;
                    this.mLyricLines.add(lyricLine);
                    i++;
                }
                this.mFullLyric = "";
                for (int i2 = 0; i2 < this.mLyricLines.size(); i2++) {
                    this.mFullLyric += this.mLyricLines.get(i2).lyric + MusicLyricParser.CRLF;
                }
            }

            /* access modifiers changed from: package-private */
            public String getAfterLines(long j) {
                if (this.mTimeArr == null) {
                    return null;
                }
                int lineNumber = getLineNumber(j);
                if (lineNumber < 0) {
                    return this.mFullLyric;
                }
                if (lineNumber >= this.mTimeArr.length - 1) {
                    return null;
                }
                LyricLine lyricLine = this.mLyricLines.get(lineNumber);
                return this.mFullLyric.substring(lyricLine.pos + lyricLine.lyric.length() + this.CRLF_LENGTH, this.mFullLyric.length());
            }

            /* access modifiers changed from: package-private */
            public String getBeforeLines(long j) {
                int lineNumber;
                if (this.mTimeArr != null && (lineNumber = getLineNumber(j)) > 0) {
                    return this.mFullLyric.substring(0, this.mLyricLines.get(lineNumber).pos - this.CRLF_LENGTH);
                }
                return null;
            }

            /* access modifiers changed from: package-private */
            public String getLastLine(long j) {
                int lineNumber;
                if (this.mTimeArr == null || (lineNumber = getLineNumber(j)) <= 0) {
                    return null;
                }
                LyricLine lyricLine = this.mLyricLines.get(lineNumber - 1);
                String str = this.mFullLyric;
                int i = lyricLine.pos;
                return str.substring(i, lyricLine.lyric.length() + i);
            }

            /* access modifiers changed from: package-private */
            public String getLine(long j) {
                int lineNumber;
                if (this.mTimeArr == null || (lineNumber = getLineNumber(j)) == -1) {
                    return null;
                }
                LyricLine lyricLine = this.mLyricLines.get(lineNumber);
                String str = this.mFullLyric;
                int i = lyricLine.pos;
                return str.substring(i, lyricLine.lyric.length() + i);
            }

            /* access modifiers changed from: package-private */
            public String getNextLine(long j) {
                int lineNumber;
                if (this.mTimeArr == null || (lineNumber = getLineNumber(j)) < -1 || lineNumber >= this.mTimeArr.length - 1) {
                    return null;
                }
                LyricLine lyricLine = this.mLyricLines.get(lineNumber + 1);
                String str = this.mFullLyric;
                int i = lyricLine.pos;
                return str.substring(i, lyricLine.lyric.length() + i);
            }

            /* access modifiers changed from: package-private */
            public void set(int[] iArr, ArrayList<CharSequence> arrayList) {
                this.mTimeArr = iArr;
                inflateLyricLines(arrayList);
            }
        }

        public Lyric(LyricHeader lyricHeader, ArrayList<LyricEntity> arrayList, boolean z) {
            this.mHeader = lyricHeader;
            this.mOriginHeaderOffset = this.mHeader.offset;
            this.mEntityList = arrayList;
            this.mIsModified = z;
            this.EMPTY_BEFORE = new LyricEntity(-1, "\n");
            this.EMPTY_AFTER = new LyricEntity(arrayList.size(), "\n");
        }

        private long getTimeFromLyricShot(int i, double d2) {
            int size = size() - 1;
            return i >= size ? ((long) (this.mEntityList.get(size).time + ((i - size) * 8000))) + ((long) (d2 * 8000.0d)) : (long) (((double) this.mEntityList.get(i).time) + (((double) (this.mEntityList.get(i + 1).time - this.mEntityList.get(i).time)) * d2));
        }

        public void addOffset(int i) {
            this.mHeader.offset += i;
            this.mIsModified = true;
        }

        public void correctLyric(LyricShot lyricShot, int i, double d2) {
            int i2;
            int size = size();
            if (i >= 0 && i <= size && (i2 = lyricShot.lineIndex) >= 0 && i2 <= size) {
                long timeFromLyricShot = getTimeFromLyricShot(i2, lyricShot.percent);
                long timeFromLyricShot2 = getTimeFromLyricShot(i, d2);
                boolean z = true;
                int i3 = lyricShot.lineIndex;
                if (i > i3 || (i == i3 && d2 > lyricShot.percent)) {
                    z = false;
                }
                if (!z && timeFromLyricShot > timeFromLyricShot2) {
                    return;
                }
                if (!z || timeFromLyricShot >= timeFromLyricShot2) {
                    addOffset((int) (timeFromLyricShot - timeFromLyricShot2));
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0009, code lost:
            r0 = r4.mEntityList;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void decorate() {
            /*
                r4 = this;
                java.util.ArrayList<com.miui.maml.elements.MusicLyricParser$LyricEntity> r0 = r4.mEntityList
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0009
                return
            L_0x0009:
                java.util.ArrayList<com.miui.maml.elements.MusicLyricParser$LyricEntity> r0 = r4.mEntityList
                int r1 = r0.size()
                if (r1 <= 0) goto L_0x002d
                r2 = 0
                java.lang.Object r3 = r0.get(r2)
                com.miui.maml.elements.MusicLyricParser$LyricEntity r3 = (com.miui.maml.elements.MusicLyricParser.LyricEntity) r3
                boolean r3 = r3.isDecorated()
                if (r3 == 0) goto L_0x001f
                goto L_0x002d
            L_0x001f:
                if (r2 >= r1) goto L_0x002d
                java.lang.Object r3 = r0.get(r2)
                com.miui.maml.elements.MusicLyricParser$LyricEntity r3 = (com.miui.maml.elements.MusicLyricParser.LyricEntity) r3
                r3.decorate()
                int r2 = r2 + 1
                goto L_0x001f
            L_0x002d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.MusicLyricParser.Lyric.decorate():void");
        }

        public String getAfterLines(long j) {
            return this.mLyricLocator.getAfterLines(j);
        }

        public String getBeforeLines(long j) {
            return this.mLyricLocator.getBeforeLines(j);
        }

        public String getLastLine(long j) {
            return this.mLyricLocator.getLastLine(j);
        }

        public String getLine(long j) {
            return this.mLyricLocator.getLine(j);
        }

        public LyricEntity getLyricContent(int i) {
            return i < 0 ? this.EMPTY_BEFORE : i >= this.mEntityList.size() ? this.EMPTY_AFTER : this.mEntityList.get(i);
        }

        public LyricShot getLyricShot(long j) {
            int i = this.mHeader.offset;
            double d2 = 0.0d;
            if (((long) (this.mEntityList.get(0).time + i)) > j) {
                return new LyricShot(0, 0.0d);
            }
            for (int i2 = 1; i2 < this.mEntityList.size(); i2++) {
                int i3 = this.mEntityList.get(i2).time + i;
                if (((long) i3) > j) {
                    int i4 = i2 - 1;
                    int i5 = this.mEntityList.get(i4).time + i;
                    if (i3 > i5) {
                        d2 = ((double) (j - ((long) i5))) / ((double) (i3 - i5));
                    }
                    return new LyricShot(i4, d2);
                }
            }
            long j2 = j - ((long) (this.mEntityList.get(size() - 1).time + i));
            if (j2 >= 8000) {
                return new LyricShot(this.mEntityList.size(), 0.0d);
            }
            return new LyricShot(size() - 1, ((double) j2) / 8000.0d);
        }

        public String getNextLine(long j) {
            return this.mLyricLocator.getNextLine(j);
        }

        public long getOpenTime() {
            return this.mOpenTime;
        }

        public ArrayList<CharSequence> getStringArr() {
            if (this.mEntityList.isEmpty()) {
                return null;
            }
            ArrayList<CharSequence> arrayList = new ArrayList<>(this.mEntityList.size());
            Iterator<LyricEntity> it = this.mEntityList.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().lyric);
            }
            return arrayList;
        }

        public int[] getTimeArr() {
            if (this.mEntityList.isEmpty()) {
                return null;
            }
            int[] iArr = new int[this.mEntityList.size()];
            int i = 0;
            Iterator<LyricEntity> it = this.mEntityList.iterator();
            while (it.hasNext()) {
                iArr[i] = it.next().time + this.mHeader.offset;
                i++;
            }
            return iArr;
        }

        public boolean isModified() {
            return this.mIsModified;
        }

        public void recycleContent() {
            this.mEntityList.clear();
        }

        public void resetHeaderOffset() {
            this.mHeader.offset = this.mOriginHeaderOffset;
        }

        public void set(int[] iArr, ArrayList<CharSequence> arrayList) {
            this.mLyricLocator.set(iArr, arrayList);
        }

        public int size() {
            return this.mEntityList.size();
        }
    }

    public static class LyricEntity {
        private static final String HTML_BR_PATTERN = "%s<br/>";
        public CharSequence lyric;
        public int time;

        public LyricEntity(int i, String str) {
            this.time = i;
            this.lyric = str;
        }

        public void decorate() {
            this.lyric = Html.fromHtml(String.format(HTML_BR_PATTERN, new Object[]{this.lyric}));
        }

        public boolean isDecorated() {
            return !(this.lyric instanceof String);
        }
    }

    public static class LyricHeader {
        public String album;
        public String artist;
        public String editor;
        public int offset;
        public String title;
        public String version;
    }

    public static class LyricShot {
        public int lineIndex;
        public double percent;

        public LyricShot(int i, double d2) {
            this.lineIndex = i;
            this.percent = d2;
        }
    }

    private static void correctTime(Lyric lyric) {
        if (lyric != null) {
            ArrayList access$000 = lyric.mEntityList;
            int size = access$000.size();
            if (size > 1 && ((LyricEntity) access$000.get(0)).time == ((LyricEntity) access$000.get(1)).time) {
                ((LyricEntity) access$000.get(0)).time = ((LyricEntity) access$000.get(1)).time / 2;
            }
            int i = 1;
            while (i < size - 1) {
                int i2 = i + 1;
                if (((LyricEntity) access$000.get(i)).time == ((LyricEntity) access$000.get(i2)).time) {
                    ((LyricEntity) access$000.get(i)).time = (((LyricEntity) access$000.get(i - 1)).time + ((LyricEntity) access$000.get(i2)).time) / 2;
                }
                i = i2;
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r2v1 */
    /* JADX WARNING: type inference failed for: r2v3, types: [int] */
    /* JADX WARNING: type inference failed for: r2v4 */
    /* JADX WARNING: type inference failed for: r2v6 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.miui.maml.elements.MusicLyricParser.Lyric doParse(java.lang.String r7) {
        /*
            com.miui.maml.elements.MusicLyricParser$LyricHeader r0 = new com.miui.maml.elements.MusicLyricParser$LyricHeader
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.lang.String r2 = "\r\n"
            java.lang.String[] r7 = r7.split(r2)
            r2 = 0
            if (r7 == 0) goto L_0x0028
            int r3 = r7.length
            r4 = r2
        L_0x0015:
            if (r2 >= r3) goto L_0x0027
            r5 = r7[r2]
            int r5 = parseLine(r5, r0, r1)
            if (r5 != 0) goto L_0x0020
            goto L_0x0027
        L_0x0020:
            r6 = 1
            if (r5 != r6) goto L_0x0024
            r4 = r6
        L_0x0024:
            int r2 = r2 + 1
            goto L_0x0015
        L_0x0027:
            r2 = r4
        L_0x0028:
            boolean r7 = r1.isEmpty()
            if (r7 != 0) goto L_0x003c
            com.miui.maml.elements.MusicLyricParser$EntityCompator r7 = new com.miui.maml.elements.MusicLyricParser$EntityCompator
            r7.<init>()
            java.util.Collections.sort(r1, r7)
            com.miui.maml.elements.MusicLyricParser$Lyric r7 = new com.miui.maml.elements.MusicLyricParser$Lyric
            r7.<init>(r0, r1, r2)
            goto L_0x003d
        L_0x003c:
            r7 = 0
        L_0x003d:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.MusicLyricParser.doParse(java.lang.String):com.miui.maml.elements.MusicLyricParser$Lyric");
    }

    private static int parseEntity(String[] strArr, ArrayList<LyricEntity> arrayList, String str) {
        try {
            int parseDouble = (int) (Double.parseDouble(strArr[strArr.length - 1]) * 1000.0d);
            int i = 0;
            int i2 = 60;
            for (int length = strArr.length - 2; length >= 0; length--) {
                int parseInt = Integer.parseInt(strArr[length]) * i2;
                i2 *= 60;
                i += parseInt;
            }
            int i3 = parseDouble + (i * 1000);
            if (i3 < 18000000) {
                arrayList.add(new LyricEntity(i3, str));
            }
            return 2;
        } catch (NumberFormatException unused) {
            return 1;
        }
    }

    private static int parseHeader(String str, LyricHeader lyricHeader) {
        int indexOf = str.indexOf(":");
        if (indexOf < 0 || indexOf >= str.length() - 1) {
            return 1;
        }
        String substring = str.substring(0, indexOf);
        String substring2 = str.substring(indexOf + 1);
        if (substring.equals(TAG_ALBUM)) {
            lyricHeader.album = substring2;
        } else if (substring.equals(TAG_ARTIST)) {
            lyricHeader.artist = substring2;
        } else if (substring.equals(TAG_TITLE)) {
            lyricHeader.title = substring2;
        } else if (substring.equals(TAG_EDITOR)) {
            lyricHeader.editor = substring2;
        } else if (substring.equals(TAG_VERSION)) {
            lyricHeader.version = substring2;
        } else if (!substring.equals(TAG_OFFSET)) {
            return 1;
        } else {
            try {
                lyricHeader.offset = Integer.parseInt(substring2);
            } catch (NumberFormatException unused) {
                return 1;
            }
        }
        return 2;
    }

    private static int parseLine(String str, LyricHeader lyricHeader, ArrayList<LyricEntity> arrayList) {
        String replaceAll;
        int lastIndexOf;
        String trim = str.trim();
        if (TextUtils.isEmpty(trim) || (lastIndexOf = replaceAll.lastIndexOf("]")) == -1) {
            return 1;
        }
        String substring = (replaceAll = TAG_EXTRA_LRC.matcher(trim).replaceAll("")).substring(lastIndexOf + 1);
        int indexOf = replaceAll.indexOf("[");
        if (indexOf == -1) {
            return 1;
        }
        int i = 2;
        for (String str2 : replaceAll.substring(indexOf, lastIndexOf).split("]")) {
            if (str2.startsWith("[")) {
                String substring2 = str2.substring(1);
                String[] split = substring2.split(":");
                if (split.length >= 2) {
                    i = TextUtils.isDigitsOnly(split[0]) ? parseEntity(split, arrayList, substring) : parseHeader(substring2, lyricHeader);
                }
            }
        }
        return i;
    }

    public static Lyric parseLyric(String str) {
        Lyric lyric = null;
        if (str == null) {
            return null;
        }
        try {
            lyric = doParse(str);
            correctTime(lyric);
            return lyric;
        } catch (Exception e) {
            e.printStackTrace();
            return lyric;
        }
    }
}
