package torna.web.controller.admin;

import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.bean.Result;
import torna.service.UserInfoService;
import torna.service.dto.UserInfoDTO;
import torna.web.controller.admin.param.ResetPasswordParam;
import torna.web.controller.admin.param.UserSearch;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("page")
    public Result<PageEasyui<UserInfoDTO>> page(@RequestBody UserSearch userSearch) {
        Query query = userSearch.toQuery();
        PageEasyui<UserInfoDTO> page = MapperUtil.queryForEasyuiDatagrid(userInfoService.getMapper(), query, UserInfoDTO.class);
        return Result.ok(page);
    }

    @PostMapping("password/reset")
    public Result<String> resetPwd(@RequestBody ResetPasswordParam param) {
        Long id = param.getId();
        String resetPassword = userInfoService.resetPassword(id);
        return Result.ok(resetPassword);
    }

}