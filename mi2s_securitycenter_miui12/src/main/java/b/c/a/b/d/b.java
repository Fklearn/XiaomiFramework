package b.c.a.b.d;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import b.b.o.g.e;
import b.c.a.b.a.a;
import b.c.a.b.d.d;
import b.c.a.c.c;
import com.miui.maml.util.net.SimpleRequest;
import com.miui.networkassistant.model.DataUsageConstants;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class b implements d {

    /* renamed from: a  reason: collision with root package name */
    protected final Context f2015a;

    /* renamed from: b  reason: collision with root package name */
    protected final int f2016b;

    /* renamed from: c  reason: collision with root package name */
    protected final int f2017c;

    public b(Context context) {
        this(context, 15000, DataUsageConstants.UID_MAX_IN_THEORY);
    }

    public b(Context context, int i, int i2) {
        this.f2015a = context.getApplicationContext();
        this.f2016b = i;
        this.f2017c = i2;
    }

    private Bitmap a(Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return createBitmap;
    }

    @TargetApi(8)
    private InputStream a(String str) {
        Bitmap createVideoThumbnail;
        if (Build.VERSION.SDK_INT < 8 || (createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(str, 2)) == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createVideoThumbnail.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private boolean b(Uri uri) {
        String type = this.f2015a.getContentResolver().getType(uri);
        return type != null && type.startsWith("video/");
    }

    private boolean b(String str) {
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(str));
        return mimeTypeFromExtension != null && mimeTypeFromExtension.startsWith("video/");
    }

    private InputStream j(String str, Object obj) {
        String a2 = d.a.APK_ICON.a(str);
        PackageManager packageManager = this.f2015a.getPackageManager();
        PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(a2, 1);
        if (packageArchiveInfo == null) {
            return null;
        }
        ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
        applicationInfo.publicSourceDir = a2;
        Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);
        if (applicationIcon instanceof BitmapDrawable) {
            return new c(((BitmapDrawable) applicationIcon).getBitmap());
        }
        return null;
    }

    private InputStream k(String str, Object obj) {
        return null;
    }

    private InputStream l(String str, Object obj) {
        try {
            PackageManager packageManager = this.f2015a.getPackageManager();
            UserManager userManager = (UserManager) this.f2015a.getSystemService("user");
            String[] split = d.a.PCK_ICON_MANAGED_PROFILE.a(str).split("/");
            Drawable drawable = (Drawable) e.a((Object) userManager, Drawable.class, "getBadgedIconForUser", (Class<?>[]) new Class[]{Drawable.class, UserHandle.class}, packageManager.getApplicationInfo(split[0], 0).loadIcon(packageManager), new UserHandle(UserHandle.getUserId(split.length > 1 ? Integer.parseInt(split[1]) : 0)));
            if (drawable != null) {
                return new c(a(drawable));
            }
            return null;
        } catch (Exception e) {
            b.c.a.c.d.a(e, "BaseImageDownloader exception, uri is %s", str);
            return null;
        }
    }

    private InputStream m(String str, Object obj) {
        try {
            Drawable applicationIcon = this.f2015a.getPackageManager().getApplicationIcon(d.a.PKG_ICON.a(str));
            if (applicationIcon instanceof BitmapDrawable) {
                return new c(((BitmapDrawable) applicationIcon).getBitmap());
            }
            if (applicationIcon != null) {
                return new c(a(applicationIcon));
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IOException(e);
        }
    }

    private InputStream n(String str, Object obj) {
        try {
            Drawable applicationIcon = this.f2015a.getPackageManager().getApplicationIcon(d.a.PKG_ICON_XSPACE.a(str));
            Method declaredMethod = Class.forName("miui.securityspace.XSpaceUserHandle").getDeclaredMethod("getXSpaceIcon", new Class[]{Context.class, Drawable.class});
            declaredMethod.setAccessible(true);
            Drawable drawable = (Drawable) declaredMethod.invoke((Object) null, new Object[]{this.f2015a, applicationIcon});
            if (applicationIcon instanceof BitmapDrawable) {
                if (drawable != null) {
                    return new c(((BitmapDrawable) drawable).getBitmap());
                }
            } else if (drawable != null) {
                return new c(a(drawable));
            }
        } catch (Exception e) {
            b.c.a.c.d.a(e, "BaseImageDownloader exception, uri is %s", str);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @TargetApi(14)
    public InputStream a(Uri uri) {
        ContentResolver contentResolver = this.f2015a.getContentResolver();
        return Build.VERSION.SDK_INT >= 14 ? ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri, true) : ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
    }

    public InputStream a(String str, Object obj) {
        switch (a.f2014a[d.a.b(str).ordinal()]) {
            case 1:
            case 2:
                return g(str, obj);
            case 3:
                return f(str, obj);
            case 4:
                return d(str, obj);
            case 5:
                return c(str, obj);
            case 6:
                return e(str, obj);
            case 7:
                return m(str, obj);
            case 8:
                return j(str, obj);
            case 9:
                return k(str, obj);
            case 10:
                return n(str, obj);
            case 11:
                return i(str, obj);
            case 12:
                return l(str, obj);
            default:
                h(str, obj);
                throw null;
        }
    }

    /* access modifiers changed from: protected */
    public boolean a(HttpURLConnection httpURLConnection) {
        return httpURLConnection.getResponseCode() == 200;
    }

    /* access modifiers changed from: protected */
    public HttpURLConnection b(String str, Object obj) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(Uri.encode(str, "@#&=*+-_.,:!?()/~'%")).openConnection();
        httpURLConnection.setConnectTimeout(this.f2016b);
        httpURLConnection.setReadTimeout(this.f2017c);
        return httpURLConnection;
    }

    /* access modifiers changed from: protected */
    public InputStream c(String str, Object obj) {
        return this.f2015a.getAssets().open(d.a.ASSETS.a(str));
    }

    /* access modifiers changed from: protected */
    public InputStream d(String str, Object obj) {
        ContentResolver contentResolver = this.f2015a.getContentResolver();
        Uri parse = Uri.parse(str);
        if (b(parse)) {
            Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, Long.valueOf(parse.getLastPathSegment()).longValue(), 1, (BitmapFactory.Options) null);
            if (thumbnail != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            }
        } else if (str.startsWith("content://com.android.contacts/")) {
            return a(parse);
        }
        return contentResolver.openInputStream(parse);
    }

    /* access modifiers changed from: protected */
    public InputStream e(String str, Object obj) {
        return this.f2015a.getResources().openRawResource(Integer.parseInt(d.a.DRAWABLE.a(str)));
    }

    /* access modifiers changed from: protected */
    public InputStream f(String str, Object obj) {
        String a2 = d.a.FILE.a(str);
        return b(str) ? a(a2) : new a(new BufferedInputStream(new FileInputStream(a2), 32768), (int) new File(a2).length());
    }

    /* access modifiers changed from: protected */
    public InputStream g(String str, Object obj) {
        HttpURLConnection b2 = b(str, obj);
        int i = 0;
        while (b2.getResponseCode() / 100 == 3 && i < 5) {
            b2 = b(b2.getHeaderField(SimpleRequest.LOCATION), obj);
            i++;
        }
        try {
            InputStream inputStream = b2.getInputStream();
            if (a(b2)) {
                return new a(new BufferedInputStream(inputStream, 32768), b2.getContentLength());
            }
            c.a((Closeable) inputStream);
            throw new IOException("Image request failed with response code " + b2.getResponseCode());
        } catch (IOException e) {
            c.a(b2.getErrorStream());
            throw e;
        }
    }

    /* access modifiers changed from: protected */
    public InputStream h(String str, Object obj) {
        throw new UnsupportedOperationException(String.format("UIL doesn't support scheme(protocol) by default [%s]. You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))", new Object[]{str}));
    }

    /* access modifiers changed from: protected */
    public InputStream i(String str, Object obj) {
        return a(d.a.VIDEO_FILE.a(str));
    }
}
