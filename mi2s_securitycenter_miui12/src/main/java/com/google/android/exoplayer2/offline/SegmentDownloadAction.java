package com.google.android.exoplayer2.offline;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.util.Assertions;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.Comparable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SegmentDownloadAction<K extends Comparable<K>> extends DownloadAction {
    public final List<K> keys;

    protected static abstract class SegmentDownloadActionDeserializer<K> extends DownloadAction.Deserializer {
        public SegmentDownloadActionDeserializer(String str, int i) {
            super(str, i);
        }

        /* access modifiers changed from: protected */
        public abstract DownloadAction createDownloadAction(Uri uri, boolean z, byte[] bArr, List<K> list);

        public final DownloadAction readFromStream(int i, DataInputStream dataInputStream) {
            Uri parse = Uri.parse(dataInputStream.readUTF());
            boolean readBoolean = dataInputStream.readBoolean();
            byte[] bArr = new byte[dataInputStream.readInt()];
            dataInputStream.readFully(bArr);
            int readInt = dataInputStream.readInt();
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < readInt; i2++) {
                arrayList.add(readKey(dataInputStream));
            }
            return createDownloadAction(parse, readBoolean, bArr, arrayList);
        }

        /* access modifiers changed from: protected */
        public abstract K readKey(DataInputStream dataInputStream);
    }

    protected SegmentDownloadAction(String str, int i, Uri uri, boolean z, @Nullable byte[] bArr, List<K> list) {
        super(str, i, uri, z, bArr);
        List<K> list2;
        if (z) {
            Assertions.checkArgument(list.isEmpty());
            list2 = Collections.emptyList();
        } else {
            ArrayList arrayList = new ArrayList(list);
            Collections.sort(arrayList);
            list2 = Collections.unmodifiableList(arrayList);
        }
        this.keys = list2;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return this.keys.equals(((SegmentDownloadAction) obj).keys);
    }

    public int hashCode() {
        return (super.hashCode() * 31) + this.keys.hashCode();
    }

    /* access modifiers changed from: protected */
    public abstract void writeKey(DataOutputStream dataOutputStream, K k);

    public final void writeToStream(DataOutputStream dataOutputStream) {
        dataOutputStream.writeUTF(this.uri.toString());
        dataOutputStream.writeBoolean(this.isRemoveAction);
        dataOutputStream.writeInt(this.data.length);
        dataOutputStream.write(this.data);
        dataOutputStream.writeInt(this.keys.size());
        for (int i = 0; i < this.keys.size(); i++) {
            writeKey(dataOutputStream, (Comparable) this.keys.get(i));
        }
    }
}
