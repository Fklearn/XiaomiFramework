package com.miui.gamebooster.m;

import android.content.Context;
import android.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.activityutil.h;
import com.miui.activityutil.o;
import com.miui.analytics.AnalyticsUtil;
import com.miui.gamebooster.mutiwindow.f;
import com.xiaomi.stat.MiStat;
import java.util.HashMap;
import java.util.Map;

/* renamed from: com.miui.gamebooster.m.d  reason: case insensitive filesystem */
public class C0373d {

    /* renamed from: com.miui.gamebooster.m.d$a */
    public static class a {
        public static void a(int i) {
            String str = i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? h.f2289a : "black" : "old" : "outdoor" : "movie" : "original";
            HashMap hashMap = new HashMap();
            hashMap.put(MiStat.Event.CLICK, str);
            AnalyticsUtil.recordCountEvent("vtb", "video_box_image_click", hashMap);
        }

        public static void a(int i, int i2) {
            String str = i == 6 ? "movie_vocal" : i == 7 ? "movie_surround" : h.f2289a;
            HashMap hashMap = new HashMap();
            try {
                hashMap.put(str, String.valueOf(i2));
            } catch (Exception unused) {
            }
            AnalyticsUtil.recordCountEvent("vtb", "video_box_music_click", hashMap);
        }

        public static void a(int i, boolean z) {
            String str = i == 8 ? "move" : i == 9 ? "Enhance" : h.f2289a;
            HashMap hashMap = new HashMap();
            try {
                hashMap.put(str, z ? o.f2310b : o.f2309a);
            } catch (Exception unused) {
            }
            AnalyticsUtil.recordCountEvent("vtb", "video_box_pic_click", hashMap);
        }

        public static void a(com.miui.gamebooster.n.c.a aVar) {
            String str;
            if (aVar != null && aVar != com.miui.gamebooster.n.c.a.RECOMMEND_APPS) {
                switch (C0372c.f4476a[aVar.ordinal()]) {
                    case 1:
                        str = "display_style";
                        break;
                    case 2:
                        str = "screen_record";
                        break;
                    case 3:
                        str = "screenshot";
                        break;
                    case 4:
                        str = "listen_book";
                        break;
                    case 5:
                        str = "cast_screen";
                        break;
                    case 6:
                        str = "pic_opt";
                        break;
                    case 7:
                        str = "voice_stronger";
                        break;
                    case 8:
                        str = "recommend_app";
                        break;
                    default:
                        str = h.f2289a;
                        break;
                }
                HashMap hashMap = new HashMap();
                hashMap.put(MiStat.Event.CLICK, str);
                AnalyticsUtil.recordCountEvent("vtb", "video_box_click", hashMap);
            }
        }

        public static void a(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("pkg", str);
            AnalyticsUtil.recordCountEvent("vtb", "video_box_app_click", hashMap);
        }

        public static void a(boolean z) {
            HashMap hashMap = new HashMap();
            hashMap.put(MiStat.Event.CLICK, z ? TtmlNode.LEFT : TtmlNode.RIGHT);
            AnalyticsUtil.recordCountEvent("vtb", "video_box_position", hashMap);
        }

        public static void b(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("pkg", str);
            AnalyticsUtil.recordCountEvent("vtb", "video_box_app_management", hashMap);
        }

        public static void b(boolean z) {
            AnalyticsUtil.recordNumericEvent("vtb", "toggle_video_box_mark", z ? 1 : 0);
        }

        public static void c(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("pkg", str);
            AnalyticsUtil.recordCountEvent("vtb", "video_box_show", hashMap);
        }

        public static void c(boolean z) {
            AnalyticsUtil.recordNumericEvent("vtb", "toggle_video_box", z ? 1 : 0);
        }
    }

    public static void A(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("sound_mode", str);
        hashMap.put("game_name", str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "voicechanger_behavior", hashMap);
    }

    public static void a() {
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_main_page_show");
    }

