package cn.torna;

import cn.torna.service.metersphere.MSApiUtil;
import cn.torna.service.metersphere.MeterSphereService;
import cn.torna.service.metersphere.dto.MeterSphereSetting;
import cn.torna.service.metersphere.dto.MeterSphereSpaceDTO;
import cn.torna.service.metersphere.dto.MeterSphereTestDTO;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author thc
 */
public class MeterSphereServiceTest {

    static MeterSphereSetting meterSphereSetting = new MeterSphereSetting();
    static {
        meterSphereSetting.setMsAddress("http://172.16.4.229:8081/api");
        meterSphereSetting.setMsAccessKey("IFKKVmGJQDDx0Dmi");
        meterSphereSetting.setMsSecretKey("x1JDCUc8Vtdmifod");
    }

    /**
     * 查询MS项目
     * @throws Exception
     */
    @Test
    public void listMsProject() throws Exception {
        MeterSphereTestDTO meterSphereTestDTO = MeterSphereService.test(meterSphereSetting);
        List<MeterSphereSpaceDTO> spaces = meterSphereTestDTO.getSpaces();
        for (MeterSphereSpaceDTO space : spaces) {
            String msSpaceId = space.getId();
            JSONObject projectList = MSApiUtil.getProjectList(meterSphereSetting, msSpaceId);
            System.out.println(projectList);
        }
    }

}
