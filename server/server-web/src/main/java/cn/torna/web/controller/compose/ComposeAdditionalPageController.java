package cn.torna.web.controller.compose;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.dao.entity.ComposeAdditionalPage;
import cn.torna.dao.mapper.ComposeAdditionalPageMapper;
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
    public Result<List<ComposeAdditionalPage>> page(@HashId Long projectId) {
        Query query = new Query()
                .eq("project_id", projectId)
                .orderby("order_index", Sort.ASC)
                .orderby("id", Sort.ASC);
        List<ComposeAdditionalPage> additionalPages = composeAdditionalPageMapper.list(query);
        return Result.ok(additionalPages);
    }


    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("get")
    public Result<ComposeAdditionalPage> getById(@HashId Long id) {
        return Result.ok(composeAdditionalPageMapper.getById(id));
    }    
     
    /**
     * 新增，忽略null字段
     *
     * @param composeAdditionalPage 新增的记录
     * @return 返回影响行数
     */
    @PostMapping("add")
    public Result insert(@RequestBody ComposeAdditionalPage composeAdditionalPage) {
    	composeAdditionalPageMapper.saveIgnoreNull(composeAdditionalPage);
        return Result.ok();
    }    
      
    /**
     * 修改，忽略null字段
     *
     * @param composeAdditionalPage 修改的记录
     * @return 返回影响行数
     */
    @PostMapping("update")
    public Result update(@RequestBody ComposeAdditionalPage composeAdditionalPage) {
        composeAdditionalPageMapper.updateIgnoreNull(composeAdditionalPage);
        return Result.ok();
    }
    
    /**
     * 删除记录
     *
     * @param composeAdditionalPage 待删除的记录
     * @return 返回影响行数
     */
    @PostMapping("delete")
    public Result delete(@RequestBody ComposeAdditionalPage composeAdditionalPage) {
        composeAdditionalPageMapper.delete(composeAdditionalPage);
        return Result.ok();
    }
    
}