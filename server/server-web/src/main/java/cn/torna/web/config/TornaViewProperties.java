package cn.torna.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tanghc
 */
@Data
@ConfigurationProperties(prefix = "torna.view-config")
public class TornaViewProperties {
    private String responseHiddenColumns;
}
