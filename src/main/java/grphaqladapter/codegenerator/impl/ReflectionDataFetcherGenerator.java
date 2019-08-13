package grphaqladapter.codegenerator.impl;

import grphaqladapter.adaptedschemabuilder.GraphqlQueryHandler;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.codegenerator.DataFetcherGenerator;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;


import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ReflectionDataFetcherGenerator implements DataFetcherGenerator {


    private final static class ReflectionDataFetcher implements DataFetcher{

        private final MappedMethod method;
        private final Map<Class, DiscoveredInputType> inputTypesMap;
        private final List<MappedParameter> parameters;
        private final Object source;

        private ReflectionDataFetcher(MappedMethod m, List<DiscoveredInputType> inputTypes, Object source)
        {
            method = m;
            this.source = source;

            Map<Class , DiscoveredInputType> map = new HashMap<>();

            for(DiscoveredInputType inputType:inputTypes){
                map.put(inputType.asMappedClass().baseClass() , inputType);
            }

            inputTypesMap = Collections.unmodifiableMap(map);
            parameters = method.parameters();
        }



        private <T> T crateNewInstance(Class<T> cls) throws IllegalAccessException, InstantiationException {
            return cls.newInstance();
        }

        private final static boolean isScalar(Class c)
        {
            return c==String.class||c==Integer.class||c==int.class||c==Float.class||c==float.class
                    ||c==Double.class||c==double.class||c==Character.class||c==char.class
                    ||c==Long.class||c==long.class||c==Byte.class||c==byte.class
                    ||c==Short.class||c==short.class;
        }


        private final Object buildUsingMap(Map<String , Object> map , DiscoveredInputType inputType) throws InstantiationException, IllegalAccessException, InvocationTargetException {

            if(map==null)return null;

            Object instance = inputType.asMappedClass().baseClass().newInstance();
            MappedClass mappedClass = inputType.asMappedClass();
            for(MappedMethod method:mappedClass.mappedMethods().values())
            {

                if(method.isList())
                {
                    //so lets handle it again!
                    if(isScalar(method.type()))
                    {
                        method.setter()
                                .invoke(instance, map.get(method.fieldName()));

                    }else {

                        method.setter().invoke(instance, buildList((List) map.get(method.fieldName()),
                                        inputTypesMap.get(method.type())));
                    }
                }else if(isScalar(method.type()))
                {
                    method.setter()
                            .invoke(instance , map.get(method.fieldName()));
                }else
                {
                    method.setter()
                            .invoke(instance , buildUsingMap((Map)map.get(method.fieldName()) ,
                                    inputTypesMap.get(method.type())));
                }
            }

            return instance;
        }
        private final List buildList(List argList , DiscoveredInputType inputType) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            if(argList==null)
                return null;
            if(argList.size()<1)
            {
                return new ArrayList(0);
            }else
            {

                List buildingList = new ArrayList();
                if(argList.get(0) instanceof List)
                {
                    //so inner list her !
                    for(int i=0;i<argList.size();i++)
                    {
                        buildingList.add(buildList((List) argList.get(i) ,inputType));
                    }
                }else {
                    //so it must be a map really !
                    //i dont care guys !
                    for(Object o:argList)
                    {
                        Map<String  , Object> map = (Map)o;
                        buildingList.add(
                                buildUsingMap(map , inputType)
                        );
                    }
                }

                return buildingList;
            }
        }
        @Override
        public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

            final Object source = this.source!=null?this.source:dataFetchingEnvironment.getSource();
            try {
                if (parameters == null || parameters.size() < 1) {

                    Object result = method.method().invoke(source);

                    if(method.isQueryHandler())
                    {
                        GraphqlQueryHandler handler = (GraphqlQueryHandler)result;
                        handler.initialize(dataFetchingEnvironment.getSelectionSet());
                        return handler.execute();
                    }

                    return result;
                } else {
                    Object[] args = new Object[parameters.size()];
                    for (int i = 0; i < parameters.size(); i++) {
                        MappedParameter parameter = parameters.get(i);
                        if (parameter.isList()) {
                            if (isScalar(parameter.type())) {
                                args[i] = dataFetchingEnvironment.getArgument(parameter.argumentName());
                            } else {
                                args[i] = buildList(dataFetchingEnvironment.getArgument(parameter.argumentName()),
                                        inputTypesMap.get(parameter.type()));
                            }
                        } else if (isScalar(parameter.type())) {
                            args[i] = dataFetchingEnvironment.getArgument(parameter.argumentName());
                        } else {
                            args[i] = buildUsingMap(dataFetchingEnvironment.getArgument(parameter.argumentName()),
                                    inputTypesMap.get(parameter.type()));
                        }
                    }

                    Object result = method.method().invoke(source , args);

                    if(method.isQueryHandler())
                    {
                        GraphqlQueryHandler handler = (GraphqlQueryHandler)result;
                        handler.initialize(dataFetchingEnvironment.getSelectionSet());
                        return handler.execute();
                    }

                    return result;
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                throw e;
            }

        }
    }


    private final static boolean isTopLevelType(MappedClass.MappedType type)
    {
        return type.is(MappedClass.MappedType.QUERY)||
                type.is(MappedClass.MappedType.MUTATION)||
                type.is(MappedClass.MappedType.SUBSCRIPTION);
    }


    private List<DiscoveredInputType> inputTypes;


    @Override
    public DataFetcher generate(MappedClass cls, MappedMethod method) {

        if(isTopLevelType(cls.mappedType())) {
            try {
                Object source = cls.baseClass().newInstance();
                return new ReflectionDataFetcher(method, inputTypes, source);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }else {
            return new ReflectionDataFetcher(method , inputTypes , null);
        }
    }

    @Override
    public void init(List<DiscoveredInputType> inputTypes) {
        this.inputTypes = inputTypes;
    }
}
