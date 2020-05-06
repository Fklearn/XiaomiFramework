package miui.telephony;

import android.content.Intent;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.telephony.phonenumber.Prefix;

public class TelephonyUtils {
    private static final String LOG_TAG = "TelephonyUtils";
    private static final HashMap<String, String> sNonRoamingMap = new HashMap<>();

    private TelephonyUtils() {
    }

    static {
        sNonRoamingMap.put("40401", "India");
        sNonRoamingMap.put("40402", "India");
        sNonRoamingMap.put("40403", "India");
        sNonRoamingMap.put("40404", "India");
        sNonRoamingMap.put("40405", "India");
        sNonRoamingMap.put("40407", "India");
        sNonRoamingMap.put("40409", "India");
        sNonRoamingMap.put("40410", "India");
        sNonRoamingMap.put("40411", "India");
        sNonRoamingMap.put("40412", "India");
        sNonRoamingMap.put("40413", "India");
        sNonRoamingMap.put("40414", "India");
        sNonRoamingMap.put("40415", "India");
        sNonRoamingMap.put("40416", "India");
        sNonRoamingMap.put("40417", "India");
        sNonRoamingMap.put("40418", "India");
        sNonRoamingMap.put("40419", "India");
        sNonRoamingMap.put("40420", "India");
        sNonRoamingMap.put("40421", "India");
        sNonRoamingMap.put("40422", "India");
        sNonRoamingMap.put("40424", "India");
        sNonRoamingMap.put("40425", "India");
        sNonRoamingMap.put("40427", "India");
        sNonRoamingMap.put("40428", "India");
        sNonRoamingMap.put("40429", "India");
        sNonRoamingMap.put("40430", "India");
        sNonRoamingMap.put("40431", "India");
        sNonRoamingMap.put("40434", "India");
        sNonRoamingMap.put("40436", "India");
        sNonRoamingMap.put("40437", "India");
        sNonRoamingMap.put("40438", "India");
        sNonRoamingMap.put("40440", "India");
        sNonRoamingMap.put("40441", "India");
        sNonRoamingMap.put("40442", "India");
        sNonRoamingMap.put("40443", "India");
        sNonRoamingMap.put("40444", "India");
        sNonRoamingMap.put("40445", "India");
        sNonRoamingMap.put("40446", "India");
        sNonRoamingMap.put("40448", "India");
        sNonRoamingMap.put("40449", "India");
        sNonRoamingMap.put("40450", "India");
        sNonRoamingMap.put("40451", "India");
        sNonRoamingMap.put("40452", "India");
        sNonRoamingMap.put("40453", "India");
        sNonRoamingMap.put("40454", "India");
        sNonRoamingMap.put("40455", "India");
        sNonRoamingMap.put("40456", "India");
        sNonRoamingMap.put("40457", "India");
        sNonRoamingMap.put("40458", "India");
        sNonRoamingMap.put("40459", "India");
        sNonRoamingMap.put("40460", "India");
        sNonRoamingMap.put("40462", "India");
        sNonRoamingMap.put("40464", "India");
        sNonRoamingMap.put("40466", "India");
        sNonRoamingMap.put("40467", "India");
        sNonRoamingMap.put("40468", "India");
        sNonRoamingMap.put("40469", "India");
        sNonRoamingMap.put("40470", "India");
        sNonRoamingMap.put("40471", "India");
        sNonRoamingMap.put("40472", "India");
        sNonRoamingMap.put("40473", "India");
        sNonRoamingMap.put("40474", "India");
        sNonRoamingMap.put("40475", "India");
        sNonRoamingMap.put("40476", "India");
        sNonRoamingMap.put("40477", "India");
        sNonRoamingMap.put("40478", "India");
        sNonRoamingMap.put("40479", "India");
        sNonRoamingMap.put("40480", "India");
        sNonRoamingMap.put("40481", "India");
        sNonRoamingMap.put("40482", "India");
        sNonRoamingMap.put("40483", "India");
        sNonRoamingMap.put("40484", "India");
        sNonRoamingMap.put("40485", "India");
        sNonRoamingMap.put("40486", "India");
        sNonRoamingMap.put("40487", "India");
        sNonRoamingMap.put("40488", "India");
        sNonRoamingMap.put("40489", "India");
        sNonRoamingMap.put("40490", "India");
        sNonRoamingMap.put("40491", "India");
        sNonRoamingMap.put("40492", "India");
        sNonRoamingMap.put("40493", "India");
        sNonRoamingMap.put("40494", "India");
        sNonRoamingMap.put("40495", "India");
        sNonRoamingMap.put("40496", "India");
        sNonRoamingMap.put("40497", "India");
        sNonRoamingMap.put("40498", "India");
        sNonRoamingMap.put("40501", "India");
        sNonRoamingMap.put("40503", "India");
        sNonRoamingMap.put("40504", "India");
        sNonRoamingMap.put("40505", "India");
        sNonRoamingMap.put("40506", "India");
        sNonRoamingMap.put("40507", "India");
        sNonRoamingMap.put("40508", "India");
        sNonRoamingMap.put("40509", "India");
        sNonRoamingMap.put("40510", "India");
        sNonRoamingMap.put("40511", "India");
        sNonRoamingMap.put("40512", "India");
        sNonRoamingMap.put("40513", "India");
        sNonRoamingMap.put("40514", "India");
        sNonRoamingMap.put("40515", "India");
        sNonRoamingMap.put("40517", "India");
        sNonRoamingMap.put("40518", "India");
        sNonRoamingMap.put("40519", "India");
        sNonRoamingMap.put("40520", "India");
        sNonRoamingMap.put("40521", "India");
        sNonRoamingMap.put("40522", "India");
        sNonRoamingMap.put("40523", "India");
        sNonRoamingMap.put("40525", "India");
        sNonRoamingMap.put("40526", "India");
        sNonRoamingMap.put("40527", "India");
        sNonRoamingMap.put("40528", "India");
        sNonRoamingMap.put("40529", "India");
        sNonRoamingMap.put("40530", "India");
        sNonRoamingMap.put("40531", "India");
        sNonRoamingMap.put("40532", "India");
        sNonRoamingMap.put("40533", "India");
        sNonRoamingMap.put("40534", "India");
        sNonRoamingMap.put("40535", "India");
        sNonRoamingMap.put("40536", "India");
        sNonRoamingMap.put("40537", "India");
        sNonRoamingMap.put("40538", "India");
        sNonRoamingMap.put("40539", "India");
        sNonRoamingMap.put("40541", "India");
        sNonRoamingMap.put("40542", "India");
        sNonRoamingMap.put("40543", "India");
        sNonRoamingMap.put("40544", "India");
        sNonRoamingMap.put("40545", "India");
        sNonRoamingMap.put("40546", "India");
        sNonRoamingMap.put("40547", "India");
        sNonRoamingMap.put("40551", "India");
        sNonRoamingMap.put("40552", "India");
        sNonRoamingMap.put("40553", "India");
        sNonRoamingMap.put("40554", "India");
        sNonRoamingMap.put("40555", "India");
        sNonRoamingMap.put("40556", "India");
        sNonRoamingMap.put("40566", "India");
        sNonRoamingMap.put("40570", "India");
        sNonRoamingMap.put("405750", "India");
        sNonRoamingMap.put("405751", "India");
        sNonRoamingMap.put("405752", "India");
        sNonRoamingMap.put("405753", "India");
        sNonRoamingMap.put("405754", "India");
        sNonRoamingMap.put("405755", "India");
        sNonRoamingMap.put("405756", "India");
        sNonRoamingMap.put("405799", "India");
        sNonRoamingMap.put("405800", "India");
        sNonRoamingMap.put("405801", "India");
        sNonRoamingMap.put("405802", "India");
        sNonRoamingMap.put("405803", "India");
        sNonRoamingMap.put("405804", "India");
        sNonRoamingMap.put("405805", "India");
        sNonRoamingMap.put("405806", "India");
        sNonRoamingMap.put("405807", "India");
        sNonRoamingMap.put("405808", "India");
        sNonRoamingMap.put("405809", "India");
        sNonRoamingMap.put("405810", "India");
        sNonRoamingMap.put("405811", "India");
        sNonRoamingMap.put("405812", "India");
        sNonRoamingMap.put("405819", "India");
        sNonRoamingMap.put("405818", "India");
        sNonRoamingMap.put("405820", "India");
        sNonRoamingMap.put("405821", "India");
        sNonRoamingMap.put("405822", "India");
        sNonRoamingMap.put("405824", "India");
        sNonRoamingMap.put("405827", "India");
        sNonRoamingMap.put("405834", "India");
        sNonRoamingMap.put("405844", "India");
        sNonRoamingMap.put("405845", "India");
        sNonRoamingMap.put("405846", "India");
        sNonRoamingMap.put("405847", "India");
        sNonRoamingMap.put("405848", "India");
        sNonRoamingMap.put("405849", "India");
        sNonRoamingMap.put("405850", "India");
        sNonRoamingMap.put("405851", "India");
        sNonRoamingMap.put("405852", "India");
        sNonRoamingMap.put("405853", "India");
        sNonRoamingMap.put("405854", "India");
        sNonRoamingMap.put("405855", "India");
        sNonRoamingMap.put("405856", "India");
        sNonRoamingMap.put("405857", "India");
        sNonRoamingMap.put("405858", "India");
        sNonRoamingMap.put("405859", "India");
        sNonRoamingMap.put("405860", "India");
        sNonRoamingMap.put("405861", "India");
        sNonRoamingMap.put("405862", "India");
        sNonRoamingMap.put("405863", "India");
        sNonRoamingMap.put("405864", "India");
        sNonRoamingMap.put("405865", "India");
        sNonRoamingMap.put("405866", "India");
        sNonRoamingMap.put("405867", "India");
        sNonRoamingMap.put("405868", "India");
        sNonRoamingMap.put("405869", "India");
        sNonRoamingMap.put("405870", "India");
        sNonRoamingMap.put("405871", "India");
        sNonRoamingMap.put("405872", "India");
        sNonRoamingMap.put("405873", "India");
        sNonRoamingMap.put("405874", "India");
        sNonRoamingMap.put("405875", "India");
        sNonRoamingMap.put("405880", "India");
        sNonRoamingMap.put("405881", "India");
        sNonRoamingMap.put("405908", "India");
        sNonRoamingMap.put("405909", "India");
        sNonRoamingMap.put("405910", "India");
        sNonRoamingMap.put("405911", "India");
        sNonRoamingMap.put("405912", "India");
        sNonRoamingMap.put("405913", "India");
        sNonRoamingMap.put("405914", "India");
        sNonRoamingMap.put("405917", "India");
        sNonRoamingMap.put("405927", "India");
        sNonRoamingMap.put("405929", "India");
        sNonRoamingMap.put("40475", "India");
        sNonRoamingMap.put("40451", "India");
        sNonRoamingMap.put("40458", "India");
        sNonRoamingMap.put("40481", "India");
        sNonRoamingMap.put("40474", "India");
        sNonRoamingMap.put("40438", "India");
        sNonRoamingMap.put("40457", "India");
        sNonRoamingMap.put("40480", "India");
        sNonRoamingMap.put("40473", "India");
        sNonRoamingMap.put("40434", "India");
        sNonRoamingMap.put("40466", "India");
        sNonRoamingMap.put("40455", "India");
        sNonRoamingMap.put("40472", "India");
        sNonRoamingMap.put("40477", "India");
        sNonRoamingMap.put("40464", "India");
        sNonRoamingMap.put("40454", "India");
        sNonRoamingMap.put("40471", "India");
        sNonRoamingMap.put("40476", "India");
        sNonRoamingMap.put("40462", "India");
        sNonRoamingMap.put("40453", "India");
        sNonRoamingMap.put("40459", "India");
    }

