package grphaqladapter.adaptedschemabuilder.utils;

public class TypoUtils {

    public static boolean isOneOf(Class clazz, Class... others) {
        for (Class other : others) {
            if (clazz == other) {
                return true;
            }
        }
        return false;
    }
}
