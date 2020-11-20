package torna.web.controller.param;

import java.util.List;

/**
 * @author tanghc
 */
public class ModuleAllowMethodSetParam {
    private Long moduleId;
    private List<String> list;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
