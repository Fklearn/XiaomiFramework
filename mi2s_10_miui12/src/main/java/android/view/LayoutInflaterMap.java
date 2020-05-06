package android.view;

import android.content.Context;
import android.util.SparseArray;
import miui.os.Environment;
import miui.util.ResourceMapper;

public class LayoutInflaterMap
{
  private static volatile SparseArray<Integer> sLayoutMap;
  private static final int[] sLayoutPairs = { 285933579, 285933608 };
  
  private static void buildLayoutMap(Context paramContext)
  {
    if (sLayoutMap != null) {
      return;
    }
    try
    {
      if (sLayoutMap == null)
      {
        SparseArray localSparseArray = new android/util/SparseArray;
        localSparseArray.<init>();
        for (int i = 0; i < sLayoutPairs.length; i += 2)
        {
          int j;
          if (needResolveReference(sLayoutPairs[i])) {
            j = ResourceMapper.resolveReference(paramContext.getResources(), sLayoutPairs[i]);
          } else {
            j = sLayoutPairs[i];
          }
          localSparseArray.put(j, Integer.valueOf(sLayoutPairs[(i + 1)]));
        }
        sLayoutMap = localSparseArray;
      }
      return;
    }
    finally {}
  }
  
  static int getResourceId(Context paramContext, int paramInt)
  {
    int i = paramInt;
    int j = i;
    if (Environment.isUsingMiui(paramContext))
    {
      buildLayoutMap(paramContext);
      paramContext = (Integer)sLayoutMap.get(paramInt);
      j = i;
      if (paramContext != null) {
        j = paramContext.intValue();
      }
    }
    return j;
  }
  
  private static boolean needResolveReference(int paramInt)
  {
    boolean bool;
    if ((0xFF000000 & paramInt) != 16777216) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/LayoutInflaterMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */