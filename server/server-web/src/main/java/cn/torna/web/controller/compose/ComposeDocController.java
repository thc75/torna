package cn.torna.web.controller.compose;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.dao.entity.ComposeDoc;
import cn.torna.service.ComposeDocService;
import cn.torna.web.controller.compose.param.ComposeDocFolderAddParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("compose/doc")
public class ComposeDocController {

    @Autowired
    private ComposeDocService composeDocService;

    @PostMapping("folder/add")
    public Result addFolder(ComposeDocFolderAddParam param) {
        User user = UserContext.getUser();
        ComposeDoc composeDoc = new ComposeDoc();
        composeDoc.setProjectId(param.getProjectId());
        composeDoc.setIsFolder(Booleans.TRUE);
        composeDoc.setFolderName(param.getName());
        composeDoc.setCreator(user.getNickname());
        composeDocService.save(composeDoc);
        return Result.ok();
    }

}
