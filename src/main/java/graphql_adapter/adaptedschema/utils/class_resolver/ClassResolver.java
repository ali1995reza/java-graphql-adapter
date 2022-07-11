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
package graphql_adapter.adaptedschema.utils.class_resolver;

import graphql_adapter.adaptedschema.utils.class_resolver.filter.AnnotationFilter;
import graphql_adapter.adaptedschema.utils.class_resolver.filter.ClassFilter;
import graphql_adapter.adaptedschema.utils.class_resolver.filter.MultipleFilter;
import graphql_adapter.annotations.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassResolver {

    public static Set<Class<?>> getAllGraphqlAnnotatedClasses(String packageName, ClassFilter filter) {
        return getClasses(packageName, MultipleFilter.of(AnnotationFilter.annotatedWith(
                GraphqlEnum.class,
                GraphqlInputType.class,
                GraphqlInterface.class,
                GraphqlMutation.class,
                GraphqlQuery.class,
                GraphqlSubscription.class,
                GraphqlObjectType.class,
                GraphqlUnion.class,
                GraphqlScalar.class,
                GraphqlDirective.class
        ), filter));
    }

    public static Set<Class<?>> getAllGraphqlAnnotatedClasses(String packageName) {
        return getAllGraphqlAnnotatedClasses(packageName, ClassFilter.ACCEPT_ALL);
    }

    public static Set<Class<?>> getClasses(String packageName, ClassFilter filter) {
        if (filter == null) {
            filter = ClassFilter.ACCEPT_ALL;
        }
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream, "can not open stream to read classes from package [ " + packageName + " ]")));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(filter::accept)
                .collect(Collectors.toSet());
    }

    public static Set<Class<?>> getClasses(String packageName, ClassFilter... filters) {
        return getClasses(packageName, MultipleFilter.of(filters));
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName((isEmpty(packageName) ? "" : (packageName + "."))
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
