package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.GeneratedMessageLite.Builder;
import com.google.protobuf.Internal;
import com.google.protobuf.MessageLite;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class GeneratedMessageLite<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> extends AbstractMessageLite<MessageType, BuilderType> {
    protected int memoizedSerializedSize = -1;
    protected UnknownFieldSetLite unknownFields = UnknownFieldSetLite.getDefaultInstance();

    public interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends MessageLiteOrBuilder {
        <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite);

        <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int i);

        <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite);

        <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite);
    }

    public enum MethodToInvoke {
        IS_INITIALIZED,
        VISIT,
        MERGE_FROM_STREAM,
        MAKE_IMMUTABLE,
        NEW_MUTABLE_INSTANCE,
        NEW_BUILDER,
        GET_DEFAULT_INSTANCE,
        GET_PARSER
    }

    protected interface Visitor {
        boolean visitBoolean(boolean z, boolean z2, boolean z3, boolean z4);

        Internal.BooleanList visitBooleanList(Internal.BooleanList booleanList, Internal.BooleanList booleanList2);

        ByteString visitByteString(boolean z, ByteString byteString, boolean z2, ByteString byteString2);

        double visitDouble(boolean z, double d, boolean z2, double d2);

        Internal.DoubleList visitDoubleList(Internal.DoubleList doubleList, Internal.DoubleList doubleList2);

        FieldSet<ExtensionDescriptor> visitExtensions(FieldSet<ExtensionDescriptor> fieldSet, FieldSet<ExtensionDescriptor> fieldSet2);

        float visitFloat(boolean z, float f, boolean z2, float f2);

        Internal.FloatList visitFloatList(Internal.FloatList floatList, Internal.FloatList floatList2);

        int visitInt(boolean z, int i, boolean z2, int i2);

        Internal.IntList visitIntList(Internal.IntList intList, Internal.IntList intList2);

        LazyFieldLite visitLazyMessage(boolean z, LazyFieldLite lazyFieldLite, boolean z2, LazyFieldLite lazyFieldLite2);

        <T> Internal.ProtobufList<T> visitList(Internal.ProtobufList<T> protobufList, Internal.ProtobufList<T> protobufList2);

        long visitLong(boolean z, long j, boolean z2, long j2);

        Internal.LongList visitLongList(Internal.LongList longList, Internal.LongList longList2);

        <K, V> MapFieldLite<K, V> visitMap(MapFieldLite<K, V> mapFieldLite, MapFieldLite<K, V> mapFieldLite2);

        <T extends MessageLite> T visitMessage(T t, T t2);

        Object visitOneofBoolean(boolean z, Object obj, Object obj2);

        Object visitOneofByteString(boolean z, Object obj, Object obj2);

        Object visitOneofDouble(boolean z, Object obj, Object obj2);

        Object visitOneofFloat(boolean z, Object obj, Object obj2);

        Object visitOneofInt(boolean z, Object obj, Object obj2);

        Object visitOneofLazyMessage(boolean z, Object obj, Object obj2);

        Object visitOneofLong(boolean z, Object obj, Object obj2);

        Object visitOneofMessage(boolean z, Object obj, Object obj2);

        void visitOneofNotSet(boolean z);

        Object visitOneofString(boolean z, Object obj, Object obj2);

        String visitString(boolean z, String str, boolean z2, String str2);

        UnknownFieldSetLite visitUnknownFields(UnknownFieldSetLite unknownFieldSetLite, UnknownFieldSetLite unknownFieldSetLite2);
    }

    /* access modifiers changed from: protected */
    public abstract Object dynamicMethod(MethodToInvoke methodToInvoke, Object obj, Object obj2);

    public final Parser<MessageType> getParserForType() {
        return (Parser) dynamicMethod(MethodToInvoke.GET_PARSER);
    }

    public final MessageType getDefaultInstanceForType() {
        return (GeneratedMessageLite) dynamicMethod(MethodToInvoke.GET_DEFAULT_INSTANCE);
    }

    public final BuilderType newBuilderForType() {
        return (Builder) dynamicMethod(MethodToInvoke.NEW_BUILDER);
    }

    public String toString() {
        return MessageLiteToString.toString(this, super.toString());
    }

    public int hashCode() {
        if (this.memoizedHashCode == 0) {
            HashCodeVisitor visitor = new HashCodeVisitor((AnonymousClass1) null);
            visit(visitor, this);
            this.memoizedHashCode = visitor.hashCode;
        }
        return this.memoizedHashCode;
    }

    /* access modifiers changed from: package-private */
    public int hashCode(HashCodeVisitor visitor) {
        if (this.memoizedHashCode == 0) {
            int inProgressHashCode = visitor.hashCode;
            int unused = visitor.hashCode = 0;
            visit(visitor, this);
            this.memoizedHashCode = visitor.hashCode;
            int unused2 = visitor.hashCode = inProgressHashCode;
        }
        return this.memoizedHashCode;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!getDefaultInstanceForType().getClass().isInstance(other)) {
            return false;
        }
        try {
            visit(EqualsVisitor.INSTANCE, (GeneratedMessageLite) other);
            return true;
        } catch (EqualsVisitor.NotEqualsException e) {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean equals(EqualsVisitor visitor, MessageLite other) {
        if (this == other) {
            return true;
        }
        if (!getDefaultInstanceForType().getClass().isInstance(other)) {
            return false;
        }
        visit(visitor, (GeneratedMessageLite) other);
        return true;
    }

    private final void ensureUnknownFieldsInitialized() {
        if (this.unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
            this.unknownFields = UnknownFieldSetLite.newInstance();
        }
    }

    /* access modifiers changed from: protected */
    public boolean parseUnknownField(int tag, CodedInputStream input) throws IOException {
        if (WireFormat.getTagWireType(tag) == 4) {
            return false;
        }
        ensureUnknownFieldsInitialized();
        return this.unknownFields.mergeFieldFrom(tag, input);
    }

    /* access modifiers changed from: protected */
    public void mergeVarintField(int tag, int value) {
        ensureUnknownFieldsInitialized();
        this.unknownFields.mergeVarintField(tag, value);
    }

    /* access modifiers changed from: protected */
    public void mergeLengthDelimitedField(int fieldNumber, ByteString value) {
        ensureUnknownFieldsInitialized();
        this.unknownFields.mergeLengthDelimitedField(fieldNumber, value);
    }

    /* access modifiers changed from: protected */
    public void makeImmutable() {
        dynamicMethod(MethodToInvoke.MAKE_IMMUTABLE);
        this.unknownFields.makeImmutable();
    }

    public final boolean isInitialized() {
        return dynamicMethod(MethodToInvoke.IS_INITIALIZED, Boolean.TRUE) != null;
    }

    public final BuilderType toBuilder() {
        BuilderType builder = (Builder) dynamicMethod(MethodToInvoke.NEW_BUILDER);
        builder.mergeFrom(this);
        return builder;
    }

    /* access modifiers changed from: protected */
    public Object dynamicMethod(MethodToInvoke method, Object arg0) {
        return dynamicMethod(method, arg0, (Object) null);
    }

    /* access modifiers changed from: protected */
    public Object dynamicMethod(MethodToInvoke method) {
        return dynamicMethod(method, (Object) null, (Object) null);
    }

    /* access modifiers changed from: package-private */
    public void visit(Visitor visitor, MessageType other) {
        dynamicMethod(MethodToInvoke.VISIT, visitor, other);
        this.unknownFields = visitor.visitUnknownFields(this.unknownFields, other.unknownFields);
    }

    /* access modifiers changed from: protected */
    public final void mergeUnknownFields(UnknownFieldSetLite unknownFields2) {
        this.unknownFields = UnknownFieldSetLite.mutableCopyOf(this.unknownFields, unknownFields2);
    }

    public static abstract class Builder<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> extends AbstractMessageLite.Builder<MessageType, BuilderType> {
        private final MessageType defaultInstance;
        protected MessageType instance;
        protected boolean isBuilt = false;

        protected Builder(MessageType defaultInstance2) {
            this.defaultInstance = defaultInstance2;
            this.instance = (GeneratedMessageLite) defaultInstance2.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
        }

        /* access modifiers changed from: protected */
        public void copyOnWrite() {
            if (this.isBuilt) {
                MessageType newInstance = (GeneratedMessageLite) this.instance.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
                newInstance.visit(MergeFromVisitor.INSTANCE, this.instance);
                this.instance = newInstance;
                this.isBuilt = false;
            }
        }

        public final boolean isInitialized() {
            return GeneratedMessageLite.isInitialized(this.instance, false);
        }

        public final BuilderType clear() {
            this.instance = (GeneratedMessageLite) this.instance.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
            return this;
        }

        public BuilderType clone() {
            BuilderType builder = getDefaultInstanceForType().newBuilderForType();
            builder.mergeFrom(buildPartial());
            return builder;
        }

        public MessageType buildPartial() {
            if (this.isBuilt) {
                return this.instance;
            }
            this.instance.makeImmutable();
            this.isBuilt = true;
            return this.instance;
        }

        public final MessageType build() {
            MessageType result = buildPartial();
            if (result.isInitialized()) {
                return result;
            }
            throw newUninitializedMessageException(result);
        }

        /* access modifiers changed from: protected */
        public BuilderType internalMergeFrom(MessageType message) {
            return mergeFrom(message);
        }

        public BuilderType mergeFrom(MessageType message) {
            copyOnWrite();
            this.instance.visit(MergeFromVisitor.INSTANCE, message);
            return this;
        }

        public MessageType getDefaultInstanceForType() {
            return this.defaultInstance;
        }

        public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            copyOnWrite();
            try {
                this.instance.dynamicMethod(MethodToInvoke.MERGE_FROM_STREAM, input, extensionRegistry);
                return this;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    throw ((IOException) e.getCause());
                }
                throw e;
            }
        }
    }

    public static abstract class ExtendableMessage<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends GeneratedMessageLite<MessageType, BuilderType> implements ExtendableMessageOrBuilder<MessageType, BuilderType> {
        protected FieldSet<ExtensionDescriptor> extensions = FieldSet.newFieldSet();

        /* access modifiers changed from: protected */
        public final void mergeExtensionFields(MessageType other) {
            if (this.extensions.isImmutable()) {
                this.extensions = this.extensions.clone();
            }
            this.extensions.mergeFrom(other.extensions);
        }

        /* access modifiers changed from: package-private */
        public final void visit(Visitor visitor, MessageType other) {
            GeneratedMessageLite.super.visit(visitor, other);
            this.extensions = visitor.visitExtensions(this.extensions, other.extensions);
        }

        /* access modifiers changed from: protected */
        public <MessageType extends MessageLite> boolean parseUnknownField(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
            Object value;
            MessageLite existingValue;
            CodedInputStream codedInputStream = input;
            ExtensionRegistryLite extensionRegistryLite = extensionRegistry;
            int wireType = WireFormat.getTagWireType(tag);
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            GeneratedExtension<MessageType, ?> extension = extensionRegistryLite.findLiteExtensionByNumber(defaultInstance, fieldNumber);
            boolean unknown = false;
            boolean packed = false;
            boolean z = true;
            if (extension == null) {
                unknown = true;
            } else if (wireType == FieldSet.getWireFormatForFieldType(extension.descriptor.getLiteType(), false)) {
                packed = false;
            } else if (!extension.descriptor.isRepeated || !extension.descriptor.type.isPackable() || wireType != FieldSet.getWireFormatForFieldType(extension.descriptor.getLiteType(), true)) {
                unknown = true;
            } else {
                packed = true;
            }
            if (unknown) {
                return parseUnknownField(tag, codedInputStream);
            }
            int i = tag;
            if (packed) {
                int limit = codedInputStream.pushLimit(input.readRawVarint32());
                if (extension.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
                    while (input.getBytesUntilLimit() > 0) {
                        Object value2 = extension.descriptor.getEnumType().findValueByNumber(input.readEnum());
                        if (value2 == null) {
                            return z;
                        }
                        this.extensions.addRepeatedField(extension.descriptor, extension.singularToFieldSetType(value2));
                        wireType = wireType;
                        z = true;
                    }
                } else {
                    while (input.getBytesUntilLimit() > 0) {
                        this.extensions.addRepeatedField(extension.descriptor, FieldSet.readPrimitiveField(codedInputStream, extension.descriptor.getLiteType(), false));
                    }
                }
                codedInputStream.popLimit(limit);
                return true;
            }
            int i2 = AnonymousClass1.$SwitchMap$com$google$protobuf$WireFormat$JavaType[extension.descriptor.getLiteJavaType().ordinal()];
            if (i2 == 1) {
                MessageLite.Builder subBuilder = null;
                if (!extension.descriptor.isRepeated() && (existingValue = (MessageLite) this.extensions.getField(extension.descriptor)) != null) {
                    subBuilder = existingValue.toBuilder();
                }
                if (subBuilder == null) {
                    subBuilder = extension.getMessageDefaultInstance().newBuilderForType();
                }
                if (extension.descriptor.getLiteType() == WireFormat.FieldType.GROUP) {
                    codedInputStream.readGroup(extension.getNumber(), subBuilder, extensionRegistryLite);
                } else {
                    codedInputStream.readMessage(subBuilder, extensionRegistryLite);
                }
                value = subBuilder.build();
            } else if (i2 != 2) {
                value = FieldSet.readPrimitiveField(codedInputStream, extension.descriptor.getLiteType(), false);
            } else {
                int rawValue = input.readEnum();
                value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
                if (value == null) {
                    mergeVarintField(fieldNumber, rawValue);
                    return true;
                }
            }
            if (extension.descriptor.isRepeated()) {
                this.extensions.addRepeatedField(extension.descriptor, extension.singularToFieldSetType(value));
                return true;
            }
            this.extensions.setField(extension.descriptor, extension.singularToFieldSetType(value));
            return true;
        }

        private void verifyExtensionContainingType(GeneratedExtension<MessageType, ?> extension) {
            if (extension.getContainingTypeDefaultInstance() != getDefaultInstanceForType()) {
                throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
            }
        }

        public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
            GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            return this.extensions.hasField(extensionLite.descriptor);
        }

        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
            GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            return this.extensions.getRepeatedFieldCount(extensionLite.descriptor);
        }

        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
            GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            Object value = this.extensions.getField(extensionLite.descriptor);
            if (value == null) {
                return extensionLite.defaultValue;
            }
            return extensionLite.fromFieldSetType(value);
        }

        public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index) {
            GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            return extensionLite.singularFromFieldSetType(this.extensions.getRepeatedField(extensionLite.descriptor, index));
        }

        /* access modifiers changed from: protected */
        public boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
        }

        /* access modifiers changed from: protected */
        public final void makeImmutable() {
            GeneratedMessageLite.super.makeImmutable();
            this.extensions.makeImmutable();
        }

        protected class ExtensionWriter {
            private final Iterator<Map.Entry<ExtensionDescriptor, Object>> iter;
            private final boolean messageSetWireFormat;
            private Map.Entry<ExtensionDescriptor, Object> next;

            /* synthetic */ ExtensionWriter(ExtendableMessage x0, boolean x1, AnonymousClass1 x2) {
                this(x1);
            }

            private ExtensionWriter(boolean messageSetWireFormat2) {
                this.iter = ExtendableMessage.this.extensions.iterator();
                if (this.iter.hasNext()) {
                    this.next = this.iter.next();
                }
                this.messageSetWireFormat = messageSetWireFormat2;
            }

            public void writeUntil(int end, CodedOutputStream output) throws IOException {
                while (true) {
                    Map.Entry<ExtensionDescriptor, Object> entry = this.next;
                    if (entry != null && entry.getKey().getNumber() < end) {
                        ExtensionDescriptor extension = this.next.getKey();
                        if (!this.messageSetWireFormat || extension.getLiteJavaType() != WireFormat.JavaType.MESSAGE || extension.isRepeated()) {
                            FieldSet.writeField(extension, this.next.getValue(), output);
                        } else {
                            output.writeMessageSetExtension(extension.getNumber(), (MessageLite) this.next.getValue());
                        }
                        if (this.iter.hasNext()) {
                            this.next = this.iter.next();
                        } else {
                            this.next = null;
                        }
                    } else {
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public ExtendableMessage<MessageType, BuilderType>.ExtensionWriter newExtensionWriter() {
            return new ExtensionWriter(this, false, (AnonymousClass1) null);
        }

        /* access modifiers changed from: protected */
        public ExtendableMessage<MessageType, BuilderType>.ExtensionWriter newMessageSetExtensionWriter() {
            return new ExtensionWriter(this, true, (AnonymousClass1) null);
        }

        /* access modifiers changed from: protected */
        public int extensionsSerializedSize() {
            return this.extensions.getSerializedSize();
        }

        /* access modifiers changed from: protected */
        public int extensionsSerializedSizeAsMessageSet() {
            return this.extensions.getMessageSetSerializedSize();
        }
    }

    /* renamed from: com.google.protobuf.GeneratedMessageLite$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$JavaType = new int[WireFormat.JavaType.values().length];

        static {
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.MESSAGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.ENUM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends Builder<MessageType, BuilderType> implements ExtendableMessageOrBuilder<MessageType, BuilderType> {
        protected ExtendableBuilder(MessageType defaultInstance) {
            super(defaultInstance);
            ((ExtendableMessage) this.instance).extensions = ((ExtendableMessage) this.instance).extensions.clone();
        }

        /* access modifiers changed from: package-private */
        public void internalSetExtensionSet(FieldSet<ExtensionDescriptor> extensions) {
            copyOnWrite();
            ((ExtendableMessage) this.instance).extensions = extensions;
        }

        /* access modifiers changed from: protected */
        public void copyOnWrite() {
            if (this.isBuilt) {
                super.copyOnWrite();
                ((ExtendableMessage) this.instance).extensions = ((ExtendableMessage) this.instance).extensions.clone();
            }
        }

        public final MessageType buildPartial() {
            if (this.isBuilt) {
                return (ExtendableMessage) this.instance;
            }
            ((ExtendableMessage) this.instance).extensions.makeImmutable();
            return (ExtendableMessage) super.buildPartial();
        }

        private void verifyExtensionContainingType(GeneratedExtension<MessageType, ?> extension) {
            if (extension.getContainingTypeDefaultInstance() != getDefaultInstanceForType()) {
                throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
            }
        }

        public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
            return ((ExtendableMessage) this.instance).hasExtension(extension);
        }

        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
            return ((ExtendableMessage) this.instance).getExtensionCount(extension);
        }

        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
            return ((ExtendableMessage) this.instance).getExtension(extension);
        }

        /* JADX WARNING: type inference failed for: r2v0, types: [com.google.protobuf.ExtensionLite, com.google.protobuf.ExtensionLite<MessageType, java.util.List<Type>>] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final <Type> Type getExtension(com.google.protobuf.ExtensionLite<MessageType, java.util.List<Type>> r2, int r3) {
            /*
                r1 = this;
                com.google.protobuf.GeneratedMessageLite r0 = r1.instance
                com.google.protobuf.GeneratedMessageLite$ExtendableMessage r0 = (com.google.protobuf.GeneratedMessageLite.ExtendableMessage) r0
                java.lang.Object r0 = r0.getExtension(r2, r3)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.GeneratedMessageLite.ExtendableBuilder.getExtension(com.google.protobuf.ExtensionLite, int):java.lang.Object");
        }

        public BuilderType clone() {
            return (ExtendableBuilder) super.clone();
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extension, Type value) {
            GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            copyOnWrite();
            ((ExtendableMessage) this.instance).extensions.setField(extensionLite.descriptor, extensionLite.toFieldSetType(value));
            return this;
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extension, int index, Type value) {
            GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            copyOnWrite();
            ((ExtendableMessage) this.instance).extensions.setRepeatedField(extensionLite.descriptor, index, extensionLite.singularToFieldSetType(value));
            return this;
        }

        public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extension, Type value) {
            GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            copyOnWrite();
            ((ExtendableMessage) this.instance).extensions.addRepeatedField(extensionLite.descriptor, extensionLite.singularToFieldSetType(value));
            return this;
        }

        public final <Type> BuilderType clearExtension(ExtensionLite<MessageType, ?> extension) {
            GeneratedExtension<MessageType, ?> extensionLite = GeneratedMessageLite.checkIsLite(extension);
            verifyExtensionContainingType(extensionLite);
            copyOnWrite();
            ((ExtendableMessage) this.instance).extensions.clearField(extensionLite.descriptor);
            return this;
        }
    }

    public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newSingularGeneratedExtension(ContainingType containingTypeDefaultInstance, Type defaultValue, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, Class singularType) {
        return new GeneratedExtension(containingTypeDefaultInstance, defaultValue, messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, false, false), singularType);
    }

    public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newRepeatedGeneratedExtension(ContainingType containingTypeDefaultInstance, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, boolean isPacked, Class singularType) {
        return new GeneratedExtension(containingTypeDefaultInstance, Collections.emptyList(), messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, true, isPacked), singularType);
    }

    static final class ExtensionDescriptor implements FieldSet.FieldDescriptorLite<ExtensionDescriptor> {
        final Internal.EnumLiteMap<?> enumTypeMap;
        final boolean isPacked;
        final boolean isRepeated;
        final int number;
        final WireFormat.FieldType type;

        ExtensionDescriptor(Internal.EnumLiteMap<?> enumTypeMap2, int number2, WireFormat.FieldType type2, boolean isRepeated2, boolean isPacked2) {
            this.enumTypeMap = enumTypeMap2;
            this.number = number2;
            this.type = type2;
            this.isRepeated = isRepeated2;
            this.isPacked = isPacked2;
        }

        public int getNumber() {
            return this.number;
        }

        public WireFormat.FieldType getLiteType() {
            return this.type;
        }

        public WireFormat.JavaType getLiteJavaType() {
            return this.type.getJavaType();
        }

        public boolean isRepeated() {
            return this.isRepeated;
        }

        public boolean isPacked() {
            return this.isPacked;
        }

        public Internal.EnumLiteMap<?> getEnumType() {
            return this.enumTypeMap;
        }

        public MessageLite.Builder internalMergeFrom(MessageLite.Builder to, MessageLite from) {
            return ((Builder) to).mergeFrom((GeneratedMessageLite) from);
        }

        public int compareTo(ExtensionDescriptor other) {
            return this.number - other.number;
        }
    }

    static Method getMethodOrDie(Class clazz, String name, Class... params) {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Generated message class \"" + clazz.getName() + "\" missing method \"" + name + "\".", e);
        }
    }

    static Object invokeOrDie(Method method, Object object, Object... params) {
        try {
            return method.invoke(object, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
        } catch (InvocationTargetException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            } else if (cause instanceof Error) {
                throw ((Error) cause);
            } else {
                throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
            }
        }
    }

    public static class GeneratedExtension<ContainingType extends MessageLite, Type> extends ExtensionLite<ContainingType, Type> {
        final ContainingType containingTypeDefaultInstance;
        final Type defaultValue;
        final ExtensionDescriptor descriptor;
        final MessageLite messageDefaultInstance;

        GeneratedExtension(ContainingType containingTypeDefaultInstance2, Type defaultValue2, MessageLite messageDefaultInstance2, ExtensionDescriptor descriptor2, Class singularType) {
            if (containingTypeDefaultInstance2 == null) {
                throw new IllegalArgumentException("Null containingTypeDefaultInstance");
            } else if (descriptor2.getLiteType() == WireFormat.FieldType.MESSAGE && messageDefaultInstance2 == null) {
                throw new IllegalArgumentException("Null messageDefaultInstance");
            } else {
                this.containingTypeDefaultInstance = containingTypeDefaultInstance2;
                this.defaultValue = defaultValue2;
                this.messageDefaultInstance = messageDefaultInstance2;
                this.descriptor = descriptor2;
            }
        }

        public ContainingType getContainingTypeDefaultInstance() {
            return this.containingTypeDefaultInstance;
        }

        public int getNumber() {
            return this.descriptor.getNumber();
        }

        public MessageLite getMessageDefaultInstance() {
            return this.messageDefaultInstance;
        }

        /* access modifiers changed from: package-private */
        public Object fromFieldSetType(Object value) {
            if (!this.descriptor.isRepeated()) {
                return singularFromFieldSetType(value);
            }
            if (this.descriptor.getLiteJavaType() != WireFormat.JavaType.ENUM) {
                return value;
            }
            List result = new ArrayList();
            for (Object element : (List) value) {
                result.add(singularFromFieldSetType(element));
            }
            return result;
        }

        /* access modifiers changed from: package-private */
        public Object singularFromFieldSetType(Object value) {
            if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
                return this.descriptor.enumTypeMap.findValueByNumber(((Integer) value).intValue());
            }
            return value;
        }

        /* access modifiers changed from: package-private */
        public Object toFieldSetType(Object value) {
            if (!this.descriptor.isRepeated()) {
                return singularToFieldSetType(value);
            }
            if (this.descriptor.getLiteJavaType() != WireFormat.JavaType.ENUM) {
                return value;
            }
            List result = new ArrayList();
            for (Object element : (List) value) {
                result.add(singularToFieldSetType(element));
            }
            return result;
        }

        /* access modifiers changed from: package-private */
        public Object singularToFieldSetType(Object value) {
            if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
                return Integer.valueOf(((Internal.EnumLite) value).getNumber());
            }
            return value;
        }

        public WireFormat.FieldType getLiteType() {
            return this.descriptor.getLiteType();
        }

        public boolean isRepeated() {
            return this.descriptor.isRepeated;
        }

        public Type getDefaultValue() {
            return this.defaultValue;
        }
    }

    protected static final class SerializedForm implements Serializable {
        private static final long serialVersionUID = 0;
        private final byte[] asBytes;
        private final String messageClassName;

        public static SerializedForm of(MessageLite message) {
            return new SerializedForm(message);
        }

        SerializedForm(MessageLite regularForm) {
            this.messageClassName = regularForm.getClass().getName();
            this.asBytes = regularForm.toByteArray();
        }

        /* access modifiers changed from: protected */
        public Object readResolve() throws ObjectStreamException {
            try {
                Field defaultInstanceField = Class.forName(this.messageClassName).getDeclaredField("DEFAULT_INSTANCE");
                defaultInstanceField.setAccessible(true);
                return ((MessageLite) defaultInstanceField.get((Object) null)).newBuilderForType().mergeFrom(this.asBytes).buildPartial();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, e);
            } catch (NoSuchFieldException e2) {
                throw new RuntimeException("Unable to find DEFAULT_INSTANCE in " + this.messageClassName, e2);
            } catch (SecurityException e3) {
                throw new RuntimeException("Unable to call DEFAULT_INSTANCE in " + this.messageClassName, e3);
            } catch (IllegalAccessException e4) {
                throw new RuntimeException("Unable to call parsePartialFrom", e4);
            } catch (InvalidProtocolBufferException e5) {
                throw new RuntimeException("Unable to understand proto buffer", e5);
            }
        }
    }

    /* access modifiers changed from: private */
    public static <MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>, T> GeneratedExtension<MessageType, T> checkIsLite(ExtensionLite<MessageType, T> extension) {
        if (extension.isLite()) {
            return (GeneratedExtension) extension;
        }
        throw new IllegalArgumentException("Expected a lite extension.");
    }

    protected static final <T extends GeneratedMessageLite<T, ?>> boolean isInitialized(T message, boolean shouldMemoize) {
        return message.dynamicMethod(MethodToInvoke.IS_INITIALIZED, Boolean.valueOf(shouldMemoize)) != null;
    }

    protected static final <T extends GeneratedMessageLite<T, ?>> void makeImmutable(T message) {
        message.dynamicMethod(MethodToInvoke.MAKE_IMMUTABLE);
    }

    protected static Internal.IntList emptyIntList() {
        return IntArrayList.emptyList();
    }

    protected static Internal.IntList mutableCopy(Internal.IntList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.LongList emptyLongList() {
        return LongArrayList.emptyList();
    }

    protected static Internal.LongList mutableCopy(Internal.LongList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.FloatList emptyFloatList() {
        return FloatArrayList.emptyList();
    }

    protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.DoubleList emptyDoubleList() {
        return DoubleArrayList.emptyList();
    }

    protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.BooleanList emptyBooleanList() {
        return BooleanArrayList.emptyList();
    }

    protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static <E> Internal.ProtobufList<E> emptyProtobufList() {
        return ProtobufArrayList.emptyList();
    }

    protected static <E> Internal.ProtobufList<E> mutableCopy(Internal.ProtobufList<E> list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static class DefaultInstanceBasedParser<T extends GeneratedMessageLite<T, ?>> extends AbstractParser<T> {
        private T defaultInstance;

        public DefaultInstanceBasedParser(T defaultInstance2) {
            this.defaultInstance = defaultInstance2;
        }

        public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, extensionRegistry);
        }
    }

    static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        T result = (GeneratedMessageLite) instance.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
        try {
            result.dynamicMethod(MethodToInvoke.MERGE_FROM_STREAM, input, extensionRegistry);
            result.makeImmutable();
            return result;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof InvalidProtocolBufferException) {
                throw ((InvalidProtocolBufferException) e.getCause());
            }
            throw e;
        }
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
        return parsePartialFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
    }

    private static <T extends GeneratedMessageLite<T, ?>> T checkMessageInitialized(T message) throws InvalidProtocolBufferException {
        if (message == null || message.isInitialized()) {
            return message;
        }
        throw message.newUninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(message);
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(defaultInstance, data, extensionRegistry));
    }

    private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        T message;
        try {
            CodedInputStream input = data.newCodedInput();
            message = parsePartialFrom(defaultInstance, input, extensionRegistry);
            input.checkLastTagWas(0);
            return message;
        } catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(message);
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        T message;
        try {
            CodedInputStream input = CodedInputStream.newInstance(data);
            message = parsePartialFrom(defaultInstance, input, extensionRegistry);
            input.checkLastTagWas(0);
            return message;
        } catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(message);
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(defaultInstance, data, extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
        return parseFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(defaultInstance, input, extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialDelimitedFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialDelimitedFrom(defaultInstance, input, extensionRegistry));
    }

    private static <T extends GeneratedMessageLite<T, ?>> T parsePartialDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            int firstByte = input.read();
            if (firstByte == -1) {
                return null;
            }
            CodedInputStream codedInput = CodedInputStream.newInstance(new AbstractMessageLite.Builder.LimitedInputStream(input, CodedInputStream.readRawVarint32(firstByte, input)));
            T message = parsePartialFrom(defaultInstance, codedInput, extensionRegistry);
            try {
                codedInput.checkLastTagWas(0);
                return message;
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(message);
            }
        } catch (IOException e2) {
            throw new InvalidProtocolBufferException(e2.getMessage());
        }
    }

    static class EqualsVisitor implements Visitor {
        static final EqualsVisitor INSTANCE = new EqualsVisitor();
        static final NotEqualsException NOT_EQUALS = new NotEqualsException();

        static final class NotEqualsException extends RuntimeException {
            NotEqualsException() {
            }
        }

        private EqualsVisitor() {
        }

        public boolean visitBoolean(boolean minePresent, boolean mine, boolean otherPresent, boolean other) {
            if (minePresent == otherPresent && mine == other) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public int visitInt(boolean minePresent, int mine, boolean otherPresent, int other) {
            if (minePresent == otherPresent && mine == other) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public double visitDouble(boolean minePresent, double mine, boolean otherPresent, double other) {
            if (minePresent == otherPresent && mine == other) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public float visitFloat(boolean minePresent, float mine, boolean otherPresent, float other) {
            if (minePresent == otherPresent && mine == other) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public long visitLong(boolean minePresent, long mine, boolean otherPresent, long other) {
            if (minePresent == otherPresent && mine == other) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public String visitString(boolean minePresent, String mine, boolean otherPresent, String other) {
            if (minePresent == otherPresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public ByteString visitByteString(boolean minePresent, ByteString mine, boolean otherPresent, ByteString other) {
            if (minePresent == otherPresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofBoolean(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofInt(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofDouble(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofFloat(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofLong(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofString(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofByteString(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofLazyMessage(boolean minePresent, Object mine, Object other) {
            if (minePresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Object visitOneofMessage(boolean minePresent, Object mine, Object other) {
            if (minePresent && ((GeneratedMessageLite) mine).equals(this, (MessageLite) other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public void visitOneofNotSet(boolean minePresent) {
            if (minePresent) {
                throw NOT_EQUALS;
            }
        }

        public <T extends MessageLite> T visitMessage(T mine, T other) {
            if (mine == null && other == null) {
                return null;
            }
            if (mine == null || other == null) {
                throw NOT_EQUALS;
            }
            ((GeneratedMessageLite) mine).equals(this, other);
            return mine;
        }

        public LazyFieldLite visitLazyMessage(boolean minePresent, LazyFieldLite mine, boolean otherPresent, LazyFieldLite other) {
            if (!minePresent && !otherPresent) {
                return mine;
            }
            if (minePresent && otherPresent && mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public <T> Internal.ProtobufList<T> visitList(Internal.ProtobufList<T> mine, Internal.ProtobufList<T> other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Internal.BooleanList visitBooleanList(Internal.BooleanList mine, Internal.BooleanList other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Internal.IntList visitIntList(Internal.IntList mine, Internal.IntList other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Internal.DoubleList visitDoubleList(Internal.DoubleList mine, Internal.DoubleList other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Internal.FloatList visitFloatList(Internal.FloatList mine, Internal.FloatList other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public Internal.LongList visitLongList(Internal.LongList mine, Internal.LongList other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public FieldSet<ExtensionDescriptor> visitExtensions(FieldSet<ExtensionDescriptor> mine, FieldSet<ExtensionDescriptor> other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public UnknownFieldSetLite visitUnknownFields(UnknownFieldSetLite mine, UnknownFieldSetLite other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }

        public <K, V> MapFieldLite<K, V> visitMap(MapFieldLite<K, V> mine, MapFieldLite<K, V> other) {
            if (mine.equals(other)) {
                return mine;
            }
            throw NOT_EQUALS;
        }
    }

    private static class HashCodeVisitor implements Visitor {
        /* access modifiers changed from: private */
        public int hashCode;

        private HashCodeVisitor() {
            this.hashCode = 0;
        }

        /* synthetic */ HashCodeVisitor(AnonymousClass1 x0) {
            this();
        }

        public boolean visitBoolean(boolean minePresent, boolean mine, boolean otherPresent, boolean other) {
            this.hashCode = (this.hashCode * 53) + Internal.hashBoolean(mine);
            return mine;
        }

        public int visitInt(boolean minePresent, int mine, boolean otherPresent, int other) {
            this.hashCode = (this.hashCode * 53) + mine;
            return mine;
        }

        public double visitDouble(boolean minePresent, double mine, boolean otherPresent, double other) {
            this.hashCode = (this.hashCode * 53) + Internal.hashLong(Double.doubleToLongBits(mine));
            return mine;
        }

        public float visitFloat(boolean minePresent, float mine, boolean otherPresent, float other) {
            this.hashCode = (this.hashCode * 53) + Float.floatToIntBits(mine);
            return mine;
        }

        public long visitLong(boolean minePresent, long mine, boolean otherPresent, long other) {
            this.hashCode = (this.hashCode * 53) + Internal.hashLong(mine);
            return mine;
        }

        public String visitString(boolean minePresent, String mine, boolean otherPresent, String other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public ByteString visitByteString(boolean minePresent, ByteString mine, boolean otherPresent, ByteString other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Object visitOneofBoolean(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + Internal.hashBoolean(((Boolean) mine).booleanValue());
            return mine;
        }

        public Object visitOneofInt(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + ((Integer) mine).intValue();
            return mine;
        }

        public Object visitOneofDouble(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + Internal.hashLong(Double.doubleToLongBits(((Double) mine).doubleValue()));
            return mine;
        }

        public Object visitOneofFloat(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + Float.floatToIntBits(((Float) mine).floatValue());
            return mine;
        }

        public Object visitOneofLong(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + Internal.hashLong(((Long) mine).longValue());
            return mine;
        }

        public Object visitOneofString(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Object visitOneofByteString(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Object visitOneofLazyMessage(boolean minePresent, Object mine, Object other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Object visitOneofMessage(boolean minePresent, Object mine, Object other) {
            return visitMessage((MessageLite) mine, (MessageLite) other);
        }

        public void visitOneofNotSet(boolean minePresent) {
            if (minePresent) {
                throw new IllegalStateException();
            }
        }

        public <T extends MessageLite> T visitMessage(T mine, T t) {
            int protoHash;
            if (mine == null) {
                protoHash = 37;
            } else if (mine instanceof GeneratedMessageLite) {
                protoHash = ((GeneratedMessageLite) mine).hashCode(this);
            } else {
                protoHash = mine.hashCode();
            }
            this.hashCode = (this.hashCode * 53) + protoHash;
            return mine;
        }

        public LazyFieldLite visitLazyMessage(boolean minePresent, LazyFieldLite mine, boolean otherPresent, LazyFieldLite other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public <T> Internal.ProtobufList<T> visitList(Internal.ProtobufList<T> mine, Internal.ProtobufList<T> protobufList) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Internal.BooleanList visitBooleanList(Internal.BooleanList mine, Internal.BooleanList other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Internal.IntList visitIntList(Internal.IntList mine, Internal.IntList other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Internal.DoubleList visitDoubleList(Internal.DoubleList mine, Internal.DoubleList other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Internal.FloatList visitFloatList(Internal.FloatList mine, Internal.FloatList other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public Internal.LongList visitLongList(Internal.LongList mine, Internal.LongList other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public FieldSet<ExtensionDescriptor> visitExtensions(FieldSet<ExtensionDescriptor> mine, FieldSet<ExtensionDescriptor> fieldSet) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public UnknownFieldSetLite visitUnknownFields(UnknownFieldSetLite mine, UnknownFieldSetLite other) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }

        public <K, V> MapFieldLite<K, V> visitMap(MapFieldLite<K, V> mine, MapFieldLite<K, V> mapFieldLite) {
            this.hashCode = (this.hashCode * 53) + mine.hashCode();
            return mine;
        }
    }

    protected static class MergeFromVisitor implements Visitor {
        public static final MergeFromVisitor INSTANCE = new MergeFromVisitor();

        private MergeFromVisitor() {
        }

        public boolean visitBoolean(boolean minePresent, boolean mine, boolean otherPresent, boolean other) {
            return otherPresent ? other : mine;
        }

        public int visitInt(boolean minePresent, int mine, boolean otherPresent, int other) {
            return otherPresent ? other : mine;
        }

        public double visitDouble(boolean minePresent, double mine, boolean otherPresent, double other) {
            return otherPresent ? other : mine;
        }

        public float visitFloat(boolean minePresent, float mine, boolean otherPresent, float other) {
            return otherPresent ? other : mine;
        }

        public long visitLong(boolean minePresent, long mine, boolean otherPresent, long other) {
            return otherPresent ? other : mine;
        }

        public String visitString(boolean minePresent, String mine, boolean otherPresent, String other) {
            return otherPresent ? other : mine;
        }

        public ByteString visitByteString(boolean minePresent, ByteString mine, boolean otherPresent, ByteString other) {
            return otherPresent ? other : mine;
        }

        public Object visitOneofBoolean(boolean minePresent, Object mine, Object other) {
            return other;
        }

        public Object visitOneofInt(boolean minePresent, Object mine, Object other) {
            return other;
        }

        public Object visitOneofDouble(boolean minePresent, Object mine, Object other) {
            return other;
        }

        public Object visitOneofFloat(boolean minePresent, Object mine, Object other) {
            return other;
        }

        public Object visitOneofLong(boolean minePresent, Object mine, Object other) {
            return other;
        }

        public Object visitOneofString(boolean minePresent, Object mine, Object other) {
            return other;
        }

        public Object visitOneofByteString(boolean minePresent, Object mine, Object other) {
            return other;
        }

        public Object visitOneofLazyMessage(boolean minePresent, Object mine, Object other) {
            if (!minePresent) {
                return other;
            }
            LazyFieldLite lazy = (LazyFieldLite) mine;
            lazy.merge((LazyFieldLite) other);
            return lazy;
        }

        public Object visitOneofMessage(boolean minePresent, Object mine, Object other) {
            if (minePresent) {
                return visitMessage((MessageLite) mine, (MessageLite) other);
            }
            return other;
        }

        public void visitOneofNotSet(boolean minePresent) {
        }

        public <T extends MessageLite> T visitMessage(T mine, T other) {
            if (mine == null || other == null) {
                return mine != null ? mine : other;
            }
            return mine.toBuilder().mergeFrom((MessageLite) other).build();
        }

        public LazyFieldLite visitLazyMessage(boolean minePresent, LazyFieldLite mine, boolean otherPresent, LazyFieldLite other) {
            mine.merge(other);
            return mine;
        }

        public <T> Internal.ProtobufList<T> visitList(Internal.ProtobufList<T> mine, Internal.ProtobufList<T> other) {
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                if (!mine.isModifiable()) {
                    mine = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine.addAll(other);
            }
            return size > 0 ? mine : other;
        }

        public Internal.BooleanList visitBooleanList(Internal.BooleanList mine, Internal.BooleanList other) {
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                if (!mine.isModifiable()) {
                    mine = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine.addAll(other);
            }
            return size > 0 ? mine : other;
        }

        public Internal.IntList visitIntList(Internal.IntList mine, Internal.IntList other) {
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                if (!mine.isModifiable()) {
                    mine = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine.addAll(other);
            }
            return size > 0 ? mine : other;
        }

        public Internal.DoubleList visitDoubleList(Internal.DoubleList mine, Internal.DoubleList other) {
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                if (!mine.isModifiable()) {
                    mine = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine.addAll(other);
            }
            return size > 0 ? mine : other;
        }

        public Internal.FloatList visitFloatList(Internal.FloatList mine, Internal.FloatList other) {
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                if (!mine.isModifiable()) {
                    mine = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine.addAll(other);
            }
            return size > 0 ? mine : other;
        }

        public Internal.LongList visitLongList(Internal.LongList mine, Internal.LongList other) {
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                if (!mine.isModifiable()) {
                    mine = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine.addAll(other);
            }
            return size > 0 ? mine : other;
        }

        public FieldSet<ExtensionDescriptor> visitExtensions(FieldSet<ExtensionDescriptor> mine, FieldSet<ExtensionDescriptor> other) {
            if (mine.isImmutable()) {
                mine = mine.clone();
            }
            mine.mergeFrom(other);
            return mine;
        }

        public UnknownFieldSetLite visitUnknownFields(UnknownFieldSetLite mine, UnknownFieldSetLite other) {
            return other == UnknownFieldSetLite.getDefaultInstance() ? mine : UnknownFieldSetLite.mutableCopyOf(mine, other);
        }

        public <K, V> MapFieldLite<K, V> visitMap(MapFieldLite<K, V> mine, MapFieldLite<K, V> other) {
            mine.mergeFrom(other);
            return mine;
        }
    }
}
