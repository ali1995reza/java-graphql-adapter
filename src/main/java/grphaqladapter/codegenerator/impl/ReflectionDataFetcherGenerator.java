package grphaqladapter.codegenerator.impl;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.codegenerator.DataFetcherGenerator;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ReflectionDataFetcherGenerator implements DataFetcherGenerator {


    private List<DiscoveredType> allTypes;


    @Override
    public DataFetcher generate(MappedClass cls, MappedMethod method, ObjectConstructor constructor) {
        return new ReflectionDataFetcher(cls, method, allTypes, constructor);
    }

    @Override
    public void init(List<DiscoveredType> allTypes) {
        this.allTypes = allTypes;
    }

    private final static class ReflectionDataFetcher implements DataFetcher {

        private final static Map<Class, Class> SameTypes = new HashMap<>();

        static {
            SameTypes.put(Integer.class, int.class);
            SameTypes.put(Character.class, char.class);
            SameTypes.put(Double.class, double.class);
            SameTypes.put(Float.class, float.class);
            SameTypes.put(Long.class, long.class);
            SameTypes.put(Boolean.class, boolean.class);
            SameTypes.put(Byte.class, byte.class);
            SameTypes.put(Short.class, short.class);
        }

        private final MappedClass mappedClass;
        private final MappedMethod method;
        private final Map<Class, DiscoveredInputType> inputTypesMap;
        private final Set<Class> scalars;
        private final List<MappedParameter> parameters;
        private final ObjectConstructor objectConstructor;

        private ReflectionDataFetcher(MappedClass mappedClass, MappedMethod mappedMethod, List<DiscoveredType> allTypes, ObjectConstructor objectConstructor) {

            this.mappedClass = mappedClass;
            this.method = mappedMethod;
            this.objectConstructor = objectConstructor;

            Map<Class, DiscoveredInputType> map = new HashMap<>();
            for (DiscoveredType t : allTypes) {
                if (t instanceof DiscoveredInputType) {
                    map.put(t.asMappedClass().baseClass(), (DiscoveredInputType) t);
                }
            }

            HashSet<Class> scalars = new HashSet<>();

            for (DiscoveredType t : allTypes) {
                if (t instanceof DiscoveredScalarType) {
                    scalars.add(t.asMappedClass().baseClass());
                }
            }

            SameTypes.forEach((key, value) -> {

                if (scalars.contains(key)) {
                    scalars.add(value);
                } else if (scalars.contains(value)) {
                    scalars.add(key);
                }

            });

            this.scalars = Collections.unmodifiableSet(scalars);
            this.inputTypesMap = Collections.unmodifiableMap(map);
            this.parameters = method.parameters();
        }


        private Object buildUsingMap(Map<String, Object> map, DiscoveredInputType inputType) throws InstantiationException, IllegalAccessException, InvocationTargetException {

            if (map == null) return null;

            Object instance = objectConstructor.getInstance(inputType.asMappedClass());
            MappedClass mappedClass = inputType.asMappedClass();
            for (MappedMethod method : mappedClass.mappedMethods().values()) {
                if (method.isList()) {
                    method.setter().invoke(instance, buildList((List) map.get(method.fieldName()), method));

                } else if (scalars.contains(method.type()) || method.type().isEnum()) {

                    method.setter().invoke(instance, map.get(method.fieldName()));
                } else {
                    method.setter()
                            .invoke(instance, buildUsingMap((Map) map.get(method.fieldName()),
                                    inputTypesMap.get(method.type())));
                }
            }

            return instance;
        }

        private List buildList(List argList, MappedParameter parameter, int dim) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            if (argList == null)
                return null;
            if (argList.size() < 1) {
                return new ArrayList(0);
            }

            if (scalars.contains(parameter.type()) || parameter.type().isEnum())
                return argList;


            --dim;
            List buildingList = new ArrayList();

            if (dim == 0) {
                for (Object o : argList) {
                    buildingList.add(buildUsingMap((Map<String, Object>) o, inputTypesMap.get(parameter.type())));
                }
            } else {
                for (Object o : argList) {
                    buildingList.add(buildList((List) o, parameter, dim));
                }
            }

            return buildingList;
        }

        private List buildList(List argList, MappedParameter parameter) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return buildList(argList, parameter, parameter.dimensions());
        }


        private List buildList(List argList, MappedMethod method, int dim) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            if (argList == null)
                return null;
            if (argList.size() < 1) {
                return new ArrayList(0);
            }

            if (scalars.contains(method.type()) || method.type().isEnum())
                return argList;


            --dim;
            List buildingList = new ArrayList();

            if (dim == 0) {
                for (Object o : argList) {
                    buildingList.add(buildUsingMap((Map<String, Object>) o, inputTypesMap.get(method.type())));
                }
            } else {
                for (Object o : argList) {
                    buildingList.add(buildList((List) o, method, dim));
                }
            }

            return buildingList;
        }

        private List buildList(List argList, MappedMethod method) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return buildList(argList, method, method.dimensions());
        }

        @Override
        public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

            final Object source = dataFetchingEnvironment.getSource() != null ? dataFetchingEnvironment.getSource() : objectConstructor.getInstance(mappedClass);

            if (parameters == null || parameters.size() < 1) {

                Object result = method.method().invoke(source);
                return result;

            } else {
                Object[] args = new Object[parameters.size()];
                for (int i = 0; i < parameters.size(); i++) {
                    MappedParameter parameter = parameters.get(i);
                    if (parameter.isEnv()) {
                        args[i] = dataFetchingEnvironment;
                    } else if (parameter.isList()) {

                        args[i] = buildList(dataFetchingEnvironment.getArgument(parameter.argumentName()),
                                parameter);

                    } else if (scalars.contains(parameter.type()) || parameter.type().isEnum()) {
                        args[i] = dataFetchingEnvironment.getArgument(parameter.argumentName());
                    } else {
                        args[i] = buildUsingMap(dataFetchingEnvironment.getArgument(parameter.argumentName()),
                                inputTypesMap.get(parameter.type()));
                    }
                }

                Object result = method.method().invoke(source, args);
                return result;
            }
        }
    }

}
