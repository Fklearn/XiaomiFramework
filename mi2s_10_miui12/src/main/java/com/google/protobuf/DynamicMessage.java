package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public final class DynamicMessage extends AbstractMessage {
    /* access modifiers changed from: private */
    public final FieldSet<Descriptors.FieldDescriptor> fields;
    private int memoizedSize;
    /* access modifiers changed from: private */
    public final Descriptors.Descriptor type;
    /* access modifiers changed from: private */
    public final UnknownFieldSet unknownFields;

    private DynamicMessage(Descriptors.Descriptor type2, FieldSet<Descriptors.FieldDescriptor> fields2, UnknownFieldSet unknownFields2) {
        this.memoizedSize = -1;
        this.type = type2;
        this.fields = fields2;
        this.unknownFields = unknownFields2;
    }

    public static DynamicMessage getDefaultInstance(Descriptors.Descriptor type2) {
        return new DynamicMessage(type2, FieldSet.emptySet(), UnknownFieldSet.getDefaultInstance());
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, CodedInputStream input) throws IOException {
        return ((Builder) newBuilder(type2).mergeFrom(input)).buildParsed();
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, CodedInputStream input, ExtensionRegistry extensionRegistry) throws IOException {
        return ((Builder) newBuilder(type2).mergeFrom(input, (ExtensionRegistryLite) extensionRegistry)).buildParsed();
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, ByteString data) throws InvalidProtocolBufferException {
        return ((Builder) newBuilder(type2).mergeFrom(data)).buildParsed();
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, ByteString data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
        return ((Builder) newBuilder(type2).mergeFrom(data, (ExtensionRegistryLite) extensionRegistry)).buildParsed();
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, byte[] data) throws InvalidProtocolBufferException {
        return ((Builder) newBuilder(type2).mergeFrom(data)).buildParsed();
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, byte[] data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
        return ((Builder) newBuilder(type2).mergeFrom(data, (ExtensionRegistryLite) extensionRegistry)).buildParsed();
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, InputStream input) throws IOException {
        return ((Builder) newBuilder(type2).mergeFrom(input)).buildParsed();
    }

    public static DynamicMessage parseFrom(Descriptors.Descriptor type2, InputStream input, ExtensionRegistry extensionRegistry) throws IOException {
        return ((Builder) newBuilder(type2).mergeFrom(input, (ExtensionRegistryLite) extensionRegistry)).buildParsed();
    }

    public static Builder newBuilder(Descriptors.Descriptor type2) {
        return new Builder(type2);
    }

    public static Builder newBuilder(Message prototype) {
        return new Builder(prototype.getDescriptorForType()).mergeFrom(prototype);
    }

    public Descriptors.Descriptor getDescriptorForType() {
        return this.type;
    }

    public DynamicMessage getDefaultInstanceForType() {
        return getDefaultInstance(this.type);
    }

    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        return this.fields.getAllFields();
    }

    public boolean hasField(Descriptors.FieldDescriptor field) {
        verifyContainingType(field);
        return this.fields.hasField(field);
    }

    public Object getField(Descriptors.FieldDescriptor field) {
        verifyContainingType(field);
        Object result = this.fields.getField(field);
        if (result != null) {
            return result;
        }
        if (field.isRepeated()) {
            return Collections.emptyList();
        }
        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            return getDefaultInstance(field.getMessageType());
        }
        return field.getDefaultValue();
    }

    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
        verifyContainingType(field);
        return this.fields.getRepeatedFieldCount(field);
    }

    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
        verifyContainingType(field);
        return this.fields.getRepeatedField(field, index);
    }

    public UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
    }

    /* access modifiers changed from: private */
    public static boolean isInitialized(Descriptors.Descriptor type2, FieldSet<Descriptors.FieldDescriptor> fields2) {
        for (Descriptors.FieldDescriptor field : type2.getFields()) {
            if (field.isRequired() && !fields2.hasField(field)) {
                return false;
            }
        }
        return fields2.isInitialized();
    }

    public boolean isInitialized() {
        return isInitialized(this.type, this.fields);
    }

    public void writeTo(CodedOutputStream output) throws IOException {
        if (this.type.getOptions().getMessageSetWireFormat()) {
            this.fields.writeMessageSetTo(output);
            this.unknownFields.writeAsMessageSetTo(output);
            return;
        }
        this.fields.writeTo(output);
        this.unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
        int size;
        int size2 = this.memoizedSize;
        if (size2 != -1) {
            return size2;
        }
        if (this.type.getOptions().getMessageSetWireFormat()) {
            size = this.fields.getMessageSetSerializedSize() + this.unknownFields.getSerializedSizeAsMessageSet();
        } else {
            size = this.fields.getSerializedSize() + this.unknownFields.getSerializedSize();
        }
        this.memoizedSize = size;
        return size;
    }

    public Builder newBuilderForType() {
        return new Builder(this.type);
    }

    public Builder toBuilder() {
        return newBuilderForType().mergeFrom((Message) this);
    }

    public Parser<DynamicMessage> getParserForType() {
        return new AbstractParser<DynamicMessage>() {
            public DynamicMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = DynamicMessage.newBuilder(DynamicMessage.this.type);
                try {
                    builder.mergeFrom(input, extensionRegistry);
                    return builder.buildPartial();
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(builder.buildPartial());
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(builder.buildPartial());
                }
            }
        };
    }

    private void verifyContainingType(Descriptors.FieldDescriptor field) {
        if (field.getContainingType() != this.type) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
        }
    }

    public static final class Builder extends AbstractMessage.Builder<Builder> {
        private FieldSet<Descriptors.FieldDescriptor> fields;
        private final Descriptors.Descriptor type;
        private UnknownFieldSet unknownFields;

        private Builder(Descriptors.Descriptor type2) {
            this.type = type2;
            this.fields = FieldSet.newFieldSet();
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public Builder clear() {
            if (this.fields.isImmutable()) {
                this.fields = FieldSet.newFieldSet();
            } else {
                this.fields.clear();
            }
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
            return this;
        }

        public Builder mergeFrom(Message other) {
            if (!(other instanceof DynamicMessage)) {
                return (Builder) super.mergeFrom(other);
            }
            DynamicMessage otherDynamicMessage = (DynamicMessage) other;
            if (otherDynamicMessage.type == this.type) {
                ensureIsMutable();
                this.fields.mergeFrom(otherDynamicMessage.fields);
                mergeUnknownFields(otherDynamicMessage.unknownFields);
                return this;
            }
            throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
        }

        public DynamicMessage build() {
            if (isInitialized()) {
                return buildPartial();
            }
            throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields, this.unknownFields));
        }

        /* access modifiers changed from: private */
        public DynamicMessage buildParsed() throws InvalidProtocolBufferException {
            if (isInitialized()) {
                return buildPartial();
            }
            throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields, this.unknownFields)).asInvalidProtocolBufferException();
        }

        public DynamicMessage buildPartial() {
            this.fields.makeImmutable();
            return new DynamicMessage(this.type, this.fields, this.unknownFields);
        }

        public Builder clone() {
            Builder result = new Builder(this.type);
            result.fields.mergeFrom(this.fields);
            result.mergeUnknownFields(this.unknownFields);
            return result;
        }

        public boolean isInitialized() {
            return DynamicMessage.isInitialized(this.type, this.fields);
        }

        public Descriptors.Descriptor getDescriptorForType() {
            return this.type;
        }

        public DynamicMessage getDefaultInstanceForType() {
            return DynamicMessage.getDefaultInstance(this.type);
        }

        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            return this.fields.getAllFields();
        }

        public Builder newBuilderForField(Descriptors.FieldDescriptor field) {
            verifyContainingType(field);
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                return new Builder(field.getMessageType());
            }
            throw new IllegalArgumentException("newBuilderForField is only valid for fields with message type.");
        }

        public boolean hasField(Descriptors.FieldDescriptor field) {
            verifyContainingType(field);
            return this.fields.hasField(field);
        }

        public Object getField(Descriptors.FieldDescriptor field) {
            verifyContainingType(field);
            Object result = this.fields.getField(field);
            if (result != null) {
                return result;
            }
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                return DynamicMessage.getDefaultInstance(field.getMessageType());
            }
            return field.getDefaultValue();
        }

        public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            verifyContainingType(field);
            ensureIsMutable();
            this.fields.setField(field, value);
            return this;
        }

        public Builder clearField(Descriptors.FieldDescriptor field) {
            verifyContainingType(field);
            ensureIsMutable();
            this.fields.clearField(field);
            return this;
        }

        public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
            verifyContainingType(field);
            return this.fields.getRepeatedFieldCount(field);
        }

        public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
            verifyContainingType(field);
            return this.fields.getRepeatedField(field, index);
        }

        public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            verifyContainingType(field);
            ensureIsMutable();
            this.fields.setRepeatedField(field, index, value);
            return this;
        }

        public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            verifyContainingType(field);
            ensureIsMutable();
            this.fields.addRepeatedField(field, value);
            return this;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        public Builder setUnknownFields(UnknownFieldSet unknownFields2) {
            this.unknownFields = unknownFields2;
            return this;
        }

        public Builder mergeUnknownFields(UnknownFieldSet unknownFields2) {
            this.unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields2).build();
            return this;
        }

        private void verifyContainingType(Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.type) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
        }

        private void ensureIsMutable() {
            if (this.fields.isImmutable()) {
                this.fields = this.fields.clone();
            }
        }

        public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a dynamic message type.");
        }
    }
}
