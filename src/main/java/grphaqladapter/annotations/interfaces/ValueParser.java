package grphaqladapter.annotations.interfaces;

import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;

public interface ValueParser<I, O> {

    O parse(I input, TypeDescriptor descriptor);

    class DefaultParser implements ValueParser<Object, Object> {
        @Override
        public Object parse(Object input, TypeDescriptor descriptor) {
            return input;
        }
    }
}
