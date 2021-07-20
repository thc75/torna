package cn.torna.web.controller.compose;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ComposeAdditionalPage;
import cn.torna.dao.mapper.ComposeAdditionalPageMapper;
import cn.torna.web.controller.compose.param.ComposeAdditionalPageParam;
import cn.torna.web.controller.compose.vo.ComposeAdditionalPageVO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 * 
 * @description
 * @author tanghc
 * @date 2021-07-18 19:17:49
 * @version 1.0.0
 */
@RestController
@RequestMapping("compose/additional")
public class ComposeAdditionalPageController {

    @Autowired
    private ComposeAdditionalPageMapper composeAdditionalPageMapper;

    /**
     * 分页查找
     *
     * @return 返回分页信息
     */
    @GetMapping("/list")
    public Result<List<ComposeAdditionalPageVO>> page(@HashId Long projectId) {
        Query query = new Query()
                .eq("project_id", projectId)
                .orderby("order_index", Sort.ASC)
                .orderby("id", Sort.ASC);
        List<ComposeAdditionalPage> additionalPages = composeAdditionalPageMapper.listBySpecifiedColumns(
                Arrays.asList("id", "title", "gmt_create"),query);
        List<ComposeAdditionalPageVO> list = CopyUtil.copyList(additionalPages, ComposeAdditionalPageVO::new);
        return Result.ok(list);
    }


    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("get")
    public Result<ComposeAdditionalPageVO> getById(@HashId Long id) {
        ComposeAdditionalPage additionalPage = composeAdditionalPageMapper.getById(id);
        ComposeAdditionalPageVO composeAdditionalPageVO = CopyUtil.copyBean(additionalPage, ComposeAdditionalPageVO::new);
        return Result.ok(composeAdditionalPageVO);
    }    
     
    /**
     * 新增，忽略null字段
     *
     * @param param 新增的记录
     * @return 返回影响行数
     */
    @PostMapping("add")
    public Result insert(@RequestBody ComposeAdditionalPageParam param) {
        ComposeAdditionalPage composeAdditionalPage = CopyUtil.copyBean(param, ComposeAdditionalPage::new);
        composeAdditionalPageMapper.saveIgnoreNull(composeAdditionalPage);
        return Result.ok();
    }    
      
    /**
     * 修改，忽略null字段
     *
     * @param param 修改的记录
     * @return 返回影响行数
     */
    @PostMapping("update")
    public Result update(@RequestBody ComposeAdditionalPageParam param) {
        ComposeAdditionalPage additionalPage = composeAdditionalPageMapper.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, additionalPage);
        composeAdditionalPageMapper.updateIgnoreNull(additionalPage);
        return Result.ok();
    }
    
    /**
     * 删除记录
     *
     * @param composeAdditionalPage 待删除的记录
     * @return 返回影响行数
     */
    @PostMapping("delete")
    public Result delete(@RequestBody ComposeAdditionalPageParam composeAdditionalPage) {
        composeAdditionalPageMapper.deleteById(composeAdditionalPage.getId());
        return Result.ok();
    }
    
}