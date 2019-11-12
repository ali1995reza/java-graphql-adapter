package grphaqladapter.objecttracer;

import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;

public interface ObjectTracerFactory {

    public void initialize(AdaptedGraphQLSchema schema);
    public ObjectTracer getTracer(Class cls);
}
