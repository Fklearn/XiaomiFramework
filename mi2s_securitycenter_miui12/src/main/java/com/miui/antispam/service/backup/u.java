package com.miui.antispam.service.backup;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

class u extends AbstractParser<v> {
    u() {
    }

    public v parsePartialFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
        return new v(codedInputStream, extensionRegistryLite);
    }
}
