package grphaqladapter.objecttracer;

import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;

import java.util.List;

public interface ObjectTraceContext {

    <A> A attach(Object o);
    <A> A attachment();
    List<Object> hierarchy();
    ObjectTracerFactory tracerFactory();
    MappedMethod field();

}
