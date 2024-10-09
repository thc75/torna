package cn.torna;

import cn.torna.service.metersphere.v3.model.state.MSModule;
import cn.torna.service.metersphere.v3.model.state.MSProject;
import cn.torna.service.metersphere.v3.util.MSClientUtils;
import cn.torna.service.metersphere.v3.model.state.AppSettingState;
import cn.torna.service.metersphere.v3.model.state.MSOrganization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author 六如
 */
public class MSApiUtilTest {

    static AppSettingState meterSphereSetting = new AppSettingState();
    static {
        meterSphereSetting.setMeterSphereAddress("http://localhost:8081");
        meterSphereSetting.setAccessKey("Ox8kbU6sFiU64FsB");
        meterSphereSetting.setSecretKey("tworDrUzYPExHt9T");
    }


    @Test
    public void testV3() throws Exception {
        boolean test = MSClientUtils.test(meterSphereSetting);
        System.out.println(test);
        Assertions.assertTrue(test);
    }

    @Test
    public void testListOrg() throws Exception {
        List<MSOrganization> organizationList1 = MSClientUtils.getOrganizationList(meterSphereSetting);
        System.out.println(organizationList1);
    }

    @Test
    public void testProject() throws Exception {
        List<MSProject> projectList = MSClientUtils.getProjectList(meterSphereSetting, "100001");
        // [MSProject(name=示例项目, id=100001100001, versionEnable=null)]
        System.out.println(projectList);
    }

    @Test
    public void testModule() throws Exception {
        List<MSModule> moduleList = MSClientUtils.getModuleList(meterSphereSetting, "100001100001");
        System.out.println(moduleList);
    }



}
