package b.d.e.a;

public class b {
    public static float a(byte[] bArr, int i) {
        return Float.intBitsToFloat(b(bArr, i));
    }

    public static int b(byte[] bArr, int i) {
        return ((bArr[i + 0] & 255) << 24) | (bArr[i + 3] & 255) | ((bArr[i + 2] & 255) << 8) | ((bArr[i + 1] & 255) << 16);
    }
}
