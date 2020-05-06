package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFormat {
    private static final int BUFFER_SIZE = 4096;
    private static final Printer DEFAULT_PRINTER = new Printer((AnonymousClass1) null);
    private static final Printer SINGLE_LINE_PRINTER = new Printer((AnonymousClass1) null).setSingleLineMode(true);
    private static final Printer UNICODE_PRINTER = new Printer((AnonymousClass1) null).setEscapeNonAscii(false);

    private TextFormat() {
    }

    public static void print(MessageOrBuilder message, Appendable output) throws IOException {
        DEFAULT_PRINTER.print(message, new TextGenerator(output, (AnonymousClass1) null));
    }

    public static void print(UnknownFieldSet fields, Appendable output) throws IOException {
        DEFAULT_PRINTER.printUnknownFields(fields, new TextGenerator(output, (AnonymousClass1) null));
    }

    public static String shortDebugString(MessageOrBuilder message) {
        try {
            StringBuilder sb = new StringBuilder();
            SINGLE_LINE_PRINTER.print(message, new TextGenerator(sb, (AnonymousClass1) null));
            return sb.toString().trim();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String shortDebugString(UnknownFieldSet fields) {
        try {
            StringBuilder sb = new StringBuilder();
            SINGLE_LINE_PRINTER.printUnknownFields(fields, new TextGenerator(sb, (AnonymousClass1) null));
            return sb.toString().trim();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String printToString(MessageOrBuilder message) {
        try {
            StringBuilder text = new StringBuilder();
            print(message, (Appendable) text);
            return text.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String printToString(UnknownFieldSet fields) {
        try {
            StringBuilder text = new StringBuilder();
            print(fields, (Appendable) text);
            return text.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String printToUnicodeString(MessageOrBuilder message) {
        try {
            StringBuilder text = new StringBuilder();
            UNICODE_PRINTER.print(message, new TextGenerator(text, (AnonymousClass1) null));
            return text.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String printToUnicodeString(UnknownFieldSet fields) {
        try {
            StringBuilder text = new StringBuilder();
            UNICODE_PRINTER.printUnknownFields(fields, new TextGenerator(text, (AnonymousClass1) null));
            return text.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void printField(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
        DEFAULT_PRINTER.printField(field, value, new TextGenerator(output, (AnonymousClass1) null));
    }

    public static String printFieldToString(Descriptors.FieldDescriptor field, Object value) {
        try {
            StringBuilder text = new StringBuilder();
            printField(field, value, text);
            return text.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void printFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
        DEFAULT_PRINTER.printFieldValue(field, value, new TextGenerator(output, (AnonymousClass1) null));
    }

    public static void printUnknownFieldValue(int tag, Object value, Appendable output) throws IOException {
        printUnknownFieldValue(tag, value, new TextGenerator(output, (AnonymousClass1) null));
    }

    /* access modifiers changed from: private */
    public static void printUnknownFieldValue(int tag, Object value, TextGenerator generator) throws IOException {
        int tagWireType = WireFormat.getTagWireType(tag);
        if (tagWireType == 0) {
            generator.print(unsignedToString(((Long) value).longValue()));
        } else if (tagWireType == 1) {
            generator.print(String.format((Locale) null, "0x%016x", new Object[]{(Long) value}));
        } else if (tagWireType == 2) {
            generator.print("\"");
            generator.print(escapeBytes((ByteString) value));
            generator.print("\"");
        } else if (tagWireType == 3) {
            DEFAULT_PRINTER.printUnknownFields((UnknownFieldSet) value, generator);
        } else if (tagWireType == 5) {
            generator.print(String.format((Locale) null, "0x%08x", new Object[]{(Integer) value}));
        } else {
            throw new IllegalArgumentException("Bad tag: " + tag);
        }
    }

    private static final class Printer {
        boolean escapeNonAscii;
        boolean singleLineMode;

        /* synthetic */ Printer(AnonymousClass1 x0) {
            this();
        }

        private Printer() {
            this.singleLineMode = false;
            this.escapeNonAscii = true;
        }

        /* access modifiers changed from: private */
        public Printer setSingleLineMode(boolean singleLineMode2) {
            this.singleLineMode = singleLineMode2;
            return this;
        }

        /* access modifiers changed from: private */
        public Printer setEscapeNonAscii(boolean escapeNonAscii2) {
            this.escapeNonAscii = escapeNonAscii2;
            return this;
        }

        /* access modifiers changed from: private */
        public void print(MessageOrBuilder message, TextGenerator generator) throws IOException {
            for (Map.Entry<Descriptors.FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
                printField(field.getKey(), field.getValue(), generator);
            }
            printUnknownFields(message.getUnknownFields(), generator);
        }

        /* access modifiers changed from: private */
        public void printField(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
            if (field.isRepeated()) {
                for (Object element : (List) value) {
                    printSingleField(field, element, generator);
                }
                return;
            }
            printSingleField(field, value, generator);
        }

        private void printSingleField(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
            if (field.isExtension()) {
                generator.print("[");
                if (!field.getContainingType().getOptions().getMessageSetWireFormat() || field.getType() != Descriptors.FieldDescriptor.Type.MESSAGE || !field.isOptional() || field.getExtensionScope() != field.getMessageType()) {
                    generator.print(field.getFullName());
                } else {
                    generator.print(field.getMessageType().getFullName());
                }
                generator.print("]");
            } else if (field.getType() == Descriptors.FieldDescriptor.Type.GROUP) {
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                generator.print(": ");
            } else if (this.singleLineMode) {
                generator.print(" { ");
            } else {
                generator.print(" {\n");
                generator.indent();
            }
            printFieldValue(field, value, generator);
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                if (this.singleLineMode) {
                    generator.print("} ");
                    return;
                }
                generator.outdent();
                generator.print("}\n");
            } else if (this.singleLineMode) {
                generator.print(" ");
            } else {
                generator.print("\n");
            }
        }

        /* access modifiers changed from: private */
        public void printFieldValue(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[field.getType().ordinal()]) {
                case 1:
                case 2:
                case 3:
                    generator.print(((Integer) value).toString());
                    return;
                case 4:
                case 5:
                case 6:
                    generator.print(((Long) value).toString());
                    return;
                case 7:
                    generator.print(((Boolean) value).toString());
                    return;
                case 8:
                    generator.print(((Float) value).toString());
                    return;
                case 9:
                    generator.print(((Double) value).toString());
                    return;
                case 10:
                case 11:
                    generator.print(TextFormat.unsignedToString(((Integer) value).intValue()));
                    return;
                case TYPE_BYTES_VALUE:
                case TYPE_UINT32_VALUE:
                    generator.print(TextFormat.unsignedToString(((Long) value).longValue()));
                    return;
                case TYPE_ENUM_VALUE:
                    generator.print("\"");
                    generator.print(this.escapeNonAscii ? TextFormat.escapeText((String) value) : (String) value);
                    generator.print("\"");
                    return;
                case TYPE_SFIXED32_VALUE:
                    generator.print("\"");
                    generator.print(TextFormat.escapeBytes((ByteString) value));
                    generator.print("\"");
                    return;
                case 16:
                    generator.print(((Descriptors.EnumValueDescriptor) value).getName());
                    return;
                case 17:
                case 18:
                    print((Message) value, generator);
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: private */
        public void printUnknownFields(UnknownFieldSet unknownFields, TextGenerator generator) throws IOException {
            for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
                int number = entry.getKey().intValue();
                UnknownFieldSet.Field field = entry.getValue();
                printUnknownField(number, 0, field.getVarintList(), generator);
                printUnknownField(number, 5, field.getFixed32List(), generator);
                printUnknownField(number, 1, field.getFixed64List(), generator);
                printUnknownField(number, 2, field.getLengthDelimitedList(), generator);
                for (UnknownFieldSet value : field.getGroupList()) {
                    generator.print(entry.getKey().toString());
                    if (this.singleLineMode) {
                        generator.print(" { ");
                    } else {
                        generator.print(" {\n");
                        generator.indent();
                    }
                    printUnknownFields(value, generator);
                    if (this.singleLineMode) {
                        generator.print("} ");
                    } else {
                        generator.outdent();
                        generator.print("}\n");
                    }
                }
            }
        }

        private void printUnknownField(int number, int wireType, List<?> values, TextGenerator generator) throws IOException {
            for (Object value : values) {
                generator.print(String.valueOf(number));
                generator.print(": ");
                TextFormat.printUnknownFieldValue(wireType, value, generator);
                generator.print(this.singleLineMode ? " " : "\n");
            }
        }
    }

    /* renamed from: com.google.protobuf.TextFormat$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type = new int[Descriptors.FieldDescriptor.Type.values().length];

        static {
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.INT32.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.SINT32.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.SFIXED32.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.INT64.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.SINT64.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.SFIXED64.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.BOOL.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.FLOAT.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.DOUBLE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.UINT32.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.FIXED32.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.UINT64.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.FIXED64.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.STRING.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.BYTES.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.ENUM.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.MESSAGE.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.GROUP.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static String unsignedToString(int value) {
        if (value >= 0) {
            return Integer.toString(value);
        }
        return Long.toString(((long) value) & 4294967295L);
    }

    /* access modifiers changed from: private */
    public static String unsignedToString(long value) {
        if (value >= 0) {
            return Long.toString(value);
        }
        return BigInteger.valueOf(Long.MAX_VALUE & value).setBit(63).toString();
    }

    private static final class TextGenerator {
        private boolean atStartOfLine;
        private final StringBuilder indent;
        private final Appendable output;

        /* synthetic */ TextGenerator(Appendable x0, AnonymousClass1 x1) {
            this(x0);
        }

        private TextGenerator(Appendable output2) {
            this.indent = new StringBuilder();
            this.atStartOfLine = true;
            this.output = output2;
        }

        public void indent() {
            this.indent.append("  ");
        }

        public void outdent() {
            int length = this.indent.length();
            if (length != 0) {
                this.indent.delete(length - 2, length);
                return;
            }
            throw new IllegalArgumentException(" Outdent() without matching Indent().");
        }

        public void print(CharSequence text) throws IOException {
            int size = text.length();
            int pos = 0;
            for (int i = 0; i < size; i++) {
                if (text.charAt(i) == 10) {
                    write(text.subSequence(pos, size), (i - pos) + 1);
                    pos = i + 1;
                    this.atStartOfLine = true;
                }
            }
            write(text.subSequence(pos, size), size - pos);
        }

        private void write(CharSequence data, int size) throws IOException {
            if (size != 0) {
                if (this.atStartOfLine) {
                    this.atStartOfLine = false;
                    this.output.append(this.indent);
                }
                this.output.append(data);
            }
        }
    }

    private static final class Tokenizer {
        private static final Pattern DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?", 2);
        private static final Pattern FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?", 2);
        private static final Pattern FLOAT_NAN = Pattern.compile("nanf?", 2);
        private static final Pattern TOKEN = Pattern.compile("[a-zA-Z_][0-9a-zA-Z_+-]*+|[.]?[0-9+-][0-9a-zA-Z_.+-]*+|\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|'([^'\n\\\\]|\\\\.)*+('|\\\\?$)", 8);
        private static final Pattern WHITESPACE = Pattern.compile("(\\s|(#.*$))++", 8);
        private int column;
        private String currentToken;
        private int line;
        private final Matcher matcher;
        private int pos;
        private int previousColumn;
        private int previousLine;
        private final CharSequence text;

        /* synthetic */ Tokenizer(CharSequence x0, AnonymousClass1 x1) {
            this(x0);
        }

        private Tokenizer(CharSequence text2) {
            this.pos = 0;
            this.line = 0;
            this.column = 0;
            this.previousLine = 0;
            this.previousColumn = 0;
            this.text = text2;
            this.matcher = WHITESPACE.matcher(text2);
            skipWhitespace();
            nextToken();
        }

        public boolean atEnd() {
            return this.currentToken.length() == 0;
        }

        public void nextToken() {
            this.previousLine = this.line;
            this.previousColumn = this.column;
            while (this.pos < this.matcher.regionStart()) {
                if (this.text.charAt(this.pos) == 10) {
                    this.line++;
                    this.column = 0;
                } else {
                    this.column++;
                }
                this.pos++;
            }
            if (this.matcher.regionStart() == this.matcher.regionEnd()) {
                this.currentToken = "";
                return;
            }
            this.matcher.usePattern(TOKEN);
            if (this.matcher.lookingAt()) {
                this.currentToken = this.matcher.group();
                Matcher matcher2 = this.matcher;
                matcher2.region(matcher2.end(), this.matcher.regionEnd());
            } else {
                this.currentToken = String.valueOf(this.text.charAt(this.pos));
                Matcher matcher3 = this.matcher;
                matcher3.region(this.pos + 1, matcher3.regionEnd());
            }
            skipWhitespace();
        }

        private void skipWhitespace() {
            this.matcher.usePattern(WHITESPACE);
            if (this.matcher.lookingAt()) {
                Matcher matcher2 = this.matcher;
                matcher2.region(matcher2.end(), this.matcher.regionEnd());
            }
        }

        public boolean tryConsume(String token) {
            if (!this.currentToken.equals(token)) {
                return false;
            }
            nextToken();
            return true;
        }

        public void consume(String token) throws ParseException {
            if (!tryConsume(token)) {
                throw parseException("Expected \"" + token + "\".");
            }
        }

        public boolean lookingAtInteger() {
            if (this.currentToken.length() == 0) {
                return false;
            }
            char c = this.currentToken.charAt(0);
            if (('0' <= c && c <= '9') || c == '-' || c == '+') {
                return true;
            }
            return false;
        }

        public String consumeIdentifier() throws ParseException {
            for (int i = 0; i < this.currentToken.length(); i++) {
                char c = this.currentToken.charAt(i);
                if (('a' > c || c > 'z') && (('A' > c || c > 'Z') && !(('0' <= c && c <= '9') || c == '_' || c == '.'))) {
                    throw parseException("Expected identifier.");
                }
            }
            String result = this.currentToken;
            nextToken();
            return result;
        }

        public int consumeInt32() throws ParseException {
            try {
                int result = TextFormat.parseInt32(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        public int consumeUInt32() throws ParseException {
            try {
                int result = TextFormat.parseUInt32(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        public long consumeInt64() throws ParseException {
            try {
                long result = TextFormat.parseInt64(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        public long consumeUInt64() throws ParseException {
            try {
                long result = TextFormat.parseUInt64(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        public double consumeDouble() throws ParseException {
            if (DOUBLE_INFINITY.matcher(this.currentToken).matches()) {
                boolean negative = this.currentToken.startsWith("-");
                nextToken();
                return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            } else if (this.currentToken.equalsIgnoreCase("nan")) {
                nextToken();
                return Double.NaN;
            } else {
                try {
                    double result = Double.parseDouble(this.currentToken);
                    nextToken();
                    return result;
                } catch (NumberFormatException e) {
                    throw floatParseException(e);
                }
            }
        }

        public float consumeFloat() throws ParseException {
            if (FLOAT_INFINITY.matcher(this.currentToken).matches()) {
                boolean negative = this.currentToken.startsWith("-");
                nextToken();
                return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            } else if (FLOAT_NAN.matcher(this.currentToken).matches()) {
                nextToken();
                return Float.NaN;
            } else {
                try {
                    float result = Float.parseFloat(this.currentToken);
                    nextToken();
                    return result;
                } catch (NumberFormatException e) {
                    throw floatParseException(e);
                }
            }
        }

        public boolean consumeBoolean() throws ParseException {
            if (this.currentToken.equals("true") || this.currentToken.equals("t") || this.currentToken.equals("1")) {
                nextToken();
                return true;
            } else if (this.currentToken.equals("false") || this.currentToken.equals("f") || this.currentToken.equals("0")) {
                nextToken();
                return false;
            } else {
                throw parseException("Expected \"true\" or \"false\".");
            }
        }

        public String consumeString() throws ParseException {
            return consumeByteString().toStringUtf8();
        }

        public ByteString consumeByteString() throws ParseException {
            List<ByteString> list = new ArrayList<>();
            consumeByteString(list);
            while (true) {
                if (!this.currentToken.startsWith("'") && !this.currentToken.startsWith("\"")) {
                    return ByteString.copyFrom((Iterable<ByteString>) list);
                }
                consumeByteString(list);
            }
        }

        private void consumeByteString(List<ByteString> list) throws ParseException {
            char c = 0;
            if (this.currentToken.length() > 0) {
                c = this.currentToken.charAt(0);
            }
            char quote = c;
            if (quote == '\"' || quote == '\'') {
                if (this.currentToken.length() >= 2) {
                    String str = this.currentToken;
                    if (str.charAt(str.length() - 1) == quote) {
                        try {
                            ByteString result = TextFormat.unescapeBytes(this.currentToken.substring(1, this.currentToken.length() - 1));
                            nextToken();
                            list.add(result);
                            return;
                        } catch (InvalidEscapeSequenceException e) {
                            throw parseException(e.getMessage());
                        }
                    }
                }
                throw parseException("String missing ending quote.");
            }
            throw parseException("Expected string.");
        }

        public ParseException parseException(String description) {
            return new ParseException(this.line + 1, this.column + 1, description);
        }

        public ParseException parseExceptionPreviousToken(String description) {
            return new ParseException(this.previousLine + 1, this.previousColumn + 1, description);
        }

        private ParseException integerParseException(NumberFormatException e) {
            return parseException("Couldn't parse integer: " + e.getMessage());
        }

        private ParseException floatParseException(NumberFormatException e) {
            return parseException("Couldn't parse number: " + e.getMessage());
        }
    }

    public static class ParseException extends IOException {
        private static final long serialVersionUID = 3196188060225107702L;
        private final int column;
        private final int line;

        public ParseException(String message) {
            this(-1, -1, message);
        }

        public ParseException(int line2, int column2, String message) {
            super(Integer.toString(line2) + ":" + column2 + ": " + message);
            this.line = line2;
            this.column = column2;
        }

        public int getLine() {
            return this.line;
        }

        public int getColumn() {
            return this.column;
        }
    }

    public static void merge(Readable input, Message.Builder builder) throws IOException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }

    public static void merge(CharSequence input, Message.Builder builder) throws ParseException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }

    public static void merge(Readable input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {
        merge((CharSequence) toStringBuilder(input), extensionRegistry, builder);
    }

    private static StringBuilder toStringBuilder(Readable input) throws IOException {
        StringBuilder text = new StringBuilder();
        CharBuffer buffer = CharBuffer.allocate(4096);
        while (true) {
            int n = input.read(buffer);
            if (n == -1) {
                return text;
            }
            buffer.flip();
            text.append(buffer, 0, n);
        }
    }

    public static void merge(CharSequence input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws ParseException {
        Tokenizer tokenizer = new Tokenizer(input, (AnonymousClass1) null);
        while (!tokenizer.atEnd()) {
            mergeField(tokenizer, extensionRegistry, builder);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0091, code lost:
        r5 = r2.toLowerCase(java.util.Locale.US);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void mergeField(com.google.protobuf.TextFormat.Tokenizer r10, com.google.protobuf.ExtensionRegistry r11, com.google.protobuf.Message.Builder r12) throws com.google.protobuf.TextFormat.ParseException {
        /*
            com.google.protobuf.Descriptors$Descriptor r0 = r12.getDescriptorForType()
            r1 = 0
            java.lang.String r2 = "["
            boolean r2 = r10.tryConsume(r2)
            r3 = 46
            java.lang.String r4 = "\"."
            r5 = 0
            if (r2 == 0) goto L_0x0087
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r6 = r10.consumeIdentifier()
            r2.<init>(r6)
        L_0x001b:
            java.lang.String r6 = "."
            boolean r6 = r10.tryConsume(r6)
            if (r6 == 0) goto L_0x002e
            r2.append(r3)
            java.lang.String r6 = r10.consumeIdentifier()
            r2.append(r6)
            goto L_0x001b
        L_0x002e:
            java.lang.String r6 = r2.toString()
            com.google.protobuf.ExtensionRegistry$ExtensionInfo r1 = r11.findExtensionByName(r6)
            java.lang.String r6 = "Extension \""
            if (r1 == 0) goto L_0x006e
            com.google.protobuf.Descriptors$FieldDescriptor r7 = r1.descriptor
            com.google.protobuf.Descriptors$Descriptor r7 = r7.getContainingType()
            if (r7 != r0) goto L_0x004b
            java.lang.String r6 = "]"
            r10.consume(r6)
            com.google.protobuf.Descriptors$FieldDescriptor r2 = r1.descriptor
            goto L_0x00c2
        L_0x004b:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            r3.append(r2)
            java.lang.String r5 = "\" does not extend message type \""
            r3.append(r5)
            java.lang.String r5 = r0.getFullName()
            r3.append(r5)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.google.protobuf.TextFormat$ParseException r3 = r10.parseExceptionPreviousToken(r3)
            throw r3
        L_0x006e:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            r3.append(r2)
            java.lang.String r4 = "\" not found in the ExtensionRegistry."
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.google.protobuf.TextFormat$ParseException r3 = r10.parseExceptionPreviousToken(r3)
            throw r3
        L_0x0087:
            java.lang.String r2 = r10.consumeIdentifier()
            com.google.protobuf.Descriptors$FieldDescriptor r6 = r0.findFieldByName(r2)
            if (r6 != 0) goto L_0x00a6
            java.util.Locale r5 = java.util.Locale.US
            java.lang.String r5 = r2.toLowerCase(r5)
            com.google.protobuf.Descriptors$FieldDescriptor r6 = r0.findFieldByName(r5)
            if (r6 == 0) goto L_0x00a6
            com.google.protobuf.Descriptors$FieldDescriptor$Type r7 = r6.getType()
            com.google.protobuf.Descriptors$FieldDescriptor$Type r8 = com.google.protobuf.Descriptors.FieldDescriptor.Type.GROUP
            if (r7 == r8) goto L_0x00a6
            r6 = 0
        L_0x00a6:
            if (r6 == 0) goto L_0x00bf
            com.google.protobuf.Descriptors$FieldDescriptor$Type r7 = r6.getType()
            com.google.protobuf.Descriptors$FieldDescriptor$Type r8 = com.google.protobuf.Descriptors.FieldDescriptor.Type.GROUP
            if (r7 != r8) goto L_0x00bf
            com.google.protobuf.Descriptors$Descriptor r7 = r6.getMessageType()
            java.lang.String r7 = r7.getName()
            boolean r7 = r7.equals(r2)
            if (r7 != 0) goto L_0x00bf
            r6 = 0
        L_0x00bf:
            if (r6 == 0) goto L_0x01fb
            r2 = r6
        L_0x00c2:
            r6 = 0
            com.google.protobuf.Descriptors$FieldDescriptor$JavaType r7 = r2.getJavaType()
            com.google.protobuf.Descriptors$FieldDescriptor$JavaType r8 = com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE
            java.lang.String r9 = ":"
            if (r7 != r8) goto L_0x011e
            r10.tryConsume(r9)
            java.lang.String r3 = "<"
            boolean r3 = r10.tryConsume(r3)
            if (r3 == 0) goto L_0x00db
            java.lang.String r3 = ">"
            goto L_0x00e2
        L_0x00db:
            java.lang.String r3 = "{"
            r10.consume(r3)
            java.lang.String r3 = "}"
        L_0x00e2:
            if (r1 != 0) goto L_0x00e9
            com.google.protobuf.Message$Builder r5 = r12.newBuilderForField(r2)
            goto L_0x00ef
        L_0x00e9:
            com.google.protobuf.Message r5 = r1.defaultInstance
            com.google.protobuf.Message$Builder r5 = r5.newBuilderForType()
        L_0x00ef:
            boolean r7 = r10.tryConsume(r3)
            if (r7 != 0) goto L_0x0118
            boolean r7 = r10.atEnd()
            if (r7 != 0) goto L_0x00ff
            mergeField(r10, r11, r5)
            goto L_0x00ef
        L_0x00ff:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Expected \""
            r7.append(r8)
            r7.append(r3)
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            com.google.protobuf.TextFormat$ParseException r4 = r10.parseException(r4)
            throw r4
        L_0x0118:
            com.google.protobuf.Message r6 = r5.buildPartial()
            goto L_0x01ed
        L_0x011e:
            r10.consume(r9)
            int[] r7 = com.google.protobuf.TextFormat.AnonymousClass1.$SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type
            com.google.protobuf.Descriptors$FieldDescriptor$Type r8 = r2.getType()
            int r8 = r8.ordinal()
            r7 = r7[r8]
            switch(r7) {
                case 1: goto L_0x01e4;
                case 2: goto L_0x01e4;
                case 3: goto L_0x01e4;
                case 4: goto L_0x01db;
                case 5: goto L_0x01db;
                case 6: goto L_0x01db;
                case 7: goto L_0x01d2;
                case 8: goto L_0x01c9;
                case 9: goto L_0x01c0;
                case 10: goto L_0x01b7;
                case 11: goto L_0x01b7;
                case 12: goto L_0x01ae;
                case 13: goto L_0x01ae;
                case 14: goto L_0x01a9;
                case 15: goto L_0x01a4;
                case 16: goto L_0x013b;
                case 17: goto L_0x0132;
                case 18: goto L_0x0132;
                default: goto L_0x0130;
            }
        L_0x0130:
            goto L_0x01ed
        L_0x0132:
            r3 = r5
            java.lang.RuntimeException r4 = new java.lang.RuntimeException
            java.lang.String r5 = "Can't get here."
            r4.<init>(r5)
            throw r4
        L_0x013b:
            com.google.protobuf.Descriptors$EnumDescriptor r5 = r2.getEnumType()
            boolean r7 = r10.lookingAtInteger()
            java.lang.String r8 = "Enum type \""
            if (r7 == 0) goto L_0x0176
            int r4 = r10.consumeInt32()
            com.google.protobuf.Descriptors$EnumValueDescriptor r6 = r5.findValueByNumber((int) r4)
            if (r6 == 0) goto L_0x0153
            goto L_0x01ed
        L_0x0153:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r8)
            java.lang.String r8 = r5.getFullName()
            r7.append(r8)
            java.lang.String r8 = "\" has no value with number "
            r7.append(r8)
            r7.append(r4)
            r7.append(r3)
            java.lang.String r3 = r7.toString()
            com.google.protobuf.TextFormat$ParseException r3 = r10.parseExceptionPreviousToken(r3)
            throw r3
        L_0x0176:
            java.lang.String r3 = r10.consumeIdentifier()
            com.google.protobuf.Descriptors$EnumValueDescriptor r6 = r5.findValueByName(r3)
            if (r6 == 0) goto L_0x0181
            goto L_0x01ed
        L_0x0181:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r8)
            java.lang.String r8 = r5.getFullName()
            r7.append(r8)
            java.lang.String r8 = "\" has no value named \""
            r7.append(r8)
            r7.append(r3)
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            com.google.protobuf.TextFormat$ParseException r4 = r10.parseExceptionPreviousToken(r4)
            throw r4
        L_0x01a4:
            com.google.protobuf.ByteString r6 = r10.consumeByteString()
            goto L_0x01ed
        L_0x01a9:
            java.lang.String r6 = r10.consumeString()
            goto L_0x01ed
        L_0x01ae:
            long r3 = r10.consumeUInt64()
            java.lang.Long r6 = java.lang.Long.valueOf(r3)
            goto L_0x01ed
        L_0x01b7:
            int r3 = r10.consumeUInt32()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
            goto L_0x01ed
        L_0x01c0:
            double r3 = r10.consumeDouble()
            java.lang.Double r6 = java.lang.Double.valueOf(r3)
            goto L_0x01ed
        L_0x01c9:
            float r3 = r10.consumeFloat()
            java.lang.Float r6 = java.lang.Float.valueOf(r3)
            goto L_0x01ed
        L_0x01d2:
            boolean r3 = r10.consumeBoolean()
            java.lang.Boolean r6 = java.lang.Boolean.valueOf(r3)
            goto L_0x01ed
        L_0x01db:
            long r3 = r10.consumeInt64()
            java.lang.Long r6 = java.lang.Long.valueOf(r3)
            goto L_0x01ed
        L_0x01e4:
            int r3 = r10.consumeInt32()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
        L_0x01ed:
            boolean r3 = r2.isRepeated()
            if (r3 == 0) goto L_0x01f7
            r12.addRepeatedField(r2, r6)
            goto L_0x01fa
        L_0x01f7:
            r12.setField(r2, r6)
        L_0x01fa:
            return
        L_0x01fb:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "Message type \""
            r3.append(r5)
            java.lang.String r5 = r0.getFullName()
            r3.append(r5)
            java.lang.String r5 = "\" has no field named \""
            r3.append(r5)
            r3.append(r2)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.google.protobuf.TextFormat$ParseException r3 = r10.parseExceptionPreviousToken(r3)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.TextFormat.mergeField(com.google.protobuf.TextFormat$Tokenizer, com.google.protobuf.ExtensionRegistry, com.google.protobuf.Message$Builder):void");
    }

    static String escapeBytes(ByteString input) {
        StringBuilder builder = new StringBuilder(input.size());
        for (int i = 0; i < input.size(); i++) {
            byte b = input.byteAt(i);
            if (b == 34) {
                builder.append("\\\"");
            } else if (b == 39) {
                builder.append("\\'");
            } else if (b != 92) {
                switch (b) {
                    case 7:
                        builder.append("\\a");
                        break;
                    case 8:
                        builder.append("\\b");
                        break;
                    case 9:
                        builder.append("\\t");
                        break;
                    case 10:
                        builder.append("\\n");
                        break;
                    case 11:
                        builder.append("\\v");
                        break;
                    case TYPE_BYTES_VALUE:
                        builder.append("\\f");
                        break;
                    case TYPE_UINT32_VALUE:
                        builder.append("\\r");
                        break;
                    default:
                        if (b < 32) {
                            builder.append('\\');
                            builder.append((char) (((b >>> 6) & 3) + 48));
                            builder.append((char) (((b >>> 3) & 7) + 48));
                            builder.append((char) ((b & 7) + 48));
                            break;
                        } else {
                            builder.append((char) b);
                            break;
                        }
                }
            } else {
                builder.append("\\\\");
            }
        }
        return builder.toString();
    }

    static ByteString unescapeBytes(CharSequence charString) throws InvalidEscapeSequenceException {
        int i;
        int pos;
        ByteString input = ByteString.copyFromUtf8(charString.toString());
        byte[] result = new byte[input.size()];
        int pos2 = 0;
        int i2 = 0;
        int code = 0;
        while (i < input.size()) {
            byte c = input.byteAt(i);
            if (c != 92) {
                pos = pos2 + 1;
                result[pos2] = c;
            } else if (i + 1 < input.size()) {
                i++;
                byte c2 = input.byteAt(i);
                if (isOctal(c2)) {
                    code = digitValue(c2);
                    if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                        i++;
                        code = (code * 8) + digitValue(input.byteAt(i));
                    }
                    if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                        i++;
                        code = (code * 8) + digitValue(input.byteAt(i));
                    }
                    pos = pos2 + 1;
                    result[pos2] = (byte) code;
                } else if (c2 == 34) {
                    pos = pos2 + 1;
                    result[pos2] = 34;
                } else if (c2 == 39) {
                    pos = pos2 + 1;
                    result[pos2] = 39;
                } else if (c2 == 92) {
                    result[pos2] = 92;
                    pos = pos2 + 1;
                } else if (c2 == 102) {
                    pos = pos2 + 1;
                    result[pos2] = 12;
                } else if (c2 == 110) {
                    pos = pos2 + 1;
                    result[pos2] = 10;
                } else if (c2 == 114) {
                    pos = pos2 + 1;
                    result[pos2] = 13;
                } else if (c2 == 116) {
                    pos = pos2 + 1;
                    result[pos2] = 9;
                } else if (c2 == 118) {
                    pos = pos2 + 1;
                    result[pos2] = 11;
                } else if (c2 != 120) {
                    if (c2 == 97) {
                        pos = pos2 + 1;
                        result[pos2] = 7;
                    } else if (c2 == 98) {
                        pos = pos2 + 1;
                        result[pos2] = 8;
                    } else {
                        int i3 = code;
                        throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\" + ((char) c2) + '\'');
                    }
                } else if (i + 1 >= input.size() || !isHex(input.byteAt(i + 1))) {
                    throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\x' with no digits");
                } else {
                    i++;
                    int code2 = digitValue(input.byteAt(i));
                    if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
                        i++;
                        code2 = (code2 * 16) + digitValue(input.byteAt(i));
                    }
                    pos = pos2 + 1;
                    result[pos2] = (byte) code;
                }
            } else {
                throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\' at end of string.");
            }
            pos2 = pos;
            i2 = i + 1;
        }
        return ByteString.copyFrom(result, 0, pos2);
    }

    static class InvalidEscapeSequenceException extends IOException {
        private static final long serialVersionUID = -8164033650142593304L;

        InvalidEscapeSequenceException(String description) {
            super(description);
        }
    }

    static String escapeText(String input) {
        return escapeBytes(ByteString.copyFromUtf8(input));
    }

    static String unescapeText(String input) throws InvalidEscapeSequenceException {
        return unescapeBytes(input).toStringUtf8();
    }

    private static boolean isOctal(byte c) {
        return 48 <= c && c <= 55;
    }

    private static boolean isHex(byte c) {
        return (48 <= c && c <= 57) || (97 <= c && c <= 102) || (65 <= c && c <= 70);
    }

    private static int digitValue(byte c) {
        if (48 <= c && c <= 57) {
            return c - 48;
        }
        if (97 > c || c > 122) {
            return (c - 65) + 10;
        }
        return (c - 97) + 10;
    }

    static int parseInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, true, false);
    }

    static int parseUInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, false, false);
    }

    static long parseInt64(String text) throws NumberFormatException {
        return parseInteger(text, true, true);
    }

    static long parseUInt64(String text) throws NumberFormatException {
        return parseInteger(text, false, true);
    }

    private static long parseInteger(String text, boolean isSigned, boolean isLong) throws NumberFormatException {
        int pos = 0;
        boolean negative = false;
        if (text.startsWith("-", 0)) {
            if (isSigned) {
                pos = 0 + 1;
                negative = true;
            } else {
                throw new NumberFormatException("Number must be positive: " + text);
            }
        }
        int radix = 10;
        if (text.startsWith("0x", pos)) {
            pos += 2;
            radix = 16;
        } else if (text.startsWith("0", pos)) {
            radix = 8;
        }
        String numberText = text.substring(pos);
        if (numberText.length() < 16) {
            long result = Long.parseLong(numberText, radix);
            if (negative) {
                result = -result;
            }
            if (isLong) {
                return result;
            }
            if (isSigned) {
                if (result <= 2147483647L && result >= -2147483648L) {
                    return result;
                }
                throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
            } else if (result < 4294967296L && result >= 0) {
                return result;
            } else {
                throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
            }
        } else {
            BigInteger bigValue = new BigInteger(numberText, radix);
            if (negative) {
                bigValue = bigValue.negate();
            }
            if (!isLong) {
                if (isSigned) {
                    if (bigValue.bitLength() > 31) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
                    }
                } else if (bigValue.bitLength() > 32) {
                    throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
                }
            } else if (isSigned) {
                if (bigValue.bitLength() > 63) {
                    throw new NumberFormatException("Number out of range for 64-bit signed integer: " + text);
                }
            } else if (bigValue.bitLength() > 64) {
                throw new NumberFormatException("Number out of range for 64-bit unsigned integer: " + text);
            }
            return bigValue.longValue();
        }
    }
}
