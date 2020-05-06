package miui.util;

import android.content.Context;
import android.net.Uri;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class InputStreamLoader {
    ByteArrayInputStream mByteArrayInputStream;
    private Context mContext;
    private FileAccessable mFileAccessable;
    private InputStream mInputStream;
    private String mPath;
    private Uri mUri;
    private ZipFile mZipFile;
    private String mZipPath;

    public InputStreamLoader(String path) {
        this.mPath = path;
    }

    public InputStreamLoader(Context context, Uri uri) {
        if ("file".equals(uri.getScheme())) {
            this.mPath = uri.getPath();
            return;
        }
        this.mContext = context;
        this.mUri = uri;
    }

    public InputStreamLoader(String zipPath, String entry) {
        this.mZipPath = zipPath;
        this.mPath = entry;
    }

    public InputStreamLoader(byte[] data) {
        this.mByteArrayInputStream = new ByteArrayInputStream(data);
    }

    public InputStreamLoader(FileAccessable fileAccessable) {
        this.mFileAccessable = fileAccessable;
    }

    public InputStream get() {
        close();
        try {
            if (this.mFileAccessable != null) {
                this.mInputStream = this.mFileAccessable.getInputStream();
            } else if (this.mUri != null) {
                this.mInputStream = this.mContext.getContentResolver().openInputStream(this.mUri);
            } else if (this.mZipPath != null) {
                this.mZipFile = new ZipFile(this.mZipPath);
                this.mInputStream = this.mZipFile.getInputStream(this.mZipFile.getEntry(this.mPath));
            } else if (this.mPath != null) {
                this.mInputStream = new FileInputStream(this.mPath);
            } else if (this.mByteArrayInputStream != null) {
                this.mByteArrayInputStream.reset();
                this.mInputStream = this.mByteArrayInputStream;
            }
        } catch (Exception e) {
        }
        InputStream inputStream = this.mInputStream;
        if (inputStream != null && !(inputStream instanceof ByteArrayInputStream)) {
            this.mInputStream = new BufferedInputStream(inputStream, 16384);
        }
        return this.mInputStream;
    }

    public void close() {
        try {
            if (this.mInputStream != null) {
                this.mInputStream.close();
            }
            if (this.mZipFile != null) {
                this.mZipFile.close();
            }
        } catch (IOException e) {
        }
    }
}
