package grphaqladapter.objecttracer.impl;


import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.objecttracer.FieldDetails;
import grphaqladapter.objecttracer.ObjectTracer;
import grphaqladapter.objecttracer.ObjectTracerFactory;
import grphaqladapter.objecttracer.TraceAcceptor;

import java.util.ArrayList;
import java.util.List;

final class ObjectTracerImpl implements ObjectTracer {

    private final DiscoveredInputType inputType;
    private final List<FieldDetails> inputTypeFields;
    private final DiscoveredObjectType objectType;
    private final List<FieldDetails> objectTypeFields;
    private final ObjectTracerFactory factory;
    private final boolean rejectGetTrace;
    private final boolean rejectSetTrace;


    ObjectTracerImpl(DiscoveredInputType inputType, DiscoveredObjectType objectType, ObjectTracerFactory factory) {
        this.inputType = inputType;
        this.objectType = objectType;
        if(inputType!=null)
        {
            inputTypeFields = new ArrayList<>();
            for(MappedMethod method:inputType.asMappedClass().mappedMethods().values())
            {
                inputTypeFields.add(new FieldDetailsImpl(inputType.asMappedClass() , method));
            }
        }else inputTypeFields = null;

        if(objectType!=null)
        {
            objectTypeFields = new ArrayList<>();
            for(MappedMethod method:objectType.asMappedClass().mappedMethods().values())
            {
                objectTypeFields.add(new FieldDetailsImpl(objectType.asMappedClass() , method));
            }
        }else objectTypeFields = null;

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
        for(FieldDetails details:inputTypeFields)
        {
            try {
                acceptor.acceptGet(context.setField(details) , details.field().method().invoke(value));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

    }


    @Override
    public void setTrace(Object attachment , TraceAcceptor acceptor) {
        assertIfSetTraceNotPossible();
        ObjectTraceContextImpl context = new ObjectTraceContextImpl(factory);
        context.attach(attachment);

        for(FieldDetails field:objectTypeFields)
        {
            try{
                acceptor.acceptSet(context.setField(field));
            }catch (Exception e)
            {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void setTrace(TraceAcceptor acceptor) {
        setTrace(null , acceptor);
    }

    @Override
    public void getTrace(Object value , TraceAcceptor acceptor) {
        getTrace(value , null , acceptor);
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
