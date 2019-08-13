package grphaqladapter.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GraphqlField {

    public String fieldName() default "";
    public boolean nullable() default true;
    public boolean inputField() default false;
    public String setter() default "";
}
