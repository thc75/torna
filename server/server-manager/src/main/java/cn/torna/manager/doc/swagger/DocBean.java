package cn.torna.manager.doc.swagger;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocBean {

    private String title;
    private String version;
    private String description;
    private String host;
    private String basePath;
    private String requestUrl;
    private List<Server> servers;
    private List<DocModule> docModules;

}
