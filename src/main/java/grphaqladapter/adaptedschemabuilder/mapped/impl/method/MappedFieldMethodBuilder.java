package grphaqladapter.adaptedschemabuilder.mapped.impl.method;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class MappedFieldMethodBuilder extends MappedMethodBuilder<MappedFieldMethodBuilder, MappedFieldMethod> {

    public static MappedFieldMethodBuilder newBuilder() {
        return new MappedFieldMethodBuilder();
    }

    private final List<MappedParameter> parameters = new ArrayList<>();

    public MappedFieldMethodBuilder() {
        super(MappedElementType.FIELD);
    }

    @Override
    public MappedFieldMethodBuilder copy(MappedFieldMethod element) {
        super.copy(element);
        element.parameters().forEach(this::addParameter);
        return this;
    }

    public MappedFieldMethodBuilder addParameter(MappedParameter parameter) {
        Assert.isFalse(parameters.stream().anyMatch(ParameterNamePredict.of(parameter)), new IllegalStateException("parameter with name [" + parameter.name() + "] already exists"));
        parameters.add(parameter);
        return this;
    }

    public MappedFieldMethodBuilder removeParameter(String parameter) {
        for (MappedParameter param : parameters) {
            if (param.name().equals(parameter)) {
                parameters.remove(param);
                break;
            }
        }
        return this;
    }

    public MappedFieldMethodBuilder removeParameter(MappedParameter parameter) {
        parameters.remove(parameter);
        return this;
    }

    public MappedFieldMethodBuilder clearParameters() {
        this.parameters.clear();
        return this;
    }

    @Override
    public MappedFieldMethodBuilder refresh() {
        this.clearParameters();
        return super.refresh();
    }

    public List<MappedParameter> parameters() {
        return Collections.unmodifiableList(new ArrayList<>(parameters));
    }

    @Override
    public MappedFieldMethod build() {
        return new MappedFieldMethodImpl(
                name(),
                description(),
                method(),
                type(),
                parameters()
        );
    }


    private final static class ParameterNamePredict implements Predicate<MappedParameter> {

        private static Predicate<MappedParameter> of(String name) {
            return new ParameterNamePredict(name);
        }

        private static Predicate<MappedParameter> of(MappedParameter parameter) {
            return of(parameter.name());
        }

        private final String parameterName;

        private ParameterNamePredict(String parameterName) {
            this.parameterName = parameterName;
        }


        @Override
        public boolean test(MappedParameter parameter) {
            if(parameter.name() == null) {
                return false;
            }
            return parameter.name().equals(parameterName);
        }
    }
}
