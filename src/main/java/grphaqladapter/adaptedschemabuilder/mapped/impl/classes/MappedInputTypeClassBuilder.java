package grphaqladapter.adaptedschemabuilder.mapped.impl.classes;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputTypeClass;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedInputTypeClassBuilder extends MappedClassBuilder<MappedInputTypeClassBuilder, MappedInputTypeClass> {

    public static MappedInputTypeClassBuilder newBuilder() {
        return new MappedInputTypeClassBuilder();
    }

    private final Map<String, MappedInputFieldMethod> inputFieldMethods = new HashMap<>();

    MappedInputTypeClassBuilder() {
        super(MappedElementType.INPUT_TYPE);
    }

    public MappedInputTypeClassBuilder addInputFieldMethod(MappedInputFieldMethod inputFieldMethod) {
        Assert.isFalse(inputFieldMethods.containsKey(inputFieldMethod.name()), new IllegalStateException("input field with name [" + inputFieldMethod.name() + "] already exists"));
        inputFieldMethods.put(inputFieldMethod.name(), inputFieldMethod);
        return this;
    }

    public MappedInputTypeClassBuilder removeInputFieldMethod(MappedInputFieldMethod inputFieldMethod) {
        inputFieldMethods.remove(inputFieldMethod.name(), inputFieldMethod);
        return this;
    }

    public MappedInputTypeClassBuilder removeInputFieldMethod(String name) {
        inputFieldMethods.remove(name);
        return this;
    }

    public MappedInputTypeClassBuilder clearInputFieldMethods() {
        this.inputFieldMethods.clear();
        return this;
    }

    public Map<String, MappedInputFieldMethod> inputFieldMethods() {
        return Collections.unmodifiableMap(new HashMap<>(inputFieldMethods));
    }

    @Override
    public MappedInputTypeClassBuilder refresh() {
        this.clearInputFieldMethods();
        return super.refresh();
    }

    @Override
    public MappedInputTypeClass build() {
        return new MappedInputTypeClassImpl(
                name(),
                description(),
                baseClass(),
                inputFieldMethods()
        );
    }
}
