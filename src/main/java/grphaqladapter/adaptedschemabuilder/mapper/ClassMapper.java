package grphaqladapter.adaptedschemabuilder.mapper;


import grphaqladapter.adaptedschemabuilder.GraphqlQueryHandler;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedParameterBuilder;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.*;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument.ParameterAutomaticAnnotationBuilder;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument.ParameterRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.chain.ChainBuilder;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field.MethodRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type.ClassRealAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

public final class ClassMapper {

    public final static Chain<MethodAnnotationDetector> DEFAULT_METHOD_ANNOTATION_DETECTOR =
            ChainBuilder.newBuilder()
            .addToLast(new MethodRealAnnotationDetector())
            .build();
    public final static Chain<ParameterAnnotationDetector> DEFAULT_PARAMETER_ANNOTATION_DETECTOR =
            ChainBuilder.newBuilder()
            .addToLast(new ParameterRealAnnotationDetector())
            .addToLast(new ParameterAutomaticAnnotationBuilder())
            .build();

    public final static Chain<ClassAnnotationDetector> DEFAULT_CLASS_ANNOTATION_DETECTOR =
            ChainBuilder.newBuilder()
            .addToLast(new ClassRealAnnotationDetector())
            .build();




    private final static Function<Class , Map<MappedClass.MappedType , MappedClass>> creator = new Function<Class, Map<MappedClass.MappedType, MappedClass>>() {
        @Override
        public Map<MappedClass.MappedType, MappedClass> apply(Class aClass) {
            return new HashMap<>();
        }
    };


    private final MappedParameterBuilder parameterBuilder = MappedParameterBuilder.newBuilder();
    private final MappedMethodBuilder methodBuilder = MappedMethodBuilder.newBuilder();
    private final MappedClassBuilder classBuilder = MappedClassBuilder.newBuilder();



    private Chain<ClassAnnotationDetector> classAnnotationDetectorChain;
    private Chain<MethodAnnotationDetector> defaultMethodAnnotationDetectorChain;
    private Chain<ParameterAnnotationDetector> defaultParameterAnnotationDetectorChain;
    private final Map<Class , Chain<MethodAnnotationDetector>> methodAnnotationDetectorChainMap;
    private final Map<Method, Chain<ParameterAnnotationDetector>> parameterAnnotationDetectorChainMap;

    public ClassMapper() {
        parameterAnnotationDetectorChainMap = new HashMap<>();
        methodAnnotationDetectorChainMap = new HashMap<>();
        classAnnotationDetectorChain = DEFAULT_CLASS_ANNOTATION_DETECTOR;
        defaultMethodAnnotationDetectorChain = DEFAULT_METHOD_ANNOTATION_DETECTOR;
        defaultParameterAnnotationDetectorChain = DEFAULT_PARAMETER_ANNOTATION_DETECTOR;
    }

    public ClassMapper setClassAnnotationDetectorChain(Chain<ClassAnnotationDetector> classAnnotationDetectorChain) {
        this.classAnnotationDetectorChain = classAnnotationDetectorChain;
        return this;
    }

    public Chain<ClassAnnotationDetector> classAnnotationDetectorChain() {
        return classAnnotationDetectorChain;
    }

    public ClassMapper setMethodAnnotationDetectorChain(Class cls , Chain<MethodAnnotationDetector> methodAnnotationDetectorChain)
    {
        methodAnnotationDetectorChainMap.put(cls , methodAnnotationDetectorChain);
        return this;
    }

    public ClassMapper removeMethodAnnotationDetectorChain(Class cls)
    {
        methodAnnotationDetectorChainMap.remove(cls);
        return this;
    }

    public Chain<MethodAnnotationDetector> methodAnnotationDetectorChain(Class cls)
    {
        return methodAnnotationDetectorChainMap.get(cls);
    }

    public ClassMapper setParameterAnnotationDetectorChain(Method method , Chain<ParameterAnnotationDetector> methodAnnotationDetectorChain)
    {
        parameterAnnotationDetectorChainMap.put(method , methodAnnotationDetectorChain);
        return this;
    }

    public ClassMapper removeParameterAnnotationDetectorChain(Method method)
    {
        parameterAnnotationDetectorChainMap.remove(method);
        return this;
    }

    public Chain<ParameterAnnotationDetector> parameterAnnotationDetectorChain(Method method)
    {
        return parameterAnnotationDetectorChainMap.get(method);
    }

    public ClassMapper setDefaultMethodAnnotationDetectorChain(Chain<MethodAnnotationDetector> defaultMethodAnnotationDetectorChain) {
        this.defaultMethodAnnotationDetectorChain = defaultMethodAnnotationDetectorChain;
        return this;
    }

