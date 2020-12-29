package torna.manager.doc.postman;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class Url {

    private String protocol;
    private List<String> host;
    private String port;
    private List<String> path;
    private List<Param> query;

    public String getFullUrl() {
        String domain = String.join(".", host);
        String port = this.port == null ? "" : (":" + this.port);
        return protocol + "://" + domain + port + "/" + String.join("/", path);
    }

}
