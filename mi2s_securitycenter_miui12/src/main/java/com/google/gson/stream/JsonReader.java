package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class JsonReader implements Closeable {
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_EOF = 17;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private final char[] buffer = new char[1024];
    private final Reader in;
    private boolean lenient = false;
    private int limit = 0;
    private int lineNumber = 0;
    private int lineStart = 0;
    /* access modifiers changed from: private */
    public int peeked = 0;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int pos = 0;
    private int[] stack = new int[32];
    private int stackSize = 0;

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
            public void promoteNameToValue(JsonReader jsonReader) {
                int i;
                if (jsonReader instanceof JsonTreeReader) {
                    ((JsonTreeReader) jsonReader).promoteNameToValue();
                    return;
                }
                int access$000 = jsonReader.peeked;
                if (access$000 == 0) {
                    access$000 = jsonReader.doPeek();
                }
                if (access$000 == 13) {
                    i = 9;
                } else if (access$000 == 12) {
                    i = 8;
                } else if (access$000 == 14) {
                    i = 10;
                } else {
                    throw new IllegalStateException("Expected a name but was " + jsonReader.peek() + " " + " at line " + jsonReader.getLineNumber() + " column " + jsonReader.getColumnNumber());
                }
                int unused = jsonReader.peeked = i;
            }
        };
    }

    public JsonReader(Reader reader) {
        int[] iArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        iArr[i] = 6;
        if (reader != null) {
            this.in = reader;
            return;
        }
        throw new NullPointerException("in == null");
    }

    private void checkLenient() {
        if (!this.lenient) {
            syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
            throw null;
        }
    }

    private void consumeNonExecutePrefix() {
        nextNonWhitespace(true);
        this.pos--;
        int i = this.pos;
        char[] cArr = NON_EXECUTE_PREFIX;
        if (i + cArr.length <= this.limit || fillBuffer(cArr.length)) {
            int i2 = 0;
            while (true) {
                char[] cArr2 = NON_EXECUTE_PREFIX;
                if (i2 >= cArr2.length) {
                    this.pos += cArr2.length;
                    return;
                } else if (this.buffer[this.pos + i2] == cArr2[i2]) {
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0113  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int doPeek() {
        /*
            r16 = this;
            r0 = r16
            int[] r1 = r0.stack
            int r2 = r0.stackSize
            int r3 = r2 + -1
            r3 = r1[r3]
            r4 = 8
            r5 = 39
            r6 = 34
            r7 = 93
            r8 = 3
            r9 = 7
            r10 = 59
            r11 = 44
            r12 = 4
            r13 = 2
            r14 = 0
            r15 = 1
            if (r3 != r15) goto L_0x0023
            int r2 = r2 - r15
            r1[r2] = r13
            goto L_0x00a3
        L_0x0023:
            if (r3 != r13) goto L_0x003c
            int r1 = r0.nextNonWhitespace(r15)
            if (r1 == r11) goto L_0x00a3
            if (r1 == r10) goto L_0x0038
            if (r1 != r7) goto L_0x0032
            r0.peeked = r12
            return r12
        L_0x0032:
            java.lang.String r1 = "Unterminated array"
            r0.syntaxError(r1)
            throw r14
        L_0x0038:
            r16.checkLenient()
            goto L_0x00a3
        L_0x003c:
            r13 = 5
            if (r3 == r8) goto L_0x0126
            if (r3 != r13) goto L_0x0043
            goto L_0x0126
        L_0x0043:
            if (r3 != r12) goto L_0x0077
            int r2 = r2 - r15
            r1[r2] = r13
            int r1 = r0.nextNonWhitespace(r15)
            r2 = 58
            if (r1 == r2) goto L_0x00a3
            r2 = 61
            if (r1 != r2) goto L_0x0071
            r16.checkLenient()
            int r1 = r0.pos
            int r2 = r0.limit
            if (r1 < r2) goto L_0x0063
            boolean r1 = r0.fillBuffer(r15)
            if (r1 == 0) goto L_0x00a3
        L_0x0063:
            char[] r1 = r0.buffer
            int r2 = r0.pos
            char r1 = r1[r2]
            r13 = 62
            if (r1 != r13) goto L_0x00a3
            int r2 = r2 + r15
            r0.pos = r2
            goto L_0x00a3
        L_0x0071:
            java.lang.String r1 = "Expected ':'"
            r0.syntaxError(r1)
            throw r14
        L_0x0077:
            r1 = 6
            if (r3 != r1) goto L_0x0089
            boolean r1 = r0.lenient
            if (r1 == 0) goto L_0x0081
            r16.consumeNonExecutePrefix()
        L_0x0081:
            int[] r1 = r0.stack
            int r2 = r0.stackSize
            int r2 = r2 - r15
            r1[r2] = r9
            goto L_0x00a3
        L_0x0089:
            if (r3 != r9) goto L_0x00a1
            r1 = 0
            int r1 = r0.nextNonWhitespace(r1)
            r2 = -1
            if (r1 != r2) goto L_0x0098
            r1 = 17
        L_0x0095:
            r0.peeked = r1
            return r1
        L_0x0098:
            r16.checkLenient()
            int r1 = r0.pos
            int r1 = r1 - r15
            r0.pos = r1
            goto L_0x00a3
        L_0x00a1:
            if (r3 == r4) goto L_0x011e
        L_0x00a3:
            int r1 = r0.nextNonWhitespace(r15)
            if (r1 == r6) goto L_0x0113
            if (r1 == r5) goto L_0x010d
            if (r1 == r11) goto L_0x00f6
            if (r1 == r10) goto L_0x00f6
            r2 = 91
            if (r1 == r2) goto L_0x00f3
            if (r1 == r7) goto L_0x00ee
            r2 = 123(0x7b, float:1.72E-43)
            if (r1 == r2) goto L_0x00eb
            int r1 = r0.pos
            int r1 = r1 - r15
            r0.pos = r1
            int r1 = r0.stackSize
            if (r1 != r15) goto L_0x00c5
            r16.checkLenient()
        L_0x00c5:
            int r1 = r16.peekKeyword()
            if (r1 == 0) goto L_0x00cc
            return r1
        L_0x00cc:
            int r1 = r16.peekNumber()
            if (r1 == 0) goto L_0x00d3
            return r1
        L_0x00d3:
            char[] r1 = r0.buffer
            int r2 = r0.pos
            char r1 = r1[r2]
            boolean r1 = r0.isLiteral(r1)
            if (r1 == 0) goto L_0x00e5
            r16.checkLenient()
            r1 = 10
            goto L_0x0095
        L_0x00e5:
            java.lang.String r1 = "Expected value"
            r0.syntaxError(r1)
            throw r14
        L_0x00eb:
            r0.peeked = r15
            return r15
        L_0x00ee:
            if (r3 != r15) goto L_0x00f6
            r0.peeked = r12
            return r12
        L_0x00f3:
            r0.peeked = r8
            return r8
        L_0x00f6:
            if (r3 == r15) goto L_0x0102
            r1 = 2
            if (r3 != r1) goto L_0x00fc
            goto L_0x0102
        L_0x00fc:
            java.lang.String r1 = "Unexpected value"
            r0.syntaxError(r1)
            throw r14
        L_0x0102:
            r16.checkLenient()
            int r1 = r0.pos
            int r1 = r1 - r15
            r0.pos = r1
            r0.peeked = r9
            return r9
        L_0x010d:
            r16.checkLenient()
            r0.peeked = r4
            return r4
        L_0x0113:
            int r1 = r0.stackSize
            if (r1 != r15) goto L_0x011a
            r16.checkLenient()
        L_0x011a:
            r1 = 9
            goto L_0x0095
        L_0x011e:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "JsonReader is closed"
            r1.<init>(r2)
            throw r1
        L_0x0126:
            int[] r1 = r0.stack
            int r2 = r0.stackSize
            int r2 = r2 - r15
            r1[r2] = r12
            r1 = 125(0x7d, float:1.75E-43)
            if (r3 != r13) goto L_0x0147
            int r2 = r0.nextNonWhitespace(r15)
            if (r2 == r11) goto L_0x0147
            if (r2 == r10) goto L_0x0144
            if (r2 != r1) goto L_0x013e
        L_0x013b:
            r1 = 2
            goto L_0x0095
        L_0x013e:
            java.lang.String r1 = "Unterminated object"
            r0.syntaxError(r1)
            throw r14
        L_0x0144:
            r16.checkLenient()
        L_0x0147:
            int r2 = r0.nextNonWhitespace(r15)
            if (r2 == r6) goto L_0x0178
            if (r2 == r5) goto L_0x0171
            java.lang.String r4 = "Expected name"
            if (r2 == r1) goto L_0x016a
            r16.checkLenient()
            int r1 = r0.pos
            int r1 = r1 - r15
            r0.pos = r1
            char r1 = (char) r2
            boolean r1 = r0.isLiteral(r1)
            if (r1 == 0) goto L_0x0166
            r1 = 14
            goto L_0x0095
        L_0x0166:
            r0.syntaxError(r4)
            throw r14
        L_0x016a:
            if (r3 == r13) goto L_0x016d
            goto L_0x013b
        L_0x016d:
            r0.syntaxError(r4)
            throw r14
        L_0x0171:
            r16.checkLenient()
            r1 = 12
            goto L_0x0095
        L_0x0178:
            r1 = 13
            goto L_0x0095
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.doPeek():int");
    }

    private boolean fillBuffer(int i) {
        int i2;
        char[] cArr = this.buffer;
        int i3 = this.lineStart;
        int i4 = this.pos;
        this.lineStart = i3 - i4;
        int i5 = this.limit;
        if (i5 != i4) {
            this.limit = i5 - i4;
            System.arraycopy(cArr, i4, cArr, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        do {
            Reader reader = this.in;
            int i6 = this.limit;
            int read = reader.read(cArr, i6, cArr.length - i6);
            if (read == -1) {
                return false;
            }
            this.limit += read;
            if (this.lineNumber == 0 && (i2 = this.lineStart) == 0 && this.limit > 0 && cArr[0] == 65279) {
                this.pos++;
                this.lineStart = i2 + 1;
                i++;
            }
        } while (this.limit < i);
        return true;
    }

    /* access modifiers changed from: private */
    public int getColumnNumber() {
        return (this.pos - this.lineStart) + 1;
    }

    /* access modifiers changed from: private */
    public int getLineNumber() {
        return this.lineNumber + 1;
    }

    private boolean isLiteral(char c2) {
        if (c2 == 9 || c2 == 10 || c2 == 12 || c2 == 13 || c2 == ' ') {
            return false;
        }
        if (c2 != '#') {
            if (c2 == ',') {
                return false;
            }
            if (!(c2 == '/' || c2 == '=')) {
                if (c2 == '{' || c2 == '}' || c2 == ':') {
                    return false;
                }
                if (c2 != ';') {
                    switch (c2) {
                        case '[':
                        case ']':
                            return false;
                        case '\\':
                            break;
                        default:
                            return true;
                    }
                }
            }
        }
        checkLenient();
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005f, code lost:
        if (r1 != '/') goto L_0x00a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0061, code lost:
        r7.pos = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0064, code lost:
        if (r4 != r2) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0066, code lost:
        r7.pos--;
        r2 = fillBuffer(2);
        r7.pos++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0074, code lost:
        if (r2 != false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0076, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0077, code lost:
        checkLenient();
        r2 = r7.pos;
        r3 = r0[r2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0080, code lost:
        if (r3 == '*') goto L_0x008e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0082, code lost:
        if (r3 == '/') goto L_0x0085;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0084, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0085, code lost:
        r7.pos = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008e, code lost:
        r7.pos = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0098, code lost:
        if (skipTo("*/") == false) goto L_0x009f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x009f, code lost:
        syntaxError("Unterminated comment");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a5, code lost:
        throw null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a8, code lost:
        if (r1 != '#') goto L_0x00b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00aa, code lost:
        r7.pos = r4;
        checkLenient();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b0, code lost:
        r7.pos = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00b2, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int nextNonWhitespace(boolean r8) {
        /*
            r7 = this;
            char[] r0 = r7.buffer
        L_0x0002:
            int r1 = r7.pos
        L_0x0004:
            int r2 = r7.limit
        L_0x0006:
            r3 = 1
            if (r1 != r2) goto L_0x0040
            r7.pos = r1
            boolean r1 = r7.fillBuffer(r3)
            if (r1 != 0) goto L_0x003c
            if (r8 != 0) goto L_0x0015
            r8 = -1
            return r8
        L_0x0015:
            java.io.EOFException r8 = new java.io.EOFException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "End of input at line "
            r0.append(r1)
            int r1 = r7.getLineNumber()
            r0.append(r1)
            java.lang.String r1 = " column "
            r0.append(r1)
            int r1 = r7.getColumnNumber()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r8.<init>(r0)
            throw r8
        L_0x003c:
            int r1 = r7.pos
            int r2 = r7.limit
        L_0x0040:
            int r4 = r1 + 1
            char r1 = r0[r1]
            r5 = 10
            if (r1 != r5) goto L_0x0050
            int r1 = r7.lineNumber
            int r1 = r1 + r3
            r7.lineNumber = r1
            r7.lineStart = r4
            goto L_0x00b3
        L_0x0050:
            r5 = 32
            if (r1 == r5) goto L_0x00b3
            r5 = 13
            if (r1 == r5) goto L_0x00b3
            r5 = 9
            if (r1 != r5) goto L_0x005d
            goto L_0x00b3
        L_0x005d:
            r5 = 47
            if (r1 != r5) goto L_0x00a6
            r7.pos = r4
            r6 = 2
            if (r4 != r2) goto L_0x0077
            int r2 = r7.pos
            int r2 = r2 - r3
            r7.pos = r2
            boolean r2 = r7.fillBuffer(r6)
            int r4 = r7.pos
            int r4 = r4 + r3
            r7.pos = r4
            if (r2 != 0) goto L_0x0077
            return r1
        L_0x0077:
            r7.checkLenient()
            int r2 = r7.pos
            char r3 = r0[r2]
            r4 = 42
            if (r3 == r4) goto L_0x008e
            if (r3 == r5) goto L_0x0085
            return r1
        L_0x0085:
            int r2 = r2 + 1
            r7.pos = r2
        L_0x0089:
            r7.skipToEndOfLine()
            goto L_0x0002
        L_0x008e:
            int r2 = r2 + 1
            r7.pos = r2
            java.lang.String r1 = "*/"
            boolean r1 = r7.skipTo(r1)
            if (r1 == 0) goto L_0x009f
            int r1 = r7.pos
            int r1 = r1 + r6
            goto L_0x0004
        L_0x009f:
            java.lang.String r8 = "Unterminated comment"
            r7.syntaxError(r8)
            r8 = 0
            throw r8
        L_0x00a6:
            r2 = 35
            if (r1 != r2) goto L_0x00b0
            r7.pos = r4
            r7.checkLenient()
            goto L_0x0089
        L_0x00b0:
            r7.pos = r4
            return r1
        L_0x00b3:
            r1 = r4
            goto L_0x0006
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.nextNonWhitespace(boolean):int");
    }

    private String nextQuotedValue(char c2) {
        char[] cArr = this.buffer;
        StringBuilder sb = new StringBuilder();
        while (true) {
            int i = this.pos;
            int i2 = this.limit;
            int i3 = i;
            while (true) {
                if (i < i2) {
                    int i4 = i + 1;
                    char c3 = cArr[i];
                    if (c3 == c2) {
                        this.pos = i4;
                        sb.append(cArr, i3, (i4 - i3) - 1);
                        return sb.toString();
                    } else if (c3 == '\\') {
                        this.pos = i4;
                        sb.append(cArr, i3, (i4 - i3) - 1);
                        sb.append(readEscapeCharacter());
                        break;
                    } else {
                        if (c3 == 10) {
                            this.lineNumber++;
                            this.lineStart = i4;
                        }
                        i = i4;
                    }
                } else {
                    sb.append(cArr, i3, i - i3);
                    this.pos = i;
                    if (!fillBuffer(1)) {
                        syntaxError("Unterminated string");
                        throw null;
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x004b, code lost:
        checkLenient();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String nextUnquotedValue() {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
            r2 = r1
        L_0x0003:
            r1 = r0
        L_0x0004:
            int r3 = r6.pos
            int r4 = r3 + r1
            int r5 = r6.limit
            if (r4 >= r5) goto L_0x004f
            char[] r4 = r6.buffer
            int r3 = r3 + r1
            char r3 = r4[r3]
            r4 = 9
            if (r3 == r4) goto L_0x005d
            r4 = 10
            if (r3 == r4) goto L_0x005d
            r4 = 12
            if (r3 == r4) goto L_0x005d
            r4 = 13
            if (r3 == r4) goto L_0x005d
            r4 = 32
            if (r3 == r4) goto L_0x005d
            r4 = 35
            if (r3 == r4) goto L_0x004b
            r4 = 44
            if (r3 == r4) goto L_0x005d
            r4 = 47
            if (r3 == r4) goto L_0x004b
            r4 = 61
            if (r3 == r4) goto L_0x004b
            r4 = 123(0x7b, float:1.72E-43)
            if (r3 == r4) goto L_0x005d
            r4 = 125(0x7d, float:1.75E-43)
            if (r3 == r4) goto L_0x005d
            r4 = 58
            if (r3 == r4) goto L_0x005d
            r4 = 59
            if (r3 == r4) goto L_0x004b
            switch(r3) {
                case 91: goto L_0x005d;
                case 92: goto L_0x004b;
                case 93: goto L_0x005d;
                default: goto L_0x0048;
            }
        L_0x0048:
            int r1 = r1 + 1
            goto L_0x0004
        L_0x004b:
            r6.checkLenient()
            goto L_0x005d
        L_0x004f:
            char[] r3 = r6.buffer
            int r3 = r3.length
            if (r1 >= r3) goto L_0x005f
            int r3 = r1 + 1
            boolean r3 = r6.fillBuffer(r3)
            if (r3 == 0) goto L_0x005d
            goto L_0x0004
        L_0x005d:
            r0 = r1
            goto L_0x0079
        L_0x005f:
            if (r2 != 0) goto L_0x0066
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
        L_0x0066:
            char[] r3 = r6.buffer
            int r4 = r6.pos
            r2.append(r3, r4, r1)
            int r3 = r6.pos
            int r3 = r3 + r1
            r6.pos = r3
            r1 = 1
            boolean r1 = r6.fillBuffer(r1)
            if (r1 != 0) goto L_0x0003
        L_0x0079:
            if (r2 != 0) goto L_0x0085
            java.lang.String r1 = new java.lang.String
            char[] r2 = r6.buffer
            int r3 = r6.pos
            r1.<init>(r2, r3, r0)
            goto L_0x0090
        L_0x0085:
            char[] r1 = r6.buffer
            int r3 = r6.pos
            r2.append(r1, r3, r0)
            java.lang.String r1 = r2.toString()
        L_0x0090:
            int r2 = r6.pos
            int r2 = r2 + r0
            r6.pos = r2
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.nextUnquotedValue():java.lang.String");
    }

    private int peekKeyword() {
        String str;
        String str2;
        int i;
        char c2 = this.buffer[this.pos];
        if (c2 == 't' || c2 == 'T') {
            i = 5;
            str2 = "true";
            str = "TRUE";
        } else if (c2 == 'f' || c2 == 'F') {
            i = 6;
            str2 = "false";
            str = "FALSE";
        } else if (c2 != 'n' && c2 != 'N') {
            return 0;
        } else {
            i = 7;
            str2 = "null";
            str = "NULL";
        }
        int length = str2.length();
        for (int i2 = 1; i2 < length; i2++) {
            if (this.pos + i2 >= this.limit && !fillBuffer(i2 + 1)) {
                return 0;
            }
            char c3 = this.buffer[this.pos + i2];
            if (c3 != str2.charAt(i2) && c3 != str.charAt(i2)) {
                return 0;
            }
        }
        if ((this.pos + length < this.limit || fillBuffer(length + 1)) && isLiteral(this.buffer[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.peeked = i;
        return i;
    }

    private int peekNumber() {
        int i;
        char c2;
        char[] cArr = this.buffer;
        int i2 = this.pos;
        int i3 = 0;
        int i4 = this.limit;
        boolean z = true;
        int i5 = 0;
        char c3 = 0;
        boolean z2 = false;
        long j = 0;
        while (true) {
            if (i2 + i5 == i4) {
                if (i5 == cArr.length) {
                    return i3;
                }
                if (!fillBuffer(i5 + 1)) {
                    break;
                }
                i2 = this.pos;
                i4 = this.limit;
            }
            c2 = cArr[i2 + i5];
            char c4 = 3;
            if (c2 == '+') {
                c4 = 6;
                i3 = 0;
                if (c3 != 5) {
                    return 0;
                }
            } else if (c2 == 'E' || c2 == 'e') {
                i3 = 0;
                if (c3 != 2 && c3 != 4) {
                    return 0;
                }
                c3 = 5;
                i5++;
            } else if (c2 == '-') {
                c4 = 6;
                i3 = 0;
                if (c3 == 0) {
                    c3 = 1;
                    z2 = true;
                    i5++;
                } else if (c3 != 5) {
                    return 0;
                }
            } else if (c2 == '.') {
                i3 = 0;
                if (c3 != 2) {
                    return 0;
                }
            } else if (c2 >= '0' && c2 <= '9') {
                if (c3 == 1 || c3 == 0) {
                    j = (long) (-(c2 - '0'));
                    c3 = 2;
                } else if (c3 != 2) {
                    if (c3 == 3) {
                        i3 = 0;
                        c3 = 4;
                    } else if (c3 == 5 || c3 == 6) {
                        i3 = 0;
                        c3 = 7;
                    }
                    i5++;
                } else if (j == 0) {
                    return 0;
                } else {
                    long j2 = (10 * j) - ((long) (c2 - '0'));
                    int i6 = (j > MIN_INCOMPLETE_INTEGER ? 1 : (j == MIN_INCOMPLETE_INTEGER ? 0 : -1));
                    boolean z3 = i6 > 0 || (i6 == 0 && j2 < j);
                    j = j2;
                    z = z3 & z;
                }
                i3 = 0;
                i5++;
            }
            c3 = c4;
            i5++;
        }
        if (isLiteral(c2)) {
            return 0;
        }
        if (c3 == 2 && z && (j != Long.MIN_VALUE || z2)) {
            if (!z2) {
                j = -j;
            }
            this.peekedLong = j;
            this.pos += i5;
            i = 15;
        } else if (c3 != 2 && c3 != 4 && c3 != 7) {
            return 0;
        } else {
            this.peekedNumberLength = i5;
            i = 16;
        }
        this.peeked = i;
        return i;
    }

    private void push(int i) {
        int i2 = this.stackSize;
        int[] iArr = this.stack;
        if (i2 == iArr.length) {
            int[] iArr2 = new int[(i2 * 2)];
            System.arraycopy(iArr, 0, iArr2, 0, i2);
            this.stack = iArr2;
        }
        int[] iArr3 = this.stack;
        int i3 = this.stackSize;
        this.stackSize = i3 + 1;
        iArr3[i3] = i;
    }

    private char readEscapeCharacter() {
        int i;
        int i2;
        if (this.pos != this.limit || fillBuffer(1)) {
            char[] cArr = this.buffer;
            int i3 = this.pos;
            this.pos = i3 + 1;
            char c2 = cArr[i3];
            if (c2 == 10) {
                this.lineNumber++;
                this.lineStart = this.pos;
            } else if (c2 == 'b') {
                return 8;
            } else {
                if (c2 == 'f') {
                    return 12;
                }
                if (c2 == 'n') {
                    return 10;
                }
                if (c2 == 'r') {
                    return 13;
                }
                if (c2 == 't') {
                    return 9;
                }
                if (c2 == 'u') {
                    if (this.pos + 4 <= this.limit || fillBuffer(4)) {
                        char c3 = 0;
                        int i4 = this.pos;
                        int i5 = i4 + 4;
                        while (i4 < i5) {
                            char c4 = this.buffer[i4];
                            char c5 = (char) (c3 << 4);
                            if (c4 < '0' || c4 > '9') {
                                if (c4 >= 'a' && c4 <= 'f') {
                                    i = c4 - 'a';
                                } else if (c4 < 'A' || c4 > 'F') {
                                    throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                                } else {
                                    i = c4 - 'A';
                                }
                                i2 = i + 10;
                            } else {
                                i2 = c4 - '0';
                            }
                            c3 = (char) (c5 + i2);
                            i4++;
                        }
                        this.pos += 4;
                        return c3;
                    }
                    syntaxError("Unterminated escape sequence");
                    throw null;
                }
            }
            return c2;
        }
        syntaxError("Unterminated escape sequence");
        throw null;
    }

    private void skipQuotedValue(char c2) {
        char[] cArr = this.buffer;
        while (true) {
            int i = this.pos;
            int i2 = this.limit;
            while (true) {
                if (i < i2) {
                    int i3 = i + 1;
                    char c3 = cArr[i];
                    if (c3 == c2) {
                        this.pos = i3;
                        return;
                    } else if (c3 == '\\') {
                        this.pos = i3;
                        readEscapeCharacter();
                        break;
                    } else {
                        if (c3 == 10) {
                            this.lineNumber++;
                            this.lineStart = i3;
                        }
                        i = i3;
                    }
                } else {
                    this.pos = i;
                    if (!fillBuffer(1)) {
                        syntaxError("Unterminated string");
                        throw null;
                    }
                }
            }
        }
    }

    private boolean skipTo(String str) {
        while (true) {
            int i = 0;
            if (this.pos + str.length() > this.limit && !fillBuffer(str.length())) {
                return false;
            }
            char[] cArr = this.buffer;
            int i2 = this.pos;
            if (cArr[i2] == 10) {
                this.lineNumber++;
                this.lineStart = i2 + 1;
            } else {
                while (i < str.length()) {
                    if (this.buffer[this.pos + i] == str.charAt(i)) {
                        i++;
                    }
                }
                return true;
            }
            this.pos++;
        }
    }

    private void skipToEndOfLine() {
        char c2;
        do {
            if (this.pos < this.limit || fillBuffer(1)) {
                char[] cArr = this.buffer;
                int i = this.pos;
                this.pos = i + 1;
                c2 = cArr[i];
                if (c2 == 10) {
                    this.lineNumber++;
                    this.lineStart = this.pos;
                    return;
                }
            } else {
                return;
            }
        } while (c2 != 13);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0048, code lost:
        checkLenient();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void skipUnquotedValue() {
        /*
            r4 = this;
        L_0x0000:
            r0 = 0
        L_0x0001:
            int r1 = r4.pos
            int r2 = r1 + r0
            int r3 = r4.limit
            if (r2 >= r3) goto L_0x0051
            char[] r2 = r4.buffer
            int r1 = r1 + r0
            char r1 = r2[r1]
            r2 = 9
            if (r1 == r2) goto L_0x004b
            r2 = 10
            if (r1 == r2) goto L_0x004b
            r2 = 12
            if (r1 == r2) goto L_0x004b
            r2 = 13
            if (r1 == r2) goto L_0x004b
            r2 = 32
            if (r1 == r2) goto L_0x004b
            r2 = 35
            if (r1 == r2) goto L_0x0048
            r2 = 44
            if (r1 == r2) goto L_0x004b
            r2 = 47
            if (r1 == r2) goto L_0x0048
            r2 = 61
            if (r1 == r2) goto L_0x0048
            r2 = 123(0x7b, float:1.72E-43)
            if (r1 == r2) goto L_0x004b
            r2 = 125(0x7d, float:1.75E-43)
            if (r1 == r2) goto L_0x004b
            r2 = 58
            if (r1 == r2) goto L_0x004b
            r2 = 59
            if (r1 == r2) goto L_0x0048
            switch(r1) {
                case 91: goto L_0x004b;
                case 92: goto L_0x0048;
                case 93: goto L_0x004b;
                default: goto L_0x0045;
            }
        L_0x0045:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x0048:
            r4.checkLenient()
        L_0x004b:
            int r1 = r4.pos
            int r1 = r1 + r0
            r4.pos = r1
            return
        L_0x0051:
            int r1 = r1 + r0
            r4.pos = r1
            r0 = 1
            boolean r0 = r4.fillBuffer(r0)
            if (r0 != 0) goto L_0x0000
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.skipUnquotedValue():void");
    }

    private IOException syntaxError(String str) {
        throw new MalformedJsonException(str + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public void beginArray() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 3) {
            push(1);
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public void beginObject() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 1) {
            push(3);
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public void close() {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    public void endArray() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 4) {
            this.stackSize--;
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected END_ARRAY but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public void endObject() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 2) {
            this.stackSize--;
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected END_OBJECT but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public boolean hasNext() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        return (i == 2 || i == 4) ? false : true;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public boolean nextBoolean() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 5) {
            this.peeked = 0;
            return true;
        } else if (i == 6) {
            this.peeked = 0;
            return false;
        } else {
            throw new IllegalStateException("Expected a boolean but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
    }

    public double nextDouble() {
        String str;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 15) {
            this.peeked = 0;
            return (double) this.peekedLong;
        }
        if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            if (i == 8 || i == 9) {
                str = nextQuotedValue(i == 8 ? '\'' : '\"');
            } else if (i == 10) {
                str = nextUnquotedValue();
            } else if (i != 11) {
                throw new IllegalStateException("Expected a double but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
            }
            this.peekedString = str;
        }
        this.peeked = 11;
        double parseDouble = Double.parseDouble(this.peekedString);
        if (this.lenient || (!Double.isNaN(parseDouble) && !Double.isInfinite(parseDouble))) {
            this.peekedString = null;
            this.peeked = 0;
            return parseDouble;
        }
        throw new MalformedJsonException("JSON forbids NaN and infinities: " + parseDouble + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public int nextInt() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 15) {
            long j = this.peekedLong;
            int i2 = (int) j;
            if (j == ((long) i2)) {
                this.peeked = 0;
                return i2;
            }
            throw new NumberFormatException("Expected an int but was " + this.peekedLong + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (i == 8 || i == 9) {
            this.peekedString = nextQuotedValue(i == 8 ? '\'' : '\"');
            try {
                int parseInt = Integer.parseInt(this.peekedString);
                this.peeked = 0;
                return parseInt;
            } catch (NumberFormatException unused) {
            }
        } else {
            throw new IllegalStateException("Expected an int but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        this.peeked = 11;
        double parseDouble = Double.parseDouble(this.peekedString);
        int i3 = (int) parseDouble;
        if (((double) i3) == parseDouble) {
            this.peekedString = null;
            this.peeked = 0;
            return i3;
        }
        throw new NumberFormatException("Expected an int but was " + this.peekedString + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public long nextLong() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 15) {
            this.peeked = 0;
            return this.peekedLong;
        }
        if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (i == 8 || i == 9) {
            this.peekedString = nextQuotedValue(i == 8 ? '\'' : '\"');
            try {
                long parseLong = Long.parseLong(this.peekedString);
                this.peeked = 0;
                return parseLong;
            } catch (NumberFormatException unused) {
            }
        } else {
            throw new IllegalStateException("Expected a long but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        this.peeked = 11;
        double parseDouble = Double.parseDouble(this.peekedString);
        long j = (long) parseDouble;
        if (((double) j) == parseDouble) {
            this.peekedString = null;
            this.peeked = 0;
            return j;
        }
        throw new NumberFormatException("Expected a long but was " + this.peekedString + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public String nextName() {
        String str;
        char c2;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 14) {
            str = nextUnquotedValue();
        } else {
            if (i == 12) {
                c2 = '\'';
            } else if (i == 13) {
                c2 = '\"';
            } else {
                throw new IllegalStateException("Expected a name but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
            }
            str = nextQuotedValue(c2);
        }
        this.peeked = 0;
        return str;
    }

    public void nextNull() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 7) {
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected null but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public String nextString() {
        String str;
        char c2;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 10) {
            str = nextUnquotedValue();
        } else {
            if (i == 8) {
                c2 = '\'';
            } else if (i == 9) {
                c2 = '\"';
            } else if (i == 11) {
                str = this.peekedString;
                this.peekedString = null;
            } else if (i == 15) {
                str = Long.toString(this.peekedLong);
            } else if (i == 16) {
                str = new String(this.buffer, this.pos, this.peekedNumberLength);
                this.pos += this.peekedNumberLength;
            } else {
                throw new IllegalStateException("Expected a string but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
            }
            str = nextQuotedValue(c2);
        }
        this.peeked = 0;
        return str;
    }

    public JsonToken peek() {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        switch (i) {
            case 1:
                return JsonToken.BEGIN_OBJECT;
            case 2:
                return JsonToken.END_OBJECT;
            case 3:
                return JsonToken.BEGIN_ARRAY;
            case 4:
                return JsonToken.END_ARRAY;
            case 5:
            case 6:
                return JsonToken.BOOLEAN;
            case 7:
                return JsonToken.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return JsonToken.STRING;
            case 12:
            case 13:
            case 14:
                return JsonToken.NAME;
            case 15:
            case 16:
                return JsonToken.NUMBER;
            case 17:
                return JsonToken.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    public final void setLenient(boolean z) {
        this.lenient = z;
    }

    public void skipValue() {
        char c2;
        int i = 0;
        do {
            int i2 = this.peeked;
            if (i2 == 0) {
                i2 = doPeek();
            }
            if (i2 == 3) {
                push(1);
            } else if (i2 == 1) {
                push(3);
            } else if (i2 == 4 || i2 == 2) {
                this.stackSize--;
                i--;
                this.peeked = 0;
            } else if (i2 == 14 || i2 == 10) {
                skipUnquotedValue();
                this.peeked = 0;
            } else {
                if (i2 == 8 || i2 == 12) {
                    c2 = '\'';
                } else if (i2 == 9 || i2 == 13) {
                    c2 = '\"';
                } else {
                    if (i2 == 16) {
                        this.pos += this.peekedNumberLength;
                    }
                    this.peeked = 0;
                }
                skipQuotedValue(c2);
                this.peeked = 0;
            }
            i++;
            this.peeked = 0;
        } while (i != 0);
    }

    public String toString() {
        return getClass().getSimpleName() + " at line " + getLineNumber() + " column " + getColumnNumber();
    }
}
