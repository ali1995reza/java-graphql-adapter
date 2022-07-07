/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grphaqladapter.adaptedschema;

import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;
import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.functions.SchemaDirectiveHandlingContext;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElement;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import grphaqladapter.adaptedschema.utils.TypoUtils;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.util.*;

final class BuildingContextImpl implements BuildingContext {


    private final Map<Class, Map<MappedElementType, MappedElement>> mappedClasses;
    private final Map<MappedClass, GraphQLNamedType> rawTypes = new HashMap<>();
    private final Map<MappedClass, List<MappedClass>> possibleTypes = new HashMap<>();
    private final Map<MappedAnnotation, GraphQLDirective> directives = new HashMap<>();
    private final DirectiveHandlingContextImpl directiveHandlingContext;
    private final ObjectConstructor objectConstructor;
    private final boolean usePrimitivesPairTypesForEachOther;

    BuildingContextImpl(Map<Class, Map<MappedElementType, MappedElement>> mappedClasses,
                        GraphQLSchema.Builder schemaBuilder,
                        ObjectConstructor objectConstructor,
                        boolean usePrimitivesPairTypesForEachOther) {
        Assert.isNotNull(mappedClasses, new IllegalStateException("mapped classes is null"));
        Assert.isNotNull(schemaBuilder, new IllegalStateException("provided schema builder is null"));
        this.mappedClasses = mappedClasses;
        this.objectConstructor = objectConstructor;
        this.usePrimitivesPairTypesForEachOther = usePrimitivesPairTypesForEachOther;
        this.directiveHandlingContext = new DirectiveHandlingContextImpl(elementsByName(mappedClasses));
    }

    @Override
    public void addToPossibleTypesOf(MappedClass mappedClass, MappedClass possible) {

        if (mappedClass.mappedType() != MappedElementType.INTERFACE &&
                mappedClass.mappedType() != MappedElementType.UNION) {
            throw new IllegalStateException("just interfaces or union types have possible types");
        }
        if (mappedClasses.get(mappedClass.baseClass())
                .get(mappedClass.mappedType()) != mappedClass) {
            throw new IllegalStateException("this class dose not related to this context");
        }

        List<MappedClass> list = possibleTypes.get(mappedClass);

        if (list == null) {
            list = new ArrayList<>();
            possibleTypes.put(mappedClass, list);
        }

        list.add(possible);
    }

    @Override
    public SchemaDirectiveHandlingContext directiveHandlingContext() {
        return directiveHandlingContext;
    }

    @Override
    public GraphQLTypeReference geOutputTypeFor(Class c) {
        GraphQLTypeReference reference = getObjectTypeFor(c);
        if (reference != null) return reference;
        reference = getEnumFor(c);
        if (reference != null) return reference;
        reference = getUnionTypeFor(c);
        if (reference != null) return reference;
        reference = getInterfaceFor(c);
        if (reference != null) return reference;
        reference = getScalarTypeFor(c);

        return reference;
    }

    @Override
    public GraphQLDirective getDirectiveFor(Class c) {
        return null;
    }

    @Override
    public GraphQLTypeReference getEnumFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedElementType.ENUM);

        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.name());
    }

    @Override
    public GraphQLTypeReference getInputObjectTypeFor(Class c) {

        MappedClass mappedClass =
                getMappedClassFor(c, MappedElementType.INPUT_TYPE);
        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.name());
    }

    @Override
    public GraphQLTypeReference getPossibleInputTypeFor(Class c) {
        GraphQLTypeReference reference = getInputObjectTypeFor(c);
        if (reference != null) {
            return reference;
        }
        reference = getEnumFor(c);
        if (reference != null) {
            return reference;
        }
        reference = getScalarTypeFor(c);
        return reference;
    }

    @Override
    public GraphQLTypeReference getInterfaceFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedElementType.INTERFACE);


        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.name());
    }

    @Override
    public <T extends MappedClass> T getMappedClassFor(Class cls, MappedElementType mappedElementType) {
        if (mappedClasses.containsKey(cls)) {
            return (T) mappedClasses.get(cls).get(mappedElementType);
        }
        if (!usePrimitivesPairTypesForEachOther) {
            return null;
        }
        cls = TypoUtils.getPairType(cls);
        if (cls == null) {
            return null;
        }
        if (mappedClasses.containsKey(cls)) {
            return (T) mappedClasses.get(cls).get(mappedElementType);
        }
        return null;
    }

    @Override
    public GraphQLTypeReference getObjectTypeFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedElementType.OBJECT_TYPE);
        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.name());
    }

    @Override
    public GraphQLTypeReference getScalarTypeFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedElementType.SCALAR);


        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.name());
    }

    @Override
    public GraphQLTypeReference getUnionTypeFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c, MappedElementType.UNION);
        return mappedClass == null ? null : new GraphQLTypeReference(mappedClass.name());
    }

    @Override
    public ObjectConstructor objectConstructor() {
        return objectConstructor;
    }

    public Map<MappedAnnotation, GraphQLDirective> getDirectives() {
        return directives;
    }

    public List<MappedClass> possibleTypesOf(MappedClass mappedClass) {
        return possibleTypes.get(mappedClass);
    }

    public GraphQLNamedType rawTypeOf(MappedClass mappedClass) {
        return rawTypes.get(mappedClass);
    }

    public Map<MappedClass, GraphQLNamedType> rawTypes() {
        return rawTypes;
    }

    public void setDirective(MappedAnnotation mappedAnnotation, GraphQLDirective directive) {
        Assert.isFalse(directives.containsKey(mappedAnnotation), new IllegalStateException("class [" + mappedAnnotation + "] discovered multiple times"));
        directives.put(mappedAnnotation, directive);
    }

    public void setGraphQLTypeFor(MappedClass cls, GraphQLNamedType type) {

        Assert.isOneOrMoreFalse(new IllegalStateException("class [" + cls + "] discovered multiple times"),
                rawTypes.containsKey(cls));

        rawTypes.put(cls, type);
    }

    boolean isTypeExists(Class clazz, MappedElementType type) {
        if (!mappedClasses.containsKey(clazz)) {
            return false;
        }
        return mappedClasses.get(clazz).get(type) != null;
    }

    private static Map<String, MappedElement> elementsByName(Map<Class, Map<MappedElementType, MappedElement>> mappedClasses) {
        Map<String, MappedElement> elementMap = new HashMap<>();
        mappedClasses.forEach((clazz, map) -> {
            map.forEach((elementType, element) -> {
                Assert.isFalse(elementMap.containsKey(element.name()), new IllegalStateException("2 element with same name [" + element.name() + "]"));
                elementMap.put(element.name(), element);
            });
        });
        return Collections.unmodifiableMap(elementMap);
    }

}
