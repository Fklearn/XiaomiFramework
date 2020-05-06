package com.google.protobuf;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.MessageOrBuilder;

public class SingleFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder> implements GeneratedMessage.BuilderParent {
    private BType builder;
    private boolean isClean;
    private MType message;
    private GeneratedMessage.BuilderParent parent;

    public SingleFieldBuilder(MType message2, GeneratedMessage.BuilderParent parent2, boolean isClean2) {
        if (message2 != null) {
            this.message = message2;
            this.parent = parent2;
            this.isClean = isClean2;
            return;
        }
        throw new NullPointerException();
    }

    public void dispose() {
        this.parent = null;
    }

    public MType getMessage() {
        if (this.message == null) {
            this.message = (GeneratedMessage) this.builder.buildPartial();
        }
        return this.message;
    }

    public MType build() {
        this.isClean = true;
        return getMessage();
    }

    public BType getBuilder() {
        if (this.builder == null) {
            this.builder = (GeneratedMessage.Builder) this.message.newBuilderForType(this);
            this.builder.mergeFrom((Message) this.message);
            this.builder.markClean();
        }
        return this.builder;
    }

    public IType getMessageOrBuilder() {
        Object obj = this.builder;
        if (obj != null) {
            return obj;
        }
        return this.message;
    }

    public SingleFieldBuilder<MType, BType, IType> setMessage(MType message2) {
        if (message2 != null) {
            this.message = message2;
            BType btype = this.builder;
            if (btype != null) {
                btype.dispose();
                this.builder = null;
            }
            onChanged();
            return this;
        }
        throw new NullPointerException();
    }

    public SingleFieldBuilder<MType, BType, IType> mergeFrom(MType value) {
        if (this.builder == null) {
            MType mtype = this.message;
            if (mtype == mtype.getDefaultInstanceForType()) {
                this.message = value;
                onChanged();
                return this;
            }
        }
        getBuilder().mergeFrom((Message) value);
        onChanged();
        return this;
    }

    public SingleFieldBuilder<MType, BType, IType> clear() {
        MType mtype = this.message;
        this.message = (GeneratedMessage) (mtype != null ? mtype.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType());
        BType btype = this.builder;
        if (btype != null) {
            btype.dispose();
            this.builder = null;
        }
        onChanged();
        return this;
    }

    private void onChanged() {
        GeneratedMessage.BuilderParent builderParent;
        if (this.builder != null) {
            this.message = null;
        }
        if (this.isClean && (builderParent = this.parent) != null) {
            builderParent.markDirty();
            this.isClean = false;
        }
    }

    public void markDirty() {
        onChanged();
    }
}
