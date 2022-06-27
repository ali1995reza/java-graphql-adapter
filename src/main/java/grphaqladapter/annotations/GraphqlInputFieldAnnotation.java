package grphaqladapter.annotations;

public interface GraphqlInputFieldAnnotation extends GraphqlElementAnnotation {

    boolean nullable();

    String setter();
}
