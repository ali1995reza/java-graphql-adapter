package grphaqladapter.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GraphqlField {

    String fieldName() default "";

    boolean nullable() default true;

    boolean inputField() default false;

    String setter() default "";
}
