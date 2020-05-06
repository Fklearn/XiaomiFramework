package com.miui.antispam.service.backup;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

/* renamed from: com.miui.antispam.service.backup.c  reason: case insensitive filesystem */
class C0199c extends AbstractParser<C0200d> {
    C0199c() {
    }

    public C0200d parsePartialFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
        return new C0200d(codedInputStream, extensionRegistryLite);
    }
}
