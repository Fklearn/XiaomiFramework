package com.miui.antispam.service.backup;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

/* renamed from: com.miui.antispam.service.backup.f  reason: case insensitive filesystem */
class C0202f extends AbstractParser<C0203g> {
    C0202f() {
    }

    public C0203g parsePartialFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
        return new C0203g(codedInputStream, extensionRegistryLite);
    }
}
