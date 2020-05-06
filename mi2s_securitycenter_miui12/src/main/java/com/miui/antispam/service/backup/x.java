package com.miui.antispam.service.backup;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

class x extends AbstractParser<y> {
    x() {
    }

    public y parsePartialFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
        return new y(codedInputStream, extensionRegistryLite);
    }
}
