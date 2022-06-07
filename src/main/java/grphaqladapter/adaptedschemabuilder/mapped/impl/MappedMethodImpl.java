package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;


final class MappedMethodImpl implements MappedMethod {

    private final String fieldName;
    private final String description;
    private final Method method;
    private final boolean nullable;
    private final List<MappedParameter> parameters;
    private final int dimensions;
    private final Class type;
    private final boolean isList;
    private final Method setter;
    private final boolean isInputField;


    public MappedMethodImpl(String f, String description, Method m, boolean n, List<MappedParameter> p, Class t, int dims, Method setter) {
        fieldName = f;
        this.description = description;
        method = m;
        nullable = n;
        this.parameters = Utils.nullifyOrGetDefault(p, Collections.EMPTY_LIST);
        type = t;
        dimensions = dims;
        this.setter = setter;
        isList = dimensions > 0;
        isInputField = setter != null;

        FieldValidator.validate(this);
    }

    static grphaqladapter.adaptedschemabuilder.mapped.MappedMethod clone(grphaqladapter.adaptedschemabuilder.mapped.MappedMethod method) {
        Assert.isOneFalse("an input mapped method must contains setter method", method.isInputField()
                , method.setter() == null);

        return new MappedMethodImpl(
                method.fieldName(),
                method.description(),
                method.method(),
                method.isNullable(),
                method.parameters() == null ? null : Collections.unmodifiableList(Utils.copy(method.parameters())),
                method.type(),
                method.dimensions(),
                method.setter());
    }

    public Method method() {
        return method;
    }

    public String fieldName() {
        return fieldName;
    }

    @Override
    public String description() {
        return description;
    }

    public boolean isInputField() {
        return isInputField;
    }

    public Method setter() {
        return setter;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isList() {
        return isList;
    }

    public int dimensions() {
        return dimensions;
    }

    public Class type() {
        return type;
    }

    public List<MappedParameter> parameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "[method:" + method + " , field-name:" + fieldName + " , parameters:" + parameters + " , is-list:" + isList + " , type:" + type + "]";
    }
}
