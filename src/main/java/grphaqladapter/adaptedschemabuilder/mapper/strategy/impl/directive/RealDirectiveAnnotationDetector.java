package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.directive;

import grphaqladapter.adaptedschemabuilder.mapped.*;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.AppliedDirectiveDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.DirectiveArgumentsValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class RealDirectiveAnnotationDetector implements AppliedDirectiveDetector {

    @Override
    public List<DirectiveArgumentsValue> detectDirectives(MappedElement element, Map<Class, MappedAnnotation> annotationsMap) {
        if (element.mappedType().isOneOf(MappedElementType.FIELD, MappedElementType.INPUT_FIELD)) {
            MappedMethod method = (MappedMethod) element;
            return detectDirectives(method.method().getAnnotations(), annotationsMap);
        } else if (element.mappedType().isTopLevelType() || element.mappedType().isOneOf(MappedElementType.OBJECT_TYPE, MappedElementType.INPUT_TYPE, MappedElementType.UNION, MappedElementType.ENUM)) {
            MappedClass mappedClass = (MappedClass) element;
            return detectDirectives(mappedClass.baseClass().getAnnotations(), annotationsMap);
        } else if (element.mappedType().is(MappedElementType.ENUM_VALUE)) {
            MappedEnumConstant constant = (MappedEnumConstant) element;
            Field field = getField(constant);
            return detectDirectives(field.getAnnotations(), annotationsMap);
        } else if (element.mappedType().is(MappedElementType.ARGUMENT)) {
            if (element instanceof MappedParameter) {
                MappedParameter parameter = (MappedParameter) element;
                return detectDirectives(parameter.parameter().getAnnotations(), annotationsMap);
            } else if (element instanceof MappedAnnotationMethod) {
                MappedAnnotationMethod method = (MappedAnnotationMethod) element;
                return detectDirectives(method.method().getAnnotations(), annotationsMap);
            }
        }
        return null;
    }

    private List<DirectiveArgumentsValue> detectDirectives(Annotation[] annotations, Map<Class, MappedAnnotation> annotationsMap) {
        if(annotations == null || annotations.length == 0) {
            return Collections.emptyList();
        }
        List<DirectiveArgumentsValue> values = new ArrayList<>();
        for (Annotation annotation: annotations) {
            MappedAnnotation mappedAnnotation = annotationsMap.get(annotation.annotationType());
            if(mappedAnnotation == null) {
                continue;
            }
            values.add(getValues(annotation, mappedAnnotation));
        }
        return values;
    }

    private static Field getField(MappedEnumConstant constant) {
        try {
            return constant.constant().getDeclaringClass()
                    .getField(constant.constant().name());
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    private static DirectiveArgumentsValue getValues(Annotation a, MappedAnnotation annotation) {
        if (a == null) {
            return null;
        }
        if(annotation.mappedMethods().isEmpty()) {
            return DirectiveArgumentsValueImpl.empty(a.annotationType());
        }
        Map<String, Object> values = new HashMap<>();
        annotation.mappedMethods().values().forEach(
                method -> {
                    Object value = invoke(a, method.method());
                    if (value == null) {
                        return;
                    }
                    values.put(method.name(), value);
                }
        );
        return new DirectiveArgumentsValueImpl(values, a.annotationType());
    }

    private static Object invoke(Object o, Method method) {
        try {
            return method.invoke(o);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
