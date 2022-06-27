package grphaqladapter.adaptedschemabuilder.mapped.impl.classes;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedTypeClassBuilder extends MappedClassBuilder<MappedTypeClassBuilder, MappedTypeClass> {

    public static MappedTypeClassBuilder newBuilder(MappedElementType elementType) {
        return new MappedTypeClassBuilder(elementType);
    }

    private final Map<String, MappedFieldMethod> fieldMethods = new HashMap<>();

    MappedTypeClassBuilder(MappedElementType elementType) {
        super(elementType);
        Assert.isTrue(elementType.isTopLevelType() || elementType.is(MappedElementType.OBJECT_TYPE) ,
                new IllegalStateException("MappedTypeClass valid element types is [QUERY, MUTATION, SUBSCRIPTION, OBJECT_TYPE]"));
    }

    public MappedTypeClassBuilder addFieldMethod(MappedFieldMethod fieldMethod) {
        Assert.isFalse(fieldMethods.containsKey(fieldMethod.name()), new IllegalStateException("input field with name [" + fieldMethod.name() + "] already exists"));
        this.fieldMethods.put(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedTypeClassBuilder removeFieldMethod(MappedFieldMethod fieldMethod) {
        this.fieldMethods.remove(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedTypeClassBuilder removeFieldMethod(String name) {
        this.fieldMethods.remove(name);
        return this;
    }

    public MappedTypeClassBuilder clearFieldMethods() {
        this.fieldMethods.clear();
        return this;
    }

    public Map<String, MappedFieldMethod> fieldMethods() {
        return Collections.unmodifiableMap(new HashMap<>(fieldMethods));
    }

    @Override
    public MappedTypeClassBuilder refresh() {
        this.clearFieldMethods();
        return super.refresh();
    }

    @Override
    public MappedTypeClass build() {
        return new MappedTypeClassImpl(
                name(),
                elementType(),
                description(),
                baseClass(),
                fieldMethods()
        );
    }
}
