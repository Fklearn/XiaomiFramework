package com.android.server.power;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.miui.R;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.android.server.LocalServices;
import com.android.server.lights.LightsManager;
import com.android.server.lights.MiuiLightsService;
import com.android.server.slice.SliceClientPermissions;
import java.io.File;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.util.SystemAnalytics;

public final class ShutdownThreadInjector {
    private static final String CUSTOMIZED_REGION = SystemProperties.get("ro.miui.customized.region", "");
    private static final String CUST_ROOT_PATH;
    private static final String CUST_VAR = (IS_CUSTOMIZED_REGION ? CUSTOMIZED_REGION : Build.getCustVariant());
    private static final boolean IS_CUSTOMIZATION;
    private static final boolean IS_CUSTOMIZATION_TEST = (Build.IS_CM_CUSTOMIZATION_TEST || Build.IS_CU_CUSTOMIZATION_TEST || Build.IS_CT_CUSTOMIZATION_TEST);
    private static final boolean IS_CUSTOMIZED_REGION = (!TextUtils.isEmpty(CUSTOMIZED_REGION));
    private static final String OPERATOR_ANIMATION_DISABLE_FLAG = (ThemeResources.THEME_MAGIC_PATH + "disable_operator_animation");
    private static final String OPERATOR_MUSIC_DISABLE_FLAG = (ThemeResources.THEME_MAGIC_PATH + "disable_operator_audio");
    private static final String OPERATOR_SHUTDOWN_ANIMATION_FILE = (CUST_ROOT_PATH + "theme/operator/boots/shutdownanimation.zip");
    private static final String OPERATOR_SHUTDOWN_MUSIC_FILE = (CUST_ROOT_PATH + "theme/operator/boots/shutdownaudio.mp3");
    private static final String SHUTDOWN_ACTION_PROPERTY_MIUI = "sys.shutdown.miui";
    private static final String SHUTDOWN_ACTION_PROPERTY_MIUI_MUSIC = "sys.shutdown.miuimusic";
    private static final String TAG = "ShutdownThreadInjector";
    /* access modifiers changed from: private */
    public static boolean sIsShutdownMusicPlaying;

    static {
        String str;
        boolean z = false;
        if (Build.IS_CM_CUSTOMIZATION || Build.IS_CU_CUSTOMIZATION || Build.IS_CT_CUSTOMIZATION) {
            z = true;
        }
        IS_CUSTOMIZATION = z;
        if (Build.HAS_CUST_PARTITION) {
            str = "/cust/cust/" + CUST_VAR + SliceClientPermissions.SliceAuthority.DELIMITER;
        } else {
            str = "/data/miui/cust/" + CUST_VAR + SliceClientPermissions.SliceAuthority.DELIMITER;
        }
        CUST_ROOT_PATH = str;
    }

    static boolean needVibrator() {
        return false;
    }

    static void showShutdownAnimOrDialog(Context context, boolean isReboot) {
        if (isCustomizedShutdownAnim(context, isReboot)) {
            SystemProperties.set("service.bootanim.exit", "0");
            SystemProperties.set("ctl.start", "bootanim");
            showShutdownAnimation(context, isReboot);
            return;
        }
        showShutdownDialog(context, isReboot);
    }

    static boolean isCustomizedShutdownAnim(Context context, boolean isReboot) {
        if ((IS_CUSTOMIZATION || IS_CUSTOMIZATION_TEST || IS_CUSTOMIZED_REGION) && checkAnimationFileExist(context, isReboot)) {
            return true;
        }
        return false;
    }

