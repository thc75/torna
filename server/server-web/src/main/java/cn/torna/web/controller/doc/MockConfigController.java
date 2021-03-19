package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.service.dto.MockDTO;
import cn.torna.web.controller.system.param.IdParam;
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
@RequestMapping("doc/mock")
public class MockConfigController {

    @Autowired
    private MockConfigService mockConfigService;


    /**
     * 获取
     */
    @GetMapping("list")
    public Result<List<MockDTO>> list(@HashId Long docId) {
        List<MockDTO> mockDTOS = mockConfigService.listMockConfig(docId);
        return Result.ok(mockDTOS);
    }

    /**
     * 保存mock
     */
    @PostMapping("save")
    public Result<MockDTO> save(@RequestBody MockDTO mockDTO) {
        User user = UserContext.getUser();
        mockConfigService.saveMock(mockDTO, user);
        return Result.ok(mockDTO);
    }

    /**
     * 删除mock
     */
    @PostMapping("delete")
    public Result delete(@RequestBody IdParam param) {
        User user = UserContext.getUser();
        MockConfig mockConfig = mockConfigService.getById(param.getId());
        if (mockConfig != null) {
            mockConfig.setModifierId(user.getUserId());
            mockConfig.setModifierName(user.getNickname());
            mockConfigService.delete(mockConfig);
        }
        return Result.ok();
    }



}