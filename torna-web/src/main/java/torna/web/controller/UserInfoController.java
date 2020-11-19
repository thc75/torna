package torna.web.controller;

import torna.common.bean.Result;
import torna.service.UserInfoService;
import torna.service.dto.UserInfoDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.PageEasyui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/search")
    public Result<PageEasyui<UserInfoDTO>> pageUser(@RequestParam(required = false) String username) {
        Query query = new Query();
        if (StringUtils.hasText(username)) {
            query.like("username", username);
        }
        PageEasyui<UserInfoDTO> pageSpaceUser = userInfoService.page(query, UserInfoDTO.class);
        return Result.ok(pageSpaceUser);
    }



}