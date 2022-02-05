package cn.torna.web.controller.share;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.enums.ShareConfigTypeEnum;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.ShareConfig;
import cn.torna.dao.entity.ShareContent;
import cn.torna.service.DocInfoService;
import cn.torna.service.ShareConfigService;
import cn.torna.web.controller.doc.param.ShareCheckPasswordParam;
import cn.torna.web.controller.doc.vo.DocInfoVO;
import cn.torna.web.controller.doc.vo.ShareConfigVO;
import cn.torna.web.controller.doc.vo.DocInfoVO;
import com.gitee.fastmybatis.core.query.Query;
import org.apache.commons.lang.BooleanUtils;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Result<List<DocInfoVO>> listProjectDoc(@HashId Long id) {
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
            Query query = new Query()
                    .in("id", docIdList);
            docInfos = docInfoService.list(query);
        }
        List<DocInfoVO> docInfoVOS = CopyUtil.copyList(docInfos, DocInfoVO::new);
        calcDocCount(docInfoVOS);
        return Result.ok(docInfoVOS);
    }

    /**
     * 计算文档数量
     * @param list 文档列表
     */
    private static void calcDocCount(List<DocInfoVO> list) {
        Map<Long, DocInfoVO> idMap = list.stream().collect(Collectors.toMap(DocInfoVO::getId, Function.identity()));
        TreeUtil.initParent(list, 0L, idMap);
        for (DocInfoVO treeVO : list) {
            // 如果是文档类型，父节点数量+1
            if (!Booleans.isTrue(treeVO.getIsFolder())) {
                DocInfoVO parent = treeVO.getParent();
                if (parent != null) {
                    parent.addApiCount();
                }
            }
        }
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
