package android.view.inspector;

public abstract interface InspectionCompanionProvider
{
  public abstract <T> InspectionCompanion<T> provide(Class<T> paramClass);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inspector/InspectionCompanionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */