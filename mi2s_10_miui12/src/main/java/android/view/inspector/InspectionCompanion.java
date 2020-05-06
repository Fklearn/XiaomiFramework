package android.view.inspector;

public abstract interface InspectionCompanion<T>
{
  public abstract void mapProperties(PropertyMapper paramPropertyMapper);
  
  public abstract void readProperties(T paramT, PropertyReader paramPropertyReader);
  
  public static class UninitializedPropertyMapException
    extends RuntimeException
  {
    public UninitializedPropertyMapException()
    {
      super();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inspector/InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */