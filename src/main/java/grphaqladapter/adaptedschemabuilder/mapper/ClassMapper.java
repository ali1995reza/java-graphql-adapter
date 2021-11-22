package grphaqladapter.adaptedschemabuilder.mapper;


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
    private Chain<MethodAnnotationDetector> methodAnnotationDetectorChain;
    private Chain<ParameterAnnotationDetector> parameterAnnotationDetectorChain;

    public ClassMapper() {
        classAnnotationDetectorChain = DEFAULT_CLASS_ANNOTATION_DETECTOR;
        methodAnnotationDetectorChain = DEFAULT_METHOD_ANNOTATION_DETECTOR;
        parameterAnnotationDetectorChain = DEFAULT_PARAMETER_ANNOTATION_DETECTOR;
    }

    public ClassMapper setClassAnnotationDetectorChain(Chain<ClassAnnotationDetector> classAnnotationDetectorChain) {
        Assert.ifNull(methodAnnotationDetectorChain , "provided chain is null");
        this.classAnnotationDetectorChain = classAnnotationDetectorChain;
        return this;
    }


    public ClassMapper setMethodAnnotationDetectorChain(Chain<MethodAnnotationDetector> methodAnnotationDetectorChain) {
        Assert.ifNull(methodAnnotationDetectorChain , "provided chain is null");
        this.methodAnnotationDetectorChain = methodAnnotationDetectorChain;
        return this;
    }

    public ClassMapper setParameterAnnotationDetectorChain(Chain<ParameterAnnotationDetector> parameterAnnotationDetectorChain) {
        Assert.ifNull(methodAnnotationDetectorChain , "provided chain is null");
        this.parameterAnnotationDetectorChain = parameterAnnotationDetectorChain;
        return this;
    }

    public ClassMapper reset()
    {
        classAnnotationDetectorChain = DEFAULT_CLASS_ANNOTATION_DETECTOR;
        methodAnnotationDetectorChain = DEFAULT_METHOD_ANNOTATION_DETECTOR;
        parameterAnnotationDetectorChain = DEFAULT_PARAMETER_ANNOTATION_DETECTOR;
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
    private final FieldAnnotations detect(Method method , Class clazz , MappedClass.MappedType mappedType)
    {

        FieldAnnotations annotations = null;

        for(MethodAnnotationDetector detector:methodAnnotationDetectorChain)
        {
            annotations = detector.detectAnnotationFor(method,clazz,mappedType);
            if(annotations!=null)
                break;
        }

        return annotations;
    }

    private final GraphqlArgumentAnnotation detect(Method method , Parameter parameter , int index)
    {

        GraphqlArgumentAnnotation annotations = null;

        for(ParameterAnnotationDetector detector:parameterAnnotationDetectorChain)
        {
            annotations = detector.detectAnnotationFor(method , parameter , index);
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

        return mapClass(cls , name , MappedClass.MappedType.INPUT_TYPE);
    }

    private MappedClass mapClass(Class cls , String name , MappedClass.MappedType type)
    {
        classBuilder.clearMappedMethods();

        if(type.isTopLevelType() || type.is(MappedClass.MappedType.OBJECT_TYPE) || type.is(MappedClass.MappedType.INTERFACE)) {
            for (Method method : MappingStatics.getAllMethods(cls)) {
                MappedMethod mappedMethod = mapFieldMethod(cls, method , type);

                if(mappedMethod==null)continue;

                classBuilder.addMappedMethod(mappedMethod);
            }
        }else if(type.is(MappedClass.MappedType.INPUT_TYPE))
        {
            for (Method method : MappingStatics.getAllMethods(cls)) {
                MappedMethod mappedMethod = mapInputFieldMethod(cls, method);

                if(mappedMethod==null)continue;

                classBuilder.addMappedMethod(mappedMethod);
            }
        }

        return classBuilder
                .setBaseClass(cls)
                .setTypeName(name)
                .setMappedType(type)
                .build();
    }


    private MappedClass mapClassIfNotNull(Class cls , Object a)
    {
        if(a==null)
            return null;

        if(a instanceof GraphqlTypeAnnotation)
        {
            GraphqlTypeAnnotation annotation = (GraphqlTypeAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.OBJECT_TYPE);
        }else if(a instanceof GraphqlInterfaceAnnotation)
        {
            GraphqlInterfaceAnnotation annotation = (GraphqlInterfaceAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                    cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.INTERFACE);
        }else if(a instanceof GraphqlInputTypeAnnotation)
        {
            GraphqlInputTypeAnnotation annotation = (GraphqlInputTypeAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                    cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.INPUT_TYPE);
        }else if(a instanceof GraphqlEnumAnnotation)
        {
            GraphqlEnumAnnotation annotation = (GraphqlEnumAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                    cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.ENUM);
        }else if(a instanceof GraphqlUnionAnnotation)
        {
            GraphqlUnionAnnotation annotation = (GraphqlUnionAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                    cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.UNION);
        }else if(a instanceof GraphqlQueryAnnotation)
        {
            GraphqlQueryAnnotation annotation = (GraphqlQueryAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                    cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.QUERY);
        }else if(a instanceof GraphqlMutationAnnotation)
        {
            GraphqlMutationAnnotation annotation = (GraphqlMutationAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                    cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.MUTATION);
        }else if(a instanceof GraphqlSubscriptionAnnotation)
        {
            GraphqlSubscriptionAnnotation annotation = (GraphqlSubscriptionAnnotation)a;
            String name = Utils.stringNullifyOrGetDefault(annotation.typeName() ,
                    cls.getSimpleName());

            return mapClass(cls , name , MappedClass.MappedType.SUBSCRIPTION);
        }else
        {
            throw new IllegalStateException("object not an annotation - [type:"+a.getClass()+"]");
        }
    }

    private MappedMethod mapFieldMethod(Class cls , Method method , MappedClass.MappedType mappedType)
    {

        FieldAnnotations annotations = detect(method , cls , mappedType);

        if(annotations==null || annotations.fieldAnnotation()==null)return null;


        FieldValidator.validate(cls , method , annotations);

        GraphqlFieldAnnotation annotation =
                annotations.fieldAnnotation();




        MappingStatics.TypeDetails typeDetails =
                MappingStatics.findTypeDetails(method);


        String fieldName = Utils.stringNullifyOrGetDefault(annotation.fieldName() , method.getName());



        String setter = annotation.setter();

        Method setterMethod = Assert.isNoNullString(setter)?
                detectSetter(setter , method , cls):null;

        methodBuilder.refresh();
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
                .setSetter(setterMethod)
                .setMethod(method)
                .build();
    }

    private MappedMethod mapInputFieldMethod(Class cls , Method method)
    {

        FieldAnnotations annotations = detect(method , cls , MappedClass.MappedType.INPUT_TYPE);


        if(annotations==null || (annotations.fieldAnnotation()==null &&
                annotations.inputFiledAnnotation()==null))return  null;


        FieldValidator.validate(cls , method , annotations);

        if(annotations.inputFiledAnnotation()==null && !annotations.fieldAnnotation().inputField())
            return null;
        
        GraphqlInputFieldAnnotation annotation =
                annotations.inputFiledAnnotation()!=null?
                        annotations.inputFiledAnnotation():
                        MappingStatics
                                .convertFieldAnnotationToInputFieldAnnotation(annotations.fieldAnnotation());


        Assert.ifNull(annotation , "provided Annotation is null");
        MappingStatics.TypeDetails typeDetails = MappingStatics
                .findTypeDetails(method.getGenericReturnType());

        String setter = annotation.setter();
        Assert.ifConditionTrue("no setter method set for input field ["+method+"]" ,
                Assert.isNullString(setter));


        String fieldName = Utils.stringNullifyOrGetDefault(annotation.inputFieldName() , method.getName());

        Method setterMethod = detectSetter(setter , method , cls);



        methodBuilder.refresh();

        //cant contains parameters
        /*
        for (int index = 0 ;index<method.getParameters().length;index++)
        {
            methodBuilder.addMappedParameter(
                    mapParameter(method , method.getParameters()[index] , index)
            );
        }*/

        return methodBuilder
                .setMethod(method)
                .setSetter(setterMethod)
                .setNullable(annotation.nullable())
                .setFieldName(fieldName)
                .setDimensions(typeDetails.dimension())
                .setType(typeDetails.type())
                .build();

    }


    private Method detectSetter(String setter , Method field , Class cls)
    {
        try {
            Method setterMethod = cls.getMethod(setter , field.getReturnType());
            FieldValidator.validateSetterMethodModifier(setterMethod);

            Assert.ifConditionTrue("setter method not match with field method ["+field+
                    "]", !setterMethod.getParameters()[0].getParameterizedType()
                    .equals(field.getGenericReturnType()));

            return setterMethod;
        } catch (NoSuchMethodException e) {

            throw new IllegalStateException("no setter method found for ["+field+"]");
        }
    }



    private MappedParameter mapParameter(Method method , Parameter parameter , int index)
    {
        GraphqlArgumentAnnotation argumentAnnotation = detect(method , parameter , index);
        Assert.ifNull(argumentAnnotation , "no annotation detected for [parameter:"
                +parameter+"]");


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


}
