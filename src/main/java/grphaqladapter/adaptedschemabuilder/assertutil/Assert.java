package grphaqladapter.adaptedschemabuilder.assertutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class Assert {

    @Deprecated
    public static boolean hasDefaultPublicConstructor(Class cls) {
        try {
            Constructor constructor = cls.getConstructor();
            return Modifier.isPublic(constructor.getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static <T extends Throwable> void isOneOrMoreFalse(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (!bool) return;
        }
        throw exception;
    }

    public static <T extends Throwable> void isExactlyOneFalse(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        boolean flag = false;
        for (boolean bool : conditions) {
            if (!bool && flag) {
                throw exception;
            } else if (!bool) {
                flag = true;
            }
        }
        if (!flag) {
            throw exception;
        }
    }

    public static <T extends Throwable> void isAllFalse(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (bool) {
                throw exception;
            }
        }
    }

    public static <T extends Throwable> void isOneOrMoreTrue(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (bool) return;
        }
        throw exception;
    }

    public static <T extends Throwable> void isExactlyOneTrue(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        boolean flag = false;
        for (boolean bool : conditions) {
            if (bool && flag) {
                throw exception;
            } else if (bool) {
                flag = true;
            }
        }
        if (!flag) {
            throw exception;
        }
    }

    public static <T extends Throwable> void isAllTrue(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (!bool) {
                throw exception;
            }
        }
    }

    public static <T extends Throwable> void isTrue(boolean condition, T exception) throws T {
        if (!condition) {
            throw exception;
        }
    }

    public static <T extends Throwable> void isFalse(boolean condition, T exception) throws T {
        if (condition) {
            throw exception;
        }
    }

    public static <T extends Throwable> void isNotNull(Object o, T exception) throws T {
        if (o == null)
            throw exception;
    }

    public static void isNotNull(Object o) {
        isNotNull(o, new NullPointerException("object is null"));
    }

    public static <T extends Throwable> void isNull(Object o, T exception) throws T {
        if (o != null)
            throw exception;
    }

    public static void isNull(Object o) {
        isNull(o, new IllegalStateException("object must be null"));
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


    public static <T extends Throwable> void isEquals(Object o1, Object o2, T exception) throws T {
        if (!Objects.equals(o1, o2))
            throw exception;
    }

    public static void isEquals(Object o1, Object o2) {
        isEquals(o1, o2, new IllegalStateException("objects not equal [object1:" + o1 + " , object2:" + o2 + "]"));
    }

    public static <T extends Throwable> void isNotEquals(Object o1, Object o2, T exception) throws T {
        if (Objects.equals(o1, o2))
            throw exception;
    }

    public static void isNotEquals(Object o1, Object o2) {
        isNotEquals(o1, o2, new IllegalStateException("objects are equal [object1:" + o1 + " , object2:" + o2 + "]"));
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

    public static <T extends Throwable> void isNotNegative(int i, T exception) throws T {
        if (i < 0)
            throw exception;
    }

    public static void isNotNegative(int i) {
        isNotNegative(i, new IllegalStateException("number can not be negative"));
    }
}
