package cn.torna.web.controller.doc;

import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.service.gen.GenerateService;
import cn.torna.service.gen.dto.GenParamDTO;
import cn.torna.service.gen.dto.GenResultDTO;
import cn.torna.web.controller.doc.param.GenParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 代码生成
 *
 * @author 六如
 */
@RestController
@RequestMapping("doc/gen")
public class GenController {

    @Autowired
    private GenerateService generateService;

    /**
     * 生成代码
     *
     * @param param 请求参数
     * @return 返回结果
     */
    @PostMapping("generate")
    public Result<GenResultDTO> generate(@Valid @RequestBody GenParam param) {
        GenParamDTO genParamDTO = CopyUtil.copyBean(param, GenParamDTO::new);
        GenResultDTO generate = generateService.generate(genParamDTO);
        return Result.ok(generate);
    }

}
