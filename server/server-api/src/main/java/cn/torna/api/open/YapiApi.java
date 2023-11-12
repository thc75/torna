package cn.torna.api.open;

import cn.torna.api.bean.ApiUser;
import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.DocPushItemParam;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.util.HtmlUtil;
import cn.torna.dao.entity.Module;
import cn.torna.service.ModuleService;
import cn.torna.service.dto.YapiMarkdownDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 导入YAPI
 *
 * @author tanghc
 */
@Service
@Slf4j
public class YapiApi {

    public static final String PATH_PREFIX = "**Path：** ";
    public static final String METHOD_PREFIX = "**Method：** ";
    @Autowired
    private DocApi docApi;

    @Autowired
    private ModuleService moduleService;

    public Module importMarkdown(YapiMarkdownDTO yapiMarkdownDTO) {
        User user = yapiMarkdownDTO.getUser();
        ApiUser apiUser = new ApiUser();
        apiUser.setId(user.getUserId());
        apiUser.setNickname(user.getNickname());

        DocInfoParam docPushParam = buildDocInfoParam(yapiMarkdownDTO);
        if (docPushParam == null) {
            log.warn("文档内容为空");
            return null;
        }
        String name = docPushParam.getName();
        Module module = moduleService.createYapiModule(yapiMarkdownDTO, name);

        RequestContext.getCurrentContext().setModule(module);
        RequestContext.getCurrentContext().setIp(yapiMarkdownDTO.getIp());
        RequestContext.getCurrentContext().setApiUser(apiUser);

        // 推送文档
        docApi.pushDoc(docPushParam.getDocPushParam());

        return module;
    }

    private DocInfoParam buildDocInfoParam(YapiMarkdownDTO yapiMarkdownDTO) {
        String name = null;
        DocPushParam docPushParam = new DocPushParam();
        List<DocPushItemParam> apis = new ArrayList<>();
        String defFolder = "_def_folder_";
        Map<String, List<DocPushItemParam>> folderMap = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(yapiMarkdownDTO.getContent()))) {
            String line;
            String currentFolder = null;
            CurrentDoc currentDoc = null;
            while ((line = reader.readLine()) != null) {
                // <h1 class="curproject-name"> xxx API </h1>
                if (name == null && line.trim().startsWith("<h1")) {
                    List<String> resultsFromHtml = HtmlUtil.parseHtmlContent(line);
                    name = resultsFromHtml.isEmpty() ? "项目" : resultsFromHtml.get(0);
                    continue;
                }
                // 结束之前的文档
                if ((line.startsWith("# ") || line.startsWith("## ")) && currentDoc != null) {
                    DocPushItemParam docPushItemParam = buildDocPushItemParam(currentDoc);
                    folderMap.get(currentDoc.getFolder()).add(docPushItemParam);
                }
                if (line.startsWith("# ")) {
                    // 目录
                    currentFolder = line.substring(2);
                    folderMap.computeIfAbsent(currentFolder, k -> new ArrayList<>());
                    continue;
                }
                // 文档
                if (line.startsWith("## ")) {
                    String docName = line.substring(3);
                    String folder = currentFolder == null ? defFolder : currentFolder;
                    currentDoc = new CurrentDoc(docName, folder);
                    continue;
                }
                if (currentDoc != null) {
                    if (line.startsWith(PATH_PREFIX)) {
                        String url = line.substring(PATH_PREFIX.length());
                        int index = url.indexOf('?');
                        if (index > -1) {
                            url = url.substring(0, index);
                        }
                        currentDoc.setUrl(url);
                    }
                    if (line.startsWith(METHOD_PREFIX)) {
                        String method = line.substring(METHOD_PREFIX.length());
                        currentDoc.setMethod(method);
                    }
                    currentDoc.appendContent(line);
                }
            }
            int index = 0;
            for (Map.Entry<String, List<DocPushItemParam>> entry : folderMap.entrySet()) {
                String folder = entry.getKey();
                List<DocPushItemParam> value = entry.getValue();
                int orderIndex = 1;
                for (DocPushItemParam docPushItemParam : value) {
                    docPushItemParam.setOrderIndex(orderIndex++);
                }
                if (Objects.equals(folder, defFolder)) {
                    apis.addAll(value);
                } else {
                    DocPushItemParam parent = new DocPushItemParam();
                    parent.setName(folder);
                    parent.setIsFolder(Booleans.TRUE);
                    parent.setItems(value);
                    parent.setOrderIndex(index++);
                    parent.setType(DocTypeEnum.MARKDOWN.getType());
                    apis.add(parent);
                }
            }
            User user = yapiMarkdownDTO.getUser();
            docPushParam.setIsReplace(Booleans.TRUE);
            docPushParam.setApis(apis);
            docPushParam.setDebugEnvs(Collections.emptyList());
            docPushParam.setAuthor(user.getNickname());
            return new DocInfoParam(name == null ? "项目" : name, docPushParam);
        } catch (IOException e) {
            log.error("生成Markdown文档失败", e);
        }
        return null;
    }

    private DocPushItemParam buildDocPushItemParam(CurrentDoc currentDoc) {
        DocPushItemParam docPushItemParam = new DocPushItemParam();
        docPushItemParam.setName(currentDoc.getName());
        docPushItemParam.setDescription(currentDoc.getMarkdown());
        docPushItemParam.setType(DocTypeEnum.MARKDOWN.getType());
        docPushItemParam.setUrl(currentDoc.getUrl());
        docPushItemParam.setHttpMethod(currentDoc.getMethod());
        return docPushItemParam;
    }


    @AllArgsConstructor
    @Data
    static class DocInfoParam {
        private String name;
        private DocPushParam docPushParam;
    }

    @Data
    static class CurrentDoc {
        private String name;
        private String folder;
        private String url;
        private String method;
        private List<String> content = new ArrayList<>();

        public CurrentDoc(String name, String folder) {
            this.name = name;
            this.folder = folder;
        }

        public void appendContent(String line) {
            this.content.add(line);
        }

        public String getMarkdown() {
            return String.join("\n", content);
        }
    }

}
