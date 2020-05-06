package android.view.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class IntFlagMapping
{
  private final List<Flag> mFlags = new ArrayList();
  
  public void add(int paramInt1, int paramInt2, String paramString)
  {
    this.mFlags.add(new Flag(paramInt1, paramInt2, paramString, null));
  }
  
  public Set<String> get(int paramInt)
  {
    HashSet localHashSet = new HashSet(this.mFlags.size());
    Iterator localIterator = this.mFlags.iterator();
    while (localIterator.hasNext())
    {
      Flag localFlag = (Flag)localIterator.next();
      if (localFlag.isEnabledFor(paramInt)) {
        localHashSet.add(localFlag.mName);
      }
    }
    return Collections.unmodifiableSet(localHashSet);
  }
  
  private static final class Flag
  {
    private final int mMask;
    private final String mName;
    private final int mTarget;
    
    private Flag(int paramInt1, int paramInt2, String paramString)
    {
      this.mTarget = paramInt2;
      this.mMask = paramInt1;
      this.mName = ((String)Objects.requireNonNull(paramString));
    }
    
    private boolean isEnabledFor(int paramInt)
    {
      boolean bool;
      if ((this.mMask & paramInt) == this.mTarget) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inspector/IntFlagMapping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */