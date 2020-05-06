package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ExtensionRegistry extends ExtensionRegistryLite {
    private static final ExtensionRegistry EMPTY = new ExtensionRegistry(true);
    private final Map<String, ExtensionInfo> extensionsByName;
    private final Map<DescriptorIntPair, ExtensionInfo> extensionsByNumber;

    public static ExtensionRegistry newInstance() {
        return new ExtensionRegistry();
    }

    public static ExtensionRegistry getEmptyRegistry() {
        return EMPTY;
    }

    public ExtensionRegistry getUnmodifiable() {
        return new ExtensionRegistry(this);
    }

    public static final class ExtensionInfo {
        public final Message defaultInstance;
        public final Descriptors.FieldDescriptor descriptor;

        private ExtensionInfo(Descriptors.FieldDescriptor descriptor2) {
            this.descriptor = descriptor2;
            this.defaultInstance = null;
        }

        private ExtensionInfo(Descriptors.FieldDescriptor descriptor2, Message defaultInstance2) {
            this.descriptor = descriptor2;
            this.defaultInstance = defaultInstance2;
        }
    }

    public ExtensionInfo findExtensionByName(String fullName) {
        return this.extensionsByName.get(fullName);
    }

    public ExtensionInfo findExtensionByNumber(Descriptors.Descriptor containingType, int fieldNumber) {
        return this.extensionsByNumber.get(new DescriptorIntPair(containingType, fieldNumber));
    }

    public void add(GeneratedMessage.GeneratedExtension<?, ?> extension) {
        if (extension.getDescriptor().getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            add(new ExtensionInfo(extension.getDescriptor(), (Message) null));
        } else if (extension.getMessageDefaultInstance() != null) {
            add(new ExtensionInfo(extension.getDescriptor(), extension.getMessageDefaultInstance()));
        } else {
            throw new IllegalStateException("Registered message-type extension had null default instance: " + extension.getDescriptor().getFullName());
        }
    }

    public void add(Descriptors.FieldDescriptor type) {
        if (type.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            add(new ExtensionInfo(type, (Message) null));
            return;
        }
        throw new IllegalArgumentException("ExtensionRegistry.add() must be provided a default instance when adding an embedded message extension.");
    }

    public void add(Descriptors.FieldDescriptor type, Message defaultInstance) {
        if (type.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            add(new ExtensionInfo(type, defaultInstance));
            return;
        }
        throw new IllegalArgumentException("ExtensionRegistry.add() provided a default instance for a non-message extension.");
    }

    private ExtensionRegistry() {
        this.extensionsByName = new HashMap();
        this.extensionsByNumber = new HashMap();
    }

    private ExtensionRegistry(ExtensionRegistry other) {
        super((ExtensionRegistryLite) other);
        this.extensionsByName = Collections.unmodifiableMap(other.extensionsByName);
        this.extensionsByNumber = Collections.unmodifiableMap(other.extensionsByNumber);
    }

    private ExtensionRegistry(boolean empty) {
        super(ExtensionRegistryLite.getEmptyRegistry());
        this.extensionsByName = Collections.emptyMap();
        this.extensionsByNumber = Collections.emptyMap();
    }

    private void add(ExtensionInfo extension) {
        if (extension.descriptor.isExtension()) {
            this.extensionsByName.put(extension.descriptor.getFullName(), extension);
            this.extensionsByNumber.put(new DescriptorIntPair(extension.descriptor.getContainingType(), extension.descriptor.getNumber()), extension);
            Descriptors.FieldDescriptor field = extension.descriptor;
            if (field.getContainingType().getOptions().getMessageSetWireFormat() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && field.isOptional() && field.getExtensionScope() == field.getMessageType()) {
                this.extensionsByName.put(field.getMessageType().getFullName(), extension);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("ExtensionRegistry.add() was given a FieldDescriptor for a regular (non-extension) field.");
    }

    private static final class DescriptorIntPair {
        private final Descriptors.Descriptor descriptor;
        private final int number;

        DescriptorIntPair(Descriptors.Descriptor descriptor2, int number2) {
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
}
