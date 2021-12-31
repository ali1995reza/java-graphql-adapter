package grphaqladapter.parser.filter;

public interface ClassFilter {

    ClassFilter ACCEPT_ALL = new ClassFilter() {
        @Override
        public boolean accept(Class cls) {
            return true;
        }
    };

    ClassFilter REJECT_ALL = new ClassFilter() {
        @Override
        public boolean accept(Class cls) {
            return false;
        }
    };

    boolean accept(Class cls);
}
