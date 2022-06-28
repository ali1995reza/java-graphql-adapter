package grphaqladapter.adaptedschemabuilder;

import graphql.language.*;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredEnumType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputTypeClass;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectBuilder {

    private final ObjectConstructor objectConstructor;
    private final TypeFinder typeFinder;
    private final ConcurrentHashMap<ZeroDimArrayKey, Object> zeroDimArraysCache = new ConcurrentHashMap<>();

    public ObjectBuilder(ObjectConstructor objectConstructor, TypeFinder typeFinder) {
        this.objectConstructor = objectConstructor;
        this.typeFinder = typeFinder;
    }

    public Object buildFromObject(Class type, int dimensions, TypeDescriptor.DimensionModel dimensionModel, Object object) {
        try {
            if (dimensions > 0) {
                if (dimensionModel.isArray()) {
                    return buildArray((List) object, type, dimensions);
                } else {
                    return buildList((List) object, type, dimensions);
                }
            } else {
                return buildFromObject(object, type);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T buildFromObject(Class<T> type, Object object) {
        return (T) buildFromObject(type, 0, null, object);
    }

    public Object buildFromObject(TypeDescriptor descriptor, Object object) {
        return buildFromObject(descriptor.type(), descriptor.dimensions(), descriptor.dimensionModel(), object);
    }

    public Object buildFromValue(Class type, int dimensions, TypeDescriptor.DimensionModel dimensionModel, Value value) {
        try {
            if (dimensions > 0) {
                return buildList((ArrayValue) value, type, dimensions);
            } else {
                return buildFromValue(value, type);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T buildFromValue(Class<T> type, Value value) {
        return (T) buildFromValue(type, 0, null, value);
    }

    public Object buildFromValue(TypeDescriptor descriptor, Value value) {
        return buildFromValue(descriptor.type(), descriptor.dimensions(), descriptor.dimensionModel(), value);
    }

    private Object buildFromObject(Object o, Class clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (typeFinder.isScalarOrEnum(clazz)) {
            return o;
        } else {
            return buildFromMap((Map<String, Object>) o, typeFinder.findInputTypeByClass(clazz));
        }
    }

    private Object buildFromValue(Value value, Class clazz) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (typeFinder.isScalar(clazz)) {
            return typeFinder.findScalarTypeByClass(clazz)
                    .asMappedElement()
                    .coercing()
                    .parseLiteral(value);
        } else if (typeFinder.isEnum(clazz)) {
            DiscoveredEnumType enumType = typeFinder.findEnumTypeByClass(clazz);
            EnumValue enumValue = (EnumValue) value;
            return enumType.asMappedElement()
                    .constants()
                    .get(enumValue.getName())
                    .constant();
        } else {
            return buildFromObjectValue((ObjectValue) value, typeFinder.findInputTypeByClass(clazz));
        }
    }

    private Object buildFromMap(Map<String, Object> map, DiscoveredInputType inputType) throws InstantiationException, IllegalAccessException, InvocationTargetException {

        if (map == null) return null;

        Object instance = objectConstructor.getInstance(inputType.asMappedElement());
        MappedInputTypeClass mappedClass = inputType.asMappedElement();
        for (MappedInputFieldMethod method : mappedClass.inputFiledMethods().values()) {
            if (method.type().dimensions() > 0) {
                method.setter().invoke(instance, buildList((List) map.get(method.name()), method.type().type(), method.type().dimensions()));
            } else if (typeFinder.isScalarOrEnum(method.type().type())) {
                method.setter().invoke(instance, map.get(method.name()));
            } else {
                method.setter()
                        .invoke(instance, buildFromMap((Map) map.get(method.name()),
                                typeFinder.findInputTypeByClass(method.type().type())));
            }
        }

        return instance;
    }

    private Object buildFromObjectValue(ObjectValue value, DiscoveredInputType inputType) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (value == null) return null;

        Object instance = objectConstructor.getInstance(inputType.asMappedElement());
        for (ObjectField field : value.getObjectFields()) {
            MappedInputFieldMethod method = inputType.asMappedElement().inputFiledMethods().get(field.getName());
            if (method.type().dimensions() > 0) {
                method.setter().invoke(instance, buildList((ArrayValue) field.getValue(), method.type().type(), method.type().dimensions()));
            } else {
                method.setter().invoke(instance, buildFromValue(field.getValue(), method.type().type()));
            }
        }

        return instance;
    }

    private List buildList(List argList, Class clazz, int dim) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (argList == null)
            return null;

        if (argList.isEmpty()) {
            return Collections.emptyList();
        }

        if (typeFinder.isScalarOrEnum(clazz))
            return argList;


        --dim;
        List buildingList = new ArrayList();

        if (dim == 0) {
            for (Object o : argList) {
                buildingList.add(buildFromMap((Map<String, Object>) o, typeFinder.findInputTypeByClass(clazz)));
            }
        } else {
            for (Object o : argList) {
                buildingList.add(buildList((List) o, clazz, dim));
            }
        }

        return buildingList;
    }

    private List buildList(ArrayValue arrayValue, Class clazz, int dim) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (arrayValue == null)
            return null;

        if (arrayValue.getValues().isEmpty()) {
            return Collections.emptyList();
        }


        --dim;
        List buildingList = new ArrayList();

        if (dim == 0) {
            for (Value value : arrayValue.getValues()) {
                buildingList.add(buildFromValue(value, clazz));
            }
        } else {
            for (Value value : arrayValue.getValues()) {
                buildingList.add(buildList((ArrayValue) value, clazz, dim));
            }
        }

        return buildingList;
    }

    private Object buildArray(List argList, Class clazz, int dim) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (argList == null)
            return null;

        if (argList.isEmpty()) {
            return getZeroDimSizeArray(clazz, dim);
        }

        --dim;
        if (dim == 0) {
            Object array = Array.newInstance(clazz, argList.size());
            for (int i = 0; i < argList.size(); i++) {
                Object o = argList.get(i);
                Object object = buildFromObject(o, clazz);
                Array.set(array, i, object);
            }
            return array;
        } else {
            Object array = Array.newInstance(getZeroDimSizeArray(clazz, dim).getClass(), argList.size());
            for (int i = 0; i < argList.size(); i++) {
                List list = (List) argList.get(i);
                Object object = buildArray(list, clazz, dim);
                Array.set(array, i, object);
            }
            return array;
        }
    }

    private Object getZeroDimSizeArray(Class clazz, int dims) {
        return zeroDimArraysCache.computeIfAbsent(ZeroDimArrayKey.of(clazz, dims), this::buildZeroDimArray);
    }

    private Object buildZeroDimArray(ZeroDimArrayKey key) {
        int[] dimensions = new int[key.dimensions];
        for (int i = 0; i < key.dimensions; i++) {
            dimensions[i] = 0;
        }
        return Array.newInstance(key.type, dimensions);
    }

    private static class ZeroDimArrayKey {

        private static ZeroDimArrayKey of(Class clazz, int dimensions) {
            return new ZeroDimArrayKey(clazz, dimensions);
        }

        private final Class type;
        private final int dimensions;

        private ZeroDimArrayKey(Class type, int dimensions) {
            this.type = type;
            this.dimensions = dimensions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ZeroDimArrayKey that = (ZeroDimArrayKey) o;
            return dimensions == that.dimensions && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, dimensions);
        }
    }
}
