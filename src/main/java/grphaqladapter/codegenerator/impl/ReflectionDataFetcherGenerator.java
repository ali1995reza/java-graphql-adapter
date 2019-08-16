package grphaqladapter.codegenerator.impl;

import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.GraphqlQueryHandler;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredType;
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
        private final Set<Class> scalars;
        private final List<MappedParameter> parameters;
        private final Object source;

        private ReflectionDataFetcher(MappedMethod m, List<DiscoveredType> allTypes, Object source)
        {
            method = m;
            this.source = source;

            Map<Class , DiscoveredInputType> map = new HashMap<>();

            for(DiscoveredType t:allTypes){

                if(t instanceof DiscoveredInputType) {
                    map.put(t.asMappedClass().baseClass(), (DiscoveredInputType) t);
                }
            }

            HashSet<Class> scalars = new HashSet<>();

            for(DiscoveredType t:allTypes)
            {
                if(t instanceof DiscoveredScalarType) {
                    scalars.add(t.asMappedClass().baseClass());
                }
            }

            this.scalars = Collections.unmodifiableSet(scalars);

            inputTypesMap = Collections.unmodifiableMap(map);
            parameters = method.parameters();
        }



        private <T> T crateNewInstance(Class<T> cls) throws IllegalAccessException, InstantiationException {
            return cls.newInstance();
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
                    if(scalars.contains(method.type()))
                    {
                        method.setter()
                                .invoke(instance, map.get(method.fieldName()));

                    }else {

                        method.setter().invoke(instance, buildList((List) map.get(method.fieldName()),
                                        inputTypesMap.get(method.type())));
                    }
                }else if(scalars.contains(method.type()))
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
                        if (scalars.contains(method.type())) {
                            args[i] = dataFetchingEnvironment.getArgument(parameter.argumentName());
                        } else {
                            args[i] = buildList(dataFetchingEnvironment.getArgument(parameter.argumentName()),
                                    inputTypesMap.get(parameter.type()));
                        }
                    } else if (scalars.contains(method.type()) || parameter.type().isEnum()) {
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

        }
    }



    private List<DiscoveredType> allTypes;


    @Override
    public DataFetcher generate(MappedClass cls, MappedMethod method) {

        if(cls.mappedType().isTopLevelType()) {
            try {
                System.err.println(cls.baseClass());
                Object source = cls.baseClass().newInstance();
                return new ReflectionDataFetcher(method, allTypes, source);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }else {
            return new ReflectionDataFetcher(method , allTypes , null);
        }
    }

    @Override
    public void init(List<DiscoveredType> allTypes) {
        this.allTypes = allTypes;
    }

}
