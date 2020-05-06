package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class UnknownFieldSet implements MessageLite {
    private static final Parser PARSER = new Parser();
    private static final UnknownFieldSet defaultInstance = new UnknownFieldSet(Collections.emptyMap());
    /* access modifiers changed from: private */
    public Map<Integer, Field> fields;

    private UnknownFieldSet() {
    }

    public static Builder newBuilder() {
        return Builder.create();
    }

    public static Builder newBuilder(UnknownFieldSet copyFrom) {
        return newBuilder().mergeFrom(copyFrom);
    }

    public static UnknownFieldSet getDefaultInstance() {
        return defaultInstance;
    }

    public UnknownFieldSet getDefaultInstanceForType() {
        return defaultInstance;
    }

    private UnknownFieldSet(Map<Integer, Field> fields2) {
        this.fields = fields2;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UnknownFieldSet) || !this.fields.equals(((UnknownFieldSet) other).fields)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.fields.hashCode();
    }

    public Map<Integer, Field> asMap() {
        return this.fields;
    }

    public boolean hasField(int number) {
        return this.fields.containsKey(Integer.valueOf(number));
    }

    public Field getField(int number) {
        Field result = this.fields.get(Integer.valueOf(number));
        return result == null ? Field.getDefaultInstance() : result;
    }

    public void writeTo(CodedOutputStream output) throws IOException {
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            entry.getValue().writeTo(entry.getKey().intValue(), output);
        }
    }

    public String toString() {
        return TextFormat.printToString(this);
    }

    public ByteString toByteString() {
        try {
            ByteString.CodedBuilder out = ByteString.newCodedBuilder(getSerializedSize());
            writeTo(out.getCodedOutput());
            return out.build();
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a ByteString threw an IOException (should never happen).", e);
        }
    }

    public byte[] toByteArray() {
        try {
            byte[] result = new byte[getSerializedSize()];
            CodedOutputStream output = CodedOutputStream.newInstance(result);
            writeTo(output);
            output.checkNoSpaceLeft();
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public void writeTo(OutputStream output) throws IOException {
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
        writeTo(codedOutput);
        codedOutput.flush();
    }

    public void writeDelimitedTo(OutputStream output) throws IOException {
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
        codedOutput.writeRawVarint32(getSerializedSize());
        writeTo(codedOutput);
        codedOutput.flush();
    }

    public int getSerializedSize() {
        int result = 0;
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            result += entry.getValue().getSerializedSize(entry.getKey().intValue());
        }
        return result;
    }

    public void writeAsMessageSetTo(CodedOutputStream output) throws IOException {
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            entry.getValue().writeAsMessageSetExtensionTo(entry.getKey().intValue(), output);
        }
    }

    public int getSerializedSizeAsMessageSet() {
        int result = 0;
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            result += entry.getValue().getSerializedSizeAsMessageSetExtension(entry.getKey().intValue());
        }
        return result;
    }

    public boolean isInitialized() {
        return true;
    }

    public static UnknownFieldSet parseFrom(CodedInputStream input) throws IOException {
        return newBuilder().mergeFrom(input).build();
    }

    public static UnknownFieldSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data).build();
    }

    public static UnknownFieldSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data).build();
    }

    public static UnknownFieldSet parseFrom(InputStream input) throws IOException {
        return newBuilder().mergeFrom(input).build();
    }

    public Builder newBuilderForType() {
        return newBuilder();
    }

    public Builder toBuilder() {
        return newBuilder().mergeFrom(this);
    }

    public static final class Builder implements MessageLite.Builder {
        private Map<Integer, Field> fields;
        private Field.Builder lastField;
        private int lastFieldNumber;

        private Builder() {
        }

        /* access modifiers changed from: private */
        public static Builder create() {
            Builder builder = new Builder();
            builder.reinitialize();
            return builder;
        }

        private Field.Builder getFieldBuilder(int number) {
            Field.Builder builder = this.lastField;
            if (builder != null) {
                int i = this.lastFieldNumber;
                if (number == i) {
                    return builder;
                }
                addField(i, builder.build());
            }
            if (number == 0) {
                return null;
            }
            Field existing = this.fields.get(Integer.valueOf(number));
            this.lastFieldNumber = number;
            this.lastField = Field.newBuilder();
            if (existing != null) {
                this.lastField.mergeFrom(existing);
            }
            return this.lastField;
        }

        public UnknownFieldSet build() {
            UnknownFieldSet result;
            getFieldBuilder(0);
            if (this.fields.isEmpty()) {
                result = UnknownFieldSet.getDefaultInstance();
            } else {
                result = new UnknownFieldSet(Collections.unmodifiableMap(this.fields));
            }
            this.fields = null;
            return result;
        }

        public UnknownFieldSet buildPartial() {
            return build();
        }

        public Builder clone() {
            getFieldBuilder(0);
            return UnknownFieldSet.newBuilder().mergeFrom(new UnknownFieldSet(this.fields));
        }

        public UnknownFieldSet getDefaultInstanceForType() {
            return UnknownFieldSet.getDefaultInstance();
        }

        private void reinitialize() {
            this.fields = Collections.emptyMap();
            this.lastFieldNumber = 0;
            this.lastField = null;
        }

        public Builder clear() {
            reinitialize();
            return this;
        }

        public Builder mergeFrom(UnknownFieldSet other) {
            if (other != UnknownFieldSet.getDefaultInstance()) {
                for (Map.Entry<Integer, Field> entry : other.fields.entrySet()) {
                    mergeField(entry.getKey().intValue(), entry.getValue());
                }
            }
            return this;
        }

        public Builder mergeField(int number, Field field) {
            if (number != 0) {
                if (hasField(number)) {
                    getFieldBuilder(number).mergeFrom(field);
                } else {
                    addField(number, field);
                }
                return this;
            }
            throw new IllegalArgumentException("Zero is not a valid field number.");
        }

        public Builder mergeVarintField(int number, int value) {
            if (number != 0) {
                getFieldBuilder(number).addVarint((long) value);
                return this;
            }
            throw new IllegalArgumentException("Zero is not a valid field number.");
        }

        public boolean hasField(int number) {
            if (number != 0) {
                return number == this.lastFieldNumber || this.fields.containsKey(Integer.valueOf(number));
            }
            throw new IllegalArgumentException("Zero is not a valid field number.");
        }

        public Builder addField(int number, Field field) {
            if (number != 0) {
                if (this.lastField != null && this.lastFieldNumber == number) {
                    this.lastField = null;
                    this.lastFieldNumber = 0;
                }
                if (this.fields.isEmpty()) {
                    this.fields = new TreeMap();
                }
                this.fields.put(Integer.valueOf(number), field);
                return this;
            }
            throw new IllegalArgumentException("Zero is not a valid field number.");
        }

        public Map<Integer, Field> asMap() {
            getFieldBuilder(0);
            return Collections.unmodifiableMap(this.fields);
        }

        /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: 
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
            	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
            */
        public com.google.protobuf.UnknownFieldSet.Builder mergeFrom(com.google.protobuf.CodedInputStream r3) throws java.io.IOException {
            /*
                r2 = this;
            L_0x0000:
                int r0 = r3.readTag()
                if (r0 == 0) goto L_0x000e
                boolean r1 = r2.mergeFieldFrom(r0, r3)
                if (r1 != 0) goto L_0x000d
                goto L_0x000e
            L_0x000d:
                goto L_0x0000
            L_0x000e:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.UnknownFieldSet.Builder.mergeFrom(com.google.protobuf.CodedInputStream):com.google.protobuf.UnknownFieldSet$Builder");
        }

        public boolean mergeFieldFrom(int tag, CodedInputStream input) throws IOException {
            int number = WireFormat.getTagFieldNumber(tag);
            int tagWireType = WireFormat.getTagWireType(tag);
            if (tagWireType == 0) {
                getFieldBuilder(number).addVarint(input.readInt64());
                return true;
            } else if (tagWireType == 1) {
                getFieldBuilder(number).addFixed64(input.readFixed64());
                return true;
            } else if (tagWireType == 2) {
                getFieldBuilder(number).addLengthDelimited(input.readBytes());
                return true;
            } else if (tagWireType == 3) {
                Builder subBuilder = UnknownFieldSet.newBuilder();
                input.readGroup(number, (MessageLite.Builder) subBuilder, (ExtensionRegistryLite) ExtensionRegistry.getEmptyRegistry());
                getFieldBuilder(number).addGroup(subBuilder.build());
                return true;
            } else if (tagWireType == 4) {
                return false;
            } else {
                if (tagWireType == 5) {
                    getFieldBuilder(number).addFixed32(input.readFixed32());
                    return true;
                }
                throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        public Builder mergeFrom(ByteString data) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = data.newCodedInput();
                mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e2);
            }
        }

        public Builder mergeFrom(byte[] data) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data);
                mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e2);
            }
        }

        public Builder mergeFrom(InputStream input) throws IOException {
            CodedInputStream codedInput = CodedInputStream.newInstance(input);
            mergeFrom(codedInput);
            codedInput.checkLastTagWas(0);
            return this;
        }

        public boolean mergeDelimitedFrom(InputStream input) throws IOException {
            int firstByte = input.read();
            if (firstByte == -1) {
                return false;
            }
            mergeFrom(new AbstractMessageLite.Builder.LimitedInputStream(input, CodedInputStream.readRawVarint32(firstByte, input)));
            return true;
        }

        public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return mergeDelimitedFrom(input);
        }

        public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return mergeFrom(input);
        }

        public Builder mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return mergeFrom(data);
        }

        public Builder mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data, off, len);
                mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e2);
            }
        }

        public Builder mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return mergeFrom(data);
        }

        public Builder mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return mergeFrom(data, off, len);
        }

        public Builder mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return mergeFrom(input);
        }

        public boolean isInitialized() {
            return true;
        }
    }

    public static final class Field {
        private static final Field fieldDefaultInstance = newBuilder().build();
        /* access modifiers changed from: private */
        public List<Integer> fixed32;
        /* access modifiers changed from: private */
        public List<Long> fixed64;
        /* access modifiers changed from: private */
        public List<UnknownFieldSet> group;
        /* access modifiers changed from: private */
        public List<ByteString> lengthDelimited;
        /* access modifiers changed from: private */
        public List<Long> varint;

        private Field() {
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public static Builder newBuilder(Field copyFrom) {
            return newBuilder().mergeFrom(copyFrom);
        }

        public static Field getDefaultInstance() {
            return fieldDefaultInstance;
        }

        public List<Long> getVarintList() {
            return this.varint;
        }

        public List<Integer> getFixed32List() {
            return this.fixed32;
        }

        public List<Long> getFixed64List() {
            return this.fixed64;
        }

        public List<ByteString> getLengthDelimitedList() {
            return this.lengthDelimited;
        }

        public List<UnknownFieldSet> getGroupList() {
            return this.group;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Field)) {
                return false;
            }
            return Arrays.equals(getIdentityArray(), ((Field) other).getIdentityArray());
        }

        public int hashCode() {
            return Arrays.hashCode(getIdentityArray());
        }

        private Object[] getIdentityArray() {
            return new Object[]{this.varint, this.fixed32, this.fixed64, this.lengthDelimited, this.group};
        }

        public void writeTo(int fieldNumber, CodedOutputStream output) throws IOException {
            for (Long longValue : this.varint) {
                output.writeUInt64(fieldNumber, longValue.longValue());
            }
            for (Integer intValue : this.fixed32) {
                output.writeFixed32(fieldNumber, intValue.intValue());
            }
            for (Long longValue2 : this.fixed64) {
                output.writeFixed64(fieldNumber, longValue2.longValue());
            }
            for (ByteString value : this.lengthDelimited) {
                output.writeBytes(fieldNumber, value);
            }
            for (UnknownFieldSet value2 : this.group) {
                output.writeGroup(fieldNumber, value2);
            }
        }

        public int getSerializedSize(int fieldNumber) {
            int result = 0;
            for (Long longValue : this.varint) {
                result += CodedOutputStream.computeUInt64Size(fieldNumber, longValue.longValue());
            }
            for (Integer intValue : this.fixed32) {
                result += CodedOutputStream.computeFixed32Size(fieldNumber, intValue.intValue());
            }
            for (Long longValue2 : this.fixed64) {
                result += CodedOutputStream.computeFixed64Size(fieldNumber, longValue2.longValue());
            }
            for (ByteString value : this.lengthDelimited) {
                result += CodedOutputStream.computeBytesSize(fieldNumber, value);
            }
            for (UnknownFieldSet value2 : this.group) {
                result += CodedOutputStream.computeGroupSize(fieldNumber, value2);
            }
            return result;
        }

        public void writeAsMessageSetExtensionTo(int fieldNumber, CodedOutputStream output) throws IOException {
            for (ByteString value : this.lengthDelimited) {
                output.writeRawMessageSetExtension(fieldNumber, value);
            }
        }

        public int getSerializedSizeAsMessageSetExtension(int fieldNumber) {
            int result = 0;
            for (ByteString value : this.lengthDelimited) {
                result += CodedOutputStream.computeRawMessageSetExtensionSize(fieldNumber, value);
            }
            return result;
        }

        public static final class Builder {
            private Field result;

            private Builder() {
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                Builder builder = new Builder();
                builder.result = new Field();
                return builder;
            }

            public Field build() {
                if (this.result.varint == null) {
                    List unused = this.result.varint = Collections.emptyList();
                } else {
                    Field field = this.result;
                    List unused2 = field.varint = Collections.unmodifiableList(field.varint);
                }
                if (this.result.fixed32 == null) {
                    List unused3 = this.result.fixed32 = Collections.emptyList();
                } else {
                    Field field2 = this.result;
                    List unused4 = field2.fixed32 = Collections.unmodifiableList(field2.fixed32);
                }
                if (this.result.fixed64 == null) {
                    List unused5 = this.result.fixed64 = Collections.emptyList();
                } else {
                    Field field3 = this.result;
                    List unused6 = field3.fixed64 = Collections.unmodifiableList(field3.fixed64);
                }
                if (this.result.lengthDelimited == null) {
                    List unused7 = this.result.lengthDelimited = Collections.emptyList();
                } else {
                    Field field4 = this.result;
                    List unused8 = field4.lengthDelimited = Collections.unmodifiableList(field4.lengthDelimited);
                }
                if (this.result.group == null) {
                    List unused9 = this.result.group = Collections.emptyList();
                } else {
                    Field field5 = this.result;
                    List unused10 = field5.group = Collections.unmodifiableList(field5.group);
                }
                Field returnMe = this.result;
                this.result = null;
                return returnMe;
            }

            public Builder clear() {
                this.result = new Field();
                return this;
            }

            public Builder mergeFrom(Field other) {
                if (!other.varint.isEmpty()) {
                    if (this.result.varint == null) {
                        List unused = this.result.varint = new ArrayList();
                    }
                    this.result.varint.addAll(other.varint);
                }
                if (!other.fixed32.isEmpty()) {
                    if (this.result.fixed32 == null) {
                        List unused2 = this.result.fixed32 = new ArrayList();
                    }
                    this.result.fixed32.addAll(other.fixed32);
                }
                if (!other.fixed64.isEmpty()) {
                    if (this.result.fixed64 == null) {
                        List unused3 = this.result.fixed64 = new ArrayList();
                    }
                    this.result.fixed64.addAll(other.fixed64);
                }
                if (!other.lengthDelimited.isEmpty()) {
                    if (this.result.lengthDelimited == null) {
                        List unused4 = this.result.lengthDelimited = new ArrayList();
                    }
                    this.result.lengthDelimited.addAll(other.lengthDelimited);
                }
                if (!other.group.isEmpty()) {
                    if (this.result.group == null) {
                        List unused5 = this.result.group = new ArrayList();
                    }
                    this.result.group.addAll(other.group);
                }
                return this;
            }

            public Builder addVarint(long value) {
                if (this.result.varint == null) {
                    List unused = this.result.varint = new ArrayList();
                }
                this.result.varint.add(Long.valueOf(value));
                return this;
            }

            public Builder addFixed32(int value) {
                if (this.result.fixed32 == null) {
                    List unused = this.result.fixed32 = new ArrayList();
                }
                this.result.fixed32.add(Integer.valueOf(value));
                return this;
            }

            public Builder addFixed64(long value) {
                if (this.result.fixed64 == null) {
                    List unused = this.result.fixed64 = new ArrayList();
                }
                this.result.fixed64.add(Long.valueOf(value));
                return this;
            }

            public Builder addLengthDelimited(ByteString value) {
                if (this.result.lengthDelimited == null) {
                    List unused = this.result.lengthDelimited = new ArrayList();
                }
                this.result.lengthDelimited.add(value);
                return this;
            }

            public Builder addGroup(UnknownFieldSet value) {
                if (this.result.group == null) {
                    List unused = this.result.group = new ArrayList();
                }
                this.result.group.add(value);
                return this;
            }
        }
    }

    public static final class Parser extends AbstractParser<UnknownFieldSet> {
        public UnknownFieldSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            Builder builder = UnknownFieldSet.newBuilder();
            try {
                builder.mergeFrom(input);
                return builder.buildPartial();
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(builder.buildPartial());
            } catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(builder.buildPartial());
            }
        }
    }

    public final Parser getParserForType() {
        return PARSER;
    }
}
