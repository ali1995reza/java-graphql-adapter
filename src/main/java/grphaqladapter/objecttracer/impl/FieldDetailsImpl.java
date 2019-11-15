package grphaqladapter.objecttracer.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.objecttracer.FieldDetails;

public class FieldDetailsImpl implements FieldDetails {


    private final MappedClass mappedClass;
    private final MappedMethod mappedMethod;

    public FieldDetailsImpl(MappedClass mappedClass, MappedMethod mappedMethod) {
        this.mappedClass = mappedClass;
        this.mappedMethod = mappedMethod;
    }


    @Override
    public MappedClass parent() {
        return mappedClass;
    }

    @Override
    public MappedMethod field() {
        return mappedMethod;
    }
}
