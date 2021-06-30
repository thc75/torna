package cn.torna.manager.doc.postman;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class Url {

    private String protocol;
    private List<String> host;
    private String raw;
    private String port;
    private List<String> path;
    private List<Param> query;

    public String getFullUrl() {
        if (StringUtils.hasText(raw)) {
            return raw;
        }
        if (!CollectionUtils.isEmpty(path)) {
            return String.join("/", path);
        } else {
            return "";
        }
    }

}
