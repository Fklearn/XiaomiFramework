package com.miui.maml;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.maml.NotifierManager;
import com.miui.maml.SoundManager;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.SensorBinder;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.data.VariableNames;
import com.miui.maml.data.VariableType;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.AnimatedScreenElement;
import com.miui.maml.elements.ElementGroup;
import com.miui.maml.elements.FunctionElement;
import com.miui.maml.elements.GraphicsElement;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.IntentInfo;
import com.miui.maml.util.MobileDataUtils;
import com.miui.maml.util.ReflectionHelper;
import com.miui.maml.util.Task;
import com.miui.maml.util.Utils;
import com.miui.maml.util.Variable;
import com.xiaomi.stat.MiStat;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class ActionCommand {
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    private static final String COMMAND_BLUETOOTH = "Bluetooth";
    private static final String COMMAND_DATA = "Data";
    private static final String COMMAND_RING_MODE = "RingMode";
    private static final String COMMAND_USB_STORAGE = "UsbStorage";
    private static final String COMMAND_WIFI = "Wifi";
    private static final String LOG_TAG = "ActionCommand";
    private static final int STATE_DISABLED = 0;
    private static final int STATE_ENABLED = 1;
    private static final int STATE_INTERMEDIATE = 5;
    private static final int STATE_TURNING_OFF = 3;
    private static final int STATE_TURNING_ON = 2;
    private static final int STATE_UNKNOWN = 4;
    public static final String TAG_NAME = "Command";
    public static final String USB_CONNECTED = "connected";
    /* access modifiers changed from: private */
    public static final Handler mHandler = new Handler(Looper.getMainLooper());
    protected ScreenElement mScreenElement;

    /* renamed from: com.miui.maml.ActionCommand$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType = new int[AnimationCommand.CommandType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type = new int[AnimationProperty.Type.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type = new int[FolmeCommand.Type.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType = new int[GraphicsCommand.CommandType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType = new int[IntentCommand.IntentType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType = new int[SensorBinderCommand.CommandType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType = new int[TargetCommand.TargetType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType = new int[TickListenerCommand.CommandType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$VariableBinderCommand$Command = new int[VariableBinderCommand.Command.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$SoundManager$Command = new int[SoundManager.Command.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$VariableType = new int[VariableType.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(100:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|17|18|19|20|21|22|(2:23|24)|25|27|28|29|30|31|33|34|35|36|37|38|(2:39|40)|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|(2:61|62)|63|65|66|67|68|69|71|72|73|74|75|76|77|78|79|80|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|(2:99|100)|101|103|104|105|106|107|108|(2:109|110)|111|(2:113|114)|115|117|118|119|120|121|122|(3:123|124|126)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(102:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|13|14|15|17|18|19|20|21|22|(2:23|24)|25|27|28|29|30|31|33|34|35|36|37|38|39|40|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|(2:61|62)|63|65|66|67|68|69|71|72|73|74|75|76|77|78|79|80|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|(2:99|100)|101|103|104|105|106|107|108|(2:109|110)|111|(2:113|114)|115|117|118|119|120|121|122|(3:123|124|126)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(103:0|(2:1|2)|3|(2:5|6)|7|9|10|11|13|14|15|17|18|19|20|21|22|(2:23|24)|25|27|28|29|30|31|33|34|35|36|37|38|39|40|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|(2:61|62)|63|65|66|67|68|69|71|72|73|74|75|76|77|78|79|80|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|(2:99|100)|101|103|104|105|106|107|108|(2:109|110)|111|(2:113|114)|115|117|118|119|120|121|122|(3:123|124|126)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(107:0|(2:1|2)|3|(2:5|6)|7|9|10|11|13|14|15|17|18|19|20|21|22|23|24|25|27|28|29|30|31|33|34|35|36|37|38|39|40|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|65|66|67|68|69|71|72|73|74|75|76|77|78|79|80|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|99|100|101|103|104|105|106|107|108|(2:109|110)|111|113|114|115|117|118|119|120|121|122|(3:123|124|126)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(108:0|(2:1|2)|3|5|6|7|9|10|11|13|14|15|17|18|19|20|21|22|23|24|25|27|28|29|30|31|33|34|35|36|37|38|39|40|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|65|66|67|68|69|71|72|73|74|75|76|77|78|79|80|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|99|100|101|103|104|105|106|107|108|(2:109|110)|111|113|114|115|117|118|119|120|121|122|(3:123|124|126)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(112:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|20|21|22|23|24|25|27|28|29|30|31|33|34|35|36|37|38|39|40|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|65|66|67|68|69|71|72|73|74|75|76|77|78|79|80|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|99|100|101|103|104|105|106|107|108|109|110|111|113|114|115|117|118|119|120|121|122|123|124|126) */
        /* JADX WARNING: Can't wrap try/catch for region: R(97:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|17|18|19|20|21|22|(2:23|24)|25|27|28|(2:29|30)|31|33|34|35|36|37|38|(2:39|40)|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|(2:61|62)|63|65|66|(2:67|68)|69|71|72|73|74|75|76|77|78|(2:79|80)|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|(2:99|100)|101|103|104|105|106|107|108|(2:109|110)|111|(2:113|114)|115|117|118|119|120|121|122|(3:123|124|126)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(98:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|17|18|19|20|21|22|(2:23|24)|25|27|28|(2:29|30)|31|33|34|35|36|37|38|(2:39|40)|41|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|(2:61|62)|63|65|66|67|68|69|71|72|73|74|75|76|77|78|(2:79|80)|81|83|84|85|86|87|88|89|90|91|93|94|95|96|97|98|(2:99|100)|101|103|104|105|106|107|108|(2:109|110)|111|(2:113|114)|115|117|118|119|120|121|122|(3:123|124|126)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:105:0x01f6 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:107:0x0200 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:109:0x020a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:119:0x023a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:121:0x0244 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:123:0x024e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0048 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0052 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x005c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x0079 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x0096 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x00a0 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x00aa */
        /* JADX WARNING: Missing exception handler attribute for start block: B:45:0x00bf */
        /* JADX WARNING: Missing exception handler attribute for start block: B:47:0x00ca */
        /* JADX WARNING: Missing exception handler attribute for start block: B:49:0x00d5 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:51:0x00e1 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:53:0x00ed */
        /* JADX WARNING: Missing exception handler attribute for start block: B:55:0x00f9 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:57:0x0105 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:59:0x0111 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x011d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x013c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:73:0x0159 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:75:0x0163 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x016d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:79:0x0177 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:85:0x0194 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:87:0x019e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:89:0x01a8 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:95:0x01c5 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:97:0x01cf */
        /* JADX WARNING: Missing exception handler attribute for start block: B:99:0x01d9 */
        static {
            /*
                com.miui.maml.ActionCommand$FolmeCommand$Type[] r0 = com.miui.maml.ActionCommand.FolmeCommand.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type = r0
                r0 = 1
                int[] r1 = $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.ActionCommand$FolmeCommand$Type r2 = com.miui.maml.ActionCommand.FolmeCommand.Type.TO     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                r1 = 2
                int[] r2 = $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.ActionCommand$FolmeCommand$Type r3 = com.miui.maml.ActionCommand.FolmeCommand.Type.SET_TO     // Catch:{ NoSuchFieldError -> 0x001f }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                r2 = 3
                int[] r3 = $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.ActionCommand$FolmeCommand$Type r4 = com.miui.maml.ActionCommand.FolmeCommand.Type.FROM_TO     // Catch:{ NoSuchFieldError -> 0x002a }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                r3 = 4
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.ActionCommand$FolmeCommand$Type r5 = com.miui.maml.ActionCommand.FolmeCommand.Type.CANCEL     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                com.miui.maml.ActionCommand$AnimationCommand$CommandType[] r4 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType = r4
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0048 }
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.PLAY     // Catch:{ NoSuchFieldError -> 0x0048 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0048 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0048 }
            L_0x0048:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0052 }
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.PAUSE     // Catch:{ NoSuchFieldError -> 0x0052 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0052 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0052 }
            L_0x0052:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x005c }
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.RESUME     // Catch:{ NoSuchFieldError -> 0x005c }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x005c }
                r4[r5] = r2     // Catch:{ NoSuchFieldError -> 0x005c }
            L_0x005c:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0066 }
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.PLAY_WITH_PARAMS     // Catch:{ NoSuchFieldError -> 0x0066 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0066 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0066 }
            L_0x0066:
                com.miui.maml.ActionCommand$TickListenerCommand$CommandType[] r4 = com.miui.maml.ActionCommand.TickListenerCommand.CommandType.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType = r4
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0079 }
                com.miui.maml.ActionCommand$TickListenerCommand$CommandType r5 = com.miui.maml.ActionCommand.TickListenerCommand.CommandType.SET     // Catch:{ NoSuchFieldError -> 0x0079 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0079 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0079 }
            L_0x0079:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0083 }
                com.miui.maml.ActionCommand$TickListenerCommand$CommandType r5 = com.miui.maml.ActionCommand.TickListenerCommand.CommandType.UNSET     // Catch:{ NoSuchFieldError -> 0x0083 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0083 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0083 }
            L_0x0083:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType[] r4 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType = r4
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0096 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r5 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.LINE_TO     // Catch:{ NoSuchFieldError -> 0x0096 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0096 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0096 }
            L_0x0096:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00a0 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r5 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.MOVE_TO     // Catch:{ NoSuchFieldError -> 0x00a0 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x00a0 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x00a0 }
            L_0x00a0:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00aa }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r5 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.CURVE_TO     // Catch:{ NoSuchFieldError -> 0x00aa }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x00aa }
                r4[r5] = r2     // Catch:{ NoSuchFieldError -> 0x00aa }
            L_0x00aa:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00b4 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r5 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_RECT     // Catch:{ NoSuchFieldError -> 0x00b4 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x00b4 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x00b4 }
            L_0x00b4:
                r4 = 5
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00bf }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.BEGIN_FILL     // Catch:{ NoSuchFieldError -> 0x00bf }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00bf }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x00bf }
            L_0x00bf:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00ca }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.LINE_STYLE     // Catch:{ NoSuchFieldError -> 0x00ca }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00ca }
                r7 = 6
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x00ca }
            L_0x00ca:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00d5 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_CIRCLE     // Catch:{ NoSuchFieldError -> 0x00d5 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00d5 }
                r7 = 7
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x00d5 }
            L_0x00d5:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00e1 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_ELLIPSE     // Catch:{ NoSuchFieldError -> 0x00e1 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00e1 }
                r7 = 8
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x00e1 }
            L_0x00e1:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00ed }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.CUBIC_CURVE_TO     // Catch:{ NoSuchFieldError -> 0x00ed }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00ed }
                r7 = 9
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x00ed }
            L_0x00ed:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x00f9 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_ROUND_RECT     // Catch:{ NoSuchFieldError -> 0x00f9 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00f9 }
                r7 = 10
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x00f9 }
            L_0x00f9:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0105 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.LINE_GRADIENT_STYLE     // Catch:{ NoSuchFieldError -> 0x0105 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0105 }
                r7 = 11
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0105 }
            L_0x0105:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0111 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.BEGIN_GRADIENT_FILL     // Catch:{ NoSuchFieldError -> 0x0111 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0111 }
                r7 = 12
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0111 }
            L_0x0111:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x011d }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.CREATE_GRADIENT_BOX     // Catch:{ NoSuchFieldError -> 0x011d }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x011d }
                r7 = 13
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x011d }
            L_0x011d:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0129 }
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r6 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.SET_RENDER_LISTENER     // Catch:{ NoSuchFieldError -> 0x0129 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0129 }
                r7 = 14
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0129 }
            L_0x0129:
                com.miui.maml.ActionCommand$SensorBinderCommand$CommandType[] r5 = com.miui.maml.ActionCommand.SensorBinderCommand.CommandType.values()
                int r5 = r5.length
                int[] r5 = new int[r5]
                $SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType = r5
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x013c }
                com.miui.maml.ActionCommand$SensorBinderCommand$CommandType r6 = com.miui.maml.ActionCommand.SensorBinderCommand.CommandType.TURN_ON     // Catch:{ NoSuchFieldError -> 0x013c }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x013c }
                r5[r6] = r0     // Catch:{ NoSuchFieldError -> 0x013c }
            L_0x013c:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType     // Catch:{ NoSuchFieldError -> 0x0146 }
                com.miui.maml.ActionCommand$SensorBinderCommand$CommandType r6 = com.miui.maml.ActionCommand.SensorBinderCommand.CommandType.TURN_OFF     // Catch:{ NoSuchFieldError -> 0x0146 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0146 }
                r5[r6] = r1     // Catch:{ NoSuchFieldError -> 0x0146 }
            L_0x0146:
                com.miui.maml.ActionCommand$TargetCommand$TargetType[] r5 = com.miui.maml.ActionCommand.TargetCommand.TargetType.values()
                int r5 = r5.length
                int[] r5 = new int[r5]
                $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType = r5
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType     // Catch:{ NoSuchFieldError -> 0x0159 }
                com.miui.maml.ActionCommand$TargetCommand$TargetType r6 = com.miui.maml.ActionCommand.TargetCommand.TargetType.SCREEN_ELEMENT     // Catch:{ NoSuchFieldError -> 0x0159 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0159 }
                r5[r6] = r0     // Catch:{ NoSuchFieldError -> 0x0159 }
            L_0x0159:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType     // Catch:{ NoSuchFieldError -> 0x0163 }
                com.miui.maml.ActionCommand$TargetCommand$TargetType r6 = com.miui.maml.ActionCommand.TargetCommand.TargetType.VARIABLE     // Catch:{ NoSuchFieldError -> 0x0163 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0163 }
                r5[r6] = r1     // Catch:{ NoSuchFieldError -> 0x0163 }
            L_0x0163:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType     // Catch:{ NoSuchFieldError -> 0x016d }
                com.miui.maml.ActionCommand$TargetCommand$TargetType r6 = com.miui.maml.ActionCommand.TargetCommand.TargetType.ANIMATION_ITEM     // Catch:{ NoSuchFieldError -> 0x016d }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x016d }
                r5[r6] = r2     // Catch:{ NoSuchFieldError -> 0x016d }
            L_0x016d:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType     // Catch:{ NoSuchFieldError -> 0x0177 }
                com.miui.maml.ActionCommand$TargetCommand$TargetType r6 = com.miui.maml.ActionCommand.TargetCommand.TargetType.VARIABLE_BINDER     // Catch:{ NoSuchFieldError -> 0x0177 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0177 }
                r5[r6] = r3     // Catch:{ NoSuchFieldError -> 0x0177 }
            L_0x0177:
                int[] r5 = $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType     // Catch:{ NoSuchFieldError -> 0x0181 }
                com.miui.maml.ActionCommand$TargetCommand$TargetType r6 = com.miui.maml.ActionCommand.TargetCommand.TargetType.CONSTRUCTOR     // Catch:{ NoSuchFieldError -> 0x0181 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0181 }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x0181 }
            L_0x0181:
                com.miui.maml.ActionCommand$AnimationProperty$Type[] r4 = com.miui.maml.ActionCommand.AnimationProperty.Type.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type = r4
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type     // Catch:{ NoSuchFieldError -> 0x0194 }
                com.miui.maml.ActionCommand$AnimationProperty$Type r5 = com.miui.maml.ActionCommand.AnimationProperty.Type.PLAY     // Catch:{ NoSuchFieldError -> 0x0194 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0194 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0194 }
            L_0x0194:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type     // Catch:{ NoSuchFieldError -> 0x019e }
                com.miui.maml.ActionCommand$AnimationProperty$Type r5 = com.miui.maml.ActionCommand.AnimationProperty.Type.PAUSE     // Catch:{ NoSuchFieldError -> 0x019e }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x019e }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x019e }
            L_0x019e:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type     // Catch:{ NoSuchFieldError -> 0x01a8 }
                com.miui.maml.ActionCommand$AnimationProperty$Type r5 = com.miui.maml.ActionCommand.AnimationProperty.Type.RESUME     // Catch:{ NoSuchFieldError -> 0x01a8 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x01a8 }
                r4[r5] = r2     // Catch:{ NoSuchFieldError -> 0x01a8 }
            L_0x01a8:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type     // Catch:{ NoSuchFieldError -> 0x01b2 }
                com.miui.maml.ActionCommand$AnimationProperty$Type r5 = com.miui.maml.ActionCommand.AnimationProperty.Type.PLAY_WITH_PARAMS     // Catch:{ NoSuchFieldError -> 0x01b2 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x01b2 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x01b2 }
            L_0x01b2:
                com.miui.maml.SoundManager$Command[] r4 = com.miui.maml.SoundManager.Command.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$SoundManager$Command = r4
                int[] r4 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x01c5 }
                com.miui.maml.SoundManager$Command r5 = com.miui.maml.SoundManager.Command.Play     // Catch:{ NoSuchFieldError -> 0x01c5 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x01c5 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x01c5 }
            L_0x01c5:
                int[] r4 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x01cf }
                com.miui.maml.SoundManager$Command r5 = com.miui.maml.SoundManager.Command.Pause     // Catch:{ NoSuchFieldError -> 0x01cf }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x01cf }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x01cf }
            L_0x01cf:
                int[] r4 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x01d9 }
                com.miui.maml.SoundManager$Command r5 = com.miui.maml.SoundManager.Command.Resume     // Catch:{ NoSuchFieldError -> 0x01d9 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x01d9 }
                r4[r5] = r2     // Catch:{ NoSuchFieldError -> 0x01d9 }
            L_0x01d9:
                int[] r4 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x01e3 }
                com.miui.maml.SoundManager$Command r5 = com.miui.maml.SoundManager.Command.Stop     // Catch:{ NoSuchFieldError -> 0x01e3 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x01e3 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x01e3 }
            L_0x01e3:
                com.miui.maml.ActionCommand$IntentCommand$IntentType[] r4 = com.miui.maml.ActionCommand.IntentCommand.IntentType.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType = r4
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType     // Catch:{ NoSuchFieldError -> 0x01f6 }
                com.miui.maml.ActionCommand$IntentCommand$IntentType r5 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Activity     // Catch:{ NoSuchFieldError -> 0x01f6 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x01f6 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x01f6 }
            L_0x01f6:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType     // Catch:{ NoSuchFieldError -> 0x0200 }
                com.miui.maml.ActionCommand$IntentCommand$IntentType r5 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Broadcast     // Catch:{ NoSuchFieldError -> 0x0200 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0200 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0200 }
            L_0x0200:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType     // Catch:{ NoSuchFieldError -> 0x020a }
                com.miui.maml.ActionCommand$IntentCommand$IntentType r5 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Service     // Catch:{ NoSuchFieldError -> 0x020a }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x020a }
                r4[r5] = r2     // Catch:{ NoSuchFieldError -> 0x020a }
            L_0x020a:
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType     // Catch:{ NoSuchFieldError -> 0x0214 }
                com.miui.maml.ActionCommand$IntentCommand$IntentType r5 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Var     // Catch:{ NoSuchFieldError -> 0x0214 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0214 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0214 }
            L_0x0214:
                com.miui.maml.ActionCommand$VariableBinderCommand$Command[] r4 = com.miui.maml.ActionCommand.VariableBinderCommand.Command.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$ActionCommand$VariableBinderCommand$Command = r4
                int[] r4 = $SwitchMap$com$miui$maml$ActionCommand$VariableBinderCommand$Command     // Catch:{ NoSuchFieldError -> 0x0227 }
                com.miui.maml.ActionCommand$VariableBinderCommand$Command r5 = com.miui.maml.ActionCommand.VariableBinderCommand.Command.Refresh     // Catch:{ NoSuchFieldError -> 0x0227 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0227 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0227 }
            L_0x0227:
                com.miui.maml.data.VariableType[] r4 = com.miui.maml.data.VariableType.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$miui$maml$data$VariableType = r4
                int[] r4 = $SwitchMap$com$miui$maml$data$VariableType     // Catch:{ NoSuchFieldError -> 0x023a }
                com.miui.maml.data.VariableType r5 = com.miui.maml.data.VariableType.NUM     // Catch:{ NoSuchFieldError -> 0x023a }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x023a }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x023a }
            L_0x023a:
                int[] r0 = $SwitchMap$com$miui$maml$data$VariableType     // Catch:{ NoSuchFieldError -> 0x0244 }
                com.miui.maml.data.VariableType r4 = com.miui.maml.data.VariableType.NUM_ARR     // Catch:{ NoSuchFieldError -> 0x0244 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0244 }
                r0[r4] = r1     // Catch:{ NoSuchFieldError -> 0x0244 }
            L_0x0244:
                int[] r0 = $SwitchMap$com$miui$maml$data$VariableType     // Catch:{ NoSuchFieldError -> 0x024e }
                com.miui.maml.data.VariableType r1 = com.miui.maml.data.VariableType.STR     // Catch:{ NoSuchFieldError -> 0x024e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x024e }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x024e }
            L_0x024e:
                int[] r0 = $SwitchMap$com$miui$maml$data$VariableType     // Catch:{ NoSuchFieldError -> 0x0258 }
                com.miui.maml.data.VariableType r1 = com.miui.maml.data.VariableType.STR_ARR     // Catch:{ NoSuchFieldError -> 0x0258 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0258 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x0258 }
            L_0x0258:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.AnonymousClass1.<clinit>():void");
        }
    }

    private static class ActionPerformCommand extends TargetCommand {
        public static final String TAG_NAME = "ActionCommand";
        private String mAction;
        private Expression mActionExp;

        public ActionPerformCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mAction = element.getAttribute("action");
            if (TextUtils.isEmpty(this.mAction)) {
                this.mAction = null;
                this.mActionExp = Expression.build(getVariables(), element.getAttribute("actionExp"));
            }
        }

        public void doPerform() {
            Expression expression;
            ScreenElement screenElement = (ScreenElement) getTarget();
            if (screenElement != null) {
                String str = this.mAction;
                if (str != null || ((expression = this.mActionExp) != null && (str = expression.evaluateStr()) != null)) {
                    screenElement.performAction(str);
                }
            }
        }
    }

    private static class AnimationCommand extends TargetCommand {
        public static final String TAG_NAME = "AnimationCommand";
        private boolean mAllAni;
        private String[] mAniTags;
        private CommandType mCommand;
        private Expression[] mPlayParams;

        private enum CommandType {
            INVALID,
            PLAY,
            PAUSE,
            RESUME,
            PLAY_WITH_PARAMS
        }

        /* JADX WARNING: Removed duplicated region for block: B:24:0x007e  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0081  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public AnimationCommand(com.miui.maml.elements.ScreenElement r5, org.w3c.dom.Element r6) {
            /*
                r4 = this;
                r4.<init>(r5, r6)
                java.lang.String r5 = "command"
                java.lang.String r5 = r6.getAttribute(r5)
                java.lang.String r0 = "play"
                boolean r0 = r5.equalsIgnoreCase(r0)
                r1 = 1
                if (r0 == 0) goto L_0x0017
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.PLAY
            L_0x0014:
                r4.mCommand = r5
                goto L_0x0070
            L_0x0017:
                java.lang.String r0 = "pause"
                boolean r0 = r5.equalsIgnoreCase(r0)
                if (r0 == 0) goto L_0x0022
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.PAUSE
                goto L_0x0014
            L_0x0022:
                java.lang.String r0 = "resume"
                boolean r0 = r5.equalsIgnoreCase(r0)
                if (r0 == 0) goto L_0x002d
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.RESUME
                goto L_0x0014
            L_0x002d:
                java.lang.String r0 = r5.toLowerCase()
                java.lang.String r2 = "play("
                boolean r0 = r0.startsWith(r2)
                if (r0 == 0) goto L_0x006d
                java.lang.String r0 = ")"
                boolean r0 = r5.endsWith(r0)
                if (r0 == 0) goto L_0x006d
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r0 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.PLAY_WITH_PARAMS
                r4.mCommand = r0
                com.miui.maml.data.Variables r0 = r4.getVariables()
                r2 = 5
                int r3 = r5.length()
                int r3 = r3 - r1
                java.lang.String r5 = r5.substring(r2, r3)
                com.miui.maml.data.Expression[] r5 = com.miui.maml.data.Expression.buildMultiple(r0, r5)
                r4.mPlayParams = r5
                com.miui.maml.data.Expression[] r5 = r4.mPlayParams
                if (r5 == 0) goto L_0x0070
                int r0 = r5.length
                r2 = 2
                if (r0 == r2) goto L_0x0070
                int r5 = r5.length
                r0 = 4
                if (r5 == r0) goto L_0x0070
                java.lang.String r5 = "ActionCommand"
                java.lang.String r0 = "bad expression format"
                android.util.Log.e(r5, r0)
                goto L_0x0070
            L_0x006d:
                com.miui.maml.ActionCommand$AnimationCommand$CommandType r5 = com.miui.maml.ActionCommand.AnimationCommand.CommandType.INVALID
                goto L_0x0014
            L_0x0070:
                java.lang.String r5 = "tags"
                java.lang.String r5 = r6.getAttribute(r5)
                java.lang.String r6 = "."
                boolean r6 = r6.equals(r5)
                if (r6 == 0) goto L_0x0081
                r4.mAllAni = r1
                goto L_0x008f
            L_0x0081:
                boolean r6 = android.text.TextUtils.isEmpty(r5)
                if (r6 != 0) goto L_0x008f
                java.lang.String r6 = ","
                java.lang.String[] r5 = r5.split(r6)
                r4.mAniTags = r5
            L_0x008f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.AnimationCommand.<init>(com.miui.maml.elements.ScreenElement, org.w3c.dom.Element):void");
        }

        public void doPerform() {
            ScreenElement screenElement = (ScreenElement) getTarget();
            if (screenElement != null) {
                CommandType commandType = this.mCommand;
                if ((commandType == CommandType.PLAY || commandType == CommandType.PLAY_WITH_PARAMS) && (this.mAllAni || this.mAniTags != null)) {
                    screenElement.setAnim(this.mAniTags);
                }
                int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType[this.mCommand.ordinal()];
                if (i == 1) {
                    screenElement.playAnim();
                } else if (i == 2) {
                    screenElement.pauseAnim();
                } else if (i == 3) {
                    screenElement.resumeAnim();
                } else if (i == 4) {
                    long j = 0;
                    long j2 = -1;
                    Expression[] expressionArr = this.mPlayParams;
                    boolean z = false;
                    if (expressionArr.length > 0) {
                        j = (long) (expressionArr[0] == null ? 0.0d : expressionArr[0].evaluate());
                    }
                    Expression[] expressionArr2 = this.mPlayParams;
                    if (expressionArr2.length > 1) {
                        j2 = (long) (expressionArr2[1] == null ? -1.0d : expressionArr2[1].evaluate());
                    }
                    Expression[] expressionArr3 = this.mPlayParams;
                    boolean z2 = expressionArr3.length > 2 && expressionArr3[2] != null && expressionArr3[2].evaluate() > 0.0d;
                    Expression[] expressionArr4 = this.mPlayParams;
                    if (expressionArr4.length > 3 && expressionArr4[3] != null && expressionArr4[3].evaluate() > 0.0d) {
                        z = true;
                    }
                    screenElement.playAnim(j, j2, z2, z);
                }
            }
        }
    }

    @Deprecated
    private static class AnimationProperty extends PropertyCommand {
        public static final String PROPERTY_NAME = "animation";
        private Expression[] mPlayParams;
        private Type mType;

        enum Type {
            INVALID,
            PLAY,
            PAUSE,
            RESUME,
            PLAY_WITH_PARAMS
        }

        protected AnimationProperty(ScreenElement screenElement, Variable variable, String str) {
            super(screenElement, variable, str);
            Type type;
            if (str.equalsIgnoreCase("play")) {
                type = Type.PLAY;
            } else if (str.equalsIgnoreCase("pause")) {
                type = Type.PAUSE;
            } else if (str.equalsIgnoreCase("resume")) {
                type = Type.RESUME;
            } else if (!str.toLowerCase().startsWith("play(") || !str.endsWith(")")) {
                type = Type.INVALID;
            } else {
                this.mType = Type.PLAY_WITH_PARAMS;
                this.mPlayParams = Expression.buildMultiple(getVariables(), str.substring(5, str.length() - 1));
                Expression[] expressionArr = this.mPlayParams;
                if (expressionArr != null && expressionArr.length != 2 && expressionArr.length != 4) {
                    Log.e("ActionCommand", "bad expression format");
                    return;
                }
                return;
            }
            this.mType = type;
        }

        public void doPerform() {
            boolean z;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type[this.mType.ordinal()];
            if (i == 1) {
                this.mTargetElement.playAnim();
            } else if (i == 2) {
                this.mTargetElement.pauseAnim();
            } else if (i == 3) {
                this.mTargetElement.resumeAnim();
            } else if (i == 4) {
                long j = 0;
                long j2 = -1;
                Expression[] expressionArr = this.mPlayParams;
                boolean z2 = false;
                if (expressionArr.length > 0) {
                    j = (long) (expressionArr[0] == null ? 0.0d : expressionArr[0].evaluate());
                }
                long j3 = j;
                Expression[] expressionArr2 = this.mPlayParams;
                if (expressionArr2.length > 1) {
                    j2 = (long) (expressionArr2[1] == null ? -1.0d : expressionArr2[1].evaluate());
                }
                long j4 = j2;
                Expression[] expressionArr3 = this.mPlayParams;
                if (expressionArr3.length > 2) {
                    z = expressionArr3[2] != null && expressionArr3[2].evaluate() > 0.0d;
                } else {
                    z = false;
                }
                Expression[] expressionArr4 = this.mPlayParams;
                if (expressionArr4.length > 3 && expressionArr4[3] != null && expressionArr4[3].evaluate() > 0.0d) {
                    z2 = true;
                }
                this.mTargetElement.playAnim(j3, j4, z, z2);
            }
        }
    }

    private static abstract class BaseMethodCommand extends TargetCommand {
        protected static final int ERROR_EXCEPTION = -2;
        protected static final int ERROR_NO_METHOD = -1;
        protected static final int ERROR_SUCCESS = 1;
        protected IndexedVariable mErrorCodeVar;
        private ObjVar[] mParamObjVars;
        protected Class<?>[] mParamTypes;
        protected Object[] mParamValues;
        private Expression[] mParams;
        protected IndexedVariable mReturnVar;
        protected Class<?> mTargetClass;
        protected String mTargetClassName;

        public BaseMethodCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mTargetClassName = element.getAttribute("class");
            if (TextUtils.isEmpty(this.mTargetClassName)) {
                this.mTargetClassName = null;
            }
            this.mParams = Expression.buildMultiple(getVariables(), element.getAttribute("params"));
            String attribute = element.getAttribute("paramTypes");
            if (this.mParams != null && !TextUtils.isEmpty(attribute)) {
                try {
                    this.mParamTypes = ReflectionHelper.strTypesToClass(TextUtils.split(attribute, ","));
                    if (this.mParams.length != this.mParamTypes.length) {
                        Log.e("ActionCommand", this.mLogStr + "different length of params and paramTypes");
                        this.mParams = null;
                        this.mParamTypes = null;
                    }
                } catch (ClassNotFoundException e) {
                    Log.e("ActionCommand", this.mLogStr + "invalid method paramTypes. " + e.toString());
                    this.mParams = null;
                    this.mParamTypes = null;
                }
            }
            String attribute2 = element.getAttribute("return");
            if (!TextUtils.isEmpty(attribute2)) {
                this.mReturnVar = new IndexedVariable(attribute2, getVariables(), VariableType.parseType(element.getAttribute("returnType")).isNumber());
            }
            String attribute3 = element.getAttribute("errorVar");
            if (!TextUtils.isEmpty(attribute3)) {
                this.mErrorCodeVar = new IndexedVariable(attribute3, getVariables(), true);
            }
            this.mLogStr += ", class=" + this.mTargetClassName + " type=" + this.mTargetType.toString();
        }

        public void finish() {
            ActionCommand.super.finish();
            this.mParamValues = null;
        }

        public void init() {
            Expression expression;
            super.init();
            Class<?>[] clsArr = this.mParamTypes;
            if (clsArr != null) {
                if (this.mParamObjVars == null) {
                    this.mParamObjVars = new ObjVar[clsArr.length];
                }
                int i = 0;
                while (true) {
                    Class<?>[] clsArr2 = this.mParamTypes;
                    if (i >= clsArr2.length) {
                        break;
                    }
                    this.mParamObjVars[i] = null;
                    Class<?> cls = clsArr2[i];
                    if (!((cls.isPrimitive() && !cls.isArray()) || cls == String.class || (expression = this.mParams[i]) == null)) {
                        String evaluateStr = expression.evaluateStr();
                        if (!TextUtils.isEmpty(evaluateStr)) {
                            this.mParamObjVars[i] = new ObjVar(evaluateStr, getVariables());
                        }
                    }
                    i++;
                }
            }
            String str = this.mTargetClassName;
            if (str != null) {
                try {
                    this.mTargetClass = Class.forName(str);
                } catch (Exception e) {
                    Log.w("ActionCommand", "target class not found, name: " + this.mTargetClassName + "\n" + e.toString());
                }
            }
        }

        /* access modifiers changed from: protected */
        public void prepareParams() {
            Expression[] expressionArr = this.mParams;
            if (expressionArr != null) {
                if (this.mParamValues == null) {
                    this.mParamValues = new Object[expressionArr.length];
                }
                int i = 0;
                while (true) {
                    Expression[] expressionArr2 = this.mParams;
                    if (i < expressionArr2.length) {
                        Object[] objArr = this.mParamValues;
                        Object obj = null;
                        objArr[i] = null;
                        Class<?> cls = this.mParamTypes[i];
                        Expression expression = expressionArr2[i];
                        if (expression != null) {
                            if (cls == String.class) {
                                objArr[i] = expression.evaluateStr();
                            } else if (cls == Integer.TYPE) {
                                objArr[i] = Integer.valueOf((int) ((long) expression.evaluate()));
                            } else if (cls == Long.TYPE) {
                                objArr[i] = Long.valueOf((long) expression.evaluate());
                            } else if (cls == Boolean.TYPE) {
                                objArr[i] = Boolean.valueOf(expression.evaluate() > 0.0d);
                            } else if (cls == Double.TYPE) {
                                objArr[i] = Double.valueOf(expression.evaluate());
                            } else if (cls == Float.TYPE) {
                                objArr[i] = Float.valueOf((float) expression.evaluate());
                            } else if (cls == Byte.TYPE) {
                                objArr[i] = Byte.valueOf((byte) ((int) ((long) expression.evaluate())));
                            } else if (cls == Short.TYPE) {
                                objArr[i] = Short.valueOf((short) ((int) ((long) expression.evaluate())));
                            } else if (cls == Character.TYPE) {
                                objArr[i] = Character.valueOf((char) ((int) ((long) expression.evaluate())));
                            } else {
                                ObjVar objVar = this.mParamObjVars[i];
                                if (objVar != null) {
                                    obj = objVar.get();
                                }
                                objArr[i] = obj;
                            }
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private static class BluetoothSwitchCommand extends NotificationReceiver {
        private BluetoothAdapter mBluetoothAdapter;
        private boolean mBluetoothEnable;
        private boolean mBluetoothEnabling;
        private OnOffCommandHelper mOnOffHelper;

        public BluetoothSwitchCommand(ScreenElement screenElement, String str) {
            super(screenElement, VariableNames.BLUETOOTH_STATE, "android.bluetooth.adapter.action.STATE_CHANGED");
            this.mOnOffHelper = new OnOffCommandHelper(str);
        }

        private boolean ensureBluetoothAdapter() {
            if (this.mBluetoothAdapter == null) {
                this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            return this.mBluetoothAdapter != null;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
            if (r0 != false) goto L_0x001b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0011, code lost:
            if (r4.mBluetoothEnable != false) goto L_0x0013;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x001b, code lost:
            r4.mBluetoothAdapter.enable();
            r4.mBluetoothEnabling = true;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void doPerform() {
            /*
                r4 = this;
                boolean r0 = r4.ensureBluetoothAdapter()
                if (r0 != 0) goto L_0x0007
                return
            L_0x0007:
                com.miui.maml.ActionCommand$OnOffCommandHelper r0 = r4.mOnOffHelper
                boolean r1 = r0.mIsToggle
                r2 = 0
                r3 = 1
                if (r1 == 0) goto L_0x0023
                boolean r0 = r4.mBluetoothEnable
                if (r0 == 0) goto L_0x001b
            L_0x0013:
                android.bluetooth.BluetoothAdapter r0 = r4.mBluetoothAdapter
                r0.disable()
                r4.mBluetoothEnabling = r2
                goto L_0x0030
            L_0x001b:
                android.bluetooth.BluetoothAdapter r0 = r4.mBluetoothAdapter
                r0.enable()
                r4.mBluetoothEnabling = r3
                goto L_0x0030
            L_0x0023:
                boolean r1 = r4.mBluetoothEnabling
                if (r1 != 0) goto L_0x0030
                boolean r1 = r4.mBluetoothEnable
                boolean r0 = r0.mIsOn
                if (r1 == r0) goto L_0x0030
                if (r0 == 0) goto L_0x0013
                goto L_0x001b
            L_0x0030:
                r4.update()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.BluetoothSwitchCommand.doPerform():void");
        }

        /* access modifiers changed from: protected */
        public void update() {
            if (ensureBluetoothAdapter()) {
                this.mBluetoothEnable = this.mBluetoothAdapter.isEnabled();
                int i = 0;
                if (this.mBluetoothEnable) {
                    this.mBluetoothEnabling = false;
                    updateState(1);
                    return;
                }
                if (this.mBluetoothEnabling) {
                    i = 2;
                }
                updateState(i);
            }
        }
    }

    private static class ConditionCommand extends ActionCommand {
        private ActionCommand mCommand;
        private Expression mCondition;

        public ConditionCommand(ActionCommand actionCommand, Expression expression) {
            super(actionCommand.getRoot());
            this.mCommand = actionCommand;
            this.mCondition = expression;
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            if (this.mCondition.evaluate() > 0.0d) {
                this.mCommand.perform();
            }
        }

        public void init() {
            this.mCommand.init();
        }
    }

    private static class DataSwitchCommand extends NotificationReceiver {
        private boolean mApnEnable;
        private MobileDataUtils mMobileDataUtils = MobileDataUtils.getInstance();
        private OnOffCommandHelper mOnOffHelper;

        public DataSwitchCommand(ScreenElement screenElement, String str) {
            super(screenElement, VariableNames.DATA_STATE, NotifierManager.TYPE_MOBILE_DATA);
            this.mOnOffHelper = new OnOffCommandHelper(str);
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            boolean z = this.mApnEnable;
            OnOffCommandHelper onOffCommandHelper = this.mOnOffHelper;
            boolean z2 = onOffCommandHelper.mIsToggle ? !z : onOffCommandHelper.mIsOn;
            if (this.mApnEnable != z2) {
                this.mMobileDataUtils.enableMobileData(this.mScreenElement.getContext().mContext, z2);
            }
        }

        /* access modifiers changed from: protected */
        public void update() {
            this.mApnEnable = this.mMobileDataUtils.isMobileEnable(this.mScreenElement.getContext().mContext);
            updateState(this.mApnEnable ? 1 : 0);
        }
    }

    private static class DelayCommand extends ActionCommand {
        private Runnable mCmd = new Runnable() {
            public void run() {
                DelayCommand.this.mCommand.perform();
            }
        };
        /* access modifiers changed from: private */
        public ActionCommand mCommand;
        private long mDelay;

        public DelayCommand(ActionCommand actionCommand, long j) {
            super(actionCommand.getRoot());
            this.mCommand = actionCommand;
            this.mDelay = j;
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            getRoot().postDelayed(this.mCmd, this.mDelay);
        }

        public void finish() {
            getRoot().removeCallbacks(this.mCmd);
        }

        public void init() {
            this.mCommand.init();
        }
    }

    private static class EaseTypeCommand extends TargetCommand {
        public static final String TAG_NAME = "EaseTypeCommand";
        private String mEaseFun;
        private String mEaseParams;
        private Expression mEaseTypeExp;

        public EaseTypeCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mTargetType = TargetCommand.TargetType.ANIMATION_ITEM;
            this.mEaseTypeExp = Expression.build(getVariables(), element.getAttribute("easeTypeExp"));
            this.mEaseFun = element.getAttribute("easeFunExp");
            this.mEaseParams = element.getAttribute("easeParamsExp");
        }

        public void doPerform() {
            ArrayList arrayList = (ArrayList) getTarget();
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ((BaseAnimation.AnimationItem) it.next()).changeInterpolator(this.mEaseTypeExp.evaluateStr(), this.mEaseParams, this.mEaseFun);
                }
            }
        }
    }

    private static class ExternCommand extends ActionCommand {
        public static final String TAG_NAME = "ExternCommand";
        private String mCommand;
        private Expression mNumParaExp;
        private Expression mStrParaExp;

        public ExternCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mCommand = element.getAttribute("command");
            this.mNumParaExp = Expression.build(getVariables(), element.getAttribute("numPara"));
            this.mStrParaExp = Expression.build(getVariables(), element.getAttribute("strPara"));
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            ScreenElementRoot root = getRoot();
            String str = this.mCommand;
            Expression expression = this.mNumParaExp;
            String str2 = null;
            Double valueOf = expression == null ? null : Double.valueOf(expression.evaluate());
            Expression expression2 = this.mStrParaExp;
            if (expression2 != null) {
                str2 = expression2.evaluateStr();
            }
            root.issueExternCommand(str, valueOf, str2);
        }
    }

    private static class FieldCommand extends BaseMethodCommand {
        public static final String TAG_NAME = "FieldCommand";
        private Field mField;
        private String mFieldName;
        private boolean mIsSet;

        public FieldCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            boolean z;
            this.mFieldName = element.getAttribute("field");
            this.mLogStr = "FieldCommand, " + this.mLogStr + ", field=" + this.mFieldName + "\n";
            String attribute = element.getAttribute(MiStat.Param.METHOD);
            if ("get".equals(attribute)) {
                z = false;
            } else if ("set".equals(attribute)) {
                z = true;
            } else {
                return;
            }
            this.mIsSet = z;
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            String str;
            if (this.mField == null) {
                loadField();
            }
            if (this.mField != null) {
                try {
                    int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
                    if (i != 1 && i != 2) {
                        return;
                    }
                    if (this.mIsSet) {
                        prepareParams();
                        if (this.mParamValues != null && this.mParamValues.length == 1) {
                            this.mField.set(getTarget(), this.mParamValues[0]);
                        }
                    } else if (this.mReturnVar != null) {
                        this.mReturnVar.set(this.mField.get(getTarget()));
                    }
                } catch (IllegalArgumentException e) {
                    str = e.toString();
                    Log.e("ActionCommand", str);
                } catch (IllegalAccessException e2) {
                    str = e2.toString();
                    Log.e("ActionCommand", str);
                } catch (NullPointerException e3) {
                    str = this.mLogStr + "Field target is null. " + e3.toString();
                    Log.e("ActionCommand", str);
                }
            }
        }

        public void init() {
            super.init();
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
            if ((i == 1 || i == 2) && this.mField != null) {
                loadField();
            }
        }

        /* access modifiers changed from: protected */
        public void loadField() {
            String str;
            Object target;
            if (this.mTargetClass == null && (target = getTarget()) != null) {
                this.mTargetClass = target.getClass();
            }
            Class<?> cls = this.mTargetClass;
            if (cls != null) {
                try {
                    this.mField = cls.getField(this.mFieldName);
                } catch (NoSuchFieldException e) {
                    str = this.mLogStr + e.toString();
                }
            } else {
                str = this.mLogStr + "class is null.";
                Log.e("ActionCommand", str);
            }
        }
    }

    private static class FolmeCommand extends TargetCommand {
        public static final String TAG_NAME = "FolmeCommand";
        private Type mCommand;
        private Expression mConfig;
        private boolean mIsParamsValid = isExpressionsValid(this.mParams);
        private boolean mIsStatesValid = isExpressionsValid(this.mStates);
        private Expression[] mParams;
        private Expression[] mStates;

        enum Type {
            TO,
            SET_TO,
            FROM_TO,
            CANCEL,
            ADD_RANGE_BOARD,
            INVALID
        }

        /* JADX WARNING: Removed duplicated region for block: B:22:0x008f  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x00a3  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public FolmeCommand(com.miui.maml.elements.ScreenElement r5, org.w3c.dom.Element r6) {
            /*
                r4 = this;
                r4.<init>(r5, r6)
                com.miui.maml.data.Variables r5 = r4.getVariables()
                java.lang.String r0 = "params"
                java.lang.String r0 = r6.getAttribute(r0)
                com.miui.maml.data.Expression[] r5 = com.miui.maml.data.Expression.buildMultiple(r5, r0)
                r4.mParams = r5
                com.miui.maml.data.Variables r5 = r4.getVariables()
                java.lang.String r0 = "states"
                java.lang.String r0 = r6.getAttribute(r0)
                com.miui.maml.data.Expression[] r5 = com.miui.maml.data.Expression.buildMultiple(r5, r0)
                r4.mStates = r5
                com.miui.maml.data.Variables r5 = r4.getVariables()
                java.lang.String r0 = "config"
                java.lang.String r0 = r6.getAttribute(r0)
                com.miui.maml.data.Expression r5 = com.miui.maml.data.Expression.build(r5, r0)
                r4.mConfig = r5
                com.miui.maml.data.Expression[] r5 = r4.mParams
                boolean r5 = r4.isExpressionsValid(r5)
                r4.mIsParamsValid = r5
                com.miui.maml.data.Expression[] r5 = r4.mStates
                boolean r5 = r4.isExpressionsValid(r5)
                r4.mIsStatesValid = r5
                java.lang.String r5 = "command"
                java.lang.String r5 = r6.getAttribute(r5)
                int r6 = r5.hashCode()
                r0 = -1367724422(0xffffffffae7a2e7a, float:-5.68847E-11)
                r1 = 3
                r2 = 2
                r3 = 1
                if (r6 == r0) goto L_0x0082
                r0 = -1266098235(0xffffffffb488dfc5, float:-2.5494788E-7)
                if (r6 == r0) goto L_0x0078
                r0 = 3707(0xe7b, float:5.195E-42)
                if (r6 == r0) goto L_0x006e
                r0 = 109327997(0x684367d, float:4.9732945E-35)
                if (r6 == r0) goto L_0x0064
                goto L_0x008c
            L_0x0064:
                java.lang.String r6 = "setTo"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x008c
                r5 = r3
                goto L_0x008d
            L_0x006e:
                java.lang.String r6 = "to"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x008c
                r5 = 0
                goto L_0x008d
            L_0x0078:
                java.lang.String r6 = "fromTo"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x008c
                r5 = r2
                goto L_0x008d
            L_0x0082:
                java.lang.String r6 = "cancel"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x008c
                r5 = r1
                goto L_0x008d
            L_0x008c:
                r5 = -1
            L_0x008d:
                if (r5 == 0) goto L_0x00a3
                if (r5 == r3) goto L_0x00a0
                if (r5 == r2) goto L_0x009d
                if (r5 == r1) goto L_0x009a
                com.miui.maml.ActionCommand$FolmeCommand$Type r5 = com.miui.maml.ActionCommand.FolmeCommand.Type.INVALID
            L_0x0097:
                r4.mCommand = r5
                goto L_0x00a6
            L_0x009a:
                com.miui.maml.ActionCommand$FolmeCommand$Type r5 = com.miui.maml.ActionCommand.FolmeCommand.Type.CANCEL
                goto L_0x0097
            L_0x009d:
                com.miui.maml.ActionCommand$FolmeCommand$Type r5 = com.miui.maml.ActionCommand.FolmeCommand.Type.FROM_TO
                goto L_0x0097
            L_0x00a0:
                com.miui.maml.ActionCommand$FolmeCommand$Type r5 = com.miui.maml.ActionCommand.FolmeCommand.Type.SET_TO
                goto L_0x0097
            L_0x00a3:
                com.miui.maml.ActionCommand$FolmeCommand$Type r5 = com.miui.maml.ActionCommand.FolmeCommand.Type.TO
                goto L_0x0097
            L_0x00a6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.FolmeCommand.<init>(com.miui.maml.elements.ScreenElement, org.w3c.dom.Element):void");
        }

        private void folmeCancel(AnimatedScreenElement animatedScreenElement) {
            animatedScreenElement.folmeCancel(this.mIsParamsValid ? this.mParams : null);
        }

        private void folmeFromTo(AnimatedScreenElement animatedScreenElement) {
            if (this.mIsStatesValid) {
                Expression[] expressionArr = this.mStates;
                if (expressionArr.length > 1) {
                    String evaluateStr = expressionArr[0].evaluateStr();
                    String evaluateStr2 = this.mStates[1].evaluateStr();
                    Expression expression = this.mConfig;
                    animatedScreenElement.folmeFromTo(evaluateStr, evaluateStr2, expression != null ? expression.evaluateStr() : null);
                }
            }
        }

        private void folmeSetTo(AnimatedScreenElement animatedScreenElement) {
            if (this.mIsStatesValid) {
                Expression[] expressionArr = this.mStates;
                if (expressionArr.length > 0) {
                    animatedScreenElement.folmeSetTo(expressionArr[0].evaluateStr());
                }
            }
        }

        private void folmeTo(AnimatedScreenElement animatedScreenElement) {
            if (this.mIsStatesValid) {
                Expression[] expressionArr = this.mStates;
                if (expressionArr.length > 0) {
                    String evaluateStr = expressionArr[0].evaluateStr();
                    Expression expression = this.mConfig;
                    animatedScreenElement.folmeTo(evaluateStr, expression != null ? expression.evaluateStr() : null);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            Object target = getTarget();
            if (target != null && (target instanceof AnimatedScreenElement)) {
                AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) target;
                int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type[this.mCommand.ordinal()];
                if (i == 1) {
                    folmeTo(animatedScreenElement);
                } else if (i == 2) {
                    folmeSetTo(animatedScreenElement);
                } else if (i == 3) {
                    folmeFromTo(animatedScreenElement);
                } else if (i == 4) {
                    folmeCancel(animatedScreenElement);
                }
            }
        }
    }

    private static class FrameRateCommand extends ActionCommand {
        private static final String TAG_NAME = "FrameRateCommand";
        private Expression mRate;

        public FrameRateCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mRate = Expression.build(screenElement.getVariables(), element.getAttribute("rate"));
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            if (this.mRate != null) {
                getRoot().requestFrameRateByCommand((float) this.mRate.evaluate());
            }
        }
    }

    private static class FunctionPerformCommand extends TargetCommand {
        public static final String TAG_NAME = "FunctionCommand";

        public FunctionPerformCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mTargetType = TargetCommand.TargetType.SCREEN_ELEMENT;
        }

        public void doPerform() {
            Object target = getTarget();
            if (target != null && (target instanceof FunctionElement)) {
                ((FunctionElement) target).perform();
            }
        }
    }

    private static class GraphicsCommand extends TargetCommand {
        public static final String TAG_NAME = "GraphicsCommand";
        private Expression mColorArrayNameExp;
        private Expression mColorExp;
        private ColorParser[] mColorParsers;
        private int[] mColors;
        private CommandType mCommand;
        private String mCurrentColorArrayName;
        private String mCurrentStopArrayName;
        private boolean mIsParamsValid;
        private boolean mIsStopsValid;
        private Expression[] mParamExps;
        private Expression mStopArrayNameExp;
        private Expression[] mStopExps;
        private float[] mStops;

        private enum CommandType {
            INVALID,
            BEGIN_FILL,
            BEGIN_GRADIENT_FILL,
            CREATE_GRADIENT_BOX,
            CURVE_TO,
            CUBIC_CURVE_TO,
            DRAW_CIRCLE,
            DRAW_ELLIPSE,
            DRAW_RECT,
            DRAW_ROUND_RECT,
            LINE_GRADIENT_STYLE,
            LINE_STYLE,
            LINE_TO,
            MOVE_TO,
            SET_RENDER_LISTENER
        }

        public GraphicsCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            checkExps(element);
            this.mColorArrayNameExp = Expression.build(getVariables(), element.getAttribute("colorArrayNameExp"));
            this.mStopArrayNameExp = Expression.build(getVariables(), element.getAttribute("stopArrayNameExp"));
            this.mColorExp = Expression.build(getVariables(), element.getAttribute("colorExp"));
            parseCommand(element);
        }

        private void beginFill(GraphicsElement graphicsElement) {
            ColorParser[] colorParserArr = this.mColorParsers;
            int color = (colorParserArr == null || colorParserArr.length <= 0) ? RoundedDrawable.DEFAULT_BORDER_COLOR : colorParserArr[0].getColor();
            Expression expression = this.mColorExp;
            if (expression != null) {
                color = (int) ((long) expression.evaluate());
            }
            graphicsElement.beginFill(color);
        }

        private void checkExps(Element element) {
            String[] split;
            this.mParamExps = Expression.buildMultiple(getVariables(), element.getAttribute("paramsExp"));
            this.mIsParamsValid = isExpressionsValid(this.mParamExps);
            this.mStopExps = Expression.buildMultiple(getVariables(), element.getAttribute("stopsExp"));
            this.mIsStopsValid = isExpressionsValid(this.mStopExps);
            String attribute = element.getAttribute("colors");
            if (!TextUtils.isEmpty(attribute) && (split = attribute.split(",")) != null && split.length > 0) {
                this.mColorParsers = new ColorParser[split.length];
                for (int i = 0; i < split.length; i++) {
                    this.mColorParsers[i] = new ColorParser(getVariables(), split[i]);
                }
            }
        }

        private void createGradientBox(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 4) {
                    graphicsElement.createOrUpdateGradientBox(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()), this.mParamExps[4].evaluateStr());
                }
            }
        }

        private void cubicCurveTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 5) {
                    graphicsElement.cubicCurveTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()), scale((float) this.mParamExps[4].evaluate()), scale((float) this.mParamExps[5].evaluate()));
                }
            }
        }

        private void curveTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 3) {
                    graphicsElement.curveTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()));
                }
            }
        }

        private void drawCircle(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 2) {
                    graphicsElement.drawCircle(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()));
                }
            }
        }

        private void drawEllipse(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 3) {
                    graphicsElement.drawEllipse(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()));
                }
            }
        }

        private void drawRect(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 3) {
                    graphicsElement.drawRect(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()));
                }
            }
        }

        private void drawRoundRect(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 4) {
                    float scale = scale((float) expressionArr[0].evaluate());
                    float scale2 = scale((float) this.mParamExps[1].evaluate());
                    float scale3 = scale((float) this.mParamExps[2].evaluate());
                    float scale4 = scale((float) this.mParamExps[3].evaluate());
                    float scale5 = scale((float) this.mParamExps[4].evaluate());
                    Expression[] expressionArr2 = this.mParamExps;
                    graphicsElement.drawRoundRect(scale, scale2, scale3, scale4, scale5, expressionArr2.length > 5 ? scale((float) expressionArr2[5].evaluate()) : scale5);
                }
            }
        }

        private void lineStyle(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid && this.mParamExps.length > 0) {
                int i = RoundedDrawable.DEFAULT_BORDER_COLOR;
                Expression expression = this.mColorExp;
                int i2 = 0;
                if (expression != null) {
                    i = (int) ((long) expression.evaluate());
                } else {
                    ColorParser[] colorParserArr = this.mColorParsers;
                    if (colorParserArr != null && colorParserArr.length > 0) {
                        i = colorParserArr[0].getColor();
                    }
                }
                int i3 = i;
                float scale = scale((float) this.mParamExps[0].evaluate());
                Expression[] expressionArr = this.mParamExps;
                int evaluate = expressionArr.length > 1 ? (int) expressionArr[1].evaluate() : 0;
                Expression[] expressionArr2 = this.mParamExps;
                int evaluate2 = expressionArr2.length > 2 ? (int) expressionArr2[2].evaluate() : 0;
                Expression[] expressionArr3 = this.mParamExps;
                if (expressionArr3.length > 3) {
                    i2 = (int) expressionArr3[3].evaluate();
                }
                graphicsElement.lineStyle(scale, i3, evaluate, evaluate2, (float) i2);
            }
        }

        private void lineTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 1) {
                    graphicsElement.lineTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()));
                }
            }
        }

        private void moveTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 1) {
                    graphicsElement.moveTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()));
                }
            }
        }

        private void parseColor() {
            if (this.mColorArrayNameExp != null) {
                parseColorByArrayName();
                return;
            }
            ColorParser[] colorParserArr = this.mColorParsers;
            if (colorParserArr != null && colorParserArr.length > 1) {
                parseColorByParsers();
            }
        }

        private void parseColorByArrayName() {
            String evaluateStr = this.mColorArrayNameExp.evaluateStr();
            if (!TextUtils.isEmpty(evaluateStr)) {
                if (!evaluateStr.equals(this.mCurrentColorArrayName)) {
                    this.mCurrentColorArrayName = evaluateStr;
                    Object obj = new IndexedVariable(evaluateStr, getVariables(), false).get();
                    if (obj != null && (obj instanceof int[])) {
                        int[] iArr = (int[]) obj;
                        if (iArr.length > 1) {
                            this.mColors = iArr;
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
            this.mColors = null;
        }

        private void parseColorByParsers() {
            if (this.mColors == null) {
                this.mColors = new int[this.mColorParsers.length];
            }
            int i = 0;
            while (true) {
                ColorParser[] colorParserArr = this.mColorParsers;
                if (i < colorParserArr.length) {
                    this.mColors[i] = colorParserArr[i].getColor();
                    i++;
                } else {
                    return;
                }
            }
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void parseCommand(org.w3c.dom.Element r2) {
            /*
                r1 = this;
                java.lang.String r0 = "command"
                java.lang.String r2 = r2.getAttribute(r0)
                int r0 = r2.hashCode()
                switch(r0) {
                    case -1807133155: goto L_0x009a;
                    case -1741117076: goto L_0x008f;
                    case -1102672497: goto L_0x0084;
                    case -1073257012: goto L_0x007a;
                    case -1068263892: goto L_0x006f;
                    case -826951352: goto L_0x0065;
                    case -556608716: goto L_0x005b;
                    case 27279565: goto L_0x0050;
                    case 80105951: goto L_0x0046;
                    case 137996206: goto L_0x003b;
                    case 753006880: goto L_0x0030;
                    case 1127058378: goto L_0x0025;
                    case 1312120860: goto L_0x001a;
                    case 1780535802: goto L_0x000f;
                    default: goto L_0x000d;
                }
            L_0x000d:
                goto L_0x00a5
            L_0x000f:
                java.lang.String r0 = "drawEllipse"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 6
                goto L_0x00a6
            L_0x001a:
                java.lang.String r0 = "beginGradientFill"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 1
                goto L_0x00a6
            L_0x0025:
                java.lang.String r0 = "curveTo"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 3
                goto L_0x00a6
            L_0x0030:
                java.lang.String r0 = "cubicCurveTo"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 4
                goto L_0x00a6
            L_0x003b:
                java.lang.String r0 = "drawRoundRect"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 8
                goto L_0x00a6
            L_0x0046:
                java.lang.String r0 = "createGradientBox"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 2
                goto L_0x00a6
            L_0x0050:
                java.lang.String r0 = "lineGradientStyle"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 9
                goto L_0x00a6
            L_0x005b:
                java.lang.String r0 = "drawCircle"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 5
                goto L_0x00a6
            L_0x0065:
                java.lang.String r0 = "drawRect"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 7
                goto L_0x00a6
            L_0x006f:
                java.lang.String r0 = "moveTo"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 12
                goto L_0x00a6
            L_0x007a:
                java.lang.String r0 = "beginFill"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 0
                goto L_0x00a6
            L_0x0084:
                java.lang.String r0 = "lineTo"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 11
                goto L_0x00a6
            L_0x008f:
                java.lang.String r0 = "setRenderListener"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 13
                goto L_0x00a6
            L_0x009a:
                java.lang.String r0 = "lineStyle"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x00a5
                r2 = 10
                goto L_0x00a6
            L_0x00a5:
                r2 = -1
            L_0x00a6:
                switch(r2) {
                    case 0: goto L_0x00d5;
                    case 1: goto L_0x00d2;
                    case 2: goto L_0x00cf;
                    case 3: goto L_0x00cc;
                    case 4: goto L_0x00c9;
                    case 5: goto L_0x00c6;
                    case 6: goto L_0x00c3;
                    case 7: goto L_0x00c0;
                    case 8: goto L_0x00bd;
                    case 9: goto L_0x00ba;
                    case 10: goto L_0x00b7;
                    case 11: goto L_0x00b4;
                    case 12: goto L_0x00b1;
                    case 13: goto L_0x00ae;
                    default: goto L_0x00a9;
                }
            L_0x00a9:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.INVALID
            L_0x00ab:
                r1.mCommand = r2
                goto L_0x00d8
            L_0x00ae:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.SET_RENDER_LISTENER
                goto L_0x00ab
            L_0x00b1:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.MOVE_TO
                goto L_0x00ab
            L_0x00b4:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.LINE_TO
                goto L_0x00ab
            L_0x00b7:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.LINE_STYLE
                goto L_0x00ab
            L_0x00ba:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.LINE_GRADIENT_STYLE
                goto L_0x00ab
            L_0x00bd:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_ROUND_RECT
                goto L_0x00ab
            L_0x00c0:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_RECT
                goto L_0x00ab
            L_0x00c3:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_ELLIPSE
                goto L_0x00ab
            L_0x00c6:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.DRAW_CIRCLE
                goto L_0x00ab
            L_0x00c9:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.CUBIC_CURVE_TO
                goto L_0x00ab
            L_0x00cc:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.CURVE_TO
                goto L_0x00ab
            L_0x00cf:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.CREATE_GRADIENT_BOX
                goto L_0x00ab
            L_0x00d2:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.BEGIN_GRADIENT_FILL
                goto L_0x00ab
            L_0x00d5:
                com.miui.maml.ActionCommand$GraphicsCommand$CommandType r2 = com.miui.maml.ActionCommand.GraphicsCommand.CommandType.BEGIN_FILL
                goto L_0x00ab
            L_0x00d8:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.GraphicsCommand.parseCommand(org.w3c.dom.Element):void");
        }

        private void parseStop() {
            if (this.mStopArrayNameExp != null) {
                parseStopByArrayName();
            } else if (this.mIsStopsValid) {
                parseStopByExp();
            }
        }

        private void parseStopByArrayName() {
            String evaluateStr = this.mStopArrayNameExp.evaluateStr();
            if (!TextUtils.isEmpty(evaluateStr)) {
                if (!evaluateStr.equals(this.mCurrentStopArrayName)) {
                    this.mCurrentStopArrayName = evaluateStr;
                    Object obj = new IndexedVariable(evaluateStr, getVariables(), false).get();
                    if (obj != null && (obj instanceof float[])) {
                        float[] fArr = (float[]) obj;
                        if (fArr.length > 1) {
                            this.mStops = fArr;
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
            this.mStops = null;
        }

        private void parseStopByExp() {
            if (this.mStops == null) {
                this.mStops = new float[this.mStopExps.length];
            }
            int i = 0;
            while (true) {
                Expression[] expressionArr = this.mStopExps;
                if (i < expressionArr.length) {
                    this.mStops[i] = (float) expressionArr[i].evaluate();
                    i++;
                } else {
                    return;
                }
            }
        }

        private float scale(float f) {
            return f * getRoot().getScale();
        }

        private void setRenderListener(GraphicsElement graphicsElement) {
            ScreenElement findElement;
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 0 && (findElement = getRoot().findElement(expressionArr[0].evaluateStr())) != null && (findElement instanceof FunctionElement)) {
                    graphicsElement.setRenderListener((FunctionElement) findElement);
                }
            }
        }

        private void setShader(GraphicsElement graphicsElement) {
            String str;
            if (this.mIsParamsValid && this.mParamExps.length > 2) {
                parseColor();
                parseStop();
                int[] iArr = this.mColors;
                if (iArr == null || iArr.length < 2) {
                    str = "needs >= 2 number of colors";
                } else {
                    float[] fArr = this.mStops;
                    if (fArr == null || fArr.length == iArr.length) {
                        int i = 0;
                        int evaluate = (int) this.mParamExps[0].evaluate();
                        String evaluateStr = this.mParamExps[1].evaluateStr();
                        String evaluateStr2 = this.mParamExps[2].evaluateStr();
                        Expression[] expressionArr = this.mParamExps;
                        if (expressionArr.length > 3) {
                            i = (int) expressionArr[3].evaluate();
                        }
                        int i2 = i;
                        CommandType commandType = this.mCommand;
                        if (commandType == CommandType.LINE_GRADIENT_STYLE) {
                            graphicsElement.lineGradientStyle(evaluate, this.mColors, this.mStops, evaluateStr, evaluateStr2, i2);
                            return;
                        } else if (commandType == CommandType.BEGIN_GRADIENT_FILL) {
                            graphicsElement.beginGradientFill(evaluate, this.mColors, this.mStops, evaluateStr, evaluateStr2, i2);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        str = "color and position arrays must be of equal length";
                    }
                }
                Log.e(TAG_NAME, str);
            }
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            Object target = getTarget();
            if (target != null && (target instanceof GraphicsElement)) {
                GraphicsElement graphicsElement = (GraphicsElement) target;
                switch (AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[this.mCommand.ordinal()]) {
                    case 1:
                        lineTo(graphicsElement);
                        return;
                    case 2:
                        moveTo(graphicsElement);
                        return;
                    case 3:
                        curveTo(graphicsElement);
                        return;
                    case 4:
                        drawRect(graphicsElement);
                        return;
                    case 5:
                        beginFill(graphicsElement);
                        return;
                    case 6:
                        lineStyle(graphicsElement);
                        return;
                    case 7:
                        drawCircle(graphicsElement);
                        return;
                    case 8:
                        drawEllipse(graphicsElement);
                        return;
                    case 9:
                        cubicCurveTo(graphicsElement);
                        return;
                    case 10:
                        drawRoundRect(graphicsElement);
                        return;
                    case 11:
                    case 12:
                        setShader(graphicsElement);
                        return;
                    case 13:
                        createGradientBox(graphicsElement);
                        return;
                    case 14:
                        setRenderListener(graphicsElement);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private static class IfCommand extends ActionCommand {
        private static final String ALTERNATE = "Alternate";
        private static final String CONSEQUENT = "Consequent";
        public static final String TAG_NAME = "IfCommand";
        private MultiCommand mAlternateCommand;
        private Expression mCondition;
        private MultiCommand mConsequentCommand;

        public IfCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mCondition = Expression.build(screenElement.getVariables(), element.getAttribute("ifCondition"));
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeType() == 1) {
                    Element element2 = (Element) childNodes.item(i);
                    String tagName = element2.getTagName();
                    if (CONSEQUENT.equalsIgnoreCase(tagName) && this.mConsequentCommand == null) {
                        this.mConsequentCommand = new MultiCommand(screenElement, element2);
                    } else if (ALTERNATE.equalsIgnoreCase(tagName) && this.mAlternateCommand == null) {
                        this.mAlternateCommand = new MultiCommand(screenElement, element2);
                    }
                }
            }
        }

        public void doPerform() {
            MultiCommand multiCommand;
            Expression expression = this.mCondition;
            if (expression != null) {
                if (expression.evaluate() <= 0.0d) {
                    multiCommand = this.mAlternateCommand;
                    if (multiCommand == null) {
                        return;
                    }
                } else {
                    multiCommand = this.mConsequentCommand;
                    if (multiCommand == null) {
                        return;
                    }
                }
                multiCommand.perform();
            }
        }

        public void finish() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.finish();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.finish();
            }
        }

        public void init() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.init();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.init();
            }
        }

        public void pause() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.pause();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.pause();
            }
        }

        public void resume() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.resume();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.resume();
            }
        }
    }

    private static class IntentCommand extends ActionCommand {
        private static final String TAG_FALLBACK = "Fallback";
        public static final String TAG_NAME = "IntentCommand";
        private ObjVar mActivityOptionsBundle;
        private CommandTrigger mFallbackTrigger;
        private int mFlags;
        private Intent mIntent;
        private IntentInfo mIntentInfo;
        private IntentType mIntentType = IntentType.Activity;
        private IndexedVariable mIntentVar;

        private enum IntentType {
            Activity,
            Broadcast,
            Service,
            Var
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0081  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0095  */
        /* JADX WARNING: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public IntentCommand(com.miui.maml.elements.ScreenElement r5, org.w3c.dom.Element r6) {
            /*
                r4 = this;
                r4.<init>(r5)
                com.miui.maml.ActionCommand$IntentCommand$IntentType r0 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Activity
                r4.mIntentType = r0
                com.miui.maml.util.IntentInfo r0 = new com.miui.maml.util.IntentInfo
                com.miui.maml.data.Variables r1 = r4.getVariables()
                r0.<init>(r6, r1)
                r4.mIntentInfo = r0
                java.lang.String r0 = "broadcast"
                java.lang.String r1 = r6.getAttribute(r0)
                boolean r1 = java.lang.Boolean.parseBoolean(r1)
                java.lang.String r2 = "type"
                java.lang.String r2 = r6.getAttribute(r2)
                if (r1 != 0) goto L_0x0066
                boolean r0 = r0.equals(r2)
                if (r0 == 0) goto L_0x002b
                goto L_0x0066
            L_0x002b:
                java.lang.String r0 = "service"
                boolean r0 = r0.equals(r2)
                if (r0 == 0) goto L_0x0036
                com.miui.maml.ActionCommand$IntentCommand$IntentType r0 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Service
                goto L_0x0068
            L_0x0036:
                java.lang.String r0 = "activity"
                boolean r0 = r0.equals(r2)
                if (r0 == 0) goto L_0x0041
                com.miui.maml.ActionCommand$IntentCommand$IntentType r0 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Activity
                goto L_0x0068
            L_0x0041:
                java.lang.String r0 = "var"
                boolean r0 = r0.equals(r2)
                if (r0 == 0) goto L_0x006a
                com.miui.maml.ActionCommand$IntentCommand$IntentType r0 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Var
                r4.mIntentType = r0
                java.lang.String r0 = "intentVar"
                java.lang.String r0 = r6.getAttribute(r0)
                boolean r1 = android.text.TextUtils.isEmpty(r0)
                if (r1 != 0) goto L_0x006a
                com.miui.maml.data.IndexedVariable r1 = new com.miui.maml.data.IndexedVariable
                com.miui.maml.data.Variables r2 = r4.getVariables()
                r3 = 0
                r1.<init>(r0, r2, r3)
                r4.mIntentVar = r1
                goto L_0x006a
            L_0x0066:
                com.miui.maml.ActionCommand$IntentCommand$IntentType r0 = com.miui.maml.ActionCommand.IntentCommand.IntentType.Broadcast
            L_0x0068:
                r4.mIntentType = r0
            L_0x006a:
                r0 = -1
                java.lang.String r1 = "flags"
                int r0 = com.miui.maml.util.Utils.getAttrAsInt(r6, r1, r0)
                r4.mFlags = r0
                java.lang.String r0 = "activityOption"
                java.lang.String r0 = r6.getAttribute(r0)
                boolean r1 = android.text.TextUtils.isEmpty(r0)
                if (r1 == 0) goto L_0x0081
                r0 = 0
                goto L_0x008b
            L_0x0081:
                com.miui.maml.ActionCommand$ObjVar r1 = new com.miui.maml.ActionCommand$ObjVar
                com.miui.maml.data.Variables r2 = r4.getVariables()
                r1.<init>(r0, r2)
                r0 = r1
            L_0x008b:
                r4.mActivityOptionsBundle = r0
                java.lang.String r0 = "Fallback"
                org.w3c.dom.Element r6 = com.miui.maml.util.Utils.getChild(r6, r0)
                if (r6 == 0) goto L_0x009c
                com.miui.maml.CommandTrigger r0 = new com.miui.maml.CommandTrigger
                r0.<init>(r6, r5)
                r4.mFallbackTrigger = r0
            L_0x009c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.IntentCommand.<init>(com.miui.maml.elements.ScreenElement, org.w3c.dom.Element):void");
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            String str;
            Intent intent = this.mIntent;
            if (intent != null) {
                this.mIntentInfo.update(intent);
                try {
                    int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType[this.mIntentType.ordinal()];
                    if (i == 1) {
                        Bundle bundle = this.mActivityOptionsBundle != null ? (Bundle) this.mActivityOptionsBundle.get() : null;
                        List<ResolveInfo> queryIntentActivities = getContext().getPackageManager().queryIntentActivities(this.mIntent, 65536);
                        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
                            if (!TextUtils.isEmpty(this.mIntent.getPackage())) {
                                str = this.mIntent.getPackage();
                            } else if (!TextUtils.isEmpty(this.mIntent.getComponent().getPackageName())) {
                                str = this.mIntent.getComponent().getPackageName();
                            } else {
                                return;
                            }
                            HideSdkDependencyUtils.PreloadedAppPolicy_installPreloadedDataApp(getContext(), str, this.mIntent, bundle);
                            return;
                        }
                        Utils.startActivity(getContext(), this.mIntent, bundle);
                    } else if (i == 2) {
                        Utils.sendBroadcast(getContext(), this.mIntent);
                    } else if (i == 3) {
                        Utils.startService(getContext(), this.mIntent);
                    } else if (i == 4) {
                        if (this.mIntentVar != null) {
                            this.mIntentVar.set((Object) this.mIntent);
                        }
                    }
                } catch (Exception e) {
                    if (this.mFallbackTrigger != null) {
                        Log.i("ActionCommand", "fail to send Intent, fallback...");
                        this.mFallbackTrigger.perform();
                        return;
                    }
                    Log.e("ActionCommand", e.toString());
                }
            }
        }

        public void finish() {
            CommandTrigger commandTrigger = this.mFallbackTrigger;
            if (commandTrigger != null) {
                commandTrigger.finish();
            }
        }

        public void init() {
            Task findTask = getRoot().findTask(this.mIntentInfo.getId());
            if (findTask != null && !TextUtils.isEmpty(findTask.action)) {
                this.mIntentInfo.set(findTask);
            }
            if (!Utils.isProtectedIntent(this.mIntentInfo.getAction())) {
                this.mIntent = new Intent();
                this.mIntentInfo.update(this.mIntent);
                int i = this.mFlags;
                if (i != -1) {
                    this.mIntent.setFlags(i);
                } else if (this.mIntentType == IntentType.Activity) {
                    this.mIntent.setFlags(872415232);
                }
                CommandTrigger commandTrigger = this.mFallbackTrigger;
                if (commandTrigger != null) {
                    commandTrigger.init();
                }
            }
        }

        public void pause() {
            CommandTrigger commandTrigger = this.mFallbackTrigger;
            if (commandTrigger != null) {
                commandTrigger.pause();
            }
        }

        public void resume() {
            CommandTrigger commandTrigger = this.mFallbackTrigger;
            if (commandTrigger != null) {
                commandTrigger.resume();
            }
        }
    }

    private static class LoopCommand extends MultiCommand {
        private static final long COUNT_WARNING = 10000;
        public static final String TAG_NAME = "LoopCommand";
        private Expression mBeginExp;
        private Expression mConditionExp;
        private Expression mCountExp;
        private Expression mEndExp;
        private IndexedVariable mIndexVar;

        public LoopCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            String attribute = element.getAttribute("indexName");
            Variables variables = getVariables();
            if (!TextUtils.isEmpty(attribute)) {
                this.mIndexVar = new IndexedVariable(attribute, variables, true);
            }
            this.mBeginExp = Expression.build(variables, element.getAttribute("begin"));
            this.mCountExp = Expression.build(variables, element.getAttribute("count"));
            if (this.mCountExp == null) {
                this.mEndExp = Expression.build(variables, element.getAttribute(TtmlNode.END));
            }
            this.mConditionExp = Expression.build(variables, element.getAttribute("loopCondition"));
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            int i;
            Expression expression = this.mBeginExp;
            int evaluate = expression == null ? 0 : (int) expression.evaluate();
            Expression expression2 = this.mCountExp;
            if (expression2 != null) {
                i = (((int) expression2.evaluate()) + evaluate) - 1;
            } else {
                Expression expression3 = this.mEndExp;
                i = expression3 == null ? 0 : (int) expression3.evaluate();
            }
            int i2 = i - evaluate;
            if (((long) i2) > COUNT_WARNING) {
                Log.w("ActionCommand", "count is too large: " + i2 + ", exceeds WARNING " + COUNT_WARNING);
            }
            while (evaluate <= i) {
                Expression expression4 = this.mConditionExp;
                if (expression4 == null || expression4.evaluate() > 0.0d) {
                    IndexedVariable indexedVariable = this.mIndexVar;
                    if (indexedVariable != null) {
                        indexedVariable.set((double) evaluate);
                    }
                    int size = this.mCommands.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        this.mCommands.get(i3).perform();
                    }
                    evaluate++;
                } else {
                    return;
                }
            }
        }
    }

    private static class MethodCommand extends BaseMethodCommand {
        public static final String TAG_NAME = "MethodCommand";
        private Constructor<?> mCtor;
        private Method mMethod;
        private String mMethodName;

        public MethodCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mMethodName = element.getAttribute(MiStat.Param.METHOD);
            this.mLogStr = "MethodCommand, " + this.mLogStr + ", method=" + this.mMethodName + "\n    ";
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0046 A[Catch:{ Exception -> 0x0056, all -> 0x0054 }] */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x004f  */
        /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void doPerform() {
            /*
                r6 = this;
                r6.prepareParams()
                r0 = 0
                r1 = 0
                int[] r2 = com.miui.maml.ActionCommand.AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType     // Catch:{ Exception -> 0x0056 }
                com.miui.maml.ActionCommand$TargetCommand$TargetType r3 = r6.mTargetType     // Catch:{ Exception -> 0x0056 }
                int r3 = r3.ordinal()     // Catch:{ Exception -> 0x0056 }
                r2 = r2[r3]     // Catch:{ Exception -> 0x0056 }
                r3 = -1
                r4 = 1
                if (r2 == r4) goto L_0x002a
                r5 = 2
                if (r2 == r5) goto L_0x002a
                r5 = 5
                if (r2 == r5) goto L_0x001a
                goto L_0x0042
            L_0x001a:
                java.lang.reflect.Constructor<?> r2 = r6.mCtor     // Catch:{ Exception -> 0x0056 }
                if (r2 == 0) goto L_0x0028
                java.lang.reflect.Constructor<?> r0 = r6.mCtor     // Catch:{ Exception -> 0x0056 }
                java.lang.Object[] r2 = r6.mParamValues     // Catch:{ Exception -> 0x0056 }
                java.lang.Object r0 = r0.newInstance(r2)     // Catch:{ Exception -> 0x0056 }
            L_0x0026:
                r1 = r4
                goto L_0x0042
            L_0x0028:
                r1 = r3
                goto L_0x0042
            L_0x002a:
                java.lang.reflect.Method r2 = r6.mMethod     // Catch:{ Exception -> 0x0056 }
                if (r2 != 0) goto L_0x0031
                r6.loadMethod()     // Catch:{ Exception -> 0x0056 }
            L_0x0031:
                java.lang.reflect.Method r2 = r6.mMethod     // Catch:{ Exception -> 0x0056 }
                if (r2 == 0) goto L_0x0028
                java.lang.Object r0 = r6.getTarget()     // Catch:{ Exception -> 0x0056 }
                java.lang.reflect.Method r2 = r6.mMethod     // Catch:{ Exception -> 0x0056 }
                java.lang.Object[] r3 = r6.mParamValues     // Catch:{ Exception -> 0x0056 }
                java.lang.Object r0 = r2.invoke(r0, r3)     // Catch:{ Exception -> 0x0056 }
                goto L_0x0026
            L_0x0042:
                com.miui.maml.data.IndexedVariable r2 = r6.mReturnVar     // Catch:{ Exception -> 0x0056 }
                if (r2 == 0) goto L_0x004b
                com.miui.maml.data.IndexedVariable r2 = r6.mReturnVar     // Catch:{ Exception -> 0x0056 }
                r2.set((java.lang.Object) r0)     // Catch:{ Exception -> 0x0056 }
            L_0x004b:
                com.miui.maml.data.IndexedVariable r0 = r6.mErrorCodeVar
                if (r0 == 0) goto L_0x009b
                double r1 = (double) r1
                r0.set((double) r1)
                goto L_0x009b
            L_0x0054:
                r0 = move-exception
                goto L_0x009c
            L_0x0056:
                r0 = move-exception
                java.lang.Throwable r2 = r0.getCause()     // Catch:{ all -> 0x0054 }
                java.lang.String r3 = "ActionCommand"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0054 }
                r4.<init>()     // Catch:{ all -> 0x0054 }
                java.lang.String r5 = r6.mLogStr     // Catch:{ all -> 0x0054 }
                r4.append(r5)     // Catch:{ all -> 0x0054 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0054 }
                r4.append(r0)     // Catch:{ all -> 0x0054 }
                if (r2 == 0) goto L_0x0086
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0054 }
                r0.<init>()     // Catch:{ all -> 0x0054 }
                java.lang.String r5 = "\n cause: "
                r0.append(r5)     // Catch:{ all -> 0x0054 }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0054 }
                r0.append(r2)     // Catch:{ all -> 0x0054 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0054 }
                goto L_0x0088
            L_0x0086:
                java.lang.String r0 = ""
            L_0x0088:
                r4.append(r0)     // Catch:{ all -> 0x0054 }
                java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0054 }
                android.util.Log.e(r3, r0)     // Catch:{ all -> 0x0054 }
                r0 = -2
                com.miui.maml.data.IndexedVariable r1 = r6.mErrorCodeVar
                if (r1 == 0) goto L_0x009b
                double r2 = (double) r0
                r1.set((double) r2)
            L_0x009b:
                return
            L_0x009c:
                com.miui.maml.data.IndexedVariable r2 = r6.mErrorCodeVar
                if (r2 == 0) goto L_0x00a4
                double r3 = (double) r1
                r2.set((double) r3)
            L_0x00a4:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.MethodCommand.doPerform():void");
        }

        public void init() {
            String str;
            super.init();
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
            if (i == 1 || i == 2) {
                if (this.mMethod == null) {
                    loadMethod();
                }
            } else if (i == 5) {
                if (!getRoot().getCapability(4)) {
                    this.mCtor = null;
                } else if (this.mCtor == null) {
                    Class<?> cls = this.mTargetClass;
                    if (cls != null) {
                        try {
                            this.mCtor = cls.getConstructor(this.mParamTypes);
                        } catch (NoSuchMethodException e) {
                            str = this.mLogStr + "init, fail to find method. " + e.toString();
                        }
                    } else {
                        str = this.mLogStr + "init, class is null.";
                        Log.e("ActionCommand", str);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void loadMethod() {
            Object target;
            if (this.mTargetClass == null && (target = getTarget()) != null) {
                this.mTargetClass = target.getClass();
            }
            Class<?> cls = this.mTargetClass;
            if (cls != null) {
                try {
                    this.mMethod = cls.getMethod(this.mMethodName, this.mParamTypes);
                } catch (NoSuchMethodException e) {
                    Log.e("ActionCommand", this.mLogStr + "loadMethod(). " + e.toString());
                }
                Log.d("ActionCommand", this.mLogStr + "loadMethod(), successful.  " + this.mMethod.toString());
                return;
            }
            Log.e("ActionCommand", this.mLogStr + "loadMethod(), class is null.");
        }
    }

    private static class ModeToggleHelper {
        private int mCurModeIndex;
        private int mCurToggleIndex;
        private ArrayList<Integer> mModeIds;
        private ArrayList<String> mModeNames;
        private boolean mToggle;
        private boolean mToggleAll;
        private ArrayList<Integer> mToggleModes;

        private ModeToggleHelper() {
            this.mModeNames = new ArrayList<>();
            this.mModeIds = new ArrayList<>();
            this.mToggleModes = new ArrayList<>();
        }

        /* synthetic */ ModeToggleHelper(AnonymousClass1 r1) {
            this();
        }

        private int findMode(String str) {
            for (int i = 0; i < this.mModeNames.size(); i++) {
                if (this.mModeNames.get(i).equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        public void addMode(String str, int i) {
            this.mModeNames.add(str);
            this.mModeIds.add(Integer.valueOf(i));
        }

        public boolean build(String str) {
            int findMode = findMode(str);
            if (findMode >= 0) {
                this.mCurModeIndex = findMode;
                return true;
            } else if ("toggle".equals(str)) {
                this.mToggleAll = true;
                return true;
            } else {
                String[] split = str.split(",");
                for (String findMode2 : split) {
                    int findMode3 = findMode(findMode2);
                    if (findMode3 < 0) {
                        return false;
                    }
                    this.mToggleModes.add(Integer.valueOf(findMode3));
                }
                this.mToggle = true;
                return true;
            }
        }

        public void click() {
            int size;
            if (this.mToggle) {
                int i = this.mCurToggleIndex + 1;
                this.mCurToggleIndex = i;
                this.mCurToggleIndex = i % this.mToggleModes.size();
                size = this.mToggleModes.get(this.mCurToggleIndex).intValue();
            } else if (this.mToggleAll) {
                int i2 = this.mCurModeIndex + 1;
                this.mCurModeIndex = i2;
                size = i2 % this.mModeNames.size();
            } else {
                return;
            }
            this.mCurModeIndex = size;
        }

        public int getModeId() {
            return this.mModeIds.get(this.mCurModeIndex).intValue();
        }

        public String getModeName() {
            return this.mModeNames.get(this.mCurModeIndex);
        }
    }

    private static class MultiCommand extends ActionCommand {
        public static final String TAG_NAME = "MultiCommand";
        public static final String TAG_NAME1 = "GroupCommand";
        protected ArrayList<ActionCommand> mCommands = new ArrayList<>();

        public MultiCommand(final ScreenElement screenElement, Element element) {
            super(screenElement);
            Utils.traverseXmlElementChildren(element, (String) null, new Utils.XmlTraverseListener() {
                public void onChild(Element element) {
                    ActionCommand create = ActionCommand.create(element, screenElement);
                    if (create != null) {
                        MultiCommand.this.mCommands.add(create);
                    }
                }
            });
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().perform();
            }
        }

        public void finish() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().finish();
            }
        }

        public void init() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().init();
            }
        }

        public void pause() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().pause();
            }
        }

        public void resume() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().resume();
            }
        }
    }

    private static abstract class NotificationReceiver extends StatefulActionCommand implements NotifierManager.OnNotifyListener {
        private NotifierManager mNotifierManager = NotifierManager.getInstance(getContext());
        private String mType;

        public NotificationReceiver(ScreenElement screenElement, String str, String str2) {
            super(screenElement, str);
            this.mType = str2;
        }

        /* access modifiers changed from: protected */
        public void asyncUpdate() {
            ActionCommand.mHandler.post(new Runnable() {
                public void run() {
                    NotificationReceiver.this.update();
                }
            });
        }

        public void finish() {
            this.mNotifierManager.releaseNotifier(this.mType, this);
        }

        public void init() {
            update();
            this.mNotifierManager.acquireNotifier(this.mType, this);
        }

        public void onNotify(Context context, Intent intent, Object obj) {
            asyncUpdate();
        }

        public void pause() {
            this.mNotifierManager.pause(this.mType, this);
        }

        public void resume() {
            update();
            this.mNotifierManager.resume(this.mType, this);
        }

        /* access modifiers changed from: protected */
        public abstract void update();
    }

    protected static class ObjVar {
        private int mIndex;
        private Expression mIndexArr;
        private Variables mVars;

        public ObjVar(String str, Variables variables) {
            this.mVars = variables;
            int indexOf = str.indexOf(91);
            if (indexOf > 0) {
                try {
                    String substring = str.substring(0, indexOf);
                    try {
                        this.mIndexArr = Expression.build(variables, str.substring(indexOf + 1, str.length() - 1));
                    } catch (IndexOutOfBoundsException unused) {
                    }
                    str = substring;
                } catch (IndexOutOfBoundsException unused2) {
                }
            }
            this.mIndex = variables.registerVariable(str);
        }

        public Object get() {
            Expression expression;
            Object obj = this.mVars.get(this.mIndex);
            if (obj == null || (expression = this.mIndexArr) == null || !(obj instanceof Object[])) {
                return obj;
            }
            try {
                return ((Object[]) obj)[(int) expression.evaluate()];
            } catch (IndexOutOfBoundsException unused) {
                return null;
            }
        }
    }

    private static class OnOffCommandHelper {
        protected boolean mIsOn;
        protected boolean mIsToggle;

        public OnOffCommandHelper(String str) {
            if (str.equalsIgnoreCase("toggle")) {
                this.mIsToggle = true;
            } else if (str.equalsIgnoreCase("on")) {
                this.mIsOn = true;
            } else if (str.equalsIgnoreCase("off")) {
                this.mIsOn = false;
            }
        }
    }

    @Deprecated
    public static abstract class PropertyCommand extends ActionCommand {
        protected ScreenElement mTargetElement;
        private Variable mTargetObj;

        protected PropertyCommand(ScreenElement screenElement, Variable variable, String str) {
            super(screenElement);
            this.mTargetObj = variable;
        }

        public static PropertyCommand create(ScreenElement screenElement, String str, String str2) {
            Variable variable = new Variable(str);
            if ("visibility".equals(variable.getPropertyName())) {
                return new VisibilityProperty(screenElement, variable, str2);
            }
            if (AnimationProperty.PROPERTY_NAME.equals(variable.getPropertyName())) {
                return new AnimationProperty(screenElement, variable, str2);
            }
            return null;
        }

        public void init() {
            ActionCommand.super.init();
            if (this.mTargetObj != null && this.mTargetElement == null) {
                this.mTargetElement = getRoot().findElement(this.mTargetObj.getObjName());
                if (this.mTargetElement == null) {
                    Log.w("ActionCommand", "could not find PropertyCommand target, name: " + this.mTargetObj.getObjName());
                    this.mTargetObj = null;
                }
            }
        }

        public void perform() {
            if (this.mTargetElement != null) {
                doPerform();
            }
        }
    }

    private static class RingModeCommand extends NotificationReceiver {
        private AudioManager mAudioManager;
        private ModeToggleHelper mToggleHelper = new ModeToggleHelper((AnonymousClass1) null);

        public RingModeCommand(ScreenElement screenElement, String str) {
            super(screenElement, VariableNames.RING_MODE, "android.media.RINGER_MODE_CHANGED");
            this.mToggleHelper.addMode("normal", 2);
            this.mToggleHelper.addMode("silent", 0);
            this.mToggleHelper.addMode("vibrate", 1);
            if (!this.mToggleHelper.build(str)) {
                Log.e("ActionCommand", "invalid ring mode command value: " + str);
            }
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            if (this.mAudioManager != null) {
                this.mToggleHelper.click();
                int modeId = this.mToggleHelper.getModeId();
                this.mAudioManager.setRingerMode(modeId);
                updateState(modeId);
            }
        }

        /* access modifiers changed from: protected */
        public void update() {
            if (this.mAudioManager == null) {
                this.mAudioManager = (AudioManager) this.mScreenElement.getContext().mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            }
            AudioManager audioManager = this.mAudioManager;
            if (audioManager != null) {
                updateState(audioManager.getRingerMode());
            }
        }
    }

    private static class SensorBinderCommand extends TargetCommand {
        public static final String TAG_NAME = "SensorCommand";
        private CommandType mCommand;

        private enum CommandType {
            INVALID,
            TURN_ON,
            TURN_OFF
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0034  */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x003e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public SensorBinderCommand(com.miui.maml.elements.ScreenElement r3, org.w3c.dom.Element r4) {
            /*
                r2 = this;
                r2.<init>(r3, r4)
                com.miui.maml.ActionCommand$TargetCommand$TargetType r3 = com.miui.maml.ActionCommand.TargetCommand.TargetType.VARIABLE_BINDER
                r2.mTargetType = r3
                java.lang.String r3 = "command"
                java.lang.String r3 = r4.getAttribute(r3)
                int r4 = r3.hashCode()
                r0 = -965507150(0xffffffffc67387b2, float:-15585.924)
                r1 = 1
                if (r4 == r0) goto L_0x0027
                r0 = -862429380(0xffffffffcc985f3c, float:-7.9886816E7)
                if (r4 == r0) goto L_0x001d
                goto L_0x0031
            L_0x001d:
                java.lang.String r4 = "turnOn"
                boolean r3 = r3.equals(r4)
                if (r3 == 0) goto L_0x0031
                r3 = 0
                goto L_0x0032
            L_0x0027:
                java.lang.String r4 = "turnOff"
                boolean r3 = r3.equals(r4)
                if (r3 == 0) goto L_0x0031
                r3 = r1
                goto L_0x0032
            L_0x0031:
                r3 = -1
            L_0x0032:
                if (r3 == 0) goto L_0x003e
                if (r3 == r1) goto L_0x003b
                com.miui.maml.ActionCommand$SensorBinderCommand$CommandType r3 = com.miui.maml.ActionCommand.SensorBinderCommand.CommandType.INVALID
            L_0x0038:
                r2.mCommand = r3
                goto L_0x0041
            L_0x003b:
                com.miui.maml.ActionCommand$SensorBinderCommand$CommandType r3 = com.miui.maml.ActionCommand.SensorBinderCommand.CommandType.TURN_OFF
                goto L_0x0038
            L_0x003e:
                com.miui.maml.ActionCommand$SensorBinderCommand$CommandType r3 = com.miui.maml.ActionCommand.SensorBinderCommand.CommandType.TURN_ON
                goto L_0x0038
            L_0x0041:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.SensorBinderCommand.<init>(com.miui.maml.elements.ScreenElement, org.w3c.dom.Element):void");
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            Object target = getTarget();
            if (target != null && (target instanceof SensorBinder)) {
                SensorBinder sensorBinder = (SensorBinder) target;
                int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType[this.mCommand.ordinal()];
                if (i == 1) {
                    sensorBinder.turnOnSensorBinder();
                } else if (i == 2) {
                    sensorBinder.turnOffSensorBinder();
                }
            }
        }
    }

    private static class SoundCommand extends ActionCommand {
        public static final String TAG_NAME = "SoundCommand";
        private SoundManager.Command mCommand;
        private boolean mKeepCur;
        private boolean mLoop;
        private String mSound;
        private Expression mStreamIdExp;
        private IndexedVariable mStreamIdVar;
        private Expression mVolumeExp;

        public SoundCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mSound = element.getAttribute("sound");
            this.mKeepCur = Boolean.parseBoolean(element.getAttribute("keepCur"));
            this.mLoop = Boolean.parseBoolean(element.getAttribute("loop"));
            this.mCommand = SoundManager.Command.parse(element.getAttribute("command"));
            this.mVolumeExp = Expression.build(getVariables(), element.getAttribute("volume"));
            if (this.mVolumeExp == null) {
                Log.e("ActionCommand", "invalid expression in SoundCommand");
            }
            this.mStreamIdExp = Expression.build(getVariables(), element.getAttribute("streamId"));
            String attribute = element.getAttribute("streamIdVar");
            if (!TextUtils.isEmpty(attribute)) {
                this.mStreamIdVar = new IndexedVariable(attribute, getVariables(), true);
            }
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            Expression expression;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$SoundManager$Command[this.mCommand.ordinal()];
            if (i == 1) {
                float f = 0.0f;
                Expression expression2 = this.mVolumeExp;
                if (expression2 != null) {
                    f = (float) expression2.evaluate();
                }
                int playSound = getRoot().playSound(this.mSound, new SoundManager.SoundOptions(this.mKeepCur, this.mLoop, f));
                IndexedVariable indexedVariable = this.mStreamIdVar;
                if (indexedVariable != null) {
                    indexedVariable.set((double) playSound);
                }
            } else if ((i == 2 || i == 3 || i == 4) && (expression = this.mStreamIdExp) != null) {
                getRoot().playSound((int) expression.evaluate(), this.mCommand);
            }
        }
    }

    public static abstract class StateTracker {
        private Boolean mActualState = null;
        private boolean mDeferredStateChangeRequestNeeded = false;
        private boolean mInTransition = false;
        private Boolean mIntendedState = null;

        public abstract int getActualState(Context context);

        public final int getTriState(Context context) {
            if (this.mInTransition) {
                return 5;
            }
            int actualState = getActualState(context);
            if (actualState != 0) {
                return actualState != 1 ? 5 : 1;
            }
            return 0;
        }

        public final boolean isTurningOn() {
            Boolean bool = this.mIntendedState;
            return bool != null && bool.booleanValue();
        }

        public abstract void onActualStateChange(Context context, Intent intent);

        /* access modifiers changed from: protected */
        public abstract void requestStateChange(Context context, boolean z);

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x002e  */
        /* JADX WARNING: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void setCurrentState(android.content.Context r5, int r6) {
            /*
                r4 = this;
                boolean r0 = r4.mInTransition
                r1 = 0
                r2 = 1
                if (r6 == 0) goto L_0x001c
                if (r6 == r2) goto L_0x0015
                r3 = 2
                if (r6 == r3) goto L_0x0012
                r3 = 3
                if (r6 == r3) goto L_0x000f
                goto L_0x0024
            L_0x000f:
                r4.mInTransition = r2
                goto L_0x0017
            L_0x0012:
                r4.mInTransition = r2
                goto L_0x001e
            L_0x0015:
                r4.mInTransition = r1
            L_0x0017:
                java.lang.Boolean r6 = java.lang.Boolean.valueOf(r2)
                goto L_0x0022
            L_0x001c:
                r4.mInTransition = r1
            L_0x001e:
                java.lang.Boolean r6 = java.lang.Boolean.valueOf(r1)
            L_0x0022:
                r4.mActualState = r6
            L_0x0024:
                if (r0 == 0) goto L_0x0058
                boolean r6 = r4.mInTransition
                if (r6 != 0) goto L_0x0058
                boolean r6 = r4.mDeferredStateChangeRequestNeeded
                if (r6 == 0) goto L_0x0058
                java.lang.String r6 = "ActionCommand"
                java.lang.String r0 = "processing deferred state change"
                android.util.Log.v(r6, r0)
                java.lang.Boolean r0 = r4.mActualState
                if (r0 == 0) goto L_0x0049
                java.lang.Boolean r3 = r4.mIntendedState
                if (r3 == 0) goto L_0x0049
                boolean r0 = r3.equals(r0)
                if (r0 == 0) goto L_0x0049
                java.lang.String r5 = "... but intended state matches, so no changes."
                android.util.Log.v(r6, r5)
                goto L_0x0056
            L_0x0049:
                java.lang.Boolean r6 = r4.mIntendedState
                if (r6 == 0) goto L_0x0056
                r4.mInTransition = r2
                boolean r6 = r6.booleanValue()
                r4.requestStateChange(r5, r6)
            L_0x0056:
                r4.mDeferredStateChangeRequestNeeded = r1
            L_0x0058:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.StateTracker.setCurrentState(android.content.Context, int):void");
        }

        public final void toggleState(Context context) {
            Boolean bool;
            int triState = getTriState(context);
            boolean z = false;
            if (triState == 0) {
                z = true;
            } else if (!(triState == 1 || triState != 5 || (bool = this.mIntendedState) == null)) {
                z = !bool.booleanValue();
            }
            this.mIntendedState = Boolean.valueOf(z);
            if (this.mInTransition) {
                this.mDeferredStateChangeRequestNeeded = true;
                return;
            }
            this.mInTransition = true;
            requestStateChange(context, z);
        }
    }

    private static abstract class StatefulActionCommand extends ActionCommand {
        private IndexedVariable mVar;

        public StatefulActionCommand(ScreenElement screenElement, String str) {
            super(screenElement);
            this.mVar = new IndexedVariable(str, getVariables(), true);
        }

        /* access modifiers changed from: protected */
        public final void updateState(int i) {
            IndexedVariable indexedVariable = this.mVar;
            if (indexedVariable != null) {
                indexedVariable.set((double) i);
                getRoot().requestUpdate();
            }
        }
    }

    private static abstract class TargetCommand extends ActionCommand {
        protected String mLogStr;
        private Object mTarget;
        protected Expression mTargetIndex;
        protected String mTargetName;
        protected Expression mTargetNameExp;
        protected TargetType mTargetType;

        protected enum TargetType {
            SCREEN_ELEMENT,
            VARIABLE,
            CONSTRUCTOR,
            ANIMATION_ITEM,
            VARIABLE_BINDER
        }

        public TargetCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            TargetType targetType;
            this.mTargetNameExp = Expression.build(getVariables(), element.getAttribute("targetExp"));
            Expression expression = this.mTargetNameExp;
            this.mTargetName = expression != null ? expression.evaluateStr() : element.getAttribute("target");
            if (TextUtils.isEmpty(this.mTargetName)) {
                this.mTargetName = null;
            }
            this.mTargetIndex = Expression.build(getVariables(), element.getAttribute("targetIndex"));
            String attribute = element.getAttribute("targetType");
            this.mTargetType = TargetType.SCREEN_ELEMENT;
            if ("element".equals(attribute)) {
                targetType = TargetType.SCREEN_ELEMENT;
            } else if ("var".equals(attribute)) {
                targetType = TargetType.VARIABLE;
            } else {
                if ("ctor".equals(attribute)) {
                    targetType = TargetType.CONSTRUCTOR;
                }
                this.mLogStr = "target=" + this.mTargetName + " type=" + this.mTargetType.toString();
            }
            this.mTargetType = targetType;
            this.mLogStr = "target=" + this.mTargetName + " type=" + this.mTargetType.toString();
        }

        private void findTarget() {
            String str;
            Object valueOf;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        valueOf = getRoot().getAnimationItems(this.mTargetName);
                    } else if (i == 4) {
                        valueOf = getRoot().findBinder(this.mTargetName);
                    } else {
                        return;
                    }
                } else if (this.mTargetName != null) {
                    valueOf = Integer.valueOf(getVariables().registerVariable(this.mTargetName));
                } else {
                    str = "MethodCommand, type=var, empty target name";
                }
                this.mTarget = valueOf;
                return;
            }
            ScreenElement findElement = getRoot().findElement(this.mTargetName);
            this.mTarget = findElement;
            if (findElement == null) {
                str = "could not find ScreenElement target, name: " + this.mTargetName;
            } else if (this.mTargetIndex != null && !ElementGroup.isArrayGroup(findElement)) {
                Log.e("ActionCommand", "target with index is not an ArrayGroup, name: " + this.mTargetName);
                this.mTargetIndex = null;
                return;
            } else {
                return;
            }
            Log.e("ActionCommand", str);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0089, code lost:
            r1 = r3.mTargetIndex;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Object getTarget() {
            /*
                r3 = this;
                com.miui.maml.data.Expression r0 = r3.mTargetNameExp
                r1 = 0
                if (r0 == 0) goto L_0x001d
                java.lang.String r0 = r0.evaluateStr()
                if (r0 != 0) goto L_0x0010
                r3.mTargetName = r1
                r3.mTarget = r1
                return r1
            L_0x0010:
                java.lang.String r2 = r3.mTargetName
                boolean r2 = r0.equals(r2)
                if (r2 != 0) goto L_0x001d
                r3.mTargetName = r0
                r3.findTarget()
            L_0x001d:
                int[] r0 = com.miui.maml.ActionCommand.AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType
                com.miui.maml.ActionCommand$TargetCommand$TargetType r2 = r3.mTargetType
                int r2 = r2.ordinal()
                r0 = r0[r2]
                r2 = 1
                if (r0 == r2) goto L_0x0085
                r2 = 2
                if (r0 == r2) goto L_0x003a
                r2 = 3
                if (r0 == r2) goto L_0x0037
                r2 = 4
                if (r0 == r2) goto L_0x0034
                return r1
            L_0x0034:
                java.lang.Object r0 = r3.mTarget
                return r0
            L_0x0037:
                java.lang.Object r0 = r3.mTarget
                return r0
            L_0x003a:
                java.lang.Object r0 = r3.mTarget
                if (r0 == 0) goto L_0x0084
                com.miui.maml.data.Variables r0 = r3.getVariables()
                java.lang.Object r2 = r3.mTarget
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                java.lang.Object r0 = r0.get((int) r2)
                com.miui.maml.data.Expression r2 = r3.mTargetIndex
                if (r2 == 0) goto L_0x0083
                java.lang.Class r2 = r0.getClass()
                boolean r2 = r2.isArray()
                if (r2 == 0) goto L_0x0068
                com.miui.maml.data.Expression r1 = r3.mTargetIndex
                double r1 = r1.evaluate()
                int r1 = (int) r1
                java.lang.Object r0 = java.lang.reflect.Array.get(r0, r1)
                return r0
            L_0x0068:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r2 = "target with index is not an Array variable, name: "
                r0.append(r2)
                java.lang.String r2 = r3.mTargetName
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                java.lang.String r2 = "ActionCommand"
                android.util.Log.e(r2, r0)
                r3.mTargetIndex = r1
                goto L_0x0084
            L_0x0083:
                return r0
            L_0x0084:
                return r1
            L_0x0085:
                java.lang.Object r0 = r3.mTarget
                if (r0 == 0) goto L_0x0099
                com.miui.maml.data.Expression r1 = r3.mTargetIndex
                if (r1 == 0) goto L_0x0099
                com.miui.maml.elements.ElementGroup r0 = (com.miui.maml.elements.ElementGroup) r0
                double r1 = r1.evaluate()
                int r1 = (int) r1
                com.miui.maml.elements.ScreenElement r0 = r0.getChild(r1)
                return r0
            L_0x0099:
                java.lang.Object r0 = r3.mTarget
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.TargetCommand.getTarget():java.lang.Object");
        }

        public void init() {
            ActionCommand.super.init();
            findTarget();
        }
    }

    private static class TickListenerCommand extends TargetCommand {
        public static final String TAG_NAME = "TickListenerCommand";
        private CommandType mCommand;
        private Expression mFunNameExp;

        private enum CommandType {
            INVALID,
            SET,
            UNSET
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0040  */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x0046  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public TickListenerCommand(com.miui.maml.elements.ScreenElement r3, org.w3c.dom.Element r4) {
            /*
                r2 = this;
                r2.<init>(r3, r4)
                com.miui.maml.data.Variables r3 = r2.getVariables()
                java.lang.String r0 = "function"
                java.lang.String r0 = r4.getAttribute(r0)
                com.miui.maml.data.Expression r3 = com.miui.maml.data.Expression.build(r3, r0)
                r2.mFunNameExp = r3
                java.lang.String r3 = "command"
                java.lang.String r3 = r4.getAttribute(r3)
                int r4 = r3.hashCode()
                r0 = 113762(0x1bc62, float:1.59415E-40)
                r1 = 1
                if (r4 == r0) goto L_0x0033
                r0 = 111442729(0x6a47b29, float:6.187091E-35)
                if (r4 == r0) goto L_0x0029
                goto L_0x003d
            L_0x0029:
                java.lang.String r4 = "unset"
                boolean r3 = r3.equals(r4)
                if (r3 == 0) goto L_0x003d
                r3 = r1
                goto L_0x003e
            L_0x0033:
                java.lang.String r4 = "set"
                boolean r3 = r3.equals(r4)
                if (r3 == 0) goto L_0x003d
                r3 = 0
                goto L_0x003e
            L_0x003d:
                r3 = -1
            L_0x003e:
                if (r3 == 0) goto L_0x0046
                if (r3 == r1) goto L_0x0043
                goto L_0x004a
            L_0x0043:
                com.miui.maml.ActionCommand$TickListenerCommand$CommandType r3 = com.miui.maml.ActionCommand.TickListenerCommand.CommandType.UNSET
                goto L_0x0048
            L_0x0046:
                com.miui.maml.ActionCommand$TickListenerCommand$CommandType r3 = com.miui.maml.ActionCommand.TickListenerCommand.CommandType.SET
            L_0x0048:
                r2.mCommand = r3
            L_0x004a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.TickListenerCommand.<init>(com.miui.maml.elements.ScreenElement, org.w3c.dom.Element):void");
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            Object target = getTarget();
            if (target != null && (target instanceof AnimatedScreenElement)) {
                AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) target;
                int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType[this.mCommand.ordinal()];
                if (i == 1) {
                    ScreenElement findElement = getRoot().findElement(this.mFunNameExp.evaluateStr());
                    if (findElement != null && (findElement instanceof FunctionElement)) {
                        animatedScreenElement.setOnTickListener((FunctionElement) findElement);
                    }
                } else if (i == 2) {
                    animatedScreenElement.unsetOnTickListener();
                }
            }
        }
    }

    private static class UsbStorageSwitchCommand extends NotificationReceiver {
        private boolean mConnected;
        private OnOffCommandHelper mOnOffHelper;
        /* access modifiers changed from: private */
        public StorageManager mStorageManager;

        public UsbStorageSwitchCommand(ScreenElement screenElement, String str) {
            super(screenElement, VariableNames.USB_MODE, ActionCommand.ACTION_USB_STATE);
            this.mOnOffHelper = new OnOffCommandHelper(str);
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            final boolean z;
            StorageManager storageManager = this.mStorageManager;
            if (storageManager != null) {
                boolean StorageManager_isUsbMassStorageEnabled = HideSdkDependencyUtils.StorageManager_isUsbMassStorageEnabled(storageManager);
                OnOffCommandHelper onOffCommandHelper = this.mOnOffHelper;
                if (onOffCommandHelper.mIsToggle) {
                    z = !StorageManager_isUsbMassStorageEnabled;
                } else {
                    boolean z2 = onOffCommandHelper.mIsOn;
                    if (z2 != StorageManager_isUsbMassStorageEnabled) {
                        z = z2;
                    } else {
                        return;
                    }
                }
                updateState(3);
                new Thread("StorageSwitchThread") {
                    public void run() {
                        if (z) {
                            HideSdkDependencyUtils.StorageManager_enableUsbMassStorage(UsbStorageSwitchCommand.this.mStorageManager);
                        } else {
                            HideSdkDependencyUtils.StorageManager_disableUsbMassStorage(UsbStorageSwitchCommand.this.mStorageManager);
                        }
                        UsbStorageSwitchCommand.this.updateState(z ? 2 : 1);
                    }
                }.start();
            }
        }

        public void onNotify(Context context, Intent intent, Object obj) {
            if (intent != null) {
                this.mConnected = intent.getExtras().getBoolean(ActionCommand.USB_CONNECTED);
            }
            super.onNotify(context, intent, obj);
        }

        /* access modifiers changed from: protected */
        public void update() {
            if (this.mStorageManager == null) {
                this.mStorageManager = (StorageManager) getContext().getSystemService("storage");
                if (this.mStorageManager == null) {
                    Log.w("ActionCommand", "Failed to get StorageManager");
                    return;
                }
            }
            updateState(this.mConnected ? HideSdkDependencyUtils.StorageManager_isUsbMassStorageEnabled(this.mStorageManager) ? 2 : 1 : 0);
        }
    }

    private static class VariableAssignmentCommand extends ActionCommand {
        public static final String TAG_NAME = "VariableCommand";
        private Expression[] mArrayValues;
        private Expression mExpression;
        private Expression mIndexExpression;
        private String mName;
        private Expression mNameExp;
        private boolean mPersist;
        private boolean mRequestUpdate;
        private VariableType mType;
        private IndexedVariable mVar;

        public VariableAssignmentCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            Variables variables = screenElement.getVariables();
            this.mNameExp = Expression.build(variables, element.getAttribute("nameExp"));
            Expression expression = this.mNameExp;
            this.mName = expression != null ? expression.evaluateStr() : element.getAttribute(CloudPushConstants.XML_NAME);
            this.mPersist = Boolean.parseBoolean(element.getAttribute("persist"));
            this.mRequestUpdate = Boolean.parseBoolean(element.getAttribute("requestUpdate"));
            this.mType = VariableType.parseType(element.getAttribute("type"));
            if (!TextUtils.isEmpty(this.mName)) {
                this.mVar = new IndexedVariable(this.mName, getVariables(), this.mType.isNumber());
            } else {
                Log.e("ActionCommand", "empty name in VariableAssignmentCommand");
            }
            this.mExpression = Expression.build(variables, element.getAttribute("expression"));
            if (this.mType.isArray()) {
                this.mIndexExpression = Expression.build(variables, element.getAttribute("index"));
                this.mArrayValues = Expression.buildMultiple(variables, element.getAttribute("values"));
            }
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            ScreenElementRoot root = getRoot();
            Expression expression = this.mNameExp;
            Object obj = null;
            if (expression != null) {
                String evaluateStr = expression.evaluateStr();
                if (TextUtils.isEmpty(evaluateStr)) {
                    this.mName = null;
                    return;
                } else if (!evaluateStr.equals(this.mName)) {
                    this.mName = evaluateStr;
                    this.mVar = new IndexedVariable(this.mName, getVariables(), this.mType.isNumber());
                }
            }
            if (this.mVar != null) {
                int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$VariableType[this.mType.ordinal()];
                if (i != 1) {
                    int i2 = 0;
                    if (i == 2) {
                        Expression expression2 = this.mIndexExpression;
                        if (expression2 == null || this.mExpression == null) {
                            Expression[] expressionArr = this.mArrayValues;
                            if (expressionArr != null) {
                                int length = expressionArr.length;
                                while (i2 < length) {
                                    Expression expression3 = this.mArrayValues[i2];
                                    this.mVar.setArr(i2, expression3 == null ? 0.0d : expression3.evaluate());
                                    i2++;
                                }
                            }
                        } else {
                            this.mVar.setArr((int) expression2.evaluate(), this.mExpression.evaluate());
                        }
                    } else if (i == 3) {
                        String evaluateStr2 = this.mExpression.evaluateStr();
                        this.mVar.set((Object) evaluateStr2);
                        if (this.mPersist && root.getCapability(2)) {
                            root.saveVar(this.mName, evaluateStr2);
                        }
                    } else if (i != 4) {
                        Expression expression4 = this.mExpression;
                        String evaluateStr3 = expression4 != null ? expression4.evaluateStr() : null;
                        Variables variables = getVariables();
                        if (!TextUtils.isEmpty(evaluateStr3) && variables.existsObj(evaluateStr3)) {
                            obj = variables.get(evaluateStr3);
                        }
                        Expression expression5 = this.mIndexExpression;
                        if (expression5 == null) {
                            this.mVar.set(obj);
                        } else {
                            this.mVar.setArr((int) expression5.evaluate(), obj);
                        }
                    } else {
                        Expression expression6 = this.mIndexExpression;
                        if (expression6 == null || this.mExpression == null) {
                            Expression[] expressionArr2 = this.mArrayValues;
                            if (expressionArr2 != null) {
                                int length2 = expressionArr2.length;
                                while (i2 < length2) {
                                    Expression expression7 = this.mArrayValues[i2];
                                    this.mVar.setArr(i2, (Object) expression7 == null ? null : expression7.evaluateStr());
                                    i2++;
                                }
                            }
                        } else {
                            this.mVar.setArr((int) expression6.evaluate(), (Object) this.mExpression.evaluateStr());
                        }
                    }
                } else {
                    Expression expression8 = this.mExpression;
                    if (expression8 != null) {
                        double evaluate = expression8.evaluate();
                        this.mVar.set(evaluate);
                        if (this.mPersist && root.getCapability(2)) {
                            root.saveVar(this.mName, Double.valueOf(evaluate));
                        }
                    }
                }
                if (this.mRequestUpdate) {
                    root.requestUpdate();
                }
            }
        }
    }

    private static class VariableBinderCommand extends ActionCommand {
        public static final String TAG_NAME = "BinderCommand";
        private VariableBinder mBinder;
        private Command mCommand = Command.Invalid;
        private String mName;

        private enum Command {
            Refresh,
            Invalid
        }

        public VariableBinderCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
            if (element.getAttribute("command").equals("refresh")) {
                this.mCommand = Command.Refresh;
            }
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            if (this.mBinder != null && AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$VariableBinderCommand$Command[this.mCommand.ordinal()] == 1) {
                this.mBinder.refresh();
            }
        }

        public void init() {
            this.mBinder = getRoot().findBinder(this.mName);
        }
    }

    @Deprecated
    private static class VisibilityProperty extends PropertyCommand {
        public static final String PROPERTY_NAME = "visibility";
        private boolean mIsShow;
        private boolean mIsToggle;

        protected VisibilityProperty(ScreenElement screenElement, Variable variable, String str) {
            super(screenElement, variable, str);
            if (str.equalsIgnoreCase("toggle")) {
                this.mIsToggle = true;
            } else if (str.equalsIgnoreCase("true")) {
                this.mIsShow = true;
            } else if (str.equalsIgnoreCase("false")) {
                this.mIsShow = false;
            }
        }

        public void doPerform() {
            boolean z;
            ScreenElement screenElement;
            if (this.mIsToggle) {
                screenElement = this.mTargetElement;
                z = !screenElement.isVisible();
            } else {
                screenElement = this.mTargetElement;
                z = this.mIsShow;
            }
            screenElement.show(z);
        }
    }

    public static final class WifiEnableAsyncTask extends AsyncTask<Void, Void, Void> {
        Context mContext;
        boolean mDesiredState;

        public WifiEnableAsyncTask(Context context, boolean z) {
            this.mContext = context;
            this.mDesiredState = z;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            if (wifiManager == null) {
                Log.d("ActionCommand", "No wifiManager.");
                return null;
            }
            if (this.mDesiredState && HideSdkDependencyUtils.WifiManager_isWifiApEnabled(wifiManager)) {
                HideSdkDependencyUtils.setWifiApEnabled(this.mContext, false);
            }
            wifiManager.setWifiEnabled(this.mDesiredState);
            return null;
        }
    }

    private static final class WifiStateTracker extends StateTracker {
        private static final int MAX_SCAN_ATTEMPT = 3;
        public boolean zConnected;
        private int zScanAttempt;

        private WifiStateTracker() {
            this.zConnected = false;
            this.zScanAttempt = 0;
        }

        /* synthetic */ WifiStateTracker(AnonymousClass1 r1) {
            this();
        }

        private static int wifiStateToFiveState(int i) {
            if (i == 0) {
                return 3;
            }
            if (i == 1) {
                return 0;
            }
            if (i != 2) {
                return i != 3 ? 4 : 1;
            }
            return 2;
        }

        public int getActualState(Context context) {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            if (wifiManager != null) {
                return wifiStateToFiveState(wifiManager.getWifiState());
            }
            return 4;
        }

        public void onActualStateChange(Context context, Intent intent) {
            boolean z = false;
            if ("android.net.wifi.WIFI_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra(VariableNames.WIFI_STATE, -1);
                setCurrentState(context, wifiStateToFiveState(intExtra));
                if (3 == intExtra) {
                    this.zConnected = true;
                    this.zScanAttempt = 0;
                    return;
                }
                return;
            }
            if ("android.net.wifi.SCAN_RESULTS".equals(intent.getAction())) {
                int i = this.zScanAttempt;
                if (i < 3) {
                    int i2 = i + 1;
                    this.zScanAttempt = i2;
                    if (i2 != 3) {
                        return;
                    }
                } else {
                    return;
                }
            } else if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
                this.zScanAttempt = 3;
                NetworkInfo.DetailedState detailedState = ((NetworkInfo) intent.getParcelableExtra("networkInfo")).getDetailedState();
                if (NetworkInfo.DetailedState.SCANNING == detailedState || NetworkInfo.DetailedState.CONNECTING == detailedState || NetworkInfo.DetailedState.AUTHENTICATING == detailedState || NetworkInfo.DetailedState.OBTAINING_IPADDR == detailedState || NetworkInfo.DetailedState.CONNECTED == detailedState) {
                    z = true;
                }
            } else {
                return;
            }
            this.zConnected = z;
        }

        /* access modifiers changed from: protected */
        public void requestStateChange(Context context, boolean z) {
            new WifiEnableAsyncTask(context, z).execute(new Void[0]);
        }
    }

    private static class WifiSwitchCommand extends NotificationReceiver {
        private OnOffCommandHelper mOnOffHelper;
        private final StateTracker mWifiState = new WifiStateTracker((AnonymousClass1) null);

        public WifiSwitchCommand(ScreenElement screenElement, String str) {
            super(screenElement, VariableNames.WIFI_STATE, NotifierManager.TYPE_WIFI_STATE);
            update();
            this.mOnOffHelper = new OnOffCommandHelper(str);
        }

        /* access modifiers changed from: protected */
        public void doPerform() {
            Context context = getContext();
            if (this.mOnOffHelper.mIsToggle) {
                this.mWifiState.toggleState(context);
            } else {
                int triState = this.mWifiState.getTriState(context);
                boolean z = true;
                if (triState == 0 ? !this.mOnOffHelper.mIsOn : triState != 1 || this.mOnOffHelper.mIsOn) {
                    z = false;
                }
                if (z) {
                    this.mWifiState.requestStateChange(context, this.mOnOffHelper.mIsOn);
                }
            }
            update();
        }

        public void onNotify(Context context, Intent intent, Object obj) {
            this.mWifiState.onActualStateChange(context, intent);
            super.onNotify(context, intent, obj);
        }

        /* access modifiers changed from: protected */
        public void update() {
            int triState = this.mWifiState.getTriState(getContext());
            int i = 0;
            if (triState != 0) {
                int i2 = 1;
                if (triState == 1) {
                    if (!((WifiStateTracker) this.mWifiState).zConnected) {
                        i2 = 2;
                    }
                    updateState(i2);
                    return;
                } else if (triState == 5) {
                    if (this.mWifiState.isTurningOn()) {
                        i = 3;
                    }
                } else {
                    return;
                }
            }
            updateState(i);
        }
    }

    public ActionCommand(ScreenElement screenElement) {
        this.mScreenElement = screenElement;
    }

    protected static ActionCommand create(ScreenElement screenElement, String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            Variable variable = new Variable(str);
            if (variable.getObjName() != null) {
                return PropertyCommand.create(screenElement, str, str2);
            }
            String propertyName = variable.getPropertyName();
            if (COMMAND_RING_MODE.equals(propertyName)) {
                return new RingModeCommand(screenElement, str2);
            }
            if (COMMAND_WIFI.equals(propertyName)) {
                return new WifiSwitchCommand(screenElement, str2);
            }
            if (COMMAND_DATA.equals(propertyName)) {
                return new DataSwitchCommand(screenElement, str2);
            }
            if (COMMAND_BLUETOOTH.equals(propertyName)) {
                return new BluetoothSwitchCommand(screenElement, str2);
            }
            if (COMMAND_USB_STORAGE.equals(propertyName)) {
                return new UsbStorageSwitchCommand(screenElement, str2);
            }
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:74:0x018b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x018c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.maml.ActionCommand create(org.w3c.dom.Element r10, com.miui.maml.elements.ScreenElement r11) {
        /*
            r0 = 0
            if (r10 != 0) goto L_0x0004
            return r0
        L_0x0004:
            com.miui.maml.data.Variables r1 = r11.getVariables()
            java.lang.String r2 = "condition"
            java.lang.String r2 = r10.getAttribute(r2)
            com.miui.maml.data.Expression r1 = com.miui.maml.data.Expression.build(r1, r2)
            com.miui.maml.data.Variables r2 = r11.getVariables()
            java.lang.String r3 = "delayCondition"
            java.lang.String r3 = r10.getAttribute(r3)
            com.miui.maml.data.Expression r2 = com.miui.maml.data.Expression.build(r2, r3)
            r3 = 0
            java.lang.String r5 = "delay"
            long r5 = com.miui.maml.util.Utils.getAttrAsLong(r10, r5, r3)
            java.lang.String r7 = r10.getNodeName()
            java.lang.String r8 = "Command"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x0047
            java.lang.String r7 = "target"
            java.lang.String r7 = r10.getAttribute(r7)
            java.lang.String r8 = "value"
            java.lang.String r10 = r10.getAttribute(r8)
            com.miui.maml.ActionCommand r10 = create(r11, r7, r10)
        L_0x0044:
            r7 = r10
            goto L_0x0189
        L_0x0047:
            java.lang.String r8 = "VariableCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x0056
            com.miui.maml.ActionCommand$VariableAssignmentCommand r7 = new com.miui.maml.ActionCommand$VariableAssignmentCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0056:
            java.lang.String r8 = "BinderCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x0065
            com.miui.maml.ActionCommand$VariableBinderCommand r7 = new com.miui.maml.ActionCommand$VariableBinderCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0065:
            java.lang.String r8 = "IntentCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x0074
            com.miui.maml.ActionCommand$IntentCommand r7 = new com.miui.maml.ActionCommand$IntentCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0074:
            java.lang.String r8 = "SoundCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x0083
            com.miui.maml.ActionCommand$SoundCommand r7 = new com.miui.maml.ActionCommand$SoundCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0083:
            java.lang.String r8 = "ExternCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x0092
            com.miui.maml.ActionCommand$ExternCommand r7 = new com.miui.maml.ActionCommand$ExternCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0092:
            java.lang.String r8 = "VibrateCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x00a1
            com.miui.maml.VibrateCommand r7 = new com.miui.maml.VibrateCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x00a1:
            java.lang.String r8 = "FrameRateCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x00b0
            com.miui.maml.ActionCommand$FrameRateCommand r7 = new com.miui.maml.ActionCommand$FrameRateCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x00b0:
            java.lang.String r8 = "MethodCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x00bf
            com.miui.maml.ActionCommand$MethodCommand r7 = new com.miui.maml.ActionCommand$MethodCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x00bf:
            java.lang.String r8 = "FieldCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x00ce
            com.miui.maml.ActionCommand$FieldCommand r7 = new com.miui.maml.ActionCommand$FieldCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x00ce:
            java.lang.String r8 = "GraphicsCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x00dd
            com.miui.maml.ActionCommand$GraphicsCommand r7 = new com.miui.maml.ActionCommand$GraphicsCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x00dd:
            java.lang.String r8 = "MultiCommand"
            boolean r8 = r7.equals(r8)
            if (r8 != 0) goto L_0x0184
            java.lang.String r8 = "GroupCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x00ef
            goto L_0x0184
        L_0x00ef:
            java.lang.String r8 = "LoopCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x00fe
            com.miui.maml.ActionCommand$LoopCommand r7 = new com.miui.maml.ActionCommand$LoopCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x00fe:
            java.lang.String r8 = "AnimationCommand"
            boolean r8 = r7.equals(r8)
            if (r8 == 0) goto L_0x010d
            com.miui.maml.ActionCommand$AnimationCommand r7 = new com.miui.maml.ActionCommand$AnimationCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x010d:
            java.lang.String r8 = "ActionCommand"
            boolean r9 = r7.equals(r8)
            if (r9 == 0) goto L_0x011c
            com.miui.maml.ActionCommand$ActionPerformCommand r7 = new com.miui.maml.ActionCommand$ActionPerformCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x011c:
            java.lang.String r9 = "FolmeCommand"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x012a
            com.miui.maml.ActionCommand$FolmeCommand r7 = new com.miui.maml.ActionCommand$FolmeCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x012a:
            java.lang.String r9 = "EaseTypeCommand"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0138
            com.miui.maml.ActionCommand$EaseTypeCommand r7 = new com.miui.maml.ActionCommand$EaseTypeCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0138:
            java.lang.String r9 = "TickListenerCommand"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0146
            com.miui.maml.ActionCommand$TickListenerCommand r7 = new com.miui.maml.ActionCommand$TickListenerCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0146:
            java.lang.String r9 = "FunctionCommand"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0154
            com.miui.maml.ActionCommand$FunctionPerformCommand r7 = new com.miui.maml.ActionCommand$FunctionPerformCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0154:
            java.lang.String r9 = "IfCommand"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0162
            com.miui.maml.ActionCommand$IfCommand r7 = new com.miui.maml.ActionCommand$IfCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0162:
            java.lang.String r9 = "SensorCommand"
            boolean r7 = r7.equals(r9)
            if (r7 == 0) goto L_0x0170
            com.miui.maml.ActionCommand$SensorBinderCommand r7 = new com.miui.maml.ActionCommand$SensorBinderCommand
            r7.<init>(r11, r10)
            goto L_0x0189
        L_0x0170:
            com.miui.maml.ScreenContext r7 = r11.getContext()
            com.miui.maml.ObjectFactory r7 = r7.getObjectFactory(r8)
            com.miui.maml.ObjectFactory$ActionCommandFactory r7 = (com.miui.maml.ObjectFactory.ActionCommandFactory) r7
            if (r7 == 0) goto L_0x0182
            com.miui.maml.ActionCommand r10 = r7.create(r11, r10)
            goto L_0x0044
        L_0x0182:
            r7 = r0
            goto L_0x0189
        L_0x0184:
            com.miui.maml.ActionCommand$MultiCommand r7 = new com.miui.maml.ActionCommand$MultiCommand
            r7.<init>(r11, r10)
        L_0x0189:
            if (r7 != 0) goto L_0x018c
            return r0
        L_0x018c:
            if (r2 == 0) goto L_0x0194
            com.miui.maml.ActionCommand$ConditionCommand r10 = new com.miui.maml.ActionCommand$ConditionCommand
            r10.<init>(r7, r2)
            goto L_0x0195
        L_0x0194:
            r10 = r7
        L_0x0195:
            int r11 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r11 <= 0) goto L_0x019f
            com.miui.maml.ActionCommand$DelayCommand r11 = new com.miui.maml.ActionCommand$DelayCommand
            r11.<init>(r10, r5)
            r10 = r11
        L_0x019f:
            if (r1 == 0) goto L_0x01a7
            com.miui.maml.ActionCommand$ConditionCommand r11 = new com.miui.maml.ActionCommand$ConditionCommand
            r11.<init>(r10, r1)
            r10 = r11
        L_0x01a7:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ActionCommand.create(org.w3c.dom.Element, com.miui.maml.elements.ScreenElement):com.miui.maml.ActionCommand");
    }

    /* access modifiers changed from: protected */
    public abstract void doPerform();

    public void finish() {
    }

    /* access modifiers changed from: protected */
    public final Context getContext() {
        return getScreenContext().mContext;
    }

    /* access modifiers changed from: protected */
    public ScreenElementRoot getRoot() {
        return this.mScreenElement.getRoot();
    }

    /* access modifiers changed from: protected */
    public final ScreenContext getScreenContext() {
        return this.mScreenElement.getContext();
    }

    /* access modifiers changed from: protected */
    public ScreenElement getScreenElement() {
        return this.mScreenElement;
    }

    /* access modifiers changed from: protected */
    public final Variables getVariables() {
        return this.mScreenElement.getVariables();
    }

    public void init() {
    }

    /* access modifiers changed from: protected */
    public boolean isExpressionsValid(Expression[] expressionArr) {
        if (expressionArr != null) {
            int i = 0;
            while (i < expressionArr.length && expressionArr[i] != null) {
                i++;
            }
            if (i == expressionArr.length) {
                return true;
            }
        }
        return false;
    }

    public void pause() {
    }

    public void perform() {
        doPerform();
    }

    public void resume() {
    }
}
