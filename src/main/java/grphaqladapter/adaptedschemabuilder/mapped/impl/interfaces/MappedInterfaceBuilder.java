package grphaqladapter.adaptedschemabuilder.mapped.impl.interfaces;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInterface;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedInterfaceBuilder extends MappedClassBuilder<MappedInterfaceBuilder, MappedInterface> {

    public static MappedInterfaceBuilder newBuilder() {
        return new MappedInterfaceBuilder();
    }

    private final Map<String, MappedFieldMethod> fieldMethods = new HashMap<>();

    MappedInterfaceBuilder() {
        super(MappedElementType.INTERFACE);
    }

    public MappedInterfaceBuilder addFieldMethod(MappedFieldMethod fieldMethod) {
        Assert.isFalse(fieldMethods.containsKey(fieldMethod.name()), new IllegalStateException("input field with name [" + fieldMethod.name() + "] already exists"));
        this.fieldMethods.put(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedInterfaceBuilder removeFieldMethod(MappedFieldMethod fieldMethod) {
        this.fieldMethods.remove(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedInterfaceBuilder removeFieldMethod(String name) {
        this.fieldMethods.remove(name);
        return this;
    }

    public MappedInterfaceBuilder clearFieldMethods() {
        this.fieldMethods.clear();
        return this;
    }

    public Map<String, MappedFieldMethod> fieldMethods() {
        return Collections.unmodifiableMap(new HashMap<>(fieldMethods));
    }

    @Override
    public MappedInterfaceBuilder refresh() {
        this.clearFieldMethods();
        return super.refresh();
    }

    @Override
    public MappedInterface build() {
        return new MappedInterfaceImpl(
                name(),
                description(),
                baseClass(),
                fieldMethods()
        );
    }
}
