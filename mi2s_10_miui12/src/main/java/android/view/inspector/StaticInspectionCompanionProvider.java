package android.view.inspector;

public class StaticInspectionCompanionProvider
  implements InspectionCompanionProvider
{
  private static final String COMPANION_SUFFIX = "$InspectionCompanion";
  
  public <T> InspectionCompanion<T> provide(Class<T> paramClass)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramClass.getName());
    ((StringBuilder)localObject).append("$InspectionCompanion");
    localObject = ((StringBuilder)localObject).toString();
    try
    {
      paramClass = paramClass.getClassLoader().loadClass((String)localObject);
      if (InspectionCompanion.class.isAssignableFrom(paramClass))
      {
        paramClass = (InspectionCompanion)paramClass.newInstance();
        return paramClass;
      }
      return null;
    }
    catch (InstantiationException paramClass)
    {
      paramClass = paramClass.getCause();
      if (!(paramClass instanceof RuntimeException))
      {
        if ((paramClass instanceof Error)) {
          throw ((Error)paramClass);
        }
        throw new RuntimeException(paramClass);
      }
      throw ((RuntimeException)paramClass);
    }
    catch (IllegalAccessException paramClass)
    {
      throw new RuntimeException(paramClass);
    }
    catch (ClassNotFoundException paramClass) {}
    return null;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inspector/StaticInspectionCompanionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */