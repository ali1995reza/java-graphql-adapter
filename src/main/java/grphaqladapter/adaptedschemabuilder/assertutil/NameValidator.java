package grphaqladapter.adaptedschemabuilder.assertutil;

import java.util.regex.Pattern;

public class NameValidator {

    private final static Pattern NAME_REGX = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");

    public static boolean isNameValid(String name) {
        if (StringUtils.isNullString(name)) {
            return false;
        }
        return NAME_REGX.matcher(name).matches();
    }
}
