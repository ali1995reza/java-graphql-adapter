package grphaqladapter.annotations.interfaces;

import graphql.language.OperationDefinition;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.mapped.*;

public abstract class GraphqlDirectiveFunction<T> {

    private GraphqlDirectiveDetails directive;

    public void preHandleFieldDirective(Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
    }

    public T handleFieldDirective(T value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return value;
    }

    public void preHandleFragmentDirective(Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
    }

    public T handleFragmentDirective(T value, Object source, MappedTypeClass type, MappedFieldMethod field, DataFetchingEnvironment env) {
        return value;
    }

    public void preHandleOperationDirective(Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
    }

    public T handleOperationDirective(T value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return value;
    }

    public GraphQLObjectType onObject(GraphQLObjectType objectType, MappedTypeClass mappedTypeClass, SchemaDirectiveHandlingContext context) {
        return objectType;
    }

    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        return fieldDefinition;
    }

    public GraphQLArgument onFieldArgument(GraphQLArgument argument, MappedTypeClass typeClass, MappedFieldMethod field, MappedParameter parameter, SchemaDirectiveHandlingContext context) {
        return argument;
    }

    public GraphQLArgument onDirectiveArgument(GraphQLArgument argument, MappedAnnotation annotationClass, MappedAnnotationMethod parameter, SchemaDirectiveHandlingContext context) {
        return argument;
    }

    public GraphQLInterfaceType onInterface(GraphQLInterfaceType interfaceType, MappedInterface mappedInterface, SchemaDirectiveHandlingContext context) {
        return interfaceType;
    }

    public GraphQLUnionType onUnion(GraphQLUnionType unionType, MappedUnionInterface mappedUnionInterface, SchemaDirectiveHandlingContext context) {
        return unionType;
    }

    public GraphQLEnumType onEnum(GraphQLEnumType enumType, MappedEnum mappedEnum, SchemaDirectiveHandlingContext context) {
        return enumType;
    }

    public GraphQLEnumValueDefinition onEnumValue(GraphQLEnumValueDefinition enumValueDefinition, MappedEnum mappedEnum, MappedEnumConstant enumConstant, SchemaDirectiveHandlingContext context) {
        return enumValueDefinition;
    }

    public GraphQLScalarType onScalar(GraphQLScalarType scalarType, MappedScalarClass mappedScalarClass, SchemaDirectiveHandlingContext context) {
        return scalarType;
    }

    public GraphQLInputObjectType onInputObjectType(GraphQLInputObjectType inputObjectType, MappedInputTypeClass mappedInputTypeClass, SchemaDirectiveHandlingContext context) {
        return inputObjectType;
    }

    public GraphQLInputObjectField onInputObjectField(GraphQLInputObjectField inputObjectField, MappedInputTypeClass mappedInputTypeClass, MappedInputFieldMethod inputField, SchemaDirectiveHandlingContext context) {
        return inputObjectField;
    }

    public final void setDirective(GraphqlDirectiveDetails directive) {
        if (this.directive != null) {
            throw new IllegalStateException("directive is set before");
        }
        this.directive = directive;
    }

    public GraphqlDirectiveDetails directive() {
        return directive;
    }
}
