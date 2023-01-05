package cn.torna.service;

import cn.torna.service.builder.DefaultDocMd5Builder;
import cn.torna.service.builder.DocMd5Builder;

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
