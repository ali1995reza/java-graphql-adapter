package grphaqladapter.adaptedschemabuilder.mapper;

import grphaqladapter.adaptedschemabuilder.GraphqlQueryHandler;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class MappingStatics {

    public static final class TypeDetails {

        private final Class type;
        private final int dimension;
        private final boolean isQueryHandler;

        private TypeDetails(Class type, int dimension, boolean isQueryHandler) {
            this.isQueryHandler = isQueryHandler;
            Assert.ifNegative(dimension , "an array dimension can not be <0");
            Assert.ifNull(type , "provided type is null");
            this.type = type;
            this.dimension = dimension;
        }


        public Class type() {
            return type;
        }

        public int dimension() {
            return dimension;
        }

        public boolean isQueryHandler() {
            return isQueryHandler;
        }

        @Override
        public String toString() {
            return "[type:"+type+" , dims:"+dimension+" , query-handler:"+isQueryHandler+"]";
        }
    }

    public static TypeDetails findTypeDetails(Method method)
    {
        Type type = method.getGenericReturnType();

        if(method.getReturnType()==GraphqlQueryHandler.class)
        {
            if(type instanceof ParameterizedType)
            {
                //so handle it please !
                ParameterizedType paraType =  (ParameterizedType)type;
                Type inner = paraType.getActualTypeArguments()[0];
                TypeDetails details =  findTypeDetails(inner);

                return new TypeDetails(details.type , details.dimension , true);

            }else
            {
                return new TypeDetails(Object.class , 0 , true);
            }
        }


        return findTypeDetails(type);
    }

    public static TypeDetails findTypeDetails(Parameter parameter)
    {
        return findTypeDetails(parameter.getParameterizedType());
    }


    public static TypeDetails findTypeDetails(Type type)
    {
        if(type==null)
            return null;

        if(type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if(parameterizedType.getRawType()!=List.class)
                return new TypeDetails((Class) parameterizedType.getRawType(), 0 , false);

            Type innerType = parameterizedType.getActualTypeArguments()[0];
            return detectDimsDetails(innerType , 1);
        }else
        {
            if(type==List.class)
                return new TypeDetails(Object.class , 1, false);
            else
                return new TypeDetails((Class) type, 0, false);
        }


    }

    private static final TypeDetails detectDimsDetails(Type type , int dims)
    {
        if(type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if(parameterizedType.getRawType()==List.class)
            {
                return detectDimsDetails(parameterizedType.getActualTypeArguments()[0]  , dims+1);
            }else {
                return new TypeDetails((Class)parameterizedType.getRawType() , dims, false);
            }
        }else
        {
            if(type==List.class)
                return new TypeDetails(Object.class , dims+1, false);
            else
                return new TypeDetails((Class)type , dims, false);
        }
    }


}
