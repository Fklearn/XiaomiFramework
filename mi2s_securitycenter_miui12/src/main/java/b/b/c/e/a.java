package b.b.c.e;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class a {
    public static String a(File file) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read > 0) {
                    instance.update(bArr, 0, read);
                } else {
                    String replace = String.format("%32s", new Object[]{new BigInteger(1, instance.digest()).toString(16)}).replace(' ', '0');
                    fileInputStream.close();
                    return replace;
                }
            }
        } catch (FileNotFoundException | NoSuchAlgorithmException unused) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        }
    }
}
