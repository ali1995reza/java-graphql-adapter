package grphaqladapter.parser;


import grphaqladapter.annotations.*;
import grphaqladapter.parser.filter.AnnotationFilter;
import grphaqladapter.parser.filter.ClassFilter;
import grphaqladapter.parser.filter.MultipleFilter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class PackageParser {

    public static Set<Class> getClasses(String packageName, ClassFilter filter) {
        if (filter == null) {
            filter = ClassFilter.ACCEPT_ALL;
        }
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Set<Class> classes = reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(filter::accept)
                .collect(Collectors.toSet());
        return classes;
    }

    public static Set<Class> getClasses(String packageName, ClassFilter... filters) {
        return getClasses(packageName, MultipleFilter.of(filters));
    }

    public static Set<Class> getAllGraphqlAnnotatedClasses(String packageName, ClassFilter filter) {
        return getClasses(packageName, MultipleFilter.of(AnnotationFilter.annotatedWith(
                GraphqlEnum.class,
                GraphqlInputType.class,
                GraphqlInterface.class,
                GraphqlMutation.class,
                GraphqlQuery.class,
                GraphqlSubscription.class,
                GraphqlType.class,
                GraphqlUnion.class
        ), filter));
    }

    public static Set<Class> getAllGraphqlAnnotatedClasses(String packageName) {
        return getAllGraphqlAnnotatedClasses(packageName, ClassFilter.ACCEPT_ALL);
    }

    private static Class getClass(String className, String packageName) {
        try {
            Class cls = Class.forName((isEmpty(packageName) ? "" : (packageName + "."))
                    + className.substring(0, className.lastIndexOf('.')));
            return cls;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }


    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
