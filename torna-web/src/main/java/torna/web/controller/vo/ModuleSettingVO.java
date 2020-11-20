package torna.web.controller.vo;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ModuleSettingVO {
    private ModuleVO moduleVO;
    private List<ModuleConfigVO> globalHeaders;
    private List<String> allowMethods;
    private String debugHost;

}
