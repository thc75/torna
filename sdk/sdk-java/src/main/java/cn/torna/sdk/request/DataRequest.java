package cn.torna.sdk.request;

import cn.torna.sdk.response.BaseResponse;

public abstract class DataRequest<T extends BaseResponse<?>> extends BaseRequest<T> {

    private String data;

    public DataRequest(String token, String data) {
        super(token);
        this.data =data;
    }


    @Override
    protected String buildJsonData() {
        return data;
    }

}