    public static boolean isOperatorConsideredNonRoaming(String oldCarrier, String newCarrier, String simCarrier) {
        if (TextUtils.isEmpty(newCarrier) || TextUtils.isEmpty(simCarrier)) {
            return false;
        }
        Rlog.i(LOG_TAG, "isOperatorConsideredNonRoaming for " + newCarrier);
        if ((newCarrier.startsWith("404") || newCarrier.startsWith("405")) && (simCarrier.startsWith("404") || simCarrier.startsWith("405"))) {
            return true;
        }
        return false;
    }

    public static void putDialConferenceExtra(Intent intent, boolean isDialConference) {
        if (intent != null) {
            intent.putExtra(TelephonyConstants.EXTRA_DIAL_CONFERENCE_URI, isDialConference);
        } else {
            Rlog.e(LOG_TAG, "putDialConferenceExtra intent==null");
        }
    }

    public static void putVideoStateExtra(Intent intent, int videoState) {
        if (intent != null) {
            intent.putExtra(TelephonyConstants.EXTRA_START_CALL_WITH_VIDEO_STATE, videoState);
        } else {
            Rlog.e(LOG_TAG, "putVideoStateExtra intent==null");
        }
    }

    public static String pii(String original) {
        int length = original == null ? 0 : original.length();
        if (length == 0) {
            return Prefix.EMPTY;
        }
        if (length >= 15) {
            return pii(original, 6, 2);
        }
        if (length >= 11) {
            return pii(original, 2, 2);
        }
        if (length >= 6) {
            return pii(original, 0, 2);
        }
        if (length >= 2) {
            return pii(original, 0, 1);
        }
        return pii(original, 0, 0);
    }

    public static String pii(CharSequence original, int beginNum, int endNum) {
        int len = original == null ? 0 : original.length();
        if (len == 0) {
            return Prefix.EMPTY;
        }
        StringBuilder builder = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            builder.append((i < beginNum || i >= len - endNum) ? original.charAt(i) : 'x');
            i++;
        }
        return builder.toString();
    }

    public static String piiIP(String original) {
        if (TextUtils.isEmpty(original)) {
            return original;
        }
        Matcher matcher = Pattern.compile("(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}" + "|" + "(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}" + "|" + "((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)").matcher(original);
        while (matcher.find()) {
            try {
                int length = 0;
                String str = matcher.group(0);
                if (str != null) {
                    length = str.length();
                }
                if (length != 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < length - 1; i++) {
                        char nextChar = str.charAt(i + 1);
                        if (nextChar != '.') {
                            if (nextChar != ':') {
                                builder.append(str.charAt(i));
                            }
                        }
                        builder.append('x');
                    }
                    builder.append('x');
                    original = original.replace(str, builder.toString());
                }
            } catch (Exception e) {
                Rlog.e(LOG_TAG, "piiIP e: " + e);
            }
        }
        return original;
    }
}
