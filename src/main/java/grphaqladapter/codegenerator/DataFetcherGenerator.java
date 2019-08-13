package grphaqladapter.codegenerator;

import graphql.schema.DataFetcher;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;

import java.util.List;

public interface DataFetcherGenerator {


    DataFetcher generate(MappedClass cls , MappedMethod method);
    void init(List<DiscoveredInputType> inputTypes);
}