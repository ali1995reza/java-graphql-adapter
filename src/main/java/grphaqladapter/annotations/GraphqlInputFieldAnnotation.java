package grphaqladapter.annotations;

public interface GraphqlInputFieldAnnotation {

    String inputFieldName();

    boolean nullable();

    String setter();
}
