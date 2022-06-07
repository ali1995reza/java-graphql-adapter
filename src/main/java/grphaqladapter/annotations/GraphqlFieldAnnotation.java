package grphaqladapter.annotations;

public interface GraphqlFieldAnnotation {

    String fieldName();

    boolean nullable();

    boolean inputField();

    String setter();
}
