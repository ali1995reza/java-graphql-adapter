package grphaqladapter.objecttracer.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.objecttracer.ObjectTraceContext;
import grphaqladapter.objecttracer.ObjectTracerFactory;

import java.util.List;

class ObjectTraceContextImpl implements ObjectTraceContext {


    private Object attachment;
    private MappedMethod field;
    private final ObjectTracerFactory factory;

    ObjectTraceContextImpl(ObjectTracerFactory factory){

        this.factory = factory;
    }


    @Override
    public <A> A attach(Object o) {
        Object old = attachment;
        attachment = o;
        return (A)old;
    }

    @Override
    public <A> A attachment() {
        return (A)attachment;
    }

    @Override
    public List<Object> hierarchy() {
        return null;
    }

    @Override
    public ObjectTracerFactory tracerFactory() {
        return factory;
    }

    @Override
    public MappedMethod field() {
        return field;
    }

    final ObjectTraceContextImpl setField(MappedMethod field) {
        this.field = field;
        return this;
    }
}
