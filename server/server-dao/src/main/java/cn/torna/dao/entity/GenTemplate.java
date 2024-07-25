package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表名：gen_template
 * 备注：代码生成模板
 *
 * @author tanghc
 */
@Table(name = "gen_template", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class GenTemplate {

    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板内容
     */
    private String content;

    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Integer isDeleted;

    /**
     * 分组
     */
    private String groupName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


    /**
     * 创建LambdaQuery对象
     *
     * @return 返回LambdaQuery对象
     */
    public static LambdaQuery<GenTemplate> query() {
        return new LambdaQuery<>(GenTemplate.class);
    }

}
