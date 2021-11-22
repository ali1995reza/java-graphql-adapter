package grphaqladapter.parser;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class PackageParser {

    private final String packageName;

    public PackageParser(String p, ClassLoader loader) throws IOException {
        packageName = p;
        String converted = p.replace('.', File.separatorChar);
        Enumeration<URL> urls = loader.getResources(converted);
        while (urls.hasMoreElements()) {
            System.out.println(urls.nextElement());
        }
    }

    public PackageParser(String p) throws IOException {
        this(p, Thread.currentThread().getContextClassLoader());
    }
}
