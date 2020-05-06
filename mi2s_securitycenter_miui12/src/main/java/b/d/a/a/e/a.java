package b.d.a.a.e;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

public class a {
    public static float[] a(String str) {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(str)));
        int readInt = dataInputStream.readInt();
        if (dataInputStream.readInt() == 1) {
            float[] fArr = new float[readInt];
            for (int i = 0; i < readInt; i++) {
                fArr[i] = dataInputStream.readFloat();
            }
            return fArr;
        }
        throw new RuntimeException("文件格式错误");
    }

    public static int[] b(String str) {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(str)));
        int readInt = dataInputStream.readInt();
        if (dataInputStream.readInt() == 1) {
            int[] iArr = new int[readInt];
            for (int i = 0; i < readInt; i++) {
                iArr[i] = dataInputStream.readInt();
            }
            return iArr;
        }
        throw new RuntimeException("文件格式错误");
    }
}
