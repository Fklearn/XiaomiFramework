package miui.util;

public final class ObjectReference<T>
{
  private final T mObject;
  
  public ObjectReference(T paramT)
  {
    this.mObject = paramT;
  }
  
  public final T get()
  {
    return (T)this.mObject;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ObjectReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */