package grphaqladapter.annotations.interfaces;

public class GraphqlDirectivesHolder {

    private final static GraphqlDirectivesHolder EMPTY = new GraphqlDirectivesHolder(
            null, null, null
    );

    public static GraphqlDirectivesHolder empty() {
        return EMPTY;
    }

    private final OperationDirectives operationDirectives;
    private final FragmentDirectives fragmentDirectives;
    private final FieldDirectives fieldDirectives;

    public GraphqlDirectivesHolder(OperationDirectives operationDirectives, FragmentDirectives fragmentDirectives, FieldDirectives fieldDirectives) {
        this.operationDirectives = operationDirectives;
        this.fragmentDirectives = fragmentDirectives;
        this.fieldDirectives = fieldDirectives;
    }

    public FragmentDirectives fragmentDirectives() {
        return fragmentDirectives;
    }

    public FieldDirectives fieldDirectives() {
        return fieldDirectives;
    }

    public OperationDirectives operationDirectives() {
        return operationDirectives;
    }
}