    public static void a(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "home_show_way", j);
    }

    public static void a(Context context) {
        if (f.d()) {
            AnalyticsUtil.recordNumericEvent("gamebooster", "quick_reply_toggle_new", f.c(context) ? 1 : 0);
        }
    }

    public static void a(String str) {
        AnalyticsUtil.recordCountEvent("gamebooster", str);
    }

    public static void a(String str, int i) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        hashMap.put("wonderful_video_game_ai_count", String.valueOf(i));
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_game_ai_count_record", hashMap);
    }

    public static void a(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_ad_click", hashMap);
    }

    public static void a(String str, String str2, String str3) {
        HashMap hashMap = new HashMap();
        hashMap.put("sound_mode", str);
        hashMap.put("game_name", str2);
        hashMap.put("duration", str3);
        AnalyticsUtil.recordCountEvent("gamebooster", "voicechanger_duration", hashMap);
    }

    public static void a(boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put("game_orientation", z ? "vertical" : "horizontal");
        if (com.miui.securityscan.c.a.f7625a) {
            Log.i("GameBooster.Analy", "trackGameToolBoxShow: params=" + hashMap);
        }
        AnalyticsUtil.recordCountEvent("gamebooster", "game_info", hashMap);
    }

    public static void b() {
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_page_show");
    }

    public static void b(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_antispam_1_fix", j);
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0151  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x015b  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x015d  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x016f  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0187 A[Catch:{ all -> 0x01d4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0139  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0143  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void b(android.content.Context r14) {
        /*
            java.lang.String r0 = "GameBooster.Analy"
            r1 = 0
            boolean r2 = com.miui.gamebooster.m.Z.b(r14, r1)
            r3 = 0
            android.content.ContentResolver r4 = r14.getContentResolver()     // Catch:{ Exception -> 0x0051, all -> 0x004d }
            java.lang.String r5 = "content://com.miui.securitycenter.gamebooster/gamebooster_analytics"
            android.net.Uri r5 = android.net.Uri.parse(r5)     // Catch:{ Exception -> 0x0051, all -> 0x004d }
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0051, all -> 0x004d }
            if (r4 != 0) goto L_0x0020
            miui.util.IOUtils.closeQuietly(r4)
            return
        L_0x0020:
            boolean r5 = r4.moveToFirst()     // Catch:{ Exception -> 0x004b }
            if (r5 == 0) goto L_0x0045
            java.lang.String r5 = "game_toggle_total_history_1"
            int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x004b }
            java.lang.String r5 = r4.getString(r5)     // Catch:{ Exception -> 0x004b }
            boolean r5 = java.lang.Boolean.parseBoolean(r5)     // Catch:{ Exception -> 0x004b }
            java.lang.String r6 = "game_games_num_1"
            int r6 = r4.getColumnIndex(r6)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r6 = r4.getString(r6)     // Catch:{ Exception -> 0x0043 }
            int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0043 }
            goto L_0x0047
        L_0x0043:
            r6 = move-exception
            goto L_0x0054
        L_0x0045:
            r5 = r3
            r6 = r5
        L_0x0047:
            miui.util.IOUtils.closeQuietly(r4)
            goto L_0x005f
        L_0x004b:
            r6 = move-exception
            goto L_0x0053
        L_0x004d:
            r14 = move-exception
            r4 = r1
            goto L_0x01da
        L_0x0051:
            r6 = move-exception
            r4 = r1
        L_0x0053:
            r5 = r3
        L_0x0054:
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01d9 }
            android.util.Log.e(r0, r6)     // Catch:{ all -> 0x01d9 }
            miui.util.IOUtils.closeQuietly(r4)
            r6 = r3
        L_0x005f:
            com.miui.gamebooster.c.a r4 = com.miui.gamebooster.c.a.a((android.content.Context) r14)
            r7 = 1
            r9 = 0
            if (r2 == 0) goto L_0x006b
            r11 = r7
            goto L_0x006c
        L_0x006b:
            r11 = r9
        L_0x006c:
            p(r11)
            r11 = 1
            boolean r4 = r4.k(r11)
            if (r4 == 0) goto L_0x0078
            r12 = r7
            goto L_0x0079
        L_0x0078:
            r12 = r9
        L_0x0079:
            r(r12)
            boolean r4 = com.miui.gamebooster.c.a.e()
            if (r4 == 0) goto L_0x0084
            r12 = r9
            goto L_0x0085
        L_0x0084:
            r12 = r7
        L_0x0085:
            q(r12)
            if (r5 == 0) goto L_0x008d
            java.lang.String r4 = "old_user"
            goto L_0x008f
        L_0x008d:
            java.lang.String r4 = "new_user"
        L_0x008f:
            java.lang.String r12 = "0_usertype"
            s(r12, r4)
            boolean r4 = com.miui.gamebooster.c.a.y(r3)
            if (r4 == 0) goto L_0x00b8
            boolean r4 = com.miui.gamebooster.c.a.o(r11)
            if (r4 == 0) goto L_0x00a2
            r12 = r7
            goto L_0x00a3
        L_0x00a2:
            r12 = r9
        L_0x00a3:
            s(r12)
            boolean r4 = com.miui.gamebooster.c.a.o(r11)
            if (r4 == 0) goto L_0x00b8
            boolean r4 = com.miui.gamebooster.c.a.p(r3)
            if (r4 == 0) goto L_0x00b4
            r12 = r7
            goto L_0x00b5
        L_0x00b4:
            r12 = r9
        L_0x00b5:
            u(r12)
        L_0x00b8:
            boolean r4 = com.miui.gamebooster.c.a.l(r11)
            if (r4 == 0) goto L_0x00c0
            r12 = r7
            goto L_0x00c1
        L_0x00c0:
            r12 = r9
        L_0x00c1:
            d((long) r12)
            boolean r4 = com.miui.gamebooster.c.a.l(r11)
            if (r4 == 0) goto L_0x00d6
            boolean r4 = com.miui.gamebooster.c.a.m(r11)
            if (r4 == 0) goto L_0x00d2
            r12 = r7
            goto L_0x00d3
        L_0x00d2:
            r12 = r9
        L_0x00d3:
            t(r12)
        L_0x00d6:
            boolean r4 = com.miui.gamebooster.c.a.q(r3)
            if (r4 == 0) goto L_0x00de
            r12 = r7
            goto L_0x00df
        L_0x00de:
            r12 = r9
        L_0x00df:
            e((long) r12)
            boolean r4 = com.miui.gamebooster.c.a.d(r11)
            if (r4 == 0) goto L_0x00ea
            r12 = r7
            goto L_0x00eb
        L_0x00ea:
            r12 = r9
        L_0x00eb:
            c((long) r12)
            boolean r4 = com.miui.gamebooster.m.C0388t.m()
            if (r4 == 0) goto L_0x00f9
            boolean r4 = com.miui.gamebooster.c.a.b((boolean) r3)
            goto L_0x00fd
        L_0x00f9:
            boolean r4 = com.miui.gamebooster.c.a.c((boolean) r3)
        L_0x00fd:
            long r12 = (long) r4
            b((long) r12)
            boolean r4 = com.miui.gamebooster.c.a.v(r3)
            if (r4 == 0) goto L_0x0109
            r12 = r7
            goto L_0x010a
        L_0x0109:
            r12 = r9
        L_0x010a:
            m((long) r12)
            boolean r4 = com.miui.gamebooster.c.a.a((boolean) r11)
            if (r4 == 0) goto L_0x0115
            r12 = r7
            goto L_0x0116
        L_0x0115:
            r12 = r9
        L_0x0116:
            g((long) r12)
            boolean r4 = com.miui.gamebooster.c.a.i(r11)
            if (r4 == 0) goto L_0x0121
            r12 = r7
            goto L_0x0122
        L_0x0121:
            r12 = r9
        L_0x0122:
            k((long) r12)
            boolean r4 = com.miui.gamebooster.c.a.e(r11)
            if (r4 == 0) goto L_0x012d
            r11 = r7
            goto L_0x012e
        L_0x012d:
            r11 = r9
        L_0x012e:
            h((long) r11)
            boolean r4 = com.miui.gamebooster.c.a.r(r3)
            if (r4 == 0) goto L_0x0139
            r11 = r7
            goto L_0x013a
        L_0x0139:
            r11 = r9
        L_0x013a:
            f((long) r11)
            boolean r4 = com.miui.gamebooster.c.a.s(r3)
            if (r4 == 0) goto L_0x0145
            r11 = r7
            goto L_0x0146
        L_0x0145:
            r11 = r9
        L_0x0146:
            i((long) r11)
            boolean r4 = com.miui.gamebooster.c.a.u(r3)
            if (r4 == 0) goto L_0x0151
            r11 = r7
            goto L_0x0152
        L_0x0151:
            r11 = r9
        L_0x0152:
            n(r11)
            boolean r4 = com.miui.gamebooster.c.a.t(r3)
            if (r4 == 0) goto L_0x015d
            r11 = r7
            goto L_0x015e
        L_0x015d:
            r11 = r9
        L_0x015e:
            l((long) r11)
            int r4 = com.miui.gamebooster.c.a.b()
            long r11 = (long) r4
            a((long) r11)
            boolean r4 = com.miui.gamebooster.m.C0388t.w()
            if (r4 == 0) goto L_0x017a
            boolean r3 = com.miui.gamebooster.c.a.j(r3)
            if (r3 == 0) goto L_0x0176
            goto L_0x0177
        L_0x0176:
            r7 = r9
        L_0x0177:
            j((long) r7)
        L_0x017a:
            long r3 = (long) r6
            o(r3)
            a((android.content.Context) r14)
            android.database.Cursor r1 = com.miui.gamebooster.provider.a.b(r14)     // Catch:{ all -> 0x01d4 }
            if (r1 == 0) goto L_0x018f
            int r14 = r1.getCount()     // Catch:{ all -> 0x01d4 }
            long r3 = (long) r14     // Catch:{ all -> 0x01d4 }
            v(r3)     // Catch:{ all -> 0x01d4 }
        L_0x018f:
            miui.util.IOUtils.closeQuietly(r1)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            long r3 = com.miui.gamebooster.m.ma.c()
            r14.append(r3)
            java.lang.String r1 = ""
            r14.append(r1)
            java.lang.String r14 = r14.toString()
            m((java.lang.String) r14)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r1 = "t:"
            r14.append(r1)
            r14.append(r2)
            java.lang.String r1 = " th:"
            r14.append(r1)
            r14.append(r5)
            java.lang.String r1 = " p:"
            r14.append(r1)
            java.lang.String r1 = " num:"
            r14.append(r1)
            r14.append(r6)
            java.lang.String r14 = r14.toString()
            android.util.Log.i(r0, r14)
            return
        L_0x01d4:
            r14 = move-exception
            miui.util.IOUtils.closeQuietly(r1)
            throw r14
        L_0x01d9:
            r14 = move-exception
        L_0x01da:
            miui.util.IOUtils.closeQuietly(r4)
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.C0373d.b(android.content.Context):void");
    }

    public static void b(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_ai_record_close", hashMap);
    }

    public static void b(String str, int i) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        hashMap.put("wonderful_video_del_count", String.valueOf(i));
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_del_record", hashMap);
    }

    public static void b(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_sign_in_page", hashMap);
    }

    public static void c() {
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_page_play_show");
    }

    public static void c(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_anti_false_touch_1_fix", j);
    }

    public static void c(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_ai_record_open", hashMap);
    }

    public static void c(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_user_current_state", hashMap);
    }

    public static void d() {
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_turbobox_click");
    }

    public static void d(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_call_auto_handsfree", j);
    }

    public static void d(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("type", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "gtb_opt_exit", hashMap);
    }

    public static void d(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_homepage_action", hashMap);
    }

    public static void e() {
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_turbobox_show");
    }

    public static void e(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_cpubooster_1_fix", j);
    }

    public static void e(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_manual_record_close", hashMap);
    }

    public static void e(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_homepage_sign_in_click", hashMap);
    }

    public static void f(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_auto_bright", j);
    }

    public static void f(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_manual_record_open", hashMap);
    }

    public static void f(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_first_guide_window", hashMap);
    }

    public static void g(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_gamebox_new", j);
    }

    public static void g(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_game_manual_record", hashMap);
    }

    public static void g(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_network_activity_document", hashMap);
    }

    public static void h(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_competition", j);
    }

    public static void h(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("package_name", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "quick_reply_add_pkg", hashMap);
    }

    public static void h(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_network_activity_window", hashMap);
    }

    public static void i(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_eye_shield", j);
    }

    public static void i(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("gamebooster_module_click", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_gamebox_click_new", hashMap);
    }

    public static void i(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "home_game_network_activity_window", hashMap);
    }

    public static void j(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_gwsd", j);
    }

    public static void j(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("gamebooster_module_show", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_gamebox_click_new", hashMap);
        k(MimeTypes.BASE_TYPE_VIDEO);
    }

    public static void j(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_network_speed_due", hashMap);
    }

    public static void k(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_optimization_new", j);
    }

    public static void k(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("gamebooster_module_show", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "gb_result_action", hashMap);
    }

    public static void k(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "home_game_network_speed_due", hashMap);
    }

    public static void l(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_pull_notification_bar", j);
    }

    public static void l(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("sound_mode", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "voicechager_mode", hashMap);
    }

    public static void l(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_network_speed_free", hashMap);
    }

    public static void m(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_sign_in", j);
    }

    public static void m(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("duration", str);
        AnalyticsUtil.recordCountEvent("gamebooster", "voicechanger_day_duration", hashMap);
        ma.b(0);
    }

    public static void m(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "home_game_network_speed_free", hashMap);
    }

    public static void n(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_three_finger", j);
    }

    public static void n(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_network_speed_open", hashMap);
    }

    public static void o(long j) {
        AnalyticsUtil.recordCalculateEvent("gamebooster", "game_games_num_1", j, (Map<String, String>) null);
    }

    public static void o(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_network_speed_overdue", hashMap);
    }

    public static void p(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "create_game_icon", j);
    }

    public static void p(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "home_game_network_speed_overdue", hashMap);
    }

    public static void q(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_has_open_gamebooster", j);
    }

    public static void q(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_noti_show_click", hashMap);
    }

    public static void r(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_total_switch", j);
    }

    public static void r(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_wlan_open_remind", hashMap);
    }

    public static void s(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_network_speed", j);
    }

    public static void s(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_toggle_total_history_1", hashMap);
    }

    public static void t(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_silent_mode", j);
    }

    public static void t(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        hashMap.put("wonderful_video_game_page", str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_play", hashMap);
    }

    public static void u(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "game_toggle_wlan", j);
    }

    public static void u(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_4d_feel_new", hashMap);
    }

    public static void v(long j) {
        AnalyticsUtil.recordCalculateEvent("gamebooster", "quick_reply_contact_number", j);
    }

    public static void v(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_screen_HDR_new", hashMap);
    }

    public static void w(long j) {
        AnalyticsUtil.recordNumericEvent("gamebooster", "gb_main_stay_time", j);
    }

    public static void w(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_inhibitory_range_new", hashMap);
    }

    public static void x(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_sensitivity_new", hashMap);
    }

    public static void y(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "game_smooth_new", hashMap);
    }

    public static void z(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("wonderful_video_game_pkg", str);
        hashMap.put("wonderful_video_game_page", str2);
        AnalyticsUtil.recordCountEvent("gamebooster", "wonderful_video_save", hashMap);
    }
}
