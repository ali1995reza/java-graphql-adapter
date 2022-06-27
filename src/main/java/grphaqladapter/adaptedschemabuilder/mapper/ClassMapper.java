package grphaqladapter.adaptedschemabuilder.mapper;


import graphql.introspection.Introspection;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlArgumentException;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlFieldException;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlTypeException;
import grphaqladapter.adaptedschemabuilder.mapped.*;
import grphaqladapter.adaptedschemabuilder.mapped.impl.annotation.MappedAnnotationBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.classes.MappedInputTypeClassBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.classes.MappedTypeClassBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.enums.MappedEnumBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.enums.MappedEnumConstantsBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.interfaces.MappedInterfaceBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.interfaces.MappedUnionInterfaceBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.method.MappedFieldMethodBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.method.MappedInputFieldMethodBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.parameter.MappedAnnotationMethodBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.parameter.MappedParameterBuilder;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.*;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument.ParameterAutomaticAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument.ParameterRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.chain.ChainBuilder;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.directive.RealDirectiveAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field.MethodRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field.PojoMethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type.ClassRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.validator.ArgumentValidator;
import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.*;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.GraphqlDirectivesHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

import static grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder.exception;

public final class ClassMapper {

    public final static Chain<MethodAnnotationDetector> DEFAULT_METHOD_ANNOTATION_DETECTOR =
            ChainBuilder.newBuilder()
                    .addToLast(new MethodRealAnnotationDetector())
                    .addToLast(new PojoMethodAnnotationDetector(true))
                    .build();
    public final static Chain<ParameterAnnotationDetector> DEFAULT_PARAMETER_ANNOTATION_DETECTOR =
            ChainBuilder.newBuilder()
                    .addToLast(new ParameterRealAnnotationDetector())
                    .addToLast(ParameterAutomaticAnnotationDetector.newBuilder()
                            .argNameIfNotPresent("arg")
                            .withNotNullAnnotation(NotNull.class)
                            .build())
                    .build();
    public final static Chain<ClassAnnotationDetector> DEFAULT_CLASS_ANNOTATION_DETECTOR =
            ChainBuilder.newBuilder()
                    .addToLast(new ClassRealAnnotationDetector())
                    .build();
    public final static Chain<AppliedDirectiveDetector> DEFAULT_APPLIED_DIRECTIVE_DETECTOR =
            ChainBuilder.newBuilder()
                    .addToLast(new RealDirectiveAnnotationDetector())
                    .build();


    private final static Function<Class, Map<MappedElementType, MappedClass>> creator = new Function<Class, Map<MappedElementType, MappedClass>>() {
        @Override
        public Map<MappedElementType, MappedClass> apply(Class aClass) {
            return new HashMap<>();
        }
    };


    private Chain<ClassAnnotationDetector> classAnnotationDetectorChain;
    private Chain<MethodAnnotationDetector> methodAnnotationDetectorChain;
    private Chain<ParameterAnnotationDetector> parameterAnnotationDetectorChain;
    private Chain<AppliedDirectiveDetector> appliedDirectiveDetectorChain;

    public ClassMapper() {
        classAnnotationDetectorChain = DEFAULT_CLASS_ANNOTATION_DETECTOR;
        methodAnnotationDetectorChain = DEFAULT_METHOD_ANNOTATION_DETECTOR;
        parameterAnnotationDetectorChain = DEFAULT_PARAMETER_ANNOTATION_DETECTOR;
        appliedDirectiveDetectorChain = DEFAULT_APPLIED_DIRECTIVE_DETECTOR;
    }

    public ClassMapper annotationDetectorChain(Chain<ClassAnnotationDetector> classAnnotationDetectorChain) {
        Assert.isNotNull(classAnnotationDetectorChain, new IllegalStateException("provided chain is null"));
        this.classAnnotationDetectorChain = classAnnotationDetectorChain;
        return this;
    }


    public ClassMapper methodAnnotationDetectorChain(Chain<MethodAnnotationDetector> methodAnnotationDetectorChain) {
        Assert.isNotNull(methodAnnotationDetectorChain, new IllegalStateException("provided chain is null"));
        this.methodAnnotationDetectorChain = methodAnnotationDetectorChain;
        return this;
    }

