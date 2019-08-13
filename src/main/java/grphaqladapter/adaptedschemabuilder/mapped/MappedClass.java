package grphaqladapter.adaptedschemabuilder.mapped;


import java.util.Map;

public interface MappedClass {

    enum MappedType {
        ENUM ,
        INTERFACE ,
        OBJECT_TYPE,
        UNION ,
        INPUT_TYPE ,
        QUERY,
        MUTATION ,
        SUBSCRIPTION;


        public boolean is(MappedType other)
        {
            return this==other;
        }

        public boolean isTopLevelType(){
            return this==QUERY || this==MUTATION || this==SUBSCRIPTION;
        }
    }

    Class baseClass();

    MappedType mappedType();

    String typeName();

    Map<String , MappedMethod> mappedMethods();
}
