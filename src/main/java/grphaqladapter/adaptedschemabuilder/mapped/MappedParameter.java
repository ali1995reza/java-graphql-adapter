package grphaqladapter.adaptedschemabuilder.mapped;

import java.lang.reflect.Parameter;

public interface MappedParameter extends MappedElement {

    enum Model {
        SCHEMA_ARGUMENT,
        ADAPTED_SCHEMA,
        DATA_FETCHING_ENVIRONMENT,
        DIRECTIVES,
        SKIPPED;

        public boolean is(Model other) {
            return this == other;
        }

        public boolean isSchemaArgument() {
            return is(SCHEMA_ARGUMENT);
        }

        public boolean isAdaptedSchema() {
            return is(ADAPTED_SCHEMA);
        }

        public boolean isDataFetchingEnvironment() {
            return is(DATA_FETCHING_ENVIRONMENT);
        }

        public boolean isDirectives() {
            return is(DIRECTIVES);
        }

        public boolean isSkipped() {
            return is(SKIPPED);
        }
    }

    Model model();

    Parameter parameter();

    TypeDescriptor type();
}
