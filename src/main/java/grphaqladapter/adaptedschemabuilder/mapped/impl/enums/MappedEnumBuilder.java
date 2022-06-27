package grphaqladapter.adaptedschemabuilder.mapped.impl.enums;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedEnum;
import grphaqladapter.adaptedschemabuilder.mapped.MappedEnumConstant;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;
import grphaqladapter.adaptedschemabuilder.utils.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedEnumBuilder extends MappedClassBuilder<MappedEnumBuilder, MappedEnum> {

    public static MappedEnumBuilder newBuilder() {
        return new MappedEnumBuilder();
    }

    private final Map<String, MappedEnumConstant> enumConstants = new HashMap<>();

    MappedEnumBuilder() {
        super(MappedElementType.ENUM);
    }

    public MappedEnumBuilder addEnumConstant(MappedEnumConstant constant) {
        Assert.isFalse(enumConstants.containsKey(constant.name()), new IllegalStateException("an enum constant with name [" + name() + "]already exists"));
        this.enumConstants.put(constant.name(), constant);
        return this;
    }

    public MappedEnumBuilder clearConstants() {
        this.enumConstants.clear();
        return this;
    }

    public MappedEnumBuilder deleteConstant(String name) {
        Assert.isTrue(enumConstants.containsKey(name), new IllegalStateException("enum constant with name [" + name + "] does not exists"));
        this.enumConstants.remove(name);
        return this;
    }

    public Map<String, MappedEnumConstant> enumConstants() {
        return Collections.unmodifiableMap(Utils.copy(enumConstants));
    }

    @Override
    public MappedEnumBuilder refresh() {
        this.clearConstants();
        return super.refresh();
    }

    @Override
    public MappedEnum build() {
        return new MappedEnumImpl(
                name(),
                description(),
                baseClass(),
                enumConstants()
        );
    }
}
