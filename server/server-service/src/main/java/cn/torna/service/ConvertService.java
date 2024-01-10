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
import cn.torna.service.dto.ModuleEnvironmentDTO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public Postman convertToPostman(Long moduleId) {
        Module module = moduleService.getById(moduleId);
        Postman postman = new Postman();
        Info info = buildInfo(module);
        List<DocInfoDTO> docInfos = docInfoService.listTreeDoc(module.getId());
        List<Item> items = buildItems(docInfos);
        postman.setInfo(info);
        postman.setItem(items);
        return postman;
    }


    private Info buildInfo(Module module) {
        Info info = new Info();
        info.setName(module.getName());
        return info;
    }

    private List<Item> buildItems(List<DocInfoDTO> docInfos) {
        return docInfos.stream()
                .map(this::buildItem)
                .collect(Collectors.toList());
    }

    private Item buildItem(DocInfoDTO docInfoDTO) {
        Item item = new Item();
        item.setName(docInfoDTO.getDocName());
        item.setRequest(buildRequest(docInfoDTO));
        List<DocInfoDTO> children = docInfoDTO.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            List<Item> childItems = children.stream()
                    .map(this::buildItem)
                    .collect(Collectors.toList());
            item.setItem(childItems);
        }
        return item;
    }

    private Request buildRequest(DocInfoDTO docInfoDTO) {
        if (docInfoDTO.getIsFolder() == Booleans.TRUE) {
            return null;
        }
        Request request = new Request();
        request.setMethod(docInfoDTO.getHttpMethod());
        request.setHeader(buildHeaders(docInfoDTO));
        request.setUrl(buildUrl(docInfoDTO));
        request.setBody(buildBody(docInfoDTO));
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

    private Url buildUrl(DocInfoDTO docInfoDTO) {
        String path = StringUtils.trimLeadingCharacter(docInfoDTO.getUrl(), '/');
        List<ModuleEnvironmentDTO> debugEnvs = docInfoDTO.getDebugEnvs();
        String host = CollectionUtils.isEmpty(debugEnvs) ? "" : debugEnvs.get(debugEnvs.size() - 1).getUrl();
        String fullUrl = host + "/" + path;
        URI uri = URI.create(fullUrl);
        Url url = new Url();
        url.setProtocol(Optional.of(uri).map(URI::getScheme).orElse("http"));
        url.setHost(Optional.of(uri).map(u -> u.getHost().split("\\.")).map(Arrays::asList).orElse(Collections.emptyList()));
        url.setRaw(fullUrl);
        url.setPort(uri.getPort() > 0 ? String.valueOf(uri.getPort()) : null);
        url.setPath(Arrays.asList(path.split("/")));
//        url.setVariable(new ArrayList<>());
        url.setQuery(this.buildParams(docInfoDTO.getQueryParams()));
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

    private Body buildBody(DocInfoDTO docInfoDTO) {
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
            String json = buildJson(requestParams);
            body.setRaw(json);
        }
        body.setMode(mode);
        return body;
    }

    private String buildJson(List<DocParamDTO> requestParams) {
        List<DocParamDTO> docParamDTOS = TreeUtil.convertTree(requestParams, 0L);
        JSONObject root = buildJsonObject(docParamDTOS);
        return root.toJSONString();
    }

    private JSONObject buildJsonObject(List<DocParamDTO> docParamDTOS) {
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

}
