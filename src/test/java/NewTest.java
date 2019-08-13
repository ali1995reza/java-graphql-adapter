import graphql.cachecontrol.CacheControl;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.MergedField;
import graphql.execution.directives.QueryDirectives;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.OperationDefinition;
import graphql.schema.*;
import org.apache.bcel.Repository;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.util.BCELifier;
import org.dataloader.DataLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class NewTest {



    public static class MyClassLoader extends ClassLoader {

        public MyClassLoader(ClassLoader p)
        {
            super(p);
        }


        public Class buildCls(byte[] data) throws IOException {

            return defineClass("MyFetcher" ,data , 0  , data.length);
        }


        @Override
        protected void finalize() throws Throwable {
            System.out.println("FINILIZE");
            super.finalize();
        }
    };

    public static void checkIt() throws Exception
    {

        MyClassLoader loader = new MyClassLoader(ClassLoader.getSystemClassLoader());
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        new NewTest2().create(stream1);
        Class cls = loader.buildCls(stream1.toByteArray());
        Object o = cls.getFields()[0].get(null);
        DataFetcher oasf = (DataFetcher)o;
        Object res = oasf.get(new DataFetchingEnvironment() {
            @Override
            public <T> T getSource() {
                return null;
            }

            @Override
            public Map<String, Object> getArguments() {
                return null;
            }

            @Override
            public boolean containsArgument(String s) {
                return false;
            }

            @Override
            public <T> T getArgument(String s) {
                return null;
            }

            @Override
            public <T> T getContext() {
                return null;
            }

            @Override
            public <T> T getLocalContext() {
                return null;
            }

            @Override
            public <T> T getRoot() {
                return null;
            }

            @Override
            public GraphQLFieldDefinition getFieldDefinition() {
                return null;
            }

            @Override
            public List<Field> getFields() {
                return null;
            }

            @Override
            public MergedField getMergedField() {
                return null;
            }

            @Override
            public Field getField() {
                return null;
            }

            @Override
            public GraphQLOutputType getFieldType() {
                return null;
            }

            @Override
            public ExecutionStepInfo getExecutionStepInfo() {
                return null;
            }

            @Override
            public GraphQLType getParentType() {
                return null;
            }

            @Override
            public GraphQLSchema getGraphQLSchema() {
                return null;
            }

            @Override
            public Map<String, FragmentDefinition> getFragmentsByName() {
                return null;
            }

            @Override
            public ExecutionId getExecutionId() {
                return null;
            }

            @Override
            public DataFetchingFieldSelectionSet getSelectionSet() {
                return null;
            }

            @Override
            public QueryDirectives getQueryDirectives() {
                return null;
            }

            @Override
            public <K, V> DataLoader<K, V> getDataLoader(String s) {
                return null;
            }

            @Override
            public CacheControl getCacheControl() {
                return null;
            }

            @Override
            public OperationDefinition getOperationDefinition() {
                return null;
            }

            @Override
            public Document getDocument() {
                return null;
            }

            @Override
            public Map<String, Object> getVariables() {
                return null;
            }
        });
        System.out.println(res);

    }
    public static void main(String[] args) throws Exception
    {

        checkIt();
        System.gc();
        System.gc();
        System.gc();

        Thread.sleep(1000);

        System.exit(1);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BCELifier bceLifier = new BCELifier(Repository.lookupClass(Test.class) ,
                stream);

        bceLifier.start();

        System.out.println(new String(stream.toByteArray()));

    }
}
