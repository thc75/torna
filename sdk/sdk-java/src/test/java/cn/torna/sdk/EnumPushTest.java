package cn.torna.sdk;

import cn.torna.sdk.param.EnumInfoParam;
import cn.torna.sdk.param.EnumItemParam;
import cn.torna.sdk.request.EnumBatchPushRequest;
import cn.torna.sdk.request.EnumPushRequest;
import cn.torna.sdk.response.EnumPushResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
public class EnumPushTest extends BaseTest {

    /**
     * 推送字典
     */
    public void testEnumPushRequest() {
        EnumBatchPushRequest request = new EnumBatchPushRequest(token);
        List<EnumInfoParam> enumInfoParams = new ArrayList<>(8);
        for (int i = 0; i < 3; i++) {
            EnumInfoParam enumInfoParam = new EnumInfoParam();
            enumInfoParam.setName("字典推送" + i);
            List<EnumItemParam> items = new ArrayList<>(8);
            for (int j = 0; j < 3; j++) {
                EnumItemParam item = new EnumItemParam();
                item.setName("name" + j);
                item.setType("string");
                item.setValue("v" + j);
                item.setDescription("描述2" + j);
                items.add(item);
            }
            enumInfoParam.setItems(items);
            enumInfoParam.setDescription("描述");
            enumInfoParams.add(enumInfoParam);
        }
        request.setEnums(enumInfoParams);
        EnumPushResponse response = client.execute(request);
        this.printResponse(response);
    }

}
