package grphaqladapter.adaptedschemabuilder.assertutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assert {

    private final static Pattern NameRegx = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");


    public static boolean isNullString(String str) {
        return str == null || str.equals("");
    }

    public static boolean isNoNullString(String str) {
        return !isNullString(str);
    }


    public static boolean hasDefaultPublicConstructor(Class cls) {
        try {
            Constructor constructor = cls.getConstructor();
            return Modifier.isPublic(constructor.getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static void isOneFalse(String msg, boolean... conditions) {
        if (conditions == null)
            return;
        for (boolean bool : conditions) {
            if (!bool) return;
        }
        throw new IllegalStateException(msg);
    }

    public static void isAllFalse(String msg, boolean... conditions) {
        if (conditions == null)
            return;
        for (boolean bool : conditions) {
            if (bool) {
                throw new IllegalStateException(msg);
            }
        }
    }

    public static void isOneTrue(String msg, boolean... conditions) {
        if (conditions == null)
            return;
        for (boolean bool : conditions) {
            if (bool) return;
        }
        throw new IllegalStateException(msg);
    }

    public static void isAllTrue(String msg, boolean... conditions) {
        if (conditions == null)
            return;
        for (boolean bool : conditions) {
            if (!bool) {
                throw new IllegalStateException(msg);
            }
        }
    }

    public static void isTrue(boolean condition, String msg) {
        if(!condition) {
            throw new IllegalStateException(msg);
        }
    }

    public static void isFalse(boolean condition, String msg) {
        if(condition) {
            throw new IllegalStateException(msg);
        }
    }

    public static void isNotNull(Object o, String msg) {
        if (o == null)
            throw new NullPointerException(msg);
    }

    public static void isNotNull(Object o) {
        isNotNull(o, "object is null");
    }

    public static void isNull(Object o, String msg) {
        if (o != null)
            throw new IllegalStateException(msg);
    }

    public static void isNull(Object o) {
        isNull(o, "object must be null");
    }

    public static void isNameValid(String name, String msg) {
        Matcher matcher = NameRegx.matcher(name);

        if (!matcher.matches())
            throw new IllegalStateException(msg);
    }

    public static void isNameValid(String name) {
        isNameValid(name, "typeName [" + name + "] not valid - valid pattern is - [_a-zA-Z][_a-zA-Z0-9]*");
    }

    public static void isMethodMemberOfClass(Method m, Class cls, String msg) {
        try {
            Method realMethod = cls.getMethod(m.getName(), m.getParameterTypes());
            if (m.equals(realMethod)) {
                return;
            }
        } catch (NoSuchMethodException e) {

        }

        throw new IllegalStateException(msg);
    }


    public static void isMethodMemberOfClass(Method m, Class cls) {
        isMethodMemberOfClass(m, cls, "method [" + m + "] not a member of class [" + cls + "]");
    }


    public static void isParameterRelatedToMethod(Parameter parameter, Method method, String msg) {
        for (Parameter p : method.getParameters()) {
            if (parameter == p)
                return;
        }

        throw new IllegalStateException(msg);
    }

    public static void isParameterRelatedToMethod(Parameter parameter, Method method) {
        isParameterRelatedToMethod(parameter, method, "parameter [" + parameter + "] not related to method [" + method + "]");
    }


    public static void isEquals(Object o1, Object o2, String msg) {
        if (!Objects.equals(o1, o2))
            throw new IllegalStateException(msg);
    }

    public static void isEquals(Object o1, Object o2) {
        isEquals(o1, o2, "objects not equal [object1:" + o1 + " , object2:" + o2 + "]");
    }

    public static void isNotEquals(Object o1, Object o2, String msg) {
        if (Objects.equals(o1, o2))
            throw new IllegalStateException(msg);
    }

    public static void isNotEquals(Object o1, Object o2) {
        isNotEquals(o1, o2, "objects are equal [object1:" + o1 + " , object2:" + o2 + "]");
    }


    public static void isAValidSetterMethod(Method fieldMethod, Method setterMethod, String msg) {
        if (setterMethod.getParameterCount() != 1 || fieldMethod.getReturnType() != setterMethod.getParameters()[0].getType()) {
            throw new IllegalStateException(msg);
        }
    }

    public static void isAValidSetterMethod(Method fieldMethod, Method setterMethod) {
        isAValidSetterMethod(fieldMethod, setterMethod, "method [" + setterMethod + "] not a valid setter for [" + fieldMethod + "] - valid setter method must contain exactly one parameter equal to return type of field method");
    }

    public static void isModifierValidForAFieldMethod(Method method, String msg) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalStateException(msg);
        }

        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException(msg);
        }

        if (method.getReturnType() == void.class) {
            throw new IllegalStateException(msg);
        }

        if (method.isSynthetic()) {
            throw new IllegalStateException(msg);
        }
    }

    public static void isModifierValidForASetterMethod(Method method, String msg) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalStateException(msg);
        }

        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException(msg);
        }

        if (method.isSynthetic()) {
            throw new IllegalStateException(msg);
        }

    }

    public static void isModifierValidForASetterMethod(Method method) {
        isModifierValidForASetterMethod(method, "just public and none-static methods can be mapped to setters - [method:" + method + "]");
    }

    public static void isModifierValidForAFieldMethod(Method method) {
        isModifierValidForAFieldMethod(method, "just public and none-static methods with return value can be mapped to fields - [method:" + method + "]");
    }

    public static void isPositive(int i, String msg) {
        if (i < 1)
            throw new IllegalStateException(msg);
    }

    public static void isPositive(int i) {
        isPositive(i, "number must be positive");
    }

    public static void isNotPositive(int i, String msg) {
        if (i > 0)
            throw new IllegalStateException(msg);
    }

    public static void isNotPositive(int i) {

        isNotPositive(i, "number cant be positive");
    }

    public static void isNotNegative(int i, String msg) {
        if (i < 0)
            throw new IllegalStateException(msg);
    }

    public static void isNotNegative(int i) {
        isNotNegative(i, "number can not be negative");
    }


    public static void isNullable(Class cls, String msg) {
        if (cls.isPrimitive())
            throw new IllegalStateException(msg);
    }


    public static void isNullable(Class cls) {
        isNullable(cls, "java scalar types can not be nullable [" + cls + "]");
    }

    public static void isModifierValidForATypeClass(Class cls, String msg) {
        if (!Modifier.isPublic(cls.getModifiers()))
            throw new IllegalStateException(msg);
        if (cls.isArray())
            throw new IllegalStateException(msg);

    }

    public static void isModifierValidForATypeClass(Class cls) {
        isModifierValidForATypeClass(cls, "just public modifier classes can map to types [" + cls + "]");
    }

    public static void isModifierValidForAnInputTypeClass(Class cls, String msg) {
        isModifierValidForATypeClass(cls, msg);
        try {
            if (cls.getConstructor() == null
                    || !Modifier.isPublic(cls.getConstructor().getModifiers()))
                throw new IllegalStateException(msg);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(msg);
        }
    }

    public static void isModifierValidForAnInputTypeClass(Class cls) {
        isModifierValidForAnInputTypeClass(cls, "class can not map to input type [" + cls + "]");
    }
}
