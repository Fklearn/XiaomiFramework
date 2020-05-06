package com.google.protobuf;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.FieldSet;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormat;
import com.google.protobuf.WireFormat;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Descriptors {

    private interface GenericDescriptor {
        FileDescriptor getFile();

        String getFullName();

        String getName();

        Message toProto();
    }

    public static final class FileDescriptor {
        private final FileDescriptor[] dependencies;
        private final EnumDescriptor[] enumTypes;
        private final FieldDescriptor[] extensions;
        private final Descriptor[] messageTypes;
        /* access modifiers changed from: private */
        public final DescriptorPool pool;
        private DescriptorProtos.FileDescriptorProto proto;
        private final FileDescriptor[] publicDependencies;
        private final ServiceDescriptor[] services;

        public interface InternalDescriptorAssigner {
            ExtensionRegistry assignDescriptors(FileDescriptor fileDescriptor);
        }

        public DescriptorProtos.FileDescriptorProto toProto() {
            return this.proto;
        }

        public String getName() {
            return this.proto.getName();
        }

        public String getPackage() {
            return this.proto.getPackage();
        }

        public DescriptorProtos.FileOptions getOptions() {
            return this.proto.getOptions();
        }

        public List<Descriptor> getMessageTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.messageTypes));
        }

        public List<EnumDescriptor> getEnumTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
        }

        public List<ServiceDescriptor> getServices() {
            return Collections.unmodifiableList(Arrays.asList(this.services));
        }

        public List<FieldDescriptor> getExtensions() {
            return Collections.unmodifiableList(Arrays.asList(this.extensions));
        }

        public List<FileDescriptor> getDependencies() {
            return Collections.unmodifiableList(Arrays.asList(this.dependencies));
        }

        public List<FileDescriptor> getPublicDependencies() {
            return Collections.unmodifiableList(Arrays.asList(this.publicDependencies));
        }

        public Descriptor findMessageTypeByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (getPackage().length() > 0) {
                name = getPackage() + '.' + name;
            }
            GenericDescriptor result = this.pool.findSymbol(name);
            if (result == null || !(result instanceof Descriptor) || result.getFile() != this) {
                return null;
            }
            return (Descriptor) result;
        }

        public EnumDescriptor findEnumTypeByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (getPackage().length() > 0) {
                name = getPackage() + '.' + name;
            }
            GenericDescriptor result = this.pool.findSymbol(name);
            if (result == null || !(result instanceof EnumDescriptor) || result.getFile() != this) {
                return null;
            }
            return (EnumDescriptor) result;
        }

        public ServiceDescriptor findServiceByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (getPackage().length() > 0) {
                name = getPackage() + '.' + name;
            }
            GenericDescriptor result = this.pool.findSymbol(name);
            if (result == null || !(result instanceof ServiceDescriptor) || result.getFile() != this) {
                return null;
            }
            return (ServiceDescriptor) result;
        }

        public FieldDescriptor findExtensionByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (getPackage().length() > 0) {
                name = getPackage() + '.' + name;
            }
            GenericDescriptor result = this.pool.findSymbol(name);
            if (result == null || !(result instanceof FieldDescriptor) || result.getFile() != this) {
                return null;
            }
            return (FieldDescriptor) result;
        }

        public static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto2, FileDescriptor[] dependencies2) throws DescriptorValidationException {
            FileDescriptor result = new FileDescriptor(proto2, dependencies2, new DescriptorPool(dependencies2));
            if (dependencies2.length == proto2.getDependencyCount()) {
                int i = 0;
                while (i < proto2.getDependencyCount()) {
                    if (dependencies2[i].getName().equals(proto2.getDependency(i))) {
                        i++;
                    } else {
                        throw new DescriptorValidationException(result, "Dependencies passed to FileDescriptor.buildFrom() don't match those listed in the FileDescriptorProto.", (AnonymousClass1) null);
                    }
                }
                result.crossLink();
                return result;
            }
            throw new DescriptorValidationException(result, "Dependencies passed to FileDescriptor.buildFrom() don't match those listed in the FileDescriptorProto.", (AnonymousClass1) null);
        }

        public static void internalBuildGeneratedFileFrom(String[] descriptorDataParts, FileDescriptor[] dependencies2, InternalDescriptorAssigner descriptorAssigner) {
            StringBuilder descriptorData = new StringBuilder();
            for (String part : descriptorDataParts) {
                descriptorData.append(part);
            }
            try {
                byte[] descriptorBytes = descriptorData.toString().getBytes("ISO-8859-1");
                try {
                    DescriptorProtos.FileDescriptorProto proto2 = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
                    try {
                        FileDescriptor result = buildFrom(proto2, dependencies2);
                        ExtensionRegistry registry = descriptorAssigner.assignDescriptors(result);
                        if (registry != null) {
                            try {
                                result.setProto(DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes, (ExtensionRegistryLite) registry));
                            } catch (InvalidProtocolBufferException e) {
                                throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
                            }
                        }
                    } catch (DescriptorValidationException e2) {
                        throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto2.getName() + "\".", e2);
                    }
                } catch (InvalidProtocolBufferException e3) {
                    throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e3);
                }
            } catch (UnsupportedEncodingException e4) {
                throw new RuntimeException("Standard encoding ISO-8859-1 not supported by JVM.", e4);
            }
        }

        private FileDescriptor(DescriptorProtos.FileDescriptorProto proto2, FileDescriptor[] dependencies2, DescriptorPool pool2) throws DescriptorValidationException {
            this.pool = pool2;
            this.proto = proto2;
            this.dependencies = (FileDescriptor[]) dependencies2.clone();
            this.publicDependencies = new FileDescriptor[proto2.getPublicDependencyCount()];
            int i = 0;
            while (i < proto2.getPublicDependencyCount()) {
                int index = proto2.getPublicDependency(i);
                if (index >= 0) {
                    FileDescriptor[] fileDescriptorArr = this.dependencies;
                    if (index < fileDescriptorArr.length) {
                        this.publicDependencies[i] = fileDescriptorArr[proto2.getPublicDependency(i)];
                        i++;
                    }
                }
                throw new DescriptorValidationException(this, "Invalid public dependency index.", (AnonymousClass1) null);
            }
            pool2.addPackage(getPackage(), this);
            this.messageTypes = new Descriptor[proto2.getMessageTypeCount()];
            for (int i2 = 0; i2 < proto2.getMessageTypeCount(); i2++) {
                this.messageTypes[i2] = new Descriptor(proto2.getMessageType(i2), this, (Descriptor) null, i2, (AnonymousClass1) null);
            }
            this.enumTypes = new EnumDescriptor[proto2.getEnumTypeCount()];
            for (int i3 = 0; i3 < proto2.getEnumTypeCount(); i3++) {
                this.enumTypes[i3] = new EnumDescriptor(proto2.getEnumType(i3), this, (Descriptor) null, i3, (AnonymousClass1) null);
            }
            this.services = new ServiceDescriptor[proto2.getServiceCount()];
            for (int i4 = 0; i4 < proto2.getServiceCount(); i4++) {
                this.services[i4] = new ServiceDescriptor(proto2.getService(i4), this, i4, (AnonymousClass1) null);
            }
            this.extensions = new FieldDescriptor[proto2.getExtensionCount()];
            for (int i5 = 0; i5 < proto2.getExtensionCount(); i5++) {
                this.extensions[i5] = new FieldDescriptor(proto2.getExtension(i5), this, (Descriptor) null, i5, true, (AnonymousClass1) null);
            }
        }

        private void crossLink() throws DescriptorValidationException {
            for (Descriptor messageType : this.messageTypes) {
                messageType.crossLink();
            }
            for (ServiceDescriptor service : this.services) {
                service.crossLink();
            }
            for (FieldDescriptor extension : this.extensions) {
                extension.crossLink();
            }
        }

        private void setProto(DescriptorProtos.FileDescriptorProto proto2) {
            this.proto = proto2;
            int i = 0;
            while (true) {
                Descriptor[] descriptorArr = this.messageTypes;
                if (i >= descriptorArr.length) {
                    break;
                }
                descriptorArr[i].setProto(proto2.getMessageType(i));
                i++;
            }
            int i2 = 0;
            while (true) {
                EnumDescriptor[] enumDescriptorArr = this.enumTypes;
                if (i2 >= enumDescriptorArr.length) {
                    break;
                }
                enumDescriptorArr[i2].setProto(proto2.getEnumType(i2));
                i2++;
            }
            int i3 = 0;
            while (true) {
                ServiceDescriptor[] serviceDescriptorArr = this.services;
                if (i3 >= serviceDescriptorArr.length) {
                    break;
                }
                serviceDescriptorArr[i3].setProto(proto2.getService(i3));
                i3++;
            }
            int i4 = 0;
            while (true) {
                FieldDescriptor[] fieldDescriptorArr = this.extensions;
                if (i4 < fieldDescriptorArr.length) {
                    fieldDescriptorArr[i4].setProto(proto2.getExtension(i4));
                    i4++;
                } else {
                    return;
                }
            }
        }
    }

    public static final class Descriptor implements GenericDescriptor {
        private final Descriptor containingType;
        private final EnumDescriptor[] enumTypes;
        private final FieldDescriptor[] extensions;
        private final FieldDescriptor[] fields;
        private final FileDescriptor file;
        private final String fullName;
        private final int index;
        private final Descriptor[] nestedTypes;
        private DescriptorProtos.DescriptorProto proto;

        /* synthetic */ Descriptor(DescriptorProtos.DescriptorProto x0, FileDescriptor x1, Descriptor x2, int x3, AnonymousClass1 x4) throws DescriptorValidationException {
            this(x0, x1, x2, x3);
        }

        public int getIndex() {
            return this.index;
        }

        public DescriptorProtos.DescriptorProto toProto() {
            return this.proto;
        }

        public String getName() {
            return this.proto.getName();
        }

        public String getFullName() {
            return this.fullName;
        }

        public FileDescriptor getFile() {
            return this.file;
        }

        public Descriptor getContainingType() {
            return this.containingType;
        }

        public DescriptorProtos.MessageOptions getOptions() {
            return this.proto.getOptions();
        }

        public List<FieldDescriptor> getFields() {
            return Collections.unmodifiableList(Arrays.asList(this.fields));
        }

        public List<FieldDescriptor> getExtensions() {
            return Collections.unmodifiableList(Arrays.asList(this.extensions));
        }

        public List<Descriptor> getNestedTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.nestedTypes));
        }

        public List<EnumDescriptor> getEnumTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
        }

        public boolean isExtensionNumber(int number) {
            for (DescriptorProtos.DescriptorProto.ExtensionRange range : this.proto.getExtensionRangeList()) {
                if (range.getStart() <= number && number < range.getEnd()) {
                    return true;
                }
            }
            return false;
        }

        public FieldDescriptor findFieldByName(String name) {
            DescriptorPool access$1200 = this.file.pool;
            GenericDescriptor result = access$1200.findSymbol(this.fullName + '.' + name);
            if (result == null || !(result instanceof FieldDescriptor)) {
                return null;
            }
            return (FieldDescriptor) result;
        }

        public FieldDescriptor findFieldByNumber(int number) {
            return (FieldDescriptor) this.file.pool.fieldsByNumber.get(new DescriptorPool.DescriptorIntPair(this, number));
        }

        public Descriptor findNestedTypeByName(String name) {
            DescriptorPool access$1200 = this.file.pool;
            GenericDescriptor result = access$1200.findSymbol(this.fullName + '.' + name);
            if (result == null || !(result instanceof Descriptor)) {
                return null;
            }
            return (Descriptor) result;
        }

        public EnumDescriptor findEnumTypeByName(String name) {
            DescriptorPool access$1200 = this.file.pool;
            GenericDescriptor result = access$1200.findSymbol(this.fullName + '.' + name);
            if (result == null || !(result instanceof EnumDescriptor)) {
                return null;
            }
            return (EnumDescriptor) result;
        }

        private Descriptor(DescriptorProtos.DescriptorProto proto2, FileDescriptor file2, Descriptor parent, int index2) throws DescriptorValidationException {
            this.index = index2;
            this.proto = proto2;
            this.fullName = Descriptors.computeFullName(file2, parent, proto2.getName());
            this.file = file2;
            this.containingType = parent;
            this.nestedTypes = new Descriptor[proto2.getNestedTypeCount()];
            for (int i = 0; i < proto2.getNestedTypeCount(); i++) {
                this.nestedTypes[i] = new Descriptor(proto2.getNestedType(i), file2, this, i);
            }
            this.enumTypes = new EnumDescriptor[proto2.getEnumTypeCount()];
            for (int i2 = 0; i2 < proto2.getEnumTypeCount(); i2++) {
                this.enumTypes[i2] = new EnumDescriptor(proto2.getEnumType(i2), file2, this, i2, (AnonymousClass1) null);
            }
            this.fields = new FieldDescriptor[proto2.getFieldCount()];
            for (int i3 = 0; i3 < proto2.getFieldCount(); i3++) {
                this.fields[i3] = new FieldDescriptor(proto2.getField(i3), file2, this, i3, false, (AnonymousClass1) null);
            }
            this.extensions = new FieldDescriptor[proto2.getExtensionCount()];
            for (int i4 = 0; i4 < proto2.getExtensionCount(); i4++) {
                this.extensions[i4] = new FieldDescriptor(proto2.getExtension(i4), file2, this, i4, true, (AnonymousClass1) null);
            }
            file2.pool.addSymbol(this);
        }

        /* access modifiers changed from: private */
        public void crossLink() throws DescriptorValidationException {
            for (Descriptor nestedType : this.nestedTypes) {
                nestedType.crossLink();
            }
            for (FieldDescriptor field : this.fields) {
                field.crossLink();
            }
            for (FieldDescriptor extension : this.extensions) {
                extension.crossLink();
            }
        }

        /* access modifiers changed from: private */
        public void setProto(DescriptorProtos.DescriptorProto proto2) {
            this.proto = proto2;
            int i = 0;
            while (true) {
                Descriptor[] descriptorArr = this.nestedTypes;
                if (i >= descriptorArr.length) {
                    break;
                }
                descriptorArr[i].setProto(proto2.getNestedType(i));
                i++;
            }
            int i2 = 0;
            while (true) {
                EnumDescriptor[] enumDescriptorArr = this.enumTypes;
                if (i2 >= enumDescriptorArr.length) {
                    break;
                }
                enumDescriptorArr[i2].setProto(proto2.getEnumType(i2));
                i2++;
            }
            int i3 = 0;
            while (true) {
                FieldDescriptor[] fieldDescriptorArr = this.fields;
                if (i3 >= fieldDescriptorArr.length) {
                    break;
                }
                fieldDescriptorArr[i3].setProto(proto2.getField(i3));
                i3++;
            }
            int i4 = 0;
            while (true) {
                FieldDescriptor[] fieldDescriptorArr2 = this.extensions;
                if (i4 < fieldDescriptorArr2.length) {
                    fieldDescriptorArr2[i4].setProto(proto2.getExtension(i4));
                    i4++;
                } else {
                    return;
                }
            }
        }
    }

    public static final class FieldDescriptor implements GenericDescriptor, Comparable<FieldDescriptor>, FieldSet.FieldDescriptorLite<FieldDescriptor> {
        private static final WireFormat.FieldType[] table = WireFormat.FieldType.values();
        private Descriptor containingType;
        private Object defaultValue;
        private EnumDescriptor enumType;
        private final Descriptor extensionScope;
        private final FileDescriptor file;
        private final String fullName;
        private final int index;
        private Descriptor messageType;
        private DescriptorProtos.FieldDescriptorProto proto;
        private Type type;

        /* synthetic */ FieldDescriptor(DescriptorProtos.FieldDescriptorProto x0, FileDescriptor x1, Descriptor x2, int x3, boolean x4, AnonymousClass1 x5) throws DescriptorValidationException {
            this(x0, x1, x2, x3, x4);
        }

        public int getIndex() {
            return this.index;
        }

        public DescriptorProtos.FieldDescriptorProto toProto() {
            return this.proto;
        }

        public String getName() {
            return this.proto.getName();
        }

        public int getNumber() {
            return this.proto.getNumber();
        }

        public String getFullName() {
            return this.fullName;
        }

        public JavaType getJavaType() {
            return this.type.getJavaType();
        }

        public WireFormat.JavaType getLiteJavaType() {
            return getLiteType().getJavaType();
        }

        public FileDescriptor getFile() {
            return this.file;
        }

        public Type getType() {
            return this.type;
        }

        public WireFormat.FieldType getLiteType() {
            return table[this.type.ordinal()];
        }

        static {
            if (Type.values().length != DescriptorProtos.FieldDescriptorProto.Type.values().length) {
                throw new RuntimeException("descriptor.proto has a new declared type but Desrciptors.java wasn't updated.");
            }
        }

        public boolean isRequired() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED;
        }

        public boolean isOptional() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
        }

        public boolean isRepeated() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
        }

        public boolean isPacked() {
            return getOptions().getPacked();
        }

        public boolean isPackable() {
            return isRepeated() && getLiteType().isPackable();
        }

        public boolean hasDefaultValue() {
            return this.proto.hasDefaultValue();
        }

        public Object getDefaultValue() {
            if (getJavaType() != JavaType.MESSAGE) {
                return this.defaultValue;
            }
            throw new UnsupportedOperationException("FieldDescriptor.getDefaultValue() called on an embedded message field.");
        }

        public DescriptorProtos.FieldOptions getOptions() {
            return this.proto.getOptions();
        }

        public boolean isExtension() {
            return this.proto.hasExtendee();
        }

        public Descriptor getContainingType() {
            return this.containingType;
        }

        public Descriptor getExtensionScope() {
            if (isExtension()) {
                return this.extensionScope;
            }
            throw new UnsupportedOperationException("This field is not an extension.");
        }

        public Descriptor getMessageType() {
            if (getJavaType() == JavaType.MESSAGE) {
                return this.messageType;
            }
            throw new UnsupportedOperationException("This field is not of message type.");
        }

        public EnumDescriptor getEnumType() {
            if (getJavaType() == JavaType.ENUM) {
                return this.enumType;
            }
            throw new UnsupportedOperationException("This field is not of enum type.");
        }

        public int compareTo(FieldDescriptor other) {
            if (other.containingType == this.containingType) {
                return getNumber() - other.getNumber();
            }
            throw new IllegalArgumentException("FieldDescriptors can only be compared to other FieldDescriptors for fields of the same message type.");
        }

        public enum Type {
            DOUBLE(JavaType.DOUBLE),
            FLOAT(JavaType.FLOAT),
            INT64(JavaType.LONG),
            UINT64(JavaType.LONG),
            INT32(JavaType.INT),
            FIXED64(JavaType.LONG),
            FIXED32(JavaType.INT),
            BOOL(JavaType.BOOLEAN),
            STRING(JavaType.STRING),
            GROUP(JavaType.MESSAGE),
            MESSAGE(JavaType.MESSAGE),
            BYTES(JavaType.BYTE_STRING),
            UINT32(JavaType.INT),
            ENUM(JavaType.ENUM),
            SFIXED32(JavaType.INT),
            SFIXED64(JavaType.LONG),
            SINT32(JavaType.INT),
            SINT64(JavaType.LONG);
            
            private JavaType javaType;

            private Type(JavaType javaType2) {
                this.javaType = javaType2;
            }

            public DescriptorProtos.FieldDescriptorProto.Type toProto() {
                return DescriptorProtos.FieldDescriptorProto.Type.valueOf(ordinal() + 1);
            }

            public JavaType getJavaType() {
                return this.javaType;
            }

            public static Type valueOf(DescriptorProtos.FieldDescriptorProto.Type type) {
                return values()[type.getNumber() - 1];
            }
        }

        public enum JavaType {
            INT(0),
            LONG(0L),
            FLOAT(Float.valueOf(0.0f)),
            DOUBLE(Double.valueOf(0.0d)),
            BOOLEAN(false),
            STRING(""),
            BYTE_STRING(ByteString.EMPTY),
            ENUM((String) null),
            MESSAGE((String) null);
            
            /* access modifiers changed from: private */
            public final Object defaultDefault;

            private JavaType(Object defaultDefault2) {
                this.defaultDefault = defaultDefault2;
            }
        }

        private FieldDescriptor(DescriptorProtos.FieldDescriptorProto proto2, FileDescriptor file2, Descriptor parent, int index2, boolean isExtension) throws DescriptorValidationException {
            this.index = index2;
            this.proto = proto2;
            this.fullName = Descriptors.computeFullName(file2, parent, proto2.getName());
            this.file = file2;
            if (proto2.hasType()) {
                this.type = Type.valueOf(proto2.getType());
            }
            if (getNumber() <= 0) {
                throw new DescriptorValidationException((GenericDescriptor) this, "Field numbers must be positive integers.", (AnonymousClass1) null);
            } else if (!proto2.getOptions().getPacked() || isPackable()) {
                if (isExtension) {
                    if (proto2.hasExtendee()) {
                        this.containingType = null;
                        if (parent != null) {
                            this.extensionScope = parent;
                        } else {
                            this.extensionScope = null;
                        }
                    } else {
                        throw new DescriptorValidationException((GenericDescriptor) this, "FieldDescriptorProto.extendee not set for extension field.", (AnonymousClass1) null);
                    }
                } else if (!proto2.hasExtendee()) {
                    this.containingType = parent;
                    this.extensionScope = null;
                } else {
                    throw new DescriptorValidationException((GenericDescriptor) this, "FieldDescriptorProto.extendee set for non-extension field.", (AnonymousClass1) null);
                }
                file2.pool.addSymbol(this);
            } else {
                throw new DescriptorValidationException((GenericDescriptor) this, "[packed = true] can only be specified for repeated primitive fields.", (AnonymousClass1) null);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        /* access modifiers changed from: private */
        public void crossLink() throws DescriptorValidationException {
            if (this.proto.hasExtendee()) {
                GenericDescriptor extendee = this.file.pool.lookupSymbol(this.proto.getExtendee(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
                if (extendee instanceof Descriptor) {
                    this.containingType = (Descriptor) extendee;
                    if (!getContainingType().isExtensionNumber(getNumber())) {
                        throw new DescriptorValidationException((GenericDescriptor) this, '\"' + getContainingType().getFullName() + "\" does not declare " + getNumber() + " as an extension number.", (AnonymousClass1) null);
                    }
                } else {
                    throw new DescriptorValidationException((GenericDescriptor) this, '\"' + this.proto.getExtendee() + "\" is not a message type.", (AnonymousClass1) null);
                }
            }
            if (this.proto.hasTypeName()) {
                GenericDescriptor typeDescriptor = this.file.pool.lookupSymbol(this.proto.getTypeName(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
                if (!this.proto.hasType()) {
                    if (typeDescriptor instanceof Descriptor) {
                        this.type = Type.MESSAGE;
                    } else if (typeDescriptor instanceof EnumDescriptor) {
                        this.type = Type.ENUM;
                    } else {
                        throw new DescriptorValidationException((GenericDescriptor) this, '\"' + this.proto.getTypeName() + "\" is not a type.", (AnonymousClass1) null);
                    }
                }
                if (getJavaType() == JavaType.MESSAGE) {
                    if (typeDescriptor instanceof Descriptor) {
                        this.messageType = (Descriptor) typeDescriptor;
                        if (this.proto.hasDefaultValue()) {
                            throw new DescriptorValidationException((GenericDescriptor) this, "Messages can't have default values.", (AnonymousClass1) null);
                        }
                    } else {
                        throw new DescriptorValidationException((GenericDescriptor) this, '\"' + this.proto.getTypeName() + "\" is not a message type.", (AnonymousClass1) null);
                    }
                } else if (getJavaType() != JavaType.ENUM) {
                    throw new DescriptorValidationException((GenericDescriptor) this, "Field with primitive type has type_name.", (AnonymousClass1) null);
                } else if (typeDescriptor instanceof EnumDescriptor) {
                    this.enumType = (EnumDescriptor) typeDescriptor;
                } else {
                    throw new DescriptorValidationException((GenericDescriptor) this, '\"' + this.proto.getTypeName() + "\" is not an enum type.", (AnonymousClass1) null);
                }
            } else if (getJavaType() == JavaType.MESSAGE || getJavaType() == JavaType.ENUM) {
                throw new DescriptorValidationException((GenericDescriptor) this, "Field with message or enum type missing type_name.", (AnonymousClass1) null);
            }
            if (this.proto.hasDefaultValue()) {
                if (!isRepeated()) {
                    try {
                        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[getType().ordinal()]) {
                            case 1:
                            case 2:
                            case 3:
                                this.defaultValue = Integer.valueOf(TextFormat.parseInt32(this.proto.getDefaultValue()));
                                break;
                            case 4:
                            case 5:
                                this.defaultValue = Integer.valueOf(TextFormat.parseUInt32(this.proto.getDefaultValue()));
                                break;
                            case 6:
                            case 7:
                            case 8:
                                this.defaultValue = Long.valueOf(TextFormat.parseInt64(this.proto.getDefaultValue()));
                                break;
                            case 9:
                            case 10:
                                this.defaultValue = Long.valueOf(TextFormat.parseUInt64(this.proto.getDefaultValue()));
                                break;
                            case 11:
                                if (!this.proto.getDefaultValue().equals("inf")) {
                                    if (!this.proto.getDefaultValue().equals("-inf")) {
                                        if (!this.proto.getDefaultValue().equals("nan")) {
                                            this.defaultValue = Float.valueOf(this.proto.getDefaultValue());
                                            break;
                                        } else {
                                            this.defaultValue = Float.valueOf(Float.NaN);
                                            break;
                                        }
                                    } else {
                                        this.defaultValue = Float.valueOf(Float.NEGATIVE_INFINITY);
                                        break;
                                    }
                                } else {
                                    this.defaultValue = Float.valueOf(Float.POSITIVE_INFINITY);
                                    break;
                                }
                            case TYPE_BYTES_VALUE:
                                if (!this.proto.getDefaultValue().equals("inf")) {
                                    if (!this.proto.getDefaultValue().equals("-inf")) {
                                        if (!this.proto.getDefaultValue().equals("nan")) {
                                            this.defaultValue = Double.valueOf(this.proto.getDefaultValue());
                                            break;
                                        } else {
                                            this.defaultValue = Double.valueOf(Double.NaN);
                                            break;
                                        }
                                    } else {
                                        this.defaultValue = Double.valueOf(Double.NEGATIVE_INFINITY);
                                        break;
                                    }
                                } else {
                                    this.defaultValue = Double.valueOf(Double.POSITIVE_INFINITY);
                                    break;
                                }
                            case TYPE_UINT32_VALUE:
                                this.defaultValue = Boolean.valueOf(this.proto.getDefaultValue());
                                break;
                            case TYPE_ENUM_VALUE:
                                this.defaultValue = this.proto.getDefaultValue();
                                break;
                            case TYPE_SFIXED32_VALUE:
                                this.defaultValue = TextFormat.unescapeBytes(this.proto.getDefaultValue());
                                break;
                            case 16:
                                this.defaultValue = this.enumType.findValueByName(this.proto.getDefaultValue());
                                if (this.defaultValue != null) {
                                    break;
                                } else {
                                    throw new DescriptorValidationException((GenericDescriptor) this, "Unknown enum default value: \"" + this.proto.getDefaultValue() + '\"', (AnonymousClass1) null);
                                }
                            case 17:
                            case 18:
                                throw new DescriptorValidationException((GenericDescriptor) this, "Message type had default value.", (AnonymousClass1) null);
                        }
                    } catch (TextFormat.InvalidEscapeSequenceException e) {
                        throw new DescriptorValidationException(this, "Couldn't parse default value: " + e.getMessage(), e, (AnonymousClass1) null);
                    } catch (NumberFormatException e2) {
                        throw new DescriptorValidationException(this, "Could not parse default value: \"" + this.proto.getDefaultValue() + '\"', e2, (AnonymousClass1) null);
                    }
                } else {
                    throw new DescriptorValidationException((GenericDescriptor) this, "Repeated fields cannot have default values.", (AnonymousClass1) null);
                }
            } else if (isRepeated()) {
                this.defaultValue = Collections.emptyList();
            } else {
                int i = AnonymousClass1.$SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$JavaType[getJavaType().ordinal()];
                if (i == 1) {
                    this.defaultValue = this.enumType.getValues().get(0);
                } else if (i != 2) {
                    this.defaultValue = getJavaType().defaultDefault;
                } else {
                    this.defaultValue = null;
                }
            }
            if (!isExtension()) {
                this.file.pool.addFieldByNumber(this);
            }
            Descriptor descriptor = this.containingType;
            if (descriptor != null && descriptor.getOptions().getMessageSetWireFormat()) {
                if (!isExtension()) {
                    throw new DescriptorValidationException((GenericDescriptor) this, "MessageSets cannot have fields, only extensions.", (AnonymousClass1) null);
                } else if (!isOptional() || getType() != Type.MESSAGE) {
                    throw new DescriptorValidationException((GenericDescriptor) this, "Extensions of MessageSets must be optional messages.", (AnonymousClass1) null);
                }
            }
        }

        /* access modifiers changed from: private */
        public void setProto(DescriptorProtos.FieldDescriptorProto proto2) {
            this.proto = proto2;
        }

        public MessageLite.Builder internalMergeFrom(MessageLite.Builder to, MessageLite from) {
            return ((Message.Builder) to).mergeFrom((Message) from);
        }
    }

    /* renamed from: com.google.protobuf.Descriptors$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$JavaType = new int[FieldDescriptor.JavaType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type = new int[FieldDescriptor.Type.values().length];

        static {
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$JavaType[FieldDescriptor.JavaType.ENUM.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$JavaType[FieldDescriptor.JavaType.MESSAGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.INT32.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.SINT32.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.SFIXED32.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.UINT32.ordinal()] = 4;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.FIXED32.ordinal()] = 5;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.INT64.ordinal()] = 6;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.SINT64.ordinal()] = 7;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.SFIXED64.ordinal()] = 8;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.UINT64.ordinal()] = 9;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.FIXED64.ordinal()] = 10;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.FLOAT.ordinal()] = 11;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.DOUBLE.ordinal()] = 12;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.BOOL.ordinal()] = 13;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.STRING.ordinal()] = 14;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.BYTES.ordinal()] = 15;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.ENUM.ordinal()] = 16;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.MESSAGE.ordinal()] = 17;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$google$protobuf$Descriptors$FieldDescriptor$Type[FieldDescriptor.Type.GROUP.ordinal()] = 18;
            } catch (NoSuchFieldError e20) {
            }
        }
    }

    public static final class EnumDescriptor implements GenericDescriptor, Internal.EnumLiteMap<EnumValueDescriptor> {
        private final Descriptor containingType;
        private final FileDescriptor file;
        private final String fullName;
        private final int index;
        private DescriptorProtos.EnumDescriptorProto proto;
        private EnumValueDescriptor[] values;

        /* synthetic */ EnumDescriptor(DescriptorProtos.EnumDescriptorProto x0, FileDescriptor x1, Descriptor x2, int x3, AnonymousClass1 x4) throws DescriptorValidationException {
            this(x0, x1, x2, x3);
        }

        public int getIndex() {
            return this.index;
        }

        public DescriptorProtos.EnumDescriptorProto toProto() {
            return this.proto;
        }

        public String getName() {
            return this.proto.getName();
        }

        public String getFullName() {
            return this.fullName;
        }

        public FileDescriptor getFile() {
            return this.file;
        }

        public Descriptor getContainingType() {
            return this.containingType;
        }

        public DescriptorProtos.EnumOptions getOptions() {
            return this.proto.getOptions();
        }

        public List<EnumValueDescriptor> getValues() {
            return Collections.unmodifiableList(Arrays.asList(this.values));
        }

        public EnumValueDescriptor findValueByName(String name) {
            DescriptorPool access$1200 = this.file.pool;
            GenericDescriptor result = access$1200.findSymbol(this.fullName + '.' + name);
            if (result == null || !(result instanceof EnumValueDescriptor)) {
                return null;
            }
            return (EnumValueDescriptor) result;
        }

        public EnumValueDescriptor findValueByNumber(int number) {
            return (EnumValueDescriptor) this.file.pool.enumValuesByNumber.get(new DescriptorPool.DescriptorIntPair(this, number));
        }

        private EnumDescriptor(DescriptorProtos.EnumDescriptorProto proto2, FileDescriptor file2, Descriptor parent, int index2) throws DescriptorValidationException {
            this.index = index2;
            this.proto = proto2;
            this.fullName = Descriptors.computeFullName(file2, parent, proto2.getName());
            this.file = file2;
            this.containingType = parent;
            if (proto2.getValueCount() != 0) {
                this.values = new EnumValueDescriptor[proto2.getValueCount()];
                for (int i = 0; i < proto2.getValueCount(); i++) {
                    this.values[i] = new EnumValueDescriptor(proto2.getValue(i), file2, this, i, (AnonymousClass1) null);
                }
                file2.pool.addSymbol(this);
                return;
            }
            throw new DescriptorValidationException((GenericDescriptor) this, "Enums must contain at least one value.", (AnonymousClass1) null);
        }

        /* access modifiers changed from: private */
        public void setProto(DescriptorProtos.EnumDescriptorProto proto2) {
            this.proto = proto2;
            int i = 0;
            while (true) {
                EnumValueDescriptor[] enumValueDescriptorArr = this.values;
                if (i < enumValueDescriptorArr.length) {
                    enumValueDescriptorArr[i].setProto(proto2.getValue(i));
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public static final class EnumValueDescriptor implements GenericDescriptor, Internal.EnumLite {
        private final FileDescriptor file;
        private final String fullName;
        private final int index;
        private DescriptorProtos.EnumValueDescriptorProto proto;
        private final EnumDescriptor type;

        /* synthetic */ EnumValueDescriptor(DescriptorProtos.EnumValueDescriptorProto x0, FileDescriptor x1, EnumDescriptor x2, int x3, AnonymousClass1 x4) throws DescriptorValidationException {
            this(x0, x1, x2, x3);
        }

        public int getIndex() {
            return this.index;
        }

        public DescriptorProtos.EnumValueDescriptorProto toProto() {
            return this.proto;
        }

        public String getName() {
            return this.proto.getName();
        }

        public int getNumber() {
            return this.proto.getNumber();
        }

        public String getFullName() {
            return this.fullName;
        }

        public FileDescriptor getFile() {
            return this.file;
        }

        public EnumDescriptor getType() {
            return this.type;
        }

        public DescriptorProtos.EnumValueOptions getOptions() {
            return this.proto.getOptions();
        }

        private EnumValueDescriptor(DescriptorProtos.EnumValueDescriptorProto proto2, FileDescriptor file2, EnumDescriptor parent, int index2) throws DescriptorValidationException {
            this.index = index2;
            this.proto = proto2;
            this.file = file2;
            this.type = parent;
            this.fullName = parent.getFullName() + '.' + proto2.getName();
            file2.pool.addSymbol(this);
            file2.pool.addEnumValueByNumber(this);
        }

        /* access modifiers changed from: private */
        public void setProto(DescriptorProtos.EnumValueDescriptorProto proto2) {
            this.proto = proto2;
        }
    }

    public static final class ServiceDescriptor implements GenericDescriptor {
        private final FileDescriptor file;
        private final String fullName;
        private final int index;
        private MethodDescriptor[] methods;
        private DescriptorProtos.ServiceDescriptorProto proto;

        /* synthetic */ ServiceDescriptor(DescriptorProtos.ServiceDescriptorProto x0, FileDescriptor x1, int x2, AnonymousClass1 x3) throws DescriptorValidationException {
            this(x0, x1, x2);
        }

        public int getIndex() {
            return this.index;
        }

        public DescriptorProtos.ServiceDescriptorProto toProto() {
            return this.proto;
        }

        public String getName() {
            return this.proto.getName();
        }

        public String getFullName() {
            return this.fullName;
        }

        public FileDescriptor getFile() {
            return this.file;
        }

        public DescriptorProtos.ServiceOptions getOptions() {
            return this.proto.getOptions();
        }

        public List<MethodDescriptor> getMethods() {
            return Collections.unmodifiableList(Arrays.asList(this.methods));
        }

        public MethodDescriptor findMethodByName(String name) {
            DescriptorPool access$1200 = this.file.pool;
            GenericDescriptor result = access$1200.findSymbol(this.fullName + '.' + name);
            if (result == null || !(result instanceof MethodDescriptor)) {
                return null;
            }
            return (MethodDescriptor) result;
        }

        private ServiceDescriptor(DescriptorProtos.ServiceDescriptorProto proto2, FileDescriptor file2, int index2) throws DescriptorValidationException {
            this.index = index2;
            this.proto = proto2;
            this.fullName = Descriptors.computeFullName(file2, (Descriptor) null, proto2.getName());
            this.file = file2;
            this.methods = new MethodDescriptor[proto2.getMethodCount()];
            for (int i = 0; i < proto2.getMethodCount(); i++) {
                this.methods[i] = new MethodDescriptor(proto2.getMethod(i), file2, this, i, (AnonymousClass1) null);
            }
            file2.pool.addSymbol(this);
        }

        /* access modifiers changed from: private */
        public void crossLink() throws DescriptorValidationException {
            for (MethodDescriptor method : this.methods) {
                method.crossLink();
            }
        }

        /* access modifiers changed from: private */
        public void setProto(DescriptorProtos.ServiceDescriptorProto proto2) {
            this.proto = proto2;
            int i = 0;
            while (true) {
                MethodDescriptor[] methodDescriptorArr = this.methods;
                if (i < methodDescriptorArr.length) {
                    methodDescriptorArr[i].setProto(proto2.getMethod(i));
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public static final class MethodDescriptor implements GenericDescriptor {
        private final FileDescriptor file;
        private final String fullName;
        private final int index;
        private Descriptor inputType;
        private Descriptor outputType;
        private DescriptorProtos.MethodDescriptorProto proto;
        private final ServiceDescriptor service;

        /* synthetic */ MethodDescriptor(DescriptorProtos.MethodDescriptorProto x0, FileDescriptor x1, ServiceDescriptor x2, int x3, AnonymousClass1 x4) throws DescriptorValidationException {
            this(x0, x1, x2, x3);
        }

        public int getIndex() {
            return this.index;
        }

        public DescriptorProtos.MethodDescriptorProto toProto() {
            return this.proto;
        }

        public String getName() {
            return this.proto.getName();
        }

        public String getFullName() {
            return this.fullName;
        }

        public FileDescriptor getFile() {
            return this.file;
        }

        public ServiceDescriptor getService() {
            return this.service;
        }

        public Descriptor getInputType() {
            return this.inputType;
        }

        public Descriptor getOutputType() {
            return this.outputType;
        }

        public DescriptorProtos.MethodOptions getOptions() {
            return this.proto.getOptions();
        }

        private MethodDescriptor(DescriptorProtos.MethodDescriptorProto proto2, FileDescriptor file2, ServiceDescriptor parent, int index2) throws DescriptorValidationException {
            this.index = index2;
            this.proto = proto2;
            this.file = file2;
            this.service = parent;
            this.fullName = parent.getFullName() + '.' + proto2.getName();
            file2.pool.addSymbol(this);
        }

        /* access modifiers changed from: private */
        public void crossLink() throws DescriptorValidationException {
            GenericDescriptor input = this.file.pool.lookupSymbol(this.proto.getInputType(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
            if (input instanceof Descriptor) {
                this.inputType = (Descriptor) input;
                GenericDescriptor output = this.file.pool.lookupSymbol(this.proto.getOutputType(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
                if (output instanceof Descriptor) {
                    this.outputType = (Descriptor) output;
                    return;
                }
                throw new DescriptorValidationException((GenericDescriptor) this, '\"' + this.proto.getOutputType() + "\" is not a message type.", (AnonymousClass1) null);
            }
            throw new DescriptorValidationException((GenericDescriptor) this, '\"' + this.proto.getInputType() + "\" is not a message type.", (AnonymousClass1) null);
        }

        /* access modifiers changed from: private */
        public void setProto(DescriptorProtos.MethodDescriptorProto proto2) {
            this.proto = proto2;
        }
    }

    /* access modifiers changed from: private */
    public static String computeFullName(FileDescriptor file, Descriptor parent, String name) {
        if (parent != null) {
            return parent.getFullName() + '.' + name;
        } else if (file.getPackage().length() <= 0) {
            return name;
        } else {
            return file.getPackage() + '.' + name;
        }
    }

    public static class DescriptorValidationException extends Exception {
        private static final long serialVersionUID = 5750205775490483148L;
        private final String description;
        private final String name;
        private final Message proto;

        /* synthetic */ DescriptorValidationException(FileDescriptor x0, String x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        /* synthetic */ DescriptorValidationException(GenericDescriptor x0, String x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        /* synthetic */ DescriptorValidationException(GenericDescriptor x0, String x1, Throwable x2, AnonymousClass1 x3) {
            this(x0, x1, x2);
        }

        public String getProblemSymbolName() {
            return this.name;
        }

        public Message getProblemProto() {
            return this.proto;
        }

        public String getDescription() {
            return this.description;
        }

        private DescriptorValidationException(GenericDescriptor problemDescriptor, String description2) {
            super(problemDescriptor.getFullName() + ": " + description2);
            this.name = problemDescriptor.getFullName();
            this.proto = problemDescriptor.toProto();
            this.description = description2;
        }

        private DescriptorValidationException(GenericDescriptor problemDescriptor, String description2, Throwable cause) {
            this(problemDescriptor, description2);
            initCause(cause);
        }

        private DescriptorValidationException(FileDescriptor problemDescriptor, String description2) {
            super(problemDescriptor.getName() + ": " + description2);
            this.name = problemDescriptor.getName();
            this.proto = problemDescriptor.toProto();
            this.description = description2;
        }
    }

    private static final class DescriptorPool {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final Set<FileDescriptor> dependencies = new HashSet();
        private final Map<String, GenericDescriptor> descriptorsByName = new HashMap();
        /* access modifiers changed from: private */
        public final Map<DescriptorIntPair, EnumValueDescriptor> enumValuesByNumber = new HashMap();
        /* access modifiers changed from: private */
        public final Map<DescriptorIntPair, FieldDescriptor> fieldsByNumber = new HashMap();

        enum SearchFilter {
            TYPES_ONLY,
            AGGREGATES_ONLY,
            ALL_SYMBOLS
        }

        static {
            Class<Descriptors> cls = Descriptors.class;
        }

        DescriptorPool(FileDescriptor[] dependencies2) {
            for (int i = 0; i < dependencies2.length; i++) {
                this.dependencies.add(dependencies2[i]);
                importPublicDependencies(dependencies2[i]);
            }
            for (FileDescriptor dependency : this.dependencies) {
                try {
                    addPackage(dependency.getPackage(), dependency);
                } catch (DescriptorValidationException e) {
                }
            }
        }

        private void importPublicDependencies(FileDescriptor file) {
            for (FileDescriptor dependency : file.getPublicDependencies()) {
                if (this.dependencies.add(dependency)) {
                    importPublicDependencies(dependency);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public GenericDescriptor findSymbol(String fullName) {
            return findSymbol(fullName, SearchFilter.ALL_SYMBOLS);
        }

        /* access modifiers changed from: package-private */
        public GenericDescriptor findSymbol(String fullName, SearchFilter filter) {
            GenericDescriptor result = this.descriptorsByName.get(fullName);
            if (result != null && (filter == SearchFilter.ALL_SYMBOLS || ((filter == SearchFilter.TYPES_ONLY && isType(result)) || (filter == SearchFilter.AGGREGATES_ONLY && isAggregate(result))))) {
                return result;
            }
            for (FileDescriptor dependency : this.dependencies) {
                GenericDescriptor result2 = dependency.pool.descriptorsByName.get(fullName);
                if (result2 != null && (filter == SearchFilter.ALL_SYMBOLS || ((filter == SearchFilter.TYPES_ONLY && isType(result2)) || (filter == SearchFilter.AGGREGATES_ONLY && isAggregate(result2))))) {
                    return result2;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public boolean isType(GenericDescriptor descriptor) {
            return (descriptor instanceof Descriptor) || (descriptor instanceof EnumDescriptor);
        }

        /* access modifiers changed from: package-private */
        public boolean isAggregate(GenericDescriptor descriptor) {
            return (descriptor instanceof Descriptor) || (descriptor instanceof EnumDescriptor) || (descriptor instanceof PackageDescriptor) || (descriptor instanceof ServiceDescriptor);
        }

        /* access modifiers changed from: package-private */
        public GenericDescriptor lookupSymbol(String name, GenericDescriptor relativeTo, SearchFilter filter) throws DescriptorValidationException {
            GenericDescriptor result;
            String firstPart;
            if (name.startsWith(".")) {
                result = findSymbol(name.substring(1), filter);
            } else {
                int firstPartLength = name.indexOf(46);
                if (firstPartLength == -1) {
                    firstPart = name;
                } else {
                    firstPart = name.substring(0, firstPartLength);
                }
                StringBuilder scopeToTry = new StringBuilder(relativeTo.getFullName());
                while (true) {
                    int dotpos = scopeToTry.lastIndexOf(".");
                    if (dotpos == -1) {
                        result = findSymbol(name, filter);
                        break;
                    }
                    scopeToTry.setLength(dotpos + 1);
                    scopeToTry.append(firstPart);
                    GenericDescriptor result2 = findSymbol(scopeToTry.toString(), SearchFilter.AGGREGATES_ONLY);
                    if (result2 == null) {
                        scopeToTry.setLength(dotpos);
                    } else if (firstPartLength != -1) {
                        scopeToTry.setLength(dotpos + 1);
                        scopeToTry.append(name);
                        result = findSymbol(scopeToTry.toString(), filter);
                    } else {
                        result = result2;
                    }
                }
            }
            if (result != null) {
                return result;
            }
            throw new DescriptorValidationException(relativeTo, '\"' + name + "\" is not defined.", (AnonymousClass1) null);
        }

        /* access modifiers changed from: package-private */
        public void addSymbol(GenericDescriptor descriptor) throws DescriptorValidationException {
            validateSymbolName(descriptor);
            String fullName = descriptor.getFullName();
            int dotpos = fullName.lastIndexOf(46);
            GenericDescriptor old = this.descriptorsByName.put(fullName, descriptor);
            if (old != null) {
                this.descriptorsByName.put(fullName, old);
                if (descriptor.getFile() != old.getFile()) {
                    throw new DescriptorValidationException(descriptor, '\"' + fullName + "\" is already defined in file \"" + old.getFile().getName() + "\".", (AnonymousClass1) null);
                } else if (dotpos == -1) {
                    throw new DescriptorValidationException(descriptor, '\"' + fullName + "\" is already defined.", (AnonymousClass1) null);
                } else {
                    throw new DescriptorValidationException(descriptor, '\"' + fullName.substring(dotpos + 1) + "\" is already defined in \"" + fullName.substring(0, dotpos) + "\".", (AnonymousClass1) null);
                }
            }
        }

        private static final class PackageDescriptor implements GenericDescriptor {
            private final FileDescriptor file;
            private final String fullName;
            private final String name;

            public Message toProto() {
                return this.file.toProto();
            }

            public String getName() {
                return this.name;
            }

            public String getFullName() {
                return this.fullName;
            }

            public FileDescriptor getFile() {
                return this.file;
            }

            PackageDescriptor(String name2, String fullName2, FileDescriptor file2) {
                this.file = file2;
                this.fullName = fullName2;
                this.name = name2;
            }
        }

        /* access modifiers changed from: package-private */
        public void addPackage(String fullName, FileDescriptor file) throws DescriptorValidationException {
            String name;
            int dotpos = fullName.lastIndexOf(46);
            if (dotpos == -1) {
                name = fullName;
            } else {
                addPackage(fullName.substring(0, dotpos), file);
                name = fullName.substring(dotpos + 1);
            }
            GenericDescriptor old = this.descriptorsByName.put(fullName, new PackageDescriptor(name, fullName, file));
            if (old != null) {
                this.descriptorsByName.put(fullName, old);
                if (!(old instanceof PackageDescriptor)) {
                    throw new DescriptorValidationException(file, '\"' + name + "\" is already defined (as something other than a " + "package) in file \"" + old.getFile().getName() + "\".", (AnonymousClass1) null);
                }
            }
        }

        private static final class DescriptorIntPair {
            private final GenericDescriptor descriptor;
            private final int number;

            DescriptorIntPair(GenericDescriptor descriptor2, int number2) {
                this.descriptor = descriptor2;
                this.number = number2;
            }

            public int hashCode() {
                return (this.descriptor.hashCode() * 65535) + this.number;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof DescriptorIntPair)) {
                    return false;
                }
                DescriptorIntPair other = (DescriptorIntPair) obj;
                if (this.descriptor == other.descriptor && this.number == other.number) {
                    return true;
                }
                return false;
            }
        }

        /* access modifiers changed from: package-private */
        public void addFieldByNumber(FieldDescriptor field) throws DescriptorValidationException {
            DescriptorIntPair key = new DescriptorIntPair(field.getContainingType(), field.getNumber());
            FieldDescriptor old = this.fieldsByNumber.put(key, field);
            if (old != null) {
                this.fieldsByNumber.put(key, old);
                throw new DescriptorValidationException((GenericDescriptor) field, "Field number " + field.getNumber() + "has already been used in \"" + field.getContainingType().getFullName() + "\" by field \"" + old.getName() + "\".", (AnonymousClass1) null);
            }
        }

        /* access modifiers changed from: package-private */
        public void addEnumValueByNumber(EnumValueDescriptor value) {
            DescriptorIntPair key = new DescriptorIntPair(value.getType(), value.getNumber());
            EnumValueDescriptor old = this.enumValuesByNumber.put(key, value);
            if (old != null) {
                this.enumValuesByNumber.put(key, old);
            }
        }

        static void validateSymbolName(GenericDescriptor descriptor) throws DescriptorValidationException {
            String name = descriptor.getName();
            if (name.length() != 0) {
                boolean valid = true;
                for (int i = 0; i < name.length(); i++) {
                    char c = name.charAt(i);
                    if (c >= 128) {
                        valid = false;
                    }
                    if (!Character.isLetter(c) && c != '_' && (!Character.isDigit(c) || i <= 0)) {
                        valid = false;
                    }
                }
                if (!valid) {
                    throw new DescriptorValidationException(descriptor, '\"' + name + "\" is not a valid identifier.", (AnonymousClass1) null);
                }
                return;
            }
            throw new DescriptorValidationException(descriptor, "Missing name.", (AnonymousClass1) null);
        }
    }
}
