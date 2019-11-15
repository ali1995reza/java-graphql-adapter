package grphaqladapter.objecttracer;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;

public interface FieldDetails {

    public MappedClass parent();
    public MappedMethod field();
}
