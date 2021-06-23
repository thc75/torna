package cn.torna.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tanghc
 */
@Data
@ConfigurationProperties(prefix = "torna.view-config")
public class TornaViewProperties {
    // old
    private boolean enableReg;
    private boolean enableThirdPartyForm;
    private boolean enableThirdPartyOauth;
    private boolean enableThirdPartyLogin;
    private String oauthLoginUrl;
    private String oauthButtonText;

    private String responseHiddenColumns;

    private Integer initOrder;
}
