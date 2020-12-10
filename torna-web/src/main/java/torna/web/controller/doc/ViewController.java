package torna.web.controller.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Booleans;
import torna.common.bean.Result;
import torna.common.util.GenerateUtil;
import torna.dao.entity.DocInfo;
import torna.dao.entity.Module;
import torna.service.DocInfoService;
import torna.service.ModuleService;
import torna.service.ProjectService;
import torna.service.dto.DocInfoDTO;
import torna.service.dto.ProjectDTO;
import torna.web.controller.doc.vo.TreeVO;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/view")
public class ViewController {

    private static final byte TYPE_PROJECT = 0;
    private static final byte TYPE_MODULE = 1;
    private static final byte TYPE_FOLDER = 2;
    private static final byte TYPE_DOC = 3;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private DocInfoService docInfoService;

    @GetMapping("data")
    public Result<List<TreeVO>> data(@HashId Long spaceId) {
        List<ProjectDTO> projectDTOS = projectService.listSpaceProject(spaceId);
        List<TreeVO> list = new ArrayList<>();
        for (ProjectDTO projectDTO : projectDTOS) {
            TreeVO projectVO = new TreeVO(GenerateUtil.getUUID(), projectDTO.getName(), "", TYPE_PROJECT);
            list.add(projectVO);
            List<Module> modules = moduleService.listProjectModules(projectDTO.getId());
            for (Module module : modules) {
                TreeVO moduleVO = new TreeVO(GenerateUtil.getUUID(), module.getName(), projectVO.getId(), TYPE_MODULE);
                list.add(moduleVO);
                List<DocInfo> docInfos = docInfoService.listDocMenuView(module.getId());
                String base = moduleVO.getId();
                for (DocInfo docInfo : docInfos) {
                    boolean isFolder = Booleans.isTrue(docInfo.getIsFolder());
                    String id = buildId(base, docInfo.getId());
                    String parentId = buildParentId(base, docInfo.getParentId());
                    byte type = isFolder ? TYPE_FOLDER : TYPE_DOC;
                    TreeVO docInfoVO = new TreeVO(id, docInfo.getName(), parentId, type);
                    if (!isFolder) {
                        docInfoVO.setDocId(docInfo.getId());
                        docInfoVO.setUrl(docInfo.getUrl());
                    }
                    list.add(docInfoVO);
                }
            }
        }
        return Result.ok(list);
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("detail")
    public Result<DocInfoDTO> detail(@HashId Long id) {
        DocInfoDTO docInfoDTO = docInfoService.getDocDetailView(id);
        return Result.ok(docInfoDTO);
    }

    private String buildId(String base, Long id) {
        return DigestUtils.md5DigestAsHex((base + id).getBytes(StandardCharsets.UTF_8));
    }

    private String buildParentId(String base, Long parentId) {
        if (parentId == null || parentId == 0) {
            return base;
        }
        return buildId(base, parentId);
    }

}
