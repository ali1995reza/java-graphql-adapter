package grphaqladapter.objecttracer.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.objecttracer.FieldDetails;
import grphaqladapter.objecttracer.ObjectTraceContext;
import grphaqladapter.objecttracer.ObjectTracerFactory;

import java.util.List;

class ObjectTraceContextImpl implements ObjectTraceContext {


    private Object attachment;
    private FieldDetails field;
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
    public ObjectTracerFactory tracerFactory() {
        return factory;
    }

    @Override
    public FieldDetails fieldDetails() {
        return field;
    }

    final ObjectTraceContextImpl setField(FieldDetails field) {
        this.field = field;
        return this;
    }
}
