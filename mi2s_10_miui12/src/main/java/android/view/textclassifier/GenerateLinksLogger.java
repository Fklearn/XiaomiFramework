package android.view.textclassifier;

import android.metrics.LogMaker;
import android.util.ArrayMap;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.Preconditions;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class GenerateLinksLogger
{
  private static final String LOG_TAG = "GenerateLinksLogger";
  private static final String ZERO = "0";
  private final MetricsLogger mMetricsLogger;
  private final Random mRng;
  private final int mSampleRate;
  
  public GenerateLinksLogger(int paramInt)
  {
    this.mSampleRate = paramInt;
    this.mRng = new Random(System.nanoTime());
    this.mMetricsLogger = new MetricsLogger();
  }
  
  @VisibleForTesting
  public GenerateLinksLogger(int paramInt, MetricsLogger paramMetricsLogger)
  {
    this.mSampleRate = paramInt;
    this.mRng = new Random(System.nanoTime());
    this.mMetricsLogger = paramMetricsLogger;
  }
  
  private static void debugLog(LogMaker paramLogMaker)
  {
    if (!Log.ENABLE_FULL_LOGGING) {
      return;
    }
    String str1 = Objects.toString(paramLogMaker.getTaggedData(1319), "");
    String str2 = Objects.toString(paramLogMaker.getTaggedData(1318), "ANY_ENTITY");
    int i = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1316), "0"));
    int j = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1317), "0"));
    int k = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1315), "0"));
    int m = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1314), "0"));
    Log.v("GenerateLinksLogger", String.format(Locale.US, "%s:%s %d links (%d/%d chars) %dms %s", new Object[] { str1, str2, Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m), paramLogMaker.getPackageName() }));
  }
  
  private boolean shouldLog()
  {
    int i = this.mSampleRate;
    boolean bool = true;
    if (i <= 1) {
      return true;
    }
    if (this.mRng.nextInt(i) != 0) {
      bool = false;
    }
    return bool;
  }
  
  private void writeStats(String paramString1, String paramString2, String paramString3, LinkifyStats paramLinkifyStats, CharSequence paramCharSequence, long paramLong)
  {
    paramString1 = new LogMaker(1313).setPackageName(paramString2).addTaggedData(1319, paramString1).addTaggedData(1316, Integer.valueOf(paramLinkifyStats.mNumLinks)).addTaggedData(1317, Integer.valueOf(paramLinkifyStats.mNumLinksTextLength)).addTaggedData(1315, Integer.valueOf(paramCharSequence.length())).addTaggedData(1314, Long.valueOf(paramLong));
    if (paramString3 != null) {
      paramString1.addTaggedData(1318, paramString3);
    }
    this.mMetricsLogger.write(paramString1);
    debugLog(paramString1);
  }
  
  public void logGenerateLinks(CharSequence paramCharSequence, TextLinks paramTextLinks, String paramString, long paramLong)
  {
    Preconditions.checkNotNull(paramCharSequence);
    Preconditions.checkNotNull(paramTextLinks);
    Preconditions.checkNotNull(paramString);
    if (!shouldLog()) {
      return;
    }
    Object localObject1 = new LinkifyStats(null);
    Object localObject2 = new ArrayMap();
    Iterator localIterator = paramTextLinks.getLinks().iterator();
    while (localIterator.hasNext())
    {
      TextLinks.TextLink localTextLink = (TextLinks.TextLink)localIterator.next();
      if (localTextLink.getEntityCount() != 0)
      {
        paramTextLinks = localTextLink.getEntity(0);
        if ((paramTextLinks != null) && (!"other".equals(paramTextLinks)) && (!"".equals(paramTextLinks)))
        {
          ((LinkifyStats)localObject1).countLink(localTextLink);
          ((LinkifyStats)((Map)localObject2).computeIfAbsent(paramTextLinks, _..Lambda.GenerateLinksLogger.vmbT_h7MLlbrIm0lJJwA_eHQhXk.INSTANCE)).countLink(localTextLink);
        }
      }
    }
    paramTextLinks = UUID.randomUUID().toString();
    writeStats(paramTextLinks, paramString, null, (LinkifyStats)localObject1, paramCharSequence, paramLong);
    localObject1 = ((Map)localObject2).entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      writeStats(paramTextLinks, paramString, (String)((Map.Entry)localObject2).getKey(), (LinkifyStats)((Map.Entry)localObject2).getValue(), paramCharSequence, paramLong);
    }
  }
  
  private static final class LinkifyStats
  {
    int mNumLinks;
    int mNumLinksTextLength;
    
    void countLink(TextLinks.TextLink paramTextLink)
    {
      this.mNumLinks += 1;
      this.mNumLinksTextLength += paramTextLink.getEnd() - paramTextLink.getStart();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/GenerateLinksLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */