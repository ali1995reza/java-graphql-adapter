package grphaqladapter.objecttracer;



public interface ObjectTracer {

    public void getTrace(Object value , Object attachment , TraceAcceptor acceptor);
    public void getTrace(Object value , TraceAcceptor acceptor);
    public void setTrace(Object attachment , TraceAcceptor acceptor);
    public void setTrace(TraceAcceptor acceptor);
    public boolean supportSetTrace();
    public boolean supportGetTrace();
}
