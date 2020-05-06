package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentMetadataMutations {
    private final Map<String, Object> editedValues = new HashMap();
    private final List<String> removedValues = new ArrayList();

    private ContentMetadataMutations checkAndSet(String str, Object obj) {
        Map<String, Object> map = this.editedValues;
        Assertions.checkNotNull(str);
        Assertions.checkNotNull(obj);
        map.put(str, obj);
        this.removedValues.remove(str);
        return this;
    }

    public Map<String, Object> getEditedValues() {
        HashMap hashMap = new HashMap(this.editedValues);
        for (Map.Entry entry : hashMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof byte[]) {
                byte[] bArr = (byte[]) value;
                entry.setValue(Arrays.copyOf(bArr, bArr.length));
            }
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public List<String> getRemovedValues() {
        return Collections.unmodifiableList(new ArrayList(this.removedValues));
    }

    public ContentMetadataMutations remove(String str) {
        this.removedValues.add(str);
        this.editedValues.remove(str);
        return this;
    }

    public ContentMetadataMutations set(String str, long j) {
        checkAndSet(str, Long.valueOf(j));
        return this;
    }

    public ContentMetadataMutations set(String str, String str2) {
        checkAndSet(str, str2);
        return this;
    }

    public ContentMetadataMutations set(String str, byte[] bArr) {
        checkAndSet(str, Arrays.copyOf(bArr, bArr.length));
        return this;
    }
}
