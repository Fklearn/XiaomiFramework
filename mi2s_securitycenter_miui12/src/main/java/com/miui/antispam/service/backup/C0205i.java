package com.miui.antispam.service.backup;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

/* renamed from: com.miui.antispam.service.backup.i  reason: case insensitive filesystem */
class C0205i extends AbstractParser<C0206j> {
    C0205i() {
    }

    public C0206j parsePartialFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
        return new C0206j(codedInputStream, extensionRegistryLite);
    }
}
