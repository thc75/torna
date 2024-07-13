package cn.torna.web.controller.openuser;

import cn.torna.common.bean.Result;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.OpenUser;
import cn.torna.service.OpenUserService;
import cn.torna.web.controller.openuser.param.OpenUserAddParam;
import cn.torna.web.controller.openuser.param.OpenUserParam;
import cn.torna.web.controller.openuser.vo.OpenUserVO;
import cn.torna.web.controller.system.param.IdParam;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 开放用户
 *
 * @author tanghc
 */
@RestController
@RequestMapping("openuser")
public class OpenUserController {

    @Autowired
    private OpenUserService openUserService;

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<PageInfo<OpenUserVO>> page(@RequestBody OpenUserParam param) {
        Query query = Query.build(param);
        PageInfo<OpenUserVO> pageEasyui = openUserService.pageAndConvert(query, list -> CopyUtil.copyList(list, OpenUserVO::new));
        return Result.ok(pageEasyui);
    }

    @PostMapping("add")
    public Result resetSecret(@RequestBody OpenUserAddParam param) {
        openUserService.createOpenUser(param.getSpaceId(), param.getApplicant());
        return Result.ok();
    }

    @PostMapping("secret/reset")
    public Result resetSecret(@RequestBody @Valid IdParam param) {
        OpenUser openUser = openUserService.getById(param.getId());
        openUser.setSecret(OpenUserService.createSecret());
        openUserService.update(openUser);
        return Result.ok();
    }

    @PostMapping("disable")
    public Result disable(@RequestBody @Valid IdParam param) {
        OpenUser openUser = openUserService.getById(param.getId());
        openUser.setStatus(StatusEnum.DISABLED.getStatus());
        openUserService.update(openUser);
        return Result.ok();
    }

    @PostMapping("enable")
    public Result enable(@RequestBody @Valid IdParam param) {
        OpenUser openUser = openUserService.getById(param.getId());
        openUser.setStatus(StatusEnum.ENABLE.getStatus());
        openUserService.update(openUser);
        return Result.ok();
    }

}
