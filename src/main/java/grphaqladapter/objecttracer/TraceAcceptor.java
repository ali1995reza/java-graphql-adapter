package grphaqladapter.objecttracer;

public interface TraceAcceptor {

    public void accept(ObjectTraceContext context , Object value);
}
