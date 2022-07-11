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

package grphaqladapter.adaptedschema.tools.type_finder;

import grphaqladapter.adaptedschema.discovered.*;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import grphaqladapter.adaptedschema.utils.ClassUtils;
import grphaqladapter.adaptedschema.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TypeFinder {

    private final List<DiscoveredElement> elements;
    private final Map<String, DiscoveredElement> elementsByName;

    private final Map<Class, DiscoveredScalarType> scalarsByClass;
    private final Map<String, DiscoveredScalarType> scalarsByName;

    private final Map<Class, DiscoveredEnumType> enumsByClass;
    private final Map<String, DiscoveredEnumType> enumsByName;

    private final Map<Class, DiscoveredObjectType> objectTypesByClass;
    private final Map<String, DiscoveredObjectType> objectTypesByName;
    private final Map<Class, DiscoveredObjectType> deepObjectTypesByClass;

    private final Map<Class, DiscoveredInputType> inputTypesByClass;
    private final Map<String, DiscoveredInputType> inputTypesByName;

    private final Map<Class, DiscoveredInterfaceType> interfaceTypeByClass;
    private final Map<String, DiscoveredInterfaceType> interfaceTypesByName;

    private final Map<Class, DiscoveredUnionType> unionTypesByClass;
    private final Map<String, DiscoveredUnionType> unionTypesByName;

    private final Map<Class, DiscoveredDirective> directivesByClass;
    private final Map<String, DiscoveredDirective> directivesByName;


    public TypeFinder(List<DiscoveredElement> elements, boolean usePairTypesForEachOther) {

        this.elements = elements;
        this.elementsByName = separateByName(elements, DiscoveredElement.class);

        this.scalarsByClass = separateByClass(elements, DiscoveredScalarType.class);
        this.scalarsByName = separateByName(elements, DiscoveredScalarType.class);

        this.enumsByClass = separateByClass(elements, DiscoveredEnumType.class);
        this.enumsByName = separateByName(elements, DiscoveredEnumType.class);

        this.objectTypesByClass = separateByClass(elements, DiscoveredObjectType.class);
        this.objectTypesByName = separateByName(elements, DiscoveredObjectType.class);
        this.deepObjectTypesByClass = new ConcurrentHashMap<>(this.objectTypesByClass);

        this.inputTypesByClass = separateByClass(elements, DiscoveredInputType.class);
        this.inputTypesByName = separateByName(elements, DiscoveredInputType.class);

        this.interfaceTypeByClass = separateByClass(elements, DiscoveredInterfaceType.class);
        this.interfaceTypesByName = separateByName(elements, DiscoveredInterfaceType.class);

        this.unionTypesByClass = separateByClass(elements, DiscoveredUnionType.class);
        this.unionTypesByName = separateByName(elements, DiscoveredUnionType.class);

        this.directivesByClass = separateByClass(elements, DiscoveredDirective.class);
        this.directivesByName = separateByName(elements, DiscoveredDirective.class);

        if (usePairTypesForEachOther) {
            addPairTypes();
        }
    }

    public List<DiscoveredElement> elements() {
        return elements;
    }

    public DiscoveredDirective findDirectiveByClass(Class<? extends Annotation> clazz) {
        DiscoveredDirective directive = directivesByClass.get(clazz);
        if (directive != null) {
            return directive;
        }
        throw new IllegalStateException("can not find any directive for class [" + clazz + "]");
    }

    public DiscoveredDirective findDirectiveByName(String name) {
        DiscoveredDirective directive = directivesByName.get(name);
        if (directive != null) {
            return directive;
        }
        throw new IllegalStateException("can not find any directive with name [" + name + "]");
    }

    public DiscoveredElement findElementByName(String name) {
        DiscoveredElement element = objectTypesByName.get(name);
        if (element != null) {
            return element;
        }
        throw new IllegalStateException("can not find any element with name [" + name + "]");
    }

    public DiscoveredEnumType findEnumTypeByClass(Class clazz) {
        DiscoveredEnumType enumType = enumsByClass.get(clazz);
        if (enumType != null) {
            return enumType;
        }
        throw new IllegalStateException("can not find any enum-type for class [" + clazz + "]");
    }

    public DiscoveredEnumType findEnumTypeByName(String name) {
        DiscoveredEnumType enumType = enumsByName.get(name);
        if (enumType != null) {
            return enumType;
        }
        throw new IllegalStateException("can not find any enum-type with name [" + name + "]");
    }

    public DiscoveredInputType findInputTypeByClass(Class clazz) {
        DiscoveredInputType type = inputTypesByClass.get(clazz);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any input-type for class [" + clazz + "]");
    }

    public DiscoveredInputType findInputTypeByName(String name) {
        DiscoveredInputType type = inputTypesByName.get(name);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any input-type with name [" + name + "]");
    }

    public DiscoveredInterfaceType findInterfaceTypeByClass(Class clazz) {
        DiscoveredInterfaceType type = interfaceTypeByClass.get(clazz);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any interface-type for class [" + clazz + "]");
    }

    public DiscoveredInterfaceType findInterfaceTypeByName(String name) {
        DiscoveredInterfaceType type = interfaceTypesByName.get(name);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any interface-type with name [" + name + "]");
    }

    public DiscoveredObjectType findObjectTypeByClass(Class clazz) {
        DiscoveredObjectType type = objectTypesByClass.get(clazz);
        if (type != null) {
            return type;
        }

        throw new IllegalStateException("can not find any exact object-type for class [" + clazz + "]");
    }

    public DiscoveredObjectType findObjectTypeByClassDeeply(Class clazz) {
        DiscoveredObjectType type = deepObjectTypesByClass.computeIfAbsent(clazz, this::deeplySearchForObjectType);
        if (type != null) {
            return type;
        }

        throw new IllegalStateException("can not find any object-type for class [" + clazz + "] deeply");
    }

    public DiscoveredObjectType findObjectTypeByName(String name) {
        DiscoveredObjectType type = objectTypesByName.get(name);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any object-type with name [" + name + "]");
    }

    public DiscoveredScalarType findScalarTypeByClass(Class clazz) {
        DiscoveredScalarType scalarType = scalarsByClass.get(clazz);
        if (scalarType != null) {
            return scalarType;
        }
        throw new IllegalStateException("can not find any scalar-type for class [" + clazz + "]");
    }

    public DiscoveredScalarType findScalarTypeByName(String name) {
        DiscoveredScalarType scalarType = scalarsByName.get(name);
        if (scalarType != null) {
            return scalarType;
        }
        throw new IllegalStateException("can not find any scalar-type with name [" + name + "]");
    }

    public DiscoveredUnionType findUnionTypeByClass(Class clazz) {
        DiscoveredUnionType type = unionTypesByClass.get(clazz);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any union-type for class [" + clazz + "]");
    }

    public DiscoveredUnionType findUnionTypeByName(String name) {
        DiscoveredUnionType type = unionTypesByName.get(name);
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("can not find any union-type with name [" + name + "]");
    }

    public boolean isEnum(Class clazz) {
        return clazz.isEnum();
    }

    public boolean isScalar(Class clazz) {
        return scalarsByClass.containsKey(clazz);
    }

    public boolean isScalarOrEnum(Class clazz) {
        return isScalar(clazz) || isEnum(clazz);
    }

    private void addPairTypes() {
        for (Class clazz : captureClasses(inputTypesByClass)) {
            Class pair = ClassUtils.getPairType(clazz);
            if (pair == null || inputTypesByClass.containsKey(pair)) {
                continue;
            }
            inputTypesByClass.put(pair, inputTypesByClass.get(clazz));
        }

        for (Class clazz : captureClasses(scalarsByClass)) {
            Class pair = ClassUtils.getPairType(clazz);
            if (pair == null || scalarsByClass.containsKey(pair)) {
                continue;
            }
            scalarsByClass.put(pair, scalarsByClass.get(clazz));
        }
    }

    private List<Class> captureClasses(Map<Class, ? extends Object> map) {
        return map.keySet().stream().collect(Collectors.toList());
    }

    private DiscoveredObjectType deeplySearchForObjectType(Class<?> clazz) {
        return this.elements.stream().filter(element -> element.asMappedElement().mappedType().isObjectType())
                .map(element -> (DiscoveredObjectType) element)
                .filter(discoveredObjectType -> discoveredObjectType.asMappedElement().baseClass().isAssignableFrom(clazz))
                .findFirst()
                .get();
    }

    private static <T extends DiscoveredElement> Map<Class, T> separateByClass(List<DiscoveredElement> elements, Class<T> clazz) {
        return CollectionUtils.separateToMap(elements, clazz, element -> ((MappedClass) element.asMappedElement()).baseClass());
    }

    private static <T extends DiscoveredElement> Map<String, T> separateByName(List<DiscoveredElement> elements, Class<T> clazz) {
        return CollectionUtils.separateToMap(elements, clazz, DiscoveredElement::name);
    }
}
