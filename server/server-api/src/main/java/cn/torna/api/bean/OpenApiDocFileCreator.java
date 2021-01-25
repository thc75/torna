package cn.torna.api.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.easyopen.bean.Consts;
import com.gitee.easyopen.doc.ApiDocItem;
import com.gitee.easyopen.doc.ApiModule;
import com.gitee.easyopen.doc.DocFileCreator;
import com.gitee.easyopen.util.VelocityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author tanghc
 */
@Slf4j
public class OpenApiDocFileCreator implements DocFileCreator {

    private static final String MARKDWON_SUFFIX = ".md";
    private static final String SIDEBAR_FILENAME = "menu.json";
    private static final String MARKDOWN_TEMPLATE_CLASSPATH = "/markdown_template.md";


    /**
     * api文件夹
     */
    private String apiDir;

    /**
     * 文档模板
     */
    private String template = MARKDOWN_TEMPLATE_CLASSPATH;

    /**
     * @param dir 文档根目录
     */
    public OpenApiDocFileCreator(String dir) {
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        this.apiDir = dir;
    }

    /**
     * 生成markdown格式的文档
     */
    @Override
    public void createMarkdownDoc(Collection<ApiModule> apiModules) throws IOException {
        log.info("生成markdown文档文件，保存路径：{}", this.apiDir);
        this.createSidebar(apiModules);
        for (ApiModule apiModule : apiModules) {
            List<ApiDocItem> moduleItems = apiModule.getModuleItems();
            for (ApiDocItem item : moduleItems) {
                this.createDocFile(item);
            }
        }
    }

    /**
     * 生成侧边菜单
     *
     * [
     *   {
     *     "title" : "OpenAPI文档",
     *     "icon": "el-icon-document",
     *     "children": [
     *       {
     *         "title": "如何请求接口",
     *         "path": "static/openapi/how-to-request.md"
     *       },
     *       {
     *         "title": "获取文档列表",
     *         "path": "static/openapi/doc.list1.0.md"
     *       },
     *       {
     *         "title": "获取文档详情",
     *         "path": "static/openapi/doc.get1.0.md"
     *       }
     *     ]
     *   }
     * ]
     *
     * @param apiModules
     */
    protected void createSidebar(Collection<ApiModule> apiModules) throws IOException {
        List<Menu> menus = new ArrayList<>();
        for (ApiModule apiModule : apiModules) {
            Menu menu = new Menu(apiModule.getName(), "el-icon-folder-opened",null, new ArrayList<>());
            // * Getting started 一级
            List<ApiDocItem> moduleItems = apiModule.getModuleItems();
            for (ApiDocItem item : moduleItems) {
                // * [Quick start](quickstart.md) 二级
                String title = item.getDescription();
                String filename = item.getNameVersion() + MARKDWON_SUFFIX;
                menu.getChildren().add(new Menu(title, null, filename, null));
            }
            menus.add(menu);
        }
        String siderbarFilePath = this.apiDir + SIDEBAR_FILENAME;
        String menuJson = JSON.toJSONString(menus, SerializerFeature.PrettyFormat);
        FileUtils.write(new File(siderbarFilePath), menuJson, Consts.UTF8);
    }

    protected void createDocFile(ApiDocItem item) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("docItem", item);
        ClassPathResource resource = new ClassPathResource(this.template);
        String fileContent = VelocityUtil.generateToString(context, resource.getInputStream());
        String filepath = this.apiDir + item.getNameVersion() + MARKDWON_SUFFIX;
        FileUtils.write(new File(filepath), fileContent, Consts.UTF8);
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setApiDir(String apiDir) {
        this.apiDir = apiDir;
    }

    @Data
    @AllArgsConstructor
    static class Menu {
        private String title;
        private String icon;
        private String path;
        private List<Menu> children;
    }
}
