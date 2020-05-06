package com.android.server.backup.encryption.chunking;

import java.io.IOException;

public class LengthlessEncryptedChunkEncoder implements EncryptedChunkEncoder {
    public void writeChunkToWriter(BackupWriter writer, EncryptedChunk chunk) throws IOException {
        writer.writeBytes(chunk.nonce());
        writer.writeBytes(chunk.encryptedBytes());
    }

    public int getEncodedLengthOfChunk(EncryptedChunk chunk) {
        return chunk.nonce().length + chunk.encryptedBytes().length;
    }

    public int getChunkOrderingType() {
        return 1;
    }
}
