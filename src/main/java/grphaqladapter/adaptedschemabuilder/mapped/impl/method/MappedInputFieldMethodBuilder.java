package grphaqladapter.adaptedschemabuilder.mapped.impl.method;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodBuilder;

import java.lang.reflect.Method;

public class MappedInputFieldMethodBuilder extends MappedMethodBuilder<MappedInputFieldMethodBuilder, MappedInputFieldMethod> {

    public static MappedInputFieldMethodBuilder newBuilder() {
        return new MappedInputFieldMethodBuilder();
    }

    private Method setter;

    public MappedInputFieldMethodBuilder() {
        super(MappedElementType.INPUT_FIELD);
    }

    @Override
    public MappedInputFieldMethodBuilder copy(MappedInputFieldMethod element) {
        return super.copy(element)
                .setter(element.setter());
    }

    public Method setter() {
        return setter;
    }

    public MappedInputFieldMethodBuilder setter(Method setter) {
        this.setter = setter;
        return this;
    }

    @Override
    public MappedInputFieldMethodBuilder refresh() {
        this.setter = null;
        return super.refresh();
    }

    @Override
    public MappedInputFieldMethod build() {
        return new MappedInputFieldMethodImpl(
                name(),
                description(),
                method(),
                type(),
                setter()
        );
    }
}
