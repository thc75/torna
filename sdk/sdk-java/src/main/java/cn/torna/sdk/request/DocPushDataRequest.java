package cn.torna.sdk.request;

import cn.torna.sdk.response.DocPushResponse;

public class DocPushDataRequest extends DataRequest<DocPushResponse> {
    public DocPushDataRequest(String token, String data) {
        super(token, data);
    }

    @Override
    public String name() {
        return "doc.push";
    }
}
