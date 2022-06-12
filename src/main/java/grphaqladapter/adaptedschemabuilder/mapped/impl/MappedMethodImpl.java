package grphaqladapter.adaptedschemabuilder.mapped.impl;

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


    public MappedMethodImpl(String f, String description, Method m, boolean n, List<MappedParameter> p, Class t, int dims, boolean isList, Method setter, boolean isInputField) {
        fieldName = f;
        this.description = description;
        method = m;
        nullable = n;
        this.parameters = Utils.nullifyOrGetDefault(p, Collections.EMPTY_LIST);
        type = t;
        dimensions = dims;
        this.isList = isList;
        this.setter = setter;
        this.isInputField = isInputField;
    }

    static MappedMethod clone(MappedMethod method) {
        MappedMethod mappedMethod = new MappedMethodImpl(
                method.fieldName(),
                method.description(),
                method.method(),
                method.isNullable(),
                method.parameters() == null ? null : Collections.unmodifiableList(Utils.copy(method.parameters())),
                method.type(),
                method.dimensions(),
                method.isList(),
                method.setter(),
                method.isInputField());

        FieldValidator.validate(mappedMethod, null, mappedMethod.method());

        return mappedMethod;
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
