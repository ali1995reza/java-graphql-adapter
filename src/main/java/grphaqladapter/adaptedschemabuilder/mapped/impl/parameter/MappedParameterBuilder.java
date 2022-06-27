package grphaqladapter.adaptedschemabuilder.mapped.impl.parameter;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedElementBuilder;

import java.lang.reflect.Parameter;

public final class MappedParameterBuilder extends MappedElementBuilder<MappedParameterBuilder, MappedParameter> {

    public static MappedParameter newAdaptedSchemaParameter(Parameter parameter) {
        return new MappedParameterImpl(
                null,
                null,
                null,
                parameter,
                TypeDescriptor.adaptedSchema(parameter),
                MappedParameter.Model.ADAPTED_SCHEMA
        );
    }

    public static MappedParameter newEnvironmentParameter(Parameter parameter) {
        return new MappedParameterImpl(
                null,
                null,
                null,
                parameter,
                TypeDescriptor.environment(parameter),
                MappedParameter.Model.DATA_FETCHING_ENVIRONMENT
        );
    }

    public static MappedParameter newSkippedParameter(Parameter parameter) {
        return new MappedParameterImpl(
                null,
                null,
                null,
                parameter,
                TypeDescriptor.of(parameter),
                MappedParameter.Model.SKIPPED
        );
    }

    public static MappedParameter newDirectiveParameter(Parameter parameter) {
        return new MappedParameterImpl(
                null,
                null,
                null,
                parameter,
                TypeDescriptor.directives(parameter),
                MappedParameter.Model.DIRECTIVES
        );
    }

    public static MappedParameterBuilder newBuilder() {
        return new MappedParameterBuilder();
    }


    public MappedParameterBuilder() {
        super(MappedElementType.ARGUMENT);
    }

    private Parameter parameter;
    private TypeDescriptor type;

    @Override
    public MappedParameterBuilder refresh() {
        this.parameter = null;
        this.type = null;
        return super.refresh();
    }

    public MappedParameterBuilder type(TypeDescriptor type) {
        this.type = type;
        return this;
    }

    public TypeDescriptor type() {
        return type;
    }

    public MappedParameterBuilder parameter(Parameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public Parameter parameter() {
        return parameter;
    }

    @Override
    public MappedParameter build() {
        return new MappedParameterImpl(
                name(),
                elementType(),
                description(),
                parameter(),
                type(),
                MappedParameter.Model.SCHEMA_ARGUMENT);
    }
}
