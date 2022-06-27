package grphaqladapter.adaptedschemabuilder;

import grphaqladapter.adaptedschemabuilder.discovered.*;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TypeFinder {

    private final List<DiscoveredElement> elements;
    private final Map<String, DiscoveredElement> elementsByName;
    private final Map<String, DiscoveredDirective> directivesByName;
    private final Map<String, DiscoveredObjectType> objectTypesByName;
    private final Map<Class, DiscoveredInputType> inputTypesByClass;
    private final Map<Class, DiscoveredEnumType> enumsByClass;
    private final Map<Class, DiscoveredScalarType> scalarsByClass;
    private final Map<Class, Class> sameTypes = new HashMap<>();
    private final ConcurrentHashMap<Class, DiscoveredObjectType> objectTypeByClass = new ConcurrentHashMap<>();

    public TypeFinder(List<DiscoveredElement> elements) {
        this.elements = elements;
        this.elementsByName = elements.stream().collect(
                Collectors.toMap(
                        DiscoveredElement::name,
                        x -> x
                )
        );
        this.directivesByName = elements.stream().filter(element -> element.asMappedElement().mappedType().is(MappedElementType.DIRECTIVE))
                .collect(Collectors.toMap(
                        DiscoveredElement::name,
                        element -> (DiscoveredDirective) element
                ));
        this.inputTypesByClass = elements.stream().filter(element -> element.asMappedElement().mappedType().is(MappedElementType.INPUT_TYPE))
                .collect(Collectors.toMap(
                        element -> ((DiscoveredInputType) element).asMappedElement().baseClass(),
                        element -> (DiscoveredInputType) element
                ));
        this.objectTypesByName = elements.stream().filter(element -> element.asMappedElement().mappedType().is(MappedElementType.OBJECT_TYPE))
                .collect(Collectors.toMap(
                        DiscoveredElement::name,
                        element -> (DiscoveredObjectType) element
                ));
        this.enumsByClass = elements.stream().filter(element -> element.asMappedElement().mappedType().is(MappedElementType.ENUM))
                .collect(Collectors.toMap(
                        element -> ((DiscoveredEnumType) element).asMappedElement().baseClass(),
                        element -> (DiscoveredEnumType) element
                ));
        this.scalarsByClass = elements.stream().filter(element -> element.asMappedElement().mappedType().is(MappedElementType.SCALAR))
                .collect(Collectors.toMap(
                        element -> ((DiscoveredScalarType) element).asMappedElement().baseClass(),
                        element -> (DiscoveredScalarType) element
                ));
        initSameTypes();
    }

    private void initSameTypes() {
        sameTypes.put(Integer.class, int.class);
        sameTypes.put(Character.class, char.class);
        sameTypes.put(Double.class, double.class);
        sameTypes.put(Float.class, float.class);
        sameTypes.put(Long.class, long.class);
        sameTypes.put(Boolean.class, boolean.class);
        sameTypes.put(Byte.class, byte.class);
        sameTypes.put(Short.class, short.class);
        sameTypes.forEach((key, value) -> {
            if (scalarsByClass.containsKey(key)) {
                scalarsByClass.put(value, scalarsByClass.get(key));
            } else if (scalarsByClass.containsKey(value)) {
                scalarsByClass.put(key, scalarsByClass.get(value));
            }
        });
    }

    public DiscoveredElement findElementByName(String name) {
        DiscoveredElement element = objectTypesByName.get(name);
        if (element != null) {
            return element;
        }
        throw new IllegalStateException("can not find any element with name [" + name + "]");
    }

    public DiscoveredScalarType findScalarTypeByClass(Class clazz) {
        DiscoveredScalarType scalarType = scalarsByClass.get(clazz);
        if(scalarType != null) {
            return scalarType;
        }
        throw new IllegalStateException("can not find any scalar-type for class ["+clazz+"]");
    }

    public DiscoveredEnumType findEnumTypeByClass(Class clazz) {
        DiscoveredEnumType enumType = enumsByClass.get(clazz);
        if(enumType != null) {
            return enumType;
        }
        throw new IllegalStateException("can not find any enum-type for class ["+clazz+"]");
    }

    public DiscoveredObjectType findObjectTypeByName(String name) {
        DiscoveredObjectType type = objectTypesByName.get(name);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any object-type with name [" + name + "]");
    }


    public DiscoveredInputType findInputTypeByClass(Class clazz) {
        DiscoveredInputType type = inputTypesByClass.get(clazz);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any input-type for class [" + clazz + "]");
    }

    public DiscoveredDirective findDirectiveByName(String name) {
        DiscoveredDirective directive = directivesByName.get(name);
        if (directive != null) {
            return directive;
        }
        throw new IllegalStateException("can not find any directive with name [" + name + "]");
    }

    public boolean isScalar(Class clazz) {
        return scalarsByClass.containsKey(clazz);
    }

    public boolean isEnum(Class clazz) {
        return clazz.isEnum();
    }

    public boolean isScalarOrEnum(Class clazz) {
        return isScalar(clazz) || isEnum(clazz);
    }

    public DiscoveredObjectType findObjectTypeByObject(Object o) {
        DiscoveredObjectType type = objectTypeByClass.computeIfAbsent(o.getClass(), this::findFromClass);
        if (type != null) {
            return type;
        }

        throw new IllegalStateException("can not find any object type for object - class [" + o.getClass() + "]");
    }

    public Object getSkippedValue(Parameter parameter) {
        if (!parameter.getType().isPrimitive()) {
            return null;
        }
        if (parameter.getType() == boolean.class) {
            return false;
        }
        if (parameter.getType() == int.class) {
            return 0;
        }
        if (parameter.getType() == byte.class) {
            return (byte) 0;
        }
        if (parameter.getType() == long.class) {
            return (long) 0;
        }
        if (parameter.getType() == float.class) {
            return (float) 0;
        }
        if (parameter.getType() == double.class) {
            return (double) 0;
        }

        return (char) 0;
    }

    public List<DiscoveredElement> elements() {
        return elements;
    }

    private DiscoveredObjectType findFromClass(Class<?> clazz) {
        return this.elements.stream().filter(element -> element.asMappedElement().mappedType().is(MappedElementType.OBJECT_TYPE))
                .map(element -> (DiscoveredObjectType) element)
                .filter(discoveredObjectType -> discoveredObjectType.asMappedElement().baseClass().isAssignableFrom(clazz))
                .findFirst()
                .get();
    }
}
