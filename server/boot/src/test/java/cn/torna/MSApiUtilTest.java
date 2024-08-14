package cn.torna;

import cn.torna.service.metersphere.v3.util.MSClientUtils;
import cn.torna.service.metersphere.v3.state.AppSettingState;
import cn.torna.service.metersphere.v3.state.MSOrganization;
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


}
