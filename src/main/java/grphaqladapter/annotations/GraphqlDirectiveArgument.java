package grphaqladapter.annotations;

import grphaqladapter.annotations.interfaces.ValueParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GraphqlDirectiveArgument {

    String name() default "";

    Class type() default Void.class;

    int dimensions() default -1;

    boolean nullable() default true;

    Class<? extends ValueParser> valueParser() default ValueParser.DefaultParser.class;
}
