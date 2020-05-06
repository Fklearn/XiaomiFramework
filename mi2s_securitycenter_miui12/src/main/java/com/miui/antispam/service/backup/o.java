package com.miui.antispam.service.backup;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

class o extends AbstractParser<p> {
    o() {
    }

    public p parsePartialFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
        return new p(codedInputStream, extensionRegistryLite);
    }
}
