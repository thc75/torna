package cn.torna.web.controller.share;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.enums.ShareConfigTypeEnum;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.ShareConfig;
import cn.torna.dao.entity.ShareContent;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocViewService;
import cn.torna.service.ShareConfigService;
import cn.torna.service.dto.TreeDTO;
import cn.torna.web.controller.doc.param.ShareCheckPasswordParam;
import cn.torna.web.controller.doc.vo.ShareConfigVO;
import com.gitee.fastmybatis.core.query.Query;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("share")
public class ShareController {

    @Autowired
    private ShareConfigService shareConfigService;

    @Autowired
    private DocInfoService docInfoService;

    @GetMapping("get")
    public Result<ShareConfigVO> get(@HashId Long id) {
        ShareConfig shareConfig = shareConfigService.getById(id);
        this.checkConfig(shareConfig);
        ShareConfigVO vo = CopyUtil.copyBean(shareConfig, ShareConfigVO::new);
        return Result.ok(vo);
    }

    private void checkConfig(ShareConfig shareConfig) {
        if (shareConfig == null) {
            throw new BizException("文档不存在");
        }
        if (shareConfig.getStatus() == StatusEnum.DISABLED.getStatus()) {
            throw new BizException("链接已失效");
        }
    }

    @PostMapping("/checkPassword")
    public Result checkPassword(@RequestBody ShareCheckPasswordParam param) {
        ShareConfig shareConfig = shareConfigService.getById(param.getId());
        this.checkConfig(shareConfig);
        if (shareConfig.getType() == ShareConfigTypeEnum.PUBLIC.getType()) {
            return Result.ok();
        }
        String password = shareConfig.getPassword();
        if (!Objects.equals(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)), param.getPassword())) {
            throw new BizException("密码不正确");
        }
        return Result.ok();
    }

    /**
     * 获取项目文档目录，可用于文档菜单
     *
     * @param id 模块id
     * @return 返回结果
     */
    @GetMapping("menu")
    public Result<List<TreeDTO>> listProjectDoc(@HashId Long id) {
        ShareConfig shareConfig = shareConfigService.getById(id);
        if (shareConfig == null || shareConfig.getStatus() == StatusEnum.DISABLED.getStatus()) {
            throw new BizException("文档不存在");
        }
        List<DocInfo> docInfos;
        if (shareConfig.getIsAll() == Booleans.TRUE) {
            docInfos = docInfoService.listModuleDoc(shareConfig.getModuleId());
        } else {
            List<ShareContent> shareContents = shareConfigService.listContent(id);
            List<Long> docIdList = listDocId(shareContents);
            docInfos = docInfoService.listDocByIds(docIdList);
        }
        List<TreeDTO> list = new ArrayList<>();
        String base = "";
        for (DocInfo docInfo : docInfos) {
            boolean isFolder = Booleans.isTrue(docInfo.getIsFolder());
            String treeId = isFolder ? DocViewService.buildId(base, docInfo.getId()) : IdUtil.encode(docInfo.getId());
            String parentId =  DocViewService.buildParentId(base, docInfo.getParentId());
            byte type = isFolder ? DocViewService.TYPE_FOLDER : DocViewService.TYPE_DOC;
            TreeDTO docInfoVO = new TreeDTO(treeId, docInfo.getName(), parentId, type);
            docInfoVO.setHttpMethod(docInfo.getHttpMethod());
            docInfoVO.setDocType(docInfo.getType());
            docInfoVO.setDocId(docInfo.getId());
            // 如果是文档
            if (!isFolder) {
                docInfoVO.setUrl(docInfo.getUrl());
                String deprecated = docInfo.getDeprecated();
                if (deprecated == null) {
                    deprecated = "$false$";
                }
                docInfoVO.setDeprecated(deprecated);
            }
            list.add(docInfoVO);
        }
        DocViewService.calcDocCount(list);
        return Result.ok(list);
    }

    private List<Long> listDocId(List<ShareContent> shareContents) {
        List<Long> idList = new ArrayList<>(8);
        for (ShareContent shareContent : shareContents) {
            idList.add(shareContent.getDocId());
            if (shareContent.getParentId() > 0) {
                idList.add(shareContent.getParentId());
            }
            // 如果分享整个文件夹
            if (BooleanUtils.toBoolean(shareContent.getIsShareFolder())) {
                Query query = new Query()
                        .eq("parent_id", shareContent.getDocId());
                List<Long> childIdList = docInfoService.listId(query);
                idList.addAll(childIdList);
            }
        }
        return idList;
    }

}