    public ClassMapper setDefaultParameterAnnotationDetectorChain(Chain<ParameterAnnotationDetector> defaultParameterAnnotationDetectorChain) {
        this.defaultParameterAnnotationDetectorChain = defaultParameterAnnotationDetectorChain;
        return this;
    }

    public ClassMapper clear()
    {
        classAnnotationDetectorChain = null;
        methodAnnotationDetectorChainMap.clear();
        parameterAnnotationDetectorChainMap.clear();
        return this;
    }

    private final TypeAnnotations detect(Class cls)
    {
        TypeAnnotations annotations = null;
        for(ClassAnnotationDetector detector:classAnnotationDetectorChain)
        {
            annotations = detector.detectAnnotationFor(cls);
            if(annotations!=null)
                break;
        }

        return annotations;
    }
    private final FieldAnnotations detect(Method method)
    {
        Chain<MethodAnnotationDetector> detectors = methodAnnotationDetectorChainMap.get(method);
        detectors=detectors==null?defaultMethodAnnotationDetectorChain:detectors;
        if(detectors==null)
            return null;

        FieldAnnotations annotations = null;

        for(MethodAnnotationDetector detector:detectors)
        {
            annotations = detector.detectAnnotationFor(method);
            if(annotations!=null)
                break;
        }

        return annotations;
    }

    private final GraphqlArgumentAnnotation detect(Method method , Parameter parameter , int index)
    {
        Chain<ParameterAnnotationDetector> detectors = parameterAnnotationDetectorChainMap.get(method);
        detectors=detectors==null?defaultParameterAnnotationDetectorChain:detectors;
        if(detectors==null)
            return null;

        GraphqlArgumentAnnotation annotations = null;

        for(ParameterAnnotationDetector detector:detectors)
        {
            annotations = detector.detectAnnotationFor(parameter , index);
            if(annotations!=null)
                break;
        }

        return annotations;
    }

    public Map<Class , Map<MappedClass.MappedType , MappedClass>> map(Collection<Class> classes)
    {
        HashMap<Class , Map<MappedClass.MappedType , MappedClass>> allMappedClasses = new HashMap<>();
        //so handle it please !

        for (Class cls:classes)
        {

            TypeAnnotations annotations = detect(cls);
            Assert.ifConditionTrue("no annotation found for class ["+cls+"]" ,
                    annotations==null);

            TypeValidator.validate(cls , annotations);

            Map<MappedClass.MappedType , MappedClass> allMappedClass =
                    allMappedClassFor(cls , annotations);

            Assert.ifConditionTrue("no annotation found for class ["+cls+"]" ,
                    allMappedClass.size()==0);
            allMappedClasses.put(cls , allMappedClass);
        }

        return allMappedClasses;

    }


    private Map<MappedClass.MappedType , MappedClass> allMappedClassFor(Class cls , TypeAnnotations annotations)
    {
        Map<MappedClass.MappedType , MappedClass> map = new HashMap<>();

        MappedClass mappedClass = mapClassIfNotNull(cls , annotations.typeAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);

        mappedClass = mapClassIfNotNull(cls , annotations.interfaceAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);

        mappedClass = mapClassIfNotNull(cls , annotations.inputTypeAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);

        mappedClass = mapClassIfNotNull(cls , annotations.unionAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);

        mappedClass = mapClassIfNotNull(cls , annotations.enumAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);

        mappedClass = mapClassIfNotNull(cls , annotations.queryAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);

        mappedClass = mapClassIfNotNull(cls , annotations.mutationAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);

        mappedClass = mapClassIfNotNull(cls , annotations.subscriptionAnnotation());
        if(mappedClass!=null)map.put(mappedClass.mappedType() , mappedClass);





        return map;
    }


    private MappedClass mapClassIfNotNull(Class cls , GraphqlInputTypeAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();
        for(Method method: getAllMethods(cls))
        {
            if(method.isSynthetic() ||
                    !Modifier.isPublic(method.getModifiers()))
                continue;

            FieldAnnotations annotations = detect(method);

            if(annotations==null)continue;


            if(annotations.inputFiledAnnotation()==null &&
                    (annotations.fieldAnnotation()==null||
                            !annotations.fieldAnnotation().inputField()))
            {
                continue;
            }

            FieldValidator.validate(cls , method , annotations);

            GraphqlFieldAnnotation fieldAnnotation = annotations.fieldAnnotation();
            GraphqlInputFieldAnnotation inputFieldAnnotation = annotations.inputFiledAnnotation();

            MappedMethod mappedMethod = null;

            if(inputFieldAnnotation!=null)
            {
                mappedMethod = mapInputFieldMethod(cls , method , inputFieldAnnotation);
            }else
            {
                mappedMethod = mapInputFieldMethod(cls , method , fieldAnnotation);
            }

            classBuilder.addMappedMethod(mappedMethod);
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.INPUT_TYPE)
                .build();
    }

