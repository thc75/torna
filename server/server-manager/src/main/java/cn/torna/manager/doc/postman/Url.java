package cn.torna.manager.doc.postman;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Data
public class Url {

    private String protocol;
    private List<String> host;
    private String raw;
    private String port;
    private List<String> path;
    /** path 参数 */
    private List<Param> variable;
    private List<Param> query;

    public String getFullUrl() {
        if (!CollectionUtils.isEmpty(path)) {
            return "/" + path.stream()
                    .map(p -> {
                        // :id
                        if (p.startsWith(":")) {
                            return "{" + p.substring(1) + "}";
                        }
                        return p;
                    })
                    .collect(Collectors.joining("/"));
        }
        if (StringUtils.hasText(raw)) {
            return raw;
        }
        return "";
    }

}
