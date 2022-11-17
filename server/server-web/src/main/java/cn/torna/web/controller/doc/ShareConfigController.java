package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ShareConfig;
import cn.torna.dao.entity.ShareContent;
import cn.torna.dao.entity.ShareEnvironment;
import cn.torna.service.ShareConfigService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.ShareConfigDTO;
import cn.torna.service.dto.ShareDocDTO;
import cn.torna.web.controller.doc.param.ShareConfigParam;
import cn.torna.web.controller.doc.vo.ShareConfigVO;
import cn.torna.web.controller.doc.vo.ShareContentVO;
import cn.torna.web.controller.doc.vo.ShareEnvironmentVO;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/share")
public class ShareConfigController {

    @Autowired
    private ShareConfigService shareConfigService;

    @PostMapping("page")
    public Result<PageEasyui<ShareConfigVO>> page(@RequestBody ShareConfigParam param) {
        PageEasyui<ShareConfigVO> pageInfo = MapperUtil.queryForEasyuiDatagrid(
                shareConfigService.getMapper(),
                param.toQuery().orderby("id", Sort.DESC),
                ShareConfigVO.class);
        return Result.ok(pageInfo);
    }

    @PostMapping("add")
    public Result add(@RequestBody ShareConfigDTO param) {
        User user = UserContext.getUser();
        shareConfigService.add(param, user);
        return Result.ok();
    }

    @PostMapping("update")
    public Result update(@RequestBody ShareConfigDTO param) {
        shareConfigService.update(param);
        return Result.ok();
    }

    @PostMapping("del")
    public Result del(@RequestBody ShareConfigDTO param) {
        shareConfigService.getMapper().deleteById(param.getId());
        return Result.ok();
    }

    @GetMapping("listContent")
    public Result<List<ShareContentVO>> docs(@HashId Long id) {
        List<ShareContent> shareContents = shareConfigService.listContent(id);
        List<ShareContentVO> list = CopyUtil.copyList(shareContents, ShareContentVO::new);
        return Result.ok(list);
    }

    @GetMapping("listShareDocIds")
    public Result<List<ShareDocDTO>> listShareDocIds(@HashId Long id) {
        List<ShareDocDTO> shareDocIds = shareConfigService.listShareDocIds(id);
        return Result.ok(shareDocIds);
    }

    @GetMapping("listEnvironment")
    public Result<List<ShareEnvironmentVO>> listEnvironment(@HashId Long id) {
        List<ShareEnvironment> shareEnvironments = shareConfigService.listEnvironment(id);
        List<ShareEnvironmentVO> list = CopyUtil.copyList(shareEnvironments, ShareEnvironmentVO::new);
        return Result.ok(list);
    }


    @PostMapping("enable")
    public Result enable(@RequestBody ShareConfigDTO param) {
        ShareConfig shareConfig = shareConfigService.getById(param.getId());
        shareConfig.setStatus(StatusEnum.ENABLE.getStatus());
        shareConfigService.update(shareConfig);
        return Result.ok();
    }

    @PostMapping("disable")
    public Result disable(@RequestBody ShareConfigDTO param) {
        ShareConfig shareConfig = shareConfigService.getById(param.getId());
        shareConfig.setStatus(StatusEnum.DISABLED.getStatus());
        shareConfigService.update(shareConfig);
        return Result.ok();
    }


    /**
     * 查询分享文档详细信息
     *
     * @param docId 主键
     * @param shareConfigId 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("view")
    @NoLogin
    public Result<DocInfoDTO> view(@HashId Long docId, @HashId Long shareConfigId) {
        DocInfoDTO docInfoDTO = shareConfigService.getShareDocDetail(docId, shareConfigId);
        return Result.ok(docInfoDTO);
    }


}