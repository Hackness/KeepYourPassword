package ru.hackness.KeepYourPassword.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Hack
 * Date: 20.08.2017 6:14
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalConfig {
    String value() default "";
}