    private MappedClass mapClassIfNotNull(Class cls , GraphqlTypeAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();
        for(Method method: getAllMethods(cls))
        {
            if(method.isSynthetic() ||
                    !Modifier.isPublic(method.getModifiers()))
                continue;

            FieldAnnotations annotations = detect(method);

            if(annotations==null)continue;

            GraphqlFieldAnnotation fieldAnnotation =
                    annotations.fieldAnnotation();

            if(fieldAnnotation==null)continue;

            System.out.println(method);

            FieldValidator.validate(cls , method , annotations);

            MappedMethod mappedMethod = mapFieldMethod(method , fieldAnnotation);

            classBuilder.addMappedMethod(mappedMethod);
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.OBJECT_TYPE)
                .build();
    }

    private MappedClass mapClassIfNotNull(Class cls , GraphqlInterfaceAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();
        for(Method method: getAllMethods(cls))
        {
            if(method.isSynthetic() ||
                    !Modifier.isPublic(method.getModifiers()))
                continue;

            FieldAnnotations annotations = detect(method);

            if(annotations==null)continue;

            GraphqlFieldAnnotation fieldAnnotation =
                    annotations.fieldAnnotation();

            if(fieldAnnotation==null)continue;

            FieldValidator.validate(cls , method , annotations);

            MappedMethod mappedMethod = mapFieldMethod(method , fieldAnnotation);

            classBuilder.addMappedMethod(mappedMethod);
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.INTERFACE)
                .build();
    }

