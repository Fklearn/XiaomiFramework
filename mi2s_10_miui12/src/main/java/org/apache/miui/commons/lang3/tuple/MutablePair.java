package org.apache.miui.commons.lang3.tuple;

public class MutablePair<L, R>
  extends Pair<L, R>
{
  private static final long serialVersionUID = 4954918890077093841L;
  public L left;
  public R right;
  
  public MutablePair() {}
  
  public MutablePair(L paramL, R paramR)
  {
    this.left = paramL;
    this.right = paramR;
  }
  
  public static <L, R> MutablePair<L, R> of(L paramL, R paramR)
  {
    return new MutablePair(paramL, paramR);
  }
  
  public L getLeft()
  {
    return (L)this.left;
  }
  
  public R getRight()
  {
    return (R)this.right;
  }
  
  public void setLeft(L paramL)
  {
    this.left = paramL;
  }
  
  public void setRight(R paramR)
  {
    this.right = paramR;
  }
  
  public R setValue(R paramR)
  {
    Object localObject = getRight();
    setRight(paramR);
    return (R)localObject;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/tuple/MutablePair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */