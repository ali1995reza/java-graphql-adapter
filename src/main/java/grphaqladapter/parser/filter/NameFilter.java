package grphaqladapter.parser.filter;

import java.util.regex.Pattern;

public class NameFilter implements ClassFilter {

    private final Pattern pattern;

    public NameFilter(Pattern pattern) {
        this.pattern = pattern;
    }

    public NameFilter(String regx) {
        this(Pattern.compile(regx));
    }

    public static ClassFilter startWith(String str) {
        return new NameFilter("^" + str + ".*");
    }

    @Override
    public boolean accept(Class cls) {
        return pattern.matcher(cls.getName()).matches();
    }
}
