package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.Module;
import cn.torna.manager.doc.postman.Body;
import cn.torna.manager.doc.postman.Header;
import cn.torna.manager.doc.postman.Info;
import cn.torna.manager.doc.postman.Item;
import cn.torna.manager.doc.postman.Param;
import cn.torna.manager.doc.postman.Postman;
import cn.torna.manager.doc.postman.Request;
import cn.torna.manager.doc.postman.Url;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author thc
 */
@Service
public class ConvertService {

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleService moduleService;

    public Postman getPostman(Long moduleId) {
        ConvertService.Config config = new ConvertService.Config();
        config.setNeedHost(false);
        return convertToPostman(moduleId, config);
    }

    public Postman convertToPostman(Long moduleId, Config config) {
        Module module = moduleService.getById(moduleId);
        Context context = new Context();
        context.setModuleId(moduleId);
        context.setAppName(module.getName());
        context.setConfig(config);
        Postman postman = new Postman();
        Info info = buildInfo(module);
        List<DocInfoDTO> docInfos = docInfoService.listTreeDoc(module.getId());
        List<Item> items = buildItems(docInfos, context);
        postman.setInfo(info);
        postman.setItem(items);
        return postman;
    }


    private Info buildInfo(Module module) {
        Info info = new Info();
        info.setName(module.getName());
        return info;
    }

    private List<Item> buildItems(List<DocInfoDTO> docInfos, Context context) {
        return docInfos.stream()
                .map(docInfoDTO -> this.buildItem(docInfoDTO, context))
                .collect(Collectors.toList());
    }

    private Item buildItem(DocInfoDTO docInfoDTO, Context context) {
        Item item = new Item();
        item.setName(docInfoDTO.getDocName());
        item.setRequest(buildRequest(docInfoDTO, context));
        List<DocInfoDTO> children = docInfoDTO.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            List<Item> childItems = children.stream()
                    .map(docInfo -> this.buildItem(docInfo, context))
                    .collect(Collectors.toList());
            item.setItem(childItems);
        }
        return item;
    }

    private Request buildRequest(DocInfoDTO docInfoDTO, Context context) {
        if (docInfoDTO.getIsFolder() == Booleans.TRUE) {
            return null;
        }
        Request request = new Request();
        request.setMethod(docInfoDTO.getHttpMethod());
        request.setHeader(buildHeaders(docInfoDTO));
        request.setUrl(buildUrl(docInfoDTO, context));
        request.setBody(buildBody(docInfoDTO, context));
        request.setDescription(docInfoDTO.getDescription());
        return request;
    }

    private List<Header> buildHeaders(DocInfoDTO docInfoDTO) {
        List<Header> headers = new ArrayList<>();
        List<DocParamDTO> allHeaders = new ArrayList<>();
        allHeaders.addAll(docInfoDTO.getGlobalHeaders());
        allHeaders.addAll(docInfoDTO.getHeaderParams());

        for (DocParamDTO headerParam : allHeaders) {
            Header header = buildHeader(headerParam);
            headers.add(header);
        }
        return headers;
    }

    private Header buildHeader(DocParamDTO docParamDTO) {
        Header header = new Header();
        header.setKey(docParamDTO.getName());
        header.setValue(docParamDTO.getExample());
        header.setType(docParamDTO.getType());
        header.setDescription(docParamDTO.getDescription());
        return header;
    }

    private Url buildUrl(DocInfoDTO docInfoDTO, Context context) {
        Config config = context.getConfig();
        String path = StringUtils.trimLeadingCharacter(docInfoDTO.getUrl(), '/');
        String host = config.isNeedHost() ? "{{host_" + context.getAppName() + "}}" : "";
        String fullUrl = host + "/" + path;
        Url url = new Url();
        url.setProtocol("http");
        url.setHost(Collections.singletonList(host));
        url.setRaw(fullUrl);
        url.setPort(null);
        url.setPath(Arrays.asList(path.split("/")));
//        url.setVariable(new ArrayList<>());
        url.setQuery(this.buildParams(docInfoDTO.getQueryParams()));
        if (config.isNeedHost()) {
            url.setProtocol("");
        }
        return url;
    }

    private List<Param> buildParams(List<DocParamDTO> docParamDTOS) {
        return docParamDTOS.stream()
                .map(this::buildParam)
                .collect(Collectors.toList());
    }


    private Param buildParam(DocParamDTO docParamDTO) {
        Param param = new Param();
        param.setKey(docParamDTO.getName());
        param.setValue(docParamDTO.getExample());
        param.setType(docParamDTO.getType());
        param.setDescription(docParamDTO.getDescription());
        param.setChildren(Collections.emptyList());
        return param;
    }

    private Body buildBody(DocInfoDTO docInfoDTO, Context context) {
        String httpMethod = docInfoDTO.getHttpMethod();
        switch (httpMethod.toLowerCase()) {
            case "get":
            case "head":
                return null;
        }
        List<DocParamDTO> requestParams = docInfoDTO.getRequestParams();
        if (CollectionUtils.isEmpty(requestParams)) {
            return null;
        }
        String contentType = Optional.ofNullable(docInfoDTO.getContentType()).orElse("").toLowerCase();
        String mode = "raw";
        Body body = new Body();
        if (contentType.contains("urlencoded")) {
            mode = "urlencoded";
            List<Param> params = buildParams(requestParams);
            body.setUrlencoded(params);
        } else if (contentType.contains("multipart")) {
            mode = "formdata";
            List<Param> params = buildParams(requestParams);
            body.setFormdata(params);
        }
        if (contentType.contains("json")) {
            String json = buildJson(requestParams, context.getConfig().isFormatJson());
            Byte isResponseArray = docInfoDTO.getIsResponseArray();
            if (Objects.equals(isResponseArray, Booleans.TRUE)) {
                json = "[" + json + "]";
            }
            body.setRaw(json);
            body.setOptions(getJsonOptions());
        }
        body.setMode(mode);
        return body;
    }

    /*
    "raw": {
        "language": "json"
    }
     */
    private static JSONObject getJsonOptions() {
        JSONObject opt = new JSONObject();
        JSONObject raw = new JSONObject();
        raw.put("language", "json");
        opt.put("raw", raw);
        return opt;
    }

    public static String buildJson(List<DocParamDTO> requestParams, boolean format) {
        List<DocParamDTO> docParamDTOS = TreeUtil.convertTree(requestParams, 0L);
        JSONObject root = buildJsonObject(docParamDTOS);
        if (format) {
            return root.toString(SerializerFeature.PrettyFormat);
        } else {
            return root.toJSONString();
        }
    }

    public static JSONObject buildJsonObject(List<DocParamDTO> docParamDTOS) {
        JSONObject root = new JSONObject();
        for (DocParamDTO requestParam : docParamDTOS) {
            List<DocParamDTO> children = requestParam.getChildren();
            // 如果有子节点
            if (!CollectionUtils.isEmpty(children)) {
                root.put(requestParam.getName(), buildJsonObject(children));
            } else {
                root.put(requestParam.getName(), requestParam.getExample());
            }
        }
        return root;
    }


    @Data
    private static class Context {
        private Long moduleId;
        private String appName;
        private Config config;
    }

    @Data
    public static class Config {
        private boolean needHost = true;
        private boolean formatJson = false;
    }

}
