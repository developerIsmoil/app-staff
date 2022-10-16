package ai.ecma.appstaff.aop.annotation;

import ai.ecma.appstaff.enums.PermissionEnum;

import java.lang.annotation.*;


@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface CheckAuth {
    PermissionEnum[] permission() default {};
}
