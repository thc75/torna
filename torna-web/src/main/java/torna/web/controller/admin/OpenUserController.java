package torna.web.controller.admin;

import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.bean.Result;
import torna.common.enums.StatusEnum;
import torna.dao.entity.OpenUser;
import torna.service.OpenUserService;
import torna.web.controller.system.param.IdParam;
import torna.web.controller.admin.param.OpenUserParam;
import torna.web.controller.admin.vo.OpenUserVO;

import javax.validation.Valid;

/**
 * 开放用户
 * @author tanghc
 */
@RestController
@RequestMapping("admin/openuser")
public class OpenUserController {

    @Autowired
    private OpenUserService openUserService;

    /**
     * 分页查询
     */
    @GetMapping("page")
    public Result<PageEasyui<OpenUserVO>> page(OpenUserParam param) {
        Query query = Query.build(param);
        PageEasyui<OpenUserVO> pageEasyui = MapperUtil.queryForEasyuiDatagrid(openUserService.getMapper(), query, OpenUserVO.class);
        return Result.ok(pageEasyui);
    }

    @PostMapping("add")
    public Result resetSecret() {
        openUserService.createOpenUser();
        return Result.ok();
    }

    @PostMapping("secret/reset")
    public Result resetSecret(@RequestBody @Valid IdParam param) {
        OpenUser openUser = openUserService.getById(param.getId());
        openUser.setSecret(OpenUserService.createSecret());
        openUserService.updateIgnoreNull(openUser);
        return Result.ok();
    }

    @PostMapping("disable")
    public Result disable(@RequestBody @Valid IdParam param) {
        OpenUser openUser = openUserService.getById(param.getId());
        openUser.setStatus(StatusEnum.DISABLED.getStatus());
        openUserService.updateIgnoreNull(openUser);
        return Result.ok();
    }

    @PostMapping("enable")
    public Result enable(@RequestBody @Valid IdParam param) {
        OpenUser openUser = openUserService.getById(param.getId());
        openUser.setStatus(StatusEnum.ENABLE.getStatus());
        openUserService.updateIgnoreNull(openUser);
        return Result.ok();
    }
    
}