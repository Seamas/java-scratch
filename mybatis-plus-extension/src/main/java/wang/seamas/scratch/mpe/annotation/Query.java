package wang.seamas.scratch.mpe.annotation;


import wang.seamas.scratch.mpe.enums.QueryEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Query {

    QueryEnum value() default QueryEnum.EQ;
    String column() default "";
}
