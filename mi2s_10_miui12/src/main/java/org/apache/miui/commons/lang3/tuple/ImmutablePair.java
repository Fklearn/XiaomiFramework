package org.apache.miui.commons.lang3.tuple;

public final class ImmutablePair<L, R>
  extends Pair<L, R>
{
  private static final long serialVersionUID = 4954918890077093841L;
  public final L left;
  public final R right;
  
  public ImmutablePair(L paramL, R paramR)
  {
    this.left = paramL;
    this.right = paramR;
  }
  
  public static <L, R> ImmutablePair<L, R> of(L paramL, R paramR)
  {
    return new ImmutablePair(paramL, paramR);
  }
  
  public L getLeft()
  {
    return (L)this.left;
  }
  
  public R getRight()
  {
    return (R)this.right;
  }
  
  public R setValue(R paramR)
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/tuple/ImmutablePair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */