package tests.T1.schema;

import grphaqladapter.annotations.GraphqlType;

@GraphqlType
public class PageDetails {

    private final int page;
    private final int size;

    public PageDetails(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
