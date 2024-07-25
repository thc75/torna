package cn.torna;

import cn.torna.api.bean.ApiUser;
import cn.torna.api.open.YapiApi;
import cn.torna.dao.entity.Project;
import cn.torna.service.ProjectService;
import cn.torna.service.dto.YapiMarkdownDTO;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author tanghc
 */
public class YapiApiTest extends TornaApplicationTests{

    @Autowired
    YapiApi yapiApi;

    @Autowired
    ProjectService projectService;


    @Test
    public void importDoc() throws IOException, InterruptedException {
        Project project = projectService.get(Project::getName, "商城项目");

        ApiUser apiUser = new ApiUser();
        apiUser.setId(1L);
        apiUser.setNickname("jim");

        YapiMarkdownDTO yapiMarkdownDTO = new YapiMarkdownDTO();
        yapiMarkdownDTO.setIp("127.0.0.1");
        yapiMarkdownDTO.setUser(apiUser);
        yapiMarkdownDTO.setProjectId(project.getId());

        String content = FileUtils.readFileToString(new File("/Users/mac/Downloads/api.md"), StandardCharsets.UTF_8);
        yapiMarkdownDTO.setContent(content);

        yapiApi.importMarkdown(yapiMarkdownDTO);

        TimeUnit.HOURS.sleep(1);
    }

}