    public ClassMapper parameterAnnotationDetectorChain(Chain<ParameterAnnotationDetector> parameterAnnotationDetectorChain) {
        Assert.isNotNull(parameterAnnotationDetectorChain, new IllegalStateException("provided chain is null"));
        this.parameterAnnotationDetectorChain = parameterAnnotationDetectorChain;
        return this;
    }

    public void appliedDirectiveDetectorChain(Chain<AppliedDirectiveDetector> appliedDirectiveDetectorChain) {
        Assert.isNotNull(appliedDirectiveDetectorChain, new IllegalStateException("provided chain is null"));
        this.appliedDirectiveDetectorChain = appliedDirectiveDetectorChain;
    }

    public ClassMapper reset() {
        this.classAnnotationDetectorChain = DEFAULT_CLASS_ANNOTATION_DETECTOR;
        this.methodAnnotationDetectorChain = DEFAULT_METHOD_ANNOTATION_DETECTOR;
        this.parameterAnnotationDetectorChain = DEFAULT_PARAMETER_ANNOTATION_DETECTOR;
        this.appliedDirectiveDetectorChain = DEFAULT_APPLIED_DIRECTIVE_DETECTOR;
        return this;
    }

    private TypeAnnotations detectTypeAnnotations(Class clazz) {
        for (ClassAnnotationDetector detector : classAnnotationDetectorChain) {
            if (detector.skipType(clazz)) {
                return null;
            }
            TypeAnnotations annotations = detector.detectAnnotationFor(clazz);
            if (annotations != null) {
                return annotations;
            }
        }
        return null;
    }