    static void showShutdownDialog(Context context, boolean isReboot) {
        Dialog bootMsgDialog = new Dialog(context, 16973933);
        View view = LayoutInflater.from(bootMsgDialog.getContext()).inflate(R.layout.reboot_view, (ViewGroup) null);
        bootMsgDialog.setContentView(view);
        bootMsgDialog.setCancelable(false);
        WindowManager.LayoutParams lp = bootMsgDialog.getWindow().getAttributes();
        WindowAdapter.setUseNotchRegion(lp);
        bootMsgDialog.getWindow().setAttributes(lp);
        bootMsgDialog.getWindow().setType(2021);
        bootMsgDialog.getWindow().clearFlags(65536);
        bootMsgDialog.show();
        if (isReboot) {
            ImageView shutdownImage = (ImageView) view.findViewById(R.id.shutdown_progress_animation);
            if (shutdownImage != null) {
                shutdownImage.setVisibility(0);
                AnimatedRotateDrawable animationDrawable = shutdownImage.getDrawable();
                animationDrawable.setFramesCount(context.getResources().getInteger(R.integer.shutdown_progress_frames_count));
                animationDrawable.setFramesDuration(context.getResources().getInteger(R.integer.shutdown_progress_frames_duration));
                animationDrawable.start();
            }
        } else {
            ((MiuiLightsService.LightImpl) ((LightsManager) LocalServices.getService(LightsManager.class)).getLight(0)).setBrightness(0, true);
        }
        SystemProperties.set("sys.in_shutdown_progress", SplitScreenReporter.ACTION_ENTER_SPLIT);
    }

    static boolean checkAnimationFileExist(Context context, boolean isReboot) {
        return !new File(OPERATOR_ANIMATION_DISABLE_FLAG).exists() && new File(OPERATOR_SHUTDOWN_ANIMATION_FILE).exists();
    }

    static void showShutdownAnimation(Context context, boolean isReboot) {
        playShutdownMusic(context, isReboot);
    }

    static String getShutdownMusicFilePath(Context context, boolean isReboot) {
        return null;
    }

    private static String getShutdownMusicFilePathInner(Context context, boolean isReboot) {
        if (!new File(OPERATOR_MUSIC_DISABLE_FLAG).exists() && new File(OPERATOR_SHUTDOWN_MUSIC_FILE).exists()) {
            return OPERATOR_SHUTDOWN_MUSIC_FILE;
        }
        return null;
    }

    static void playShutdownMusic(Context context, boolean isReboot) {
        SystemProperties.set(SHUTDOWN_ACTION_PROPERTY_MIUI, "shutdown");
        String shutdownMusicPath = getShutdownMusicFilePathInner(context, isReboot);
        Log.d(TAG, "shutdown music: " + shutdownMusicPath + " " + isSilentMode(context));
        if (!isSilentMode(context) && shutdownMusicPath != null) {
            SystemProperties.set(SHUTDOWN_ACTION_PROPERTY_MIUI_MUSIC, "shutdown_music");
        }
    }

    private static void playShutdownMusicImpl(String shutdownMusicPath) {
        final Object actionDoneSync = new Object();
        sIsShutdownMusicPlaying = true;
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(shutdownMusicPath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    synchronized (actionDoneSync) {
                        boolean unused = ShutdownThreadInjector.sIsShutdownMusicPlaying = false;
                        actionDoneSync.notifyAll();
                    }
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            sIsShutdownMusicPlaying = false;
            Log.d(TAG, "play shutdown music error:" + e);
        }
        long endTimeForMusic = SystemClock.elapsedRealtime() + 5000;
        synchronized (actionDoneSync) {
            while (true) {
                if (sIsShutdownMusicPlaying) {
                    long delay = endTimeForMusic - SystemClock.elapsedRealtime();
                    if (delay <= 0) {
                        Log.d(TAG, "play shutdown music timeout");
                        break;
                    }
                    try {
                        actionDoneSync.wait(delay);
                    } catch (InterruptedException e2) {
                    }
                }
            }
            if (!sIsShutdownMusicPlaying) {
                Log.d(TAG, "play shutdown music complete");
            }
        }
    }

    private static boolean isSilentMode(Context context) {
        return ((AudioManager) context.getSystemService("audio")).isSilentMode();
    }

    static void recordShutdownTime(Context context, boolean reboot) {
        SystemAnalytics.Action action = new SystemAnalytics.Action();
        action.addParam(SplitScreenReporter.STR_ACTION, reboot ? "reboot" : "shutdown");
        action.addParam(SplitScreenReporter.STR_DEAL_TIME, System.currentTimeMillis());
        SystemAnalytics.trackSystem(context, "systemserver_bootshuttime", action);
    }
}