    private MappedClass mapClassIfNotNull(Class cls , GraphqlUnionAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.UNION)
                .build();
    }

    private MappedClass mapClassIfNotNull(Class cls , GraphqlQueryAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();

        for(Method method: getAllMethods(cls))
        {
            if(method.isSynthetic() ||
                    !Modifier.isPublic(method.getModifiers()))
                continue;

            FieldAnnotations annotations = detect(method);

            if(annotations==null)continue;

            GraphqlFieldAnnotation fieldAnnotation =
                    annotations.fieldAnnotation();

            if(fieldAnnotation==null)continue;

            System.err.println(method);

            FieldValidator.validate(cls , method , annotations);

            MappedMethod mappedMethod = mapFieldMethod(method , fieldAnnotation);

            classBuilder.addMappedMethod(mappedMethod);
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.QUERY)
                .build();
    }

    private MappedClass mapClassIfNotNull(Class cls , GraphqlMutationAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();

        for(Method method: getAllMethods(cls))
        {
            if(method.isSynthetic() ||
                    !Modifier.isPublic(method.getModifiers()))
                continue;

            FieldAnnotations annotations = detect(method);

            if(annotations==null)continue;

            GraphqlFieldAnnotation fieldAnnotation =
                    annotations.fieldAnnotation();

            if(fieldAnnotation==null)continue;

            System.out.println(method);

            FieldValidator.validate(cls , method , annotations);

            MappedMethod mappedMethod = mapFieldMethod(method , fieldAnnotation);

            classBuilder.addMappedMethod(mappedMethod);
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.MUTATION)
                .build();
    }

    private MappedClass mapClassIfNotNull(Class cls , GraphqlSubscriptionAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();

        for(Method method: getAllMethods(cls))
        {
            if(method.isSynthetic() ||
                    !Modifier.isPublic(method.getModifiers()))
                continue;

            FieldAnnotations annotations = detect(method);

            if(annotations==null)continue;

            GraphqlFieldAnnotation fieldAnnotation =
                    annotations.fieldAnnotation();

            if(fieldAnnotation==null)continue;

            System.out.println(method);

            FieldValidator.validate(cls , method , annotations);

            MappedMethod mappedMethod = mapFieldMethod(method , fieldAnnotation);

            classBuilder.addMappedMethod(mappedMethod);
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.SUBSCRIPTION)
                .build();
    }

    private MappedClass mapClassIfNotNull(Class cls , GraphqlEnumAnnotation annotation)
    {
        if(annotation==null)
            return null;

        String name = Utils.stringNullifyOrGetDefault(annotation.typeName() , cls.getSimpleName());

        classBuilder.clearMappedMethods();

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(MappedClass.MappedType.ENUM)
                .build();
    }

    private MappedMethod mapFieldMethod(Method method , GraphqlFieldAnnotation annotation)
    {
        Assert.ifNull(annotation , "provided Annotation is null");



        MappingStatics.TypeDetails typeDetails =
                MappingStatics.findTypeDetails(method);

        String fieldName = Utils.stringNullifyOrGetDefault(annotation.fieldName() , method.getName());

        methodBuilder.clearMappedParameters();
        for (int index = 0 ;index<method.getParameters().length;index++)
        {
            methodBuilder.addMappedParameter(
                    mapParameter(method , method.getParameters()[index] , index)
            );
        }

        return methodBuilder
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setFieldName(fieldName)
                .setNullable(annotation.nullable())
                .setQueryHandler(typeDetails.isQueryHandler())
                .setMethod(method)
                .build();
    }

    private MappedMethod mapInputFieldMethod(Class cls , Method method , GraphqlInputFieldAnnotation annotation)
    {
        Assert.ifNull(annotation , "provided Annotation is null");
        MappingStatics.TypeDetails typeDetails = MappingStatics
                .findTypeDetails(method.getGenericReturnType());

        String fieldName = Utils.stringNullifyOrGetDefault(annotation.inputFieldName() , method.getName());
        String setter = annotation.setter();
        Assert.ifConditionTrue("no setter method set for input field ["+method+"]" ,
                Assert.isNullString(setter));

        Method setterMethod = null;

        try {
            setterMethod = cls.getMethod(setter , method.getReturnType());
            FieldValidator.validateSetterMethodModifier(setterMethod);

            Assert.ifConditionTrue("setter method not match with field method ["+method+
                    "]", !setterMethod.getParameters()[0].getParameterizedType()
                    .equals(method.getGenericReturnType()));

        } catch (NoSuchMethodException e) {

            throw new IllegalStateException("no setter method found for ["+method+"]");
        }

        methodBuilder.clearMappedParameters();
        for (int index = 0 ;index<method.getParameters().length;index++)
        {
            methodBuilder.addMappedParameter(
                    mapParameter(method , method.getParameters()[index] , index)
            );
        }

        return methodBuilder
                .setMethod(method)
                .setSetter(setterMethod)
                .setNullable(annotation.nullable())
                .setFieldName(fieldName)
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setQueryHandler(typeDetails.isQueryHandler())
                .build();

    }

    private MappedMethod mapInputFieldMethod(Class cls , Method method , GraphqlFieldAnnotation annotation)
    {
        Assert.ifNull(annotation , "provided Annotation is null");
        Assert.ifConditionTrue("provided annotation dose not contain input filed details" ,
                !annotation.inputField());
        MappingStatics.TypeDetails typeDetails = MappingStatics
                .findTypeDetails(method.getGenericReturnType());


        String fieldName = Utils.stringNullifyOrGetDefault(annotation.fieldName() , method.getName());
        String setter = annotation.setter();
        Assert.ifConditionTrue("no setter method set for input field ["+method+"]" ,
                Assert.isNullString(setter));

        Method setterMethod = null;

        try {
            setterMethod = cls.getMethod(setter , method.getReturnType());
            FieldValidator.validateSetterMethodModifier(setterMethod);

            Assert.ifConditionTrue("setter method not match with field method ["+method+
                    "]", !setterMethod.getParameters()[0].getParameterizedType()
                    .equals(method.getGenericReturnType()));

        } catch (NoSuchMethodException e) {

            throw new IllegalStateException("no setter method found for ["+method+"]");
        }


        methodBuilder.clearMappedParameters();
        for (int index = 0 ;index<method.getParameters().length;index++)
        {
            methodBuilder.addMappedParameter(
                    mapParameter(method , method.getParameters()[index] , index)
            );
        }

        return methodBuilder
                .setMethod(method)
                .setSetter(setterMethod)
                .setNullable(annotation.nullable())
                .setFieldName(fieldName)
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setQueryHandler(typeDetails.isQueryHandler())
                .build();

    }



    private MappedParameter mapParameter(Method method , Parameter parameter , int index)
    {
        GraphqlArgumentAnnotation argumentAnnotation = detect(method , parameter , index);
        Assert.ifNull(argumentAnnotation , "no annotation detected for [parameter:"
                +parameter+"  , method:"+method+"]");


        MappingStatics.TypeDetails typeDetails =
                MappingStatics.findTypeDetails(parameter.getParameterizedType());


        Assert.ifConditionTrue("can not detect typeName for parameter ["+parameter
                        +"] , because no typeName set in annotation and parameter typeName not present" ,
                !parameter.isNamePresent() , Assert.isNullString(argumentAnnotation.argumentName()));

        String name = Utils.stringNullifyOrGetDefault(argumentAnnotation.argumentName() , parameter.getName());

        return parameterBuilder.setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .setParameter(parameter)
                .setArgumentName(name)
                .setNullable(argumentAnnotation.nullable())
                .build();
    }


    private List<Method> getAllMethods(Class cls)
    {
        Method[] methods = cls.getDeclaredMethods();
        Method[] declaredMethods = cls.getDeclaredMethods();
        List<Method> list = new ArrayList<>();

        if(methods!=null)
        {
            for(Method method:methods) {
                list.add(method);
            }
        }

        if(declaredMethods!=null) {
            for (Method method : declaredMethods) {
                if(!list.contains(method))
                    list.add(method);
            }
        }

        return list;
    }
}
