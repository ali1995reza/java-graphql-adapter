package grphaqladapter.adaptedschemabuilder.assertutil;

import javax.jws.Oneway;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.channels.ClosedSelectorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assert {

    private final static Pattern NameRegx = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");



    public static boolean isNullString(String str)
    {
        return str==null || str.equals("");
    }

    public static boolean isNoNullString(String str)
    {
        return !isNullString(str);
    }


    public static boolean hasDefaultPublicConstructor(Class cls)
    {
        try {
            Constructor constructor = cls.getConstructor();
            if(!Modifier.isPublic(constructor.getModifiers()))
                return false;
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static void ifConditionTrue(String msg , boolean  ... conditions)
    {
        if(conditions==null)
            return;
        for(boolean bool:conditions)
        {
            if(!bool)return;
        }

        throw new IllegalStateException(msg);
    }

    public static void ifNull(Object o , String msg)
    {
        if(o==null)
            new NullPointerException(msg);
    }

    public static void ifNull(Object o)
    {
        ifNull(o);
    }

    public static void ifNotNull(Object o , String msg)
    {
        if(o!=null)
            throw new IllegalStateException(msg);
    }

    public static void ifNotNull(Object o){

        ifNotNull(o , "object must be null");
    }

    public static void ifNameNotValid(String name , String msg)
    {
        Matcher matcher = NameRegx.matcher(name);

        if(!matcher.matches())
            throw new IllegalStateException(msg);
    }

    public static void ifNameNotValid(String name)
    {
        ifNameNotValid(name , "typeName ["+name+"] not valid - valid pattern is - [_a-zA-Z][_a-zA-Z0-9]*");
    }

    public static void ifMethodNotMemberOfClass(Method m , Class cls , String msg)
    {
        boolean related = false;
        try {
            Method realMethod = cls.getMethod(m.getName() , m.getParameterTypes());
            related = m.equals(realMethod);
        } catch (NoSuchMethodException e) {

        }

        if(!related)
            throw new IllegalStateException(msg);
    }


    public static void ifMethodNotMemberOfClass(Method m , Class cls)
    {
        ifMethodNotMemberOfClass(m , cls , "method ["+m+"] not a member of class ["+cls+"]");
    }


    public static void ifParameterNotRelatedToMethod(Parameter parameter , Method method , String msg)
    {
        for(Parameter p:method.getParameters())
        {
            if(parameter==p)
                return;
        }

        throw new IllegalStateException(msg);
    }

    public static void ifParameterNotRelatedToMethod(Parameter parameter , Method method){
        ifParameterNotRelatedToMethod(parameter, method , "parameter ["+parameter+"] not related to method ["+method+"]");
    }


    public static void ifNotEqual(Object o1 , Object o2 , String msg)
    {
        if(o1==o2)
            return;

        if(!(o1==null?o2.equals(o1):o1.equals(o2)))
            throw new IllegalStateException(msg);
    }

    public static void ifNotEqual(Object o1 , Object o2 )
    {
        ifNotEqual(o1 , o2 , "objects not equal [object1:"+o1+" , object2:"+o2+"]");
    }

    public static void ifEqual(Object o1 , Object o2 , String msg)
    {
        if(o1==o2)

            throw new IllegalStateException(msg);

        if(o1==null?o2.equals(o1):o1.equals(o2))
            throw new IllegalStateException(msg);
    }

    public static void ifEqual(Object o1 , Object o2 )
    {
        ifEqual(o1 , o2 , "objects are equal [object1:"+o1+" , object2:"+o2+"]");
    }


    public static void ifNotAValidSetterMethod(Method fieldMethod , Method setterMethod , String msg)
    {
        if(setterMethod.getParameterCount()!=1 || fieldMethod.getReturnType()!=setterMethod.getParameters()[0].getType())
        {
            throw new IllegalStateException(msg);
        }
    }

    public static void ifNotAValidSetterMethod(Method fieldMethod  , Method setterMethod)
    {
        ifNotAValidSetterMethod(fieldMethod, setterMethod , "method ["+setterMethod+"] not a valid setter for ["+fieldMethod+"] - valid setter method must contain exactly one parameter equal to return type of field method");
    }

    public static void ifModifierNotValidForAFieldMethod(Method method  , String msg)
    {
        if(!Modifier.isPublic(method.getModifiers()))
        {
            throw new IllegalStateException(msg);
        }

        if(Modifier.isStatic(method.getModifiers()))
        {
            throw new IllegalStateException(msg);
        }

        if(method.getReturnType()==void.class)
        {
            throw new IllegalStateException(msg);
        }

        if(method.isSynthetic())
        {
            throw new IllegalStateException(msg);
        }
    }

    public static void ifModifierNotValidForASetterMethod(Method method  , String msg)
    {
        if(!Modifier.isPublic(method.getModifiers()))
        {
            throw new IllegalStateException(msg);
        }

        if(Modifier.isStatic(method.getModifiers()))
        {
            throw new IllegalStateException(msg);
        }

        if(method.isSynthetic())
        {
            throw new IllegalStateException(msg);
        }

    }

    public static void ifModifierNotValidForASetterMethod(Method method)
    {
        ifModifierNotValidForASetterMethod(method , "just public and none-static methods can be mapped to setters - [method:"+method+"]");
    }

    public static void ifModifierNotValidForAFieldMethod(Method method)
    {
        ifModifierNotValidForAFieldMethod(method , "just public and none-static methods with return value can be mapped to fields - [method:"+method+"]");
    }

    public static void ifNotPositive(int i , String msg)
    {
        if(i<1)
            throw new IllegalStateException(msg);
    }

    public static void ifNotPositive(int i)
    {
        ifNotPositive(i , "number must be positive");
    }

    public static void ifPositive(int i , String msg)
    {
        if(i>0)
            throw new IllegalStateException(msg);
    }

    public static void ifPositive(int i)
    {

        ifPositive(i , "number cant be positive");
    }

    public static void ifNegative(int i , String msg)
    {
        if(i<0)
            throw new IllegalStateException(msg);
    }

    public static void ifNegative(int i)
    {
        ifNegative(i , "number can not be negative");
    }


    public static void ifCantBeNullable(Class cls , String msg)
    {
        if(cls.isPrimitive())
            throw new IllegalStateException(msg);
    }


    public static void ifCantBeNullable(Class cls)
    {
        ifCantBeNullable(cls , "java scalar types can not be nullable ["+cls+"]");
    }

    public static void ifModifierNotValidForATypeClass(Class cls , String msg)
    {
        if(!Modifier.isPublic(cls.getModifiers()))
            throw new IllegalStateException(msg);
        if(cls.isArray())
            throw new IllegalStateException(msg);

    }

    public static void ifModifierNotValidForATypeClass(Class cls)
    {
        ifModifierNotValidForATypeClass(cls , "just public modifier classes can map to types ["+cls+"]");
    }

    public static void ifModifierNotValidForAnInputTypeClass(Class cls , String msg)
    {
        ifModifierNotValidForATypeClass(cls ,msg);
        try {
            if(cls.getConstructor()==null
                    || !Modifier.isPublic(cls.getConstructor().getModifiers()))
                throw new IllegalStateException(msg);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(msg);
        }
    }

    public static void ifModifierNotValidForAnInputTypeClass(Class cls)
    {
        ifModifierNotValidForAnInputTypeClass(cls , "class can not map to input type ["+cls+"]");
    }
}
