package grphaqladapter.objecttracer.impl;


import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.objecttracer.ObjectTracer;
import grphaqladapter.objecttracer.ObjectTracerFactory;
import grphaqladapter.objecttracer.TraceAcceptor;

final class ObjectTracerImpl implements ObjectTracer {

    private final DiscoveredInputType inputType;
    private final DiscoveredObjectType objectType;
    private final ObjectTracerFactory factory;
    private final boolean rejectGetTrace;
    private final boolean rejectSetTrace;


    ObjectTracerImpl(DiscoveredInputType inputType, DiscoveredObjectType objectType, ObjectTracerFactory factory) {
        this.inputType = inputType;
        this.objectType = objectType;
        rejectGetTrace = inputType==null;
        rejectSetTrace = objectType==null;
        this.factory = factory;
    }

    public ObjectTracerImpl(DiscoveredInputType inputType, ObjectTracerFactory factory)
    {
        this(inputType , null, factory);
    }

    public ObjectTracerImpl(DiscoveredObjectType objectType, ObjectTracerFactory factory)
    {
        this(null , objectType, factory);
    }

    @Override
    public void getTrace(Object value, Object attachment , TraceAcceptor acceptor) {
        assertIfGetTraceNotPossible();

        ObjectTraceContextImpl context = new ObjectTraceContextImpl(factory);
        context.attach(attachment);

        //so trace object !
        for(MappedMethod method:inputType.asMappedClass().mappedMethods().values())
        {
            try {
                acceptor.accept(context.setField(method) , method.method().invoke(value));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

    }


    @Override
    public void setTrace(Object attachment , TraceAcceptor acceptor) {
        assertIfSetTraceNotPossible();
    }

    @Override
    public void getTrace(Object value , TraceAcceptor acceptor) {
        assertIfGetTraceNotPossible();
        ObjectTraceContextImpl context = new ObjectTraceContextImpl(factory);

        //so trace object !
        for(MappedMethod method:inputType.asMappedClass().mappedMethods().values())
        {
            try {
                acceptor.accept(context.setField(method) , method.method().invoke(value));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public boolean supportSetTrace() {
        return !rejectSetTrace;
    }

    @Override
    public boolean supportGetTrace() {
        return !rejectGetTrace;
    }

    private final void assertIfGetTraceNotPossible()
    {

        if(rejectGetTrace)
            throw new IllegalStateException("get getTrace rejected for DiscoveredOutputType");
    }

    private final void assertIfSetTraceNotPossible()
    {

        if(rejectSetTrace)
            throw new IllegalStateException("set getTrace rejected for DiscoveredOutputType");
    }
}
