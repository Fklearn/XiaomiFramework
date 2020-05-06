package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DescriptorProtos {
    /* access modifiers changed from: private */
    public static Descriptors.FileDescriptor descriptor;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_DescriptorProto_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_DescriptorProto_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_EnumDescriptorProto_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_EnumOptions_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumOptions_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_EnumValueOptions_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_FieldDescriptorProto_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_FieldOptions_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FieldOptions_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_FileDescriptorProto_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_FileDescriptorSet_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_FileOptions_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FileOptions_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_MessageOptions_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_MessageOptions_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_MethodDescriptorProto_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_MethodOptions_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_MethodOptions_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_ServiceOptions_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_ServiceOptions_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_SourceCodeInfo_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable;
    /* access modifiers changed from: private */
    public static Descriptors.Descriptor internal_static_google_protobuf_UninterpretedOption_descriptor;
    /* access modifiers changed from: private */
    public static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable;

    public interface DescriptorProtoOrBuilder extends MessageOrBuilder {
        EnumDescriptorProto getEnumType(int i);

        int getEnumTypeCount();

        List<EnumDescriptorProto> getEnumTypeList();

        EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int i);

        List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList();

        FieldDescriptorProto getExtension(int i);

        int getExtensionCount();

        List<FieldDescriptorProto> getExtensionList();

        FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int i);

        List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList();

        DescriptorProto.ExtensionRange getExtensionRange(int i);

        int getExtensionRangeCount();

        List<DescriptorProto.ExtensionRange> getExtensionRangeList();

        DescriptorProto.ExtensionRangeOrBuilder getExtensionRangeOrBuilder(int i);

        List<? extends DescriptorProto.ExtensionRangeOrBuilder> getExtensionRangeOrBuilderList();

        FieldDescriptorProto getField(int i);

        int getFieldCount();

        List<FieldDescriptorProto> getFieldList();

        FieldDescriptorProtoOrBuilder getFieldOrBuilder(int i);

        List<? extends FieldDescriptorProtoOrBuilder> getFieldOrBuilderList();

        String getName();

        ByteString getNameBytes();

        DescriptorProto getNestedType(int i);

        int getNestedTypeCount();

        List<DescriptorProto> getNestedTypeList();

        DescriptorProtoOrBuilder getNestedTypeOrBuilder(int i);

        List<? extends DescriptorProtoOrBuilder> getNestedTypeOrBuilderList();

        MessageOptions getOptions();

        MessageOptionsOrBuilder getOptionsOrBuilder();

        boolean hasName();

        boolean hasOptions();
    }

    public interface EnumDescriptorProtoOrBuilder extends MessageOrBuilder {
        String getName();

        ByteString getNameBytes();

        EnumOptions getOptions();

        EnumOptionsOrBuilder getOptionsOrBuilder();

        EnumValueDescriptorProto getValue(int i);

        int getValueCount();

        List<EnumValueDescriptorProto> getValueList();

        EnumValueDescriptorProtoOrBuilder getValueOrBuilder(int i);

        List<? extends EnumValueDescriptorProtoOrBuilder> getValueOrBuilderList();

        boolean hasName();

        boolean hasOptions();
    }

    public interface EnumOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<EnumOptions> {
        boolean getAllowAlias();

        UninterpretedOption getUninterpretedOption(int i);

        int getUninterpretedOptionCount();

        List<UninterpretedOption> getUninterpretedOptionList();

        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int i);

        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();

        boolean hasAllowAlias();
    }

    public interface EnumValueDescriptorProtoOrBuilder extends MessageOrBuilder {
        String getName();

        ByteString getNameBytes();

        int getNumber();

        EnumValueOptions getOptions();

        EnumValueOptionsOrBuilder getOptionsOrBuilder();

        boolean hasName();

        boolean hasNumber();

        boolean hasOptions();
    }

    public interface EnumValueOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<EnumValueOptions> {
        UninterpretedOption getUninterpretedOption(int i);

        int getUninterpretedOptionCount();

        List<UninterpretedOption> getUninterpretedOptionList();

        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int i);

        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
    }

    public interface FieldDescriptorProtoOrBuilder extends MessageOrBuilder {
        String getDefaultValue();

        ByteString getDefaultValueBytes();

        String getExtendee();

        ByteString getExtendeeBytes();

        FieldDescriptorProto.Label getLabel();

        String getName();

        ByteString getNameBytes();

        int getNumber();

        FieldOptions getOptions();

        FieldOptionsOrBuilder getOptionsOrBuilder();

        FieldDescriptorProto.Type getType();

        String getTypeName();

        ByteString getTypeNameBytes();

        boolean hasDefaultValue();

        boolean hasExtendee();

        boolean hasLabel();

        boolean hasName();

        boolean hasNumber();

        boolean hasOptions();

        boolean hasType();

        boolean hasTypeName();
    }

    public interface FieldOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<FieldOptions> {
        FieldOptions.CType getCtype();

        boolean getDeprecated();

        String getExperimentalMapKey();

        ByteString getExperimentalMapKeyBytes();

        boolean getLazy();

        boolean getPacked();

        UninterpretedOption getUninterpretedOption(int i);

        int getUninterpretedOptionCount();

        List<UninterpretedOption> getUninterpretedOptionList();

        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int i);

        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();

        boolean getWeak();

        boolean hasCtype();

        boolean hasDeprecated();

        boolean hasExperimentalMapKey();

        boolean hasLazy();

        boolean hasPacked();

        boolean hasWeak();
    }

    public interface FileDescriptorProtoOrBuilder extends MessageOrBuilder {
        String getDependency(int i);

        ByteString getDependencyBytes(int i);

        int getDependencyCount();

        List<String> getDependencyList();

        EnumDescriptorProto getEnumType(int i);

        int getEnumTypeCount();

        List<EnumDescriptorProto> getEnumTypeList();

        EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int i);

        List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList();

        FieldDescriptorProto getExtension(int i);

        int getExtensionCount();

        List<FieldDescriptorProto> getExtensionList();

        FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int i);

        List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList();

        DescriptorProto getMessageType(int i);

        int getMessageTypeCount();

        List<DescriptorProto> getMessageTypeList();

        DescriptorProtoOrBuilder getMessageTypeOrBuilder(int i);

        List<? extends DescriptorProtoOrBuilder> getMessageTypeOrBuilderList();

        String getName();

        ByteString getNameBytes();

        FileOptions getOptions();

        FileOptionsOrBuilder getOptionsOrBuilder();

        String getPackage();

        ByteString getPackageBytes();

        int getPublicDependency(int i);

        int getPublicDependencyCount();

        List<Integer> getPublicDependencyList();

        ServiceDescriptorProto getService(int i);

        int getServiceCount();

        List<ServiceDescriptorProto> getServiceList();

        ServiceDescriptorProtoOrBuilder getServiceOrBuilder(int i);

        List<? extends ServiceDescriptorProtoOrBuilder> getServiceOrBuilderList();

        SourceCodeInfo getSourceCodeInfo();

        SourceCodeInfoOrBuilder getSourceCodeInfoOrBuilder();

        int getWeakDependency(int i);

        int getWeakDependencyCount();

        List<Integer> getWeakDependencyList();

        boolean hasName();

        boolean hasOptions();

        boolean hasPackage();

        boolean hasSourceCodeInfo();
    }

    public interface FileDescriptorSetOrBuilder extends MessageOrBuilder {
        FileDescriptorProto getFile(int i);

        int getFileCount();

        List<FileDescriptorProto> getFileList();

        FileDescriptorProtoOrBuilder getFileOrBuilder(int i);

        List<? extends FileDescriptorProtoOrBuilder> getFileOrBuilderList();
    }

    public interface FileOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<FileOptions> {
        boolean getCcGenericServices();

        String getGoPackage();

        ByteString getGoPackageBytes();

        boolean getJavaGenerateEqualsAndHash();

        boolean getJavaGenericServices();

        boolean getJavaMultipleFiles();

        String getJavaOuterClassname();

        ByteString getJavaOuterClassnameBytes();

        String getJavaPackage();

        ByteString getJavaPackageBytes();

        FileOptions.OptimizeMode getOptimizeFor();

        boolean getPyGenericServices();

        UninterpretedOption getUninterpretedOption(int i);

        int getUninterpretedOptionCount();

        List<UninterpretedOption> getUninterpretedOptionList();

        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int i);

        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();

        boolean hasCcGenericServices();

        boolean hasGoPackage();

        boolean hasJavaGenerateEqualsAndHash();

        boolean hasJavaGenericServices();

        boolean hasJavaMultipleFiles();

        boolean hasJavaOuterClassname();

        boolean hasJavaPackage();

        boolean hasOptimizeFor();

        boolean hasPyGenericServices();
    }

    public interface MessageOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<MessageOptions> {
        boolean getMessageSetWireFormat();

        boolean getNoStandardDescriptorAccessor();

        UninterpretedOption getUninterpretedOption(int i);

        int getUninterpretedOptionCount();

        List<UninterpretedOption> getUninterpretedOptionList();

        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int i);

        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();

        boolean hasMessageSetWireFormat();

        boolean hasNoStandardDescriptorAccessor();
    }

    public interface MethodDescriptorProtoOrBuilder extends MessageOrBuilder {
        String getInputType();

        ByteString getInputTypeBytes();

        String getName();

        ByteString getNameBytes();

        MethodOptions getOptions();

        MethodOptionsOrBuilder getOptionsOrBuilder();

        String getOutputType();

        ByteString getOutputTypeBytes();

        boolean hasInputType();

        boolean hasName();

        boolean hasOptions();

        boolean hasOutputType();
    }

    public interface MethodOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<MethodOptions> {
        UninterpretedOption getUninterpretedOption(int i);

        int getUninterpretedOptionCount();

        List<UninterpretedOption> getUninterpretedOptionList();

        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int i);

        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
    }

    public interface ServiceDescriptorProtoOrBuilder extends MessageOrBuilder {
        MethodDescriptorProto getMethod(int i);

        int getMethodCount();

        List<MethodDescriptorProto> getMethodList();

        MethodDescriptorProtoOrBuilder getMethodOrBuilder(int i);

        List<? extends MethodDescriptorProtoOrBuilder> getMethodOrBuilderList();

        String getName();

        ByteString getNameBytes();

        ServiceOptions getOptions();

        ServiceOptionsOrBuilder getOptionsOrBuilder();

        boolean hasName();

        boolean hasOptions();
    }

    public interface ServiceOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<ServiceOptions> {
        UninterpretedOption getUninterpretedOption(int i);

        int getUninterpretedOptionCount();

        List<UninterpretedOption> getUninterpretedOptionList();

        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int i);

        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
    }

    public interface SourceCodeInfoOrBuilder extends MessageOrBuilder {
        SourceCodeInfo.Location getLocation(int i);

        int getLocationCount();

        List<SourceCodeInfo.Location> getLocationList();

        SourceCodeInfo.LocationOrBuilder getLocationOrBuilder(int i);

        List<? extends SourceCodeInfo.LocationOrBuilder> getLocationOrBuilderList();
    }

    public interface UninterpretedOptionOrBuilder extends MessageOrBuilder {
        String getAggregateValue();

        ByteString getAggregateValueBytes();

        double getDoubleValue();

        String getIdentifierValue();

        ByteString getIdentifierValueBytes();

        UninterpretedOption.NamePart getName(int i);

        int getNameCount();

        List<UninterpretedOption.NamePart> getNameList();

        UninterpretedOption.NamePartOrBuilder getNameOrBuilder(int i);

        List<? extends UninterpretedOption.NamePartOrBuilder> getNameOrBuilderList();

        long getNegativeIntValue();

        long getPositiveIntValue();

        ByteString getStringValue();

        boolean hasAggregateValue();

        boolean hasDoubleValue();

        boolean hasIdentifierValue();

        boolean hasNegativeIntValue();

        boolean hasPositiveIntValue();

        boolean hasStringValue();
    }

    private DescriptorProtos() {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
    }

    public static final class FileDescriptorSet extends GeneratedMessage implements FileDescriptorSetOrBuilder {
        public static final int FILE_FIELD_NUMBER = 1;
        public static Parser<FileDescriptorSet> PARSER = new AbstractParser<FileDescriptorSet>() {
            public FileDescriptorSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new FileDescriptorSet(input, extensionRegistry);
            }
        };
        private static final FileDescriptorSet defaultInstance = new FileDescriptorSet(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public List<FileDescriptorProto> file_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private final UnknownFieldSet unknownFields;

        private FileDescriptorSet(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private FileDescriptorSet(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static FileDescriptorSet getDefaultInstance() {
            return defaultInstance;
        }

        public FileDescriptorSet getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private FileDescriptorSet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        if ((mutable_bitField0_ & 1) != 1) {
                            this.file_ = new ArrayList();
                            mutable_bitField0_ |= 1;
                        }
                        this.file_.add(input.readMessage(FileDescriptorProto.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 1) == 1) {
                        this.file_ = Collections.unmodifiableList(this.file_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 1) == 1) {
                this.file_ = Collections.unmodifiableList(this.file_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorSet.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<FileDescriptorSet> getParserForType() {
            return PARSER;
        }

        public List<FileDescriptorProto> getFileList() {
            return this.file_;
        }

        public List<? extends FileDescriptorProtoOrBuilder> getFileOrBuilderList() {
            return this.file_;
        }

        public int getFileCount() {
            return this.file_.size();
        }

        public FileDescriptorProto getFile(int index) {
            return this.file_.get(index);
        }

        public FileDescriptorProtoOrBuilder getFileOrBuilder(int index) {
            return this.file_.get(index);
        }

        private void initFields() {
            this.file_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getFileCount(); i++) {
                if (!getFile(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            for (int i = 0; i < this.file_.size(); i++) {
                output.writeMessage(1, this.file_.get(i));
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.file_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(1, this.file_.get(i));
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static FileDescriptorSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FileDescriptorSet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FileDescriptorSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FileDescriptorSet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FileDescriptorSet parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FileDescriptorSet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static FileDescriptorSet parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static FileDescriptorSet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static FileDescriptorSet parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FileDescriptorSet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(FileDescriptorSet prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FileDescriptorSetOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> fileBuilder_;
            private List<FileDescriptorProto> file_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorSet.class, Builder.class);
            }

            private Builder() {
                this.file_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.file_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getFileFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.file_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor;
            }

            public FileDescriptorSet getDefaultInstanceForType() {
                return FileDescriptorSet.getDefaultInstance();
            }

            public FileDescriptorSet build() {
                FileDescriptorSet result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public FileDescriptorSet buildPartial() {
                FileDescriptorSet result = new FileDescriptorSet((GeneratedMessage.Builder) this);
                int i = this.bitField0_;
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 1) == 1) {
                        this.file_ = Collections.unmodifiableList(this.file_);
                        this.bitField0_ &= -2;
                    }
                    List unused = result.file_ = this.file_;
                } else {
                    List unused2 = result.file_ = repeatedFieldBuilder.build();
                }
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof FileDescriptorSet) {
                    return mergeFrom((FileDescriptorSet) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(FileDescriptorSet other) {
                if (other == FileDescriptorSet.getDefaultInstance()) {
                    return this;
                }
                if (this.fileBuilder_ == null) {
                    if (!other.file_.isEmpty()) {
                        if (this.file_.isEmpty()) {
                            this.file_ = other.file_;
                            this.bitField0_ &= -2;
                        } else {
                            ensureFileIsMutable();
                            this.file_.addAll(other.file_);
                        }
                        onChanged();
                    }
                } else if (!other.file_.isEmpty()) {
                    if (this.fileBuilder_.isEmpty()) {
                        this.fileBuilder_.dispose();
                        RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = null;
                        this.fileBuilder_ = null;
                        this.file_ = other.file_;
                        this.bitField0_ &= -2;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getFileFieldBuilder();
                        }
                        this.fileBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.fileBuilder_.addAllMessages(other.file_);
                    }
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getFileCount(); i++) {
                    if (!getFile(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    FileDescriptorSet parsedMessage = FileDescriptorSet.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    FileDescriptorSet parsedMessage2 = (FileDescriptorSet) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((FileDescriptorSet) null);
                    }
                    throw th;
                }
            }

            private void ensureFileIsMutable() {
                if ((this.bitField0_ & 1) != 1) {
                    this.file_ = new ArrayList(this.file_);
                    this.bitField0_ |= 1;
                }
            }

            public List<FileDescriptorProto> getFileList() {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.file_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getFileCount() {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.file_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public FileDescriptorProto getFile(int index) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.file_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setFile(int index, FileDescriptorProto value) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureFileIsMutable();
                    this.file_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setFile(int index, FileDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFileIsMutable();
                    this.file_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addFile(FileDescriptorProto value) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureFileIsMutable();
                    this.file_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addFile(int index, FileDescriptorProto value) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureFileIsMutable();
                    this.file_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addFile(FileDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFileIsMutable();
                    this.file_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addFile(int index, FileDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFileIsMutable();
                    this.file_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllFile(Iterable<? extends FileDescriptorProto> values) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFileIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.file_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearFile() {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.file_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeFile(int index) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFileIsMutable();
                    this.file_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public FileDescriptorProto.Builder getFileBuilder(int index) {
                return getFileFieldBuilder().getBuilder(index);
            }

            public FileDescriptorProtoOrBuilder getFileOrBuilder(int index) {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.file_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends FileDescriptorProtoOrBuilder> getFileOrBuilderList() {
                RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fileBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.file_);
            }

            public FileDescriptorProto.Builder addFileBuilder() {
                return getFileFieldBuilder().addBuilder(FileDescriptorProto.getDefaultInstance());
            }

            public FileDescriptorProto.Builder addFileBuilder(int index) {
                return getFileFieldBuilder().addBuilder(index, FileDescriptorProto.getDefaultInstance());
            }

            public List<FileDescriptorProto.Builder> getFileBuilderList() {
                return getFileFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> getFileFieldBuilder() {
                if (this.fileBuilder_ == null) {
                    List<FileDescriptorProto> list = this.file_;
                    boolean z = true;
                    if ((this.bitField0_ & 1) != 1) {
                        z = false;
                    }
                    this.fileBuilder_ = new RepeatedFieldBuilder<>(list, z, getParentForChildren(), isClean());
                    this.file_ = null;
                }
                return this.fileBuilder_;
            }
        }
    }

    public static final class FileDescriptorProto extends GeneratedMessage implements FileDescriptorProtoOrBuilder {
        public static final int DEPENDENCY_FIELD_NUMBER = 3;
        public static final int ENUM_TYPE_FIELD_NUMBER = 5;
        public static final int EXTENSION_FIELD_NUMBER = 7;
        public static final int MESSAGE_TYPE_FIELD_NUMBER = 4;
        public static final int NAME_FIELD_NUMBER = 1;
        public static final int OPTIONS_FIELD_NUMBER = 8;
        public static final int PACKAGE_FIELD_NUMBER = 2;
        public static Parser<FileDescriptorProto> PARSER = new AbstractParser<FileDescriptorProto>() {
            public FileDescriptorProto parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new FileDescriptorProto(input, extensionRegistry);
            }
        };
        public static final int PUBLIC_DEPENDENCY_FIELD_NUMBER = 10;
        public static final int SERVICE_FIELD_NUMBER = 6;
        public static final int SOURCE_CODE_INFO_FIELD_NUMBER = 9;
        public static final int WEAK_DEPENDENCY_FIELD_NUMBER = 11;
        private static final FileDescriptorProto defaultInstance = new FileDescriptorProto(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        /* access modifiers changed from: private */
        public LazyStringList dependency_;
        /* access modifiers changed from: private */
        public List<EnumDescriptorProto> enumType_;
        /* access modifiers changed from: private */
        public List<FieldDescriptorProto> extension_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public List<DescriptorProto> messageType_;
        /* access modifiers changed from: private */
        public Object name_;
        /* access modifiers changed from: private */
        public FileOptions options_;
        /* access modifiers changed from: private */
        public Object package_;
        /* access modifiers changed from: private */
        public List<Integer> publicDependency_;
        /* access modifiers changed from: private */
        public List<ServiceDescriptorProto> service_;
        /* access modifiers changed from: private */
        public SourceCodeInfo sourceCodeInfo_;
        private final UnknownFieldSet unknownFields;
        /* access modifiers changed from: private */
        public List<Integer> weakDependency_;

        private FileDescriptorProto(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private FileDescriptorProto(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static FileDescriptorProto getDefaultInstance() {
            return defaultInstance;
        }

        public FileDescriptorProto getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 17 */
        private FileDescriptorProto(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            CodedInputStream codedInputStream = input;
            ExtensionRegistryLite extensionRegistryLite = extensionRegistry;
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            int mutable_bitField0_ = 0;
            while (!done) {
                try {
                    int tag = input.readTag();
                    switch (tag) {
                        case Utf8.COMPLETE /*0*/:
                            done = true;
                            break;
                        case 10:
                            this.bitField0_ |= 1;
                            this.name_ = input.readBytes();
                            break;
                        case 18:
                            this.bitField0_ |= 2;
                            this.package_ = input.readBytes();
                            break;
                        case 26:
                            if ((mutable_bitField0_ & 4) != 4) {
                                this.dependency_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 4;
                            }
                            this.dependency_.add(input.readBytes());
                            break;
                        case 34:
                            if ((mutable_bitField0_ & 32) != 32) {
                                this.messageType_ = new ArrayList();
                                mutable_bitField0_ |= 32;
                            }
                            this.messageType_.add(codedInputStream.readMessage(DescriptorProto.PARSER, extensionRegistryLite));
                            break;
                        case 42:
                            if ((mutable_bitField0_ & 64) != 64) {
                                this.enumType_ = new ArrayList();
                                mutable_bitField0_ |= 64;
                            }
                            this.enumType_.add(codedInputStream.readMessage(EnumDescriptorProto.PARSER, extensionRegistryLite));
                            break;
                        case 50:
                            if ((mutable_bitField0_ & 128) != 128) {
                                this.service_ = new ArrayList();
                                mutable_bitField0_ |= 128;
                            }
                            this.service_.add(codedInputStream.readMessage(ServiceDescriptorProto.PARSER, extensionRegistryLite));
                            break;
                        case 58:
                            if ((mutable_bitField0_ & 256) != 256) {
                                this.extension_ = new ArrayList();
                                mutable_bitField0_ |= 256;
                            }
                            this.extension_.add(codedInputStream.readMessage(FieldDescriptorProto.PARSER, extensionRegistryLite));
                            break;
                        case 66:
                            FileOptions.Builder subBuilder = (this.bitField0_ & 4) == 4 ? this.options_.toBuilder() : null;
                            this.options_ = (FileOptions) codedInputStream.readMessage(FileOptions.PARSER, extensionRegistryLite);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 4;
                            break;
                        case 74:
                            SourceCodeInfo.Builder subBuilder2 = (this.bitField0_ & 8) == 8 ? this.sourceCodeInfo_.toBuilder() : null;
                            this.sourceCodeInfo_ = (SourceCodeInfo) codedInputStream.readMessage(SourceCodeInfo.PARSER, extensionRegistryLite);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.sourceCodeInfo_);
                                this.sourceCodeInfo_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 8;
                            break;
                        case 80:
                            if ((mutable_bitField0_ & 8) != 8) {
                                this.publicDependency_ = new ArrayList();
                                mutable_bitField0_ |= 8;
                            }
                            this.publicDependency_.add(Integer.valueOf(input.readInt32()));
                            break;
                        case 82:
                            int limit = codedInputStream.pushLimit(input.readRawVarint32());
                            if ((mutable_bitField0_ & 8) != 8 && input.getBytesUntilLimit() > 0) {
                                this.publicDependency_ = new ArrayList();
                                mutable_bitField0_ |= 8;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.publicDependency_.add(Integer.valueOf(input.readInt32()));
                            }
                            codedInputStream.popLimit(limit);
                            break;
                        case 88:
                            if ((mutable_bitField0_ & 16) != 16) {
                                this.weakDependency_ = new ArrayList();
                                mutable_bitField0_ |= 16;
                            }
                            this.weakDependency_.add(Integer.valueOf(input.readInt32()));
                            break;
                        case 90:
                            int limit2 = codedInputStream.pushLimit(input.readRawVarint32());
                            if ((mutable_bitField0_ & 16) != 16 && input.getBytesUntilLimit() > 0) {
                                this.weakDependency_ = new ArrayList();
                                mutable_bitField0_ |= 16;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.weakDependency_.add(Integer.valueOf(input.readInt32()));
                            }
                            codedInputStream.popLimit(limit2);
                            break;
                        default:
                            if (parseUnknownField(codedInputStream, unknownFields2, extensionRegistryLite, tag)) {
                                break;
                            } else {
                                done = true;
                                break;
                            }
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 4) == 4) {
                        this.dependency_ = new UnmodifiableLazyStringList(this.dependency_);
                    }
                    if ((mutable_bitField0_ & 32) == 32) {
                        this.messageType_ = Collections.unmodifiableList(this.messageType_);
                    }
                    if ((mutable_bitField0_ & 64) == 64) {
                        this.enumType_ = Collections.unmodifiableList(this.enumType_);
                    }
                    if ((mutable_bitField0_ & 128) == 128) {
                        this.service_ = Collections.unmodifiableList(this.service_);
                    }
                    if ((mutable_bitField0_ & 256) == 256) {
                        this.extension_ = Collections.unmodifiableList(this.extension_);
                    }
                    if ((mutable_bitField0_ & 8) == 8) {
                        this.publicDependency_ = Collections.unmodifiableList(this.publicDependency_);
                    }
                    if ((mutable_bitField0_ & 16) == 16) {
                        this.weakDependency_ = Collections.unmodifiableList(this.weakDependency_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 4) == 4) {
                this.dependency_ = new UnmodifiableLazyStringList(this.dependency_);
            }
            if ((mutable_bitField0_ & 32) == 32) {
                this.messageType_ = Collections.unmodifiableList(this.messageType_);
            }
            if ((mutable_bitField0_ & 64) == 64) {
                this.enumType_ = Collections.unmodifiableList(this.enumType_);
            }
            if ((mutable_bitField0_ & 128) == 128) {
                this.service_ = Collections.unmodifiableList(this.service_);
            }
            if ((mutable_bitField0_ & 256) == 256) {
                this.extension_ = Collections.unmodifiableList(this.extension_);
            }
            if ((mutable_bitField0_ & 8) == 8) {
                this.publicDependency_ = Collections.unmodifiableList(this.publicDependency_);
            }
            if ((mutable_bitField0_ & 16) == 16) {
                this.weakDependency_ = Collections.unmodifiableList(this.weakDependency_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorProto.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<FileDescriptorProto> getParserForType() {
            return PARSER;
        }

        public boolean hasName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public boolean hasPackage() {
            return (this.bitField0_ & 2) == 2;
        }

        public String getPackage() {
            Object ref = this.package_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.package_ = s;
            }
            return s;
        }

        public ByteString getPackageBytes() {
            Object ref = this.package_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.package_ = b;
            return b;
        }

        public List<String> getDependencyList() {
            return this.dependency_;
        }

        public int getDependencyCount() {
            return this.dependency_.size();
        }

        public String getDependency(int index) {
            return (String) this.dependency_.get(index);
        }

        public ByteString getDependencyBytes(int index) {
            return this.dependency_.getByteString(index);
        }

        public List<Integer> getPublicDependencyList() {
            return this.publicDependency_;
        }

        public int getPublicDependencyCount() {
            return this.publicDependency_.size();
        }

        public int getPublicDependency(int index) {
            return this.publicDependency_.get(index).intValue();
        }

        public List<Integer> getWeakDependencyList() {
            return this.weakDependency_;
        }

        public int getWeakDependencyCount() {
            return this.weakDependency_.size();
        }

        public int getWeakDependency(int index) {
            return this.weakDependency_.get(index).intValue();
        }

        public List<DescriptorProto> getMessageTypeList() {
            return this.messageType_;
        }

        public List<? extends DescriptorProtoOrBuilder> getMessageTypeOrBuilderList() {
            return this.messageType_;
        }

        public int getMessageTypeCount() {
            return this.messageType_.size();
        }

        public DescriptorProto getMessageType(int index) {
            return this.messageType_.get(index);
        }

        public DescriptorProtoOrBuilder getMessageTypeOrBuilder(int index) {
            return this.messageType_.get(index);
        }

        public List<EnumDescriptorProto> getEnumTypeList() {
            return this.enumType_;
        }

        public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
            return this.enumType_;
        }

        public int getEnumTypeCount() {
            return this.enumType_.size();
        }

        public EnumDescriptorProto getEnumType(int index) {
            return this.enumType_.get(index);
        }

        public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int index) {
            return this.enumType_.get(index);
        }

        public List<ServiceDescriptorProto> getServiceList() {
            return this.service_;
        }

        public List<? extends ServiceDescriptorProtoOrBuilder> getServiceOrBuilderList() {
            return this.service_;
        }

        public int getServiceCount() {
            return this.service_.size();
        }

        public ServiceDescriptorProto getService(int index) {
            return this.service_.get(index);
        }

        public ServiceDescriptorProtoOrBuilder getServiceOrBuilder(int index) {
            return this.service_.get(index);
        }

        public List<FieldDescriptorProto> getExtensionList() {
            return this.extension_;
        }

        public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
            return this.extension_;
        }

        public int getExtensionCount() {
            return this.extension_.size();
        }

        public FieldDescriptorProto getExtension(int index) {
            return this.extension_.get(index);
        }

        public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int index) {
            return this.extension_.get(index);
        }

        public boolean hasOptions() {
            return (this.bitField0_ & 4) == 4;
        }

        public FileOptions getOptions() {
            return this.options_;
        }

        public FileOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }

        public boolean hasSourceCodeInfo() {
            return (this.bitField0_ & 8) == 8;
        }

        public SourceCodeInfo getSourceCodeInfo() {
            return this.sourceCodeInfo_;
        }

        public SourceCodeInfoOrBuilder getSourceCodeInfoOrBuilder() {
            return this.sourceCodeInfo_;
        }

        private void initFields() {
            this.name_ = "";
            this.package_ = "";
            this.dependency_ = LazyStringArrayList.EMPTY;
            this.publicDependency_ = Collections.emptyList();
            this.weakDependency_ = Collections.emptyList();
            this.messageType_ = Collections.emptyList();
            this.enumType_ = Collections.emptyList();
            this.service_ = Collections.emptyList();
            this.extension_ = Collections.emptyList();
            this.options_ = FileOptions.getDefaultInstance();
            this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getMessageTypeCount(); i++) {
                if (!getMessageType(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i2 = 0; i2 < getEnumTypeCount(); i2++) {
                if (!getEnumType(i2).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i3 = 0; i3 < getServiceCount(); i3++) {
                if (!getService(i3).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i4 = 0; i4 < getExtensionCount(); i4++) {
                if (!getExtension(i4).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (hasOptions() == 0 || getOptions().isInitialized()) {
                this.memoizedIsInitialized = 1;
                return true;
            }
            this.memoizedIsInitialized = 0;
            return false;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getNameBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeBytes(2, getPackageBytes());
            }
            for (int i = 0; i < this.dependency_.size(); i++) {
                output.writeBytes(3, this.dependency_.getByteString(i));
            }
            for (int i2 = 0; i2 < this.messageType_.size(); i2++) {
                output.writeMessage(4, this.messageType_.get(i2));
            }
            for (int i3 = 0; i3 < this.enumType_.size(); i3++) {
                output.writeMessage(5, this.enumType_.get(i3));
            }
            for (int i4 = 0; i4 < this.service_.size(); i4++) {
                output.writeMessage(6, this.service_.get(i4));
            }
            for (int i5 = 0; i5 < this.extension_.size(); i5++) {
                output.writeMessage(7, this.extension_.get(i5));
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeMessage(8, this.options_);
            }
            if ((this.bitField0_ & 8) == 8) {
                output.writeMessage(9, this.sourceCodeInfo_);
            }
            for (int i6 = 0; i6 < this.publicDependency_.size(); i6++) {
                output.writeInt32(10, this.publicDependency_.get(i6).intValue());
            }
            for (int i7 = 0; i7 < this.weakDependency_.size(); i7++) {
                output.writeInt32(11, this.weakDependency_.get(i7).intValue());
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getNameBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeBytesSize(2, getPackageBytes());
            }
            int dataSize = 0;
            for (int i = 0; i < this.dependency_.size(); i++) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.dependency_.getByteString(i));
            }
            int size3 = size2 + dataSize + (getDependencyList().size() * 1);
            for (int i2 = 0; i2 < this.messageType_.size(); i2++) {
                size3 += CodedOutputStream.computeMessageSize(4, this.messageType_.get(i2));
            }
            for (int i3 = 0; i3 < this.enumType_.size(); i3++) {
                size3 += CodedOutputStream.computeMessageSize(5, this.enumType_.get(i3));
            }
            for (int i4 = 0; i4 < this.service_.size(); i4++) {
                size3 += CodedOutputStream.computeMessageSize(6, this.service_.get(i4));
            }
            for (int i5 = 0; i5 < this.extension_.size(); i5++) {
                size3 += CodedOutputStream.computeMessageSize(7, this.extension_.get(i5));
            }
            if ((this.bitField0_ & 4) == 4) {
                size3 += CodedOutputStream.computeMessageSize(8, this.options_);
            }
            if ((this.bitField0_ & 8) == 8) {
                size3 += CodedOutputStream.computeMessageSize(9, this.sourceCodeInfo_);
            }
            int dataSize2 = 0;
            for (int i6 = 0; i6 < this.publicDependency_.size(); i6++) {
                dataSize2 += CodedOutputStream.computeInt32SizeNoTag(this.publicDependency_.get(i6).intValue());
            }
            int size4 = size3 + dataSize2 + (getPublicDependencyList().size() * 1);
            int dataSize3 = 0;
            for (int i7 = 0; i7 < this.weakDependency_.size(); i7++) {
                dataSize3 += CodedOutputStream.computeInt32SizeNoTag(this.weakDependency_.get(i7).intValue());
            }
            int size5 = size4 + dataSize3 + (getWeakDependencyList().size() * 1) + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size5;
            return size5;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static FileDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FileDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FileDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FileDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FileDescriptorProto parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FileDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static FileDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static FileDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static FileDescriptorProto parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FileDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(FileDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FileDescriptorProtoOrBuilder {
            private int bitField0_;
            private LazyStringList dependency_;
            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> enumTypeBuilder_;
            private List<EnumDescriptorProto> enumType_;
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> extensionBuilder_;
            private List<FieldDescriptorProto> extension_;
            private RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> messageTypeBuilder_;
            private List<DescriptorProto> messageType_;
            private Object name_;
            private SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> optionsBuilder_;
            private FileOptions options_;
            private Object package_;
            private List<Integer> publicDependency_;
            private RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> serviceBuilder_;
            private List<ServiceDescriptorProto> service_;
            private SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> sourceCodeInfoBuilder_;
            private SourceCodeInfo sourceCodeInfo_;
            private List<Integer> weakDependency_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorProto.class, Builder.class);
            }

            private Builder() {
                this.name_ = "";
                this.package_ = "";
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.publicDependency_ = Collections.emptyList();
                this.weakDependency_ = Collections.emptyList();
                this.messageType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.service_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.options_ = FileOptions.getDefaultInstance();
                this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.package_ = "";
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.publicDependency_ = Collections.emptyList();
                this.weakDependency_ = Collections.emptyList();
                this.messageType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.service_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.options_ = FileOptions.getDefaultInstance();
                this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getMessageTypeFieldBuilder();
                    getEnumTypeFieldBuilder();
                    getServiceFieldBuilder();
                    getExtensionFieldBuilder();
                    getOptionsFieldBuilder();
                    getSourceCodeInfoFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= -2;
                this.package_ = "";
                this.bitField0_ &= -3;
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= -5;
                this.publicDependency_ = Collections.emptyList();
                this.bitField0_ &= -9;
                this.weakDependency_ = Collections.emptyList();
                this.bitField0_ &= -17;
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.messageType_ = Collections.emptyList();
                    this.bitField0_ &= -33;
                } else {
                    repeatedFieldBuilder.clear();
                }
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder2 = this.enumTypeBuilder_;
                if (repeatedFieldBuilder2 == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= -65;
                } else {
                    repeatedFieldBuilder2.clear();
                }
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder3 = this.serviceBuilder_;
                if (repeatedFieldBuilder3 == null) {
                    this.service_ = Collections.emptyList();
                    this.bitField0_ &= -129;
                } else {
                    repeatedFieldBuilder3.clear();
                }
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder4 = this.extensionBuilder_;
                if (repeatedFieldBuilder4 == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= -257;
                } else {
                    repeatedFieldBuilder4.clear();
                }
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = FileOptions.getDefaultInstance();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -513;
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder2 = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder2 == null) {
                    this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                } else {
                    singleFieldBuilder2.clear();
                }
                this.bitField0_ &= -1025;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor;
            }

            public FileDescriptorProto getDefaultInstanceForType() {
                return FileDescriptorProto.getDefaultInstance();
            }

            public FileDescriptorProto build() {
                FileDescriptorProto result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public FileDescriptorProto buildPartial() {
                FileDescriptorProto result = new FileDescriptorProto((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.name_ = this.name_;
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ |= 2;
                }
                Object unused2 = result.package_ = this.package_;
                if ((this.bitField0_ & 4) == 4) {
                    this.dependency_ = new UnmodifiableLazyStringList(this.dependency_);
                    this.bitField0_ &= -5;
                }
                LazyStringList unused3 = result.dependency_ = this.dependency_;
                if ((this.bitField0_ & 8) == 8) {
                    this.publicDependency_ = Collections.unmodifiableList(this.publicDependency_);
                    this.bitField0_ &= -9;
                }
                List unused4 = result.publicDependency_ = this.publicDependency_;
                if ((this.bitField0_ & 16) == 16) {
                    this.weakDependency_ = Collections.unmodifiableList(this.weakDependency_);
                    this.bitField0_ &= -17;
                }
                List unused5 = result.weakDependency_ = this.weakDependency_;
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 32) == 32) {
                        this.messageType_ = Collections.unmodifiableList(this.messageType_);
                        this.bitField0_ &= -33;
                    }
                    List unused6 = result.messageType_ = this.messageType_;
                } else {
                    List unused7 = result.messageType_ = repeatedFieldBuilder.build();
                }
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder2 = this.enumTypeBuilder_;
                if (repeatedFieldBuilder2 == null) {
                    if ((this.bitField0_ & 64) == 64) {
                        this.enumType_ = Collections.unmodifiableList(this.enumType_);
                        this.bitField0_ &= -65;
                    }
                    List unused8 = result.enumType_ = this.enumType_;
                } else {
                    List unused9 = result.enumType_ = repeatedFieldBuilder2.build();
                }
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder3 = this.serviceBuilder_;
                if (repeatedFieldBuilder3 == null) {
                    if ((this.bitField0_ & 128) == 128) {
                        this.service_ = Collections.unmodifiableList(this.service_);
                        this.bitField0_ &= -129;
                    }
                    List unused10 = result.service_ = this.service_;
                } else {
                    List unused11 = result.service_ = repeatedFieldBuilder3.build();
                }
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder4 = this.extensionBuilder_;
                if (repeatedFieldBuilder4 == null) {
                    if ((this.bitField0_ & 256) == 256) {
                        this.extension_ = Collections.unmodifiableList(this.extension_);
                        this.bitField0_ &= -257;
                    }
                    List unused12 = result.extension_ = this.extension_;
                } else {
                    List unused13 = result.extension_ = repeatedFieldBuilder4.build();
                }
                if ((from_bitField0_ & 512) == 512) {
                    to_bitField0_ |= 4;
                }
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    FileOptions unused14 = result.options_ = this.options_;
                } else {
                    FileOptions unused15 = result.options_ = singleFieldBuilder.build();
                }
                if ((from_bitField0_ & 1024) == 1024) {
                    to_bitField0_ |= 8;
                }
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder2 = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder2 == null) {
                    SourceCodeInfo unused16 = result.sourceCodeInfo_ = this.sourceCodeInfo_;
                } else {
                    SourceCodeInfo unused17 = result.sourceCodeInfo_ = singleFieldBuilder2.build();
                }
                int unused18 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof FileDescriptorProto) {
                    return mergeFrom((FileDescriptorProto) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(FileDescriptorProto other) {
                if (other == FileDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 1;
                    this.name_ = other.name_;
                    onChanged();
                }
                if (other.hasPackage()) {
                    this.bitField0_ |= 2;
                    this.package_ = other.package_;
                    onChanged();
                }
                if (!other.dependency_.isEmpty()) {
                    if (this.dependency_.isEmpty()) {
                        this.dependency_ = other.dependency_;
                        this.bitField0_ &= -5;
                    } else {
                        ensureDependencyIsMutable();
                        this.dependency_.addAll(other.dependency_);
                    }
                    onChanged();
                }
                if (!other.publicDependency_.isEmpty()) {
                    if (this.publicDependency_.isEmpty()) {
                        this.publicDependency_ = other.publicDependency_;
                        this.bitField0_ &= -9;
                    } else {
                        ensurePublicDependencyIsMutable();
                        this.publicDependency_.addAll(other.publicDependency_);
                    }
                    onChanged();
                }
                if (!other.weakDependency_.isEmpty()) {
                    if (this.weakDependency_.isEmpty()) {
                        this.weakDependency_ = other.weakDependency_;
                        this.bitField0_ &= -17;
                    } else {
                        ensureWeakDependencyIsMutable();
                        this.weakDependency_.addAll(other.weakDependency_);
                    }
                    onChanged();
                }
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = null;
                if (this.messageTypeBuilder_ == null) {
                    if (!other.messageType_.isEmpty()) {
                        if (this.messageType_.isEmpty()) {
                            this.messageType_ = other.messageType_;
                            this.bitField0_ &= -33;
                        } else {
                            ensureMessageTypeIsMutable();
                            this.messageType_.addAll(other.messageType_);
                        }
                        onChanged();
                    }
                } else if (!other.messageType_.isEmpty()) {
                    if (this.messageTypeBuilder_.isEmpty()) {
                        this.messageTypeBuilder_.dispose();
                        this.messageTypeBuilder_ = null;
                        this.messageType_ = other.messageType_;
                        this.bitField0_ &= -33;
                        this.messageTypeBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? getMessageTypeFieldBuilder() : null;
                    } else {
                        this.messageTypeBuilder_.addAllMessages(other.messageType_);
                    }
                }
                if (this.enumTypeBuilder_ == null) {
                    if (!other.enumType_.isEmpty()) {
                        if (this.enumType_.isEmpty()) {
                            this.enumType_ = other.enumType_;
                            this.bitField0_ &= -65;
                        } else {
                            ensureEnumTypeIsMutable();
                            this.enumType_.addAll(other.enumType_);
                        }
                        onChanged();
                    }
                } else if (!other.enumType_.isEmpty()) {
                    if (this.enumTypeBuilder_.isEmpty()) {
                        this.enumTypeBuilder_.dispose();
                        this.enumTypeBuilder_ = null;
                        this.enumType_ = other.enumType_;
                        this.bitField0_ &= -65;
                        this.enumTypeBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? getEnumTypeFieldBuilder() : null;
                    } else {
                        this.enumTypeBuilder_.addAllMessages(other.enumType_);
                    }
                }
                if (this.serviceBuilder_ == null) {
                    if (!other.service_.isEmpty()) {
                        if (this.service_.isEmpty()) {
                            this.service_ = other.service_;
                            this.bitField0_ &= -129;
                        } else {
                            ensureServiceIsMutable();
                            this.service_.addAll(other.service_);
                        }
                        onChanged();
                    }
                } else if (!other.service_.isEmpty()) {
                    if (this.serviceBuilder_.isEmpty()) {
                        this.serviceBuilder_.dispose();
                        this.serviceBuilder_ = null;
                        this.service_ = other.service_;
                        this.bitField0_ &= -129;
                        this.serviceBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? getServiceFieldBuilder() : null;
                    } else {
                        this.serviceBuilder_.addAllMessages(other.service_);
                    }
                }
                if (this.extensionBuilder_ == null) {
                    if (!other.extension_.isEmpty()) {
                        if (this.extension_.isEmpty()) {
                            this.extension_ = other.extension_;
                            this.bitField0_ &= -257;
                        } else {
                            ensureExtensionIsMutable();
                            this.extension_.addAll(other.extension_);
                        }
                        onChanged();
                    }
                } else if (!other.extension_.isEmpty()) {
                    if (this.extensionBuilder_.isEmpty()) {
                        this.extensionBuilder_.dispose();
                        this.extensionBuilder_ = null;
                        this.extension_ = other.extension_;
                        this.bitField0_ &= -257;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getExtensionFieldBuilder();
                        }
                        this.extensionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.extensionBuilder_.addAllMessages(other.extension_);
                    }
                }
                if (other.hasOptions()) {
                    mergeOptions(other.getOptions());
                }
                if (other.hasSourceCodeInfo()) {
                    mergeSourceCodeInfo(other.getSourceCodeInfo());
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getMessageTypeCount(); i++) {
                    if (!getMessageType(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i2 = 0; i2 < getEnumTypeCount(); i2++) {
                    if (!getEnumType(i2).isInitialized()) {
                        return false;
                    }
                }
                for (int i3 = 0; i3 < getServiceCount(); i3++) {
                    if (!getService(i3).isInitialized()) {
                        return false;
                    }
                }
                for (int i4 = 0; i4 < getExtensionCount(); i4++) {
                    if (!getExtension(i4).isInitialized()) {
                        return false;
                    }
                }
                if (hasOptions() == 0 || getOptions().isInitialized()) {
                    return true;
                }
                return false;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    FileDescriptorProto parsedMessage = FileDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    FileDescriptorProto parsedMessage2 = (FileDescriptorProto) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((FileDescriptorProto) null);
                    }
                    throw th;
                }
            }

            public boolean hasName() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearName() {
                this.bitField0_ &= -2;
                this.name_ = FileDescriptorProto.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasPackage() {
                return (this.bitField0_ & 2) == 2;
            }

            public String getPackage() {
                Object ref = this.package_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.package_ = s;
                return s;
            }

            public ByteString getPackageBytes() {
                Object ref = this.package_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.package_ = b;
                return b;
            }

            public Builder setPackage(String value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.package_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearPackage() {
                this.bitField0_ &= -3;
                this.package_ = FileDescriptorProto.getDefaultInstance().getPackage();
                onChanged();
                return this;
            }

            public Builder setPackageBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.package_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            private void ensureDependencyIsMutable() {
                if ((this.bitField0_ & 4) != 4) {
                    this.dependency_ = new LazyStringArrayList(this.dependency_);
                    this.bitField0_ |= 4;
                }
            }

            public List<String> getDependencyList() {
                return Collections.unmodifiableList(this.dependency_);
            }

            public int getDependencyCount() {
                return this.dependency_.size();
            }

            public String getDependency(int index) {
                return (String) this.dependency_.get(index);
            }

            public ByteString getDependencyBytes(int index) {
                return this.dependency_.getByteString(index);
            }

            public Builder setDependency(int index, String value) {
                if (value != null) {
                    ensureDependencyIsMutable();
                    this.dependency_.set(index, value);
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder addDependency(String value) {
                if (value != null) {
                    ensureDependencyIsMutable();
                    this.dependency_.add(value);
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder addAllDependency(Iterable<String> values) {
                ensureDependencyIsMutable();
                GeneratedMessage.Builder.addAll(values, this.dependency_);
                onChanged();
                return this;
            }

            public Builder clearDependency() {
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= -5;
                onChanged();
                return this;
            }

            public Builder addDependencyBytes(ByteString value) {
                if (value != null) {
                    ensureDependencyIsMutable();
                    this.dependency_.add(value);
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            private void ensurePublicDependencyIsMutable() {
                if ((this.bitField0_ & 8) != 8) {
                    this.publicDependency_ = new ArrayList(this.publicDependency_);
                    this.bitField0_ |= 8;
                }
            }

            public List<Integer> getPublicDependencyList() {
                return Collections.unmodifiableList(this.publicDependency_);
            }

            public int getPublicDependencyCount() {
                return this.publicDependency_.size();
            }

            public int getPublicDependency(int index) {
                return this.publicDependency_.get(index).intValue();
            }

            public Builder setPublicDependency(int index, int value) {
                ensurePublicDependencyIsMutable();
                this.publicDependency_.set(index, Integer.valueOf(value));
                onChanged();
                return this;
            }

            public Builder addPublicDependency(int value) {
                ensurePublicDependencyIsMutable();
                this.publicDependency_.add(Integer.valueOf(value));
                onChanged();
                return this;
            }

            public Builder addAllPublicDependency(Iterable<? extends Integer> values) {
                ensurePublicDependencyIsMutable();
                GeneratedMessage.Builder.addAll(values, this.publicDependency_);
                onChanged();
                return this;
            }

            public Builder clearPublicDependency() {
                this.publicDependency_ = Collections.emptyList();
                this.bitField0_ &= -9;
                onChanged();
                return this;
            }

            private void ensureWeakDependencyIsMutable() {
                if ((this.bitField0_ & 16) != 16) {
                    this.weakDependency_ = new ArrayList(this.weakDependency_);
                    this.bitField0_ |= 16;
                }
            }

            public List<Integer> getWeakDependencyList() {
                return Collections.unmodifiableList(this.weakDependency_);
            }

            public int getWeakDependencyCount() {
                return this.weakDependency_.size();
            }

            public int getWeakDependency(int index) {
                return this.weakDependency_.get(index).intValue();
            }

            public Builder setWeakDependency(int index, int value) {
                ensureWeakDependencyIsMutable();
                this.weakDependency_.set(index, Integer.valueOf(value));
                onChanged();
                return this;
            }

            public Builder addWeakDependency(int value) {
                ensureWeakDependencyIsMutable();
                this.weakDependency_.add(Integer.valueOf(value));
                onChanged();
                return this;
            }

            public Builder addAllWeakDependency(Iterable<? extends Integer> values) {
                ensureWeakDependencyIsMutable();
                GeneratedMessage.Builder.addAll(values, this.weakDependency_);
                onChanged();
                return this;
            }

            public Builder clearWeakDependency() {
                this.weakDependency_ = Collections.emptyList();
                this.bitField0_ &= -17;
                onChanged();
                return this;
            }

            private void ensureMessageTypeIsMutable() {
                if ((this.bitField0_ & 32) != 32) {
                    this.messageType_ = new ArrayList(this.messageType_);
                    this.bitField0_ |= 32;
                }
            }

            public List<DescriptorProto> getMessageTypeList() {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.messageType_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getMessageTypeCount() {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.messageType_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public DescriptorProto getMessageType(int index) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.messageType_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setMessageType(int index, DescriptorProto value) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureMessageTypeIsMutable();
                    this.messageType_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setMessageType(int index, DescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMessageTypeIsMutable();
                    this.messageType_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addMessageType(DescriptorProto value) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureMessageTypeIsMutable();
                    this.messageType_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addMessageType(int index, DescriptorProto value) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureMessageTypeIsMutable();
                    this.messageType_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addMessageType(DescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMessageTypeIsMutable();
                    this.messageType_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addMessageType(int index, DescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMessageTypeIsMutable();
                    this.messageType_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllMessageType(Iterable<? extends DescriptorProto> values) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMessageTypeIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.messageType_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearMessageType() {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.messageType_ = Collections.emptyList();
                    this.bitField0_ &= -33;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeMessageType(int index) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMessageTypeIsMutable();
                    this.messageType_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public DescriptorProto.Builder getMessageTypeBuilder(int index) {
                return getMessageTypeFieldBuilder().getBuilder(index);
            }

            public DescriptorProtoOrBuilder getMessageTypeOrBuilder(int index) {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.messageType_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends DescriptorProtoOrBuilder> getMessageTypeOrBuilderList() {
                RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.messageTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.messageType_);
            }

            public DescriptorProto.Builder addMessageTypeBuilder() {
                return getMessageTypeFieldBuilder().addBuilder(DescriptorProto.getDefaultInstance());
            }

            public DescriptorProto.Builder addMessageTypeBuilder(int index) {
                return getMessageTypeFieldBuilder().addBuilder(index, DescriptorProto.getDefaultInstance());
            }

            public List<DescriptorProto.Builder> getMessageTypeBuilderList() {
                return getMessageTypeFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> getMessageTypeFieldBuilder() {
                if (this.messageTypeBuilder_ == null) {
                    this.messageTypeBuilder_ = new RepeatedFieldBuilder<>(this.messageType_, (this.bitField0_ & 32) == 32, getParentForChildren(), isClean());
                    this.messageType_ = null;
                }
                return this.messageTypeBuilder_;
            }

            private void ensureEnumTypeIsMutable() {
                if ((this.bitField0_ & 64) != 64) {
                    this.enumType_ = new ArrayList(this.enumType_);
                    this.bitField0_ |= 64;
                }
            }

            public List<EnumDescriptorProto> getEnumTypeList() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.enumType_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getEnumTypeCount() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.enumType_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public EnumDescriptorProto getEnumType(int index) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.enumType_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setEnumType(int index, EnumDescriptorProto value) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setEnumType(int index, EnumDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addEnumType(EnumDescriptorProto value) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addEnumType(int index, EnumDescriptorProto value) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addEnumType(EnumDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addEnumType(int index, EnumDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllEnumType(Iterable<? extends EnumDescriptorProto> values) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.enumType_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearEnumType() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= -65;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeEnumType(int index) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public EnumDescriptorProto.Builder getEnumTypeBuilder(int index) {
                return getEnumTypeFieldBuilder().getBuilder(index);
            }

            public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int index) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.enumType_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.enumType_);
            }

            public EnumDescriptorProto.Builder addEnumTypeBuilder() {
                return getEnumTypeFieldBuilder().addBuilder(EnumDescriptorProto.getDefaultInstance());
            }

            public EnumDescriptorProto.Builder addEnumTypeBuilder(int index) {
                return getEnumTypeFieldBuilder().addBuilder(index, EnumDescriptorProto.getDefaultInstance());
            }

            public List<EnumDescriptorProto.Builder> getEnumTypeBuilderList() {
                return getEnumTypeFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> getEnumTypeFieldBuilder() {
                if (this.enumTypeBuilder_ == null) {
                    this.enumTypeBuilder_ = new RepeatedFieldBuilder<>(this.enumType_, (this.bitField0_ & 64) == 64, getParentForChildren(), isClean());
                    this.enumType_ = null;
                }
                return this.enumTypeBuilder_;
            }

            private void ensureServiceIsMutable() {
                if ((this.bitField0_ & 128) != 128) {
                    this.service_ = new ArrayList(this.service_);
                    this.bitField0_ |= 128;
                }
            }

            public List<ServiceDescriptorProto> getServiceList() {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.service_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getServiceCount() {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.service_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public ServiceDescriptorProto getService(int index) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.service_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setService(int index, ServiceDescriptorProto value) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureServiceIsMutable();
                    this.service_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setService(int index, ServiceDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureServiceIsMutable();
                    this.service_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addService(ServiceDescriptorProto value) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureServiceIsMutable();
                    this.service_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addService(int index, ServiceDescriptorProto value) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureServiceIsMutable();
                    this.service_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addService(ServiceDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureServiceIsMutable();
                    this.service_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addService(int index, ServiceDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureServiceIsMutable();
                    this.service_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllService(Iterable<? extends ServiceDescriptorProto> values) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureServiceIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.service_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearService() {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.service_ = Collections.emptyList();
                    this.bitField0_ &= -129;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeService(int index) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureServiceIsMutable();
                    this.service_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public ServiceDescriptorProto.Builder getServiceBuilder(int index) {
                return getServiceFieldBuilder().getBuilder(index);
            }

            public ServiceDescriptorProtoOrBuilder getServiceOrBuilder(int index) {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.service_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends ServiceDescriptorProtoOrBuilder> getServiceOrBuilderList() {
                RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> repeatedFieldBuilder = this.serviceBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.service_);
            }

            public ServiceDescriptorProto.Builder addServiceBuilder() {
                return getServiceFieldBuilder().addBuilder(ServiceDescriptorProto.getDefaultInstance());
            }

            public ServiceDescriptorProto.Builder addServiceBuilder(int index) {
                return getServiceFieldBuilder().addBuilder(index, ServiceDescriptorProto.getDefaultInstance());
            }

            public List<ServiceDescriptorProto.Builder> getServiceBuilderList() {
                return getServiceFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> getServiceFieldBuilder() {
                if (this.serviceBuilder_ == null) {
                    this.serviceBuilder_ = new RepeatedFieldBuilder<>(this.service_, (this.bitField0_ & 128) == 128, getParentForChildren(), isClean());
                    this.service_ = null;
                }
                return this.serviceBuilder_;
            }

            private void ensureExtensionIsMutable() {
                if ((this.bitField0_ & 256) != 256) {
                    this.extension_ = new ArrayList(this.extension_);
                    this.bitField0_ |= 256;
                }
            }

            public List<FieldDescriptorProto> getExtensionList() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.extension_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getExtensionCount() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extension_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public FieldDescriptorProto getExtension(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extension_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setExtension(int index, FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureExtensionIsMutable();
                    this.extension_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setExtension(int index, FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addExtension(FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addExtension(int index, FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addExtension(FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addExtension(int index, FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllExtension(Iterable<? extends FieldDescriptorProto> values) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.extension_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearExtension() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= -257;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeExtension(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public FieldDescriptorProto.Builder getExtensionBuilder(int index) {
                return getExtensionFieldBuilder().getBuilder(index);
            }

            public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extension_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.extension_);
            }

            public FieldDescriptorProto.Builder addExtensionBuilder() {
                return getExtensionFieldBuilder().addBuilder(FieldDescriptorProto.getDefaultInstance());
            }

            public FieldDescriptorProto.Builder addExtensionBuilder(int index) {
                return getExtensionFieldBuilder().addBuilder(index, FieldDescriptorProto.getDefaultInstance());
            }

            public List<FieldDescriptorProto.Builder> getExtensionBuilderList() {
                return getExtensionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> getExtensionFieldBuilder() {
                if (this.extensionBuilder_ == null) {
                    this.extensionBuilder_ = new RepeatedFieldBuilder<>(this.extension_, (this.bitField0_ & 256) == 256, getParentForChildren(), isClean());
                    this.extension_ = null;
                }
                return this.extensionBuilder_;
            }

            public boolean hasOptions() {
                return (this.bitField0_ & 512) == 512;
            }

            public FileOptions getOptions() {
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    return this.options_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setOptions(FileOptions value) {
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.options_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 512;
                return this;
            }

            public Builder setOptions(FileOptions.Builder builderForValue) {
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 512;
                return this;
            }

            public Builder mergeOptions(FileOptions value) {
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 512) != 512 || this.options_ == FileOptions.getDefaultInstance()) {
                        this.options_ = value;
                    } else {
                        this.options_ = FileOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 512;
                return this;
            }

            public Builder clearOptions() {
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = FileOptions.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -513;
                return this;
            }

            public FileOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 512;
                onChanged();
                return getOptionsFieldBuilder().getBuilder();
            }

            public FileOptionsOrBuilder getOptionsOrBuilder() {
                SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.options_;
            }

            private SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<>(this.options_, getParentForChildren(), isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }

            public boolean hasSourceCodeInfo() {
                return (this.bitField0_ & 1024) == 1024;
            }

            public SourceCodeInfo getSourceCodeInfo() {
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder == null) {
                    return this.sourceCodeInfo_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setSourceCodeInfo(SourceCodeInfo value) {
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.sourceCodeInfo_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1024;
                return this;
            }

            public Builder setSourceCodeInfo(SourceCodeInfo.Builder builderForValue) {
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder == null) {
                    this.sourceCodeInfo_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 1024;
                return this;
            }

            public Builder mergeSourceCodeInfo(SourceCodeInfo value) {
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 1024) != 1024 || this.sourceCodeInfo_ == SourceCodeInfo.getDefaultInstance()) {
                        this.sourceCodeInfo_ = value;
                    } else {
                        this.sourceCodeInfo_ = SourceCodeInfo.newBuilder(this.sourceCodeInfo_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 1024;
                return this;
            }

            public Builder clearSourceCodeInfo() {
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder == null) {
                    this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -1025;
                return this;
            }

            public SourceCodeInfo.Builder getSourceCodeInfoBuilder() {
                this.bitField0_ |= 1024;
                onChanged();
                return getSourceCodeInfoFieldBuilder().getBuilder();
            }

            public SourceCodeInfoOrBuilder getSourceCodeInfoOrBuilder() {
                SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> singleFieldBuilder = this.sourceCodeInfoBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.sourceCodeInfo_;
            }

            private SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> getSourceCodeInfoFieldBuilder() {
                if (this.sourceCodeInfoBuilder_ == null) {
                    this.sourceCodeInfoBuilder_ = new SingleFieldBuilder<>(this.sourceCodeInfo_, getParentForChildren(), isClean());
                    this.sourceCodeInfo_ = null;
                }
                return this.sourceCodeInfoBuilder_;
            }
        }
    }

    public static final class DescriptorProto extends GeneratedMessage implements DescriptorProtoOrBuilder {
        public static final int ENUM_TYPE_FIELD_NUMBER = 4;
        public static final int EXTENSION_FIELD_NUMBER = 6;
        public static final int EXTENSION_RANGE_FIELD_NUMBER = 5;
        public static final int FIELD_FIELD_NUMBER = 2;
        public static final int NAME_FIELD_NUMBER = 1;
        public static final int NESTED_TYPE_FIELD_NUMBER = 3;
        public static final int OPTIONS_FIELD_NUMBER = 7;
        public static Parser<DescriptorProto> PARSER = new AbstractParser<DescriptorProto>() {
            public DescriptorProto parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new DescriptorProto(input, extensionRegistry);
            }
        };
        private static final DescriptorProto defaultInstance = new DescriptorProto(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        /* access modifiers changed from: private */
        public List<EnumDescriptorProto> enumType_;
        /* access modifiers changed from: private */
        public List<ExtensionRange> extensionRange_;
        /* access modifiers changed from: private */
        public List<FieldDescriptorProto> extension_;
        /* access modifiers changed from: private */
        public List<FieldDescriptorProto> field_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public Object name_;
        /* access modifiers changed from: private */
        public List<DescriptorProto> nestedType_;
        /* access modifiers changed from: private */
        public MessageOptions options_;
        private final UnknownFieldSet unknownFields;

        public interface ExtensionRangeOrBuilder extends MessageOrBuilder {
            int getEnd();

            int getStart();

            boolean hasEnd();

            boolean hasStart();
        }

        private DescriptorProto(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private DescriptorProto(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static DescriptorProto getDefaultInstance() {
            return defaultInstance;
        }

        public DescriptorProto getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        private DescriptorProto(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        this.bitField0_ |= 1;
                        this.name_ = input.readBytes();
                    } else if (tag == 18) {
                        if ((mutable_bitField0_ & 2) != 2) {
                            this.field_ = new ArrayList();
                            mutable_bitField0_ |= 2;
                        }
                        this.field_.add(input.readMessage(FieldDescriptorProto.PARSER, extensionRegistry));
                    } else if (tag == 26) {
                        if ((mutable_bitField0_ & 8) != 8) {
                            this.nestedType_ = new ArrayList();
                            mutable_bitField0_ |= 8;
                        }
                        this.nestedType_.add(input.readMessage(PARSER, extensionRegistry));
                    } else if (tag == 34) {
                        if ((mutable_bitField0_ & 16) != 16) {
                            this.enumType_ = new ArrayList();
                            mutable_bitField0_ |= 16;
                        }
                        this.enumType_.add(input.readMessage(EnumDescriptorProto.PARSER, extensionRegistry));
                    } else if (tag == 42) {
                        if ((mutable_bitField0_ & 32) != 32) {
                            this.extensionRange_ = new ArrayList();
                            mutable_bitField0_ |= 32;
                        }
                        this.extensionRange_.add(input.readMessage(ExtensionRange.PARSER, extensionRegistry));
                    } else if (tag == 50) {
                        if ((mutable_bitField0_ & 4) != 4) {
                            this.extension_ = new ArrayList();
                            mutable_bitField0_ |= 4;
                        }
                        this.extension_.add(input.readMessage(FieldDescriptorProto.PARSER, extensionRegistry));
                    } else if (tag == 58) {
                        MessageOptions.Builder subBuilder = (this.bitField0_ & 2) == 2 ? this.options_.toBuilder() : null;
                        this.options_ = (MessageOptions) input.readMessage(MessageOptions.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                            subBuilder.mergeFrom(this.options_);
                            this.options_ = subBuilder.buildPartial();
                        }
                        this.bitField0_ |= 2;
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 2) == 2) {
                        this.field_ = Collections.unmodifiableList(this.field_);
                    }
                    if ((mutable_bitField0_ & 8) == 8) {
                        this.nestedType_ = Collections.unmodifiableList(this.nestedType_);
                    }
                    if ((mutable_bitField0_ & 16) == 16) {
                        this.enumType_ = Collections.unmodifiableList(this.enumType_);
                    }
                    if ((mutable_bitField0_ & 32) == 32) {
                        this.extensionRange_ = Collections.unmodifiableList(this.extensionRange_);
                    }
                    if ((mutable_bitField0_ & 4) == 4) {
                        this.extension_ = Collections.unmodifiableList(this.extension_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 2) == 2) {
                this.field_ = Collections.unmodifiableList(this.field_);
            }
            if ((mutable_bitField0_ & 8) == 8) {
                this.nestedType_ = Collections.unmodifiableList(this.nestedType_);
            }
            if ((mutable_bitField0_ & 16) == 16) {
                this.enumType_ = Collections.unmodifiableList(this.enumType_);
            }
            if ((mutable_bitField0_ & 32) == 32) {
                this.extensionRange_ = Collections.unmodifiableList(this.extensionRange_);
            }
            if ((mutable_bitField0_ & 4) == 4) {
                this.extension_ = Collections.unmodifiableList(this.extension_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(DescriptorProto.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<DescriptorProto> getParserForType() {
            return PARSER;
        }

        public static final class ExtensionRange extends GeneratedMessage implements ExtensionRangeOrBuilder {
            public static final int END_FIELD_NUMBER = 2;
            public static Parser<ExtensionRange> PARSER = new AbstractParser<ExtensionRange>() {
                public ExtensionRange parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ExtensionRange(input, extensionRegistry);
                }
            };
            public static final int START_FIELD_NUMBER = 1;
            private static final ExtensionRange defaultInstance = new ExtensionRange(true);
            private static final long serialVersionUID = 0;
            /* access modifiers changed from: private */
            public int bitField0_;
            /* access modifiers changed from: private */
            public int end_;
            private byte memoizedIsInitialized;
            private int memoizedSerializedSize;
            /* access modifiers changed from: private */
            public int start_;
            private final UnknownFieldSet unknownFields;

            private ExtensionRange(GeneratedMessage.Builder<?> builder) {
                super(builder);
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = builder.getUnknownFields();
            }

            private ExtensionRange(boolean noInit) {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = UnknownFieldSet.getDefaultInstance();
            }

            public static ExtensionRange getDefaultInstance() {
                return defaultInstance;
            }

            public ExtensionRange getDefaultInstanceForType() {
                return defaultInstance;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            /* Debug info: failed to restart local var, previous not found, register: 5 */
            private ExtensionRange(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                initFields();
                UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
                boolean done = false;
                while (!done) {
                    try {
                        int tag = input.readTag();
                        if (tag == 0) {
                            done = true;
                        } else if (tag == 8) {
                            this.bitField0_ |= 1;
                            this.start_ = input.readInt32();
                        } else if (tag == 16) {
                            this.bitField0_ |= 2;
                            this.end_ = input.readInt32();
                        } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                            done = true;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                    } catch (Throwable th) {
                        this.unknownFields = unknownFields2.build();
                        makeExtensionsImmutable();
                        throw th;
                    }
                }
                this.unknownFields = unknownFields2.build();
                makeExtensionsImmutable();
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable.ensureFieldAccessorsInitialized(ExtensionRange.class, Builder.class);
            }

            static {
                defaultInstance.initFields();
            }

            public Parser<ExtensionRange> getParserForType() {
                return PARSER;
            }

            public boolean hasStart() {
                return (this.bitField0_ & 1) == 1;
            }

            public int getStart() {
                return this.start_;
            }

            public boolean hasEnd() {
                return (this.bitField0_ & 2) == 2;
            }

            public int getEnd() {
                return this.end_;
            }

            private void initFields() {
                this.start_ = 0;
                this.end_ = 0;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized != -1) {
                    return isInitialized == 1;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                getSerializedSize();
                if ((this.bitField0_ & 1) == 1) {
                    output.writeInt32(1, this.start_);
                }
                if ((this.bitField0_ & 2) == 2) {
                    output.writeInt32(2, this.end_);
                }
                getUnknownFields().writeTo(output);
            }

            public int getSerializedSize() {
                int size = this.memoizedSerializedSize;
                if (size != -1) {
                    return size;
                }
                int size2 = 0;
                if ((this.bitField0_ & 1) == 1) {
                    size2 = 0 + CodedOutputStream.computeInt32Size(1, this.start_);
                }
                if ((this.bitField0_ & 2) == 2) {
                    size2 += CodedOutputStream.computeInt32Size(2, this.end_);
                }
                int size3 = size2 + getUnknownFields().getSerializedSize();
                this.memoizedSerializedSize = size3;
                return size3;
            }

            /* access modifiers changed from: protected */
            public Object writeReplace() throws ObjectStreamException {
                return super.writeReplace();
            }

            public static ExtensionRange parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static ExtensionRange parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static ExtensionRange parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static ExtensionRange parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static ExtensionRange parseFrom(InputStream input) throws IOException {
                return PARSER.parseFrom(input);
            }

            public static ExtensionRange parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseFrom(input, extensionRegistry);
            }

            public static ExtensionRange parseDelimitedFrom(InputStream input) throws IOException {
                return PARSER.parseDelimitedFrom(input);
            }

            public static ExtensionRange parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseDelimitedFrom(input, extensionRegistry);
            }

            public static ExtensionRange parseFrom(CodedInputStream input) throws IOException {
                return PARSER.parseFrom(input);
            }

            public static ExtensionRange parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseFrom(input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return Builder.create();
            }

            public Builder newBuilderForType() {
                return newBuilder();
            }

            public static Builder newBuilder(ExtensionRange prototype) {
                return newBuilder().mergeFrom(prototype);
            }

            public Builder toBuilder() {
                return newBuilder(this);
            }

            /* access modifiers changed from: protected */
            public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
                return new Builder(parent);
            }

            public static final class Builder extends GeneratedMessage.Builder<Builder> implements ExtensionRangeOrBuilder {
                private int bitField0_;
                private int end_;
                private int start_;

                public static final Descriptors.Descriptor getDescriptor() {
                    return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
                }

                /* access modifiers changed from: protected */
                public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                    return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable.ensureFieldAccessorsInitialized(ExtensionRange.class, Builder.class);
                }

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessage.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                private void maybeForceBuilderInitialization() {
                    boolean z = GeneratedMessage.alwaysUseFieldBuilders;
                }

                /* access modifiers changed from: private */
                public static Builder create() {
                    return new Builder();
                }

                public Builder clear() {
                    super.clear();
                    this.start_ = 0;
                    this.bitField0_ &= -2;
                    this.end_ = 0;
                    this.bitField0_ &= -3;
                    return this;
                }

                public Builder clone() {
                    return create().mergeFrom(buildPartial());
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
                }

                public ExtensionRange getDefaultInstanceForType() {
                    return ExtensionRange.getDefaultInstance();
                }

                public ExtensionRange build() {
                    ExtensionRange result = buildPartial();
                    if (result.isInitialized()) {
                        return result;
                    }
                    throw newUninitializedMessageException(result);
                }

                public ExtensionRange buildPartial() {
                    ExtensionRange result = new ExtensionRange((GeneratedMessage.Builder) this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 1) == 1) {
                        to_bitField0_ = 0 | 1;
                    }
                    int unused = result.start_ = this.start_;
                    if ((from_bitField0_ & 2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    int unused2 = result.end_ = this.end_;
                    int unused3 = result.bitField0_ = to_bitField0_;
                    onBuilt();
                    return result;
                }

                public Builder mergeFrom(Message other) {
                    if (other instanceof ExtensionRange) {
                        return mergeFrom((ExtensionRange) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ExtensionRange other) {
                    if (other == ExtensionRange.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasStart()) {
                        setStart(other.getStart());
                    }
                    if (other.hasEnd()) {
                        setEnd(other.getEnd());
                    }
                    mergeUnknownFields(other.getUnknownFields());
                    return this;
                }

                public final boolean isInitialized() {
                    return true;
                }

                /* Debug info: failed to restart local var, previous not found, register: 3 */
                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    try {
                        ExtensionRange parsedMessage = ExtensionRange.PARSER.parsePartialFrom(input, extensionRegistry);
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        ExtensionRange parsedMessage2 = (ExtensionRange) e.getUnfinishedMessage();
                        throw e;
                    } catch (Throwable th) {
                        if (0 != 0) {
                            mergeFrom((ExtensionRange) null);
                        }
                        throw th;
                    }
                }

                public boolean hasStart() {
                    return (this.bitField0_ & 1) == 1;
                }

                public int getStart() {
                    return this.start_;
                }

                public Builder setStart(int value) {
                    this.bitField0_ |= 1;
                    this.start_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearStart() {
                    this.bitField0_ &= -2;
                    this.start_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasEnd() {
                    return (this.bitField0_ & 2) == 2;
                }

                public int getEnd() {
                    return this.end_;
                }

                public Builder setEnd(int value) {
                    this.bitField0_ |= 2;
                    this.end_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearEnd() {
                    this.bitField0_ &= -3;
                    this.end_ = 0;
                    onChanged();
                    return this;
                }
            }
        }

        public boolean hasName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public List<FieldDescriptorProto> getFieldList() {
            return this.field_;
        }

        public List<? extends FieldDescriptorProtoOrBuilder> getFieldOrBuilderList() {
            return this.field_;
        }

        public int getFieldCount() {
            return this.field_.size();
        }

        public FieldDescriptorProto getField(int index) {
            return this.field_.get(index);
        }

        public FieldDescriptorProtoOrBuilder getFieldOrBuilder(int index) {
            return this.field_.get(index);
        }

        public List<FieldDescriptorProto> getExtensionList() {
            return this.extension_;
        }

        public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
            return this.extension_;
        }

        public int getExtensionCount() {
            return this.extension_.size();
        }

        public FieldDescriptorProto getExtension(int index) {
            return this.extension_.get(index);
        }

        public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int index) {
            return this.extension_.get(index);
        }

        public List<DescriptorProto> getNestedTypeList() {
            return this.nestedType_;
        }

        public List<? extends DescriptorProtoOrBuilder> getNestedTypeOrBuilderList() {
            return this.nestedType_;
        }

        public int getNestedTypeCount() {
            return this.nestedType_.size();
        }

        public DescriptorProto getNestedType(int index) {
            return this.nestedType_.get(index);
        }

        public DescriptorProtoOrBuilder getNestedTypeOrBuilder(int index) {
            return this.nestedType_.get(index);
        }

        public List<EnumDescriptorProto> getEnumTypeList() {
            return this.enumType_;
        }

        public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
            return this.enumType_;
        }

        public int getEnumTypeCount() {
            return this.enumType_.size();
        }

        public EnumDescriptorProto getEnumType(int index) {
            return this.enumType_.get(index);
        }

        public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int index) {
            return this.enumType_.get(index);
        }

        public List<ExtensionRange> getExtensionRangeList() {
            return this.extensionRange_;
        }

        public List<? extends ExtensionRangeOrBuilder> getExtensionRangeOrBuilderList() {
            return this.extensionRange_;
        }

        public int getExtensionRangeCount() {
            return this.extensionRange_.size();
        }

        public ExtensionRange getExtensionRange(int index) {
            return this.extensionRange_.get(index);
        }

        public ExtensionRangeOrBuilder getExtensionRangeOrBuilder(int index) {
            return this.extensionRange_.get(index);
        }

        public boolean hasOptions() {
            return (this.bitField0_ & 2) == 2;
        }

        public MessageOptions getOptions() {
            return this.options_;
        }

        public MessageOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }

        private void initFields() {
            this.name_ = "";
            this.field_ = Collections.emptyList();
            this.extension_ = Collections.emptyList();
            this.nestedType_ = Collections.emptyList();
            this.enumType_ = Collections.emptyList();
            this.extensionRange_ = Collections.emptyList();
            this.options_ = MessageOptions.getDefaultInstance();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getFieldCount(); i++) {
                if (!getField(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i2 = 0; i2 < getExtensionCount(); i2++) {
                if (!getExtension(i2).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i3 = 0; i3 < getNestedTypeCount(); i3++) {
                if (!getNestedType(i3).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i4 = 0; i4 < getEnumTypeCount(); i4++) {
                if (!getEnumType(i4).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (hasOptions() == 0 || getOptions().isInitialized()) {
                this.memoizedIsInitialized = 1;
                return true;
            }
            this.memoizedIsInitialized = 0;
            return false;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getNameBytes());
            }
            for (int i = 0; i < this.field_.size(); i++) {
                output.writeMessage(2, this.field_.get(i));
            }
            for (int i2 = 0; i2 < this.nestedType_.size(); i2++) {
                output.writeMessage(3, this.nestedType_.get(i2));
            }
            for (int i3 = 0; i3 < this.enumType_.size(); i3++) {
                output.writeMessage(4, this.enumType_.get(i3));
            }
            for (int i4 = 0; i4 < this.extensionRange_.size(); i4++) {
                output.writeMessage(5, this.extensionRange_.get(i4));
            }
            for (int i5 = 0; i5 < this.extension_.size(); i5++) {
                output.writeMessage(6, this.extension_.get(i5));
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeMessage(7, this.options_);
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getNameBytes());
            }
            for (int i = 0; i < this.field_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(2, this.field_.get(i));
            }
            for (int i2 = 0; i2 < this.nestedType_.size(); i2++) {
                size2 += CodedOutputStream.computeMessageSize(3, this.nestedType_.get(i2));
            }
            for (int i3 = 0; i3 < this.enumType_.size(); i3++) {
                size2 += CodedOutputStream.computeMessageSize(4, this.enumType_.get(i3));
            }
            for (int i4 = 0; i4 < this.extensionRange_.size(); i4++) {
                size2 += CodedOutputStream.computeMessageSize(5, this.extensionRange_.get(i4));
            }
            for (int i5 = 0; i5 < this.extension_.size(); i5++) {
                size2 += CodedOutputStream.computeMessageSize(6, this.extension_.get(i5));
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeMessageSize(7, this.options_);
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static DescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DescriptorProto parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static DescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static DescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static DescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static DescriptorProto parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static DescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(DescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements DescriptorProtoOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> enumTypeBuilder_;
            private List<EnumDescriptorProto> enumType_;
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> extensionBuilder_;
            private RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> extensionRangeBuilder_;
            private List<ExtensionRange> extensionRange_;
            private List<FieldDescriptorProto> extension_;
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> fieldBuilder_;
            private List<FieldDescriptorProto> field_;
            private Object name_;
            private RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> nestedTypeBuilder_;
            private List<DescriptorProto> nestedType_;
            private SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> optionsBuilder_;
            private MessageOptions options_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(DescriptorProto.class, Builder.class);
            }

            private Builder() {
                this.name_ = "";
                this.field_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.nestedType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.extensionRange_ = Collections.emptyList();
                this.options_ = MessageOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.field_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.nestedType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.extensionRange_ = Collections.emptyList();
                this.options_ = MessageOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getFieldFieldBuilder();
                    getExtensionFieldBuilder();
                    getNestedTypeFieldBuilder();
                    getEnumTypeFieldBuilder();
                    getExtensionRangeFieldBuilder();
                    getOptionsFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= -2;
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.field_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                } else {
                    repeatedFieldBuilder.clear();
                }
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder2 = this.extensionBuilder_;
                if (repeatedFieldBuilder2 == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= -5;
                } else {
                    repeatedFieldBuilder2.clear();
                }
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder3 = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder3 == null) {
                    this.nestedType_ = Collections.emptyList();
                    this.bitField0_ &= -9;
                } else {
                    repeatedFieldBuilder3.clear();
                }
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder4 = this.enumTypeBuilder_;
                if (repeatedFieldBuilder4 == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= -17;
                } else {
                    repeatedFieldBuilder4.clear();
                }
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder5 = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder5 == null) {
                    this.extensionRange_ = Collections.emptyList();
                    this.bitField0_ &= -33;
                } else {
                    repeatedFieldBuilder5.clear();
                }
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = MessageOptions.getDefaultInstance();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -65;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor;
            }

            public DescriptorProto getDefaultInstanceForType() {
                return DescriptorProto.getDefaultInstance();
            }

            public DescriptorProto build() {
                DescriptorProto result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public DescriptorProto buildPartial() {
                DescriptorProto result = new DescriptorProto((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.name_ = this.name_;
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 2) == 2) {
                        this.field_ = Collections.unmodifiableList(this.field_);
                        this.bitField0_ &= -3;
                    }
                    List unused2 = result.field_ = this.field_;
                } else {
                    List unused3 = result.field_ = repeatedFieldBuilder.build();
                }
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder2 = this.extensionBuilder_;
                if (repeatedFieldBuilder2 == null) {
                    if ((this.bitField0_ & 4) == 4) {
                        this.extension_ = Collections.unmodifiableList(this.extension_);
                        this.bitField0_ &= -5;
                    }
                    List unused4 = result.extension_ = this.extension_;
                } else {
                    List unused5 = result.extension_ = repeatedFieldBuilder2.build();
                }
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder3 = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder3 == null) {
                    if ((this.bitField0_ & 8) == 8) {
                        this.nestedType_ = Collections.unmodifiableList(this.nestedType_);
                        this.bitField0_ &= -9;
                    }
                    List unused6 = result.nestedType_ = this.nestedType_;
                } else {
                    List unused7 = result.nestedType_ = repeatedFieldBuilder3.build();
                }
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder4 = this.enumTypeBuilder_;
                if (repeatedFieldBuilder4 == null) {
                    if ((this.bitField0_ & 16) == 16) {
                        this.enumType_ = Collections.unmodifiableList(this.enumType_);
                        this.bitField0_ &= -17;
                    }
                    List unused8 = result.enumType_ = this.enumType_;
                } else {
                    List unused9 = result.enumType_ = repeatedFieldBuilder4.build();
                }
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder5 = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder5 == null) {
                    if ((this.bitField0_ & 32) == 32) {
                        this.extensionRange_ = Collections.unmodifiableList(this.extensionRange_);
                        this.bitField0_ &= -33;
                    }
                    List unused10 = result.extensionRange_ = this.extensionRange_;
                } else {
                    List unused11 = result.extensionRange_ = repeatedFieldBuilder5.build();
                }
                if ((from_bitField0_ & 64) == 64) {
                    to_bitField0_ |= 2;
                }
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    MessageOptions unused12 = result.options_ = this.options_;
                } else {
                    MessageOptions unused13 = result.options_ = singleFieldBuilder.build();
                }
                int unused14 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof DescriptorProto) {
                    return mergeFrom((DescriptorProto) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(DescriptorProto other) {
                if (other == DescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 1;
                    this.name_ = other.name_;
                    onChanged();
                }
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = null;
                if (this.fieldBuilder_ == null) {
                    if (!other.field_.isEmpty()) {
                        if (this.field_.isEmpty()) {
                            this.field_ = other.field_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureFieldIsMutable();
                            this.field_.addAll(other.field_);
                        }
                        onChanged();
                    }
                } else if (!other.field_.isEmpty()) {
                    if (this.fieldBuilder_.isEmpty()) {
                        this.fieldBuilder_.dispose();
                        this.fieldBuilder_ = null;
                        this.field_ = other.field_;
                        this.bitField0_ &= -3;
                        this.fieldBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? getFieldFieldBuilder() : null;
                    } else {
                        this.fieldBuilder_.addAllMessages(other.field_);
                    }
                }
                if (this.extensionBuilder_ == null) {
                    if (!other.extension_.isEmpty()) {
                        if (this.extension_.isEmpty()) {
                            this.extension_ = other.extension_;
                            this.bitField0_ &= -5;
                        } else {
                            ensureExtensionIsMutable();
                            this.extension_.addAll(other.extension_);
                        }
                        onChanged();
                    }
                } else if (!other.extension_.isEmpty()) {
                    if (this.extensionBuilder_.isEmpty()) {
                        this.extensionBuilder_.dispose();
                        this.extensionBuilder_ = null;
                        this.extension_ = other.extension_;
                        this.bitField0_ &= -5;
                        this.extensionBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? getExtensionFieldBuilder() : null;
                    } else {
                        this.extensionBuilder_.addAllMessages(other.extension_);
                    }
                }
                if (this.nestedTypeBuilder_ == null) {
                    if (!other.nestedType_.isEmpty()) {
                        if (this.nestedType_.isEmpty()) {
                            this.nestedType_ = other.nestedType_;
                            this.bitField0_ &= -9;
                        } else {
                            ensureNestedTypeIsMutable();
                            this.nestedType_.addAll(other.nestedType_);
                        }
                        onChanged();
                    }
                } else if (!other.nestedType_.isEmpty()) {
                    if (this.nestedTypeBuilder_.isEmpty()) {
                        this.nestedTypeBuilder_.dispose();
                        this.nestedTypeBuilder_ = null;
                        this.nestedType_ = other.nestedType_;
                        this.bitField0_ &= -9;
                        this.nestedTypeBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? getNestedTypeFieldBuilder() : null;
                    } else {
                        this.nestedTypeBuilder_.addAllMessages(other.nestedType_);
                    }
                }
                if (this.enumTypeBuilder_ == null) {
                    if (!other.enumType_.isEmpty()) {
                        if (this.enumType_.isEmpty()) {
                            this.enumType_ = other.enumType_;
                            this.bitField0_ &= -17;
                        } else {
                            ensureEnumTypeIsMutable();
                            this.enumType_.addAll(other.enumType_);
                        }
                        onChanged();
                    }
                } else if (!other.enumType_.isEmpty()) {
                    if (this.enumTypeBuilder_.isEmpty()) {
                        this.enumTypeBuilder_.dispose();
                        this.enumTypeBuilder_ = null;
                        this.enumType_ = other.enumType_;
                        this.bitField0_ &= -17;
                        this.enumTypeBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? getEnumTypeFieldBuilder() : null;
                    } else {
                        this.enumTypeBuilder_.addAllMessages(other.enumType_);
                    }
                }
                if (this.extensionRangeBuilder_ == null) {
                    if (!other.extensionRange_.isEmpty()) {
                        if (this.extensionRange_.isEmpty()) {
                            this.extensionRange_ = other.extensionRange_;
                            this.bitField0_ &= -33;
                        } else {
                            ensureExtensionRangeIsMutable();
                            this.extensionRange_.addAll(other.extensionRange_);
                        }
                        onChanged();
                    }
                } else if (!other.extensionRange_.isEmpty()) {
                    if (this.extensionRangeBuilder_.isEmpty()) {
                        this.extensionRangeBuilder_.dispose();
                        this.extensionRangeBuilder_ = null;
                        this.extensionRange_ = other.extensionRange_;
                        this.bitField0_ &= -33;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getExtensionRangeFieldBuilder();
                        }
                        this.extensionRangeBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.extensionRangeBuilder_.addAllMessages(other.extensionRange_);
                    }
                }
                if (other.hasOptions()) {
                    mergeOptions(other.getOptions());
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getFieldCount(); i++) {
                    if (!getField(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i2 = 0; i2 < getExtensionCount(); i2++) {
                    if (!getExtension(i2).isInitialized()) {
                        return false;
                    }
                }
                for (int i3 = 0; i3 < getNestedTypeCount(); i3++) {
                    if (!getNestedType(i3).isInitialized()) {
                        return false;
                    }
                }
                for (int i4 = 0; i4 < getEnumTypeCount(); i4++) {
                    if (!getEnumType(i4).isInitialized()) {
                        return false;
                    }
                }
                if (hasOptions() == 0 || getOptions().isInitialized()) {
                    return true;
                }
                return false;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    DescriptorProto parsedMessage = DescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    DescriptorProto parsedMessage2 = (DescriptorProto) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((DescriptorProto) null);
                    }
                    throw th;
                }
            }

            public boolean hasName() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearName() {
                this.bitField0_ &= -2;
                this.name_ = DescriptorProto.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            private void ensureFieldIsMutable() {
                if ((this.bitField0_ & 2) != 2) {
                    this.field_ = new ArrayList(this.field_);
                    this.bitField0_ |= 2;
                }
            }

            public List<FieldDescriptorProto> getFieldList() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.field_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getFieldCount() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.field_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public FieldDescriptorProto getField(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.field_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setField(int index, FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureFieldIsMutable();
                    this.field_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setField(int index, FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFieldIsMutable();
                    this.field_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addField(FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureFieldIsMutable();
                    this.field_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addField(int index, FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureFieldIsMutable();
                    this.field_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addField(FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFieldIsMutable();
                    this.field_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addField(int index, FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFieldIsMutable();
                    this.field_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllField(Iterable<? extends FieldDescriptorProto> values) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFieldIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.field_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearField() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.field_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeField(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureFieldIsMutable();
                    this.field_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public FieldDescriptorProto.Builder getFieldBuilder(int index) {
                return getFieldFieldBuilder().getBuilder(index);
            }

            public FieldDescriptorProtoOrBuilder getFieldOrBuilder(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.field_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends FieldDescriptorProtoOrBuilder> getFieldOrBuilderList() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.fieldBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.field_);
            }

            public FieldDescriptorProto.Builder addFieldBuilder() {
                return getFieldFieldBuilder().addBuilder(FieldDescriptorProto.getDefaultInstance());
            }

            public FieldDescriptorProto.Builder addFieldBuilder(int index) {
                return getFieldFieldBuilder().addBuilder(index, FieldDescriptorProto.getDefaultInstance());
            }

            public List<FieldDescriptorProto.Builder> getFieldBuilderList() {
                return getFieldFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> getFieldFieldBuilder() {
                if (this.fieldBuilder_ == null) {
                    this.fieldBuilder_ = new RepeatedFieldBuilder<>(this.field_, (this.bitField0_ & 2) == 2, getParentForChildren(), isClean());
                    this.field_ = null;
                }
                return this.fieldBuilder_;
            }

            private void ensureExtensionIsMutable() {
                if ((this.bitField0_ & 4) != 4) {
                    this.extension_ = new ArrayList(this.extension_);
                    this.bitField0_ |= 4;
                }
            }

            public List<FieldDescriptorProto> getExtensionList() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.extension_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getExtensionCount() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extension_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public FieldDescriptorProto getExtension(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extension_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setExtension(int index, FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureExtensionIsMutable();
                    this.extension_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setExtension(int index, FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addExtension(FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addExtension(int index, FieldDescriptorProto value) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addExtension(FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addExtension(int index, FieldDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllExtension(Iterable<? extends FieldDescriptorProto> values) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.extension_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearExtension() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= -5;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeExtension(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionIsMutable();
                    this.extension_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public FieldDescriptorProto.Builder getExtensionBuilder(int index) {
                return getExtensionFieldBuilder().getBuilder(index);
            }

            public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int index) {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extension_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
                RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> repeatedFieldBuilder = this.extensionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.extension_);
            }

            public FieldDescriptorProto.Builder addExtensionBuilder() {
                return getExtensionFieldBuilder().addBuilder(FieldDescriptorProto.getDefaultInstance());
            }

            public FieldDescriptorProto.Builder addExtensionBuilder(int index) {
                return getExtensionFieldBuilder().addBuilder(index, FieldDescriptorProto.getDefaultInstance());
            }

            public List<FieldDescriptorProto.Builder> getExtensionBuilderList() {
                return getExtensionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> getExtensionFieldBuilder() {
                if (this.extensionBuilder_ == null) {
                    this.extensionBuilder_ = new RepeatedFieldBuilder<>(this.extension_, (this.bitField0_ & 4) == 4, getParentForChildren(), isClean());
                    this.extension_ = null;
                }
                return this.extensionBuilder_;
            }

            private void ensureNestedTypeIsMutable() {
                if ((this.bitField0_ & 8) != 8) {
                    this.nestedType_ = new ArrayList(this.nestedType_);
                    this.bitField0_ |= 8;
                }
            }

            public List<DescriptorProto> getNestedTypeList() {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.nestedType_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getNestedTypeCount() {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.nestedType_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public DescriptorProto getNestedType(int index) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.nestedType_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setNestedType(int index, DescriptorProto value) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureNestedTypeIsMutable();
                    this.nestedType_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setNestedType(int index, Builder builderForValue) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNestedTypeIsMutable();
                    this.nestedType_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addNestedType(DescriptorProto value) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureNestedTypeIsMutable();
                    this.nestedType_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addNestedType(int index, DescriptorProto value) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureNestedTypeIsMutable();
                    this.nestedType_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addNestedType(Builder builderForValue) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNestedTypeIsMutable();
                    this.nestedType_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addNestedType(int index, Builder builderForValue) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNestedTypeIsMutable();
                    this.nestedType_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllNestedType(Iterable<? extends DescriptorProto> values) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNestedTypeIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.nestedType_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearNestedType() {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.nestedType_ = Collections.emptyList();
                    this.bitField0_ &= -9;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeNestedType(int index) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNestedTypeIsMutable();
                    this.nestedType_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public Builder getNestedTypeBuilder(int index) {
                return getNestedTypeFieldBuilder().getBuilder(index);
            }

            public DescriptorProtoOrBuilder getNestedTypeOrBuilder(int index) {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.nestedType_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends DescriptorProtoOrBuilder> getNestedTypeOrBuilderList() {
                RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> repeatedFieldBuilder = this.nestedTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.nestedType_);
            }

            public Builder addNestedTypeBuilder() {
                return getNestedTypeFieldBuilder().addBuilder(DescriptorProto.getDefaultInstance());
            }

            public Builder addNestedTypeBuilder(int index) {
                return getNestedTypeFieldBuilder().addBuilder(index, DescriptorProto.getDefaultInstance());
            }

            public List<Builder> getNestedTypeBuilderList() {
                return getNestedTypeFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> getNestedTypeFieldBuilder() {
                if (this.nestedTypeBuilder_ == null) {
                    this.nestedTypeBuilder_ = new RepeatedFieldBuilder<>(this.nestedType_, (this.bitField0_ & 8) == 8, getParentForChildren(), isClean());
                    this.nestedType_ = null;
                }
                return this.nestedTypeBuilder_;
            }

            private void ensureEnumTypeIsMutable() {
                if ((this.bitField0_ & 16) != 16) {
                    this.enumType_ = new ArrayList(this.enumType_);
                    this.bitField0_ |= 16;
                }
            }

            public List<EnumDescriptorProto> getEnumTypeList() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.enumType_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getEnumTypeCount() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.enumType_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public EnumDescriptorProto getEnumType(int index) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.enumType_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setEnumType(int index, EnumDescriptorProto value) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setEnumType(int index, EnumDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addEnumType(EnumDescriptorProto value) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addEnumType(int index, EnumDescriptorProto value) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addEnumType(EnumDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addEnumType(int index, EnumDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllEnumType(Iterable<? extends EnumDescriptorProto> values) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.enumType_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearEnumType() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= -17;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeEnumType(int index) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureEnumTypeIsMutable();
                    this.enumType_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public EnumDescriptorProto.Builder getEnumTypeBuilder(int index) {
                return getEnumTypeFieldBuilder().getBuilder(index);
            }

            public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int index) {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.enumType_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
                RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> repeatedFieldBuilder = this.enumTypeBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.enumType_);
            }

            public EnumDescriptorProto.Builder addEnumTypeBuilder() {
                return getEnumTypeFieldBuilder().addBuilder(EnumDescriptorProto.getDefaultInstance());
            }

            public EnumDescriptorProto.Builder addEnumTypeBuilder(int index) {
                return getEnumTypeFieldBuilder().addBuilder(index, EnumDescriptorProto.getDefaultInstance());
            }

            public List<EnumDescriptorProto.Builder> getEnumTypeBuilderList() {
                return getEnumTypeFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> getEnumTypeFieldBuilder() {
                if (this.enumTypeBuilder_ == null) {
                    this.enumTypeBuilder_ = new RepeatedFieldBuilder<>(this.enumType_, (this.bitField0_ & 16) == 16, getParentForChildren(), isClean());
                    this.enumType_ = null;
                }
                return this.enumTypeBuilder_;
            }

            private void ensureExtensionRangeIsMutable() {
                if ((this.bitField0_ & 32) != 32) {
                    this.extensionRange_ = new ArrayList(this.extensionRange_);
                    this.bitField0_ |= 32;
                }
            }

            public List<ExtensionRange> getExtensionRangeList() {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.extensionRange_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getExtensionRangeCount() {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extensionRange_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public ExtensionRange getExtensionRange(int index) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extensionRange_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setExtensionRange(int index, ExtensionRange value) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureExtensionRangeIsMutable();
                    this.extensionRange_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setExtensionRange(int index, ExtensionRange.Builder builderForValue) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionRangeIsMutable();
                    this.extensionRange_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addExtensionRange(ExtensionRange value) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addExtensionRange(int index, ExtensionRange value) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addExtensionRange(ExtensionRange.Builder builderForValue) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addExtensionRange(int index, ExtensionRange.Builder builderForValue) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllExtensionRange(Iterable<? extends ExtensionRange> values) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionRangeIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.extensionRange_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearExtensionRange() {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.extensionRange_ = Collections.emptyList();
                    this.bitField0_ &= -33;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeExtensionRange(int index) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureExtensionRangeIsMutable();
                    this.extensionRange_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public ExtensionRange.Builder getExtensionRangeBuilder(int index) {
                return getExtensionRangeFieldBuilder().getBuilder(index);
            }

            public ExtensionRangeOrBuilder getExtensionRangeOrBuilder(int index) {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.extensionRange_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends ExtensionRangeOrBuilder> getExtensionRangeOrBuilderList() {
                RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> repeatedFieldBuilder = this.extensionRangeBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.extensionRange_);
            }

            public ExtensionRange.Builder addExtensionRangeBuilder() {
                return getExtensionRangeFieldBuilder().addBuilder(ExtensionRange.getDefaultInstance());
            }

            public ExtensionRange.Builder addExtensionRangeBuilder(int index) {
                return getExtensionRangeFieldBuilder().addBuilder(index, ExtensionRange.getDefaultInstance());
            }

            public List<ExtensionRange.Builder> getExtensionRangeBuilderList() {
                return getExtensionRangeFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> getExtensionRangeFieldBuilder() {
                if (this.extensionRangeBuilder_ == null) {
                    this.extensionRangeBuilder_ = new RepeatedFieldBuilder<>(this.extensionRange_, (this.bitField0_ & 32) == 32, getParentForChildren(), isClean());
                    this.extensionRange_ = null;
                }
                return this.extensionRangeBuilder_;
            }

            public boolean hasOptions() {
                return (this.bitField0_ & 64) == 64;
            }

            public MessageOptions getOptions() {
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    return this.options_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setOptions(MessageOptions value) {
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.options_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 64;
                return this;
            }

            public Builder setOptions(MessageOptions.Builder builderForValue) {
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 64;
                return this;
            }

            public Builder mergeOptions(MessageOptions value) {
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 64) != 64 || this.options_ == MessageOptions.getDefaultInstance()) {
                        this.options_ = value;
                    } else {
                        this.options_ = MessageOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 64;
                return this;
            }

            public Builder clearOptions() {
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = MessageOptions.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -65;
                return this;
            }

            public MessageOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 64;
                onChanged();
                return getOptionsFieldBuilder().getBuilder();
            }

            public MessageOptionsOrBuilder getOptionsOrBuilder() {
                SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.options_;
            }

            private SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<>(this.options_, getParentForChildren(), isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }

    public static final class FieldDescriptorProto extends GeneratedMessage implements FieldDescriptorProtoOrBuilder {
        public static final int DEFAULT_VALUE_FIELD_NUMBER = 7;
        public static final int EXTENDEE_FIELD_NUMBER = 2;
        public static final int LABEL_FIELD_NUMBER = 4;
        public static final int NAME_FIELD_NUMBER = 1;
        public static final int NUMBER_FIELD_NUMBER = 3;
        public static final int OPTIONS_FIELD_NUMBER = 8;
        public static Parser<FieldDescriptorProto> PARSER = new AbstractParser<FieldDescriptorProto>() {
            public FieldDescriptorProto parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new FieldDescriptorProto(input, extensionRegistry);
            }
        };
        public static final int TYPE_FIELD_NUMBER = 5;
        public static final int TYPE_NAME_FIELD_NUMBER = 6;
        private static final FieldDescriptorProto defaultInstance = new FieldDescriptorProto(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        /* access modifiers changed from: private */
        public Object defaultValue_;
        /* access modifiers changed from: private */
        public Object extendee_;
        /* access modifiers changed from: private */
        public Label label_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public Object name_;
        /* access modifiers changed from: private */
        public int number_;
        /* access modifiers changed from: private */
        public FieldOptions options_;
        /* access modifiers changed from: private */
        public Object typeName_;
        /* access modifiers changed from: private */
        public Type type_;
        private final UnknownFieldSet unknownFields;

        private FieldDescriptorProto(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private FieldDescriptorProto(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static FieldDescriptorProto getDefaultInstance() {
            return defaultInstance;
        }

        public FieldDescriptorProto getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        private FieldDescriptorProto(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        this.bitField0_ |= 1;
                        this.name_ = input.readBytes();
                    } else if (tag == 18) {
                        this.bitField0_ |= 32;
                        this.extendee_ = input.readBytes();
                    } else if (tag == 24) {
                        this.bitField0_ |= 2;
                        this.number_ = input.readInt32();
                    } else if (tag == 32) {
                        int rawValue = input.readEnum();
                        Label value = Label.valueOf(rawValue);
                        if (value == null) {
                            unknownFields2.mergeVarintField(4, rawValue);
                        } else {
                            this.bitField0_ = 4 | this.bitField0_;
                            this.label_ = value;
                        }
                    } else if (tag == 40) {
                        int rawValue2 = input.readEnum();
                        Type value2 = Type.valueOf(rawValue2);
                        if (value2 == null) {
                            unknownFields2.mergeVarintField(5, rawValue2);
                        } else {
                            this.bitField0_ |= 8;
                            this.type_ = value2;
                        }
                    } else if (tag == 50) {
                        this.bitField0_ |= 16;
                        this.typeName_ = input.readBytes();
                    } else if (tag == 58) {
                        this.bitField0_ |= 64;
                        this.defaultValue_ = input.readBytes();
                    } else if (tag == 66) {
                        FieldOptions.Builder subBuilder = (this.bitField0_ & 128) == 128 ? this.options_.toBuilder() : null;
                        this.options_ = (FieldOptions) input.readMessage(FieldOptions.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                            subBuilder.mergeFrom(this.options_);
                            this.options_ = subBuilder.buildPartial();
                        }
                        this.bitField0_ |= 128;
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldDescriptorProto.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<FieldDescriptorProto> getParserForType() {
            return PARSER;
        }

        public enum Type implements ProtocolMessageEnum {
            TYPE_DOUBLE(0, 1),
            TYPE_FLOAT(1, 2),
            TYPE_INT64(2, 3),
            TYPE_UINT64(3, 4),
            TYPE_INT32(4, 5),
            TYPE_FIXED64(5, 6),
            TYPE_FIXED32(6, 7),
            TYPE_BOOL(7, 8),
            TYPE_STRING(8, 9),
            TYPE_GROUP(9, 10),
            TYPE_MESSAGE(10, 11),
            TYPE_BYTES(11, 12),
            TYPE_UINT32(12, 13),
            TYPE_ENUM(13, 14),
            TYPE_SFIXED32(14, 15),
            TYPE_SFIXED64(15, 16),
            TYPE_SINT32(16, 17),
            TYPE_SINT64(17, 18);
            
            public static final int TYPE_BOOL_VALUE = 8;
            public static final int TYPE_BYTES_VALUE = 12;
            public static final int TYPE_DOUBLE_VALUE = 1;
            public static final int TYPE_ENUM_VALUE = 14;
            public static final int TYPE_FIXED32_VALUE = 7;
            public static final int TYPE_FIXED64_VALUE = 6;
            public static final int TYPE_FLOAT_VALUE = 2;
            public static final int TYPE_GROUP_VALUE = 10;
            public static final int TYPE_INT32_VALUE = 5;
            public static final int TYPE_INT64_VALUE = 3;
            public static final int TYPE_MESSAGE_VALUE = 11;
            public static final int TYPE_SFIXED32_VALUE = 15;
            public static final int TYPE_SFIXED64_VALUE = 16;
            public static final int TYPE_SINT32_VALUE = 17;
            public static final int TYPE_SINT64_VALUE = 18;
            public static final int TYPE_STRING_VALUE = 9;
            public static final int TYPE_UINT32_VALUE = 13;
            public static final int TYPE_UINT64_VALUE = 4;
            private static final Type[] VALUES = null;
            private static Internal.EnumLiteMap<Type> internalValueMap;
            private final int index;
            private final int value;

            static {
                internalValueMap = new Internal.EnumLiteMap<Type>() {
                    public Type findValueByNumber(int number) {
                        return Type.valueOf(number);
                    }
                };
                VALUES = values();
            }

            public final int getNumber() {
                return this.value;
            }

            public static Type valueOf(int value2) {
                switch (value2) {
                    case 1:
                        return TYPE_DOUBLE;
                    case 2:
                        return TYPE_FLOAT;
                    case 3:
                        return TYPE_INT64;
                    case 4:
                        return TYPE_UINT64;
                    case 5:
                        return TYPE_INT32;
                    case 6:
                        return TYPE_FIXED64;
                    case 7:
                        return TYPE_FIXED32;
                    case 8:
                        return TYPE_BOOL;
                    case 9:
                        return TYPE_STRING;
                    case 10:
                        return TYPE_GROUP;
                    case 11:
                        return TYPE_MESSAGE;
                    case TYPE_BYTES_VALUE:
                        return TYPE_BYTES;
                    case TYPE_UINT32_VALUE:
                        return TYPE_UINT32;
                    case TYPE_ENUM_VALUE:
                        return TYPE_ENUM;
                    case TYPE_SFIXED32_VALUE:
                        return TYPE_SFIXED32;
                    case 16:
                        return TYPE_SFIXED64;
                    case 17:
                        return TYPE_SINT32;
                    case 18:
                        return TYPE_SINT64;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<Type> internalGetValueMap() {
                return internalValueMap;
            }

            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }

            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FieldDescriptorProto.getDescriptor().getEnumTypes().get(0);
            }

            public static Type valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() == getDescriptor()) {
                    return VALUES[desc.getIndex()];
                }
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }

            private Type(int index2, int value2) {
                this.index = index2;
                this.value = value2;
            }
        }

        public enum Label implements ProtocolMessageEnum {
            LABEL_OPTIONAL(0, 1),
            LABEL_REQUIRED(1, 2),
            LABEL_REPEATED(2, 3);
            
            public static final int LABEL_OPTIONAL_VALUE = 1;
            public static final int LABEL_REPEATED_VALUE = 3;
            public static final int LABEL_REQUIRED_VALUE = 2;
            private static final Label[] VALUES = null;
            private static Internal.EnumLiteMap<Label> internalValueMap;
            private final int index;
            private final int value;

            static {
                internalValueMap = new Internal.EnumLiteMap<Label>() {
                    public Label findValueByNumber(int number) {
                        return Label.valueOf(number);
                    }
                };
                VALUES = values();
            }

            public final int getNumber() {
                return this.value;
            }

            public static Label valueOf(int value2) {
                if (value2 == 1) {
                    return LABEL_OPTIONAL;
                }
                if (value2 == 2) {
                    return LABEL_REQUIRED;
                }
                if (value2 != 3) {
                    return null;
                }
                return LABEL_REPEATED;
            }

            public static Internal.EnumLiteMap<Label> internalGetValueMap() {
                return internalValueMap;
            }

            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }

            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FieldDescriptorProto.getDescriptor().getEnumTypes().get(1);
            }

            public static Label valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() == getDescriptor()) {
                    return VALUES[desc.getIndex()];
                }
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }

            private Label(int index2, int value2) {
                this.index = index2;
                this.value = value2;
            }
        }

        public boolean hasName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public boolean hasNumber() {
            return (this.bitField0_ & 2) == 2;
        }

        public int getNumber() {
            return this.number_;
        }

        public boolean hasLabel() {
            return (this.bitField0_ & 4) == 4;
        }

        public Label getLabel() {
            return this.label_;
        }

        public boolean hasType() {
            return (this.bitField0_ & 8) == 8;
        }

        public Type getType() {
            return this.type_;
        }

        public boolean hasTypeName() {
            return (this.bitField0_ & 16) == 16;
        }

        public String getTypeName() {
            Object ref = this.typeName_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.typeName_ = s;
            }
            return s;
        }

        public ByteString getTypeNameBytes() {
            Object ref = this.typeName_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.typeName_ = b;
            return b;
        }

        public boolean hasExtendee() {
            return (this.bitField0_ & 32) == 32;
        }

        public String getExtendee() {
            Object ref = this.extendee_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.extendee_ = s;
            }
            return s;
        }

        public ByteString getExtendeeBytes() {
            Object ref = this.extendee_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.extendee_ = b;
            return b;
        }

        public boolean hasDefaultValue() {
            return (this.bitField0_ & 64) == 64;
        }

        public String getDefaultValue() {
            Object ref = this.defaultValue_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.defaultValue_ = s;
            }
            return s;
        }

        public ByteString getDefaultValueBytes() {
            Object ref = this.defaultValue_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.defaultValue_ = b;
            return b;
        }

        public boolean hasOptions() {
            return (this.bitField0_ & 128) == 128;
        }

        public FieldOptions getOptions() {
            return this.options_;
        }

        public FieldOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }

        private void initFields() {
            this.name_ = "";
            this.number_ = 0;
            this.label_ = Label.LABEL_OPTIONAL;
            this.type_ = Type.TYPE_DOUBLE;
            this.typeName_ = "";
            this.extendee_ = "";
            this.defaultValue_ = "";
            this.options_ = FieldOptions.getDefaultInstance();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!hasOptions() || getOptions().isInitialized()) {
                this.memoizedIsInitialized = 1;
                return true;
            }
            this.memoizedIsInitialized = 0;
            return false;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getNameBytes());
            }
            if ((this.bitField0_ & 32) == 32) {
                output.writeBytes(2, getExtendeeBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeInt32(3, this.number_);
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeEnum(4, this.label_.getNumber());
            }
            if ((this.bitField0_ & 8) == 8) {
                output.writeEnum(5, this.type_.getNumber());
            }
            if ((this.bitField0_ & 16) == 16) {
                output.writeBytes(6, getTypeNameBytes());
            }
            if ((this.bitField0_ & 64) == 64) {
                output.writeBytes(7, getDefaultValueBytes());
            }
            if ((this.bitField0_ & 128) == 128) {
                output.writeMessage(8, this.options_);
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getNameBytes());
            }
            if ((this.bitField0_ & 32) == 32) {
                size2 += CodedOutputStream.computeBytesSize(2, getExtendeeBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeInt32Size(3, this.number_);
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeEnumSize(4, this.label_.getNumber());
            }
            if ((this.bitField0_ & 8) == 8) {
                size2 += CodedOutputStream.computeEnumSize(5, this.type_.getNumber());
            }
            if ((this.bitField0_ & 16) == 16) {
                size2 += CodedOutputStream.computeBytesSize(6, getTypeNameBytes());
            }
            if ((this.bitField0_ & 64) == 64) {
                size2 += CodedOutputStream.computeBytesSize(7, getDefaultValueBytes());
            }
            if ((this.bitField0_ & 128) == 128) {
                size2 += CodedOutputStream.computeMessageSize(8, this.options_);
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static FieldDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FieldDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FieldDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FieldDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FieldDescriptorProto parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FieldDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static FieldDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static FieldDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static FieldDescriptorProto parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FieldDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(FieldDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FieldDescriptorProtoOrBuilder {
            private int bitField0_;
            private Object defaultValue_;
            private Object extendee_;
            private Label label_;
            private Object name_;
            private int number_;
            private SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> optionsBuilder_;
            private FieldOptions options_;
            private Object typeName_;
            private Type type_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldDescriptorProto.class, Builder.class);
            }

            private Builder() {
                this.name_ = "";
                this.label_ = Label.LABEL_OPTIONAL;
                this.type_ = Type.TYPE_DOUBLE;
                this.typeName_ = "";
                this.extendee_ = "";
                this.defaultValue_ = "";
                this.options_ = FieldOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.label_ = Label.LABEL_OPTIONAL;
                this.type_ = Type.TYPE_DOUBLE;
                this.typeName_ = "";
                this.extendee_ = "";
                this.defaultValue_ = "";
                this.options_ = FieldOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getOptionsFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= -2;
                this.number_ = 0;
                this.bitField0_ &= -3;
                this.label_ = Label.LABEL_OPTIONAL;
                this.bitField0_ &= -5;
                this.type_ = Type.TYPE_DOUBLE;
                this.bitField0_ &= -9;
                this.typeName_ = "";
                this.bitField0_ &= -17;
                this.extendee_ = "";
                this.bitField0_ &= -33;
                this.defaultValue_ = "";
                this.bitField0_ &= -65;
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = FieldOptions.getDefaultInstance();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -129;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor;
            }

            public FieldDescriptorProto getDefaultInstanceForType() {
                return FieldDescriptorProto.getDefaultInstance();
            }

            public FieldDescriptorProto build() {
                FieldDescriptorProto result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public FieldDescriptorProto buildPartial() {
                FieldDescriptorProto result = new FieldDescriptorProto((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.name_ = this.name_;
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ |= 2;
                }
                int unused2 = result.number_ = this.number_;
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 4;
                }
                Label unused3 = result.label_ = this.label_;
                if ((from_bitField0_ & 8) == 8) {
                    to_bitField0_ |= 8;
                }
                Type unused4 = result.type_ = this.type_;
                if ((from_bitField0_ & 16) == 16) {
                    to_bitField0_ |= 16;
                }
                Object unused5 = result.typeName_ = this.typeName_;
                if ((from_bitField0_ & 32) == 32) {
                    to_bitField0_ |= 32;
                }
                Object unused6 = result.extendee_ = this.extendee_;
                if ((from_bitField0_ & 64) == 64) {
                    to_bitField0_ |= 64;
                }
                Object unused7 = result.defaultValue_ = this.defaultValue_;
                if ((from_bitField0_ & 128) == 128) {
                    to_bitField0_ |= 128;
                }
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    FieldOptions unused8 = result.options_ = this.options_;
                } else {
                    FieldOptions unused9 = result.options_ = singleFieldBuilder.build();
                }
                int unused10 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof FieldDescriptorProto) {
                    return mergeFrom((FieldDescriptorProto) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(FieldDescriptorProto other) {
                if (other == FieldDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 1;
                    this.name_ = other.name_;
                    onChanged();
                }
                if (other.hasNumber()) {
                    setNumber(other.getNumber());
                }
                if (other.hasLabel()) {
                    setLabel(other.getLabel());
                }
                if (other.hasType()) {
                    setType(other.getType());
                }
                if (other.hasTypeName()) {
                    this.bitField0_ |= 16;
                    this.typeName_ = other.typeName_;
                    onChanged();
                }
                if (other.hasExtendee()) {
                    this.bitField0_ |= 32;
                    this.extendee_ = other.extendee_;
                    onChanged();
                }
                if (other.hasDefaultValue()) {
                    this.bitField0_ |= 64;
                    this.defaultValue_ = other.defaultValue_;
                    onChanged();
                }
                if (other.hasOptions()) {
                    mergeOptions(other.getOptions());
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                if (!hasOptions() || getOptions().isInitialized()) {
                    return true;
                }
                return false;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    FieldDescriptorProto parsedMessage = FieldDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    FieldDescriptorProto parsedMessage2 = (FieldDescriptorProto) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((FieldDescriptorProto) null);
                    }
                    throw th;
                }
            }

            public boolean hasName() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearName() {
                this.bitField0_ &= -2;
                this.name_ = FieldDescriptorProto.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasNumber() {
                return (this.bitField0_ & 2) == 2;
            }

            public int getNumber() {
                return this.number_;
            }

            public Builder setNumber(int value) {
                this.bitField0_ |= 2;
                this.number_ = value;
                onChanged();
                return this;
            }

            public Builder clearNumber() {
                this.bitField0_ &= -3;
                this.number_ = 0;
                onChanged();
                return this;
            }

            public boolean hasLabel() {
                return (this.bitField0_ & 4) == 4;
            }

            public Label getLabel() {
                return this.label_;
            }

            public Builder setLabel(Label value) {
                if (value != null) {
                    this.bitField0_ |= 4;
                    this.label_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearLabel() {
                this.bitField0_ &= -5;
                this.label_ = Label.LABEL_OPTIONAL;
                onChanged();
                return this;
            }

            public boolean hasType() {
                return (this.bitField0_ & 8) == 8;
            }

            public Type getType() {
                return this.type_;
            }

            public Builder setType(Type value) {
                if (value != null) {
                    this.bitField0_ |= 8;
                    this.type_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearType() {
                this.bitField0_ &= -9;
                this.type_ = Type.TYPE_DOUBLE;
                onChanged();
                return this;
            }

            public boolean hasTypeName() {
                return (this.bitField0_ & 16) == 16;
            }

            public String getTypeName() {
                Object ref = this.typeName_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.typeName_ = s;
                return s;
            }

            public ByteString getTypeNameBytes() {
                Object ref = this.typeName_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.typeName_ = b;
                return b;
            }

            public Builder setTypeName(String value) {
                if (value != null) {
                    this.bitField0_ |= 16;
                    this.typeName_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearTypeName() {
                this.bitField0_ &= -17;
                this.typeName_ = FieldDescriptorProto.getDefaultInstance().getTypeName();
                onChanged();
                return this;
            }

            public Builder setTypeNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 16;
                    this.typeName_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasExtendee() {
                return (this.bitField0_ & 32) == 32;
            }

            public String getExtendee() {
                Object ref = this.extendee_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.extendee_ = s;
                return s;
            }

            public ByteString getExtendeeBytes() {
                Object ref = this.extendee_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.extendee_ = b;
                return b;
            }

            public Builder setExtendee(String value) {
                if (value != null) {
                    this.bitField0_ |= 32;
                    this.extendee_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearExtendee() {
                this.bitField0_ &= -33;
                this.extendee_ = FieldDescriptorProto.getDefaultInstance().getExtendee();
                onChanged();
                return this;
            }

            public Builder setExtendeeBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 32;
                    this.extendee_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasDefaultValue() {
                return (this.bitField0_ & 64) == 64;
            }

            public String getDefaultValue() {
                Object ref = this.defaultValue_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.defaultValue_ = s;
                return s;
            }

            public ByteString getDefaultValueBytes() {
                Object ref = this.defaultValue_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.defaultValue_ = b;
                return b;
            }

            public Builder setDefaultValue(String value) {
                if (value != null) {
                    this.bitField0_ |= 64;
                    this.defaultValue_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearDefaultValue() {
                this.bitField0_ &= -65;
                this.defaultValue_ = FieldDescriptorProto.getDefaultInstance().getDefaultValue();
                onChanged();
                return this;
            }

            public Builder setDefaultValueBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 64;
                    this.defaultValue_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasOptions() {
                return (this.bitField0_ & 128) == 128;
            }

            public FieldOptions getOptions() {
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    return this.options_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setOptions(FieldOptions value) {
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.options_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 128;
                return this;
            }

            public Builder setOptions(FieldOptions.Builder builderForValue) {
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 128;
                return this;
            }

            public Builder mergeOptions(FieldOptions value) {
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 128) != 128 || this.options_ == FieldOptions.getDefaultInstance()) {
                        this.options_ = value;
                    } else {
                        this.options_ = FieldOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 128;
                return this;
            }

            public Builder clearOptions() {
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = FieldOptions.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -129;
                return this;
            }

            public FieldOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 128;
                onChanged();
                return getOptionsFieldBuilder().getBuilder();
            }

            public FieldOptionsOrBuilder getOptionsOrBuilder() {
                SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.options_;
            }

            private SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<>(this.options_, getParentForChildren(), isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }

    public static final class EnumDescriptorProto extends GeneratedMessage implements EnumDescriptorProtoOrBuilder {
        public static final int NAME_FIELD_NUMBER = 1;
        public static final int OPTIONS_FIELD_NUMBER = 3;
        public static Parser<EnumDescriptorProto> PARSER = new AbstractParser<EnumDescriptorProto>() {
            public EnumDescriptorProto parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new EnumDescriptorProto(input, extensionRegistry);
            }
        };
        public static final int VALUE_FIELD_NUMBER = 2;
        private static final EnumDescriptorProto defaultInstance = new EnumDescriptorProto(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public Object name_;
        /* access modifiers changed from: private */
        public EnumOptions options_;
        private final UnknownFieldSet unknownFields;
        /* access modifiers changed from: private */
        public List<EnumValueDescriptorProto> value_;

        private EnumDescriptorProto(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private EnumDescriptorProto(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static EnumDescriptorProto getDefaultInstance() {
            return defaultInstance;
        }

        public EnumDescriptorProto getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private EnumDescriptorProto(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        this.bitField0_ |= 1;
                        this.name_ = input.readBytes();
                    } else if (tag == 18) {
                        if ((mutable_bitField0_ & 2) != 2) {
                            this.value_ = new ArrayList();
                            mutable_bitField0_ |= 2;
                        }
                        this.value_.add(input.readMessage(EnumValueDescriptorProto.PARSER, extensionRegistry));
                    } else if (tag == 26) {
                        EnumOptions.Builder subBuilder = (this.bitField0_ & 2) == 2 ? this.options_.toBuilder() : null;
                        this.options_ = (EnumOptions) input.readMessage(EnumOptions.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                            subBuilder.mergeFrom(this.options_);
                            this.options_ = subBuilder.buildPartial();
                        }
                        this.bitField0_ |= 2;
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 2) == 2) {
                        this.value_ = Collections.unmodifiableList(this.value_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 2) == 2) {
                this.value_ = Collections.unmodifiableList(this.value_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumDescriptorProto.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<EnumDescriptorProto> getParserForType() {
            return PARSER;
        }

        public boolean hasName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public List<EnumValueDescriptorProto> getValueList() {
            return this.value_;
        }

        public List<? extends EnumValueDescriptorProtoOrBuilder> getValueOrBuilderList() {
            return this.value_;
        }

        public int getValueCount() {
            return this.value_.size();
        }

        public EnumValueDescriptorProto getValue(int index) {
            return this.value_.get(index);
        }

        public EnumValueDescriptorProtoOrBuilder getValueOrBuilder(int index) {
            return this.value_.get(index);
        }

        public boolean hasOptions() {
            return (this.bitField0_ & 2) == 2;
        }

        public EnumOptions getOptions() {
            return this.options_;
        }

        public EnumOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }

        private void initFields() {
            this.name_ = "";
            this.value_ = Collections.emptyList();
            this.options_ = EnumOptions.getDefaultInstance();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getValueCount(); i++) {
                if (!getValue(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (hasOptions() == 0 || getOptions().isInitialized()) {
                this.memoizedIsInitialized = 1;
                return true;
            }
            this.memoizedIsInitialized = 0;
            return false;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getNameBytes());
            }
            for (int i = 0; i < this.value_.size(); i++) {
                output.writeMessage(2, this.value_.get(i));
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeMessage(3, this.options_);
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getNameBytes());
            }
            for (int i = 0; i < this.value_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(2, this.value_.get(i));
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeMessageSize(3, this.options_);
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static EnumDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumDescriptorProto parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static EnumDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static EnumDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static EnumDescriptorProto parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(EnumDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements EnumDescriptorProtoOrBuilder {
            private int bitField0_;
            private Object name_;
            private SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> optionsBuilder_;
            private EnumOptions options_;
            private RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> valueBuilder_;
            private List<EnumValueDescriptorProto> value_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumDescriptorProto.class, Builder.class);
            }

            private Builder() {
                this.name_ = "";
                this.value_ = Collections.emptyList();
                this.options_ = EnumOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.value_ = Collections.emptyList();
                this.options_ = EnumOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getValueFieldBuilder();
                    getOptionsFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= -2;
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.value_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                } else {
                    repeatedFieldBuilder.clear();
                }
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = EnumOptions.getDefaultInstance();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -5;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor;
            }

            public EnumDescriptorProto getDefaultInstanceForType() {
                return EnumDescriptorProto.getDefaultInstance();
            }

            public EnumDescriptorProto build() {
                EnumDescriptorProto result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public EnumDescriptorProto buildPartial() {
                EnumDescriptorProto result = new EnumDescriptorProto((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.name_ = this.name_;
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 2) == 2) {
                        this.value_ = Collections.unmodifiableList(this.value_);
                        this.bitField0_ &= -3;
                    }
                    List unused2 = result.value_ = this.value_;
                } else {
                    List unused3 = result.value_ = repeatedFieldBuilder.build();
                }
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 2;
                }
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    EnumOptions unused4 = result.options_ = this.options_;
                } else {
                    EnumOptions unused5 = result.options_ = singleFieldBuilder.build();
                }
                int unused6 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof EnumDescriptorProto) {
                    return mergeFrom((EnumDescriptorProto) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(EnumDescriptorProto other) {
                if (other == EnumDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 1;
                    this.name_ = other.name_;
                    onChanged();
                }
                if (this.valueBuilder_ == null) {
                    if (!other.value_.isEmpty()) {
                        if (this.value_.isEmpty()) {
                            this.value_ = other.value_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureValueIsMutable();
                            this.value_.addAll(other.value_);
                        }
                        onChanged();
                    }
                } else if (!other.value_.isEmpty()) {
                    if (this.valueBuilder_.isEmpty()) {
                        this.valueBuilder_.dispose();
                        RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = null;
                        this.valueBuilder_ = null;
                        this.value_ = other.value_;
                        this.bitField0_ &= -3;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getValueFieldBuilder();
                        }
                        this.valueBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.valueBuilder_.addAllMessages(other.value_);
                    }
                }
                if (other.hasOptions()) {
                    mergeOptions(other.getOptions());
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getValueCount(); i++) {
                    if (!getValue(i).isInitialized()) {
                        return false;
                    }
                }
                if (hasOptions() == 0 || getOptions().isInitialized()) {
                    return true;
                }
                return false;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    EnumDescriptorProto parsedMessage = EnumDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    EnumDescriptorProto parsedMessage2 = (EnumDescriptorProto) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((EnumDescriptorProto) null);
                    }
                    throw th;
                }
            }

            public boolean hasName() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearName() {
                this.bitField0_ &= -2;
                this.name_ = EnumDescriptorProto.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            private void ensureValueIsMutable() {
                if ((this.bitField0_ & 2) != 2) {
                    this.value_ = new ArrayList(this.value_);
                    this.bitField0_ |= 2;
                }
            }

            public List<EnumValueDescriptorProto> getValueList() {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.value_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getValueCount() {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.value_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public EnumValueDescriptorProto getValue(int index) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.value_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setValue(int index, EnumValueDescriptorProto value) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureValueIsMutable();
                    this.value_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setValue(int index, EnumValueDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureValueIsMutable();
                    this.value_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addValue(EnumValueDescriptorProto value) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureValueIsMutable();
                    this.value_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addValue(int index, EnumValueDescriptorProto value) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureValueIsMutable();
                    this.value_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addValue(EnumValueDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureValueIsMutable();
                    this.value_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addValue(int index, EnumValueDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureValueIsMutable();
                    this.value_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllValue(Iterable<? extends EnumValueDescriptorProto> values) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureValueIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.value_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearValue() {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.value_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeValue(int index) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureValueIsMutable();
                    this.value_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public EnumValueDescriptorProto.Builder getValueBuilder(int index) {
                return getValueFieldBuilder().getBuilder(index);
            }

            public EnumValueDescriptorProtoOrBuilder getValueOrBuilder(int index) {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.value_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends EnumValueDescriptorProtoOrBuilder> getValueOrBuilderList() {
                RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> repeatedFieldBuilder = this.valueBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.value_);
            }

            public EnumValueDescriptorProto.Builder addValueBuilder() {
                return getValueFieldBuilder().addBuilder(EnumValueDescriptorProto.getDefaultInstance());
            }

            public EnumValueDescriptorProto.Builder addValueBuilder(int index) {
                return getValueFieldBuilder().addBuilder(index, EnumValueDescriptorProto.getDefaultInstance());
            }

            public List<EnumValueDescriptorProto.Builder> getValueBuilderList() {
                return getValueFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> getValueFieldBuilder() {
                if (this.valueBuilder_ == null) {
                    this.valueBuilder_ = new RepeatedFieldBuilder<>(this.value_, (this.bitField0_ & 2) == 2, getParentForChildren(), isClean());
                    this.value_ = null;
                }
                return this.valueBuilder_;
            }

            public boolean hasOptions() {
                return (this.bitField0_ & 4) == 4;
            }

            public EnumOptions getOptions() {
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    return this.options_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setOptions(EnumOptions value) {
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.options_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder setOptions(EnumOptions.Builder builderForValue) {
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder mergeOptions(EnumOptions value) {
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 4) != 4 || this.options_ == EnumOptions.getDefaultInstance()) {
                        this.options_ = value;
                    } else {
                        this.options_ = EnumOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder clearOptions() {
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = EnumOptions.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -5;
                return this;
            }

            public EnumOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 4;
                onChanged();
                return getOptionsFieldBuilder().getBuilder();
            }

            public EnumOptionsOrBuilder getOptionsOrBuilder() {
                SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.options_;
            }

            private SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<>(this.options_, getParentForChildren(), isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }

    public static final class EnumValueDescriptorProto extends GeneratedMessage implements EnumValueDescriptorProtoOrBuilder {
        public static final int NAME_FIELD_NUMBER = 1;
        public static final int NUMBER_FIELD_NUMBER = 2;
        public static final int OPTIONS_FIELD_NUMBER = 3;
        public static Parser<EnumValueDescriptorProto> PARSER = new AbstractParser<EnumValueDescriptorProto>() {
            public EnumValueDescriptorProto parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new EnumValueDescriptorProto(input, extensionRegistry);
            }
        };
        private static final EnumValueDescriptorProto defaultInstance = new EnumValueDescriptorProto(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public Object name_;
        /* access modifiers changed from: private */
        public int number_;
        /* access modifiers changed from: private */
        public EnumValueOptions options_;
        private final UnknownFieldSet unknownFields;

        private EnumValueDescriptorProto(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private EnumValueDescriptorProto(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static EnumValueDescriptorProto getDefaultInstance() {
            return defaultInstance;
        }

        public EnumValueDescriptorProto getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private EnumValueDescriptorProto(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        this.bitField0_ |= 1;
                        this.name_ = input.readBytes();
                    } else if (tag == 16) {
                        this.bitField0_ |= 2;
                        this.number_ = input.readInt32();
                    } else if (tag == 26) {
                        EnumValueOptions.Builder subBuilder = (this.bitField0_ & 4) == 4 ? this.options_.toBuilder() : null;
                        this.options_ = (EnumValueOptions) input.readMessage(EnumValueOptions.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                            subBuilder.mergeFrom(this.options_);
                            this.options_ = subBuilder.buildPartial();
                        }
                        this.bitField0_ |= 4;
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueDescriptorProto.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<EnumValueDescriptorProto> getParserForType() {
            return PARSER;
        }

        public boolean hasName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public boolean hasNumber() {
            return (this.bitField0_ & 2) == 2;
        }

        public int getNumber() {
            return this.number_;
        }

        public boolean hasOptions() {
            return (this.bitField0_ & 4) == 4;
        }

        public EnumValueOptions getOptions() {
            return this.options_;
        }

        public EnumValueOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }

        private void initFields() {
            this.name_ = "";
            this.number_ = 0;
            this.options_ = EnumValueOptions.getDefaultInstance();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!hasOptions() || getOptions().isInitialized()) {
                this.memoizedIsInitialized = 1;
                return true;
            }
            this.memoizedIsInitialized = 0;
            return false;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getNameBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeInt32(2, this.number_);
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeMessage(3, this.options_);
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getNameBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeInt32Size(2, this.number_);
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeMessageSize(3, this.options_);
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static EnumValueDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumValueDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumValueDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumValueDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumValueDescriptorProto parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumValueDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static EnumValueDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static EnumValueDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static EnumValueDescriptorProto parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumValueDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(EnumValueDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements EnumValueDescriptorProtoOrBuilder {
            private int bitField0_;
            private Object name_;
            private int number_;
            private SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> optionsBuilder_;
            private EnumValueOptions options_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueDescriptorProto.class, Builder.class);
            }

            private Builder() {
                this.name_ = "";
                this.options_ = EnumValueOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.options_ = EnumValueOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getOptionsFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= -2;
                this.number_ = 0;
                this.bitField0_ &= -3;
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = EnumValueOptions.getDefaultInstance();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -5;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
            }

            public EnumValueDescriptorProto getDefaultInstanceForType() {
                return EnumValueDescriptorProto.getDefaultInstance();
            }

            public EnumValueDescriptorProto build() {
                EnumValueDescriptorProto result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public EnumValueDescriptorProto buildPartial() {
                EnumValueDescriptorProto result = new EnumValueDescriptorProto((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.name_ = this.name_;
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ |= 2;
                }
                int unused2 = result.number_ = this.number_;
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 4;
                }
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    EnumValueOptions unused3 = result.options_ = this.options_;
                } else {
                    EnumValueOptions unused4 = result.options_ = singleFieldBuilder.build();
                }
                int unused5 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof EnumValueDescriptorProto) {
                    return mergeFrom((EnumValueDescriptorProto) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(EnumValueDescriptorProto other) {
                if (other == EnumValueDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 1;
                    this.name_ = other.name_;
                    onChanged();
                }
                if (other.hasNumber()) {
                    setNumber(other.getNumber());
                }
                if (other.hasOptions()) {
                    mergeOptions(other.getOptions());
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                if (!hasOptions() || getOptions().isInitialized()) {
                    return true;
                }
                return false;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    EnumValueDescriptorProto parsedMessage = EnumValueDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    EnumValueDescriptorProto parsedMessage2 = (EnumValueDescriptorProto) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((EnumValueDescriptorProto) null);
                    }
                    throw th;
                }
            }

            public boolean hasName() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearName() {
                this.bitField0_ &= -2;
                this.name_ = EnumValueDescriptorProto.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasNumber() {
                return (this.bitField0_ & 2) == 2;
            }

            public int getNumber() {
                return this.number_;
            }

            public Builder setNumber(int value) {
                this.bitField0_ |= 2;
                this.number_ = value;
                onChanged();
                return this;
            }

            public Builder clearNumber() {
                this.bitField0_ &= -3;
                this.number_ = 0;
                onChanged();
                return this;
            }

            public boolean hasOptions() {
                return (this.bitField0_ & 4) == 4;
            }

            public EnumValueOptions getOptions() {
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    return this.options_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setOptions(EnumValueOptions value) {
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.options_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder setOptions(EnumValueOptions.Builder builderForValue) {
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder mergeOptions(EnumValueOptions value) {
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 4) != 4 || this.options_ == EnumValueOptions.getDefaultInstance()) {
                        this.options_ = value;
                    } else {
                        this.options_ = EnumValueOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder clearOptions() {
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = EnumValueOptions.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -5;
                return this;
            }

            public EnumValueOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 4;
                onChanged();
                return getOptionsFieldBuilder().getBuilder();
            }

            public EnumValueOptionsOrBuilder getOptionsOrBuilder() {
                SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.options_;
            }

            private SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<>(this.options_, getParentForChildren(), isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }

    public static final class ServiceDescriptorProto extends GeneratedMessage implements ServiceDescriptorProtoOrBuilder {
        public static final int METHOD_FIELD_NUMBER = 2;
        public static final int NAME_FIELD_NUMBER = 1;
        public static final int OPTIONS_FIELD_NUMBER = 3;
        public static Parser<ServiceDescriptorProto> PARSER = new AbstractParser<ServiceDescriptorProto>() {
            public ServiceDescriptorProto parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ServiceDescriptorProto(input, extensionRegistry);
            }
        };
        private static final ServiceDescriptorProto defaultInstance = new ServiceDescriptorProto(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public List<MethodDescriptorProto> method_;
        /* access modifiers changed from: private */
        public Object name_;
        /* access modifiers changed from: private */
        public ServiceOptions options_;
        private final UnknownFieldSet unknownFields;

        private ServiceDescriptorProto(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private ServiceDescriptorProto(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static ServiceDescriptorProto getDefaultInstance() {
            return defaultInstance;
        }

        public ServiceDescriptorProto getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private ServiceDescriptorProto(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        this.bitField0_ |= 1;
                        this.name_ = input.readBytes();
                    } else if (tag == 18) {
                        if ((mutable_bitField0_ & 2) != 2) {
                            this.method_ = new ArrayList();
                            mutable_bitField0_ |= 2;
                        }
                        this.method_.add(input.readMessage(MethodDescriptorProto.PARSER, extensionRegistry));
                    } else if (tag == 26) {
                        ServiceOptions.Builder subBuilder = (this.bitField0_ & 2) == 2 ? this.options_.toBuilder() : null;
                        this.options_ = (ServiceOptions) input.readMessage(ServiceOptions.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                            subBuilder.mergeFrom(this.options_);
                            this.options_ = subBuilder.buildPartial();
                        }
                        this.bitField0_ |= 2;
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 2) == 2) {
                        this.method_ = Collections.unmodifiableList(this.method_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 2) == 2) {
                this.method_ = Collections.unmodifiableList(this.method_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceDescriptorProto.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<ServiceDescriptorProto> getParserForType() {
            return PARSER;
        }

        public boolean hasName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public List<MethodDescriptorProto> getMethodList() {
            return this.method_;
        }

        public List<? extends MethodDescriptorProtoOrBuilder> getMethodOrBuilderList() {
            return this.method_;
        }

        public int getMethodCount() {
            return this.method_.size();
        }

        public MethodDescriptorProto getMethod(int index) {
            return this.method_.get(index);
        }

        public MethodDescriptorProtoOrBuilder getMethodOrBuilder(int index) {
            return this.method_.get(index);
        }

        public boolean hasOptions() {
            return (this.bitField0_ & 2) == 2;
        }

        public ServiceOptions getOptions() {
            return this.options_;
        }

        public ServiceOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }

        private void initFields() {
            this.name_ = "";
            this.method_ = Collections.emptyList();
            this.options_ = ServiceOptions.getDefaultInstance();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getMethodCount(); i++) {
                if (!getMethod(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (hasOptions() == 0 || getOptions().isInitialized()) {
                this.memoizedIsInitialized = 1;
                return true;
            }
            this.memoizedIsInitialized = 0;
            return false;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getNameBytes());
            }
            for (int i = 0; i < this.method_.size(); i++) {
                output.writeMessage(2, this.method_.get(i));
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeMessage(3, this.options_);
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getNameBytes());
            }
            for (int i = 0; i < this.method_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(2, this.method_.get(i));
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeMessageSize(3, this.options_);
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static ServiceDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ServiceDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ServiceDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ServiceDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ServiceDescriptorProto parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static ServiceDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static ServiceDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static ServiceDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static ServiceDescriptorProto parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static ServiceDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(ServiceDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ServiceDescriptorProtoOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> methodBuilder_;
            private List<MethodDescriptorProto> method_;
            private Object name_;
            private SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> optionsBuilder_;
            private ServiceOptions options_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceDescriptorProto.class, Builder.class);
            }

            private Builder() {
                this.name_ = "";
                this.method_ = Collections.emptyList();
                this.options_ = ServiceOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.method_ = Collections.emptyList();
                this.options_ = ServiceOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getMethodFieldBuilder();
                    getOptionsFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= -2;
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.method_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                } else {
                    repeatedFieldBuilder.clear();
                }
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = ServiceOptions.getDefaultInstance();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -5;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
            }

            public ServiceDescriptorProto getDefaultInstanceForType() {
                return ServiceDescriptorProto.getDefaultInstance();
            }

            public ServiceDescriptorProto build() {
                ServiceDescriptorProto result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public ServiceDescriptorProto buildPartial() {
                ServiceDescriptorProto result = new ServiceDescriptorProto((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.name_ = this.name_;
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 2) == 2) {
                        this.method_ = Collections.unmodifiableList(this.method_);
                        this.bitField0_ &= -3;
                    }
                    List unused2 = result.method_ = this.method_;
                } else {
                    List unused3 = result.method_ = repeatedFieldBuilder.build();
                }
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 2;
                }
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    ServiceOptions unused4 = result.options_ = this.options_;
                } else {
                    ServiceOptions unused5 = result.options_ = singleFieldBuilder.build();
                }
                int unused6 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof ServiceDescriptorProto) {
                    return mergeFrom((ServiceDescriptorProto) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ServiceDescriptorProto other) {
                if (other == ServiceDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 1;
                    this.name_ = other.name_;
                    onChanged();
                }
                if (this.methodBuilder_ == null) {
                    if (!other.method_.isEmpty()) {
                        if (this.method_.isEmpty()) {
                            this.method_ = other.method_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureMethodIsMutable();
                            this.method_.addAll(other.method_);
                        }
                        onChanged();
                    }
                } else if (!other.method_.isEmpty()) {
                    if (this.methodBuilder_.isEmpty()) {
                        this.methodBuilder_.dispose();
                        RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = null;
                        this.methodBuilder_ = null;
                        this.method_ = other.method_;
                        this.bitField0_ &= -3;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getMethodFieldBuilder();
                        }
                        this.methodBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.methodBuilder_.addAllMessages(other.method_);
                    }
                }
                if (other.hasOptions()) {
                    mergeOptions(other.getOptions());
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getMethodCount(); i++) {
                    if (!getMethod(i).isInitialized()) {
                        return false;
                    }
                }
                if (hasOptions() == 0 || getOptions().isInitialized()) {
                    return true;
                }
                return false;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    ServiceDescriptorProto parsedMessage = ServiceDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    ServiceDescriptorProto parsedMessage2 = (ServiceDescriptorProto) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((ServiceDescriptorProto) null);
                    }
                    throw th;
                }
            }

            public boolean hasName() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearName() {
                this.bitField0_ &= -2;
                this.name_ = ServiceDescriptorProto.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            private void ensureMethodIsMutable() {
                if ((this.bitField0_ & 2) != 2) {
                    this.method_ = new ArrayList(this.method_);
                    this.bitField0_ |= 2;
                }
            }

            public List<MethodDescriptorProto> getMethodList() {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.method_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getMethodCount() {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.method_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public MethodDescriptorProto getMethod(int index) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.method_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setMethod(int index, MethodDescriptorProto value) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureMethodIsMutable();
                    this.method_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setMethod(int index, MethodDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMethodIsMutable();
                    this.method_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addMethod(MethodDescriptorProto value) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureMethodIsMutable();
                    this.method_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addMethod(int index, MethodDescriptorProto value) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureMethodIsMutable();
                    this.method_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addMethod(MethodDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMethodIsMutable();
                    this.method_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addMethod(int index, MethodDescriptorProto.Builder builderForValue) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMethodIsMutable();
                    this.method_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllMethod(Iterable<? extends MethodDescriptorProto> values) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMethodIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.method_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearMethod() {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.method_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeMethod(int index) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureMethodIsMutable();
                    this.method_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public MethodDescriptorProto.Builder getMethodBuilder(int index) {
                return getMethodFieldBuilder().getBuilder(index);
            }

            public MethodDescriptorProtoOrBuilder getMethodOrBuilder(int index) {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.method_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends MethodDescriptorProtoOrBuilder> getMethodOrBuilderList() {
                RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> repeatedFieldBuilder = this.methodBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.method_);
            }

            public MethodDescriptorProto.Builder addMethodBuilder() {
                return getMethodFieldBuilder().addBuilder(MethodDescriptorProto.getDefaultInstance());
            }

            public MethodDescriptorProto.Builder addMethodBuilder(int index) {
                return getMethodFieldBuilder().addBuilder(index, MethodDescriptorProto.getDefaultInstance());
            }

            public List<MethodDescriptorProto.Builder> getMethodBuilderList() {
                return getMethodFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> getMethodFieldBuilder() {
                if (this.methodBuilder_ == null) {
                    this.methodBuilder_ = new RepeatedFieldBuilder<>(this.method_, (this.bitField0_ & 2) == 2, getParentForChildren(), isClean());
                    this.method_ = null;
                }
                return this.methodBuilder_;
            }

            public boolean hasOptions() {
                return (this.bitField0_ & 4) == 4;
            }

            public ServiceOptions getOptions() {
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    return this.options_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setOptions(ServiceOptions value) {
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.options_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder setOptions(ServiceOptions.Builder builderForValue) {
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder mergeOptions(ServiceOptions value) {
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 4) != 4 || this.options_ == ServiceOptions.getDefaultInstance()) {
                        this.options_ = value;
                    } else {
                        this.options_ = ServiceOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 4;
                return this;
            }

            public Builder clearOptions() {
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = ServiceOptions.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -5;
                return this;
            }

            public ServiceOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 4;
                onChanged();
                return getOptionsFieldBuilder().getBuilder();
            }

            public ServiceOptionsOrBuilder getOptionsOrBuilder() {
                SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.options_;
            }

            private SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<>(this.options_, getParentForChildren(), isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }

    public static final class MethodDescriptorProto extends GeneratedMessage implements MethodDescriptorProtoOrBuilder {
        public static final int INPUT_TYPE_FIELD_NUMBER = 2;
        public static final int NAME_FIELD_NUMBER = 1;
        public static final int OPTIONS_FIELD_NUMBER = 4;
        public static final int OUTPUT_TYPE_FIELD_NUMBER = 3;
        public static Parser<MethodDescriptorProto> PARSER = new AbstractParser<MethodDescriptorProto>() {
            public MethodDescriptorProto parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new MethodDescriptorProto(input, extensionRegistry);
            }
        };
        private static final MethodDescriptorProto defaultInstance = new MethodDescriptorProto(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        /* access modifiers changed from: private */
        public Object inputType_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public Object name_;
        /* access modifiers changed from: private */
        public MethodOptions options_;
        /* access modifiers changed from: private */
        public Object outputType_;
        private final UnknownFieldSet unknownFields;

        private MethodDescriptorProto(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private MethodDescriptorProto(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static MethodDescriptorProto getDefaultInstance() {
            return defaultInstance;
        }

        public MethodDescriptorProto getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private MethodDescriptorProto(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        this.bitField0_ |= 1;
                        this.name_ = input.readBytes();
                    } else if (tag == 18) {
                        this.bitField0_ |= 2;
                        this.inputType_ = input.readBytes();
                    } else if (tag == 26) {
                        this.bitField0_ |= 4;
                        this.outputType_ = input.readBytes();
                    } else if (tag == 34) {
                        MethodOptions.Builder subBuilder = (this.bitField0_ & 8) == 8 ? this.options_.toBuilder() : null;
                        this.options_ = (MethodOptions) input.readMessage(MethodOptions.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                            subBuilder.mergeFrom(this.options_);
                            this.options_ = subBuilder.buildPartial();
                        }
                        this.bitField0_ |= 8;
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodDescriptorProto.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<MethodDescriptorProto> getParserForType() {
            return PARSER;
        }

        public boolean hasName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }

        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.name_ = b;
            return b;
        }

        public boolean hasInputType() {
            return (this.bitField0_ & 2) == 2;
        }

        public String getInputType() {
            Object ref = this.inputType_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.inputType_ = s;
            }
            return s;
        }

        public ByteString getInputTypeBytes() {
            Object ref = this.inputType_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.inputType_ = b;
            return b;
        }

        public boolean hasOutputType() {
            return (this.bitField0_ & 4) == 4;
        }

        public String getOutputType() {
            Object ref = this.outputType_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.outputType_ = s;
            }
            return s;
        }

        public ByteString getOutputTypeBytes() {
            Object ref = this.outputType_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.outputType_ = b;
            return b;
        }

        public boolean hasOptions() {
            return (this.bitField0_ & 8) == 8;
        }

        public MethodOptions getOptions() {
            return this.options_;
        }

        public MethodOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }

        private void initFields() {
            this.name_ = "";
            this.inputType_ = "";
            this.outputType_ = "";
            this.options_ = MethodOptions.getDefaultInstance();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!hasOptions() || getOptions().isInitialized()) {
                this.memoizedIsInitialized = 1;
                return true;
            }
            this.memoizedIsInitialized = 0;
            return false;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getNameBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeBytes(2, getInputTypeBytes());
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeBytes(3, getOutputTypeBytes());
            }
            if ((this.bitField0_ & 8) == 8) {
                output.writeMessage(4, this.options_);
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getNameBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeBytesSize(2, getInputTypeBytes());
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeBytesSize(3, getOutputTypeBytes());
            }
            if ((this.bitField0_ & 8) == 8) {
                size2 += CodedOutputStream.computeMessageSize(4, this.options_);
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static MethodDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static MethodDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static MethodDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static MethodDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static MethodDescriptorProto parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static MethodDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static MethodDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static MethodDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static MethodDescriptorProto parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static MethodDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(MethodDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements MethodDescriptorProtoOrBuilder {
            private int bitField0_;
            private Object inputType_;
            private Object name_;
            private SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> optionsBuilder_;
            private MethodOptions options_;
            private Object outputType_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodDescriptorProto.class, Builder.class);
            }

            private Builder() {
                this.name_ = "";
                this.inputType_ = "";
                this.outputType_ = "";
                this.options_ = MethodOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.inputType_ = "";
                this.outputType_ = "";
                this.options_ = MethodOptions.getDefaultInstance();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getOptionsFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= -2;
                this.inputType_ = "";
                this.bitField0_ &= -3;
                this.outputType_ = "";
                this.bitField0_ &= -5;
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = MethodOptions.getDefaultInstance();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -9;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor;
            }

            public MethodDescriptorProto getDefaultInstanceForType() {
                return MethodDescriptorProto.getDefaultInstance();
            }

            public MethodDescriptorProto build() {
                MethodDescriptorProto result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public MethodDescriptorProto buildPartial() {
                MethodDescriptorProto result = new MethodDescriptorProto((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.name_ = this.name_;
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ |= 2;
                }
                Object unused2 = result.inputType_ = this.inputType_;
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 4;
                }
                Object unused3 = result.outputType_ = this.outputType_;
                if ((from_bitField0_ & 8) == 8) {
                    to_bitField0_ |= 8;
                }
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    MethodOptions unused4 = result.options_ = this.options_;
                } else {
                    MethodOptions unused5 = result.options_ = singleFieldBuilder.build();
                }
                int unused6 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof MethodDescriptorProto) {
                    return mergeFrom((MethodDescriptorProto) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(MethodDescriptorProto other) {
                if (other == MethodDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 1;
                    this.name_ = other.name_;
                    onChanged();
                }
                if (other.hasInputType()) {
                    this.bitField0_ |= 2;
                    this.inputType_ = other.inputType_;
                    onChanged();
                }
                if (other.hasOutputType()) {
                    this.bitField0_ |= 4;
                    this.outputType_ = other.outputType_;
                    onChanged();
                }
                if (other.hasOptions()) {
                    mergeOptions(other.getOptions());
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                if (!hasOptions() || getOptions().isInitialized()) {
                    return true;
                }
                return false;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    MethodDescriptorProto parsedMessage = MethodDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    MethodDescriptorProto parsedMessage2 = (MethodDescriptorProto) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((MethodDescriptorProto) null);
                    }
                    throw th;
                }
            }

            public boolean hasName() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.name_ = s;
                return s;
            }

            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.name_ = b;
                return b;
            }

            public Builder setName(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearName() {
                this.bitField0_ &= -2;
                this.name_ = MethodDescriptorProto.getDefaultInstance().getName();
                onChanged();
                return this;
            }

            public Builder setNameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasInputType() {
                return (this.bitField0_ & 2) == 2;
            }

            public String getInputType() {
                Object ref = this.inputType_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.inputType_ = s;
                return s;
            }

            public ByteString getInputTypeBytes() {
                Object ref = this.inputType_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.inputType_ = b;
                return b;
            }

            public Builder setInputType(String value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.inputType_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearInputType() {
                this.bitField0_ &= -3;
                this.inputType_ = MethodDescriptorProto.getDefaultInstance().getInputType();
                onChanged();
                return this;
            }

            public Builder setInputTypeBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.inputType_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasOutputType() {
                return (this.bitField0_ & 4) == 4;
            }

            public String getOutputType() {
                Object ref = this.outputType_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.outputType_ = s;
                return s;
            }

            public ByteString getOutputTypeBytes() {
                Object ref = this.outputType_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.outputType_ = b;
                return b;
            }

            public Builder setOutputType(String value) {
                if (value != null) {
                    this.bitField0_ |= 4;
                    this.outputType_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearOutputType() {
                this.bitField0_ &= -5;
                this.outputType_ = MethodDescriptorProto.getDefaultInstance().getOutputType();
                onChanged();
                return this;
            }

            public Builder setOutputTypeBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 4;
                    this.outputType_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasOptions() {
                return (this.bitField0_ & 8) == 8;
            }

            public MethodOptions getOptions() {
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    return this.options_;
                }
                return singleFieldBuilder.getMessage();
            }

            public Builder setOptions(MethodOptions value) {
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    singleFieldBuilder.setMessage(value);
                } else if (value != null) {
                    this.options_ = value;
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder setOptions(MethodOptions.Builder builderForValue) {
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = builderForValue.build();
                    onChanged();
                } else {
                    singleFieldBuilder.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder mergeOptions(MethodOptions value) {
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    if ((this.bitField0_ & 8) != 8 || this.options_ == MethodOptions.getDefaultInstance()) {
                        this.options_ = value;
                    } else {
                        this.options_ = MethodOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    onChanged();
                } else {
                    singleFieldBuilder.mergeFrom(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder clearOptions() {
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder == null) {
                    this.options_ = MethodOptions.getDefaultInstance();
                    onChanged();
                } else {
                    singleFieldBuilder.clear();
                }
                this.bitField0_ &= -9;
                return this;
            }

            public MethodOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 8;
                onChanged();
                return getOptionsFieldBuilder().getBuilder();
            }

            public MethodOptionsOrBuilder getOptionsOrBuilder() {
                SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> singleFieldBuilder = this.optionsBuilder_;
                if (singleFieldBuilder != null) {
                    return singleFieldBuilder.getMessageOrBuilder();
                }
                return this.options_;
            }

            private SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<>(this.options_, getParentForChildren(), isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }

    public static final class FileOptions extends GeneratedMessage.ExtendableMessage<FileOptions> implements FileOptionsOrBuilder {
        public static final int CC_GENERIC_SERVICES_FIELD_NUMBER = 16;
        public static final int GO_PACKAGE_FIELD_NUMBER = 11;
        public static final int JAVA_GENERATE_EQUALS_AND_HASH_FIELD_NUMBER = 20;
        public static final int JAVA_GENERIC_SERVICES_FIELD_NUMBER = 17;
        public static final int JAVA_MULTIPLE_FILES_FIELD_NUMBER = 10;
        public static final int JAVA_OUTER_CLASSNAME_FIELD_NUMBER = 8;
        public static final int JAVA_PACKAGE_FIELD_NUMBER = 1;
        public static final int OPTIMIZE_FOR_FIELD_NUMBER = 9;
        public static Parser<FileOptions> PARSER = new AbstractParser<FileOptions>() {
            public FileOptions parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new FileOptions(input, extensionRegistry);
            }
        };
        public static final int PY_GENERIC_SERVICES_FIELD_NUMBER = 18;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private static final FileOptions defaultInstance = new FileOptions(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        /* access modifiers changed from: private */
        public boolean ccGenericServices_;
        /* access modifiers changed from: private */
        public Object goPackage_;
        /* access modifiers changed from: private */
        public boolean javaGenerateEqualsAndHash_;
        /* access modifiers changed from: private */
        public boolean javaGenericServices_;
        /* access modifiers changed from: private */
        public boolean javaMultipleFiles_;
        /* access modifiers changed from: private */
        public Object javaOuterClassname_;
        /* access modifiers changed from: private */
        public Object javaPackage_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public OptimizeMode optimizeFor_;
        /* access modifiers changed from: private */
        public boolean pyGenericServices_;
        /* access modifiers changed from: private */
        public List<UninterpretedOption> uninterpretedOption_;
        private final UnknownFieldSet unknownFields;

        private FileOptions(GeneratedMessage.ExtendableBuilder<FileOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private FileOptions(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static FileOptions getDefaultInstance() {
            return defaultInstance;
        }

        public FileOptions getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        private FileOptions(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    switch (tag) {
                        case Utf8.COMPLETE:
                            done = true;
                            break;
                        case 10:
                            this.bitField0_ |= 1;
                            this.javaPackage_ = input.readBytes();
                            break;
                        case 66:
                            this.bitField0_ |= 2;
                            this.javaOuterClassname_ = input.readBytes();
                            break;
                        case 72:
                            int rawValue = input.readEnum();
                            OptimizeMode value = OptimizeMode.valueOf(rawValue);
                            if (value != null) {
                                this.bitField0_ |= 16;
                                this.optimizeFor_ = value;
                                break;
                            } else {
                                unknownFields2.mergeVarintField(9, rawValue);
                                break;
                            }
                        case 80:
                            this.bitField0_ |= 4;
                            this.javaMultipleFiles_ = input.readBool();
                            break;
                        case 90:
                            this.bitField0_ |= 32;
                            this.goPackage_ = input.readBytes();
                            break;
                        case 128:
                            this.bitField0_ |= 64;
                            this.ccGenericServices_ = input.readBool();
                            break;
                        case 136:
                            this.bitField0_ |= 128;
                            this.javaGenericServices_ = input.readBool();
                            break;
                        case 144:
                            this.bitField0_ |= 256;
                            this.pyGenericServices_ = input.readBool();
                            break;
                        case 160:
                            this.bitField0_ |= 8;
                            this.javaGenerateEqualsAndHash_ = input.readBool();
                            break;
                        case 7994:
                            if ((mutable_bitField0_ & 512) != 512) {
                                this.uninterpretedOption_ = new ArrayList();
                                mutable_bitField0_ |= 512;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            break;
                        default:
                            if (parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                                break;
                            } else {
                                done = true;
                                break;
                            }
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 512) == 512) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 512) == 512) {
                this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FileOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FileOptions.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<FileOptions> getParserForType() {
            return PARSER;
        }

        public enum OptimizeMode implements ProtocolMessageEnum {
            SPEED(0, 1),
            CODE_SIZE(1, 2),
            LITE_RUNTIME(2, 3);
            
            public static final int CODE_SIZE_VALUE = 2;
            public static final int LITE_RUNTIME_VALUE = 3;
            public static final int SPEED_VALUE = 1;
            private static final OptimizeMode[] VALUES = null;
            private static Internal.EnumLiteMap<OptimizeMode> internalValueMap;
            private final int index;
            private final int value;

            static {
                internalValueMap = new Internal.EnumLiteMap<OptimizeMode>() {
                    public OptimizeMode findValueByNumber(int number) {
                        return OptimizeMode.valueOf(number);
                    }
                };
                VALUES = values();
            }

            public final int getNumber() {
                return this.value;
            }

            public static OptimizeMode valueOf(int value2) {
                if (value2 == 1) {
                    return SPEED;
                }
                if (value2 == 2) {
                    return CODE_SIZE;
                }
                if (value2 != 3) {
                    return null;
                }
                return LITE_RUNTIME;
            }

            public static Internal.EnumLiteMap<OptimizeMode> internalGetValueMap() {
                return internalValueMap;
            }

            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }

            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FileOptions.getDescriptor().getEnumTypes().get(0);
            }

            public static OptimizeMode valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() == getDescriptor()) {
                    return VALUES[desc.getIndex()];
                }
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }

            private OptimizeMode(int index2, int value2) {
                this.index = index2;
                this.value = value2;
            }
        }

        public boolean hasJavaPackage() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getJavaPackage() {
            Object ref = this.javaPackage_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.javaPackage_ = s;
            }
            return s;
        }

        public ByteString getJavaPackageBytes() {
            Object ref = this.javaPackage_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.javaPackage_ = b;
            return b;
        }

        public boolean hasJavaOuterClassname() {
            return (this.bitField0_ & 2) == 2;
        }

        public String getJavaOuterClassname() {
            Object ref = this.javaOuterClassname_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.javaOuterClassname_ = s;
            }
            return s;
        }

        public ByteString getJavaOuterClassnameBytes() {
            Object ref = this.javaOuterClassname_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.javaOuterClassname_ = b;
            return b;
        }

        public boolean hasJavaMultipleFiles() {
            return (this.bitField0_ & 4) == 4;
        }

        public boolean getJavaMultipleFiles() {
            return this.javaMultipleFiles_;
        }

        public boolean hasJavaGenerateEqualsAndHash() {
            return (this.bitField0_ & 8) == 8;
        }

        public boolean getJavaGenerateEqualsAndHash() {
            return this.javaGenerateEqualsAndHash_;
        }

        public boolean hasOptimizeFor() {
            return (this.bitField0_ & 16) == 16;
        }

        public OptimizeMode getOptimizeFor() {
            return this.optimizeFor_;
        }

        public boolean hasGoPackage() {
            return (this.bitField0_ & 32) == 32;
        }

        public String getGoPackage() {
            Object ref = this.goPackage_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.goPackage_ = s;
            }
            return s;
        }

        public ByteString getGoPackageBytes() {
            Object ref = this.goPackage_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.goPackage_ = b;
            return b;
        }

        public boolean hasCcGenericServices() {
            return (this.bitField0_ & 64) == 64;
        }

        public boolean getCcGenericServices() {
            return this.ccGenericServices_;
        }

        public boolean hasJavaGenericServices() {
            return (this.bitField0_ & 128) == 128;
        }

        public boolean getJavaGenericServices() {
            return this.javaGenericServices_;
        }

        public boolean hasPyGenericServices() {
            return (this.bitField0_ & 256) == 256;
        }

        public boolean getPyGenericServices() {
            return this.pyGenericServices_;
        }

        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }

        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }

        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }

        public UninterpretedOption getUninterpretedOption(int index) {
            return this.uninterpretedOption_.get(index);
        }

        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
            return this.uninterpretedOption_.get(index);
        }

        private void initFields() {
            this.javaPackage_ = "";
            this.javaOuterClassname_ = "";
            this.javaMultipleFiles_ = false;
            this.javaGenerateEqualsAndHash_ = false;
            this.optimizeFor_ = OptimizeMode.SPEED;
            this.goPackage_ = "";
            this.ccGenericServices_ = false;
            this.javaGenericServices_ = false;
            this.pyGenericServices_ = false;
            this.uninterpretedOption_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                if (!getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (extensionsAreInitialized() == 0) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter = newExtensionWriter();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(1, getJavaPackageBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeBytes(8, getJavaOuterClassnameBytes());
            }
            if ((this.bitField0_ & 16) == 16) {
                output.writeEnum(9, this.optimizeFor_.getNumber());
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeBool(10, this.javaMultipleFiles_);
            }
            if ((this.bitField0_ & 32) == 32) {
                output.writeBytes(11, getGoPackageBytes());
            }
            if ((this.bitField0_ & 64) == 64) {
                output.writeBool(16, this.ccGenericServices_);
            }
            if ((this.bitField0_ & 128) == 128) {
                output.writeBool(17, this.javaGenericServices_);
            }
            if ((this.bitField0_ & 256) == 256) {
                output.writeBool(18, this.pyGenericServices_);
            }
            if ((this.bitField0_ & 8) == 8) {
                output.writeBool(20, this.javaGenerateEqualsAndHash_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            newExtensionWriter.writeUntil(536870912, output);
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBytesSize(1, getJavaPackageBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeBytesSize(8, getJavaOuterClassnameBytes());
            }
            if ((this.bitField0_ & 16) == 16) {
                size2 += CodedOutputStream.computeEnumSize(9, this.optimizeFor_.getNumber());
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeBoolSize(10, this.javaMultipleFiles_);
            }
            if ((this.bitField0_ & 32) == 32) {
                size2 += CodedOutputStream.computeBytesSize(11, getGoPackageBytes());
            }
            if ((this.bitField0_ & 64) == 64) {
                size2 += CodedOutputStream.computeBoolSize(16, this.ccGenericServices_);
            }
            if ((this.bitField0_ & 128) == 128) {
                size2 += CodedOutputStream.computeBoolSize(17, this.javaGenericServices_);
            }
            if ((this.bitField0_ & 256) == 256) {
                size2 += CodedOutputStream.computeBoolSize(18, this.pyGenericServices_);
            }
            if ((this.bitField0_ & 8) == 8) {
                size2 += CodedOutputStream.computeBoolSize(20, this.javaGenerateEqualsAndHash_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            int size3 = size2 + extensionsSerializedSize() + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static FileOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FileOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FileOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FileOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FileOptions parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FileOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static FileOptions parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static FileOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static FileOptions parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FileOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(FileOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.ExtendableBuilder<FileOptions, Builder> implements FileOptionsOrBuilder {
            private int bitField0_;
            private boolean ccGenericServices_;
            private Object goPackage_;
            private boolean javaGenerateEqualsAndHash_;
            private boolean javaGenericServices_;
            private boolean javaMultipleFiles_;
            private Object javaOuterClassname_;
            private Object javaPackage_;
            private OptimizeMode optimizeFor_;
            private boolean pyGenericServices_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            private List<UninterpretedOption> uninterpretedOption_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FileOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FileOptions.class, Builder.class);
            }

            private Builder() {
                this.javaPackage_ = "";
                this.javaOuterClassname_ = "";
                this.optimizeFor_ = OptimizeMode.SPEED;
                this.goPackage_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.javaPackage_ = "";
                this.javaOuterClassname_ = "";
                this.optimizeFor_ = OptimizeMode.SPEED;
                this.goPackage_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getUninterpretedOptionFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.javaPackage_ = "";
                this.bitField0_ &= -2;
                this.javaOuterClassname_ = "";
                this.bitField0_ &= -3;
                this.javaMultipleFiles_ = false;
                this.bitField0_ &= -5;
                this.javaGenerateEqualsAndHash_ = false;
                this.bitField0_ &= -9;
                this.optimizeFor_ = OptimizeMode.SPEED;
                this.bitField0_ &= -17;
                this.goPackage_ = "";
                this.bitField0_ &= -33;
                this.ccGenericServices_ = false;
                this.bitField0_ &= -65;
                this.javaGenericServices_ = false;
                this.bitField0_ &= -129;
                this.pyGenericServices_ = false;
                this.bitField0_ &= -257;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -513;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor;
            }

            public FileOptions getDefaultInstanceForType() {
                return FileOptions.getDefaultInstance();
            }

            public FileOptions build() {
                FileOptions result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public FileOptions buildPartial() {
                FileOptions result = new FileOptions((GeneratedMessage.ExtendableBuilder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused = result.javaPackage_ = this.javaPackage_;
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ |= 2;
                }
                Object unused2 = result.javaOuterClassname_ = this.javaOuterClassname_;
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 4;
                }
                boolean unused3 = result.javaMultipleFiles_ = this.javaMultipleFiles_;
                if ((from_bitField0_ & 8) == 8) {
                    to_bitField0_ |= 8;
                }
                boolean unused4 = result.javaGenerateEqualsAndHash_ = this.javaGenerateEqualsAndHash_;
                if ((from_bitField0_ & 16) == 16) {
                    to_bitField0_ |= 16;
                }
                OptimizeMode unused5 = result.optimizeFor_ = this.optimizeFor_;
                if ((from_bitField0_ & 32) == 32) {
                    to_bitField0_ |= 32;
                }
                Object unused6 = result.goPackage_ = this.goPackage_;
                if ((from_bitField0_ & 64) == 64) {
                    to_bitField0_ |= 64;
                }
                boolean unused7 = result.ccGenericServices_ = this.ccGenericServices_;
                if ((from_bitField0_ & 128) == 128) {
                    to_bitField0_ |= 128;
                }
                boolean unused8 = result.javaGenericServices_ = this.javaGenericServices_;
                if ((from_bitField0_ & 256) == 256) {
                    to_bitField0_ |= 256;
                }
                boolean unused9 = result.pyGenericServices_ = this.pyGenericServices_;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 512) == 512) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                        this.bitField0_ &= -513;
                    }
                    List unused10 = result.uninterpretedOption_ = this.uninterpretedOption_;
                } else {
                    List unused11 = result.uninterpretedOption_ = repeatedFieldBuilder.build();
                }
                int unused12 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof FileOptions) {
                    return mergeFrom((FileOptions) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(FileOptions other) {
                if (other == FileOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasJavaPackage()) {
                    this.bitField0_ |= 1;
                    this.javaPackage_ = other.javaPackage_;
                    onChanged();
                }
                if (other.hasJavaOuterClassname()) {
                    this.bitField0_ |= 2;
                    this.javaOuterClassname_ = other.javaOuterClassname_;
                    onChanged();
                }
                if (other.hasJavaMultipleFiles()) {
                    setJavaMultipleFiles(other.getJavaMultipleFiles());
                }
                if (other.hasJavaGenerateEqualsAndHash()) {
                    setJavaGenerateEqualsAndHash(other.getJavaGenerateEqualsAndHash());
                }
                if (other.hasOptimizeFor()) {
                    setOptimizeFor(other.getOptimizeFor());
                }
                if (other.hasGoPackage()) {
                    this.bitField0_ |= 32;
                    this.goPackage_ = other.goPackage_;
                    onChanged();
                }
                if (other.hasCcGenericServices()) {
                    setCcGenericServices(other.getCcGenericServices());
                }
                if (other.hasJavaGenericServices()) {
                    setJavaGenericServices(other.getJavaGenericServices());
                }
                if (other.hasPyGenericServices()) {
                    setPyGenericServices(other.getPyGenericServices());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= -513;
                        } else {
                            ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        onChanged();
                    }
                } else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = null;
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= -513;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getUninterpretedOptionFieldBuilder();
                        }
                        this.uninterpretedOptionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                mergeExtensionFields(other);
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                    if (!getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                if (extensionsAreInitialized() == 0) {
                    return false;
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    FileOptions parsedMessage = FileOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    FileOptions parsedMessage2 = (FileOptions) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((FileOptions) null);
                    }
                    throw th;
                }
            }

            public boolean hasJavaPackage() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getJavaPackage() {
                Object ref = this.javaPackage_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.javaPackage_ = s;
                return s;
            }

            public ByteString getJavaPackageBytes() {
                Object ref = this.javaPackage_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.javaPackage_ = b;
                return b;
            }

            public Builder setJavaPackage(String value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.javaPackage_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearJavaPackage() {
                this.bitField0_ &= -2;
                this.javaPackage_ = FileOptions.getDefaultInstance().getJavaPackage();
                onChanged();
                return this;
            }

            public Builder setJavaPackageBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.javaPackage_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasJavaOuterClassname() {
                return (this.bitField0_ & 2) == 2;
            }

            public String getJavaOuterClassname() {
                Object ref = this.javaOuterClassname_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.javaOuterClassname_ = s;
                return s;
            }

            public ByteString getJavaOuterClassnameBytes() {
                Object ref = this.javaOuterClassname_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.javaOuterClassname_ = b;
                return b;
            }

            public Builder setJavaOuterClassname(String value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.javaOuterClassname_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearJavaOuterClassname() {
                this.bitField0_ &= -3;
                this.javaOuterClassname_ = FileOptions.getDefaultInstance().getJavaOuterClassname();
                onChanged();
                return this;
            }

            public Builder setJavaOuterClassnameBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.javaOuterClassname_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasJavaMultipleFiles() {
                return (this.bitField0_ & 4) == 4;
            }

            public boolean getJavaMultipleFiles() {
                return this.javaMultipleFiles_;
            }

            public Builder setJavaMultipleFiles(boolean value) {
                this.bitField0_ |= 4;
                this.javaMultipleFiles_ = value;
                onChanged();
                return this;
            }

            public Builder clearJavaMultipleFiles() {
                this.bitField0_ &= -5;
                this.javaMultipleFiles_ = false;
                onChanged();
                return this;
            }

            public boolean hasJavaGenerateEqualsAndHash() {
                return (this.bitField0_ & 8) == 8;
            }

            public boolean getJavaGenerateEqualsAndHash() {
                return this.javaGenerateEqualsAndHash_;
            }

            public Builder setJavaGenerateEqualsAndHash(boolean value) {
                this.bitField0_ |= 8;
                this.javaGenerateEqualsAndHash_ = value;
                onChanged();
                return this;
            }

            public Builder clearJavaGenerateEqualsAndHash() {
                this.bitField0_ &= -9;
                this.javaGenerateEqualsAndHash_ = false;
                onChanged();
                return this;
            }

            public boolean hasOptimizeFor() {
                return (this.bitField0_ & 16) == 16;
            }

            public OptimizeMode getOptimizeFor() {
                return this.optimizeFor_;
            }

            public Builder setOptimizeFor(OptimizeMode value) {
                if (value != null) {
                    this.bitField0_ |= 16;
                    this.optimizeFor_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearOptimizeFor() {
                this.bitField0_ &= -17;
                this.optimizeFor_ = OptimizeMode.SPEED;
                onChanged();
                return this;
            }

            public boolean hasGoPackage() {
                return (this.bitField0_ & 32) == 32;
            }

            public String getGoPackage() {
                Object ref = this.goPackage_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.goPackage_ = s;
                return s;
            }

            public ByteString getGoPackageBytes() {
                Object ref = this.goPackage_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.goPackage_ = b;
                return b;
            }

            public Builder setGoPackage(String value) {
                if (value != null) {
                    this.bitField0_ |= 32;
                    this.goPackage_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearGoPackage() {
                this.bitField0_ &= -33;
                this.goPackage_ = FileOptions.getDefaultInstance().getGoPackage();
                onChanged();
                return this;
            }

            public Builder setGoPackageBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 32;
                    this.goPackage_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasCcGenericServices() {
                return (this.bitField0_ & 64) == 64;
            }

            public boolean getCcGenericServices() {
                return this.ccGenericServices_;
            }

            public Builder setCcGenericServices(boolean value) {
                this.bitField0_ |= 64;
                this.ccGenericServices_ = value;
                onChanged();
                return this;
            }

            public Builder clearCcGenericServices() {
                this.bitField0_ &= -65;
                this.ccGenericServices_ = false;
                onChanged();
                return this;
            }

            public boolean hasJavaGenericServices() {
                return (this.bitField0_ & 128) == 128;
            }

            public boolean getJavaGenericServices() {
                return this.javaGenericServices_;
            }

            public Builder setJavaGenericServices(boolean value) {
                this.bitField0_ |= 128;
                this.javaGenericServices_ = value;
                onChanged();
                return this;
            }

            public Builder clearJavaGenericServices() {
                this.bitField0_ &= -129;
                this.javaGenericServices_ = false;
                onChanged();
                return this;
            }

            public boolean hasPyGenericServices() {
                return (this.bitField0_ & 256) == 256;
            }

            public boolean getPyGenericServices() {
                return this.pyGenericServices_;
            }

            public Builder setPyGenericServices(boolean value) {
                this.bitField0_ |= 256;
                this.pyGenericServices_ = value;
                onChanged();
                return this;
            }

            public Builder clearPyGenericServices() {
                this.bitField0_ &= -257;
                this.pyGenericServices_ = false;
                onChanged();
                return this;
            }

            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 512) != 512) {
                    this.uninterpretedOption_ = new ArrayList(this.uninterpretedOption_);
                    this.bitField0_ |= 512;
                }
            }

            public List<UninterpretedOption> getUninterpretedOptionList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.uninterpretedOption_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getUninterpretedOptionCount() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public UninterpretedOption getUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllUninterpretedOption(Iterable<? extends UninterpretedOption> values) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    GeneratedMessage.ExtendableBuilder.addAll(values, this.uninterpretedOption_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearUninterpretedOption() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -513;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public UninterpretedOption.Builder getUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().getBuilder(index);
            }

            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.uninterpretedOption_);
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }

            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return getUninterpretedOptionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<>(this.uninterpretedOption_, (this.bitField0_ & 512) == 512, getParentForChildren(), isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }

    public static final class MessageOptions extends GeneratedMessage.ExtendableMessage<MessageOptions> implements MessageOptionsOrBuilder {
        public static final int MESSAGE_SET_WIRE_FORMAT_FIELD_NUMBER = 1;
        public static final int NO_STANDARD_DESCRIPTOR_ACCESSOR_FIELD_NUMBER = 2;
        public static Parser<MessageOptions> PARSER = new AbstractParser<MessageOptions>() {
            public MessageOptions parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new MessageOptions(input, extensionRegistry);
            }
        };
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private static final MessageOptions defaultInstance = new MessageOptions(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public boolean messageSetWireFormat_;
        /* access modifiers changed from: private */
        public boolean noStandardDescriptorAccessor_;
        /* access modifiers changed from: private */
        public List<UninterpretedOption> uninterpretedOption_;
        private final UnknownFieldSet unknownFields;

        private MessageOptions(GeneratedMessage.ExtendableBuilder<MessageOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private MessageOptions(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static MessageOptions getDefaultInstance() {
            return defaultInstance;
        }

        public MessageOptions getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private MessageOptions(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 8) {
                        this.bitField0_ |= 1;
                        this.messageSetWireFormat_ = input.readBool();
                    } else if (tag == 16) {
                        this.bitField0_ |= 2;
                        this.noStandardDescriptorAccessor_ = input.readBool();
                    } else if (tag == 7994) {
                        if ((mutable_bitField0_ & 4) != 4) {
                            this.uninterpretedOption_ = new ArrayList();
                            mutable_bitField0_ |= 4;
                        }
                        this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 4) == 4) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 4) == 4) {
                this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_MessageOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MessageOptions.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<MessageOptions> getParserForType() {
            return PARSER;
        }

        public boolean hasMessageSetWireFormat() {
            return (this.bitField0_ & 1) == 1;
        }

        public boolean getMessageSetWireFormat() {
            return this.messageSetWireFormat_;
        }

        public boolean hasNoStandardDescriptorAccessor() {
            return (this.bitField0_ & 2) == 2;
        }

        public boolean getNoStandardDescriptorAccessor() {
            return this.noStandardDescriptorAccessor_;
        }

        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }

        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }

        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }

        public UninterpretedOption getUninterpretedOption(int index) {
            return this.uninterpretedOption_.get(index);
        }

        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
            return this.uninterpretedOption_.get(index);
        }

        private void initFields() {
            this.messageSetWireFormat_ = false;
            this.noStandardDescriptorAccessor_ = false;
            this.uninterpretedOption_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                if (!getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (extensionsAreInitialized() == 0) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter = newExtensionWriter();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBool(1, this.messageSetWireFormat_);
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeBool(2, this.noStandardDescriptorAccessor_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            newExtensionWriter.writeUntil(536870912, output);
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBoolSize(1, this.messageSetWireFormat_);
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeBoolSize(2, this.noStandardDescriptorAccessor_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            int size3 = size2 + extensionsSerializedSize() + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static MessageOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static MessageOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static MessageOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static MessageOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static MessageOptions parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static MessageOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static MessageOptions parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static MessageOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static MessageOptions parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static MessageOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(MessageOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.ExtendableBuilder<MessageOptions, Builder> implements MessageOptionsOrBuilder {
            private int bitField0_;
            private boolean messageSetWireFormat_;
            private boolean noStandardDescriptorAccessor_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            private List<UninterpretedOption> uninterpretedOption_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_MessageOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MessageOptions.class, Builder.class);
            }

            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getUninterpretedOptionFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.messageSetWireFormat_ = false;
                this.bitField0_ &= -2;
                this.noStandardDescriptorAccessor_ = false;
                this.bitField0_ &= -3;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -5;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor;
            }

            public MessageOptions getDefaultInstanceForType() {
                return MessageOptions.getDefaultInstance();
            }

            public MessageOptions build() {
                MessageOptions result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public MessageOptions buildPartial() {
                MessageOptions result = new MessageOptions((GeneratedMessage.ExtendableBuilder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                boolean unused = result.messageSetWireFormat_ = this.messageSetWireFormat_;
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ |= 2;
                }
                boolean unused2 = result.noStandardDescriptorAccessor_ = this.noStandardDescriptorAccessor_;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 4) == 4) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                        this.bitField0_ &= -5;
                    }
                    List unused3 = result.uninterpretedOption_ = this.uninterpretedOption_;
                } else {
                    List unused4 = result.uninterpretedOption_ = repeatedFieldBuilder.build();
                }
                int unused5 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof MessageOptions) {
                    return mergeFrom((MessageOptions) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(MessageOptions other) {
                if (other == MessageOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMessageSetWireFormat()) {
                    setMessageSetWireFormat(other.getMessageSetWireFormat());
                }
                if (other.hasNoStandardDescriptorAccessor()) {
                    setNoStandardDescriptorAccessor(other.getNoStandardDescriptorAccessor());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= -5;
                        } else {
                            ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        onChanged();
                    }
                } else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = null;
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= -5;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getUninterpretedOptionFieldBuilder();
                        }
                        this.uninterpretedOptionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                mergeExtensionFields(other);
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                    if (!getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                if (extensionsAreInitialized() == 0) {
                    return false;
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    MessageOptions parsedMessage = MessageOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    MessageOptions parsedMessage2 = (MessageOptions) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((MessageOptions) null);
                    }
                    throw th;
                }
            }

            public boolean hasMessageSetWireFormat() {
                return (this.bitField0_ & 1) == 1;
            }

            public boolean getMessageSetWireFormat() {
                return this.messageSetWireFormat_;
            }

            public Builder setMessageSetWireFormat(boolean value) {
                this.bitField0_ |= 1;
                this.messageSetWireFormat_ = value;
                onChanged();
                return this;
            }

            public Builder clearMessageSetWireFormat() {
                this.bitField0_ &= -2;
                this.messageSetWireFormat_ = false;
                onChanged();
                return this;
            }

            public boolean hasNoStandardDescriptorAccessor() {
                return (this.bitField0_ & 2) == 2;
            }

            public boolean getNoStandardDescriptorAccessor() {
                return this.noStandardDescriptorAccessor_;
            }

            public Builder setNoStandardDescriptorAccessor(boolean value) {
                this.bitField0_ |= 2;
                this.noStandardDescriptorAccessor_ = value;
                onChanged();
                return this;
            }

            public Builder clearNoStandardDescriptorAccessor() {
                this.bitField0_ &= -3;
                this.noStandardDescriptorAccessor_ = false;
                onChanged();
                return this;
            }

            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 4) != 4) {
                    this.uninterpretedOption_ = new ArrayList(this.uninterpretedOption_);
                    this.bitField0_ |= 4;
                }
            }

            public List<UninterpretedOption> getUninterpretedOptionList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.uninterpretedOption_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getUninterpretedOptionCount() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public UninterpretedOption getUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllUninterpretedOption(Iterable<? extends UninterpretedOption> values) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    GeneratedMessage.ExtendableBuilder.addAll(values, this.uninterpretedOption_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearUninterpretedOption() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -5;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public UninterpretedOption.Builder getUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().getBuilder(index);
            }

            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.uninterpretedOption_);
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }

            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return getUninterpretedOptionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<>(this.uninterpretedOption_, (this.bitField0_ & 4) == 4, getParentForChildren(), isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }

    public static final class FieldOptions extends GeneratedMessage.ExtendableMessage<FieldOptions> implements FieldOptionsOrBuilder {
        public static final int CTYPE_FIELD_NUMBER = 1;
        public static final int DEPRECATED_FIELD_NUMBER = 3;
        public static final int EXPERIMENTAL_MAP_KEY_FIELD_NUMBER = 9;
        public static final int LAZY_FIELD_NUMBER = 5;
        public static final int PACKED_FIELD_NUMBER = 2;
        public static Parser<FieldOptions> PARSER = new AbstractParser<FieldOptions>() {
            public FieldOptions parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new FieldOptions(input, extensionRegistry);
            }
        };
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        public static final int WEAK_FIELD_NUMBER = 10;
        private static final FieldOptions defaultInstance = new FieldOptions(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public int bitField0_;
        /* access modifiers changed from: private */
        public CType ctype_;
        /* access modifiers changed from: private */
        public boolean deprecated_;
        /* access modifiers changed from: private */
        public Object experimentalMapKey_;
        /* access modifiers changed from: private */
        public boolean lazy_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public boolean packed_;
        /* access modifiers changed from: private */
        public List<UninterpretedOption> uninterpretedOption_;
        private final UnknownFieldSet unknownFields;
        /* access modifiers changed from: private */
        public boolean weak_;

        private FieldOptions(GeneratedMessage.ExtendableBuilder<FieldOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private FieldOptions(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static FieldOptions getDefaultInstance() {
            return defaultInstance;
        }

        public FieldOptions getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        private FieldOptions(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 8) {
                        int rawValue = input.readEnum();
                        CType value = CType.valueOf(rawValue);
                        if (value == null) {
                            unknownFields2.mergeVarintField(1, rawValue);
                        } else {
                            this.bitField0_ = 1 | this.bitField0_;
                            this.ctype_ = value;
                        }
                    } else if (tag == 16) {
                        this.bitField0_ |= 2;
                        this.packed_ = input.readBool();
                    } else if (tag == 24) {
                        this.bitField0_ = 8 | this.bitField0_;
                        this.deprecated_ = input.readBool();
                    } else if (tag == 40) {
                        this.bitField0_ |= 4;
                        this.lazy_ = input.readBool();
                    } else if (tag == 74) {
                        this.bitField0_ |= 16;
                        this.experimentalMapKey_ = input.readBytes();
                    } else if (tag == 80) {
                        this.bitField0_ |= 32;
                        this.weak_ = input.readBool();
                    } else if (tag == 7994) {
                        if ((mutable_bitField0_ & 64) != 64) {
                            this.uninterpretedOption_ = new ArrayList();
                            mutable_bitField0_ |= 64;
                        }
                        this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 64) == 64) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 64) == 64) {
                this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FieldOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldOptions.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<FieldOptions> getParserForType() {
            return PARSER;
        }

        public enum CType implements ProtocolMessageEnum {
            STRING(0, 0),
            CORD(1, 1),
            STRING_PIECE(2, 2);
            
            public static final int CORD_VALUE = 1;
            public static final int STRING_PIECE_VALUE = 2;
            public static final int STRING_VALUE = 0;
            private static final CType[] VALUES = null;
            private static Internal.EnumLiteMap<CType> internalValueMap;
            private final int index;
            private final int value;

            static {
                internalValueMap = new Internal.EnumLiteMap<CType>() {
                    public CType findValueByNumber(int number) {
                        return CType.valueOf(number);
                    }
                };
                VALUES = values();
            }

            public final int getNumber() {
                return this.value;
            }

            public static CType valueOf(int value2) {
                if (value2 == 0) {
                    return STRING;
                }
                if (value2 == 1) {
                    return CORD;
                }
                if (value2 != 2) {
                    return null;
                }
                return STRING_PIECE;
            }

            public static Internal.EnumLiteMap<CType> internalGetValueMap() {
                return internalValueMap;
            }

            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }

            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FieldOptions.getDescriptor().getEnumTypes().get(0);
            }

            public static CType valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() == getDescriptor()) {
                    return VALUES[desc.getIndex()];
                }
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }

            private CType(int index2, int value2) {
                this.index = index2;
                this.value = value2;
            }
        }

        public boolean hasCtype() {
            return (this.bitField0_ & 1) == 1;
        }

        public CType getCtype() {
            return this.ctype_;
        }

        public boolean hasPacked() {
            return (this.bitField0_ & 2) == 2;
        }

        public boolean getPacked() {
            return this.packed_;
        }

        public boolean hasLazy() {
            return (this.bitField0_ & 4) == 4;
        }

        public boolean getLazy() {
            return this.lazy_;
        }

        public boolean hasDeprecated() {
            return (this.bitField0_ & 8) == 8;
        }

        public boolean getDeprecated() {
            return this.deprecated_;
        }

        public boolean hasExperimentalMapKey() {
            return (this.bitField0_ & 16) == 16;
        }

        public String getExperimentalMapKey() {
            Object ref = this.experimentalMapKey_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.experimentalMapKey_ = s;
            }
            return s;
        }

        public ByteString getExperimentalMapKeyBytes() {
            Object ref = this.experimentalMapKey_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.experimentalMapKey_ = b;
            return b;
        }

        public boolean hasWeak() {
            return (this.bitField0_ & 32) == 32;
        }

        public boolean getWeak() {
            return this.weak_;
        }

        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }

        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }

        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }

        public UninterpretedOption getUninterpretedOption(int index) {
            return this.uninterpretedOption_.get(index);
        }

        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
            return this.uninterpretedOption_.get(index);
        }

        private void initFields() {
            this.ctype_ = CType.STRING;
            this.packed_ = false;
            this.lazy_ = false;
            this.deprecated_ = false;
            this.experimentalMapKey_ = "";
            this.weak_ = false;
            this.uninterpretedOption_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                if (!getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (extensionsAreInitialized() == 0) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter = newExtensionWriter();
            if ((this.bitField0_ & 1) == 1) {
                output.writeEnum(1, this.ctype_.getNumber());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeBool(2, this.packed_);
            }
            if ((this.bitField0_ & 8) == 8) {
                output.writeBool(3, this.deprecated_);
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeBool(5, this.lazy_);
            }
            if ((this.bitField0_ & 16) == 16) {
                output.writeBytes(9, getExperimentalMapKeyBytes());
            }
            if ((this.bitField0_ & 32) == 32) {
                output.writeBool(10, this.weak_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            newExtensionWriter.writeUntil(536870912, output);
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeEnumSize(1, this.ctype_.getNumber());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeBoolSize(2, this.packed_);
            }
            if ((this.bitField0_ & 8) == 8) {
                size2 += CodedOutputStream.computeBoolSize(3, this.deprecated_);
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeBoolSize(5, this.lazy_);
            }
            if ((this.bitField0_ & 16) == 16) {
                size2 += CodedOutputStream.computeBytesSize(9, getExperimentalMapKeyBytes());
            }
            if ((this.bitField0_ & 32) == 32) {
                size2 += CodedOutputStream.computeBoolSize(10, this.weak_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            int size3 = size2 + extensionsSerializedSize() + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static FieldOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FieldOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FieldOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static FieldOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static FieldOptions parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FieldOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static FieldOptions parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static FieldOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static FieldOptions parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static FieldOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(FieldOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.ExtendableBuilder<FieldOptions, Builder> implements FieldOptionsOrBuilder {
            private int bitField0_;
            private CType ctype_;
            private boolean deprecated_;
            private Object experimentalMapKey_;
            private boolean lazy_;
            private boolean packed_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            private List<UninterpretedOption> uninterpretedOption_;
            private boolean weak_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FieldOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldOptions.class, Builder.class);
            }

            private Builder() {
                this.ctype_ = CType.STRING;
                this.experimentalMapKey_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.ctype_ = CType.STRING;
                this.experimentalMapKey_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getUninterpretedOptionFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.ctype_ = CType.STRING;
                this.bitField0_ &= -2;
                this.packed_ = false;
                this.bitField0_ &= -3;
                this.lazy_ = false;
                this.bitField0_ &= -5;
                this.deprecated_ = false;
                this.bitField0_ &= -9;
                this.experimentalMapKey_ = "";
                this.bitField0_ &= -17;
                this.weak_ = false;
                this.bitField0_ &= -33;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -65;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor;
            }

            public FieldOptions getDefaultInstanceForType() {
                return FieldOptions.getDefaultInstance();
            }

            public FieldOptions build() {
                FieldOptions result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public FieldOptions buildPartial() {
                FieldOptions result = new FieldOptions((GeneratedMessage.ExtendableBuilder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                CType unused = result.ctype_ = this.ctype_;
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ |= 2;
                }
                boolean unused2 = result.packed_ = this.packed_;
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 4;
                }
                boolean unused3 = result.lazy_ = this.lazy_;
                if ((from_bitField0_ & 8) == 8) {
                    to_bitField0_ |= 8;
                }
                boolean unused4 = result.deprecated_ = this.deprecated_;
                if ((from_bitField0_ & 16) == 16) {
                    to_bitField0_ |= 16;
                }
                Object unused5 = result.experimentalMapKey_ = this.experimentalMapKey_;
                if ((from_bitField0_ & 32) == 32) {
                    to_bitField0_ |= 32;
                }
                boolean unused6 = result.weak_ = this.weak_;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 64) == 64) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                        this.bitField0_ &= -65;
                    }
                    List unused7 = result.uninterpretedOption_ = this.uninterpretedOption_;
                } else {
                    List unused8 = result.uninterpretedOption_ = repeatedFieldBuilder.build();
                }
                int unused9 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof FieldOptions) {
                    return mergeFrom((FieldOptions) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(FieldOptions other) {
                if (other == FieldOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasCtype()) {
                    setCtype(other.getCtype());
                }
                if (other.hasPacked()) {
                    setPacked(other.getPacked());
                }
                if (other.hasLazy()) {
                    setLazy(other.getLazy());
                }
                if (other.hasDeprecated()) {
                    setDeprecated(other.getDeprecated());
                }
                if (other.hasExperimentalMapKey()) {
                    this.bitField0_ |= 16;
                    this.experimentalMapKey_ = other.experimentalMapKey_;
                    onChanged();
                }
                if (other.hasWeak()) {
                    setWeak(other.getWeak());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= -65;
                        } else {
                            ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        onChanged();
                    }
                } else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = null;
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= -65;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getUninterpretedOptionFieldBuilder();
                        }
                        this.uninterpretedOptionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                mergeExtensionFields(other);
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                    if (!getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                if (extensionsAreInitialized() == 0) {
                    return false;
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    FieldOptions parsedMessage = FieldOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    FieldOptions parsedMessage2 = (FieldOptions) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((FieldOptions) null);
                    }
                    throw th;
                }
            }

            public boolean hasCtype() {
                return (this.bitField0_ & 1) == 1;
            }

            public CType getCtype() {
                return this.ctype_;
            }

            public Builder setCtype(CType value) {
                if (value != null) {
                    this.bitField0_ |= 1;
                    this.ctype_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearCtype() {
                this.bitField0_ &= -2;
                this.ctype_ = CType.STRING;
                onChanged();
                return this;
            }

            public boolean hasPacked() {
                return (this.bitField0_ & 2) == 2;
            }

            public boolean getPacked() {
                return this.packed_;
            }

            public Builder setPacked(boolean value) {
                this.bitField0_ |= 2;
                this.packed_ = value;
                onChanged();
                return this;
            }

            public Builder clearPacked() {
                this.bitField0_ &= -3;
                this.packed_ = false;
                onChanged();
                return this;
            }

            public boolean hasLazy() {
                return (this.bitField0_ & 4) == 4;
            }

            public boolean getLazy() {
                return this.lazy_;
            }

            public Builder setLazy(boolean value) {
                this.bitField0_ |= 4;
                this.lazy_ = value;
                onChanged();
                return this;
            }

            public Builder clearLazy() {
                this.bitField0_ &= -5;
                this.lazy_ = false;
                onChanged();
                return this;
            }

            public boolean hasDeprecated() {
                return (this.bitField0_ & 8) == 8;
            }

            public boolean getDeprecated() {
                return this.deprecated_;
            }

            public Builder setDeprecated(boolean value) {
                this.bitField0_ |= 8;
                this.deprecated_ = value;
                onChanged();
                return this;
            }

            public Builder clearDeprecated() {
                this.bitField0_ &= -9;
                this.deprecated_ = false;
                onChanged();
                return this;
            }

            public boolean hasExperimentalMapKey() {
                return (this.bitField0_ & 16) == 16;
            }

            public String getExperimentalMapKey() {
                Object ref = this.experimentalMapKey_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.experimentalMapKey_ = s;
                return s;
            }

            public ByteString getExperimentalMapKeyBytes() {
                Object ref = this.experimentalMapKey_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.experimentalMapKey_ = b;
                return b;
            }

            public Builder setExperimentalMapKey(String value) {
                if (value != null) {
                    this.bitField0_ |= 16;
                    this.experimentalMapKey_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearExperimentalMapKey() {
                this.bitField0_ &= -17;
                this.experimentalMapKey_ = FieldOptions.getDefaultInstance().getExperimentalMapKey();
                onChanged();
                return this;
            }

            public Builder setExperimentalMapKeyBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 16;
                    this.experimentalMapKey_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasWeak() {
                return (this.bitField0_ & 32) == 32;
            }

            public boolean getWeak() {
                return this.weak_;
            }

            public Builder setWeak(boolean value) {
                this.bitField0_ |= 32;
                this.weak_ = value;
                onChanged();
                return this;
            }

            public Builder clearWeak() {
                this.bitField0_ &= -33;
                this.weak_ = false;
                onChanged();
                return this;
            }

            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 64) != 64) {
                    this.uninterpretedOption_ = new ArrayList(this.uninterpretedOption_);
                    this.bitField0_ |= 64;
                }
            }

            public List<UninterpretedOption> getUninterpretedOptionList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.uninterpretedOption_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getUninterpretedOptionCount() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public UninterpretedOption getUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllUninterpretedOption(Iterable<? extends UninterpretedOption> values) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    GeneratedMessage.ExtendableBuilder.addAll(values, this.uninterpretedOption_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearUninterpretedOption() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -65;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public UninterpretedOption.Builder getUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().getBuilder(index);
            }

            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.uninterpretedOption_);
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }

            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return getUninterpretedOptionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<>(this.uninterpretedOption_, (this.bitField0_ & 64) == 64, getParentForChildren(), isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }

    public static final class EnumOptions extends GeneratedMessage.ExtendableMessage<EnumOptions> implements EnumOptionsOrBuilder {
        public static final int ALLOW_ALIAS_FIELD_NUMBER = 2;
        public static Parser<EnumOptions> PARSER = new AbstractParser<EnumOptions>() {
            public EnumOptions parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new EnumOptions(input, extensionRegistry);
            }
        };
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private static final EnumOptions defaultInstance = new EnumOptions(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public boolean allowAlias_;
        /* access modifiers changed from: private */
        public int bitField0_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public List<UninterpretedOption> uninterpretedOption_;
        private final UnknownFieldSet unknownFields;

        private EnumOptions(GeneratedMessage.ExtendableBuilder<EnumOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private EnumOptions(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static EnumOptions getDefaultInstance() {
            return defaultInstance;
        }

        public EnumOptions getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private EnumOptions(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 16) {
                        this.bitField0_ |= 1;
                        this.allowAlias_ = input.readBool();
                    } else if (tag == 7994) {
                        if ((mutable_bitField0_ & 2) != 2) {
                            this.uninterpretedOption_ = new ArrayList();
                            mutable_bitField0_ |= 2;
                        }
                        this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 2) == 2) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 2) == 2) {
                this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumOptions.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<EnumOptions> getParserForType() {
            return PARSER;
        }

        public boolean hasAllowAlias() {
            return (this.bitField0_ & 1) == 1;
        }

        public boolean getAllowAlias() {
            return this.allowAlias_;
        }

        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }

        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }

        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }

        public UninterpretedOption getUninterpretedOption(int index) {
            return this.uninterpretedOption_.get(index);
        }

        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
            return this.uninterpretedOption_.get(index);
        }

        private void initFields() {
            this.allowAlias_ = true;
            this.uninterpretedOption_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                if (!getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (extensionsAreInitialized() == 0) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter = newExtensionWriter();
            if ((this.bitField0_ & 1) == 1) {
                output.writeBool(2, this.allowAlias_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            newExtensionWriter.writeUntil(536870912, output);
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeBoolSize(2, this.allowAlias_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            int size3 = size2 + extensionsSerializedSize() + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static EnumOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumOptions parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static EnumOptions parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static EnumOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static EnumOptions parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(EnumOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.ExtendableBuilder<EnumOptions, Builder> implements EnumOptionsOrBuilder {
            private boolean allowAlias_;
            private int bitField0_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            private List<UninterpretedOption> uninterpretedOption_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumOptions.class, Builder.class);
            }

            private Builder() {
                this.allowAlias_ = true;
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.allowAlias_ = true;
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getUninterpretedOptionFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                this.allowAlias_ = true;
                this.bitField0_ &= -2;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor;
            }

            public EnumOptions getDefaultInstanceForType() {
                return EnumOptions.getDefaultInstance();
            }

            public EnumOptions build() {
                EnumOptions result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public EnumOptions buildPartial() {
                EnumOptions result = new EnumOptions((GeneratedMessage.ExtendableBuilder) this);
                int to_bitField0_ = 0;
                if ((this.bitField0_ & 1) == 1) {
                    to_bitField0_ = 0 | 1;
                }
                boolean unused = result.allowAlias_ = this.allowAlias_;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 2) == 2) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                        this.bitField0_ &= -3;
                    }
                    List unused2 = result.uninterpretedOption_ = this.uninterpretedOption_;
                } else {
                    List unused3 = result.uninterpretedOption_ = repeatedFieldBuilder.build();
                }
                int unused4 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof EnumOptions) {
                    return mergeFrom((EnumOptions) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(EnumOptions other) {
                if (other == EnumOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAllowAlias()) {
                    setAllowAlias(other.getAllowAlias());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        onChanged();
                    }
                } else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = null;
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= -3;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getUninterpretedOptionFieldBuilder();
                        }
                        this.uninterpretedOptionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                mergeExtensionFields(other);
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                    if (!getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                if (extensionsAreInitialized() == 0) {
                    return false;
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    EnumOptions parsedMessage = EnumOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    EnumOptions parsedMessage2 = (EnumOptions) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((EnumOptions) null);
                    }
                    throw th;
                }
            }

            public boolean hasAllowAlias() {
                return (this.bitField0_ & 1) == 1;
            }

            public boolean getAllowAlias() {
                return this.allowAlias_;
            }

            public Builder setAllowAlias(boolean value) {
                this.bitField0_ |= 1;
                this.allowAlias_ = value;
                onChanged();
                return this;
            }

            public Builder clearAllowAlias() {
                this.bitField0_ &= -2;
                this.allowAlias_ = true;
                onChanged();
                return this;
            }

            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 2) != 2) {
                    this.uninterpretedOption_ = new ArrayList(this.uninterpretedOption_);
                    this.bitField0_ |= 2;
                }
            }

            public List<UninterpretedOption> getUninterpretedOptionList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.uninterpretedOption_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getUninterpretedOptionCount() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public UninterpretedOption getUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllUninterpretedOption(Iterable<? extends UninterpretedOption> values) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    GeneratedMessage.ExtendableBuilder.addAll(values, this.uninterpretedOption_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearUninterpretedOption() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public UninterpretedOption.Builder getUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().getBuilder(index);
            }

            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.uninterpretedOption_);
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }

            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return getUninterpretedOptionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<>(this.uninterpretedOption_, (this.bitField0_ & 2) == 2, getParentForChildren(), isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }

    public static final class EnumValueOptions extends GeneratedMessage.ExtendableMessage<EnumValueOptions> implements EnumValueOptionsOrBuilder {
        public static Parser<EnumValueOptions> PARSER = new AbstractParser<EnumValueOptions>() {
            public EnumValueOptions parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new EnumValueOptions(input, extensionRegistry);
            }
        };
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private static final EnumValueOptions defaultInstance = new EnumValueOptions(true);
        private static final long serialVersionUID = 0;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public List<UninterpretedOption> uninterpretedOption_;
        private final UnknownFieldSet unknownFields;

        private EnumValueOptions(GeneratedMessage.ExtendableBuilder<EnumValueOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private EnumValueOptions(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static EnumValueOptions getDefaultInstance() {
            return defaultInstance;
        }

        public EnumValueOptions getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private EnumValueOptions(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 7994) {
                        if ((mutable_bitField0_ & 1) != 1) {
                            this.uninterpretedOption_ = new ArrayList();
                            mutable_bitField0_ |= 1;
                        }
                        this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 1) == 1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 1) == 1) {
                this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueOptions.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<EnumValueOptions> getParserForType() {
            return PARSER;
        }

        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }

        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }

        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }

        public UninterpretedOption getUninterpretedOption(int index) {
            return this.uninterpretedOption_.get(index);
        }

        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
            return this.uninterpretedOption_.get(index);
        }

        private void initFields() {
            this.uninterpretedOption_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                if (!getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (extensionsAreInitialized() == 0) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter = newExtensionWriter();
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            newExtensionWriter.writeUntil(536870912, output);
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            int size3 = size2 + extensionsSerializedSize() + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static EnumValueOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumValueOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumValueOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static EnumValueOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static EnumValueOptions parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumValueOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static EnumValueOptions parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static EnumValueOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static EnumValueOptions parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static EnumValueOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(EnumValueOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.ExtendableBuilder<EnumValueOptions, Builder> implements EnumValueOptionsOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            private List<UninterpretedOption> uninterpretedOption_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueOptions.class, Builder.class);
            }

            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getUninterpretedOptionFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor;
            }

            public EnumValueOptions getDefaultInstanceForType() {
                return EnumValueOptions.getDefaultInstance();
            }

            public EnumValueOptions build() {
                EnumValueOptions result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public EnumValueOptions buildPartial() {
                EnumValueOptions result = new EnumValueOptions((GeneratedMessage.ExtendableBuilder) this);
                int i = this.bitField0_;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 1) == 1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                        this.bitField0_ &= -2;
                    }
                    List unused = result.uninterpretedOption_ = this.uninterpretedOption_;
                } else {
                    List unused2 = result.uninterpretedOption_ = repeatedFieldBuilder.build();
                }
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof EnumValueOptions) {
                    return mergeFrom((EnumValueOptions) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(EnumValueOptions other) {
                if (other == EnumValueOptions.getDefaultInstance()) {
                    return this;
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= -2;
                        } else {
                            ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        onChanged();
                    }
                } else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = null;
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= -2;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getUninterpretedOptionFieldBuilder();
                        }
                        this.uninterpretedOptionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                mergeExtensionFields(other);
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                    if (!getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                if (extensionsAreInitialized() == 0) {
                    return false;
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    EnumValueOptions parsedMessage = EnumValueOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    EnumValueOptions parsedMessage2 = (EnumValueOptions) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((EnumValueOptions) null);
                    }
                    throw th;
                }
            }

            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 1) != 1) {
                    this.uninterpretedOption_ = new ArrayList(this.uninterpretedOption_);
                    this.bitField0_ |= 1;
                }
            }

            public List<UninterpretedOption> getUninterpretedOptionList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.uninterpretedOption_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getUninterpretedOptionCount() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public UninterpretedOption getUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllUninterpretedOption(Iterable<? extends UninterpretedOption> values) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    GeneratedMessage.ExtendableBuilder.addAll(values, this.uninterpretedOption_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearUninterpretedOption() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public UninterpretedOption.Builder getUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().getBuilder(index);
            }

            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.uninterpretedOption_);
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }

            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return getUninterpretedOptionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    List<UninterpretedOption> list = this.uninterpretedOption_;
                    boolean z = true;
                    if ((this.bitField0_ & 1) != 1) {
                        z = false;
                    }
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<>(list, z, getParentForChildren(), isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }

    public static final class ServiceOptions extends GeneratedMessage.ExtendableMessage<ServiceOptions> implements ServiceOptionsOrBuilder {
        public static Parser<ServiceOptions> PARSER = new AbstractParser<ServiceOptions>() {
            public ServiceOptions parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ServiceOptions(input, extensionRegistry);
            }
        };
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private static final ServiceOptions defaultInstance = new ServiceOptions(true);
        private static final long serialVersionUID = 0;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public List<UninterpretedOption> uninterpretedOption_;
        private final UnknownFieldSet unknownFields;

        private ServiceOptions(GeneratedMessage.ExtendableBuilder<ServiceOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private ServiceOptions(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static ServiceOptions getDefaultInstance() {
            return defaultInstance;
        }

        public ServiceOptions getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private ServiceOptions(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 7994) {
                        if ((mutable_bitField0_ & 1) != 1) {
                            this.uninterpretedOption_ = new ArrayList();
                            mutable_bitField0_ |= 1;
                        }
                        this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 1) == 1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 1) == 1) {
                this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceOptions.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<ServiceOptions> getParserForType() {
            return PARSER;
        }

        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }

        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }

        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }

        public UninterpretedOption getUninterpretedOption(int index) {
            return this.uninterpretedOption_.get(index);
        }

        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
            return this.uninterpretedOption_.get(index);
        }

        private void initFields() {
            this.uninterpretedOption_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                if (!getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (extensionsAreInitialized() == 0) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter = newExtensionWriter();
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            newExtensionWriter.writeUntil(536870912, output);
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            int size3 = size2 + extensionsSerializedSize() + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static ServiceOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ServiceOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ServiceOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ServiceOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ServiceOptions parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static ServiceOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static ServiceOptions parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static ServiceOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static ServiceOptions parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static ServiceOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(ServiceOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.ExtendableBuilder<ServiceOptions, Builder> implements ServiceOptionsOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            private List<UninterpretedOption> uninterpretedOption_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceOptions.class, Builder.class);
            }

            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getUninterpretedOptionFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor;
            }

            public ServiceOptions getDefaultInstanceForType() {
                return ServiceOptions.getDefaultInstance();
            }

            public ServiceOptions build() {
                ServiceOptions result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public ServiceOptions buildPartial() {
                ServiceOptions result = new ServiceOptions((GeneratedMessage.ExtendableBuilder) this);
                int i = this.bitField0_;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 1) == 1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                        this.bitField0_ &= -2;
                    }
                    List unused = result.uninterpretedOption_ = this.uninterpretedOption_;
                } else {
                    List unused2 = result.uninterpretedOption_ = repeatedFieldBuilder.build();
                }
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof ServiceOptions) {
                    return mergeFrom((ServiceOptions) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ServiceOptions other) {
                if (other == ServiceOptions.getDefaultInstance()) {
                    return this;
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= -2;
                        } else {
                            ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        onChanged();
                    }
                } else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = null;
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= -2;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getUninterpretedOptionFieldBuilder();
                        }
                        this.uninterpretedOptionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                mergeExtensionFields(other);
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                    if (!getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                if (extensionsAreInitialized() == 0) {
                    return false;
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    ServiceOptions parsedMessage = ServiceOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    ServiceOptions parsedMessage2 = (ServiceOptions) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((ServiceOptions) null);
                    }
                    throw th;
                }
            }

            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 1) != 1) {
                    this.uninterpretedOption_ = new ArrayList(this.uninterpretedOption_);
                    this.bitField0_ |= 1;
                }
            }

            public List<UninterpretedOption> getUninterpretedOptionList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.uninterpretedOption_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getUninterpretedOptionCount() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public UninterpretedOption getUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllUninterpretedOption(Iterable<? extends UninterpretedOption> values) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    GeneratedMessage.ExtendableBuilder.addAll(values, this.uninterpretedOption_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearUninterpretedOption() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public UninterpretedOption.Builder getUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().getBuilder(index);
            }

            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.uninterpretedOption_);
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }

            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return getUninterpretedOptionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    List<UninterpretedOption> list = this.uninterpretedOption_;
                    boolean z = true;
                    if ((this.bitField0_ & 1) != 1) {
                        z = false;
                    }
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<>(list, z, getParentForChildren(), isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }

    public static final class MethodOptions extends GeneratedMessage.ExtendableMessage<MethodOptions> implements MethodOptionsOrBuilder {
        public static Parser<MethodOptions> PARSER = new AbstractParser<MethodOptions>() {
            public MethodOptions parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new MethodOptions(input, extensionRegistry);
            }
        };
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private static final MethodOptions defaultInstance = new MethodOptions(true);
        private static final long serialVersionUID = 0;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public List<UninterpretedOption> uninterpretedOption_;
        private final UnknownFieldSet unknownFields;

        private MethodOptions(GeneratedMessage.ExtendableBuilder<MethodOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private MethodOptions(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static MethodOptions getDefaultInstance() {
            return defaultInstance;
        }

        public MethodOptions getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private MethodOptions(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 7994) {
                        if ((mutable_bitField0_ & 1) != 1) {
                            this.uninterpretedOption_ = new ArrayList();
                            mutable_bitField0_ |= 1;
                        }
                        this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 1) == 1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 1) == 1) {
                this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_MethodOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodOptions.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<MethodOptions> getParserForType() {
            return PARSER;
        }

        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }

        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }

        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }

        public UninterpretedOption getUninterpretedOption(int index) {
            return this.uninterpretedOption_.get(index);
        }

        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
            return this.uninterpretedOption_.get(index);
        }

        private void initFields() {
            this.uninterpretedOption_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                if (!getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (extensionsAreInitialized() == 0) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter = newExtensionWriter();
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            newExtensionWriter.writeUntil(536870912, output);
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.uninterpretedOption_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            int size3 = size2 + extensionsSerializedSize() + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static MethodOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static MethodOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static MethodOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static MethodOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static MethodOptions parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static MethodOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static MethodOptions parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static MethodOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static MethodOptions parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static MethodOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(MethodOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.ExtendableBuilder<MethodOptions, Builder> implements MethodOptionsOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            private List<UninterpretedOption> uninterpretedOption_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_MethodOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodOptions.class, Builder.class);
            }

            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getUninterpretedOptionFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor;
            }

            public MethodOptions getDefaultInstanceForType() {
                return MethodOptions.getDefaultInstance();
            }

            public MethodOptions build() {
                MethodOptions result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public MethodOptions buildPartial() {
                MethodOptions result = new MethodOptions((GeneratedMessage.ExtendableBuilder) this);
                int i = this.bitField0_;
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 1) == 1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList(this.uninterpretedOption_);
                        this.bitField0_ &= -2;
                    }
                    List unused = result.uninterpretedOption_ = this.uninterpretedOption_;
                } else {
                    List unused2 = result.uninterpretedOption_ = repeatedFieldBuilder.build();
                }
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof MethodOptions) {
                    return mergeFrom((MethodOptions) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(MethodOptions other) {
                if (other == MethodOptions.getDefaultInstance()) {
                    return this;
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= -2;
                        } else {
                            ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        onChanged();
                    }
                } else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = null;
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= -2;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getUninterpretedOptionFieldBuilder();
                        }
                        this.uninterpretedOptionBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                mergeExtensionFields(other);
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getUninterpretedOptionCount(); i++) {
                    if (!getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                if (extensionsAreInitialized() == 0) {
                    return false;
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    MethodOptions parsedMessage = MethodOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    MethodOptions parsedMessage2 = (MethodOptions) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((MethodOptions) null);
                    }
                    throw th;
                }
            }

            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 1) != 1) {
                    this.uninterpretedOption_ = new ArrayList(this.uninterpretedOption_);
                    this.bitField0_ |= 1;
                }
            }

            public List<UninterpretedOption> getUninterpretedOptionList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.uninterpretedOption_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getUninterpretedOptionCount() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public UninterpretedOption getUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption value) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addUninterpretedOption(UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addUninterpretedOption(int index, UninterpretedOption.Builder builderForValue) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllUninterpretedOption(Iterable<? extends UninterpretedOption> values) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    GeneratedMessage.ExtendableBuilder.addAll(values, this.uninterpretedOption_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearUninterpretedOption() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeUninterpretedOption(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public UninterpretedOption.Builder getUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().getBuilder(index);
            }

            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> repeatedFieldBuilder = this.uninterpretedOptionBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.uninterpretedOption_);
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }

            public UninterpretedOption.Builder addUninterpretedOptionBuilder(int index) {
                return getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }

            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return getUninterpretedOptionFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    List<UninterpretedOption> list = this.uninterpretedOption_;
                    boolean z = true;
                    if ((this.bitField0_ & 1) != 1) {
                        z = false;
                    }
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<>(list, z, getParentForChildren(), isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }

    public static final class UninterpretedOption extends GeneratedMessage implements UninterpretedOptionOrBuilder {
        public static final int AGGREGATE_VALUE_FIELD_NUMBER = 8;
        public static final int DOUBLE_VALUE_FIELD_NUMBER = 6;
        public static final int IDENTIFIER_VALUE_FIELD_NUMBER = 3;
        public static final int NAME_FIELD_NUMBER = 2;
        public static final int NEGATIVE_INT_VALUE_FIELD_NUMBER = 5;
        public static Parser<UninterpretedOption> PARSER = new AbstractParser<UninterpretedOption>() {
            public UninterpretedOption parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new UninterpretedOption(input, extensionRegistry);
            }
        };
        public static final int POSITIVE_INT_VALUE_FIELD_NUMBER = 4;
        public static final int STRING_VALUE_FIELD_NUMBER = 7;
        private static final UninterpretedOption defaultInstance = new UninterpretedOption(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public Object aggregateValue_;
        /* access modifiers changed from: private */
        public int bitField0_;
        /* access modifiers changed from: private */
        public double doubleValue_;
        /* access modifiers changed from: private */
        public Object identifierValue_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        /* access modifiers changed from: private */
        public List<NamePart> name_;
        /* access modifiers changed from: private */
        public long negativeIntValue_;
        /* access modifiers changed from: private */
        public long positiveIntValue_;
        /* access modifiers changed from: private */
        public ByteString stringValue_;
        private final UnknownFieldSet unknownFields;

        public interface NamePartOrBuilder extends MessageOrBuilder {
            boolean getIsExtension();

            String getNamePart();

            ByteString getNamePartBytes();

            boolean hasIsExtension();

            boolean hasNamePart();
        }

        private UninterpretedOption(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private UninterpretedOption(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static UninterpretedOption getDefaultInstance() {
            return defaultInstance;
        }

        public UninterpretedOption getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private UninterpretedOption(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 18) {
                        if ((mutable_bitField0_ & 1) != 1) {
                            this.name_ = new ArrayList();
                            mutable_bitField0_ |= 1;
                        }
                        this.name_.add(input.readMessage(NamePart.PARSER, extensionRegistry));
                    } else if (tag == 26) {
                        this.bitField0_ |= 1;
                        this.identifierValue_ = input.readBytes();
                    } else if (tag == 32) {
                        this.bitField0_ |= 2;
                        this.positiveIntValue_ = input.readUInt64();
                    } else if (tag == 40) {
                        this.bitField0_ |= 4;
                        this.negativeIntValue_ = input.readInt64();
                    } else if (tag == 49) {
                        this.bitField0_ |= 8;
                        this.doubleValue_ = input.readDouble();
                    } else if (tag == 58) {
                        this.bitField0_ |= 16;
                        this.stringValue_ = input.readBytes();
                    } else if (tag == 66) {
                        this.bitField0_ = 32 | this.bitField0_;
                        this.aggregateValue_ = input.readBytes();
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 1) == 1) {
                        this.name_ = Collections.unmodifiableList(this.name_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 1) == 1) {
                this.name_ = Collections.unmodifiableList(this.name_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable.ensureFieldAccessorsInitialized(UninterpretedOption.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<UninterpretedOption> getParserForType() {
            return PARSER;
        }

        public static final class NamePart extends GeneratedMessage implements NamePartOrBuilder {
            public static final int IS_EXTENSION_FIELD_NUMBER = 2;
            public static final int NAME_PART_FIELD_NUMBER = 1;
            public static Parser<NamePart> PARSER = new AbstractParser<NamePart>() {
                public NamePart parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new NamePart(input, extensionRegistry);
                }
            };
            private static final NamePart defaultInstance = new NamePart(true);
            private static final long serialVersionUID = 0;
            /* access modifiers changed from: private */
            public int bitField0_;
            /* access modifiers changed from: private */
            public boolean isExtension_;
            private byte memoizedIsInitialized;
            private int memoizedSerializedSize;
            /* access modifiers changed from: private */
            public Object namePart_;
            private final UnknownFieldSet unknownFields;

            private NamePart(GeneratedMessage.Builder<?> builder) {
                super(builder);
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = builder.getUnknownFields();
            }

            private NamePart(boolean noInit) {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = UnknownFieldSet.getDefaultInstance();
            }

            public static NamePart getDefaultInstance() {
                return defaultInstance;
            }

            public NamePart getDefaultInstanceForType() {
                return defaultInstance;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            /* Debug info: failed to restart local var, previous not found, register: 5 */
            private NamePart(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                initFields();
                UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
                boolean done = false;
                while (!done) {
                    try {
                        int tag = input.readTag();
                        if (tag == 0) {
                            done = true;
                        } else if (tag == 10) {
                            this.bitField0_ |= 1;
                            this.namePart_ = input.readBytes();
                        } else if (tag == 16) {
                            this.bitField0_ |= 2;
                            this.isExtension_ = input.readBool();
                        } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                            done = true;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                    } catch (Throwable th) {
                        this.unknownFields = unknownFields2.build();
                        makeExtensionsImmutable();
                        throw th;
                    }
                }
                this.unknownFields = unknownFields2.build();
                makeExtensionsImmutable();
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable.ensureFieldAccessorsInitialized(NamePart.class, Builder.class);
            }

            static {
                defaultInstance.initFields();
            }

            public Parser<NamePart> getParserForType() {
                return PARSER;
            }

            public boolean hasNamePart() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getNamePart() {
                Object ref = this.namePart_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.namePart_ = s;
                }
                return s;
            }

            public ByteString getNamePartBytes() {
                Object ref = this.namePart_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.namePart_ = b;
                return b;
            }

            public boolean hasIsExtension() {
                return (this.bitField0_ & 2) == 2;
            }

            public boolean getIsExtension() {
                return this.isExtension_;
            }

            private void initFields() {
                this.namePart_ = "";
                this.isExtension_ = false;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized != -1) {
                    return isInitialized == 1;
                }
                if (!hasNamePart()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                } else if (!hasIsExtension()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                } else {
                    this.memoizedIsInitialized = 1;
                    return true;
                }
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                getSerializedSize();
                if ((this.bitField0_ & 1) == 1) {
                    output.writeBytes(1, getNamePartBytes());
                }
                if ((this.bitField0_ & 2) == 2) {
                    output.writeBool(2, this.isExtension_);
                }
                getUnknownFields().writeTo(output);
            }

            public int getSerializedSize() {
                int size = this.memoizedSerializedSize;
                if (size != -1) {
                    return size;
                }
                int size2 = 0;
                if ((this.bitField0_ & 1) == 1) {
                    size2 = 0 + CodedOutputStream.computeBytesSize(1, getNamePartBytes());
                }
                if ((this.bitField0_ & 2) == 2) {
                    size2 += CodedOutputStream.computeBoolSize(2, this.isExtension_);
                }
                int size3 = size2 + getUnknownFields().getSerializedSize();
                this.memoizedSerializedSize = size3;
                return size3;
            }

            /* access modifiers changed from: protected */
            public Object writeReplace() throws ObjectStreamException {
                return super.writeReplace();
            }

            public static NamePart parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static NamePart parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static NamePart parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static NamePart parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static NamePart parseFrom(InputStream input) throws IOException {
                return PARSER.parseFrom(input);
            }

            public static NamePart parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseFrom(input, extensionRegistry);
            }

            public static NamePart parseDelimitedFrom(InputStream input) throws IOException {
                return PARSER.parseDelimitedFrom(input);
            }

            public static NamePart parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseDelimitedFrom(input, extensionRegistry);
            }

            public static NamePart parseFrom(CodedInputStream input) throws IOException {
                return PARSER.parseFrom(input);
            }

            public static NamePart parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseFrom(input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return Builder.create();
            }

            public Builder newBuilderForType() {
                return newBuilder();
            }

            public static Builder newBuilder(NamePart prototype) {
                return newBuilder().mergeFrom(prototype);
            }

            public Builder toBuilder() {
                return newBuilder(this);
            }

            /* access modifiers changed from: protected */
            public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
                return new Builder(parent);
            }

            public static final class Builder extends GeneratedMessage.Builder<Builder> implements NamePartOrBuilder {
                private int bitField0_;
                private boolean isExtension_;
                private Object namePart_;

                public static final Descriptors.Descriptor getDescriptor() {
                    return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
                }

                /* access modifiers changed from: protected */
                public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                    return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable.ensureFieldAccessorsInitialized(NamePart.class, Builder.class);
                }

                private Builder() {
                    this.namePart_ = "";
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessage.BuilderParent parent) {
                    super(parent);
                    this.namePart_ = "";
                    maybeForceBuilderInitialization();
                }

                private void maybeForceBuilderInitialization() {
                    boolean z = GeneratedMessage.alwaysUseFieldBuilders;
                }

                /* access modifiers changed from: private */
                public static Builder create() {
                    return new Builder();
                }

                public Builder clear() {
                    super.clear();
                    this.namePart_ = "";
                    this.bitField0_ &= -2;
                    this.isExtension_ = false;
                    this.bitField0_ &= -3;
                    return this;
                }

                public Builder clone() {
                    return create().mergeFrom(buildPartial());
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
                }

                public NamePart getDefaultInstanceForType() {
                    return NamePart.getDefaultInstance();
                }

                public NamePart build() {
                    NamePart result = buildPartial();
                    if (result.isInitialized()) {
                        return result;
                    }
                    throw newUninitializedMessageException(result);
                }

                public NamePart buildPartial() {
                    NamePart result = new NamePart((GeneratedMessage.Builder) this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 1) == 1) {
                        to_bitField0_ = 0 | 1;
                    }
                    Object unused = result.namePart_ = this.namePart_;
                    if ((from_bitField0_ & 2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    boolean unused2 = result.isExtension_ = this.isExtension_;
                    int unused3 = result.bitField0_ = to_bitField0_;
                    onBuilt();
                    return result;
                }

                public Builder mergeFrom(Message other) {
                    if (other instanceof NamePart) {
                        return mergeFrom((NamePart) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(NamePart other) {
                    if (other == NamePart.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasNamePart()) {
                        this.bitField0_ |= 1;
                        this.namePart_ = other.namePart_;
                        onChanged();
                    }
                    if (other.hasIsExtension()) {
                        setIsExtension(other.getIsExtension());
                    }
                    mergeUnknownFields(other.getUnknownFields());
                    return this;
                }

                public final boolean isInitialized() {
                    if (hasNamePart() && hasIsExtension()) {
                        return true;
                    }
                    return false;
                }

                /* Debug info: failed to restart local var, previous not found, register: 3 */
                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    try {
                        NamePart parsedMessage = NamePart.PARSER.parsePartialFrom(input, extensionRegistry);
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        NamePart parsedMessage2 = (NamePart) e.getUnfinishedMessage();
                        throw e;
                    } catch (Throwable th) {
                        if (0 != 0) {
                            mergeFrom((NamePart) null);
                        }
                        throw th;
                    }
                }

                public boolean hasNamePart() {
                    return (this.bitField0_ & 1) == 1;
                }

                public String getNamePart() {
                    Object ref = this.namePart_;
                    if (ref instanceof String) {
                        return (String) ref;
                    }
                    String s = ((ByteString) ref).toStringUtf8();
                    this.namePart_ = s;
                    return s;
                }

                public ByteString getNamePartBytes() {
                    Object ref = this.namePart_;
                    if (!(ref instanceof String)) {
                        return (ByteString) ref;
                    }
                    ByteString b = ByteString.copyFromUtf8((String) ref);
                    this.namePart_ = b;
                    return b;
                }

                public Builder setNamePart(String value) {
                    if (value != null) {
                        this.bitField0_ |= 1;
                        this.namePart_ = value;
                        onChanged();
                        return this;
                    }
                    throw new NullPointerException();
                }

                public Builder clearNamePart() {
                    this.bitField0_ &= -2;
                    this.namePart_ = NamePart.getDefaultInstance().getNamePart();
                    onChanged();
                    return this;
                }

                public Builder setNamePartBytes(ByteString value) {
                    if (value != null) {
                        this.bitField0_ |= 1;
                        this.namePart_ = value;
                        onChanged();
                        return this;
                    }
                    throw new NullPointerException();
                }

                public boolean hasIsExtension() {
                    return (this.bitField0_ & 2) == 2;
                }

                public boolean getIsExtension() {
                    return this.isExtension_;
                }

                public Builder setIsExtension(boolean value) {
                    this.bitField0_ |= 2;
                    this.isExtension_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearIsExtension() {
                    this.bitField0_ &= -3;
                    this.isExtension_ = false;
                    onChanged();
                    return this;
                }
            }
        }

        public List<NamePart> getNameList() {
            return this.name_;
        }

        public List<? extends NamePartOrBuilder> getNameOrBuilderList() {
            return this.name_;
        }

        public int getNameCount() {
            return this.name_.size();
        }

        public NamePart getName(int index) {
            return this.name_.get(index);
        }

        public NamePartOrBuilder getNameOrBuilder(int index) {
            return this.name_.get(index);
        }

        public boolean hasIdentifierValue() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getIdentifierValue() {
            Object ref = this.identifierValue_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.identifierValue_ = s;
            }
            return s;
        }

        public ByteString getIdentifierValueBytes() {
            Object ref = this.identifierValue_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.identifierValue_ = b;
            return b;
        }

        public boolean hasPositiveIntValue() {
            return (this.bitField0_ & 2) == 2;
        }

        public long getPositiveIntValue() {
            return this.positiveIntValue_;
        }

        public boolean hasNegativeIntValue() {
            return (this.bitField0_ & 4) == 4;
        }

        public long getNegativeIntValue() {
            return this.negativeIntValue_;
        }

        public boolean hasDoubleValue() {
            return (this.bitField0_ & 8) == 8;
        }

        public double getDoubleValue() {
            return this.doubleValue_;
        }

        public boolean hasStringValue() {
            return (this.bitField0_ & 16) == 16;
        }

        public ByteString getStringValue() {
            return this.stringValue_;
        }

        public boolean hasAggregateValue() {
            return (this.bitField0_ & 32) == 32;
        }

        public String getAggregateValue() {
            Object ref = this.aggregateValue_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.aggregateValue_ = s;
            }
            return s;
        }

        public ByteString getAggregateValueBytes() {
            Object ref = this.aggregateValue_;
            if (!(ref instanceof String)) {
                return (ByteString) ref;
            }
            ByteString b = ByteString.copyFromUtf8((String) ref);
            this.aggregateValue_ = b;
            return b;
        }

        private void initFields() {
            this.name_ = Collections.emptyList();
            this.identifierValue_ = "";
            this.positiveIntValue_ = 0;
            this.negativeIntValue_ = 0;
            this.doubleValue_ = 0.0d;
            this.stringValue_ = ByteString.EMPTY;
            this.aggregateValue_ = "";
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < getNameCount(); i++) {
                if (!getName(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            for (int i = 0; i < this.name_.size(); i++) {
                output.writeMessage(2, this.name_.get(i));
            }
            if ((this.bitField0_ & 1) == 1) {
                output.writeBytes(3, getIdentifierValueBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeUInt64(4, this.positiveIntValue_);
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeInt64(5, this.negativeIntValue_);
            }
            if ((this.bitField0_ & 8) == 8) {
                output.writeDouble(6, this.doubleValue_);
            }
            if ((this.bitField0_ & 16) == 16) {
                output.writeBytes(7, this.stringValue_);
            }
            if ((this.bitField0_ & 32) == 32) {
                output.writeBytes(8, getAggregateValueBytes());
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.name_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(2, this.name_.get(i));
            }
            if ((this.bitField0_ & 1) == 1) {
                size2 += CodedOutputStream.computeBytesSize(3, getIdentifierValueBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeUInt64Size(4, this.positiveIntValue_);
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeInt64Size(5, this.negativeIntValue_);
            }
            if ((this.bitField0_ & 8) == 8) {
                size2 += CodedOutputStream.computeDoubleSize(6, this.doubleValue_);
            }
            if ((this.bitField0_ & 16) == 16) {
                size2 += CodedOutputStream.computeBytesSize(7, this.stringValue_);
            }
            if ((this.bitField0_ & 32) == 32) {
                size2 += CodedOutputStream.computeBytesSize(8, getAggregateValueBytes());
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static UninterpretedOption parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static UninterpretedOption parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static UninterpretedOption parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static UninterpretedOption parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static UninterpretedOption parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static UninterpretedOption parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static UninterpretedOption parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static UninterpretedOption parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static UninterpretedOption parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static UninterpretedOption parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(UninterpretedOption prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements UninterpretedOptionOrBuilder {
            private Object aggregateValue_;
            private int bitField0_;
            private double doubleValue_;
            private Object identifierValue_;
            private RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> nameBuilder_;
            private List<NamePart> name_;
            private long negativeIntValue_;
            private long positiveIntValue_;
            private ByteString stringValue_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable.ensureFieldAccessorsInitialized(UninterpretedOption.class, Builder.class);
            }

            private Builder() {
                this.name_ = Collections.emptyList();
                this.identifierValue_ = "";
                this.stringValue_ = ByteString.EMPTY;
                this.aggregateValue_ = "";
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.name_ = Collections.emptyList();
                this.identifierValue_ = "";
                this.stringValue_ = ByteString.EMPTY;
                this.aggregateValue_ = "";
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getNameFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.name_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                } else {
                    repeatedFieldBuilder.clear();
                }
                this.identifierValue_ = "";
                this.bitField0_ &= -3;
                this.positiveIntValue_ = 0;
                this.bitField0_ &= -5;
                this.negativeIntValue_ = 0;
                this.bitField0_ &= -9;
                this.doubleValue_ = 0.0d;
                this.bitField0_ &= -17;
                this.stringValue_ = ByteString.EMPTY;
                this.bitField0_ &= -33;
                this.aggregateValue_ = "";
                this.bitField0_ &= -65;
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor;
            }

            public UninterpretedOption getDefaultInstanceForType() {
                return UninterpretedOption.getDefaultInstance();
            }

            public UninterpretedOption build() {
                UninterpretedOption result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public UninterpretedOption buildPartial() {
                UninterpretedOption result = new UninterpretedOption((GeneratedMessage.Builder) this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 1) == 1) {
                        this.name_ = Collections.unmodifiableList(this.name_);
                        this.bitField0_ &= -2;
                    }
                    List unused = result.name_ = this.name_;
                } else {
                    List unused2 = result.name_ = repeatedFieldBuilder.build();
                }
                if ((from_bitField0_ & 2) == 2) {
                    to_bitField0_ = 0 | 1;
                }
                Object unused3 = result.identifierValue_ = this.identifierValue_;
                if ((from_bitField0_ & 4) == 4) {
                    to_bitField0_ |= 2;
                }
                long unused4 = result.positiveIntValue_ = this.positiveIntValue_;
                if ((from_bitField0_ & 8) == 8) {
                    to_bitField0_ |= 4;
                }
                long unused5 = result.negativeIntValue_ = this.negativeIntValue_;
                if ((from_bitField0_ & 16) == 16) {
                    to_bitField0_ |= 8;
                }
                double unused6 = result.doubleValue_ = this.doubleValue_;
                if ((from_bitField0_ & 32) == 32) {
                    to_bitField0_ |= 16;
                }
                ByteString unused7 = result.stringValue_ = this.stringValue_;
                if ((from_bitField0_ & 64) == 64) {
                    to_bitField0_ |= 32;
                }
                Object unused8 = result.aggregateValue_ = this.aggregateValue_;
                int unused9 = result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof UninterpretedOption) {
                    return mergeFrom((UninterpretedOption) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(UninterpretedOption other) {
                if (other == UninterpretedOption.getDefaultInstance()) {
                    return this;
                }
                if (this.nameBuilder_ == null) {
                    if (!other.name_.isEmpty()) {
                        if (this.name_.isEmpty()) {
                            this.name_ = other.name_;
                            this.bitField0_ &= -2;
                        } else {
                            ensureNameIsMutable();
                            this.name_.addAll(other.name_);
                        }
                        onChanged();
                    }
                } else if (!other.name_.isEmpty()) {
                    if (this.nameBuilder_.isEmpty()) {
                        this.nameBuilder_.dispose();
                        RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = null;
                        this.nameBuilder_ = null;
                        this.name_ = other.name_;
                        this.bitField0_ &= -2;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getNameFieldBuilder();
                        }
                        this.nameBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.nameBuilder_.addAllMessages(other.name_);
                    }
                }
                if (other.hasIdentifierValue()) {
                    this.bitField0_ |= 2;
                    this.identifierValue_ = other.identifierValue_;
                    onChanged();
                }
                if (other.hasPositiveIntValue()) {
                    setPositiveIntValue(other.getPositiveIntValue());
                }
                if (other.hasNegativeIntValue()) {
                    setNegativeIntValue(other.getNegativeIntValue());
                }
                if (other.hasDoubleValue()) {
                    setDoubleValue(other.getDoubleValue());
                }
                if (other.hasStringValue()) {
                    setStringValue(other.getStringValue());
                }
                if (other.hasAggregateValue()) {
                    this.bitField0_ |= 64;
                    this.aggregateValue_ = other.aggregateValue_;
                    onChanged();
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                for (int i = 0; i < getNameCount(); i++) {
                    if (!getName(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    UninterpretedOption parsedMessage = UninterpretedOption.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    UninterpretedOption parsedMessage2 = (UninterpretedOption) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((UninterpretedOption) null);
                    }
                    throw th;
                }
            }

            private void ensureNameIsMutable() {
                if ((this.bitField0_ & 1) != 1) {
                    this.name_ = new ArrayList(this.name_);
                    this.bitField0_ |= 1;
                }
            }

            public List<NamePart> getNameList() {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.name_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getNameCount() {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.name_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public NamePart getName(int index) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.name_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setName(int index, NamePart value) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureNameIsMutable();
                    this.name_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setName(int index, NamePart.Builder builderForValue) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNameIsMutable();
                    this.name_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addName(NamePart value) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureNameIsMutable();
                    this.name_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addName(int index, NamePart value) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureNameIsMutable();
                    this.name_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addName(NamePart.Builder builderForValue) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNameIsMutable();
                    this.name_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addName(int index, NamePart.Builder builderForValue) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNameIsMutable();
                    this.name_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllName(Iterable<? extends NamePart> values) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNameIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.name_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearName() {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.name_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeName(int index) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureNameIsMutable();
                    this.name_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public NamePart.Builder getNameBuilder(int index) {
                return getNameFieldBuilder().getBuilder(index);
            }

            public NamePartOrBuilder getNameOrBuilder(int index) {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.name_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends NamePartOrBuilder> getNameOrBuilderList() {
                RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> repeatedFieldBuilder = this.nameBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.name_);
            }

            public NamePart.Builder addNameBuilder() {
                return getNameFieldBuilder().addBuilder(NamePart.getDefaultInstance());
            }

            public NamePart.Builder addNameBuilder(int index) {
                return getNameFieldBuilder().addBuilder(index, NamePart.getDefaultInstance());
            }

            public List<NamePart.Builder> getNameBuilderList() {
                return getNameFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> getNameFieldBuilder() {
                if (this.nameBuilder_ == null) {
                    List<NamePart> list = this.name_;
                    boolean z = true;
                    if ((this.bitField0_ & 1) != 1) {
                        z = false;
                    }
                    this.nameBuilder_ = new RepeatedFieldBuilder<>(list, z, getParentForChildren(), isClean());
                    this.name_ = null;
                }
                return this.nameBuilder_;
            }

            public boolean hasIdentifierValue() {
                return (this.bitField0_ & 2) == 2;
            }

            public String getIdentifierValue() {
                Object ref = this.identifierValue_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.identifierValue_ = s;
                return s;
            }

            public ByteString getIdentifierValueBytes() {
                Object ref = this.identifierValue_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.identifierValue_ = b;
                return b;
            }

            public Builder setIdentifierValue(String value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.identifierValue_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearIdentifierValue() {
                this.bitField0_ &= -3;
                this.identifierValue_ = UninterpretedOption.getDefaultInstance().getIdentifierValue();
                onChanged();
                return this;
            }

            public Builder setIdentifierValueBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 2;
                    this.identifierValue_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public boolean hasPositiveIntValue() {
                return (this.bitField0_ & 4) == 4;
            }

            public long getPositiveIntValue() {
                return this.positiveIntValue_;
            }

            public Builder setPositiveIntValue(long value) {
                this.bitField0_ |= 4;
                this.positiveIntValue_ = value;
                onChanged();
                return this;
            }

            public Builder clearPositiveIntValue() {
                this.bitField0_ &= -5;
                this.positiveIntValue_ = 0;
                onChanged();
                return this;
            }

            public boolean hasNegativeIntValue() {
                return (this.bitField0_ & 8) == 8;
            }

            public long getNegativeIntValue() {
                return this.negativeIntValue_;
            }

            public Builder setNegativeIntValue(long value) {
                this.bitField0_ |= 8;
                this.negativeIntValue_ = value;
                onChanged();
                return this;
            }

            public Builder clearNegativeIntValue() {
                this.bitField0_ &= -9;
                this.negativeIntValue_ = 0;
                onChanged();
                return this;
            }

            public boolean hasDoubleValue() {
                return (this.bitField0_ & 16) == 16;
            }

            public double getDoubleValue() {
                return this.doubleValue_;
            }

            public Builder setDoubleValue(double value) {
                this.bitField0_ |= 16;
                this.doubleValue_ = value;
                onChanged();
                return this;
            }

            public Builder clearDoubleValue() {
                this.bitField0_ &= -17;
                this.doubleValue_ = 0.0d;
                onChanged();
                return this;
            }

            public boolean hasStringValue() {
                return (this.bitField0_ & 32) == 32;
            }

            public ByteString getStringValue() {
                return this.stringValue_;
            }

            public Builder setStringValue(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 32;
                    this.stringValue_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearStringValue() {
                this.bitField0_ &= -33;
                this.stringValue_ = UninterpretedOption.getDefaultInstance().getStringValue();
                onChanged();
                return this;
            }

            public boolean hasAggregateValue() {
                return (this.bitField0_ & 64) == 64;
            }

            public String getAggregateValue() {
                Object ref = this.aggregateValue_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                String s = ((ByteString) ref).toStringUtf8();
                this.aggregateValue_ = s;
                return s;
            }

            public ByteString getAggregateValueBytes() {
                Object ref = this.aggregateValue_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.aggregateValue_ = b;
                return b;
            }

            public Builder setAggregateValue(String value) {
                if (value != null) {
                    this.bitField0_ |= 64;
                    this.aggregateValue_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }

            public Builder clearAggregateValue() {
                this.bitField0_ &= -65;
                this.aggregateValue_ = UninterpretedOption.getDefaultInstance().getAggregateValue();
                onChanged();
                return this;
            }

            public Builder setAggregateValueBytes(ByteString value) {
                if (value != null) {
                    this.bitField0_ |= 64;
                    this.aggregateValue_ = value;
                    onChanged();
                    return this;
                }
                throw new NullPointerException();
            }
        }
    }

    public static final class SourceCodeInfo extends GeneratedMessage implements SourceCodeInfoOrBuilder {
        public static final int LOCATION_FIELD_NUMBER = 1;
        public static Parser<SourceCodeInfo> PARSER = new AbstractParser<SourceCodeInfo>() {
            public SourceCodeInfo parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new SourceCodeInfo(input, extensionRegistry);
            }
        };
        private static final SourceCodeInfo defaultInstance = new SourceCodeInfo(true);
        private static final long serialVersionUID = 0;
        /* access modifiers changed from: private */
        public List<Location> location_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private final UnknownFieldSet unknownFields;

        public interface LocationOrBuilder extends MessageOrBuilder {
            String getLeadingComments();

            ByteString getLeadingCommentsBytes();

            int getPath(int i);

            int getPathCount();

            List<Integer> getPathList();

            int getSpan(int i);

            int getSpanCount();

            List<Integer> getSpanList();

            String getTrailingComments();

            ByteString getTrailingCommentsBytes();

            boolean hasLeadingComments();

            boolean hasTrailingComments();
        }

        private SourceCodeInfo(GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }

        private SourceCodeInfo(boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }

        public static SourceCodeInfo getDefaultInstance() {
            return defaultInstance;
        }

        public SourceCodeInfo getDefaultInstanceForType() {
            return defaultInstance;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        private SourceCodeInfo(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    int tag = input.readTag();
                    if (tag == 0) {
                        done = true;
                    } else if (tag == 10) {
                        if ((mutable_bitField0_ & 1) != 1) {
                            this.location_ = new ArrayList();
                            mutable_bitField0_ |= 1;
                        }
                        this.location_.add(input.readMessage(Location.PARSER, extensionRegistry));
                    } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                        done = true;
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                } catch (Throwable th) {
                    if ((mutable_bitField0_ & 1) == 1) {
                        this.location_ = Collections.unmodifiableList(this.location_);
                    }
                    this.unknownFields = unknownFields2.build();
                    makeExtensionsImmutable();
                    throw th;
                }
            }
            if ((mutable_bitField0_ & 1) == 1) {
                this.location_ = Collections.unmodifiableList(this.location_);
            }
            this.unknownFields = unknownFields2.build();
            makeExtensionsImmutable();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor;
        }

        /* access modifiers changed from: protected */
        public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(SourceCodeInfo.class, Builder.class);
        }

        static {
            defaultInstance.initFields();
        }

        public Parser<SourceCodeInfo> getParserForType() {
            return PARSER;
        }

        public static final class Location extends GeneratedMessage implements LocationOrBuilder {
            public static final int LEADING_COMMENTS_FIELD_NUMBER = 3;
            public static Parser<Location> PARSER = new AbstractParser<Location>() {
                public Location parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new Location(input, extensionRegistry);
                }
            };
            public static final int PATH_FIELD_NUMBER = 1;
            public static final int SPAN_FIELD_NUMBER = 2;
            public static final int TRAILING_COMMENTS_FIELD_NUMBER = 4;
            private static final Location defaultInstance = new Location(true);
            private static final long serialVersionUID = 0;
            /* access modifiers changed from: private */
            public int bitField0_;
            /* access modifiers changed from: private */
            public Object leadingComments_;
            private byte memoizedIsInitialized;
            private int memoizedSerializedSize;
            private int pathMemoizedSerializedSize;
            /* access modifiers changed from: private */
            public List<Integer> path_;
            private int spanMemoizedSerializedSize;
            /* access modifiers changed from: private */
            public List<Integer> span_;
            /* access modifiers changed from: private */
            public Object trailingComments_;
            private final UnknownFieldSet unknownFields;

            private Location(GeneratedMessage.Builder<?> builder) {
                super(builder);
                this.pathMemoizedSerializedSize = -1;
                this.spanMemoizedSerializedSize = -1;
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = builder.getUnknownFields();
            }

            private Location(boolean noInit) {
                this.pathMemoizedSerializedSize = -1;
                this.spanMemoizedSerializedSize = -1;
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = UnknownFieldSet.getDefaultInstance();
            }

            public static Location getDefaultInstance() {
                return defaultInstance;
            }

            public Location getDefaultInstanceForType() {
                return defaultInstance;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            /* Debug info: failed to restart local var, previous not found, register: 10 */
            private Location(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this.pathMemoizedSerializedSize = -1;
                this.spanMemoizedSerializedSize = -1;
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                initFields();
                int mutable_bitField0_ = 0;
                UnknownFieldSet.Builder unknownFields2 = UnknownFieldSet.newBuilder();
                boolean done = false;
                while (!done) {
                    try {
                        int tag = input.readTag();
                        if (tag == 0) {
                            done = true;
                        } else if (tag == 8) {
                            if ((mutable_bitField0_ & 1) != 1) {
                                this.path_ = new ArrayList();
                                mutable_bitField0_ |= 1;
                            }
                            this.path_.add(Integer.valueOf(input.readInt32()));
                        } else if (tag == 10) {
                            int limit = input.pushLimit(input.readRawVarint32());
                            if ((mutable_bitField0_ & 1) != 1 && input.getBytesUntilLimit() > 0) {
                                this.path_ = new ArrayList();
                                mutable_bitField0_ |= 1;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.path_.add(Integer.valueOf(input.readInt32()));
                            }
                            input.popLimit(limit);
                        } else if (tag == 16) {
                            if ((mutable_bitField0_ & 2) != 2) {
                                this.span_ = new ArrayList();
                                mutable_bitField0_ |= 2;
                            }
                            this.span_.add(Integer.valueOf(input.readInt32()));
                        } else if (tag == 18) {
                            int limit2 = input.pushLimit(input.readRawVarint32());
                            if ((mutable_bitField0_ & 2) != 2 && input.getBytesUntilLimit() > 0) {
                                this.span_ = new ArrayList();
                                mutable_bitField0_ |= 2;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.span_.add(Integer.valueOf(input.readInt32()));
                            }
                            input.popLimit(limit2);
                        } else if (tag == 26) {
                            this.bitField0_ |= 1;
                            this.leadingComments_ = input.readBytes();
                        } else if (tag == 34) {
                            this.bitField0_ |= 2;
                            this.trailingComments_ = input.readBytes();
                        } else if (!parseUnknownField(input, unknownFields2, extensionRegistry, tag)) {
                            done = true;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                    } catch (Throwable th) {
                        if ((mutable_bitField0_ & 1) == 1) {
                            this.path_ = Collections.unmodifiableList(this.path_);
                        }
                        if ((mutable_bitField0_ & 2) == 2) {
                            this.span_ = Collections.unmodifiableList(this.span_);
                        }
                        this.unknownFields = unknownFields2.build();
                        makeExtensionsImmutable();
                        throw th;
                    }
                }
                if ((mutable_bitField0_ & 1) == 1) {
                    this.path_ = Collections.unmodifiableList(this.path_);
                }
                if ((mutable_bitField0_ & 2) == 2) {
                    this.span_ = Collections.unmodifiableList(this.span_);
                }
                this.unknownFields = unknownFields2.build();
                makeExtensionsImmutable();
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable.ensureFieldAccessorsInitialized(Location.class, Builder.class);
            }

            static {
                defaultInstance.initFields();
            }

            public Parser<Location> getParserForType() {
                return PARSER;
            }

            public List<Integer> getPathList() {
                return this.path_;
            }

            public int getPathCount() {
                return this.path_.size();
            }

            public int getPath(int index) {
                return this.path_.get(index).intValue();
            }

            public List<Integer> getSpanList() {
                return this.span_;
            }

            public int getSpanCount() {
                return this.span_.size();
            }

            public int getSpan(int index) {
                return this.span_.get(index).intValue();
            }

            public boolean hasLeadingComments() {
                return (this.bitField0_ & 1) == 1;
            }

            public String getLeadingComments() {
                Object ref = this.leadingComments_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.leadingComments_ = s;
                }
                return s;
            }

            public ByteString getLeadingCommentsBytes() {
                Object ref = this.leadingComments_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.leadingComments_ = b;
                return b;
            }

            public boolean hasTrailingComments() {
                return (this.bitField0_ & 2) == 2;
            }

            public String getTrailingComments() {
                Object ref = this.trailingComments_;
                if (ref instanceof String) {
                    return (String) ref;
                }
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.trailingComments_ = s;
                }
                return s;
            }

            public ByteString getTrailingCommentsBytes() {
                Object ref = this.trailingComments_;
                if (!(ref instanceof String)) {
                    return (ByteString) ref;
                }
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.trailingComments_ = b;
                return b;
            }

            private void initFields() {
                this.path_ = Collections.emptyList();
                this.span_ = Collections.emptyList();
                this.leadingComments_ = "";
                this.trailingComments_ = "";
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized != -1) {
                    return isInitialized == 1;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                getSerializedSize();
                if (getPathList().size() > 0) {
                    output.writeRawVarint32(10);
                    output.writeRawVarint32(this.pathMemoizedSerializedSize);
                }
                for (int i = 0; i < this.path_.size(); i++) {
                    output.writeInt32NoTag(this.path_.get(i).intValue());
                }
                if (getSpanList().size() > 0) {
                    output.writeRawVarint32(18);
                    output.writeRawVarint32(this.spanMemoizedSerializedSize);
                }
                for (int i2 = 0; i2 < this.span_.size(); i2++) {
                    output.writeInt32NoTag(this.span_.get(i2).intValue());
                }
                if ((this.bitField0_ & 1) == 1) {
                    output.writeBytes(3, getLeadingCommentsBytes());
                }
                if ((this.bitField0_ & 2) == 2) {
                    output.writeBytes(4, getTrailingCommentsBytes());
                }
                getUnknownFields().writeTo(output);
            }

            public int getSerializedSize() {
                int size = this.memoizedSerializedSize;
                if (size != -1) {
                    return size;
                }
                int dataSize = 0;
                for (int i = 0; i < this.path_.size(); i++) {
                    dataSize += CodedOutputStream.computeInt32SizeNoTag(this.path_.get(i).intValue());
                }
                int size2 = 0 + dataSize;
                if (!getPathList().isEmpty()) {
                    size2 = size2 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize);
                }
                this.pathMemoizedSerializedSize = dataSize;
                int dataSize2 = 0;
                for (int i2 = 0; i2 < this.span_.size(); i2++) {
                    dataSize2 += CodedOutputStream.computeInt32SizeNoTag(this.span_.get(i2).intValue());
                }
                int size3 = size2 + dataSize2;
                if (!getSpanList().isEmpty()) {
                    size3 = size3 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize2);
                }
                this.spanMemoizedSerializedSize = dataSize2;
                if ((this.bitField0_ & 1) == 1) {
                    size3 += CodedOutputStream.computeBytesSize(3, getLeadingCommentsBytes());
                }
                if ((this.bitField0_ & 2) == 2) {
                    size3 += CodedOutputStream.computeBytesSize(4, getTrailingCommentsBytes());
                }
                int size4 = size3 + getUnknownFields().getSerializedSize();
                this.memoizedSerializedSize = size4;
                return size4;
            }

            /* access modifiers changed from: protected */
            public Object writeReplace() throws ObjectStreamException {
                return super.writeReplace();
            }

            public static Location parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static Location parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static Location parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static Location parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static Location parseFrom(InputStream input) throws IOException {
                return PARSER.parseFrom(input);
            }

            public static Location parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseFrom(input, extensionRegistry);
            }

            public static Location parseDelimitedFrom(InputStream input) throws IOException {
                return PARSER.parseDelimitedFrom(input);
            }

            public static Location parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseDelimitedFrom(input, extensionRegistry);
            }

            public static Location parseFrom(CodedInputStream input) throws IOException {
                return PARSER.parseFrom(input);
            }

            public static Location parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return PARSER.parseFrom(input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return Builder.create();
            }

            public Builder newBuilderForType() {
                return newBuilder();
            }

            public static Builder newBuilder(Location prototype) {
                return newBuilder().mergeFrom(prototype);
            }

            public Builder toBuilder() {
                return newBuilder(this);
            }

            /* access modifiers changed from: protected */
            public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
                return new Builder(parent);
            }

            public static final class Builder extends GeneratedMessage.Builder<Builder> implements LocationOrBuilder {
                private int bitField0_;
                private Object leadingComments_;
                private List<Integer> path_;
                private List<Integer> span_;
                private Object trailingComments_;

                public static final Descriptors.Descriptor getDescriptor() {
                    return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
                }

                /* access modifiers changed from: protected */
                public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                    return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable.ensureFieldAccessorsInitialized(Location.class, Builder.class);
                }

                private Builder() {
                    this.path_ = Collections.emptyList();
                    this.span_ = Collections.emptyList();
                    this.leadingComments_ = "";
                    this.trailingComments_ = "";
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessage.BuilderParent parent) {
                    super(parent);
                    this.path_ = Collections.emptyList();
                    this.span_ = Collections.emptyList();
                    this.leadingComments_ = "";
                    this.trailingComments_ = "";
                    maybeForceBuilderInitialization();
                }

                private void maybeForceBuilderInitialization() {
                    boolean z = GeneratedMessage.alwaysUseFieldBuilders;
                }

                /* access modifiers changed from: private */
                public static Builder create() {
                    return new Builder();
                }

                public Builder clear() {
                    super.clear();
                    this.path_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    this.span_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    this.leadingComments_ = "";
                    this.bitField0_ &= -5;
                    this.trailingComments_ = "";
                    this.bitField0_ &= -9;
                    return this;
                }

                public Builder clone() {
                    return create().mergeFrom(buildPartial());
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
                }

                public Location getDefaultInstanceForType() {
                    return Location.getDefaultInstance();
                }

                public Location build() {
                    Location result = buildPartial();
                    if (result.isInitialized()) {
                        return result;
                    }
                    throw newUninitializedMessageException(result);
                }

                public Location buildPartial() {
                    Location result = new Location((GeneratedMessage.Builder) this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((this.bitField0_ & 1) == 1) {
                        this.path_ = Collections.unmodifiableList(this.path_);
                        this.bitField0_ &= -2;
                    }
                    List unused = result.path_ = this.path_;
                    if ((this.bitField0_ & 2) == 2) {
                        this.span_ = Collections.unmodifiableList(this.span_);
                        this.bitField0_ &= -3;
                    }
                    List unused2 = result.span_ = this.span_;
                    if ((from_bitField0_ & 4) == 4) {
                        to_bitField0_ = 0 | 1;
                    }
                    Object unused3 = result.leadingComments_ = this.leadingComments_;
                    if ((from_bitField0_ & 8) == 8) {
                        to_bitField0_ |= 2;
                    }
                    Object unused4 = result.trailingComments_ = this.trailingComments_;
                    int unused5 = result.bitField0_ = to_bitField0_;
                    onBuilt();
                    return result;
                }

                public Builder mergeFrom(Message other) {
                    if (other instanceof Location) {
                        return mergeFrom((Location) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(Location other) {
                    if (other == Location.getDefaultInstance()) {
                        return this;
                    }
                    if (!other.path_.isEmpty()) {
                        if (this.path_.isEmpty()) {
                            this.path_ = other.path_;
                            this.bitField0_ &= -2;
                        } else {
                            ensurePathIsMutable();
                            this.path_.addAll(other.path_);
                        }
                        onChanged();
                    }
                    if (!other.span_.isEmpty()) {
                        if (this.span_.isEmpty()) {
                            this.span_ = other.span_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureSpanIsMutable();
                            this.span_.addAll(other.span_);
                        }
                        onChanged();
                    }
                    if (other.hasLeadingComments()) {
                        this.bitField0_ |= 4;
                        this.leadingComments_ = other.leadingComments_;
                        onChanged();
                    }
                    if (other.hasTrailingComments()) {
                        this.bitField0_ |= 8;
                        this.trailingComments_ = other.trailingComments_;
                        onChanged();
                    }
                    mergeUnknownFields(other.getUnknownFields());
                    return this;
                }

                public final boolean isInitialized() {
                    return true;
                }

                /* Debug info: failed to restart local var, previous not found, register: 3 */
                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    try {
                        Location parsedMessage = Location.PARSER.parsePartialFrom(input, extensionRegistry);
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        Location parsedMessage2 = (Location) e.getUnfinishedMessage();
                        throw e;
                    } catch (Throwable th) {
                        if (0 != 0) {
                            mergeFrom((Location) null);
                        }
                        throw th;
                    }
                }

                private void ensurePathIsMutable() {
                    if ((this.bitField0_ & 1) != 1) {
                        this.path_ = new ArrayList(this.path_);
                        this.bitField0_ |= 1;
                    }
                }

                public List<Integer> getPathList() {
                    return Collections.unmodifiableList(this.path_);
                }

                public int getPathCount() {
                    return this.path_.size();
                }

                public int getPath(int index) {
                    return this.path_.get(index).intValue();
                }

                public Builder setPath(int index, int value) {
                    ensurePathIsMutable();
                    this.path_.set(index, Integer.valueOf(value));
                    onChanged();
                    return this;
                }

                public Builder addPath(int value) {
                    ensurePathIsMutable();
                    this.path_.add(Integer.valueOf(value));
                    onChanged();
                    return this;
                }

                public Builder addAllPath(Iterable<? extends Integer> values) {
                    ensurePathIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.path_);
                    onChanged();
                    return this;
                }

                public Builder clearPath() {
                    this.path_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                    return this;
                }

                private void ensureSpanIsMutable() {
                    if ((this.bitField0_ & 2) != 2) {
                        this.span_ = new ArrayList(this.span_);
                        this.bitField0_ |= 2;
                    }
                }

                public List<Integer> getSpanList() {
                    return Collections.unmodifiableList(this.span_);
                }

                public int getSpanCount() {
                    return this.span_.size();
                }

                public int getSpan(int index) {
                    return this.span_.get(index).intValue();
                }

                public Builder setSpan(int index, int value) {
                    ensureSpanIsMutable();
                    this.span_.set(index, Integer.valueOf(value));
                    onChanged();
                    return this;
                }

                public Builder addSpan(int value) {
                    ensureSpanIsMutable();
                    this.span_.add(Integer.valueOf(value));
                    onChanged();
                    return this;
                }

                public Builder addAllSpan(Iterable<? extends Integer> values) {
                    ensureSpanIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.span_);
                    onChanged();
                    return this;
                }

                public Builder clearSpan() {
                    this.span_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                    return this;
                }

                public boolean hasLeadingComments() {
                    return (this.bitField0_ & 4) == 4;
                }

                public String getLeadingComments() {
                    Object ref = this.leadingComments_;
                    if (ref instanceof String) {
                        return (String) ref;
                    }
                    String s = ((ByteString) ref).toStringUtf8();
                    this.leadingComments_ = s;
                    return s;
                }

                public ByteString getLeadingCommentsBytes() {
                    Object ref = this.leadingComments_;
                    if (!(ref instanceof String)) {
                        return (ByteString) ref;
                    }
                    ByteString b = ByteString.copyFromUtf8((String) ref);
                    this.leadingComments_ = b;
                    return b;
                }

                public Builder setLeadingComments(String value) {
                    if (value != null) {
                        this.bitField0_ |= 4;
                        this.leadingComments_ = value;
                        onChanged();
                        return this;
                    }
                    throw new NullPointerException();
                }

                public Builder clearLeadingComments() {
                    this.bitField0_ &= -5;
                    this.leadingComments_ = Location.getDefaultInstance().getLeadingComments();
                    onChanged();
                    return this;
                }

                public Builder setLeadingCommentsBytes(ByteString value) {
                    if (value != null) {
                        this.bitField0_ |= 4;
                        this.leadingComments_ = value;
                        onChanged();
                        return this;
                    }
                    throw new NullPointerException();
                }

                public boolean hasTrailingComments() {
                    return (this.bitField0_ & 8) == 8;
                }

                public String getTrailingComments() {
                    Object ref = this.trailingComments_;
                    if (ref instanceof String) {
                        return (String) ref;
                    }
                    String s = ((ByteString) ref).toStringUtf8();
                    this.trailingComments_ = s;
                    return s;
                }

                public ByteString getTrailingCommentsBytes() {
                    Object ref = this.trailingComments_;
                    if (!(ref instanceof String)) {
                        return (ByteString) ref;
                    }
                    ByteString b = ByteString.copyFromUtf8((String) ref);
                    this.trailingComments_ = b;
                    return b;
                }

                public Builder setTrailingComments(String value) {
                    if (value != null) {
                        this.bitField0_ |= 8;
                        this.trailingComments_ = value;
                        onChanged();
                        return this;
                    }
                    throw new NullPointerException();
                }

                public Builder clearTrailingComments() {
                    this.bitField0_ &= -9;
                    this.trailingComments_ = Location.getDefaultInstance().getTrailingComments();
                    onChanged();
                    return this;
                }

                public Builder setTrailingCommentsBytes(ByteString value) {
                    if (value != null) {
                        this.bitField0_ |= 8;
                        this.trailingComments_ = value;
                        onChanged();
                        return this;
                    }
                    throw new NullPointerException();
                }
            }
        }

        public List<Location> getLocationList() {
            return this.location_;
        }

        public List<? extends LocationOrBuilder> getLocationOrBuilderList() {
            return this.location_;
        }

        public int getLocationCount() {
            return this.location_.size();
        }

        public Location getLocation(int index) {
            return this.location_.get(index);
        }

        public LocationOrBuilder getLocationOrBuilder(int index) {
            return this.location_.get(index);
        }

        private void initFields() {
            this.location_ = Collections.emptyList();
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            for (int i = 0; i < this.location_.size(); i++) {
                output.writeMessage(1, this.location_.get(i));
            }
            getUnknownFields().writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.location_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(1, this.location_.get(i));
            }
            int size3 = size2 + getUnknownFields().getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        /* access modifiers changed from: protected */
        public Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        public static SourceCodeInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SourceCodeInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SourceCodeInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SourceCodeInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SourceCodeInfo parseFrom(InputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static SourceCodeInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static SourceCodeInfo parseDelimitedFrom(InputStream input) throws IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static SourceCodeInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static SourceCodeInfo parseFrom(CodedInputStream input) throws IOException {
            return PARSER.parseFrom(input);
        }

        public static SourceCodeInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(SourceCodeInfo prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /* access modifiers changed from: protected */
        public Builder newBuilderForType(GeneratedMessage.BuilderParent parent) {
            return new Builder(parent);
        }

        public static final class Builder extends GeneratedMessage.Builder<Builder> implements SourceCodeInfoOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> locationBuilder_;
            private List<Location> location_;

            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor;
            }

            /* access modifiers changed from: protected */
            public GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(SourceCodeInfo.class, Builder.class);
            }

            private Builder() {
                this.location_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessage.BuilderParent parent) {
                super(parent);
                this.location_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    getLocationFieldBuilder();
                }
            }

            /* access modifiers changed from: private */
            public static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.location_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor;
            }

            public SourceCodeInfo getDefaultInstanceForType() {
                return SourceCodeInfo.getDefaultInstance();
            }

            public SourceCodeInfo build() {
                SourceCodeInfo result = buildPartial();
                if (result.isInitialized()) {
                    return result;
                }
                throw newUninitializedMessageException(result);
            }

            public SourceCodeInfo buildPartial() {
                SourceCodeInfo result = new SourceCodeInfo((GeneratedMessage.Builder) this);
                int i = this.bitField0_;
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    if ((this.bitField0_ & 1) == 1) {
                        this.location_ = Collections.unmodifiableList(this.location_);
                        this.bitField0_ &= -2;
                    }
                    List unused = result.location_ = this.location_;
                } else {
                    List unused2 = result.location_ = repeatedFieldBuilder.build();
                }
                onBuilt();
                return result;
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof SourceCodeInfo) {
                    return mergeFrom((SourceCodeInfo) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(SourceCodeInfo other) {
                if (other == SourceCodeInfo.getDefaultInstance()) {
                    return this;
                }
                if (this.locationBuilder_ == null) {
                    if (!other.location_.isEmpty()) {
                        if (this.location_.isEmpty()) {
                            this.location_ = other.location_;
                            this.bitField0_ &= -2;
                        } else {
                            ensureLocationIsMutable();
                            this.location_.addAll(other.location_);
                        }
                        onChanged();
                    }
                } else if (!other.location_.isEmpty()) {
                    if (this.locationBuilder_.isEmpty()) {
                        this.locationBuilder_.dispose();
                        RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = null;
                        this.locationBuilder_ = null;
                        this.location_ = other.location_;
                        this.bitField0_ &= -2;
                        if (GeneratedMessage.alwaysUseFieldBuilders) {
                            repeatedFieldBuilder = getLocationFieldBuilder();
                        }
                        this.locationBuilder_ = repeatedFieldBuilder;
                    } else {
                        this.locationBuilder_.addAllMessages(other.location_);
                    }
                }
                mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                try {
                    SourceCodeInfo parsedMessage = SourceCodeInfo.PARSER.parsePartialFrom(input, extensionRegistry);
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                    return this;
                } catch (InvalidProtocolBufferException e) {
                    SourceCodeInfo parsedMessage2 = (SourceCodeInfo) e.getUnfinishedMessage();
                    throw e;
                } catch (Throwable th) {
                    if (0 != 0) {
                        mergeFrom((SourceCodeInfo) null);
                    }
                    throw th;
                }
            }

            private void ensureLocationIsMutable() {
                if ((this.bitField0_ & 1) != 1) {
                    this.location_ = new ArrayList(this.location_);
                    this.bitField0_ |= 1;
                }
            }

            public List<Location> getLocationList() {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    return Collections.unmodifiableList(this.location_);
                }
                return repeatedFieldBuilder.getMessageList();
            }

            public int getLocationCount() {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.location_.size();
                }
                return repeatedFieldBuilder.getCount();
            }

            public Location getLocation(int index) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.location_.get(index);
                }
                return repeatedFieldBuilder.getMessage(index);
            }

            public Builder setLocation(int index, Location value) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.setMessage(index, value);
                } else if (value != null) {
                    ensureLocationIsMutable();
                    this.location_.set(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder setLocation(int index, Location.Builder builderForValue) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureLocationIsMutable();
                    this.location_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addLocation(Location value) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(value);
                } else if (value != null) {
                    ensureLocationIsMutable();
                    this.location_.add(value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addLocation(int index, Location value) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder != null) {
                    repeatedFieldBuilder.addMessage(index, value);
                } else if (value != null) {
                    ensureLocationIsMutable();
                    this.location_.add(index, value);
                    onChanged();
                } else {
                    throw new NullPointerException();
                }
                return this;
            }

            public Builder addLocation(Location.Builder builderForValue) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureLocationIsMutable();
                    this.location_.add(builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addLocation(int index, Location.Builder builderForValue) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureLocationIsMutable();
                    this.location_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    repeatedFieldBuilder.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllLocation(Iterable<? extends Location> values) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureLocationIsMutable();
                    GeneratedMessage.Builder.addAll(values, this.location_);
                    onChanged();
                } else {
                    repeatedFieldBuilder.addAllMessages(values);
                }
                return this;
            }

            public Builder clearLocation() {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    this.location_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                } else {
                    repeatedFieldBuilder.clear();
                }
                return this;
            }

            public Builder removeLocation(int index) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    ensureLocationIsMutable();
                    this.location_.remove(index);
                    onChanged();
                } else {
                    repeatedFieldBuilder.remove(index);
                }
                return this;
            }

            public Location.Builder getLocationBuilder(int index) {
                return getLocationFieldBuilder().getBuilder(index);
            }

            public LocationOrBuilder getLocationOrBuilder(int index) {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder == null) {
                    return this.location_.get(index);
                }
                return repeatedFieldBuilder.getMessageOrBuilder(index);
            }

            public List<? extends LocationOrBuilder> getLocationOrBuilderList() {
                RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> repeatedFieldBuilder = this.locationBuilder_;
                if (repeatedFieldBuilder != null) {
                    return repeatedFieldBuilder.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.location_);
            }

            public Location.Builder addLocationBuilder() {
                return getLocationFieldBuilder().addBuilder(Location.getDefaultInstance());
            }

            public Location.Builder addLocationBuilder(int index) {
                return getLocationFieldBuilder().addBuilder(index, Location.getDefaultInstance());
            }

            public List<Location.Builder> getLocationBuilderList() {
                return getLocationFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> getLocationFieldBuilder() {
                if (this.locationBuilder_ == null) {
                    List<Location> list = this.location_;
                    boolean z = true;
                    if ((this.bitField0_ & 1) != 1) {
                        z = false;
                    }
                    this.locationBuilder_ = new RepeatedFieldBuilder<>(list, z, getParentForChildren(), isClean());
                    this.location_ = null;
                }
                return this.locationBuilder_;
            }
        }
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(new String[]{"\n google/protobuf/descriptor.proto\u0012\u000fgoogle.protobuf\"G\n\u0011FileDescriptorSet\u00122\n\u0004file\u0018\u0001 \u0003(\u000b2$.google.protobuf.FileDescriptorProto\"Ë\u0003\n\u0013FileDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u000f\n\u0007package\u0018\u0002 \u0001(\t\u0012\u0012\n\ndependency\u0018\u0003 \u0003(\t\u0012\u0019\n\u0011public_dependency\u0018\n \u0003(\u0005\u0012\u0017\n\u000fweak_dependency\u0018\u000b \u0003(\u0005\u00126\n\fmessage_type\u0018\u0004 \u0003(\u000b2 .google.protobuf.DescriptorProto\u00127\n\tenum_type\u0018\u0005 \u0003(\u000b2$.google.protobuf.EnumDescriptorProto\u00128\n\u0007service\u0018\u0006 \u0003(\u000b2'.google.protobuf.", "ServiceDescriptorProto\u00128\n\textension\u0018\u0007 \u0003(\u000b2%.google.protobuf.FieldDescriptorProto\u0012-\n\u0007options\u0018\b \u0001(\u000b2\u001c.google.protobuf.FileOptions\u00129\n\u0010source_code_info\u0018\t \u0001(\u000b2\u001f.google.protobuf.SourceCodeInfo\"©\u0003\n\u000fDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u00124\n\u0005field\u0018\u0002 \u0003(\u000b2%.google.protobuf.FieldDescriptorProto\u00128\n\textension\u0018\u0006 \u0003(\u000b2%.google.protobuf.FieldDescriptorProto\u00125\n\u000bnested_type\u0018\u0003 \u0003(\u000b2 .google.protobuf.DescriptorProto\u00127\n\tenum_type", "\u0018\u0004 \u0003(\u000b2$.google.protobuf.EnumDescriptorProto\u0012H\n\u000fextension_range\u0018\u0005 \u0003(\u000b2/.google.protobuf.DescriptorProto.ExtensionRange\u00120\n\u0007options\u0018\u0007 \u0001(\u000b2\u001f.google.protobuf.MessageOptions\u001a,\n\u000eExtensionRange\u0012\r\n\u0005start\u0018\u0001 \u0001(\u0005\u0012\u000b\n\u0003end\u0018\u0002 \u0001(\u0005\"\u0005\n\u0014FieldDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u000e\n\u0006number\u0018\u0003 \u0001(\u0005\u0012:\n\u0005label\u0018\u0004 \u0001(\u000e2+.google.protobuf.FieldDescriptorProto.Label\u00128\n\u0004type\u0018\u0005 \u0001(\u000e2*.google.protobuf.FieldDescriptorProto.Type\u0012\u0011\n\ttype_name", "\u0018\u0006 \u0001(\t\u0012\u0010\n\bextendee\u0018\u0002 \u0001(\t\u0012\u0015\n\rdefault_value\u0018\u0007 \u0001(\t\u0012.\n\u0007options\u0018\b \u0001(\u000b2\u001d.google.protobuf.FieldOptions\"¶\u0002\n\u0004Type\u0012\u000f\n\u000bTYPE_DOUBLE\u0010\u0001\u0012\u000e\n\nTYPE_FLOAT\u0010\u0002\u0012\u000e\n\nTYPE_INT64\u0010\u0003\u0012\u000f\n\u000bTYPE_UINT64\u0010\u0004\u0012\u000e\n\nTYPE_INT32\u0010\u0005\u0012\u0010\n\fTYPE_FIXED64\u0010\u0006\u0012\u0010\n\fTYPE_FIXED32\u0010\u0007\u0012\r\n\tTYPE_BOOL\u0010\b\u0012\u000f\n\u000bTYPE_STRING\u0010\t\u0012\u000e\n\nTYPE_GROUP\u0010\n\u0012\u0010\n\fTYPE_MESSAGE\u0010\u000b\u0012\u000e\n\nTYPE_BYTES\u0010\f\u0012\u000f\n\u000bTYPE_UINT32\u0010\r\u0012\r\n\tTYPE_ENUM\u0010\u000e\u0012\u0011\n\rTYPE_SFIXED32\u0010\u000f\u0012\u0011\n\rTYPE_SFIXED64\u0010\u0010\u0012\u000f\n\u000bTYPE_SINT32\u0010\u0011\u0012\u000f\n\u000bTYPE_", "SINT64\u0010\u0012\"C\n\u0005Label\u0012\u0012\n\u000eLABEL_OPTIONAL\u0010\u0001\u0012\u0012\n\u000eLABEL_REQUIRED\u0010\u0002\u0012\u0012\n\u000eLABEL_REPEATED\u0010\u0003\"\u0001\n\u0013EnumDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u00128\n\u0005value\u0018\u0002 \u0003(\u000b2).google.protobuf.EnumValueDescriptorProto\u0012-\n\u0007options\u0018\u0003 \u0001(\u000b2\u001c.google.protobuf.EnumOptions\"l\n\u0018EnumValueDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u000e\n\u0006number\u0018\u0002 \u0001(\u0005\u00122\n\u0007options\u0018\u0003 \u0001(\u000b2!.google.protobuf.EnumValueOptions\"\u0001\n\u0016ServiceDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u00126\n\u0006method\u0018\u0002 \u0003(\u000b2&.google.pro", "tobuf.MethodDescriptorProto\u00120\n\u0007options\u0018\u0003 \u0001(\u000b2\u001f.google.protobuf.ServiceOptions\"\n\u0015MethodDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u0012\n\ninput_type\u0018\u0002 \u0001(\t\u0012\u0013\n\u000boutput_type\u0018\u0003 \u0001(\t\u0012/\n\u0007options\u0018\u0004 \u0001(\u000b2\u001e.google.protobuf.MethodOptions\"é\u0003\n\u000bFileOptions\u0012\u0014\n\fjava_package\u0018\u0001 \u0001(\t\u0012\u001c\n\u0014java_outer_classname\u0018\b \u0001(\t\u0012\"\n\u0013java_multiple_files\u0018\n \u0001(\b:\u0005false\u0012,\n\u001djava_generate_equals_and_hash\u0018\u0014 \u0001(\b:\u0005false\u0012F\n\foptimize_for\u0018\t \u0001(\u000e2).google.protobuf.Fil", "eOptions.OptimizeMode:\u0005SPEED\u0012\u0012\n\ngo_package\u0018\u000b \u0001(\t\u0012\"\n\u0013cc_generic_services\u0018\u0010 \u0001(\b:\u0005false\u0012$\n\u0015java_generic_services\u0018\u0011 \u0001(\b:\u0005false\u0012\"\n\u0013py_generic_services\u0018\u0012 \u0001(\b:\u0005false\u0012C\n\u0014uninterpreted_option\u0018ç\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption\":\n\fOptimizeMode\u0012\t\n\u0005SPEED\u0010\u0001\u0012\r\n\tCODE_SIZE\u0010\u0002\u0012\u0010\n\fLITE_RUNTIME\u0010\u0003*\t\bè\u0007\u0010\u0002\"¸\u0001\n\u000eMessageOptions\u0012&\n\u0017message_set_wire_format\u0018\u0001 \u0001(\b:\u0005false\u0012.\n\u001fno_standard_descriptor_accessor\u0018\u0002 \u0001(\b:\u0005", "false\u0012C\n\u0014uninterpreted_option\u0018ç\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\bè\u0007\u0010\u0002\"¾\u0002\n\fFieldOptions\u0012:\n\u0005ctype\u0018\u0001 \u0001(\u000e2#.google.protobuf.FieldOptions.CType:\u0006STRING\u0012\u000e\n\u0006packed\u0018\u0002 \u0001(\b\u0012\u0013\n\u0004lazy\u0018\u0005 \u0001(\b:\u0005false\u0012\u0019\n\ndeprecated\u0018\u0003 \u0001(\b:\u0005false\u0012\u001c\n\u0014experimental_map_key\u0018\t \u0001(\t\u0012\u0013\n\u0004weak\u0018\n \u0001(\b:\u0005false\u0012C\n\u0014uninterpreted_option\u0018ç\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption\"/\n\u0005CType\u0012\n\n\u0006STRING\u0010\u0000\u0012\b\n\u0004CORD\u0010\u0001\u0012\u0010\n\fSTRING_PIECE\u0010\u0002*\t\bè\u0007", "\u0010\u0002\"x\n\u000bEnumOptions\u0012\u0019\n\u000ballow_alias\u0018\u0002 \u0001(\b:\u0004true\u0012C\n\u0014uninterpreted_option\u0018ç\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\bè\u0007\u0010\u0002\"b\n\u0010EnumValueOptions\u0012C\n\u0014uninterpreted_option\u0018ç\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\bè\u0007\u0010\u0002\"`\n\u000eServiceOptions\u0012C\n\u0014uninterpreted_option\u0018ç\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\bè\u0007\u0010\u0002\"_\n\rMethodOptions\u0012C\n\u0014uninterpreted_option\u0018ç\u0007 \u0003(\u000b2$.google.protobuf.Uninter", "pretedOption*\t\bè\u0007\u0010\u0002\"\u0002\n\u0013UninterpretedOption\u0012;\n\u0004name\u0018\u0002 \u0003(\u000b2-.google.protobuf.UninterpretedOption.NamePart\u0012\u0018\n\u0010identifier_value\u0018\u0003 \u0001(\t\u0012\u001a\n\u0012positive_int_value\u0018\u0004 \u0001(\u0004\u0012\u001a\n\u0012negative_int_value\u0018\u0005 \u0001(\u0003\u0012\u0014\n\fdouble_value\u0018\u0006 \u0001(\u0001\u0012\u0014\n\fstring_value\u0018\u0007 \u0001(\f\u0012\u0017\n\u000faggregate_value\u0018\b \u0001(\t\u001a3\n\bNamePart\u0012\u0011\n\tname_part\u0018\u0001 \u0002(\t\u0012\u0014\n\fis_extension\u0018\u0002 \u0002(\b\"±\u0001\n\u000eSourceCodeInfo\u0012:\n\blocation\u0018\u0001 \u0003(\u000b2(.google.protobuf.SourceCodeInfo.Location\u001ac\n\bLocat", "ion\u0012\u0010\n\u0004path\u0018\u0001 \u0003(\u0005B\u0002\u0010\u0001\u0012\u0010\n\u0004span\u0018\u0002 \u0003(\u0005B\u0002\u0010\u0001\u0012\u0018\n\u0010leading_comments\u0018\u0003 \u0001(\t\u0012\u0019\n\u0011trailing_comments\u0018\u0004 \u0001(\tB)\n\u0013com.google.protobufB\u0010DescriptorProtosH\u0001"}, new Descriptors.FileDescriptor[0], new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            public ExtensionRegistry assignDescriptors(Descriptors.FileDescriptor root) {
                Descriptors.FileDescriptor unused = DescriptorProtos.descriptor = root;
                Descriptors.Descriptor unused2 = DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(0);
                GeneratedMessage.FieldAccessorTable unused3 = DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor, new String[]{"File"});
                Descriptors.Descriptor unused4 = DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(1);
                GeneratedMessage.FieldAccessorTable unused5 = DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor, new String[]{"Name", "Package", "Dependency", "PublicDependency", "WeakDependency", "MessageType", "EnumType", "Service", "Extension", "Options", "SourceCodeInfo"});
                Descriptors.Descriptor unused6 = DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(2);
                GeneratedMessage.FieldAccessorTable unused7 = DescriptorProtos.internal_static_google_protobuf_DescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor, new String[]{"Name", "Field", "Extension", "NestedType", "EnumType", "ExtensionRange", "Options"});
                Descriptors.Descriptor unused8 = DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor = DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor.getNestedTypes().get(0);
                GeneratedMessage.FieldAccessorTable unused9 = DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor, new String[]{"Start", "End"});
                Descriptors.Descriptor unused10 = DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(3);
                GeneratedMessage.FieldAccessorTable unused11 = DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor, new String[]{"Name", "Number", "Label", "Type", "TypeName", "Extendee", "DefaultValue", "Options"});
                Descriptors.Descriptor unused12 = DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(4);
                GeneratedMessage.FieldAccessorTable unused13 = DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor, new String[]{"Name", "Value", "Options"});
                Descriptors.Descriptor unused14 = DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(5);
                GeneratedMessage.FieldAccessorTable unused15 = DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor, new String[]{"Name", "Number", "Options"});
                Descriptors.Descriptor unused16 = DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(6);
                GeneratedMessage.FieldAccessorTable unused17 = DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor, new String[]{"Name", "Method", "Options"});
                Descriptors.Descriptor unused18 = DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(7);
                GeneratedMessage.FieldAccessorTable unused19 = DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor, new String[]{"Name", "InputType", "OutputType", "Options"});
                Descriptors.Descriptor unused20 = DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(8);
                GeneratedMessage.FieldAccessorTable unused21 = DescriptorProtos.internal_static_google_protobuf_FileOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor, new String[]{"JavaPackage", "JavaOuterClassname", "JavaMultipleFiles", "JavaGenerateEqualsAndHash", "OptimizeFor", "GoPackage", "CcGenericServices", "JavaGenericServices", "PyGenericServices", "UninterpretedOption"});
                Descriptors.Descriptor unused22 = DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(9);
                GeneratedMessage.FieldAccessorTable unused23 = DescriptorProtos.internal_static_google_protobuf_MessageOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor, new String[]{"MessageSetWireFormat", "NoStandardDescriptorAccessor", "UninterpretedOption"});
                Descriptors.Descriptor unused24 = DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(10);
                GeneratedMessage.FieldAccessorTable unused25 = DescriptorProtos.internal_static_google_protobuf_FieldOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor, new String[]{"Ctype", "Packed", "Lazy", "Deprecated", "ExperimentalMapKey", "Weak", "UninterpretedOption"});
                Descriptors.Descriptor unused26 = DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(11);
                GeneratedMessage.FieldAccessorTable unused27 = DescriptorProtos.internal_static_google_protobuf_EnumOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor, new String[]{"AllowAlias", "UninterpretedOption"});
                Descriptors.Descriptor unused28 = DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(12);
                GeneratedMessage.FieldAccessorTable unused29 = DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor, new String[]{"UninterpretedOption"});
                Descriptors.Descriptor unused30 = DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(13);
                GeneratedMessage.FieldAccessorTable unused31 = DescriptorProtos.internal_static_google_protobuf_ServiceOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor, new String[]{"UninterpretedOption"});
                Descriptors.Descriptor unused32 = DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(14);
                GeneratedMessage.FieldAccessorTable unused33 = DescriptorProtos.internal_static_google_protobuf_MethodOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor, new String[]{"UninterpretedOption"});
                Descriptors.Descriptor unused34 = DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(15);
                GeneratedMessage.FieldAccessorTable unused35 = DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor, new String[]{"Name", "IdentifierValue", "PositiveIntValue", "NegativeIntValue", "DoubleValue", "StringValue", "AggregateValue"});
                Descriptors.Descriptor unused36 = DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor = DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor.getNestedTypes().get(0);
                GeneratedMessage.FieldAccessorTable unused37 = DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor, new String[]{"NamePart", "IsExtension"});
                Descriptors.Descriptor unused38 = DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(16);
                GeneratedMessage.FieldAccessorTable unused39 = DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor, new String[]{"Location"});
                Descriptors.Descriptor unused40 = DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor = DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor.getNestedTypes().get(0);
                GeneratedMessage.FieldAccessorTable unused41 = DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor, new String[]{"Path", "Span", "LeadingComments", "TrailingComments"});
                return null;
            }
        });
    }
}
