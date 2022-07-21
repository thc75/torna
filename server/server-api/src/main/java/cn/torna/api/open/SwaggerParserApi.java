package cn.torna.api.open;

import cn.torna.api.open.param.DebugEnvParam;
import cn.torna.api.open.param.DocPushItemParam;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.common.bean.Configs;
import cn.torna.common.exception.BizException;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author thc
 */
@Service
@Slf4j
public class SwaggerParserApi {

    @Autowired
    private DocApi docApi;

    public void parse(String content) {
        try {
            SwaggerParseResult result = new OpenAPIParser().readContents(content, null, null);
            OpenAPI openAPI = result.getOpenAPI();
            if (result.getMessages() != null) {
                for (String message : result.getMessages()) {
                    log.error("解析Swagger文档, msg={}", message);
                }
            }
            DocPushParam docPushParam = this.convertDocInfo(openAPI);
            docApi.pushDoc(docPushParam);
        } catch (Exception e) {
            log.error("解析Swagger文档失败", e);
            throw new BizException("解析Swagger文档失败");
        }
    }

    private DocPushParam convertDocInfo(OpenAPI openAPI) {
        if (openAPI == null) {
            return null;
        }
        DocPushParam docPushParam = new DocPushParam();
        Info info = openAPI.getInfo();
        String defaultAuthor = Configs.getValue("swagger.default-author", "swagger");
        String author = Optional.ofNullable(info).map(Info::getContact).map(Contact::getName).orElse(defaultAuthor);
        List<DebugEnvParam> debugEnvParams = buildDebugEnvParams(openAPI);
        List<DocPushItemParam> apis = buildDocPushItemParams(openAPI);
        docPushParam.setAuthor(author);
        docPushParam.setDebugEnvs(debugEnvParams);
        docPushParam.setApis(apis);
        return docPushParam;
    }

    private List<DebugEnvParam> buildDebugEnvParams(OpenAPI openAPI) {
        List<Server> servers = openAPI.getServers();
        if (CollectionUtils.isEmpty(servers)) {
            return Collections.emptyList();
        }
        AtomicInteger i = new AtomicInteger();
        return servers.stream()
                .map(server -> {
                    DebugEnvParam debugEnvParam = new DebugEnvParam();
                    debugEnvParam.setName(Optional.ofNullable(server.getDescription()).orElse("test" + i.incrementAndGet()));
                    debugEnvParam.setUrl(server.getUrl());
                    return debugEnvParam;
                })
                .collect(Collectors.toList());
    }

    private List<DocPushItemParam> buildDocPushItemParams(OpenAPI openAPI) {
        List<Tag> tags = openAPI.getTags();
        return null;
    }

}
