package grphaqladapter.objecttracer;

public interface TraceAcceptor {

    public void acceptGet(ObjectTraceContext context , Object value);
    public void acceptSet(ObjectTraceContext context);
}
