package cn.torna;

import cn.torna.service.metersphere.MeterSpherePushService;
import cn.torna.service.metersphere.MeterSphereService;
import cn.torna.service.metersphere.dto.MeterSphereProjectDTO;
import cn.torna.service.metersphere.dto.MeterSphereSetting;
import java.io.IOException;
import java.util.List;

import cn.torna.service.metersphere.dto.MeterSphereTestDTO;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author thc
 */
public class MeterSpherePushServiceTest extends TornaApplicationTests{

    @Autowired
    MeterSpherePushService meterSpherePushService;

    @Autowired
    MeterSphereService meterSphereService;

    static  MeterSphereSetting meterSphereSetting = new MeterSphereSetting();
    static {
        meterSphereSetting.setMsAddress("http://172.16.4.229:8081/api");
        meterSphereSetting.setMsAccessKey("IFKKVmGJQDDx0Dmi");
        meterSphereSetting.setMsSecretKey("x1JDCUc8Vtdmifod");
    }


    @Test
    public void test() {
        MeterSphereTestDTO meterSphereTestDTO = MeterSphereService.test(meterSphereSetting);
        System.out.println(meterSphereTestDTO);
    }

    @Test
    public void push() throws Exception {
        meterSpherePushService.push(8L);
        Thread.sleep(5000);
    }

    @Test
    public void listProject() throws Exception {
        List<MeterSphereProjectDTO> meterSphereProjectDTOS = meterSphereService.listProject(5L);
        System.out.println(JSON.toJSONString(meterSphereProjectDTOS));
    }


}
