package cn.torna.service.metersphere.v3.state;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 存储映射类
 */
@Data
public class AppSettingState {
    private String userId;

    private String meterSphereAddress;

    private String accessKey;

    private String secretKey;

    private List<MSOrganization> organizationOptions;

    private MSOrganization organization;

    private List<MSProject> projectOptions;

    private MSProject project;

    private List<MSModule> moduleOptions;

    private MSModule module;

    private String exportModuleName;

    //是否覆盖模块 coverModule
    private CoverModule coverModule;

    private List<CoverModule> coverModuleList;

    public String getMeterSphereAddress() {
        if (StringUtils.isNotBlank(this.meterSphereAddress)) {
            if (this.meterSphereAddress.endsWith("/")) {
                return this.meterSphereAddress.substring(0, this.meterSphereAddress.length() - 1);
            }
        }
        return this.meterSphereAddress;
    }

    /**
     * 清空
     */
    public void clear() {
        projectOptions = null;
        project = null;
        moduleOptions = null;
        module = null;
    }
}
