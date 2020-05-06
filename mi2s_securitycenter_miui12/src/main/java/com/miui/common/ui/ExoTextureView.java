package com.miui.common.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.miui.analytics.AnalyticsUtil;
import com.miui.securitycenter.R;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class ExoTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    /* renamed from: a  reason: collision with root package name */
    private a f3853a;

    private class a implements SurfaceTexture.OnFrameAvailableListener {

        /* renamed from: a  reason: collision with root package name */
        private final float[] f3854a = {-1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};

        /* renamed from: b  reason: collision with root package name */
        private FloatBuffer f3855b;

        /* renamed from: c  reason: collision with root package name */
        private final String f3856c = "attribute vec3 aPosition;\nattribute vec2 aTexCoord;\nvarying vec2 vTexCoord;\n\nvoid main() {\n  vTexCoord = aTexCoord;\n\n  vec4 positionVec4 = vec4(aPosition, 1.0);\n  gl_Position = positionVec4;\n}\n";

        /* renamed from: d  reason: collision with root package name */
        private final String f3857d = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\n\nvarying vec2 vTexCoord;\n\nuniform samplerExternalOES uTexture;\nuniform float uState;\nuniform float uHue;\n\n#define tex0 uTexture\n\n#define OFFSET 0.002\nconst vec2 OFFSET0 = vec2(0.0, OFFSET);\nconst vec2 OFFSET1 = vec2(0.0, -OFFSET);\nconst vec2 OFFSET2 = vec2(OFFSET, 0.0);\nconst vec2 OFFSET3 = vec2(-OFFSET, 0.0);\n\nfloat getAlpha(vec3 color){\n    return 3.0 - color.r - color.g - color.b;\n}\n\nvec3 rgb2hsv(vec3 c)\n{\n    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);\n    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));\n    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));\n\n    float d = q.x - min(q.w, q.y);\n    float e = 1.0e-10;\n    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);\n}\n\nvec3 hsv2rgb(vec3 c)\n{\n    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n}\n\n\nvec3 transferH(vec3 rgb,float h){\n    vec3 hsv = rgb2hsv(rgb);\n    hsv.x += h;\n    return hsv2rgb(hsv);\n}\n\nvec3 setH(vec3 rgb,float h){\n    vec3 hsv = rgb2hsv(rgb);\n    hsv.x = h;\n    return hsv2rgb(hsv);\n}\n\nvec3 toRed(vec3 rgb){\n    return setH(rgb,0.05);\n}\n\nvec3 toGreen(vec3 rgb){\n    return transferH(rgb,0.72);\n}\n\nvoid main() {\n    vec2 uv = vTexCoord;   \n    uv.y = 1. - uv.y;\n    vec3 rgb = texture2D(tex0, uv).rgb;\n    rgb = mix(rgb,setH(rgb,uHue/360.),uState);\n    gl_FragColor = vec4(rgb, 1.);\n}";
        private float[] e = new float[16];
        private float[] f = new float[16];
        private int g;
        private int h;
        private int i;
        private int j;
        private int k;
        private int l;
        private float m = 0.0f;
        private float n = 20.0f;
        private SurfaceTexture o;
        private AtomicBoolean p = new AtomicBoolean(false);
        /* access modifiers changed from: private */
        public SimpleExoPlayer q;
        private Context r;
        private HandlerThread s;
        private Handler t;
        private EGL10 u = null;
        private EGLDisplay v = EGL10.EGL_NO_DISPLAY;
        private EGLContext w = EGL10.EGL_NO_CONTEXT;
        private EGLConfig[] x = new EGLConfig[1];
        private EGLSurface y;

        public a(Context context) {
            this.r = context;
        }

        private int a(int i2, String str) {
            int glCreateShader = GLES20.glCreateShader(i2);
            if (glCreateShader == 0) {
                return glCreateShader;
            }
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] != 0) {
                return glCreateShader;
            }
            Log.e("VideoRender", "Could not compile shader " + i2 + ":");
            Log.e("VideoRender", GLES20.glGetShaderInfoLog(glCreateShader));
            GLES20.glDeleteShader(glCreateShader);
            return 0;
        }

        private int a(String str, String str2) {
            int a2;
            int a3 = a(35633, str);
            if (a3 == 0 || (a2 = a(35632, str2)) == 0) {
                return 0;
            }
            int glCreateProgram = GLES20.glCreateProgram();
            if (glCreateProgram != 0) {
                GLES20.glAttachShader(glCreateProgram, a3);
                a("glAttachShader");
                GLES20.glAttachShader(glCreateProgram, a2);
                a("glAttachShader");
                GLES20.glLinkProgram(glCreateProgram);
                int[] iArr = new int[1];
                GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
                if (iArr[0] != 1) {
                    Log.e("VideoRender", "Could not link program: ");
                    Log.e("VideoRender", GLES20.glGetProgramInfoLog(glCreateProgram));
                    GLES20.glDeleteProgram(glCreateProgram);
                    return 0;
                }
            }
            return glCreateProgram;
        }

        private void a(String str) {
            while (true) {
                int glGetError = GLES20.glGetError();
                if (glGetError != 0) {
                    Log.e("VideoRender", str + ": glError " + glGetError);
                } else {
                    return;
                }
            }
        }

        private void c() {
            if (this.t != null) {
                this.s.quit();
                this.s = null;
                this.t = null;
            }
        }

        /* access modifiers changed from: private */
        public void d() {
            this.u = (EGL10) EGLContext.getEGL();
            this.v = this.u.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.v == EGL10.EGL_NO_DISPLAY) {
                Log.e("VideoRender", "eglGetDisplay failed! " + this.u.eglGetError());
            }
            if (!this.u.eglInitialize(this.v, new int[2])) {
                Log.e("VideoRender", "eglInitialize failed! " + this.u.eglGetError());
            }
            if (!this.u.eglChooseConfig(this.v, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12320, 32, 12352, 4, 12339, 4, 12344}, this.x, 1, new int[1])) {
                Log.e("VideoRender", "eglChooseConfig failed! " + this.u.eglGetError());
            }
            SurfaceTexture surfaceTexture = ExoTextureView.this.getSurfaceTexture();
            if (surfaceTexture != null) {
                this.y = this.u.eglCreateWindowSurface(this.v, this.x[0], surfaceTexture, (int[]) null);
                this.w = this.u.eglCreateContext(this.v, this.x[0], EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                if (this.v == EGL10.EGL_NO_DISPLAY || this.w == EGL10.EGL_NO_CONTEXT) {
                    Log.e("VideoRender", "eglCreateContext fail failed! " + this.u.eglGetError());
                }
                EGL10 egl10 = this.u;
                EGLDisplay eGLDisplay = this.v;
                EGLSurface eGLSurface = this.y;
                if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.w)) {
                    Log.e("VideoRender", "eglMakeCurrent failed! " + this.u.eglGetError());
                }
                this.g = a("attribute vec3 aPosition;\nattribute vec2 aTexCoord;\nvarying vec2 vTexCoord;\n\nvoid main() {\n  vTexCoord = aTexCoord;\n\n  vec4 positionVec4 = vec4(aPosition, 1.0);\n  gl_Position = positionVec4;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\n\nvarying vec2 vTexCoord;\n\nuniform samplerExternalOES uTexture;\nuniform float uState;\nuniform float uHue;\n\n#define tex0 uTexture\n\n#define OFFSET 0.002\nconst vec2 OFFSET0 = vec2(0.0, OFFSET);\nconst vec2 OFFSET1 = vec2(0.0, -OFFSET);\nconst vec2 OFFSET2 = vec2(OFFSET, 0.0);\nconst vec2 OFFSET3 = vec2(-OFFSET, 0.0);\n\nfloat getAlpha(vec3 color){\n    return 3.0 - color.r - color.g - color.b;\n}\n\nvec3 rgb2hsv(vec3 c)\n{\n    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);\n    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));\n    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));\n\n    float d = q.x - min(q.w, q.y);\n    float e = 1.0e-10;\n    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);\n}\n\nvec3 hsv2rgb(vec3 c)\n{\n    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n}\n\n\nvec3 transferH(vec3 rgb,float h){\n    vec3 hsv = rgb2hsv(rgb);\n    hsv.x += h;\n    return hsv2rgb(hsv);\n}\n\nvec3 setH(vec3 rgb,float h){\n    vec3 hsv = rgb2hsv(rgb);\n    hsv.x = h;\n    return hsv2rgb(hsv);\n}\n\nvec3 toRed(vec3 rgb){\n    return setH(rgb,0.05);\n}\n\nvec3 toGreen(vec3 rgb){\n    return transferH(rgb,0.72);\n}\n\nvoid main() {\n    vec2 uv = vTexCoord;   \n    uv.y = 1. - uv.y;\n    vec3 rgb = texture2D(tex0, uv).rgb;\n    rgb = mix(rgb,setH(rgb,uHue/360.),uState);\n    gl_FragColor = vec4(rgb, 1.);\n}");
                int i2 = this.g;
                if (i2 != 0) {
                    this.k = GLES20.glGetAttribLocation(i2, "aPosition");
                    a("glGetAttribLocation aPosition");
                    if (this.k == -1) {
                        Log.e("VideoRender", "Could not get attrib location for aPosition");
                    }
                    this.l = GLES20.glGetAttribLocation(this.g, "aTexCoord");
                    a("glGetAttribLocation aTextureCoord");
                    if (this.l == -1) {
                        Log.e("VideoRender", "Could not get attrib location for aTexCoord");
                    }
                    this.i = GLES20.glGetUniformLocation(this.g, "uState");
                    this.j = GLES20.glGetUniformLocation(this.g, "uHue");
                    this.f3855b = ByteBuffer.allocateDirect(this.f3854a.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                    this.f3855b.put(this.f3854a).position(0);
                    Matrix.setIdentityM(this.f, 0);
                }
            }
        }

        private void e() {
            if (this.t == null) {
                this.s = new HandlerThread("Renderer Thread");
                this.s.start();
                this.t = new a(this, this.s.getLooper());
            }
        }

        /* access modifiers changed from: private */
        public void f() {
            if (this.p.compareAndSet(true, false)) {
                try {
                    this.o.updateTexImage();
                    this.o.getTransformMatrix(this.f);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    AnalyticsUtil.trackException(e2);
                    return;
                }
            }
            EGL10 egl10 = this.u;
            EGLDisplay eGLDisplay = this.v;
            EGLSurface eGLSurface = this.y;
            egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.w);
            GLES20.glClearColor(0.0f, -1.0f, 0.0f, 1.0f);
            GLES20.glClear(16640);
            GLES20.glUseProgram(this.g);
            a("glUseProgram");
            this.f3855b.position(0);
            GLES20.glVertexAttribPointer(this.k, 3, 5126, false, 20, this.f3855b);
            a("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(this.k);
            a("glEnableVertexAttribArray maPositionHandle");
            this.f3855b.position(3);
            GLES20.glVertexAttribPointer(this.l, 3, 5126, false, 20, this.f3855b);
            a("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(this.l);
            a("glEnableVertexAttribArray maTextureHandle");
            Matrix.setIdentityM(this.e, 0);
            GLES20.glUniform1f(this.i, this.m);
            GLES20.glUniform1f(this.j, this.n);
            GLES20.glDrawArrays(5, 0, 4);
            a("glDrawArrays");
            this.u.eglSwapBuffers(this.v, this.y);
        }

        public void a() {
            onFrameAvailable(this.o);
        }

        public void a(float f2) {
            this.n = f2;
        }

        public void a(SurfaceTexture surfaceTexture, int i2, int i3) {
            e();
            int[] iArr = new int[1];
            GLES20.glGenTextures(1, iArr, 0);
            this.h = iArr[0];
            GLES20.glBindTexture(36197, this.h);
            a("glBindTexture mTextureID");
            GLES20.glTexParameterf(36197, 10241, 9728.0f);
            GLES20.glTexParameterf(36197, 10240, 9729.0f);
            GLES20.glTexParameteri(36197, 10242, 33071);
            GLES20.glTexParameteri(36197, 10243, 33071);
            this.t.sendEmptyMessage(1);
            this.o = new SurfaceTexture(this.h);
            this.o.setOnFrameAvailableListener(this);
            this.q.setVideoSurface(new Surface(this.o));
            synchronized (this) {
                this.p.set(false);
            }
        }

        public void b() {
            c();
        }

        public void b(float f2) {
            this.m = f2;
        }

        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            if (this.t != null && !this.p.get()) {
                this.p.set(true);
                this.t.sendEmptyMessage(2);
            }
        }
    }

    public ExoTextureView(Context context) {
        super(context);
        a(context);
    }

    public ExoTextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context);
    }

    public ExoTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context);
    }

    public ExoTextureView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        a(context);
    }

    private void a(Context context) {
        this.f3853a = new a(getContext());
        setSurfaceTextureListener(this);
    }

    public void a() {
        this.f3853a.a();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.main_contentview_height);
        setMeasuredDimension(dimensionPixelSize, dimensionPixelSize);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        this.f3853a.a(surfaceTexture, i, i2);
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        this.f3853a.b();
        return true;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void setPlayer(SimpleExoPlayer simpleExoPlayer) {
        SimpleExoPlayer unused = this.f3853a.q = simpleExoPlayer;
    }

    public void setRenderHue(float f) {
        this.f3853a.a(f);
    }

    public void setRenderState(float f) {
        this.f3853a.b(f);
    }
}
