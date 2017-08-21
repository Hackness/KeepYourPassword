package manager.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Hack
 * Date: 20.08.2017 6:06
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {
    String value();
    double min() default 0; //TODO
    double max() default Long.MAX_VALUE; //TODO
}
