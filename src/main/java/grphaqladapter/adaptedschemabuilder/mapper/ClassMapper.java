package grphaqladapter.adaptedschemabuilder.mapper;


import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlArgumentException;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlFieldException;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedParameterBuilder;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.*;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument.ParameterAutomaticAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument.ParameterRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.chain.ChainBuilder;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field.MethodRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field.PojoMethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type.ClassRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.adaptedschemabuilder.validator.ArgumentValidator;
import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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


    private final static Function<Class, Map<MappedClass.MappedType, MappedClass>> creator = new Function<Class, Map<MappedClass.MappedType, MappedClass>>() {
        @Override
        public Map<MappedClass.MappedType, MappedClass> apply(Class aClass) {
            return new HashMap<>();
        }
    };


    private final MappedParameterBuilder parameterBuilder = MappedParameterBuilder.newBuilder();
    private final MappedMethodBuilder methodBuilder = MappedMethodBuilder.newBuilder();
    private final MappedClassBuilder classBuilder = MappedClassBuilder.newBuilder();


    private Chain<ClassAnnotationDetector> classAnnotationDetectorChain;
    private Chain<MethodAnnotationDetector> methodAnnotationDetectorChain;
    private Chain<ParameterAnnotationDetector> parameterAnnotationDetectorChain;

    public ClassMapper() {
        classAnnotationDetectorChain = DEFAULT_CLASS_ANNOTATION_DETECTOR;
        methodAnnotationDetectorChain = DEFAULT_METHOD_ANNOTATION_DETECTOR;
        parameterAnnotationDetectorChain = DEFAULT_PARAMETER_ANNOTATION_DETECTOR;
    }

    public ClassMapper setClassAnnotationDetectorChain(Chain<ClassAnnotationDetector> classAnnotationDetectorChain) {
        Assert.isNotNull(methodAnnotationDetectorChain, new IllegalStateException("provided chain is null"));
        this.classAnnotationDetectorChain = classAnnotationDetectorChain;
        return this;
    }


    public ClassMapper setMethodAnnotationDetectorChain(Chain<MethodAnnotationDetector> methodAnnotationDetectorChain) {
        Assert.isNotNull(methodAnnotationDetectorChain, new IllegalStateException("provided chain is null"));
        this.methodAnnotationDetectorChain = methodAnnotationDetectorChain;
        return this;
    }

    public ClassMapper setParameterAnnotationDetectorChain(Chain<ParameterAnnotationDetector> parameterAnnotationDetectorChain) {
        Assert.isNotNull(methodAnnotationDetectorChain, new IllegalStateException("provided chain is null"));
        this.parameterAnnotationDetectorChain = parameterAnnotationDetectorChain;
        return this;
    }

    public ClassMapper reset() {
        classAnnotationDetectorChain = DEFAULT_CLASS_ANNOTATION_DETECTOR;
        methodAnnotationDetectorChain = DEFAULT_METHOD_ANNOTATION_DETECTOR;
        parameterAnnotationDetectorChain = DEFAULT_PARAMETER_ANNOTATION_DETECTOR;
        return this;
    }

    private TypeAnnotations detect(Class cls) {
        TypeAnnotations annotations = null;
        for (ClassAnnotationDetector detector : classAnnotationDetectorChain) {
            annotations = detector.detectAnnotationFor(cls);
            if (annotations != null)
                break;
        }

        return annotations;
    }

    private GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {

        for (MethodAnnotationDetector detector : methodAnnotationDetectorChain) {
            if (detector.skipField(method, clazz, mappedType)) {
                return null;
            }
            GraphqlFieldAnnotation annotation = detector.detectFieldAnnotation(method, clazz, mappedType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    private GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {

        for (MethodAnnotationDetector detector : methodAnnotationDetectorChain) {
            if (detector.skipField(method, clazz, mappedType)) {
                return null;
            }
            GraphqlInputFieldAnnotation annotation = detector.detectInputFieldAnnotation(method, clazz, mappedType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    private GraphqlDescriptionAnnotation detectFieldDescription(Method method, Class clazz, MappedClass.MappedType mappedType) {

        for (MethodAnnotationDetector detector : methodAnnotationDetectorChain) {
            GraphqlDescriptionAnnotation annotation = detector.detectDescriptionAnnotation(method, clazz, mappedType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    private GraphqlArgumentAnnotation detectArgument(Method method, Parameter parameter, int index) {

        if (parameter.getType() == DataFetchingEnvironment.class) {
            return null;
        }

        GraphqlArgumentAnnotation annotations = null;

        for (ParameterAnnotationDetector detector : parameterAnnotationDetectorChain) {
            annotations = detector.detectArgumentAnnotation(method, parameter, index);
            if (annotations != null)
                break;
        }

        return annotations;
    }

    public Map<Class, Map<MappedClass.MappedType, MappedClass>> map(Collection<Class> classes) {
        HashMap<Class, Map<MappedClass.MappedType, MappedClass>> allMappedClasses = new HashMap<>();
        //so handle it please !

        for (Class cls : classes) {

            TypeAnnotations annotations = detect(cls);
            Assert.isOneFalse(new IllegalStateException("no annotation found for class [" + cls + "]"),
                    annotations == null);

            TypeValidator.validate(cls, annotations);

            Map<MappedClass.MappedType, MappedClass> allMappedClass =
                    allMappedClassFor(cls, annotations);

            Assert.isOneFalse(new IllegalStateException("no annotation found for class [" + cls + "]"),
                    allMappedClass.size() == 0);
            allMappedClasses.put(cls, allMappedClass);
        }

        return allMappedClasses;

    }


    private Map<MappedClass.MappedType, MappedClass> allMappedClassFor(Class cls, TypeAnnotations annotations) {
        Map<MappedClass.MappedType, MappedClass> map = new HashMap<>();

        String description = annotations.descriptionAnnotation() == null ? null : annotations.descriptionAnnotation().value();

        MappedClass mappedClass = mapClassIfNotNull(cls, annotations.typeAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);

        mappedClass = mapClassIfNotNull(cls, annotations.interfaceAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);

        mappedClass = mapClassIfNotNull(cls, annotations.inputTypeAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);

        mappedClass = mapClassIfNotNull(cls, annotations.unionAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);

        mappedClass = mapClassIfNotNull(cls, annotations.enumAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);

        mappedClass = mapClassIfNotNull(cls, annotations.queryAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);

        mappedClass = mapClassIfNotNull(cls, annotations.mutationAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);

        mappedClass = mapClassIfNotNull(cls, annotations.subscriptionAnnotation(), description);
        if (mappedClass != null) map.put(mappedClass.mappedType(), mappedClass);


        return map;
    }


    private MappedClass mapClassIfNotNull(Class cls, GraphqlInputTypeAnnotation annotation, GraphqlDescriptionAnnotation descriptionAnnotation) {
        if (annotation == null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName(), cls.getSimpleName());

        return mapClass(cls, name, MappedClass.MappedType.INPUT_TYPE, descriptionAnnotation == null ? null : descriptionAnnotation.value());
    }

    private MappedClass mapClass(Class cls, String name, MappedClass.MappedType type, String description) {
        classBuilder.refresh();

        if (type.isTopLevelType() || type.is(MappedClass.MappedType.OBJECT_TYPE) || type.is(MappedClass.MappedType.INTERFACE)) {
            for (Method method : MappingStatics.getAllMethods(cls)) {
                MappedMethod mappedMethod = mapFieldMethod(cls, method, type);

                if (mappedMethod == null) continue;

                classBuilder.addMappedMethod(mappedMethod);
            }
        } else if (type.is(MappedClass.MappedType.INPUT_TYPE)) {
            for (Method method : MappingStatics.getAllMethods(cls)) {
                MappedMethod mappedMethod = mapInputFieldMethod(cls, method);

                if (mappedMethod == null) continue;

                classBuilder.addMappedMethod(mappedMethod);
            }
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(type)
                .setDescription(description)
                .build();
    }


    private MappedClass mapClassIfNotNull(Class cls, Object a, String description) {
        if (a == null)
            return null;

        if (a instanceof GraphqlTypeAnnotation) {
            GraphqlTypeAnnotation annotation = (GraphqlTypeAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.OBJECT_TYPE, description);
        } else if (a instanceof GraphqlInterfaceAnnotation) {
            GraphqlInterfaceAnnotation annotation = (GraphqlInterfaceAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.INTERFACE, description);
        } else if (a instanceof GraphqlInputTypeAnnotation) {
            GraphqlInputTypeAnnotation annotation = (GraphqlInputTypeAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.INPUT_TYPE, description);
        } else if (a instanceof GraphqlEnumAnnotation) {
            GraphqlEnumAnnotation annotation = (GraphqlEnumAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.ENUM, description);
        } else if (a instanceof GraphqlUnionAnnotation) {
            GraphqlUnionAnnotation annotation = (GraphqlUnionAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.UNION, description);
        } else if (a instanceof GraphqlQueryAnnotation) {
            GraphqlQueryAnnotation annotation = (GraphqlQueryAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.QUERY, description);
        } else if (a instanceof GraphqlMutationAnnotation) {
            GraphqlMutationAnnotation annotation = (GraphqlMutationAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.MUTATION, description);
        } else if (a instanceof GraphqlSubscriptionAnnotation) {
            GraphqlSubscriptionAnnotation annotation = (GraphqlSubscriptionAnnotation) a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName(),
                    cls.getSimpleName());

            return mapClass(cls, name, MappedClass.MappedType.SUBSCRIPTION, description);
        } else {
            throw new IllegalStateException("object not an annotation - [type:" + a.getClass() + "]");
        }
    }

    private MappedMethod mapFieldMethod(Class clazz, Method method, MappedClass.MappedType mappedType) {

        GraphqlFieldAnnotation annotation = detectFieldAnnotation(method, clazz, mappedType);

        if (annotation == null) {
            return null;
        }

        FieldValidator.validate(annotation, clazz, method);

        GraphqlDescriptionAnnotation descriptionAnnotation = detectFieldDescription(method, clazz, mappedType);

        MappingStatics.TypeDetails typeDetails =
                MappingStatics.findTypeDetails(method);

        methodBuilder.refresh();
        for (int index = 0; index < method.getParameters().length; index++) {
            methodBuilder.addMappedParameter(
                    mapParameter(clazz, method, method.getParameters()[index], index)
            );
        }

        MappedMethod mappedMethod = methodBuilder
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setFieldName(annotation.fieldName())
                .setNullable(annotation.nullable())
                .setMethod(method)
                .setDescriptionFrom(descriptionAnnotation)
                .build();

        FieldValidator.validate(mappedMethod, clazz, method);

        return mappedMethod;
    }

    private MappedMethod mapInputFieldMethod(Class clazz, Method method) {

        GraphqlInputFieldAnnotation annotation = detectInputFieldAnnotation(method, clazz, MappedClass.MappedType.INPUT_TYPE);

        if (annotation == null) {
            return null;
        }

        FieldValidator.validate(annotation, clazz, method);

        GraphqlDescriptionAnnotation descriptionAnnotation = detectFieldDescription(method, clazz, MappedClass.MappedType.INPUT_TYPE);

        MappingStatics.TypeDetails typeDetails = MappingStatics
                .findTypeDetails(method.getGenericReturnType());

        Method setterMethod = detectSetter(annotation.setter(), clazz, method);

        methodBuilder.refresh();

        MappedMethod mappedMethod = methodBuilder
                .setMethod(method)
                .setSetter(setterMethod)
                .setNullable(annotation.nullable())
                .setFieldName(annotation.inputFieldName())
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setDescriptionFrom(descriptionAnnotation)
                .build();

        FieldValidator.validate(mappedMethod, clazz, method);

        return mappedMethod;
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
        if (parameter.getType() == DataFetchingEnvironment.class) {
            return mapEnvParameter(clazz, method, parameter, index);
        } else {
            return mapSchemaParameter(clazz, method, parameter, index);
        }

    }

    private MappedParameter mapEnvParameter(Class clazz, Method method, Parameter parameter, int index) {

        GraphqlArgumentAnnotation argumentAnnotation = detectArgument(method, parameter, index);

        Assert.isNull(argumentAnnotation, exception(MappingGraphqlArgumentException.class, "env parameter must not contains argument annotation", clazz, method, parameter));


        MappingStatics.TypeDetails typeDetails =
                MappingStatics.findTypeDetails(parameter.getParameterizedType());

        return parameterBuilder
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setParameter(parameter)
                .setArgumentName("ENV_PARAMETER")
                .setNullable(false)
                .setEnv(true)
                .build();
    }

    private MappedParameter mapSchemaParameter(Class clazz, Method method, Parameter parameter, int index) {
        GraphqlArgumentAnnotation argumentAnnotation = detectArgument(method, parameter, index);

        ArgumentValidator.validate(argumentAnnotation, clazz, method, parameter);

        MappingStatics.TypeDetails typeDetails =
                MappingStatics.findTypeDetails(parameter.getParameterizedType());

        String name = Utils.stringNullifyOrGetDefault(argumentAnnotation.argumentName(), parameter.getName());

        MappedParameter mappedParameter = parameterBuilder
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setParameter(parameter)
                .setArgumentName(name)
                .setNullable(argumentAnnotation.nullable())
                .setEnv(false)
                .build();

        ArgumentValidator.validate(mappedParameter, clazz, method, parameter);

        return mappedParameter;
    }

}
