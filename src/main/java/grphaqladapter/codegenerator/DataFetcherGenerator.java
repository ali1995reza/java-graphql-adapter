package grphaqladapter.codegenerator;

import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredElement;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;

import java.util.List;

public interface DataFetcherGenerator {


    AdaptedDataFetcher generate(MappedTypeClass cls, MappedFieldMethod method);

    void init(List<DiscoveredElement> discoveredElements);
}
