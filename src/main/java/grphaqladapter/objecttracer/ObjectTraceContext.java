package grphaqladapter.objecttracer;

public interface ObjectTraceContext {

    <A> A attach(Object o);
    <A> A attachment();
    ObjectTracerFactory tracerFactory();
    FieldDetails fieldDetails();

}
