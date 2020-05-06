package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessage extends AbstractMessageLite implements Message {
    private int memoizedSize = -1;

    public boolean isInitialized() {
        for (Descriptors.FieldDescriptor field : getDescriptorForType().getFields()) {
            if (field.isRequired() && !hasField(field)) {
                return false;
            }
        }
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : getAllFields().entrySet()) {
            Descriptors.FieldDescriptor field2 = entry.getKey();
            if (field2.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                if (field2.isRepeated()) {
                    for (Message element : (List) entry.getValue()) {
                        if (!element.isInitialized()) {
                            return false;
                        }
                    }
                    continue;
                } else if (!((Message) entry.getValue()).isInitialized()) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<String> findInitializationErrors() {
        return Builder.findMissingFields(this);
    }

    public String getInitializationErrorString() {
        return delimitWithCommas(findInitializationErrors());
    }

    /* access modifiers changed from: private */
    public static String delimitWithCommas(List<String> parts) {
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(part);
        }
        return result.toString();
    }

    public final String toString() {
        return TextFormat.printToString((MessageOrBuilder) this);
    }

    public void writeTo(CodedOutputStream output) throws IOException {
        boolean isMessageSet = getDescriptorForType().getOptions().getMessageSetWireFormat();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : getAllFields().entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();
            if (!isMessageSet || !field.isExtension() || field.getType() != Descriptors.FieldDescriptor.Type.MESSAGE || field.isRepeated()) {
                FieldSet.writeField(field, value, output);
            } else {
                output.writeMessageSetExtension(field.getNumber(), (Message) value);
            }
        }
        UnknownFieldSet unknownFields = getUnknownFields();
        if (isMessageSet) {
            unknownFields.writeAsMessageSetTo(output);
        } else {
            unknownFields.writeTo(output);
        }
    }

    public int getSerializedSize() {
        int size;
        int size2 = this.memoizedSize;
        if (size2 != -1) {
            return size2;
        }
        int size3 = 0;
        boolean isMessageSet = getDescriptorForType().getOptions().getMessageSetWireFormat();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : getAllFields().entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();
            if (!isMessageSet || !field.isExtension() || field.getType() != Descriptors.FieldDescriptor.Type.MESSAGE || field.isRepeated()) {
                size3 += FieldSet.computeFieldSize(field, value);
            } else {
                size3 += CodedOutputStream.computeMessageSetExtensionSize(field.getNumber(), (Message) value);
            }
        }
        UnknownFieldSet unknownFields = getUnknownFields();
        if (isMessageSet) {
            size = size3 + unknownFields.getSerializedSizeAsMessageSet();
        } else {
            size = size3 + unknownFields.getSerializedSize();
        }
        this.memoizedSize = size;
        return size;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Message)) {
            return false;
        }
        Message otherMessage = (Message) other;
        if (getDescriptorForType() != otherMessage.getDescriptorForType()) {
            return false;
        }
        if (!getAllFields().equals(otherMessage.getAllFields()) || !getUnknownFields().equals(otherMessage.getUnknownFields())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (hashFields((41 * 19) + getDescriptorForType().hashCode(), getAllFields()) * 29) + getUnknownFields().hashCode();
    }

    /* access modifiers changed from: protected */
    public int hashFields(int hash, Map<Descriptors.FieldDescriptor, Object> map) {
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : map.entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();
            int hash2 = (hash * 37) + field.getNumber();
            if (field.getType() != Descriptors.FieldDescriptor.Type.ENUM) {
                hash = (hash2 * 53) + value.hashCode();
            } else if (field.isRepeated() != 0) {
                hash = (hash2 * 53) + hashEnumList((List) value);
            } else {
                hash = (hash2 * 53) + hashEnum((Internal.EnumLite) value);
            }
        }
        return hash;
    }

    protected static int hashLong(long n) {
        return (int) ((n >>> 32) ^ n);
    }

    protected static int hashBoolean(boolean b) {
        return b ? 1231 : 1237;
    }

    /* access modifiers changed from: package-private */
    public UninitializedMessageException newUninitializedMessageException() {
        return Builder.newUninitializedMessageException(this);
    }

    protected static int hashEnum(Internal.EnumLite e) {
        return e.getNumber();
    }

    protected static int hashEnumList(List<? extends Internal.EnumLite> list) {
        int hash = 1;
        for (Internal.EnumLite e : list) {
            hash = (hash * 31) + hashEnum(e);
        }
        return hash;
    }

    public static abstract class Builder<BuilderType extends Builder> extends AbstractMessageLite.Builder<BuilderType> implements Message.Builder {
        public abstract BuilderType clone();

        public BuilderType clear() {
            for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : getAllFields().entrySet()) {
                clearField(entry.getKey());
            }
            return this;
        }

        public List<String> findInitializationErrors() {
            return findMissingFields(this);
        }

        public String getInitializationErrorString() {
            return AbstractMessage.delimitWithCommas(findInitializationErrors());
        }

        public BuilderType mergeFrom(Message other) {
            if (other.getDescriptorForType() == getDescriptorForType()) {
                for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : other.getAllFields().entrySet()) {
                    Descriptors.FieldDescriptor field = entry.getKey();
                    if (field.isRepeated()) {
                        for (Object element : (List) entry.getValue()) {
                            addRepeatedField(field, element);
                        }
                    } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        Message existingValue = (Message) getField(field);
                        if (existingValue == existingValue.getDefaultInstanceForType()) {
                            setField(field, entry.getValue());
                        } else {
                            setField(field, existingValue.newBuilderForType().mergeFrom(existingValue).mergeFrom((Message) entry.getValue()).build());
                        }
                    } else {
                        setField(field, entry.getValue());
                    }
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
        }

        public BuilderType mergeFrom(CodedInputStream input) throws IOException {
            return mergeFrom(input, (ExtensionRegistryLite) ExtensionRegistry.getEmptyRegistry());
        }

        public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            int tag;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(getUnknownFields());
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (mergeFieldFrom(input, unknownFields, extensionRegistry, getDescriptorForType(), this, (FieldSet<Descriptors.FieldDescriptor>) null, tag));
            setUnknownFields(unknownFields.build());
            return this;
        }

        private static void addRepeatedField(Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions, Descriptors.FieldDescriptor field, Object value) {
            if (builder != null) {
                builder.addRepeatedField(field, value);
            } else {
                extensions.addRepeatedField(field, value);
            }
        }

        private static void setField(Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions, Descriptors.FieldDescriptor field, Object value) {
            if (builder != null) {
                builder.setField(field, value);
            } else {
                extensions.setField(field, value);
            }
        }

        private static boolean hasOriginalMessage(Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions, Descriptors.FieldDescriptor field) {
            if (builder != null) {
                return builder.hasField(field);
            }
            return extensions.hasField(field);
        }

        private static Message getOriginalMessage(Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions, Descriptors.FieldDescriptor field) {
            if (builder != null) {
                return (Message) builder.getField(field);
            }
            return (Message) extensions.getField(field);
        }

        private static void mergeOriginalMessage(Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions, Descriptors.FieldDescriptor field, Message.Builder subBuilder) {
            Message originalMessage = getOriginalMessage(builder, extensions, field);
            if (originalMessage != null) {
                subBuilder.mergeFrom(originalMessage);
            }
        }

        static boolean mergeFieldFrom(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions, int tag) throws IOException {
            Descriptors.FieldDescriptor field;
            Object value;
            Message.Builder subBuilder;
            Message.Builder subBuilder2;
            CodedInputStream codedInputStream = input;
            UnknownFieldSet.Builder builder2 = unknownFields;
            ExtensionRegistryLite extensionRegistryLite = extensionRegistry;
            Descriptors.Descriptor descriptor = type;
            Message.Builder builder3 = builder;
            FieldSet<Descriptors.FieldDescriptor> fieldSet = extensions;
            int i = tag;
            if (!type.getOptions().getMessageSetWireFormat() || i != WireFormat.MESSAGE_SET_ITEM_TAG) {
                int wireType = WireFormat.getTagWireType(tag);
                int fieldNumber = WireFormat.getTagFieldNumber(tag);
                Message defaultInstance = null;
                if (!descriptor.isExtensionNumber(fieldNumber)) {
                    field = builder3 != null ? descriptor.findFieldByNumber(fieldNumber) : null;
                } else if (extensionRegistryLite instanceof ExtensionRegistry) {
                    ExtensionRegistry.ExtensionInfo extension = ((ExtensionRegistry) extensionRegistryLite).findExtensionByNumber(descriptor, fieldNumber);
                    if (extension == null) {
                        field = null;
                    } else {
                        field = extension.descriptor;
                        defaultInstance = extension.defaultInstance;
                        if (defaultInstance == null && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                            throw new IllegalStateException("Message-typed extension lacked default instance: " + field.getFullName());
                        }
                    }
                } else {
                    field = null;
                }
                boolean unknown = false;
                boolean packed = false;
                if (field == null) {
                    unknown = true;
                } else if (wireType == FieldSet.getWireFormatForFieldType(field.getLiteType(), false)) {
                    packed = false;
                } else if (!field.isPackable() || wireType != FieldSet.getWireFormatForFieldType(field.getLiteType(), true)) {
                    unknown = true;
                } else {
                    packed = true;
                }
                if (unknown) {
                    return builder2.mergeFieldFrom(i, codedInputStream);
                }
                if (packed) {
                    int limit = codedInputStream.pushLimit(input.readRawVarint32());
                    if (field.getLiteType() == WireFormat.FieldType.ENUM) {
                        while (input.getBytesUntilLimit() > 0) {
                            Object value2 = field.getEnumType().findValueByNumber(input.readEnum());
                            if (value2 == null) {
                                return true;
                            }
                            addRepeatedField(builder3, fieldSet, field, value2);
                        }
                    } else {
                        while (input.getBytesUntilLimit() > 0) {
                            addRepeatedField(builder3, fieldSet, field, FieldSet.readPrimitiveField(codedInputStream, field.getLiteType()));
                        }
                    }
                    codedInputStream.popLimit(limit);
                    return true;
                }
                int i2 = AnonymousClass1.$SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[field.getType().ordinal()];
                if (i2 == 1) {
                    if (defaultInstance != null) {
                        subBuilder = defaultInstance.newBuilderForType();
                    } else {
                        subBuilder = builder3.newBuilderForField(field);
                    }
                    if (!field.isRepeated()) {
                        mergeOriginalMessage(builder3, fieldSet, field, subBuilder);
                    }
                    codedInputStream.readGroup(field.getNumber(), (MessageLite.Builder) subBuilder, extensionRegistryLite);
                    value = subBuilder.buildPartial();
                } else if (i2 == 2) {
                    if (defaultInstance != null) {
                        subBuilder2 = defaultInstance.newBuilderForType();
                    } else {
                        subBuilder2 = builder3.newBuilderForField(field);
                    }
                    if (!field.isRepeated()) {
                        mergeOriginalMessage(builder3, fieldSet, field, subBuilder2);
                    }
                    codedInputStream.readMessage((MessageLite.Builder) subBuilder2, extensionRegistryLite);
                    value = subBuilder2.buildPartial();
                } else if (i2 != 3) {
                    value = FieldSet.readPrimitiveField(codedInputStream, field.getLiteType());
                } else {
                    int rawValue = input.readEnum();
                    value = field.getEnumType().findValueByNumber(rawValue);
                    if (value == null) {
                        builder2.mergeVarintField(fieldNumber, rawValue);
                        return true;
                    }
                }
                if (field.isRepeated()) {
                    addRepeatedField(builder3, fieldSet, field, value);
                    return true;
                }
                setField(builder3, fieldSet, field, value);
                return true;
            }
            mergeMessageSetExtensionFromCodedStream(input, unknownFields, extensionRegistry, type, builder, extensions);
            return true;
        }

        private static void mergeMessageSetExtensionFromCodedStream(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
            int typeId = 0;
            ByteString rawBytes = null;
            ExtensionRegistry.ExtensionInfo extension = null;
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    break;
                } else if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
                    typeId = input.readUInt32();
                    if (typeId != 0 && (extensionRegistry instanceof ExtensionRegistry)) {
                        extension = ((ExtensionRegistry) extensionRegistry).findExtensionByNumber(type, typeId);
                    }
                } else if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
                    if (typeId == 0 || extension == null || !ExtensionRegistryLite.isEagerlyParseMessageSets()) {
                        rawBytes = input.readBytes();
                    } else {
                        eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, builder, extensions);
                        rawBytes = null;
                    }
                } else if (!input.skipField(tag)) {
                    break;
                }
            }
            input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
            if (rawBytes != null && typeId != 0) {
                if (extension != null) {
                    mergeMessageSetExtensionFromBytes(rawBytes, extension, extensionRegistry, builder, extensions);
                } else {
                    unknownFields.mergeField(typeId, UnknownFieldSet.Field.newBuilder().addLengthDelimited(rawBytes).build());
                }
            }
        }

        private static void eagerlyMergeMessageSetExtension(CodedInputStream input, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
            Message value;
            Descriptors.FieldDescriptor field = extension.descriptor;
            if (hasOriginalMessage(builder, extensions, field)) {
                Message.Builder subBuilder = getOriginalMessage(builder, extensions, field).toBuilder();
                input.readMessage((MessageLite.Builder) subBuilder, extensionRegistry);
                value = subBuilder.buildPartial();
            } else {
                value = (Message) input.readMessage(extension.defaultInstance.getParserForType(), extensionRegistry);
            }
            if (builder != null) {
                builder.setField(field, value);
            } else {
                extensions.setField(field, value);
            }
        }

        private static void mergeMessageSetExtensionFromBytes(ByteString rawBytes, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, Message.Builder builder, FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
            Message value;
            Descriptors.FieldDescriptor field = extension.descriptor;
            boolean hasOriginalValue = hasOriginalMessage(builder, extensions, field);
            if (hasOriginalValue || ExtensionRegistryLite.isEagerlyParseMessageSets()) {
                if (hasOriginalValue) {
                    Message.Builder subBuilder = getOriginalMessage(builder, extensions, field).toBuilder();
                    subBuilder.mergeFrom(rawBytes, extensionRegistry);
                    value = subBuilder.buildPartial();
                } else {
                    value = (Message) extension.defaultInstance.getParserForType().parsePartialFrom(rawBytes, extensionRegistry);
                }
                setField(builder, extensions, field, value);
                return;
            }
            LazyField lazyField = new LazyField(extension.defaultInstance, extensionRegistry, rawBytes);
            if (builder == null) {
                extensions.setField(field, lazyField);
            } else if (builder instanceof GeneratedMessage.ExtendableBuilder) {
                builder.setField(field, lazyField);
            } else {
                builder.setField(field, lazyField.getValue());
            }
        }

        public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
            setUnknownFields(UnknownFieldSet.newBuilder(getUnknownFields()).mergeFrom(unknownFields).build());
            return this;
        }

        public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
            throw new UnsupportedOperationException("getFieldBuilder() called on an unsupported message type.");
        }

        protected static UninitializedMessageException newUninitializedMessageException(Message message) {
            return new UninitializedMessageException(findMissingFields(message));
        }

        /* access modifiers changed from: private */
        public static List<String> findMissingFields(MessageOrBuilder message) {
            List<String> results = new ArrayList<>();
            findMissingFields(message, "", results);
            return results;
        }

        private static void findMissingFields(MessageOrBuilder message, String prefix, List<String> results) {
            for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
                if (field.isRequired() && !message.hasField(field)) {
                    results.add(prefix + field.getName());
                }
            }
            for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
                Descriptors.FieldDescriptor field2 = entry.getKey();
                Object value = entry.getValue();
                if (field2.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    if (field2.isRepeated()) {
                        int i = 0;
                        for (MessageOrBuilder findMissingFields : (List) value) {
                            findMissingFields(findMissingFields, subMessagePrefix(prefix, field2, i), results);
                            i++;
                        }
                    } else if (message.hasField(field2)) {
                        findMissingFields((MessageOrBuilder) value, subMessagePrefix(prefix, field2, -1), results);
                    }
                }
            }
        }

        private static String subMessagePrefix(String prefix, Descriptors.FieldDescriptor field, int index) {
            StringBuilder result = new StringBuilder(prefix);
            if (field.isExtension()) {
                result.append('(');
                result.append(field.getFullName());
                result.append(')');
            } else {
                result.append(field.getName());
            }
            if (index != -1) {
                result.append('[');
                result.append(index);
                result.append(']');
            }
            result.append('.');
            return result.toString();
        }

        public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Builder) super.mergeFrom(data);
        }

        public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Builder) super.mergeFrom(data, extensionRegistry);
        }

        public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Builder) super.mergeFrom(data);
        }

        public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
            return (Builder) super.mergeFrom(data, off, len);
        }

        public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Builder) super.mergeFrom(data, extensionRegistry);
        }

        public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Builder) super.mergeFrom(data, off, len, extensionRegistry);
        }

        public BuilderType mergeFrom(InputStream input) throws IOException {
            return (Builder) super.mergeFrom(input);
        }

        public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Builder) super.mergeFrom(input, extensionRegistry);
        }

        public boolean mergeDelimitedFrom(InputStream input) throws IOException {
            return super.mergeDelimitedFrom(input);
        }

        public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return super.mergeDelimitedFrom(input, extensionRegistry);
        }
    }

    /* renamed from: com.google.protobuf.AbstractMessage$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type = new int[Descriptors.FieldDescriptor.Type.values().length];

        static {
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.GROUP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.MESSAGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[Descriptors.FieldDescriptor.Type.ENUM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}