    private GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {

        for (MethodAnnotationDetector detector : methodAnnotationDetectorChain) {
            if (detector.skipField(method, clazz, mappedElementType)) {
                return null;
            }
            GraphqlFieldAnnotation annotation = detector.detectFieldAnnotation(method, clazz, mappedElementType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    private GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {

        for (MethodAnnotationDetector detector : methodAnnotationDetectorChain) {
            if (detector.skipInputField(method, clazz, mappedElementType)) {
                return null;
            }
            GraphqlInputFieldAnnotation annotation = detector.detectInputFieldAnnotation(method, clazz, mappedElementType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    private GraphqlDirectiveArgumentAnnotation detectDirectiveArgumentAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {

        for (MethodAnnotationDetector detector : methodAnnotationDetectorChain) {
            if (detector.skipDirectiveArgument(method, clazz, mappedElementType)) {
                return null;
            }
            GraphqlDirectiveArgumentAnnotation annotation = detector.detectDirectiveArgumentAnnotation(method, clazz, mappedElementType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    public List<DirectiveArgumentsValue> detectDirective(MappedElement element, Map<Class, MappedAnnotation> annotationMap) {
        for (AppliedDirectiveDetector detector : appliedDirectiveDetectorChain) {
            List<DirectiveArgumentsValue> value = detector.detectDirectives(element, annotationMap);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private GraphqlDescriptionAnnotation detectFieldDescription(Method method, Class clazz, MappedElementType mappedElementType) {

        for (MethodAnnotationDetector detector : methodAnnotationDetectorChain) {
            GraphqlDescriptionAnnotation annotation = detector.detectDescriptionAnnotation(method, clazz, mappedElementType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    private Object detectArgument(Class clazz, Method method, Parameter parameter, int index) {
        for (ParameterAnnotationDetector detector : parameterAnnotationDetectorChain) {
            if (detector.skip(method, parameter, index)) {
                return MappedParameter.Model.SKIPPED;
            }
            GraphqlArgumentAnnotation annotation = detector.detectArgumentAnnotation(method, parameter, index);
            if (annotation != null) {
                return annotation;
            }
            if ((parameter.getType() == DataFetchingEnvironment.class ||
                    parameter.getType() == GraphqlDirectivesHolder.class ||
                    parameter.getType() == AdaptedGraphQLSchema.class) &&
                    detector.isSystemParameter(method, parameter, index)) {

                if (parameter.getType() == AdaptedGraphQLSchema.class) {
                    return MappedParameter.Model.ADAPTED_SCHEMA;
                } else if (parameter.getType() == DataFetchingEnvironment.class) {
                    return MappedParameter.Model.DATA_FETCHING_ENVIRONMENT;
                } else if (parameter.getType() == GraphqlDirectivesHolder.class) {
                    return MappedParameter.Model.DIRECTIVES;
                }

                throw exception(MappingGraphqlArgumentException.class, "can not map parameter to system-parameter type. A system-parameter type must be AdaptedGraphQLSchema, DataFetchingEnvironment or GraphqlDirectivesHolder", clazz, method, parameter);
            }
        }

        throw exception(MappingGraphqlArgumentException.class, "can not map parameter", clazz, method, parameter);
    }

    public Map<Class, Map<MappedElementType, MappedElement>> map(Collection<Class> classes) {
        HashMap<Class, Map<MappedElementType, MappedElement>> allMappedClasses = new HashMap<>();

        for (Class clazz : classes) {

            TypeAnnotations annotations = detectTypeAnnotations(clazz);

            if (annotations == null) {
                continue;
            }

            TypeValidator.validate(annotations, clazz);

            Map<MappedElementType, MappedElement> allMappedClass =
                    allMappedElementFor(clazz, annotations);

            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "no mapped class", clazz),
                    allMappedClass.size() == 0);

            allMappedClasses.put(clazz, allMappedClass);
        }

        return allMappedClasses;

    }

    private Map<MappedElementType, MappedElement> allMappedElementFor(Class cls, TypeAnnotations annotations) {
        Map<MappedElementType, MappedElement> map = new HashMap<>();

        final String description = annotations.descriptionAnnotation() == null ? null :
                annotations.descriptionAnnotation().value();

        Arrays.asList(annotations.typeAnnotation(),
                        annotations.interfaceAnnotation(),
                        annotations.inputTypeAnnotation(),
                        annotations.unionAnnotation(),
                        annotations.enumAnnotation(),
                        annotations.queryAnnotation(),
                        annotations.mutationAnnotation(),
                        annotations.subscriptionAnnotation(),
                        annotations.directiveAnnotation())
                .stream()
                .filter(Objects::nonNull)
                .forEach(element -> {
                    MappedElement mappedClass = mapClassIfNotNull(cls, element, description);
                    map.put(mappedClass.mappedType(), mappedClass);
                });

        return map;
    }

    private MappedTypeClass mapTypeClass(Class clazz, String name, MappedElementType elementType, String description) {

        MappedTypeClassBuilder builder = MappedTypeClassBuilder.newBuilder(elementType)
                .name(name)
                .baseClass(clazz)
                .description(description);

        for (Method method : MappingStatics.getAllMethods(clazz)) {
            MappedFieldMethod mappedMethod = mapFieldMethod(clazz, method);

            if (mappedMethod == null) continue;

            builder.addFieldMethod(mappedMethod);
        }

        MappedTypeClass mappedTypeClass = builder.build();

        TypeValidator.validate(mappedTypeClass, clazz);

        return mappedTypeClass;
    }

    private MappedInterface mapInterfaceClass(Class clazz, String name, String description) {

        MappedInterfaceBuilder builder = MappedInterfaceBuilder.newBuilder()
                .name(name)
                .baseClass(clazz)
                .description(description);

        for (Method method : MappingStatics.getAllMethods(clazz)) {
            MappedFieldMethod mappedMethod = mapFieldMethod(clazz, method);

            if (mappedMethod == null) continue;

            builder.addFieldMethod(mappedMethod);
        }

        MappedInterface mappedInterface = builder.build();

        TypeValidator.validate(mappedInterface, clazz);

        return mappedInterface;
    }


    private MappedInputTypeClass mapInputTypeClass(Class clazz, String name, String description) {
        MappedInputTypeClassBuilder builder = MappedInputTypeClassBuilder.newBuilder()
                .name(name)
                .baseClass(clazz)
                .description(description);

        for (Method method : MappingStatics.getAllMethods(clazz)) {
            MappedInputFieldMethod mappedMethod = mapInputFieldMethod(clazz, method);

            if (mappedMethod == null) continue;

            builder.addInputFieldMethod(mappedMethod);
        }

        MappedInputTypeClass mappedTypeClass = builder.build();

        TypeValidator.validate(mappedTypeClass, clazz);

        return mappedTypeClass;
    }

    private MappedEnum mapEnumClass(Class clazz, String name, String description) {

        MappedEnumBuilder builder = MappedEnumBuilder.newBuilder()
                .name(name)
                .baseClass(clazz)
                .description(description);

        for (Object c : clazz.getEnumConstants()) {
            Enum constant = (Enum) c;

            MappedEnumConstant mappedEnumConstant = mapEnumConstant(clazz, constant);

            if (mappedEnumConstant == null) {
                continue;
            }

            builder.addEnumConstant(mappedEnumConstant);
        }

        MappedEnum mappedEnum = builder.build();

        TypeValidator.validate(mappedEnum, clazz);

        return mappedEnum;

    }

    private MappedUnionInterface mapUnionInterface(Class clazz, String name, String description) {

        MappedUnionInterfaceBuilder builder = MappedUnionInterfaceBuilder.newBuilder()
                .name(name)
                .baseClass(clazz)
                .description(description);

        MappedUnionInterface unionInterface = builder.build();

        TypeValidator.validate(unionInterface, clazz);

        return unionInterface;
    }

    private MappedClass mapClass(Class clazz, String name, MappedElementType type, String description) {

        if (type.isTopLevelType() || type.is(MappedElementType.OBJECT_TYPE)) {
            return mapTypeClass(clazz, name, type, description);
        } else if (type.is(MappedElementType.INPUT_TYPE)) {
            return mapInputTypeClass(clazz, name, description);
        } else if (type.is(MappedElementType.ENUM)) {
            return mapEnumClass(clazz, name, description);
        } else if (type.is(MappedElementType.INTERFACE)) {
            return mapInterfaceClass(clazz, name, description);
        } else if (type.is(MappedElementType.UNION)) {
            return mapUnionInterface(clazz, name, description);
        }

        throw new IllegalStateException("bad element type [" + type + "] for mapping a class");
    }

    private MappedAnnotation mapAnnotation(Class<? extends Annotation> clazz, String name, Class<? extends GraphqlDirectiveFunction> functionality, Introspection.DirectiveLocation[] locations, String description) {

        MappedAnnotationBuilder builder = MappedAnnotationBuilder.newBuilder();
        builder.name(name).description(description)
                .baseClass(clazz)
                .functionality(functionality)
                .addLocations(locations);

        for (Method method : MappingStatics.getAllMethods(clazz)) {
            MappedAnnotationMethod mappedMethod = mapAnnotationMethod(clazz, method);

            if (mappedMethod == null) {
                continue;
            }

            builder.addMethod(mappedMethod);

        }

        return builder.build();
    }

    private static MappedElementType getTypeFromAnnotation(GraphqlElementAnnotation annotation) {
        if (annotation instanceof GraphqlTypeAnnotation) {
            return MappedElementType.OBJECT_TYPE;
        }
        if (annotation instanceof GraphqlInterfaceAnnotation) {
            return MappedElementType.INTERFACE;
        }
        if (annotation instanceof GraphqlInputTypeAnnotation) {
            return MappedElementType.INPUT_TYPE;
        }
        if (annotation instanceof GraphqlUnionAnnotation) {
            return MappedElementType.UNION;
        }
        if (annotation instanceof GraphqlEnumAnnotation) {
            return MappedElementType.ENUM;
        }
        if (annotation instanceof GraphqlMutationAnnotation) {
            return MappedElementType.MUTATION;
        }
        if (annotation instanceof GraphqlQueryAnnotation) {
            return MappedElementType.QUERY;
        }
        if (annotation instanceof GraphqlSubscriptionAnnotation) {
            return MappedElementType.SUBSCRIPTION;
        }
        throw new IllegalStateException("bad annotation [" + annotation + "] for a class");
    }

    private MappedElement mapClassIfNotNull(Class cls, GraphqlElementAnnotation annotation, String description) {
        if (annotation == null)
            return null;

        if (annotation instanceof GraphqlDirectiveAnnotation) {
            GraphqlDirectiveAnnotation directiveAnnotation = (GraphqlDirectiveAnnotation) annotation;
            return mapAnnotation(cls, directiveAnnotation.name(), directiveAnnotation.functionality(), directiveAnnotation.locations(), description);
        }
        return mapClass(cls, annotation.name(), getTypeFromAnnotation(annotation), description);
    }

    private MappedFieldMethod mapFieldMethod(Class clazz, Method method) {

        GraphqlFieldAnnotation annotation = detectFieldAnnotation(method, clazz, MappedElementType.FIELD);

        if (annotation == null) {
            return null;
        }

        FieldValidator.validate(annotation, clazz, method);

        GraphqlDescriptionAnnotation descriptionAnnotation = detectFieldDescription(method, clazz, MappedElementType.FIELD);

        MappedFieldMethodBuilder methodBuilder = MappedFieldMethodBuilder.newBuilder();

        for (int index = 0; index < method.getParameters().length; index++) {
            methodBuilder.addParameter(
                    mapParameter(clazz, method, method.getParameters()[index], index)
            );
        }

        MappedFieldMethod mappedMethod = methodBuilder.method(method)
                .description(getDescription(descriptionAnnotation))
                .name(annotation.name())
                .type(TypeDescriptor.of(method, annotation.nullable()))
                .build();

        FieldValidator.validate(mappedMethod, clazz, method);

        return mappedMethod;
    }

    private MappedInputFieldMethod mapInputFieldMethod(Class clazz, Method method) {

        GraphqlInputFieldAnnotation annotation = detectInputFieldAnnotation(method, clazz, MappedElementType.INPUT_TYPE);

        if (annotation == null) {
            return null;
        }

        FieldValidator.validate(annotation, clazz, method);

        GraphqlDescriptionAnnotation descriptionAnnotation = detectFieldDescription(method, clazz, MappedElementType.INPUT_TYPE);

        Method setterMethod = detectSetter(annotation.setter(), clazz, method);

        MappedInputFieldMethodBuilder methodBuilder = MappedInputFieldMethodBuilder.newBuilder();

        MappedInputFieldMethod mappedMethod = methodBuilder.method(method)
                .description(getDescription(descriptionAnnotation))
                .name(annotation.name())
                .type(TypeDescriptor.of(method, annotation.nullable()))
                .setter(setterMethod)
                .build();

        FieldValidator.validate(mappedMethod, clazz, method);

        return mappedMethod;
    }

    private MappedEnumConstant mapEnumConstant(Class clazz, Enum e) {
        MappedEnumConstant constant = MappedEnumConstantsBuilder.newBuilder()
                .name(e.name())
                .constant(e)
                .build();

        //todo validate

        return constant;
    }

    private MappedAnnotationMethod mapAnnotationMethod(Class clazz, Method method) {
        GraphqlDirectiveArgumentAnnotation annotation = detectDirectiveArgumentAnnotation(method, clazz, MappedElementType.DIRECTIVE);
        if (annotation == null) {
            return null;
        }
        //ArgumentValidator.validate(); //todo


        return MappedAnnotationMethodBuilder.newBuilder()
                .name(annotation.name())
                .method(method)
                .type(new TypeDescriptor(
                        annotation.type(),
                        annotation.nullable(),
                        annotation.dimensions(),
                        annotation.dimensions() > 0 ? TypeDescriptor.DimensionModel.LIST : null
                ))
                .valueParser(annotation.valueParser())
                .build();
    }

    private Method detectSetter(String setter, Class clazz, Method method) {
        try {
            Method setterMethod = clazz.getMethod(setter, method.getReturnType());

            FieldValidator.validateSetterMethod(setterMethod, clazz, method);

            return setterMethod;
        } catch (NoSuchMethodException e) {
            throw exception(MappingGraphqlFieldException.class, "can not find setter method", clazz, method);
        }
    }


    private MappedParameter mapParameter(Class clazz, Method method, Parameter parameter, int index) {
        Object o = detectArgument(clazz, method, parameter, index);

        if (o == MappedParameter.Model.ADAPTED_SCHEMA) {
            return MappedParameterBuilder.newAdaptedSchemaParameter(parameter);
        }

        if (o == MappedParameter.Model.DATA_FETCHING_ENVIRONMENT) {
            return MappedParameterBuilder.newEnvironmentParameter(parameter);
        }

        if (o == MappedParameter.Model.DIRECTIVES) {
            return MappedParameterBuilder.newDirectiveParameter(parameter);
        }

        if (o == MappedParameter.Model.SKIPPED) {
            return MappedParameterBuilder.newSkippedParameter(parameter);
        }

        GraphqlArgumentAnnotation argumentAnnotation = (GraphqlArgumentAnnotation) o;

        MappedParameter mappedParameter = MappedParameterBuilder
                .newBuilder()
                .name(argumentAnnotation.name())
                .parameter(parameter)
                .type(TypeDescriptor.of(parameter, argumentAnnotation.nullable()))
                .build();

        ArgumentValidator.validate(mappedParameter, clazz, method, parameter);

        return mappedParameter;
    }

    private static String getDescription(GraphqlDescriptionAnnotation annotation) {
        return annotation == null ? null : annotation.value();
    }

}
