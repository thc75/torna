package cn.torna.sdk.request;

import cn.torna.sdk.param.EnumInfoParam;
import cn.torna.sdk.param.EnumItemParam;
import cn.torna.sdk.response.EnumPushResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 批量推送枚举
 * 接口名	enum.batch.push	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class EnumBatchPushRequest extends BaseRequest<EnumPushResponse> {

    private List<EnumInfoParam> enums;

    /**
     * @param token OpenAPI对应的token
     */
    public EnumBatchPushRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "enum.batch.push";
    }
}
