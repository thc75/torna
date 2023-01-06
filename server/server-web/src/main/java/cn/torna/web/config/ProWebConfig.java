package cn.torna.web.config;

import cn.torna.service.DocMd5BuilderManager;
import cn.torna.service.builder.ProDocMd5Builder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author thc
 */
@Component
public class ProWebConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        DocMd5BuilderManager.setBuilder(new ProDocMd5Builder());
    }
}
