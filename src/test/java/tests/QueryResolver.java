package tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class QueryResolver {

    public static String getQuery(String filename) {
        InputStream stream = getResource("/queries/" + filename + ".txt");
        try {
            return readString(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static InputStream getResource(String name) {
        return QueryResolver.class.getResourceAsStream(name);
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    public static String readString(InputStream stream) throws IOException {
        return new String(readAllBytes(stream));
    }
}
