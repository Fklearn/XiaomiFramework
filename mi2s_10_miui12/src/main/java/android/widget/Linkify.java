package android.widget;

import android.telephony.PhoneNumberUtils;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import com.android.i18n.phonenumbers.PhoneNumberMatch;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.util.Patterns;

public class Linkify
{
  public static final int PHONE_NUMBERS = 4;
  private static final int PHONE_NUMBER_MINIMUM_DIGITS = 5;
  private static final Pattern WEB_CHAR_PATTERN = Pattern.compile("[a-zA-Z0-9\\.]");
  public static final int WEB_URLS = 1;
  public static final Linkify.MatchFilter sUrlMatchFilter = android.text.util.Linkify.sUrlMatchFilter;
  
  private static final void gatherLinks(ArrayList<LinkSpec> paramArrayList, CharSequence paramCharSequence, int paramInt1, int paramInt2, Pattern paramPattern, String[] paramArrayOfString, Linkify.MatchFilter paramMatchFilter, Linkify.TransformFilter paramTransformFilter)
  {
    paramPattern = paramPattern.matcher(paramCharSequence.subSequence(paramInt1, paramInt2));
    while (paramPattern.find())
    {
      paramInt2 = paramPattern.start();
      paramInt1 = paramPattern.end();
      if ((paramMatchFilter == null) || (paramMatchFilter.acceptMatch(paramCharSequence, paramInt2, paramInt1)))
      {
        LinkSpec localLinkSpec = new LinkSpec();
        localLinkSpec.url = makeUrl(paramPattern.group(0), paramArrayOfString, paramPattern, paramTransformFilter);
        localLinkSpec.start = paramInt2;
        localLinkSpec.end = paramInt1;
        paramArrayList.add(localLinkSpec);
      }
    }
  }
  
  private static final void gatherTelLinks(ArrayList<LinkSpec> paramArrayList, CharSequence paramCharSequence, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramBoolean)
    {
      i = paramInt1;
      j = paramInt1;
    }
    else
    {
      i = paramInt1 - 1;
      j = paramInt1;
    }
    while ((i >= 0) && (Character.isDigit(paramCharSequence.charAt(i))))
    {
      j = i;
      i--;
    }
    if (paramBoolean) {
      paramInt1 = paramInt2 - 1;
    }
    for (paramInt1 = paramInt2; (paramInt1 >= 0) && (paramInt1 < paramCharSequence.length()) && (Character.isDigit(paramCharSequence.charAt(paramInt1))); paramInt1++) {
      paramInt2 = paramInt1 + 1;
    }
    if (paramInt2 - j < 5) {
      return;
    }
    Iterator localIterator = PhoneNumberUtil.getInstance().findNumbers(paramCharSequence.subSequence(j, paramInt2), Locale.getDefault().getCountry(), PhoneNumberUtil.Leniency.POSSIBLE, Long.MAX_VALUE).iterator();
    while (localIterator.hasNext())
    {
      PhoneNumberMatch localPhoneNumberMatch = (PhoneNumberMatch)localIterator.next();
      paramCharSequence = new LinkSpec();
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("tel:");
      localStringBuilder.append(PhoneNumberUtils.normalizeNumber(localPhoneNumberMatch.rawString()));
      paramCharSequence.url = localStringBuilder.toString();
      paramCharSequence.start = localPhoneNumberMatch.start();
      paramCharSequence.end = localPhoneNumberMatch.end();
      paramArrayList.add(paramCharSequence);
    }
  }
  
  private static final void gatherWebLinks(ArrayList<LinkSpec> paramArrayList, CharSequence paramCharSequence, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramBoolean)
    {
      i = paramInt1;
      j = paramInt1;
    }
    else
    {
      i = paramInt1 - 1;
      j = paramInt1;
    }
    while ((i >= 0) && (isValidChar(paramCharSequence.charAt(i), WEB_CHAR_PATTERN)))
    {
      j = i;
      i--;
    }
    if (paramBoolean) {
      paramInt1 = paramInt2 - 1;
    }
    for (paramInt1 = paramInt2; (paramInt1 >= 0) && (paramInt1 < paramCharSequence.length()) && (isValidChar(paramCharSequence.charAt(paramInt1), WEB_CHAR_PATTERN)); paramInt1++) {
      paramInt2 = paramInt1 + 1;
    }
    Pattern localPattern = Patterns.WEB_URL;
    Linkify.MatchFilter localMatchFilter = sUrlMatchFilter;
    gatherLinks(paramArrayList, paramCharSequence, j, paramInt2, localPattern, new String[] { "http://", "https://", "rtsp://" }, localMatchFilter, null);
  }
  
  public static String getClipboardFistLink(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    paramCharSequence = getLinks(paramCharSequence, paramInt1, paramInt2, paramInt3);
    if ((paramCharSequence != null) && (paramCharSequence.size() > 0) && (paramCharSequence.get(0) != null)) {
      return ((LinkSpec)paramCharSequence.get(0)).url;
    }
    return null;
  }
  
  static final ArrayList<LinkSpec> getLinks(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 != -1) && (paramInt2 != -1) && (paramInt1 <= paramInt2))
    {
      ArrayList localArrayList = new ArrayList();
      boolean bool;
      if (paramInt1 != paramInt2) {
        bool = true;
      } else {
        bool = false;
      }
      if ((paramInt3 & 0x1) != 0) {
        gatherWebLinks(localArrayList, paramCharSequence, paramInt1, paramInt2, bool);
      }
      if ((paramInt3 & 0x4) != 0) {
        gatherTelLinks(localArrayList, paramCharSequence, paramInt1, paramInt2, bool);
      }
      return localArrayList;
    }
    return null;
  }
  
  private static boolean isValidChar(char paramChar, Pattern paramPattern)
  {
    return paramPattern.matcher(String.valueOf(paramChar)).matches();
  }
  
  private static final String makeUrl(String paramString, String[] paramArrayOfString, Matcher paramMatcher, Linkify.TransformFilter paramTransformFilter)
  {
    String str = paramString;
    if (paramTransformFilter != null) {
      str = paramTransformFilter.transformUrl(paramMatcher, paramString);
    }
    int i = 0;
    int k;
    for (int j = 0;; j++)
    {
      k = i;
      paramString = str;
      if (j >= paramArrayOfString.length) {
        break;
      }
      if (str.regionMatches(true, 0, paramArrayOfString[j], 0, paramArrayOfString[j].length()))
      {
        i = 1;
        k = i;
        paramString = str;
        if (str.regionMatches(false, 0, paramArrayOfString[j], 0, paramArrayOfString[j].length())) {
          break;
        }
        paramString = new StringBuilder();
        paramString.append(paramArrayOfString[j]);
        paramString.append(str.substring(paramArrayOfString[j].length()));
        paramString = paramString.toString();
        k = i;
        break;
      }
    }
    paramMatcher = paramString;
    if (k == 0)
    {
      paramMatcher = new StringBuilder();
      paramMatcher.append(paramArrayOfString[0]);
      paramMatcher.append(paramString);
      paramMatcher = paramMatcher.toString();
    }
    return paramMatcher;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Linkify.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */