package cn.torna.web.controller.admin;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.TreeData;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.GenTemplate;
import cn.torna.service.gen.GenTemplateService;
import cn.torna.web.controller.admin.param.GenTemplateParam;
import cn.torna.web.controller.admin.vo.GenTemplateVO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


/**
 * 代码生成模板
 *
 * @author tanghc
 */
@RestController
@RequestMapping("admin/gen/template")
public class GenTemplateController {

    @Autowired
    private GenTemplateService genTemplateService;

    /**
     * 返回树结构
     *
     * @return
     */
    @GetMapping("/tree")
    public Result<List<TreeData>> tree() {
        List<TreeData> templateDTOList = genTemplateService.listGroupTree();
        return Result.ok(CopyUtil.copyList(templateDTOList, TreeData::new));
    }

    /**
     * 返回分组信息结构
     *
     * @return
     */
    @GetMapping("/listGroupName")
    public Result<List<String>> listGroupName() {
        List<String> templateDTOList = genTemplateService.listGroupName();
        return Result.ok(templateDTOList);
    }

    /**
     * 详情
     *
     * @param id
     * @return 返回详情
     */
    @GetMapping("/detail")
    public Result<GenTemplateVO> detail(@HashId Long id) {
        GenTemplate record = genTemplateService.getById(id);
        return Result.ok(CopyUtil.copyBean(record, GenTemplateVO::new));
    }


    /**
     * 新增记录
     *
     * @param param
     * @return
     */
    @PostMapping("/save")
    public Result<Long> save(@Valid @RequestBody GenTemplateParam param) {
        this.check(param);
        GenTemplate genTemplate = CopyUtil.copyBean(param, GenTemplate::new);
        genTemplateService.save(genTemplate);
        // 返回添加后的主键值
        return Result.ok(genTemplate.getId());
    }

    /**
     * 修改记录
     *
     * @param param 表单数据
     * @return
     */
    @PostMapping("/update")
    public Result<?> update(@Valid @RequestBody GenTemplateParam param) {
        this.check(param);
        GenTemplate genTemplate = CopyUtil.copyBean(param, GenTemplate::new);
        genTemplateService.update(genTemplate);
        return Result.ok();
    }

    private void check(GenTemplateParam genTemplate) {
        String groupName = genTemplate.getGroupName();
        if (ObjectUtils.isEmpty(groupName)) {
            genTemplate.setGroupName("默认分组");
        }
        if (ObjectUtils.isEmpty(genTemplate.getContent())) {
            throw new BizException("请输入模板内容");
        }
    }

    /**
     * 删除记录
     *
     * @param param 主键id
     * @return
     */
    @PostMapping("/delete")
    public Result<?> delete(@Valid @RequestBody IdParam param) {
        genTemplateService.deleteById(param.getId());
        return Result.ok();
    }

}
