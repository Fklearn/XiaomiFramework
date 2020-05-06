package miui.imagefilters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FilterParamType {

    public enum ParamType {
        DEFAULT,
        ICON_SIZE
    }

    ParamType value() default ParamType.DEFAULT;
}
