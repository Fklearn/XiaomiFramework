package android.view;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface RemotableViewMethod
{
  String asyncImpl() default "";
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/RemotableViewMethod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */