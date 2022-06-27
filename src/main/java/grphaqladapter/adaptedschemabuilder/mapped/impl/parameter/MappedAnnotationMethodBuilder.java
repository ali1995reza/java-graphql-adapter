package grphaqladapter.adaptedschemabuilder.mapped.impl.parameter;

import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotationMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodBuilder;
import grphaqladapter.annotations.interfaces.ValueParser;

public class MappedAnnotationMethodBuilder extends MappedMethodBuilder<MappedAnnotationMethodBuilder, MappedAnnotationMethod> {

    public static MappedAnnotationMethodBuilder newBuilder() {
        return new MappedAnnotationMethodBuilder();
    }

    private Class<? extends ValueParser> valueParser;

    public MappedAnnotationMethodBuilder() {
        super(MappedElementType.ARGUMENT);
    }

    @Override
    public MappedAnnotationMethodBuilder copy(MappedAnnotationMethod element) {
        return super.copy(element)
                .valueParser(element.valueParser());
    }

    public MappedAnnotationMethodBuilder valueParser(Class<? extends ValueParser> valueParser) {
        this.valueParser = valueParser;
        return this;
    }

    public Class<? extends ValueParser> valueParser() {
        return valueParser;
    }

    @Override
    public MappedAnnotationMethodBuilder refresh() {
        this.valueParser = null;
        return super.refresh();
    }

    @Override
    public MappedAnnotationMethod build() {
        return new MappedAnnotationMethodImpl(
                name(),
                description(),
                method(),
                type(),
                valueParser()
        );
    }
}
