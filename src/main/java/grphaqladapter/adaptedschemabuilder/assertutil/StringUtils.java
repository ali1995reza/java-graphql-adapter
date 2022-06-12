package grphaqladapter.adaptedschemabuilder.assertutil;

public class StringUtils {

    public static boolean isNullString(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNoNullString(String str) {
        return !isNullString(str);
    }
}
