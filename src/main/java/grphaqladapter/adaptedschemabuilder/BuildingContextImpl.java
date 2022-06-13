package grphaqladapter.adaptedschemabuilder;

import graphql.Scalars;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.scalars.java.JavaPrimitives;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.builtinscalars.ID;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;

import java.math.BigDecimal;
import java.util.*;

final class BuildingContextImpl implements BuildingContext {

    private final static GraphQLScalarType GraphQLDouble = GraphQLScalarType.newScalar().name("Double").description("java.lang.Double").coercing(new Coercing<Object, Double>() {
        private Double convert(Object input) {
            if (input instanceof Number || input instanceof String) {
                try {
                    return Double.parseDouble(input.toString());
                } catch (NumberFormatException var3) {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public Double serialize(Object input) {
            Double result = this.convert(input);
            if (result == null) {
                throw new CoercingSerializeException("Expected type Number or String but was '" + input.getClass() + "'.");
            } else {
                return result;
            }
        }

        @Override
        public Double parseValue(Object input) {
            Double result = this.convert(input);
            if (result == null) {
                throw new CoercingParseValueException("Expected type 'BigDecimal' but was '" + input.getClass() + "'.");
            } else {
                return result;
            }
        }

        @Override
        public Double parseLiteral(Object input) {
            if (input instanceof StringValue) {
                return Double.parseDouble(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return Double.parseDouble(((IntValue) input).getValue().toString());
            } else if (input instanceof FloatValue) {
                return Double.parseDouble(((FloatValue) input).getValue().toString());
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Double result = Objects.requireNonNull(this.convert(input));
            return FloatValue.newFloatValue(new BigDecimal(result)).build();
        }
    }).build();


    private final Map<Class, Map<MappedClass.MappedType, MappedClass>> mappedClasses;
    private final Map<MappedClass, GraphQLNamedType> rawTypes;
    private final Map<MappedClass, List<MappedClass>> possibleTypes;
    private final ScalarAddController scalarAddController;

    BuildingContextImpl(Map<Class, Map<MappedClass.MappedType, MappedClass>> mcs,
                        GraphQLSchema.Builder schemaBuilder,
                        Map<Class, GraphQLScalarType> providedScalars) {
        Assert.isNotNull(mcs, new IllegalStateException("mapped classes is null"));
        Assert.isNotNull(schemaBuilder, new IllegalStateException("provided schema builder is null"));
        mappedClasses = mcs;
        rawTypes = new HashMap<>();
        possibleTypes = new HashMap<>();
        scalarAddController = new ScalarAddController(schemaBuilder, providedScalars);
    }

    @Override
    public GraphQLTypeReference getInputObjectTypeFor(Class c) {

        MappedClass mappedClass =
                getMappedClassFor(c, MappedClass.MappedType.INPUT_TYPE);
        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.typeName());
    }

    @Override
    public GraphQLTypeReference getInputTypeFor(Class c) {
        GraphQLTypeReference reference = getInputObjectTypeFor(c);
        if (reference != null) return reference;
        reference = getEnumFor(c);
        if (reference != null) return reference;
        reference = getScalarTypeFor(c);

        return reference;
    }

    @Override
    public GraphQLTypeReference geOutputTypeFor(Class c) {
        GraphQLTypeReference reference = getObjectTypeFor(c);
        if (reference != null) return reference;
        reference = getEnumFor(c);
        if (reference != null) return reference;
        reference = getUnionTypeFor(c);
        if (reference != null) return reference;
        reference = getInterfaceFor(c);
        if (reference != null) return reference;
        reference = getScalarTypeFor(c);

        return reference;
    }

    @Override
    public GraphQLTypeReference getScalarTypeFor(Class c) {
        GraphQLScalarType scalarType =
                scalarAddController.findScalarTypeFor(c);
        return scalarType == null ? null : new GraphQLTypeReference(scalarType.getName());
    }

    @Override
    public GraphQLTypeReference getObjectTypeFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedClass.MappedType.OBJECT_TYPE);
        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.typeName());
    }

    public GraphQLNamedType rawTypeOf(MappedClass mappedClass) {
        return rawTypes.get(mappedClass);
    }

    public List<MappedClass> possibleTypesOf(MappedClass mappedClass) {
        return possibleTypes.get(mappedClass);
    }

    public Map<MappedClass, GraphQLNamedType> rawTypes() {
        return rawTypes;
    }

    @Override
    public GraphQLTypeReference getInterfaceFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedClass.MappedType.INTERFACE);


        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.typeName());
    }

    @Override
    public GraphQLTypeReference getEnumFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedClass.MappedType.ENUM);

        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.typeName());
    }

    @Override
    public GraphQLTypeReference getUnionTypeFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedClass.MappedType.UNION);
        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.typeName());
    }

    @Override
    public boolean isAnInterface(Class cls) {
        return mappedClasses.get(cls) != null &&
                mappedClasses.get(cls).get(MappedClass.MappedType.INTERFACE) != null;
    }

    @Override
    public boolean isAnUnion(Class cls) {
        return mappedClasses.get(cls) != null &&
                mappedClasses.get(cls).get(MappedClass.MappedType.UNION) != null;
    }

    @Override
    public void addToPossibleTypesOf(MappedClass mappedClass, MappedClass possible) {

        if (mappedClass.mappedType() != MappedClass.MappedType.INTERFACE &&
                mappedClass.mappedType() != MappedClass.MappedType.UNION) {
            throw new IllegalStateException("just interfaces or union types have possible types");
        }
        if (mappedClasses.get(mappedClass.baseClass())
                .get(mappedClass.mappedType()) != mappedClass) {
            throw new IllegalStateException("this class dose not related to this context");
        }

        List<MappedClass> list = possibleTypes.get(mappedClass);

        if (list == null) {
            list = new ArrayList<>();
            possibleTypes.put(mappedClass, list);
        }

        list.add(possible);
    }

    @Override
    public MappedClass getMappedClassFor(Class cls, MappedClass.MappedType mappedType) {
        if (mappedClasses.get(cls) == null)
            return null;

        return mappedClasses.get(cls).get(mappedType);
    }

    public void setGraphQLTypeFor(MappedClass cls, GraphQLNamedType type) {

        Assert.isOneOrMoreFalse(new IllegalStateException("class [" + cls + "] discovered multiple times"),
                rawTypes.containsKey(cls));

        rawTypes.put(cls, type);
    }

    public List<DiscoveredScalarType> allScalars() {
        return scalarAddController.discoveredScalarTypes;
    }

    private final static class ScalarAddController {

        private final static Map<Class, GraphQLScalarType> BuiltInScalars;

        static {

            HashMap<Class, GraphQLScalarType> scalars = new HashMap<>();
            scalars.put(String.class, Scalars.GraphQLString);
            scalars.put(Integer.class, Scalars.GraphQLInt);
            scalars.put(int.class, Scalars.GraphQLInt);
            scalars.put(Long.class, JavaPrimitives.GraphQLLong);
            scalars.put(long.class, JavaPrimitives.GraphQLLong);
            scalars.put(Double.class, GraphQLDouble);
            scalars.put(double.class, GraphQLDouble);
            scalars.put(Float.class, Scalars.GraphQLFloat);
            scalars.put(float.class, Scalars.GraphQLFloat);
            scalars.put(Boolean.class, Scalars.GraphQLBoolean);
            scalars.put(boolean.class, Scalars.GraphQLBoolean);
            scalars.put(Character.class, JavaPrimitives.GraphQLChar);
            scalars.put(char.class, JavaPrimitives.GraphQLChar);
            scalars.put(Byte.class, JavaPrimitives.GraphQLByte);
            scalars.put(byte.class, JavaPrimitives.GraphQLByte);
            scalars.put(Short.class, JavaPrimitives.GraphQLShort);
            scalars.put(short.class, JavaPrimitives.GraphQLShort);
            scalars.put(BigDecimal.class, JavaPrimitives.GraphQLBigDecimal);
            scalars.put(ID.class, ID.ScalarType);

            BuiltInScalars = Collections.unmodifiableMap(scalars);

        }


        private final Map<Class, GraphQLScalarType> scalars;
        private final GraphQLSchema.Builder schema;
        private final List<DiscoveredScalarType> discoveredScalarTypes;
        private final Set<GraphQLScalarType> addedScalars;

        private ScalarAddController(GraphQLSchema.Builder builder, Map<Class, GraphQLScalarType> providedScalars) {
            schema = builder;
            HashMap<Class, GraphQLScalarType> scalars = new HashMap<>();
            for (Class cls : providedScalars.keySet()) {
                GraphQLScalarType scalarType = providedScalars.get(cls);
                scalars.put(cls, scalarType);
            }

            for (Class cls : BuiltInScalars.keySet()) {
                scalars.putIfAbsent(cls, BuiltInScalars.get(cls));
            }

            this.scalars = Collections.unmodifiableMap(scalars);

            discoveredScalarTypes = new ArrayList<>();

            addedScalars = new HashSet<>();

        }

        private GraphQLScalarType findScalarTypeFor(Class clazz) {

            GraphQLScalarType scalarType = scalars.get(clazz);

            if (scalarType != null && !addedScalars.contains(scalarType)) {
                schema.additionalType(scalarType);
                addedScalars.add(scalarType);

                MappedClass mappedClass = MappedClassBuilder
                        .newBuilder()
                        .setTypeName(scalarType.getName())
                        .setBaseClass(clazz)
                        .setDescription(scalarType.getDescription())
                        .setMappedType(MappedClass.MappedType.SCALAR)
                        .build();

                TypeValidator.validate(mappedClass, clazz);

                discoveredScalarTypes.add(
                        new DiscoveredScalarTypeImpl(
                                mappedClass,
                                scalarType.getName(),
                                scalarType
                        )
                );
            }

            return scalarType;
        }

    }
}
