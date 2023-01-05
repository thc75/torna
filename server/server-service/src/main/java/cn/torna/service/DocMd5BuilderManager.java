package cn.torna.service;

/**
 * @author thc
 */
public class DocMd5BuilderManager {

    private static DocMd5Builder builder = new DefaultDocMd5Builder();

    public static DocMd5Builder getBuilder() {
        return builder;
    }

    public static void setBuilder(DocMd5Builder builder) {
        DocMd5BuilderManager.builder = builder;
    }
}
